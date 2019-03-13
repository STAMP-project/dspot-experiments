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
package org.pentaho.di.trans.steps.csvinput;


import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


/**
 * Tests for double line endings in CsvInput step
 *
 * @author Pavel Sakun
 * @see CsvInput
 */
public class CsvInputDoubleLineEndTest extends CsvInputUnitTestBase {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private static final String ASCII = "windows-1252";

    private static final String UTF8 = "UTF-8";

    private static final String UTF16LE = "UTF-16LE";

    private static final String UTF16BE = "UTF-16BE";

    private static final String TEST_DATA = "Header1\tHeader2\r\nValue\tValue\r\nValue\tValue\r\n";

    private static StepMockHelper<CsvInputMeta, StepDataInterface> stepMockHelper;

    @Test
    public void testASCII() throws Exception {
        doTest(CsvInputDoubleLineEndTest.ASCII, CsvInputDoubleLineEndTest.ASCII, CsvInputDoubleLineEndTest.TEST_DATA);
    }

    @Test
    public void testUTF16LE() throws Exception {
        doTest(CsvInputDoubleLineEndTest.UTF16LE, CsvInputDoubleLineEndTest.UTF16LE, CsvInputDoubleLineEndTest.TEST_DATA);
    }

    @Test
    public void testUTF16BE() throws Exception {
        doTest(CsvInputDoubleLineEndTest.UTF16BE, CsvInputDoubleLineEndTest.UTF16BE, CsvInputDoubleLineEndTest.TEST_DATA);
    }

    @Test
    public void testUTF8() throws Exception {
        doTest(CsvInputDoubleLineEndTest.UTF8, CsvInputDoubleLineEndTest.UTF8, CsvInputDoubleLineEndTest.TEST_DATA);
    }
}

