/**
 * Copyright 2002-2013 the original author or authors.
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
package org.springframework.jdbc.core.support;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.tests.sample.beans.TestBean;


/**
 *
 *
 * @author Rod Johnson
 */
public class JdbcBeanDefinitionReaderTests {
    @Test
    public void testValid() throws Exception {
        String sql = "SELECT NAME AS NAME, PROPERTY AS PROPERTY, VALUE AS VALUE FROM T";
        Connection connection = Mockito.mock(Connection.class);
        DataSource dataSource = Mockito.mock(DataSource.class);
        BDDMockito.given(dataSource.getConnection()).willReturn(connection);
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        BDDMockito.given(resultSet.next()).willReturn(true, true, false);
        BDDMockito.given(resultSet.getString(1)).willReturn("one", "one");
        BDDMockito.given(resultSet.getString(2)).willReturn("(class)", "age");
        BDDMockito.given(resultSet.getString(3)).willReturn("org.springframework.tests.sample.beans.TestBean", "53");
        Statement statement = Mockito.mock(Statement.class);
        BDDMockito.given(statement.executeQuery(sql)).willReturn(resultSet);
        BDDMockito.given(connection.createStatement()).willReturn(statement);
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        JdbcBeanDefinitionReader reader = new JdbcBeanDefinitionReader(bf);
        reader.setDataSource(dataSource);
        reader.loadBeanDefinitions(sql);
        Assert.assertEquals("Incorrect number of bean definitions", 1, bf.getBeanDefinitionCount());
        TestBean tb = ((TestBean) (bf.getBean("one")));
        Assert.assertEquals("Age in TestBean was wrong.", 53, tb.getAge());
        Mockito.verify(resultSet).close();
        Mockito.verify(statement).close();
    }
}

