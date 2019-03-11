/**
 * Copyright 2017 LinkedIn Corp. Licensed under the BSD 2-Clause License (the "License"). See License in the project root for license information.
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
 * Unit test for testing goals with excluded topics under fixed cluster properties.
 */
@RunWith(Parameterized.class)
public class ExcludedTopicsTest {
    @Rule
    public ExpectedException expected = ExpectedException.none();

    private int _testId;

    private Goal _goal;

    private OptimizationOptions _optimizationOptions;

    private Class<Throwable> _exceptionClass;

    private ClusterModel _clusterModel;

    private Boolean _expectedToOptimize;

    /**
     * Constructor of Excluded Topics Test.
     *
     * @param testId
     * 		the test id
     * @param goal
     * 		Goal to be tested.
     * @param excludedTopics
     * 		Topics to be excluded from the goal.
     * @param exceptionClass
     * 		Expected exception class (if any).
     * @param clusterModel
     * 		Cluster model to be used for the test.
     * @param expectedToOptimize
     * 		The expectation on whether the cluster state will be considered optimized or not.
     */
    public ExcludedTopicsTest(int testId, Goal goal, Set<String> excludedTopics, Class<Throwable> exceptionClass, ClusterModel clusterModel, Boolean expectedToOptimize) {
        _testId = testId;
        _goal = goal;
        _optimizationOptions = new OptimizationOptions(excludedTopics);
        _exceptionClass = exceptionClass;
        _clusterModel = clusterModel;
        _expectedToOptimize = expectedToOptimize;
    }

    @Test
    public void test() throws Exception {
        if ((_exceptionClass) == null) {
            Map<TopicPartition, List<Integer>> initReplicaDistribution = _clusterModel.getReplicaDistribution();
            Map<TopicPartition, Integer> initLeaderDistribution = _clusterModel.getLeaderDistribution();
            Set<String> excludedTopics = _optimizationOptions.excludedTopics();
            if (_expectedToOptimize) {
                Assert.assertTrue((("Excluded Topics Test failed to optimize " + (_goal.name())) + " with excluded topics."), _goal.optimize(_clusterModel, Collections.emptySet(), _optimizationOptions));
            } else {
                Assert.assertFalse(((("Excluded Topics Test optimized " + (_goal.name())) + " with excluded topics ") + excludedTopics), _goal.optimize(_clusterModel, Collections.emptySet(), _optimizationOptions));
            }
            // Generated proposals cannot have the excluded topic.
            if (!(excludedTopics.isEmpty())) {
                Set<ExecutionProposal> goalProposals = AnalyzerUtils.getDiff(initReplicaDistribution, initLeaderDistribution, _clusterModel);
                for (ExecutionProposal proposal : goalProposals) {
                    if (excludedTopics.contains(proposal.topic())) {
                        for (int brokerId : proposal.replicasToRemove()) {
                            if (_clusterModel.broker(brokerId).isAlive()) {
                                Assert.fail(String.format("Proposal %s contains excluded topic %s, but the broker %d is still alive.", proposal, proposal.topic(), brokerId));
                            }
                        }
                    }
                }
            }
        } else {
            expected.expect(_exceptionClass);
            Assert.assertTrue("Excluded Topics Test failed to optimize with excluded topics.", _goal.optimize(_clusterModel, Collections.emptySet(), _optimizationOptions));
        }
    }
}

