package com.spartajet.shardingboot;

/**
 * 单例
 */
public class SingLeton {

    private static SingLeton singLeton=null;

    private static SingLeton singLeton2=new SingLeton();

    SingLeton(){

    }

   /* public SingLeton getInstance(){
        if(singLeton==null){
            //加同步锁时，前后两次判断实例是否存在（可行）
            synchronized (SingLeton.class){
                singLeton=new SingLeton();
            }
        }
        return singLeton;
    }
*/

   // 适用于多线程的情况
/*   public static synchronized SingLeton getInstance(){
       if(singLeton==null){
           singLeton=new SingLeton();
       }
       return  singLeton;
   }*/

    //静态内部类
   public static class SingLeonHolder{
       private static final SingLeton singLeton=new SingLeton();

       public static SingLeton getInstance(){
           return singLeton;
       }
    }

    //按需调用
    public SingLeton getInstance(){
       return SingLeonHolder.getInstance();
    }

   /* public SingLeton getInstance(){
        return singLeton2;
    }*/



}
