/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.trans.steps.aggregaterows;


import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


public class AggregateRowsTest {
    private StepMockHelper<AggregateRowsMeta, AggregateRowsData> stepMockHelper;

    @Test
    public void testProcessRow() throws KettleException {
        AggregateRows aggregateRows = new AggregateRows(stepMockHelper.stepMeta, stepMockHelper.stepDataInterface, 0, stepMockHelper.transMeta, stepMockHelper.trans);
        aggregateRows.init(stepMockHelper.initStepMetaInterface, stepMockHelper.initStepDataInterface);
        aggregateRows.setInputRowSets(new ArrayList<org.pentaho.di.core.RowSet>(Arrays.asList(createSourceRowSet("TEST"))));
        int fieldSize = stepMockHelper.initStepMetaInterface.getFieldName().length;
        AggregateRowsData data = new AggregateRowsData();
        data.fieldnrs = new int[fieldSize];
        data.counts = new long[fieldSize];
        data.values = new Object[fieldSize];
        Assert.assertTrue(aggregateRows.processRow(stepMockHelper.initStepMetaInterface, data));
        Assert.assertTrue(((aggregateRows.getErrors()) == 0));
        Assert.assertTrue(((aggregateRows.getLinesRead()) > 0));
        RowMetaInterface outputRowMeta = Mockito.mock(RowMetaInterface.class);
        Mockito.when(outputRowMeta.size()).thenReturn(fieldSize);
        data.outputRowMeta = outputRowMeta;
        Assert.assertFalse(aggregateRows.processRow(stepMockHelper.initStepMetaInterface, data));
        Assert.assertTrue(((aggregateRows.getLinesWritten()) > 0));
    }
}

