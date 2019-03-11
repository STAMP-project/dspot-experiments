package com.stackify.test;


import com.stackify.utils.ConnectionUtil;
import java.sql.Connection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public interface DatabaseConnectionTest {
    @Test
    default void testDatabaseConnection() {
        Connection con = ConnectionUtil.getConnection();
        Assertions.assertNotNull(con);
    }
}

