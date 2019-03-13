/**
 * Copyright 2008-2014 the original author or authors.
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
package org.springframework.batch.item.database;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;


@RunWith(JUnit4.class)
public class JdbcCursorItemReaderConfigTests {
    /* Should fail if trying to call getConnection() twice */
    @Test
    public void testUsesCurrentTransaction() throws Exception {
        DataSource ds = Mockito.mock(DataSource.class);
        Connection con = Mockito.mock(Connection.class);
        Mockito.when(con.getAutoCommit()).thenReturn(false);
        PreparedStatement ps = Mockito.mock(PreparedStatement.class);
        Mockito.when(con.prepareStatement("select foo from bar", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT)).thenReturn(ps);
        Mockito.when(ds.getConnection()).thenReturn(con);
        Mockito.when(ds.getConnection()).thenReturn(con);
        con.commit();
        PlatformTransactionManager tm = new DataSourceTransactionManager(ds);
        TransactionTemplate tt = new TransactionTemplate(tm);
        final JdbcCursorItemReader<String> reader = new JdbcCursorItemReader();
        reader.setDataSource(new ExtendedConnectionDataSourceProxy(ds));
        reader.setUseSharedExtendedConnection(true);
        reader.setSql("select foo from bar");
        final ExecutionContext ec = new ExecutionContext();
        tt.execute(new org.springframework.transaction.support.TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
                reader.open(ec);
                reader.close();
                return null;
            }
        });
    }

    /* Should fail if trying to call getConnection() twice */
    @Test
    public void testUsesItsOwnTransaction() throws Exception {
        DataSource ds = Mockito.mock(DataSource.class);
        Connection con = Mockito.mock(Connection.class);
        Mockito.when(con.getAutoCommit()).thenReturn(false);
        PreparedStatement ps = Mockito.mock(PreparedStatement.class);
        Mockito.when(con.prepareStatement("select foo from bar", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)).thenReturn(ps);
        Mockito.when(ds.getConnection()).thenReturn(con);
        Mockito.when(ds.getConnection()).thenReturn(con);
        con.commit();
        PlatformTransactionManager tm = new DataSourceTransactionManager(ds);
        TransactionTemplate tt = new TransactionTemplate(tm);
        final JdbcCursorItemReader<String> reader = new JdbcCursorItemReader();
        reader.setDataSource(ds);
        reader.setSql("select foo from bar");
        final ExecutionContext ec = new ExecutionContext();
        tt.execute(new org.springframework.transaction.support.TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
                reader.open(ec);
                reader.close();
                return null;
            }
        });
    }

    @Test
    public void testOverrideConnectionAutoCommit() throws Exception {
        boolean initialAutoCommit = false;
        boolean neededAutoCommit = true;
        DataSource ds = Mockito.mock(DataSource.class);
        Connection con = Mockito.mock(Connection.class);
        Mockito.when(con.getAutoCommit()).thenReturn(initialAutoCommit);
        PreparedStatement ps = Mockito.mock(PreparedStatement.class);
        Mockito.when(con.prepareStatement("select foo from bar", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)).thenReturn(ps);
        Mockito.when(ds.getConnection()).thenReturn(con);
        final JdbcCursorItemReader<String> reader = new JdbcCursorItemReader();
        reader.setDataSource(ds);
        reader.setSql("select foo from bar");
        reader.setConnectionAutoCommit(neededAutoCommit);
        // Check "open" outside of a transaction (see AbstractStep#execute())
        final ExecutionContext ec = new ExecutionContext();
        reader.open(ec);
        ArgumentCaptor<Boolean> autoCommitCaptor = ArgumentCaptor.forClass(Boolean.class);
        Mockito.verify(con, Mockito.times(1)).setAutoCommit(autoCommitCaptor.capture());
        Assert.assertEquals(neededAutoCommit, autoCommitCaptor.getValue());
        Mockito.reset(con);
        reader.close();
        // Check restored autocommit value
        autoCommitCaptor = ArgumentCaptor.forClass(Boolean.class);
        Mockito.verify(con, Mockito.times(1)).setAutoCommit(autoCommitCaptor.capture());
        Assert.assertEquals(initialAutoCommit, autoCommitCaptor.getValue());
    }
}

