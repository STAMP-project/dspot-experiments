package com.querydsl.sql.spatial.suites;


import com.querydsl.core.testutil.H2;
import com.querydsl.sql.Connections;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(H2.class)
public class SpatialTest {
    @Test
    public void test() throws SQLException {
        Statement stmt = Connections.getStatement();
        ResultSet rs = stmt.executeQuery("select \"GEOMETRY\" from \"SHAPES\"");
        try {
            while (rs.next()) {
                System.err.println(rs.getObject(1).getClass().getName());
                System.err.println(rs.getString(1));
                // Clob clob = rs.getClob(1);
                // System.err.println(clob.getSubString(1, (int) clob.length()));
            } 
        } finally {
            rs.close();
        }
    }

    @Test
    public void metadata() throws SQLException {
        Connection conn = Connections.getConnection();
        DatabaseMetaData md = conn.getMetaData();
        ResultSet rs = md.getColumns(null, null, "SHAPES", "GEOMETRY");
        try {
            rs.next();
            int type = rs.getInt("DATA_TYPE");
            String typeName = rs.getString("TYPE_NAME");
            System.err.println(((type + " ") + typeName));
        } finally {
            rs.close();
        }
    }
}

