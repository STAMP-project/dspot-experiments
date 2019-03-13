/**
 * Copyright 2002-2016 the original author or authors.
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


import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
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


/**
 *
 *
 * @author Juergen Hoeller
 * @author Phillip Webb
 * @author Rob Winch
 * @since 19.12.2004
 */
public class JdbcTemplateQueryTests {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Connection connection;

    private DataSource dataSource;

    private Statement statement;

    private PreparedStatement preparedStatement;

    private ResultSet resultSet;

    private ResultSetMetaData resultSetMetaData;

    private JdbcTemplate template;

    @Test
    public void testQueryForList() throws Exception {
        String sql = "SELECT AGE FROM CUSTMR WHERE ID < 3";
        BDDMockito.given(this.resultSet.next()).willReturn(true, true, false);
        BDDMockito.given(this.resultSet.getObject(1)).willReturn(11, 12);
        List<Map<String, Object>> li = this.template.queryForList(sql);
        Assert.assertEquals("All rows returned", 2, li.size());
        Assert.assertEquals("First row is Integer", 11, ((Integer) (li.get(0).get("age"))).intValue());
        Assert.assertEquals("Second row is Integer", 12, ((Integer) (li.get(1).get("age"))).intValue());
        Mockito.verify(this.resultSet).close();
        Mockito.verify(this.statement).close();
    }

    @Test
    public void testQueryForListWithEmptyResult() throws Exception {
        String sql = "SELECT AGE FROM CUSTMR WHERE ID < 3";
        BDDMockito.given(this.resultSet.next()).willReturn(false);
        List<Map<String, Object>> li = this.template.queryForList(sql);
        Assert.assertEquals("All rows returned", 0, li.size());
        Mockito.verify(this.resultSet).close();
        Mockito.verify(this.statement).close();
    }

    @Test
    public void testQueryForListWithSingleRowAndColumn() throws Exception {
        String sql = "SELECT AGE FROM CUSTMR WHERE ID < 3";
        BDDMockito.given(this.resultSet.next()).willReturn(true, false);
        BDDMockito.given(this.resultSet.getObject(1)).willReturn(11);
        List<Map<String, Object>> li = this.template.queryForList(sql);
        Assert.assertEquals("All rows returned", 1, li.size());
        Assert.assertEquals("First row is Integer", 11, ((Integer) (li.get(0).get("age"))).intValue());
        Mockito.verify(this.resultSet).close();
        Mockito.verify(this.statement).close();
    }

    @Test
    public void testQueryForListWithIntegerElement() throws Exception {
        String sql = "SELECT AGE FROM CUSTMR WHERE ID < 3";
        BDDMockito.given(this.resultSet.next()).willReturn(true, false);
        BDDMockito.given(this.resultSet.getInt(1)).willReturn(11);
        List<Integer> li = this.template.queryForList(sql, Integer.class);
        Assert.assertEquals("All rows returned", 1, li.size());
        Assert.assertEquals("Element is Integer", 11, li.get(0).intValue());
        Mockito.verify(this.resultSet).close();
        Mockito.verify(this.statement).close();
    }

    @Test
    public void testQueryForMapWithSingleRowAndColumn() throws Exception {
        String sql = "SELECT AGE FROM CUSTMR WHERE ID < 3";
        BDDMockito.given(this.resultSet.next()).willReturn(true, false);
        BDDMockito.given(this.resultSet.getObject(1)).willReturn(11);
        Map<String, Object> map = this.template.queryForMap(sql);
        Assert.assertEquals("Wow is Integer", 11, ((Integer) (map.get("age"))).intValue());
        Mockito.verify(this.resultSet).close();
        Mockito.verify(this.statement).close();
    }

    @Test
    public void testQueryForObjectThrowsIncorrectResultSizeForMoreThanOneRow() throws Exception {
        String sql = "select pass from t_account where first_name='Alef'";
        BDDMockito.given(this.resultSet.next()).willReturn(true, true, false);
        BDDMockito.given(this.resultSet.getString(1)).willReturn("pass");
        this.thrown.expect(IncorrectResultSizeDataAccessException.class);
        try {
            this.template.queryForObject(sql, String.class);
        } finally {
            Mockito.verify(this.resultSet).close();
            Mockito.verify(this.statement).close();
        }
    }

    @Test
    public void testQueryForObjectWithRowMapper() throws Exception {
        String sql = "SELECT AGE FROM CUSTMR WHERE ID = 3";
        BDDMockito.given(this.resultSet.next()).willReturn(true, false);
        BDDMockito.given(this.resultSet.getInt(1)).willReturn(22);
        Object o = this.template.queryForObject(sql, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt(1);
            }
        });
        Assert.assertTrue("Correct result type", (o instanceof Integer));
        Mockito.verify(this.resultSet).close();
        Mockito.verify(this.statement).close();
    }

    @Test
    public void testQueryForObjectWithString() throws Exception {
        String sql = "SELECT AGE FROM CUSTMR WHERE ID = 3";
        BDDMockito.given(this.resultSet.next()).willReturn(true, false);
        BDDMockito.given(this.resultSet.getString(1)).willReturn("myvalue");
        Assert.assertEquals("myvalue", this.template.queryForObject(sql, String.class));
        Mockito.verify(this.resultSet).close();
        Mockito.verify(this.statement).close();
    }

    @Test
    public void testQueryForObjectWithBigInteger() throws Exception {
        String sql = "SELECT AGE FROM CUSTMR WHERE ID = 3";
        BDDMockito.given(this.resultSet.next()).willReturn(true, false);
        BDDMockito.given(this.resultSet.getObject(1, BigInteger.class)).willReturn(new BigInteger("22"));
        Assert.assertEquals(new BigInteger("22"), this.template.queryForObject(sql, BigInteger.class));
        Mockito.verify(this.resultSet).close();
        Mockito.verify(this.statement).close();
    }

    @Test
    public void testQueryForObjectWithBigDecimal() throws Exception {
        String sql = "SELECT AGE FROM CUSTMR WHERE ID = 3";
        BDDMockito.given(this.resultSet.next()).willReturn(true, false);
        BDDMockito.given(this.resultSet.getBigDecimal(1)).willReturn(new BigDecimal("22.5"));
        Assert.assertEquals(new BigDecimal("22.5"), this.template.queryForObject(sql, BigDecimal.class));
        Mockito.verify(this.resultSet).close();
        Mockito.verify(this.statement).close();
    }

    @Test
    public void testQueryForObjectWithInteger() throws Exception {
        String sql = "SELECT AGE FROM CUSTMR WHERE ID = 3";
        BDDMockito.given(this.resultSet.next()).willReturn(true, false);
        BDDMockito.given(this.resultSet.getInt(1)).willReturn(22);
        Assert.assertEquals(Integer.valueOf(22), this.template.queryForObject(sql, Integer.class));
        Mockito.verify(this.resultSet).close();
        Mockito.verify(this.statement).close();
    }

    @Test
    public void testQueryForObjectWithIntegerAndNull() throws Exception {
        String sql = "SELECT AGE FROM CUSTMR WHERE ID = 3";
        BDDMockito.given(this.resultSet.next()).willReturn(true, false);
        BDDMockito.given(this.resultSet.getInt(1)).willReturn(0);
        BDDMockito.given(this.resultSet.wasNull()).willReturn(true);
        Assert.assertNull(this.template.queryForObject(sql, Integer.class));
        Mockito.verify(this.resultSet).close();
        Mockito.verify(this.statement).close();
    }

    @Test
    public void testQueryForInt() throws Exception {
        String sql = "SELECT AGE FROM CUSTMR WHERE ID = 3";
        BDDMockito.given(this.resultSet.next()).willReturn(true, false);
        BDDMockito.given(this.resultSet.getInt(1)).willReturn(22);
        int i = this.template.queryForObject(sql, Integer.class).intValue();
        Assert.assertEquals("Return of an int", 22, i);
        Mockito.verify(this.resultSet).close();
        Mockito.verify(this.statement).close();
    }

    @Test
    public void testQueryForIntPrimitive() throws Exception {
        String sql = "SELECT AGE FROM CUSTMR WHERE ID = 3";
        BDDMockito.given(this.resultSet.next()).willReturn(true, false);
        BDDMockito.given(this.resultSet.getInt(1)).willReturn(22);
        int i = this.template.queryForObject(sql, int.class);
        Assert.assertEquals("Return of an int", 22, i);
        Mockito.verify(this.resultSet).close();
        Mockito.verify(this.statement).close();
    }

    @Test
    public void testQueryForLong() throws Exception {
        String sql = "SELECT AGE FROM CUSTMR WHERE ID = 3";
        BDDMockito.given(this.resultSet.next()).willReturn(true, false);
        BDDMockito.given(this.resultSet.getLong(1)).willReturn(87L);
        long l = this.template.queryForObject(sql, Long.class).longValue();
        Assert.assertEquals("Return of a long", 87, l);
        Mockito.verify(this.resultSet).close();
        Mockito.verify(this.statement).close();
    }

    @Test
    public void testQueryForLongPrimitive() throws Exception {
        String sql = "SELECT AGE FROM CUSTMR WHERE ID = 3";
        BDDMockito.given(this.resultSet.next()).willReturn(true, false);
        BDDMockito.given(this.resultSet.getLong(1)).willReturn(87L);
        long l = this.template.queryForObject(sql, long.class);
        Assert.assertEquals("Return of a long", 87, l);
        Mockito.verify(this.resultSet).close();
        Mockito.verify(this.statement).close();
    }

    @Test
    public void testQueryForListWithArgs() throws Exception {
        doTestQueryForListWithArgs("SELECT AGE FROM CUSTMR WHERE ID < ?");
    }

    @Test
    public void testQueryForListIsNotConfusedByNamedParameterPrefix() throws Exception {
        doTestQueryForListWithArgs("SELECT AGE FROM PREFIX:CUSTMR WHERE ID < ?");
    }

    @Test
    public void testQueryForListWithArgsAndEmptyResult() throws Exception {
        String sql = "SELECT AGE FROM CUSTMR WHERE ID < ?";
        BDDMockito.given(this.resultSet.next()).willReturn(false);
        List<Map<String, Object>> li = this.template.queryForList(sql, new Object[]{ 3 });
        Assert.assertEquals("All rows returned", 0, li.size());
        Mockito.verify(this.preparedStatement).setObject(1, 3);
        Mockito.verify(this.resultSet).close();
        Mockito.verify(this.preparedStatement).close();
    }

    @Test
    public void testQueryForListWithArgsAndSingleRowAndColumn() throws Exception {
        String sql = "SELECT AGE FROM CUSTMR WHERE ID < ?";
        BDDMockito.given(this.resultSet.next()).willReturn(true, false);
        BDDMockito.given(this.resultSet.getObject(1)).willReturn(11);
        List<Map<String, Object>> li = this.template.queryForList(sql, new Object[]{ 3 });
        Assert.assertEquals("All rows returned", 1, li.size());
        Assert.assertEquals("First row is Integer", 11, ((Integer) (li.get(0).get("age"))).intValue());
        Mockito.verify(this.preparedStatement).setObject(1, 3);
        Mockito.verify(this.resultSet).close();
        Mockito.verify(this.preparedStatement).close();
    }

    @Test
    public void testQueryForListWithArgsAndIntegerElementAndSingleRowAndColumn() throws Exception {
        String sql = "SELECT AGE FROM CUSTMR WHERE ID < ?";
        BDDMockito.given(this.resultSet.next()).willReturn(true, false);
        BDDMockito.given(this.resultSet.getInt(1)).willReturn(11);
        List<Integer> li = this.template.queryForList(sql, new Object[]{ 3 }, Integer.class);
        Assert.assertEquals("All rows returned", 1, li.size());
        Assert.assertEquals("First row is Integer", 11, li.get(0).intValue());
        Mockito.verify(this.preparedStatement).setObject(1, 3);
        Mockito.verify(this.resultSet).close();
        Mockito.verify(this.preparedStatement).close();
    }

    @Test
    public void testQueryForMapWithArgsAndSingleRowAndColumn() throws Exception {
        String sql = "SELECT AGE FROM CUSTMR WHERE ID < ?";
        BDDMockito.given(this.resultSet.next()).willReturn(true, false);
        BDDMockito.given(this.resultSet.getObject(1)).willReturn(11);
        Map<String, Object> map = this.template.queryForMap(sql, new Object[]{ 3 });
        Assert.assertEquals("Row is Integer", 11, ((Integer) (map.get("age"))).intValue());
        Mockito.verify(this.preparedStatement).setObject(1, 3);
        Mockito.verify(this.resultSet).close();
        Mockito.verify(this.preparedStatement).close();
    }

    @Test
    public void testQueryForObjectWithArgsAndRowMapper() throws Exception {
        String sql = "SELECT AGE FROM CUSTMR WHERE ID = ?";
        BDDMockito.given(this.resultSet.next()).willReturn(true, false);
        BDDMockito.given(this.resultSet.getInt(1)).willReturn(22);
        Object o = this.template.queryForObject(sql, new Object[]{ 3 }, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt(1);
            }
        });
        Assert.assertTrue("Correct result type", (o instanceof Integer));
        Mockito.verify(this.preparedStatement).setObject(1, 3);
        Mockito.verify(this.resultSet).close();
        Mockito.verify(this.preparedStatement).close();
    }

    @Test
    public void testQueryForObjectWithArgsAndInteger() throws Exception {
        String sql = "SELECT AGE FROM CUSTMR WHERE ID = ?";
        BDDMockito.given(this.resultSet.next()).willReturn(true, false);
        BDDMockito.given(this.resultSet.getInt(1)).willReturn(22);
        Object o = this.template.queryForObject(sql, new Object[]{ 3 }, Integer.class);
        Assert.assertTrue("Correct result type", (o instanceof Integer));
        Mockito.verify(this.preparedStatement).setObject(1, 3);
        Mockito.verify(this.resultSet).close();
        Mockito.verify(this.preparedStatement).close();
    }

    @Test
    public void testQueryForIntWithArgs() throws Exception {
        String sql = "SELECT AGE FROM CUSTMR WHERE ID = ?";
        BDDMockito.given(this.resultSet.next()).willReturn(true, false);
        BDDMockito.given(this.resultSet.getInt(1)).willReturn(22);
        int i = this.template.queryForObject(sql, new Object[]{ 3 }, Integer.class).intValue();
        Assert.assertEquals("Return of an int", 22, i);
        Mockito.verify(this.preparedStatement).setObject(1, 3);
        Mockito.verify(this.resultSet).close();
        Mockito.verify(this.preparedStatement).close();
    }

    @Test
    public void testQueryForLongWithArgs() throws Exception {
        String sql = "SELECT AGE FROM CUSTMR WHERE ID = ?";
        BDDMockito.given(this.resultSet.next()).willReturn(true, false);
        BDDMockito.given(this.resultSet.getLong(1)).willReturn(87L);
        long l = this.template.queryForObject(sql, new Object[]{ 3 }, Long.class).longValue();
        Assert.assertEquals("Return of a long", 87, l);
        Mockito.verify(this.preparedStatement).setObject(1, 3);
        Mockito.verify(this.resultSet).close();
        Mockito.verify(this.preparedStatement).close();
    }
}

