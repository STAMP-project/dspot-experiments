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
package org.springframework.jdbc.core.namedparam;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.BDDMockito;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.jdbc.Customer;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.SqlParameterValue;


/**
 *
 *
 * @author Rick Evans
 * @author Juergen Hoeller
 * @author Chris Beams
 * @author Nikita Khateev
 * @author Fedor Bobin
 */
public class NamedParameterJdbcTemplateTests {
    private static final String SELECT_NAMED_PARAMETERS = "select id, forename from custmr where id = :id and country = :country";

    private static final String SELECT_NAMED_PARAMETERS_PARSED = "select id, forename from custmr where id = ? and country = ?";

    private static final String SELECT_NO_PARAMETERS = "select id, forename from custmr";

    private static final String UPDATE_NAMED_PARAMETERS = "update seat_status set booking_id = null where performance_id = :perfId and price_band_id = :priceId";

    private static final String UPDATE_NAMED_PARAMETERS_PARSED = "update seat_status set booking_id = null where performance_id = ? and price_band_id = ?";

    private static final String UPDATE_ARRAY_PARAMETERS = "update customer set type = array[:typeIds] where id = :id";

    private static final String UPDATE_ARRAY_PARAMETERS_PARSED = "update customer set type = array[?, ?, ?] where id = ?";

    private static final String[] COLUMN_NAMES = new String[]{ "id", "forename" };

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Connection connection;

    private DataSource dataSource;

    private PreparedStatement preparedStatement;

    private ResultSet resultSet;

    private DatabaseMetaData databaseMetaData;

    private Map<String, Object> params = new HashMap<>();

    private NamedParameterJdbcTemplate namedParameterTemplate;

    @Test
    public void testNullDataSourceProvidedToCtor() {
        thrown.expect(IllegalArgumentException.class);
        new NamedParameterJdbcTemplate(((DataSource) (null)));
    }

    @Test
    public void testNullJdbcTemplateProvidedToCtor() {
        thrown.expect(IllegalArgumentException.class);
        new NamedParameterJdbcTemplate(((JdbcOperations) (null)));
    }

    @Test
    public void testTemplateConfiguration() {
        Assert.assertSame(dataSource, namedParameterTemplate.getJdbcTemplate().getDataSource());
    }

    @Test
    public void testExecute() throws SQLException {
        BDDMockito.given(preparedStatement.executeUpdate()).willReturn(1);
        params.put("perfId", 1);
        params.put("priceId", 1);
        Object result = namedParameterTemplate.execute(NamedParameterJdbcTemplateTests.UPDATE_NAMED_PARAMETERS, params, ((PreparedStatementCallback<Object>) (( ps) -> {
            assertEquals(preparedStatement, ps);
            ps.executeUpdate();
            return "result";
        })));
        Assert.assertEquals("result", result);
        Mockito.verify(connection).prepareStatement(NamedParameterJdbcTemplateTests.UPDATE_NAMED_PARAMETERS_PARSED);
        Mockito.verify(preparedStatement).setObject(1, 1);
        Mockito.verify(preparedStatement).setObject(2, 1);
        Mockito.verify(preparedStatement).close();
        Mockito.verify(connection).close();
    }

    @Test
    public void testExecuteWithTypedParameters() throws SQLException {
        BDDMockito.given(preparedStatement.executeUpdate()).willReturn(1);
        params.put("perfId", new SqlParameterValue(Types.DECIMAL, 1));
        params.put("priceId", new SqlParameterValue(Types.INTEGER, 1));
        Object result = namedParameterTemplate.execute(NamedParameterJdbcTemplateTests.UPDATE_NAMED_PARAMETERS, params, ((PreparedStatementCallback<Object>) (( ps) -> {
            assertEquals(preparedStatement, ps);
            ps.executeUpdate();
            return "result";
        })));
        Assert.assertEquals("result", result);
        Mockito.verify(connection).prepareStatement(NamedParameterJdbcTemplateTests.UPDATE_NAMED_PARAMETERS_PARSED);
        Mockito.verify(preparedStatement).setObject(1, 1, Types.DECIMAL);
        Mockito.verify(preparedStatement).setObject(2, 1, Types.INTEGER);
        Mockito.verify(preparedStatement).close();
        Mockito.verify(connection).close();
    }

    @Test
    public void testExecuteNoParameters() throws SQLException {
        BDDMockito.given(preparedStatement.executeUpdate()).willReturn(1);
        Object result = namedParameterTemplate.execute(NamedParameterJdbcTemplateTests.SELECT_NO_PARAMETERS, ((PreparedStatementCallback<Object>) (( ps) -> {
            assertEquals(preparedStatement, ps);
            ps.executeQuery();
            return "result";
        })));
        Assert.assertEquals("result", result);
        Mockito.verify(connection).prepareStatement(NamedParameterJdbcTemplateTests.SELECT_NO_PARAMETERS);
        Mockito.verify(preparedStatement).close();
        Mockito.verify(connection).close();
    }

    @Test
    public void testQueryWithResultSetExtractor() throws SQLException {
        BDDMockito.given(resultSet.next()).willReturn(true);
        BDDMockito.given(resultSet.getInt("id")).willReturn(1);
        BDDMockito.given(resultSet.getString("forename")).willReturn("rod");
        params.put("id", new SqlParameterValue(Types.DECIMAL, 1));
        params.put("country", "UK");
        Customer cust = namedParameterTemplate.query(NamedParameterJdbcTemplateTests.SELECT_NAMED_PARAMETERS, params, ( rs) -> {
            rs.next();
            Customer cust1 = new Customer();
            cust1.setId(rs.getInt(COLUMN_NAMES[0]));
            cust1.setForename(rs.getString(COLUMN_NAMES[1]));
            return cust1;
        });
        Assert.assertTrue("Customer id was assigned correctly", ((cust.getId()) == 1));
        Assert.assertTrue("Customer forename was assigned correctly", cust.getForename().equals("rod"));
        Mockito.verify(connection).prepareStatement(NamedParameterJdbcTemplateTests.SELECT_NAMED_PARAMETERS_PARSED);
        Mockito.verify(preparedStatement).setObject(1, 1, Types.DECIMAL);
        Mockito.verify(preparedStatement).setString(2, "UK");
        Mockito.verify(preparedStatement).close();
        Mockito.verify(connection).close();
    }

    @Test
    public void testQueryWithResultSetExtractorNoParameters() throws SQLException {
        BDDMockito.given(resultSet.next()).willReturn(true);
        BDDMockito.given(resultSet.getInt("id")).willReturn(1);
        BDDMockito.given(resultSet.getString("forename")).willReturn("rod");
        Customer cust = namedParameterTemplate.query(NamedParameterJdbcTemplateTests.SELECT_NO_PARAMETERS, ( rs) -> {
            rs.next();
            Customer cust1 = new Customer();
            cust1.setId(rs.getInt(COLUMN_NAMES[0]));
            cust1.setForename(rs.getString(COLUMN_NAMES[1]));
            return cust1;
        });
        Assert.assertTrue("Customer id was assigned correctly", ((cust.getId()) == 1));
        Assert.assertTrue("Customer forename was assigned correctly", cust.getForename().equals("rod"));
        Mockito.verify(connection).prepareStatement(NamedParameterJdbcTemplateTests.SELECT_NO_PARAMETERS);
        Mockito.verify(preparedStatement).close();
        Mockito.verify(connection).close();
    }

    @Test
    public void testQueryWithRowCallbackHandler() throws SQLException {
        BDDMockito.given(resultSet.next()).willReturn(true, false);
        BDDMockito.given(resultSet.getInt("id")).willReturn(1);
        BDDMockito.given(resultSet.getString("forename")).willReturn("rod");
        params.put("id", new SqlParameterValue(Types.DECIMAL, 1));
        params.put("country", "UK");
        final List<Customer> customers = new LinkedList<>();
        namedParameterTemplate.query(NamedParameterJdbcTemplateTests.SELECT_NAMED_PARAMETERS, params, ( rs) -> {
            Customer cust = new Customer();
            cust.setId(rs.getInt(COLUMN_NAMES[0]));
            cust.setForename(rs.getString(COLUMN_NAMES[1]));
            customers.add(cust);
        });
        Assert.assertEquals(1, customers.size());
        Assert.assertTrue("Customer id was assigned correctly", ((customers.get(0).getId()) == 1));
        Assert.assertTrue("Customer forename was assigned correctly", customers.get(0).getForename().equals("rod"));
        Mockito.verify(connection).prepareStatement(NamedParameterJdbcTemplateTests.SELECT_NAMED_PARAMETERS_PARSED);
        Mockito.verify(preparedStatement).setObject(1, 1, Types.DECIMAL);
        Mockito.verify(preparedStatement).setString(2, "UK");
        Mockito.verify(preparedStatement).close();
        Mockito.verify(connection).close();
    }

    @Test
    public void testQueryWithRowCallbackHandlerNoParameters() throws SQLException {
        BDDMockito.given(resultSet.next()).willReturn(true, false);
        BDDMockito.given(resultSet.getInt("id")).willReturn(1);
        BDDMockito.given(resultSet.getString("forename")).willReturn("rod");
        final List<Customer> customers = new LinkedList<>();
        namedParameterTemplate.query(NamedParameterJdbcTemplateTests.SELECT_NO_PARAMETERS, ( rs) -> {
            Customer cust = new Customer();
            cust.setId(rs.getInt(COLUMN_NAMES[0]));
            cust.setForename(rs.getString(COLUMN_NAMES[1]));
            customers.add(cust);
        });
        Assert.assertEquals(1, customers.size());
        Assert.assertTrue("Customer id was assigned correctly", ((customers.get(0).getId()) == 1));
        Assert.assertTrue("Customer forename was assigned correctly", customers.get(0).getForename().equals("rod"));
        Mockito.verify(connection).prepareStatement(NamedParameterJdbcTemplateTests.SELECT_NO_PARAMETERS);
        Mockito.verify(preparedStatement).close();
        Mockito.verify(connection).close();
    }

    @Test
    public void testQueryWithRowMapper() throws SQLException {
        BDDMockito.given(resultSet.next()).willReturn(true, false);
        BDDMockito.given(resultSet.getInt("id")).willReturn(1);
        BDDMockito.given(resultSet.getString("forename")).willReturn("rod");
        params.put("id", new SqlParameterValue(Types.DECIMAL, 1));
        params.put("country", "UK");
        List<Customer> customers = namedParameterTemplate.query(NamedParameterJdbcTemplateTests.SELECT_NAMED_PARAMETERS, params, ( rs, rownum) -> {
            Customer cust = new Customer();
            cust.setId(rs.getInt(COLUMN_NAMES[0]));
            cust.setForename(rs.getString(COLUMN_NAMES[1]));
            return cust;
        });
        Assert.assertEquals(1, customers.size());
        Assert.assertTrue("Customer id was assigned correctly", ((customers.get(0).getId()) == 1));
        Assert.assertTrue("Customer forename was assigned correctly", customers.get(0).getForename().equals("rod"));
        Mockito.verify(connection).prepareStatement(NamedParameterJdbcTemplateTests.SELECT_NAMED_PARAMETERS_PARSED);
        Mockito.verify(preparedStatement).setObject(1, 1, Types.DECIMAL);
        Mockito.verify(preparedStatement).setString(2, "UK");
        Mockito.verify(preparedStatement).close();
        Mockito.verify(connection).close();
    }

    @Test
    public void testQueryWithRowMapperNoParameters() throws SQLException {
        BDDMockito.given(resultSet.next()).willReturn(true, false);
        BDDMockito.given(resultSet.getInt("id")).willReturn(1);
        BDDMockito.given(resultSet.getString("forename")).willReturn("rod");
        List<Customer> customers = namedParameterTemplate.query(NamedParameterJdbcTemplateTests.SELECT_NO_PARAMETERS, ( rs, rownum) -> {
            Customer cust = new Customer();
            cust.setId(rs.getInt(COLUMN_NAMES[0]));
            cust.setForename(rs.getString(COLUMN_NAMES[1]));
            return cust;
        });
        Assert.assertEquals(1, customers.size());
        Assert.assertTrue("Customer id was assigned correctly", ((customers.get(0).getId()) == 1));
        Assert.assertTrue("Customer forename was assigned correctly", customers.get(0).getForename().equals("rod"));
        Mockito.verify(connection).prepareStatement(NamedParameterJdbcTemplateTests.SELECT_NO_PARAMETERS);
        Mockito.verify(preparedStatement).close();
        Mockito.verify(connection).close();
    }

    @Test
    public void testQueryForObjectWithRowMapper() throws SQLException {
        BDDMockito.given(resultSet.next()).willReturn(true, false);
        BDDMockito.given(resultSet.getInt("id")).willReturn(1);
        BDDMockito.given(resultSet.getString("forename")).willReturn("rod");
        params.put("id", new SqlParameterValue(Types.DECIMAL, 1));
        params.put("country", "UK");
        Customer cust = namedParameterTemplate.queryForObject(NamedParameterJdbcTemplateTests.SELECT_NAMED_PARAMETERS, params, ( rs, rownum) -> {
            Customer cust1 = new Customer();
            cust1.setId(rs.getInt(COLUMN_NAMES[0]));
            cust1.setForename(rs.getString(COLUMN_NAMES[1]));
            return cust1;
        });
        Assert.assertTrue("Customer id was assigned correctly", ((cust.getId()) == 1));
        Assert.assertTrue("Customer forename was assigned correctly", cust.getForename().equals("rod"));
        Mockito.verify(connection).prepareStatement(NamedParameterJdbcTemplateTests.SELECT_NAMED_PARAMETERS_PARSED);
        Mockito.verify(preparedStatement).setObject(1, 1, Types.DECIMAL);
        Mockito.verify(preparedStatement).setString(2, "UK");
        Mockito.verify(preparedStatement).close();
        Mockito.verify(connection).close();
    }

    @Test
    public void testUpdate() throws SQLException {
        BDDMockito.given(preparedStatement.executeUpdate()).willReturn(1);
        params.put("perfId", 1);
        params.put("priceId", 1);
        int rowsAffected = namedParameterTemplate.update(NamedParameterJdbcTemplateTests.UPDATE_NAMED_PARAMETERS, params);
        Assert.assertEquals(1, rowsAffected);
        Mockito.verify(connection).prepareStatement(NamedParameterJdbcTemplateTests.UPDATE_NAMED_PARAMETERS_PARSED);
        Mockito.verify(preparedStatement).setObject(1, 1);
        Mockito.verify(preparedStatement).setObject(2, 1);
        Mockito.verify(preparedStatement).close();
        Mockito.verify(connection).close();
    }

    @Test
    public void testUpdateWithTypedParameters() throws SQLException {
        BDDMockito.given(preparedStatement.executeUpdate()).willReturn(1);
        params.put("perfId", new SqlParameterValue(Types.DECIMAL, 1));
        params.put("priceId", new SqlParameterValue(Types.INTEGER, 1));
        int rowsAffected = namedParameterTemplate.update(NamedParameterJdbcTemplateTests.UPDATE_NAMED_PARAMETERS, params);
        Assert.assertEquals(1, rowsAffected);
        Mockito.verify(connection).prepareStatement(NamedParameterJdbcTemplateTests.UPDATE_NAMED_PARAMETERS_PARSED);
        Mockito.verify(preparedStatement).setObject(1, 1, Types.DECIMAL);
        Mockito.verify(preparedStatement).setObject(2, 1, Types.INTEGER);
        Mockito.verify(preparedStatement).close();
        Mockito.verify(connection).close();
    }

    @Test
    public void testBatchUpdateWithPlainMap() throws Exception {
        @SuppressWarnings("unchecked")
        final Map<String, Integer>[] ids = new Map[2];
        ids[0] = Collections.singletonMap("id", 100);
        ids[1] = Collections.singletonMap("id", 200);
        final int[] rowsAffected = new int[]{ 1, 2 };
        BDDMockito.given(preparedStatement.executeBatch()).willReturn(rowsAffected);
        BDDMockito.given(connection.getMetaData()).willReturn(databaseMetaData);
        namedParameterTemplate = new NamedParameterJdbcTemplate(new JdbcTemplate(dataSource, false));
        int[] actualRowsAffected = namedParameterTemplate.batchUpdate("UPDATE NOSUCHTABLE SET DATE_DISPATCHED = SYSDATE WHERE ID = :id", ids);
        Assert.assertTrue("executed 2 updates", ((actualRowsAffected.length) == 2));
        Assert.assertEquals(rowsAffected[0], actualRowsAffected[0]);
        Assert.assertEquals(rowsAffected[1], actualRowsAffected[1]);
        Mockito.verify(connection).prepareStatement("UPDATE NOSUCHTABLE SET DATE_DISPATCHED = SYSDATE WHERE ID = ?");
        Mockito.verify(preparedStatement).setObject(1, 100);
        Mockito.verify(preparedStatement).setObject(1, 200);
        Mockito.verify(preparedStatement, Mockito.times(2)).addBatch();
        Mockito.verify(preparedStatement, Mockito.atLeastOnce()).close();
        Mockito.verify(connection, Mockito.atLeastOnce()).close();
    }

    @Test
    public void testBatchUpdateWithEmptyMap() throws Exception {
        @SuppressWarnings("unchecked")
        final Map<String, Integer>[] ids = new Map[0];
        namedParameterTemplate = new NamedParameterJdbcTemplate(new JdbcTemplate(dataSource, false));
        int[] actualRowsAffected = namedParameterTemplate.batchUpdate("UPDATE NOSUCHTABLE SET DATE_DISPATCHED = SYSDATE WHERE ID = :id", ids);
        Assert.assertTrue("executed 0 updates", ((actualRowsAffected.length) == 0));
    }

    @Test
    public void testBatchUpdateWithSqlParameterSource() throws Exception {
        SqlParameterSource[] ids = new SqlParameterSource[2];
        ids[0] = new MapSqlParameterSource("id", 100);
        ids[1] = new MapSqlParameterSource("id", 200);
        final int[] rowsAffected = new int[]{ 1, 2 };
        BDDMockito.given(preparedStatement.executeBatch()).willReturn(rowsAffected);
        BDDMockito.given(connection.getMetaData()).willReturn(databaseMetaData);
        namedParameterTemplate = new NamedParameterJdbcTemplate(new JdbcTemplate(dataSource, false));
        int[] actualRowsAffected = namedParameterTemplate.batchUpdate("UPDATE NOSUCHTABLE SET DATE_DISPATCHED = SYSDATE WHERE ID = :id", ids);
        Assert.assertTrue("executed 2 updates", ((actualRowsAffected.length) == 2));
        Assert.assertEquals(rowsAffected[0], actualRowsAffected[0]);
        Assert.assertEquals(rowsAffected[1], actualRowsAffected[1]);
        Mockito.verify(connection).prepareStatement("UPDATE NOSUCHTABLE SET DATE_DISPATCHED = SYSDATE WHERE ID = ?");
        Mockito.verify(preparedStatement).setObject(1, 100);
        Mockito.verify(preparedStatement).setObject(1, 200);
        Mockito.verify(preparedStatement, Mockito.times(2)).addBatch();
        Mockito.verify(preparedStatement, Mockito.atLeastOnce()).close();
        Mockito.verify(connection, Mockito.atLeastOnce()).close();
    }

    @Test
    public void testBatchUpdateWithInClause() throws Exception {
        @SuppressWarnings("unchecked")
        Map<String, Object>[] parameters = new Map[2];
        parameters[0] = Collections.singletonMap("ids", Arrays.asList(1, 2));
        parameters[1] = Collections.singletonMap("ids", Arrays.asList(3, 4));
        final int[] rowsAffected = new int[]{ 1, 2 };
        BDDMockito.given(preparedStatement.executeBatch()).willReturn(rowsAffected);
        BDDMockito.given(connection.getMetaData()).willReturn(databaseMetaData);
        JdbcTemplate template = new JdbcTemplate(dataSource, false);
        namedParameterTemplate = new NamedParameterJdbcTemplate(template);
        int[] actualRowsAffected = namedParameterTemplate.batchUpdate("delete sometable where id in (:ids)", parameters);
        Assert.assertEquals("executed 2 updates", 2, actualRowsAffected.length);
        InOrder inOrder = Mockito.inOrder(preparedStatement);
        inOrder.verify(preparedStatement).setObject(1, 1);
        inOrder.verify(preparedStatement).setObject(2, 2);
        inOrder.verify(preparedStatement).addBatch();
        inOrder.verify(preparedStatement).setObject(1, 3);
        inOrder.verify(preparedStatement).setObject(2, 4);
        inOrder.verify(preparedStatement).addBatch();
        inOrder.verify(preparedStatement, Mockito.atLeastOnce()).close();
        Mockito.verify(connection, Mockito.atLeastOnce()).close();
    }

    @Test
    public void testBatchUpdateWithSqlParameterSourcePlusTypeInfo() throws Exception {
        SqlParameterSource[] ids = new SqlParameterSource[2];
        ids[0] = new MapSqlParameterSource().addValue("id", 100, Types.NUMERIC);
        ids[1] = new MapSqlParameterSource().addValue("id", 200, Types.NUMERIC);
        final int[] rowsAffected = new int[]{ 1, 2 };
        BDDMockito.given(preparedStatement.executeBatch()).willReturn(rowsAffected);
        BDDMockito.given(connection.getMetaData()).willReturn(databaseMetaData);
        namedParameterTemplate = new NamedParameterJdbcTemplate(new JdbcTemplate(dataSource, false));
        int[] actualRowsAffected = namedParameterTemplate.batchUpdate("UPDATE NOSUCHTABLE SET DATE_DISPATCHED = SYSDATE WHERE ID = :id", ids);
        Assert.assertTrue("executed 2 updates", ((actualRowsAffected.length) == 2));
        Assert.assertEquals(rowsAffected[0], actualRowsAffected[0]);
        Assert.assertEquals(rowsAffected[1], actualRowsAffected[1]);
        Mockito.verify(connection).prepareStatement("UPDATE NOSUCHTABLE SET DATE_DISPATCHED = SYSDATE WHERE ID = ?");
        Mockito.verify(preparedStatement).setObject(1, 100, Types.NUMERIC);
        Mockito.verify(preparedStatement).setObject(1, 200, Types.NUMERIC);
        Mockito.verify(preparedStatement, Mockito.times(2)).addBatch();
        Mockito.verify(preparedStatement, Mockito.atLeastOnce()).close();
        Mockito.verify(connection, Mockito.atLeastOnce()).close();
    }
}

