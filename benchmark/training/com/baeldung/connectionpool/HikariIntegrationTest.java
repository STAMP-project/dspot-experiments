package com.baeldung.connectionpool;


import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class HikariIntegrationTest {
    @Autowired
    private DataSource dataSource;

    @Test
    public void hikariConnectionPoolIsConfigured() {
        Assert.assertEquals("com.zaxxer.hikari.HikariDataSource", dataSource.getClass().getName());
    }
}

