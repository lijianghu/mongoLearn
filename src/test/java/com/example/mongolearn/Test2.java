package com.example.mongolearn;

/***
 * *   功能描述：demo
 * *
 * *   @DATE    2023-03-26
 * *   @AUTHOR  LIJIANGHU
 ***/

import com.example.mongolearn.com.timesaving.cn.bo.Student;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.Assert;

import java.util.List;
@SpringBootTest
@Slf4j
public class Test2 {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void testSelect1() {

        //1.保存测试数据
        for (int i = 1; i <= 10; i++) {
            Student student = new Student();
            student.setId(i+"");
            student.setName(i + "ftc");
            student.setAge(i);
            mongoTemplate.insert(student);
        }

        //2.验证数据存入
        long count = mongoTemplate.count(new Query(), Student.class);
        Assert.isTrue(10 == count);

        //3.限制条数查询
        Query query = new Query().limit(2);
        List<Student> students = mongoTemplate.find(query, Student.class);
        Assert.isTrue(2 == students.size());

        //4.跳过指定条数查询
        query = new Query().skip(5);
        Student student = mongoTemplate.findOne(query, Student.class);
        Assert.isTrue(6 == student.getAge());

        //5.分页查询
        int pageNum = 4;
        int pageSize = 2;
        query = new Query().skip((pageNum - 1) * pageSize).limit(pageSize);
        students = mongoTemplate.find(query, Student.class);
        Assert.isTrue("7".equals(students.get(0).getId()));
        Assert.isTrue("8".equals(students.get(1).getId()));
    }

    @Test
    void testSelect() {

        //1.保存测试数据
        for (int i = 1; i <= 10; i++) {
            Student student = new Student();
            student.setId(i+"");
            student.setName(i + "ftc");
            student.setAge(i);
            // mongoTemplate.insert(student);
        }

        //2.验证数据存入
        long count = mongoTemplate.count(new Query(), Student.class);
        Assert.isTrue(10 == count);

        //3.分页查询
        int pageNum = 4;
        int pageSize = 2;

        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize);
        Query query = new Query().with(pageRequest);
        List<Student> students = mongoTemplate.find(query, Student.class);
        Assert.isTrue("7".equals(students.get(0).getId()));
        Assert.isTrue("8".equals(students.get(1).getId()));
    }

    @Test
    void testSelectSort() {

        //1.保存测试数据
        for (int i = 1; i <= 10; i++) {
            Student student = new Student();
            student.setId(i+"");
            student.setName(i + "ftc");
            student.setAge(i);
            //mongoTemplate.insert(student);
        }

        //2.验证数据存入
        long count = mongoTemplate.count(new Query(), Student.class);
        Assert.isTrue(10 == count);

        //3.单一排序查询
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        ;
        List<Student> students = mongoTemplate.find(new Query()
                .with(sort), Student.class);
        Assert.isTrue(students.get(0).getAge() == 10);
        Assert.isTrue(students.get(9).getAge() == 1);

        //4.组合排序查询
        sort = Sort.by(Sort.Direction.ASC, "id").and(Sort.by(Sort.Direction.ASC, "age"));
        // 或 直接构建query
        //Query.query(Criteria.where("").is(1))
            //    .with(Sort.by(Sort.Order.asc("age")))
             //   .with(Sort.by(Sort.Order.asc("age")));
        students = mongoTemplate.find(new Query().with(sort), Student.class);
        Assert.isTrue(students.get(0).getAge() == 1);
        Assert.isTrue(students.get(9).getAge() == 10);
    }
    // TODO https://www.yuque.com/u27809381/ahfk5y/tp5z7i#O0KBK
}
