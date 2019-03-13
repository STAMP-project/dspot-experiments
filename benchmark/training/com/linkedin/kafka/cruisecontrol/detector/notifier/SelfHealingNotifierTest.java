/**
 * Copyright 2017 LinkedIn Corp. Licensed under the BSD 2-Clause License (the "License"). See License in the project root for license information.
 */
package com.linkedin.kafka.cruisecontrol.detector.notifier;


import AnomalyNotificationResult.Action.CHECK;
import AnomalyNotificationResult.Action.FIX;
import AnomalyNotificationResult.Action.IGNORE;
import AnomalyType.BROKER_FAILURE;
import AnomalyType.GOAL_VIOLATION;
import AnomalyType.METRIC_ANOMALY;
import SelfHealingNotifier.SELF_HEALING_BROKER_FAILURE_ENABLED_CONFIG;
import SelfHealingNotifier.SELF_HEALING_ENABLED_CONFIG;
import SelfHealingNotifier.SELF_HEALING_GOAL_VIOLATION_ENABLED_CONFIG;
import SelfHealingNotifier.SELF_HEALING_METRIC_ANOMALY_ENABLED_CONFIG;
import com.linkedin.kafka.cruisecontrol.KafkaCruiseControl;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.common.utils.MockTime;
import org.apache.kafka.common.utils.Time;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import static SelfHealingNotifier.DEFAULT_ALERT_THRESHOLD_MS;
import static SelfHealingNotifier.DEFAULT_AUTO_FIX_THRESHOLD_MS;


/**
 * Unit test for SelfHealingNotifier.
 */
public class SelfHealingNotifierTest {
    private static final boolean EXCLUDE_RECENTLY_DEMOTED_BROKERS = true;

    private static final boolean EXCLUDE_RECENTLY_REMOVED_BROKERS = true;

    @Test
    public void testOnBrokerFailure() {
        final long failureTime1 = 200L;
        final long failureTime2 = 400L;
        final long startTime = 500L;
        KafkaCruiseControl mockKafkaCruiseControl = EasyMock.mock(KafkaCruiseControl.class);
        Time mockTime = new MockTime(0, startTime, TimeUnit.NANOSECONDS.convert(startTime, TimeUnit.MILLISECONDS));
        SelfHealingNotifierTest.TestingBrokerFailureAutoFixNotifier anomalyNotifier = new SelfHealingNotifierTest.TestingBrokerFailureAutoFixNotifier(mockTime);
        anomalyNotifier.configure(Collections.singletonMap(SELF_HEALING_BROKER_FAILURE_ENABLED_CONFIG, "true"));
        Map<Integer, Long> failedBrokers = new HashMap<>();
        failedBrokers.put(1, failureTime1);
        failedBrokers.put(2, failureTime2);
        boolean allowCapacityEstimation = true;
        AnomalyNotificationResult result = onBrokerFailure(new com.linkedin.kafka.cruisecontrol.detector.BrokerFailures(mockKafkaCruiseControl, failedBrokers, allowCapacityEstimation, SelfHealingNotifierTest.EXCLUDE_RECENTLY_DEMOTED_BROKERS, SelfHealingNotifierTest.EXCLUDE_RECENTLY_REMOVED_BROKERS));
        Assert.assertEquals(CHECK, result.action());
        Assert.assertEquals((((DEFAULT_ALERT_THRESHOLD_MS) + failureTime1) - (mockTime.milliseconds())), result.delay());
        Assert.assertFalse(anomalyNotifier._alertCalled.get(BROKER_FAILURE));
        // Sleep to 1 ms before alert.
        mockTime.sleep(((result.delay()) - 1));
        result = anomalyNotifier.onBrokerFailure(new com.linkedin.kafka.cruisecontrol.detector.BrokerFailures(mockKafkaCruiseControl, failedBrokers, allowCapacityEstimation, SelfHealingNotifierTest.EXCLUDE_RECENTLY_DEMOTED_BROKERS, SelfHealingNotifierTest.EXCLUDE_RECENTLY_REMOVED_BROKERS));
        Assert.assertEquals(CHECK, result.action());
        Assert.assertEquals(1, result.delay());
        Assert.assertFalse(anomalyNotifier._alertCalled.get(BROKER_FAILURE));
        // Sleep 1 ms
        mockTime.sleep(1);
        anomalyNotifier.resetAlert(BROKER_FAILURE);
        result = anomalyNotifier.onBrokerFailure(new com.linkedin.kafka.cruisecontrol.detector.BrokerFailures(mockKafkaCruiseControl, failedBrokers, allowCapacityEstimation, SelfHealingNotifierTest.EXCLUDE_RECENTLY_DEMOTED_BROKERS, SelfHealingNotifierTest.EXCLUDE_RECENTLY_REMOVED_BROKERS));
        Assert.assertEquals(CHECK, result.action());
        Assert.assertEquals((((DEFAULT_AUTO_FIX_THRESHOLD_MS) + failureTime1) - (mockTime.milliseconds())), result.delay());
        Assert.assertTrue(anomalyNotifier._alertCalled.get(BROKER_FAILURE));
        // Sleep to 1 ms before alert.
        mockTime.sleep(((result.delay()) - 1));
        anomalyNotifier.resetAlert(BROKER_FAILURE);
        result = anomalyNotifier.onBrokerFailure(new com.linkedin.kafka.cruisecontrol.detector.BrokerFailures(mockKafkaCruiseControl, failedBrokers, allowCapacityEstimation, SelfHealingNotifierTest.EXCLUDE_RECENTLY_DEMOTED_BROKERS, SelfHealingNotifierTest.EXCLUDE_RECENTLY_REMOVED_BROKERS));
        Assert.assertEquals(CHECK, result.action());
        Assert.assertEquals(1, result.delay());
        Assert.assertTrue(anomalyNotifier._alertCalled.get(BROKER_FAILURE));
        Assert.assertFalse(anomalyNotifier._autoFixTriggered.get(BROKER_FAILURE));
        // Sleep 1 ms
        mockTime.sleep(1);
        anomalyNotifier.resetAlert(BROKER_FAILURE);
        result = anomalyNotifier.onBrokerFailure(new com.linkedin.kafka.cruisecontrol.detector.BrokerFailures(mockKafkaCruiseControl, failedBrokers, allowCapacityEstimation, SelfHealingNotifierTest.EXCLUDE_RECENTLY_DEMOTED_BROKERS, SelfHealingNotifierTest.EXCLUDE_RECENTLY_REMOVED_BROKERS));
        Assert.assertEquals(FIX, result.action());
        Assert.assertEquals((-1L), result.delay());
        Assert.assertTrue(anomalyNotifier._alertCalled.get(BROKER_FAILURE));
        Assert.assertTrue(anomalyNotifier._autoFixTriggered.get(BROKER_FAILURE));
        Assert.assertFalse(anomalyNotifier._alertCalled.get(GOAL_VIOLATION));
        Assert.assertFalse(anomalyNotifier._alertCalled.get(METRIC_ANOMALY));
    }

    @Test
    public void testSelfHealingDisabled() {
        final long startTime = 500L;
        Time mockTime = new MockTime(startTime);
        KafkaCruiseControl mockKafkaCruiseControl = EasyMock.mock(KafkaCruiseControl.class);
        SelfHealingNotifierTest.TestingBrokerFailureAutoFixNotifier anomalyNotifier = new SelfHealingNotifierTest.TestingBrokerFailureAutoFixNotifier(mockTime);
        Map<String, String> selfHealingExplicitlyDisabled = new HashMap<>(4);
        selfHealingExplicitlyDisabled.put(SELF_HEALING_BROKER_FAILURE_ENABLED_CONFIG, "false");
        selfHealingExplicitlyDisabled.put(SELF_HEALING_GOAL_VIOLATION_ENABLED_CONFIG, "false");
        selfHealingExplicitlyDisabled.put(SELF_HEALING_METRIC_ANOMALY_ENABLED_CONFIG, "false");
        // Set to verify the overriding of specific config over general config
        selfHealingExplicitlyDisabled.put(SELF_HEALING_ENABLED_CONFIG, "true");
        configure(selfHealingExplicitlyDisabled);
        // (1) Broker Failure
        final long failureTime1 = 200L;
        final long failureTime2 = 400L;
        Map<Integer, Long> failedBrokers = new HashMap<>();
        failedBrokers.put(1, failureTime1);
        failedBrokers.put(2, failureTime2);
        mockTime.sleep(((DEFAULT_AUTO_FIX_THRESHOLD_MS) + failureTime1));
        anomalyNotifier.resetAlert(BROKER_FAILURE);
        AnomalyNotificationResult result = onBrokerFailure(new com.linkedin.kafka.cruisecontrol.detector.BrokerFailures(mockKafkaCruiseControl, failedBrokers, true, SelfHealingNotifierTest.EXCLUDE_RECENTLY_DEMOTED_BROKERS, SelfHealingNotifierTest.EXCLUDE_RECENTLY_REMOVED_BROKERS));
        Assert.assertEquals(IGNORE, result.action());
        Assert.assertTrue(anomalyNotifier._alertCalled.get(BROKER_FAILURE));
        Assert.assertFalse(anomalyNotifier._autoFixTriggered.get(BROKER_FAILURE));
        // (2) Goal Violation
        anomalyNotifier.resetAlert(GOAL_VIOLATION);
        result = onGoalViolation(new com.linkedin.kafka.cruisecontrol.detector.GoalViolations(mockKafkaCruiseControl, true, SelfHealingNotifierTest.EXCLUDE_RECENTLY_DEMOTED_BROKERS, SelfHealingNotifierTest.EXCLUDE_RECENTLY_REMOVED_BROKERS));
        Assert.assertEquals(IGNORE, result.action());
        Assert.assertTrue(anomalyNotifier._alertCalled.get(GOAL_VIOLATION));
        Assert.assertFalse(anomalyNotifier._autoFixTriggered.get(GOAL_VIOLATION));
        // (3) Metric Anomaly
        anomalyNotifier.resetAlert(METRIC_ANOMALY);
        result = onMetricAnomaly(new com.linkedin.kafka.cruisecontrol.detector.KafkaMetricAnomaly(mockKafkaCruiseControl, "", null, null, null));
        Assert.assertEquals(IGNORE, result.action());
        Assert.assertTrue(anomalyNotifier._alertCalled.get(METRIC_ANOMALY));
        Assert.assertFalse(anomalyNotifier._autoFixTriggered.get(METRIC_ANOMALY));
    }

    private static class TestingBrokerFailureAutoFixNotifier extends SelfHealingNotifier {
        final Map<AnomalyType, Boolean> _alertCalled;

        final Map<AnomalyType, Boolean> _autoFixTriggered;

        TestingBrokerFailureAutoFixNotifier(Time time) {
            super(time);
            _alertCalled = new HashMap(AnomalyType.cachedValues().size());
            _autoFixTriggered = new HashMap(AnomalyType.cachedValues().size());
            for (AnomalyType alertType : AnomalyType.cachedValues()) {
                _alertCalled.put(alertType, false);
                _autoFixTriggered.put(alertType, false);
            }
        }

        @Override
        public void alert(Object anomaly, boolean autoFixTriggered, long selfHealingStartTime, AnomalyType anomalyType) {
            _alertCalled.put(anomalyType, true);
            _autoFixTriggered.put(anomalyType, autoFixTriggered);
        }

        void resetAlert(AnomalyType anomalyType) {
            _autoFixTriggered.put(anomalyType, false);
            _alertCalled.put(anomalyType, false);
        }
    }
}

