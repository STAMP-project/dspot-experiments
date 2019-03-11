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


import CommonConfigurationKeysPublic.HADOOP_SECURITY_SECURE_RANDOM_IMPL_KEY;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.crypto.random.OsSecureRandom;
import org.apache.hadoop.fs.CommonConfigurationKeysPublic;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.Whitebox;
import org.junit.Assert;
import org.junit.Test;


public class TestCryptoStreamsWithOpensslAesCtrCryptoCodec extends TestCryptoStreams {
    @Test
    public void testCodecClosesRandom() throws Exception {
        GenericTestUtils.assumeInNativeProfile();
        Configuration conf = new Configuration();
        conf.set(CommonConfigurationKeysPublic.HADOOP_SECURITY_CRYPTO_CODEC_CLASSES_AES_CTR_NOPADDING_KEY, OpensslAesCtrCryptoCodec.class.getName());
        conf.set(HADOOP_SECURITY_SECURE_RANDOM_IMPL_KEY, OsSecureRandom.class.getName());
        CryptoCodec codecWithRandom = CryptoCodec.getInstance(conf);
        Assert.assertNotNull(((("Unable to instantiate codec " + (OpensslAesCtrCryptoCodec.class.getName())) + ", is the required ") + "version of OpenSSL installed?"), codecWithRandom);
        OsSecureRandom random = ((OsSecureRandom) (Whitebox.getInternalState(codecWithRandom, "random")));
        // trigger the OsSecureRandom to create an internal FileInputStream
        random.nextBytes(new byte[10]);
        Assert.assertNotNull(Whitebox.getInternalState(random, "stream"));
        // verify closing the codec closes the codec's random's stream.
        codecWithRandom.close();
        Assert.assertNull(Whitebox.getInternalState(random, "stream"));
    }
}

