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
package org.apache.harmony.crypto.tests.javax.crypto;


import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.security.spec.PSSParameterSpec;
import java.util.Arrays;
import javax.crypto.Mac;
import javax.crypto.MacSpi;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.DHGenParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import junit.framework.TestCase;
import org.apache.harmony.crypto.tests.support.MyMacSpi;
import org.apache.harmony.security.tests.support.SpiEngUtils;


/**
 * Tests for Mac class constructors and methods
 */
public class MacTest extends TestCase {
    public static final String srvMac = "Mac";

    private static String defaultAlgorithm = null;

    private static String defaultProviderName = null;

    private static Provider defaultProvider = null;

    private static boolean DEFSupported = false;

    private static final String NotSupportedMsg = "There is no suitable provider for Mac";

    private static final String[] invalidValues = SpiEngUtils.invalidValues;

    private static String[] validValues = new String[3];

    public static final String[] validAlgorithmsMac = new String[]{ "HmacSHA1", "HmacMD5", "HmacSHA256", "HmacSHA384", "HmacSHA512" };

    static {
        for (int i = 0; i < (MacTest.validAlgorithmsMac.length); i++) {
            MacTest.defaultProvider = SpiEngUtils.isSupport(MacTest.validAlgorithmsMac[i], MacTest.srvMac);
            MacTest.DEFSupported = (MacTest.defaultProvider) != null;
            if (MacTest.DEFSupported) {
                MacTest.defaultAlgorithm = MacTest.validAlgorithmsMac[i];
                MacTest.defaultProviderName = MacTest.defaultProvider.getName();
                MacTest.validValues[0] = MacTest.defaultAlgorithm;
                MacTest.validValues[1] = MacTest.defaultAlgorithm.toUpperCase();
                MacTest.validValues[2] = MacTest.defaultAlgorithm.toLowerCase();
                break;
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm)</code> method
     * Assertion:
     * throws NullPointerException when algorithm is null
     * throws NoSuchAlgorithmException when algorithm is not available
     */
    public void testMac01() {
        try {
            Mac.getInstance(null);
            TestCase.fail("NullPointerException or NoSuchAlgorithmException should be thrown when algorithm is null");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < (MacTest.invalidValues.length); i++) {
            try {
                Mac.getInstance(MacTest.invalidValues[i]);
                TestCase.fail("NoSuchAlgorithmException must be thrown when algorithm is not available: ".concat(MacTest.invalidValues[i]));
            } catch (NoSuchAlgorithmException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm)</code> method
     * Assertion: returns Mac object
     */
    public void testMac02() throws NoSuchAlgorithmException {
        if (!(MacTest.DEFSupported)) {
            TestCase.fail(MacTest.NotSupportedMsg);
            return;
        }
        Mac mac;
        for (int i = 0; i < (MacTest.validValues.length); i++) {
            mac = Mac.getInstance(MacTest.validValues[i]);
            TestCase.assertEquals("Incorrect algorithm", mac.getAlgorithm(), MacTest.validValues[i]);
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code> method
     * Assertion:
     * throws IllegalArgumentException when provider is null or empty
     * throws NoSuchProviderException when provider is not available
     */
    public void testMac03() throws NoSuchAlgorithmException, NoSuchProviderException {
        if (!(MacTest.DEFSupported)) {
            TestCase.fail(MacTest.NotSupportedMsg);
            return;
        }
        String provider = null;
        for (int i = 0; i < (MacTest.validValues.length); i++) {
            try {
                Mac.getInstance(MacTest.validValues[i], provider);
                TestCase.fail("IllegalArgumentException must be thrown when provider is null");
            } catch (IllegalArgumentException e) {
            }
            try {
                Mac.getInstance(MacTest.validValues[i], "");
                TestCase.fail("IllegalArgumentException must be thrown when provider is empty");
            } catch (IllegalArgumentException e) {
            }
            for (int j = 1; j < (MacTest.invalidValues.length); j++) {
                try {
                    Mac.getInstance(MacTest.validValues[i], MacTest.invalidValues[j]);
                    TestCase.fail("NoSuchProviderException must be thrown (algorithm: ".concat(MacTest.validValues[i]).concat(" provider: ").concat(MacTest.invalidValues[j]).concat(")"));
                } catch (NoSuchProviderException e) {
                }
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code> method
     * Assertion:
     * throws NullPointerException when algorithm is null
     * throws NoSuchAlgorithmException when algorithm is not available
     */
    public void testMac04() throws IllegalArgumentException, NoSuchAlgorithmException, NoSuchProviderException {
        if (!(MacTest.DEFSupported)) {
            TestCase.fail(MacTest.NotSupportedMsg);
            return;
        }
        try {
            Mac.getInstance(null, MacTest.defaultProviderName);
            TestCase.fail("NullPointerException or NoSuchAlgorithmException should be thrown when algorithm is null");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < (MacTest.invalidValues.length); i++) {
            try {
                Mac.getInstance(MacTest.invalidValues[i], MacTest.defaultProviderName);
                TestCase.fail("NoSuchAlgorithmException must be throws when algorithm is not available: ".concat(MacTest.invalidValues[i]));
            } catch (NoSuchAlgorithmException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code> method
     * Assertion: returns Mac object
     */
    public void testMac05() throws IllegalArgumentException, NoSuchAlgorithmException, NoSuchProviderException {
        if (!(MacTest.DEFSupported)) {
            TestCase.fail(MacTest.NotSupportedMsg);
            return;
        }
        Mac mac;
        for (int i = 0; i < (MacTest.validValues.length); i++) {
            mac = Mac.getInstance(MacTest.validValues[i], MacTest.defaultProviderName);
            TestCase.assertEquals("Incorrect algorithm", mac.getAlgorithm(), MacTest.validValues[i]);
            TestCase.assertEquals("Incorrect provider", mac.getProvider().getName(), MacTest.defaultProviderName);
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, Provider provider)</code> method
     * Assertion: throws IllegalArgumentException when provider is null
     */
    public void testMac06() throws NoSuchAlgorithmException, NoSuchProviderException {
        if (!(MacTest.DEFSupported)) {
            TestCase.fail(MacTest.NotSupportedMsg);
            return;
        }
        Provider provider = null;
        for (int i = 0; i < (MacTest.validValues.length); i++) {
            try {
                Mac.getInstance(MacTest.validValues[i], provider);
                TestCase.fail("IllegalArgumentException must be thrown when provider is null");
            } catch (IllegalArgumentException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, Provider provider)</code> method
     * Assertion:
     * throws NullPointerException when algorithm is null
     * throws NoSuchAlgorithmException when algorithm is not available
     */
    public void testMac07() throws IllegalArgumentException, NoSuchAlgorithmException, NoSuchProviderException {
        if (!(MacTest.DEFSupported)) {
            TestCase.fail(MacTest.NotSupportedMsg);
            return;
        }
        try {
            Mac.getInstance(null, MacTest.defaultProvider);
            TestCase.fail("NullPointerException or NoSuchAlgorithmException should be thrown when algorithm is null");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < (MacTest.invalidValues.length); i++) {
            try {
                Mac.getInstance(MacTest.invalidValues[i], MacTest.defaultProvider);
                TestCase.fail("NoSuchAlgorithmException must be thrown when algorithm is not available: ".concat(MacTest.invalidValues[i]));
            } catch (NoSuchAlgorithmException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, Provider provider)</code> method
     * Assertion: returns Mac object
     */
    public void testMac08() throws IllegalArgumentException, NoSuchAlgorithmException, NoSuchProviderException {
        if (!(MacTest.DEFSupported)) {
            TestCase.fail(MacTest.NotSupportedMsg);
            return;
        }
        Mac mac;
        for (int i = 0; i < (MacTest.validValues.length); i++) {
            mac = Mac.getInstance(MacTest.validValues[i], MacTest.defaultProvider);
            TestCase.assertEquals("Incorrect algorithm", mac.getAlgorithm(), MacTest.validValues[i]);
            TestCase.assertEquals("Incorrect provider", mac.getProvider(), MacTest.defaultProvider);
        }
    }

    /**
     * Test for <code>update</code> and <code>doFinal</code> methods
     * Assertion: throws IllegalStateException when Mac is not initialized
     *
     * @throws Exception
     * 		
     */
    public void testMac09() throws Exception {
        if (!(MacTest.DEFSupported)) {
            TestCase.fail(MacTest.NotSupportedMsg);
            return;
        }
        Mac[] macs = createMacs();
        TestCase.assertNotNull("Mac objects were not created", macs);
        byte[] buf = new byte[10];
        ByteBuffer bBuf = ByteBuffer.wrap(buf, 0, 10);
        byte[] bb = new byte[]{ ((byte) (1)), ((byte) (2)), ((byte) (3)), ((byte) (4)), ((byte) (5)) };
        SecretKeySpec sks = new SecretKeySpec(bb, "SHA1");
        for (int i = 0; i < (macs.length); i++) {
            try {
                macs[i].update(((byte) (0)));
                TestCase.fail("IllegalStateException must be thrown");
            } catch (IllegalStateException e) {
            }
            try {
                macs[i].update(buf);
                TestCase.fail("IllegalStateException must be thrown");
            } catch (IllegalStateException e) {
            }
            try {
                macs[i].update(buf, 0, 3);
                TestCase.fail("IllegalStateException must be thrown");
            } catch (IllegalStateException e) {
            }
            try {
                macs[i].update(bBuf);
                TestCase.fail("IllegalStateException must be thrown");
            } catch (IllegalStateException e) {
            }
            try {
                macs[i].doFinal();
                TestCase.fail("IllegalStateException must be thrown");
            } catch (IllegalStateException e) {
            }
            try {
                macs[i].doFinal(new byte[10]);
                TestCase.fail("IllegalStateException must be thrown");
            } catch (IllegalStateException e) {
            }
            try {
                macs[i].doFinal(new byte[10], 0);
                TestCase.fail("IllegalStateException must be thrown");
            } catch (IllegalStateException e) {
            }
            macs[i].init(sks);
            try {
                macs[i].doFinal(new byte[1], 0);
                TestCase.fail("ShortBufferException expected");
            } catch (ShortBufferException e) {
                // expected
            }
        }
    }

    /**
     * Test for <code>doFinal(byte[] output, int outOffset)</code> method
     * Assertion:
     * throws ShotBufferException when outOffset  is negative or
     * outOffset >= output.length  or when given buffer is small
     */
    public void testMac10() throws IllegalArgumentException, IllegalStateException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException {
        if (!(MacTest.DEFSupported)) {
            TestCase.fail(MacTest.NotSupportedMsg);
            return;
        }
        Mac[] macs = createMacs();
        TestCase.assertNotNull("Mac objects were not created", macs);
        byte[] b = new byte[]{ ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)) };
        byte[] byteA = new byte[b.length];
        SecretKeySpec sks = new SecretKeySpec(b, "SHA1");
        for (int i = 0; i < (macs.length); i++) {
            macs[i].init(sks);
            try {
                macs[i].doFinal(null, 10);
                TestCase.fail("ShortBufferException must be thrown");
            } catch (ShortBufferException e) {
            }
            try {
                macs[i].doFinal(byteA, (-4));
                TestCase.fail("ShortBufferException must be thrown");
            } catch (ShortBufferException e) {
            }
            try {
                macs[i].doFinal(byteA, 10);
                TestCase.fail("ShortBufferException must be thrown");
            } catch (ShortBufferException e) {
            }
            try {
                macs[i].doFinal(new byte[1], 0);
                TestCase.fail("ShortBufferException must be thrown");
            } catch (ShortBufferException e) {
            }
            byte[] res = macs[i].doFinal();
            try {
                macs[i].doFinal(new byte[(res.length) - 1], 0);
                TestCase.fail("ShortBufferException must be thrown");
            } catch (ShortBufferException e) {
            }
        }
    }

    /**
     * Test for <code>doFinal(byte[] output, int outOffset)</code> and
     * <code>doFinal()</code> methods Assertion: Mac result is stored in
     * output buffer
     */
    public void testMac11() throws IllegalArgumentException, IllegalStateException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, ShortBufferException {
        if (!(MacTest.DEFSupported)) {
            TestCase.fail(MacTest.NotSupportedMsg);
            return;
        }
        Mac[] macs = createMacs();
        TestCase.assertNotNull("Mac objects were not created", macs);
        byte[] b = new byte[]{ ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)) };
        SecretKeySpec scs = new SecretKeySpec(b, "SHA1");
        for (int i = 0; i < (macs.length); i++) {
            macs[i].init(scs);
            byte[] res1 = macs[i].doFinal();
            byte[] res2 = new byte[(res1.length) + 10];
            macs[i].doFinal(res2, 0);
            for (int j = 0; j < (res1.length); j++) {
                TestCase.assertEquals("Not equals byte number: ".concat(Integer.toString(j)), res1[j], res2[j]);
            }
        }
    }

    /**
     * Test for <code>doFinal(byte[] input)</code> method
     * Assertion: update Mac and returns result
     */
    public void testMac12() throws IllegalArgumentException, IllegalStateException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException {
        if (!(MacTest.DEFSupported)) {
            TestCase.fail(MacTest.NotSupportedMsg);
            return;
        }
        Mac[] macs = createMacs();
        TestCase.assertNotNull("Mac objects were not created", macs);
        byte[] b = new byte[]{ ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)) };
        byte[] upd = new byte[]{ ((byte) (5)), ((byte) (4)), ((byte) (3)), ((byte) (2)), ((byte) (1)), ((byte) (0)) };
        SecretKeySpec scs = new SecretKeySpec(b, "SHA1");
        for (int i = 0; i < (macs.length); i++) {
            macs[i].init(scs);
            byte[] res1 = macs[i].doFinal();
            byte[] res2 = macs[i].doFinal();
            TestCase.assertEquals("Results are not the same", IntegralToString.bytesToHexString(res1, false), IntegralToString.bytesToHexString(res2, false));
            res2 = macs[i].doFinal(upd);
            macs[i].update(upd);
            res1 = macs[i].doFinal();
            TestCase.assertEquals("Results are not the same", IntegralToString.bytesToHexString(res1, false), IntegralToString.bytesToHexString(res2, false));
        }
    }

    /**
     * Test for <code>update(byte[] input, int outset, int len)</code> method
     * Assertion: throws IllegalArgumentException when offset or len is negative,
     * offset + len >= input.length
     */
    public void testMac13() throws IllegalArgumentException, IllegalStateException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException {
        if (!(MacTest.DEFSupported)) {
            TestCase.fail(MacTest.NotSupportedMsg);
            return;
        }
        Mac[] macs = createMacs();
        TestCase.assertNotNull("Mac objects were not created", macs);
        byte[] b = new byte[]{ ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)) };
        SecretKeySpec scs = new SecretKeySpec(b, "SHA1");
        for (int i = 0; i < (macs.length); i++) {
            macs[i].init(scs);
            try {
                macs[i].update(b, (-10), b.length);
                TestCase.fail("IllegalArgumentException must be thrown");
            } catch (IllegalArgumentException e) {
            }
            try {
                macs[i].update(b, 0, (-10));
                TestCase.fail("IllegalArgumentException must be thrown");
            } catch (IllegalArgumentException e) {
            }
            try {
                macs[i].update(b, 0, ((b.length) + 1));
                TestCase.fail("IllegalArgumentException must be thrown");
            } catch (IllegalArgumentException e) {
            }
            try {
                macs[i].update(b, ((b.length) - 1), 2);
                TestCase.fail("IllegalArgumentException must be thrown");
            } catch (IllegalArgumentException e) {
            }
        }
    }

    /**
     * Test for <code>update(byte[] input, int outset, int len)</code> and
     * <code>update(byte[] input</code>
     * methods
     * Assertion: updates Mac
     */
    public void testMac14() throws IllegalArgumentException, IllegalStateException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException {
        if (!(MacTest.DEFSupported)) {
            TestCase.fail(MacTest.NotSupportedMsg);
            return;
        }
        Mac[] macs = createMacs();
        TestCase.assertNotNull("Mac objects were not created", macs);
        byte[] b = new byte[]{ ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)) };
        byte[] upd1 = new byte[]{ ((byte) (0)), ((byte) (1)), ((byte) (5)), ((byte) (4)), ((byte) (3)), ((byte) (2)) };
        byte[] upd2 = new byte[]{ ((byte) (5)), ((byte) (4)), ((byte) (3)), ((byte) (2)) };
        byte[] res1;
        byte[] res2;
        SecretKeySpec scs = new SecretKeySpec(b, "SHA1");
        for (int i = 0; i < (macs.length); i++) {
            macs[i].init(scs);
            macs[i].update(upd1, 2, 4);
            res1 = macs[i].doFinal();
            macs[i].init(scs);
            macs[i].update(upd2);
            res2 = macs[i].doFinal();
            TestCase.assertEquals("Results are not the same", res1.length, res2.length);
            for (int t = 0; t < (res1.length); t++) {
                TestCase.assertEquals("Results are not the same", res1[t], res2[t]);
            }
            macs[i].init(scs);
            macs[i].update(((byte) (5)));
            res1 = macs[i].doFinal();
            macs[i].init(scs);
            macs[i].update(upd1, 2, 1);
            res2 = macs[i].doFinal();
            TestCase.assertEquals("Results are not the same", res1.length, res2.length);
            for (int t = 0; t < (res1.length); t++) {
                TestCase.assertEquals("Results are not the same", res1[t], res2[t]);
            }
        }
    }

    /**
     * Test for <code>clone()</code> method
     * Assertion: returns Mac object or throws CloneNotSupportedException
     */
    public void testMacClone() throws CloneNotSupportedException, NoSuchAlgorithmException {
        if (!(MacTest.DEFSupported)) {
            TestCase.fail(MacTest.NotSupportedMsg);
            return;
        }
        Mac[] macs = createMacs();
        TestCase.assertNotNull("Mac objects were not created", macs);
        for (int i = 0; i < (macs.length); i++) {
            try {
                Mac mac1 = ((Mac) (macs[i].clone()));
                TestCase.assertEquals(mac1.getAlgorithm(), macs[i].getAlgorithm());
                TestCase.assertEquals(mac1.getProvider(), macs[i].getProvider());
                TestCase.assertFalse(macs[i].equals(mac1));
            } catch (CloneNotSupportedException e) {
            }
        }
    }

    /**
     * Test for
     * <code>init(Key key, AlgorithmParameterSpec params)</code>
     * <code>init(Key key)</code>
     * methods
     * Assertion: throws InvalidKeyException and InvalidAlgorithmParameterException
     * when parameters are not appropriate
     */
    public void testInit() throws IllegalArgumentException, IllegalStateException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException {
        if (!(MacTest.DEFSupported)) {
            TestCase.fail(MacTest.NotSupportedMsg);
            return;
        }
        Mac[] macs = createMacs();
        TestCase.assertNotNull("Mac objects were not created", macs);
        byte[] b = new byte[]{ ((byte) (1)), ((byte) (2)), ((byte) (3)), ((byte) (4)), ((byte) (5)) };
        SecretKeySpec sks = new SecretKeySpec(b, "SHA1");
        DHGenParameterSpec algPS = new DHGenParameterSpec(1, 2);
        PSSParameterSpec algPSS = new PSSParameterSpec(20);
        SecretKeySpec sks1 = new SecretKeySpec(b, "RSA");
        for (int i = 0; i < (macs.length); i++) {
            macs[i].init(sks);
            try {
                macs[i].init(sks1, algPSS);
                TestCase.fail("init(..) accepts incorrect AlgorithmParameterSpec parameter");
            } catch (InvalidAlgorithmParameterException e) {
            }
            try {
                macs[i].init(sks, algPS);
                TestCase.fail("init(..) accepts incorrect AlgorithmParameterSpec parameter");
            } catch (InvalidAlgorithmParameterException e) {
            }
            try {
                macs[i].init(null, null);
                TestCase.fail("InvalidKeyException must be thrown");
            } catch (InvalidKeyException e) {
            }
            try {
                macs[i].init(null);
                TestCase.fail("InvalidKeyException must be thrown");
            } catch (InvalidKeyException e) {
            }
            // macs[i].init(sks, null);
        }
    }

    /**
     * Test for <code>update(ByteBuffer input)</code>
     * <code>update(byte[] input, int offset, int len)</code>
     * methods
     * Assertion: processes Mac; if input is null then do nothing
     */
    public void testUpdateByteBuffer01() throws IllegalArgumentException, IllegalStateException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException {
        if (!(MacTest.DEFSupported)) {
            TestCase.fail(MacTest.NotSupportedMsg);
            return;
        }
        Mac[] macs = createMacs();
        TestCase.assertNotNull("Mac objects were not created", macs);
        byte[] bb = new byte[]{ ((byte) (1)), ((byte) (2)), ((byte) (3)), ((byte) (4)), ((byte) (5)) };
        SecretKeySpec sks = new SecretKeySpec(bb, "SHA1");
        ByteBuffer byteNull = null;
        ByteBuffer byteBuff = ByteBuffer.allocate(0);
        byte[] bb1;
        byte[] bb2;
        for (int i = 0; i < (macs.length); i++) {
            macs[i].init(sks);
            bb1 = macs[i].doFinal();
            try {
                macs[i].update(byteNull);
                TestCase.fail("IllegalArgumentException must be thrown because buffer is null");
            } catch (IllegalArgumentException e) {
            }
            macs[i].update(byteBuff);
            bb2 = macs[i].doFinal();
            for (int t = 0; t < (bb1.length); t++) {
                TestCase.assertEquals("Incorrect doFinal result", bb1[t], bb2[t]);
            }
            macs[i].init(sks);
            bb1 = macs[i].doFinal();
            macs[i].update(null, 0, 0);
            bb2 = macs[i].doFinal();
            for (int t = 0; t < (bb1.length); t++) {
                TestCase.assertEquals("Incorrect doFinal result", bb1[t], bb2[t]);
            }
        }
    }

    /**
     * Test for <code>update(ByteBuffer input)</code>
     * <code>update(byte[] input, int offset, int len)</code>
     * methods
     * Assertion: processes Mac
     */
    public void testUpdateByteBuffer02() throws IllegalArgumentException, IllegalStateException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException {
        if (!(MacTest.DEFSupported)) {
            TestCase.fail(MacTest.NotSupportedMsg);
            return;
        }
        Mac[] macs = createMacs();
        TestCase.assertNotNull("Mac objects were not created", macs);
        byte[] bb = new byte[]{ ((byte) (1)), ((byte) (2)), ((byte) (3)), ((byte) (4)), ((byte) (5)) };
        SecretKeySpec sks = new SecretKeySpec(bb, "SHA1");
        byte[] bbuf = new byte[]{ ((byte) (5)), ((byte) (4)), ((byte) (3)), ((byte) (2)), ((byte) (1)) };
        ByteBuffer byteBuf;
        byte[] bb1;
        byte[] bb2;
        for (int i = 0; i < (macs.length); i++) {
            byteBuf = ByteBuffer.allocate(5);
            byteBuf.put(bbuf);
            byteBuf.position(2);
            macs[i].init(sks);
            macs[i].update(byteBuf);
            bb1 = macs[i].doFinal();
            macs[i].init(sks);
            macs[i].update(bbuf, 2, 3);
            bb2 = macs[i].doFinal();
            for (int t = 0; t < (bb1.length); t++) {
                TestCase.assertEquals("Incorrect doFinal result", bb1[t], bb2[t]);
            }
        }
    }

    /**
     * Test for <code>clone()</code> method
     * Assertion: clone if provider is clo
     */
    public void testClone() {
        if (!(MacTest.DEFSupported)) {
            TestCase.fail(MacTest.NotSupportedMsg);
            return;
        }
        Mac[] macs = createMacs();
        TestCase.assertNotNull("Mac objects were not created", macs);
        Mac res;
        for (int i = 0; i < (macs.length); i++) {
            try {
                res = ((Mac) (macs[i].clone()));
                TestCase.assertTrue("Object should not be equals", (!(macs[i].equals(res))));
                TestCase.assertEquals("Incorrect class", macs[i].getClass(), res.getClass());
            } catch (CloneNotSupportedException e) {
            }
        }
    }

    /**
     * Test for <code>getMacLength()</code> method
     * Assertion: return Mac length
     */
    public void testGetMacLength() {
        if (!(MacTest.DEFSupported)) {
            TestCase.fail(MacTest.NotSupportedMsg);
            return;
        }
        Mac[] macs = createMacs();
        TestCase.assertNotNull("Mac objects were not created", macs);
        for (int i = 0; i < (macs.length); i++) {
            TestCase.assertTrue("Length should be positive", ((macs[i].getMacLength()) >= 0));
        }
    }

    /**
     * Test for <code>reset()</code> method
     * Assertion: return Mac length
     */
    public void testReset() throws InvalidKeyException {
        if (!(MacTest.DEFSupported)) {
            TestCase.fail(MacTest.NotSupportedMsg);
            return;
        }
        Mac[] macs = createMacs();
        TestCase.assertNotNull("Mac objects were not created", macs);
        byte[] bb = new byte[]{ ((byte) (1)), ((byte) (2)), ((byte) (3)), ((byte) (4)), ((byte) (5)) };
        SecretKeySpec sks = new SecretKeySpec(bb, "SHA1");
        byte[] bbuf = new byte[]{ ((byte) (5)), ((byte) (4)), ((byte) (3)), ((byte) (2)), ((byte) (1)) };
        byte[] bb1;
        byte[] bb2;
        for (int i = 0; i < (macs.length); i++) {
            macs[i].init(sks);
            bb1 = macs[i].doFinal();
            macs[i].reset();
            bb2 = macs[i].doFinal();
            TestCase.assertEquals("incorrect result", bb1.length, bb2.length);
            for (int t = 0; t < (bb1.length); t++) {
                TestCase.assertEquals("Incorrect doFinal result", bb1[t], bb2[t]);
            }
            macs[i].reset();
            macs[i].update(bbuf);
            bb1 = macs[i].doFinal();
            macs[i].reset();
            macs[i].update(bbuf, 0, bbuf.length);
            bb2 = macs[i].doFinal();
            TestCase.assertEquals("incorrect result", bb1.length, bb2.length);
            for (int t = 0; t < (bb1.length); t++) {
                TestCase.assertEquals("Incorrect doFinal result", bb1[t], bb2[t]);
            }
        }
    }

    /**
     * Test for <code>Mac</code> constructor
     * Assertion: returns Mac object
     */
    public void testMacConstructor() throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchAlgorithmException {
        if (!(MacTest.DEFSupported)) {
            TestCase.fail(MacTest.NotSupportedMsg);
            return;
        }
        MacSpi spi = new MyMacSpi();
        Mac mac = new myMac(spi, MacTest.defaultProvider, MacTest.defaultAlgorithm);
        TestCase.assertEquals("Incorrect algorithm", mac.getAlgorithm(), MacTest.defaultAlgorithm);
        TestCase.assertEquals("Incorrect provider", mac.getProvider(), MacTest.defaultProvider);
        try {
            mac.init(null, null);
            TestCase.fail("Exception should be thrown because init(..) uses incorrect parameters");
        } catch (Exception e) {
        }
        TestCase.assertEquals("Invalid mac length", mac.getMacLength(), 0);
        mac = new myMac(null, null, null);
        TestCase.assertNull("Algorithm must be null", mac.getAlgorithm());
        TestCase.assertNull("Provider must be null", mac.getProvider());
        try {
            mac.init(null, null);
            TestCase.fail("Exception should be thrown because init(..) uses incorrect parameters");
        } catch (Exception e) {
        }
        try {
            mac.getMacLength();
            TestCase.fail("NullPointerException must be thrown");
        } catch (NullPointerException e) {
        }
    }

    public void test_getAlgorithm() throws NoSuchAlgorithmException {
        Mac mac;
        for (int i = 0; i < (MacTest.validValues.length); i++) {
            mac = Mac.getInstance(MacTest.validValues[i]);
            TestCase.assertEquals("Incorrect algorithm", mac.getAlgorithm(), MacTest.validValues[i]);
        }
        mac = new MacTest.Mock_Mac(null, null, null);
        TestCase.assertNull(mac.getAlgorithm());
    }

    public void test_getProvider() throws NoSuchAlgorithmException {
        Mac mac;
        for (int i = 0; i < (MacTest.validValues.length); i++) {
            mac = Mac.getInstance(MacTest.validValues[i]);
            TestCase.assertNotNull(mac.getProvider());
        }
        mac = new MacTest.Mock_Mac(null, null, null);
        TestCase.assertNull(mac.getProvider());
    }

    private static final byte[] TEST_INPUT = new byte[]{ 1, ((byte) (255)), 85, ((byte) (170)) };

    public void test_ConsistentBetweenProviders() throws Exception {
        SecretKey key = new SecretKeySpec(new byte[]{ ((byte) (123)), ((byte) (16)), ((byte) (109)), ((byte) (104)), ((byte) (63)), ((byte) (112)), ((byte) (163)), ((byte) (181)), ((byte) (163)), ((byte) (221)), ((byte) (159)), ((byte) (84)), ((byte) (116)), ((byte) (54)), ((byte) (222)), ((byte) (167)), ((byte) (136)), ((byte) (129)), ((byte) (13)), ((byte) (137)), ((byte) (239)), ((byte) (46)), ((byte) (66)), ((byte) (79)) }, "HmacMD5");
        byte[] label = new byte[]{ ((byte) (107)), ((byte) (101)), ((byte) (121)), ((byte) (32)), ((byte) (101)), ((byte) (120)), ((byte) (112)), ((byte) (97)), ((byte) (110)), ((byte) (115)), ((byte) (105)), ((byte) (111)), ((byte) (110)) };
        byte[] seed = new byte[]{ ((byte) (80)), ((byte) (249)), ((byte) (206)), ((byte) (20)), ((byte) (178)), ((byte) (221)), ((byte) (61)), ((byte) (250)), ((byte) (150)), ((byte) (217)), ((byte) (254)), ((byte) (58)), ((byte) (26)), ((byte) (229)), ((byte) (121)), ((byte) (85)), ((byte) (231)), ((byte) (188)), ((byte) (132)), ((byte) (104)), ((byte) (14)), ((byte) (45)), ((byte) (32)), ((byte) (208)), ((byte) (110)), ((byte) (180)), ((byte) (3)), ((byte) (191)), ((byte) (162)), ((byte) (230)), ((byte) (196)), ((byte) (157)), ((byte) (80)), ((byte) (249)), ((byte) (206)), ((byte) (20)), ((byte) (188)), ((byte) (197)), ((byte) (158)), ((byte) (154)), ((byte) (54)), ((byte) (167)), ((byte) (170)), ((byte) (254)), ((byte) (59)), ((byte) (202)), ((byte) (203)), ((byte) (76)), ((byte) (250)), ((byte) (135)), ((byte) (154)), ((byte) (172)), ((byte) (2)), ((byte) (37)), ((byte) (206)), ((byte) (218)), ((byte) (116)), ((byte) (16)), ((byte) (134)), ((byte) (156)), ((byte) (3)), ((byte) (24)), ((byte) (15)), ((byte) (226)) };
        Provider[] providers = Security.getProviders("Mac.HmacMD5");
        Provider defProvider = null;
        byte[] output = null;
        byte[] output2 = null;
        for (int i = 0; i < (providers.length); i++) {
            System.out.println(("provider = " + (providers[i].getName())));
            Mac mac = Mac.getInstance("HmacMD5", providers[i]);
            mac.init(key);
            mac.update(label);
            mac.update(seed);
            if (output == null) {
                output = new byte[mac.getMacLength()];
                defProvider = providers[i];
                mac.doFinal(output, 0);
                mac.init(new SecretKeySpec(label, "HmacMD5"));
                output2 = mac.doFinal(output);
            } else {
                byte[] tmp = new byte[mac.getMacLength()];
                mac.doFinal(tmp, 0);
                TestCase.assertEquals((((defProvider.getName()) + " vs. ") + (providers[i].getName())), Arrays.toString(output), Arrays.toString(tmp));
                mac.init(new SecretKeySpec(label, "HmacMD5"));
                TestCase.assertEquals((((defProvider.getName()) + " vs. ") + (providers[i].getName())), Arrays.toString(output2), Arrays.toString(mac.doFinal(output)));
            }
        }
    }

    class Mock_Mac extends Mac {
        protected Mock_Mac(MacSpi arg0, Provider arg1, String arg2) {
            super(arg0, arg1, arg2);
        }
    }
}

