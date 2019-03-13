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
package org.pentaho.di.trans.steps.fileinput.text;


import java.util.Date;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


/**
 * Test case for PDI-2875
 *
 * @author Pavel Sakun
 */
public class PDI_2875_Test {
    private static StepMockHelper<TextFileInputMeta, TextFileInputData> smh;

    private final String VAR_NAME = "VAR";

    private final String EXPRESSION = ("${" + (VAR_NAME)) + "}";

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testVariableSubstitution() {
        Mockito.doReturn(new Date()).when(PDI_2875_Test.smh.trans).getCurrentDate();
        TextFileInput step = Mockito.spy(new TextFileInput(PDI_2875_Test.smh.stepMeta, PDI_2875_Test.smh.stepDataInterface, 0, PDI_2875_Test.smh.transMeta, PDI_2875_Test.smh.trans));
        TextFileInputData data = new TextFileInputData();
        step.setVariable(VAR_NAME, "value");
        step.init(getMeta(), data);
        Mockito.verify(step, Mockito.times(2)).environmentSubstitute(EXPRESSION);
    }
}

