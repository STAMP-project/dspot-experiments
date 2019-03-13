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


import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PolicyNode;
import java.security.cert.TrustAnchor;
import java.security.spec.InvalidKeySpecException;
import junit.framework.TestCase;
import org.apache.harmony.security.tests.support.cert.TestUtils;


/**
 * Tests for <code>PKIXCertPathValidatorResult</code>
 */
public class PKIXCertPathValidatorResultTest extends TestCase {
    /**
     * PublicKey stub
     */
    private static PublicKey testPublicKey = new PublicKey() {
        private static final long serialVersionUID = -737454523739489192L;

        public String getAlgorithm() {
            return "NeverMind";
        }

        public String getFormat() {
            return "NeverMind";
        }

        public byte[] getEncoded() {
            return new byte[]{  };
        }
    };

    // 
    // Tests
    // 
    /**
     * Test #1 for <code>PKIXCertPathValidatorResult(TrustAnchor,
     * PolicyNode, PublicKey)</code> constructor<br>
     * Assertion: creates an instance of
     * <code>PKIXCertPathValidatorResult</code>
     *
     * @throws NoSuchAlgorithmException
     * 		
     * @throws InvalidKeySpecException
     * 		
     */
    public final void testPKIXCertPathValidatorResult01() throws NoSuchAlgorithmException, InvalidKeySpecException {
        TrustAnchor ta = TestUtils.getTrustAnchor();
        if (ta == null) {
            TestCase.fail(((getName()) + ": not performed (could not create test TrustAnchor)"));
        }
        new PKIXCertPathValidatorResult(ta, TestUtils.getPolicyTree(), PKIXCertPathValidatorResultTest.testPublicKey);
    }

    /**
     * Test #2 for <code>PKIXCertPathValidatorResult(TrustAnchor,
     * PolicyNode, PublicKey)</code> constructor<br>
     * Assertion: <code>NullPointerException</code> if
     * <code>TrustAnchor</code> parameter is <code>null</code>
     */
    public final void testPKIXCertPathValidatorResult02() {
        try {
            // pass null
            new PKIXCertPathValidatorResult(null, TestUtils.getPolicyTree(), PKIXCertPathValidatorResultTest.testPublicKey);
            TestCase.fail("NPE expected");
        } catch (NullPointerException e) {
        }
    }

    /**
     * Test #3 for <code>PKIXCertPathValidatorResult(TrustAnchor,
     * PolicyNode, PublicKey)</code> constructor<br>
     * Assertion: <code>NullPointerException</code> if
     * <code>PublicKey</code> parameter is <code>null</code>
     */
    public final void testPKIXCertPathValidatorResult03() {
        TrustAnchor ta = TestUtils.getTrustAnchor();
        if (ta == null) {
            TestCase.fail(((getName()) + ": not performed (could not create test TrustAnchor)"));
        }
        try {
            // pass null
            new PKIXCertPathValidatorResult(ta, TestUtils.getPolicyTree(), null);
            TestCase.fail("NPE expected");
        } catch (NullPointerException e) {
        }
    }

    /**
     * Test #4 for <code>PKIXCertPathValidatorResult(TrustAnchor,
     * PolicyNode, PublicKey)</code> constructor<br>
     * Assertion: <code>PolicyNode</code>can be <code>null</code>
     */
    public final void testPKIXCertPathValidatorResult04() throws Exception {
        TrustAnchor ta = TestUtils.getTrustAnchor();
        if (ta == null) {
            TestCase.fail(((getName()) + ": not performed (could not create test TrustAnchor)"));
        }
        new PKIXCertPathValidatorResult(ta, null, PKIXCertPathValidatorResultTest.testPublicKey);
    }

    /**
     * Test for <code>getTrustAnchor()</code> method<br>
     * Assertion: returns <code>TrustAnchor</code> (never <code>null</code>)
     *
     * @throws NoSuchAlgorithmException
     * 		
     * @throws InvalidKeySpecException
     * 		
     */
    public final void testGetTrustAnchor() throws Exception {
        TrustAnchor ta = TestUtils.getTrustAnchor();
        if (ta == null) {
            TestCase.fail(((getName()) + ": not performed (could not create test TrustAnchor)"));
        }
        PKIXCertPathValidatorResult vr = new PKIXCertPathValidatorResult(ta, null, PKIXCertPathValidatorResultTest.testPublicKey);
        // must return the same reference passed
        // as a parameter to the constructor
        TestCase.assertSame(ta, vr.getTrustAnchor());
    }

    /**
     * Test for <code>getPublicKey()</code> method<br>
     * Assertion: returns the subject's public key (never <code>null</code>)
     *
     * @throws NoSuchAlgorithmException
     * 		
     * @throws InvalidKeySpecException
     * 		
     */
    public final void testGetPublicKey() throws Exception {
        TrustAnchor ta = TestUtils.getTrustAnchor();
        if (ta == null) {
            TestCase.fail(((getName()) + ": not performed (could not create test TrustAnchor)"));
        }
        PublicKey pk = PKIXCertPathValidatorResultTest.testPublicKey;
        PKIXCertPathValidatorResult vr = new PKIXCertPathValidatorResult(ta, null, pk);
        // must return the same reference passed
        // as a parameter to the constructor
        TestCase.assertSame(pk, vr.getPublicKey());
    }

    /**
     * Test for <code>getPolicyTree()</code> method<br>
     * Assertion: returns the root node of the valid
     * policy tree or <code>null</code> if there are
     * no valid policies
     *
     * @throws NoSuchAlgorithmException
     * 		
     * @throws InvalidKeySpecException
     * 		
     */
    public final void testGetPolicyTree01() throws Exception {
        TrustAnchor ta = TestUtils.getTrustAnchor();
        if (ta == null) {
            TestCase.fail(((getName()) + ": not performed (could not create test TrustAnchor)"));
        }
        // valid policy tree case;
        PolicyNode pn = TestUtils.getPolicyTree();
        PKIXCertPathValidatorResult vr = new PKIXCertPathValidatorResult(ta, pn, PKIXCertPathValidatorResultTest.testPublicKey);
        // must return the same reference passed
        // as a parameter to the constructor
        TestCase.assertSame(pn, vr.getPolicyTree());
    }

    /**
     * Test for <code>getPolicyTree()</code> method<br>
     * Assertion: returns the root node of the valid
     * policy tree or <code>null</code> if there are
     * no valid policies
     *
     * @throws NoSuchAlgorithmException
     * 		
     * @throws InvalidKeySpecException
     * 		
     */
    public final void testGetPolicyTree02() throws Exception {
        TrustAnchor ta = TestUtils.getTrustAnchor();
        if (ta == null) {
            TestCase.fail(((getName()) + ": not performed (could not create test TrustAnchor)"));
        }
        // no valid policy tree case (null)
        PKIXCertPathValidatorResult vr = new PKIXCertPathValidatorResult(ta, null, PKIXCertPathValidatorResultTest.testPublicKey);
        // must return the same reference passed
        // as a parameter to the constructor
        TestCase.assertNull(vr.getPolicyTree());
    }

    /**
     * Test for <code>clone()</code> method<br>
     * Assertion: returns a copy of this object
     *
     * @throws NoSuchAlgorithmException
     * 		
     * @throws InvalidKeySpecException
     * 		
     */
    public final void testClone() throws Exception {
        TrustAnchor ta = TestUtils.getTrustAnchor();
        if (ta == null) {
            TestCase.fail(((getName()) + ": not performed (could not create test TrustAnchor)"));
        }
        PKIXCertPathValidatorResult vr1 = new PKIXCertPathValidatorResult(ta, TestUtils.getPolicyTree(), PKIXCertPathValidatorResultTest.testPublicKey);
        PKIXCertPathValidatorResult vr2 = ((PKIXCertPathValidatorResult) (vr1.clone()));
        // check that method makes shallow copy
        TestCase.assertNotSame("notSame", vr1, vr2);
        TestCase.assertSame("trustAncor", vr1.getTrustAnchor(), vr2.getTrustAnchor());
        TestCase.assertSame("policyTree", vr1.getPolicyTree(), vr2.getPolicyTree());
        TestCase.assertSame("publicKey", vr1.getPublicKey(), vr2.getPublicKey());
        // Regression for HARMONY-2786.
        byte[] encoding = new byte[]{ 1 };
        PKIXCertPathValidatorResultTest.MyPKIXCertPathBuilderResult my = new PKIXCertPathValidatorResultTest.MyPKIXCertPathBuilderResult(ta, TestUtils.getPolicyTree(), PKIXCertPathValidatorResultTest.testPublicKey, encoding);
        PKIXCertPathValidatorResultTest.MyPKIXCertPathBuilderResult myClone = ((PKIXCertPathValidatorResultTest.MyPKIXCertPathBuilderResult) (my.clone()));
        TestCase.assertSame(my.getPolicyTree(), myClone.getPolicyTree());
        TestCase.assertSame(my.getPublicKey(), myClone.getPublicKey());
        TestCase.assertSame(my.getTrustAnchor(), myClone.getTrustAnchor());
        TestCase.assertSame(my.enc, myClone.enc);
    }

    class MyPKIXCertPathBuilderResult extends PKIXCertPathValidatorResult {
        public byte[] enc;// byte array is cloneable


        public MyPKIXCertPathBuilderResult(TrustAnchor trustAnchor, PolicyNode policyTree, PublicKey subjectPublicKey, byte[] enc) {
            super(trustAnchor, policyTree, subjectPublicKey);
            this.enc = enc;
        }
    }

    /**
     * Test #1 for <code>toString()</code> method<br>
     * Assertion: Returns a formatted string describing this object
     *
     * @throws NoSuchAlgorithmException
     * 		
     * @throws InvalidKeySpecException
     * 		
     */
    public final void testToString01() throws Exception {
        TrustAnchor ta = TestUtils.getTrustAnchor();
        if (ta == null) {
            TestCase.fail(((getName()) + ": not performed (could not create test TrustAnchor)"));
        }
        PKIXCertPathValidatorResult vr = new PKIXCertPathValidatorResult(ta, TestUtils.getPolicyTree(), PKIXCertPathValidatorResultTest.testPublicKey);
        TestCase.assertNotNull(vr.toString());
    }

    /**
     * Test #2 for <code>toString()</code> method<br>
     * Assertion: Returns a formatted string describing this object
     *
     * @throws NoSuchAlgorithmException
     * 		
     * @throws InvalidKeySpecException
     * 		
     */
    public final void testToString02() throws Exception {
        TrustAnchor ta = TestUtils.getTrustAnchor();
        if (ta == null) {
            TestCase.fail(((getName()) + ": not performed (could not create test TrustAnchor)"));
        }
        PKIXCertPathValidatorResult vr = new PKIXCertPathValidatorResult(ta, null, PKIXCertPathValidatorResultTest.testPublicKey);
        TestCase.assertNotNull(vr.toString());
    }
}

