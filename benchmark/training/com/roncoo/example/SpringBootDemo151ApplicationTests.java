package com.roncoo.example;


import com.roncoo.example.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootDemo151ApplicationTests {
    @Autowired
    private UserService userService;

    @Test
    public void register() {
        String result = userService.register("??", "192.168.1.1");
        System.out.println(result);
    }
}

