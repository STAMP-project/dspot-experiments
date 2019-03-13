/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.jdbc.object;


import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
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
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SimpleRowCountCallbackHandler;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.core.support.AbstractSqlTypeValue;
import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.jdbc.support.SQLStateSQLExceptionTranslator;
import org.springframework.lang.Nullable;
import org.springframework.transaction.support.TransactionSynchronizationManager;


/**
 *
 *
 * @author Thomas Risberg
 * @author Trevor Cook
 * @author Rod Johnson
 */
public class StoredProcedureTests {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private DataSource dataSource;

    private Connection connection;

    private CallableStatement callableStatement;

    private boolean verifyClosedAfter = true;

    @Test
    public void testNoSuchStoredProcedure() throws Exception {
        SQLException sqlException = new SQLException("Syntax error or access violation exception", "42000");
        BDDMockito.given(callableStatement.execute()).willThrow(sqlException);
        BDDMockito.given(connection.prepareCall((("{call " + (StoredProcedureTests.NoSuchStoredProcedure.SQL)) + "()}"))).willReturn(callableStatement);
        StoredProcedureTests.NoSuchStoredProcedure sproc = new StoredProcedureTests.NoSuchStoredProcedure(dataSource);
        thrown.expect(BadSqlGrammarException.class);
        sproc.execute();
    }

    @Test
    public void testAddInvoices() throws Exception {
        BDDMockito.given(callableStatement.execute()).willReturn(false);
        BDDMockito.given(callableStatement.getUpdateCount()).willReturn((-1));
        BDDMockito.given(callableStatement.getObject(3)).willReturn(4);
        BDDMockito.given(connection.prepareCall((("{call " + (StoredProcedureTests.AddInvoice.SQL)) + "(?, ?, ?)}"))).willReturn(callableStatement);
        testAddInvoice(1106, 3);
        Mockito.verify(callableStatement).setObject(1, 1106, Types.INTEGER);
        Mockito.verify(callableStatement).setObject(2, 3, Types.INTEGER);
        Mockito.verify(callableStatement).registerOutParameter(3, Types.INTEGER);
    }

    @Test
    public void testAddInvoicesUsingObjectArray() throws Exception {
        BDDMockito.given(callableStatement.execute()).willReturn(false);
        BDDMockito.given(callableStatement.getUpdateCount()).willReturn((-1));
        BDDMockito.given(callableStatement.getObject(3)).willReturn(5);
        BDDMockito.given(connection.prepareCall((("{call " + (StoredProcedureTests.AddInvoice.SQL)) + "(?, ?, ?)}"))).willReturn(callableStatement);
        testAddInvoiceUsingObjectArray(1106, 4);
        Mockito.verify(callableStatement).setObject(1, 1106, Types.INTEGER);
        Mockito.verify(callableStatement).setObject(2, 4, Types.INTEGER);
        Mockito.verify(callableStatement).registerOutParameter(3, Types.INTEGER);
    }

    @Test
    public void testAddInvoicesWithinTransaction() throws Exception {
        BDDMockito.given(callableStatement.execute()).willReturn(false);
        BDDMockito.given(callableStatement.getUpdateCount()).willReturn((-1));
        BDDMockito.given(callableStatement.getObject(3)).willReturn(4);
        BDDMockito.given(connection.prepareCall((("{call " + (StoredProcedureTests.AddInvoice.SQL)) + "(?, ?, ?)}"))).willReturn(callableStatement);
        TransactionSynchronizationManager.bindResource(dataSource, new ConnectionHolder(connection));
        try {
            testAddInvoice(1106, 3);
            Mockito.verify(callableStatement).setObject(1, 1106, Types.INTEGER);
            Mockito.verify(callableStatement).setObject(2, 3, Types.INTEGER);
            Mockito.verify(callableStatement).registerOutParameter(3, Types.INTEGER);
            Mockito.verify(connection, Mockito.never()).close();
        } finally {
            TransactionSynchronizationManager.unbindResource(dataSource);
            connection.close();
        }
    }

    /**
     * Confirm no connection was used to get metadata. Does not use superclass replay
     * mechanism.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testStoredProcedureConfiguredViaJdbcTemplateWithCustomExceptionTranslator() throws Exception {
        BDDMockito.given(callableStatement.execute()).willReturn(false);
        BDDMockito.given(callableStatement.getUpdateCount()).willReturn((-1));
        BDDMockito.given(callableStatement.getObject(2)).willReturn(5);
        BDDMockito.given(connection.prepareCall((("{call " + (StoredProcedureTests.StoredProcedureConfiguredViaJdbcTemplate.SQL)) + "(?, ?)}"))).willReturn(callableStatement);
        class TestJdbcTemplate extends JdbcTemplate {
            int calls;

            @Override
            public Map<String, Object> call(CallableStatementCreator csc, List<SqlParameter> declaredParameters) throws DataAccessException {
                (calls)++;
                return super.call(csc, declaredParameters);
            }
        }
        TestJdbcTemplate t = new TestJdbcTemplate();
        setDataSource(dataSource);
        // Will fail without the following, because we're not able to get a connection
        // from the DataSource here if we need to create an ExceptionTranslator
        t.setExceptionTranslator(new SQLStateSQLExceptionTranslator());
        StoredProcedureTests.StoredProcedureConfiguredViaJdbcTemplate sp = new StoredProcedureTests.StoredProcedureConfiguredViaJdbcTemplate(t);
        Assert.assertEquals(5, sp.execute(11));
        Assert.assertEquals(1, t.calls);
        Mockito.verify(callableStatement).setObject(1, 11, Types.INTEGER);
        Mockito.verify(callableStatement).registerOutParameter(2, Types.INTEGER);
    }

    /**
     * Confirm our JdbcTemplate is used
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testStoredProcedureConfiguredViaJdbcTemplate() throws Exception {
        BDDMockito.given(callableStatement.execute()).willReturn(false);
        BDDMockito.given(callableStatement.getUpdateCount()).willReturn((-1));
        BDDMockito.given(callableStatement.getObject(2)).willReturn(4);
        BDDMockito.given(connection.prepareCall((("{call " + (StoredProcedureTests.StoredProcedureConfiguredViaJdbcTemplate.SQL)) + "(?, ?)}"))).willReturn(callableStatement);
        JdbcTemplate t = new JdbcTemplate();
        t.setDataSource(dataSource);
        StoredProcedureTests.StoredProcedureConfiguredViaJdbcTemplate sp = new StoredProcedureTests.StoredProcedureConfiguredViaJdbcTemplate(t);
        Assert.assertEquals(4, sp.execute(1106));
        Mockito.verify(callableStatement).setObject(1, 1106, Types.INTEGER);
        Mockito.verify(callableStatement).registerOutParameter(2, Types.INTEGER);
    }

    @Test
    public void testNullArg() throws Exception {
        BDDMockito.given(callableStatement.execute()).willReturn(false);
        BDDMockito.given(callableStatement.getUpdateCount()).willReturn((-1));
        BDDMockito.given(connection.prepareCall((("{call " + (StoredProcedureTests.NullArg.SQL)) + "(?)}"))).willReturn(callableStatement);
        StoredProcedureTests.NullArg na = new StoredProcedureTests.NullArg(dataSource);
        na.execute(((String) (null)));
        callableStatement.setNull(1, Types.VARCHAR);
    }

    @Test
    public void testUnnamedParameter() throws Exception {
        this.verifyClosedAfter = false;
        // Shouldn't succeed in creating stored procedure with unnamed parameter
        thrown.expect(InvalidDataAccessApiUsageException.class);
        new StoredProcedureTests.UnnamedParameterStoredProcedure(dataSource);
    }

    @Test
    public void testMissingParameter() throws Exception {
        this.verifyClosedAfter = false;
        StoredProcedureTests.MissingParameterStoredProcedure mp = new StoredProcedureTests.MissingParameterStoredProcedure(dataSource);
        thrown.expect(InvalidDataAccessApiUsageException.class);
        mp.execute();
        Assert.fail("Shouldn't succeed in running stored procedure with missing required parameter");
    }

    @Test
    public void testStoredProcedureExceptionTranslator() throws Exception {
        SQLException sqlException = new SQLException("Syntax error or access violation exception", "42000");
        BDDMockito.given(callableStatement.execute()).willThrow(sqlException);
        BDDMockito.given(connection.prepareCall((("{call " + (StoredProcedureTests.StoredProcedureExceptionTranslator.SQL)) + "()}"))).willReturn(callableStatement);
        StoredProcedureTests.StoredProcedureExceptionTranslator sproc = new StoredProcedureTests.StoredProcedureExceptionTranslator(dataSource);
        thrown.expect(StoredProcedureTests.CustomDataException.class);
        sproc.execute();
    }

    @Test
    public void testStoredProcedureWithResultSet() throws Exception {
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        BDDMockito.given(resultSet.next()).willReturn(true, true, false);
        BDDMockito.given(callableStatement.execute()).willReturn(true);
        BDDMockito.given(callableStatement.getUpdateCount()).willReturn((-1));
        BDDMockito.given(callableStatement.getResultSet()).willReturn(resultSet);
        BDDMockito.given(callableStatement.getUpdateCount()).willReturn((-1));
        BDDMockito.given(connection.prepareCall((("{call " + (StoredProcedureTests.StoredProcedureWithResultSet.SQL)) + "()}"))).willReturn(callableStatement);
        StoredProcedureTests.StoredProcedureWithResultSet sproc = new StoredProcedureTests.StoredProcedureWithResultSet(dataSource);
        sproc.execute();
        Assert.assertEquals(2, sproc.getCount());
        Mockito.verify(resultSet).close();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testStoredProcedureWithResultSetMapped() throws Exception {
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        BDDMockito.given(resultSet.next()).willReturn(true, true, false);
        BDDMockito.given(resultSet.getString(2)).willReturn("Foo", "Bar");
        BDDMockito.given(callableStatement.execute()).willReturn(true);
        BDDMockito.given(callableStatement.getUpdateCount()).willReturn((-1));
        BDDMockito.given(callableStatement.getResultSet()).willReturn(resultSet);
        BDDMockito.given(callableStatement.getMoreResults()).willReturn(false);
        BDDMockito.given(callableStatement.getUpdateCount()).willReturn((-1));
        BDDMockito.given(connection.prepareCall((("{call " + (StoredProcedureTests.StoredProcedureWithResultSetMapped.SQL)) + "()}"))).willReturn(callableStatement);
        StoredProcedureTests.StoredProcedureWithResultSetMapped sproc = new StoredProcedureTests.StoredProcedureWithResultSetMapped(dataSource);
        Map<String, Object> res = sproc.execute();
        List<String> rs = ((List<String>) (res.get("rs")));
        Assert.assertEquals(2, rs.size());
        Assert.assertEquals("Foo", rs.get(0));
        Assert.assertEquals("Bar", rs.get(1));
        Mockito.verify(resultSet).close();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testStoredProcedureWithUndeclaredResults() throws Exception {
        ResultSet resultSet1 = Mockito.mock(ResultSet.class);
        BDDMockito.given(resultSet1.next()).willReturn(true, true, false);
        BDDMockito.given(resultSet1.getString(2)).willReturn("Foo", "Bar");
        ResultSetMetaData resultSetMetaData = Mockito.mock(ResultSetMetaData.class);
        BDDMockito.given(resultSetMetaData.getColumnCount()).willReturn(2);
        BDDMockito.given(resultSetMetaData.getColumnLabel(1)).willReturn("spam");
        BDDMockito.given(resultSetMetaData.getColumnLabel(2)).willReturn("eggs");
        ResultSet resultSet2 = Mockito.mock(ResultSet.class);
        BDDMockito.given(resultSet2.getMetaData()).willReturn(resultSetMetaData);
        BDDMockito.given(resultSet2.next()).willReturn(true, false);
        BDDMockito.given(resultSet2.getObject(1)).willReturn("Spam");
        BDDMockito.given(resultSet2.getObject(2)).willReturn("Eggs");
        BDDMockito.given(callableStatement.execute()).willReturn(true);
        BDDMockito.given(callableStatement.getUpdateCount()).willReturn((-1));
        BDDMockito.given(callableStatement.getResultSet()).willReturn(resultSet1, resultSet2);
        BDDMockito.given(callableStatement.getMoreResults()).willReturn(true, false, false);
        BDDMockito.given(callableStatement.getUpdateCount()).willReturn((-1), (-1), 0, (-1));
        BDDMockito.given(connection.prepareCall((("{call " + (StoredProcedureTests.StoredProcedureWithResultSetMapped.SQL)) + "()}"))).willReturn(callableStatement);
        StoredProcedureTests.StoredProcedureWithResultSetMapped sproc = new StoredProcedureTests.StoredProcedureWithResultSetMapped(dataSource);
        Map<String, Object> res = sproc.execute();
        Assert.assertEquals("incorrect number of returns", 3, res.size());
        List<String> rs1 = ((List<String>) (res.get("rs")));
        Assert.assertEquals(2, rs1.size());
        Assert.assertEquals("Foo", rs1.get(0));
        Assert.assertEquals("Bar", rs1.get(1));
        List<Object> rs2 = ((List<Object>) (res.get("#result-set-2")));
        Assert.assertEquals(1, rs2.size());
        Object o2 = rs2.get(0);
        Assert.assertTrue("wron type returned for result set 2", (o2 instanceof Map));
        Map<String, String> m2 = ((Map<String, String>) (o2));
        Assert.assertEquals("Spam", m2.get("spam"));
        Assert.assertEquals("Eggs", m2.get("eggs"));
        Number n = ((Number) (res.get("#update-count-1")));
        Assert.assertEquals("wrong update count", 0, n.intValue());
        Mockito.verify(resultSet1).close();
        Mockito.verify(resultSet2).close();
    }

    @Test
    public void testStoredProcedureSkippingResultsProcessing() throws Exception {
        BDDMockito.given(callableStatement.execute()).willReturn(true);
        BDDMockito.given(callableStatement.getUpdateCount()).willReturn((-1));
        BDDMockito.given(connection.prepareCall((("{call " + (StoredProcedureTests.StoredProcedureWithResultSetMapped.SQL)) + "()}"))).willReturn(callableStatement);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.setSkipResultsProcessing(true);
        StoredProcedureTests.StoredProcedureWithResultSetMapped sproc = new StoredProcedureTests.StoredProcedureWithResultSetMapped(jdbcTemplate);
        Map<String, Object> res = sproc.execute();
        Assert.assertEquals("incorrect number of returns", 0, res.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testStoredProcedureSkippingUndeclaredResults() throws Exception {
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        BDDMockito.given(resultSet.next()).willReturn(true, true, false);
        BDDMockito.given(resultSet.getString(2)).willReturn("Foo", "Bar");
        BDDMockito.given(callableStatement.execute()).willReturn(true);
        BDDMockito.given(callableStatement.getUpdateCount()).willReturn((-1));
        BDDMockito.given(callableStatement.getResultSet()).willReturn(resultSet);
        BDDMockito.given(callableStatement.getMoreResults()).willReturn(true, false);
        BDDMockito.given(callableStatement.getUpdateCount()).willReturn((-1), (-1));
        BDDMockito.given(connection.prepareCall((("{call " + (StoredProcedureTests.StoredProcedureWithResultSetMapped.SQL)) + "()}"))).willReturn(callableStatement);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.setSkipUndeclaredResults(true);
        StoredProcedureTests.StoredProcedureWithResultSetMapped sproc = new StoredProcedureTests.StoredProcedureWithResultSetMapped(jdbcTemplate);
        Map<String, Object> res = sproc.execute();
        Assert.assertEquals("incorrect number of returns", 1, res.size());
        List<String> rs1 = ((List<String>) (res.get("rs")));
        Assert.assertEquals(2, rs1.size());
        Assert.assertEquals("Foo", rs1.get(0));
        Assert.assertEquals("Bar", rs1.get(1));
        Mockito.verify(resultSet).close();
    }

    @Test
    public void testParameterMapper() throws Exception {
        BDDMockito.given(callableStatement.execute()).willReturn(false);
        BDDMockito.given(callableStatement.getUpdateCount()).willReturn((-1));
        BDDMockito.given(callableStatement.getObject(2)).willReturn("OK");
        BDDMockito.given(connection.prepareCall((("{call " + (StoredProcedureTests.ParameterMapperStoredProcedure.SQL)) + "(?, ?)}"))).willReturn(callableStatement);
        StoredProcedureTests.ParameterMapperStoredProcedure pmsp = new StoredProcedureTests.ParameterMapperStoredProcedure(dataSource);
        Map<String, Object> out = pmsp.executeTest();
        Assert.assertEquals("OK", out.get("out"));
        Mockito.verify(callableStatement).setString(ArgumentMatchers.eq(1), ArgumentMatchers.startsWith("Mock for Connection"));
        Mockito.verify(callableStatement).registerOutParameter(2, Types.VARCHAR);
    }

    @Test
    public void testSqlTypeValue() throws Exception {
        int[] testVal = new int[]{ 1, 2 };
        BDDMockito.given(callableStatement.execute()).willReturn(false);
        BDDMockito.given(callableStatement.getUpdateCount()).willReturn((-1));
        BDDMockito.given(callableStatement.getObject(2)).willReturn("OK");
        BDDMockito.given(connection.prepareCall((("{call " + (StoredProcedureTests.SqlTypeValueStoredProcedure.SQL)) + "(?, ?)}"))).willReturn(callableStatement);
        StoredProcedureTests.SqlTypeValueStoredProcedure stvsp = new StoredProcedureTests.SqlTypeValueStoredProcedure(dataSource);
        Map<String, Object> out = stvsp.executeTest(testVal);
        Assert.assertEquals("OK", out.get("out"));
        Mockito.verify(callableStatement).setObject(1, testVal, Types.ARRAY);
        Mockito.verify(callableStatement).registerOutParameter(2, Types.VARCHAR);
    }

    @Test
    public void testNumericWithScale() throws Exception {
        BDDMockito.given(callableStatement.execute()).willReturn(false);
        BDDMockito.given(callableStatement.getUpdateCount()).willReturn((-1));
        BDDMockito.given(callableStatement.getObject(1)).willReturn(new BigDecimal("12345.6789"));
        BDDMockito.given(connection.prepareCall((("{call " + (StoredProcedureTests.NumericWithScaleStoredProcedure.SQL)) + "(?)}"))).willReturn(callableStatement);
        StoredProcedureTests.NumericWithScaleStoredProcedure nwssp = new StoredProcedureTests.NumericWithScaleStoredProcedure(dataSource);
        Map<String, Object> out = nwssp.executeTest();
        Assert.assertEquals(new BigDecimal("12345.6789"), out.get("out"));
        Mockito.verify(callableStatement).registerOutParameter(1, Types.DECIMAL, 4);
    }

    private static class StoredProcedureConfiguredViaJdbcTemplate extends StoredProcedure {
        public static final String SQL = "configured_via_jt";

        public StoredProcedureConfiguredViaJdbcTemplate(JdbcTemplate t) {
            setJdbcTemplate(t);
            setSql(StoredProcedureTests.StoredProcedureConfiguredViaJdbcTemplate.SQL);
            declareParameter(new SqlParameter("intIn", Types.INTEGER));
            declareParameter(new SqlOutParameter("intOut", Types.INTEGER));
            compile();
        }

        public int execute(int intIn) {
            Map<String, Integer> in = new HashMap<>();
            in.put("intIn", intIn);
            Map<String, Object> out = execute(in);
            return ((Number) (out.get("intOut"))).intValue();
        }
    }

    private static class AddInvoice extends StoredProcedure {
        public static final String SQL = "add_invoice";

        public AddInvoice(DataSource ds) {
            setDataSource(ds);
            setSql(StoredProcedureTests.AddInvoice.SQL);
            declareParameter(new SqlParameter("amount", Types.INTEGER));
            declareParameter(new SqlParameter("custid", Types.INTEGER));
            declareParameter(new SqlOutParameter("newid", Types.INTEGER));
            compile();
        }

        public int execute(int amount, int custid) {
            Map<String, Integer> in = new HashMap<>();
            in.put("amount", amount);
            in.put("custid", custid);
            Map<String, Object> out = execute(in);
            return ((Number) (out.get("newid"))).intValue();
        }
    }

    private static class AddInvoiceUsingObjectArray extends StoredProcedure {
        public static final String SQL = "add_invoice";

        public AddInvoiceUsingObjectArray(DataSource ds) {
            setDataSource(ds);
            setSql(StoredProcedureTests.AddInvoiceUsingObjectArray.SQL);
            declareParameter(new SqlParameter("amount", Types.INTEGER));
            declareParameter(new SqlParameter("custid", Types.INTEGER));
            declareParameter(new SqlOutParameter("newid", Types.INTEGER));
            compile();
        }

        public int execute(int amount, int custid) {
            Map<String, Object> out = execute(new Object[]{ amount, custid });
            return ((Number) (out.get("newid"))).intValue();
        }
    }

    private static class NullArg extends StoredProcedure {
        public static final String SQL = "takes_null";

        public NullArg(DataSource ds) {
            setDataSource(ds);
            setSql(StoredProcedureTests.NullArg.SQL);
            declareParameter(new SqlParameter("ptest", Types.VARCHAR));
            compile();
        }

        public void execute(String s) {
            Map<String, String> in = new HashMap<>();
            in.put("ptest", s);
            execute(in);
        }
    }

    private static class NoSuchStoredProcedure extends StoredProcedure {
        public static final String SQL = "no_sproc_with_this_name";

        public NoSuchStoredProcedure(DataSource ds) {
            setDataSource(ds);
            setSql(StoredProcedureTests.NoSuchStoredProcedure.SQL);
            compile();
        }

        public void execute() {
            execute(new HashMap());
        }
    }

    private static class UnnamedParameterStoredProcedure extends StoredProcedure {
        public UnnamedParameterStoredProcedure(DataSource ds) {
            super(ds, "unnamed_parameter_sp");
            declareParameter(new SqlParameter(Types.INTEGER));
            compile();
        }
    }

    private static class MissingParameterStoredProcedure extends StoredProcedure {
        public MissingParameterStoredProcedure(DataSource ds) {
            setDataSource(ds);
            setSql("takes_string");
            declareParameter(new SqlParameter("mystring", Types.VARCHAR));
            compile();
        }

        public void execute() {
            execute(new HashMap());
        }
    }

    private static class StoredProcedureWithResultSet extends StoredProcedure {
        public static final String SQL = "sproc_with_result_set";

        private final SimpleRowCountCallbackHandler handler = new SimpleRowCountCallbackHandler();

        public StoredProcedureWithResultSet(DataSource ds) {
            setDataSource(ds);
            setSql(StoredProcedureTests.StoredProcedureWithResultSet.SQL);
            declareParameter(new SqlReturnResultSet("rs", this.handler));
            compile();
        }

        public void execute() {
            execute(new HashMap());
        }

        public int getCount() {
            return this.handler.getCount();
        }
    }

    private static class StoredProcedureWithResultSetMapped extends StoredProcedure {
        public static final String SQL = "sproc_with_result_set";

        public StoredProcedureWithResultSetMapped(DataSource ds) {
            setDataSource(ds);
            setSql(StoredProcedureTests.StoredProcedureWithResultSetMapped.SQL);
            declareParameter(new SqlReturnResultSet("rs", new StoredProcedureTests.StoredProcedureWithResultSetMapped.RowMapperImpl()));
            compile();
        }

        public StoredProcedureWithResultSetMapped(JdbcTemplate jt) {
            setJdbcTemplate(jt);
            setSql(StoredProcedureTests.StoredProcedureWithResultSetMapped.SQL);
            declareParameter(new SqlReturnResultSet("rs", new StoredProcedureTests.StoredProcedureWithResultSetMapped.RowMapperImpl()));
            compile();
        }

        public Map<String, Object> execute() {
            return execute(new HashMap());
        }

        private static class RowMapperImpl implements RowMapper<String> {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString(2);
            }
        }
    }

    private static class ParameterMapperStoredProcedure extends StoredProcedure {
        public static final String SQL = "parameter_mapper_sp";

        public ParameterMapperStoredProcedure(DataSource ds) {
            setDataSource(ds);
            setSql(StoredProcedureTests.ParameterMapperStoredProcedure.SQL);
            declareParameter(new SqlParameter("in", Types.VARCHAR));
            declareParameter(new SqlOutParameter("out", Types.VARCHAR));
            compile();
        }

        public Map<String, Object> executeTest() {
            return execute(new StoredProcedureTests.ParameterMapperStoredProcedure.TestParameterMapper());
        }

        private static class TestParameterMapper implements ParameterMapper {
            private TestParameterMapper() {
            }

            @Override
            public Map<String, ?> createMap(Connection con) throws SQLException {
                Map<String, Object> inParms = new HashMap<>();
                String testValue = con.toString();
                inParms.put("in", testValue);
                return inParms;
            }
        }
    }

    private static class SqlTypeValueStoredProcedure extends StoredProcedure {
        public static final String SQL = "sql_type_value_sp";

        public SqlTypeValueStoredProcedure(DataSource ds) {
            setDataSource(ds);
            setSql(StoredProcedureTests.SqlTypeValueStoredProcedure.SQL);
            declareParameter(new SqlParameter("in", Types.ARRAY, "NUMBERS"));
            declareParameter(new SqlOutParameter("out", Types.VARCHAR));
            compile();
        }

        public Map<String, Object> executeTest(final int[] inValue) {
            Map<String, AbstractSqlTypeValue> in = new HashMap<>();
            in.put("in", new AbstractSqlTypeValue() {
                @Override
                public Object createTypeValue(Connection con, int type, String typeName) {
                    // assertEquals(Connection.class, con.getClass());
                    // assertEquals(Types.ARRAY, type);
                    // assertEquals("NUMBER", typeName);
                    return inValue;
                }
            });
            return execute(in);
        }
    }

    private static class NumericWithScaleStoredProcedure extends StoredProcedure {
        public static final String SQL = "numeric_with_scale_sp";

        public NumericWithScaleStoredProcedure(DataSource ds) {
            setDataSource(ds);
            setSql(StoredProcedureTests.NumericWithScaleStoredProcedure.SQL);
            declareParameter(new SqlOutParameter("out", Types.DECIMAL, 4));
            compile();
        }

        public Map<String, Object> executeTest() {
            return execute(new HashMap());
        }
    }

    private static class StoredProcedureExceptionTranslator extends StoredProcedure {
        public static final String SQL = "no_sproc_with_this_name";

        public StoredProcedureExceptionTranslator(DataSource ds) {
            setDataSource(ds);
            setSql(StoredProcedureTests.StoredProcedureExceptionTranslator.SQL);
            getJdbcTemplate().setExceptionTranslator(new SQLExceptionTranslator() {
                @Override
                public DataAccessException translate(String task, @Nullable
                String sql, SQLException ex) {
                    return new StoredProcedureTests.CustomDataException(sql, ex);
                }
            });
            compile();
        }

        public void execute() {
            execute(new HashMap());
        }
    }

    @SuppressWarnings("serial")
    private static class CustomDataException extends DataAccessException {
        public CustomDataException(String s) {
            super(s);
        }

        public CustomDataException(String s, Throwable ex) {
            super(s, ex);
        }
    }
}

