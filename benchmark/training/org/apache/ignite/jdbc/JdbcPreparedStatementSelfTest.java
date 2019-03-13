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
package org.apache.ignite.jdbc;


import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Prepared statement test.
 */
public class JdbcPreparedStatementSelfTest extends GridCommonAbstractTest {
    /**
     * URL.
     */
    private static final String URL = "jdbc:ignite://127.0.0.1/";

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
        stmt = conn.prepareStatement("select * from TestObject where id = ?");
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
    public void testBoolean() throws Exception {
        stmt = conn.prepareStatement("select * from TestObject where boolVal is not distinct from ?");
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
        rs = stmt.executeQuery();
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
        stmt = conn.prepareStatement("select * from TestObject where byteVal is not distinct from ?");
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
        rs = stmt.executeQuery();
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
        stmt = conn.prepareStatement("select * from TestObject where shortVal is not distinct from ?");
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
        rs = stmt.executeQuery();
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
        stmt = conn.prepareStatement("select * from TestObject where intVal is not distinct from ?");
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
        rs = stmt.executeQuery();
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
        stmt = conn.prepareStatement("select * from TestObject where longVal is not distinct from ?");
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
        rs = stmt.executeQuery();
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
        stmt = conn.prepareStatement("select * from TestObject where floatVal is not distinct from ?");
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
        rs = stmt.executeQuery();
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
        stmt = conn.prepareStatement("select * from TestObject where doubleVal is not distinct from ?");
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
        rs = stmt.executeQuery();
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
        stmt = conn.prepareStatement("select * from TestObject where bigVal is not distinct from ?");
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
        rs = stmt.executeQuery();
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
        stmt = conn.prepareStatement("select * from TestObject where strVal is not distinct from ?");
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
        rs = stmt.executeQuery();
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
        stmt = conn.prepareStatement("select * from TestObject where arrVal is not distinct from ?");
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
        rs = stmt.executeQuery();
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
        stmt = conn.prepareStatement("select * from TestObject where dateVal is not distinct from ?");
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
        rs = stmt.executeQuery();
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
        stmt = conn.prepareStatement("select * from TestObject where timeVal is not distinct from ?");
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
        rs = stmt.executeQuery();
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
        stmt = conn.prepareStatement("select * from TestObject where tsVal is not distinct from ?");
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
        rs = stmt.executeQuery();
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
    public void testUrl() throws Exception {
        stmt = conn.prepareStatement("select * from TestObject where urlVal is not distinct from ?");
        stmt.setURL(1, new java.net.URL("http://abc.com/"));
        ResultSet rs = stmt.executeQuery();
        int cnt = 0;
        while (rs.next()) {
            if (cnt == 0)
                assert (rs.getInt("id")) == 1;

            cnt++;
        } 
        assert cnt == 1;
        stmt.setNull(1, Types.DATALINK);
        rs = stmt.executeQuery();
        cnt = 0;
        while (rs.next()) {
            if (cnt == 0)
                assert (rs.getInt("id")) == 2;

            cnt++;
        } 
        assert cnt == 1;
    }

    /**
     * Test object.
     */
    private static class TestObject implements Serializable {
        /**
         *
         */
        @QuerySqlField(index = false)
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

