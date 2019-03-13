/**
 * Copyright (C) 2009 Google Inc.
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
package com.google.gson;


import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;
import junit.framework.TestCase;


/**
 * Unit tests for the {@link FieldAttributes} class.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class FieldAttributesTest extends TestCase {
    private FieldAttributes fieldAttributes;

    public void testNullField() throws Exception {
        try {
            new FieldAttributes(null);
            TestCase.fail("Field parameter can not be null");
        } catch (NullPointerException expected) {
        }
    }

    public void testDeclaringClass() throws Exception {
        TestCase.assertEquals(FieldAttributesTest.Foo.class, fieldAttributes.getDeclaringClass());
    }

    public void testModifiers() throws Exception {
        TestCase.assertFalse(fieldAttributes.hasModifier(Modifier.STATIC));
        TestCase.assertFalse(fieldAttributes.hasModifier(Modifier.FINAL));
        TestCase.assertFalse(fieldAttributes.hasModifier(Modifier.ABSTRACT));
        TestCase.assertFalse(fieldAttributes.hasModifier(Modifier.VOLATILE));
        TestCase.assertFalse(fieldAttributes.hasModifier(Modifier.PROTECTED));
        TestCase.assertTrue(fieldAttributes.hasModifier(Modifier.PUBLIC));
        TestCase.assertTrue(fieldAttributes.hasModifier(Modifier.TRANSIENT));
    }

    public void testIsSynthetic() throws Exception {
        TestCase.assertFalse(fieldAttributes.isSynthetic());
    }

    public void testName() throws Exception {
        TestCase.assertEquals("bar", fieldAttributes.getName());
    }

    public void testDeclaredTypeAndClass() throws Exception {
        Type expectedType = new TypeToken<List<String>>() {}.getType();
        TestCase.assertEquals(expectedType, fieldAttributes.getDeclaredType());
        TestCase.assertEquals(List.class, fieldAttributes.getDeclaredClass());
    }

    private static class Foo {
        @SuppressWarnings("unused")
        public transient List<String> bar;
    }
}

