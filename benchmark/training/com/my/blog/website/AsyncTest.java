package com.my.blog.website;


import java.util.concurrent.Future;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * ??????
 * Created by Administrator on 2017/3/6 006.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@EnableAsync
public class AsyncTest {
    @Autowired
    private Task task;

    @Test
    public void Test() throws Exception {
        long start = System.currentTimeMillis();
        Future<String> task1 = task.doTaskOne();
        Future<String> task2 = task.doTaskTwo();
        Future<String> task3 = task.doTaskThree();
        while (true) {
            if (((task1.isDone()) && (task2.isDone())) && (task3.isDone())) {
                // ????????????????
                break;
            }
            Thread.sleep(1000);
        } 
        long end = System.currentTimeMillis();
        System.out.println((("???????????" + (end - start)) + "??"));
    }
}

