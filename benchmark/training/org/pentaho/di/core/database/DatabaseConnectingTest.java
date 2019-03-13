/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.core.database;


import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.logging.LoggingObjectInterface;
import org.pentaho.di.junit.rules.RestorePDIEnvironment;


/**
 *
 *
 * @author Andrey Khayrutdinov
 */
public class DatabaseConnectingTest {
    @ClassRule
    public static RestorePDIEnvironment env = new RestorePDIEnvironment();

    private static final String GROUP = "group";

    private static final String ANOTHER_GROUP = "another-group";

    @Test
    public void connect_GroupIsNull() throws Exception {
        Connection connection1 = Mockito.mock(Connection.class);
        DatabaseConnectingTest.DatabaseStub db1 = createStubDatabase(connection1);
        Connection connection2 = Mockito.mock(Connection.class);
        DatabaseConnectingTest.DatabaseStub db2 = createStubDatabase(connection2);
        db1.connect();
        db2.connect();
        Assert.assertEquals(connection1, getConnection());
        Assert.assertEquals(connection2, getConnection());
    }

    @Test
    public void connect_GroupIsEqual_Consequently() throws Exception {
        Connection shared = Mockito.mock(Connection.class);
        DatabaseConnectingTest.DatabaseStub db1 = createStubDatabase(shared);
        connect(DatabaseConnectingTest.GROUP, null);
        DatabaseConnectingTest.DatabaseStub db2 = createStubDatabase(shared);
        connect(DatabaseConnectingTest.GROUP, null);
        assertSharedAmongTheGroup(shared, db1, db2);
    }

    @Test
    public void connect_GroupIsEqual_InParallel() throws Exception {
        final Connection shared = Mockito.mock(Connection.class);
        final int dbsAmount = 300;
        final int threadsAmount = 50;
        List<DatabaseConnectingTest.DatabaseStub> dbs = new ArrayList<DatabaseConnectingTest.DatabaseStub>(dbsAmount);
        Set<Integer> copies = new HashSet<Integer>(dbsAmount);
        ExecutorService pool = Executors.newFixedThreadPool(threadsAmount);
        try {
            CompletionService<DatabaseConnectingTest.DatabaseStub> service = new ExecutorCompletionService<DatabaseConnectingTest.DatabaseStub>(pool);
            for (int i = 0; i < dbsAmount; i++) {
                service.submit(createStubDatabase(shared));
                copies.add((i + 1));
            }
            for (int i = 0; i < dbsAmount; i++) {
                DatabaseConnectingTest.DatabaseStub db = service.take().get();
                Assert.assertEquals(shared, getConnection());
                dbs.add(db);
            }
        } finally {
            pool.shutdown();
        }
        for (DatabaseConnectingTest.DatabaseStub db : dbs) {
            String message = String.format("There should be %d shares of the connection, but found %d", dbsAmount, getOpened());
            // 0 is for those instances that use the shared connection
            Assert.assertTrue(message, (((getOpened()) == 0) || ((getOpened()) == dbsAmount)));
            Assert.assertTrue("Each instance should have a unique 'copy' value", copies.remove(getCopy()));
        }
        Assert.assertTrue(copies.isEmpty());
    }

    @Test
    public void connect_TwoGroups() throws Exception {
        try {
            Connection shared1 = Mockito.mock(Connection.class);
            Connection shared2 = Mockito.mock(Connection.class);
            DatabaseConnectingTest.DatabaseStub db11 = createStubDatabase(shared1);
            DatabaseConnectingTest.DatabaseStub db12 = createStubDatabase(shared1);
            DatabaseConnectingTest.DatabaseStub db21 = createStubDatabase(shared2);
            DatabaseConnectingTest.DatabaseStub db22 = createStubDatabase(shared2);
            connect(DatabaseConnectingTest.GROUP, null);
            connect(DatabaseConnectingTest.GROUP, null);
            connect(DatabaseConnectingTest.ANOTHER_GROUP, null);
            connect(DatabaseConnectingTest.ANOTHER_GROUP, null);
            assertSharedAmongTheGroup(shared1, db11, db12);
            assertSharedAmongTheGroup(shared2, db21, db22);
        } finally {
            removeFromSharedConnectionMap(DatabaseConnectingTest.ANOTHER_GROUP);
        }
    }

    @Test
    public void connect_ManyGroups_Simultaneously() throws Exception {
        final int groupsAmount = 30;
        Map<String, Connection> groups = new HashMap<String, Connection>(groupsAmount);
        for (int i = 0; i < groupsAmount; i++) {
            groups.put(Integer.toString(i), Mockito.mock(Connection.class));
        }
        try {
            ExecutorService pool = Executors.newFixedThreadPool(groupsAmount);
            try {
                CompletionService<DatabaseConnectingTest.DatabaseStub> service = new ExecutorCompletionService<DatabaseConnectingTest.DatabaseStub>(pool);
                Map<DatabaseConnectingTest.DatabaseStub, String> mapping = new HashMap<DatabaseConnectingTest.DatabaseStub, String>(groupsAmount);
                for (Map.Entry<String, Connection> entry : groups.entrySet()) {
                    DatabaseConnectingTest.DatabaseStub stub = createStubDatabase(entry.getValue());
                    mapping.put(stub, entry.getKey());
                    service.submit(stub);
                }
                Set<String> unmatchedGroups = new HashSet<String>(groups.keySet());
                for (int i = 0; i < groupsAmount; i++) {
                    DatabaseConnectingTest.DatabaseStub stub = service.take().get();
                    Assert.assertTrue(unmatchedGroups.remove(mapping.get(stub)));
                }
                Assert.assertTrue(unmatchedGroups.isEmpty());
            } finally {
                pool.shutdown();
            }
        } finally {
            for (String group : groups.keySet()) {
                removeFromSharedConnectionMap(group);
            }
        }
    }

    private static class DatabaseStub extends Database implements Callable<DatabaseConnectingTest.DatabaseStub> {
        private final Connection sharedConnection;

        private boolean connected;

        public DatabaseStub(LoggingObjectInterface parentObject, DatabaseMeta databaseMeta, Connection sharedConnection) {
            super(parentObject, databaseMeta);
            this.sharedConnection = sharedConnection;
            this.connected = false;
        }

        @Override
        public synchronized void normalConnect(String partitionId) throws KettleDatabaseException {
            if (!(connected)) {
                // make a delay to emulate real scenario
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    Assert.fail(e.getMessage());
                }
                setConnection(sharedConnection);
                connected = true;
            }
        }

        @Override
        public DatabaseConnectingTest.DatabaseStub call() throws Exception {
            connect(DatabaseConnectingTest.GROUP, null);
            return this;
        }
    }
}

