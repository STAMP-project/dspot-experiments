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
package com.thoughtworks.go.domain;


import com.thoughtworks.go.config.ArtifactConfigs;
import com.thoughtworks.go.config.BuildArtifactConfig;
import com.thoughtworks.go.config.PluggableArtifactConfig;
import com.thoughtworks.go.config.TestArtifactConfig;
import com.thoughtworks.go.domain.packagerepository.ConfigurationPropertyMother;
import com.thoughtworks.go.work.DefaultGoPublisher;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static ArtifactPlanType.file;
import static ArtifactPlanType.unit;


public class ArtifactPlanTest {
    private File testFolder;

    private File srcFolder;

    @Test
    public void shouldPublishArtifacts() {
        final DefaultGoPublisher publisher = Mockito.mock(DefaultGoPublisher.class);
        final ArtifactPlan artifactPlan = new ArtifactPlan(file, "src", "dest");
        artifactPlan.publishBuiltInArtifacts(publisher, testFolder);
        Mockito.verify(publisher).upload(new File(testFolder, "src"), "dest");
    }

    @Test
    public void shouldIgnoreIdAndBuildIdAsPartOfEqualAndHashCodeCheck() {
        final ArtifactPlan installer_1 = new ArtifactPlan(file, "src", "dest");
        installer_1.setId(100);
        installer_1.setBuildId(1000);
        final ArtifactPlan installer_2 = new ArtifactPlan(file, "src", "dest");
        installer_2.setId(200);
        installer_2.setBuildId(2000);
        Assert.assertTrue(installer_1.equals(installer_2));
    }

    @Test
    public void shouldNormalizePath() {
        ArtifactPlan artifactPlan = new ArtifactPlan(file, "folder\\src", "folder\\dest");
        Assert.assertThat(artifactPlan.getSrc(), is("folder/src"));
        Assert.assertThat(artifactPlan.getDest(), is("folder/dest"));
    }

    @Test
    public void shouldProvideAppendFilePathToDest() {
        ArtifactPlan artifactPlan = new ArtifactPlan(file, "test/**/*/a.log", "logs");
        Assert.assertThat(artifactPlan.destinationURL(new File("pipelines/pipelineA"), new File("pipelines/pipelineA/test/a/b/a.log")), is("logs/a/b"));
    }

    @Test
    public void shouldProvideAppendFilePathToDestWhenUsingDoubleStart() {
        ArtifactPlan artifactPlan = new ArtifactPlan(file, "**/*/a.log", "logs");
        Assert.assertThat(artifactPlan.destinationURL(new File("pipelines/pipelineA"), new File("pipelines/pipelineA/test/a/b/a.log")), is("logs/test/a/b"));
    }

    @Test
    public void shouldProvideAppendFilePathToDestWhenPathProvidedAreSame() {
        ArtifactPlan artifactPlan = new ArtifactPlan(file, "test/a/b/a.log", "logs");
        Assert.assertThat(artifactPlan.destinationURL(new File("pipelines/pipelineA"), new File("pipelines/pipelineA/test/b/a.log")), is("logs"));
    }

    @Test
    public void shouldProvideAppendFilePathToDestWhenUsingSingleStarToMatchFile() {
        ArtifactPlan artifactPlan = new ArtifactPlan(file, "test/a/b/*.log", "logs");
        Assert.assertThat(artifactPlan.destinationURL(new File("pipelines/pipelineA"), new File("pipelines/pipelineA/test/a/b/a.log")), is("logs"));
    }

    @Test
    public void shouldProvideAppendFilePathToDestWhenPathMatchingAtTheRoot() {
        ArtifactPlan artifactPlan = new ArtifactPlan(file, "*.jar", "logs");
        Assert.assertThat(artifactPlan.destinationURL(new File("pipelines/pipelineA"), new File("pipelines/pipelineA/a.jar")), is("logs"));
    }

    @Test
    public void shouldTrimThePath() {
        Assert.assertThat(new ArtifactPlan(file, "pkg   ", "logs "), is(new ArtifactPlan(file, "pkg", "logs")));
    }

    @Test
    public void toArtifactPlans_shouldConvertArtifactConfigsToArtifactPlanList() {
        final PluggableArtifactConfig artifactConfig = new PluggableArtifactConfig("id", "storeId", ConfigurationPropertyMother.create("Foo", true, "Bar"));
        final ArtifactConfigs artifactConfigs = new ArtifactConfigs(Arrays.asList(new BuildArtifactConfig("source", "destination"), new TestArtifactConfig("test-source", "test-destination"), artifactConfig));
        final List<ArtifactPlan> artifactPlans = ArtifactPlan.toArtifactPlans(artifactConfigs);
        Assert.assertThat(artifactPlans, containsInAnyOrder(new ArtifactPlan(file, "source", "destination"), new ArtifactPlan(unit, "test-source", "test-destination"), new ArtifactPlan(artifactConfig.toJSON())));
    }

    @Test
    public void shouldConvertPluggableArtifactConfigToArtifactPlans() {
        final PluggableArtifactConfig artifactConfig = new PluggableArtifactConfig("ID", "StoreID", ConfigurationPropertyMother.create("Foo", true, "Bar"), ConfigurationPropertyMother.create("Baz", false, "Car"));
        final ArtifactPlan artifactPlan = new ArtifactPlan(artifactConfig);
        Assert.assertThat(artifactPlan.getArtifactPlanType(), is(ArtifactPlanType.external));
        Assert.assertThat(artifactPlan.getPluggableArtifactConfiguration().size(), is(3));
        Assert.assertThat(artifactPlan.getPluggableArtifactConfiguration(), hasEntry("id", "ID"));
        Assert.assertThat(artifactPlan.getPluggableArtifactConfiguration(), hasEntry("storeId", "StoreID"));
        final Map<String, String> configuration = ((Map<String, String>) (artifactPlan.getPluggableArtifactConfiguration().get("configuration")));
        Assert.assertThat(configuration.size(), is(2));
        Assert.assertThat(configuration, hasEntry("Foo", "Bar"));
        Assert.assertThat(configuration, hasEntry("Baz", "Car"));
    }
}

