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
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import junit.framework.TestCase;


/**
 * Tests for <code>CipherSpi</code> class constructors and methods.
 */
public class CipherSpiTest extends TestCase {
    class Mock_CipherSpi extends myCipherSpi {
        @Override
        protected byte[] engineDoFinal(byte[] input, int inputOffset, int inputLen) throws BadPaddingException, IllegalBlockSizeException {
            return super.engineDoFinal(input, inputOffset, inputLen);
        }

        @Override
        protected int engineDoFinal(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws BadPaddingException, IllegalBlockSizeException, ShortBufferException {
            return super.engineDoFinal(input, inputOffset, inputLen, output, outputOffset);
        }

        @Override
        protected int engineGetBlockSize() {
            return super.engineGetBlockSize();
        }

        @Override
        protected byte[] engineGetIV() {
            return super.engineGetIV();
        }

        @Override
        protected int engineGetOutputSize(int inputLen) {
            return super.engineGetOutputSize(inputLen);
        }

        @Override
        protected AlgorithmParameters engineGetParameters() {
            return super.engineGetParameters();
        }

        @Override
        protected void engineInit(int opmode, Key key, SecureRandom random) throws InvalidKeyException {
            super.engineInit(opmode, key, random);
        }

        @Override
        protected void engineInit(int opmode, Key key, AlgorithmParameterSpec params, SecureRandom random) throws InvalidAlgorithmParameterException, InvalidKeyException {
            super.engineInit(opmode, key, params, random);
        }

        @Override
        protected void engineInit(int opmode, Key key, AlgorithmParameters params, SecureRandom random) throws InvalidAlgorithmParameterException, InvalidKeyException {
            super.engineInit(opmode, key, params, random);
        }

        @Override
        protected void engineSetMode(String mode) throws NoSuchAlgorithmException {
            super.engineSetMode(mode);
        }

        @Override
        protected void engineSetPadding(String padding) throws NoSuchPaddingException {
            super.engineSetPadding(padding);
        }

        @Override
        protected byte[] engineUpdate(byte[] input, int inputOffset, int inputLen) {
            return super.engineUpdate(input, inputOffset, inputLen);
        }

        @Override
        protected int engineUpdate(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws ShortBufferException {
            return super.engineUpdate(input, inputOffset, inputLen, output, outputOffset);
        }

        @Override
        protected int engineGetKeySize(Key key) throws InvalidKeyException {
            return super.engineGetKeySize(key);
        }

        @Override
        protected byte[] engineWrap(Key key) throws InvalidKeyException, IllegalBlockSizeException {
            return super.engineWrap(key);
        }

        @Override
        protected Key engineUnwrap(byte[] wrappedKey, String wrappedKeyAlgorithm, int wrappedKeyType) throws InvalidKeyException, NoSuchAlgorithmException {
            return super.engineUnwrap(wrappedKey, wrappedKeyAlgorithm, wrappedKeyType);
        }
    }

    /**
     * Test for <code>CipherSpi</code> constructor
     * Assertion: constructs CipherSpi
     */
    public void testCipherSpiTests01() throws BadPaddingException, IllegalBlockSizeException, ShortBufferException {
        CipherSpiTest.Mock_CipherSpi cSpi = new CipherSpiTest.Mock_CipherSpi();
        TestCase.assertEquals("BlockSize is not 0", cSpi.engineGetBlockSize(), 0);
        TestCase.assertEquals("OutputSize is not 0", cSpi.engineGetOutputSize(1), 0);
        byte[] bb = cSpi.engineGetIV();
        TestCase.assertEquals("Length of result byte array is not 0", bb.length, 0);
        TestCase.assertNull("Not null result", cSpi.engineGetParameters());
        byte[] bb1 = new byte[10];
        byte[] bb2 = new byte[10];
        bb = cSpi.engineUpdate(bb1, 1, 2);
        TestCase.assertEquals("Incorrect result of engineUpdate(byte, int, int)", bb.length, 2);
        bb = cSpi.engineDoFinal(bb1, 1, 2);
        TestCase.assertEquals("Incorrect result of engineDoFinal(byte, int, int)", 2, bb.length);
        TestCase.assertEquals("Incorrect result of engineUpdate(byte, int, int, byte, int)", cSpi.engineUpdate(bb1, 1, 2, bb2, 7), 2);
        TestCase.assertEquals("Incorrect result of engineDoFinal(byte, int, int, byte, int)", 2, cSpi.engineDoFinal(bb1, 1, 2, bb2, 0));
    }

    /**
     * Test for <code>engineGetKeySize(Key)</code> method
     * Assertion: It throws UnsupportedOperationException if it is not overridden
     */
    public void testCipherSpi02() throws Exception {
        CipherSpiTest.Mock_CipherSpi cSpi = new CipherSpiTest.Mock_CipherSpi();
        try {
            cSpi.engineGetKeySize(null);
            TestCase.fail("UnsupportedOperationException must be thrown");
        } catch (UnsupportedOperationException e) {
        }
    }

    /**
     * Test for <code>engineWrap(Key)</code> method
     * Assertion: It throws UnsupportedOperationException if it is not overridden
     */
    public void testCipherSpi03() throws Exception {
        CipherSpiTest.Mock_CipherSpi cSpi = new CipherSpiTest.Mock_CipherSpi();
        try {
            cSpi.engineWrap(null);
            TestCase.fail("UnsupportedOperationException must be thrown");
        } catch (UnsupportedOperationException e) {
        }
    }

    /**
     * Test for <code>engineUnwrap(byte[], String, int)</code> method
     * Assertion: It throws UnsupportedOperationException if it is not overridden
     */
    public void testCipherSpi04() throws Exception {
        CipherSpiTest.Mock_CipherSpi cSpi = new CipherSpiTest.Mock_CipherSpi();
        try {
            cSpi.engineUnwrap(new byte[0], "", 0);
            TestCase.fail("UnsupportedOperationException must be thrown");
        } catch (UnsupportedOperationException e) {
        }
    }

    /**
     * Test for <code>engineUpdate(ByteBuffer, ByteBuffer)</code> method
     * Assertions:
     * throws NullPointerException if one of these buffers is null;
     * throws ShortBufferException is there is no space in output to hold result
     */
    public void testCipherSpi05() throws ShortBufferException {
        CipherSpiTest.Mock_CipherSpi cSpi = new CipherSpiTest.Mock_CipherSpi();
        byte[] bb = new byte[]{ ((byte) (0)), ((byte) (1)), ((byte) (2)), ((byte) (3)), ((byte) (4)), ((byte) (5)), ((byte) (6)), ((byte) (7)), ((byte) (8)), ((byte) (9)), ((byte) (10)) };
        int pos = 5;
        int len = bb.length;
        ByteBuffer bbNull = null;
        ByteBuffer bb1 = ByteBuffer.allocate(len);
        bb1.put(bb);
        bb1.position(0);
        try {
            cSpi.engineUpdate(bbNull, bb1);
            TestCase.fail("NullPointerException must be thrown");
        } catch (NullPointerException e) {
        }
        try {
            cSpi.engineUpdate(bb1, bbNull);
            TestCase.fail("NullPointerException must be thrown");
        } catch (NullPointerException e) {
        }
        ByteBuffer bb2 = ByteBuffer.allocate(bb.length);
        bb1.position(len);
        TestCase.assertEquals("Incorrect number of stored bytes", 0, cSpi.engineUpdate(bb1, bb2));
        bb1.position(0);
        bb2.position((len - 2));
        try {
            cSpi.engineUpdate(bb1, bb2);
            TestCase.fail("ShortBufferException bust be thrown. Output buffer remaining: ".concat(Integer.toString(bb2.remaining())));
        } catch (ShortBufferException e) {
        }
        bb1.position(10);
        bb2.position(0);
        TestCase.assertTrue("Incorrect number of stored bytes", ((cSpi.engineUpdate(bb1, bb2)) > 0));
        bb1.position(bb.length);
        cSpi.engineUpdate(bb1, bb2);
        bb1.position(pos);
        bb2.position(0);
        int res = cSpi.engineUpdate(bb1, bb2);
        TestCase.assertTrue("Incorrect result", (res > 0));
    }

    /**
     * Test for <code>engineDoFinal(ByteBuffer, ByteBuffer)</code> method
     * Assertions:
     * throws NullPointerException if one of these buffers is null;
     * throws ShortBufferException is there is no space in output to hold result
     */
    public void testCipherSpi06() throws BadPaddingException, IllegalBlockSizeException, ShortBufferException {
        CipherSpiTest.Mock_CipherSpi cSpi = new CipherSpiTest.Mock_CipherSpi();
        int len = 10;
        byte[] bbuf = new byte[len];
        for (int i = 0; i < (bbuf.length); i++) {
            bbuf[i] = ((byte) (i));
        }
        ByteBuffer bb1 = ByteBuffer.wrap(bbuf);
        ByteBuffer bbNull = null;
        try {
            cSpi.engineDoFinal(bbNull, bb1);
            TestCase.fail("NullPointerException must be thrown");
        } catch (NullPointerException e) {
        }
        try {
            cSpi.engineDoFinal(bb1, bbNull);
            TestCase.fail("NullPointerException must be thrown");
        } catch (NullPointerException e) {
        }
        ByteBuffer bb2 = ByteBuffer.allocate(len);
        bb1.position(bb1.limit());
        TestCase.assertEquals("Incorrect result", 0, cSpi.engineDoFinal(bb1, bb2));
        bb1.position(0);
        bb2.position((len - 2));
        try {
            cSpi.engineDoFinal(bb1, bb2);
            TestCase.fail("ShortBufferException must be thrown. Output buffer remaining: ".concat(Integer.toString(bb2.remaining())));
        } catch (ShortBufferException e) {
        }
        int pos = 5;
        bb1.position(pos);
        bb2.position(0);
        TestCase.assertTrue("Incorrect result", ((cSpi.engineDoFinal(bb1, bb2)) > 0));
    }
}

