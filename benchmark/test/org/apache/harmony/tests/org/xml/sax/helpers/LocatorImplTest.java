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
import org.xml.sax.helpers.LocatorImpl;


public class LocatorImplTest extends TestCase {
    public static final String SYS = "mySystemID";

    public static final String PUB = "myPublicID";

    public static final int ROW = 1;

    public static final int COL = 2;

    public void testLocatorImpl() {
        LocatorImpl l = new LocatorImpl();
        TestCase.assertEquals(null, l.getPublicId());
        TestCase.assertEquals(null, l.getSystemId());
        TestCase.assertEquals(0, l.getLineNumber());
        TestCase.assertEquals(0, l.getColumnNumber());
    }

    public void testLocatorImplLocator() {
        LocatorImpl inner = new LocatorImpl();
        inner.setPublicId(LocatorImplTest.PUB);
        inner.setSystemId(LocatorImplTest.SYS);
        inner.setLineNumber(LocatorImplTest.ROW);
        inner.setColumnNumber(LocatorImplTest.COL);
        // Ordinary case
        LocatorImpl outer = new LocatorImpl(inner);
        TestCase.assertEquals(LocatorImplTest.PUB, outer.getPublicId());
        TestCase.assertEquals(LocatorImplTest.SYS, outer.getSystemId());
        TestCase.assertEquals(LocatorImplTest.ROW, outer.getLineNumber());
        TestCase.assertEquals(LocatorImplTest.COL, outer.getColumnNumber());
        // No locator
        try {
            outer = new LocatorImpl(null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    public void testSetPublicIdGetPublicId() {
        LocatorImpl l = new LocatorImpl();
        l.setPublicId(LocatorImplTest.PUB);
        TestCase.assertEquals(LocatorImplTest.PUB, l.getPublicId());
        l.setPublicId(null);
        TestCase.assertEquals(null, l.getPublicId());
    }

    public void testSetSystemIdGetSystemId() {
        LocatorImpl l = new LocatorImpl();
        l.setSystemId(LocatorImplTest.SYS);
        TestCase.assertEquals(LocatorImplTest.SYS, l.getSystemId());
        l.setSystemId(null);
        TestCase.assertEquals(null, l.getSystemId());
    }

    public void testSetLineNumberGetLineNumber() {
        LocatorImpl l = new LocatorImpl();
        l.setLineNumber(LocatorImplTest.ROW);
        TestCase.assertEquals(LocatorImplTest.ROW, l.getLineNumber());
        l.setLineNumber(0);
        TestCase.assertEquals(0, l.getLineNumber());
    }

    public void testSetColumnNumberGetColumnNumber() {
        LocatorImpl l = new LocatorImpl();
        l.setColumnNumber(LocatorImplTest.COL);
        TestCase.assertEquals(LocatorImplTest.COL, l.getColumnNumber());
        l.setColumnNumber(0);
        TestCase.assertEquals(0, l.getColumnNumber());
    }
}

