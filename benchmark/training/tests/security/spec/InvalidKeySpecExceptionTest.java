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
 * @author Vera Y. Petrashkova
 * @version $Revision$
 */
package tests.security.spec;


import java.security.spec.InvalidKeySpecException;
import junit.framework.TestCase;


/**
 * Tests for <code>InvalidKeySpecException</code> class constructors and
 * methods.
 */
public class InvalidKeySpecExceptionTest extends TestCase {
    private static String[] msgs = new String[]{ "", "Check new message", "Check new message Check new message Check new message Check new message Check new message" };

    private static Throwable tCause = new Throwable("Throwable for exception");

    /**
     * Test for <code>InvalidKeySpecException()</code> constructor Assertion:
     * constructs InvalidKeySpecException with no detail message
     */
    public void testInvalidKeySpecException01() {
        InvalidKeySpecException tE = new InvalidKeySpecException();
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>InvalidKeySpecException(String)</code> constructor
     * Assertion: constructs InvalidKeySpecException with detail message msg.
     * Parameter <code>msg</code> is not null.
     */
    public void testInvalidKeySpecException02() {
        InvalidKeySpecException tE;
        for (int i = 0; i < (InvalidKeySpecExceptionTest.msgs.length); i++) {
            tE = new InvalidKeySpecException(InvalidKeySpecExceptionTest.msgs[i]);
            TestCase.assertEquals("getMessage() must return: ".concat(InvalidKeySpecExceptionTest.msgs[i]), tE.getMessage(), InvalidKeySpecExceptionTest.msgs[i]);
            TestCase.assertNull("getCause() must return null", tE.getCause());
        }
    }

    /**
     * Test for <code>InvalidKeySpecException(String)</code> constructor
     * Assertion: constructs InvalidKeySpecException when <code>msg</code> is
     * null
     */
    public void testInvalidKeySpecException03() {
        String msg = null;
        InvalidKeySpecException tE = new InvalidKeySpecException(msg);
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>InvalidKeySpecException(Throwable)</code> constructor
     * Assertion: constructs InvalidKeySpecException when <code>cause</code>
     * is null
     */
    public void testInvalidKeySpecException04() {
        Throwable cause = null;
        InvalidKeySpecException tE = new InvalidKeySpecException(cause);
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>InvalidKeySpecException(Throwable)</code> constructor
     * Assertion: constructs InvalidKeySpecException when <code>cause</code>
     * is not null
     */
    public void testInvalidKeySpecException05() {
        InvalidKeySpecException tE = new InvalidKeySpecException(InvalidKeySpecExceptionTest.tCause);
        if ((tE.getMessage()) != null) {
            String toS = InvalidKeySpecExceptionTest.tCause.toString();
            String getM = tE.getMessage();
            TestCase.assertTrue("getMessage() should contain ".concat(toS), ((getM.indexOf(toS)) != (-1)));
        }
        TestCase.assertNotNull("getCause() must not return null", tE.getCause());
        TestCase.assertEquals("getCause() must return ".concat(InvalidKeySpecExceptionTest.tCause.toString()), tE.getCause(), InvalidKeySpecExceptionTest.tCause);
    }

    /**
     * Test for <code>InvalidKeySpecException(String, Throwable)</code>
     * constructor Assertion: constructs InvalidKeySpecException when
     * <code>cause</code> is null <code>msg</code> is null
     */
    public void testInvalidKeySpecException06() {
        InvalidKeySpecException tE = new InvalidKeySpecException(null, null);
        TestCase.assertNull("getMessage() must return null", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>InvalidKeySpecException(String, Throwable)</code>
     * constructor Assertion: constructs InvalidKeySpecException when
     * <code>cause</code> is null <code>msg</code> is not null
     */
    public void testInvalidKeySpecException07() {
        InvalidKeySpecException tE;
        for (int i = 0; i < (InvalidKeySpecExceptionTest.msgs.length); i++) {
            tE = new InvalidKeySpecException(InvalidKeySpecExceptionTest.msgs[i], null);
            TestCase.assertEquals("getMessage() must return: ".concat(InvalidKeySpecExceptionTest.msgs[i]), tE.getMessage(), InvalidKeySpecExceptionTest.msgs[i]);
            TestCase.assertNull("getCause() must return null", tE.getCause());
        }
    }

    /**
     * Test for <code>InvalidKeySpecException(String, Throwable)</code>
     * constructor Assertion: constructs InvalidKeySpecException when
     * <code>cause</code> is not null <code>msg</code> is null
     */
    public void testInvalidKeySpecException08() {
        InvalidKeySpecException tE = new InvalidKeySpecException(null, InvalidKeySpecExceptionTest.tCause);
        if ((tE.getMessage()) != null) {
            String toS = InvalidKeySpecExceptionTest.tCause.toString();
            String getM = tE.getMessage();
            TestCase.assertTrue("getMessage() must should ".concat(toS), ((getM.indexOf(toS)) != (-1)));
        }
        TestCase.assertNotNull("getCause() must not return null", tE.getCause());
        TestCase.assertEquals("getCause() must return ".concat(InvalidKeySpecExceptionTest.tCause.toString()), tE.getCause(), InvalidKeySpecExceptionTest.tCause);
    }

    /**
     * Test for <code>InvalidKeySpecException(String, Throwable)</code>
     * constructor Assertion: constructs InvalidKeySpecException when
     * <code>cause</code> is not null <code>msg</code> is not null
     */
    public void testInvalidKeySpecException09() {
        InvalidKeySpecException tE;
        for (int i = 0; i < (InvalidKeySpecExceptionTest.msgs.length); i++) {
            tE = new InvalidKeySpecException(InvalidKeySpecExceptionTest.msgs[i], InvalidKeySpecExceptionTest.tCause);
            String getM = tE.getMessage();
            String toS = InvalidKeySpecExceptionTest.tCause.toString();
            if ((InvalidKeySpecExceptionTest.msgs[i].length()) > 0) {
                TestCase.assertTrue("getMessage() must contain ".concat(InvalidKeySpecExceptionTest.msgs[i]), ((getM.indexOf(InvalidKeySpecExceptionTest.msgs[i])) != (-1)));
                if (!(getM.equals(InvalidKeySpecExceptionTest.msgs[i]))) {
                    TestCase.assertTrue("getMessage() should contain ".concat(toS), ((getM.indexOf(toS)) != (-1)));
                }
            }
            TestCase.assertNotNull("getCause() must not return null", tE.getCause());
            TestCase.assertEquals("getCause() must return ".concat(InvalidKeySpecExceptionTest.tCause.toString()), tE.getCause(), InvalidKeySpecExceptionTest.tCause);
        }
    }
}

