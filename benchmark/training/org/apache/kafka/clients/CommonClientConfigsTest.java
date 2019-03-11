/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.clients;


import CommonClientConfigs.RECONNECT_BACKOFF_MAX_MS_CONFIG;
import CommonClientConfigs.RECONNECT_BACKOFF_MS_CONFIG;
import ConfigDef.Importance.LOW;
import ConfigDef.Type.LONG;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.junit.Assert;
import org.junit.Test;

import static Range.atLeast;


public class CommonClientConfigsTest {
    private static class TestConfig extends AbstractConfig {
        private static final ConfigDef CONFIG;

        static {
            CONFIG = new ConfigDef().define(RECONNECT_BACKOFF_MS_CONFIG, LONG, 50L, atLeast(0L), LOW, "").define(RECONNECT_BACKOFF_MAX_MS_CONFIG, LONG, 1000L, atLeast(0L), LOW, "");
        }

        @Override
        protected Map<String, Object> postProcessParsedConfig(final Map<String, Object> parsedValues) {
            return CommonClientConfigs.postProcessReconnectBackoffConfigs(this, parsedValues);
        }

        public TestConfig(Map<?, ?> props) {
            super(CommonClientConfigsTest.TestConfig.CONFIG, props);
        }
    }

    @Test
    public void testExponentialBackoffDefaults() throws Exception {
        CommonClientConfigsTest.TestConfig defaultConf = new CommonClientConfigsTest.TestConfig(Collections.emptyMap());
        Assert.assertEquals(Long.valueOf(50L), defaultConf.getLong(RECONNECT_BACKOFF_MS_CONFIG));
        Assert.assertEquals(Long.valueOf(1000L), defaultConf.getLong(RECONNECT_BACKOFF_MAX_MS_CONFIG));
        CommonClientConfigsTest.TestConfig bothSetConfig = new CommonClientConfigsTest.TestConfig(new HashMap<String, Object>() {
            {
                put(RECONNECT_BACKOFF_MS_CONFIG, "123");
                put(RECONNECT_BACKOFF_MAX_MS_CONFIG, "12345");
            }
        });
        Assert.assertEquals(Long.valueOf(123L), bothSetConfig.getLong(RECONNECT_BACKOFF_MS_CONFIG));
        Assert.assertEquals(Long.valueOf(12345L), bothSetConfig.getLong(RECONNECT_BACKOFF_MAX_MS_CONFIG));
        CommonClientConfigsTest.TestConfig reconnectBackoffSetConf = new CommonClientConfigsTest.TestConfig(new HashMap<String, Object>() {
            {
                put(RECONNECT_BACKOFF_MS_CONFIG, "123");
            }
        });
        Assert.assertEquals(Long.valueOf(123L), reconnectBackoffSetConf.getLong(RECONNECT_BACKOFF_MS_CONFIG));
        Assert.assertEquals(Long.valueOf(123L), reconnectBackoffSetConf.getLong(RECONNECT_BACKOFF_MAX_MS_CONFIG));
    }
}

