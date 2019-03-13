/**
 * -
 * -\-\-
 * Helios Client
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
package com.spotify.helios.common.descriptors;


import DeploymentGroup.Builder;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import static RollingUpdateReason.MANUAL;


public class DeploymentGroupTest {
    @Test
    public void verifyBuilder() {
        final DeploymentGroup.Builder builder = DeploymentGroup.newBuilder();
        // Input to setXXX
        final String setName = "foo-group";
        // noinspection ConstantConditions
        final List<HostSelector> setHostSelectors = ImmutableList.of(HostSelector.parse("foo=bar"), HostSelector.parse("baz=qux"));
        final JobId setJobId = JobId.fromString("foo:0.1.0");
        final RolloutOptions setRolloutOptions = RolloutOptions.newBuilder().setTimeout(1000L).setParallelism(2).setMigrate(false).build();
        final DeploymentGroup.RollingUpdateReason setReason = MANUAL;
        // Check setXXX methods
        builder.setName(setName);
        builder.setHostSelectors(setHostSelectors);
        builder.setJobId(setJobId);
        builder.setRolloutOptions(setRolloutOptions);
        builder.setRollingUpdateReason(setReason);
        Assert.assertEquals("name", setName, builder.getName());
        Assert.assertEquals("hostSelectors", setHostSelectors, builder.getHostSelectors());
        Assert.assertEquals("jobId", setJobId, builder.getJobId());
        Assert.assertEquals("rolloutOptions", setRolloutOptions, builder.getRolloutOptions());
        Assert.assertEquals("reason", setReason, builder.getRollingUpdateReason());
        // Check final output
        final DeploymentGroup deploymentGroup = builder.build();
        Assert.assertEquals("name", setName, deploymentGroup.getName());
        Assert.assertEquals("hostSelectors", setHostSelectors, deploymentGroup.getHostSelectors());
        Assert.assertEquals("jobId", setJobId, deploymentGroup.getJobId());
        Assert.assertEquals("rolloutOptions", setRolloutOptions, deploymentGroup.getRolloutOptions());
        Assert.assertEquals("reason", setReason, deploymentGroup.getRollingUpdateReason());
    }
}

