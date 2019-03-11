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
 * ReadCommitedTest.java
 */
/**
 * Since: Jan 19, 2009
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


import SQLiteConnection.TRANSACTION_READ_UNCOMMITTED;
import SQLiteConnection.TRANSACTION_REPEATABLE_READ;
import SQLiteConnection.TRANSACTION_SERIALIZABLE;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.Test;


public class ReadUncommittedTest {
    private Connection conn;

    private Statement stat;

    @Test
    public void setReadUncommitted() throws SQLException {
        conn.setTransactionIsolation(TRANSACTION_READ_UNCOMMITTED);
    }

    @Test
    public void setSerializable() throws SQLException {
        conn.setTransactionIsolation(TRANSACTION_SERIALIZABLE);
    }

    @Test(expected = SQLException.class)
    public void setUnsupportedIsolationLevel() throws SQLException {
        conn.setTransactionIsolation(TRANSACTION_REPEATABLE_READ);
    }
}

