/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.rest.server;


import ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import KsqlConfig.KSQL_SERVICE_ID_CONFIG;
import RestConfig.LISTENERS_CONFIG;
import StreamsConfig.APPLICATION_ID_CONFIG;
import StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;
import StreamsConfig.COMMIT_INTERVAL_MS_CONFIG;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static KsqlRestConfig.COMMAND_TOPIC_SUFFIX;


public class KsqlRestConfigTest {
    private static final Map<String, ?> MIN_VALID_CONFIGS = ImmutableMap.<String, Object>builder().put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092").put(APPLICATION_ID_CONFIG, "ksql_config_test").put(LISTENERS_CONFIG, "http://localhost:8088").build();

    @Test
    public void testGetKsqlConfigProperties() {
        final Map<String, Object> inputProperties = new HashMap<>(KsqlRestConfigTest.MIN_VALID_CONFIGS);
        inputProperties.put(AUTO_OFFSET_RESET_CONFIG, "earliest");
        inputProperties.put(KSQL_SERVICE_ID_CONFIG, "test");
        final KsqlRestConfig config = new KsqlRestConfig(inputProperties);
        final Map<String, Object> ksqlConfigProperties = config.getKsqlConfigProperties();
        final Map<String, Object> expectedKsqlConfigProperties = new HashMap<>();
        expectedKsqlConfigProperties.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        expectedKsqlConfigProperties.put(APPLICATION_ID_CONFIG, "ksql_config_test");
        expectedKsqlConfigProperties.put(LISTENERS_CONFIG, "http://localhost:8088");
        expectedKsqlConfigProperties.put(AUTO_OFFSET_RESET_CONFIG, "earliest");
        expectedKsqlConfigProperties.put(KSQL_SERVICE_ID_CONFIG, "test");
        Assert.assertThat(ksqlConfigProperties, CoreMatchers.equalTo(expectedKsqlConfigProperties));
    }

    // Just a sanity check to make sure that, although they contain identical mappings, successive maps returned by calls
    // to KsqlRestConfig.getOriginals() do not actually return the same object (mutability would then be an issue)
    @Test
    public void testOriginalsReplicability() {
        final String COMMIT_INTERVAL_MS = "10";
        final Map<String, Object> inputProperties = new HashMap<>(KsqlRestConfigTest.MIN_VALID_CONFIGS);
        inputProperties.put(COMMIT_INTERVAL_MS_CONFIG, COMMIT_INTERVAL_MS);
        final KsqlRestConfig config = new KsqlRestConfig(inputProperties);
        final Map<String, Object> originals1 = config.getOriginals();
        final Map<String, Object> originals2 = config.getOriginals();
        Assert.assertEquals(originals1, originals2);
        Assert.assertNotSame(originals1, originals2);
        Assert.assertEquals(COMMIT_INTERVAL_MS, originals1.get(COMMIT_INTERVAL_MS_CONFIG));
        Assert.assertEquals(COMMIT_INTERVAL_MS, originals2.get(COMMIT_INTERVAL_MS_CONFIG));
    }

    @Test
    public void ensureCorrectCommandTopicName() {
        final String commandTopicName = KsqlRestConfig.getCommandTopic("TestKSql");
        Assert.assertThat(commandTopicName, CoreMatchers.equalTo(("_confluent-ksql-TestKSql_" + (COMMAND_TOPIC_SUFFIX))));
    }
}

