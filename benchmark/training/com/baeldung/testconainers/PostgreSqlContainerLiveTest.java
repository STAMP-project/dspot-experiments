package com.baeldung.testconainers;


import java.sql.ResultSet;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.platform.commons.annotation.Testable;
import org.testcontainers.containers.PostgreSQLContainer;


@Testable
public class PostgreSqlContainerLiveTest {
    @Rule
    public PostgreSQLContainer postgresContainer = new PostgreSQLContainer();

    @Test
    public void whenSelectQueryExecuted_thenResulstsReturned() throws Exception {
        ResultSet resultSet = performQuery(postgresContainer, "SELECT 1");
        resultSet.next();
        int result = resultSet.getInt(1);
        Assert.assertEquals(1, result);
    }
}

