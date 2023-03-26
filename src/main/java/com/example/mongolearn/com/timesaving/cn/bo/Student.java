package com.example.mongolearn.com.timesaving.cn.bo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/***
 * *   功能描述：demo
 * *
 * *   @DATE    2023-03-26
 * *   @AUTHOR  LIJIANGHU
 ***/
@Data
@Document(collection = "student")
public class Student {

    private String id;

    private String name;

    private int age;
}
