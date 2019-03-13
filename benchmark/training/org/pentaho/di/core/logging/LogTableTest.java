/**
 * !
 * Copyright 2010 - 2018 Hitachi Vantara.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pentaho.di.core.logging;


import Const.KETTLE_GLOBAL_LOG_VARIABLES_CLEAR_ON_EXPORT;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.HasDatabasesInterface;


public class LogTableTest {
    private static String PARAM_START_SYMBOL = "${";

    private static String PARAM_END_SYMBOL = "}";

    private static String GLOBAL_PARAM = ((LogTableTest.PARAM_START_SYMBOL) + (Const.KETTLE_STEP_LOG_DB)) + (LogTableTest.PARAM_END_SYMBOL);

    private static String USER_PARAM = ((LogTableTest.PARAM_START_SYMBOL) + "param-content") + (LogTableTest.PARAM_END_SYMBOL);

    private static String HARDCODED_VALUE = "hardcoded";

    private VariableSpace mockedVariableSpace;

    private HasDatabasesInterface mockedHasDbInterface;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void hardcodedFieldsNotChanged() {
        tableFieldsChangedCorrectlyAfterNullingGlobalParams(LogTableTest.HARDCODED_VALUE, LogTableTest.HARDCODED_VALUE);
    }

    @Test
    public void userParamsFieldsNotChanged() {
        tableFieldsChangedCorrectlyAfterNullingGlobalParams(LogTableTest.USER_PARAM, LogTableTest.USER_PARAM);
    }

    @Test
    public void globalParamsFieldsAreNulled() {
        tableFieldsChangedCorrectlyAfterNullingGlobalParams(LogTableTest.GLOBAL_PARAM, null);
    }

    @Test
    public void globalParamsFieldsAreNotNulled() {
        System.setProperty(KETTLE_GLOBAL_LOG_VARIABLES_CLEAR_ON_EXPORT, "false");
        tableFieldsChangedCorrectlyAfterNullingGlobalParams(LogTableTest.GLOBAL_PARAM, LogTableTest.GLOBAL_PARAM);
    }
}

