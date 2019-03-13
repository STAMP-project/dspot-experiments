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
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import junit.framework.TestCase;
import org.apache.harmony.crypto.tests.support.MyMacSpi;


/**
 * Tests for <code>MacSpi</code> class constructors and methods.
 */
public class MacSpiTest extends TestCase {
    class Mock_MacSpi extends MyMacSpi {
        @Override
        protected byte[] engineDoFinal() {
            return super.engineDoFinal();
        }

        @Override
        protected int engineGetMacLength() {
            return super.engineGetMacLength();
        }

        @Override
        protected void engineInit(Key key, AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException, InvalidKeyException {
            super.engineInit(key, params);
        }

        @Override
        protected void engineReset() {
            super.engineReset();
        }

        @Override
        protected void engineUpdate(byte input) {
            super.engineUpdate(input);
        }

        @Override
        protected void engineUpdate(byte[] input, int offset, int len) {
            super.engineUpdate(input, offset, len);
        }
    }

    class Mock_MacSpi1 extends MyMacSpi1 {
        @Override
        protected byte[] engineDoFinal() {
            return super.engineDoFinal();
        }

        @Override
        protected int engineGetMacLength() {
            return super.engineGetMacLength();
        }

        @Override
        protected void engineInit(Key key, AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException, InvalidKeyException {
            super.engineInit(key, params);
        }

        @Override
        protected void engineReset() {
            super.engineReset();
        }

        @Override
        protected void engineUpdate(byte input) {
            super.engineUpdate(input);
        }

        @Override
        protected void engineUpdate(byte[] input, int offset, int len) {
            super.engineUpdate(input, offset, len);
        }

        protected void engineUpdate(ByteBuffer input) {
            super.engineUpdate(input);
        }
    }

    class Mock_MacSpi2 extends MyMacSpi2 {
        @Override
        protected byte[] engineDoFinal() {
            return super.engineDoFinal();
        }

        @Override
        protected int engineGetMacLength() {
            return super.engineGetMacLength();
        }

        @Override
        protected void engineInit(Key key, AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException, InvalidKeyException {
            super.engineInit(key, params);
        }

        @Override
        protected void engineReset() {
            super.engineReset();
        }

        @Override
        protected void engineUpdate(byte input) {
            super.engineUpdate(input);
        }

        @Override
        protected void engineUpdate(byte[] input, int offset, int len) {
            super.engineUpdate(input, offset, len);
        }

        protected void engineUpdate(ByteBuffer input) {
            super.engineUpdate(input);
        }
    }

    /**
     * Test for <code>MacSpi</code> constructor
     * Assertion: constructs MacSpi
     */
    public void testMacSpiTests01() throws Exception {
        MacSpiTest.Mock_MacSpi mSpi = new MacSpiTest.Mock_MacSpi();
        byte[] bb1 = new byte[]{ ((byte) (1)), ((byte) (2)), ((byte) (3)), ((byte) (4)), ((byte) (5)) };
        SecretKeySpec sks = new SecretKeySpec(bb1, "SHA1");
        TestCase.assertEquals("Incorrect MacLength", mSpi.engineGetMacLength(), 0);
        try {
            mSpi.engineInit(null, null);
            TestCase.fail("IllegalArgumentException must be thrown");
        } catch (IllegalArgumentException e) {
        }
        mSpi.engineInit(sks, null);
        byte[] bb = mSpi.engineDoFinal();
        TestCase.assertEquals(bb.length, 0);
        try {
            mSpi.clone();
            TestCase.fail("CloneNotSupportedException was not thrown as expected");
        } catch (CloneNotSupportedException e) {
        }
        MacSpiTest.Mock_MacSpi1 mSpi1 = new MacSpiTest.Mock_MacSpi1();
        mSpi1.clone();
        byte[] bbb = new byte[10];
        for (int i = 0; i < (bbb.length); i++) {
            bbb[i] = ((byte) (i));
        }
        try {
            mSpi1.engineInit(null, null);
            TestCase.fail("IllegalArgumentException must be thrown");
        } catch (IllegalArgumentException e) {
        }
        mSpi1.engineInit(sks, null);
        ByteBuffer byteBuf = ByteBuffer.allocate(10);
        byteBuf.put(bbb);
        byteBuf.position(5);
        int beforeUp = byteBuf.remaining();
        mSpi1.engineUpdate(byteBuf);
        bb = mSpi1.engineDoFinal();
        TestCase.assertEquals("Incorrect result of engineDoFinal", bb.length, beforeUp);
        MacSpiTest.Mock_MacSpi2 mSpi2 = new MacSpiTest.Mock_MacSpi2();
        mSpi2.engineInit(null, null);
        mSpi2.engineInit(sks, null);
        try {
            mSpi2.clone();
        } catch (CloneNotSupportedException e) {
        }
        byte[] bbuf = new byte[]{ ((byte) (5)), ((byte) (4)), ((byte) (3)), ((byte) (2)), ((byte) (1)) };
        byteBuf = ByteBuffer.allocate(5);
        byteBuf.put(bbuf);
        byteBuf.position(5);
        if (!(byteBuf.hasRemaining())) {
            mSpi2.engineUpdate(byteBuf);
        }
    }
}

