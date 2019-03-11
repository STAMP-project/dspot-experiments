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


public class X509CRLTest extends TestCase {
    private Provider[] mX509Providers;

    private static final String CERT_RSA = "x509/cert-rsa.der";

    private static final String CERT_DSA = "x509/cert-dsa.der";

    private static final String CERT_CRL_CA = "x509/cert-crl-ca.der";

    private static final String CRL_RSA = "x509/crl-rsa.der";

    private static final String CRL_RSA_DSA = "x509/crl-rsa-dsa.der";

    private static final String CRL_RSA_DSA_SIGOPT = "x509/crl-rsa-dsa-sigopt.der";

    private static final String CRL_UNSUPPORTED = "x509/crl-unsupported.der";

    private static final String CRL_RSA_DATES = "x509/crl-rsa-dates.txt";

    private static final String CRL_RSA_DSA_DATES = "x509/crl-rsa-dsa-dates.txt";

    private static final String CRL_RSA_SIG = "x509/crl-rsa-sig.der";

    private static final String CRL_RSA_TBS = "x509/crl-rsa-tbs.der";

    private static final String CRL_EMPTY = "x509/crl-empty.der";

    public void test_Provider() throws Exception {
        final ByteArrayOutputStream errBuffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(errBuffer);
        for (Provider p : mX509Providers) {
            try {
                CertificateFactory f = CertificateFactory.getInstance("X.509", p);
                isRevoked(f);
                getType(f);
                getEncoded(f);
                getVersion(f);
                hasUnsupportedCriticalExtension(f);
                getSignature(f);
                getTBSCertList(f);
                getRevokedCertificates(f);
                getThisUpdateNextUpdate(f);
                getSigAlgName(f);
                getSigAlgOID(f);
                getSigAlgParams(f);
                verify(f);
                test_toString(f);
                test_equals(f);
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

