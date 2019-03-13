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
package org.pentaho.di.core.parameters;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class NamedParamsDefaultTest {
    NamedParams namedParams;

    @Test
    public void testParameters() throws Exception {
        Assert.assertNull(namedParams.getParameterValue("var1"));
        Assert.assertNull(namedParams.getParameterDefault("var1"));
        Assert.assertNull(namedParams.getParameterDescription("var1"));
        namedParams.setParameterValue("var1", "y");
        // Values for new parameters must be added by addParameterDefinition
        Assert.assertNull(namedParams.getParameterValue("var1"));
        Assert.assertNull(namedParams.getParameterDefault("var1"));
        Assert.assertNull(namedParams.getParameterDescription("var1"));
        namedParams.addParameterDefinition("var2", "z", "My Description");
        Assert.assertEquals("", namedParams.getParameterValue("var2"));
        Assert.assertEquals("z", namedParams.getParameterDefault("var2"));
        Assert.assertEquals("My Description", namedParams.getParameterDescription("var2"));
        namedParams.setParameterValue("var2", "y");
        Assert.assertEquals("y", namedParams.getParameterValue("var2"));
        Assert.assertEquals("z", namedParams.getParameterDefault("var2"));
        // clearParameters() just clears their values, not their presence
        namedParams.clearParameters();
        Assert.assertEquals("", namedParams.getParameterValue("var2"));
        // eraseParameters() clears the list of parameters
        namedParams.eraseParameters();
        Assert.assertNull(namedParams.getParameterValue("var1"));
        // Call activateParameters(), an empty method, for coverage
        namedParams.activateParameters();
    }

    @Test(expected = DuplicateParamException.class)
    public void testAddParameterDefinitionWithException() throws DuplicateParamException {
        namedParams.addParameterDefinition("key", "value", "description");
        namedParams.addParameterDefinition("key", "value", "description");
    }

    @Test
    public void testCopyParametersFromNullChecks() throws Exception {
        // Test null case
        namedParams.copyParametersFrom(null);
        NamedParams namedParams2 = new NamedParamsDefault();
        // Test internal params == null case
        ((NamedParamsDefault) (namedParams)).params = null;
        namedParams.copyParametersFrom(namedParams2);
    }

    @Test
    public void testCopyParametersFrom() throws Exception {
        NamedParams namedParams2 = new NamedParamsDefault();
        namedParams2.addParameterDefinition("key", "default value", "description");
        namedParams2.setParameterValue("key", "value");
        Assert.assertNull(namedParams.getParameterValue("key"));
        namedParams.copyParametersFrom(namedParams2);
        Assert.assertEquals("value", namedParams.getParameterValue("key"));
    }

    @Test
    public void testCopyParametersFromWithException() throws Exception {
        NamedParams namedParams2 = Mockito.mock(NamedParams.class);
        Mockito.when(namedParams2.listParameters()).thenReturn(new String[]{ "key" });
        Mockito.when(namedParams2.getParameterDescription(ArgumentMatchers.anyString())).thenThrow(UnknownParamException.class);
        Mockito.when(namedParams2.getParameterDefault(ArgumentMatchers.anyString())).thenThrow(UnknownParamException.class);
        Mockito.when(namedParams2.getParameterValue(ArgumentMatchers.anyString())).thenThrow(UnknownParamException.class);
        Mockito.doThrow(DuplicateParamException.class).when(namedParams).addParameterDefinition(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        namedParams.copyParametersFrom(namedParams2);
        // Normally the param's properties would be empty, but we're overriding the addParameterDefinition() to throw an
        // exception, so the param properties never get set and are thus null
        Assert.assertNull(namedParams.getParameterDescription("key"));
        Assert.assertNull(namedParams.getParameterDefault("key"));
        Assert.assertNull(namedParams.getParameterValue("key"));
    }

    @Test
    public void testMergeParametersWith() throws Exception {
        NamedParams namedParamsTest = new NamedParamsDefault();
        namedParamsTest.addParameterDefinition("key1", "def1", "desc1");
        namedParamsTest.addParameterDefinition("key2", "def2", "desc2");
        namedParamsTest.addParameterDefinition("key3", "def3", "desc3");
        namedParamsTest.addParameterDefinition("key4", "def4", "desc4");
        namedParamsTest.setParameterValue("key1", "val1");
        namedParamsTest.setParameterValue("key2", "val2");
        namedParamsTest.setParameterValue("key3", "val3");
        namedParamsTest.setParameterValue("key4", "val4");
        NamedParams namedParamsMerge = new NamedParamsDefault();
        namedParamsMerge.addParameterDefinition("key5", "def5", "desc5");
        namedParamsMerge.addParameterDefinition("key6", "def6", "desc6");
        namedParamsMerge.addParameterDefinition("key3", "def3a", "desc3a");
        namedParamsMerge.addParameterDefinition("key4", "def4a", "desc4a");
        namedParamsMerge.setParameterValue("key5", "val1");
        namedParamsMerge.setParameterValue("key6", "val2");
        namedParamsMerge.setParameterValue("key3", "val3a");
        namedParamsMerge.setParameterValue("key4", "val4a");
        namedParamsTest.mergeParametersWith(namedParamsMerge, false);
        Assert.assertEquals(6, namedParamsTest.listParameters().length);
        Assert.assertEquals("def3", namedParamsTest.getParameterDefault("key3"));
        Assert.assertEquals("desc3", namedParamsTest.getParameterDescription("key3"));
        Assert.assertEquals("val3", namedParamsTest.getParameterValue("key3"));
        Assert.assertEquals("def4", namedParamsTest.getParameterDefault("key4"));
        Assert.assertEquals("desc4", namedParamsTest.getParameterDescription("key4"));
        Assert.assertEquals("val4", namedParamsTest.getParameterValue("key4"));
        namedParamsMerge.addParameterDefinition("key7", "def7", "desc7");
        namedParamsTest.setParameterValue("ke7", "val7");
        namedParamsTest.mergeParametersWith(namedParamsMerge, true);
        Assert.assertEquals(7, namedParamsTest.listParameters().length);
        Assert.assertEquals("def3a", namedParamsTest.getParameterDefault("key3"));
        Assert.assertEquals("desc3a", namedParamsTest.getParameterDescription("key3"));
        Assert.assertEquals("val3a", namedParamsTest.getParameterValue("key3"));
        Assert.assertEquals("def4a", namedParamsTest.getParameterDefault("key4"));
        Assert.assertEquals("desc4a", namedParamsTest.getParameterDescription("key4"));
        Assert.assertEquals("val4a", namedParamsTest.getParameterValue("key4"));
    }
}

