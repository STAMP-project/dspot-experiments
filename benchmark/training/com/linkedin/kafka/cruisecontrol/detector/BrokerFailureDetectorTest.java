/**
 * Copyright 2017 LinkedIn Corp. Licensed under the BSD 2-Clause License (the "License"). See License in the project root for license information.
 */
package com.linkedin.kafka.cruisecontrol.detector;


import com.linkedin.cruisecontrol.detector.Anomaly;
import com.linkedin.kafka.clients.utils.tests.AbstractKafkaIntegrationTestHarness;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.kafka.common.utils.Time;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for broker failure detector.
 */
public class BrokerFailureDetectorTest extends AbstractKafkaIntegrationTestHarness {
    @Test
    public void testFailureDetection() throws Exception {
        Time mockTime = getMockTime();
        Queue<Anomaly> anomalies = new ConcurrentLinkedQueue<>();
        BrokerFailureDetector detector = createBrokerFailureDetector(anomalies, mockTime);
        try {
            // Start detection.
            detector.startDetection();
            Assert.assertTrue(anomalies.isEmpty());
            int brokerId = 0;
            killBroker(brokerId);
            long start = System.currentTimeMillis();
            while ((anomalies.isEmpty()) && ((System.currentTimeMillis()) < (start + 30000))) {
                // wait for the anomalies to be drained.
            } 
            Assert.assertEquals("One broker failure should have been detected before timeout.", 1, anomalies.size());
            Anomaly anomaly = anomalies.remove();
            Assert.assertTrue("The anomaly should be BrokerFailure", (anomaly instanceof BrokerFailures));
            BrokerFailures brokerFailures = ((BrokerFailures) (anomaly));
            Assert.assertEquals("The failed broker should be 0 and time should be 100L", Collections.singletonMap(brokerId, 100L), brokerFailures.failedBrokers());
            // Bring the broker back
            System.out.println("Starting brokers.");
            restartDeadBroker(brokerId);
            detector.detectBrokerFailures();
            Assert.assertTrue(detector.failedBrokers().isEmpty());
        } finally {
            detector.shutdown();
        }
    }

    @Test
    public void testDetectorStartWithFailedBrokers() throws Exception {
        Time mockTime = getMockTime();
        Queue<Anomaly> anomalies = new ConcurrentLinkedQueue<>();
        BrokerFailureDetector detector = createBrokerFailureDetector(anomalies, mockTime);
        try {
            int brokerId = 0;
            killBroker(brokerId);
            detector.startDetection();
            Assert.assertEquals(Collections.singletonMap(brokerId, 100L), detector.failedBrokers());
        } finally {
            detector.shutdown();
        }
    }

    @Test
    public void testLoadFailedBrokersFromZK() throws Exception {
        Time mockTime = getMockTime();
        Queue<Anomaly> anomalies = new ConcurrentLinkedQueue<>();
        BrokerFailureDetector detector = createBrokerFailureDetector(anomalies, mockTime);
        try {
            detector.startDetection();
            int brokerId = 0;
            killBroker(brokerId);
            long start = System.currentTimeMillis();
            while ((anomalies.isEmpty()) && ((System.currentTimeMillis()) < (start + 30000))) {
                // Wait for the anomalies to be drained.
            } 
            Assert.assertEquals(Collections.singletonMap(brokerId, 100L), detector.failedBrokers());
            // shutdown, advance the clock and create a new detector.
            detector.shutdown();
            mockTime.sleep(100L);
            detector = createBrokerFailureDetector(anomalies, mockTime);
            // start the newly created detector and the broker down time should remain previous time.
            detector.startDetection();
            Assert.assertEquals(Collections.singletonMap(brokerId, 100L), detector.failedBrokers());
        } finally {
            detector.shutdown();
        }
    }
}

