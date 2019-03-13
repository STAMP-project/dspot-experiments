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


import ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
import SecurityProtocol.SSL;
import io.confluent.common.utils.IntegrationTest;
import io.confluent.ksql.engine.KsqlEngine;
import io.confluent.ksql.query.QueryId;
import io.confluent.ksql.services.KafkaTopicClient;
import io.confluent.ksql.services.ServiceContext;
import io.confluent.ksql.test.util.EmbeddedSingleNodeKafkaCluster;
import io.confluent.ksql.test.util.secure.ClientTrustStore;
import io.confluent.ksql.test.util.secure.Credentials;
import io.confluent.ksql.test.util.secure.SecureKafkaHelper;
import io.confluent.ksql.util.KsqlConfig;
import io.confluent.ksql.util.TopicProducer;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import kafka.security.auth.Acl;
import org.apache.kafka.clients.admin.AdminClient;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Tests covering integration with secured components, e.g. secure Kafka cluster.
 */
@Category({ IntegrationTest.class })
public class SecureIntegrationTest {
    private static final String INPUT_TOPIC = "orders_topic";

    private static final String INPUT_STREAM = "ORDERS";

    private static final Credentials ALL_USERS = new Credentials("*", "ignored");

    private static final Credentials SUPER_USER = VALID_USER1;

    private static final Credentials NORMAL_USER = VALID_USER2;

    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    @ClassRule
    public static final EmbeddedSingleNodeKafkaCluster SECURE_CLUSTER = EmbeddedSingleNodeKafkaCluster.newBuilder().withoutPlainListeners().withSaslSslListeners().withSslListeners().withAcls(SecureIntegrationTest.SUPER_USER.username).build();

    private QueryId queryId;

    private KsqlConfig ksqlConfig;

    private KsqlEngine ksqlEngine;

    private final TopicProducer topicProducer = new TopicProducer(SecureIntegrationTest.SECURE_CLUSTER);

    private KafkaTopicClient topicClient;

    private String outputTopic;

    private AdminClient adminClient;

    private ServiceContext serviceContext;

    @Test
    public void shouldRunQueryAgainstKafkaClusterOverSsl() throws Exception {
        // Given:
        SecureIntegrationTest.givenAllowAcl(SecureIntegrationTest.ALL_USERS, SecureIntegrationTest.resource(CLUSTER, "kafka-cluster"), SecureIntegrationTest.ops(DESCRIBE_CONFIGS, CREATE));
        SecureIntegrationTest.givenAllowAcl(SecureIntegrationTest.ALL_USERS, SecureIntegrationTest.resource(TOPIC, Acl.WildCardResource()), SecureIntegrationTest.ops(DESCRIBE, READ, WRITE, DELETE));
        SecureIntegrationTest.givenAllowAcl(SecureIntegrationTest.ALL_USERS, SecureIntegrationTest.resource(GROUP, Acl.WildCardResource()), SecureIntegrationTest.ops(DESCRIBE, READ));
        final Map<String, Object> configs = SecureIntegrationTest.getBaseKsqlConfig();
        configs.put(BOOTSTRAP_SERVERS_CONFIG, SecureIntegrationTest.SECURE_CLUSTER.bootstrapServers(SSL));
        // Additional Properties required for KSQL to talk to cluster over SSL:
        configs.put("security.protocol", "SSL");
        configs.put("ssl.truststore.location", ClientTrustStore.trustStorePath());
        configs.put("ssl.truststore.password", ClientTrustStore.trustStorePassword());
        givenTestSetupWithConfig(configs);
        // Then:
        assertCanRunSimpleKsqlQuery();
    }

    @Test
    public void shouldRunQueryAgainstKafkaClusterOverSaslSsl() throws Exception {
        // Given:
        final Map<String, Object> configs = SecureIntegrationTest.getBaseKsqlConfig();
        // Additional Properties required for KSQL to talk to secure cluster using SSL and SASL:
        configs.put("security.protocol", "SASL_SSL");
        configs.put("sasl.mechanism", "PLAIN");
        configs.put("sasl.jaas.config", SecureKafkaHelper.buildJaasConfig(SecureIntegrationTest.SUPER_USER));
        givenTestSetupWithConfig(configs);
        // Then:
        assertCanRunSimpleKsqlQuery();
    }

    @Test
    public void shouldRunQueriesRequiringChangeLogsAndRepartitionTopicsWithMinimalPrefixedAcls() throws Exception {
        final String serviceId = "my-service-id_";// Defaults to "default_"

        final Map<String, Object> ksqlConfig = SecureIntegrationTest.getKsqlConfig(SecureIntegrationTest.NORMAL_USER);
        ksqlConfig.put(KsqlConfig.KSQL_SERVICE_ID_CONFIG, serviceId);
        SecureIntegrationTest.givenAllowAcl(SecureIntegrationTest.NORMAL_USER, SecureIntegrationTest.resource(CLUSTER, "kafka-cluster"), SecureIntegrationTest.ops(DESCRIBE_CONFIGS));
        SecureIntegrationTest.givenAllowAcl(SecureIntegrationTest.NORMAL_USER, SecureIntegrationTest.resource(TOPIC, SecureIntegrationTest.INPUT_TOPIC), SecureIntegrationTest.ops(READ));
        SecureIntegrationTest.givenAllowAcl(SecureIntegrationTest.NORMAL_USER, SecureIntegrationTest.resource(TOPIC, outputTopic), /* as the topic doesn't exist yet */
        SecureIntegrationTest.ops(CREATE, WRITE));
        SecureIntegrationTest.givenAllowAcl(SecureIntegrationTest.NORMAL_USER, SecureIntegrationTest.prefixedResource(TOPIC, "_confluent-ksql-my-service-id_"), SecureIntegrationTest.ops(ALL));
        SecureIntegrationTest.givenAllowAcl(SecureIntegrationTest.NORMAL_USER, SecureIntegrationTest.prefixedResource(GROUP, "_confluent-ksql-my-service-id_"), SecureIntegrationTest.ops(ALL));
        givenTestSetupWithConfig(ksqlConfig);
        // Then:
        assertCanRunRepartitioningKsqlQuery();
    }
}

