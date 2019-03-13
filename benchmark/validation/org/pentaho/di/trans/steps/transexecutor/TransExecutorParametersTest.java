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
package org.pentaho.di.trans.steps.transexecutor;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class TransExecutorParametersTest {
    @Test
    public void testClone() throws Exception {
        TransExecutorParameters meta = new TransExecutorParameters();
        meta.setField(new String[]{ "field1", "field2" });
        meta.setVariable(new String[]{ "var1", "var2" });
        meta.setInput(new String[]{ "input1", "input2" });
        TransExecutorParameters cloned = ((TransExecutorParameters) (meta.clone()));
        Assert.assertFalse(((cloned.getField()) == (meta.getField())));
        Assert.assertTrue(Arrays.equals(cloned.getField(), meta.getField()));
        Assert.assertFalse(((cloned.getVariable()) == (meta.getVariable())));
        Assert.assertTrue(Arrays.equals(cloned.getVariable(), meta.getVariable()));
        Assert.assertFalse(((cloned.getInput()) == (meta.getInput())));
        Assert.assertTrue(Arrays.equals(cloned.getInput(), meta.getInput()));
    }
}

