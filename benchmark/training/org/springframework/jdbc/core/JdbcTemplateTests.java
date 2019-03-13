/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.jdbc.core;


import java.sql.BatchUpdateException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.SQLWarningException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.support.AbstractInterruptibleBatchPreparedStatementSetter;
import org.springframework.jdbc.datasource.ConnectionProxy;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.SQLStateSQLExceptionTranslator;
import org.springframework.util.LinkedCaseInsensitiveMap;


/**
 * Mock object based tests for JdbcTemplate.
 *
 * @author Rod Johnson
 * @author Thomas Risberg
 * @author Juergen Hoeller
 * @author Phillip Webb
 */
public class JdbcTemplateTests {
    private Connection connection;

    private DataSource dataSource;

    private PreparedStatement preparedStatement;

    private Statement statement;

    private ResultSet resultSet;

    private JdbcTemplate template;

    private CallableStatement callableStatement;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testBeanProperties() throws Exception {
        Assert.assertTrue("datasource ok", ((this.template.getDataSource()) == (this.dataSource)));
        Assert.assertTrue("ignores warnings by default", this.template.isIgnoreWarnings());
        this.template.setIgnoreWarnings(false);
        Assert.assertTrue("can set NOT to ignore warnings", (!(this.template.isIgnoreWarnings())));
    }

    @Test
    public void testUpdateCount() throws Exception {
        final String sql = "UPDATE INVOICE SET DATE_DISPATCHED = SYSDATE WHERE ID = ?";
        int idParam = 11111;
        BDDMockito.given(this.preparedStatement.executeUpdate()).willReturn(1);
        JdbcTemplateTests.Dispatcher d = new JdbcTemplateTests.Dispatcher(idParam, sql);
        int rowsAffected = this.template.update(d);
        Assert.assertTrue("1 update affected 1 row", (rowsAffected == 1));
        Mockito.verify(this.preparedStatement).setInt(1, idParam);
        Mockito.verify(this.preparedStatement).close();
        Mockito.verify(this.connection).close();
    }

    @Test
    public void testBogusUpdate() throws Exception {
        final String sql = "UPDATE NOSUCHTABLE SET DATE_DISPATCHED = SYSDATE WHERE ID = ?";
        final int idParam = 6666;
        // It's because Integers aren't canonical
        SQLException sqlException = new SQLException("bad update");
        BDDMockito.given(this.preparedStatement.executeUpdate()).willThrow(sqlException);
        JdbcTemplateTests.Dispatcher d = new JdbcTemplateTests.Dispatcher(idParam, sql);
        this.thrown.expect(UncategorizedSQLException.class);
        this.thrown.expect(exceptionCause(equalTo(sqlException)));
        try {
            this.template.update(d);
        } finally {
            Mockito.verify(this.preparedStatement).setInt(1, idParam);
            Mockito.verify(this.preparedStatement).close();
            Mockito.verify(this.connection, Mockito.atLeastOnce()).close();
        }
    }

    @Test
    public void testStringsWithStaticSql() throws Exception {
        doTestStrings(null, null, null, null, ( template, sql, rch) -> template.query(sql, rch));
    }

    @Test
    public void testStringsWithStaticSqlAndFetchSizeAndMaxRows() throws Exception {
        doTestStrings(10, 20, 30, null, ( template, sql, rch) -> template.query(sql, rch));
    }

    @Test
    public void testStringsWithEmptyPreparedStatementSetter() throws Exception {
        doTestStrings(null, null, null, null, ( template, sql, rch) -> template.query(sql, ((PreparedStatementSetter) (null)), rch));
    }

    @Test
    public void testStringsWithPreparedStatementSetter() throws Exception {
        final Integer argument = 99;
        doTestStrings(null, null, null, argument, ( template, sql, rch) -> template.query(sql, ( ps) -> {
            ps.setObject(1, argument);
        }, rch));
    }

    @Test
    public void testStringsWithEmptyPreparedStatementArgs() throws Exception {
        doTestStrings(null, null, null, null, ( template, sql, rch) -> template.query(sql, ((Object[]) (null)), rch));
    }

    @Test
    public void testStringsWithPreparedStatementArgs() throws Exception {
        final Integer argument = 99;
        doTestStrings(null, null, null, argument, ( template, sql, rch) -> template.query(sql, new Object[]{ argument }, rch));
    }

    @Test
    public void testLeaveConnectionOpenOnRequest() throws Exception {
        String sql = "SELECT ID, FORENAME FROM CUSTMR WHERE ID < 3";
        BDDMockito.given(this.resultSet.next()).willReturn(false);
        BDDMockito.given(this.connection.isClosed()).willReturn(false);
        BDDMockito.given(this.connection.createStatement()).willReturn(this.preparedStatement);
        // if close is called entire test will fail
        BDDMockito.willThrow(new RuntimeException()).given(this.connection).close();
        SingleConnectionDataSource scf = new SingleConnectionDataSource(this.dataSource.getConnection(), false);
        this.template = new JdbcTemplate(scf, false);
        RowCountCallbackHandler rcch = new RowCountCallbackHandler();
        this.template.query(sql, rcch);
        Mockito.verify(this.resultSet).close();
        Mockito.verify(this.preparedStatement).close();
    }

    @Test
    public void testConnectionCallback() throws Exception {
        String result = this.template.execute(new ConnectionCallback<String>() {
            @Override
            public String doInConnection(Connection con) {
                Assert.assertTrue((con instanceof ConnectionProxy));
                Assert.assertSame(JdbcTemplateTests.this.connection, getTargetConnection());
                return "test";
            }
        });
        Assert.assertEquals("test", result);
    }

    @Test
    public void testConnectionCallbackWithStatementSettings() throws Exception {
        String result = this.template.execute(new ConnectionCallback<String>() {
            @Override
            public String doInConnection(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement("some SQL");
                ps.setFetchSize(10);
                ps.setMaxRows(20);
                ps.close();
                return "test";
            }
        });
        Assert.assertEquals("test", result);
        Mockito.verify(this.preparedStatement).setFetchSize(10);
        Mockito.verify(this.preparedStatement).setMaxRows(20);
        Mockito.verify(this.preparedStatement).close();
        Mockito.verify(this.connection).close();
    }

    @Test
    public void testCloseConnectionOnRequest() throws Exception {
        String sql = "SELECT ID, FORENAME FROM CUSTMR WHERE ID < 3";
        BDDMockito.given(this.resultSet.next()).willReturn(false);
        BDDMockito.given(this.connection.createStatement()).willReturn(this.preparedStatement);
        RowCountCallbackHandler rcch = new RowCountCallbackHandler();
        this.template.query(sql, rcch);
        Mockito.verify(this.resultSet).close();
        Mockito.verify(this.preparedStatement).close();
        Mockito.verify(this.connection).close();
    }

    /**
     * Test that we see a runtime exception come back.
     */
    @Test
    public void testExceptionComesBack() throws Exception {
        final String sql = "SELECT ID FROM CUSTMR";
        final RuntimeException runtimeException = new RuntimeException("Expected");
        BDDMockito.given(this.resultSet.next()).willReturn(true);
        BDDMockito.given(this.connection.createStatement()).willReturn(this.preparedStatement);
        this.thrown.expect(sameInstance(runtimeException));
        try {
            this.template.query(sql, ((RowCallbackHandler) (( rs) -> {
                throw runtimeException;
            })));
        } finally {
            Mockito.verify(this.resultSet).close();
            Mockito.verify(this.preparedStatement).close();
            Mockito.verify(this.connection).close();
        }
    }

    /**
     * Test update with static SQL.
     */
    @Test
    public void testSqlUpdate() throws Exception {
        final String sql = "UPDATE NOSUCHTABLE SET DATE_DISPATCHED = SYSDATE WHERE ID = 4";
        int rowsAffected = 33;
        BDDMockito.given(this.statement.executeUpdate(sql)).willReturn(rowsAffected);
        BDDMockito.given(this.connection.createStatement()).willReturn(this.statement);
        int actualRowsAffected = this.template.update(sql);
        Assert.assertTrue("Actual rows affected is correct", (actualRowsAffected == rowsAffected));
        Mockito.verify(this.statement).close();
        Mockito.verify(this.connection).close();
    }

    /**
     * Test update with dynamic SQL.
     */
    @Test
    public void testSqlUpdateWithArguments() throws Exception {
        final String sql = "UPDATE NOSUCHTABLE SET DATE_DISPATCHED = SYSDATE WHERE ID = ? and PR = ?";
        int rowsAffected = 33;
        BDDMockito.given(this.preparedStatement.executeUpdate()).willReturn(rowsAffected);
        int actualRowsAffected = this.template.update(sql, 4, new SqlParameterValue(Types.NUMERIC, 2, Float.valueOf(1.4142F)));
        Assert.assertTrue("Actual rows affected is correct", (actualRowsAffected == rowsAffected));
        Mockito.verify(this.preparedStatement).setObject(1, 4);
        Mockito.verify(this.preparedStatement).setObject(2, Float.valueOf(1.4142F), Types.NUMERIC, 2);
        Mockito.verify(this.preparedStatement).close();
        Mockito.verify(this.connection).close();
    }

    @Test
    public void testSqlUpdateEncountersSqlException() throws Exception {
        SQLException sqlException = new SQLException("bad update");
        final String sql = "UPDATE NOSUCHTABLE SET DATE_DISPATCHED = SYSDATE WHERE ID = 4";
        BDDMockito.given(this.statement.executeUpdate(sql)).willThrow(sqlException);
        BDDMockito.given(this.connection.createStatement()).willReturn(this.statement);
        this.thrown.expect(exceptionCause(sameInstance(sqlException)));
        try {
            this.template.update(sql);
        } finally {
            Mockito.verify(this.statement).close();
            Mockito.verify(this.connection, Mockito.atLeastOnce()).close();
        }
    }

    @Test
    public void testSqlUpdateWithThreadConnection() throws Exception {
        final String sql = "UPDATE NOSUCHTABLE SET DATE_DISPATCHED = SYSDATE WHERE ID = 4";
        int rowsAffected = 33;
        BDDMockito.given(this.statement.executeUpdate(sql)).willReturn(rowsAffected);
        BDDMockito.given(this.connection.createStatement()).willReturn(this.statement);
        int actualRowsAffected = this.template.update(sql);
        Assert.assertTrue("Actual rows affected is correct", (actualRowsAffected == rowsAffected));
        Mockito.verify(this.statement).close();
        Mockito.verify(this.connection).close();
    }

    @Test
    public void testBatchUpdate() throws Exception {
        final String[] sql = new String[]{ "UPDATE NOSUCHTABLE SET DATE_DISPATCHED = SYSDATE WHERE ID = 1", "UPDATE NOSUCHTABLE SET DATE_DISPATCHED = SYSDATE WHERE ID = 2" };
        BDDMockito.given(this.statement.executeBatch()).willReturn(new int[]{ 1, 1 });
        mockDatabaseMetaData(true);
        BDDMockito.given(this.connection.createStatement()).willReturn(this.statement);
        JdbcTemplate template = new JdbcTemplate(this.dataSource, false);
        int[] actualRowsAffected = template.batchUpdate(sql);
        Assert.assertTrue("executed 2 updates", ((actualRowsAffected.length) == 2));
        Mockito.verify(this.statement).addBatch(sql[0]);
        Mockito.verify(this.statement).addBatch(sql[1]);
        Mockito.verify(this.statement).close();
        Mockito.verify(this.connection, Mockito.atLeastOnce()).close();
    }

    @Test
    public void testBatchUpdateWithBatchFailure() throws Exception {
        final String[] sql = new String[]{ "A", "B", "C", "D" };
        BDDMockito.given(this.statement.executeBatch()).willThrow(new BatchUpdateException(new int[]{ 1, Statement.EXECUTE_FAILED, 1, Statement.EXECUTE_FAILED }));
        mockDatabaseMetaData(true);
        BDDMockito.given(this.connection.createStatement()).willReturn(this.statement);
        JdbcTemplate template = new JdbcTemplate(this.dataSource, false);
        try {
            template.batchUpdate(sql);
        } catch (UncategorizedSQLException ex) {
            Assert.assertThat(ex.getSql(), equalTo("B; D"));
        }
    }

    @Test
    public void testBatchUpdateWithNoBatchSupport() throws Exception {
        final String[] sql = new String[]{ "UPDATE NOSUCHTABLE SET DATE_DISPATCHED = SYSDATE WHERE ID = 1", "UPDATE NOSUCHTABLE SET DATE_DISPATCHED = SYSDATE WHERE ID = 2" };
        BDDMockito.given(this.statement.execute(sql[0])).willReturn(false);
        BDDMockito.given(this.statement.getUpdateCount()).willReturn(1, 1);
        BDDMockito.given(this.statement.execute(sql[1])).willReturn(false);
        mockDatabaseMetaData(false);
        BDDMockito.given(this.connection.createStatement()).willReturn(this.statement);
        JdbcTemplate template = new JdbcTemplate(this.dataSource, false);
        int[] actualRowsAffected = template.batchUpdate(sql);
        Assert.assertTrue("executed 2 updates", ((actualRowsAffected.length) == 2));
        Mockito.verify(this.statement, Mockito.never()).addBatch(ArgumentMatchers.anyString());
        Mockito.verify(this.statement).close();
        Mockito.verify(this.connection, Mockito.atLeastOnce()).close();
    }

    @Test
    public void testBatchUpdateWithNoBatchSupportAndSelect() throws Exception {
        final String[] sql = new String[]{ "UPDATE NOSUCHTABLE SET DATE_DISPATCHED = SYSDATE WHERE ID = 1", "SELECT * FROM NOSUCHTABLE" };
        BDDMockito.given(this.statement.execute(sql[0])).willReturn(false);
        BDDMockito.given(this.statement.getUpdateCount()).willReturn(1);
        BDDMockito.given(this.statement.execute(sql[1])).willReturn(true);
        mockDatabaseMetaData(false);
        BDDMockito.given(this.connection.createStatement()).willReturn(this.statement);
        JdbcTemplate template = new JdbcTemplate(this.dataSource, false);
        this.thrown.expect(InvalidDataAccessApiUsageException.class);
        try {
            template.batchUpdate(sql);
        } finally {
            Mockito.verify(this.statement, Mockito.never()).addBatch(ArgumentMatchers.anyString());
            Mockito.verify(this.statement).close();
            Mockito.verify(this.connection, Mockito.atLeastOnce()).close();
        }
    }

    @Test
    public void testBatchUpdateWithPreparedStatement() throws Exception {
        final String sql = "UPDATE NOSUCHTABLE SET DATE_DISPATCHED = SYSDATE WHERE ID = ?";
        final int[] ids = new int[]{ 100, 200 };
        final int[] rowsAffected = new int[]{ 1, 2 };
        BDDMockito.given(this.preparedStatement.executeBatch()).willReturn(rowsAffected);
        mockDatabaseMetaData(true);
        BatchPreparedStatementSetter setter = new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, ids[i]);
            }

            @Override
            public int getBatchSize() {
                return ids.length;
            }
        };
        JdbcTemplate template = new JdbcTemplate(this.dataSource, false);
        int[] actualRowsAffected = template.batchUpdate(sql, setter);
        Assert.assertTrue("executed 2 updates", ((actualRowsAffected.length) == 2));
        Assert.assertEquals(rowsAffected[0], actualRowsAffected[0]);
        Assert.assertEquals(rowsAffected[1], actualRowsAffected[1]);
        Mockito.verify(this.preparedStatement, Mockito.times(2)).addBatch();
        Mockito.verify(this.preparedStatement).setInt(1, ids[0]);
        Mockito.verify(this.preparedStatement).setInt(1, ids[1]);
        Mockito.verify(this.preparedStatement).close();
        Mockito.verify(this.connection, Mockito.atLeastOnce()).close();
    }

    @Test
    public void testInterruptibleBatchUpdate() throws Exception {
        final String sql = "UPDATE NOSUCHTABLE SET DATE_DISPATCHED = SYSDATE WHERE ID = ?";
        final int[] ids = new int[]{ 100, 200 };
        final int[] rowsAffected = new int[]{ 1, 2 };
        BDDMockito.given(this.preparedStatement.executeBatch()).willReturn(rowsAffected);
        mockDatabaseMetaData(true);
        BatchPreparedStatementSetter setter = new InterruptibleBatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                if (i < (ids.length)) {
                    ps.setInt(1, ids[i]);
                }
            }

            @Override
            public int getBatchSize() {
                return 1000;
            }

            @Override
            public boolean isBatchExhausted(int i) {
                return i >= (ids.length);
            }
        };
        JdbcTemplate template = new JdbcTemplate(this.dataSource, false);
        int[] actualRowsAffected = template.batchUpdate(sql, setter);
        Assert.assertTrue("executed 2 updates", ((actualRowsAffected.length) == 2));
        Assert.assertEquals(rowsAffected[0], actualRowsAffected[0]);
        Assert.assertEquals(rowsAffected[1], actualRowsAffected[1]);
        Mockito.verify(this.preparedStatement, Mockito.times(2)).addBatch();
        Mockito.verify(this.preparedStatement).setInt(1, ids[0]);
        Mockito.verify(this.preparedStatement).setInt(1, ids[1]);
        Mockito.verify(this.preparedStatement).close();
        Mockito.verify(this.connection, Mockito.atLeastOnce()).close();
    }

    @Test
    public void testInterruptibleBatchUpdateWithBaseClass() throws Exception {
        final String sql = "UPDATE NOSUCHTABLE SET DATE_DISPATCHED = SYSDATE WHERE ID = ?";
        final int[] ids = new int[]{ 100, 200 };
        final int[] rowsAffected = new int[]{ 1, 2 };
        BDDMockito.given(this.preparedStatement.executeBatch()).willReturn(rowsAffected);
        mockDatabaseMetaData(true);
        BatchPreparedStatementSetter setter = new AbstractInterruptibleBatchPreparedStatementSetter() {
            @Override
            protected boolean setValuesIfAvailable(PreparedStatement ps, int i) throws SQLException {
                if (i < (ids.length)) {
                    ps.setInt(1, ids[i]);
                    return true;
                } else {
                    return false;
                }
            }
        };
        JdbcTemplate template = new JdbcTemplate(this.dataSource, false);
        int[] actualRowsAffected = template.batchUpdate(sql, setter);
        Assert.assertTrue("executed 2 updates", ((actualRowsAffected.length) == 2));
        Assert.assertEquals(rowsAffected[0], actualRowsAffected[0]);
        Assert.assertEquals(rowsAffected[1], actualRowsAffected[1]);
        Mockito.verify(this.preparedStatement, Mockito.times(2)).addBatch();
        Mockito.verify(this.preparedStatement).setInt(1, ids[0]);
        Mockito.verify(this.preparedStatement).setInt(1, ids[1]);
        Mockito.verify(this.preparedStatement).close();
        Mockito.verify(this.connection, Mockito.atLeastOnce()).close();
    }

    @Test
    public void testInterruptibleBatchUpdateWithBaseClassAndNoBatchSupport() throws Exception {
        final String sql = "UPDATE NOSUCHTABLE SET DATE_DISPATCHED = SYSDATE WHERE ID = ?";
        final int[] ids = new int[]{ 100, 200 };
        final int[] rowsAffected = new int[]{ 1, 2 };
        BDDMockito.given(this.preparedStatement.executeUpdate()).willReturn(rowsAffected[0], rowsAffected[1]);
        mockDatabaseMetaData(false);
        BatchPreparedStatementSetter setter = new AbstractInterruptibleBatchPreparedStatementSetter() {
            @Override
            protected boolean setValuesIfAvailable(PreparedStatement ps, int i) throws SQLException {
                if (i < (ids.length)) {
                    ps.setInt(1, ids[i]);
                    return true;
                } else {
                    return false;
                }
            }
        };
        JdbcTemplate template = new JdbcTemplate(this.dataSource, false);
        int[] actualRowsAffected = template.batchUpdate(sql, setter);
        Assert.assertTrue("executed 2 updates", ((actualRowsAffected.length) == 2));
        Assert.assertEquals(rowsAffected[0], actualRowsAffected[0]);
        Assert.assertEquals(rowsAffected[1], actualRowsAffected[1]);
        Mockito.verify(this.preparedStatement, Mockito.never()).addBatch();
        Mockito.verify(this.preparedStatement).setInt(1, ids[0]);
        Mockito.verify(this.preparedStatement).setInt(1, ids[1]);
        Mockito.verify(this.preparedStatement).close();
        Mockito.verify(this.connection, Mockito.atLeastOnce()).close();
    }

    @Test
    public void testBatchUpdateWithPreparedStatementAndNoBatchSupport() throws Exception {
        final String sql = "UPDATE NOSUCHTABLE SET DATE_DISPATCHED = SYSDATE WHERE ID = ?";
        final int[] ids = new int[]{ 100, 200 };
        final int[] rowsAffected = new int[]{ 1, 2 };
        BDDMockito.given(this.preparedStatement.executeUpdate()).willReturn(rowsAffected[0], rowsAffected[1]);
        BatchPreparedStatementSetter setter = new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, ids[i]);
            }

            @Override
            public int getBatchSize() {
                return ids.length;
            }
        };
        int[] actualRowsAffected = this.template.batchUpdate(sql, setter);
        Assert.assertTrue("executed 2 updates", ((actualRowsAffected.length) == 2));
        Assert.assertEquals(rowsAffected[0], actualRowsAffected[0]);
        Assert.assertEquals(rowsAffected[1], actualRowsAffected[1]);
        Mockito.verify(this.preparedStatement, Mockito.never()).addBatch();
        Mockito.verify(this.preparedStatement).setInt(1, ids[0]);
        Mockito.verify(this.preparedStatement).setInt(1, ids[1]);
        Mockito.verify(this.preparedStatement).close();
        Mockito.verify(this.connection).close();
    }

    @Test
    public void testBatchUpdateFails() throws Exception {
        final String sql = "UPDATE NOSUCHTABLE SET DATE_DISPATCHED = SYSDATE WHERE ID = ?";
        final int[] ids = new int[]{ 100, 200 };
        SQLException sqlException = new SQLException();
        BDDMockito.given(this.preparedStatement.executeBatch()).willThrow(sqlException);
        mockDatabaseMetaData(true);
        BatchPreparedStatementSetter setter = new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, ids[i]);
            }

            @Override
            public int getBatchSize() {
                return ids.length;
            }
        };
        this.thrown.expect(DataAccessException.class);
        this.thrown.expect(exceptionCause(sameInstance(sqlException)));
        try {
            this.template.batchUpdate(sql, setter);
        } finally {
            Mockito.verify(this.preparedStatement, Mockito.times(2)).addBatch();
            Mockito.verify(this.preparedStatement).setInt(1, ids[0]);
            Mockito.verify(this.preparedStatement).setInt(1, ids[1]);
            Mockito.verify(this.preparedStatement).close();
            Mockito.verify(this.connection, Mockito.atLeastOnce()).close();
        }
    }

    @Test
    public void testBatchUpdateWithEmptyList() throws Exception {
        final String sql = "UPDATE NOSUCHTABLE SET DATE_DISPATCHED = SYSDATE WHERE ID = ?";
        JdbcTemplate template = new JdbcTemplate(this.dataSource, false);
        int[] actualRowsAffected = template.batchUpdate(sql, Collections.emptyList());
        Assert.assertTrue("executed 0 updates", ((actualRowsAffected.length) == 0));
    }

    @Test
    public void testBatchUpdateWithListOfObjectArrays() throws Exception {
        final String sql = "UPDATE NOSUCHTABLE SET DATE_DISPATCHED = SYSDATE WHERE ID = ?";
        final List<Object[]> ids = new ArrayList<>(2);
        ids.add(new Object[]{ 100 });
        ids.add(new Object[]{ 200 });
        final int[] rowsAffected = new int[]{ 1, 2 };
        BDDMockito.given(this.preparedStatement.executeBatch()).willReturn(rowsAffected);
        mockDatabaseMetaData(true);
        JdbcTemplate template = new JdbcTemplate(this.dataSource, false);
        int[] actualRowsAffected = template.batchUpdate(sql, ids);
        Assert.assertTrue("executed 2 updates", ((actualRowsAffected.length) == 2));
        Assert.assertEquals(rowsAffected[0], actualRowsAffected[0]);
        Assert.assertEquals(rowsAffected[1], actualRowsAffected[1]);
        Mockito.verify(this.preparedStatement, Mockito.times(2)).addBatch();
        Mockito.verify(this.preparedStatement).setObject(1, 100);
        Mockito.verify(this.preparedStatement).setObject(1, 200);
        Mockito.verify(this.preparedStatement).close();
        Mockito.verify(this.connection, Mockito.atLeastOnce()).close();
    }

    @Test
    public void testBatchUpdateWithListOfObjectArraysPlusTypeInfo() throws Exception {
        final String sql = "UPDATE NOSUCHTABLE SET DATE_DISPATCHED = SYSDATE WHERE ID = ?";
        final List<Object[]> ids = new ArrayList<>(2);
        ids.add(new Object[]{ 100 });
        ids.add(new Object[]{ 200 });
        final int[] sqlTypes = new int[]{ Types.NUMERIC };
        final int[] rowsAffected = new int[]{ 1, 2 };
        BDDMockito.given(this.preparedStatement.executeBatch()).willReturn(rowsAffected);
        mockDatabaseMetaData(true);
        this.template = new JdbcTemplate(this.dataSource, false);
        int[] actualRowsAffected = this.template.batchUpdate(sql, ids, sqlTypes);
        Assert.assertTrue("executed 2 updates", ((actualRowsAffected.length) == 2));
        Assert.assertEquals(rowsAffected[0], actualRowsAffected[0]);
        Assert.assertEquals(rowsAffected[1], actualRowsAffected[1]);
        Mockito.verify(this.preparedStatement, Mockito.times(2)).addBatch();
        Mockito.verify(this.preparedStatement).setObject(1, 100, sqlTypes[0]);
        Mockito.verify(this.preparedStatement).setObject(1, 200, sqlTypes[0]);
        Mockito.verify(this.preparedStatement).close();
        Mockito.verify(this.connection, Mockito.atLeastOnce()).close();
    }

    @Test
    public void testBatchUpdateWithCollectionOfObjects() throws Exception {
        final String sql = "UPDATE NOSUCHTABLE SET DATE_DISPATCHED = SYSDATE WHERE ID = ?";
        final List<Integer> ids = Arrays.asList(100, 200, 300);
        final int[] rowsAffected1 = new int[]{ 1, 2 };
        final int[] rowsAffected2 = new int[]{ 3 };
        BDDMockito.given(this.preparedStatement.executeBatch()).willReturn(rowsAffected1, rowsAffected2);
        mockDatabaseMetaData(true);
        ParameterizedPreparedStatementSetter<Integer> setter = ( ps, argument) -> ps.setInt(1, argument.intValue());
        JdbcTemplate template = new JdbcTemplate(this.dataSource, false);
        int[][] actualRowsAffected = template.batchUpdate(sql, ids, 2, setter);
        Assert.assertEquals("executed 2 updates", 2, actualRowsAffected[0].length);
        Assert.assertEquals(rowsAffected1[0], actualRowsAffected[0][0]);
        Assert.assertEquals(rowsAffected1[1], actualRowsAffected[0][1]);
        Assert.assertEquals(rowsAffected2[0], actualRowsAffected[1][0]);
        Mockito.verify(this.preparedStatement, Mockito.times(3)).addBatch();
        Mockito.verify(this.preparedStatement).setInt(1, ids.get(0));
        Mockito.verify(this.preparedStatement).setInt(1, ids.get(1));
        Mockito.verify(this.preparedStatement).setInt(1, ids.get(2));
        Mockito.verify(this.preparedStatement).close();
        Mockito.verify(this.connection, Mockito.atLeastOnce()).close();
    }

    @Test
    public void testCouldNotGetConnectionForOperationOrExceptionTranslator() throws SQLException {
        SQLException sqlException = new SQLException("foo", "07xxx");
        this.dataSource = Mockito.mock(DataSource.class);
        BDDMockito.given(this.dataSource.getConnection()).willThrow(sqlException);
        JdbcTemplate template = new JdbcTemplate(this.dataSource, false);
        RowCountCallbackHandler rcch = new RowCountCallbackHandler();
        this.thrown.expect(CannotGetJdbcConnectionException.class);
        this.thrown.expect(exceptionCause(sameInstance(sqlException)));
        template.query("SELECT ID, FORENAME FROM CUSTMR WHERE ID < 3", rcch);
    }

    @Test
    public void testCouldNotGetConnectionForOperationWithLazyExceptionTranslator() throws SQLException {
        SQLException sqlException = new SQLException("foo", "07xxx");
        this.dataSource = Mockito.mock(DataSource.class);
        BDDMockito.given(this.dataSource.getConnection()).willThrow(sqlException);
        this.template = new JdbcTemplate();
        this.template.setDataSource(this.dataSource);
        this.template.afterPropertiesSet();
        RowCountCallbackHandler rcch = new RowCountCallbackHandler();
        this.thrown.expect(CannotGetJdbcConnectionException.class);
        this.thrown.expect(exceptionCause(sameInstance(sqlException)));
        this.template.query("SELECT ID, FORENAME FROM CUSTMR WHERE ID < 3", rcch);
    }

    @Test
    public void testCouldNotGetConnectionInOperationWithExceptionTranslatorInitializedViaBeanProperty() throws SQLException {
        doTestCouldNotGetConnectionInOperationWithExceptionTranslatorInitialized(true);
    }

    @Test
    public void testCouldNotGetConnectionInOperationWithExceptionTranslatorInitializedInAfterPropertiesSet() throws SQLException {
        doTestCouldNotGetConnectionInOperationWithExceptionTranslatorInitialized(false);
    }

    @Test
    public void testPreparedStatementSetterSucceeds() throws Exception {
        final String sql = "UPDATE FOO SET NAME=? WHERE ID = 1";
        final String name = "Gary";
        int expectedRowsUpdated = 1;
        BDDMockito.given(this.preparedStatement.executeUpdate()).willReturn(expectedRowsUpdated);
        PreparedStatementSetter pss = ( ps) -> ps.setString(1, name);
        int actualRowsUpdated = new JdbcTemplate(this.dataSource).update(sql, pss);
        Assert.assertEquals("updated correct # of rows", actualRowsUpdated, expectedRowsUpdated);
        Mockito.verify(this.preparedStatement).setString(1, name);
        Mockito.verify(this.preparedStatement).close();
        Mockito.verify(this.connection).close();
    }

    @Test
    public void testPreparedStatementSetterFails() throws Exception {
        final String sql = "UPDATE FOO SET NAME=? WHERE ID = 1";
        final String name = "Gary";
        SQLException sqlException = new SQLException();
        BDDMockito.given(this.preparedStatement.executeUpdate()).willThrow(sqlException);
        PreparedStatementSetter pss = ( ps) -> ps.setString(1, name);
        this.thrown.expect(DataAccessException.class);
        this.thrown.expect(exceptionCause(sameInstance(sqlException)));
        try {
            new JdbcTemplate(this.dataSource).update(sql, pss);
        } finally {
            Mockito.verify(this.preparedStatement).setString(1, name);
            Mockito.verify(this.preparedStatement).close();
            Mockito.verify(this.connection, Mockito.atLeastOnce()).close();
        }
    }

    @Test
    public void testCouldNotClose() throws Exception {
        SQLException sqlException = new SQLException("bar");
        BDDMockito.given(this.connection.createStatement()).willReturn(this.statement);
        BDDMockito.given(this.resultSet.next()).willReturn(false);
        BDDMockito.willThrow(sqlException).given(this.resultSet).close();
        BDDMockito.willThrow(sqlException).given(this.statement).close();
        BDDMockito.willThrow(sqlException).given(this.connection).close();
        RowCountCallbackHandler rcch = new RowCountCallbackHandler();
        this.template.query("SELECT ID, FORENAME FROM CUSTMR WHERE ID < 3", rcch);
        Mockito.verify(this.connection).close();
    }

    /**
     * Mock objects allow us to produce warnings at will
     */
    @Test
    public void testFatalWarning() throws Exception {
        String sql = "SELECT forename from custmr";
        SQLWarning warnings = new SQLWarning("My warning");
        BDDMockito.given(this.resultSet.next()).willReturn(false);
        BDDMockito.given(this.preparedStatement.getWarnings()).willReturn(warnings);
        BDDMockito.given(this.connection.createStatement()).willReturn(this.preparedStatement);
        JdbcTemplate t = new JdbcTemplate(this.dataSource);
        t.setIgnoreWarnings(false);
        this.thrown.expect(SQLWarningException.class);
        this.thrown.expect(exceptionCause(sameInstance(warnings)));
        try {
            t.query(sql, ( rs) -> {
                rs.getByte(1);
            });
        } finally {
            Mockito.verify(this.resultSet).close();
            Mockito.verify(this.preparedStatement).close();
            Mockito.verify(this.connection).close();
        }
    }

    @Test
    public void testIgnoredWarning() throws Exception {
        String sql = "SELECT forename from custmr";
        SQLWarning warnings = new SQLWarning("My warning");
        BDDMockito.given(this.resultSet.next()).willReturn(false);
        BDDMockito.given(this.connection.createStatement()).willReturn(this.preparedStatement);
        BDDMockito.given(this.preparedStatement.getWarnings()).willReturn(warnings);
        // Too long: truncation
        this.template.setIgnoreWarnings(true);
        this.template.query(sql, ( rs) -> {
            rs.getByte(1);
        });
        Mockito.verify(this.resultSet).close();
        Mockito.verify(this.preparedStatement).close();
        Mockito.verify(this.connection).close();
    }

    @Test
    public void testSQLErrorCodeTranslation() throws Exception {
        final SQLException sqlException = new SQLException("I have a known problem", "99999", 1054);
        final String sql = "SELECT ID FROM CUSTOMER";
        BDDMockito.given(this.resultSet.next()).willReturn(true);
        mockDatabaseMetaData(false);
        BDDMockito.given(this.connection.createStatement()).willReturn(this.preparedStatement);
        this.thrown.expect(BadSqlGrammarException.class);
        this.thrown.expect(exceptionCause(sameInstance(sqlException)));
        try {
            this.template.query(sql, ((RowCallbackHandler) (( rs) -> {
                throw sqlException;
            })));
            Assert.fail("Should have thrown BadSqlGrammarException");
        } finally {
            Mockito.verify(this.resultSet).close();
            Mockito.verify(this.preparedStatement).close();
            Mockito.verify(this.connection, Mockito.atLeastOnce()).close();
        }
    }

    @Test
    public void testSQLErrorCodeTranslationWithSpecifiedDbName() throws Exception {
        final SQLException sqlException = new SQLException("I have a known problem", "99999", 1054);
        final String sql = "SELECT ID FROM CUSTOMER";
        BDDMockito.given(this.resultSet.next()).willReturn(true);
        BDDMockito.given(this.connection.createStatement()).willReturn(this.preparedStatement);
        JdbcTemplate template = new JdbcTemplate();
        template.setDataSource(this.dataSource);
        template.setDatabaseProductName("MySQL");
        template.afterPropertiesSet();
        this.thrown.expect(BadSqlGrammarException.class);
        this.thrown.expect(exceptionCause(sameInstance(sqlException)));
        try {
            template.query(sql, ((RowCallbackHandler) (( rs) -> {
                throw sqlException;
            })));
        } finally {
            Mockito.verify(this.resultSet).close();
            Mockito.verify(this.preparedStatement).close();
            Mockito.verify(this.connection).close();
        }
    }

    /**
     * Test that we see an SQLException translated using Error Code.
     * If we provide the SQLExceptionTranslator, we shouldn't use a connection
     * to get the metadata
     */
    @Test
    public void testUseCustomSQLErrorCodeTranslator() throws Exception {
        // Bad SQL state
        final SQLException sqlException = new SQLException("I have a known problem", "07000", 1054);
        final String sql = "SELECT ID FROM CUSTOMER";
        BDDMockito.given(this.resultSet.next()).willReturn(true);
        BDDMockito.given(this.connection.createStatement()).willReturn(this.preparedStatement);
        JdbcTemplate template = new JdbcTemplate();
        template.setDataSource(this.dataSource);
        // Set custom exception translator
        template.setExceptionTranslator(new SQLStateSQLExceptionTranslator());
        template.afterPropertiesSet();
        this.thrown.expect(BadSqlGrammarException.class);
        this.thrown.expect(exceptionCause(sameInstance(sqlException)));
        try {
            template.query(sql, ((RowCallbackHandler) (( rs) -> {
                throw sqlException;
            })));
        } finally {
            Mockito.verify(this.resultSet).close();
            Mockito.verify(this.preparedStatement).close();
            Mockito.verify(this.connection).close();
        }
    }

    @Test
    public void testStaticResultSetClosed() throws Exception {
        ResultSet resultSet2 = Mockito.mock(ResultSet.class);
        Mockito.reset(this.preparedStatement);
        BDDMockito.given(this.preparedStatement.executeQuery()).willReturn(resultSet2);
        BDDMockito.given(this.connection.createStatement()).willReturn(this.statement);
        try {
            this.template.query("my query", ((ResultSetExtractor<Object>) (( rs) -> {
                throw new InvalidDataAccessApiUsageException("");
            })));
            Assert.fail("Should have thrown InvalidDataAccessApiUsageException");
        } catch (InvalidDataAccessApiUsageException ex) {
            // ok
        }
        try {
            this.template.query(( con) -> con.prepareStatement("my query"), ((ResultSetExtractor<Object>) (( rs2) -> {
                throw new InvalidDataAccessApiUsageException("");
            })));
            Assert.fail("Should have thrown InvalidDataAccessApiUsageException");
        } catch (InvalidDataAccessApiUsageException ex) {
            // ok
        }
        Mockito.verify(this.resultSet).close();
        Mockito.verify(resultSet2).close();
        Mockito.verify(this.preparedStatement).close();
        Mockito.verify(this.connection, Mockito.atLeastOnce()).close();
    }

    @Test
    public void testExecuteClosed() throws Exception {
        BDDMockito.given(this.resultSet.next()).willReturn(true);
        BDDMockito.given(this.callableStatement.execute()).willReturn(true);
        BDDMockito.given(this.callableStatement.getUpdateCount()).willReturn((-1));
        SqlParameter param = new SqlReturnResultSet("", ((RowCallbackHandler) (( rs) -> {
            throw new InvalidDataAccessApiUsageException("");
        })));
        this.thrown.expect(InvalidDataAccessApiUsageException.class);
        try {
            this.template.call(( conn) -> conn.prepareCall("my query"), Collections.singletonList(param));
        } finally {
            Mockito.verify(this.resultSet).close();
            Mockito.verify(this.callableStatement).close();
            Mockito.verify(this.connection).close();
        }
    }

    @Test
    public void testCaseInsensitiveResultsMap() throws Exception {
        BDDMockito.given(this.callableStatement.execute()).willReturn(false);
        BDDMockito.given(this.callableStatement.getUpdateCount()).willReturn((-1));
        BDDMockito.given(this.callableStatement.getObject(1)).willReturn("X");
        Assert.assertTrue("default should have been NOT case insensitive", (!(this.template.isResultsMapCaseInsensitive())));
        this.template.setResultsMapCaseInsensitive(true);
        Assert.assertTrue("now it should have been set to case insensitive", this.template.isResultsMapCaseInsensitive());
        Map<String, Object> out = this.template.call(( conn) -> conn.prepareCall("my query"), Collections.singletonList(new SqlOutParameter("a", 12)));
        Assert.assertThat(out, instanceOf(LinkedCaseInsensitiveMap.class));
        Assert.assertNotNull("we should have gotten the result with upper case", out.get("A"));
        Assert.assertNotNull("we should have gotten the result with lower case", out.get("a"));
        Mockito.verify(this.callableStatement).close();
        Mockito.verify(this.connection).close();
    }

    // SPR-16578
    @Test
    public void testEquallyNamedColumn() throws SQLException {
        BDDMockito.given(this.connection.createStatement()).willReturn(this.statement);
        ResultSetMetaData metaData = Mockito.mock(ResultSetMetaData.class);
        BDDMockito.given(metaData.getColumnCount()).willReturn(2);
        BDDMockito.given(metaData.getColumnLabel(1)).willReturn("x");
        BDDMockito.given(metaData.getColumnLabel(2)).willReturn("X");
        BDDMockito.given(this.resultSet.getMetaData()).willReturn(metaData);
        BDDMockito.given(this.resultSet.next()).willReturn(true, false);
        BDDMockito.given(this.resultSet.getObject(1)).willReturn("first value");
        BDDMockito.given(this.resultSet.getObject(2)).willReturn("second value");
        Map<String, Object> map = this.template.queryForMap("my query");
        Assert.assertEquals(1, map.size());
        Assert.assertEquals("first value", map.get("x"));
    }

    private interface JdbcTemplateCallback {
        void doInJdbcTemplate(JdbcTemplate template, String sql, RowCallbackHandler rch);
    }

    private static class Dispatcher implements PreparedStatementCreator , SqlProvider {
        private int id;

        private String sql;

        public Dispatcher(int id, String sql) {
            this.id = id;
            this.sql = sql;
        }

        @Override
        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
            PreparedStatement ps = connection.prepareStatement(this.sql);
            ps.setInt(1, this.id);
            return ps;
        }

        @Override
        public String getSql() {
            return this.sql;
        }
    }
}

