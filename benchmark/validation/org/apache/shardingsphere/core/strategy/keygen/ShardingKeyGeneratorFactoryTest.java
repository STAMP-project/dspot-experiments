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
package org.apache.shardingsphere.core.strategy.keygen;


import java.util.Properties;
import org.apache.shardingsphere.core.spi.algorithm.keygen.ShardingKeyGeneratorFactory;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class ShardingKeyGeneratorFactoryTest {
    @Test
    public void assertNewSnowflakeKeyGenerator() {
        Assert.assertThat(ShardingKeyGeneratorFactory.getInstance().newAlgorithm("SNOWFLAKE", new Properties()), CoreMatchers.instanceOf(SnowflakeShardingKeyGenerator.class));
    }

    @Test
    public void assertNewUUIDKeyGenerator() {
        Assert.assertThat(ShardingKeyGeneratorFactory.getInstance().newAlgorithm("UUID", new Properties()), CoreMatchers.instanceOf(UUIDShardingKeyGenerator.class));
    }

    @Test
    public void assertNewDefaultKeyGenerator() {
        Assert.assertThat(ShardingKeyGeneratorFactory.getInstance().newAlgorithm(), CoreMatchers.instanceOf(SnowflakeShardingKeyGenerator.class));
    }
}

