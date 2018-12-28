package com.spartajet.shardingboot;

import lombok.Data;

import java.io.Serializable;

@Data
public class SerializabileBean implements Serializable{

    private static final long serialVersionUID = -4826627639411816731L;

    private Integer id;

    private String name;

    private String sex;
}
