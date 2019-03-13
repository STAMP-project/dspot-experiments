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
package org.apache.hadoop.crypto.key;


import KeyProvider.DEFAULT_BITLENGTH;
import KeyProvider.DEFAULT_BITLENGTH_NAME;
import KeyProvider.DEFAULT_CIPHER;
import KeyProvider.DEFAULT_CIPHER_NAME;
import KeyProvider.KeyVersion;
import KeyProvider.Metadata;
import KeyProvider.Options;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestKeyProvider {
    private static final String CIPHER = "AES";

    @Test
    public void testBuildVersionName() throws Exception {
        Assert.assertEquals("/a/b@3", KeyProvider.buildVersionName("/a/b", 3));
        Assert.assertEquals("/aaa@12", KeyProvider.buildVersionName("/aaa", 12));
    }

    @Test
    public void testParseVersionName() throws Exception {
        Assert.assertEquals("/a/b", KeyProvider.getBaseName("/a/b@3"));
        Assert.assertEquals("/aaa", KeyProvider.getBaseName("/aaa@112"));
        try {
            KeyProvider.getBaseName("no-slashes");
            Assert.assertTrue("should have thrown", false);
        } catch (IOException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testKeyMaterial() throws Exception {
        byte[] key1 = new byte[]{ 1, 2, 3, 4 };
        KeyProvider.KeyVersion obj = new KeyProvider.KeyVersion("key1", "key1@1", key1);
        Assert.assertEquals("key1@1", obj.getVersionName());
        Assert.assertArrayEquals(new byte[]{ 1, 2, 3, 4 }, obj.getMaterial());
    }

    @Test
    public void testMetadata() throws Exception {
        // Metadata without description
        DateFormat format = new SimpleDateFormat("y/m/d");
        Date date = format.parse("2013/12/25");
        KeyProvider.Metadata meta = new KeyProvider.Metadata("myCipher", 100, null, null, date, 123);
        Assert.assertEquals("myCipher", meta.getCipher());
        Assert.assertEquals(100, meta.getBitLength());
        Assert.assertNull(meta.getDescription());
        Assert.assertEquals(date, meta.getCreated());
        Assert.assertEquals(123, meta.getVersions());
        KeyProvider.Metadata second = new KeyProvider.Metadata(meta.serialize());
        Assert.assertEquals(meta.getCipher(), second.getCipher());
        Assert.assertEquals(meta.getBitLength(), second.getBitLength());
        Assert.assertNull(second.getDescription());
        Assert.assertTrue(second.getAttributes().isEmpty());
        Assert.assertEquals(meta.getCreated(), second.getCreated());
        Assert.assertEquals(meta.getVersions(), second.getVersions());
        int newVersion = second.addVersion();
        Assert.assertEquals(123, newVersion);
        Assert.assertEquals(124, second.getVersions());
        Assert.assertEquals(123, meta.getVersions());
        // Metadata with description
        format = new SimpleDateFormat("y/m/d");
        date = format.parse("2013/12/25");
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("a", "A");
        meta = new KeyProvider.Metadata("myCipher", 100, "description", attributes, date, 123);
        Assert.assertEquals("myCipher", meta.getCipher());
        Assert.assertEquals(100, meta.getBitLength());
        Assert.assertEquals("description", meta.getDescription());
        Assert.assertEquals(attributes, meta.getAttributes());
        Assert.assertEquals(date, meta.getCreated());
        Assert.assertEquals(123, meta.getVersions());
        second = new KeyProvider.Metadata(meta.serialize());
        Assert.assertEquals(meta.getCipher(), second.getCipher());
        Assert.assertEquals(meta.getBitLength(), second.getBitLength());
        Assert.assertEquals(meta.getDescription(), second.getDescription());
        Assert.assertEquals(meta.getAttributes(), second.getAttributes());
        Assert.assertEquals(meta.getCreated(), second.getCreated());
        Assert.assertEquals(meta.getVersions(), second.getVersions());
        newVersion = second.addVersion();
        Assert.assertEquals(123, newVersion);
        Assert.assertEquals(124, second.getVersions());
        Assert.assertEquals(123, meta.getVersions());
    }

    @Test
    public void testOptions() throws Exception {
        Configuration conf = new Configuration();
        conf.set(DEFAULT_CIPHER_NAME, "myCipher");
        conf.setInt(DEFAULT_BITLENGTH_NAME, 512);
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("a", "A");
        KeyProvider.Options options = KeyProvider.options(conf);
        Assert.assertEquals("myCipher", options.getCipher());
        Assert.assertEquals(512, options.getBitLength());
        options.setCipher("yourCipher");
        options.setDescription("description");
        options.setAttributes(attributes);
        options.setBitLength(128);
        Assert.assertEquals("yourCipher", options.getCipher());
        Assert.assertEquals(128, options.getBitLength());
        Assert.assertEquals("description", options.getDescription());
        Assert.assertEquals(attributes, options.getAttributes());
        options = KeyProvider.options(new Configuration());
        Assert.assertEquals(DEFAULT_CIPHER, options.getCipher());
        Assert.assertEquals(DEFAULT_BITLENGTH, options.getBitLength());
    }

    @Test
    public void testUnnestUri() throws Exception {
        assertUnwraps("hdfs://nn.example.com/my/path", "myscheme://hdfs@nn.example.com/my/path");
        assertUnwraps("hdfs://nn/my/path?foo=bar&baz=bat#yyy", "myscheme://hdfs@nn/my/path?foo=bar&baz=bat#yyy");
        assertUnwraps("inner://hdfs@nn1.example.com/my/path", "outer://inner@hdfs@nn1.example.com/my/path");
        assertUnwraps("user:///", "outer://user/");
        assertUnwraps("wasb://account@container/secret.jceks", "jceks://wasb@account@container/secret.jceks");
        assertUnwraps("abfs://account@container/secret.jceks", "jceks://abfs@account@container/secret.jceks");
        assertUnwraps("s3a://container/secret.jceks", "jceks://s3a@container/secret.jceks");
        assertUnwraps("file:///tmp/secret.jceks", "jceks://file/tmp/secret.jceks");
        assertUnwraps("https://user:pass@service/secret.jceks?token=aia", "jceks://https@user:pass@service/secret.jceks?token=aia");
    }

    private static class MyKeyProvider extends KeyProvider {
        private String algorithm;

        private int size;

        private byte[] material;

        public MyKeyProvider(Configuration conf) {
            super(conf);
        }

        @Override
        public KeyVersion getKeyVersion(String versionName) throws IOException {
            return null;
        }

        @Override
        public List<String> getKeys() throws IOException {
            return null;
        }

        @Override
        public List<KeyVersion> getKeyVersions(String name) throws IOException {
            return null;
        }

        @Override
        public Metadata getMetadata(String name) throws IOException {
            if (!("unknown".equals(name))) {
                return new Metadata(TestKeyProvider.CIPHER, 128, "description", null, new Date(), 0);
            }
            return null;
        }

        @Override
        public KeyVersion createKey(String name, byte[] material, Options options) throws IOException {
            this.material = material;
            return null;
        }

        @Override
        public void deleteKey(String name) throws IOException {
        }

        @Override
        public KeyVersion rollNewVersion(String name, byte[] material) throws IOException {
            this.material = material;
            return null;
        }

        @Override
        public void flush() throws IOException {
        }

        @Override
        protected byte[] generateKey(int size, String algorithm) throws NoSuchAlgorithmException {
            this.size = size;
            this.algorithm = algorithm;
            return super.generateKey(size, algorithm);
        }
    }

    @Test
    public void testMaterialGeneration() throws Exception {
        TestKeyProvider.MyKeyProvider kp = new TestKeyProvider.MyKeyProvider(new Configuration());
        KeyProvider.Options options = new KeyProvider.Options(new Configuration());
        options.setCipher(TestKeyProvider.CIPHER);
        options.setBitLength(128);
        kp.createKey("hello", options);
        Assert.assertEquals(128, kp.size);
        Assert.assertEquals(TestKeyProvider.CIPHER, kp.algorithm);
        Assert.assertNotNull(kp.material);
        kp = new TestKeyProvider.MyKeyProvider(new Configuration());
        kp.rollNewVersion("hello");
        Assert.assertEquals(128, kp.size);
        Assert.assertEquals(TestKeyProvider.CIPHER, kp.algorithm);
        Assert.assertNotNull(kp.material);
    }

    @Test
    public void testRolloverUnknownKey() throws Exception {
        TestKeyProvider.MyKeyProvider kp = new TestKeyProvider.MyKeyProvider(new Configuration());
        KeyProvider.Options options = new KeyProvider.Options(new Configuration());
        options.setCipher(TestKeyProvider.CIPHER);
        options.setBitLength(128);
        kp.createKey("hello", options);
        Assert.assertEquals(128, kp.size);
        Assert.assertEquals(TestKeyProvider.CIPHER, kp.algorithm);
        Assert.assertNotNull(kp.material);
        kp = new TestKeyProvider.MyKeyProvider(new Configuration());
        try {
            kp.rollNewVersion("unknown");
            Assert.fail("should have thrown");
        } catch (IOException e) {
            String expectedError = "Can't find Metadata for key";
            GenericTestUtils.assertExceptionContains(expectedError, e);
        }
    }

    @Test
    public void testConfiguration() throws Exception {
        Configuration conf = new Configuration(false);
        conf.set("a", "A");
        TestKeyProvider.MyKeyProvider kp = new TestKeyProvider.MyKeyProvider(conf);
        Assert.assertEquals("A", getConf().get("a"));
    }
}

