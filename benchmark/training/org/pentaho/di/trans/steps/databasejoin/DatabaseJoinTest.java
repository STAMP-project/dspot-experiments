/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2019 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.trans.steps.databasejoin;


import java.sql.Connection;
import java.sql.PreparedStatement;
import junit.framework.TestCase;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;


public class DatabaseJoinTest {
    DatabaseJoinMeta mockStepMetaInterface;

    DatabaseJoinData mockStepDataInterface;

    DatabaseJoin mockDatabaseJoin;

    @Test
    public void testStopRunningWhenStepIsStopped() throws KettleException {
        Mockito.doReturn(true).when(mockDatabaseJoin).isStopped();
        mockDatabaseJoin.stopRunning(mockStepMetaInterface, mockStepDataInterface);
        Mockito.verify(mockDatabaseJoin, Mockito.times(1)).isStopped();
        Mockito.verify(mockStepDataInterface, Mockito.times(0)).isDisposed();
    }

    @Test
    public void testStopRunningWhenStepDataInterfaceIsDisposed() throws KettleException {
        Mockito.doReturn(false).when(mockDatabaseJoin).isStopped();
        Mockito.doReturn(true).when(mockStepDataInterface).isDisposed();
        mockDatabaseJoin.stopRunning(mockStepMetaInterface, mockStepDataInterface);
        Mockito.verify(mockDatabaseJoin, Mockito.times(1)).isStopped();
        Mockito.verify(mockStepDataInterface, Mockito.times(1)).isDisposed();
    }

    @Test
    public void testStopRunningWhenStepIsNotStoppedNorStepDataInterfaceIsDisposedAndDatabaseConnectionIsValid() throws KettleException {
        Mockito.doReturn(false).when(mockDatabaseJoin).isStopped();
        Mockito.doReturn(false).when(mockStepDataInterface).isDisposed();
        Mockito.when(mockStepDataInterface.db.getConnection()).thenReturn(Mockito.mock(Connection.class));
        mockDatabaseJoin.stopRunning(mockStepMetaInterface, mockStepDataInterface);
        Mockito.verify(mockDatabaseJoin, Mockito.times(1)).isStopped();
        Mockito.verify(mockStepDataInterface, Mockito.times(1)).isDisposed();
        Mockito.verify(mockStepDataInterface.db, Mockito.times(1)).getConnection();
        Mockito.verify(mockStepDataInterface.db, Mockito.times(1)).cancelStatement(ArgumentMatchers.any(PreparedStatement.class));
        TestCase.assertTrue(mockStepDataInterface.isCanceled);
    }

    @Test
    public void testStopRunningWhenStepIsNotStoppedNorStepDataInterfaceIsDisposedAndDatabaseConnectionIsNotValid() throws KettleException {
        Mockito.doReturn(false).when(mockDatabaseJoin).isStopped();
        Mockito.doReturn(false).when(mockStepDataInterface).isDisposed();
        Mockito.when(mockStepDataInterface.db.getConnection()).thenReturn(null);
        mockDatabaseJoin.stopRunning(mockStepMetaInterface, mockStepDataInterface);
        Mockito.verify(mockDatabaseJoin, Mockito.times(1)).isStopped();
        Mockito.verify(mockStepDataInterface, Mockito.times(1)).isDisposed();
        Mockito.verify(mockStepDataInterface.db, Mockito.times(1)).getConnection();
        Mockito.verify(mockStepDataInterface.db, Mockito.times(0)).cancelStatement(ArgumentMatchers.any(PreparedStatement.class));
        TestCase.assertFalse(mockStepDataInterface.isCanceled);
    }
}

