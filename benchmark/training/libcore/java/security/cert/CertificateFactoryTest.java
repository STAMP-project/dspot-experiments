/**
 * Copyright (C) 2010 The Android Open Source Project
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


import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertPath;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import junit.framework.TestCase;
import libcore.java.security.StandardNames;


public class CertificateFactoryTest extends TestCase {
    private static final String VALID_CERTIFICATE_PEM = "-----BEGIN CERTIFICATE-----\n" + ((((((((((((((((("MIIDITCCAoqgAwIBAgIQL9+89q6RUm0PmqPfQDQ+mjANBgkqhkiG9w0BAQUFADBM\n" + "MQswCQYDVQQGEwJaQTElMCMGA1UEChMcVGhhd3RlIENvbnN1bHRpbmcgKFB0eSkg\n") + "THRkLjEWMBQGA1UEAxMNVGhhd3RlIFNHQyBDQTAeFw0wOTEyMTgwMDAwMDBaFw0x\n") + "MTEyMTgyMzU5NTlaMGgxCzAJBgNVBAYTAlVTMRMwEQYDVQQIEwpDYWxpZm9ybmlh\n") + "MRYwFAYDVQQHFA1Nb3VudGFpbiBWaWV3MRMwEQYDVQQKFApHb29nbGUgSW5jMRcw\n") + "FQYDVQQDFA53d3cuZ29vZ2xlLmNvbTCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkC\n") + "gYEA6PmGD5D6htffvXImttdEAoN4c9kCKO+IRTn7EOh8rqk41XXGOOsKFQebg+jN\n") + "gtXj9xVoRaELGYW84u+E593y17iYwqG7tcFR39SDAqc9BkJb4SLD3muFXxzW2k6L\n") + "05vuuWciKh0R73mkszeK9P4Y/bz5RiNQl/Os/CRGK1w7t0UCAwEAAaOB5zCB5DAM\n") + "BgNVHRMBAf8EAjAAMDYGA1UdHwQvMC0wK6ApoCeGJWh0dHA6Ly9jcmwudGhhd3Rl\n") + "LmNvbS9UaGF3dGVTR0NDQS5jcmwwKAYDVR0lBCEwHwYIKwYBBQUHAwEGCCsGAQUF\n") + "BwMCBglghkgBhvhCBAEwcgYIKwYBBQUHAQEEZjBkMCIGCCsGAQUFBzABhhZodHRw\n") + "Oi8vb2NzcC50aGF3dGUuY29tMD4GCCsGAQUFBzAChjJodHRwOi8vd3d3LnRoYXd0\n") + "ZS5jb20vcmVwb3NpdG9yeS9UaGF3dGVfU0dDX0NBLmNydDANBgkqhkiG9w0BAQUF\n") + "AAOBgQCfQ89bxFApsb/isJr/aiEdLRLDLE5a+RLizrmCUi3nHX4adpaQedEkUjh5\n") + "u2ONgJd8IyAPkU0Wueru9G2Jysa9zCRo1kNbzipYvzwY4OA8Ys+WAi0oR1A04Se6\n") + "z5nRUP8pJcA2NhUzUnC+MY+f6H/nEQyNv4SgQhqAibAxWEEHXw==\n") + "-----END CERTIFICATE-----\n");

    private static final String VALID_CERTIFICATE_PEM_CRLF = "-----BEGIN CERTIFICATE-----\r\n" + ((((((((((((((((("MIIDITCCAoqgAwIBAgIQL9+89q6RUm0PmqPfQDQ+mjANBgkqhkiG9w0BAQUFADBM\r\n" + "MQswCQYDVQQGEwJaQTElMCMGA1UEChMcVGhhd3RlIENvbnN1bHRpbmcgKFB0eSkg\r\n") + "THRkLjEWMBQGA1UEAxMNVGhhd3RlIFNHQyBDQTAeFw0wOTEyMTgwMDAwMDBaFw0x\r\n") + "MTEyMTgyMzU5NTlaMGgxCzAJBgNVBAYTAlVTMRMwEQYDVQQIEwpDYWxpZm9ybmlh\r\n") + "MRYwFAYDVQQHFA1Nb3VudGFpbiBWaWV3MRMwEQYDVQQKFApHb29nbGUgSW5jMRcw\r\n") + "FQYDVQQDFA53d3cuZ29vZ2xlLmNvbTCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkC\r\n") + "gYEA6PmGD5D6htffvXImttdEAoN4c9kCKO+IRTn7EOh8rqk41XXGOOsKFQebg+jN\r\n") + "gtXj9xVoRaELGYW84u+E593y17iYwqG7tcFR39SDAqc9BkJb4SLD3muFXxzW2k6L\r\n") + "05vuuWciKh0R73mkszeK9P4Y/bz5RiNQl/Os/CRGK1w7t0UCAwEAAaOB5zCB5DAM\r\n") + "BgNVHRMBAf8EAjAAMDYGA1UdHwQvMC0wK6ApoCeGJWh0dHA6Ly9jcmwudGhhd3Rl\r\n") + "LmNvbS9UaGF3dGVTR0NDQS5jcmwwKAYDVR0lBCEwHwYIKwYBBQUHAwEGCCsGAQUF\r\n") + "BwMCBglghkgBhvhCBAEwcgYIKwYBBQUHAQEEZjBkMCIGCCsGAQUFBzABhhZodHRw\r\n") + "Oi8vb2NzcC50aGF3dGUuY29tMD4GCCsGAQUFBzAChjJodHRwOi8vd3d3LnRoYXd0\r\n") + "ZS5jb20vcmVwb3NpdG9yeS9UaGF3dGVfU0dDX0NBLmNydDANBgkqhkiG9w0BAQUF\r\n") + "AAOBgQCfQ89bxFApsb/isJr/aiEdLRLDLE5a+RLizrmCUi3nHX4adpaQedEkUjh5\r\n") + "u2ONgJd8IyAPkU0Wueru9G2Jysa9zCRo1kNbzipYvzwY4OA8Ys+WAi0oR1A04Se6\r\n") + "z5nRUP8pJcA2NhUzUnC+MY+f6H/nEQyNv4SgQhqAibAxWEEHXw==\r\n") + "-----END CERTIFICATE-----\r\n");

    private static final byte[] VALID_CERTIFICATE_PEM_HEADER = "-----BEGIN CERTIFICATE-----\n".getBytes();

    private static final byte[] VALID_CERTIFICATE_PEM_DATA = ("MIIDITCCAoqgAwIBAgIQL9+89q6RUm0PmqPfQDQ+mjANBgkqhkiG9w0BAQUFADBM" + ((((((((((((((("MQswCQYDVQQGEwJaQTElMCMGA1UEChMcVGhhd3RlIENvbnN1bHRpbmcgKFB0eSkg" + "THRkLjEWMBQGA1UEAxMNVGhhd3RlIFNHQyBDQTAeFw0wOTEyMTgwMDAwMDBaFw0x") + "MTEyMTgyMzU5NTlaMGgxCzAJBgNVBAYTAlVTMRMwEQYDVQQIEwpDYWxpZm9ybmlh") + "MRYwFAYDVQQHFA1Nb3VudGFpbiBWaWV3MRMwEQYDVQQKFApHb29nbGUgSW5jMRcw") + "FQYDVQQDFA53d3cuZ29vZ2xlLmNvbTCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkC") + "gYEA6PmGD5D6htffvXImttdEAoN4c9kCKO+IRTn7EOh8rqk41XXGOOsKFQebg+jN") + "gtXj9xVoRaELGYW84u+E593y17iYwqG7tcFR39SDAqc9BkJb4SLD3muFXxzW2k6L") + "05vuuWciKh0R73mkszeK9P4Y/bz5RiNQl/Os/CRGK1w7t0UCAwEAAaOB5zCB5DAM") + "BgNVHRMBAf8EAjAAMDYGA1UdHwQvMC0wK6ApoCeGJWh0dHA6Ly9jcmwudGhhd3Rl") + "LmNvbS9UaGF3dGVTR0NDQS5jcmwwKAYDVR0lBCEwHwYIKwYBBQUHAwEGCCsGAQUF") + "BwMCBglghkgBhvhCBAEwcgYIKwYBBQUHAQEEZjBkMCIGCCsGAQUFBzABhhZodHRw") + "Oi8vb2NzcC50aGF3dGUuY29tMD4GCCsGAQUFBzAChjJodHRwOi8vd3d3LnRoYXd0") + "ZS5jb20vcmVwb3NpdG9yeS9UaGF3dGVfU0dDX0NBLmNydDANBgkqhkiG9w0BAQUF") + "AAOBgQCfQ89bxFApsb/isJr/aiEdLRLDLE5a+RLizrmCUi3nHX4adpaQedEkUjh5") + "u2ONgJd8IyAPkU0Wueru9G2Jysa9zCRo1kNbzipYvzwY4OA8Ys+WAi0oR1A04Se6") + "z5nRUP8pJcA2NhUzUnC+MY+f6H/nEQyNv4SgQhqAibAxWEEHXw==")).getBytes();

    private static final byte[] VALID_CERTIFICATE_PEM_FOOTER = "\n-----END CERTIFICATE-----\n".getBytes();

    private static final String INVALID_CERTIFICATE_PEM = "-----BEGIN CERTIFICATE-----\n" + (((((((((((((((((((((((((((((((((("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n") + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n") + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n") + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n") + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n") + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n") + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n") + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n") + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n") + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n") + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n") + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n") + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n") + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n") + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n") + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n") + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n") + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n") + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n") + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n") + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n") + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n") + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n") + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n") + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n") + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n") + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n") + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n") + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n") + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n") + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n") + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n") + "AAAAAAAA\n") + "-----END CERTIFICATE-----");

    public void test_generateCertificate() throws Exception {
        Provider[] providers = Security.getProviders("CertificateFactory.X509");
        for (Provider p : providers) {
            CertificateFactory cf = CertificateFactory.getInstance("X509", p);
            try {
                test_generateCertificate(cf);
                test_generateCertificate_InputStream_Offset_Correct(cf);
                test_generateCertificate_InputStream_Empty(cf);
                test_generateCertificate_InputStream_InvalidStart_Failure(cf);
                test_generateCertificate_AnyLineLength_Success(cf);
            } catch (Throwable e) {
                throw new Exception(("Problem testing " + (p.getName())), e);
            }
        }
    }

    /**
     * Proxy that counts the number of bytes read from an InputStream.
     */
    private static class MeasuredInputStream extends InputStream {
        private long mCount = 0;

        private long mMarked = 0;

        private InputStream mStream;

        public MeasuredInputStream(InputStream is) {
            mStream = is;
        }

        public long getCount() {
            return mCount;
        }

        @Override
        public int read() throws IOException {
            int nextByte = mStream.read();
            (mCount)++;
            return nextByte;
        }

        @Override
        public int read(byte[] buffer) throws IOException {
            int count = mStream.read(buffer);
            mCount += count;
            return count;
        }

        @Override
        public int read(byte[] buffer, int offset, int length) throws IOException {
            int count = mStream.read(buffer, offset, length);
            mCount += count;
            return count;
        }

        @Override
        public long skip(long byteCount) throws IOException {
            long count = mStream.skip(byteCount);
            mCount += count;
            return count;
        }

        @Override
        public int available() throws IOException {
            return mStream.available();
        }

        @Override
        public void close() throws IOException {
            mStream.close();
        }

        @Override
        public void mark(int readlimit) {
            mMarked = mCount;
            mStream.mark(readlimit);
        }

        @Override
        public boolean markSupported() {
            return mStream.markSupported();
        }

        @Override
        public synchronized void reset() throws IOException {
            mCount = mMarked;
            mStream.reset();
        }
    }

    /* CertPath tests */
    public void testGenerateCertPath() throws Exception {
        CertificateFactoryTest.KeyHolder ca = CertificateFactoryTest.generateCertificate(true, null);
        CertificateFactoryTest.KeyHolder cert1 = CertificateFactoryTest.generateCertificate(true, ca);
        CertificateFactoryTest.KeyHolder cert2 = CertificateFactoryTest.generateCertificate(false, cert1);
        CertificateFactoryTest.KeyHolder cert3 = CertificateFactoryTest.generateCertificate(false, cert2);
        List<X509Certificate> certs = new ArrayList<X509Certificate>();
        certs.add(cert3.certificate);
        certs.add(cert2.certificate);
        certs.add(cert1.certificate);
        List<X509Certificate> duplicatedCerts = new ArrayList<X509Certificate>(certs);
        duplicatedCerts.add(cert2.certificate);
        Provider[] providers = Security.getProviders("CertificateFactory.X509");
        for (Provider p : providers) {
            final CertificateFactory cf = CertificateFactory.getInstance("X.509", p);
            // Duplicate certificates can cause an exception.
            {
                final CertPath duplicatedPath = cf.generateCertPath(duplicatedCerts);
                try {
                    duplicatedPath.getEncoded();
                    if (StandardNames.IS_RI) {
                        TestCase.fail(("duplicate certificates should cause failure: " + (p.getName())));
                    }
                } catch (CertificateEncodingException expected) {
                    if (!(StandardNames.IS_RI)) {
                        TestCase.fail(("duplicate certificates should pass: " + (p.getName())));
                    }
                }
            }
            testCertPathEncoding(cf, certs, null);
            /* Make sure all encoding entries are the same. */
            final Iterator<String> it1 = cf.getCertPathEncodings();
            final Iterator<String> it2 = cf.generateCertPath(certs).getEncodings();
            for (; ;) {
                TestCase.assertEquals(p.getName(), it1.hasNext(), it2.hasNext());
                if (!(it1.hasNext())) {
                    break;
                }
                String encoding = it1.next();
                TestCase.assertEquals(p.getName(), encoding, it2.next());
                try {
                    it1.remove();
                    TestCase.fail("Should not be able to remove from iterator");
                } catch (UnsupportedOperationException expected) {
                }
                try {
                    it2.remove();
                    TestCase.fail("Should not be able to remove from iterator");
                } catch (UnsupportedOperationException expected) {
                }
                /* Now test using this encoding. */
                testCertPathEncoding(cf, certs, encoding);
            }
        }
    }

    public static class KeyHolder {
        public X509Certificate certificate;

        public PrivateKey privateKey;
    }
}

