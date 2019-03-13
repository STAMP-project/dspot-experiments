package io.confluent.kafkarest.unit;


import KafkaRestConfig.KAFKACLIENT_INIT_TIMEOUT_CONFIG;
import io.confluent.kafkarest.KafkaRestConfig;
import io.confluent.kafkarest.entities.Topic;
import io.confluent.rest.RestConfigException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Properties;
import org.apache.kafka.clients.admin.MockAdminClient;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartitionInfo;
import org.junit.Assert;
import org.junit.Test;


public class AdminClientWrapperTest {
    @Test
    public void testGetTopicOnNullLeaderDoesntThrowNPE() throws RestConfigException {
        Properties props = new Properties();
        props.put(KAFKACLIENT_INIT_TIMEOUT_CONFIG, "100");
        KafkaRestConfig config = new KafkaRestConfig(props);
        Node controller = new Node(1, "a", 1);
        MockAdminClient adminClient = new MockAdminClient(Arrays.asList(controller, null), controller);
        TopicPartitionInfo partition = new TopicPartitionInfo(1, null, Collections.singletonList(controller), Collections.singletonList(controller));
        adminClient.addTopic(false, "topic", Collections.singletonList(partition), new HashMap<String, String>());
        Topic topic = getTopic("topic");
        Assert.assertEquals(topic.getName(), "topic");
    }
}

