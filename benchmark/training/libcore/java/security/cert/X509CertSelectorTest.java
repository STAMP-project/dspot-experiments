/**
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package libcore.java.security.cert;


import GeneralName.iPAddress;
import java.io.IOException;
import java.security.cert.X509CertSelector;
import junit.framework.TestCase;


public final class X509CertSelectorTest extends TestCase {
    public void testMatchIpv4SubjectAlternativeName() throws Exception {
        X509CertSelector certSelector = new X509CertSelector();
        certSelector.addSubjectAlternativeName(iPAddress, "127.0.0.1");
        byte[] match = new byte[]{ 127, 0, 0, 1 };
        TestCase.assertTrue(certSelector.match(newCertWithSubjectAltNameIpAddress(match)));
        byte[] noMatch = new byte[]{ 127, 0, 0, 2 };
        TestCase.assertFalse(certSelector.match(newCertWithSubjectAltNameIpAddress(noMatch)));
    }

    public void testMatchIpv4MappedSubjectAlternativeName() throws Exception {
        X509CertSelector certSelector = new X509CertSelector();
        certSelector.addSubjectAlternativeName(iPAddress, "::ffff:127.0.0.1");
        byte[] match = new byte[]{ 127, 0, 0, 1 };
        TestCase.assertTrue(certSelector.match(newCertWithSubjectAltNameIpAddress(match)));
        byte[] noMatch = new byte[]{ 127, 0, 0, 2 };
        TestCase.assertFalse(certSelector.match(newCertWithSubjectAltNameIpAddress(noMatch)));
    }

    public void testMatchIpv6SubjectAlternativeName() throws Exception {
        X509CertSelector certSelector = new X509CertSelector();
        certSelector.setMatchAllSubjectAltNames(false);
        certSelector.addSubjectAlternativeName(iPAddress, "::1");
        byte[] match = new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 };
        TestCase.assertTrue(certSelector.match(newCertWithSubjectAltNameIpAddress(match)));
        byte[] noMatch = new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2 };
        TestCase.assertFalse(certSelector.match(newCertWithSubjectAltNameIpAddress(noMatch)));
    }

    public void testMatchMaskedIpv4NameConstraint() throws Exception {
        byte[] excluded = new byte[]{ ((byte) (192)), ((byte) (168)), 0, 1 };
        X509CertSelector certSelector = new X509CertSelector();
        certSelector.addPathToName(iPAddress, "127.0.0.1");
        byte[] directMatch = new byte[]{ 127, 0, 0, 1, -1, -1, -1, -1 };
        TestCase.assertTrue(certSelector.match(newCertWithNameConstraint(directMatch, excluded)));
        byte[] noMatch = new byte[]{ 127, 0, 0, 2, -1, -1, -1, 127 };
        TestCase.assertFalse(certSelector.match(newCertWithNameConstraint(noMatch, excluded)));
        // TODO: test that requires mask to match
    }

    public void testMatchMaskedIpv6NameConstraint() throws Exception {
        byte[] excluded = new byte[]{ 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0 };
        X509CertSelector certSelector = new X509CertSelector();
        certSelector.addPathToName(iPAddress, "1::1");
        byte[] directMatch = new byte[]{ 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 127 };
        TestCase.assertTrue(certSelector.match(newCertWithNameConstraint(directMatch, excluded)));
        byte[] noMatch = new byte[]{ 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 127 };
        TestCase.assertFalse(certSelector.match(newCertWithNameConstraint(noMatch, excluded)));
        // TODO: test that requires mask to match
    }

    public void testMatchMalformedSubjectAlternativeName() throws Exception {
        X509CertSelector certSelector = new X509CertSelector();
        try {
            certSelector.addSubjectAlternativeName(iPAddress, "1::x");
            TestCase.fail();
        } catch (IOException expected) {
        }
        try {
            certSelector.addSubjectAlternativeName(iPAddress, "127.0.0.x");
            TestCase.fail();
        } catch (IOException expected) {
        }
    }
}

