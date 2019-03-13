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
package libcore.java.text;


import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.CharacterIterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.WeakHashMap;
import junit.framework.TestCase;

import static java.text.AttributedCharacterIterator.Attribute.INPUT_METHOD_SEGMENT;
import static java.text.AttributedCharacterIterator.Attribute.LANGUAGE;
import static java.text.AttributedCharacterIterator.Attribute.READING;


public class OldAttributedStringTest extends TestCase {
    public void test_ConstructorLAttributedCharacterIterator_1() {
        String testString = "Test string";
        AttributedString attrString = new AttributedString(testString);
        AttributedCharacterIterator iter = attrString.getIterator();
        AttributedString attrString2 = new AttributedString(iter);
        OldAttributedStringTest.assertEqualString("String must match!", testString, attrString2);
    }

    public void test_ConstructorLAttributedCharacterIterator_2() {
        String testString = "Test string";
        AttributedString attrString = new AttributedString(testString);
        AttributedCharacterIterator iter = attrString.getIterator();
        AttributedString attrString2 = new AttributedString(iter, 2, 7);
        OldAttributedStringTest.assertEqualString("String must match!", "st st", attrString2);
    }

    /**
     * java.text.AttributedString#AttributedString(AttributedCharacterIterator,
     *        int, int) Test of method
     *        java.text.AttributedString#AttributedString(AttributedCharacterIterator,
     *        int, int). Case 1: Try to consruct AttributedString. Case 2: Try
     *        to consruct AttributedString using incorrect beginIndex. Case 3:
     *        Try to consruct AttributedString using incorrect endIndex.
     */
    public void test_ConstructorLAttributedCharacterIteratorII() {
        // Regression for HARMONY-1355
        // case 1: Try to consruct AttributedString.
        try {
            new AttributedString(new OldAttributedStringTest.testAttributedCharacterIterator(), 0, 0);
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception " + (e.toString())));
        }
        // case 2: Try to consruct AttributedString using incorrect beginIndex.
        try {
            new AttributedString(new OldAttributedStringTest.testAttributedCharacterIterator(), (-1), 0);
            TestCase.fail("Expected IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException e) {
            // expected
        }
        // case 3: Try to consruct AttributedString using incorrect endIndex.
        try {
            new AttributedString(new OldAttributedStringTest.testAttributedCharacterIterator(), 0, (-1));
            TestCase.fail("Expected IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void test_ConstructorLAttributedCharacterIterator_3() {
        String testString = "Test string";
        AttributedString attrString = new AttributedString(testString);
        AttributedCharacterIterator iter = attrString.getIterator();
        AttributedString attrString2;
        attrString2 = new AttributedString(iter, 2, 7, new AttributedCharacterIterator.Attribute[]{  });
        OldAttributedStringTest.assertEqualString("String must match!", "st st", attrString2);
        attrString2 = new AttributedString(iter, 2, 7, null);
        OldAttributedStringTest.assertEqualString("String must match!", "st st", attrString2);
    }

    /**
     * java.text.AttributedString#AttributedString(AttributedCharacterIterator,
     *        int, int, AttributedCharacterIterator.Attribute[]) Test of method
     *        java.text.AttributedString#AttributedString(AttributedCharacterIterator,
     *        int, int, AttributedCharacterIterator.Attribute[]). Case 1: Try to
     *        consruct AttributedString. Case 2: Try to consruct
     *        AttributedString using incorrect beginIndex. Case 3: Try to
     *        consruct AttributedString using incorrect endIndex. Case 4: Try to
     *        consruct AttributedString using specified attributes.
     */
    public void test_ConstructorLAttributedCharacterIteratorII$Ljava_text_AttributedCharacterIterator$Attribute() {
        // case 1: Try to consruct AttributedString.
        try {
            new AttributedString(new OldAttributedStringTest.testAttributedCharacterIterator(), 0, 0, null);
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception " + (e.toString())));
        }
        // case 2: Try to consruct AttributedString using incorrect beginIndex.
        try {
            new AttributedString(new OldAttributedStringTest.testAttributedCharacterIterator(), (-1), 0, null);
            TestCase.fail("Expected IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException e) {
            // expected
        }
        // case 3: Try to consruct AttributedString using incorrect endIndex.
        try {
            new AttributedString(new OldAttributedStringTest.testAttributedCharacterIterator(), 0, (-1), null);
            TestCase.fail("Expected IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException e) {
            // expected
        }
        // case 4: Try to consruct AttributedString using specified attributes.
        try {
            AttributedCharacterIterator.Attribute[] attributes = new AttributedCharacterIterator.Attribute[1];
            attributes[0] = new OldAttributedStringTest.TestAttributedCharacterIteratorAttribute("test");
            new AttributedString(new OldAttributedStringTest.testAttributedCharacterIterator(), 0, 0, attributes);
        } catch (IllegalArgumentException e) {
            TestCase.fail(("Unexpected expected " + (e.toString())));
        }
    }

    /**
     * java.text.AttributedString#AttributedString(AttributedCharacterIterator,
     *        int, int, Map<? extends AttributedCharacterIterator.Attribute,?>)
     *        Test of method
     *        java.text.AttributedString#AttributedString(AttributedCharacterIterator,
     *        int, int, Map<? extends
     *        AttributedCharacterIterator.Attribute,?>). Case 1: Try to
     *        construct AttributedString. Case 2: Try to construct
     *        AttributedString using 0-length text and not an empty Map
     *        attributes.
     */
    public void test_ConstructorLjava_lang_StringLjava_util_Map() {
        String test = "Test string";
        // case 1: Try to construct AttributedString
        try {
            AttributedString attrString = new AttributedString(test, new WeakHashMap<AttributedCharacterIterator.Attribute, String>());
            AttributedCharacterIterator it = attrString.getIterator();
            StringBuffer buf = new StringBuffer();
            buf.append(it.first());
            char ch;
            while ((ch = it.next()) != (CharacterIterator.DONE))
                buf.append(ch);

            TestCase.assertTrue(("Wrong string: " + buf), buf.toString().equals(test));
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception " + (e.toString())));
        }
        // case 2: Try to construct AttributedString using 0-length text and
        // not an empty Map attributes.
        try {
            Map<AttributedCharacterIterator.Attribute, String> whm = new WeakHashMap<AttributedCharacterIterator.Attribute, String>();
            whm.put(new OldAttributedStringTest.TestAttributedCharacterIteratorAttribute("test"), "value");
            new AttributedString("", whm);
            TestCase.fail("Expected IllegalArgumentException was not thrown");
        } catch (Exception e) {
            // expected
        }
    }

    private class TestAttributedCharacterIteratorAttribute extends AttributedCharacterIterator.Attribute {
        private static final long serialVersionUID = -2917613373935785179L;

        public TestAttributedCharacterIteratorAttribute(String name) {
            super(name);
        }
    }

    private class testAttributedCharacterIterator implements AttributedCharacterIterator {
        public Set getAllAttributeKeys() {
            return null;
        }

        public Object getAttribute(AttributedCharacterIterator.Attribute p) {
            return null;
        }

        public Map getAttributes() {
            return null;
        }

        public int getRunLimit(Set p) {
            return 0;
        }

        public int getRunLimit(AttributedCharacterIterator.Attribute p) {
            return 0;
        }

        public int getRunLimit() {
            return 0;
        }

        public int getRunStart(Set p) {
            return 0;
        }

        public int getRunStart(AttributedCharacterIterator.Attribute p) {
            return 0;
        }

        public int getRunStart() {
            return 0;
        }

        public Object clone() {
            return null;
        }

        public int getIndex() {
            return 0;
        }

        public int getEndIndex() {
            return 0;
        }

        public int getBeginIndex() {
            return 0;
        }

        public char setIndex(int p) {
            return 'a';
        }

        public char previous() {
            return 'a';
        }

        public char next() {
            return 'a';
        }

        public char current() {
            return 'a';
        }

        public char last() {
            return 'a';
        }

        public char first() {
            return 'a';
        }
    }

    public void test_addAttributeLjava_text_AttributedCharacterIterator$AttributeLjava_lang_ObjectII() {
        AttributedString as = new AttributedString("test");
        as.addAttribute(LANGUAGE, "a", 2, 3);
        AttributedCharacterIterator it = as.getIterator();
        TestCase.assertEquals("non-null value limit", 2, it.getRunLimit(LANGUAGE));
        as = new AttributedString("test");
        as.addAttribute(LANGUAGE, null, 2, 3);
        it = as.getIterator();
        TestCase.assertEquals("null value limit", 4, it.getRunLimit(LANGUAGE));
        try {
            as = new AttributedString("test");
            as.addAttribute(LANGUAGE, null, (-1), 3);
            TestCase.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        // regression for Harmony-1244
        as = new AttributedString("123", new WeakHashMap());
        try {
            as.addAttribute(null, new TreeSet(), 0, 1);
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            as.addAttribute(null, new TreeSet(), (-1), 1);
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * java.text.AttributedString.addAttribute(AttributedCharacterIterator,
     *        Object)
     */
    public void test_addAttributeLjava_text_AttributedCharacterIterator$AttributeLjava_lang_Object() {
        // regression for Harmony-1244
        AttributedString as = new AttributedString("123", new WeakHashMap());
        as.addAttribute(LANGUAGE, "english");
        as.addAttribute(INPUT_METHOD_SEGMENT, "input method");
        as.addAttribute(READING, "reading");
        try {
            as.addAttribute(null, new TreeSet());
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            as.addAttribute(null, null);
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * java.text.AttributedString#addAttributes(Map<? extends
     *        AttributedCharacterIterator.Attribute,?>, int, int) Tests of
     *        method java.text.AttributedString#addAttributes(Map<? extends
     *        AttributedCharacterIterator.Attribute,?>, int, int). Case 1: Try
     *        to add attributes to AttributesString. Case 2: Try to add
     *        null-attributes to AttributesString. Case 3: Try to add attributes
     *        to AttributesString using incorrect index.
     */
    public void test_addAttributesLjava_util_MapII() {
        AttributedString as = new AttributedString("test");
        Map<AttributedCharacterIterator.Attribute, String> whm = new WeakHashMap<AttributedCharacterIterator.Attribute, String>();
        // case 1: Try to add attributes to AttributesString.
        try {
            whm.put(new OldAttributedStringTest.TestAttributedCharacterIteratorAttribute("test1"), "value1");
            whm.put(new OldAttributedStringTest.TestAttributedCharacterIteratorAttribute("test2"), "value2");
            whm.put(new OldAttributedStringTest.TestAttributedCharacterIteratorAttribute("test3"), "value3");
            as.addAttributes(whm, 0, 3);
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception " + (e.toString())));
        }
        // case 2: Try to add null-attributes to AttributesString.
        try {
            as.addAttributes(null, 0, 3);
            TestCase.fail("Expected NullPointerException was not thrown");
        } catch (NullPointerException e) {
            // expected
        }
        // case 3: Try to add attributes to AttributesString using incorrect
        // index.
        try {
            as.addAttributes(whm, 0, 0);
            TestCase.fail("Expected IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    /**
     * java.text.AttributedString#getIterator() Test of method
     *        java.text.AttributedString#getIterator().
     */
    public void test_getIterator() {
        String test = "Test string";
        try {
            AttributedString attrString = new AttributedString(test);
            AttributedCharacterIterator it = attrString.getIterator();
            TestCase.assertEquals("Incorrect iteration on AttributedString", it.first(), test.charAt(0));
        } catch (Exception e) {
            TestCase.fail(("Unexpected exceptiption " + (e.toString())));
        }
    }

    /**
     * java.text.AttributedString#getIterator(AttributedCharacterIterator.Attribute[])
     *        Test of method
     *        java.text.AttributedString#getIterator(AttributedCharacterIterator.Attribute[]).
     */
    public void test_getIterator$Ljava_text_AttributedCharacterIterator$Attribute() {
        String test = "Test string";
        try {
            Map<AttributedCharacterIterator.Attribute, String> hm = new HashMap<AttributedCharacterIterator.Attribute, String>();
            AttributedCharacterIterator.Attribute[] aci = new AttributedCharacterIterator.Attribute[3];
            aci[0] = new OldAttributedStringTest.TestAttributedCharacterIteratorAttribute("att1");
            aci[1] = new OldAttributedStringTest.TestAttributedCharacterIteratorAttribute("att2");
            aci[2] = new OldAttributedStringTest.TestAttributedCharacterIteratorAttribute("att3");
            hm.put(aci[0], "value1");
            hm.put(aci[1], "value2");
            AttributedString attrString = new AttributedString(test, hm);
            AttributedCharacterIterator it = attrString.getIterator(aci);
            TestCase.assertTrue("Incorrect iteration on AttributedString", it.getAttribute(aci[0]).equals("value1"));
            TestCase.assertTrue("Incorrect iteration on AttributedString", it.getAttribute(aci[1]).equals("value2"));
            TestCase.assertTrue("Incorrect iteration on AttributedString", ((it.getAttribute(aci[2])) == null));
        } catch (Exception e) {
            TestCase.fail(("Unexpected exceptiption " + (e.toString())));
        }
    }

    /**
     * java.text.AttributedString#getIterator(AttributedCharacterIterator.Attribute[],
     *        int, int) Test of method
     *        java.text.AttributedString#getIterator(AttributedCharacterIterator.Attribute[],
     *        int, int).
     */
    public void test_getIterator$Ljava_text_AttributedCharacterIterator$AttributeII() {
        String test = "Test string";
        try {
            Map<AttributedCharacterIterator.Attribute, String> hm = new HashMap<AttributedCharacterIterator.Attribute, String>();
            AttributedCharacterIterator.Attribute[] aci = new AttributedCharacterIterator.Attribute[3];
            aci[0] = new OldAttributedStringTest.TestAttributedCharacterIteratorAttribute("att1");
            aci[1] = new OldAttributedStringTest.TestAttributedCharacterIteratorAttribute("att2");
            aci[2] = new OldAttributedStringTest.TestAttributedCharacterIteratorAttribute("att3");
            hm.put(aci[0], "value1");
            hm.put(aci[1], "value2");
            AttributedString attrString = new AttributedString(test);
            attrString.addAttributes(hm, 2, 4);
            AttributedCharacterIterator it = attrString.getIterator(aci, 1, 5);
            TestCase.assertTrue("Incorrect iteration on AttributedString", ((it.getAttribute(aci[0])) == null));
            TestCase.assertTrue("Incorrect iteration on AttributedString", ((it.getAttribute(aci[1])) == null));
            TestCase.assertTrue("Incorrect iteration on AttributedString", ((it.getAttribute(aci[2])) == null));
            it.next();
            TestCase.assertTrue("Incorrect iteration on AttributedString", it.getAttribute(aci[0]).equals("value1"));
            TestCase.assertTrue("Incorrect iteration on AttributedString", it.getAttribute(aci[1]).equals("value2"));
            TestCase.assertTrue("Incorrect iteration on AttributedString", ((it.getAttribute(aci[2])) == null));
            try {
                attrString.getIterator(aci, (-1), 5);
                TestCase.fail("IllegalArgumentException is not thrown.");
            } catch (IllegalArgumentException iae) {
                // expected
            }
            try {
                attrString.getIterator(aci, 6, 5);
                TestCase.fail("IllegalArgumentException is not thrown.");
            } catch (IllegalArgumentException iae) {
                // expected
            }
            try {
                attrString.getIterator(aci, 3, 2);
                TestCase.fail("IllegalArgumentException is not thrown.");
            } catch (IllegalArgumentException iae) {
                // expected
            }
        } catch (Exception e) {
            TestCase.fail(("Unexpected exceptiption " + (e.toString())));
        }
    }
}

