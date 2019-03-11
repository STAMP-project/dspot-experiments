/**
 * --------------------------------------
 */
/**
 * sqlite-jdbc Project
 */
/**
 *
 */
/**
 * InsertQueryTest.java
 */
/**
 * Since: Apr 7, 2009
 */
/**
 *
 */
/**
 * $URL$
 */
/**
 * $Author$
 */
/**
 * --------------------------------------
 */
package org.sqlite;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.Test;


public class InsertQueryTest {
    String dbName;

    interface ConnectionFactory {
        Connection getConnection() throws SQLException;

        void dispose() throws SQLException;
    }

    class IndependentConnectionFactory implements InsertQueryTest.ConnectionFactory {
        public Connection getConnection() throws SQLException {
            return DriverManager.getConnection(("jdbc:sqlite:" + (dbName)));
        }

        public void dispose() throws SQLException {
        }
    }

    class SharedConnectionFactory implements InsertQueryTest.ConnectionFactory {
        private Connection conn = null;

        public Connection getConnection() throws SQLException {
            if ((conn) == null)
                conn = DriverManager.getConnection(("jdbc:sqlite:" + (dbName)));

            return conn;
        }

        public void dispose() throws SQLException {
            if ((conn) != null)
                conn.close();

        }
    }

    static class BD {
        String fullId;

        String type;

        public BD(String fullId, String type) {
            this.fullId = fullId;
            this.type = type;
        }

        public String getFullId() {
            return fullId;
        }

        public void setFullId(String fullId) {
            this.fullId = fullId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public static byte[] serializeBD(InsertQueryTest.BD item) {
            return new byte[0];
        }
    }

    @Test
    public void insertLockTestUsingSharedConnection() throws Exception {
        insertAndQuery(new InsertQueryTest.SharedConnectionFactory());
    }

    @Test
    public void insertLockTestUsingIndependentConnection() throws Exception {
        insertAndQuery(new InsertQueryTest.IndependentConnectionFactory());
    }

    @Test(expected = SQLException.class)
    public void reproduceDatabaseLocked() throws SQLException {
        Connection conn = DriverManager.getConnection(("jdbc:sqlite:" + (dbName)));
        Connection conn2 = DriverManager.getConnection(("jdbc:sqlite:" + (dbName)));
        Statement stat = conn.createStatement();
        Statement stat2 = conn2.createStatement();
        conn.setAutoCommit(false);
        stat.executeUpdate("drop table if exists sample");
        stat.executeUpdate("create table sample(id, name)");
        stat.executeUpdate("insert into sample values(1, 'leo')");
        ResultSet rs = stat2.executeQuery("select count(*) from sample");
        rs.next();
        conn.commit();// causes "database is locked" (SQLITE_BUSY)

    }
}

