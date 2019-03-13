/**
 * -
 * -\-\-
 * Helios Services
 * --
 * Copyright (C) 2016 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.helios.rollingupdate;


import RolloutTask.Action.AWAIT_UNDEPLOYED;
import RolloutTask.Action.FORCE_UNDEPLOY_JOBS;
import RolloutTask.Action.MARK_UNDEPLOYED;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.spotify.helios.common.descriptors.DeploymentGroup;
import com.spotify.helios.common.descriptors.RolloutOptions;
import com.spotify.helios.common.descriptors.RolloutTask;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class RollingUndeployPlannerTest {
    private static final List<String> HOSTS = ImmutableList.of("agent1", "agent2", "agent3", "agent4");

    @Test
    public void testSerialRollout() {
        final DeploymentGroup deploymentGroup = DeploymentGroup.newBuilder().setRolloutOptions(RolloutOptions.newBuilder().setParallelism(1).build()).build();
        final RolloutPlanner rolloutPlanner = RollingUndeployPlanner.of(deploymentGroup);
        final List<RolloutTask> tasks = rolloutPlanner.plan(RollingUndeployPlannerTest.HOSTS);
        final List<RolloutTask> expected = Lists.newArrayList(RolloutTask.of(FORCE_UNDEPLOY_JOBS, "agent1"), RolloutTask.of(AWAIT_UNDEPLOYED, "agent1"), RolloutTask.of(MARK_UNDEPLOYED, "agent1"), RolloutTask.of(FORCE_UNDEPLOY_JOBS, "agent2"), RolloutTask.of(AWAIT_UNDEPLOYED, "agent2"), RolloutTask.of(MARK_UNDEPLOYED, "agent2"), RolloutTask.of(FORCE_UNDEPLOY_JOBS, "agent3"), RolloutTask.of(AWAIT_UNDEPLOYED, "agent3"), RolloutTask.of(MARK_UNDEPLOYED, "agent3"), RolloutTask.of(FORCE_UNDEPLOY_JOBS, "agent4"), RolloutTask.of(AWAIT_UNDEPLOYED, "agent4"), RolloutTask.of(MARK_UNDEPLOYED, "agent4"));
        Assert.assertEquals(expected, tasks);
    }

    @Test
    public void testParallelRollout() {
        final DeploymentGroup deploymentGroup = DeploymentGroup.newBuilder().setRolloutOptions(RolloutOptions.newBuilder().setParallelism(2).build()).build();
        final RolloutPlanner rolloutPlanner = RollingUndeployPlanner.of(deploymentGroup);
        final List<RolloutTask> tasks = rolloutPlanner.plan(RollingUndeployPlannerTest.HOSTS);
        final List<RolloutTask> expected = Lists.newArrayList(RolloutTask.of(FORCE_UNDEPLOY_JOBS, "agent1"), RolloutTask.of(FORCE_UNDEPLOY_JOBS, "agent2"), RolloutTask.of(AWAIT_UNDEPLOYED, "agent1"), RolloutTask.of(MARK_UNDEPLOYED, "agent1"), RolloutTask.of(AWAIT_UNDEPLOYED, "agent2"), RolloutTask.of(MARK_UNDEPLOYED, "agent2"), RolloutTask.of(FORCE_UNDEPLOY_JOBS, "agent3"), RolloutTask.of(FORCE_UNDEPLOY_JOBS, "agent4"), RolloutTask.of(AWAIT_UNDEPLOYED, "agent3"), RolloutTask.of(MARK_UNDEPLOYED, "agent3"), RolloutTask.of(AWAIT_UNDEPLOYED, "agent4"), RolloutTask.of(MARK_UNDEPLOYED, "agent4"));
        Assert.assertEquals(expected, tasks);
    }

    @Test
    public void testParallelRolloutWithRemainder() {
        final DeploymentGroup deploymentGroup = DeploymentGroup.newBuilder().setRolloutOptions(RolloutOptions.newBuilder().setParallelism(3).build()).build();
        final RolloutPlanner rolloutPlanner = RollingUndeployPlanner.of(deploymentGroup);
        final List<RolloutTask> tasks = rolloutPlanner.plan(RollingUndeployPlannerTest.HOSTS);
        final List<RolloutTask> expected = Lists.newArrayList(RolloutTask.of(FORCE_UNDEPLOY_JOBS, "agent1"), RolloutTask.of(FORCE_UNDEPLOY_JOBS, "agent2"), RolloutTask.of(FORCE_UNDEPLOY_JOBS, "agent3"), RolloutTask.of(AWAIT_UNDEPLOYED, "agent1"), RolloutTask.of(MARK_UNDEPLOYED, "agent1"), RolloutTask.of(AWAIT_UNDEPLOYED, "agent2"), RolloutTask.of(MARK_UNDEPLOYED, "agent2"), RolloutTask.of(AWAIT_UNDEPLOYED, "agent3"), RolloutTask.of(MARK_UNDEPLOYED, "agent3"), RolloutTask.of(FORCE_UNDEPLOY_JOBS, "agent4"), RolloutTask.of(AWAIT_UNDEPLOYED, "agent4"), RolloutTask.of(MARK_UNDEPLOYED, "agent4"));
        Assert.assertEquals(expected, tasks);
    }
}

