package org.testcontainers.containers;


import org.junit.Test;


public class PulsarContainerTest {
    public static final String TEST_TOPIC = "test_topic";

    @Test
    public void testUsage() throws Exception {
        try (PulsarContainer pulsar = new PulsarContainer()) {
            pulsar.start();
            testPulsarFunctionality(pulsar.getPulsarBrokerUrl());
        }
    }
}

