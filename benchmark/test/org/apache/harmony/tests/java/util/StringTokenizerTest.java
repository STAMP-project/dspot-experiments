/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.harmony.tests.java.util;


import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import junit.framework.TestCase;


public class StringTokenizerTest extends TestCase {
    /**
     * java.util.StringTokenizer#StringTokenizer(java.lang.String)
     */
    public void test_ConstructorLjava_lang_String() {
        // Test for method java.util.StringTokenizer(java.lang.String)
        try {
            new StringTokenizer(null);
            TestCase.fail("NullPointerException is not thrown.");
        } catch (NullPointerException npe) {
            // expected
        }
    }

    /**
     * java.util.StringTokenizer#StringTokenizer(java.lang.String,
     *        java.lang.String)
     */
    public void test_ConstructorLjava_lang_StringLjava_lang_String() {
        // Test for method java.util.StringTokenizer(java.lang.String,
        // java.lang.String)
        StringTokenizer st = new StringTokenizer("This:is:a:test:String", ":");
        TestCase.assertTrue("Created incorrect tokenizer", (((st.countTokens()) == 5) && (st.nextElement().equals("This"))));
        st = new StringTokenizer("This:is:a:test:String", null);
        try {
            new StringTokenizer(null, ":");
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * java.util.StringTokenizer#StringTokenizer(java.lang.String,
     *        java.lang.String, boolean)
     */
    public void test_ConstructorLjava_lang_StringLjava_lang_StringZ() {
        // Test for method java.util.StringTokenizer(java.lang.String,
        // java.lang.String, boolean)
        StringTokenizer st = new StringTokenizer("This:is:a:test:String", ":", true);
        st.nextElement();
        TestCase.assertTrue("Created incorrect tokenizer", (((st.countTokens()) == 8) && (st.nextElement().equals(":"))));
        st = new StringTokenizer("This:is:a:test:String", null, true);
        st = new StringTokenizer("This:is:a:test:String", null, false);
        try {
            new StringTokenizer(null, ":", true);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * java.util.StringTokenizer#countTokens()
     */
    public void test_countTokens() {
        // Test for method int java.util.StringTokenizer.countTokens()
        StringTokenizer st = new StringTokenizer("This is a test String");
        TestCase.assertEquals("Incorrect token count returned", 5, st.countTokens());
    }

    /**
     * java.util.StringTokenizer#hasMoreElements()
     */
    public void test_hasMoreElements() {
        // Test for method boolean java.util.StringTokenizer.hasMoreElements()
        StringTokenizer st = new StringTokenizer("This is a test String");
        st.nextElement();
        TestCase.assertTrue("hasMoreElements returned incorrect value", st.hasMoreElements());
        st.nextElement();
        st.nextElement();
        st.nextElement();
        st.nextElement();
        TestCase.assertTrue("hasMoreElements returned incorrect value", (!(st.hasMoreElements())));
    }

    /**
     * java.util.StringTokenizer#hasMoreTokens()
     */
    public void test_hasMoreTokens() {
        // Test for method boolean java.util.StringTokenizer.hasMoreTokens()
        StringTokenizer st = new StringTokenizer("This is a test String");
        for (int counter = 0; counter < 5; counter++) {
            TestCase.assertTrue("StringTokenizer incorrectly reports it has no more tokens", st.hasMoreTokens());
            st.nextToken();
        }
        TestCase.assertTrue("StringTokenizer incorrectly reports it has more tokens", (!(st.hasMoreTokens())));
    }

    /**
     * java.util.StringTokenizer#nextElement()
     */
    public void test_nextElement() {
        // Test for method java.lang.Object
        // java.util.StringTokenizer.nextElement()
        StringTokenizer st = new StringTokenizer("This is a test String");
        TestCase.assertEquals("nextElement returned incorrect value", "This", ((String) (st.nextElement())));
        TestCase.assertEquals("nextElement returned incorrect value", "is", ((String) (st.nextElement())));
        TestCase.assertEquals("nextElement returned incorrect value", "a", ((String) (st.nextElement())));
        TestCase.assertEquals("nextElement returned incorrect value", "test", ((String) (st.nextElement())));
        TestCase.assertEquals("nextElement returned incorrect value", "String", ((String) (st.nextElement())));
        try {
            st.nextElement();
            TestCase.fail("nextElement failed to throw a NoSuchElementException when it should have been out of elements");
        } catch (NoSuchElementException e) {
            return;
        }
    }

    /**
     * java.util.StringTokenizer#nextToken()
     */
    public void test_nextToken() {
        // Test for method java.lang.String
        // java.util.StringTokenizer.nextToken()
        StringTokenizer st = new StringTokenizer("This is a test String");
        TestCase.assertEquals("nextToken returned incorrect value", "This", st.nextToken());
        TestCase.assertEquals("nextToken returned incorrect value", "is", st.nextToken());
        TestCase.assertEquals("nextToken returned incorrect value", "a", st.nextToken());
        TestCase.assertEquals("nextToken returned incorrect value", "test", st.nextToken());
        TestCase.assertEquals("nextToken returned incorrect value", "String", st.nextToken());
        try {
            st.nextToken();
            TestCase.fail("nextToken failed to throw a NoSuchElementException when it should have been out of elements");
        } catch (NoSuchElementException e) {
            return;
        }
    }

    /**
     * java.util.StringTokenizer#nextToken(java.lang.String)
     */
    public void test_nextTokenLjava_lang_String() {
        // Test for method java.lang.String
        // java.util.StringTokenizer.nextToken(java.lang.String)
        StringTokenizer st = new StringTokenizer("This is a test String");
        TestCase.assertEquals("nextToken(String) returned incorrect value with normal token String", "This", st.nextToken(" "));
        TestCase.assertEquals("nextToken(String) returned incorrect value with custom token String", " is a ", st.nextToken("tr"));
        TestCase.assertEquals("calling nextToken() did not use the new default delimiter list", "es", st.nextToken());
        st = new StringTokenizer("This:is:a:test:String", " ");
        TestCase.assertTrue(st.nextToken(":").equals("This"));
        TestCase.assertTrue(st.nextToken(":").equals("is"));
        TestCase.assertTrue(st.nextToken(":").equals("a"));
        TestCase.assertTrue(st.nextToken(":").equals("test"));
        TestCase.assertTrue(st.nextToken(":").equals("String"));
        try {
            st.nextToken(":");
            TestCase.fail("NoSuchElementException expected");
        } catch (NoSuchElementException e) {
            // expected
        }
        try {
            st.nextToken(null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }

    public void test_hasMoreElements_NPE() {
        StringTokenizer stringTokenizer = new StringTokenizer(new String(), ((String) (null)), true);
        try {
            stringTokenizer.hasMoreElements();
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }
        stringTokenizer = new StringTokenizer(new String(), ((String) (null)));
        try {
            stringTokenizer.hasMoreElements();
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    public void test_hasMoreTokens_NPE() {
        StringTokenizer stringTokenizer = new StringTokenizer(new String(), ((String) (null)), true);
        try {
            stringTokenizer.hasMoreTokens();
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }
        stringTokenizer = new StringTokenizer(new String(), ((String) (null)));
        try {
            stringTokenizer.hasMoreTokens();
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    public void test_nextElement_NPE() {
        StringTokenizer stringTokenizer = new StringTokenizer(new String(), ((String) (null)), true);
        try {
            stringTokenizer.nextElement();
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }
        stringTokenizer = new StringTokenizer(new String(), ((String) (null)));
        try {
            stringTokenizer.nextElement();
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    public void test_nextToken_NPE() {
        StringTokenizer stringTokenizer = new StringTokenizer(new String(), ((String) (null)), true);
        try {
            stringTokenizer.nextToken();
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }
        stringTokenizer = new StringTokenizer(new String(), ((String) (null)));
        try {
            stringTokenizer.nextToken();
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    public void test_nextTokenLjava_lang_String_NPE() {
        StringTokenizer stringTokenizer = new StringTokenizer(new String());
        try {
            stringTokenizer.nextToken(null);
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }
    }
}

