package com.alibaba.druid.test;


import java.sql.Connection;
import java.sql.DriverManager;
import junit.framework.TestCase;


// public void test_for_g() throws Exception {
// String url = "jdbc:mysql://100.81.165.195:3306/test";
// Connection conn = DriverManager.getConnection(url, "test", "test");
// conn.close();
// }
public class AlibDriverTest extends TestCase {
    public void test_for_alib() throws Exception {
        String url = "jdbc:mysql://127.0.0.1:8507";
        Connection conn = DriverManager.getConnection(url, "root", "root");
        conn.close();
    }
}

