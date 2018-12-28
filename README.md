> 项目中遇到了分库分表的问题，找到了shrding-jdbc，于是就搞了一个springboot+sharding-jdbc+mybatis的增量分片的应用。今天写博客总结一下遇到的坑。
> 
> 其实，我自己写了一个increament-jdbc组件的，当我读了sharding-jdbc的源码之后，发现思路和原理差不多，sharding这个各方面要比我的强，毕竟我是一天之内赶出来的东东。
> 
> 示例代码地址:https://gitee.com/spartajet/springboot-sharding-jdbc-demo.git
> 
> demo没有写日志，也没有各种异常判断，只是说明问题

## 一、需求背景
我的项目背景就不说了，现在举一个例子吧：A,B两支股票都在上海，深圳上市，需要实时记录这两支股票的交易tick(不懂tick也没有关系)。现在的分片策略是：上海、深圳分别建库，每个库都存各自交易所的两支股票的ticktick，且按照月分表。如图：

* db_sh
	* tick_a_2017_01
	* tick_b_2017_01
	* ........
	* tick_a_2017_12
	* tick_b_2017_12
* db_sz
  * tick_a_2017_01
	* tick_b_2017_01
	* ........
	* tick_a_2017_12
	* tick_b_2017_12

	分库分表就是这样的。根据这个建库。
	
	**千万不要讨论这样分库分表是否合适，这里这样分片只是举个栗子，说明分库分表这个事情。**
	
	**Sharding-jdbc是不支持建库的SQL，如果像我这样增量的数据库和数据表，那就要一次性把一段时期的数据库和数据表都要建好。**

## 二、建库 
考虑到表确实多，所以我就只建1，2月份的表。语句见demo文件。
## 三、springboot集成sharding-jdbc
mvn配置pom如下：

```xml
<groupId>com.spartajet</groupId>
	<artifactId>springboot-sharding-jdbc-demo</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<packaging>jar</packaging>

	<name>springboot-sharding-jdbc-demo</name>
	<description>Springboot integrate Sharding-jdbc Demo</description>

	<properties>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
		<project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
		<project.build.locales>zh_CN</project.build.locales>
		<java.version>1.8</java.version>
		<project.build.jdk>${java.version}</project.build.jdk>
		<spring.boot.version>1.4.1.RELEASE</spring.boot.version>
		<com.alibaba.druid.version>1.0.13</com.alibaba.druid.version>
		<mysql-connector-java.version>5.1.36</mysql-connector-java.version>
		<sharding-jdbc.version>1.4.1</sharding-jdbc.version>
		<com.google.code.gson.version>2.8.0</com.google.code.gson.version>
		<joda-trade.version>2.9.7</joda-trade.version>
		<commons-dbcp.version>1.4</commons-dbcp.version>
		<commons-io.version>2.5</commons-io.version>
		<mybatis-spring-boot-starter.version>1.2.0</mybatis-spring-boot-starter.version>
	</properties>

	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-jdbc</artifactId>
			<version>${spring.boot.version}</version>
		</dependency>
		<dependency>
			<groupId>org.mybatis.spring.boot</groupId>
			<artifactId>mybatis-spring-boot-starter</artifactId>
			<version>${mybatis-spring-boot-starter.version}</version>
		</dependency>
		<dependency>
			<groupId>commons-dbcp</groupId>
			<artifactId>commons-dbcp</artifactId>
			<version>${commons-dbcp.version}</version>
		</dependency>
		<dependency>
			<groupId>com.dangdang</groupId>
			<artifactId>sharding-jdbc-core</artifactId>
			<version>${sharding-jdbc.version}</version>
		</dependency>
		<dependency>
			<groupId>com.dangdang</groupId>
			<artifactId>sharding-jdbc-config-spring</artifactId>
			<version>${sharding-jdbc.version}</version>
		</dependency>
		<dependency>
			<groupId>com.dangdang</groupId>
			<artifactId>sharding-jdbc-self-id-generator</artifactId>
			<version>${sharding-jdbc.version}</version>
		</dependency>
		<dependency>
			<groupId>com.google.code.gson</groupId>
			<artifactId>gson</artifactId>
			<version>${com.google.code.gson.version}</version>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
			<version>${spring.boot.version}</version>
			<exclusions>
				<exclusion>
					<artifactId>org.springframework.boot</artifactId>
					<groupId>spring-boot-start-logging</groupId>
				</exclusion>
			</exclusions>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<version>${spring.boot.version}</version>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-log4j2</artifactId>
			<version>${spring.boot.version}</version>
			<exclusions>
				<exclusion>
					<groupId>log4j</groupId>
					<artifactId>log4j</artifactId>
				</exclusion>
			</exclusions>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter</artifactId>
			<version>${spring.boot.version}</version>
			<exclusions>
				<exclusion>
					<artifactId>org.springframework.boot</artifactId>
					<groupId>spring-boot-start-logging</groupId>
				</exclusion>
				<exclusion>
					<artifactId>logback-classic</artifactId>
					<groupId>ch.qos.logback</groupId>
				</exclusion>
				<exclusion>
					<artifactId>log4j-over-slf4j</artifactId>
					<groupId>org.slf4j</groupId>
				</exclusion>
			</exclusions>
		</dependency>
		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
			<version>${mysql-connector-java.version}</version>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
				<version>${spring.boot.version}</version>
			</plugin>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-compiler-plugin</artifactId>
				<version>3.1</version>
				<configuration>
					<source>${project.build.jdk}</source>
					<target>${project.build.jdk}</target>
					<encoding>${project.build.sourceEncoding}</encoding>
				</configuration>
			</plugin>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-jar-plugin</artifactId>
				<version>2.4</version>
			</plugin>
		</plugins>
	</build>
```
其实这个和sharding-jdbc的官网差不多。其实我想写一个`sharding-jdbc-spring-boot-starter`的pom的，等项目业务都做完再说吧。
## 四、配置数据源
我想将数据库做成可配置的，所以我没有在`application.properties`文件中直接配置数据库，而是写在了`database.json`文件中。

```json
[
  {
    "name": "db_sh",
    "url": "jdbc:mysql://localhost:3306/db_sh",
    "username": "root",
    "password": "root",
    "driveClassName":"com.mysql.jdbc.Driver"
  },
  {
    "name": "db_sz",
    "url": "jdbc:mysql://localhost:3306/db_sz",
    "username": "root",
    "password": "root",
    "driveClassName":"com.mysql.jdbc.Driver"
  }
]
```
然后在springboot读取database文件，加载方式如下：

```java
@Value("classpath:database.json")
    private Resource databaseFile;

    @Bean
    public List<Database> databases() throws IOException {
        String databasesString = IOUtils.toString(databaseFile.getInputStream(), Charset.forName("UTF-8"));
        List<Database> databases = new Gson().fromJson(databasesString, new TypeToken<List<Database>>() {
        }.getType());
        return databases;
    }
```
加载完database信息之后，可以通过工厂方法配置逻辑数据库：

```java
    @Bean
    public HashMap<String, DataSource> dataSourceMap(List<Database> databases) {
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        for (Database database : databases) {
            DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
            dataSourceBuilder.url(database.getUrl());
            dataSourceBuilder.driverClassName(database.getDriveClassName());
            dataSourceBuilder.username(database.getUsername());
            dataSourceBuilder.password(database.getPassword());
            DataSource dataSource = dataSourceBuilder.build();
            dataSourceMap.put(database.getName(), dataSource);
        }
        return dataSourceMap;
    }
```
这样就把各个逻辑数据库就加载好了。
## 五、配置分片策略

### 5.1数据库分片策略
在这个实例中，数据库的分库就是根据上海(sh)和深圳(sz)来分的，在sharding-jdbc中是单键分片。根据官方文档实现接口`SingleKeyDatabaseShardingAlgorithm`就可以

```java
@service
public class DatabaseShardingAlgorithm implements SingleKeyDatabaseShardingAlgorithm<String> {
    /**
     * 根据分片值和SQL的=运算符计算分片结果名称集合.
     *
     * @param availableTargetNames 所有的可用目标名称集合, 一般是数据源或表名称
     * @param shardingValue        分片值
     *
     * @return 分片后指向的目标名称, 一般是数据源或表名称
     */
    @Override
    public String doEqualSharding(Collection<String> availableTargetNames, ShardingValue<String> shardingValue) {
        String databaseName = "";
        for (String targetName : availableTargetNames) {
            if (targetName.endsWith(shardingValue.getValue())) {
                databaseName = targetName;
                break;
            }
        }
        return databaseName;
    }
}
```
此接口还有另外两个方法，`doInSharding`和`doBetweenSharding`，因为我暂时不用IN和BETWEEN方法，所以就没有写，直接返回null。

### 5.2数据表分片策略
数据表的分片策略是根据股票和时间共同决定的，在sharding-jdbc中是多键分片。根据官方文档，实现`MultipleKeysTableShardingAlgorithm`接口就OK了

```java
@service
public class TableShardingAlgorithm implements MultipleKeysTableShardingAlgorithm {
    /**
     * 根据分片值计算分片结果名称集合.
     *
     * @param availableTargetNames 所有的可用目标名称集合, 一般是数据源或表名称
     * @param shardingValues       分片值集合
     *
     * @return 分片后指向的目标名称集合, 一般是数据源或表名称
     */
    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, Collection<ShardingValue<?>> shardingValues) {
        String name = null;
        Date time = null;
        for (ShardingValue<?> shardingValue : shardingValues) {
            if (shardingValue.getColumnName().equals("name")) {
                name = ((ShardingValue<String>) shardingValue).getValue();
            }
            if (shardingValue.getColumnName().equals("time")) {
                time = ((ShardingValue<Date>) shardingValue).getValue();
            }
            if (name != null && time != null) {
                break;
            }
        }
        String timeString = new SimpleDateFormat("yyyy_MM").format(time);
        String suffix = name + "_" + timeString;
        Collection<String> result = new LinkedHashSet<>();
        for (String targetName : availableTargetNames) {
            if (targetName.endsWith(suffix)) {
                result.add(targetName);
            }
        }
        return result;
    }
}
```
这些方法的使用可以查官方文档。
### 5.3注入分片策略
以上只是定义了分片算法，还没有形成策略，还没有告诉shrding将哪个字段给分片算法：

```
@Configuration
public class ShardingStrategyConfig {
    @Bean
    public DatabaseShardingStrategy databaseShardingStrategy(DatabaseShardingAlgorithm databaseShardingAlgorithm) {
        DatabaseShardingStrategy databaseShardingStrategy = new DatabaseShardingStrategy("exchange", databaseShardingAlgorithm);
        return databaseShardingStrategy;
    }

    @Bean
    public TableShardingStrategy tableShardingStrategy(TableShardingAlgorithm tableShardingAlgorithm) {
        Collection<String> columns = new LinkedList<>();
        columns.add("name");
        columns.add("time");
        TableShardingStrategy tableShardingStrategy = new TableShardingStrategy(columns, tableShardingAlgorithm);
        return tableShardingStrategy;
    }
}
```
这样才能形成完成的分片策略。
## 六、配置Sharding-jdbc的DataSource

sharding-jdbc的原理其实很简单，就是自己做一个DataSource给上层应用使用，这个DataSource包含所有的逻辑库和逻辑表，应用增删改查时，他自己再修改sql，然后选择合适的数据库继续操作。所以这个DataSource创建很重要。

```java
    @Bean
    @Primary
    public DataSource shardingDataSource(HashMap<String, DataSource> dataSourceMap, DatabaseShardingStrategy databaseShardingStrategy, TableShardingStrategy tableShardingStrategy) {
        DataSourceRule dataSourceRule = new DataSourceRule(dataSourceMap);
        TableRule tableRule = TableRule.builder("tick").actualTables(Arrays.asList("db_sh.tick_a_2017_01", "db_sh.tick_a_2017_02", "db_sh.tick_b_2017_01", "db_sh.tick_b_2017_02", "db_sz.tick_a_2017_01", "db_sz.tick_a_2017_02", "db_sz.tick_b_2017_01", "db_sz.tick_a_2017_02")).dataSourceRule(dataSourceRule).build();
        ShardingRule shardingRule = ShardingRule.builder().dataSourceRule(dataSourceRule).tableRules(Arrays.asList(tableRule)).databaseShardingStrategy(databaseShardingStrategy).tableShardingStrategy(tableShardingStrategy).build();
        DataSource shardingDataSource = ShardingDataSourceFactory.createDataSource(shardingRule);
        return shardingDataSource;
    }
```
**这里要着重说一下为什么要用@Primary这个注解，没有这个注解是会报错的，错误大致意思就是DataSource太多了，mybatis不知道用哪个。加上这个mybatis就知道用sharding的DataSource了。这里参考的是jpa的多数据源配置**

## 七、配置mybatis
### 7.1 Bean

```java
public class Tick {
    private long id;
    private String name;
    private String exchange;
    private int ask;
    private int bid;
    private Date time;
}
```
### 7.2 Mapper
很简单，只实现一个插入方法

```java
@Mapper
public interface TickMapper {
    @Insert("insert into tick (id,name,exchange,ask,bid,time) values (#{id},#{name},#{exchange},#{ask},#{bid},#{time})")
    void insertTick(Tick tick);
}
```
### 7.3 SessionFactory配置
还要设置一下tick的SessionFactory：

```
@Configuration
@MapperScan(basePackages = "com.spartajet.shardingboot.mapper", sqlSessionFactoryRef = "sessionFactory")
public class TickSessionFactoryConfig {
    @Bean
    public SqlSessionFactory sessionFactory(DataSource shardingDataSource) throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(shardingDataSource);
        return sessionFactory.getObject();
    }

    @Bean
    public CommonSelfIdGenerator commonSelfIdGenerator() {
        CommonSelfIdGenerator.setClock(AbstractClock.systemClock());
        CommonSelfIdGenerator commonSelfIdGenerator = new CommonSelfIdGenerator();
        return commonSelfIdGenerator;
    }
}
```
这里添加了一个`CommonSelfIdGenerator`，sharding自带的id生成器，看了下代码和`facebook`的`snowflake`类似。我又不想把数据库的主键设置成自增的，否则数据双向同步会死的很惨的。
### 
## 八、测试写入

```
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class SpringbootShardingJdbcDemoApplicationTests {
    @Autowired
    private TickMapper tickMapper;
    @Autowired
    private CommonSelfIdGenerator commonSelfIdGenerator;
    
    
    @Test
    public void contextLoads() {
        Tick tick = new Tick(commonSelfIdGenerator.generateId().longValue(), "a", "sh", 100, 200, new Date());
        this.tickMapper.insertTick(tick);
    }

}
```
成功实现增量分库分表！！！



