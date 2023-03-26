package com.example.mongolearn;

import com.example.mongolearn.com.timesaving.cn.bo.Student;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONValue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ExecutableRemoveOperation;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;

/***
 * *   功能描述：demo
 * *
 * *   @DATE    2023-03-26
 * *   @AUTHOR  LIJIANGHU
 ***/
@SpringBootTest
@Slf4j
public class Test1 {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void t1() {
        //1.指定_id字段
        Student student = new Student();
        student.setId("1");
        student.setName("ljh");
        student.setAge(18);
        mongoTemplate.insert(student);
        //2.校验
        Student result = mongoTemplate.findOne(new Query(), Student.class);
        String s = JSONValue.toJSONString(result);
        System.out.println(s);
    }

    @Test
    void t2() {
        //1.指定_id字段
        Student student = new Student();
        student.setName("ljh2");
        student.setAge(18);
        mongoTemplate.insert(student);
        //2.校验
        Student result = mongoTemplate.findOne(new Query(), Student.class);
        String s = JSONValue.toJSONString(result);
        System.out.println(s);
    }

    @Test
    void testSave() {
        //1.保存
        Student student = new Student();
        student.setId("1");
        student.setName("ljh222");
        student.setAge(18);
        mongoTemplate.save(student);
        //2.校验
        Student result = mongoTemplate.findOne(new Query(Criteria.where("_id").is("1")), Student.class);
        Assert.isTrue("ljh222".equals(result.getName()));

        //3.覆盖保存(更新)
        student.setName("马冬梅");
        mongoTemplate.save(student);
        mongoTemplate.insert(student);

        //4.校验
        result = mongoTemplate.findOne(new Query(Criteria.where("_id").is("1")), Student.class);
        Assert.isTrue("马冬梅".equals(result.getName()));
    }

    @Test
    void testDelete() {

        //1.保存测试数据
        Student student = null;
        for (int i = 1; i <= 10; i++) {
            student = new Student();
            student.setId(i + "");
            student.setName(i + "ftc");
            student.setAge(18);
            mongoTemplate.save(student);
        }

        //2.验证数据存入
        long count = mongoTemplate.count(new Query(), Student.class);
        Assert.isTrue(10 == count);

        //3.按条件删除,删除id=1
        DeleteResult remove = mongoTemplate.remove(new Query(Criteria.where("id").is("1")), Student.class);
        Assert.isTrue(1 == remove.getDeletedCount());

        //4.直接传入对象根据ID删除,删除id=10
        remove = mongoTemplate.remove(student);
        Assert.isTrue(1 == remove.getDeletedCount());

        //5.全量删除
        ExecutableRemoveOperation.ExecutableRemove<Student> removeAll = mongoTemplate.remove(Student.class);
        Assert.isTrue(8 == removeAll.all().getDeletedCount());

        //6.验证数据全部删除
        count = mongoTemplate.count(new Query(), Student.class);
        Assert.isTrue(0 == count);
    }


    @Test
    void testUpdate() {

        //1.保存测试数据
        Student student = null;
        for (int i = 1; i <= 10; i++) {
            student = new Student();
            student.setId(i + "");
            student.setName(i + "ftc");
            student.setAge(18);
            mongoTemplate.insert(student);
        }

        //2.验证数据存入
        long count = mongoTemplate.count(new Query(), Student.class);
        Assert.isTrue(10 == count);

        //3.按条件更新一条
        Query query = new Query(Criteria.where("id").is("1"));
        Update update = new Update();
        update.set("name", "夏洛");
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Student.class);
        Assert.isTrue(1 == updateResult.getModifiedCount());

        Student result = mongoTemplate.findOne(query, Student.class);
        Assert.isTrue("夏洛".equals(result.getName()));

        //4.按条件更新全部
        query = new Query();
        update = new Update();
        update.set("name", "马冬梅");
        updateResult = mongoTemplate.updateMulti(query, update, Student.class);
        Assert.isTrue(10 == updateResult.getModifiedCount());

        List<Student> students = mongoTemplate.find(query, Student.class);
        students.forEach(s -> Assert.isTrue("马冬梅".equals(s.getName())));

        //5.另一种更新方式的写法
        update = new Update();
        update.set("name", "大傻春");
        ExecutableUpdateOperation.TerminatingUpdate<Student> apply = mongoTemplate.update(Student.class).apply(update);
        Assert.isTrue(10 == apply.all().getModifiedCount());

        students = mongoTemplate.find(query, Student.class);
        students.forEach(s -> Assert.isTrue("大傻春".equals(s.getName())));
    }

    @Test
    void testUpdate2() {

        //1.保存测试数据
        for (int i = 1; i <= 10; i++) {
            Student student = new Student();
            student.setId(i + "");
            student.setName(i + "ftc");
            student.setAge(i);
            mongoTemplate.insert(student);
        }

        //2.验证数据存入
        long count = mongoTemplate.count(new Query(), Student.class);
        Assert.isTrue(10 == count);

        //3.定义更新属性
        Query query = new Query(Criteria.where("age").gt(5));
        Update update = new Update();
        update.set("name", "马冬梅");

        //4.查询数据并修改属性
        Student andModify = mongoTemplate.findAndModify(query, update, Student.class);

        //5.验证只有一条数据被修改
        andModify = mongoTemplate.findById(andModify.getId(), Student.class);
        Assert.isTrue("马冬梅".equals(andModify.getName()));

        //6.验证其他数据没有被修改
        List<Student> students = mongoTemplate.find(new Query(Criteria.where("id").ne(andModify.getId())), Student.class);
        students.forEach(s -> Assert.isTrue(!"马冬梅".equals(s.getName())));
    }

    @Test
    void testSelect2() {
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

        //3.根据ID查询单个数据
        Student result = mongoTemplate.findById(1, Student.class);
        Assert.isTrue(1 == result.getAge());

        //4.根据条件查询单个数据
        result = mongoTemplate.findOne(new Query(Criteria.where("id").is(2)), Student.class);
        Assert.isTrue(2 == result.getAge());

        //5.根据条件查询批量数据
        List<Student> results = mongoTemplate.find(new Query(Criteria.where("id").gt(5)), Student.class);
        Assert.isTrue(5 == results.size());

        //6.集合全量查询
        results = mongoTemplate.findAll(Student.class);
        Assert.isTrue(10 == results.size());

        //7.新增一条重复age数据
        Student repeatStudent = new Student();
        repeatStudent.setId("11");
        repeatStudent.setAge(1);
        repeatStudent.setName("ftccccc");
        mongoTemplate.insert(repeatStudent);
        count = mongoTemplate.count(new Query(), Student.class);
        Assert.isTrue(11 == count);

        //8.去重查询
        List<Integer> ages = mongoTemplate.findDistinct(new Query(), "age", Student.class, Integer.class);
        Assert.isTrue(10 == ages.size());
    }

    @Test
    void testSelect3() {

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

        //3.等值查询
        Query query = new Query(Criteria.where("id").is(1));
        List<Student> students = mongoTemplate.find(query, Student.class);
        Assert.isTrue(1 == students.size());
        Assert.isTrue(1 == students.get(0).getAge());

        //4.小于查询
        query = new Query(Criteria.where("age").lt(5));
        students = mongoTemplate.find(query, Student.class);
        Assert.isTrue(4 == students.size());

        //5.小于等于查询
        query = new Query(Criteria.where("age").lte(5));
        students = mongoTemplate.find(query, Student.class);
        Assert.isTrue(5 == students.size());

        //6.大于查询
        query = new Query(Criteria.where("age").gt(7));
        students = mongoTemplate.find(query, Student.class);
        Assert.isTrue(3 == students.size());

        //7.大于等于查询
        query = new Query(Criteria.where("age").gte(7));
        students = mongoTemplate.find(query, Student.class);
        Assert.isTrue(4 == students.size());

        //8.不等于查询
        query = new Query(Criteria.where("id").ne(5));
        students = mongoTemplate.find(query, Student.class);
        Assert.isTrue(9 == students.size());

        //9.in查询
        query = new Query(Criteria.where("age").in(1, 2, 3));
        students = mongoTemplate.find(query, Student.class);
        Assert.isTrue(3 == students.size());

        //10.not in查询
        query = new Query(Criteria.where("age").nin(Arrays.asList("1,2,3".split(","))));
        students = mongoTemplate.find(query, Student.class);
        Assert.isTrue(7 == students.size());

        //11.and查询
        query = new Query(Criteria.where("id").is(1).and("age").is(2));
        students = mongoTemplate.find(query, Student.class);
        Assert.isTrue(0 == students.size());

        //12.or查询
        query = new Query(new Criteria().orOperator(
                Criteria.where("id").is(2), Criteria.where("id").is(3))
        );
        students = mongoTemplate.find(query, Student.class);
        Assert.isTrue(2 == students.size());

        //13.and与or结合查询
        query = new Query(Criteria.where("id").is(2).orOperator(
                Criteria.where("age").is(2),
                Criteria.where("age").is(3)
        ));
        students = mongoTemplate.find(query, Student.class);
        Assert.isTrue(1 == students.size());
    }

    @Test
    void testSelect() {

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

        //3.原子性查询并删除批量
        Query query = new Query(Criteria.where("id").is(1));
        List<Student> students = mongoTemplate.findAllAndRemove(query, Student.class);
        Assert.isTrue(1 == students.size());
        count = mongoTemplate.count(new Query(), Student.class);
        Assert.isTrue(9 == count);

        //4.原子性查询并修改
        query = new Query(Criteria.where("id").is(2));
        Update update = new Update();
        update.set("name", "马冬梅");
        mongoTemplate.findAndModify(query, update, Student.class);
        Student student = mongoTemplate.findOne(query, Student.class);
        Assert.isTrue("马冬梅".equals(student.getName()));

        //5.原子性查询并删除
        query = new Query(Criteria.where("id").is(3));
        Student andRemove = mongoTemplate.findAndRemove(query, Student.class);
        student = mongoTemplate.findOne(query, Student.class);
        Assert.isNull(student);

        //6.原子性查询并替换文档
        Student studentReplace = new Student();
        studentReplace.setId("4");
        studentReplace.setAge(100);
        studentReplace.setName("替换对象");

        query = new Query(Criteria.where("id").is(4));
        mongoTemplate.findAndReplace(query, studentReplace, "student");
        Student result = mongoTemplate.findOne(query, Student.class);
        Assert.isTrue(100 == result.getAge());
    }

}
