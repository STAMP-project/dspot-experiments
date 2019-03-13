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
 *
 *
 * @author Andrey Khayrutdinov
 */
public class CsvInputEnclosureTest extends CsvInputUnitTestBase {
    private static final String QUOTATION_AND_EXCLAMATION_MARK = "\"!";

    private static final String QUOTATION_MARK = "\"";

    private static final String SEMICOLON = ";";

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private CsvInput csvInput;

    private StepMockHelper<CsvInputMeta, StepDataInterface> stepMockHelper;

    @Test
    public void hasEnclosures_HasNewLine() throws Exception {
        doTest("\"value1\";\"value2\"\n", CsvInputEnclosureTest.QUOTATION_MARK);
    }

    @Test
    public void hasEnclosures_HasNotNewLine() throws Exception {
        doTest("\"value1\";\"value2\"", CsvInputEnclosureTest.QUOTATION_MARK);
    }

    @Test
    public void hasNotEnclosures_HasNewLine() throws Exception {
        doTest("value1;value2\n", CsvInputEnclosureTest.QUOTATION_MARK);
    }

    @Test
    public void hasNotEnclosures_HasNotNewLine() throws Exception {
        doTest("value1;value2", CsvInputEnclosureTest.QUOTATION_MARK);
    }

    @Test
    public void hasMultiSymbolsEnclosureWithoutEnclosureAndEndFile() throws Exception {
        doTest("value1;value2", CsvInputEnclosureTest.QUOTATION_AND_EXCLAMATION_MARK);
    }

    @Test
    public void hasMultiSymbolsEnclosureWithEnclosureAndWithoutEndFile() throws Exception {
        doTest("\"!value1\"!;value2", CsvInputEnclosureTest.QUOTATION_AND_EXCLAMATION_MARK);
    }

    @Test
    public void hasMultiSymbolsEnclosurewithEnclosureInBothfield() throws Exception {
        doTest("\"!value1\"!;\"!value2\"!", CsvInputEnclosureTest.QUOTATION_AND_EXCLAMATION_MARK);
    }

    @Test
    public void hasMultiSymbolsEnclosureWithoutEnclosureAndWithEndfileRN() throws Exception {
        doTest("value1;value2\r\n", CsvInputEnclosureTest.QUOTATION_AND_EXCLAMATION_MARK);
    }

    @Test
    public void hasMultiSymbolsEnclosureWithEnclosureAndWithEndfileRN() throws Exception {
        doTest("value1;\"!value2\"!\r\n", CsvInputEnclosureTest.QUOTATION_AND_EXCLAMATION_MARK);
    }

    @Test
    public void hasMultiSymbolsEnclosureWithoutEnclosureAndWithEndfileN() throws Exception {
        doTest("value1;value2\n", CsvInputEnclosureTest.QUOTATION_AND_EXCLAMATION_MARK);
    }

    @Test
    public void hasMultiSymbolsEnclosureWithEnclosureAndWithEndfileN() throws Exception {
        doTest("value1;\"!value2\"!\n", CsvInputEnclosureTest.QUOTATION_AND_EXCLAMATION_MARK);
    }
}

