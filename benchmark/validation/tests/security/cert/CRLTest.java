/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
/**
 *
 *
 * @author Vladimir N. Molotkov
 * @version $Revision$
 */
package tests.security.cert;


import java.security.cert.CRL;
import java.security.cert.Certificate;
import junit.framework.TestCase;
import org.apache.harmony.security.tests.support.SpiEngUtils;


/**
 * Tests for <code>java.security.cert.CRL</code> fields and methods
 */
public class CRLTest extends TestCase {
    public static final String[] validValues = new String[]{ "X.509", "x.509" };

    private static final String[] invalidValues = SpiEngUtils.invalidValues;

    // 
    // Tests
    // 
    /**
     * Test for <code>CRL(String type)</code> constructor<br>
     */
    public final void testConstructor() {
        for (int i = 0; i < (CRLTest.validValues.length); i++) {
            CRL crl = new CRLTest.MyCRL(CRLTest.validValues[i]);
            TestCase.assertEquals(CRLTest.validValues[i], crl.getType());
        }
        for (int i = 0; i < (CRLTest.invalidValues.length); i++) {
            CRL crl = new CRLTest.MyCRL(CRLTest.invalidValues[i]);
            TestCase.assertEquals(CRLTest.invalidValues[i], crl.getType());
        }
        try {
            CRL crl = new CRLTest.MyCRL(null);
        } catch (Exception e) {
            TestCase.fail("Unexpected exception for NULL parameter");
        }
    }

    /**
     * Test #1 for <code>getType()</code> method<br>
     * Assertion: returns <code>CRL</code> type
     */
    public final void testGetType01() {
        CRL crl = new CRLTest.MyCRL("TEST_TYPE");
        TestCase.assertEquals("TEST_TYPE", crl.getType());
    }

    /**
     * Test #2 for <code>getType()</code> method<br>
     * Assertion: returns <code>CRL</code> type
     */
    public final void testGetType02() {
        CRL crl = new CRLTest.MyCRL(null);
        TestCase.assertNull(crl.getType());
    }

    // 
    // the following tests just call methods
    // that are abstract in <code>Certificate</code>
    // (So they just like signature tests)
    // 
    /**
     * Test for <code>toString()</code> method
     */
    public final void testToString() {
        CRL crl = new CRLTest.MyCRL("TEST_TYPE");
        crl.toString();
    }

    /**
     * Test for <code>isRevoked()</code> method
     */
    public final void testIsRevoked() {
        CRL crl = new CRLTest.MyCRL("TEST_TYPE");
        crl.isRevoked(null);
    }

    class MyCRL extends CRL {
        protected MyCRL(String type) {
            super(type);
        }

        @Override
        public boolean isRevoked(Certificate cert) {
            return false;
        }

        @Override
        public String toString() {
            return null;
        }
    }
}

