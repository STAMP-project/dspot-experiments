/**
 * Copyright 2017 LinkedIn Corp. Licensed under the BSD 2-Clause License (the "License"). See License in the project root for license information.
 */
package com.linkedin.kafka.cruisecontrol.analyzer;


import com.linkedin.kafka.cruisecontrol.common.ClusterProperty;
import com.linkedin.kafka.cruisecontrol.common.TestConstants;
import com.linkedin.kafka.cruisecontrol.model.ClusterModel;
import com.linkedin.kafka.cruisecontrol.model.RandomCluster;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.linkedin.kafka.cruisecontrol.common.TestConstants.Distribution.EXPONENTIAL;


/**
 * Unit test for testing with different goals and fixed cluster properties.
 */
@RunWith(Parameterized.class)
public class RandomGoalTest {
    private static final Logger LOG = LoggerFactory.getLogger(RandomGoalTest.class);

    private static final Random RANDOM = new Random(34534534);

    private Map<ClusterProperty, Number> _modifiedProperties;

    private List<String> _goalNameByPriority;

    private BalancingConstraint _balancingConstraint;

    private List<OptimizationVerifier.Verification> _verifications;

    /**
     * Constructor of Random Goal Test.
     *
     * @param modifiedProperties
     * 		Modified cluster properties over the {@link TestConstants#BASE_PROPERTIES}.
     * @param goalNameByPriority
     * 		Goal name by priority.
     * @param balancingConstraint
     * 		the balancing constraints.
     * @param verifications
     * 		the verifications to make.
     */
    public RandomGoalTest(Map<ClusterProperty, Number> modifiedProperties, List<String> goalNameByPriority, BalancingConstraint balancingConstraint, List<OptimizationVerifier.Verification> verifications) {
        _modifiedProperties = modifiedProperties;
        _goalNameByPriority = goalNameByPriority;
        _balancingConstraint = balancingConstraint;
        _verifications = verifications;
    }

    @Test
    public void test() throws Exception {
        // Create cluster properties by applying modified properties to base properties.
        Map<ClusterProperty, Number> clusterProperties = new HashMap<>(TestConstants.BASE_PROPERTIES);
        clusterProperties.putAll(_modifiedProperties);
        RandomGoalTest.LOG.debug("Replica distribution: {} || Goals: {}.", EXPONENTIAL, _goalNameByPriority);
        ClusterModel clusterModel = RandomCluster.generate(clusterProperties);
        RandomCluster.populate(clusterModel, clusterProperties, EXPONENTIAL);
        Assert.assertTrue("Random Goal Test failed to improve the existing state.", OptimizationVerifier.executeGoalsFor(_balancingConstraint, clusterModel, _goalNameByPriority, _verifications));
    }
}

