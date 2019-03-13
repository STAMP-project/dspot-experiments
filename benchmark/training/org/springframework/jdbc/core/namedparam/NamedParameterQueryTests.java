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
package org.springframework.jdbc.core.namedparam;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


/**
 *
 *
 * @author Thomas Risberg
 * @author Phillip Webb
 */
public class NamedParameterQueryTests {
    private DataSource dataSource;

    private Connection connection;

    private PreparedStatement preparedStatement;

    private ResultSet resultSet;

    private ResultSetMetaData resultSetMetaData;

    private NamedParameterJdbcTemplate template;

    @Test
    public void testQueryForListWithParamMap() throws Exception {
        BDDMockito.given(resultSet.getMetaData()).willReturn(resultSetMetaData);
        BDDMockito.given(resultSet.next()).willReturn(true, true, false);
        BDDMockito.given(resultSet.getObject(1)).willReturn(11, 12);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", 3);
        List<Map<String, Object>> li = template.queryForList("SELECT AGE FROM CUSTMR WHERE ID < :id", params);
        Assert.assertEquals("All rows returned", 2, li.size());
        Assert.assertEquals("First row is Integer", 11, ((Integer) (li.get(0).get("age"))).intValue());
        Assert.assertEquals("Second row is Integer", 12, ((Integer) (li.get(1).get("age"))).intValue());
        Mockito.verify(connection).prepareStatement("SELECT AGE FROM CUSTMR WHERE ID < ?");
        Mockito.verify(preparedStatement).setObject(1, 3);
    }

    @Test
    public void testQueryForListWithParamMapAndEmptyResult() throws Exception {
        BDDMockito.given(resultSet.next()).willReturn(false);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", 3);
        List<Map<String, Object>> li = template.queryForList("SELECT AGE FROM CUSTMR WHERE ID < :id", params);
        Assert.assertEquals("All rows returned", 0, li.size());
        Mockito.verify(connection).prepareStatement("SELECT AGE FROM CUSTMR WHERE ID < ?");
        Mockito.verify(preparedStatement).setObject(1, 3);
    }

    @Test
    public void testQueryForListWithParamMapAndSingleRowAndColumn() throws Exception {
        BDDMockito.given(resultSet.getMetaData()).willReturn(resultSetMetaData);
        BDDMockito.given(resultSet.next()).willReturn(true, false);
        BDDMockito.given(resultSet.getObject(1)).willReturn(11);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", 3);
        List<Map<String, Object>> li = template.queryForList("SELECT AGE FROM CUSTMR WHERE ID < :id", params);
        Assert.assertEquals("All rows returned", 1, li.size());
        Assert.assertEquals("First row is Integer", 11, ((Integer) (li.get(0).get("age"))).intValue());
        Mockito.verify(connection).prepareStatement("SELECT AGE FROM CUSTMR WHERE ID < ?");
        Mockito.verify(preparedStatement).setObject(1, 3);
    }

    @Test
    public void testQueryForListWithParamMapAndIntegerElementAndSingleRowAndColumn() throws Exception {
        BDDMockito.given(resultSet.getMetaData()).willReturn(resultSetMetaData);
        BDDMockito.given(resultSet.next()).willReturn(true, false);
        BDDMockito.given(resultSet.getInt(1)).willReturn(11);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", 3);
        List<Integer> li = template.queryForList("SELECT AGE FROM CUSTMR WHERE ID < :id", params, Integer.class);
        Assert.assertEquals("All rows returned", 1, li.size());
        Assert.assertEquals("First row is Integer", 11, li.get(0).intValue());
        Mockito.verify(connection).prepareStatement("SELECT AGE FROM CUSTMR WHERE ID < ?");
        Mockito.verify(preparedStatement).setObject(1, 3);
    }

    @Test
    public void testQueryForMapWithParamMapAndSingleRowAndColumn() throws Exception {
        BDDMockito.given(resultSet.getMetaData()).willReturn(resultSetMetaData);
        BDDMockito.given(resultSet.next()).willReturn(true, false);
        BDDMockito.given(resultSet.getObject(1)).willReturn(11);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", 3);
        Map<String, Object> map = template.queryForMap("SELECT AGE FROM CUSTMR WHERE ID < :id", params);
        Assert.assertEquals("Row is Integer", 11, ((Integer) (map.get("age"))).intValue());
        Mockito.verify(connection).prepareStatement("SELECT AGE FROM CUSTMR WHERE ID < ?");
        Mockito.verify(preparedStatement).setObject(1, 3);
    }

    @Test
    public void testQueryForObjectWithParamMapAndRowMapper() throws Exception {
        BDDMockito.given(resultSet.next()).willReturn(true, false);
        BDDMockito.given(resultSet.getInt(1)).willReturn(22);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", 3);
        Object o = template.queryForObject("SELECT AGE FROM CUSTMR WHERE ID = :id", params, new org.springframework.jdbc.core.RowMapper<Object>() {
            @Override
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt(1);
            }
        });
        Assert.assertTrue("Correct result type", (o instanceof Integer));
        Mockito.verify(connection).prepareStatement("SELECT AGE FROM CUSTMR WHERE ID = ?");
        Mockito.verify(preparedStatement).setObject(1, 3);
    }

    @Test
    public void testQueryForObjectWithMapAndInteger() throws Exception {
        BDDMockito.given(resultSet.getMetaData()).willReturn(resultSetMetaData);
        BDDMockito.given(resultSet.next()).willReturn(true, false);
        BDDMockito.given(resultSet.getInt(1)).willReturn(22);
        Map<String, Object> params = new HashMap<>();
        params.put("id", 3);
        Object o = template.queryForObject("SELECT AGE FROM CUSTMR WHERE ID = :id", params, Integer.class);
        Assert.assertTrue("Correct result type", (o instanceof Integer));
        Mockito.verify(connection).prepareStatement("SELECT AGE FROM CUSTMR WHERE ID = ?");
        Mockito.verify(preparedStatement).setObject(1, 3);
    }

    @Test
    public void testQueryForObjectWithParamMapAndInteger() throws Exception {
        BDDMockito.given(resultSet.getMetaData()).willReturn(resultSetMetaData);
        BDDMockito.given(resultSet.next()).willReturn(true, false);
        BDDMockito.given(resultSet.getInt(1)).willReturn(22);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", 3);
        Object o = template.queryForObject("SELECT AGE FROM CUSTMR WHERE ID = :id", params, Integer.class);
        Assert.assertTrue("Correct result type", (o instanceof Integer));
        Mockito.verify(connection).prepareStatement("SELECT AGE FROM CUSTMR WHERE ID = ?");
        Mockito.verify(preparedStatement).setObject(1, 3);
    }

    @Test
    public void testQueryForObjectWithParamMapAndList() throws Exception {
        String sql = "SELECT AGE FROM CUSTMR WHERE ID IN (:ids)";
        String sqlToUse = "SELECT AGE FROM CUSTMR WHERE ID IN (?, ?)";
        BDDMockito.given(resultSet.getMetaData()).willReturn(resultSetMetaData);
        BDDMockito.given(resultSet.next()).willReturn(true, false);
        BDDMockito.given(resultSet.getInt(1)).willReturn(22);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ids", Arrays.asList(3, 4));
        Object o = template.queryForObject(sql, params, Integer.class);
        Assert.assertTrue("Correct result type", (o instanceof Integer));
        Mockito.verify(connection).prepareStatement(sqlToUse);
        Mockito.verify(preparedStatement).setObject(1, 3);
    }

    @Test
    public void testQueryForObjectWithParamMapAndListOfExpressionLists() throws Exception {
        BDDMockito.given(resultSet.getMetaData()).willReturn(resultSetMetaData);
        BDDMockito.given(resultSet.next()).willReturn(true, false);
        BDDMockito.given(resultSet.getInt(1)).willReturn(22);
        MapSqlParameterSource params = new MapSqlParameterSource();
        List<Object[]> l1 = new ArrayList<>();
        l1.add(new Object[]{ 3, "Rod" });
        l1.add(new Object[]{ 4, "Juergen" });
        params.addValue("multiExpressionList", l1);
        Object o = template.queryForObject("SELECT AGE FROM CUSTMR WHERE (ID, NAME) IN (:multiExpressionList)", params, Integer.class);
        Assert.assertTrue("Correct result type", (o instanceof Integer));
        Mockito.verify(connection).prepareStatement("SELECT AGE FROM CUSTMR WHERE (ID, NAME) IN ((?, ?), (?, ?))");
        Mockito.verify(preparedStatement).setObject(1, 3);
    }

    @Test
    public void testQueryForIntWithParamMap() throws Exception {
        BDDMockito.given(resultSet.getMetaData()).willReturn(resultSetMetaData);
        BDDMockito.given(resultSet.next()).willReturn(true, false);
        BDDMockito.given(resultSet.getInt(1)).willReturn(22);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", 3);
        int i = template.queryForObject("SELECT AGE FROM CUSTMR WHERE ID = :id", params, Integer.class).intValue();
        Assert.assertEquals("Return of an int", 22, i);
        Mockito.verify(connection).prepareStatement("SELECT AGE FROM CUSTMR WHERE ID = ?");
        Mockito.verify(preparedStatement).setObject(1, 3);
    }

    @Test
    public void testQueryForLongWithParamBean() throws Exception {
        BDDMockito.given(resultSet.getMetaData()).willReturn(resultSetMetaData);
        BDDMockito.given(resultSet.next()).willReturn(true, false);
        BDDMockito.given(resultSet.getLong(1)).willReturn(87L);
        BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(new NamedParameterQueryTests.ParameterBean(3));
        long l = template.queryForObject("SELECT AGE FROM CUSTMR WHERE ID = :id", params, Long.class).longValue();
        Assert.assertEquals("Return of a long", 87, l);
        Mockito.verify(connection).prepareStatement("SELECT AGE FROM CUSTMR WHERE ID = ?");
        Mockito.verify(preparedStatement).setObject(1, 3, Types.INTEGER);
    }

    @Test
    public void testQueryForLongWithParamBeanWithCollection() throws Exception {
        BDDMockito.given(resultSet.getMetaData()).willReturn(resultSetMetaData);
        BDDMockito.given(resultSet.next()).willReturn(true, false);
        BDDMockito.given(resultSet.getLong(1)).willReturn(87L);
        BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(new NamedParameterQueryTests.ParameterCollectionBean(3, 5));
        long l = template.queryForObject("SELECT AGE FROM CUSTMR WHERE ID IN (:ids)", params, Long.class).longValue();
        Assert.assertEquals("Return of a long", 87, l);
        Mockito.verify(connection).prepareStatement("SELECT AGE FROM CUSTMR WHERE ID IN (?, ?)");
        Mockito.verify(preparedStatement).setObject(1, 3);
        Mockito.verify(preparedStatement).setObject(2, 5);
    }

    static class ParameterBean {
        private final int id;

        public ParameterBean(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    static class ParameterCollectionBean {
        private final Collection<Integer> ids;

        public ParameterCollectionBean(Integer... ids) {
            this.ids = Arrays.asList(ids);
        }

        public Collection<Integer> getIds() {
            return ids;
        }
    }
}

