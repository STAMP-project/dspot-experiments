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


import org.junit.Assert;
import org.junit.Test;


/**
 * The ObjectUtilsJUnitTest class is a test suite of test cases for testing the contract and
 * functionality of the ObjectUtils class.
 * <p/>
 *
 * @see org.apache.geode.internal.lang.ObjectUtils
 * @see org.junit.Assert
 * @see org.junit.Test
 */
public class ObjectUtilsJUnitTest {
    @Test
    public void testEqualsWithUnequalObjects() {
        Assert.assertFalse(ObjectUtils.equals(null, null));
        Assert.assertFalse(ObjectUtils.equals(null, "null"));
        Assert.assertFalse(ObjectUtils.equals("nil", null));
        Assert.assertFalse(ObjectUtils.equals("nil", "null"));
        Assert.assertFalse(ObjectUtils.equals("true", true));
        Assert.assertFalse(ObjectUtils.equals(false, true));
        Assert.assertFalse(ObjectUtils.equals('c', 'C'));
        Assert.assertFalse(ObjectUtils.equals(0.0, (-0.0)));
        Assert.assertFalse(ObjectUtils.equals(3.14159, Math.PI));
        Assert.assertFalse(ObjectUtils.equals(Integer.MIN_VALUE, Integer.MAX_VALUE));
        Assert.assertFalse(ObjectUtils.equals("test", "TEST"));
    }

    @Test
    public void testEqualsWithEqualObjects() {
        Assert.assertTrue(ObjectUtils.equals(true, Boolean.TRUE));
        Assert.assertTrue(ObjectUtils.equals(new Character('c'), 'c'));
        Assert.assertTrue(ObjectUtils.equals(Double.MIN_VALUE, Double.MIN_VALUE));
        Assert.assertTrue(ObjectUtils.equals(Integer.MAX_VALUE, Integer.MAX_VALUE));
        Assert.assertTrue(ObjectUtils.equals("null", "null"));
        Assert.assertTrue(ObjectUtils.equals("test", new String("test")));
    }

    @Test
    public void testEqualsIgnoreNullWithUnequalObjects() {
        Assert.assertFalse(ObjectUtils.equalsIgnoreNull(null, "null"));
        Assert.assertFalse(ObjectUtils.equalsIgnoreNull("nil", null));
        Assert.assertFalse(ObjectUtils.equalsIgnoreNull("nil", "null"));
        Assert.assertFalse(ObjectUtils.equalsIgnoreNull("test", "testing"));
    }

    @Test
    public void testEqualsIgnoreNullWithEqualObjects() {
        Assert.assertTrue(ObjectUtils.equalsIgnoreNull(null, null));
        Assert.assertTrue(ObjectUtils.equalsIgnoreNull("nil", "nil"));
        Assert.assertTrue(ObjectUtils.equalsIgnoreNull("null", "null"));
        Assert.assertTrue(ObjectUtils.equalsIgnoreNull("test", "test"));
    }

    @Test
    public void testHashCode() {
        Assert.assertEquals(0, ObjectUtils.hashCode(null));
        Assert.assertEquals(Character.valueOf('c').hashCode(), ObjectUtils.hashCode('c'));
        Assert.assertEquals(Boolean.TRUE.hashCode(), ObjectUtils.hashCode(true));
        Assert.assertEquals(Double.valueOf(Math.PI).hashCode(), ObjectUtils.hashCode(Math.PI));
        Assert.assertEquals(Integer.valueOf(0).hashCode(), ObjectUtils.hashCode(0));
        Assert.assertEquals("test".hashCode(), ObjectUtils.hashCode("test"));
    }

    @Test
    public void testToString() {
        Assert.assertNull(ObjectUtils.toString(null));
        Assert.assertEquals("", ObjectUtils.toString(""));
        Assert.assertEquals(" ", ObjectUtils.toString(" "));
        Assert.assertEquals("null", ObjectUtils.toString("null"));
        Assert.assertEquals("test", ObjectUtils.toString("test"));
        Assert.assertEquals("J", ObjectUtils.toString('J'));
        Assert.assertEquals("2", ObjectUtils.toString(2));
        Assert.assertEquals(String.valueOf(Math.PI), ObjectUtils.toString(Math.PI));
        Assert.assertEquals("true", ObjectUtils.toString(Boolean.TRUE));
    }
}

