/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shardingsphere.core.strategy.encrypt.impl;


import java.util.Properties;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class AESShardingEncryptorTest {
    private final AESShardingEncryptor encryptor = new AESShardingEncryptor();

    @Test
    public void assertGetType() {
        Assert.assertThat(encryptor.getType(), CoreMatchers.is("AES"));
    }

    @Test
    public void assertEncode() {
        Assert.assertThat(encryptor.encrypt("test"), CoreMatchers.is("dSpPiyENQGDUXMKFMJPGWA=="));
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertEncodeWithoutKey() {
        Properties properties = new Properties();
        encryptor.setProperties(properties);
        Assert.assertThat(encryptor.encrypt("test"), CoreMatchers.is("dSpPiyENQGDUXMKFMJPGWA=="));
    }

    @Test
    public void assertDecode() {
        Assert.assertThat(encryptor.decrypt("dSpPiyENQGDUXMKFMJPGWA==").toString(), CoreMatchers.is("test"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertDecodeWithoutKey() {
        Properties properties = new Properties();
        encryptor.setProperties(properties);
        Assert.assertThat(encryptor.decrypt("dSpPiyENQGDUXMKFMJPGWA==").toString(), CoreMatchers.is("test"));
    }

    @Test
    public void assertGetProperties() {
        Assert.assertThat(encryptor.getProperties().get("aes.key.value").toString(), CoreMatchers.is("test"));
    }
}

