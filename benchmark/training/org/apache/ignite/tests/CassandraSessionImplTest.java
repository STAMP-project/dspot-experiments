/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.tests;


import Cluster.Builder;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.exceptions.InvalidQueryException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.ignite.IgniteLogger;
import org.apache.ignite.cache.store.cassandra.persistence.KeyValuePersistenceSettings;
import org.apache.ignite.cache.store.cassandra.session.BatchExecutionAssistant;
import org.apache.ignite.cache.store.cassandra.session.CassandraSessionImpl;
import org.apache.ignite.cache.store.cassandra.session.WrappedPreparedStatement;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class CassandraSessionImplTest {
    private PreparedStatement preparedStatement1 = CassandraSessionImplTest.mockPreparedStatement();

    private PreparedStatement preparedStatement2 = CassandraSessionImplTest.mockPreparedStatement();

    private CassandraSessionImplTest.MyBoundStatement1 boundStatement1 = new CassandraSessionImplTest.MyBoundStatement1(preparedStatement1);

    private CassandraSessionImplTest.MyBoundStatement2 boundStatement2 = new CassandraSessionImplTest.MyBoundStatement2(preparedStatement2);

    @SuppressWarnings("unchecked")
    @Test
    public void executeFailureTest() {
        Session session1 = Mockito.mock(Session.class);
        Session session2 = Mockito.mock(Session.class);
        Mockito.when(session1.prepare(ArgumentMatchers.any(String.class))).thenReturn(preparedStatement1);
        Mockito.when(session2.prepare(ArgumentMatchers.any(String.class))).thenReturn(preparedStatement2);
        ResultSetFuture rsFuture = Mockito.mock(ResultSetFuture.class);
        ResultSet rs = Mockito.mock(ResultSet.class);
        Iterator it = Mockito.mock(Iterator.class);
        Mockito.when(it.hasNext()).thenReturn(true);
        Mockito.when(it.next()).thenReturn(Mockito.mock(Row.class));
        Mockito.when(rs.iterator()).thenReturn(it);
        Mockito.when(rsFuture.getUninterruptibly()).thenReturn(rs);
        /* @formatter:off */
        Mockito.when(session1.executeAsync(ArgumentMatchers.any(Statement.class))).thenThrow(new InvalidQueryException("You may have used a PreparedStatement that was created with another Cluster instance")).thenThrow(new RuntimeException("this session should be refreshed / recreated"));
        Mockito.when(session2.executeAsync(boundStatement1)).thenThrow(new InvalidQueryException("You may have used a PreparedStatement that was created with another Cluster instance"));
        Mockito.when(session2.executeAsync(boundStatement2)).thenReturn(rsFuture);
        /* @formatter:on */
        Cluster cluster = Mockito.mock(Cluster.class);
        Mockito.when(cluster.connect()).thenReturn(session1).thenReturn(session2);
        Mockito.when(session1.getCluster()).thenReturn(cluster);
        Mockito.when(session2.getCluster()).thenReturn(cluster);
        Cluster.Builder builder = Mockito.mock(Builder.class);
        Mockito.when(builder.build()).thenReturn(cluster);
        CassandraSessionImpl cassandraSession = new CassandraSessionImpl(builder, null, ConsistencyLevel.ONE, ConsistencyLevel.ONE, 0, Mockito.mock(IgniteLogger.class));
        BatchExecutionAssistant<String, String> batchExecutionAssistant = new CassandraSessionImplTest.MyBatchExecutionAssistant();
        ArrayList<String> data = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            data.add(String.valueOf(i));
        }
        cassandraSession.execute(batchExecutionAssistant, data);
        Mockito.verify(cluster, Mockito.times(2)).connect();
        Mockito.verify(session1, Mockito.times(1)).prepare(ArgumentMatchers.any(String.class));
        Mockito.verify(session2, Mockito.times(1)).prepare(ArgumentMatchers.any(String.class));
        Assert.assertEquals(10, batchExecutionAssistant.processedCount());
    }

    private class MyBatchExecutionAssistant implements BatchExecutionAssistant {
        private Set<Integer> processed = new HashSet<>();

        @Override
        public void process(Row row, int seqNum) {
            if (processed.contains(seqNum))
                return;

            processed.add(seqNum);
        }

        @Override
        public boolean alreadyProcessed(int seqNum) {
            return processed.contains(seqNum);
        }

        @Override
        public int processedCount() {
            return processed.size();
        }

        @Override
        public boolean tableExistenceRequired() {
            return false;
        }

        @Override
        public String getTable() {
            return null;
        }

        @Override
        public String getStatement() {
            return null;
        }

        @Override
        public BoundStatement bindStatement(PreparedStatement statement, Object obj) {
            if (statement instanceof WrappedPreparedStatement)
                statement = getWrappedStatement();

            if (statement == (preparedStatement1)) {
                return boundStatement1;
            } else
                if (statement == (preparedStatement2)) {
                    return boundStatement2;
                }

            throw new RuntimeException("unexpected");
        }

        @Override
        public KeyValuePersistenceSettings getPersistenceSettings() {
            return null;
        }

        @Override
        public String operationName() {
            return null;
        }

        @Override
        public Object processedData() {
            return null;
        }
    }

    private static class MyBoundStatement1 extends BoundStatement {
        MyBoundStatement1(PreparedStatement ps) {
            super(ps);
        }
    }

    private static class MyBoundStatement2 extends BoundStatement {
        MyBoundStatement2(PreparedStatement ps) {
            super(ps);
        }
    }
}

