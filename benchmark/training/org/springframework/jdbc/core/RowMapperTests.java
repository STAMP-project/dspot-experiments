/**
 * Copyright 2002-2015 the original author or authors.
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


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.tests.sample.beans.TestBean;


/**
 *
 *
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 02.08.2004
 */
public class RowMapperTests {
    private final Connection connection = Mockito.mock(Connection.class);

    private final Statement statement = Mockito.mock(Statement.class);

    private final PreparedStatement preparedStatement = Mockito.mock(PreparedStatement.class);

    private final ResultSet resultSet = Mockito.mock(ResultSet.class);

    private final JdbcTemplate template = new JdbcTemplate();

    private final RowMapper<TestBean> testRowMapper = ( rs, rowNum) -> new TestBean(rs.getString(1), rs.getInt(2));

    private List<TestBean> result;

    @Test
    public void staticQueryWithRowMapper() throws SQLException {
        result = template.query("some SQL", testRowMapper);
        Mockito.verify(statement).close();
    }

    @Test
    public void preparedStatementCreatorWithRowMapper() throws SQLException {
        result = template.query(( con) -> preparedStatement, testRowMapper);
        Mockito.verify(preparedStatement).close();
    }

    @Test
    public void preparedStatementSetterWithRowMapper() throws SQLException {
        result = template.query("some SQL", ( ps) -> ps.setString(1, "test"), testRowMapper);
        Mockito.verify(preparedStatement).setString(1, "test");
        Mockito.verify(preparedStatement).close();
    }

    @Test
    public void queryWithArgsAndRowMapper() throws SQLException {
        result = template.query("some SQL", new Object[]{ "test1", "test2" }, testRowMapper);
        preparedStatement.setString(1, "test1");
        preparedStatement.setString(2, "test2");
        preparedStatement.close();
    }

    @Test
    public void queryWithArgsAndTypesAndRowMapper() throws SQLException {
        result = template.query("some SQL", new Object[]{ "test1", "test2" }, new int[]{ Types.VARCHAR, Types.VARCHAR }, testRowMapper);
        Mockito.verify(preparedStatement).setString(1, "test1");
        Mockito.verify(preparedStatement).setString(2, "test2");
        Mockito.verify(preparedStatement).close();
    }
}

