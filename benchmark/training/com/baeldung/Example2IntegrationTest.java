package com.baeldung;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class Example2IntegrationTest {
    @Test
    public void test1a() {
        Example2IntegrationTest.block(3000);
    }

    @Test
    public void test1b() {
        Example2IntegrationTest.block(3000);
    }
}

