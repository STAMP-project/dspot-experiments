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
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


public class SAXExceptionTest extends TestCase {
    public static final String ERR = "Houston, we have a problem";

    public void testSAXParseException() {
        SAXException e = new SAXException();
        TestCase.assertNull(e.getMessage());
        TestCase.assertNull(e.getException());
    }

    public void testSAXException_String_Exception() {
        Exception c = new Exception();
        // Ordinary case
        SAXException e = new SAXException(SAXExceptionTest.ERR, c);
        TestCase.assertEquals(SAXExceptionTest.ERR, e.getMessage());
        TestCase.assertEquals(c, e.getException());
        // No message
        e = new SAXException(null, c);
        TestCase.assertNull(e.getMessage());
        TestCase.assertEquals(c, e.getException());
        // No cause
        e = new SAXParseException(SAXExceptionTest.ERR, null);
        TestCase.assertEquals(SAXExceptionTest.ERR, e.getMessage());
        TestCase.assertNull(e.getException());
    }

    public void testSAXException_String() {
        // Ordinary case
        SAXException e = new SAXException(SAXExceptionTest.ERR);
        TestCase.assertEquals(SAXExceptionTest.ERR, e.getMessage());
        TestCase.assertNull(e.getException());
        // No message
        e = new SAXException(((String) (null)));
        TestCase.assertNull(e.getMessage());
        TestCase.assertNull(e.getException());
    }

    public void testSAXException_Exception() {
        Exception c = new Exception();
        // Ordinary case
        SAXException e = new SAXException(c);
        TestCase.assertNull(e.getMessage());
        TestCase.assertEquals(c, e.getException());
        // No cause
        e = new SAXException(((Exception) (null)));
        TestCase.assertNull(e.getMessage());
        TestCase.assertNull(e.getException());
    }

    public void testToString() {
        // Ordinary case
        SAXException e = new SAXException(SAXExceptionTest.ERR);
        String s = e.toString();
        TestCase.assertTrue(s.contains(SAXExceptionTest.ERR));
        // No message
        e = new SAXException();
        s = e.toString();
        TestCase.assertFalse(s.contains(SAXExceptionTest.ERR));
    }
}

