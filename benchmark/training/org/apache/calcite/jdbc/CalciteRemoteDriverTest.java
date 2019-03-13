/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.jdbc;


import CalciteConnectionImpl.TROJAN;
import CalciteSystemProperty.DEBUG;
import Meta.DatabaseProperty;
import SqlType.Method;
import com.google.common.collect.ImmutableList;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Struct;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.jcip.annotations.NotThreadSafe;
import org.apache.calcite.avatica.AvaticaConnection;
import org.apache.calcite.avatica.Meta;
import org.apache.calcite.avatica.SqlType;
import org.apache.calcite.avatica.remote.LocalJsonService;
import org.apache.calcite.avatica.remote.Service;
import org.apache.calcite.avatica.server.HttpServer;
import org.apache.calcite.test.CalciteAssert;
import org.apache.calcite.test.JdbcFrontLinqBackTest;
import org.apache.calcite.util.TestUtil;
import org.apache.calcite.util.Util;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for Calcite's remote JDBC driver.
 * Technically speaking, the test is thread safe, however Caclite/Avatica have thread-safety issues
 * see https://issues.apache.org/jira/browse/CALCITE-2853.
 */
@NotThreadSafe
public class CalciteRemoteDriverTest {
    public static final String LJS = CalciteRemoteDriverTest.Factory2.class.getName();

    private final PrintWriter out = (DEBUG.value()) ? Util.printWriter(System.out) : new PrintWriter(new StringWriter());

    private static final CalciteAssert.ConnectionFactory REMOTE_CONNECTION_FACTORY = new CalciteAssert.ConnectionFactory() {
        public Connection createConnection() throws SQLException {
            return CalciteRemoteDriverTest.getRemoteConnection();
        }
    };

    private static final Function<Connection, ResultSet> GET_SCHEMAS = ( connection) -> {
        try {
            return connection.getMetaData().getSchemas();
        } catch (SQLException e) {
            throw TestUtil.rethrow(e);
        }
    };

    private static final Function<Connection, ResultSet> GET_CATALOGS = ( connection) -> {
        try {
            return connection.getMetaData().getCatalogs();
        } catch (SQLException e) {
            throw TestUtil.rethrow(e);
        }
    };

    private static final Function<Connection, ResultSet> GET_COLUMNS = ( connection) -> {
        try {
            return connection.getMetaData().getColumns(null, null, null, null);
        } catch (SQLException e) {
            throw TestUtil.rethrow(e);
        }
    };

    private static final Function<Connection, ResultSet> GET_TYPEINFO = ( connection) -> {
        try {
            return connection.getMetaData().getTypeInfo();
        } catch (SQLException e) {
            throw TestUtil.rethrow(e);
        }
    };

    private static final Function<Connection, ResultSet> GET_TABLE_TYPES = ( connection) -> {
        try {
            return connection.getMetaData().getTableTypes();
        } catch (SQLException e) {
            throw TestUtil.rethrow(e);
        }
    };

    private static Connection localConnection;

    private static HttpServer start;

    @Test
    public void testCatalogsLocal() throws Exception {
        final Connection connection = DriverManager.getConnection(("jdbc:avatica:remote:factory=" + (CalciteRemoteDriverTest.LJS)));
        Assert.assertThat(connection.isClosed(), CoreMatchers.is(false));
        final ResultSet resultSet = connection.getMetaData().getCatalogs();
        final ResultSetMetaData metaData = resultSet.getMetaData();
        Assert.assertThat(metaData.getColumnCount(), CoreMatchers.is(1));
        Assert.assertThat(metaData.getColumnName(1), CoreMatchers.is("TABLE_CAT"));
        Assert.assertThat(resultSet.next(), CoreMatchers.is(true));
        Assert.assertThat(resultSet.next(), CoreMatchers.is(false));
        resultSet.close();
        connection.close();
        Assert.assertThat(connection.isClosed(), CoreMatchers.is(true));
    }

    @Test
    public void testSchemasLocal() throws Exception {
        final Connection connection = DriverManager.getConnection(("jdbc:avatica:remote:factory=" + (CalciteRemoteDriverTest.LJS)));
        Assert.assertThat(connection.isClosed(), CoreMatchers.is(false));
        final ResultSet resultSet = connection.getMetaData().getSchemas();
        final ResultSetMetaData metaData = resultSet.getMetaData();
        Assert.assertThat(metaData.getColumnCount(), CoreMatchers.is(2));
        Assert.assertThat(metaData.getColumnName(1), CoreMatchers.is("TABLE_SCHEM"));
        Assert.assertThat(metaData.getColumnName(2), CoreMatchers.is("TABLE_CATALOG"));
        Assert.assertThat(resultSet.next(), CoreMatchers.is(true));
        Assert.assertThat(resultSet.getString(1), CoreMatchers.equalTo("POST"));
        Assert.assertThat(resultSet.getString(2), CoreMatchers.nullValue());
        Assert.assertThat(resultSet.next(), CoreMatchers.is(true));
        Assert.assertThat(resultSet.getString(1), CoreMatchers.equalTo("foodmart"));
        Assert.assertThat(resultSet.getString(2), CoreMatchers.nullValue());
        Assert.assertThat(resultSet.next(), CoreMatchers.is(true));
        Assert.assertThat(resultSet.next(), CoreMatchers.is(true));
        Assert.assertThat(resultSet.next(), CoreMatchers.is(false));
        resultSet.close();
        connection.close();
        Assert.assertThat(connection.isClosed(), CoreMatchers.is(true));
    }

    @Test
    public void testMetaFunctionsLocal() throws Exception {
        final Connection connection = CalciteAssert.hr().connect();
        Assert.assertThat(connection.isClosed(), CoreMatchers.is(false));
        for (Meta.DatabaseProperty p : DatabaseProperty.values()) {
            switch (p) {
                case GET_NUMERIC_FUNCTIONS :
                    Assert.assertThat(connection.getMetaData().getNumericFunctions(), CoreMatchers.not(CoreMatchers.equalTo("")));
                    break;
                case GET_SYSTEM_FUNCTIONS :
                    Assert.assertThat(connection.getMetaData().getSystemFunctions(), CoreMatchers.notNullValue());
                    break;
                case GET_TIME_DATE_FUNCTIONS :
                    Assert.assertThat(connection.getMetaData().getTimeDateFunctions(), CoreMatchers.not(CoreMatchers.equalTo("")));
                    break;
                case GET_S_Q_L_KEYWORDS :
                    Assert.assertThat(connection.getMetaData().getSQLKeywords(), CoreMatchers.not(CoreMatchers.equalTo("")));
                    break;
                case GET_STRING_FUNCTIONS :
                    Assert.assertThat(connection.getMetaData().getStringFunctions(), CoreMatchers.not(CoreMatchers.equalTo("")));
                    break;
                default :
            }
        }
        connection.close();
        Assert.assertThat(connection.isClosed(), CoreMatchers.is(true));
    }

    @Test
    public void testRemoteCatalogs() throws Exception {
        CalciteAssert.hr().with(CalciteRemoteDriverTest.REMOTE_CONNECTION_FACTORY).metaData(CalciteRemoteDriverTest.GET_CATALOGS).returns("TABLE_CAT=null\n");
    }

    @Test
    public void testRemoteSchemas() throws Exception {
        CalciteAssert.hr().with(CalciteRemoteDriverTest.REMOTE_CONNECTION_FACTORY).metaData(CalciteRemoteDriverTest.GET_SCHEMAS).returns(("TABLE_SCHEM=POST; TABLE_CATALOG=null\n" + (("TABLE_SCHEM=foodmart; TABLE_CATALOG=null\n" + "TABLE_SCHEM=hr; TABLE_CATALOG=null\n") + "TABLE_SCHEM=metadata; TABLE_CATALOG=null\n")));
    }

    @Test
    public void testRemoteColumns() throws Exception {
        CalciteAssert.hr().with(CalciteRemoteDriverTest.REMOTE_CONNECTION_FACTORY).metaData(CalciteRemoteDriverTest.GET_COLUMNS).returns(CalciteAssert.checkResultContains("COLUMN_NAME=EMPNO"));
    }

    @Test
    public void testRemoteTypeInfo() throws Exception {
        CalciteAssert.hr().with(CalciteRemoteDriverTest.REMOTE_CONNECTION_FACTORY).metaData(CalciteRemoteDriverTest.GET_TYPEINFO).returns(CalciteAssert.checkResultCount(CoreMatchers.is(45)));
    }

    @Test
    public void testRemoteTableTypes() throws Exception {
        CalciteAssert.hr().with(CalciteRemoteDriverTest.REMOTE_CONNECTION_FACTORY).metaData(CalciteRemoteDriverTest.GET_TABLE_TYPES).returns(("TABLE_TYPE=TABLE\n" + "TABLE_TYPE=VIEW\n"));
    }

    @Test
    public void testRemoteExecuteQuery() throws Exception {
        CalciteAssert.hr().with(CalciteRemoteDriverTest.REMOTE_CONNECTION_FACTORY).query("values (1, 'a'), (cast(null as integer), 'b')").returnsUnordered("EXPR$0=1; EXPR$1=a", "EXPR$0=null; EXPR$1=b");
    }

    /**
     * Same query as {@link #testRemoteExecuteQuery()}, run without the test
     * infrastructure.
     */
    @Test
    public void testRemoteExecuteQuery2() throws Exception {
        try (Connection remoteConnection = CalciteRemoteDriverTest.getRemoteConnection()) {
            final Statement statement = remoteConnection.createStatement();
            final String sql = "values (1, 'a'), (cast(null as integer), 'b')";
            final ResultSet resultSet = statement.executeQuery(sql);
            int n = 0;
            while (resultSet.next()) {
                ++n;
            } 
            Assert.assertThat(n, CoreMatchers.equalTo(2));
        }
    }

    /**
     * For each (source, destination) type, make sure that we can convert bind
     * variables.
     */
    @Test
    public void testParameterConvert() throws Exception {
        final StringBuilder sql = new StringBuilder("select 1");
        final Map<SqlType, Integer> map = new HashMap<>();
        for (Map.Entry<Class, SqlType> entry : SqlType.getSetConversions()) {
            final SqlType sqlType = entry.getValue();
            switch (sqlType) {
                case BIT :
                case LONGVARCHAR :
                case LONGVARBINARY :
                case NCHAR :
                case NVARCHAR :
                case LONGNVARCHAR :
                case BLOB :
                case CLOB :
                case NCLOB :
                case ARRAY :
                case REF :
                case STRUCT :
                case DATALINK :
                case ROWID :
                case JAVA_OBJECT :
                case SQLXML :
                    continue;
            }
            if (!(map.containsKey(sqlType))) {
                sql.append(", cast(? as ").append(sqlType).append(")");
                map.put(sqlType, ((map.size()) + 1));
            }
        }
        sql.append(" from (values 1)");
        final PreparedStatement statement = CalciteRemoteDriverTest.localConnection.prepareStatement(sql.toString());
        for (Map.Entry<SqlType, Integer> entry : map.entrySet()) {
            statement.setNull(entry.getValue(), entry.getKey().id);
        }
        for (Map.Entry<Class, SqlType> entry : SqlType.getSetConversions()) {
            final SqlType sqlType = entry.getValue();
            if (!(map.containsKey(sqlType))) {
                continue;
            }
            int param = map.get(sqlType);
            Class clazz = entry.getKey();
            for (Object sampleValue : CalciteRemoteDriverTest.values(sqlType.boxedClass())) {
                switch (sqlType) {
                    case DATE :
                    case TIME :
                    case TIMESTAMP :
                        continue;// FIXME

                }
                if (clazz == (Calendar.class)) {
                    continue;// FIXME

                }
                final Object o;
                try {
                    o = convert(sampleValue, clazz);
                } catch (IllegalArgumentException | ParseException e) {
                    continue;
                }
                out.println(((((((("check " + o) + " (originally ") + (sampleValue.getClass())) + ", now ") + (o.getClass())) + ") converted to ") + sqlType));
                if (((o instanceof Double) && (o.equals(Double.POSITIVE_INFINITY))) || ((o instanceof Float) && (o.equals(Float.POSITIVE_INFINITY)))) {
                    continue;
                }
                statement.setObject(param, o, sqlType.id);
                final ResultSet resultSet = statement.executeQuery();
                Assert.assertThat(resultSet.next(), CoreMatchers.is(true));
                out.println(resultSet.getString((param + 1)));
            }
        }
        statement.close();
    }

    /**
     * Check that the "set" conversion table looks like Table B-5 in JDBC 4.1
     * specification
     */
    @Test
    public void testTableB5() {
        SqlType[] columns = new SqlType[]{ SqlType.TINYINT, SqlType.SMALLINT, SqlType.INTEGER, SqlType.BIGINT, SqlType.REAL, SqlType.FLOAT, SqlType.DOUBLE, SqlType.DECIMAL, SqlType.NUMERIC, SqlType.BIT, SqlType.BOOLEAN, SqlType.CHAR, SqlType.VARCHAR, SqlType.LONGVARCHAR, SqlType.BINARY, SqlType.VARBINARY, SqlType.LONGVARBINARY, SqlType.DATE, SqlType.TIME, SqlType.TIMESTAMP, SqlType.ARRAY, SqlType.BLOB, SqlType.CLOB, SqlType.STRUCT, SqlType.REF, SqlType.DATALINK, SqlType.JAVA_OBJECT, SqlType.ROWID, SqlType.NCHAR, SqlType.NVARCHAR, SqlType.LONGNVARCHAR, SqlType.NCLOB, SqlType.SQLXML };
        Class[] rows = new Class[]{ String.class, BigDecimal.class, Boolean.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, byte[].class, BigInteger.class, Date.class, Time.class, Timestamp.class, Array.class, Blob.class, Clob.class, Struct.class, Ref.class, URL.class, Class.class, RowId.class, NClob.class, SQLXML.class, Calendar.class, java.util.Date.class };
        for (Class row : rows) {
            final String s = (row == (java.util.Date.class)) ? row.getName() : row.getSimpleName();
            out.print(pad(s));
            for (SqlType column : columns) {
                out.print((SqlType.canSet(row, column) ? "x " : ". "));
            }
            out.println();
        }
    }

    /**
     * Check that the "get" conversion table looks like Table B-5 in JDBC 4.1
     * specification
     */
    @Test
    public void testTableB6() {
        SqlType[] columns = new SqlType[]{ SqlType.TINYINT, SqlType.SMALLINT, SqlType.INTEGER, SqlType.BIGINT, SqlType.REAL, SqlType.FLOAT, SqlType.DOUBLE, SqlType.DECIMAL, SqlType.NUMERIC, SqlType.BIT, SqlType.BOOLEAN, SqlType.CHAR, SqlType.VARCHAR, SqlType.LONGVARCHAR, SqlType.BINARY, SqlType.VARBINARY, SqlType.LONGVARBINARY, SqlType.DATE, SqlType.TIME, SqlType.TIMESTAMP, SqlType.CLOB, SqlType.BLOB, SqlType.ARRAY, SqlType.REF, SqlType.DATALINK, SqlType.STRUCT, SqlType.JAVA_OBJECT, SqlType.ROWID, SqlType.NCHAR, SqlType.NVARCHAR, SqlType.LONGNVARCHAR, SqlType.NCLOB, SqlType.SQLXML };
        final PrintWriter out = (DEBUG.value()) ? Util.printWriter(System.out) : new PrintWriter(new StringWriter());
        for (SqlType.Method row : Method.values()) {
            out.print(pad(row.methodName));
            for (SqlType column : columns) {
                out.print((SqlType.canGet(row, column) ? "x " : ". "));
            }
            out.println();
        }
    }

    /**
     * Checks {@link Statement#execute} on a query over a remote connection.
     *
     * <p>Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-646">[CALCITE-646]
     * AvaticaStatement execute method broken over remote JDBC</a>.
     */
    @Test
    public void testRemoteStatementExecute() throws Exception {
        try (Connection remoteConnection = CalciteRemoteDriverTest.getRemoteConnection()) {
            final Statement statement = remoteConnection.createStatement();
            final boolean status = statement.execute("values (1, 2), (3, 4), (5, 6)");
            Assert.assertThat(status, CoreMatchers.is(true));
            final ResultSet resultSet = statement.getResultSet();
            int n = 0;
            while (resultSet.next()) {
                ++n;
            } 
            Assert.assertThat(n, CoreMatchers.equalTo(3));
        }
    }

    @Test(expected = SQLException.class)
    public void testAvaticaConnectionException() throws Exception {
        try (Connection remoteConnection = CalciteRemoteDriverTest.getRemoteConnection()) {
            remoteConnection.isValid((-1));
        }
    }

    @Test(expected = SQLException.class)
    public void testAvaticaStatementException() throws Exception {
        try (Connection remoteConnection = CalciteRemoteDriverTest.getRemoteConnection()) {
            try (Statement statement = remoteConnection.createStatement()) {
                statement.setCursorName("foo");
            }
        }
    }

    @Test
    public void testAvaticaStatementGetMoreResults() throws Exception {
        try (Connection remoteConnection = CalciteRemoteDriverTest.getRemoteConnection()) {
            try (Statement statement = remoteConnection.createStatement()) {
                Assert.assertThat(statement.getMoreResults(), CoreMatchers.is(false));
            }
        }
    }

    @Test
    public void testRemoteExecute() throws Exception {
        try (Connection remoteConnection = CalciteRemoteDriverTest.getRemoteConnection()) {
            ResultSet resultSet = remoteConnection.createStatement().executeQuery("select * from \"hr\".\"emps\"");
            int count = 0;
            while (resultSet.next()) {
                ++count;
            } 
            Assert.assertThat((count > 0), CoreMatchers.is(true));
        }
    }

    @Test
    public void testRemoteExecuteMaxRow() throws Exception {
        try (Connection remoteConnection = CalciteRemoteDriverTest.getRemoteConnection()) {
            Statement statement = remoteConnection.createStatement();
            statement.setMaxRows(2);
            ResultSet resultSet = statement.executeQuery("select * from \"hr\".\"emps\"");
            int count = 0;
            while (resultSet.next()) {
                ++count;
            } 
            Assert.assertThat(count, CoreMatchers.equalTo(2));
        }
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-661">[CALCITE-661]
     * Remote fetch in Calcite JDBC driver</a>.
     */
    @Test
    public void testRemotePrepareExecute() throws Exception {
        try (Connection remoteConnection = CalciteRemoteDriverTest.getRemoteConnection()) {
            final PreparedStatement preparedStatement = remoteConnection.prepareStatement("select * from \"hr\".\"emps\"");
            ResultSet resultSet = preparedStatement.executeQuery();
            int count = 0;
            while (resultSet.next()) {
                ++count;
            } 
            Assert.assertThat((count > 0), CoreMatchers.is(true));
        }
    }

    @Test
    public void testLocalStatementFetch() throws Exception {
        Connection conn = CalciteRemoteDriverTest.makeConnection();
        String sql = "select * from \"foo\".\"bar\"";
        Statement statement = conn.createStatement();
        boolean status = statement.execute(sql);
        Assert.assertThat(status, CoreMatchers.is(true));
        ResultSet resultSet = statement.getResultSet();
        int count = 0;
        while (resultSet.next()) {
            count += 1;
        } 
        Assert.assertThat(count, CoreMatchers.is(101));
    }

    /**
     * Test that returns all result sets in one go.
     */
    @Test
    public void testLocalPreparedStatementFetch() throws Exception {
        Connection conn = CalciteRemoteDriverTest.makeConnection();
        Assert.assertThat(conn.isClosed(), CoreMatchers.is(false));
        String sql = "select * from \"foo\".\"bar\"";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        Assert.assertThat(conn.isClosed(), CoreMatchers.is(false));
        boolean status = preparedStatement.execute();
        Assert.assertThat(status, CoreMatchers.is(true));
        ResultSet resultSet = preparedStatement.getResultSet();
        Assert.assertThat(resultSet, CoreMatchers.notNullValue());
        int count = 0;
        while (resultSet.next()) {
            Assert.assertThat(resultSet.getObject(1), CoreMatchers.notNullValue());
            count += 1;
        } 
        Assert.assertThat(count, CoreMatchers.is(101));
    }

    @Test
    public void testRemoteStatementFetch() throws Exception {
        final Connection connection = DriverManager.getConnection(("jdbc:avatica:remote:factory=" + (CalciteRemoteDriverTest.LocalServiceMoreFactory.class.getName())));
        String sql = "select * from \"foo\".\"bar\"";
        Statement statement = connection.createStatement();
        boolean status = statement.execute(sql);
        Assert.assertThat(status, CoreMatchers.is(true));
        ResultSet resultSet = statement.getResultSet();
        int count = 0;
        while (resultSet.next()) {
            count += 1;
        } 
        Assert.assertThat(count, CoreMatchers.is(101));
    }

    @Test
    public void testRemotePreparedStatementFetch() throws Exception {
        final Connection connection = DriverManager.getConnection(("jdbc:avatica:remote:factory=" + (CalciteRemoteDriverTest.LocalServiceMoreFactory.class.getName())));
        Assert.assertThat(connection.isClosed(), CoreMatchers.is(false));
        String sql = "select * from \"foo\".\"bar\"";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        Assert.assertThat(preparedStatement.isClosed(), CoreMatchers.is(false));
        boolean status = preparedStatement.execute();
        Assert.assertThat(status, CoreMatchers.is(true));
        ResultSet resultSet = preparedStatement.getResultSet();
        Assert.assertThat(resultSet, CoreMatchers.notNullValue());
        int count = 0;
        while (resultSet.next()) {
            Assert.assertThat(resultSet.getObject(1), CoreMatchers.notNullValue());
            count += 1;
        } 
        Assert.assertThat(count, CoreMatchers.is(101));
    }

    /**
     * Service factory that creates a Calcite instance with more data.
     */
    public static class LocalServiceMoreFactory implements Service.Factory {
        @Override
        public Service create(AvaticaConnection connection) {
            try {
                Connection conn = CalciteRemoteDriverTest.makeConnection();
                final CalciteMetaImpl meta = new CalciteMetaImpl(conn.unwrap(CalciteConnectionImpl.class));
                return new org.apache.calcite.avatica.remote.LocalService(meta);
            } catch (Exception e) {
                throw TestUtil.rethrow(e);
            }
        }
    }

    /**
     * A bunch of sample values of various types.
     */
    private static final List<Object> SAMPLE_VALUES = // byte
    // short
    // int
    // long
    // float
    // double
    // BigDecimal
    // datetime
    // string
    // byte[]
    ImmutableList.of(false, true, ((byte) (0)), ((byte) (1)), Byte.MIN_VALUE, Byte.MAX_VALUE, ((short) (0)), ((short) (1)), Short.MIN_VALUE, Short.MAX_VALUE, ((short) (Byte.MIN_VALUE)), ((short) (Byte.MAX_VALUE)), 0, 1, (-3), Integer.MIN_VALUE, Integer.MAX_VALUE, ((int) (Short.MIN_VALUE)), ((int) (Short.MAX_VALUE)), ((int) (Byte.MIN_VALUE)), ((int) (Byte.MAX_VALUE)), 0L, 1L, (-2L), Long.MIN_VALUE, Long.MAX_VALUE, ((long) (Integer.MIN_VALUE)), ((long) (Integer.MAX_VALUE)), ((long) (Short.MIN_VALUE)), ((long) (Short.MAX_VALUE)), ((long) (Byte.MIN_VALUE)), ((long) (Byte.MAX_VALUE)), 0.0F, 1.5F, (-10.0F), Float.MIN_VALUE, Float.MAX_VALUE, 0.0, Math.PI, Double.MIN_VALUE, Double.MAX_VALUE, ((double) (Float.MIN_VALUE)), ((double) (Float.MAX_VALUE)), ((double) (Integer.MIN_VALUE)), ((double) (Integer.MAX_VALUE)), BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.valueOf(2.5), BigDecimal.valueOf(Double.MAX_VALUE), BigDecimal.valueOf(Long.MIN_VALUE), new Timestamp(0), new Date(0), new Time(0), "", "foo", " foo! Baz ", new byte[0], "hello".getBytes(StandardCharsets.UTF_8));

    /**
     * Factory that creates a {@link Meta} that can see the test databases.
     */
    public static class Factory implements Meta.Factory {
        public Meta create(List<String> args) {
            try {
                final Connection connection = CalciteAssert.hr().connect();
                return new CalciteMetaImpl(((CalciteConnectionImpl) (connection)));
            } catch (Exception e) {
                throw TestUtil.rethrow(e);
            }
        }
    }

    /**
     * Factory that creates a {@code LocalJsonService}.
     */
    public static class Factory2 implements Service.Factory {
        public Service create(AvaticaConnection connection) {
            try {
                Connection localConnection = CalciteAssert.hr().connect();
                final Meta meta = TROJAN.getMeta(((CalciteConnectionImpl) (localConnection)));
                return new LocalJsonService(new org.apache.calcite.avatica.remote.LocalService(meta));
            } catch (Exception e) {
                throw TestUtil.rethrow(e);
            }
        }
    }

    /**
     * Factory that creates a Service with connection to a modifiable table.
     */
    public static class LocalServiceModifiableFactory implements Service.Factory {
        @Override
        public Service create(AvaticaConnection connection) {
            try {
                Connection conn = JdbcFrontLinqBackTest.makeConnection();
                final CalciteMetaImpl meta = new CalciteMetaImpl(conn.unwrap(CalciteConnectionImpl.class));
                return new org.apache.calcite.avatica.remote.LocalService(meta);
            } catch (Exception e) {
                throw TestUtil.rethrow(e);
            }
        }
    }

    /**
     * Test remote Statement insert.
     */
    @Test
    public void testInsert() throws Exception {
        final Connection connection = DriverManager.getConnection(("jdbc:avatica:remote:factory=" + (CalciteRemoteDriverTest.LocalServiceModifiableFactory.class.getName())));
        Assert.assertThat(connection.isClosed(), CoreMatchers.is(false));
        Statement statement = connection.createStatement();
        Assert.assertThat(statement.isClosed(), CoreMatchers.is(false));
        String sql = "insert into \"foo\".\"bar\" values (1, 1, \'second\', 2, 2)";
        boolean status = statement.execute(sql);
        Assert.assertThat(status, CoreMatchers.is(false));
        ResultSet resultSet = statement.getResultSet();
        Assert.assertThat(resultSet, CoreMatchers.nullValue());
        int updateCount = statement.getUpdateCount();
        Assert.assertThat(updateCount, CoreMatchers.is(1));
        connection.close();
    }

    /**
     * Test remote Statement batched insert.
     */
    @Test
    public void testInsertBatch() throws Exception {
        final Connection connection = DriverManager.getConnection(("jdbc:avatica:remote:factory=" + (CalciteRemoteDriverTest.LocalServiceModifiableFactory.class.getName())));
        Assert.assertThat(connection.getMetaData().supportsBatchUpdates(), CoreMatchers.is(true));
        Assert.assertThat(connection.isClosed(), CoreMatchers.is(false));
        Statement statement = connection.createStatement();
        Assert.assertThat(statement.isClosed(), CoreMatchers.is(false));
        String sql = "insert into \"foo\".\"bar\" values (1, 1, \'second\', 2, 2)";
        statement.addBatch(sql);
        statement.addBatch(sql);
        int[] updateCounts = statement.executeBatch();
        Assert.assertThat(updateCounts.length, CoreMatchers.is(2));
        Assert.assertThat(updateCounts[0], CoreMatchers.is(1));
        Assert.assertThat(updateCounts[1], CoreMatchers.is(1));
        ResultSet resultSet = statement.getResultSet();
        Assert.assertThat(resultSet, CoreMatchers.nullValue());
        // Now empty batch
        statement.clearBatch();
        updateCounts = statement.executeBatch();
        Assert.assertThat(updateCounts.length, CoreMatchers.is(0));
        resultSet = statement.getResultSet();
        Assert.assertThat(resultSet, CoreMatchers.nullValue());
        connection.close();
    }

    /**
     * Remote PreparedStatement insert WITHOUT bind variables
     */
    @Test
    public void testRemotePreparedStatementInsert() throws Exception {
        final Connection connection = DriverManager.getConnection(("jdbc:avatica:remote:factory=" + (CalciteRemoteDriverTest.LocalServiceModifiableFactory.class.getName())));
        Assert.assertThat(connection.isClosed(), CoreMatchers.is(false));
        String sql = "insert into \"foo\".\"bar\" values (1, 1, \'second\', 2, 2)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        Assert.assertThat(preparedStatement.isClosed(), CoreMatchers.is(false));
        boolean status = preparedStatement.execute();
        Assert.assertThat(status, CoreMatchers.is(false));
        ResultSet resultSet = preparedStatement.getResultSet();
        Assert.assertThat(resultSet, CoreMatchers.nullValue());
        int updateCount = preparedStatement.getUpdateCount();
        Assert.assertThat(updateCount, CoreMatchers.is(1));
    }
}

/**
 * End CalciteRemoteDriverTest.java
 */
