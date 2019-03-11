package org.linlinjava.litemall.admin;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;


@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class AdminConfigTest {
    @Autowired
    private Environment environment;

    @Test
    public void test() {
        // ????application-core.yml????
        System.out.println(environment.getProperty("litemall.express.appId"));
        // ????application-db.yml????
        System.out.println(environment.getProperty("spring.datasource.druid.url"));
        // ????application-admin.yml????
        // System.out.println(environment.getProperty(""));
        // ????application.yml????
        System.out.println(environment.getProperty("logging.level.org.linlinjava.litemall.admin"));
    }
}

