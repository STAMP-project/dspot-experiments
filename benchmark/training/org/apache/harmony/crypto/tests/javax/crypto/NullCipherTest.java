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
 * @author Boris V. Kuznetsov
 * @version $Revision$
 */
package org.apache.harmony.crypto.tests.javax.crypto;


import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.NullCipher;
import javax.crypto.spec.SecretKeySpec;
import junit.framework.TestCase;


/**
 * Tests for NullCipher
 */
public class NullCipherTest extends TestCase {
    private Cipher c;

    public void testGetAlgorithm() {
        c.getAlgorithm();
    }

    public void testGetBlockSize() {
        TestCase.assertEquals("Incorrect BlockSize", 1, c.getBlockSize());
    }

    public void testGetOutputSize() {
        TestCase.assertEquals("Incorrect OutputSize", 111, c.getOutputSize(111));
    }

    public void testGetIV() {
        TestCase.assertTrue("Incorrect IV", Arrays.equals(c.getIV(), new byte[8]));
    }

    public void testGetParameters() {
        TestCase.assertNull("Incorrect Parameters", c.getParameters());
    }

    public void testGetExemptionMechanism() {
        TestCase.assertNull("Incorrect ExemptionMechanism", c.getExemptionMechanism());
    }

    /* Class under test for void init(int, Key) */
    public void testInitintKey() throws Exception {
        c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(new byte[1], "algorithm"));
    }

    /* Class under test for void init(int, Key, SecureRandom) */
    public void testInitintKeySecureRandom() throws Exception {
        c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(new byte[1], "algorithm"), new SecureRandom());
    }

    /* Class under test for void init(int, Key, AlgorithmParameterSpec) */
    public void testInitintKeyAlgorithmParameterSpec() throws Exception {
        class myAlgorithmParameterSpec implements AlgorithmParameterSpec {}
        c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(new byte[1], "algorithm"), new myAlgorithmParameterSpec());
    }

    /* Class under test for byte[] update(byte[]) */
    public void testUpdatebyteArray() throws Exception {
        byte[] b = new byte[]{ 1, 2, 3, 4, 5 };
        byte[] r = c.update(b);
        TestCase.assertEquals("different length", b.length, r.length);
        TestCase.assertTrue("different content", Arrays.equals(b, r));
    }

    /* Class under test for byte[] update(byte[], int, int) */
    public void testUpdatebyteArrayintint() throws Exception {
        byte[] b = new byte[]{ 1, 2, 3, 4, 5 };
        byte[] r = c.update(b, 0, 5);
        TestCase.assertEquals("different length", b.length, r.length);
        TestCase.assertTrue("different content", Arrays.equals(b, r));
        r = c.update(b, 1, 3);
        TestCase.assertEquals("different length", 3, r.length);
        for (int i = 0; i < 3; i++) {
            TestCase.assertEquals("different content", b[(i + 1)], r[i]);
        }
    }

    /* Class under test for int update(byte[], int, int, byte[]) */
    public void testUpdatebyteArrayintintbyteArray() throws Exception {
        byte[] b = new byte[]{ 1, 2, 3, 4, 5 };
        byte[] r = new byte[5];
        c.update(b, 0, 5, r);
        TestCase.assertTrue("different content", Arrays.equals(b, r));
    }

    /* Class under test for int update(byte[], int, int, byte[], int) */
    public void testUpdatebyteArrayintintbyteArrayint() throws Exception {
        byte[] b = new byte[]{ 1, 2, 3, 4, 5 };
        byte[] r = new byte[5];
        c.update(b, 0, 5, r, 0);
        TestCase.assertTrue("different content", Arrays.equals(b, r));
    }

    /* Class under test for byte[] doFinal() */
    public void testDoFinal() throws Exception {
        TestCase.assertNull("doFinal failed", c.doFinal());
    }

    /* Class under test for int doFinal(byte[], int) */
    public void testDoFinalbyteArrayint() throws Exception {
        byte[] r = new byte[5];
        TestCase.assertEquals("doFinal failed", 0, c.doFinal(r, 0));
    }

    /* Class under test for byte[] doFinal(byte[]) */
    public void testDoFinalbyteArray() throws Exception {
        byte[] b = new byte[]{ 1, 2, 3, 4, 5 };
        byte[] r = null;
        r = c.doFinal(b);
        TestCase.assertEquals("different length", b.length, r.length);
        TestCase.assertTrue("different content", Arrays.equals(b, r));
    }

    /* Class under test for byte[] doFinal(byte[], int, int) */
    public void testDoFinalbyteArrayintint() throws Exception {
        byte[] b = new byte[]{ 1, 2, 3, 4, 5 };
        byte[] r = null;
        r = c.doFinal(b, 0, 5);
        TestCase.assertEquals("different length", b.length, r.length);
        TestCase.assertTrue("different content", Arrays.equals(b, r));
        r = c.doFinal(b, 1, 3);
        TestCase.assertEquals("different length", 3, r.length);
        for (int i = 0; i < 3; i++) {
            TestCase.assertEquals("different content", b[(i + 1)], r[i]);
        }
    }

    /* Class under test for byte[] update(byte[], int, int) */
    public void testUpdatebyteArrayintint2() {
        // Regression for HARMONY-758
        try {
            new NullCipher().update(new byte[1], 1, Integer.MAX_VALUE);
            TestCase.fail("Expected IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException e) {
        }
    }

    /* Class under test for int doFinal(byte[], int, int, byte[]) */
    public void testDoFinalbyteArrayintintbyteArray() throws Exception {
        byte[] b = new byte[]{ 1, 2, 3, 4, 5 };
        byte[] r = new byte[5];
        c.doFinal(b, 0, 5, r);
        TestCase.assertTrue("different content", Arrays.equals(b, r));
    }

    /* Class under test for int doFinal(byte[], int, int, byte[]) */
    public void testDoFinalbyteArrayintintbyteArray2() throws Exception {
        // Regression for HARMONY-758
        try {
            new NullCipher().update(new byte[1], 1, Integer.MAX_VALUE, new byte[1]);
            TestCase.fail("Expected IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException e) {
        }
    }

    /* Class under test for int doFinal(byte[], int, int, byte[]) */
    public void testDoFinalbyteArrayintintbyteArray3() throws Exception {
        // Regression for HARMONY-758
        try {
            new NullCipher().update(new byte[1], 0, 1, new byte[0]);
            TestCase.fail("Expected IndexOutOfBoundsException was not thrown");
        } catch (IndexOutOfBoundsException e) {
        }
    }

    /* Class under test for int doFinal(byte[], int, int, byte[], int) */
    public void testDoFinalbyteArrayintintbyteArrayint() throws Exception {
        byte[] b = new byte[]{ 1, 2, 3, 4, 5 };
        byte[] r = new byte[5];
        c.doFinal(b, 0, 5, r, 0);
        TestCase.assertTrue("different content", Arrays.equals(b, r));
    }

    /* Class under test for int doFinal(byte[], int, int, byte[], int) */
    public void testDoFinalbyteArrayintintbyteArrayint2() throws Exception {
        // Regression for HARMONY-758
        try {
            new NullCipher().update(new byte[1], 1, Integer.MAX_VALUE, new byte[1], 0);
            TestCase.fail("Expected IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException e) {
        }
    }

    /* Class under test for int doFinal(byte[], int, int, byte[], int) */
    public void testDoFinalbyteArrayintintbyteArrayint3() throws Exception {
        // Regression for HARMONY-758
        try {
            new NullCipher().update(new byte[1], 0, 1, new byte[0], 0);
            TestCase.fail("Expected IndexOutOfBoundsException was not thrown");
        } catch (IndexOutOfBoundsException e) {
        }
    }
}

