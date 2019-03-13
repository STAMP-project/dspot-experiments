/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.crypto;


import OpensslCipher.ENCRYPT_MODE;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public class TestOpensslCipher {
    private static final byte[] key = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 16, 17, 18, 19, 20, 21, 22 };

    private static final byte[] iv = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8 };

    @Test(timeout = 120000)
    public void testGetInstance() throws Exception {
        Assume.assumeTrue(((OpensslCipher.getLoadingFailureReason()) == null));
        OpensslCipher cipher = OpensslCipher.getInstance("AES/CTR/NoPadding");
        Assert.assertTrue((cipher != null));
        try {
            cipher = OpensslCipher.getInstance("AES2/CTR/NoPadding");
            Assert.fail("Should specify correct algorithm.");
        } catch (NoSuchAlgorithmException e) {
            // Expect NoSuchAlgorithmException
        }
        try {
            cipher = OpensslCipher.getInstance("AES/CTR/NoPadding2");
            Assert.fail("Should specify correct padding.");
        } catch (NoSuchPaddingException e) {
            // Expect NoSuchPaddingException
        }
    }

    @Test(timeout = 120000)
    public void testUpdateArguments() throws Exception {
        Assume.assumeTrue(((OpensslCipher.getLoadingFailureReason()) == null));
        OpensslCipher cipher = OpensslCipher.getInstance("AES/CTR/NoPadding");
        Assert.assertTrue((cipher != null));
        cipher.init(ENCRYPT_MODE, TestOpensslCipher.key, TestOpensslCipher.iv);
        // Require direct buffers
        ByteBuffer input = ByteBuffer.allocate(1024);
        ByteBuffer output = ByteBuffer.allocate(1024);
        try {
            cipher.update(input, output);
            Assert.fail("Input and output buffer should be direct buffer.");
        } catch (IllegalArgumentException e) {
            GenericTestUtils.assertExceptionContains("Direct buffers are required", e);
        }
        // Output buffer length should be sufficient to store output data
        input = ByteBuffer.allocateDirect(1024);
        output = ByteBuffer.allocateDirect(1000);
        try {
            cipher.update(input, output);
            Assert.fail(("Output buffer length should be sufficient " + "to store output data"));
        } catch (ShortBufferException e) {
            GenericTestUtils.assertExceptionContains("Output buffer is not sufficient", e);
        }
    }

    @Test(timeout = 120000)
    public void testDoFinalArguments() throws Exception {
        Assume.assumeTrue(((OpensslCipher.getLoadingFailureReason()) == null));
        OpensslCipher cipher = OpensslCipher.getInstance("AES/CTR/NoPadding");
        Assert.assertTrue((cipher != null));
        cipher.init(ENCRYPT_MODE, TestOpensslCipher.key, TestOpensslCipher.iv);
        // Require direct buffer
        ByteBuffer output = ByteBuffer.allocate(1024);
        try {
            cipher.doFinal(output);
            Assert.fail("Output buffer should be direct buffer.");
        } catch (IllegalArgumentException e) {
            GenericTestUtils.assertExceptionContains("Direct buffer is required", e);
        }
    }
}

