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
package org.apache.shardingsphere.core.encrypt.impl;


import java.util.Properties;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class MD5ShardingEncryptorTest {
    private final MD5ShardingEncryptor encryptor = new MD5ShardingEncryptor();

    @Test
    public void assertGetType() {
        Assert.assertThat(encryptor.getType(), CoreMatchers.is("MD5"));
    }

    @Test
    public void assertEncode() {
        Assert.assertThat(encryptor.encrypt("test"), CoreMatchers.is("098f6bcd4621d373cade4e832627b4f6"));
    }

    @Test
    public void assertDecode() {
        Assert.assertThat(encryptor.decrypt("test").toString(), CoreMatchers.is("test"));
    }

    @Test
    public void assertProperties() {
        Properties properties = new Properties();
        properties.setProperty("key1", "value1");
        encryptor.setProperties(properties);
        Assert.assertThat(encryptor.getProperties().get("key1").toString(), CoreMatchers.is("value1"));
    }
}

