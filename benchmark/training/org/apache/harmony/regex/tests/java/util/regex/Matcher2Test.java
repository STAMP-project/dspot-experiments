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
package org.apache.harmony.regex.tests.java.util.regex;


import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import junit.framework.TestCase;


/**
 * Tests Matcher methods
 */
public class Matcher2Test extends TestCase {
    public void test_toString() {
        Pattern p = Pattern.compile("foo");
        Matcher m = p.matcher("bar");
        TestCase.assertNotNull(m.toString());
    }

    public void testErrorConditions() throws PatternSyntaxException {
        // Test match cursors in absence of a match
        Pattern p = Pattern.compile("foo");
        Matcher m = p.matcher("bar");
        TestCase.assertFalse(m.matches());
        try {
            m.start();
            TestCase.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
        }
        try {
            m.end();
            TestCase.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
        }
        try {
            m.group();
            TestCase.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
        }
        try {
            m.start(1);
            TestCase.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
        }
        try {
            m.end(1);
            TestCase.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
        }
        try {
            m.group(1);
            TestCase.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
        }
        // regression test for HARMONY-2418
        try {
            m.usePattern(null);
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // PASSED
        }
    }

    public void testErrorConditions2() throws PatternSyntaxException {
        // Test match cursors in absence of a match
        Pattern p = Pattern.compile("(foo[0-9])(bar[a-z])");
        Matcher m = p.matcher("foo1barzfoo2baryfoozbar5");
        TestCase.assertTrue(m.find());
        TestCase.assertEquals(0, m.start());
        TestCase.assertEquals(8, m.end());
        TestCase.assertEquals(0, m.start(1));
        TestCase.assertEquals(4, m.end(1));
        TestCase.assertEquals(4, m.start(2));
        TestCase.assertEquals(8, m.end(2));
        try {
            m.start(3);
            TestCase.fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            m.end(3);
            TestCase.fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            m.group(3);
            TestCase.fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            m.start((-1));
            TestCase.fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            m.end((-1));
            TestCase.fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            m.group((-1));
            TestCase.fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException e) {
        }
        TestCase.assertTrue(m.find());
        TestCase.assertEquals(8, m.start());
        TestCase.assertEquals(16, m.end());
        TestCase.assertEquals(8, m.start(1));
        TestCase.assertEquals(12, m.end(1));
        TestCase.assertEquals(12, m.start(2));
        TestCase.assertEquals(16, m.end(2));
        try {
            m.start(3);
            TestCase.fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            m.end(3);
            TestCase.fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            m.group(3);
            TestCase.fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            m.start((-1));
            TestCase.fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            m.end((-1));
            TestCase.fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            m.group((-1));
            TestCase.fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException e) {
        }
        TestCase.assertFalse(m.find());
        try {
            m.start(3);
            TestCase.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
        }
        try {
            m.end(3);
            TestCase.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
        }
        try {
            m.group(3);
            TestCase.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
        }
        try {
            m.start((-1));
            TestCase.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
        }
        try {
            m.end((-1));
            TestCase.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
        }
        try {
            m.group((-1));
            TestCase.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
        }
    }

    /* Regression test for HARMONY-997 */
    public void testReplacementBackSlash() {
        String str = "replace me";
        String replacedString = "me";
        String substitutionString = "\\";
        Pattern pat = Pattern.compile(replacedString);
        Matcher mat = pat.matcher(str);
        try {
            String res = mat.replaceAll(substitutionString);
            TestCase.fail(("IndexOutOfBoundsException should be thrown - " + res));
        } catch (Exception e) {
        }
    }
}

