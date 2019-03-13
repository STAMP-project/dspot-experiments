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


import java.io.File;
import java.net.URLEncoder;
import java.security.Key;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseCommonTestingUtility;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MiscTests.class, SmallTests.class })
public class TestKeyStoreKeyProvider {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestKeyStoreKeyProvider.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestKeyStoreKeyProvider.class);

    static final HBaseCommonTestingUtility TEST_UTIL = new HBaseCommonTestingUtility();

    static final String ALIAS = "test";

    static final String PASSWORD = "password";

    static byte[] KEY;

    static File storeFile;

    static File passwordFile;

    @Test
    public void testKeyStoreKeyProviderWithPassword() throws Exception {
        KeyProvider provider = new KeyStoreKeyProvider();
        provider.init(((("jceks://" + (TestKeyStoreKeyProvider.storeFile.toURI().getPath())) + "?password=") + (TestKeyStoreKeyProvider.PASSWORD)));
        Key key = provider.getKey(TestKeyStoreKeyProvider.ALIAS);
        Assert.assertNotNull(key);
        byte[] keyBytes = key.getEncoded();
        Assert.assertEquals(keyBytes.length, TestKeyStoreKeyProvider.KEY.length);
        for (int i = 0; i < (TestKeyStoreKeyProvider.KEY.length); i++) {
            Assert.assertEquals(keyBytes[i], TestKeyStoreKeyProvider.KEY[i]);
        }
    }

    @Test
    public void testKeyStoreKeyProviderWithPasswordFile() throws Exception {
        KeyProvider provider = new KeyStoreKeyProvider();
        provider.init(((("jceks://" + (TestKeyStoreKeyProvider.storeFile.toURI().getPath())) + "?passwordFile=") + (URLEncoder.encode(TestKeyStoreKeyProvider.passwordFile.getAbsolutePath(), "UTF-8"))));
        Key key = provider.getKey(TestKeyStoreKeyProvider.ALIAS);
        Assert.assertNotNull(key);
        byte[] keyBytes = key.getEncoded();
        Assert.assertEquals(keyBytes.length, TestKeyStoreKeyProvider.KEY.length);
        for (int i = 0; i < (TestKeyStoreKeyProvider.KEY.length); i++) {
            Assert.assertEquals(keyBytes[i], TestKeyStoreKeyProvider.KEY[i]);
        }
    }
}

