package org.testcontainers.containers;


import java.util.stream.Stream;
import org.junit.Test;


public class KafkaContainerTest {
    @Test
    public void testUsage() throws Exception {
        try (KafkaContainer kafka = new KafkaContainer()) {
            kafka.start();
            testKafkaFunctionality(kafka.getBootstrapServers());
        }
    }

    @Test
    public void testExternalZookeeperWithKafkaNetwork() throws Exception {
        try (KafkaContainer kafka = new KafkaContainer().withExternalZookeeper("zookeeper:2181");GenericContainer zookeeper = new GenericContainer("confluentinc/cp-zookeeper:4.0.0").withNetwork(kafka.getNetwork()).withNetworkAliases("zookeeper").withEnv("ZOOKEEPER_CLIENT_PORT", "2181")) {
            Stream.of(kafka, zookeeper).parallel().forEach(GenericContainer::start);
            testKafkaFunctionality(kafka.getBootstrapServers());
        }
    }

    @Test
    public void testExternalZookeeperWithExternalNetwork() throws Exception {
        try (Network network = Network.newNetwork();KafkaContainer kafka = new KafkaContainer().withNetwork(network).withExternalZookeeper("zookeeper:2181");GenericContainer zookeeper = new GenericContainer("confluentinc/cp-zookeeper:4.0.0").withNetwork(network).withNetworkAliases("zookeeper").withEnv("ZOOKEEPER_CLIENT_PORT", "2181")) {
            Stream.of(kafka, zookeeper).parallel().forEach(GenericContainer::start);
            testKafkaFunctionality(kafka.getBootstrapServers());
        }
    }
}

