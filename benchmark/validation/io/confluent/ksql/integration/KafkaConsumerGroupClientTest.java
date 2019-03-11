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
package io.confluent.ksql.integration;


import com.google.common.collect.ImmutableList;
import io.confluent.ksql.util.KafkaConsumerGroupClient;
import java.util.Arrays;
import java.util.Collections;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.test.IntegrationTest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Unfortunately needs to be an integration test as there is no way
 * of stubbing results from the admin client as constructors are package-private. Mocking
 * the results would be tedious and distract from the actual testing.
 */
@Category({ IntegrationTest.class })
public class KafkaConsumerGroupClientTest {
    private static final int PARTITION_COUNT = 3;

    @Rule
    public final IntegrationTestHarness testHarness = IntegrationTestHarness.build();

    private AdminClient adminClient;

    private KafkaConsumerGroupClient consumerGroupClient;

    private String topicName;

    private String group0;

    private String group1;

    @Test
    public void shouldListNoConsumerGroupsWhenThereAreNone() {
        MatcherAssert.assertThat(consumerGroupClient.listGroups(), Matchers.equalTo(Collections.<String>emptyList()));
    }

    @Test
    public void shouldListConsumerGroupsWhenTheyExist() throws InterruptedException {
        givenTopicExistsWithData();
        verifyListsGroups(group0, ImmutableList.of(group0));
        verifyListsGroups(group1, ImmutableList.of(group0, group1));
    }

    @Test
    public void shouldDescribeGroup() throws InterruptedException {
        givenTopicExistsWithData();
        try (final KafkaConsumer<String, byte[]> c1 = createConsumer(group0)) {
            verifyDescribeGroup(1, group0, Collections.singletonList(c1));
            try (final KafkaConsumer<String, byte[]> c2 = createConsumer(group0)) {
                verifyDescribeGroup(2, group0, Arrays.asList(c1, c2));
            }
        }
    }
}

