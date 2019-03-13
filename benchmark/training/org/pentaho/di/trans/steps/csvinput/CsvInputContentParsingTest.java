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
package org.pentaho.di.trans.steps.csvinput;


import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.steps.fileinput.BaseParsingTest;
import org.pentaho.di.trans.steps.textfileinput.TextFileInputField;


public class CsvInputContentParsingTest extends BaseCsvParsingTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testDefaultOptions() throws Exception {
        init("default.csv");
        setFields(new TextFileInputField("Field 1", (-1), (-1)), new TextFileInputField("Field 2", (-1), (-1)), new TextFileInputField("Field 3", (-1), (-1)));
        process();
        check(new Object[][]{ new Object[]{ "first", "1", "1.1" }, new Object[]{ "second", "2", "2.2" }, new Object[]{ "third", "3", "3.3" } });
    }

    @Test
    public void testColumnNameWithSpaces() throws Exception {
        init("column_name_with_spaces.csv");
        setFields(new TextFileInputField("Field 1", (-1), (-1)), new TextFileInputField("Field 2", (-1), (-1)), new TextFileInputField("Field 3", (-1), (-1)));
        process();
        check(new Object[][]{ new Object[]{ "first", "1", "1.1" }, new Object[]{ "second", "2", "2.2" }, new Object[]{ "third", "3", "3.3" } });
    }

    @Test
    public void testSemicolonOptions() throws Exception {
        meta.setDelimiter(";");
        init("semicolon.csv");
        setFields(new TextFileInputField("Field 1", (-1), (-1)), new TextFileInputField("Field 2", (-1), (-1)), new TextFileInputField("Field 3", (-1), (-1)));
        process();
        check(new Object[][]{ new Object[]{ "first", "1", "1.1" }, new Object[]{ "second", "2", "2.2" }, new Object[]{ "third", "3", "3.3" }, new Object[]{ "\u043d\u0435-\u043b\u0430\u0446\u0456\u043d\u043a\u0430(non-latin)", "4", "4" } });
    }

    @Test
    public void testMultiCharDelimOptions() throws Exception {
        meta.setDelimiter("|||");
        init("multi_delim.csv");
        setFields(new TextFileInputField("Field 1", (-1), (-1)), new TextFileInputField("Field 2", (-1), (-1)), new TextFileInputField("Field 3", (-1), (-1)));
        process();
        check(new Object[][]{ new Object[]{ "first", "1", "1.1" }, new Object[]{ "second", "2", "2.2" }, new Object[]{ "third", "3", "3.3" }, new Object[]{ "\u043d\u0435-\u043b\u0430\u0446\u0456\u043d\u043a\u0430(non-latin)", "4", "4" } });
    }

    @Test(expected = KettleStepException.class)
    public void testNoHeaderOptions() throws Exception {
        meta.setHeaderPresent(false);
        init("default.csv");
        setFields(new TextFileInputField(), new TextFileInputField(), new TextFileInputField());
        process();
    }
}

