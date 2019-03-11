/**
 * Copyright 2017 ThoughtWorks, Inc.
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


import Authorization.PRIVILEGES;
import Authorization.PrivilegeState.ON;
import Authorization.PrivilegeType.ADMIN;
import Authorization.TYPE;
import Authorization.UserType.USER;
import BasicPipelineConfigs.AUTHORIZATION;
import PipelineTemplateConfig.NAME;
import com.thoughtworks.go.domain.materials.MaterialConfig;
import com.thoughtworks.go.helper.GoConfigMother;
import com.thoughtworks.go.helper.PipelineConfigMother;
import com.thoughtworks.go.helper.PipelineTemplateConfigMother;
import com.thoughtworks.go.helper.StageConfigMother;
import com.thoughtworks.go.util.DataStructureUtils;
import java.util.Arrays;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class PipelineTemplateConfigTest {
    @Test
    public void shouldFindByName() {
        PipelineTemplateConfig pipelineTemplateConfig = new PipelineTemplateConfig(new CaseInsensitiveString("pipeline"), StageConfigMother.manualStage("manual"), StageConfigMother.manualStage("manual2"));
        Assert.assertThat(pipelineTemplateConfig.findBy(new CaseInsensitiveString("manuaL2")).name(), Matchers.is(new CaseInsensitiveString("manual2")));
    }

    @Test
    public void shouldGetStageByName() {
        PipelineTemplateConfig pipelineTemplateConfig = new PipelineTemplateConfig(new CaseInsensitiveString("pipeline"), StageConfigMother.manualStage("manual"), StageConfigMother.manualStage("manual2"));
        Assert.assertThat(pipelineTemplateConfig.findBy(new CaseInsensitiveString("manual")).name(), Matchers.is(new CaseInsensitiveString("manual")));
        Assert.assertThat(pipelineTemplateConfig.findBy(new CaseInsensitiveString("Does-not-exist")), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldIgnoreCaseWhileMatchingATemplateWithName() {
        Assert.assertThat(new PipelineTemplateConfig(new CaseInsensitiveString("FOO")).matches(new CaseInsensitiveString("foo")), Matchers.is(true));
        Assert.assertThat(new PipelineTemplateConfig(new CaseInsensitiveString("FOO")).matches(new CaseInsensitiveString("FOO")), Matchers.is(true));
        Assert.assertThat(new PipelineTemplateConfig(new CaseInsensitiveString("FOO")).matches(new CaseInsensitiveString("bar")), Matchers.is(false));
    }

    @Test
    public void shouldSetPrimitiveAttributes() {
        PipelineTemplateConfig pipelineTemplateConfig = new PipelineTemplateConfig();
        Map map = DataStructureUtils.m(NAME, "templateName");
        pipelineTemplateConfig.setConfigAttributes(map);
        Assert.assertThat(pipelineTemplateConfig.name(), Matchers.is(new CaseInsensitiveString("templateName")));
    }

    @Test
    public void shouldUpdateAuthorization() {
        PipelineTemplateConfig templateConfig = PipelineTemplateConfigMother.createTemplate("template-1");
        templateConfig.setConfigAttributes(DataStructureUtils.m(AUTHORIZATION, DataStructureUtils.a(DataStructureUtils.m(Authorization.NAME, "loser", TYPE, USER.toString(), PRIVILEGES, DataStructureUtils.a(DataStructureUtils.m(ADMIN.toString(), ON.toString()))), DataStructureUtils.m(Authorization.NAME, "boozer", TYPE, USER.toString(), PRIVILEGES, DataStructureUtils.a(DataStructureUtils.m(ADMIN.toString(), ON.toString()))), DataStructureUtils.m(Authorization.NAME, "geezer", TYPE, USER.toString(), PRIVILEGES, DataStructureUtils.a(DataStructureUtils.m(ADMIN.toString(), ON.toString()))))));
        Authorization authorization = templateConfig.getAuthorization();
        Assert.assertThat(authorization.getAdminsConfig().size(), Matchers.is(3));
        Assert.assertThat(authorization.getAdminsConfig(), Matchers.hasItem(new AdminUser(new CaseInsensitiveString("loser"))));
        Assert.assertThat(authorization.getAdminsConfig(), Matchers.hasItem(new AdminUser(new CaseInsensitiveString("boozer"))));
        Assert.assertThat(authorization.getAdminsConfig(), Matchers.hasItem(new AdminUser(new CaseInsensitiveString("geezer"))));
        Assert.assertThat(authorization.getOperationConfig().size(), Matchers.is(0));
        Assert.assertThat(authorization.getViewConfig().size(), Matchers.is(0));
    }

    @Test
    public void shouldReInitializeAuthorizationIfWeClearAllPermissions() {
        PipelineTemplateConfig templateConfig = PipelineTemplateConfigMother.createTemplate("template-1");
        templateConfig.setConfigAttributes(DataStructureUtils.m(AUTHORIZATION, DataStructureUtils.a(DataStructureUtils.m(Authorization.NAME, "loser", TYPE, USER.toString(), PRIVILEGES, DataStructureUtils.a(DataStructureUtils.m(ADMIN.toString(), ON.toString()))), DataStructureUtils.m(Authorization.NAME, "boozer", TYPE, USER.toString(), PRIVILEGES, DataStructureUtils.a(DataStructureUtils.m(ADMIN.toString(), ON.toString()))), DataStructureUtils.m(Authorization.NAME, "geezer", TYPE, USER.toString(), PRIVILEGES, DataStructureUtils.a(DataStructureUtils.m(ADMIN.toString(), ON.toString()))))));
        Authorization authorization = templateConfig.getAuthorization();
        Assert.assertThat(authorization.getAdminsConfig().size(), Matchers.is(3));
        templateConfig.setConfigAttributes(DataStructureUtils.m());
        authorization = templateConfig.getAuthorization();
        Assert.assertThat(authorization.getAdminsConfig().size(), Matchers.is(0));
    }

    @Test
    public void shouldIgnoreBlankUserWhileSettingAttributes() {
        PipelineTemplateConfig templateConfig = PipelineTemplateConfigMother.createTemplate("template-1");
        templateConfig.setConfigAttributes(DataStructureUtils.m(AUTHORIZATION, DataStructureUtils.a(DataStructureUtils.m(Authorization.NAME, "", TYPE, USER.toString(), PRIVILEGES, DataStructureUtils.a(DataStructureUtils.m(ADMIN.toString(), ON.toString()))), DataStructureUtils.m(Authorization.NAME, null, TYPE, USER.toString(), PRIVILEGES, DataStructureUtils.a(DataStructureUtils.m(ADMIN.toString(), ON.toString()))), DataStructureUtils.m(Authorization.NAME, "geezer", TYPE, USER.toString(), PRIVILEGES, DataStructureUtils.a(DataStructureUtils.m(ADMIN.toString(), ON.toString()))))));
        Authorization authorization = templateConfig.getAuthorization();
        Assert.assertThat(authorization.getAdminsConfig().size(), Matchers.is(1));
        Assert.assertThat(authorization.getAdminsConfig(), Matchers.hasItem(new AdminUser(new CaseInsensitiveString("geezer"))));
    }

    @Test
    public void validate_shouldEnsureThatTemplateFollowsTheNameType() {
        BasicCruiseConfig cruiseConfig = GoConfigMother.defaultCruiseConfig();
        PipelineTemplateConfig config = new PipelineTemplateConfig(new CaseInsensitiveString(".Abc"));
        config.validate(ConfigSaveValidationContext.forChain(cruiseConfig));
        Assert.assertThat(config.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(config.errors().on(NAME), Matchers.is("Invalid template name '.Abc'. This must be alphanumeric and can contain underscores and periods (however, it cannot start with a period). The maximum allowed length is 255 characters."));
    }

    @Test
    public void shouldErrorOutWhenTryingToAddTwoStagesWithSameName() {
        BasicCruiseConfig cruiseConfig = GoConfigMother.defaultCruiseConfig();
        PipelineTemplateConfig pipelineTemplateConfig = new PipelineTemplateConfig(new CaseInsensitiveString("template"), StageConfigMother.manualStage("stage1"), StageConfigMother.manualStage("stage1"));
        pipelineTemplateConfig.validate(ConfigSaveValidationContext.forChain(cruiseConfig));
        Assert.assertThat(pipelineTemplateConfig.get(0).errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(pipelineTemplateConfig.get(1).errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(pipelineTemplateConfig.get(0).errors().on(StageConfig.NAME), Matchers.is("You have defined multiple stages called 'stage1'. Stage names are case-insensitive and must be unique."));
        Assert.assertThat(pipelineTemplateConfig.get(1).errors().on(StageConfig.NAME), Matchers.is("You have defined multiple stages called 'stage1'. Stage names are case-insensitive and must be unique."));
    }

    @Test
    public void shouldValidateRoleNamesInTemplateAdminAuthorization() {
        BasicCruiseConfig cruiseConfig = GoConfigMother.defaultCruiseConfig();
        ServerConfig serverConfig = new ServerConfig(new SecurityConfig(new AdminsConfig(new AdminUser(new CaseInsensitiveString("admin")))), null);
        cruiseConfig.setServerConfig(serverConfig);
        GoConfigMother.enableSecurityWithPasswordFilePlugin(cruiseConfig);
        RoleConfig roleConfig = new RoleConfig(new CaseInsensitiveString("non-existent-role"), new RoleUser("non-existent-user"));
        PipelineTemplateConfig template = new PipelineTemplateConfig(new CaseInsensitiveString("template"), new Authorization(new AdminsConfig(new AdminRole(roleConfig))), StageConfigMother.manualStage("stage2"), StageConfigMother.manualStage("stage"));
        template.validate(ConfigSaveValidationContext.forChain(cruiseConfig));
        Assert.assertThat(template.getAllErrors().get(0).getAllOn("name"), Matchers.is(Arrays.asList("Role \"non-existent-role\" does not exist.")));
    }

    @Test
    public void shouldValidateRoleNamesInTemplateViewAuthorization() {
        BasicCruiseConfig cruiseConfig = GoConfigMother.defaultCruiseConfig();
        ServerConfig serverConfig = new ServerConfig(new SecurityConfig(new AdminsConfig(new AdminUser(new CaseInsensitiveString("admin")))), null);
        cruiseConfig.setServerConfig(serverConfig);
        GoConfigMother.enableSecurityWithPasswordFilePlugin(cruiseConfig);
        RoleConfig roleConfig = new RoleConfig(new CaseInsensitiveString("non-existent-role"), new RoleUser("non-existent-user"));
        PipelineTemplateConfig template = new PipelineTemplateConfig(new CaseInsensitiveString("template"), new Authorization(new ViewConfig(new AdminRole(roleConfig))), StageConfigMother.manualStage("stage2"), StageConfigMother.manualStage("stage"));
        template.validate(ConfigSaveValidationContext.forChain(cruiseConfig));
        Assert.assertThat(template.getAllErrors().get(0).getAllOn("name"), Matchers.is(Arrays.asList("Role \"non-existent-role\" does not exist.")));
    }

    @Test
    public void shouldUnderstandUsedParams() {
        PipelineTemplateConfig template = PipelineTemplateConfigMother.createTemplateWithParams("template-name", "foo", "bar", "baz");
        ParamsConfig params = template.referredParams();
        Assert.assertThat(params.size(), Matchers.is(3));
        Assert.assertThat(params, Matchers.hasItem(new ParamConfig("foo", null)));
        Assert.assertThat(params, Matchers.hasItem(new ParamConfig("bar", null)));
        Assert.assertThat(params, Matchers.hasItem(new ParamConfig("baz", null)));
        params = template.referredParams();// should not mutate

        Assert.assertThat(params.size(), Matchers.is(3));
    }

    @Test
    public void shouldValidateWhetherTheReferredParamsAreDefinedInPipelinesUsingTheTemplate() {
        PipelineTemplateConfig templateWithParams = PipelineTemplateConfigMother.createTemplateWithParams("template", "param1", "param2");
        PipelineConfig pipelineConfig = PipelineConfigMother.pipelineConfigWithTemplate("pipeline", "template");
        BasicCruiseConfig cruiseConfig = GoConfigMother.defaultCruiseConfig();
        cruiseConfig.addTemplate(templateWithParams);
        cruiseConfig.addPipelineWithoutValidation("group", pipelineConfig);
        templateWithParams.validateTree(ConfigSaveValidationContext.forChain(cruiseConfig), cruiseConfig, false);
        Assert.assertThat(templateWithParams.errors().getAllOn("params"), Matchers.is(Arrays.asList("The param 'param1' is not defined in pipeline 'pipeline'", "The param 'param2' is not defined in pipeline 'pipeline'")));
    }

    @Test
    public void shouldValidateFetchTasksOfATemplateInTheContextOfPipelinesUsingTheTemplate() throws Exception {
        JobConfig jobConfig = new JobConfig(new CaseInsensitiveString("defaultJob"));
        jobConfig.addTask(new FetchTask(new CaseInsensitiveString("non-existent-pipeline"), new CaseInsensitiveString("stage"), new CaseInsensitiveString("job"), "src", "dest"));
        JobConfigs jobConfigs = new JobConfigs(jobConfig);
        StageConfig stageConfig = StageConfigMother.custom("stage", jobConfigs);
        PipelineTemplateConfig template = PipelineTemplateConfigMother.createTemplate("template", stageConfig);
        PipelineConfig pipelineConfig = PipelineConfigMother.pipelineConfigWithTemplate("pipeline", "template");
        pipelineConfig.usingTemplate(template);
        BasicCruiseConfig cruiseConfig = GoConfigMother.defaultCruiseConfig();
        cruiseConfig.addTemplate(template);
        cruiseConfig.addPipelineWithoutValidation("group", pipelineConfig);
        template.validateTree(ConfigSaveValidationContext.forChain(cruiseConfig), cruiseConfig, false);
        Assert.assertThat(template.errors().getAllOn("pipeline"), Matchers.is(Arrays.asList("\"pipeline :: stage :: defaultJob\" tries to fetch artifact from pipeline \"non-existent-pipeline\" which does not exist.")));
    }

    @Test
    public void shouldValidateFetchPluggableTasksOfATemplateInTheContextOfPipelinesUsingTheTemplate() throws Exception {
        JobConfig jobConfig = new JobConfig(new CaseInsensitiveString("defaultJob"));
        jobConfig.addTask(new FetchPluggableArtifactTask(new CaseInsensitiveString("non-existent-pipeline"), new CaseInsensitiveString("stage"), new CaseInsensitiveString("job"), "artifactId"));
        JobConfigs jobConfigs = new JobConfigs(jobConfig);
        StageConfig stageConfig = StageConfigMother.custom("stage", jobConfigs);
        PipelineTemplateConfig template = PipelineTemplateConfigMother.createTemplate("template", stageConfig);
        PipelineConfig pipelineConfig = PipelineConfigMother.pipelineConfigWithTemplate("pipeline", "template");
        pipelineConfig.usingTemplate(template);
        BasicCruiseConfig cruiseConfig = GoConfigMother.defaultCruiseConfig();
        cruiseConfig.addTemplate(template);
        cruiseConfig.addPipelineWithoutValidation("group", pipelineConfig);
        template.validateTree(ConfigSaveValidationContext.forChain(cruiseConfig), cruiseConfig, false);
        Assert.assertThat(template.errors().getAllOn("pipeline"), Matchers.is(Arrays.asList("\"pipeline :: stage :: defaultJob\" tries to fetch artifact from pipeline \"non-existent-pipeline\" which does not exist.")));
    }

    @Test
    public void shouldValidatePublishExternalArtifactOfATemplateInTheContextOfPipelinesUsingTheTemplate() throws Exception {
        JobConfig jobConfig = new JobConfig(new CaseInsensitiveString("defaultJob"));
        jobConfig.artifactConfigs().add(new PluggableArtifactConfig("some-id", "non-existent-store-id"));
        JobConfigs jobConfigs = new JobConfigs(jobConfig);
        StageConfig stageConfig = StageConfigMother.custom("stage", jobConfigs);
        PipelineTemplateConfig template = PipelineTemplateConfigMother.createTemplate("template", stageConfig);
        PipelineConfig pipelineConfig = PipelineConfigMother.pipelineConfigWithTemplate("pipeline", "template");
        pipelineConfig.usingTemplate(template);
        BasicCruiseConfig cruiseConfig = GoConfigMother.defaultCruiseConfig();
        cruiseConfig.addTemplate(template);
        cruiseConfig.addPipelineWithoutValidation("group", pipelineConfig);
        template.validateTree(ConfigSaveValidationContext.forChain(cruiseConfig), cruiseConfig, false);
        Assert.assertThat(template.errors().getAllOn("storeId"), Matchers.is(Arrays.asList("Artifact store with id `non-existent-store-id` does not exist. Please correct the `storeId` attribute on pipeline `pipeline`.")));
    }

    @Test
    public void shouldValidateStageApprovalAuthorizationOfATemplateInTheContextOfPipelinesUsingTheTemplate() throws Exception {
        JobConfig jobConfig = new JobConfig(new CaseInsensitiveString("defaultJob"));
        JobConfigs jobConfigs = new JobConfigs(jobConfig);
        StageConfig stageConfig = StageConfigMother.custom("stage", jobConfigs);
        stageConfig.setApproval(new Approval(new AuthConfig(new AdminRole(new CaseInsensitiveString("non-existent-role")))));
        PipelineTemplateConfig template = PipelineTemplateConfigMother.createTemplate("template", stageConfig);
        PipelineConfig pipelineConfig = PipelineConfigMother.pipelineConfigWithTemplate("pipeline", "template");
        pipelineConfig.usingTemplate(template);
        BasicCruiseConfig cruiseConfig = GoConfigMother.defaultCruiseConfig();
        cruiseConfig.addTemplate(template);
        cruiseConfig.addPipelineWithoutValidation("group", pipelineConfig);
        template.validateTree(ConfigSaveValidationContext.forChain(cruiseConfig), cruiseConfig, false);
        Assert.assertThat(template.errors().getAllOn("name"), Matchers.is(Arrays.asList("Role \"non-existent-role\" does not exist.")));
    }

    @Test
    public void shouldValidateStagePermissionsOfATemplateStageInTheContextOfPipelineUsingTheTemplate() {
        StageConfig stageConfig = StageConfigMother.custom("stage", new JobConfigs(new JobConfig(new CaseInsensitiveString("defaultJob"))));
        stageConfig.setApproval(new Approval(new AuthConfig(new AdminUser(new CaseInsensitiveString("non-admin-non-operate")))));
        PipelineTemplateConfig template = PipelineTemplateConfigMother.createTemplate("template", stageConfig);
        PipelineConfig pipelineConfig = PipelineConfigMother.pipelineConfigWithTemplate("pipeline", "template");
        pipelineConfig.usingTemplate(template);
        BasicCruiseConfig cruiseConfig = GoConfigMother.defaultCruiseConfig();
        cruiseConfig.addTemplate(template);
        cruiseConfig.addPipelineWithoutValidation("group", pipelineConfig);
        PipelineConfigs group = cruiseConfig.findGroup("group");
        group.setAuthorization(new Authorization(new ViewConfig(), new OperationConfig(new AdminUser(new CaseInsensitiveString("foo"))), new AdminsConfig()));
        cruiseConfig.server().security().securityAuthConfigs().add(new SecurityAuthConfig());
        cruiseConfig.server().security().adminsConfig().add(new AdminUser(new CaseInsensitiveString("super-admin")));
        template.validateTree(ConfigSaveValidationContext.forChain(cruiseConfig), cruiseConfig, false);
        Assert.assertThat(template.errors().getAllOn("name"), Matchers.is(Arrays.asList("User \"non-admin-non-operate\" who is not authorized to operate pipeline group `group` can not be authorized to approve stage")));
    }

    @Test
    public void shouldValidateTemplateStageUsedInDownstreamPipelines() {
        JobConfig jobConfigWithExecTask = new JobConfig(new CaseInsensitiveString("defaultJob"));
        jobConfigWithExecTask.addTask(new ExecTask("ls", "l", "server/config"));
        JobConfigs jobConfigs = new JobConfigs(jobConfigWithExecTask);
        StageConfig stageConfig = StageConfigMother.custom("stage", jobConfigs);
        PipelineTemplateConfig template = PipelineTemplateConfigMother.createTemplate("template", stageConfig);
        PipelineConfig upstreamPipelineUsingTemplate = PipelineConfigMother.pipelineConfigWithTemplate("pipeline", "template");
        upstreamPipelineUsingTemplate.usingTemplate(template);
        // Pipeline and stage of upstreamPipelineUsingTemplate
        MaterialConfig dependency = new com.thoughtworks.go.config.materials.dependency.DependencyMaterialConfig(new CaseInsensitiveString("pipeline"), new CaseInsensitiveString("non-existent-stage"));
        PipelineConfig downStreamPipeline = PipelineConfigMother.pipelineConfig("downstreamPipeline", new com.thoughtworks.go.config.materials.MaterialConfigs(dependency));
        BasicCruiseConfig cruiseConfig = GoConfigMother.defaultCruiseConfig();
        cruiseConfig.addTemplate(template);
        cruiseConfig.addPipelineWithoutValidation("group", upstreamPipelineUsingTemplate);
        cruiseConfig.addPipelineWithoutValidation("group", downStreamPipeline);
        template.validateTree(ConfigSaveValidationContext.forChain(cruiseConfig), cruiseConfig, false);
        Assert.assertThat(template.errors().getAllOn("base"), Matchers.is(Arrays.asList("Stage with name 'non-existent-stage' does not exist on pipeline 'pipeline', it is being referred to from pipeline 'downstreamPipeline' (cruise-config.xml)")));
    }

    @Test
    public void shouldValidateTemplateJobsUsedInDownstreamPipelines() {
        JobConfig jobConfigWithExecTask = new JobConfig(new CaseInsensitiveString("defaultJob"));
        jobConfigWithExecTask.addTask(new ExecTask("ls", "l", "server/config"));
        JobConfigs jobConfigs = new JobConfigs(jobConfigWithExecTask);
        JobConfig jobConfigWithFetchTask = new JobConfig(new CaseInsensitiveString("fetchJob"));
        jobConfigWithFetchTask.addTask(new FetchTask(new CaseInsensitiveString("pipeline"), new CaseInsensitiveString("stage"), new CaseInsensitiveString("non-existent-job"), "src", "dest"));
        JobConfigs jobConfigsForDownstream = new JobConfigs(jobConfigWithFetchTask);
        StageConfig stageConfig = StageConfigMother.custom("stage", jobConfigs);
        PipelineTemplateConfig template = PipelineTemplateConfigMother.createTemplate("template", stageConfig);
        PipelineConfig upstreamPipelineUsingTemplate = PipelineConfigMother.pipelineConfigWithTemplate("pipeline", "template");
        upstreamPipelineUsingTemplate.usingTemplate(template);
        // Pipeline and stage of upstreamPipelineUsingTemplate
        MaterialConfig dependency = new com.thoughtworks.go.config.materials.dependency.DependencyMaterialConfig(new CaseInsensitiveString("pipeline"), new CaseInsensitiveString("stage"));
        PipelineConfig downStreamPipeline = PipelineConfigMother.pipelineConfig("downstreamPipeline", new com.thoughtworks.go.config.materials.MaterialConfigs(dependency), jobConfigsForDownstream);
        BasicCruiseConfig cruiseConfig = GoConfigMother.defaultCruiseConfig();
        cruiseConfig.addTemplate(template);
        cruiseConfig.addPipelineWithoutValidation("group", upstreamPipelineUsingTemplate);
        cruiseConfig.addPipelineWithoutValidation("group", downStreamPipeline);
        template.validateTree(ConfigSaveValidationContext.forChain(cruiseConfig), cruiseConfig, false);
        Assert.assertThat(template.errors().getAllOn("base"), Matchers.is(Arrays.asList("\"downstreamPipeline :: mingle :: fetchJob\" tries to fetch artifact from job \"pipeline :: stage :: non-existent-job\" which does not exist.")));
    }

    @Test
    public void shouldAllowEditingOfStageNameWhenItIsNotUsedAsDependencyMaterial() throws Exception {
        PipelineTemplateConfig template = new PipelineTemplateConfig(new CaseInsensitiveString("template"), StageConfigMother.oneBuildPlanWithResourcesAndMaterials("stage2"));
        BasicCruiseConfig cruiseConfig = GoConfigMother.defaultCruiseConfig();
        cruiseConfig.addTemplate(template);
        template.getStages().get(0).setName(new CaseInsensitiveString("updatedStageName"));
        template.validateTree(ConfigSaveValidationContext.forChain(cruiseConfig), cruiseConfig, false);
        Assert.assertThat(template.errors().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldAllowEditingOfJobNameWhenItIsNotUsedAsFetchArtifact() throws Exception {
        PipelineTemplateConfig template = new PipelineTemplateConfig(new CaseInsensitiveString("template"), StageConfigMother.oneBuildPlanWithResourcesAndMaterials("stage", "job2"));
        BasicCruiseConfig cruiseConfig = GoConfigMother.defaultCruiseConfig();
        cruiseConfig.addTemplate(template);
        template.getStages().get(0).getJobs().get(0).setName(new CaseInsensitiveString("updatedJobName"));
        template.validateTree(ConfigSaveValidationContext.forChain(cruiseConfig), cruiseConfig, false);
        Assert.assertThat(template.errors().isEmpty(), Matchers.is(true));
    }

    @Test
    public void copyStagesShouldNotThrowExceptionIfInputPipelineConfigIsNull() {
        PipelineTemplateConfig template = PipelineTemplateConfigMother.createTemplateWithParams("template-name", "foo", "bar", "baz");
        int sizeBeforeCopy = template.size();
        template.copyStages(null);
        Assert.assertThat(template.size(), Matchers.is(sizeBeforeCopy));
    }
}

