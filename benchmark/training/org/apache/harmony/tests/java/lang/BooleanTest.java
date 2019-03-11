/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.harmony.tests.java.lang;


import junit.framework.TestCase;


public class BooleanTest extends TestCase {
    /**
     * java.lang.Boolean#hashCode()
     */
    public void test_hashCode() {
        TestCase.assertEquals(1231, Boolean.TRUE.hashCode());
        TestCase.assertEquals(1237, Boolean.FALSE.hashCode());
    }

    /**
     * java.lang.Boolean#Boolean(String)
     */
    public void test_ConstructorLjava_lang_String() {
        TestCase.assertEquals(Boolean.TRUE, new Boolean("TRUE"));
        TestCase.assertEquals(Boolean.TRUE, new Boolean("true"));
        TestCase.assertEquals(Boolean.TRUE, new Boolean("True"));
        TestCase.assertEquals(Boolean.FALSE, new Boolean("yes"));
        TestCase.assertEquals(Boolean.FALSE, new Boolean("false"));
    }

    /**
     * java.lang.Boolean#Boolean(boolean)
     */
    public void test_ConstructorZ() {
        TestCase.assertEquals(Boolean.TRUE, new Boolean(true));
        TestCase.assertEquals(Boolean.FALSE, new Boolean(false));
    }

    /**
     * java.lang.Boolean#booleanValue()
     */
    public void test_booleanValue() {
        TestCase.assertTrue(Boolean.TRUE.booleanValue());
        TestCase.assertFalse(Boolean.FALSE.booleanValue());
    }

    /**
     * java.lang.Boolean#equals(Object)
     */
    public void test_equalsLjava_lang_Object() {
        TestCase.assertTrue(Boolean.TRUE.equals(Boolean.TRUE));
        TestCase.assertTrue(Boolean.TRUE.equals(new Boolean(true)));
        TestCase.assertFalse(Boolean.TRUE.equals("true"));
        TestCase.assertFalse(Boolean.TRUE.equals(null));
        TestCase.assertFalse(Boolean.FALSE.equals(Boolean.TRUE));
        TestCase.assertTrue(Boolean.FALSE.equals(Boolean.FALSE));
        TestCase.assertTrue(Boolean.FALSE.equals(new Boolean(false)));
    }

    /**
     * java.lang.Boolean#getBoolean(String)
     */
    public void test_getBooleanLjava_lang_String() {
        System.setProperty(getClass().getName(), "true");
        TestCase.assertTrue(Boolean.getBoolean(getClass().getName()));
        System.setProperty(getClass().getName(), "TRUE");
        TestCase.assertTrue(Boolean.getBoolean(getClass().getName()));
        System.setProperty(getClass().getName(), "false");
        TestCase.assertFalse(Boolean.getBoolean(getClass().getName()));
    }

    /**
     * java.lang.Boolean#toString()
     */
    public void test_toString() {
        TestCase.assertEquals("true", Boolean.TRUE.toString());
        TestCase.assertEquals("false", Boolean.FALSE.toString());
    }

    /**
     * java.lang.Boolean#toString(boolean)
     */
    public void test_toStringZ() {
        TestCase.assertEquals("true", Boolean.toString(true));
        TestCase.assertEquals("false", Boolean.toString(false));
    }

    /**
     * java.lang.Boolean#valueOf(String)
     */
    public void test_valueOfLjava_lang_String() {
        TestCase.assertEquals(Boolean.TRUE, Boolean.valueOf("true"));
        TestCase.assertEquals(Boolean.FALSE, Boolean.valueOf("false"));
        TestCase.assertEquals(Boolean.TRUE, Boolean.valueOf("TRUE"));
        TestCase.assertEquals(Boolean.FALSE, Boolean.valueOf("false"));
        TestCase.assertEquals(Boolean.FALSE, Boolean.valueOf(null));
        TestCase.assertEquals(Boolean.FALSE, Boolean.valueOf(""));
        TestCase.assertEquals(Boolean.FALSE, Boolean.valueOf("invalid"));
        TestCase.assertTrue("Failed to parse true to true", Boolean.valueOf("true").booleanValue());
        TestCase.assertTrue("Failed to parse mixed case true to true", Boolean.valueOf("TrUe").booleanValue());
        TestCase.assertTrue("parsed non-true to true", (!(Boolean.valueOf("ddddd").booleanValue())));
    }

    /**
     * java.lang.Boolean#valueOf(boolean)
     */
    public void test_valueOfZ() {
        TestCase.assertEquals(Boolean.TRUE, Boolean.valueOf(true));
        TestCase.assertEquals(Boolean.FALSE, Boolean.valueOf(false));
    }

    /**
     * java.lang.Boolean#parseBoolean(String)
     */
    public void test_parseBooleanLjava_lang_String() {
        TestCase.assertTrue(Boolean.parseBoolean("true"));
        TestCase.assertTrue(Boolean.parseBoolean("TRUE"));
        TestCase.assertFalse(Boolean.parseBoolean("false"));
        TestCase.assertFalse(Boolean.parseBoolean(null));
        TestCase.assertFalse(Boolean.parseBoolean(""));
        TestCase.assertFalse(Boolean.parseBoolean("invalid"));
    }

    /**
     * java.lang.Boolean#compareTo(Boolean)
     */
    public void test_compareToLjava_lang_Boolean() {
        TestCase.assertTrue(((Boolean.TRUE.compareTo(Boolean.TRUE)) == 0));
        TestCase.assertTrue(((Boolean.FALSE.compareTo(Boolean.FALSE)) == 0));
        TestCase.assertTrue(((Boolean.TRUE.compareTo(Boolean.FALSE)) > 0));
        TestCase.assertTrue(((Boolean.FALSE.compareTo(Boolean.TRUE)) < 0));
        try {
            Boolean.TRUE.compareTo(null);
            TestCase.fail("No NPE");
        } catch (NullPointerException e) {
        }
    }
}

