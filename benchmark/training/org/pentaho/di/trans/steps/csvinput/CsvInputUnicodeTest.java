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


import java.nio.charset.StandardCharsets;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


/**
 * Tests for unicode support in CsvInput step
 *
 * @author Pavel Sakun
 * @see CsvInput
 */
public class CsvInputUnicodeTest extends CsvInputUnitTestBase {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private static final String UTF8 = "UTF-8";

    private static final String UTF16LE = "UTF-16LE";

    private static final String UTF16LEBOM = "x-UTF-16LE-BOM";

    private static final String UTF16BE = "UTF-16BE";

    private static final String ONE_CHAR_DELIM = "\t";

    private static final String MULTI_CHAR_DELIM = "|||";

    private static final String TEXT = "Header1%1$sHeader2\nValue%1$sValue\nValue%1$sValue\n";

    private static final String TEXTHEADER = "Header1%1$sHeader2\n";

    private static final String TEXTBODY = "Value%1$sValue\nValue%1$sValue\n";

    private static final String TEXT_WITH_ENCLOSURES = "Header1%1$sHeader2\n\"Value\"%1$s\"Value\"\n\"Value\"%1$s\"Value\"\n";

    private static final String TEST_DATA = String.format(CsvInputUnicodeTest.TEXT, CsvInputUnicodeTest.ONE_CHAR_DELIM);

    private static final String TEST_DATA1 = String.format(CsvInputUnicodeTest.TEXT, CsvInputUnicodeTest.MULTI_CHAR_DELIM);

    private static final String TEST_DATA2 = String.format(CsvInputUnicodeTest.TEXT_WITH_ENCLOSURES, CsvInputUnicodeTest.ONE_CHAR_DELIM);

    private static final String TEST_DATA3 = String.format(CsvInputUnicodeTest.TEXT_WITH_ENCLOSURES, CsvInputUnicodeTest.MULTI_CHAR_DELIM);

    private static final byte[] UTF8_BOM = new byte[]{ ((byte) (239)), ((byte) (187)), ((byte) (191)) };

    private static final String TEST_DATA_UTF8_BOM = String.format(((new String(CsvInputUnicodeTest.UTF8_BOM, StandardCharsets.UTF_8)) + (CsvInputUnicodeTest.TEXT)), CsvInputUnicodeTest.ONE_CHAR_DELIM);

    private static final String TEST_DATA_NOHEADER_UTF8_BOM = String.format(((new String(CsvInputUnicodeTest.UTF8_BOM, StandardCharsets.UTF_8)) + (CsvInputUnicodeTest.TEXTBODY)), CsvInputUnicodeTest.ONE_CHAR_DELIM);

    private static final byte[] UTF16LE_BOM = new byte[]{ ((byte) (255)), ((byte) (254)) };

    private static final String TEST_DATA_UTF16LE_BOM = String.format(((new String(CsvInputUnicodeTest.UTF16LE_BOM, StandardCharsets.UTF_16LE)) + (CsvInputUnicodeTest.TEST_DATA2)), CsvInputUnicodeTest.ONE_CHAR_DELIM);

    private static final byte[] UTF16BE_BOM = new byte[]{ ((byte) (254)), ((byte) (255)) };

    private static final String TEST_DATA_UTF16BE_BOM = String.format(((new String(CsvInputUnicodeTest.UTF16BE_BOM, StandardCharsets.UTF_16BE)) + (CsvInputUnicodeTest.TEST_DATA2)), CsvInputUnicodeTest.ONE_CHAR_DELIM);

    private static StepMockHelper<CsvInputMeta, StepDataInterface> stepMockHelper;

    @Test
    public void testUTF16LE() throws Exception {
        doTest(CsvInputUnicodeTest.UTF16LE, CsvInputUnicodeTest.UTF16LE, CsvInputUnicodeTest.TEST_DATA, CsvInputUnicodeTest.ONE_CHAR_DELIM, true);
    }

    @Test
    public void testUTF16BE() throws Exception {
        doTest(CsvInputUnicodeTest.UTF16BE, CsvInputUnicodeTest.UTF16BE, CsvInputUnicodeTest.TEST_DATA, CsvInputUnicodeTest.ONE_CHAR_DELIM, true);
    }

    @Test
    public void testUTF16BE_multiDelim() throws Exception {
        doTest(CsvInputUnicodeTest.UTF16BE, CsvInputUnicodeTest.UTF16BE, CsvInputUnicodeTest.TEST_DATA1, CsvInputUnicodeTest.MULTI_CHAR_DELIM, true);
    }

    @Test
    public void testUTF16LEBOM() throws Exception {
        doTest(CsvInputUnicodeTest.UTF16LEBOM, CsvInputUnicodeTest.UTF16LE, CsvInputUnicodeTest.TEST_DATA, CsvInputUnicodeTest.ONE_CHAR_DELIM, true);
    }

    @Test
    public void testUTF8() throws Exception {
        doTest(CsvInputUnicodeTest.UTF8, CsvInputUnicodeTest.UTF8, CsvInputUnicodeTest.TEST_DATA, CsvInputUnicodeTest.ONE_CHAR_DELIM, true);
    }

    @Test
    public void testUTF8_multiDelim() throws Exception {
        doTest(CsvInputUnicodeTest.UTF8, CsvInputUnicodeTest.UTF8, CsvInputUnicodeTest.TEST_DATA1, CsvInputUnicodeTest.MULTI_CHAR_DELIM, true);
    }

    @Test
    public void testUTF8_headerWithBOM() throws Exception {
        doTest(CsvInputUnicodeTest.UTF8, CsvInputUnicodeTest.UTF8, CsvInputUnicodeTest.TEST_DATA_UTF8_BOM, CsvInputUnicodeTest.ONE_CHAR_DELIM, true);
    }

    @Test
    public void testUTF8_withoutHeaderWithBOM() throws Exception {
        doTest(CsvInputUnicodeTest.UTF8, CsvInputUnicodeTest.UTF8, CsvInputUnicodeTest.TEST_DATA_NOHEADER_UTF8_BOM, CsvInputUnicodeTest.ONE_CHAR_DELIM, false);
    }

    @Test
    public void testUTF16LEDataWithEnclosures() throws Exception {
        doTest(CsvInputUnicodeTest.UTF16LE, CsvInputUnicodeTest.UTF16LE, CsvInputUnicodeTest.TEST_DATA2, CsvInputUnicodeTest.ONE_CHAR_DELIM, true);
    }

    @Test
    public void testUTF16LE_headerWithBOM() throws Exception {
        doTest(CsvInputUnicodeTest.UTF16LE, CsvInputUnicodeTest.UTF16LE, CsvInputUnicodeTest.TEST_DATA_UTF16LE_BOM, CsvInputUnicodeTest.ONE_CHAR_DELIM, true);
    }

    @Test
    public void testUTF16BEDataWithEnclosures() throws Exception {
        doTest(CsvInputUnicodeTest.UTF16BE, CsvInputUnicodeTest.UTF16BE, CsvInputUnicodeTest.TEST_DATA2, CsvInputUnicodeTest.ONE_CHAR_DELIM, true);
    }

    @Test
    public void testUTF16BE_headerWithBOM() throws Exception {
        doTest(CsvInputUnicodeTest.UTF16BE, CsvInputUnicodeTest.UTF16BE, CsvInputUnicodeTest.TEST_DATA_UTF16BE_BOM, CsvInputUnicodeTest.ONE_CHAR_DELIM, true);
    }

    @Test
    public void testUTF16LEBOMDataWithEnclosures() throws Exception {
        doTest(CsvInputUnicodeTest.UTF16LEBOM, CsvInputUnicodeTest.UTF16LE, CsvInputUnicodeTest.TEST_DATA2, CsvInputUnicodeTest.ONE_CHAR_DELIM, true);
    }

    @Test
    public void testUTF16BE_multiDelim_DataWithEnclosures() throws Exception {
        doTest(CsvInputUnicodeTest.UTF16BE, CsvInputUnicodeTest.UTF16BE, CsvInputUnicodeTest.TEST_DATA3, CsvInputUnicodeTest.MULTI_CHAR_DELIM, true);
    }

    @Test
    public void testUTF16LE_multiDelim_DataWithEnclosures() throws Exception {
        doTest(CsvInputUnicodeTest.UTF16LE, CsvInputUnicodeTest.UTF16LE, CsvInputUnicodeTest.TEST_DATA3, CsvInputUnicodeTest.MULTI_CHAR_DELIM, true);
    }

    @Test
    public void testUTF8_multiDelim_DataWithEnclosures() throws Exception {
        doTest(CsvInputUnicodeTest.UTF8, CsvInputUnicodeTest.UTF8, CsvInputUnicodeTest.TEST_DATA3, CsvInputUnicodeTest.MULTI_CHAR_DELIM, true);
    }
}

