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
package libcore.java.security.cert;


import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.util.Calendar;
import java.util.Collections;
import java.util.Set;
import junit.framework.TestCase;
import org.apache.harmony.security.tests.support.cert.TestUtils;


/**
 * Tests for <code>PKIXParameters</code> fields and methods
 */
public class OldPKIXParametersTest extends TestCase {
    public final void testClone() throws InvalidAlgorithmParameterException {
        Set<TrustAnchor> taSet = TestUtils.getTrustAnchorSet();
        if (taSet == null) {
            TestCase.fail(((getName()) + ": not performed (could not create test TrustAnchor set)"));
        }
        PKIXParameters cpp = new PKIXParameters(taSet);
        PKIXParameters cppc = ((PKIXParameters) (cpp.clone()));
        TestCase.assertEquals(cpp.getPolicyQualifiersRejected(), cppc.getPolicyQualifiersRejected());
        TestCase.assertEquals(cpp.getCertPathCheckers(), cppc.getCertPathCheckers());
        TestCase.assertEquals(cpp.getCertStores(), cppc.getCertStores());
        TestCase.assertEquals(cpp.getDate(), cppc.getDate());
        TestCase.assertEquals(cpp.getInitialPolicies(), cppc.getInitialPolicies());
        TestCase.assertEquals(cpp.getSigProvider(), cppc.getSigProvider());
        TestCase.assertEquals(cpp.getTargetCertConstraints(), cppc.getTargetCertConstraints());
        TestCase.assertEquals(cpp.getTrustAnchors(), cppc.getTrustAnchors());
        TestCase.assertEquals(cpp.isAnyPolicyInhibited(), cppc.isAnyPolicyInhibited());
        TestCase.assertEquals(cpp.isExplicitPolicyRequired(), cppc.isExplicitPolicyRequired());
        TestCase.assertEquals(cpp.isPolicyMappingInhibited(), cppc.isPolicyMappingInhibited());
        TestCase.assertEquals(cpp.isRevocationEnabled(), cppc.isRevocationEnabled());
        cpp.setDate(Calendar.getInstance().getTime());
        cpp.setPolicyQualifiersRejected((!(cppc.getPolicyQualifiersRejected())));
        TestCase.assertFalse(cpp.getDate().equals(cppc.getDate()));
        TestCase.assertFalse(((cpp.getPolicyQualifiersRejected()) == (cppc.getPolicyQualifiersRejected())));
        cppc.setExplicitPolicyRequired((!(cpp.isExplicitPolicyRequired())));
        cppc.setRevocationEnabled((!(cpp.isRevocationEnabled())));
        TestCase.assertFalse(((cpp.isExplicitPolicyRequired()) == (cppc.isExplicitPolicyRequired())));
        TestCase.assertFalse(((cpp.isRevocationEnabled()) == (cppc.isRevocationEnabled())));
        PKIXParameters cpp1 = null;
        try {
            cpp1.clone();
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * Test for <code>isPolicyMappingInhibited()</code> method<br>
     * Assertion: returns true if policy mapping is inhibited, false otherwise
     * Assertion: by default, policy mapping is not inhibited (the flag is
     * false)
     *
     * @throws InvalidAlgorithmParameterException
     * 		
     */
    public final void testIsPolicyMappingInhibited() throws Exception {
        Set<TrustAnchor> taSet = TestUtils.getTrustAnchorSet();
        if (taSet == null) {
            TestCase.fail(((getName()) + ": not performed (could not create test TrustAnchor set)"));
        }
        PKIXParameters p = new PKIXParameters(taSet);
        TestCase.assertFalse(p.isPolicyMappingInhibited());
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        TestUtils.initCertPathSSCertChain();
        Set<TrustAnchor> taSet2 = Collections.singleton(new TrustAnchor(TestUtils.rootCertificateSS, null));
        p = new PKIXParameters(taSet2);
        TestCase.assertFalse(p.isPolicyMappingInhibited());
        p.setPolicyMappingInhibited(true);
        TestCase.assertTrue(p.isRevocationEnabled());
    }

    /**
     * Test for <code>isPolicyMappingInhibited()</code> method<br>
     * Assertion: returns the current value of the RevocationEnabled flag
     * Assertion: when a <code>PKIXParameters</code> object is created, this
     * flag is set to true
     *
     * @throws InvalidAlgorithmParameterException
     * 		
     */
    public final void testIsRevocationEnabled() throws Exception {
        Set<TrustAnchor> taSet = TestUtils.getTrustAnchorSet();
        if (taSet == null) {
            TestCase.fail(((getName()) + ": not performed (could not create test TrustAnchor set)"));
        }
        PKIXParameters p = new PKIXParameters(taSet);
        TestCase.assertTrue(p.isRevocationEnabled());
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        TestUtils.initCertPathSSCertChain();
        Set<TrustAnchor> taSet2 = Collections.singleton(new TrustAnchor(TestUtils.rootCertificateSS, null));
        p = new PKIXParameters(taSet2);
        TestCase.assertTrue(p.isRevocationEnabled());
        p.setRevocationEnabled(false);
        TestCase.assertFalse(p.isRevocationEnabled());
    }

    /**
     * Test for <code>toString</code> method<br>
     */
    public final void testToString() throws Exception {
        Set<TrustAnchor> taSet = TestUtils.getTrustAnchorSet();
        if (taSet == null) {
            TestCase.fail(((getName()) + ": not performed (could not create test TrustAnchor set)"));
        }
        PKIXParameters p = new PKIXParameters(taSet);
        TestCase.assertNotNull(p.toString());
        PKIXParameters p1 = null;
        try {
            p1.toString();
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }
}

