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
package org.pentaho.di.trans.steps.simplemapping;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.RowProducer;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.steps.mappinginput.MappingInput;
import org.pentaho.di.trans.steps.mappingoutput.MappingOutput;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


/**
 *
 *
 * @author Tatsiana_Kasiankova
 */
public class SimpleMappingTest {
    private static final String MAPPING_INPUT_STEP_NAME = "MAPPING_INPUT_STEP_NAME";

    private static final String MAPPING_OUTPUT_STEP_NAME = "MAPPING_OUTPUT_STEP_NAME";

    private StepMockHelper<SimpleMappingMeta, SimpleMappingData> stepMockHelper;

    // Using real SimpleMappingData object
    private SimpleMappingData simpleMpData = new SimpleMappingData();

    private SimpleMapping smp;

    @Test
    public void testStepSetUpAsWasStarted_AtProcessingFirstRow() throws KettleException {
        smp = new SimpleMapping(stepMockHelper.stepMeta, stepMockHelper.stepDataInterface, 0, stepMockHelper.transMeta, stepMockHelper.trans);
        smp.init(stepMockHelper.initStepMetaInterface, stepMockHelper.initStepDataInterface);
        smp.addRowSetToInputRowSets(stepMockHelper.getMockInputRowSet(new Object[]{  }));
        Assert.assertTrue("The step is processing in first", smp.first);
        Assert.assertTrue(smp.processRow(stepMockHelper.processRowsStepMetaInterface, simpleMpData));
        Assert.assertFalse("The step is processing not in first", smp.first);
        Assert.assertTrue("The step was started", smp.getData().wasStarted);
    }

    @Test
    public void testStepShouldProcessError_WhenMappingTransHasError() throws KettleException {
        // Set Up TransMock to return the error
        int errorCount = 1;
        Mockito.when(stepMockHelper.trans.getErrors()).thenReturn(errorCount);
        // The step has been already finished
        Mockito.when(stepMockHelper.trans.isFinished()).thenReturn(Boolean.TRUE);
        // The step was started
        simpleMpData.wasStarted = true;
        smp = new SimpleMapping(stepMockHelper.stepMeta, stepMockHelper.stepDataInterface, 0, stepMockHelper.transMeta, stepMockHelper.trans);
        smp.init(stepMockHelper.initStepMetaInterface, simpleMpData);
        smp.dispose(stepMockHelper.processRowsStepMetaInterface, simpleMpData);
        Mockito.verify(stepMockHelper.trans, Mockito.times(1)).isFinished();
        Mockito.verify(stepMockHelper.trans, Mockito.never()).waitUntilFinished();
        Mockito.verify(stepMockHelper.trans, Mockito.never()).addActiveSubTransformation(ArgumentMatchers.anyString(), ArgumentMatchers.any(Trans.class));
        Mockito.verify(stepMockHelper.trans, Mockito.times(1)).removeActiveSubTransformation(ArgumentMatchers.anyString());
        Mockito.verify(stepMockHelper.trans, Mockito.never()).getActiveSubTransformation(ArgumentMatchers.anyString());
        Mockito.verify(stepMockHelper.trans, Mockito.times(1)).getErrors();
        Assert.assertTrue("The step contains the errors", ((smp.getErrors()) == errorCount));
    }

    @Test
    public void testStepShouldStopProcessingInput_IfUnderlyingTransitionIsStopped() throws Exception {
        MappingInput mappingInput = Mockito.mock(MappingInput.class);
        Mockito.when(mappingInput.getStepname()).thenReturn(SimpleMappingTest.MAPPING_INPUT_STEP_NAME);
        stepMockHelper.processRowsStepDataInterface.mappingInput = mappingInput;
        RowProducer rowProducer = Mockito.mock(RowProducer.class);
        Mockito.when(rowProducer.putRow(ArgumentMatchers.any(RowMetaInterface.class), ArgumentMatchers.any(Object[].class), ArgumentMatchers.anyBoolean())).thenReturn(true);
        StepInterface stepInterface = Mockito.mock(StepInterface.class);
        Trans mappingTrans = Mockito.mock(Trans.class);
        Mockito.when(mappingTrans.addRowProducer(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())).thenReturn(rowProducer);
        Mockito.when(mappingTrans.findStepInterface(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())).thenReturn(stepInterface);
        Mockito.when(mappingTrans.isFinishedOrStopped()).thenReturn(Boolean.FALSE).thenReturn(Boolean.TRUE);
        stepMockHelper.processRowsStepDataInterface.mappingTrans = mappingTrans;
        MappingOutput mappingOutput = Mockito.mock(MappingOutput.class);
        Mockito.when(mappingOutput.getStepname()).thenReturn(SimpleMappingTest.MAPPING_OUTPUT_STEP_NAME);
        stepMockHelper.processRowsStepDataInterface.mappingOutput = mappingOutput;
        smp = new SimpleMapping(stepMockHelper.stepMeta, stepMockHelper.stepDataInterface, 0, stepMockHelper.transMeta, stepMockHelper.trans);
        smp.init(stepMockHelper.initStepMetaInterface, simpleMpData);
        smp.addRowSetToInputRowSets(stepMockHelper.getMockInputRowSet(new Object[]{  }));
        smp.addRowSetToInputRowSets(stepMockHelper.getMockInputRowSet(new Object[]{  }));
        Assert.assertTrue(smp.processRow(stepMockHelper.processRowsStepMetaInterface, stepMockHelper.processRowsStepDataInterface));
        Assert.assertFalse(smp.processRow(stepMockHelper.processRowsStepMetaInterface, stepMockHelper.processRowsStepDataInterface));
    }

    @Test
    public void testDispose() throws KettleException {
        // Set Up TransMock to return the error
        Mockito.when(stepMockHelper.trans.getErrors()).thenReturn(0);
        // The step has been already finished
        Mockito.when(stepMockHelper.trans.isFinished()).thenReturn(Boolean.FALSE);
        // The step was started
        simpleMpData.wasStarted = true;
        smp = new SimpleMapping(stepMockHelper.stepMeta, stepMockHelper.stepDataInterface, 0, stepMockHelper.transMeta, stepMockHelper.trans);
        smp.init(stepMockHelper.initStepMetaInterface, simpleMpData);
        smp.dispose(stepMockHelper.processRowsStepMetaInterface, simpleMpData);
        Mockito.verify(stepMockHelper.trans, Mockito.times(1)).isFinished();
        Mockito.verify(stepMockHelper.trans, Mockito.times(1)).waitUntilFinished();
        Mockito.verify(stepMockHelper.trans, Mockito.times(1)).removeActiveSubTransformation(ArgumentMatchers.anyString());
    }
}

