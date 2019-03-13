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
package org.apache.harmony.tests.org.xml.sax;


import junit.framework.TestCase;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;


public class SAXParseExceptionTest extends TestCase {
    public static final String ERR = "Houston, we have a problem";

    public static final String SYS = "mySystemID";

    public static final String PUB = "myPublicID";

    public static final int ROW = 1;

    public static final int COL = 2;

    public void testSAXParseException_String_Locator_Exception() {
        LocatorImpl l = new LocatorImpl();
        l.setPublicId(SAXParseExceptionTest.PUB);
        l.setSystemId(SAXParseExceptionTest.SYS);
        l.setLineNumber(SAXParseExceptionTest.ROW);
        l.setColumnNumber(SAXParseExceptionTest.COL);
        Exception c = new Exception();
        // Ordinary case
        SAXParseException e = new SAXParseException(SAXParseExceptionTest.ERR, l, c);
        TestCase.assertEquals(SAXParseExceptionTest.ERR, e.getMessage());
        TestCase.assertEquals(c, e.getException());
        TestCase.assertEquals(SAXParseExceptionTest.PUB, e.getPublicId());
        TestCase.assertEquals(SAXParseExceptionTest.SYS, e.getSystemId());
        TestCase.assertEquals(SAXParseExceptionTest.ROW, e.getLineNumber());
        TestCase.assertEquals(SAXParseExceptionTest.COL, e.getColumnNumber());
        // No message
        e = new SAXParseException(null, l, c);
        TestCase.assertNull(e.getMessage());
        TestCase.assertEquals(c, e.getException());
        TestCase.assertEquals(SAXParseExceptionTest.PUB, e.getPublicId());
        TestCase.assertEquals(SAXParseExceptionTest.SYS, e.getSystemId());
        TestCase.assertEquals(SAXParseExceptionTest.ROW, e.getLineNumber());
        TestCase.assertEquals(SAXParseExceptionTest.COL, e.getColumnNumber());
        // No locator
        e = new SAXParseException(SAXParseExceptionTest.ERR, null, c);
        TestCase.assertEquals(SAXParseExceptionTest.ERR, e.getMessage());
        TestCase.assertEquals(c, e.getException());
        TestCase.assertNull(e.getPublicId());
        TestCase.assertNull(e.getSystemId());
        TestCase.assertEquals((-1), e.getLineNumber());
        TestCase.assertEquals((-1), e.getColumnNumber());
        // No cause
        e = new SAXParseException(SAXParseExceptionTest.ERR, l, null);
        TestCase.assertEquals(SAXParseExceptionTest.ERR, e.getMessage());
        TestCase.assertNull(e.getException());
        TestCase.assertEquals(SAXParseExceptionTest.PUB, e.getPublicId());
        TestCase.assertEquals(SAXParseExceptionTest.SYS, e.getSystemId());
        TestCase.assertEquals(SAXParseExceptionTest.ROW, e.getLineNumber());
        TestCase.assertEquals(SAXParseExceptionTest.COL, e.getColumnNumber());
    }

    public void testSAXParseException_String_Locator() {
        LocatorImpl l = new LocatorImpl();
        l.setPublicId(SAXParseExceptionTest.PUB);
        l.setSystemId(SAXParseExceptionTest.SYS);
        l.setLineNumber(SAXParseExceptionTest.ROW);
        l.setColumnNumber(SAXParseExceptionTest.COL);
        // Ordinary case
        SAXParseException e = new SAXParseException(SAXParseExceptionTest.ERR, l);
        TestCase.assertEquals(SAXParseExceptionTest.ERR, e.getMessage());
        TestCase.assertNull(e.getException());
        TestCase.assertEquals(SAXParseExceptionTest.PUB, e.getPublicId());
        TestCase.assertEquals(SAXParseExceptionTest.SYS, e.getSystemId());
        TestCase.assertEquals(SAXParseExceptionTest.ROW, e.getLineNumber());
        TestCase.assertEquals(SAXParseExceptionTest.COL, e.getColumnNumber());
        // No message
        e = new SAXParseException(null, l);
        TestCase.assertNull(e.getMessage());
        TestCase.assertNull(e.getException());
        TestCase.assertEquals(SAXParseExceptionTest.PUB, e.getPublicId());
        TestCase.assertEquals(SAXParseExceptionTest.SYS, e.getSystemId());
        TestCase.assertEquals(SAXParseExceptionTest.ROW, e.getLineNumber());
        TestCase.assertEquals(SAXParseExceptionTest.COL, e.getColumnNumber());
        // No locator
        e = new SAXParseException(SAXParseExceptionTest.ERR, null);
        TestCase.assertEquals(SAXParseExceptionTest.ERR, e.getMessage());
        TestCase.assertNull(e.getException());
        TestCase.assertNull(e.getPublicId());
        TestCase.assertNull(e.getSystemId());
        TestCase.assertEquals((-1), e.getLineNumber());
        TestCase.assertEquals((-1), e.getColumnNumber());
    }

    public void testSAXParseException_String_String_String_int_int_Exception() {
        Exception c = new Exception();
        // Ordinary case
        SAXParseException e = new SAXParseException(SAXParseExceptionTest.ERR, SAXParseExceptionTest.PUB, SAXParseExceptionTest.SYS, SAXParseExceptionTest.ROW, SAXParseExceptionTest.COL, c);
        TestCase.assertEquals(SAXParseExceptionTest.ERR, e.getMessage());
        TestCase.assertEquals(c, e.getException());
        TestCase.assertEquals(SAXParseExceptionTest.PUB, e.getPublicId());
        TestCase.assertEquals(SAXParseExceptionTest.SYS, e.getSystemId());
        TestCase.assertEquals(SAXParseExceptionTest.ROW, e.getLineNumber());
        TestCase.assertEquals(SAXParseExceptionTest.COL, e.getColumnNumber());
        // No message
        e = new SAXParseException(null, SAXParseExceptionTest.PUB, SAXParseExceptionTest.SYS, SAXParseExceptionTest.ROW, SAXParseExceptionTest.COL, c);
        TestCase.assertNull(e.getMessage());
        TestCase.assertEquals(c, e.getException());
        TestCase.assertEquals(SAXParseExceptionTest.PUB, e.getPublicId());
        TestCase.assertEquals(SAXParseExceptionTest.SYS, e.getSystemId());
        TestCase.assertEquals(SAXParseExceptionTest.ROW, e.getLineNumber());
        TestCase.assertEquals(SAXParseExceptionTest.COL, e.getColumnNumber());
        // No locator
        e = new SAXParseException(SAXParseExceptionTest.ERR, null, null, (-1), (-1), c);
        TestCase.assertEquals(SAXParseExceptionTest.ERR, e.getMessage());
        TestCase.assertEquals(c, e.getException());
        TestCase.assertNull(e.getPublicId());
        TestCase.assertNull(e.getSystemId());
        TestCase.assertEquals((-1), e.getLineNumber());
        TestCase.assertEquals((-1), e.getColumnNumber());
        // No cause
        e = new SAXParseException(SAXParseExceptionTest.ERR, SAXParseExceptionTest.PUB, SAXParseExceptionTest.SYS, SAXParseExceptionTest.ROW, SAXParseExceptionTest.COL, null);
        TestCase.assertEquals(SAXParseExceptionTest.ERR, e.getMessage());
        TestCase.assertNull(e.getException());
        TestCase.assertEquals(SAXParseExceptionTest.PUB, e.getPublicId());
        TestCase.assertEquals(SAXParseExceptionTest.SYS, e.getSystemId());
        TestCase.assertEquals(SAXParseExceptionTest.ROW, e.getLineNumber());
        TestCase.assertEquals(SAXParseExceptionTest.COL, e.getColumnNumber());
    }

    public void testSAXParseException_String_String_String_int_int() {
        // Ordinary case
        SAXParseException e = new SAXParseException(SAXParseExceptionTest.ERR, SAXParseExceptionTest.PUB, SAXParseExceptionTest.SYS, SAXParseExceptionTest.ROW, SAXParseExceptionTest.COL);
        TestCase.assertEquals(SAXParseExceptionTest.ERR, e.getMessage());
        TestCase.assertNull(e.getException());
        TestCase.assertEquals(SAXParseExceptionTest.PUB, e.getPublicId());
        TestCase.assertEquals(SAXParseExceptionTest.SYS, e.getSystemId());
        TestCase.assertEquals(SAXParseExceptionTest.ROW, e.getLineNumber());
        TestCase.assertEquals(SAXParseExceptionTest.COL, e.getColumnNumber());
        // No message
        e = new SAXParseException(null, SAXParseExceptionTest.PUB, SAXParseExceptionTest.SYS, SAXParseExceptionTest.ROW, SAXParseExceptionTest.COL);
        TestCase.assertNull(e.getMessage());
        TestCase.assertNull(e.getException());
        TestCase.assertEquals(SAXParseExceptionTest.PUB, e.getPublicId());
        TestCase.assertEquals(SAXParseExceptionTest.SYS, e.getSystemId());
        TestCase.assertEquals(SAXParseExceptionTest.ROW, e.getLineNumber());
        TestCase.assertEquals(SAXParseExceptionTest.COL, e.getColumnNumber());
        // No locator
        e = new SAXParseException(SAXParseExceptionTest.ERR, null, null, (-1), (-1));
        TestCase.assertEquals(SAXParseExceptionTest.ERR, e.getMessage());
        TestCase.assertNull(e.getException());
        TestCase.assertNull(e.getPublicId());
        TestCase.assertNull(e.getSystemId());
        TestCase.assertEquals((-1), e.getLineNumber());
        TestCase.assertEquals((-1), e.getColumnNumber());
    }
}

