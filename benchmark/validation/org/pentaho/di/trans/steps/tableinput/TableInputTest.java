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
package org.pentaho.di.trans.steps.tableinput;


import java.sql.Connection;
import java.sql.PreparedStatement;
import junit.framework.TestCase;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;


public class TableInputTest {
    TableInputMeta mockStepMetaInterface;

    TableInputData mockStepDataInterface;

    TableInput mockTableInput;

    @Test
    public void testStopRunningWhenStepIsStopped() throws KettleException {
        Mockito.doReturn(true).when(mockTableInput).isStopped();
        mockTableInput.stopRunning(mockStepMetaInterface, mockStepDataInterface);
        Mockito.verify(mockTableInput, Mockito.times(1)).isStopped();
        Mockito.verify(mockStepDataInterface, Mockito.times(0)).isDisposed();
    }

    @Test
    public void testStopRunningWhenStepDataInterfaceIsDisposed() throws KettleException {
        Mockito.doReturn(false).when(mockTableInput).isStopped();
        Mockito.doReturn(true).when(mockStepDataInterface).isDisposed();
        mockTableInput.stopRunning(mockStepMetaInterface, mockStepDataInterface);
        Mockito.verify(mockTableInput, Mockito.times(1)).isStopped();
        Mockito.verify(mockStepDataInterface, Mockito.times(1)).isDisposed();
    }

    @Test
    public void testStopRunningWhenStepIsNotStoppedNorStepDataInterfaceIsDisposedAndDatabaseConnectionIsValid() throws KettleException {
        Mockito.doReturn(false).when(mockTableInput).isStopped();
        Mockito.doReturn(false).when(mockStepDataInterface).isDisposed();
        Mockito.when(mockStepDataInterface.db.getConnection()).thenReturn(Mockito.mock(Connection.class));
        mockTableInput.stopRunning(mockStepMetaInterface, mockStepDataInterface);
        Mockito.verify(mockTableInput, Mockito.times(1)).isStopped();
        Mockito.verify(mockStepDataInterface, Mockito.times(1)).isDisposed();
        Mockito.verify(mockStepDataInterface.db, Mockito.times(1)).getConnection();
        Mockito.verify(mockStepDataInterface.db, Mockito.times(1)).cancelQuery();
        TestCase.assertTrue(mockStepDataInterface.isCanceled);
    }

    @Test
    public void testStopRunningWhenStepIsNotStoppedNorStepDataInterfaceIsDisposedAndDatabaseConnectionIsNotValid() throws KettleException {
        Mockito.doReturn(false).when(mockTableInput).isStopped();
        Mockito.doReturn(false).when(mockStepDataInterface).isDisposed();
        Mockito.when(mockStepDataInterface.db.getConnection()).thenReturn(null);
        mockTableInput.stopRunning(mockStepMetaInterface, mockStepDataInterface);
        Mockito.verify(mockTableInput, Mockito.times(1)).isStopped();
        Mockito.verify(mockStepDataInterface, Mockito.times(1)).isDisposed();
        Mockito.verify(mockStepDataInterface.db, Mockito.times(1)).getConnection();
        Mockito.verify(mockStepDataInterface.db, Mockito.times(0)).cancelStatement(ArgumentMatchers.any(PreparedStatement.class));
        TestCase.assertFalse(mockStepDataInterface.isCanceled);
    }
}

