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
package org.springframework.jdbc.object;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;


/**
 *
 *
 * @author Thomas Risberg
 * @author Juergen Hoeller
 */
public class GenericSqlQueryTests {
    private static final String SELECT_ID_FORENAME_NAMED_PARAMETERS_PARSED = "select id, forename from custmr where id = ? and country = ?";

    private DefaultListableBeanFactory beanFactory;

    private Connection connection;

    private PreparedStatement preparedStatement;

    private ResultSet resultSet;

    @Test
    public void testCustomerQueryWithPlaceholders() throws SQLException {
        SqlQuery<?> query = ((SqlQuery<?>) (beanFactory.getBean("queryWithPlaceholders")));
        doTestCustomerQuery(query, false);
    }

    @Test
    public void testCustomerQueryWithNamedParameters() throws SQLException {
        SqlQuery<?> query = ((SqlQuery<?>) (beanFactory.getBean("queryWithNamedParameters")));
        doTestCustomerQuery(query, true);
    }

    @Test
    public void testCustomerQueryWithRowMapperInstance() throws SQLException {
        SqlQuery<?> query = ((SqlQuery<?>) (beanFactory.getBean("queryWithRowMapperBean")));
        doTestCustomerQuery(query, true);
    }
}

