/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.lang;


import StringUtils.EMPTY;
import java.util.Calendar;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;


/**
 * The ClassUtilsJUnitTest class is a test suite with test cases to test the contract and
 * functionality of the ClassUtils class.
 * <p/>
 *
 * @see org.apache.geode.internal.lang.ClassUtils
 * @see org.junit.Assert
 * @see org.junit.Test
 * @since GemFire 7.0
 */
public class ClassUtilsJUnitTest {
    @Test
    public void testForNameWithExistingClass() {
        Assert.assertEquals(Object.class, ClassUtils.forName("java.lang.Object", new RuntimeException("unexpected")));
    }

    @Test(expected = RuntimeException.class)
    public void testForNameWithNonExistingClass() {
        try {
            ClassUtils.forName("com.mycompany.non.existing.Class", new RuntimeException("expected"));
        } catch (RuntimeException expected) {
            Assert.assertEquals("expected", expected.getMessage());
            throw expected;
        }
    }

    @Test(expected = NullPointerException.class)
    public void testForNameWithNullClassName() {
        ClassUtils.forName(null, new IllegalArgumentException("Null Class!"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testForNameWithEmptyClassName() {
        try {
            ClassUtils.forName(EMPTY, new IllegalArgumentException("Empty Class Name!"));
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Empty Class Name!", expected.getMessage());
            throw expected;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testForNameWithBlankClassName() {
        try {
            ClassUtils.forName("  ", new IllegalArgumentException("Blank Class Name!"));
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Blank Class Name!", expected.getMessage());
            throw expected;
        }
    }

    @Test(expected = NullPointerException.class)
    public void testForNameThrowsNullPointerExceptionForNullRuntimeExceptionArgument() {
        ClassUtils.forName("com.mycompany.non.existing.Class", null);
    }

    @Test
    public void testGetClassWithNull() {
        Assert.assertNull(ClassUtils.getClass(null));
    }

    @Test
    public void testGetClassWithObject() {
        Assert.assertEquals(String.class, ClassUtils.getClass("test"));
    }

    @Test
    public void testGetClassNameWithNull() {
        Assert.assertNull(ClassUtils.getClassName(null));
    }

    @Test
    public void testGetClassNameWithObject() {
        Assert.assertEquals(String.class.getName(), ClassUtils.getClassName("null"));
    }

    @Test
    public void testIsClassAvailableWithExistingClass() {
        Assert.assertTrue(ClassUtils.isClassAvailable("java.lang.Object"));
    }

    @Test
    public void testIsClassAvailableWithNonExistingClass() {
        Assert.assertFalse(ClassUtils.isClassAvailable("com.mycompany.non.existing.Class"));
    }

    @Test
    public void testIsAnInstanceOf() {
        Assert.assertTrue(ClassUtils.isInstanceOf(Object.class, "null"));
        Assert.assertTrue(ClassUtils.isInstanceOf(String.class, "C"));
        Assert.assertTrue(ClassUtils.isInstanceOf(Number.class, Math.PI));
        Assert.assertTrue(ClassUtils.isInstanceOf(Number.class, 3.14F));
        Assert.assertTrue(ClassUtils.isInstanceOf(Number.class, 0L));
        Assert.assertTrue(ClassUtils.isInstanceOf(Number.class, 0));
        Assert.assertTrue(ClassUtils.isInstanceOf(Boolean.class, true));
        Assert.assertTrue(ClassUtils.isInstanceOf(Boolean.class, false));
    }

    @Test
    public void testIsNotAnInstanceOf() {
        Assert.assertFalse(ClassUtils.isInstanceOf(null, null));
        Assert.assertFalse(ClassUtils.isInstanceOf(null, new Object()));
        Assert.assertFalse(ClassUtils.isInstanceOf(Object.class, null));
        Assert.assertFalse(ClassUtils.isInstanceOf(String.class, 'C'));
        Assert.assertFalse(ClassUtils.isInstanceOf(Long.class, Math.PI));
        Assert.assertFalse(ClassUtils.isInstanceOf(Double.class, 3.14F));
        Assert.assertFalse(ClassUtils.isInstanceOf(Date.class, Calendar.getInstance()));
        Assert.assertFalse(ClassUtils.isInstanceOf(Boolean.class, 1));
        Assert.assertFalse(ClassUtils.isInstanceOf(Boolean.class, "false"));
    }

    @Test
    public void testIsNotInstanceOfWithNoTypes() {
        Assert.assertTrue(ClassUtils.isNotInstanceOf("test"));
    }

    @Test
    public void testIsNotInstanceOfWithMultipleTypes() {
        Assert.assertTrue(ClassUtils.isNotInstanceOf("test", Boolean.class, Character.class, Integer.class, Double.class));
    }

    @Test
    public void testIsNotInstanceOfWithMultipleNumberTypes() {
        Assert.assertFalse(ClassUtils.isNotInstanceOf(1, Double.class, Long.class, Number.class));
    }

    @Test
    public void testIsNotInstanceOfWithMultipleCompatibleTypes() {
        Assert.assertFalse(ClassUtils.isNotInstanceOf(1, Double.class, Float.class, Integer.class, Long.class, Number.class));
    }
}

