package com.baeldung.connectionpool;


import java.sql.SQLException;
import org.junit.Assert;
import org.junit.Test;


public class DBCPDataSourceUnitTest {
    @Test
    public void givenDBCPDataSourceClass_whenCalledgetConnection_thenCorrect() throws SQLException {
        Assert.assertTrue(DBCPDataSource.getConnection().isValid(1));
    }
}

