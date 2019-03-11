/**
 * Copyright 2017 LinkedIn Corp. Licensed under the BSD 2-Clause License (the "License"). See License in the project root for license information.
 */
package com.linkedin.kafka.cruisecontrol.analyzer;


import com.linkedin.kafka.cruisecontrol.common.ClusterProperty;
import com.linkedin.kafka.cruisecontrol.common.TestConstants;
import com.linkedin.kafka.cruisecontrol.model.ClusterModel;
import com.linkedin.kafka.cruisecontrol.model.RandomCluster;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.linkedin.kafka.cruisecontrol.common.TestConstants.Distribution.UNIFORM;


@RunWith(Parameterized.class)
public class RandomSelfHealingTest {
    private static final Logger LOG = LoggerFactory.getLogger(RandomSelfHealingTest.class);

    private int _testId;

    private Map<ClusterProperty, Number> _modifiedProperties;

    private List<String> _goalNameByPriority;

    private BalancingConstraint _balancingConstraint;

    private Set<String> _excludedTopics;

    private List<OptimizationVerifier.Verification> _verifications;

    private boolean _leaderInFirstPosition;

    /**
     * Constructor of Self Healing Test.
     *
     * @param testId
     * 		Test id.
     * @param modifiedProperties
     * 		Modified cluster properties over the {@link TestConstants#BASE_PROPERTIES}.
     * @param goalNameByPriority
     * 		Goal name by priority.
     * @param balancingConstraint
     * 		Balancing constraint.
     * @param excludedTopics
     * 		Excluded topics.
     * @param verifications
     * 		the verifications to make.
     * @param leaderInFirstPosition
     * 		Leader of each partition is in the first position or not.
     */
    public RandomSelfHealingTest(int testId, Map<ClusterProperty, Number> modifiedProperties, List<String> goalNameByPriority, BalancingConstraint balancingConstraint, Collection<String> excludedTopics, List<OptimizationVerifier.Verification> verifications, boolean leaderInFirstPosition) {
        _testId = testId;
        _modifiedProperties = modifiedProperties;
        _goalNameByPriority = goalNameByPriority;
        _balancingConstraint = balancingConstraint;
        _excludedTopics = new HashSet<>(excludedTopics);
        _verifications = verifications;
        _leaderInFirstPosition = leaderInFirstPosition;
    }

    @Test
    public void test() throws Exception {
        // Create cluster properties by applying modified properties to base properties.
        Map<ClusterProperty, Number> clusterProperties = new HashMap<>(TestConstants.BASE_PROPERTIES);
        clusterProperties.putAll(_modifiedProperties);
        RandomSelfHealingTest.LOG.debug("Replica distribution: {}.", UNIFORM);
        ClusterModel clusterModel = RandomCluster.generate(clusterProperties);
        RandomCluster.populate(clusterModel, clusterProperties, UNIFORM, true, _leaderInFirstPosition, _excludedTopics);
        Assert.assertTrue("Self Healing Test failed to improve the existing state.", OptimizationVerifier.executeGoalsFor(_balancingConstraint, clusterModel, _goalNameByPriority, _excludedTopics, _verifications));
    }
}

