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
package tests.api.org.xml.sax.ext;


import junit.framework.TestCase;
import org.xml.sax.ext.Attributes2Impl;
import org.xml.sax.helpers.AttributesImpl;


public class Attributes2ImplTest extends TestCase {
    // Note: The original SAX2 implementation of Attributes2Impl is
    // severely broken. Thus all of these tests will probably fail
    // unless the Android implementation of the class gets fixed.
    private Attributes2Impl empty = new Attributes2Impl();

    private Attributes2Impl multi = new Attributes2Impl();

    private Attributes2Impl cdata = new Attributes2Impl();

    public void testSetAttributes() {
        // Ordinary case with Attributes2Impl
        Attributes2Impl attrs = new Attributes2Impl();
        attrs.addAttribute("", "", "john", "string", "doe");
        attrs.setAttributes(empty);
        TestCase.assertEquals(0, attrs.getLength());
        attrs.setAttributes(multi);
        for (int i = 0; i < (multi.getLength()); i++) {
            TestCase.assertEquals(multi.getURI(i), attrs.getURI(i));
            TestCase.assertEquals(multi.getLocalName(i), attrs.getLocalName(i));
            TestCase.assertEquals(multi.getQName(i), attrs.getQName(i));
            TestCase.assertEquals(multi.getType(i), attrs.getType(i));
            TestCase.assertEquals(multi.getValue(i), attrs.getValue(i));
            TestCase.assertEquals(multi.isDeclared(i), attrs.isDeclared(i));
            TestCase.assertEquals(multi.isSpecified(i), attrs.isSpecified(i));
        }
        attrs.setAttributes(empty);
        TestCase.assertEquals(0, attrs.getLength());
        // Ordinary case with AttributesImpl
        attrs.setAttributes(new AttributesImpl(multi));
        TestCase.assertEquals(multi.getLength(), attrs.getLength());
        for (int i = 0; i < (multi.getLength()); i++) {
            TestCase.assertEquals(multi.getURI(i), attrs.getURI(i));
            TestCase.assertEquals(multi.getLocalName(i), attrs.getLocalName(i));
            TestCase.assertEquals(multi.getQName(i), attrs.getQName(i));
            TestCase.assertEquals(multi.getType(i), attrs.getType(i));
            TestCase.assertEquals(multi.getValue(i), attrs.getValue(i));
            TestCase.assertEquals(true, attrs.isDeclared(i));
            TestCase.assertEquals(true, attrs.isSpecified(i));
        }
        // Special case with CDATA
        attrs.setAttributes(new AttributesImpl(cdata));
        TestCase.assertEquals(1, attrs.getLength());
        TestCase.assertEquals(false, attrs.isDeclared(0));
        TestCase.assertEquals(true, attrs.isSpecified(0));
        // null case
        try {
            attrs.setAttributes(null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    public void testAddAttribute() {
        Attributes2Impl attrs = new Attributes2Impl();
        // Ordinary case
        attrs.addAttribute("http://yet.another.uri", "doe", "john:doe", "string", "abc");
        TestCase.assertEquals(1, attrs.getLength());
        TestCase.assertEquals("http://yet.another.uri", attrs.getURI(0));
        TestCase.assertEquals("doe", attrs.getLocalName(0));
        TestCase.assertEquals("john:doe", attrs.getQName(0));
        TestCase.assertEquals("string", attrs.getType(0));
        TestCase.assertEquals("abc", attrs.getValue(0));
        TestCase.assertEquals(true, attrs.isDeclared(0));
        TestCase.assertEquals(true, attrs.isSpecified(0));
        // CDATA case
        attrs.addAttribute("http://yet.another.uri", "doe", "jane:doe", "CDATA", "abc");
        TestCase.assertEquals(2, attrs.getLength());
        TestCase.assertEquals("http://yet.another.uri", attrs.getURI(1));
        TestCase.assertEquals("doe", attrs.getLocalName(1));
        TestCase.assertEquals("jane:doe", attrs.getQName(1));
        TestCase.assertEquals("CDATA", attrs.getType(1));
        TestCase.assertEquals("abc", attrs.getValue(1));
        TestCase.assertEquals(false, attrs.isDeclared(1));
        TestCase.assertEquals(true, attrs.isSpecified(1));
    }

    public void testRemoveAttribute() {
        Attributes2Impl attrs = new Attributes2Impl(multi);
        // Ordinary case
        attrs.removeAttribute(1);
        TestCase.assertEquals(3, attrs.getLength());
        TestCase.assertEquals(multi.getURI(0), attrs.getURI(0));
        TestCase.assertEquals(multi.getLocalName(0), attrs.getLocalName(0));
        TestCase.assertEquals(multi.getQName(0), attrs.getQName(0));
        TestCase.assertEquals(multi.getType(0), attrs.getType(0));
        TestCase.assertEquals(multi.getValue(0), attrs.getValue(0));
        TestCase.assertEquals(multi.isDeclared(0), attrs.isDeclared(0));
        TestCase.assertEquals(multi.isSpecified(0), attrs.isSpecified(0));
        TestCase.assertEquals(multi.getURI(2), attrs.getURI(1));
        TestCase.assertEquals(multi.getLocalName(2), attrs.getLocalName(1));
        TestCase.assertEquals(multi.getQName(2), attrs.getQName(1));
        TestCase.assertEquals(multi.getType(2), attrs.getType(1));
        TestCase.assertEquals(multi.getValue(2), attrs.getValue(1));
        TestCase.assertEquals(multi.isDeclared(2), attrs.isDeclared(1));
        TestCase.assertEquals(multi.isSpecified(2), attrs.isSpecified(1));
        // Out of range
        try {
            attrs.removeAttribute((-1));
            TestCase.fail("ArrayIndexOutOfBoundsException expected");
        } catch (ArrayIndexOutOfBoundsException e) {
            // Expected
        }
        try {
            attrs.removeAttribute(3);
            TestCase.fail("ArrayIndexOutOfBoundsException expected");
        } catch (ArrayIndexOutOfBoundsException e) {
            // Expected
        }
    }

    public void testAttributes2Impl() {
        TestCase.assertEquals(0, empty.getLength());
    }

    public void testAttributes2ImplAttributes() {
        // Ordinary case with Attributes2Impl
        Attributes2Impl attrs = new Attributes2Impl(multi);
        TestCase.assertEquals(multi.getLength(), attrs.getLength());
        for (int i = 0; i < (multi.getLength()); i++) {
            TestCase.assertEquals(multi.getURI(i), attrs.getURI(i));
            TestCase.assertEquals(multi.getLocalName(i), attrs.getLocalName(i));
            TestCase.assertEquals(multi.getQName(i), attrs.getQName(i));
            TestCase.assertEquals(multi.getType(i), attrs.getType(i));
            TestCase.assertEquals(multi.getValue(i), attrs.getValue(i));
            TestCase.assertEquals(multi.isDeclared(i), attrs.isDeclared(i));
            TestCase.assertEquals(multi.isSpecified(i), attrs.isSpecified(i));
        }
        attrs = new Attributes2Impl(empty);
        TestCase.assertEquals(0, attrs.getLength());
        // Ordinary case with AttributesImpl
        attrs = new Attributes2Impl(new AttributesImpl(multi));
        TestCase.assertEquals(multi.getLength(), attrs.getLength());
        for (int i = 0; i < (multi.getLength()); i++) {
            TestCase.assertEquals(multi.getURI(i), attrs.getURI(i));
            TestCase.assertEquals(multi.getLocalName(i), attrs.getLocalName(i));
            TestCase.assertEquals(multi.getQName(i), attrs.getQName(i));
            TestCase.assertEquals(multi.getType(i), attrs.getType(i));
            TestCase.assertEquals(multi.getValue(i), attrs.getValue(i));
            TestCase.assertEquals(true, attrs.isDeclared(i));
            TestCase.assertEquals(true, attrs.isSpecified(i));
        }
        // Special case with CDATA
        attrs = new Attributes2Impl(new AttributesImpl(cdata));
        TestCase.assertEquals(1, attrs.getLength());
        TestCase.assertEquals(false, attrs.isDeclared(0));
        TestCase.assertEquals(true, attrs.isSpecified(0));
        // null case
        try {
            attrs = new Attributes2Impl(null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    public void testIsDeclaredInt() {
        // Ordinary cases
        TestCase.assertEquals(false, multi.isDeclared(0));
        TestCase.assertEquals(true, multi.isDeclared(1));
        // Out of range
        try {
            multi.isDeclared((-1));
            TestCase.fail("ArrayIndexOutOfBoundsException expected");
        } catch (ArrayIndexOutOfBoundsException e) {
            // Expected
        }
        try {
            multi.isDeclared(4);
            TestCase.fail("ArrayIndexOutOfBoundsException expected");
        } catch (ArrayIndexOutOfBoundsException e) {
            // Expected
        }
    }

    public void testIsDeclaredStringString() {
        // Ordinary cases
        TestCase.assertEquals(false, multi.isDeclared("http://some.uri", "foo"));
        TestCase.assertEquals(true, multi.isDeclared("http://some.uri", "bar"));
        // Not found
        try {
            TestCase.assertFalse(multi.isDeclared("not", "found"));
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    public void testIsDeclaredString() {
        // Ordinary cases
        TestCase.assertEquals(false, multi.isDeclared("ns1:foo"));
        TestCase.assertEquals(true, multi.isDeclared("ns1:bar"));
        // Not found
        try {
            TestCase.assertFalse(multi.isDeclared("notfound"));
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    public void testIsSpecifiedInt() {
        // Ordinary cases
        TestCase.assertEquals(false, multi.isSpecified(1));
        TestCase.assertEquals(true, multi.isSpecified(2));
        // Out of range
        try {
            multi.isSpecified((-1));
            TestCase.fail("ArrayIndexOutOfBoundsException expected");
        } catch (ArrayIndexOutOfBoundsException e) {
            // Expected
        }
        try {
            multi.isSpecified(4);
            TestCase.fail("ArrayIndexOutOfBoundsException expected");
        } catch (ArrayIndexOutOfBoundsException e) {
            // Expected
        }
    }

    public void testIsSpecifiedStringString() {
        // Ordinary cases
        TestCase.assertEquals(false, multi.isSpecified("http://some.uri", "bar"));
        TestCase.assertEquals(true, multi.isSpecified("http://some.other.uri", "answer"));
        // Not found
        try {
            TestCase.assertFalse(multi.isSpecified("not", "found"));
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    public void testIsSpecifiedString() {
        // Ordinary cases
        TestCase.assertEquals(false, multi.isSpecified("ns1:bar"));
        TestCase.assertEquals(true, multi.isSpecified("ns2:answer"));
        // Not found
        try {
            TestCase.assertFalse(multi.isSpecified("notfound"));
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    public void testSetDeclared() {
        // Ordinary cases
        multi.setSpecified(0, false);
        TestCase.assertEquals(false, multi.isSpecified(0));
        multi.setSpecified(0, true);
        TestCase.assertEquals(true, multi.isSpecified(0));
        multi.setSpecified(0, false);
        TestCase.assertEquals(false, multi.isSpecified(0));
        // Out of range
        try {
            multi.setSpecified((-1), true);
            TestCase.fail("ArrayIndexOutOfBoundsException expected");
        } catch (ArrayIndexOutOfBoundsException e) {
            // Expected
        }
        try {
            multi.setSpecified(5, true);
            TestCase.fail("ArrayIndexOutOfBoundsException expected");
        } catch (ArrayIndexOutOfBoundsException e) {
            // Expected
        }
    }

    public void testSetSpecified() {
        // Ordinary cases
        multi.setSpecified(0, false);
        TestCase.assertEquals(false, multi.isSpecified(0));
        multi.setSpecified(0, true);
        TestCase.assertEquals(true, multi.isSpecified(0));
        multi.setSpecified(0, false);
        TestCase.assertEquals(false, multi.isSpecified(0));
        // Out of range
        try {
            multi.setSpecified((-1), true);
            TestCase.fail("ArrayIndexOutOfBoundsException expected");
        } catch (ArrayIndexOutOfBoundsException e) {
            // Expected
        }
        try {
            multi.setSpecified(5, true);
            TestCase.fail("ArrayIndexOutOfBoundsException expected");
        } catch (ArrayIndexOutOfBoundsException e) {
            // Expected
        }
    }
}

