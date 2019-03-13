/**
 * Copyright 2017 LinkedIn Corp. Licensed under the BSD 2-Clause License (the "License"). See License in the project root for license information.
 */
package com.linkedin.kafka.cruisecontrol.analyzer;


import com.linkedin.kafka.cruisecontrol.exception.OptimizationFailureException;
import com.linkedin.kafka.cruisecontrol.model.ClusterModel;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Unit test for testing with various balancing percentages and capacity thresholds using various deterministic clusters.
 */
@RunWith(Parameterized.class)
public class DeterministicClusterTest {
    private BalancingConstraint _balancingConstraint;

    private ClusterModel _cluster;

    private List<String> _goalNameByPriority;

    private List<OptimizationVerifier.Verification> _verifications;

    @Rule
    public ExpectedException expected = ExpectedException.none();

    /**
     * Constructor for Deterministic Cluster Test.
     *
     * @param balancingConstraint
     * 		Balancing constraint.
     * @param cluster
     * 		The state of the cluster.
     * @param goalNameByPriority
     * 		Name of goals by the order of execution priority.
     */
    public DeterministicClusterTest(BalancingConstraint balancingConstraint, ClusterModel cluster, List<String> goalNameByPriority, List<OptimizationVerifier.Verification> verifications, Class<? extends Throwable> expectedException) {
        _balancingConstraint = balancingConstraint;
        _cluster = cluster;
        _goalNameByPriority = goalNameByPriority;
        _verifications = verifications;
        if (expectedException != null) {
            expected.expect(expectedException);
        }
    }

    @Test
    public void test() throws Exception {
        try {
            Assert.assertTrue("Deterministic Cluster Test failed to improve the existing state.", OptimizationVerifier.executeGoalsFor(_balancingConstraint, _cluster, _goalNameByPriority, _verifications));
        } catch (OptimizationFailureException optimizationFailureException) {
            // This exception is thrown if rebalance fails due to alive brokers having insufficient capacity.
            if (!(optimizationFailureException.getMessage().contains("Insufficient healthy cluster capacity for resource"))) {
                throw optimizationFailureException;
            }
        }
    }
}

