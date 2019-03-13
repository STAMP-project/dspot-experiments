/**
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.harmony.regex.tests.java.util.regex;


import SerializationTest.THROWABLE_COMPARATOR;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import junit.framework.TestCase;
import org.apache.harmony.testframework.serialization.SerializationTest;
import org.apache.harmony.testframework.serialization.SerializationTest.SerializableAssert;


public class PatternSyntaxExceptionTest extends TestCase {
    public void testPatternSyntaxException() {
        // Normal case
        PatternSyntaxException e = new PatternSyntaxException("Foo", "Bar", 0);
        TestCase.assertEquals("Foo", e.getDescription());
        TestCase.assertEquals("Bar", e.getPattern());
        TestCase.assertEquals(0, e.getIndex());
        String s = e.getMessage();
        TestCase.assertTrue(s.contains("Foo"));
        TestCase.assertTrue(s.contains("Bar"));
        TestCase.assertTrue(s.contains("0"));
        // No description specified
        e = new PatternSyntaxException(null, "Bar", 0);
        TestCase.assertEquals(null, e.getDescription());
        TestCase.assertEquals("Bar", e.getPattern());
        TestCase.assertEquals(0, e.getIndex());
        s = e.getMessage();
        TestCase.assertFalse(s.contains("Foo"));
        TestCase.assertTrue(s.contains("Bar"));
        TestCase.assertTrue(s.contains("0"));
        // No pattern specified
        e = new PatternSyntaxException("Foo", null, 0);
        TestCase.assertEquals("Foo", e.getDescription());
        TestCase.assertEquals(null, e.getPattern());
        TestCase.assertEquals(0, e.getIndex());
        s = e.getMessage();
        TestCase.assertTrue(s.contains("Foo"));
        TestCase.assertFalse(s.contains("Bar"));
        TestCase.assertTrue(s.contains("0"));
        // Neither description nor pattern specified
        e = new PatternSyntaxException(null, null, 0);
        TestCase.assertEquals(null, e.getDescription());
        TestCase.assertEquals(null, e.getPattern());
        TestCase.assertEquals(0, e.getIndex());
        s = e.getMessage();
        TestCase.assertFalse(s.contains("Foo"));
        TestCase.assertFalse(s.contains("Bar"));
        TestCase.assertTrue(s.contains("0"));
        // No index specified
        e = new PatternSyntaxException("Foo", "Bar", (-1));
        TestCase.assertEquals((-1), e.getIndex());
        s = e.getMessage();
        TestCase.assertFalse(s.contains("^"));
        // No pattern, but index specified
        e = new PatternSyntaxException("Foo", null, 0);
        TestCase.assertEquals(0, e.getIndex());
        s = e.getMessage();
        TestCase.assertFalse(s.contains("^"));
    }

    public void testCase() {
        String regex = "(";
        try {
            Pattern.compile(regex);
            TestCase.fail("PatternSyntaxException expected");
        } catch (PatternSyntaxException e) {
            TestCase.assertEquals(1, e.getIndex());
            TestCase.assertEquals(regex, e.getPattern());
        }
    }

    public void testCase2() {
        String regex = "[4-";
        try {
            Pattern.compile(regex);
            TestCase.fail("PatternSyntaxException expected");
        } catch (PatternSyntaxException e) {
            TestCase.assertEquals(3, e.getIndex());
            TestCase.assertEquals(regex, e.getPattern());
        }
    }

    // Regression test for HARMONY-3787
    public void test_objectStreamField() {
        ObjectStreamClass objectStreamClass = ObjectStreamClass.lookup(PatternSyntaxException.class);
        TestCase.assertNotNull(objectStreamClass.getField("desc"));
    }

    public void testSerializationCompatibility() throws Exception {
        PatternSyntaxException object = new PatternSyntaxException("TESTDESC", "TESTREGEX", 3);
        SerializationTest.verifyGolden(this, object, PatternSyntaxExceptionTest.PATTERNSYNTAXEXCEPTION_COMPARATOR);
    }

    public void testSerializationSelf() throws Exception {
        PatternSyntaxException object = new PatternSyntaxException("TESTDESC", "TESTREGEX", 3);
        SerializationTest.verifySelf(object, PatternSyntaxExceptionTest.PATTERNSYNTAXEXCEPTION_COMPARATOR);
    }

    private static final SerializableAssert PATTERNSYNTAXEXCEPTION_COMPARATOR = new SerializableAssert() {
        public void assertDeserialized(Serializable initial, Serializable deserialized) {
            // do common checks for all throwable objects
            THROWABLE_COMPARATOR.assertDeserialized(initial, deserialized);
            PatternSyntaxException initPatternSyntaxException = ((PatternSyntaxException) (initial));
            PatternSyntaxException dserPatternSyntaxException = ((PatternSyntaxException) (deserialized));
            // verify fields
            TestCase.assertEquals(initPatternSyntaxException.getDescription(), dserPatternSyntaxException.getDescription());
            TestCase.assertEquals(initPatternSyntaxException.getPattern(), dserPatternSyntaxException.getPattern());
            TestCase.assertEquals(initPatternSyntaxException.getIndex(), dserPatternSyntaxException.getIndex());
        }
    };
}

