package com.baeldung.connectionpool;


import java.sql.SQLException;
import org.junit.Assert;
import org.junit.Test;


public class C3poDataSourceUnitTest {
    @Test
    public void givenC3poDataSourceClass_whenCalledgetConnection_thenCorrect() throws SQLException {
        Assert.assertTrue(C3poDataSource.getConnection().isValid(1));
    }
}

