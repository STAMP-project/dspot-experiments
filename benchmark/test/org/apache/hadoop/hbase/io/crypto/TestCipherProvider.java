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
package org.apache.hadoop.hbase.io.crypto;


import AES.KEY_LENGTH;
import HConstants.CIPHER_AES;
import HConstants.CRYPTO_CIPHERPROVIDER_CONF_KEY;
import HConstants.CRYPTO_KEY_ALGORITHM_CONF_KEY;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.util.Arrays;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MiscTests.class, SmallTests.class })
public class TestCipherProvider {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCipherProvider.class);

    public static class MyCipherProvider implements CipherProvider {
        private Configuration conf;

        @Override
        public Configuration getConf() {
            return conf;
        }

        @Override
        public void setConf(Configuration conf) {
            this.conf = conf;
        }

        @Override
        public String getName() {
            return TestCipherProvider.MyCipherProvider.class.getName();
        }

        @Override
        public String[] getSupportedCiphers() {
            return new String[]{ "TEST" };
        }

        @Override
        public Cipher getCipher(String name) {
            if (name.equals("TEST")) {
                return new Cipher(this) {
                    @Override
                    public String getName() {
                        return "TEST";
                    }

                    @Override
                    public int getKeyLength() {
                        return 0;
                    }

                    @Override
                    public int getIvLength() {
                        return 0;
                    }

                    @Override
                    public Key getRandomKey() {
                        return null;
                    }

                    @Override
                    public Encryptor getEncryptor() {
                        return null;
                    }

                    @Override
                    public Decryptor getDecryptor() {
                        return null;
                    }

                    @Override
                    public OutputStream createEncryptionStream(OutputStream out, Context context, byte[] iv) throws IOException {
                        return null;
                    }

                    @Override
                    public OutputStream createEncryptionStream(OutputStream out, Encryptor encryptor) throws IOException {
                        return null;
                    }

                    @Override
                    public InputStream createDecryptionStream(InputStream in, Context context, byte[] iv) throws IOException {
                        return null;
                    }

                    @Override
                    public InputStream createDecryptionStream(InputStream in, Decryptor decryptor) throws IOException {
                        return null;
                    }
                };
            }
            return null;
        }
    }

    @Test
    public void testCustomProvider() {
        Configuration conf = HBaseConfiguration.create();
        conf.set(CRYPTO_CIPHERPROVIDER_CONF_KEY, TestCipherProvider.MyCipherProvider.class.getName());
        CipherProvider provider = Encryption.getCipherProvider(conf);
        Assert.assertTrue((provider instanceof TestCipherProvider.MyCipherProvider));
        Assert.assertTrue(Arrays.asList(provider.getSupportedCiphers()).contains("TEST"));
        Cipher a = Encryption.getCipher(conf, "TEST");
        Assert.assertNotNull(a);
        Assert.assertTrue(((a.getProvider()) instanceof TestCipherProvider.MyCipherProvider));
        Assert.assertEquals("TEST", a.getName());
        Assert.assertEquals(0, a.getKeyLength());
    }

    @Test
    public void testDefaultProvider() {
        Configuration conf = HBaseConfiguration.create();
        CipherProvider provider = Encryption.getCipherProvider(conf);
        Assert.assertTrue((provider instanceof DefaultCipherProvider));
        String algorithm = conf.get(CRYPTO_KEY_ALGORITHM_CONF_KEY, CIPHER_AES);
        Assert.assertTrue(Arrays.asList(provider.getSupportedCiphers()).contains(algorithm));
        Cipher a = Encryption.getCipher(conf, algorithm);
        Assert.assertNotNull(a);
        Assert.assertTrue(((a.getProvider()) instanceof DefaultCipherProvider));
        Assert.assertEquals(a.getName(), algorithm);
        Assert.assertEquals(KEY_LENGTH, a.getKeyLength());
    }
}

