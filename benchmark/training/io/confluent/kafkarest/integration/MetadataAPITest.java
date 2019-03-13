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
package io.confluent.kafkarest.integration;


import Errors.PARTITION_NOT_FOUND_ERROR_CODE;
import Errors.PARTITION_NOT_FOUND_MESSAGE;
import Errors.TOPIC_NOT_FOUND_ERROR_CODE;
import Errors.TOPIC_NOT_FOUND_MESSAGE;
import Response.Status.NOT_FOUND;
import Versions.KAFKA_MOST_SPECIFIC_DEFAULT;
import io.confluent.kafkarest.TestUtils;
import io.confluent.kafkarest.entities.BrokerList;
import io.confluent.kafkarest.entities.Partition;
import io.confluent.kafkarest.entities.PartitionReplica;
import io.confluent.kafkarest.entities.Topic;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests metadata access against a real cluster. This isn't exhaustive since the unit tests cover
 * corner cases; rather it verifies the basic functionality works against a real cluster.
 */
public class MetadataAPITest extends ClusterTestHarness {
    private static final String topic1Name = "topic1";

    private static final List<Partition> topic1Partitions = Arrays.asList(new Partition(0, 0, Arrays.asList(new PartitionReplica(0, true, true), new PartitionReplica(1, false, false))));

    private static final Topic topic1 = new Topic(MetadataAPITest.topic1Name, new Properties(), MetadataAPITest.topic1Partitions);

    private static final String topic2Name = "topic2";

    private static final List<Partition> topic2Partitions = Arrays.asList(new Partition(0, 0, Arrays.asList(new PartitionReplica(0, true, true), new PartitionReplica(1, false, false))), new Partition(1, 1, Arrays.asList(new PartitionReplica(0, false, true), new PartitionReplica(1, true, true))));

    private static final Properties topic2Configs;

    private static final Topic topic2;

    static {
        topic2Configs = new Properties();
        MetadataAPITest.topic2Configs.setProperty("cleanup.policy", "delete");
        topic2 = new Topic(MetadataAPITest.topic2Name, MetadataAPITest.topic2Configs, MetadataAPITest.topic2Partitions);
    }

    private static final int numReplicas = 2;

    public MetadataAPITest() {
        super(2, false);
    }

    @Test
    public void testBrokers() throws InterruptedException {
        // Listing
        Response response = request("/brokers").get();
        TestUtils.assertOKResponse(response, KAFKA_MOST_SPECIFIC_DEFAULT);
        final BrokerList brokers = TestUtils.tryReadEntityOrLog(response, BrokerList.class);
        Assert.assertEquals(new BrokerList(Arrays.asList(0, 1)), brokers);
    }

    /* This should work, but due to the lack of timeouts in ZkClient, if ZK is down some calls
     will just block forever, see https://issues.apache.org/jira/browse/KAFKA-1907. We should
     reenable this once we can apply timeouts to ZK operations.
     */
    /* @Test
    public void testZkFailure() throws InterruptedException {
    // Kill ZK so the request will generate an error.
    zookeeper.shutdown();

    // Since this is handled via an ExceptionMapper, testing one endpoint is good enough
    Response response = request("/brokers").get();
    assertErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, response,
    Errors.ZOOKEEPER_ERROR_ERROR_CODE, Errors.ZOOKEEPER_ERROR_MESSAGE,
    Versions.KAFKA_MOST_SPECIFIC_DEFAULT);
    }
     */
    @Test
    public void testTopicsList() throws InterruptedException {
        // Listing
        Response response = request("/topics").get();
        TestUtils.assertOKResponse(response, KAFKA_MOST_SPECIFIC_DEFAULT);
        final List<String> topicsResponse = TestUtils.tryReadEntityOrLog(response, new javax.ws.rs.core.GenericType<List<String>>() {});
        Assert.assertEquals(Arrays.asList(MetadataAPITest.topic1Name, MetadataAPITest.topic2Name), topicsResponse);
        // Get topic
        Response response1 = request("/topics/{topic}", "topic", MetadataAPITest.topic1Name).get();
        TestUtils.assertOKResponse(response, KAFKA_MOST_SPECIFIC_DEFAULT);
        final Topic topic1Response = TestUtils.tryReadEntityOrLog(response1, Topic.class);
        // Just verify some basic properties because the exact values can vary based on replica
        // assignment, leader election
        Assert.assertEquals(MetadataAPITest.topic1.getName(), topic1Response.getName());
        // admin client provides default configs as well and hence not asserting for now
        // assertEquals(topic1.getConfigs(), topic1Response.getConfigs());
        Assert.assertEquals(MetadataAPITest.topic1Partitions.size(), topic1Response.getPartitions().size());
        Assert.assertEquals(MetadataAPITest.numReplicas, topic1Response.getPartitions().get(0).getReplicas().size());
        // Get invalid topic
        final Response invalidResponse = request("/topics/{topic}", "topic", "topicdoesntexist").get();
        TestUtils.assertErrorResponse(NOT_FOUND, invalidResponse, TOPIC_NOT_FOUND_ERROR_CODE, TOPIC_NOT_FOUND_MESSAGE, KAFKA_MOST_SPECIFIC_DEFAULT);
    }

    @Test
    public void testPartitionsList() throws InterruptedException {
        // Listing
        Response response = request((("/topics/" + (MetadataAPITest.topic1Name)) + "/partitions")).get();
        TestUtils.assertOKResponse(response, KAFKA_MOST_SPECIFIC_DEFAULT);
        final List<Partition> partitions1Response = TestUtils.tryReadEntityOrLog(response, new javax.ws.rs.core.GenericType<List<Partition>>() {});
        // Just verify some basic properties because the exact values can vary based on replica
        // assignment, leader election
        Assert.assertEquals(MetadataAPITest.topic1Partitions.size(), partitions1Response.size());
        Assert.assertEquals(MetadataAPITest.numReplicas, partitions1Response.get(0).getReplicas().size());
        response = request((("/topics/" + (MetadataAPITest.topic2Name)) + "/partitions")).get();
        TestUtils.assertOKResponse(response, KAFKA_MOST_SPECIFIC_DEFAULT);
        final List<Partition> partitions2Response = TestUtils.tryReadEntityOrLog(response, new javax.ws.rs.core.GenericType<List<Partition>>() {});
        Assert.assertEquals(MetadataAPITest.topic2Partitions.size(), partitions2Response.size());
        Assert.assertEquals(MetadataAPITest.numReplicas, partitions2Response.get(0).getReplicas().size());
        Assert.assertEquals(MetadataAPITest.numReplicas, partitions2Response.get(1).getReplicas().size());
        // Get single partition
        response = request((("/topics/" + (MetadataAPITest.topic1Name)) + "/partitions/0")).get();
        TestUtils.assertOKResponse(response, KAFKA_MOST_SPECIFIC_DEFAULT);
        final Partition getPartitionResponse = TestUtils.tryReadEntityOrLog(response, Partition.class);
        Assert.assertEquals(0, getPartitionResponse.getPartition());
        Assert.assertEquals(MetadataAPITest.numReplicas, getPartitionResponse.getReplicas().size());
        // Get invalid partition
        final Response invalidResponse = request("/topics/topic1/partitions/1000").get();
        TestUtils.assertErrorResponse(NOT_FOUND, invalidResponse, PARTITION_NOT_FOUND_ERROR_CODE, PARTITION_NOT_FOUND_MESSAGE, KAFKA_MOST_SPECIFIC_DEFAULT);
    }
}

