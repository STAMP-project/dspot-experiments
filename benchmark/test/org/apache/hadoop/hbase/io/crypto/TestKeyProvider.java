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
import HConstants.CRYPTO_KEYPROVIDER_CONF_KEY;
import java.security.Key;
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
public class TestKeyProvider {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestKeyProvider.class);

    @Test
    public void testTestProvider() {
        Configuration conf = HBaseConfiguration.create();
        conf.set(CRYPTO_KEYPROVIDER_CONF_KEY, KeyProviderForTesting.class.getName());
        KeyProvider provider = Encryption.getKeyProvider(conf);
        Assert.assertNotNull("Null returned for provider", provider);
        Assert.assertTrue("Provider is not the expected type", (provider instanceof KeyProviderForTesting));
        Key key = provider.getKey("foo");
        Assert.assertNotNull("Test provider did not return a key as expected", key);
        Assert.assertEquals("Test provider did not create a key for AES", "AES", key.getAlgorithm());
        Assert.assertEquals("Test provider did not create a key of adequate length", KEY_LENGTH, key.getEncoded().length);
    }
}

