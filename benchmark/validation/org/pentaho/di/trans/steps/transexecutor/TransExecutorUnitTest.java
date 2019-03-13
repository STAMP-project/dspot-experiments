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
package org.pentaho.di.trans.steps.transexecutor;


import LogLevel.DEBUG;
import LogLevel.NOTHING;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.QueueRowSet;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.ResultFile;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannel;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;


/**
 *
 *
 * @author Andrey Khayrutdinov
 */
public class TransExecutorUnitTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private TransExecutor executor;

    private TransExecutorMeta meta;

    private TransExecutorData data;

    private Trans internalTrans;

    private Result internalResult;

    @Test
    public void testCreateInternalTransSetsRepository() throws KettleException {
        Trans transParentMock = Mockito.mock(Trans.class);
        Repository repositoryMock = Mockito.mock(Repository.class);
        TransExecutorData transExecutorDataMock = Mockito.mock(TransExecutorData.class);
        TransMeta transMetaMock = Mockito.mock(TransMeta.class);
        executor.init(meta, data);
        Mockito.when(transParentMock.getRepository()).thenReturn(repositoryMock);
        Mockito.when(transParentMock.getLogLevel()).thenReturn(DEBUG);
        Mockito.doNothing().when(transParentMock).initializeVariablesFrom(ArgumentMatchers.any(VariableSpace.class));
        Mockito.when(executor.getLogLevel()).thenReturn(DEBUG);
        Mockito.when(executor.createInternalTrans()).thenCallRealMethod();
        Mockito.when(executor.getTrans()).thenReturn(transParentMock);
        Mockito.when(executor.getData()).thenReturn(transExecutorDataMock);
        Mockito.when(transMetaMock.listVariables()).thenReturn(new String[0]);
        Mockito.when(transMetaMock.listParameters()).thenReturn(new String[0]);
        Mockito.when(transExecutorDataMock.getExecutorTransMeta()).thenReturn(transMetaMock);
        Trans internalTrans = executor.createInternalTrans();
        Assert.assertNotNull(internalTrans);
        Trans parentTrans = internalTrans.getParentTrans();
        Assert.assertEquals(parentTrans, transParentMock);
        Assert.assertEquals(parentTrans.getRepository(), repositoryMock);
        Assert.assertEquals(internalTrans.getRepository(), repositoryMock);
    }

    @Test
    public void collectsResultsFromInternalTransformation() throws Exception {
        prepareOneRowForExecutor();
        RowMetaAndData expectedResult = new RowMetaAndData(new RowMeta(), "fake result");
        internalResult.getRows().add(expectedResult);
        RowSet rowSet = new QueueRowSet();
        // any value except null
        StepMeta stepMeta = mockStepAndMapItToRowSet("stepMetaMock", rowSet);
        meta.setOutputRowsSourceStepMeta(stepMeta);
        executor.init(meta, data);
        executor.setInputRowMeta(new RowMeta());
        Assert.assertTrue("Passing one line at first time", executor.processRow(meta, data));
        Assert.assertFalse("Executing the internal trans during the second round", executor.processRow(meta, data));
        Object[] resultsRow = rowSet.getRowImmediate();
        Assert.assertNotNull(resultsRow);
        Assert.assertArrayEquals(expectedResult.getData(), resultsRow);
        Assert.assertNull("Only one row is expected", rowSet.getRowImmediate());
    }

    @Test
    public void collectsExecutionResults() throws Exception {
        prepareOneRowForExecutor();
        StepMeta parentStepMeta = Mockito.mock(StepMeta.class);
        Mockito.when(parentStepMeta.getName()).thenReturn("parentStepMeta");
        meta.setParentStepMeta(parentStepMeta);
        internalResult.setResult(true);
        meta.setExecutionResultField("executionResultField");
        internalResult.setNrErrors(1);
        meta.setExecutionNrErrorsField("executionNrErrorsField");
        internalResult.setNrLinesRead(2);
        meta.setExecutionLinesReadField("executionLinesReadField");
        internalResult.setNrLinesWritten(3);
        meta.setExecutionLinesWrittenField("executionLinesWrittenField");
        internalResult.setNrLinesInput(4);
        meta.setExecutionLinesInputField("executionLinesInputField");
        internalResult.setNrLinesOutput(5);
        meta.setExecutionLinesOutputField("executionLinesOutputField");
        internalResult.setNrLinesRejected(6);
        meta.setExecutionLinesRejectedField("executionLinesRejectedField");
        internalResult.setNrLinesUpdated(7);
        meta.setExecutionLinesUpdatedField("executionLinesUpdatedField");
        internalResult.setNrLinesDeleted(8);
        meta.setExecutionLinesDeletedField("executionLinesDeletedField");
        internalResult.setNrFilesRetrieved(9);
        meta.setExecutionFilesRetrievedField("executionFilesRetrievedField");
        internalResult.setExitStatus(10);
        meta.setExecutionExitStatusField("executionExitStatusField");
        RowSet rowSet = new QueueRowSet();
        // any value except null
        StepMeta stepMeta = mockStepAndMapItToRowSet("stepMetaMock", rowSet);
        meta.setExecutionResultTargetStepMeta(stepMeta);
        executor.init(meta, data);
        executor.setInputRowMeta(new RowMeta());
        Assert.assertTrue("Passing one line at first time", executor.processRow(meta, data));
        Assert.assertFalse("Executing the internal trans during the second round", executor.processRow(meta, data));
        Object[] resultsRow = rowSet.getRowImmediate();
        Assert.assertNotNull(resultsRow);
        Assert.assertNull("Only one row is expected", rowSet.getRowImmediate());
        Assert.assertEquals(internalResult.getResult(), resultsRow[0]);
        Assert.assertEquals(internalResult.getNrErrors(), resultsRow[1]);
        Assert.assertEquals(internalResult.getNrLinesRead(), resultsRow[2]);
        Assert.assertEquals(internalResult.getNrLinesWritten(), resultsRow[3]);
        Assert.assertEquals(internalResult.getNrLinesInput(), resultsRow[4]);
        Assert.assertEquals(internalResult.getNrLinesOutput(), resultsRow[5]);
        Assert.assertEquals(internalResult.getNrLinesRejected(), resultsRow[6]);
        Assert.assertEquals(internalResult.getNrLinesUpdated(), resultsRow[7]);
        Assert.assertEquals(internalResult.getNrLinesDeleted(), resultsRow[8]);
        Assert.assertEquals(internalResult.getNrFilesRetrieved(), resultsRow[9]);
        Assert.assertEquals(internalResult.getExitStatus(), ((Number) (resultsRow[10])).intValue());
    }

    /**
     * Given an input data and a transformation executor with specified field to group rows on.
     * <br/>
     * When transformation executor is processing rows of an input data,
     * then rows should be accumulated in a group as long as the specified field value stays the same.
     */
    @Test
    public void shouldAccumulateRowsWhenGroupFieldIsSpecified() throws KettleException {
        prepareMultipleRowsForExecutor();
        meta.setGroupField("groupField");
        executor.init(meta, data);
        RowMetaInterface rowMeta = new RowMeta();
        rowMeta.addValueMeta(new ValueMetaString("groupField"));
        executor.setInputRowMeta(rowMeta);
        // start processing
        executor.processRow(meta, data);// 1st row - 'value1'

        // should be added to group buffer
        Assert.assertEquals(1, data.groupBuffer.size());
        executor.processRow(meta, data);
        executor.processRow(meta, data);
        executor.processRow(meta, data);// 4th row - still 'value1'

        // first 4 rows should be added to the same group
        Assert.assertEquals(4, data.groupBuffer.size());
        executor.processRow(meta, data);// 5th row - value has been changed - 'value12'

        // previous group buffer should be flushed
        // and a new group should be started
        Assert.assertEquals(1, data.groupBuffer.size());
        executor.processRow(meta, data);// 6th row - 'value12'

        executor.processRow(meta, data);// 7th row - 'value12'

        // the rest rows should be added to another group
        Assert.assertEquals(3, data.groupBuffer.size());
        executor.processRow(meta, data);// end of file

        // group buffer should be flushed in the end
        Assert.assertEquals(0, data.groupBuffer.size());
    }

    /**
     * Given an input data and a transformation executor
     * with specified number of rows to send to the transformation (X).
     * <br/>
     * When transformation executor is processing rows of an input data,
     * then every X rows should be accumulated in a group.
     */
    @Test
    public void shouldAccumulateRowsByCount() throws KettleException {
        prepareMultipleRowsForExecutor();
        meta.setGroupSize("5");
        executor.init(meta, data);
        // start processing
        executor.processRow(meta, data);// 1st row

        // should be added to group buffer
        Assert.assertEquals(1, data.groupBuffer.size());
        executor.processRow(meta, data);
        executor.processRow(meta, data);
        executor.processRow(meta, data);// 4th row

        // first 4 rows should be added to the same group
        Assert.assertEquals(4, data.groupBuffer.size());
        executor.processRow(meta, data);// 5th row

        // once the 5th row is processed, the transformation executor should be triggered
        // and thus, group buffer should be flushed
        Assert.assertEquals(0, data.groupBuffer.size());
        executor.processRow(meta, data);// 6th row

        // previous group buffer should be flushed
        // and a new group should be started
        Assert.assertEquals(1, data.groupBuffer.size());
        executor.processRow(meta, data);// 7th row

        // the rest rows should be added to another group
        Assert.assertEquals(2, data.groupBuffer.size());
        executor.processRow(meta, data);// end of file

        // group buffer should be flushed in the end
        Assert.assertEquals(0, data.groupBuffer.size());
    }

    @Test
    public void testCollectTransResultsDisabledHop() throws KettleException {
        StepMeta outputRowsSourceStepMeta = Mockito.mock(StepMeta.class);
        meta.setOutputRowsSourceStepMeta(outputRowsSourceStepMeta);
        Result result = Mockito.mock(Result.class);
        RowMetaAndData rowMetaAndData = Mockito.mock(RowMetaAndData.class);
        Mockito.when(result.getRows()).thenReturn(Arrays.asList(rowMetaAndData));
        Mockito.doNothing().when(executor).putRowTo(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        executor.init(meta, data);
        executor.collectTransResults(result);
        Mockito.verify(executor, Mockito.never()).putRowTo(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void testCollectExecutionResultsDisabledHop() throws KettleException {
        StepMeta executionResultTargetStepMeta = Mockito.mock(StepMeta.class);
        meta.setExecutionResultTargetStepMeta(executionResultTargetStepMeta);
        RowMetaInterface executionResultsOutputRowMeta = Mockito.mock(RowMetaInterface.class);
        data.setExecutionResultsOutputRowMeta(executionResultsOutputRowMeta);
        Mockito.doNothing().when(executor).putRowTo(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        executor.init(meta, data);
        Result result = Mockito.mock(Result.class);
        executor.collectExecutionResults(result);
        Mockito.verify(executor, Mockito.never()).putRowTo(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void testCollectExecutionResultFilesDisabledHop() throws KettleException {
        Result result = Mockito.mock(Result.class);
        ResultFile resultFile = Mockito.mock(ResultFile.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(result.getResultFilesList()).thenReturn(Arrays.asList(resultFile));
        StepMeta resultFilesTargetStepMeta = Mockito.mock(StepMeta.class);
        meta.setResultFilesTargetStepMeta(resultFilesTargetStepMeta);
        RowMetaInterface resultFilesOutputRowMeta = Mockito.mock(RowMetaInterface.class);
        data.setResultFilesOutputRowMeta(resultFilesOutputRowMeta);
        Mockito.doNothing().when(executor).putRowTo(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        executor.init(meta, data);
        executor.collectExecutionResultFiles(result);
        Mockito.verify(executor, Mockito.never()).putRowTo(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    // PDI-16066
    @Test
    public void testExecuteTrans() throws KettleException {
        String childParam = "childParam";
        String childValue = "childValue";
        String paramOverwrite = "paramOverwrite";
        String parentValue = "parentValue";
        meta.getParameters().setVariable(new String[]{ childParam, paramOverwrite });
        meta.getParameters().setInput(new String[]{ childValue, childValue });
        Trans parent = new Trans();
        Mockito.when(executor.getTrans()).thenReturn(parent);
        executor.init(meta, data);
        executor.setVariable(paramOverwrite, parentValue);
        executor.setVariable(childParam, childValue);
        Mockito.when(executor.getLogLevel()).thenReturn(NOTHING);
        parent.setLog(new LogChannel(this));
        Mockito.doCallRealMethod().when(executor).createInternalTrans();
        Mockito.when(executor.getData().getExecutorTransMeta().listVariables()).thenReturn(new String[0]);
        /* {parentParam} */
        Mockito.when(executor.getData().getExecutorTransMeta().listParameters()).thenReturn(new String[0]);
        Trans internalTrans = executor.createInternalTrans();
        executor.getData().setExecutorTrans(internalTrans);
        executor.passParametersToTrans(Arrays.asList(meta.getParameters().getInput()));
        // When the child parameter does exist in the parent parameters, overwrite the child parameter by the parent parameter.
        Assert.assertEquals(parentValue, internalTrans.getVariable(paramOverwrite));
        // All other parent parameters need to get copied into the child parameters  (when the 'Inherit all variables from the transformation?' option is checked)
        Assert.assertEquals(childValue, internalTrans.getVariable(childParam));
    }

    @Test
    public void testSafeStop() throws Exception {
        prepareOneRowForExecutor();
        meta.setGroupSize("1");
        data.groupSize = 1;
        internalResult.setSafeStop(true);
        executor.init(meta, data);
        executor.setInputRowMeta(new RowMeta());
        Assert.assertTrue(executor.processRow(meta, data));
        Mockito.verify(executor.getTrans()).safeStop();
        Mockito.verify(executor.getTrans(), Mockito.never()).stopAll();
    }

    @Test
    public void testAbortWithError() throws Exception {
        prepareOneRowForExecutor();
        meta.setGroupSize("1");
        data.groupSize = 1;
        internalResult.setSafeStop(false);
        internalResult.setNrErrors(1);
        executor.init(meta, data);
        executor.setInputRowMeta(new RowMeta());
        Assert.assertTrue(executor.processRow(meta, data));
        Mockito.verify(executor.getTrans(), Mockito.never()).safeStop();
        Mockito.verify(executor.getTrans(), Mockito.never()).stopAll();
    }
}

