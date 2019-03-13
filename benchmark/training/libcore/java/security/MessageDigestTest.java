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
package libcore.java.security;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;


public final class MessageDigestTest extends TestCase {
    private final byte[] sha_456 = new byte[]{ -24, 9, -59, -47, -50, -92, 123, 69, -29, 71, 1, -46, 63, 96, -118, -102, 88, 3, 77, -55 };

    public void testShaReset() throws NoSuchAlgorithmException {
        MessageDigest sha = MessageDigest.getInstance("SHA");
        sha.update(new byte[]{ 1, 2, 3 });
        sha.reset();
        sha.update(new byte[]{ 4, 5, 6 });
        TestCase.assertEquals(Arrays.toString(sha_456), Arrays.toString(sha.digest()));
    }

    public void test_getInstance() throws Exception {
        Provider[] providers = Security.getProviders();
        for (Provider provider : providers) {
            Set<Provider.Service> services = provider.getServices();
            for (Provider.Service service : services) {
                String type = service.getType();
                if (!(type.equals("MessageDigest"))) {
                    continue;
                }
                String algorithm = service.getAlgorithm();
                try {
                    // MessageDigest.getInstance(String)
                    MessageDigest md1 = MessageDigest.getInstance(algorithm);
                    TestCase.assertEquals(algorithm, md1.getAlgorithm());
                    test_MessageDigest(md1);
                    // MessageDigest.getInstance(String, Provider)
                    MessageDigest md2 = MessageDigest.getInstance(algorithm, provider);
                    TestCase.assertEquals(algorithm, md2.getAlgorithm());
                    TestCase.assertEquals(provider, md2.getProvider());
                    test_MessageDigest(md2);
                    // MessageDigest.getInstance(String, String)
                    MessageDigest md3 = MessageDigest.getInstance(algorithm, provider.getName());
                    TestCase.assertEquals(algorithm, md3.getAlgorithm());
                    TestCase.assertEquals(provider, md3.getProvider());
                    test_MessageDigest(md3);
                } catch (Exception e) {
                    throw new Exception(("Problem testing MessageDigest." + algorithm), e);
                }
            }
        }
    }

    private static final Map<String, Map<String, byte[]>> EXPECTATIONS = new HashMap<String, Map<String, byte[]>>();

    private static final String INPUT_EMPTY = "empty";

    private static final String INPUT_256MB = "256mb";

    static {
        // INPUT_EMPTY
        MessageDigestTest.putExpectation("MD2", MessageDigestTest.INPUT_EMPTY, new byte[]{ -125, 80, -27, -93, -30, 76, 21, 61, -14, 39, 92, -97, -128, 105, 39, 115 });
        MessageDigestTest.putExpectation("MD5", MessageDigestTest.INPUT_EMPTY, new byte[]{ -44, 29, -116, -39, -113, 0, -78, 4, -23, -128, 9, -104, -20, -8, 66, 126 });
        MessageDigestTest.putExpectation("SHA", MessageDigestTest.INPUT_EMPTY, new byte[]{ -38, 57, -93, -18, 94, 107, 75, 13, 50, 85, -65, -17, -107, 96, 24, -112, -81, -40, 7, 9 });
        MessageDigestTest.putExpectation("SHA-1", MessageDigestTest.INPUT_EMPTY, new byte[]{ -38, 57, -93, -18, 94, 107, 75, 13, 50, 85, -65, -17, -107, 96, 24, -112, -81, -40, 7, 9 });
        MessageDigestTest.putExpectation("SHA-256", MessageDigestTest.INPUT_EMPTY, new byte[]{ -29, -80, -60, 66, -104, -4, 28, 20, -102, -5, -12, -56, -103, 111, -71, 36, 39, -82, 65, -28, 100, -101, -109, 76, -92, -107, -103, 27, 120, 82, -72, 85 });
        MessageDigestTest.putExpectation("SHA-384", MessageDigestTest.INPUT_EMPTY, new byte[]{ 56, -80, 96, -89, 81, -84, -106, 56, 76, -39, 50, 126, -79, -79, -29, 106, 33, -3, -73, 17, 20, -66, 7, 67, 76, 12, -57, -65, 99, -10, -31, -38, 39, 78, -34, -65, -25, 111, 101, -5, -43, 26, -46, -15, 72, -104, -71, 91 });
        MessageDigestTest.putExpectation("SHA-512", MessageDigestTest.INPUT_EMPTY, new byte[]{ -49, -125, -31, 53, 126, -17, -72, -67, -15, 84, 40, 80, -42, 109, -128, 7, -42, 32, -28, 5, 11, 87, 21, -36, -125, -12, -87, 33, -45, 108, -23, -50, 71, -48, -47, 60, 93, -123, -14, -80, -1, -125, 24, -46, -121, 126, -20, 47, 99, -71, 49, -67, 71, 65, 122, -127, -91, 56, 50, 122, -7, 39, -38, 62 });
        // Regression test for a SHA-1 problem with inputs larger than 256 MiB. http://b/4501620
        // In mid-2013 this takes 3 minutes even on the host, so let's not run it on devices.
        if ((System.getenv("ANDROID_BUILD_TOP")) != null) {
            // INPUT_256MB
            MessageDigestTest.putExpectation("MD2", MessageDigestTest.INPUT_256MB, new byte[]{ -63, -120, 6, 67, 12, -87, -39, -11, -67, -3, -31, -41, -91, 16, -35, 91 });
            MessageDigestTest.putExpectation("MD5", MessageDigestTest.INPUT_256MB, new byte[]{ 31, 80, 57, -27, 11, -42, 107, 41, 12, 86, 104, 77, -123, 80, -58, -62 });
            MessageDigestTest.putExpectation("SHA", MessageDigestTest.INPUT_256MB, new byte[]{ 123, -111, -37, -36, 86, -59, 120, 30, -33, 108, -120, 71, -76, -86, 105, 101, 86, 108, 92, 117 });
            MessageDigestTest.putExpectation("SHA-1", MessageDigestTest.INPUT_256MB, new byte[]{ 123, -111, -37, -36, 86, -59, 120, 30, -33, 108, -120, 71, -76, -86, 105, 101, 86, 108, 92, 117 });
            MessageDigestTest.putExpectation("SHA-256", MessageDigestTest.INPUT_256MB, new byte[]{ -90, -41, 42, -57, 105, 15, 83, -66, 106, -28, 107, -88, -123, 6, -67, -105, 48, 42, 9, 63, 113, 8, 71, 43, -39, -17, -61, -50, -3, -96, 100, -124 });
            MessageDigestTest.putExpectation("SHA-384", MessageDigestTest.INPUT_256MB, new byte[]{ 71, 72, 77, -83, -110, 22, -118, -18, -58, 119, 115, 74, -67, -36, 84, 122, -105, -67, -75, 15, -33, 37, 78, -95, 4, 118, -53, 106, 65, -115, -19, 121, -59, -94, -45, -111, -124, 35, 35, 60, 67, -34, 62, 106, -16, 122, -110, -14 });
            MessageDigestTest.putExpectation("SHA-512", MessageDigestTest.INPUT_256MB, new byte[]{ 36, 7, -120, 39, -87, -87, 84, -40, -66, 114, 62, -73, 107, 101, -117, -12, -124, 20, 109, 103, -92, 125, 111, 102, 12, 114, -68, 100, 30, 25, -88, 62, 108, 56, 9, -107, 89, -25, -50, 118, -87, 100, 13, 37, -14, 66, -40, -97, 105, -27, 79, -62, 53, -31, 83, 40, 4, 57, 90, -81, 63, -77, -42, 113 });
        }
    }
}

