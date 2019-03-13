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
package org.pentaho.di.trans.steps.fieldsplitter;


import Const.KETTLE_EMPTY_STRING_DIFFERS_FROM_NULL;
import java.util.Arrays;
import java.util.List;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.steps.mock.StepMockHelper;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 *
 *
 * @author Andrey Khayrutdinov
 */
@RunWith(PowerMockRunner.class)
public class FieldSplitter_EmptyStringVsNull_Test {
    private StepMockHelper<FieldSplitterMeta, StepDataInterface> helper;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void emptyAndNullsAreNotDifferent() throws Exception {
        System.setProperty(KETTLE_EMPTY_STRING_DIFFERS_FROM_NULL, "N");
        List<Object[]> expected = Arrays.asList(new Object[]{ "a", "", "a" }, new Object[]{ "b", null, "b" }, new Object[]{ null });
        executeAndAssertResults(expected);
    }

    @Test
    public void emptyAndNullsAreDifferent() throws Exception {
        System.setProperty(KETTLE_EMPTY_STRING_DIFFERS_FROM_NULL, "Y");
        List<Object[]> expected = Arrays.asList(new Object[]{ "a", "", "a" }, new Object[]{ "b", "", "b" }, new Object[]{ "", "", "" });
        executeAndAssertResults(expected);
    }
}

