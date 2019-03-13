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
package org.pentaho.di.trans.steps.jsonoutput;


import junit.framework.TestCase;
import org.junit.Assert;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.logging.LoggingObjectInterface;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


/**
 * This class was a "copy and modification" of Kettle's JsonOutputTests.
 *
 * @author Hendy Irawan <hendy@soluvas.com> Modified by Sean Flatley, removing dependency on external text file to hold
expected results and modifying code to handle "Compatibility Mode".
 */
public class JsonOutputTest extends TestCase {
    private static final String EXPECTED_NON_COMPATIBILITY_JSON = "{\"data\":[{\"id\":1,\"state\":\"Florida\",\"city\":\"Orlando\"}," + (((((((("{\"id\":1,\"state\":\"Florida\",\"city\":\"Orlando\"}," + "{\"id\":1,\"state\":\"Florida\",\"city\":\"Orlando\"},") + "{\"id\":1,\"state\":\"Florida\",\"city\":\"Orlando\"},") + "{\"id\":1,\"state\":\"Florida\",\"city\":\"Orlando\"},") + "{\"id\":1,\"state\":\"Florida\",\"city\":\"Orlando\"},") + "{\"id\":1,\"state\":\"Florida\",\"city\":\"Orlando\"},") + "{\"id\":1,\"state\":\"Florida\",\"city\":\"Orlando\"},") + "{\"id\":1,\"state\":\"Florida\",\"city\":\"Orlando\"},") + "{\"id\":1,\"state\":\"Florida\",\"city\":\"Orlando\"}]}");

    private static final String EXPECTED_COMPATIBILITY_MODE_JSON = "{\"data\":[{\"id\":1},{\"state\":\"Florida\"},{\"city\":\"Orlando\"},{\"id\":1},{\"state\":\"Florida\"}," + ((((("{\"city\":\"Orlando\"},{\"id\":1},{\"state\":\"Florida\"},{\"city\":\"Orlando\"},{\"id\":1}," + "{\"state\":\"Florida\"},{\"city\":\"Orlando\"},{\"id\":1},{\"state\":\"Florida\"},") + "{\"city\":\"Orlando\"},{\"id\":1},{\"state\":\"Florida\"},{\"city\":\"Orlando\"},{\"id\":1},") + "{\"state\":\"Florida\"},{\"city\":\"Orlando\"},{\"id\":1},{\"state\":\"Florida\"},") + "{\"city\":\"Orlando\"},{\"id\":1},{\"state\":\"Florida\"},{\"city\":\"Orlando\"},{\"id\":1},") + "{\"state\":\"Florida\"},{\"city\":\"Orlando\"}]}");

    // The actual tests
    public void testNonCompatibilityMode() throws Exception {
        String jsonStructure = test(false);
        Assert.assertTrue(jsonEquals(JsonOutputTest.EXPECTED_NON_COMPATIBILITY_JSON, jsonStructure));
    }

    public void testCompatibilityMode() throws Exception {
        String jsonStructure = test(true);
        Assert.assertEquals(JsonOutputTest.EXPECTED_COMPATIBILITY_MODE_JSON, jsonStructure);
    }

    /* PDI-7243 */
    public void testNpeIsNotThrownOnNullInput() throws Exception {
        StepMockHelper<JsonOutputMeta, JsonOutputData> mockHelper = new StepMockHelper<JsonOutputMeta, JsonOutputData>("jsonOutput", JsonOutputMeta.class, JsonOutputData.class);
        Mockito.when(mockHelper.logChannelInterfaceFactory.create(ArgumentMatchers.any(), ArgumentMatchers.any(LoggingObjectInterface.class))).thenReturn(mockHelper.logChannelInterface);
        Mockito.when(mockHelper.trans.isRunning()).thenReturn(true);
        Mockito.when(mockHelper.stepMeta.getStepMetaInterface()).thenReturn(new JsonOutputMeta());
        JsonOutput step = new JsonOutput(mockHelper.stepMeta, mockHelper.stepDataInterface, 0, mockHelper.transMeta, mockHelper.trans);
        step = Mockito.spy(step);
        Mockito.doReturn(null).when(step).getRow();
        step.processRow(mockHelper.processRowsStepMetaInterface, mockHelper.processRowsStepDataInterface);
    }

    public void testEmptyDoesntWriteToFile() throws Exception {
        StepMockHelper<JsonOutputMeta, JsonOutputData> mockHelper = new StepMockHelper<JsonOutputMeta, JsonOutputData>("jsonOutput", JsonOutputMeta.class, JsonOutputData.class);
        Mockito.when(mockHelper.logChannelInterfaceFactory.create(ArgumentMatchers.any(), ArgumentMatchers.any(LoggingObjectInterface.class))).thenReturn(mockHelper.logChannelInterface);
        Mockito.when(mockHelper.trans.isRunning()).thenReturn(true);
        Mockito.when(mockHelper.stepMeta.getStepMetaInterface()).thenReturn(new JsonOutputMeta());
        JsonOutputData stepData = new JsonOutputData();
        stepData.writeToFile = true;
        JsonOutput step = new JsonOutput(mockHelper.stepMeta, stepData, 0, mockHelper.transMeta, mockHelper.trans);
        step = Mockito.spy(step);
        Mockito.doReturn(null).when(step).getRow();
        Mockito.doReturn(true).when(step).openNewFile();
        Mockito.doReturn(true).when(step).closeFile();
        step.processRow(mockHelper.processRowsStepMetaInterface, stepData);
        Mockito.verify(step, Mockito.times(0)).openNewFile();
        Mockito.verify(step, Mockito.times(0)).closeFile();
    }
}

