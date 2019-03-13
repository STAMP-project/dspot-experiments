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
package org.springframework.jdbc.support;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.jdbc.support.incrementer.HanaSequenceMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.HsqlMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.MySQLMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.OracleSequenceMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.PostgresSequenceMaxValueIncrementer;


/**
 *
 *
 * @author Juergen Hoeller
 * @since 27.02.2004
 */
public class DataFieldMaxValueIncrementerTests {
    private final DataSource dataSource = Mockito.mock(DataSource.class);

    private final Connection connection = Mockito.mock(Connection.class);

    private final Statement statement = Mockito.mock(Statement.class);

    private final ResultSet resultSet = Mockito.mock(ResultSet.class);

    @Test
    public void testHanaSequenceMaxValueIncrementer() throws SQLException {
        BDDMockito.given(dataSource.getConnection()).willReturn(connection);
        BDDMockito.given(connection.createStatement()).willReturn(statement);
        BDDMockito.given(statement.executeQuery("select myseq.nextval from dummy")).willReturn(resultSet);
        BDDMockito.given(resultSet.next()).willReturn(true);
        BDDMockito.given(resultSet.getLong(1)).willReturn(10L, 12L);
        HanaSequenceMaxValueIncrementer incrementer = new HanaSequenceMaxValueIncrementer();
        incrementer.setDataSource(dataSource);
        incrementer.setIncrementerName("myseq");
        incrementer.setPaddingLength(2);
        incrementer.afterPropertiesSet();
        Assert.assertEquals(10, incrementer.nextLongValue());
        Assert.assertEquals("12", incrementer.nextStringValue());
        Mockito.verify(resultSet, Mockito.times(2)).close();
        Mockito.verify(statement, Mockito.times(2)).close();
        Mockito.verify(connection, Mockito.times(2)).close();
    }

    @Test
    public void testHsqlMaxValueIncrementer() throws SQLException {
        BDDMockito.given(dataSource.getConnection()).willReturn(connection);
        BDDMockito.given(connection.createStatement()).willReturn(statement);
        BDDMockito.given(statement.executeQuery("select max(identity()) from myseq")).willReturn(resultSet);
        BDDMockito.given(resultSet.next()).willReturn(true);
        BDDMockito.given(resultSet.getLong(1)).willReturn(0L, 1L, 2L, 3L, 4L, 5L);
        HsqlMaxValueIncrementer incrementer = new HsqlMaxValueIncrementer();
        incrementer.setDataSource(dataSource);
        incrementer.setIncrementerName("myseq");
        incrementer.setColumnName("seq");
        incrementer.setCacheSize(3);
        incrementer.setPaddingLength(3);
        incrementer.afterPropertiesSet();
        Assert.assertEquals(0, incrementer.nextIntValue());
        Assert.assertEquals(1, incrementer.nextLongValue());
        Assert.assertEquals("002", incrementer.nextStringValue());
        Assert.assertEquals(3, incrementer.nextIntValue());
        Assert.assertEquals(4, incrementer.nextLongValue());
        Mockito.verify(statement, Mockito.times(6)).executeUpdate("insert into myseq values(null)");
        Mockito.verify(statement).executeUpdate("delete from myseq where seq < 2");
        Mockito.verify(statement).executeUpdate("delete from myseq where seq < 5");
        Mockito.verify(resultSet, Mockito.times(6)).close();
        Mockito.verify(statement, Mockito.times(2)).close();
        Mockito.verify(connection, Mockito.times(2)).close();
    }

    @Test
    public void testHsqlMaxValueIncrementerWithDeleteSpecificValues() throws SQLException {
        BDDMockito.given(dataSource.getConnection()).willReturn(connection);
        BDDMockito.given(connection.createStatement()).willReturn(statement);
        BDDMockito.given(statement.executeQuery("select max(identity()) from myseq")).willReturn(resultSet);
        BDDMockito.given(resultSet.next()).willReturn(true);
        BDDMockito.given(resultSet.getLong(1)).willReturn(0L, 1L, 2L, 3L, 4L, 5L);
        HsqlMaxValueIncrementer incrementer = new HsqlMaxValueIncrementer();
        incrementer.setDataSource(dataSource);
        incrementer.setIncrementerName("myseq");
        incrementer.setColumnName("seq");
        incrementer.setCacheSize(3);
        incrementer.setPaddingLength(3);
        incrementer.setDeleteSpecificValues(true);
        incrementer.afterPropertiesSet();
        Assert.assertEquals(0, incrementer.nextIntValue());
        Assert.assertEquals(1, incrementer.nextLongValue());
        Assert.assertEquals("002", incrementer.nextStringValue());
        Assert.assertEquals(3, incrementer.nextIntValue());
        Assert.assertEquals(4, incrementer.nextLongValue());
        Mockito.verify(statement, Mockito.times(6)).executeUpdate("insert into myseq values(null)");
        Mockito.verify(statement).executeUpdate("delete from myseq where seq in (-1, 0, 1)");
        Mockito.verify(statement).executeUpdate("delete from myseq where seq in (2, 3, 4)");
        Mockito.verify(resultSet, Mockito.times(6)).close();
        Mockito.verify(statement, Mockito.times(2)).close();
        Mockito.verify(connection, Mockito.times(2)).close();
    }

    @Test
    public void testMySQLMaxValueIncrementer() throws SQLException {
        BDDMockito.given(dataSource.getConnection()).willReturn(connection);
        BDDMockito.given(connection.createStatement()).willReturn(statement);
        BDDMockito.given(statement.executeQuery("select last_insert_id()")).willReturn(resultSet);
        BDDMockito.given(resultSet.next()).willReturn(true);
        BDDMockito.given(resultSet.getLong(1)).willReturn(2L, 4L);
        MySQLMaxValueIncrementer incrementer = new MySQLMaxValueIncrementer();
        incrementer.setDataSource(dataSource);
        incrementer.setIncrementerName("myseq");
        incrementer.setColumnName("seq");
        incrementer.setCacheSize(2);
        incrementer.setPaddingLength(1);
        incrementer.afterPropertiesSet();
        Assert.assertEquals(1, incrementer.nextIntValue());
        Assert.assertEquals(2, incrementer.nextLongValue());
        Assert.assertEquals("3", incrementer.nextStringValue());
        Assert.assertEquals(4, incrementer.nextLongValue());
        Mockito.verify(statement, Mockito.times(2)).executeUpdate("update myseq set seq = last_insert_id(seq + 2)");
        Mockito.verify(resultSet, Mockito.times(2)).close();
        Mockito.verify(statement, Mockito.times(2)).close();
        Mockito.verify(connection, Mockito.times(2)).close();
    }

    @Test
    public void testOracleSequenceMaxValueIncrementer() throws SQLException {
        BDDMockito.given(dataSource.getConnection()).willReturn(connection);
        BDDMockito.given(connection.createStatement()).willReturn(statement);
        BDDMockito.given(statement.executeQuery("select myseq.nextval from dual")).willReturn(resultSet);
        BDDMockito.given(resultSet.next()).willReturn(true);
        BDDMockito.given(resultSet.getLong(1)).willReturn(10L, 12L);
        OracleSequenceMaxValueIncrementer incrementer = new OracleSequenceMaxValueIncrementer();
        incrementer.setDataSource(dataSource);
        incrementer.setIncrementerName("myseq");
        incrementer.setPaddingLength(2);
        incrementer.afterPropertiesSet();
        Assert.assertEquals(10, incrementer.nextLongValue());
        Assert.assertEquals("12", incrementer.nextStringValue());
        Mockito.verify(resultSet, Mockito.times(2)).close();
        Mockito.verify(statement, Mockito.times(2)).close();
        Mockito.verify(connection, Mockito.times(2)).close();
    }

    @Test
    public void testPostgresSequenceMaxValueIncrementer() throws SQLException {
        BDDMockito.given(dataSource.getConnection()).willReturn(connection);
        BDDMockito.given(connection.createStatement()).willReturn(statement);
        BDDMockito.given(statement.executeQuery("select nextval('myseq')")).willReturn(resultSet);
        BDDMockito.given(resultSet.next()).willReturn(true);
        BDDMockito.given(resultSet.getLong(1)).willReturn(10L, 12L);
        PostgresSequenceMaxValueIncrementer incrementer = new PostgresSequenceMaxValueIncrementer();
        incrementer.setDataSource(dataSource);
        incrementer.setIncrementerName("myseq");
        incrementer.setPaddingLength(5);
        incrementer.afterPropertiesSet();
        Assert.assertEquals("00010", incrementer.nextStringValue());
        Assert.assertEquals(12, incrementer.nextIntValue());
        Mockito.verify(resultSet, Mockito.times(2)).close();
        Mockito.verify(statement, Mockito.times(2)).close();
        Mockito.verify(connection, Mockito.times(2)).close();
    }
}

