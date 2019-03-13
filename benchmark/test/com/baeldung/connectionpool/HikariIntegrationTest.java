package com.baeldung.connectionpool;


import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(properties = // instead of setting this property, we can exclude the dependency to org.apache.tomcat:tomcat-jdbc in pom.xml
"spring.datasource.type=com.zaxxer.hikari.HikariDataSource")
public class HikariIntegrationTest {
    @Autowired
    private DataSource dataSource;

    @Test
    public void hikariConnectionPoolIsConfigured() {
        Assert.assertEquals("com.zaxxer.hikari.HikariDataSource", dataSource.getClass().getName());
    }
}

