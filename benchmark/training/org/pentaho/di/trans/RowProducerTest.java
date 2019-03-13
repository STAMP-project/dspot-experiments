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
package org.pentaho.di.trans;


import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.StepInterface;


/**
 * Created by mburgess on 10/7/15.
 */
public class RowProducerTest {
    RowProducer rowProducer;

    StepInterface stepInterface;

    RowSet rowSet;

    RowMetaInterface rowMeta;

    Object[] rowData;

    @Test
    public void testPutRow2Arg() throws Exception {
        Mockito.when(rowSet.putRowWait(ArgumentMatchers.any(RowMetaInterface.class), ArgumentMatchers.any(Object[].class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class))).thenReturn(true);
        rowProducer.putRow(rowMeta, rowData);
        Mockito.verify(rowSet, Mockito.times(1)).putRowWait(rowMeta, rowData, Long.MAX_VALUE, TimeUnit.DAYS);
        Assert.assertTrue(rowProducer.putRow(rowMeta, rowData, true));
    }

    @Test
    public void testPutRow3Arg() throws Exception {
        Mockito.when(rowSet.putRowWait(ArgumentMatchers.any(RowMetaInterface.class), ArgumentMatchers.any(Object[].class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class))).thenReturn(true);
        rowProducer.putRow(rowMeta, rowData, false);
        Mockito.verify(rowSet, Mockito.times(1)).putRow(rowMeta, rowData);
    }

    @Test
    public void testPutRowWait() throws Exception {
        rowProducer.putRowWait(rowMeta, rowData, 1, TimeUnit.MILLISECONDS);
        Mockito.verify(rowSet, Mockito.times(1)).putRowWait(rowMeta, rowData, 1, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testFinished() throws Exception {
        rowProducer.finished();
        Mockito.verify(rowSet, Mockito.times(1)).setDone();
    }

    @Test
    public void testGetSetRowSet() throws Exception {
        Assert.assertEquals(rowSet, rowProducer.getRowSet());
        rowProducer.setRowSet(null);
        Assert.assertNull(rowProducer.getRowSet());
        RowSet newRowSet = Mockito.mock(RowSet.class);
        rowProducer.setRowSet(newRowSet);
        Assert.assertEquals(newRowSet, rowProducer.getRowSet());
    }

    @Test
    public void testGetSetStepInterface() throws Exception {
        Assert.assertEquals(stepInterface, rowProducer.getStepInterface());
        rowProducer.setStepInterface(null);
        Assert.assertNull(rowProducer.getStepInterface());
        StepInterface newStepInterface = Mockito.mock(StepInterface.class);
        rowProducer.setStepInterface(newStepInterface);
        Assert.assertEquals(newStepInterface, rowProducer.getStepInterface());
    }
}

