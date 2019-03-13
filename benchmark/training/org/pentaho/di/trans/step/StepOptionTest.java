/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2018 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.trans.step;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.variables.VariableSpace;


@RunWith(MockitoJUnitRunner.class)
public class StepOptionTest {
    @Mock
    StepMeta stepMeta;

    @Mock
    VariableSpace space;

    @Test
    public void testCheckPass() {
        List<CheckResultInterface> remarks = new ArrayList<>();
        StepOption.checkInteger(remarks, stepMeta, space, "IDENTIFIER", "9");
        StepOption.checkLong(remarks, stepMeta, space, "IDENTIFIER", "9");
        StepOption.checkBoolean(remarks, stepMeta, space, "IDENTIFIER", "true");
        StepOption.checkBoolean(remarks, stepMeta, space, "IDENTIFIER", "false");
        Assert.assertEquals(0, remarks.size());
    }

    @Test
    public void testCheckPassEmpty() {
        List<CheckResultInterface> remarks = new ArrayList<>();
        StepOption.checkInteger(remarks, stepMeta, space, "IDENTIFIER", "");
        StepOption.checkLong(remarks, stepMeta, space, "IDENTIFIER", "");
        StepOption.checkBoolean(remarks, stepMeta, space, "IDENTIFIER", "");
        StepOption.checkInteger(remarks, stepMeta, space, "IDENTIFIER", null);
        StepOption.checkLong(remarks, stepMeta, space, "IDENTIFIER", null);
        StepOption.checkBoolean(remarks, stepMeta, space, "IDENTIFIER", null);
        Assert.assertEquals(0, remarks.size());
    }

    @Test
    public void testCheckFailInteger() {
        List<CheckResultInterface> remarks = new ArrayList<>();
        StepOption.checkInteger(remarks, stepMeta, space, "IDENTIFIER", "asdf");
        Assert.assertEquals(1, remarks.size());
        Assert.assertEquals(remarks.get(0).getText(), getString(StepOption.class, "StepOption.CheckResult.NotAInteger", "IDENTIFIER"));
    }

    @Test
    public void testCheckFailLong() {
        List<CheckResultInterface> remarks = new ArrayList<>();
        StepOption.checkLong(remarks, stepMeta, space, "IDENTIFIER", "asdf");
        Assert.assertEquals(1, remarks.size());
        Assert.assertEquals(remarks.get(0).getText(), getString(StepOption.class, "StepOption.CheckResult.NotAInteger", "IDENTIFIER"));
    }

    @Test
    public void testCheckFailBoolean() {
        List<CheckResultInterface> remarks = new ArrayList<>();
        StepOption.checkBoolean(remarks, stepMeta, space, "IDENTIFIER", "asdf");
        Assert.assertEquals(1, remarks.size());
        Assert.assertEquals(remarks.get(0).getText(), getString(StepOption.class, "StepOption.CheckResult.NotABoolean", "IDENTIFIER"));
    }
}

