/**
 * Copyright (C) 2012 The Android Open Source Project
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


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.security.Provider;
import java.security.cert.CertificateFactory;
import junit.framework.TestCase;


public class X509CertificateTest extends TestCase {
    private Provider[] mX509Providers;

    private static final String CERT_RSA = "x509/cert-rsa.der";

    private static final String CERT_DSA = "x509/cert-dsa.der";

    private static final String CERT_EC = "x509/cert-ec.der";

    private static final String CERT_KEYUSAGE_EXTRALONG = "x509/cert-keyUsage-extraLong.der";

    private static final String CERT_EXTENDEDKEYUSAGE = "x509/cert-extendedKeyUsage.der";

    private static final String CERT_RSA_TBS = "x509/cert-rsa-tbs.der";

    private static final String CERT_RSA_SIGNATURE = "x509/cert-rsa-sig.der";

    private static final String CERT_USERWITHPATHLEN = "x509/cert-userWithPathLen.der";

    private static final String CERT_CA = "x509/cert-ca.der";

    private static final String CERT_CAWITHPATHLEN = "x509/cert-caWithPathLen.der";

    private static final String CERT_INVALIDIP = "x509/cert-invalidip.der";

    private static final String CERT_IPV6 = "x509/cert-ipv6.der";

    private static final String CERT_ALT_OTHER = "x509/cert-alt-other.der";

    private static final String CERT_ALT_EMAIL = "x509/cert-alt-email.der";

    private static final String CERT_ALT_DNS = "x509/cert-alt-dns.der";

    private static final String CERT_ALT_DIRNAME = "x509/cert-alt-dirname.der";

    private static final String CERT_ALT_URI = "x509/cert-alt-uri.der";

    private static final String CERT_ALT_RID = "x509/cert-alt-rid.der";

    private static final String CERT_ALT_NONE = "x509/cert-alt-none.der";

    private static final String CERT_UNSUPPORTED = "x509/cert-unsupported.der";

    private static final String CERT_SIGOPT = "x509/cert-sigopt.der";

    private static final String CERTS_X509_PEM = "x509/certs.pem";

    private static final String CERTS_X509_DER = "x509/certs.der";

    private static final String CERTS_PKCS7_PEM = "x509/certs-pk7.pem";

    private static final String CERTS_PKCS7_DER = "x509/certs-pk7.der";

    /**
     * A list of certs that are all slightly different.
     */
    private static final String[] VARIOUS_CERTS = new String[]{ X509CertificateTest.CERT_RSA, X509CertificateTest.CERT_DSA, X509CertificateTest.CERT_EC };

    public void test_Provider() throws Exception {
        final ByteArrayOutputStream errBuffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(errBuffer);
        for (Provider p : mX509Providers) {
            try {
                CertificateFactory f = CertificateFactory.getInstance("X.509", p);
                getPublicKey(f);
                getType(f);
                check_equals(f);
                check_toString(f);
                check_hashCode(f);
                checkValidity(f);
                getVersion(f);
                getSerialNumber(f);
                getIssuerDN(f);
                getIssuerX500Principal(f);
                getSubjectDN(f);
                getSubjectUniqueID(f);
                getSubjectX500Principal(f);
                getNotBeforeAndNotAfterDates(f);
                getSigAlgName(f);
                getSigAlgOID(f);
                getSigAlgParams(f);
                getIssuerUniqueID(f);
                getSubjectUniqueID(f);
                getKeyUsage(f);
                getExtendedKeyUsage(f);
                getBasicConstraints(f);
                getSubjectAlternativeNames(f);
                getSubjectAlternativeNames_IPV6(f);
                getSubjectAlternativeNames_InvalidIP(f);
                getSubjectAlternativeNames_Other(f);
                getSubjectAlternativeNames_Email(f);
                getSubjectAlternativeNames_DNS(f);
                getSubjectAlternativeNames_DirName(f);
                getSubjectAlternativeNames_URI(f);
                getSubjectAlternativeNames_RID(f);
                getSubjectAlternativeNames_None(f);
                getIssuerAlternativeNames(f);
                getTBSCertificate(f);
                getSignature(f);
                hasUnsupportedCriticalExtension(f);
                getEncoded(f);
                verify(f);
                generateCertificate_PEM_TrailingData(f);
                generateCertificate_DER_TrailingData(f);
                generateCertificates_X509_PEM(f);
                generateCertificates_X509_DER(f);
                generateCertificates_PKCS7_PEM(f);
                generateCertificates_PKCS7_DER(f);
                generateCertificates_Empty(f);
                generateCertificates_X509_PEM_TrailingData(f);
                generateCertificates_X509_DER_TrailingData(f);
                generateCertificates_PKCS7_PEM_TrailingData(f);
                generateCertificates_PKCS7_DER_TrailingData(f);
                test_Serialization(f);
            } catch (Throwable e) {
                out.append((("Error encountered checking " + (p.getName())) + "\n"));
                e.printStackTrace(out);
            }
        }
        out.flush();
        if ((errBuffer.size()) > 0) {
            throw new Exception((("Errors encountered:\n\n" + (errBuffer.toString())) + "\n\n"));
        }
    }
}

