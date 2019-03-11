/**
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.harmony.tests.org.xml.sax.helpers;


import junit.framework.TestCase;
import org.xml.sax.helpers.AttributesImpl;


public class AttributesImplTest extends TestCase {
    private AttributesImpl empty = new AttributesImpl();

    private AttributesImpl multi = new AttributesImpl();

    public void testAttributesImpl() {
        TestCase.assertEquals(0, empty.getLength());
        TestCase.assertEquals(5, multi.getLength());
    }

    public void testAttributesImplAttributes() {
        // Ordinary case
        AttributesImpl ai = new AttributesImpl(empty);
        TestCase.assertEquals(0, ai.getLength());
        // Another ordinary case
        ai = new AttributesImpl(multi);
        TestCase.assertEquals(5, ai.getLength());
        // No Attributes
        try {
            ai = new AttributesImpl(null);
            TestCase.assertEquals(0, ai.getLength());
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    public void testGetLength() {
        AttributesImpl ai = new AttributesImpl(empty);
        TestCase.assertEquals(0, ai.getLength());
        ai = new AttributesImpl(multi);
        TestCase.assertEquals(5, ai.getLength());
        for (int i = 4; i >= 0; i--) {
            ai.removeAttribute(i);
            TestCase.assertEquals(i, ai.getLength());
        }
    }

    public void testGetURI() {
        // Ordinary cases
        TestCase.assertEquals("http://some.uri", multi.getURI(0));
        TestCase.assertEquals("http://some.uri", multi.getURI(1));
        TestCase.assertEquals("http://some.other.uri", multi.getURI(2));
        TestCase.assertEquals("", multi.getURI(3));
        TestCase.assertEquals("", multi.getURI(4));
        // Out of range
        TestCase.assertEquals(null, multi.getURI((-1)));
        TestCase.assertEquals(null, multi.getURI(5));
    }

    public void testGetLocalName() {
        // Ordinary cases
        TestCase.assertEquals("foo", multi.getLocalName(0));
        TestCase.assertEquals("bar", multi.getLocalName(1));
        TestCase.assertEquals("answer", multi.getLocalName(2));
        TestCase.assertEquals("gabbaHey", multi.getLocalName(3));
        TestCase.assertEquals("", multi.getLocalName(4));
        // Out of range
        TestCase.assertEquals(null, multi.getLocalName((-1)));
        TestCase.assertEquals(null, multi.getLocalName(5));
    }

    public void testGetQName() {
        // Ordinary cases
        TestCase.assertEquals("ns1:foo", multi.getQName(0));
        TestCase.assertEquals("ns1:bar", multi.getQName(1));
        TestCase.assertEquals("ns2:answer", multi.getQName(2));
        TestCase.assertEquals("", multi.getQName(3));
        TestCase.assertEquals("gabba:hey", multi.getQName(4));
        // Out of range
        TestCase.assertEquals(null, multi.getQName((-1)));
        TestCase.assertEquals(null, multi.getQName(5));
    }

    public void testGetTypeInt() {
        // Ordinary cases
        TestCase.assertEquals("string", multi.getType(0));
        TestCase.assertEquals("string", multi.getType(1));
        TestCase.assertEquals("int", multi.getType(2));
        TestCase.assertEquals("string", multi.getType(3));
        TestCase.assertEquals("string", multi.getType(4));
        // Out of range
        TestCase.assertEquals(null, multi.getType((-1)));
        TestCase.assertEquals(null, multi.getType(5));
    }

    public void testGetValueInt() {
        // Ordinary cases
        TestCase.assertEquals("abc", multi.getValue(0));
        TestCase.assertEquals("xyz", multi.getValue(1));
        TestCase.assertEquals("42", multi.getValue(2));
        TestCase.assertEquals("1-2-3-4", multi.getValue(3));
        TestCase.assertEquals("1-2-3-4", multi.getValue(4));
        // Out of range
        TestCase.assertEquals(null, multi.getValue((-1)));
        TestCase.assertEquals(null, multi.getValue(5));
    }

    public void testGetIndexStringString() {
        // Ordinary cases
        TestCase.assertEquals(0, multi.getIndex("http://some.uri", "foo"));
        TestCase.assertEquals(1, multi.getIndex("http://some.uri", "bar"));
        TestCase.assertEquals(2, multi.getIndex("http://some.other.uri", "answer"));
        // Not found
        TestCase.assertEquals((-1), multi.getIndex("john", "doe"));
        // null cases
        TestCase.assertEquals((-1), multi.getIndex("http://some.uri", null));
        TestCase.assertEquals((-1), multi.getIndex(null, "foo"));
    }

    public void testGetIndexString() {
        // Ordinary cases
        TestCase.assertEquals(0, multi.getIndex("ns1:foo"));
        TestCase.assertEquals(1, multi.getIndex("ns1:bar"));
        TestCase.assertEquals(2, multi.getIndex("ns2:answer"));
        TestCase.assertEquals(4, multi.getIndex("gabba:hey"));
        // Not found
        TestCase.assertEquals((-1), multi.getIndex("john:doe"));
        // null case
        TestCase.assertEquals((-1), multi.getIndex(null));
    }

    public void testGetTypeStringString() {
        // Ordinary cases
        TestCase.assertEquals("string", multi.getType("http://some.uri", "foo"));
        TestCase.assertEquals("string", multi.getType("http://some.uri", "bar"));
        TestCase.assertEquals("int", multi.getType("http://some.other.uri", "answer"));
        // Not found
        TestCase.assertEquals(null, multi.getType("john", "doe"));
        // null cases
        TestCase.assertEquals(null, multi.getType("http://some.uri", null));
        TestCase.assertEquals(null, multi.getType(null, "foo"));
    }

    public void testGetTypeString() {
        // Ordinary cases
        TestCase.assertEquals("string", multi.getType("ns1:foo"));
        TestCase.assertEquals("string", multi.getType("ns1:bar"));
        TestCase.assertEquals("int", multi.getType("ns2:answer"));
        TestCase.assertEquals("string", multi.getType("gabba:hey"));
        // Not found
        TestCase.assertEquals(null, multi.getType("john:doe"));
        // null case
        TestCase.assertEquals(null, multi.getType(null));
    }

    public void testGetValueStringString() {
        // Ordinary cases
        TestCase.assertEquals("abc", multi.getValue("http://some.uri", "foo"));
        TestCase.assertEquals("xyz", multi.getValue("http://some.uri", "bar"));
        TestCase.assertEquals("42", multi.getValue("http://some.other.uri", "answer"));
        // Not found
        TestCase.assertEquals(null, multi.getValue("john", "doe"));
        // null cases
        TestCase.assertEquals(null, multi.getValue("http://some.uri", null));
        TestCase.assertEquals(null, multi.getValue(null, "foo"));
    }

    public void testGetValueString() {
        // Ordinary cases
        TestCase.assertEquals("abc", multi.getValue("ns1:foo"));
        TestCase.assertEquals("xyz", multi.getValue("ns1:bar"));
        TestCase.assertEquals("42", multi.getValue("ns2:answer"));
        TestCase.assertEquals("1-2-3-4", multi.getValue("gabba:hey"));
        // Not found
        TestCase.assertEquals(null, multi.getValue("john:doe"));
        // null case
        TestCase.assertEquals(null, multi.getValue(null));
    }

    public void testClear() {
        TestCase.assertEquals(5, multi.getLength());
        multi.clear();
        TestCase.assertEquals(0, multi.getLength());
    }

    public void testSetAttributes() {
        // Ordinary cases
        AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute("http://yet.another.uri", "doe", "john:doe", "boolean", "false");
        attrs.setAttributes(empty);
        TestCase.assertEquals(0, attrs.getLength());
        attrs.setAttributes(multi);
        TestCase.assertEquals(multi.getLength(), attrs.getLength());
        for (int i = 0; i < (multi.getLength()); i++) {
            TestCase.assertEquals(multi.getURI(i), attrs.getURI(i));
            TestCase.assertEquals(multi.getLocalName(i), attrs.getLocalName(i));
            TestCase.assertEquals(multi.getQName(i), attrs.getQName(i));
            TestCase.assertEquals(multi.getType(i), attrs.getType(i));
            TestCase.assertEquals(multi.getValue(i), attrs.getValue(i));
        }
        // null case
        try {
            attrs.setAttributes(null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // Expected, but must be empty now
            TestCase.assertEquals(0, attrs.getLength());
        }
    }

    public void testAddAttribute() {
        // Ordinary case
        multi.addAttribute("http://yet.another.uri", "doe", "john:doe", "boolean", "false");
        TestCase.assertEquals("http://yet.another.uri", multi.getURI(5));
        TestCase.assertEquals("doe", multi.getLocalName(5));
        TestCase.assertEquals("john:doe", multi.getQName(5));
        TestCase.assertEquals("boolean", multi.getType(5));
        TestCase.assertEquals("false", multi.getValue(5));
        // Duplicate case
        multi.addAttribute("http://yet.another.uri", "doe", "john:doe", "boolean", "false");
        TestCase.assertEquals("http://yet.another.uri", multi.getURI(6));
        TestCase.assertEquals("doe", multi.getLocalName(6));
        TestCase.assertEquals("john:doe", multi.getQName(6));
        TestCase.assertEquals("boolean", multi.getType(6));
        TestCase.assertEquals("false", multi.getValue(6));
        // null case
        multi.addAttribute(null, null, null, null, null);
        TestCase.assertEquals(null, multi.getURI(7));
        TestCase.assertEquals(null, multi.getLocalName(7));
        TestCase.assertEquals(null, multi.getQName(7));
        TestCase.assertEquals(null, multi.getType(7));
        TestCase.assertEquals(null, multi.getValue(7));
    }

    public void testSetAttribute() {
        // Ordinary case
        multi.setAttribute(0, "http://yet.another.uri", "doe", "john:doe", "boolean", "false");
        TestCase.assertEquals("http://yet.another.uri", multi.getURI(0));
        TestCase.assertEquals("doe", multi.getLocalName(0));
        TestCase.assertEquals("john:doe", multi.getQName(0));
        TestCase.assertEquals("boolean", multi.getType(0));
        TestCase.assertEquals("false", multi.getValue(0));
        // null case
        multi.setAttribute(1, null, null, null, null, null);
        TestCase.assertEquals(null, multi.getURI(1));
        TestCase.assertEquals(null, multi.getLocalName(1));
        TestCase.assertEquals(null, multi.getQName(1));
        TestCase.assertEquals(null, multi.getType(1));
        TestCase.assertEquals(null, multi.getValue(1));
        // Out of range
        try {
            multi.setAttribute((-1), "http://yet.another.uri", "doe", "john:doe", "boolean", "false");
            TestCase.fail("ArrayIndexOutOfBoundsException expected");
        } catch (ArrayIndexOutOfBoundsException e) {
            // Expected
        }
        try {
            multi.setAttribute(5, "http://yet.another.uri", "doe", "john:doe", "boolean", "false");
            TestCase.fail("ArrayIndexOutOfBoundsException expected");
        } catch (ArrayIndexOutOfBoundsException e) {
            // Expected
        }
    }

    public void testRemoveAttribute() {
        // Ordinary case
        multi.removeAttribute(0);
        TestCase.assertEquals("http://some.uri", multi.getURI(0));
        TestCase.assertEquals("bar", multi.getLocalName(0));
        TestCase.assertEquals("ns1:bar", multi.getQName(0));
        TestCase.assertEquals("string", multi.getType(0));
        TestCase.assertEquals("xyz", multi.getValue(0));
        // Out of range
        try {
            multi.removeAttribute((-1));
            TestCase.fail("ArrayIndexOutOfBoundsException expected");
        } catch (ArrayIndexOutOfBoundsException e) {
            // Expected
        }
        try {
            multi.removeAttribute(4);
            TestCase.fail("ArrayIndexOutOfBoundsException expected");
        } catch (ArrayIndexOutOfBoundsException e) {
            // Expected
        }
    }

    public void testSetURI() {
        // Ordinary case
        multi.setURI(0, "http://yet.another.uri");
        TestCase.assertEquals("http://yet.another.uri", multi.getURI(0));
        // null case
        multi.setURI(1, null);
        TestCase.assertEquals(null, multi.getURI(1));
        // Out of range
        try {
            multi.setURI((-1), "http://yet.another.uri");
            TestCase.fail("ArrayIndexOutOfBoundsException expected");
        } catch (ArrayIndexOutOfBoundsException e) {
            // Expected
        }
        try {
            multi.setURI(5, "http://yet.another.uri");
            TestCase.fail("ArrayIndexOutOfBoundsException expected");
        } catch (ArrayIndexOutOfBoundsException e) {
            // Expected
        }
    }

    public void testSetLocalName() {
        // Ordinary case
        multi.setLocalName(0, "john");
        TestCase.assertEquals("john", multi.getLocalName(0));
        // null case
        multi.setLocalName(1, null);
        TestCase.assertEquals(null, multi.getLocalName(1));
        // Out of range
        try {
            multi.setLocalName((-1), "john");
            TestCase.fail("ArrayIndexOutOfBoundsException expected");
        } catch (ArrayIndexOutOfBoundsException e) {
            // Expected
        }
        try {
            multi.setLocalName(5, "john");
            TestCase.fail("ArrayIndexOutOfBoundsException expected");
        } catch (ArrayIndexOutOfBoundsException e) {
            // Expected
        }
    }

    public void testSetQName() {
        // Ordinary case
        multi.setQName(0, "john:doe");
        TestCase.assertEquals("john:doe", multi.getQName(0));
        // null case
        multi.setQName(1, null);
        TestCase.assertEquals(null, multi.getQName(1));
        // Out of range
        try {
            multi.setQName((-1), "john:doe");
            TestCase.fail("ArrayIndexOutOfBoundsException expected");
        } catch (ArrayIndexOutOfBoundsException e) {
            // Expected
        }
        try {
            multi.setQName(5, "john:doe");
            TestCase.fail("ArrayIndexOutOfBoundsException expected");
        } catch (ArrayIndexOutOfBoundsException e) {
            // Expected
        }
    }

    public void testSetType() {
        // Ordinary case
        multi.setType(0, "float");
        TestCase.assertEquals("float", multi.getType(0));
        // null case
        multi.setType(1, null);
        TestCase.assertEquals(null, multi.getType(1));
        // Out of range
        try {
            multi.setType((-1), "float");
            TestCase.fail("ArrayIndexOutOfBoundsException expected");
        } catch (ArrayIndexOutOfBoundsException e) {
            // Expected
        }
        try {
            multi.setType(5, "float");
            TestCase.fail("ArrayIndexOutOfBoundsException expected");
        } catch (ArrayIndexOutOfBoundsException e) {
            // Expected
        }
    }

    public void testSetValue() {
        // Ordinary case
        multi.setValue(0, "too much");
        TestCase.assertEquals("too much", multi.getValue(0));
        // null case
        multi.setValue(1, null);
        TestCase.assertEquals(null, multi.getValue(1));
        // Out of range
        try {
            multi.setValue((-1), "too much");
            TestCase.fail("ArrayIndexOutOfBoundsException expected");
        } catch (ArrayIndexOutOfBoundsException e) {
            // Expected
        }
        try {
            multi.setValue(5, "too much");
            TestCase.fail("ArrayIndexOutOfBoundsException expected");
        } catch (ArrayIndexOutOfBoundsException e) {
            // Expected
        }
    }
}

