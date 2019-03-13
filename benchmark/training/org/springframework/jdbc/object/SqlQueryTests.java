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
package org.springframework.jdbc.object;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.Customer;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;


/**
 *
 *
 * @author Trevor Cook
 * @author Thomas Risberg
 * @author Juergen Hoeller
 */
public class SqlQueryTests {
    // FIXME inline?
    private static final String SELECT_ID = "select id from custmr";

    private static final String SELECT_ID_WHERE = "select id from custmr where forename = ? and id = ?";

    private static final String SELECT_FORENAME = "select forename from custmr";

    private static final String SELECT_FORENAME_EMPTY = "select forename from custmr WHERE 1 = 2";

    private static final String SELECT_ID_FORENAME_WHERE = "select id, forename from prefix:custmr where forename = ?";

    private static final String SELECT_ID_FORENAME_NAMED_PARAMETERS = "select id, forename from custmr where id = :id and country = :country";

    private static final String SELECT_ID_FORENAME_NAMED_PARAMETERS_PARSED = "select id, forename from custmr where id = ? and country = ?";

    private static final String SELECT_ID_FORENAME_WHERE_ID_IN_LIST_1 = "select id, forename from custmr where id in (?, ?)";

    private static final String SELECT_ID_FORENAME_WHERE_ID_IN_LIST_2 = "select id, forename from custmr where id in (:ids)";

    private static final String SELECT_ID_FORENAME_WHERE_ID_REUSED_1 = "select id, forename from custmr where id = ? or id = ?)";

    private static final String SELECT_ID_FORENAME_WHERE_ID_REUSED_2 = "select id, forename from custmr where id = :id1 or id = :id1)";

    private static final String SELECT_ID_FORENAME_WHERE_ID = "select id, forename from custmr where id <= ?";

    private static final String[] COLUMN_NAMES = new String[]{ "id", "forename" };

    private static final int[] COLUMN_TYPES = new int[]{ Types.INTEGER, Types.VARCHAR };

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Connection connection;

    private DataSource dataSource;

    private PreparedStatement preparedStatement;

    private ResultSet resultSet;

    @Test
    public void testQueryWithoutParams() throws SQLException {
        BDDMockito.given(resultSet.next()).willReturn(true, false);
        BDDMockito.given(resultSet.getInt(1)).willReturn(1);
        SqlQuery<Integer> query = new MappingSqlQueryWithParameters<Integer>() {
            @Override
            protected Integer mapRow(ResultSet rs, int rownum, @Nullable
            Object[] params, @Nullable
            Map<?, ?> context) throws SQLException {
                Assert.assertTrue("params were null", (params == null));
                Assert.assertTrue("context was null", (context == null));
                return rs.getInt(1);
            }
        };
        query.setDataSource(dataSource);
        query.setSql(SqlQueryTests.SELECT_ID);
        query.compile();
        List<Integer> list = query.execute();
        Assert.assertThat(list, is(equalTo(Arrays.asList(1))));
        Mockito.verify(connection).prepareStatement(SqlQueryTests.SELECT_ID);
        Mockito.verify(resultSet).close();
        Mockito.verify(preparedStatement).close();
    }

    @Test
    public void testQueryWithoutEnoughParams() {
        MappingSqlQuery<Integer> query = new MappingSqlQuery<Integer>() {
            @Override
            protected Integer mapRow(ResultSet rs, int rownum) throws SQLException {
                return rs.getInt(1);
            }
        };
        query.setDataSource(dataSource);
        query.setSql(SqlQueryTests.SELECT_ID_WHERE);
        query.declareParameter(new SqlParameter(SqlQueryTests.COLUMN_NAMES[0], SqlQueryTests.COLUMN_TYPES[0]));
        query.declareParameter(new SqlParameter(SqlQueryTests.COLUMN_NAMES[1], SqlQueryTests.COLUMN_TYPES[1]));
        query.compile();
        thrown.expect(InvalidDataAccessApiUsageException.class);
        query.execute();
    }

    @Test
    public void testQueryWithMissingMapParams() {
        MappingSqlQuery<Integer> query = new MappingSqlQuery<Integer>() {
            @Override
            protected Integer mapRow(ResultSet rs, int rownum) throws SQLException {
                return rs.getInt(1);
            }
        };
        query.setDataSource(dataSource);
        query.setSql(SqlQueryTests.SELECT_ID_WHERE);
        query.declareParameter(new SqlParameter(SqlQueryTests.COLUMN_NAMES[0], SqlQueryTests.COLUMN_TYPES[0]));
        query.declareParameter(new SqlParameter(SqlQueryTests.COLUMN_NAMES[1], SqlQueryTests.COLUMN_TYPES[1]));
        query.compile();
        thrown.expect(InvalidDataAccessApiUsageException.class);
        query.executeByNamedParam(Collections.singletonMap(SqlQueryTests.COLUMN_NAMES[0], "value"));
    }

    @Test
    public void testStringQueryWithResults() throws Exception {
        String[] dbResults = new String[]{ "alpha", "beta", "charlie" };
        BDDMockito.given(resultSet.next()).willReturn(true, true, true, false);
        BDDMockito.given(resultSet.getString(1)).willReturn(dbResults[0], dbResults[1], dbResults[2]);
        SqlQueryTests.StringQuery query = new SqlQueryTests.StringQuery(dataSource, SqlQueryTests.SELECT_FORENAME);
        setRowsExpected(3);
        String[] results = query.run();
        Assert.assertThat(results, is(equalTo(dbResults)));
        Mockito.verify(connection).prepareStatement(SqlQueryTests.SELECT_FORENAME);
        Mockito.verify(resultSet).close();
        Mockito.verify(preparedStatement).close();
        Mockito.verify(connection).close();
    }

    @Test
    public void testStringQueryWithoutResults() throws SQLException {
        BDDMockito.given(resultSet.next()).willReturn(false);
        SqlQueryTests.StringQuery query = new SqlQueryTests.StringQuery(dataSource, SqlQueryTests.SELECT_FORENAME_EMPTY);
        String[] results = query.run();
        Assert.assertThat(results, is(equalTo(new String[0])));
        Mockito.verify(connection).prepareStatement(SqlQueryTests.SELECT_FORENAME_EMPTY);
        Mockito.verify(resultSet).close();
        Mockito.verify(preparedStatement).close();
        Mockito.verify(connection).close();
    }

    @Test
    public void testFindCustomerIntInt() throws SQLException {
        BDDMockito.given(resultSet.next()).willReturn(true, false);
        BDDMockito.given(resultSet.getInt("id")).willReturn(1);
        BDDMockito.given(resultSet.getString("forename")).willReturn("rod");
        class CustomerQuery extends MappingSqlQuery<Customer> {
            public CustomerQuery(DataSource ds) {
                super(ds, SqlQueryTests.SELECT_ID_WHERE);
                declareParameter(new SqlParameter(Types.NUMERIC));
                declareParameter(new SqlParameter(Types.NUMERIC));
                compile();
            }

            @Override
            protected Customer mapRow(ResultSet rs, int rownum) throws SQLException {
                Customer cust = new Customer();
                cust.setId(rs.getInt(SqlQueryTests.COLUMN_NAMES[0]));
                cust.setForename(rs.getString(SqlQueryTests.COLUMN_NAMES[1]));
                return cust;
            }

            public Customer findCustomer(int id, int otherNum) {
                return findObject(id, otherNum);
            }
        }
        CustomerQuery query = new CustomerQuery(dataSource);
        Customer cust = query.findCustomer(1, 1);
        Assert.assertTrue("Customer id was assigned correctly", ((cust.getId()) == 1));
        Assert.assertTrue("Customer forename was assigned correctly", cust.getForename().equals("rod"));
        Mockito.verify(preparedStatement).setObject(1, 1, Types.NUMERIC);
        Mockito.verify(preparedStatement).setObject(2, 1, Types.NUMERIC);
        Mockito.verify(connection).prepareStatement(SqlQueryTests.SELECT_ID_WHERE);
        Mockito.verify(resultSet).close();
        Mockito.verify(preparedStatement).close();
        Mockito.verify(connection).close();
    }

    @Test
    public void testFindCustomerString() throws SQLException {
        BDDMockito.given(resultSet.next()).willReturn(true, false);
        BDDMockito.given(resultSet.getInt("id")).willReturn(1);
        BDDMockito.given(resultSet.getString("forename")).willReturn("rod");
        class CustomerQuery extends MappingSqlQuery<Customer> {
            public CustomerQuery(DataSource ds) {
                super(ds, SqlQueryTests.SELECT_ID_FORENAME_WHERE);
                declareParameter(new SqlParameter(Types.VARCHAR));
                compile();
            }

            @Override
            protected Customer mapRow(ResultSet rs, int rownum) throws SQLException {
                Customer cust = new Customer();
                cust.setId(rs.getInt(SqlQueryTests.COLUMN_NAMES[0]));
                cust.setForename(rs.getString(SqlQueryTests.COLUMN_NAMES[1]));
                return cust;
            }

            public Customer findCustomer(String id) {
                return findObject(id);
            }
        }
        CustomerQuery query = new CustomerQuery(dataSource);
        Customer cust = query.findCustomer("rod");
        Assert.assertTrue("Customer id was assigned correctly", ((cust.getId()) == 1));
        Assert.assertTrue("Customer forename was assigned correctly", cust.getForename().equals("rod"));
        Mockito.verify(preparedStatement).setString(1, "rod");
        Mockito.verify(connection).prepareStatement(SqlQueryTests.SELECT_ID_FORENAME_WHERE);
        Mockito.verify(resultSet).close();
        Mockito.verify(preparedStatement).close();
        Mockito.verify(connection).close();
    }

    @Test
    public void testFindCustomerMixed() throws SQLException {
        Mockito.reset(connection);
        PreparedStatement preparedStatement2 = Mockito.mock(PreparedStatement.class);
        ResultSet resultSet2 = Mockito.mock(ResultSet.class);
        BDDMockito.given(preparedStatement2.executeQuery()).willReturn(resultSet2);
        BDDMockito.given(resultSet.next()).willReturn(true, false);
        BDDMockito.given(resultSet.getInt("id")).willReturn(1);
        BDDMockito.given(resultSet.getString("forename")).willReturn("rod");
        BDDMockito.given(resultSet2.next()).willReturn(false);
        BDDMockito.given(connection.prepareStatement(SqlQueryTests.SELECT_ID_WHERE)).willReturn(preparedStatement, preparedStatement2);
        class CustomerQuery extends MappingSqlQuery<Customer> {
            public CustomerQuery(DataSource ds) {
                super(ds, SqlQueryTests.SELECT_ID_WHERE);
                declareParameter(new SqlParameter(SqlQueryTests.COLUMN_NAMES[0], SqlQueryTests.COLUMN_TYPES[0]));
                declareParameter(new SqlParameter(SqlQueryTests.COLUMN_NAMES[1], SqlQueryTests.COLUMN_TYPES[1]));
                compile();
            }

            @Override
            protected Customer mapRow(ResultSet rs, int rownum) throws SQLException {
                Customer cust = new Customer();
                cust.setId(rs.getInt(SqlQueryTests.COLUMN_NAMES[0]));
                cust.setForename(rs.getString(SqlQueryTests.COLUMN_NAMES[1]));
                return cust;
            }

            public Customer findCustomer(int id, String name) {
                return findObject(new Object[]{ id, name });
            }
        }
        CustomerQuery query = new CustomerQuery(dataSource);
        Customer cust1 = query.findCustomer(1, "rod");
        Assert.assertTrue("Found customer", (cust1 != null));
        Assert.assertTrue("Customer id was assigned correctly", ((cust1.getId()) == 1));
        Customer cust2 = query.findCustomer(1, "Roger");
        Assert.assertTrue("No customer found", (cust2 == null));
        Mockito.verify(preparedStatement).setObject(1, 1, Types.INTEGER);
        Mockito.verify(preparedStatement).setString(2, "rod");
        Mockito.verify(preparedStatement2).setObject(1, 1, Types.INTEGER);
        Mockito.verify(preparedStatement2).setString(2, "Roger");
        Mockito.verify(resultSet).close();
        Mockito.verify(resultSet2).close();
        Mockito.verify(preparedStatement).close();
        Mockito.verify(preparedStatement2).close();
        Mockito.verify(connection, Mockito.times(2)).close();
    }

    @Test
    public void testFindTooManyCustomers() throws SQLException {
        BDDMockito.given(resultSet.next()).willReturn(true, true, false);
        BDDMockito.given(resultSet.getInt("id")).willReturn(1, 2);
        BDDMockito.given(resultSet.getString("forename")).willReturn("rod", "rod");
        class CustomerQuery extends MappingSqlQuery<Customer> {
            public CustomerQuery(DataSource ds) {
                super(ds, SqlQueryTests.SELECT_ID_FORENAME_WHERE);
                declareParameter(new SqlParameter(Types.VARCHAR));
                compile();
            }

            @Override
            protected Customer mapRow(ResultSet rs, int rownum) throws SQLException {
                Customer cust = new Customer();
                cust.setId(rs.getInt(SqlQueryTests.COLUMN_NAMES[0]));
                cust.setForename(rs.getString(SqlQueryTests.COLUMN_NAMES[1]));
                return cust;
            }

            public Customer findCustomer(String id) {
                return findObject(id);
            }
        }
        CustomerQuery query = new CustomerQuery(dataSource);
        thrown.expect(IncorrectResultSizeDataAccessException.class);
        try {
            query.findCustomer("rod");
        } finally {
            Mockito.verify(preparedStatement).setString(1, "rod");
            Mockito.verify(connection).prepareStatement(SqlQueryTests.SELECT_ID_FORENAME_WHERE);
            Mockito.verify(resultSet).close();
            Mockito.verify(preparedStatement).close();
            Mockito.verify(connection).close();
        }
    }

    @Test
    public void testListCustomersIntInt() throws SQLException {
        BDDMockito.given(resultSet.next()).willReturn(true, true, false);
        BDDMockito.given(resultSet.getInt("id")).willReturn(1, 2);
        BDDMockito.given(resultSet.getString("forename")).willReturn("rod", "dave");
        class CustomerQuery extends MappingSqlQuery<Customer> {
            public CustomerQuery(DataSource ds) {
                super(ds, SqlQueryTests.SELECT_ID_WHERE);
                declareParameter(new SqlParameter(Types.NUMERIC));
                declareParameter(new SqlParameter(Types.NUMERIC));
                compile();
            }

            @Override
            protected Customer mapRow(ResultSet rs, int rownum) throws SQLException {
                Customer cust = new Customer();
                cust.setId(rs.getInt(SqlQueryTests.COLUMN_NAMES[0]));
                cust.setForename(rs.getString(SqlQueryTests.COLUMN_NAMES[1]));
                return cust;
            }
        }
        CustomerQuery query = new CustomerQuery(dataSource);
        List<Customer> list = query.execute(1, 1);
        Assert.assertTrue("2 results in list", ((list.size()) == 2));
        Assert.assertThat(list.get(0).getForename(), is("rod"));
        Assert.assertThat(list.get(1).getForename(), is("dave"));
        Mockito.verify(preparedStatement).setObject(1, 1, Types.NUMERIC);
        Mockito.verify(preparedStatement).setObject(2, 1, Types.NUMERIC);
        Mockito.verify(connection).prepareStatement(SqlQueryTests.SELECT_ID_WHERE);
        Mockito.verify(resultSet).close();
        Mockito.verify(preparedStatement).close();
        Mockito.verify(connection).close();
    }

    @Test
    public void testListCustomersString() throws SQLException {
        BDDMockito.given(resultSet.next()).willReturn(true, true, false);
        BDDMockito.given(resultSet.getInt("id")).willReturn(1, 2);
        BDDMockito.given(resultSet.getString("forename")).willReturn("rod", "dave");
        class CustomerQuery extends MappingSqlQuery<Customer> {
            public CustomerQuery(DataSource ds) {
                super(ds, SqlQueryTests.SELECT_ID_FORENAME_WHERE);
                declareParameter(new SqlParameter(Types.VARCHAR));
                compile();
            }

            @Override
            protected Customer mapRow(ResultSet rs, int rownum) throws SQLException {
                Customer cust = new Customer();
                cust.setId(rs.getInt(SqlQueryTests.COLUMN_NAMES[0]));
                cust.setForename(rs.getString(SqlQueryTests.COLUMN_NAMES[1]));
                return cust;
            }
        }
        CustomerQuery query = new CustomerQuery(dataSource);
        List<Customer> list = query.execute("one");
        Assert.assertTrue("2 results in list", ((list.size()) == 2));
        Assert.assertThat(list.get(0).getForename(), is("rod"));
        Assert.assertThat(list.get(1).getForename(), is("dave"));
        Mockito.verify(preparedStatement).setString(1, "one");
        Mockito.verify(connection).prepareStatement(SqlQueryTests.SELECT_ID_FORENAME_WHERE);
        Mockito.verify(resultSet).close();
        Mockito.verify(preparedStatement).close();
        Mockito.verify(connection).close();
    }

    @Test
    public void testFancyCustomerQuery() throws SQLException {
        BDDMockito.given(resultSet.next()).willReturn(true, false);
        BDDMockito.given(resultSet.getInt("id")).willReturn(1);
        BDDMockito.given(resultSet.getString("forename")).willReturn("rod");
        BDDMockito.given(connection.prepareStatement(SqlQueryTests.SELECT_ID_FORENAME_WHERE, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)).willReturn(preparedStatement);
        class CustomerQuery extends MappingSqlQuery<Customer> {
            public CustomerQuery(DataSource ds) {
                super(ds, SqlQueryTests.SELECT_ID_FORENAME_WHERE);
                setResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE);
                declareParameter(new SqlParameter(Types.NUMERIC));
                compile();
            }

            @Override
            protected Customer mapRow(ResultSet rs, int rownum) throws SQLException {
                Customer cust = new Customer();
                cust.setId(rs.getInt(SqlQueryTests.COLUMN_NAMES[0]));
                cust.setForename(rs.getString(SqlQueryTests.COLUMN_NAMES[1]));
                return cust;
            }

            public Customer findCustomer(int id) {
                return findObject(id);
            }
        }
        CustomerQuery query = new CustomerQuery(dataSource);
        Customer cust = query.findCustomer(1);
        Assert.assertTrue("Customer id was assigned correctly", ((cust.getId()) == 1));
        Assert.assertTrue("Customer forename was assigned correctly", cust.getForename().equals("rod"));
        Mockito.verify(preparedStatement).setObject(1, 1, Types.NUMERIC);
        Mockito.verify(resultSet).close();
        Mockito.verify(preparedStatement).close();
        Mockito.verify(connection).close();
    }

    @Test
    public void testUnnamedParameterDeclarationWithNamedParameterQuery() throws SQLException {
        class CustomerQuery extends MappingSqlQuery<Customer> {
            public CustomerQuery(DataSource ds) {
                super(ds, SqlQueryTests.SELECT_ID_FORENAME_WHERE);
                setResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE);
                declareParameter(new SqlParameter(Types.NUMERIC));
                compile();
            }

            @Override
            protected Customer mapRow(ResultSet rs, int rownum) throws SQLException {
                Customer cust = new Customer();
                cust.setId(rs.getInt(SqlQueryTests.COLUMN_NAMES[0]));
                cust.setForename(rs.getString(SqlQueryTests.COLUMN_NAMES[1]));
                return cust;
            }

            public Customer findCustomer(int id) {
                Map<String, Integer> params = new HashMap<>();
                params.put("id", id);
                return executeByNamedParam(params).get(0);
            }
        }
        // Query should not succeed since parameter declaration did not specify parameter name
        CustomerQuery query = new CustomerQuery(dataSource);
        thrown.expect(InvalidDataAccessApiUsageException.class);
        query.findCustomer(1);
    }

    @Test
    public void testNamedParameterCustomerQueryWithUnnamedDeclarations() throws SQLException {
        doTestNamedParameterCustomerQuery(false);
    }

    @Test
    public void testNamedParameterCustomerQueryWithNamedDeclarations() throws SQLException {
        doTestNamedParameterCustomerQuery(true);
    }

    @Test
    public void testNamedParameterInListQuery() throws SQLException {
        BDDMockito.given(resultSet.next()).willReturn(true, true, false);
        BDDMockito.given(resultSet.getInt("id")).willReturn(1, 2);
        BDDMockito.given(resultSet.getString("forename")).willReturn("rod", "juergen");
        BDDMockito.given(connection.prepareStatement(SqlQueryTests.SELECT_ID_FORENAME_WHERE_ID_IN_LIST_1, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)).willReturn(preparedStatement);
        class CustomerQuery extends MappingSqlQuery<Customer> {
            public CustomerQuery(DataSource ds) {
                super(ds, SqlQueryTests.SELECT_ID_FORENAME_WHERE_ID_IN_LIST_2);
                setResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE);
                declareParameter(new SqlParameter("ids", Types.NUMERIC));
                compile();
            }

            @Override
            protected Customer mapRow(ResultSet rs, int rownum) throws SQLException {
                Customer cust = new Customer();
                cust.setId(rs.getInt(SqlQueryTests.COLUMN_NAMES[0]));
                cust.setForename(rs.getString(SqlQueryTests.COLUMN_NAMES[1]));
                return cust;
            }

            public List<Customer> findCustomers(List<Integer> ids) {
                Map<String, Object> params = new HashMap<>();
                params.put("ids", ids);
                return executeByNamedParam(params);
            }
        }
        CustomerQuery query = new CustomerQuery(dataSource);
        List<Integer> ids = new ArrayList<>();
        ids.add(1);
        ids.add(2);
        List<Customer> cust = query.findCustomers(ids);
        Assert.assertEquals("We got two customers back", 2, cust.size());
        Assert.assertEquals("First customer id was assigned correctly", cust.get(0).getId(), 1);
        Assert.assertEquals("First customer forename was assigned correctly", cust.get(0).getForename(), "rod");
        Assert.assertEquals("Second customer id was assigned correctly", cust.get(1).getId(), 2);
        Assert.assertEquals("Second customer forename was assigned correctly", cust.get(1).getForename(), "juergen");
        Mockito.verify(preparedStatement).setObject(1, 1, Types.NUMERIC);
        Mockito.verify(preparedStatement).setObject(2, 2, Types.NUMERIC);
        Mockito.verify(resultSet).close();
        Mockito.verify(preparedStatement).close();
        Mockito.verify(connection).close();
    }

    @Test
    public void testNamedParameterQueryReusingParameter() throws SQLException {
        BDDMockito.given(resultSet.next()).willReturn(true, true, false);
        BDDMockito.given(resultSet.getInt("id")).willReturn(1, 2);
        BDDMockito.given(resultSet.getString("forename")).willReturn("rod", "juergen");
        BDDMockito.given(connection.prepareStatement(SqlQueryTests.SELECT_ID_FORENAME_WHERE_ID_REUSED_1, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)).willReturn(preparedStatement);
        class CustomerQuery extends MappingSqlQuery<Customer> {
            public CustomerQuery(DataSource ds) {
                super(ds, SqlQueryTests.SELECT_ID_FORENAME_WHERE_ID_REUSED_2);
                setResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE);
                declareParameter(new SqlParameter("id1", Types.NUMERIC));
                compile();
            }

            @Override
            protected Customer mapRow(ResultSet rs, int rownum) throws SQLException {
                Customer cust = new Customer();
                cust.setId(rs.getInt(SqlQueryTests.COLUMN_NAMES[0]));
                cust.setForename(rs.getString(SqlQueryTests.COLUMN_NAMES[1]));
                return cust;
            }

            public List<Customer> findCustomers(Integer id) {
                Map<String, Object> params = new HashMap<>();
                params.put("id1", id);
                return executeByNamedParam(params);
            }
        }
        CustomerQuery query = new CustomerQuery(dataSource);
        List<Customer> cust = query.findCustomers(1);
        Assert.assertEquals("We got two customers back", 2, cust.size());
        Assert.assertEquals("First customer id was assigned correctly", cust.get(0).getId(), 1);
        Assert.assertEquals("First customer forename was assigned correctly", cust.get(0).getForename(), "rod");
        Assert.assertEquals("Second customer id was assigned correctly", cust.get(1).getId(), 2);
        Assert.assertEquals("Second customer forename was assigned correctly", cust.get(1).getForename(), "juergen");
        Mockito.verify(preparedStatement).setObject(1, 1, Types.NUMERIC);
        Mockito.verify(preparedStatement).setObject(2, 1, Types.NUMERIC);
        Mockito.verify(resultSet).close();
        Mockito.verify(preparedStatement).close();
        Mockito.verify(connection).close();
    }

    @Test
    public void testNamedParameterUsingInvalidQuestionMarkPlaceHolders() throws SQLException {
        BDDMockito.given(connection.prepareStatement(SqlQueryTests.SELECT_ID_FORENAME_WHERE_ID_REUSED_1, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)).willReturn(preparedStatement);
        class CustomerQuery extends MappingSqlQuery<Customer> {
            public CustomerQuery(DataSource ds) {
                super(ds, SqlQueryTests.SELECT_ID_FORENAME_WHERE_ID_REUSED_1);
                setResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE);
                declareParameter(new SqlParameter("id1", Types.NUMERIC));
                compile();
            }

            @Override
            protected Customer mapRow(ResultSet rs, int rownum) throws SQLException {
                Customer cust = new Customer();
                cust.setId(rs.getInt(SqlQueryTests.COLUMN_NAMES[0]));
                cust.setForename(rs.getString(SqlQueryTests.COLUMN_NAMES[1]));
                return cust;
            }

            public List<Customer> findCustomers(Integer id1) {
                Map<String, Integer> params = new HashMap<>();
                params.put("id1", id1);
                return executeByNamedParam(params);
            }
        }
        CustomerQuery query = new CustomerQuery(dataSource);
        thrown.expect(InvalidDataAccessApiUsageException.class);
        query.findCustomers(1);
    }

    @Test
    public void testUpdateCustomers() throws SQLException {
        BDDMockito.given(resultSet.next()).willReturn(true, true, false);
        BDDMockito.given(resultSet.getInt("id")).willReturn(1, 2);
        BDDMockito.given(connection.prepareStatement(SqlQueryTests.SELECT_ID_FORENAME_WHERE_ID, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)).willReturn(preparedStatement);
        class CustomerUpdateQuery extends UpdatableSqlQuery<Customer> {
            public CustomerUpdateQuery(DataSource ds) {
                super(ds, SqlQueryTests.SELECT_ID_FORENAME_WHERE_ID);
                declareParameter(new SqlParameter(Types.NUMERIC));
                compile();
            }

            @Override
            protected Customer updateRow(ResultSet rs, int rownum, @Nullable
            Map<?, ?> context) throws SQLException {
                rs.updateString(2, ("" + (context.get(rs.getInt(SqlQueryTests.COLUMN_NAMES[0])))));
                return null;
            }
        }
        CustomerUpdateQuery query = new CustomerUpdateQuery(dataSource);
        Map<Integer, String> values = new HashMap<>(2);
        values.put(1, "Rod");
        values.put(2, "Thomas");
        query.execute(2, values);
        Mockito.verify(resultSet).updateString(2, "Rod");
        Mockito.verify(resultSet).updateString(2, "Thomas");
        Mockito.verify(resultSet, Mockito.times(2)).updateRow();
        Mockito.verify(preparedStatement).setObject(1, 2, Types.NUMERIC);
        Mockito.verify(resultSet).close();
        Mockito.verify(preparedStatement).close();
        Mockito.verify(connection).close();
    }

    private static class StringQuery extends MappingSqlQuery<String> {
        public StringQuery(DataSource ds, String sql) {
            super(ds, sql);
            compile();
        }

        @Override
        protected String mapRow(ResultSet rs, int rownum) throws SQLException {
            return rs.getString(1);
        }

        public String[] run() {
            return StringUtils.toStringArray(execute());
        }
    }
}

