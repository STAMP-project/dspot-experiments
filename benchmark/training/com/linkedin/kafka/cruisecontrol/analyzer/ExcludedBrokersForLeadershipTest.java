/**
 * Copyright 2019 LinkedIn Corp. Licensed under the BSD 2-Clause License (the "License"). See License in the project root for license information.
 */
package com.linkedin.kafka.cruisecontrol.analyzer;


import com.linkedin.kafka.cruisecontrol.analyzer.goals.Goal;
import com.linkedin.kafka.cruisecontrol.executor.ExecutionProposal;
import com.linkedin.kafka.cruisecontrol.model.ClusterModel;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.kafka.common.TopicPartition;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Unit test for testing goals with excluded brokers for leadership under fixed cluster properties.
 */
@RunWith(Parameterized.class)
public class ExcludedBrokersForLeadershipTest {
    @Rule
    public ExpectedException expected = ExpectedException.none();

    private int _testId;

    private Goal _goal;

    private OptimizationOptions _optimizationOptions;

    private Class<Throwable> _exceptionClass;

    private ClusterModel _clusterModel;

    private Boolean _expectedToOptimize;

    /**
     * Constructor of Excluded Brokers For Leadership Test.
     *
     * @param testId
     * 		the test id
     * @param goal
     * 		Goal to be tested.
     * @param excludedBrokersForLeadership
     * 		Brokers excluded from receiving leadership upon proposal generation.
     * @param exceptionClass
     * 		Expected exception class (if any).
     * @param clusterModel
     * 		Cluster model to be used for the test.
     * @param expectedToOptimize
     * 		The expectation on whether the cluster state will be considered optimized or not.
     */
    public ExcludedBrokersForLeadershipTest(int testId, Goal goal, Set<Integer> excludedBrokersForLeadership, Class<Throwable> exceptionClass, ClusterModel clusterModel, Boolean expectedToOptimize) {
        _testId = testId;
        _goal = goal;
        _optimizationOptions = new OptimizationOptions(Collections.emptySet(), excludedBrokersForLeadership);
        _exceptionClass = exceptionClass;
        _clusterModel = clusterModel;
        _expectedToOptimize = expectedToOptimize;
    }

    @Test
    public void test() throws Exception {
        if ((_exceptionClass) == null) {
            Map<TopicPartition, List<Integer>> initReplicaDistribution = _clusterModel.getReplicaDistribution();
            Map<TopicPartition, Integer> initLeaderDistribution = _clusterModel.getLeaderDistribution();
            Set<Integer> excludedBrokersForLeadership = _optimizationOptions.excludedBrokersForLeadership();
            if (_expectedToOptimize) {
                Assert.assertTrue((("Failed to optimize " + (_goal.name())) + " with excluded brokers for leadership."), _goal.optimize(_clusterModel, Collections.emptySet(), _optimizationOptions));
            } else {
                Assert.assertFalse(((("Optimized " + (_goal.name())) + " with excluded brokers for leadership ") + excludedBrokersForLeadership), _goal.optimize(_clusterModel, Collections.emptySet(), _optimizationOptions));
            }
            // Generated proposals cannot move leadership to the excluded brokers for leadership.
            if (!(excludedBrokersForLeadership.isEmpty())) {
                Set<ExecutionProposal> goalProposals = AnalyzerUtils.getDiff(initReplicaDistribution, initLeaderDistribution, _clusterModel);
                for (ExecutionProposal proposal : goalProposals) {
                    if (((proposal.hasLeaderAction()) && (excludedBrokersForLeadership.contains(proposal.newLeader()))) && (_clusterModel.broker(proposal.oldLeader()).isAlive())) {
                        Assert.fail(String.format("Leadership move in %s from an online replica to an excluded broker for leadership %s.", proposal, excludedBrokersForLeadership));
                    }
                }
            }
        } else {
            expected.expect(_exceptionClass);
            Assert.assertTrue("Failed to optimize with excluded brokers for leadership.", _goal.optimize(_clusterModel, Collections.emptySet(), _optimizationOptions));
        }
    }
}

