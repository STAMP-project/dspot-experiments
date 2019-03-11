/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.serverhealth;


import HealthStateLevel.ERROR;
import Timeout.NEVER;
import Timeout.NINETY_SECONDS;
import Timeout.THREE_MINUTES;
import Timeout.TWO_MINUTES;
import com.google.common.collect.Sets;
import com.thoughtworks.go.config.materials.mercurial.HgMaterial;
import com.thoughtworks.go.config.materials.svn.SvnMaterialConfig;
import com.thoughtworks.go.helper.GoConfigMother;
import com.thoughtworks.go.helper.MaterialConfigsMother;
import com.thoughtworks.go.helper.MaterialsMother;
import com.thoughtworks.go.helper.PipelineConfigMother;
import com.thoughtworks.go.util.TestingClock;
import java.util.Collections;
import java.util.Set;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;


public class ServerHealthServiceTest {
    private ServerHealthService serverHealthService;

    private HealthStateType globalId;

    private HealthStateType groupId;

    private HealthStateType pipelineId;

    private static final String PIPELINE_NAME = "pipeline";

    private TestingClock testingClock;

    @Test
    public void shouldRemoveExpiredLogMessages() throws Exception {
        testingClock.setTime(new DateTime(2002, 10, 10, 10, 10, 10, 10));
        ServerHealthState expiresInNintySecs = ServerHealthState.warning("hg-message1", "description", HealthStateType.databaseDiskFull(), NINETY_SECONDS);
        ServerHealthState expiresInThreeMins = ServerHealthState.warning("hg-message2", "description", HealthStateType.artifactsDirChanged(), THREE_MINUTES);
        ServerHealthState expiresNever = ServerHealthState.warning("hg-message3", "description", HealthStateType.artifactsDiskFull(), NEVER);
        serverHealthService.update(expiresInThreeMins);
        serverHealthService.update(expiresInNintySecs);
        serverHealthService.update(expiresNever);
        serverHealthService.purgeStaleHealthMessages(new BasicCruiseConfig());
        ServerHealthStates logs = serverHealthService.logs();
        Assert.assertThat(logs.size(), Matchers.is(3));
        Assert.assertThat(logs, Matchers.hasItems(expiresInThreeMins, expiresInNintySecs, expiresNever));
        testingClock.addSeconds(100);
        serverHealthService.purgeStaleHealthMessages(new BasicCruiseConfig());
        logs = serverHealthService.logs();
        Assert.assertThat(logs.size(), Matchers.is(2));
        Assert.assertThat(logs, Matchers.hasItems(expiresInThreeMins, expiresNever));
        testingClock.addMillis(((int) (TWO_MINUTES.inMillis())));
        serverHealthService.purgeStaleHealthMessages(new BasicCruiseConfig());
        logs = serverHealthService.logs();
        Assert.assertThat(logs.size(), Matchers.is(1));
        Assert.assertThat(logs, Matchers.hasItem(expiresNever));
    }

    @Test
    public void shouldRemoveErrorLogWhenCorrespondingMaterialIsMissing() throws Exception {
        serverHealthService.update(ServerHealthState.error("hg-message", "description", HealthStateType.general(forMaterial(MaterialsMother.hgMaterial()))));
        SvnMaterialConfig svnMaterialConfig = MaterialConfigsMother.svnMaterialConfig();
        serverHealthService.update(ServerHealthState.error("svn-message", "description", HealthStateType.general(forMaterialConfig(svnMaterialConfig))));
        CruiseConfig cruiseConfig = new BasicCruiseConfig();
        cruiseConfig.addPipeline("defaultGroup", new PipelineConfig(new CaseInsensitiveString("dev"), new com.thoughtworks.go.config.materials.MaterialConfigs(svnMaterialConfig), new StageConfig(new CaseInsensitiveString("first"), new JobConfigs())));
        serverHealthService.purgeStaleHealthMessages(cruiseConfig);
        Assert.assertThat(serverHealthService.logs().size(), Matchers.is(1));
    }

    @Test
    public void shouldRemoveErrorLogWhenCorrespondingPipelineIsMissing() throws Exception {
        serverHealthService.update(ServerHealthState.error("message", "description", pipelineId));
        serverHealthService.update(ServerHealthState.error("message", "description", HealthStateType.general(forPipeline("other"))));
        serverHealthService.purgeStaleHealthMessages(new BasicCruiseConfig());
        Assert.assertThat(serverHealthService.logs().size(), Matchers.is(0));
    }

    @Test
    public void shouldRemoveErrorLogWhenCorrespondingGroupIsMissing() throws Exception {
        serverHealthService.update(ServerHealthState.error("message", "description", groupId));
        serverHealthService.purgeStaleHealthMessages(new BasicCruiseConfig());
        Assert.assertThat(serverHealthService.logs().size(), Matchers.is(0));
    }

    @Test
    public void shouldReturnErrorLogs() throws Exception {
        serverHealthService.update(ServerHealthState.error("message", "description", pipelineId));
        CruiseConfig cruiseConfig = new BasicCruiseConfig();
        new GoConfigMother().addPipeline(cruiseConfig, ServerHealthServiceTest.PIPELINE_NAME, "stageName", "jon");
        serverHealthService.purgeStaleHealthMessages(cruiseConfig);
        Assert.assertThat(serverHealthService.logs().size(), Matchers.is(1));
    }

    @Test
    public void shouldUpdateLogInServerHealth() throws Exception {
        ServerHealthState serverHealthState = ServerHealthState.error("message", "description", globalId);
        serverHealthService.update(serverHealthState);
        ServerHealthState newServerHealthState = ServerHealthState.error("updated message", "updated description", globalId);
        serverHealthService.update(newServerHealthState);
        Assert.assertThat(serverHealthService.logs().size(), Matchers.is(1));
        Assert.assertThat(serverHealthService, ServerHealthMatcher.containsState(globalId, ERROR, "updated message"));
    }

    @Test
    public void shouldAddMultipleLogToServerHealth() throws Exception {
        Assert.assertThat(serverHealthService.update(ServerHealthState.error("message", "description", globalId)), Matchers.is(globalId));
        Assert.assertThat(serverHealthService.update(ServerHealthState.error("message", "description", pipelineId)), Matchers.is(pipelineId));
        Assert.assertThat(serverHealthService.logs().size(), Matchers.is(2));
        Assert.assertThat(serverHealthService, ServerHealthMatcher.containsState(globalId));
        Assert.assertThat(serverHealthService, ServerHealthMatcher.containsState(pipelineId));
    }

    @Test
    public void shouldRemoveLogWhenUpdateIsFine() throws Exception {
        serverHealthService.update(ServerHealthState.error("message", "description", globalId));
        Assert.assertThat(serverHealthService, ServerHealthMatcher.containsState(globalId));
        Assert.assertThat(serverHealthService.update(ServerHealthState.success(globalId)), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(serverHealthService, ServerHealthMatcher.doesNotContainState(globalId));
    }

    @Test
    public void shouldRemoveLogByCategoryFromServerHealth() throws Exception {
        HealthStateScope.HealthStateScope scope = forPipeline(ServerHealthServiceTest.PIPELINE_NAME);
        serverHealthService.update(ServerHealthState.error("message", "description", HealthStateType.general(scope)));
        serverHealthService.update(ServerHealthState.error("message", "description", HealthStateType.invalidLicense(scope)));
        serverHealthService.update(ServerHealthState.error("message", "description", globalId));
        Assert.assertThat(serverHealthService.logs().size(), Matchers.is(3));
        serverHealthService.removeByScope(scope);
        Assert.assertThat(serverHealthService.logs().size(), Matchers.is(1));
        Assert.assertThat(serverHealthService, ServerHealthMatcher.containsState(globalId));
    }

    @Test
    public void stateRelatedPipelineNames() {
        HgMaterial hgMaterial = MaterialsMother.hgMaterial();
        CruiseConfig config = new BasicCruiseConfig();
        config.addPipeline("group", PipelineConfigMother.pipelineConfig(ServerHealthServiceTest.PIPELINE_NAME, new com.thoughtworks.go.config.materials.MaterialConfigs(hgMaterial.config())));
        config.addPipeline("group", PipelineConfigMother.pipelineConfig("pipeline2", new com.thoughtworks.go.config.materials.MaterialConfigs(hgMaterial.config())));
        config.addPipeline("group", PipelineConfigMother.pipelineConfig("pipeline3"));
        serverHealthService = new ServerHealthService();
        serverHealthService.update(ServerHealthState.error("message", "description", HealthStateType.general(forPipeline(ServerHealthServiceTest.PIPELINE_NAME))));
        Assert.assertThat(serverHealthService.logs().get(0).getPipelineNames(config), Matchers.equalTo(Collections.singleton(ServerHealthServiceTest.PIPELINE_NAME)));
        serverHealthService = new ServerHealthService();
        serverHealthService.update(ServerHealthState.error("message", "description", HealthStateType.general(forStage(ServerHealthServiceTest.PIPELINE_NAME, "stage1"))));
        Assert.assertThat(serverHealthService.logs().get(0).getPipelineNames(config), Matchers.equalTo(Collections.singleton(ServerHealthServiceTest.PIPELINE_NAME)));
        serverHealthService = new ServerHealthService();
        serverHealthService.update(ServerHealthState.error("message", "description", HealthStateType.general(forJob(ServerHealthServiceTest.PIPELINE_NAME, "stage1", "job1"))));
        Assert.assertThat(serverHealthService.logs().get(0).getPipelineNames(config), Matchers.equalTo(Collections.singleton(ServerHealthServiceTest.PIPELINE_NAME)));
    }

    @Test
    public void globalStateRelatedPipelineNames() {
        HgMaterial hgMaterial = MaterialsMother.hgMaterial();
        CruiseConfig config = new BasicCruiseConfig();
        config.addPipeline("group", PipelineConfigMother.pipelineConfig(ServerHealthServiceTest.PIPELINE_NAME, new com.thoughtworks.go.config.materials.MaterialConfigs(hgMaterial.config())));
        config.addPipeline("group", PipelineConfigMother.pipelineConfig("pipeline2", new com.thoughtworks.go.config.materials.MaterialConfigs(hgMaterial.config())));
        config.addPipeline("group", PipelineConfigMother.pipelineConfig("pipeline3"));
        serverHealthService.update(ServerHealthState.error("message", "description", HealthStateType.invalidConfig()));
        Assert.assertTrue(serverHealthService.logs().get(0).getPipelineNames(config).isEmpty());
    }

    @Test
    public void noPipelineMatchMaterialStateRelatedPipelineNames() {
        HgMaterial hgMaterial = MaterialsMother.hgMaterial();
        CruiseConfig config = new BasicCruiseConfig();
        config.addPipeline("group", PipelineConfigMother.pipelineConfig(ServerHealthServiceTest.PIPELINE_NAME, new com.thoughtworks.go.config.materials.MaterialConfigs(hgMaterial.config())));
        config.addPipeline("group", PipelineConfigMother.pipelineConfig("pipeline2", new com.thoughtworks.go.config.materials.MaterialConfigs(hgMaterial.config())));
        config.addPipeline("group", PipelineConfigMother.pipelineConfig("pipeline3"));
        serverHealthService.update(ServerHealthState.error("message", "description", HealthStateType.general(forMaterial(MaterialsMother.p4Material()))));
        Assert.assertTrue(serverHealthService.logs().get(0).getPipelineNames(config).isEmpty());
    }

    @Test
    public void materialStateRelatedPipelineNames() {
        HgMaterial hgMaterial = MaterialsMother.hgMaterial();
        CruiseConfig config = new BasicCruiseConfig();
        config.addPipeline("group", PipelineConfigMother.pipelineConfig(ServerHealthServiceTest.PIPELINE_NAME, new com.thoughtworks.go.config.materials.MaterialConfigs(hgMaterial.config())));
        config.addPipeline("group", PipelineConfigMother.pipelineConfig("pipeline2", new com.thoughtworks.go.config.materials.MaterialConfigs(hgMaterial.config())));
        config.addPipeline("group", PipelineConfigMother.pipelineConfig("pipeline3"));
        serverHealthService.update(ServerHealthState.error("message", "description", HealthStateType.general(forMaterial(hgMaterial))));
        Set<String> pipelines = serverHealthService.logs().get(0).getPipelineNames(config);
        Assert.assertEquals(Sets.newHashSet("pipeline", "pipeline2"), pipelines);
    }

    @Test
    public void materialUpdateStateRelatedPipelineNames() {
        HgMaterial hgMaterial = MaterialsMother.hgMaterial();
        CruiseConfig config = new BasicCruiseConfig();
        config.addPipeline("group", PipelineConfigMother.pipelineConfig(ServerHealthServiceTest.PIPELINE_NAME, new com.thoughtworks.go.config.materials.MaterialConfigs(hgMaterial.config())));
        config.addPipeline("group", PipelineConfigMother.pipelineConfig("pipeline2", new com.thoughtworks.go.config.materials.MaterialConfigs(hgMaterial.config())));
        config.addPipeline("group", PipelineConfigMother.pipelineConfig("pipeline3"));
        serverHealthService.update(ServerHealthState.error("message", "description", HealthStateType.general(forMaterialUpdate(hgMaterial))));
        Set<String> pipelines = serverHealthService.logs().get(0).getPipelineNames(config);
        Assert.assertEquals(Sets.newHashSet("pipeline", "pipeline2"), pipelines);
    }

    @Test
    public void faninErrorStateRelatedPipelineNames() {
        HgMaterial hgMaterial = MaterialsMother.hgMaterial();
        CruiseConfig config = new BasicCruiseConfig();
        config.addPipeline("group", PipelineConfigMother.pipelineConfig(ServerHealthServiceTest.PIPELINE_NAME, new com.thoughtworks.go.config.materials.MaterialConfigs(hgMaterial.config())));
        config.addPipeline("group", PipelineConfigMother.pipelineConfig("pipeline2", new com.thoughtworks.go.config.materials.MaterialConfigs(hgMaterial.config())));
        config.addPipeline("group", PipelineConfigMother.pipelineConfig("pipeline3"));
        serverHealthService.update(ServerHealthState.error("message", "description", HealthStateType.general(HealthStateScope.HealthStateScope.forFanin("pipeline2"))));
        Set<String> pipelines = serverHealthService.logs().get(0).getPipelineNames(config);
        Assert.assertEquals(Sets.newHashSet("pipeline2"), pipelines);
    }
}

