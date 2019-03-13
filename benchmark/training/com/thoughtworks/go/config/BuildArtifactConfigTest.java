/**
 * Copyright 2018 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.config;


import BuiltinArtifactConfig.DEST;
import BuiltinArtifactConfig.SRC;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class BuildArtifactConfigTest {
    @Test
    public void validate_shouldFailIfSourceIsEmpty() {
        BuildArtifactConfig artifactPlan = new BuildArtifactConfig(null, "bar");
        artifactPlan.validate(ConfigSaveValidationContext.forChain(new JobConfig("jobname")));
        Assert.assertThat(artifactPlan.errors().on(SRC), Matchers.is("Job 'jobname' has an artifact with an empty source"));
    }

    @Test
    public void validate_shouldFailIfDestDoesNotMatchAFilePattern() {
        BuildArtifactConfig artifactPlan = new BuildArtifactConfig("foo/bar", "..");
        artifactPlan.validate(null);
        Assert.assertThat(artifactPlan.errors().on(DEST), Matchers.is("Invalid destination path. Destination path should match the pattern (([.]\\/)?[.][^. ]+)|([^. ].+[^. ])|([^. ][^. ])|([^. ])"));
    }

    @Test
    public void validate_shouldNotFailWhenDestinationIsNotSet() {
        BuildArtifactConfig artifactPlan = new BuildArtifactConfig(null, null);
        artifactPlan.setSource("source");
        artifactPlan.validate(null);
        Assert.assertThat(artifactPlan.errors().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldErrorOutWhenDuplicateArtifactConfigsExists() {
        List<ArtifactConfig> plans = new ArrayList<>();
        BuildArtifactConfig existingPlan = new BuildArtifactConfig("src", "dest");
        plans.add(existingPlan);
        BuildArtifactConfig artifactPlan = new BuildArtifactConfig("src", "dest");
        artifactPlan.validateUniqueness(plans);
        Assert.assertThat(artifactPlan.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(artifactPlan.errors().on(SRC), Matchers.is("Duplicate artifacts defined."));
        Assert.assertThat(artifactPlan.errors().on(DEST), Matchers.is("Duplicate artifacts defined."));
        Assert.assertThat(existingPlan.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(existingPlan.errors().on(SRC), Matchers.is("Duplicate artifacts defined."));
        Assert.assertThat(existingPlan.errors().on(DEST), Matchers.is("Duplicate artifacts defined."));
    }

    @Test
    public void validate_shouldNotFailWhenComparingBuildAndTestArtifacts() {
        List<ArtifactConfig> plans = new ArrayList<>();
        TestArtifactConfig testArtifactConfig = new TestArtifactConfig("src", "dest");
        plans.add(testArtifactConfig);
        BuildArtifactConfig buildArtifactConfig = new BuildArtifactConfig("src", "dest");
        buildArtifactConfig.validateUniqueness(plans);
        Assert.assertThat(buildArtifactConfig.errors().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldAllowOverridingDefaultArtifactDestination() {
        BuildArtifactConfig artifactConfig = new BuildArtifactConfig("src", "dest");
        Assert.assertThat(artifactConfig.getDestination(), Matchers.is("dest"));
        TestArtifactConfig testArtifactConfig = new TestArtifactConfig("src", "destination");
        Assert.assertThat(testArtifactConfig.getDestination(), Matchers.is("destination"));
    }

    @Test
    public void shouldNotOverrideDefaultArtifactDestinationWhenNotSpecified() {
        BuildArtifactConfig artifactConfig = new BuildArtifactConfig("src", null);
        Assert.assertThat(artifactConfig.getDestination(), Matchers.is(""));
        TestArtifactConfig testArtifactConfig = new TestArtifactConfig("src", null);
        Assert.assertThat(testArtifactConfig.getDestination(), Matchers.is("testoutput"));
    }
}

