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
 * Unit test for testing goals with excluded brokers for replica move under fixed cluster properties.
 */
@RunWith(Parameterized.class)
public class ExcludedBrokersForReplicaMoveTest {
    @Rule
    public ExpectedException expected = ExpectedException.none();

    private int _testId;

    private Goal _goal;

    private OptimizationOptions _optimizationOptions;

    private Class<Throwable> _exceptionClass;

    private ClusterModel _clusterModel;

    private Boolean _expectedToOptimize;

    /**
     * Constructor of Excluded Brokers For Replica Move Test.
     *
     * @param testId
     * 		the test id
     * @param goal
     * 		Goal to be tested.
     * @param excludedBrokersForReplicaMove
     * 		Brokers excluded from receiving replicas upon proposal generation.
     * @param exceptionClass
     * 		Expected exception class (if any).
     * @param clusterModel
     * 		Cluster model to be used for the test.
     * @param expectedToOptimize
     * 		The expectation on whether the cluster state will be considered optimized or not.
     */
    public ExcludedBrokersForReplicaMoveTest(int testId, Goal goal, Set<Integer> excludedBrokersForReplicaMove, Class<Throwable> exceptionClass, ClusterModel clusterModel, Boolean expectedToOptimize) {
        _testId = testId;
        _goal = goal;
        _optimizationOptions = new OptimizationOptions(Collections.emptySet(), Collections.emptySet(), excludedBrokersForReplicaMove);
        _exceptionClass = exceptionClass;
        _clusterModel = clusterModel;
        _expectedToOptimize = expectedToOptimize;
    }

    @Test
    public void test() throws Exception {
        if ((_exceptionClass) == null) {
            Map<TopicPartition, List<Integer>> initReplicaDistribution = _clusterModel.getReplicaDistribution();
            Map<TopicPartition, Integer> initLeaderDistribution = _clusterModel.getLeaderDistribution();
            Set<Integer> excludedBrokersForReplicaMove = _optimizationOptions.excludedBrokersForReplicaMove();
            if (_expectedToOptimize) {
                Assert.assertTrue((("Failed to optimize " + (_goal.name())) + " with excluded brokers for replica move."), _goal.optimize(_clusterModel, Collections.emptySet(), _optimizationOptions));
            } else {
                Assert.assertFalse(((("Optimized " + (_goal.name())) + " with excluded brokers for replicaMove ") + excludedBrokersForReplicaMove), _goal.optimize(_clusterModel, Collections.emptySet(), _optimizationOptions));
            }
            // Generated proposals cannot move replicas to the excluded brokers for replica move.
            if (!(excludedBrokersForReplicaMove.isEmpty())) {
                Set<ExecutionProposal> goalProposals = AnalyzerUtils.getDiff(initReplicaDistribution, initLeaderDistribution, _clusterModel);
                for (ExecutionProposal proposal : goalProposals) {
                    if ((proposal.hasReplicaAction()) && (violatesExcludedBrokersForReplicaMove(excludedBrokersForReplicaMove, proposal))) {
                        Assert.fail(String.format("A replica move in %s to an excluded broker for replica move %s.", proposal, excludedBrokersForReplicaMove));
                    }
                }
            }
        } else {
            expected.expect(_exceptionClass);
            Assert.assertTrue("Failed to optimize with excluded brokers for replica move.", _goal.optimize(_clusterModel, Collections.emptySet(), _optimizationOptions));
        }
    }
}

