/**
 * All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.quartz.impl.jdbcjobstore;


import Constants.COL_TRIGGER_TYPE;
import Constants.TTYPE_BLOB;
import Constants.TTYPE_SIMPLE;
import java.io.IOException;
import java.io.NotSerializableException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import junit.framework.TestCase;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.quartz.JobDataMap;
import org.quartz.JobPersistenceException;
import org.quartz.TriggerKey;
import org.quartz.simpl.SimpleClassLoadHelper;
import org.quartz.spi.OperableTrigger;
import org.slf4j.LoggerFactory;


public class StdJDBCDelegateTest extends TestCase {
    public void testSerializeJobData() throws IOException, NoSuchDelegateException {
        StdJDBCDelegate delegate = new StdJDBCDelegate();
        delegate.initialize(LoggerFactory.getLogger(getClass()), "QRTZ_", "TESTSCHED", "INSTANCE", new SimpleClassLoadHelper(), false, "");
        JobDataMap jdm = new JobDataMap();
        delegate.serializeJobData(jdm).close();
        jdm.clear();
        jdm.put("key", "value");
        jdm.put("key2", null);
        delegate.serializeJobData(jdm).close();
        jdm.clear();
        jdm.put("key1", "value");
        jdm.put("key2", null);
        jdm.put("key3", new Object());
        try {
            delegate.serializeJobData(jdm);
            TestCase.fail();
        } catch (NotSerializableException e) {
            TestCase.assertTrue(((e.getMessage().indexOf("key3")) >= 0));
        }
    }

    public void testSelectBlobTriggerWithNoBlobContent() throws IOException, ClassNotFoundException, SQLException, JobPersistenceException {
        StdJDBCDelegate jdbcDelegate = new StdJDBCDelegate();
        jdbcDelegate.initialize(LoggerFactory.getLogger(getClass()), "QRTZ_", "TESTSCHED", "INSTANCE", new SimpleClassLoadHelper(), false, "");
        Connection conn = Mockito.mock(Connection.class);
        PreparedStatement preparedStatement = Mockito.mock(PreparedStatement.class);
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        Mockito.when(conn.prepareStatement(ArgumentMatchers.anyString())).thenReturn(preparedStatement);
        Mockito.when(preparedStatement.executeQuery()).thenReturn(resultSet);
        // First result set has results, second has none
        Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
        Mockito.when(resultSet.getString(COL_TRIGGER_TYPE)).thenReturn(TTYPE_BLOB);
        OperableTrigger trigger = jdbcDelegate.selectTrigger(conn, TriggerKey.triggerKey("test"));
        TestCase.assertNull(trigger);
    }

    public void testSelectSimpleTriggerWithExceptionWithExtendedProps() throws IOException, ClassNotFoundException, SQLException, JobPersistenceException {
        TriggerPersistenceDelegate persistenceDelegate = Mockito.mock(TriggerPersistenceDelegate.class);
        IllegalStateException exception = new IllegalStateException();
        Mockito.when(persistenceDelegate.loadExtendedTriggerProperties(ArgumentMatchers.any(Connection.class), ArgumentMatchers.any(TriggerKey.class))).thenThrow(exception);
        StdJDBCDelegate jdbcDelegate = new StdJDBCDelegateTest.TestStdJDBCDelegate(persistenceDelegate);
        jdbcDelegate.initialize(LoggerFactory.getLogger(getClass()), "QRTZ_", "TESTSCHED", "INSTANCE", new SimpleClassLoadHelper(), false, "");
        Connection conn = Mockito.mock(Connection.class);
        PreparedStatement preparedStatement = Mockito.mock(PreparedStatement.class);
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        Mockito.when(conn.prepareStatement(ArgumentMatchers.anyString())).thenReturn(preparedStatement);
        // Mock basic trigger data
        Mockito.when(preparedStatement.executeQuery()).thenReturn(resultSet);
        Mockito.when(resultSet.next()).thenReturn(true);
        Mockito.when(resultSet.getString(COL_TRIGGER_TYPE)).thenReturn(TTYPE_SIMPLE);
        try {
            jdbcDelegate.selectTrigger(conn, TriggerKey.triggerKey("test"));
            TestCase.fail("Trigger selection should result in exception");
        } catch (IllegalStateException e) {
            TestCase.assertSame(exception, e);
        }
        Mockito.verify(persistenceDelegate).loadExtendedTriggerProperties(ArgumentMatchers.any(Connection.class), ArgumentMatchers.any(TriggerKey.class));
    }

    public void testSelectSimpleTriggerWithDeleteBeforeSelectExtendedProps() throws IOException, ClassNotFoundException, SQLException, JobPersistenceException {
        TriggerPersistenceDelegate persistenceDelegate = Mockito.mock(TriggerPersistenceDelegate.class);
        Mockito.when(persistenceDelegate.loadExtendedTriggerProperties(ArgumentMatchers.any(Connection.class), ArgumentMatchers.any(TriggerKey.class))).thenThrow(new IllegalStateException());
        StdJDBCDelegate jdbcDelegate = new StdJDBCDelegateTest.TestStdJDBCDelegate(persistenceDelegate);
        jdbcDelegate.initialize(LoggerFactory.getLogger(getClass()), "QRTZ_", "TESTSCHED", "INSTANCE", new SimpleClassLoadHelper(), false, "");
        Connection conn = Mockito.mock(Connection.class);
        PreparedStatement preparedStatement = Mockito.mock(PreparedStatement.class);
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        Mockito.when(conn.prepareStatement(ArgumentMatchers.anyString())).thenReturn(preparedStatement);
        Mockito.when(preparedStatement.executeQuery()).thenReturn(resultSet);
        // First result set has results, second has none
        Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
        Mockito.when(resultSet.getString(COL_TRIGGER_TYPE)).thenReturn(TTYPE_SIMPLE);
        OperableTrigger trigger = jdbcDelegate.selectTrigger(conn, TriggerKey.triggerKey("test"));
        TestCase.assertNull(trigger);
        Mockito.verify(persistenceDelegate).loadExtendedTriggerProperties(ArgumentMatchers.any(Connection.class), ArgumentMatchers.any(TriggerKey.class));
    }

    static class TestStdJDBCDelegate extends StdJDBCDelegate {
        private final TriggerPersistenceDelegate testDelegate;

        public TestStdJDBCDelegate(TriggerPersistenceDelegate testDelegate) {
            this.testDelegate = testDelegate;
        }

        @Override
        public TriggerPersistenceDelegate findTriggerPersistenceDelegate(String discriminator) {
            return testDelegate;
        }
    }
}

