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
package org.pentaho.di.trans.steps.propertyinput;


import PropertyInputField.COLUMN_KEY;
import PropertyInputField.COLUMN_VALUE;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;


public class PropertyInputContentParsingTest extends BasePropertyParsingTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testDefaultOptions() throws Exception {
        init("default.properties");
        PropertyInputField f1 = new PropertyInputField("f1");
        f1.setColumn(COLUMN_KEY);
        PropertyInputField f2 = new PropertyInputField("f2");
        f2.setColumn(COLUMN_VALUE);
        setFields(f1, f2);
        process();
        check(new Object[][]{ new Object[]{ "f1", "d1" }, new Object[]{ "f2", "d2" } });
    }
}

