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
import org.xml.sax.ext.Locator2Impl;
import org.xml.sax.helpers.LocatorImpl;


public class Locator2ImplTest extends TestCase {
    public static final String SYS = "mySystemID";

    public static final String PUB = "myPublicID";

    public static final int ROW = 1;

    public static final int COL = 2;

    public static final String ENC = "Klingon";

    public static final String XML = "1.0";

    public void testLocatorImpl() {
        Locator2Impl l = new Locator2Impl();
        TestCase.assertEquals(null, l.getPublicId());
        TestCase.assertEquals(null, l.getSystemId());
        TestCase.assertEquals(0, l.getLineNumber());
        TestCase.assertEquals(0, l.getColumnNumber());
        TestCase.assertEquals(null, l.getEncoding());
        TestCase.assertEquals(null, l.getXMLVersion());
    }

    public void testLocatorImplLocator() {
        Locator2Impl inner = new Locator2Impl();
        inner.setPublicId(Locator2ImplTest.PUB);
        inner.setSystemId(Locator2ImplTest.SYS);
        inner.setLineNumber(Locator2ImplTest.ROW);
        inner.setColumnNumber(Locator2ImplTest.COL);
        inner.setEncoding(Locator2ImplTest.ENC);
        inner.setXMLVersion(Locator2ImplTest.XML);
        // Ordinary case
        Locator2Impl outer = new Locator2Impl(inner);
        TestCase.assertEquals(Locator2ImplTest.PUB, outer.getPublicId());
        TestCase.assertEquals(Locator2ImplTest.SYS, outer.getSystemId());
        TestCase.assertEquals(Locator2ImplTest.ROW, outer.getLineNumber());
        TestCase.assertEquals(Locator2ImplTest.COL, outer.getColumnNumber());
        TestCase.assertEquals(Locator2ImplTest.ENC, outer.getEncoding());
        TestCase.assertEquals(Locator2ImplTest.XML, outer.getXMLVersion());
        // Instance of old locator
        outer = new Locator2Impl(new LocatorImpl(inner));
        TestCase.assertEquals(Locator2ImplTest.PUB, outer.getPublicId());
        TestCase.assertEquals(Locator2ImplTest.SYS, outer.getSystemId());
        TestCase.assertEquals(Locator2ImplTest.ROW, outer.getLineNumber());
        TestCase.assertEquals(Locator2ImplTest.COL, outer.getColumnNumber());
        TestCase.assertEquals(null, outer.getEncoding());
        TestCase.assertEquals(null, outer.getXMLVersion());
        // No locator
        try {
            outer = new Locator2Impl(null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    public void testSetXMLVersionGetXMLVersion() {
        Locator2Impl l = new Locator2Impl();
        l.setXMLVersion(Locator2ImplTest.XML);
        TestCase.assertEquals(Locator2ImplTest.XML, l.getXMLVersion());
        l.setXMLVersion(null);
        TestCase.assertEquals(null, l.getXMLVersion());
    }

    public void testSetEncodingGetEncoding() {
        Locator2Impl l = new Locator2Impl();
        l.setEncoding(Locator2ImplTest.ENC);
        TestCase.assertEquals(Locator2ImplTest.ENC, l.getEncoding());
        l.setEncoding(null);
        TestCase.assertEquals(null, l.getEncoding());
    }
}

