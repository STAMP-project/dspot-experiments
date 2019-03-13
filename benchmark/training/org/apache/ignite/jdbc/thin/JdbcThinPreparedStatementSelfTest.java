/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.jdbc.thin;


import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.concurrent.Callable;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.testframework.GridTestUtils;
import org.junit.Test;


/**
 * Prepared statement test.
 */
@SuppressWarnings("ThrowableNotThrown")
public class JdbcThinPreparedStatementSelfTest extends JdbcThinAbstractSelfTest {
    /**
     * URL.
     */
    private static final String URL = "jdbc:ignite:thin://127.0.0.1/";

    /**
     * SQL query.
     */
    private static final String SQL_PART = "select id, boolVal, byteVal, shortVal, intVal, longVal, floatVal, " + ("doubleVal, bigVal, strVal, arrVal, dateVal, timeVal, tsVal " + "from TestObject ");

    /**
     * Connection.
     */
    private Connection conn;

    /**
     * Statement.
     */
    private PreparedStatement stmt;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRepeatableUsage() throws Exception {
        stmt = conn.prepareStatement(((JdbcThinPreparedStatementSelfTest.SQL_PART) + " where id = ?"));
        stmt.setInt(1, 1);
        ResultSet rs = stmt.executeQuery();
        int cnt = 0;
        while (rs.next()) {
            if (cnt == 0)
                assertEquals(1, rs.getInt(1));

            cnt++;
        } 
        assertEquals(1, cnt);
        cnt = 0;
        rs = stmt.executeQuery();
        while (rs.next()) {
            if (cnt == 0)
                assertEquals(1, rs.getInt(1));

            cnt++;
        } 
        assertEquals(1, cnt);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testQueryExecuteException() throws Exception {
        stmt = conn.prepareStatement(((JdbcThinPreparedStatementSelfTest.SQL_PART) + " where boolVal is not distinct from ?"));
        stmt.setBoolean(1, true);
        GridTestUtils.assertThrowsAnyCause(log, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                stmt.executeQuery("select 1");
                return null;
            }
        }, SQLException.class, "The method 'executeQuery(String)' is called on PreparedStatement instance.");
        GridTestUtils.assertThrowsAnyCause(log, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                stmt.execute("select 1");
                return null;
            }
        }, SQLException.class, "The method 'execute(String)' is called on PreparedStatement instance.");
        GridTestUtils.assertThrowsAnyCause(log, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                stmt.execute("select 1", Statement.NO_GENERATED_KEYS);
                return null;
            }
        }, SQLException.class, "The method 'execute(String)' is called on PreparedStatement instance.");
        GridTestUtils.assertThrowsAnyCause(log, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                stmt.executeUpdate("select 1", Statement.NO_GENERATED_KEYS);
                return null;
            }
        }, SQLException.class, "The method 'executeUpdate(String, int)' is called on PreparedStatement instance.");
        GridTestUtils.assertThrowsAnyCause(log, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                stmt.executeUpdate("select 1", new int[]{ 1 });
                return null;
            }
        }, SQLException.class, "The method 'executeUpdate(String, int[])' is called on PreparedStatement instance.");
        GridTestUtils.assertThrowsAnyCause(log, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                stmt.executeUpdate("select 1 as a", new String[]{ "a" });
                return null;
            }
        }, SQLException.class, "The method 'executeUpdate(String, String[])' is called on PreparedStatement instance.");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testBoolean() throws Exception {
        stmt = conn.prepareStatement(((JdbcThinPreparedStatementSelfTest.SQL_PART) + " where boolVal is not distinct from ?"));
        stmt.setBoolean(1, true);
        ResultSet rs = stmt.executeQuery();
        int cnt = 0;
        while (rs.next()) {
            if (cnt == 0)
                assert (rs.getInt("id")) == 1;

            cnt++;
        } 
        assert cnt == 1;
        stmt.setNull(1, Types.BOOLEAN);
        stmt.execute();
        rs = stmt.getResultSet();
        cnt = 0;
        while (rs.next()) {
            if (cnt == 0)
                assert (rs.getInt("id")) == 2;

            cnt++;
        } 
        assert cnt == 1;
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testByte() throws Exception {
        stmt = conn.prepareStatement(((JdbcThinPreparedStatementSelfTest.SQL_PART) + " where byteVal is not distinct from ?"));
        stmt.setByte(1, ((byte) (1)));
        ResultSet rs = stmt.executeQuery();
        int cnt = 0;
        while (rs.next()) {
            if (cnt == 0)
                assert (rs.getInt("id")) == 1;

            cnt++;
        } 
        assert cnt == 1;
        stmt.setNull(1, Types.TINYINT);
        stmt.execute();
        rs = stmt.getResultSet();
        cnt = 0;
        while (rs.next()) {
            if (cnt == 0)
                assert (rs.getInt("id")) == 2;

            cnt++;
        } 
        assert cnt == 1;
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testShort() throws Exception {
        stmt = conn.prepareStatement(((JdbcThinPreparedStatementSelfTest.SQL_PART) + " where shortVal is not distinct from ?"));
        stmt.setShort(1, ((short) (1)));
        ResultSet rs = stmt.executeQuery();
        int cnt = 0;
        while (rs.next()) {
            if (cnt == 0)
                assert (rs.getInt("id")) == 1;

            cnt++;
        } 
        assert cnt == 1;
        stmt.setNull(1, Types.SMALLINT);
        stmt.execute();
        rs = stmt.getResultSet();
        cnt = 0;
        while (rs.next()) {
            if (cnt == 0)
                assert (rs.getInt("id")) == 2;

            cnt++;
        } 
        assert cnt == 1;
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testInteger() throws Exception {
        stmt = conn.prepareStatement(((JdbcThinPreparedStatementSelfTest.SQL_PART) + " where intVal is not distinct from ?"));
        stmt.setInt(1, 1);
        ResultSet rs = stmt.executeQuery();
        int cnt = 0;
        while (rs.next()) {
            if (cnt == 0)
                assert (rs.getInt("id")) == 1;

            cnt++;
        } 
        assert cnt == 1;
        stmt.setNull(1, Types.INTEGER);
        stmt.execute();
        rs = stmt.getResultSet();
        cnt = 0;
        while (rs.next()) {
            if (cnt == 0)
                assert (rs.getInt("id")) == 2;

            cnt++;
        } 
        assert cnt == 1;
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testLong() throws Exception {
        stmt = conn.prepareStatement(((JdbcThinPreparedStatementSelfTest.SQL_PART) + " where longVal is not distinct from ?"));
        stmt.setLong(1, 1L);
        ResultSet rs = stmt.executeQuery();
        int cnt = 0;
        while (rs.next()) {
            if (cnt == 0)
                assert (rs.getInt("id")) == 1;

            cnt++;
        } 
        assert cnt == 1;
        stmt.setNull(1, Types.BIGINT);
        stmt.execute();
        rs = stmt.getResultSet();
        cnt = 0;
        while (rs.next()) {
            if (cnt == 0)
                assert (rs.getInt("id")) == 2;

            cnt++;
        } 
        assert cnt == 1;
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testFloat() throws Exception {
        stmt = conn.prepareStatement(((JdbcThinPreparedStatementSelfTest.SQL_PART) + " where floatVal is not distinct from ?"));
        stmt.setFloat(1, 1.0F);
        ResultSet rs = stmt.executeQuery();
        int cnt = 0;
        while (rs.next()) {
            if (cnt == 0)
                assert (rs.getInt("id")) == 1;

            cnt++;
        } 
        assert cnt == 1;
        stmt.setNull(1, Types.FLOAT);
        stmt.execute();
        rs = stmt.getResultSet();
        cnt = 0;
        while (rs.next()) {
            if (cnt == 0)
                assert (rs.getInt("id")) == 2;

            cnt++;
        } 
        assert cnt == 1;
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDouble() throws Exception {
        stmt = conn.prepareStatement(((JdbcThinPreparedStatementSelfTest.SQL_PART) + " where doubleVal is not distinct from ?"));
        stmt.setDouble(1, 1.0);
        ResultSet rs = stmt.executeQuery();
        int cnt = 0;
        while (rs.next()) {
            if (cnt == 0)
                assert (rs.getInt("id")) == 1;

            cnt++;
        } 
        assert cnt == 1;
        stmt.setNull(1, Types.DOUBLE);
        stmt.execute();
        rs = stmt.getResultSet();
        cnt = 0;
        while (rs.next()) {
            if (cnt == 0)
                assert (rs.getInt("id")) == 2;

            cnt++;
        } 
        assert cnt == 1;
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testBigDecimal() throws Exception {
        stmt = conn.prepareStatement(((JdbcThinPreparedStatementSelfTest.SQL_PART) + " where bigVal is not distinct from ?"));
        stmt.setBigDecimal(1, new BigDecimal(1));
        ResultSet rs = stmt.executeQuery();
        int cnt = 0;
        while (rs.next()) {
            if (cnt == 0)
                assert (rs.getInt("id")) == 1;

            cnt++;
        } 
        assert cnt == 1;
        stmt.setNull(1, Types.OTHER);
        stmt.execute();
        rs = stmt.getResultSet();
        cnt = 0;
        while (rs.next()) {
            if (cnt == 0)
                assert (rs.getInt("id")) == 2;

            cnt++;
        } 
        assert cnt == 1;
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testString() throws Exception {
        stmt = conn.prepareStatement(((JdbcThinPreparedStatementSelfTest.SQL_PART) + " where strVal is not distinct from ?"));
        stmt.setString(1, "str");
        ResultSet rs = stmt.executeQuery();
        int cnt = 0;
        while (rs.next()) {
            if (cnt == 0)
                assert (rs.getInt("id")) == 1;

            cnt++;
        } 
        assert cnt == 1;
        stmt.setNull(1, Types.VARCHAR);
        stmt.execute();
        rs = stmt.getResultSet();
        cnt = 0;
        while (rs.next()) {
            if (cnt == 0)
                assert (rs.getInt("id")) == 2;

            cnt++;
        } 
        assert cnt == 1;
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testArray() throws Exception {
        stmt = conn.prepareStatement(((JdbcThinPreparedStatementSelfTest.SQL_PART) + " where arrVal is not distinct from ?"));
        stmt.setBytes(1, new byte[]{ 1 });
        ResultSet rs = stmt.executeQuery();
        int cnt = 0;
        while (rs.next()) {
            if (cnt == 0)
                assert (rs.getInt("id")) == 1;

            cnt++;
        } 
        assert cnt == 1;
        stmt.setNull(1, Types.BINARY);
        stmt.execute();
        rs = stmt.getResultSet();
        cnt = 0;
        while (rs.next()) {
            if (cnt == 0)
                assert (rs.getInt("id")) == 2;

            cnt++;
        } 
        assert cnt == 1;
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDate() throws Exception {
        stmt = conn.prepareStatement(((JdbcThinPreparedStatementSelfTest.SQL_PART) + " where dateVal is not distinct from ?"));
        stmt.setObject(1, new Date(1));
        ResultSet rs = stmt.executeQuery();
        int cnt = 0;
        while (rs.next()) {
            if (cnt == 0)
                assert (rs.getInt("id")) == 1;

            cnt++;
        } 
        assert cnt == 1;
        stmt.setNull(1, Types.DATE);
        stmt.execute();
        rs = stmt.getResultSet();
        cnt = 0;
        while (rs.next()) {
            if (cnt == 0)
                assert (rs.getInt("id")) == 2;

            cnt++;
        } 
        assert cnt == 1;
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testTime() throws Exception {
        stmt = conn.prepareStatement(((JdbcThinPreparedStatementSelfTest.SQL_PART) + " where timeVal is not distinct from ?"));
        stmt.setTime(1, new Time(1));
        ResultSet rs = stmt.executeQuery();
        int cnt = 0;
        while (rs.next()) {
            if (cnt == 0)
                assert (rs.getInt("id")) == 1;

            cnt++;
        } 
        assert cnt == 1;
        stmt.setNull(1, Types.TIME);
        stmt.execute();
        rs = stmt.getResultSet();
        cnt = 0;
        while (rs.next()) {
            if (cnt == 0)
                assert (rs.getInt("id")) == 2;

            cnt++;
        } 
        assert cnt == 1;
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testTimestamp() throws Exception {
        stmt = conn.prepareStatement(((JdbcThinPreparedStatementSelfTest.SQL_PART) + " where tsVal is not distinct from ?"));
        stmt.setTimestamp(1, new Timestamp(1));
        ResultSet rs = stmt.executeQuery();
        int cnt = 0;
        while (rs.next()) {
            if (cnt == 0)
                assert (rs.getInt("id")) == 1;

            cnt++;
        } 
        assert cnt == 1;
        stmt.setNull(1, Types.TIMESTAMP);
        stmt.execute();
        rs = stmt.getResultSet();
        cnt = 0;
        while (rs.next()) {
            if (cnt == 0)
                assert (rs.getInt("id")) == 2;

            cnt++;
        } 
        assert cnt == 1;
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClearParameter() throws Exception {
        stmt = conn.prepareStatement(((JdbcThinPreparedStatementSelfTest.SQL_PART) + " where boolVal is not distinct from ?"));
        stmt.setString(1, "");
        stmt.setLong(2, 1L);
        stmt.setInt(5, 1);
        stmt.clearParameters();
        stmt.setBoolean(1, true);
        ResultSet rs = stmt.executeQuery();
        boolean hasNext = rs.next();
        assert hasNext;
        assert (rs.getInt("id")) == 1;
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testNotSupportedTypes() throws Exception {
        stmt = conn.prepareStatement("");
        checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.setArray(1, null);
            }
        });
        checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.setAsciiStream(1, null);
            }
        });
        checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.setAsciiStream(1, null, 0);
            }
        });
        checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.setAsciiStream(1, null, 0L);
            }
        });
        checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.setBinaryStream(1, null);
            }
        });
        checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.setBinaryStream(1, null, 0);
            }
        });
        checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.setBinaryStream(1, null, 0L);
            }
        });
        checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.setBlob(1, ((Blob) (null)));
            }
        });
        checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.setBlob(1, ((InputStream) (null)));
            }
        });
        checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.setBlob(1, null, 0L);
            }
        });
        checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.setCharacterStream(1, null);
            }
        });
        checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.setCharacterStream(1, null, 0);
            }
        });
        checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.setCharacterStream(1, null, 0L);
            }
        });
        checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.setClob(1, ((Clob) (null)));
            }
        });
        checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.setClob(1, ((Reader) (null)));
            }
        });
        checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.setClob(1, null, 0L);
            }
        });
        checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.setNCharacterStream(1, null);
            }
        });
        checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.setNCharacterStream(1, null, 0L);
            }
        });
        checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.setNClob(1, ((NClob) (null)));
            }
        });
        checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.setNClob(1, ((Reader) (null)));
            }
        });
        checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.setNClob(1, null, 0L);
            }
        });
        checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.setRowId(1, null);
            }
        });
        checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.setRef(1, null);
            }
        });
        checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                stmt.setSQLXML(1, null);
            }
        });
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                stmt.setURL(1, new java.net.URL("http://test"));
                return null;
            }
        }, SQLException.class, "Parameter type is unsupported");
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                stmt.setObject(1, new JdbcThinPreparedStatementSelfTest.TestObject(0));
                return null;
            }
        }, SQLException.class, "Parameter type is unsupported");
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                stmt.setObject(1, new JdbcThinPreparedStatementSelfTest.TestObject(0), Types.JAVA_OBJECT);
                return null;
            }
        }, SQLException.class, "Parameter type is unsupported");
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                stmt.setObject(1, new JdbcThinPreparedStatementSelfTest.TestObject(0), Types.JAVA_OBJECT, 0);
                return null;
            }
        }, SQLException.class, "Parameter type is unsupported");
    }

    /**
     * Test object.
     */
    private static class TestObject implements Serializable {
        /**
         *
         */
        @QuerySqlField
        private final int id;

        /**
         *
         */
        @QuerySqlField
        private Boolean boolVal;

        /**
         *
         */
        @QuerySqlField
        private Byte byteVal;

        /**
         *
         */
        @QuerySqlField
        private Short shortVal;

        /**
         *
         */
        @QuerySqlField
        private Integer intVal;

        /**
         *
         */
        @QuerySqlField
        private Long longVal;

        /**
         *
         */
        @QuerySqlField
        private Float floatVal;

        /**
         *
         */
        @QuerySqlField
        private Double doubleVal;

        /**
         *
         */
        @QuerySqlField
        private BigDecimal bigVal;

        /**
         *
         */
        @QuerySqlField
        private String strVal;

        /**
         *
         */
        @QuerySqlField
        private byte[] arrVal;

        /**
         *
         */
        @QuerySqlField
        private Date dateVal;

        /**
         *
         */
        @QuerySqlField
        private Time timeVal;

        /**
         *
         */
        @QuerySqlField
        private Timestamp tsVal;

        /**
         *
         */
        @QuerySqlField
        private java.net.URL urlVal;

        /**
         *
         *
         * @param id
         * 		ID.
         */
        private TestObject(int id) {
            this.id = id;
        }
    }
}

