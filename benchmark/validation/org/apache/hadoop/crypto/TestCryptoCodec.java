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


import java.security.SecureRandom;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.NativeCodeLoader;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestCryptoCodec {
    private static final Logger LOG = LoggerFactory.getLogger(TestCryptoCodec.class);

    private static byte[] key = new byte[16];

    private static byte[] iv = new byte[16];

    private static final int bufferSize = 4096;

    private Configuration conf = new Configuration();

    private int count = 10000;

    private int seed = new Random().nextInt();

    private final String jceCodecClass = "org.apache.hadoop.crypto.JceAesCtrCryptoCodec";

    private final String opensslCodecClass = "org.apache.hadoop.crypto.OpensslAesCtrCryptoCodec";

    @Test(timeout = 120000)
    public void testJceAesCtrCryptoCodec() throws Exception {
        GenericTestUtils.assumeInNativeProfile();
        if (!(NativeCodeLoader.buildSupportsOpenssl())) {
            TestCryptoCodec.LOG.warn("Skipping test since openSSL library not loaded");
            Assume.assumeTrue(false);
        }
        Assert.assertEquals(null, OpensslCipher.getLoadingFailureReason());
        cryptoCodecTest(conf, seed, 0, jceCodecClass, jceCodecClass, TestCryptoCodec.iv);
        cryptoCodecTest(conf, seed, count, jceCodecClass, jceCodecClass, TestCryptoCodec.iv);
        cryptoCodecTest(conf, seed, count, jceCodecClass, opensslCodecClass, TestCryptoCodec.iv);
        // Overflow test, IV: xx xx xx xx xx xx xx xx ff ff ff ff ff ff ff ff
        for (int i = 0; i < 8; i++) {
            TestCryptoCodec.iv[(8 + i)] = ((byte) (255));
        }
        cryptoCodecTest(conf, seed, count, jceCodecClass, jceCodecClass, TestCryptoCodec.iv);
        cryptoCodecTest(conf, seed, count, jceCodecClass, opensslCodecClass, TestCryptoCodec.iv);
    }

    @Test(timeout = 120000)
    public void testOpensslAesCtrCryptoCodec() throws Exception {
        GenericTestUtils.assumeInNativeProfile();
        if (!(NativeCodeLoader.buildSupportsOpenssl())) {
            TestCryptoCodec.LOG.warn("Skipping test since openSSL library not loaded");
            Assume.assumeTrue(false);
        }
        Assert.assertEquals(null, OpensslCipher.getLoadingFailureReason());
        cryptoCodecTest(conf, seed, 0, opensslCodecClass, opensslCodecClass, TestCryptoCodec.iv);
        cryptoCodecTest(conf, seed, count, opensslCodecClass, opensslCodecClass, TestCryptoCodec.iv);
        cryptoCodecTest(conf, seed, count, opensslCodecClass, jceCodecClass, TestCryptoCodec.iv);
        // Overflow test, IV: xx xx xx xx xx xx xx xx ff ff ff ff ff ff ff ff
        for (int i = 0; i < 8; i++) {
            TestCryptoCodec.iv[(8 + i)] = ((byte) (255));
        }
        cryptoCodecTest(conf, seed, count, opensslCodecClass, opensslCodecClass, TestCryptoCodec.iv);
        cryptoCodecTest(conf, seed, count, opensslCodecClass, jceCodecClass, TestCryptoCodec.iv);
    }

    /**
     * Regression test for IV calculation, see HADOOP-11343
     */
    @Test(timeout = 120000)
    public void testCalculateIV() throws Exception {
        JceAesCtrCryptoCodec codec = new JceAesCtrCryptoCodec();
        codec.setConf(conf);
        SecureRandom sr = new SecureRandom();
        byte[] initIV = new byte[16];
        byte[] IV = new byte[16];
        long iterations = 1000;
        long counter = 10000;
        // Overflow test, IV: 00 00 00 00 00 00 00 00 ff ff ff ff ff ff ff ff
        for (int i = 0; i < 8; i++) {
            initIV[(8 + i)] = ((byte) (255));
        }
        for (long j = 0; j < counter; j++) {
            assertIVCalculation(codec, initIV, j, IV);
        }
        // Random IV and counter sequence test
        for (long i = 0; i < iterations; i++) {
            sr.nextBytes(initIV);
            for (long j = 0; j < counter; j++) {
                assertIVCalculation(codec, initIV, j, IV);
            }
        }
        // Random IV and random counter test
        for (long i = 0; i < iterations; i++) {
            sr.nextBytes(initIV);
            for (long j = 0; j < counter; j++) {
                long c = sr.nextLong();
                assertIVCalculation(codec, initIV, c, IV);
            }
        }
    }
}

