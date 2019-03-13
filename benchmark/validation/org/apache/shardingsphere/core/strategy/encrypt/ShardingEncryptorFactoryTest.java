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
package org.apache.shardingsphere.core.strategy.encrypt;


import java.util.Properties;
import org.apache.shardingsphere.core.spi.algorithm.encrypt.ShardingEncryptorFactory;
import org.apache.shardingsphere.core.strategy.encrypt.fixture.TestShardingEncryptor;
import org.apache.shardingsphere.core.strategy.encrypt.impl.AESShardingEncryptor;
import org.apache.shardingsphere.core.strategy.encrypt.impl.MD5ShardingEncryptor;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class ShardingEncryptorFactoryTest {
    @Test
    public void assertNewMD5Encryptor() {
        Assert.assertThat(ShardingEncryptorFactory.getInstance().newAlgorithm("MD5", new Properties()), CoreMatchers.instanceOf(MD5ShardingEncryptor.class));
    }

    @Test
    public void assertNewAESEncryptor() {
        Assert.assertThat(ShardingEncryptorFactory.getInstance().newAlgorithm("AES", new Properties()), CoreMatchers.instanceOf(AESShardingEncryptor.class));
    }

    @Test
    public void assertNewDefaultEncryptor() {
        Assert.assertThat(ShardingEncryptorFactory.getInstance().newAlgorithm(), CoreMatchers.instanceOf(TestShardingEncryptor.class));
    }
}

