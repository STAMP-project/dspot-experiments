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
package org.pentaho.di.trans.steps.mappinginput;


import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.trans.step.StepErrorMeta;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.steps.mapping.MappingValueRename;
import org.pentaho.di.trans.steps.mock.StepMockHelper;
import org.pentaho.di.trans.steps.validator.Validator;
import org.pentaho.di.trans.steps.validator.ValidatorData;


/**
 * User: Dzmitry Stsiapanau Date: 12/24/13 Time: 12:45 PM
 */
public class MappingInputTest {
    private String stepName = "MAPPING INPUT";

    private StepMockHelper<MappingInputMeta, MappingInputData> stepMockHelper;

    private volatile boolean processRowEnded;

    @Test
    public void testSetConnectorSteps() throws Exception {
        Mockito.when(stepMockHelper.transMeta.getSizeRowset()).thenReturn(1);
        MappingInputData mappingInputData = new MappingInputData();
        MappingInput mappingInput = new MappingInput(stepMockHelper.stepMeta, mappingInputData, 0, stepMockHelper.transMeta, stepMockHelper.trans);
        mappingInput.init(stepMockHelper.initStepMetaInterface, mappingInputData);
        ValidatorData validatorData = new ValidatorData();
        Validator previousStep = new Validator(stepMockHelper.stepMeta, validatorData, 0, stepMockHelper.transMeta, stepMockHelper.trans);
        Mockito.when(stepMockHelper.stepMeta.isDoingErrorHandling()).thenReturn(true);
        StepErrorMeta stepErrorMeta = Mockito.mock(StepErrorMeta.class);
        Mockito.when(stepErrorMeta.getTargetStep()).thenReturn(stepMockHelper.stepMeta);
        Mockito.when(stepMockHelper.stepMeta.getName()).thenReturn(stepName);
        Mockito.when(stepMockHelper.stepMeta.getStepErrorMeta()).thenReturn(stepErrorMeta);
        StepInterface[] si = new StepInterface[]{ previousStep };
        mappingInput.setConnectorSteps(si, Collections.<MappingValueRename>emptyList(), stepName);
        Assert.assertEquals(previousStep.getOutputRowSets().size(), 0);
    }

    @Test
    public void testSetConnectorStepsWithNullArguments() throws Exception {
        try {
            final MappingInputData mappingInputData = new MappingInputData();
            final MappingInput mappingInput = new MappingInput(stepMockHelper.stepMeta, mappingInputData, 0, stepMockHelper.transMeta, stepMockHelper.trans);
            mappingInput.init(stepMockHelper.initStepMetaInterface, mappingInputData);
            int timeOut = 1000;
            final int junitMaxTimeOut = 40000;
            mappingInput.setTimeOut(timeOut);
            final MappingInputTest mit = this;
            final Thread processRow = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mappingInput.processRow(stepMockHelper.initStepMetaInterface, mappingInputData);
                        mit.setProcessRowEnded(true);
                    } catch (KettleException e) {
                        mit.setProcessRowEnded(true);
                    }
                }
            });
            processRow.start();
            boolean exception = false;
            try {
                mappingInput.setConnectorSteps(null, Collections.<MappingValueRename>emptyList(), "");
            } catch (IllegalArgumentException ex1) {
                try {
                    mappingInput.setConnectorSteps(new StepInterface[0], null, "");
                } catch (IllegalArgumentException ex3) {
                    try {
                        mappingInput.setConnectorSteps(new StepInterface[]{ Mockito.mock(StepInterface.class) }, Collections.<MappingValueRename>emptyList(), null);
                    } catch (IllegalArgumentException ignored) {
                        exception = true;
                    }
                }
            }
            processRow.join(junitMaxTimeOut);
            Assert.assertTrue("not enough IllegalArgumentExceptions", exception);
            Assert.assertTrue("Process wasn`t stopped at null", isProcessRowEnded());
        } catch (NullPointerException npe) {
            Assert.fail("Null values are not suitable");
        }
    }
}

