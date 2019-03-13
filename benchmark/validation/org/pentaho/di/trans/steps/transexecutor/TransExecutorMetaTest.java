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
package org.pentaho.di.trans.steps.transexecutor;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.step.StepIOMetaInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.errorhandling.StreamInterface;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;


public class TransExecutorMetaTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    LoadSaveTester loadSaveTester;

    @Test
    public void testSerialization() throws KettleException {
        loadSaveTester.testSerialization();
    }

    @Test
    public void firstStreamIsExecutionStatistics() throws Exception {
        StreamInterface stream = TransExecutorMetaTest.mockStream();
        StepIOMetaInterface stepIo = TransExecutorMetaTest.mockStepIo(stream, 0);
        TransExecutorMeta meta = new TransExecutorMeta();
        meta = Mockito.spy(meta);
        Mockito.when(meta.getStepIOMeta()).thenReturn(stepIo);
        Mockito.doCallRealMethod().when(meta).handleStreamSelection(ArgumentMatchers.any(StreamInterface.class));
        meta.handleStreamSelection(stream);
        Assert.assertEquals(stream.getStepMeta(), meta.getExecutionResultTargetStepMeta());
    }

    @Test
    public void secondStreamIsInternalTransformationsOutput() throws Exception {
        StreamInterface stream = TransExecutorMetaTest.mockStream();
        StepIOMetaInterface stepIo = TransExecutorMetaTest.mockStepIo(stream, 1);
        TransExecutorMeta meta = new TransExecutorMeta();
        meta = Mockito.spy(meta);
        Mockito.when(meta.getStepIOMeta()).thenReturn(stepIo);
        Mockito.doCallRealMethod().when(meta).handleStreamSelection(ArgumentMatchers.any(StreamInterface.class));
        meta.handleStreamSelection(stream);
        Assert.assertEquals(stream.getStepMeta(), meta.getOutputRowsSourceStepMeta());
    }

    @Test
    public void thirdStreamIsExecutionResultFiles() throws Exception {
        StreamInterface stream = TransExecutorMetaTest.mockStream();
        StepIOMetaInterface stepIo = TransExecutorMetaTest.mockStepIo(stream, 2);
        TransExecutorMeta meta = new TransExecutorMeta();
        meta = Mockito.spy(meta);
        Mockito.when(meta.getStepIOMeta()).thenReturn(stepIo);
        Mockito.doCallRealMethod().when(meta).handleStreamSelection(ArgumentMatchers.any(StreamInterface.class));
        meta.handleStreamSelection(stream);
        Assert.assertEquals(stream.getStepMeta(), meta.getResultFilesTargetStepMeta());
    }

    @Test
    public void forthStreamIsExecutorsInput() throws Exception {
        StreamInterface stream = TransExecutorMetaTest.mockStream();
        StepIOMetaInterface stepIo = TransExecutorMetaTest.mockStepIo(stream, 3);
        TransExecutorMeta meta = new TransExecutorMeta();
        meta = Mockito.spy(meta);
        Mockito.when(meta.getStepIOMeta()).thenReturn(stepIo);
        Mockito.doCallRealMethod().when(meta).handleStreamSelection(ArgumentMatchers.any(StreamInterface.class));
        meta.handleStreamSelection(stream);
        Assert.assertEquals(stream.getStepMeta(), meta.getExecutorsOutputStepMeta());
    }

    @Test
    public void testPrepareExecutionResultsFields() throws Exception {
        TransExecutorMeta meta = new TransExecutorMeta();
        meta = Mockito.spy(meta);
        RowMetaInterface row = Mockito.mock(RowMetaInterface.class);
        StepMeta nextStep = Mockito.mock(StepMeta.class);
        meta.setExecutionResultTargetStepMeta(nextStep);
        meta.setExecutionTimeField("time");
        StepMeta parent = Mockito.mock(StepMeta.class);
        Mockito.doReturn(parent).when(meta).getParentStepMeta();
        Mockito.when(parent.getName()).thenReturn("parent step");
        meta.prepareExecutionResultsFields(row, nextStep);
        // make sure we get the name of the parent step meta... used for the origin step
        Mockito.verify(parent).getName();
        ArgumentCaptor<ValueMetaInterface> argumentCaptor = ArgumentCaptor.forClass(ValueMetaInterface.class);
        Mockito.verify(row).addValueMeta(argumentCaptor.capture());
        Assert.assertEquals("parent step", argumentCaptor.getValue().getOrigin());
    }

    @Test
    public void testPrepareExecutionResultsFileFields() throws Exception {
        TransExecutorMeta meta = new TransExecutorMeta();
        meta = Mockito.spy(meta);
        RowMetaInterface row = Mockito.mock(RowMetaInterface.class);
        StepMeta nextStep = Mockito.mock(StepMeta.class);
        meta.setResultFilesTargetStepMeta(nextStep);
        meta.setResultFilesFileNameField("file_name");
        StepMeta parent = Mockito.mock(StepMeta.class);
        Mockito.doReturn(parent).when(meta).getParentStepMeta();
        Mockito.when(parent.getName()).thenReturn("parent step");
        meta.prepareExecutionResultsFileFields(row, nextStep);
        // make sure we get the name of the parent step meta... used for the origin step
        Mockito.verify(parent).getName();
        ArgumentCaptor<ValueMetaInterface> argumentCaptor = ArgumentCaptor.forClass(ValueMetaInterface.class);
        Mockito.verify(row).addValueMeta(argumentCaptor.capture());
        Assert.assertEquals("parent step", argumentCaptor.getValue().getOrigin());
    }

    @Test
    public void testPrepareResultsRowsFields() throws Exception {
        TransExecutorMeta meta = new TransExecutorMeta();
        String[] outputFieldNames = new String[]{ "one", "two" };
        int[] outputFieldTypes = new int[]{ 0, 1 };
        int[] outputFieldLength = new int[]{ 4, 8 };
        int[] outputFieldPrecision = new int[]{ 2, 4 };
        meta.setOutputRowsField(outputFieldNames);
        meta.setOutputRowsType(outputFieldTypes);
        meta.setOutputRowsLength(outputFieldLength);
        meta.setOutputRowsPrecision(outputFieldPrecision);
        meta = Mockito.spy(meta);
        RowMetaInterface row = Mockito.mock(RowMetaInterface.class);
        StepMeta parent = Mockito.mock(StepMeta.class);
        Mockito.doReturn(parent).when(meta).getParentStepMeta();
        Mockito.when(parent.getName()).thenReturn("parent step");
        meta.prepareResultsRowsFields(row);
        // make sure we get the name of the parent step meta... used for the origin step
        Mockito.verify(parent, Mockito.times(outputFieldNames.length)).getName();
        ArgumentCaptor<ValueMetaInterface> argumentCaptor = ArgumentCaptor.forClass(ValueMetaInterface.class);
        Mockito.verify(row, Mockito.times(outputFieldNames.length)).addValueMeta(argumentCaptor.capture());
        Assert.assertEquals("parent step", argumentCaptor.getValue().getOrigin());
    }

    @Test
    public void testGetFields() throws Exception {
        TransExecutorMeta meta = new TransExecutorMeta();
        meta = Mockito.spy(meta);
        StepMeta nextStep = Mockito.mock(StepMeta.class);
        // Test null
        meta.getFields(null, null, null, nextStep, null, null, null);
        Mockito.verify(meta, Mockito.never()).addFieldToRow(ArgumentMatchers.any(RowMetaInterface.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt());
        RowMetaInterface rowMeta = Mockito.mock(RowMetaInterface.class);
        meta.getFields(rowMeta, null, null, nextStep, null, null, null);
        Mockito.verify(rowMeta, Mockito.never()).clear();
        StepMeta executionResultTargetStepMeta = Mockito.mock(StepMeta.class);
        meta.setExecutionResultTargetStepMeta(executionResultTargetStepMeta);
        meta.getFields(rowMeta, null, null, nextStep, null, null, null);
        Mockito.verify(rowMeta, Mockito.atMost(1)).clear();
        meta.setExecutionResultTargetStepMeta(null);
        StepMeta resultFilesTargetStepMeta = Mockito.mock(StepMeta.class);
        meta.setResultFilesTargetStepMeta(resultFilesTargetStepMeta);
        meta.getFields(rowMeta, null, null, nextStep, null, null, null);
        Mockito.verify(rowMeta, Mockito.atMost(1)).clear();
        meta.setResultFilesTargetStepMeta(null);
        StepMeta outputRowsSourceStepMeta = Mockito.mock(StepMeta.class);
        meta.setOutputRowsSourceStepMeta(outputRowsSourceStepMeta);
        meta.getFields(rowMeta, null, null, nextStep, null, null, null);
        Mockito.verify(rowMeta, Mockito.atMost(1)).clear();
        meta.setOutputRowsSourceStepMeta(null);
    }

    @Test
    public void testClone() throws Exception {
        TransExecutorMeta meta = new TransExecutorMeta();
        meta.setOutputRowsField(new String[]{ "field1", "field2" });
        meta.setOutputRowsLength(new int[]{ 5, 5 });
        meta.setOutputRowsPrecision(new int[]{ 5, 5 });
        meta.setOutputRowsType(new int[]{ 0, 0 });
        TransExecutorMeta cloned = ((TransExecutorMeta) (meta.clone()));
        Assert.assertFalse(((cloned.getOutputRowsField()) == (meta.getOutputRowsField())));
        Assert.assertTrue(Arrays.equals(cloned.getOutputRowsField(), meta.getOutputRowsField()));
        Assert.assertFalse(((cloned.getOutputRowsLength()) == (meta.getOutputRowsLength())));
        Assert.assertTrue(Arrays.equals(cloned.getOutputRowsLength(), meta.getOutputRowsLength()));
        Assert.assertFalse(((cloned.getOutputRowsPrecision()) == (meta.getOutputRowsPrecision())));
        Assert.assertTrue(Arrays.equals(cloned.getOutputRowsPrecision(), meta.getOutputRowsPrecision()));
        Assert.assertFalse(((cloned.getOutputRowsType()) == (meta.getOutputRowsType())));
        Assert.assertTrue(Arrays.equals(cloned.getOutputRowsType(), meta.getOutputRowsType()));
    }

    @Test
    public void testRemoveHopFrom() throws Exception {
        TransExecutorMeta transExecutorMeta = new TransExecutorMeta();
        transExecutorMeta.setExecutionResultTargetStepMeta(new StepMeta());
        transExecutorMeta.setOutputRowsSourceStepMeta(new StepMeta());
        transExecutorMeta.setResultFilesTargetStepMeta(new StepMeta());
        transExecutorMeta.setExecutorsOutputStepMeta(new StepMeta());
        transExecutorMeta.cleanAfterHopFromRemove();
        Assert.assertNull(transExecutorMeta.getExecutionResultTargetStepMeta());
        Assert.assertNull(transExecutorMeta.getOutputRowsSourceStepMeta());
        Assert.assertNull(transExecutorMeta.getResultFilesTargetStepMeta());
        Assert.assertNull(transExecutorMeta.getExecutorsOutputStepMeta());
    }
}

