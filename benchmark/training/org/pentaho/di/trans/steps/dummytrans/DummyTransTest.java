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
package org.pentaho.di.trans.steps.dummytrans;


import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


public class DummyTransTest {
    private StepMockHelper<StepMetaInterface, StepDataInterface> stepMockHelper;

    @Test
    public void testDummyTransDoesntWriteOutputWithoutInputRow() throws KettleException {
        DummyTrans dummy = new DummyTrans(stepMockHelper.stepMeta, stepMockHelper.stepDataInterface, 0, stepMockHelper.transMeta, stepMockHelper.trans);
        dummy.init(stepMockHelper.initStepMetaInterface, stepMockHelper.initStepDataInterface);
        RowSet rowSet = stepMockHelper.getMockInputRowSet();
        RowMetaInterface inputRowMeta = Mockito.mock(RowMetaInterface.class);
        Mockito.when(rowSet.getRowMeta()).thenReturn(inputRowMeta);
        dummy.addRowSetToInputRowSets(rowSet);
        RowSet outputRowSet = Mockito.mock(RowSet.class);
        dummy.addRowSetToOutputRowSets(outputRowSet);
        dummy.processRow(stepMockHelper.processRowsStepMetaInterface, stepMockHelper.processRowsStepDataInterface);
        Mockito.verify(inputRowMeta, Mockito.never()).cloneRow(ArgumentMatchers.any(Object[].class));
        Mockito.verify(outputRowSet, Mockito.never()).putRow(ArgumentMatchers.any(RowMetaInterface.class), ArgumentMatchers.any(Object[].class));
    }

    @Test
    public void testDummyTransWritesOutputWithInputRow() throws KettleException {
        DummyTrans dummy = new DummyTrans(stepMockHelper.stepMeta, stepMockHelper.stepDataInterface, 0, stepMockHelper.transMeta, stepMockHelper.trans);
        dummy.init(stepMockHelper.initStepMetaInterface, stepMockHelper.initStepDataInterface);
        Object[] row = new Object[]{ "abcd" };
        RowSet rowSet = stepMockHelper.getMockInputRowSet(row);
        RowMetaInterface inputRowMeta = Mockito.mock(RowMetaInterface.class);
        Mockito.when(inputRowMeta.clone()).thenReturn(inputRowMeta);
        Mockito.when(rowSet.getRowMeta()).thenReturn(inputRowMeta);
        dummy.addRowSetToInputRowSets(rowSet);
        RowSet outputRowSet = Mockito.mock(RowSet.class);
        dummy.addRowSetToOutputRowSets(outputRowSet);
        Mockito.when(outputRowSet.putRow(inputRowMeta, row)).thenReturn(true);
        dummy.processRow(stepMockHelper.processRowsStepMetaInterface, stepMockHelper.processRowsStepDataInterface);
        Mockito.verify(outputRowSet, Mockito.times(1)).putRow(inputRowMeta, row);
    }
}

