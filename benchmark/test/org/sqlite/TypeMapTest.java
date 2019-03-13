package org.sqlite;


import java.sql.Connection;
import java.util.Map;
import org.junit.Test;


public class TypeMapTest {
    @Test
    public void getTypeMap() throws Exception {
        Connection conn = getConnection();
        Map<String, Class<?>> m = conn.getTypeMap();
        conn.close();
    }

    @Test
    public void setTypeMap() throws Exception {
        Connection conn = getConnection();
        Map<String, Class<?>> m = conn.getTypeMap();
        conn.setTypeMap(m);
        conn.close();
    }
}

