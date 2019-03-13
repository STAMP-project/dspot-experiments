/**
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.models.datamodel.workitems;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for PortableParameterDefinitions
 */
public class PortableParameterDefinitionTest {
    @Test
    public void testClassNames() {
        PortableBooleanParameterDefinition p1 = new PortableBooleanParameterDefinition();
        Assert.assertEquals("java.lang.Boolean", p1.getClassName());
        PortableEnumParameterDefinition p2 = new PortableEnumParameterDefinition();
        p2.setClassName("com.tests.example.Smurf");
        Assert.assertEquals("com.tests.example.Smurf", p2.getClassName());
        PortableFloatParameterDefinition p3 = new PortableFloatParameterDefinition();
        Assert.assertEquals("java.lang.Float", p3.getClassName());
        PortableIntegerParameterDefinition p4 = new PortableIntegerParameterDefinition();
        Assert.assertEquals("java.lang.Integer", p4.getClassName());
        PortableObjectParameterDefinition p5 = new PortableObjectParameterDefinition();
        p5.setClassName("com.tests.example.Smurf");
        Assert.assertEquals("com.tests.example.Smurf", p5.getClassName());
        PortableStringParameterDefinition p6 = new PortableStringParameterDefinition();
        Assert.assertEquals("java.lang.String", p6.getClassName());
    }

    @Test
    public void testSimpleClassNames() {
        PortableBooleanParameterDefinition p1 = new PortableBooleanParameterDefinition();
        Assert.assertEquals("Boolean", p1.getSimpleClassName());
        PortableEnumParameterDefinition p2 = new PortableEnumParameterDefinition();
        p2.setClassName("com.tests.example.Smurf");
        Assert.assertEquals("Smurf", p2.getSimpleClassName());
        PortableFloatParameterDefinition p3 = new PortableFloatParameterDefinition();
        Assert.assertEquals("Float", p3.getSimpleClassName());
        PortableIntegerParameterDefinition p4 = new PortableIntegerParameterDefinition();
        Assert.assertEquals("Integer", p4.getSimpleClassName());
        PortableObjectParameterDefinition p5 = new PortableObjectParameterDefinition();
        p5.setClassName("com.tests.example.Smurf");
        Assert.assertEquals("Smurf", p5.getSimpleClassName());
        PortableStringParameterDefinition p6 = new PortableStringParameterDefinition();
        Assert.assertEquals("String", p6.getSimpleClassName());
    }

    @Test
    public void testAsString() {
        PortableBooleanParameterDefinition p1 = new PortableBooleanParameterDefinition();
        p1.setValue(Boolean.TRUE);
        Assert.assertEquals("Boolean.TRUE", p1.asString());
        PortableEnumParameterDefinition p2 = new PortableEnumParameterDefinition();
        p2.setValue("PUPA");
        p2.setClassName("Smurf");
        Assert.assertEquals("Smurf.PUPA", p2.asString());
        PortableFloatParameterDefinition p3 = new PortableFloatParameterDefinition();
        p3.setValue(1.23F);
        Assert.assertEquals("1.23f", p3.asString());
        PortableIntegerParameterDefinition p4 = new PortableIntegerParameterDefinition();
        p4.setValue(123);
        Assert.assertEquals("123", p4.asString());
        PortableObjectParameterDefinition p5 = new PortableObjectParameterDefinition();
        p5.setBinding("$b");
        Assert.assertEquals("$b", p5.asString());
        PortableStringParameterDefinition p6 = new PortableStringParameterDefinition();
        p6.setValue("hello");
        Assert.assertEquals("\"hello\"", p6.asString());
    }

    @Test
    public void testAsStringNullValues() {
        PortableBooleanParameterDefinition p1 = new PortableBooleanParameterDefinition();
        Assert.assertEquals("null", p1.asString());
        PortableEnumParameterDefinition p2 = new PortableEnumParameterDefinition();
        p2.setClassName("Smurf");
        Assert.assertEquals("null", p2.asString());
        PortableFloatParameterDefinition p3 = new PortableFloatParameterDefinition();
        Assert.assertEquals("null", p3.asString());
        PortableIntegerParameterDefinition p4 = new PortableIntegerParameterDefinition();
        Assert.assertEquals("null", p4.asString());
        PortableObjectParameterDefinition p5 = new PortableObjectParameterDefinition();
        Assert.assertEquals("null", p5.asString());
        PortableStringParameterDefinition p6 = new PortableStringParameterDefinition();
        Assert.assertEquals("null", p6.asString());
    }

    @Test
    public void testAsStringWithBindings() {
        PortableBooleanParameterDefinition p1 = new PortableBooleanParameterDefinition();
        p1.setValue(Boolean.TRUE);
        p1.setBinding("$b");
        Assert.assertEquals("$b", p1.asString());
        PortableEnumParameterDefinition p2 = new PortableEnumParameterDefinition();
        p2.setValue("PUPA");
        p2.setBinding("$b");
        p2.setClassName("Smurf");
        Assert.assertEquals("$b", p2.asString());
        PortableFloatParameterDefinition p3 = new PortableFloatParameterDefinition();
        p3.setValue(1.23F);
        p3.setBinding("$b");
        Assert.assertEquals("$b", p3.asString());
        PortableIntegerParameterDefinition p4 = new PortableIntegerParameterDefinition();
        p4.setValue(123);
        p4.setBinding("$b");
        Assert.assertEquals("$b", p4.asString());
        PortableObjectParameterDefinition p5 = new PortableObjectParameterDefinition();
        p5.setBinding("$b");
        Assert.assertEquals("$b", p5.asString());
        PortableStringParameterDefinition p6 = new PortableStringParameterDefinition();
        p6.setValue("hello");
        p6.setBinding("$b");
        Assert.assertEquals("$b", p6.asString());
    }

    @Test
    public void testBindings1() {
        PortableBooleanParameterDefinition p1 = new PortableBooleanParameterDefinition();
        p1.setValue(Boolean.TRUE);
        p1.setBinding("$b");
        Assert.assertEquals(true, p1.isBound());
        PortableEnumParameterDefinition p2 = new PortableEnumParameterDefinition();
        p2.setValue("PUPA");
        p2.setBinding("$b");
        p2.setClassName("Smurf");
        Assert.assertEquals(true, p2.isBound());
        PortableFloatParameterDefinition p3 = new PortableFloatParameterDefinition();
        p3.setValue(1.23F);
        p3.setBinding("$b");
        Assert.assertEquals(true, p3.isBound());
        PortableIntegerParameterDefinition p4 = new PortableIntegerParameterDefinition();
        p4.setValue(123);
        p4.setBinding("$b");
        Assert.assertEquals(true, p4.isBound());
        PortableObjectParameterDefinition p5 = new PortableObjectParameterDefinition();
        p5.setBinding("$b");
        Assert.assertEquals(true, p5.isBound());
        PortableStringParameterDefinition p6 = new PortableStringParameterDefinition();
        p6.setValue("hello");
        p6.setBinding("$b");
        Assert.assertEquals(true, p6.isBound());
    }

    @Test
    public void testBindings2() {
        PortableBooleanParameterDefinition p1 = new PortableBooleanParameterDefinition();
        p1.setValue(Boolean.TRUE);
        p1.setBinding("");
        Assert.assertEquals(false, p1.isBound());
        PortableEnumParameterDefinition p2 = new PortableEnumParameterDefinition();
        p2.setValue("PUPA");
        p2.setBinding("");
        p2.setClassName("Smurf");
        Assert.assertEquals(false, p2.isBound());
        PortableFloatParameterDefinition p3 = new PortableFloatParameterDefinition();
        p3.setValue(1.23F);
        p3.setBinding("");
        Assert.assertEquals(false, p3.isBound());
        PortableIntegerParameterDefinition p4 = new PortableIntegerParameterDefinition();
        p4.setValue(123);
        p4.setBinding("");
        Assert.assertEquals(false, p4.isBound());
        PortableObjectParameterDefinition p5 = new PortableObjectParameterDefinition();
        p5.setBinding("");
        Assert.assertEquals(false, p5.isBound());
        PortableStringParameterDefinition p6 = new PortableStringParameterDefinition();
        p6.setValue("hello");
        p6.setBinding("");
        Assert.assertEquals(false, p6.isBound());
    }
}

