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
package org.apache.kafka.connect.runtime;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.kafka.common.config.ConfigChangeCallback;
import org.apache.kafka.common.config.ConfigData;
import org.apache.kafka.common.config.provider.ConfigProvider;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
public class WorkerConfigTransformerTest {
    public static final String MY_KEY = "myKey";

    public static final String MY_CONNECTOR = "myConnector";

    public static final String TEST_KEY = "testKey";

    public static final String TEST_PATH = "testPath";

    public static final String TEST_KEY_WITH_TTL = "testKeyWithTTL";

    public static final String TEST_KEY_WITH_LONGER_TTL = "testKeyWithLongerTTL";

    public static final String TEST_RESULT = "testResult";

    public static final String TEST_RESULT_WITH_TTL = "testResultWithTTL";

    public static final String TEST_RESULT_WITH_LONGER_TTL = "testResultWithLongerTTL";

    @Mock
    private Herder herder;

    @Mock
    private Worker worker;

    @Mock
    private HerderRequest requestId;

    private WorkerConfigTransformer configTransformer;

    @Test
    public void testReplaceVariable() {
        Map<String, String> result = configTransformer.transform(WorkerConfigTransformerTest.MY_CONNECTOR, Collections.singletonMap(WorkerConfigTransformerTest.MY_KEY, "${test:testPath:testKey}"));
        Assert.assertEquals(WorkerConfigTransformerTest.TEST_RESULT, result.get(WorkerConfigTransformerTest.MY_KEY));
    }

    @Test
    public void testReplaceVariableWithTTL() {
        EasyMock.expect(worker.herder()).andReturn(herder);
        replayAll();
        Map<String, String> props = new HashMap<>();
        props.put(WorkerConfigTransformerTest.MY_KEY, "${test:testPath:testKeyWithTTL}");
        props.put(ConnectorConfig.CONFIG_RELOAD_ACTION_CONFIG, ConnectorConfig.CONFIG_RELOAD_ACTION_NONE);
        Map<String, String> result = configTransformer.transform(WorkerConfigTransformerTest.MY_CONNECTOR, props);
    }

    @Test
    public void testReplaceVariableWithTTLAndScheduleRestart() {
        EasyMock.expect(worker.herder()).andReturn(herder);
        EasyMock.expect(herder.restartConnector(1L, WorkerConfigTransformerTest.MY_CONNECTOR, null)).andReturn(requestId);
        replayAll();
        Map<String, String> result = configTransformer.transform(WorkerConfigTransformerTest.MY_CONNECTOR, Collections.singletonMap(WorkerConfigTransformerTest.MY_KEY, "${test:testPath:testKeyWithTTL}"));
        Assert.assertEquals(WorkerConfigTransformerTest.TEST_RESULT_WITH_TTL, result.get(WorkerConfigTransformerTest.MY_KEY));
    }

    @Test
    public void testReplaceVariableWithTTLFirstCancelThenScheduleRestart() {
        EasyMock.expect(worker.herder()).andReturn(herder);
        EasyMock.expect(herder.restartConnector(1L, WorkerConfigTransformerTest.MY_CONNECTOR, null)).andReturn(requestId);
        EasyMock.expect(worker.herder()).andReturn(herder);
        EasyMock.expectLastCall();
        requestId.cancel();
        EasyMock.expectLastCall();
        EasyMock.expect(herder.restartConnector(10L, WorkerConfigTransformerTest.MY_CONNECTOR, null)).andReturn(requestId);
        replayAll();
        Map<String, String> result = configTransformer.transform(WorkerConfigTransformerTest.MY_CONNECTOR, Collections.singletonMap(WorkerConfigTransformerTest.MY_KEY, "${test:testPath:testKeyWithTTL}"));
        Assert.assertEquals(WorkerConfigTransformerTest.TEST_RESULT_WITH_TTL, result.get(WorkerConfigTransformerTest.MY_KEY));
        result = configTransformer.transform(WorkerConfigTransformerTest.MY_CONNECTOR, Collections.singletonMap(WorkerConfigTransformerTest.MY_KEY, "${test:testPath:testKeyWithLongerTTL}"));
        Assert.assertEquals(WorkerConfigTransformerTest.TEST_RESULT_WITH_LONGER_TTL, result.get(WorkerConfigTransformerTest.MY_KEY));
    }

    @Test
    public void testTransformNullConfiguration() {
        Assert.assertNull(configTransformer.transform(WorkerConfigTransformerTest.MY_CONNECTOR, null));
    }

    public static class TestConfigProvider implements ConfigProvider {
        public void configure(Map<String, ?> configs) {
        }

        public ConfigData get(String path) {
            return null;
        }

        public ConfigData get(String path, Set<String> keys) {
            if (path.equals(WorkerConfigTransformerTest.TEST_PATH)) {
                if (keys.contains(WorkerConfigTransformerTest.TEST_KEY)) {
                    return new ConfigData(Collections.singletonMap(WorkerConfigTransformerTest.TEST_KEY, WorkerConfigTransformerTest.TEST_RESULT));
                } else
                    if (keys.contains(WorkerConfigTransformerTest.TEST_KEY_WITH_TTL)) {
                        return new ConfigData(Collections.singletonMap(WorkerConfigTransformerTest.TEST_KEY_WITH_TTL, WorkerConfigTransformerTest.TEST_RESULT_WITH_TTL), 1L);
                    } else
                        if (keys.contains(WorkerConfigTransformerTest.TEST_KEY_WITH_LONGER_TTL)) {
                            return new ConfigData(Collections.singletonMap(WorkerConfigTransformerTest.TEST_KEY_WITH_LONGER_TTL, WorkerConfigTransformerTest.TEST_RESULT_WITH_LONGER_TTL), 10L);
                        }


            }
            return new ConfigData(Collections.emptyMap());
        }

        public void subscribe(String path, Set<String> keys, ConfigChangeCallback callback) {
            throw new UnsupportedOperationException();
        }

        public void unsubscribe(String path, Set<String> keys) {
            throw new UnsupportedOperationException();
        }

        public void close() {
        }
    }
}

