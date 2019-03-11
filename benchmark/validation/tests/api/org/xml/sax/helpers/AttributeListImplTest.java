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
package tests.api.org.xml.sax.helpers;


import junit.framework.TestCase;
import org.xml.sax.helpers.AttributeListImpl;


@SuppressWarnings("deprecation")
public class AttributeListImplTest extends TestCase {
    private AttributeListImpl empty = new AttributeListImpl();

    private AttributeListImpl multi = new AttributeListImpl();

    public void testAttributeListImpl() {
        TestCase.assertEquals(0, empty.getLength());
        TestCase.assertEquals(3, multi.getLength());
    }

    public void testAttributeListImplAttributeList() {
        // Ordinary case
        AttributeListImpl ai = new AttributeListImpl(empty);
        TestCase.assertEquals(0, ai.getLength());
        // Another ordinary case
        ai = new AttributeListImpl(multi);
        TestCase.assertEquals(3, ai.getLength());
        // No Attributes
        try {
            ai = new AttributeListImpl(null);
            TestCase.assertEquals(0, ai.getLength());
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    public void testSetAttributeList() {
        // Ordinary cases
        AttributeListImpl attrs = new AttributeListImpl();
        attrs.addAttribute("doe", "boolean", "false");
        attrs.setAttributeList(empty);
        TestCase.assertEquals(0, attrs.getLength());
        attrs.setAttributeList(multi);
        TestCase.assertEquals(multi.getLength(), attrs.getLength());
        for (int i = 0; i < (multi.getLength()); i++) {
            TestCase.assertEquals(multi.getName(i), attrs.getName(i));
            TestCase.assertEquals(multi.getType(i), attrs.getType(i));
            TestCase.assertEquals(multi.getValue(i), attrs.getValue(i));
        }
        // null case
        try {
            attrs.setAttributeList(null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // Expected, must still have old elements
            TestCase.assertEquals(3, attrs.getLength());
        }
    }

    public void testAddAttribute() {
        // Ordinary case
        multi.addAttribute("doe", "boolean", "false");
        TestCase.assertEquals("doe", multi.getName(3));
        TestCase.assertEquals("boolean", multi.getType(3));
        TestCase.assertEquals("false", multi.getValue(3));
        // Duplicate case
        multi.addAttribute("doe", "boolean", "false");
        TestCase.assertEquals("doe", multi.getName(4));
        TestCase.assertEquals("boolean", multi.getType(4));
        TestCase.assertEquals("false", multi.getValue(4));
        // null case
        multi.addAttribute(null, null, null);
        TestCase.assertEquals(null, multi.getName(5));
        TestCase.assertEquals(null, multi.getType(5));
        TestCase.assertEquals(null, multi.getValue(5));
    }

    public void testRemoveAttribute() {
        // Ordinary case
        multi.removeAttribute("foo");
        TestCase.assertEquals("bar", multi.getName(0));
        TestCase.assertEquals("string", multi.getType(0));
        TestCase.assertEquals("xyz", multi.getValue(0));
        // Unknown attribute
        multi.removeAttribute("john");
        TestCase.assertEquals(2, multi.getLength());
        // null case
        multi.removeAttribute(null);
        TestCase.assertEquals(2, multi.getLength());
    }

    public void testClear() {
        TestCase.assertEquals(3, multi.getLength());
        multi.clear();
        TestCase.assertEquals(0, multi.getLength());
    }

    public void testGetLength() {
        AttributeListImpl ai = new AttributeListImpl(empty);
        TestCase.assertEquals(0, ai.getLength());
        ai = new AttributeListImpl(multi);
        TestCase.assertEquals(3, ai.getLength());
        for (int i = 2; i >= 0; i--) {
            ai.removeAttribute(ai.getName(i));
            TestCase.assertEquals(i, ai.getLength());
        }
    }

    public void testGetName() {
        // Ordinary cases
        TestCase.assertEquals("foo", multi.getName(0));
        TestCase.assertEquals("bar", multi.getName(1));
        TestCase.assertEquals("answer", multi.getName(2));
        // Out of range
        TestCase.assertEquals(null, multi.getName((-1)));
        TestCase.assertEquals(null, multi.getName(3));
    }

    public void testGetTypeInt() {
        // Ordinary cases
        TestCase.assertEquals("string", multi.getType(0));
        TestCase.assertEquals("string", multi.getType(1));
        TestCase.assertEquals("int", multi.getType(2));
        // Out of range
        TestCase.assertEquals(null, multi.getType((-1)));
        TestCase.assertEquals(null, multi.getType(3));
    }

    public void testGetValueInt() {
        // Ordinary cases
        TestCase.assertEquals("abc", multi.getValue(0));
        TestCase.assertEquals("xyz", multi.getValue(1));
        TestCase.assertEquals("42", multi.getValue(2));
        // Out of range
        TestCase.assertEquals(null, multi.getValue((-1)));
        TestCase.assertEquals(null, multi.getValue(5));
    }

    public void testGetTypeString() {
        // Ordinary cases
        TestCase.assertEquals("string", multi.getType("foo"));
        TestCase.assertEquals("string", multi.getType("bar"));
        TestCase.assertEquals("int", multi.getType("answer"));
        // Not found
        TestCase.assertEquals(null, multi.getType("john"));
        // null case
        TestCase.assertEquals(null, multi.getType(null));
    }

    public void testGetValueString() {
        // Ordinary cases
        TestCase.assertEquals("abc", multi.getValue("foo"));
        TestCase.assertEquals("xyz", multi.getValue("bar"));
        TestCase.assertEquals("42", multi.getValue("answer"));
        // Not found
        TestCase.assertEquals(null, multi.getValue("john"));
        // null case
        TestCase.assertEquals(null, multi.getValue(null));
    }
}

