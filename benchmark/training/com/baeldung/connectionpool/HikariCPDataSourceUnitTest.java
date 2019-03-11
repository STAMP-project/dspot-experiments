package com.baeldung.connectionpool;


import java.sql.SQLException;
import org.junit.Assert;
import org.junit.Test;


public class HikariCPDataSourceUnitTest {
    @Test
    public void givenHikariDataSourceClass_whenCalledgetConnection_thenCorrect() throws SQLException {
        Assert.assertTrue(HikariCPDataSource.getConnection().isValid(1));
    }
}

