/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2016-2019 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.trans.steps.csvinput;


import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


public class CsvInputMultiCharDelimiterTest extends CsvInputUnitTestBase {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private CsvInput csvInput;

    private StepMockHelper<CsvInputMeta, StepDataInterface> stepMockHelper;

    @Test
    public void multiChar_hasEnclosures_HasNewLine() throws Exception {
        doTest("\"value1\"delimiter\"value2\"delimiter\"value3\"\n");
    }

    @Test
    public void multiChar_hasEnclosures_HasNewLineDoubleEnd() throws Exception {
        doTest("\"value1\"delimiter\"value2\"delimiter\"value3\"\r\n");
    }

    @Test
    public void multiChar_hasEnclosures_HasNotNewLine() throws Exception {
        doTest("\"value1\"delimiter\"value2\"delimiter\"value3\"");
    }

    @Test
    public void multiChar_hasNotEnclosures_HasNewLine() throws Exception {
        doTest("value1delimitervalue2delimitervalue3\n");
    }

    @Test
    public void multiChar_hasNotEnclosures_HasNewLineDoubleEnd() throws Exception {
        doTest("value1delimitervalue2delimitervalue3\r\n");
    }

    @Test
    public void multiChar_hasNotEnclosures_HasNotNewLine() throws Exception {
        doTest("value1delimitervalue2delimitervalue3");
    }
}

