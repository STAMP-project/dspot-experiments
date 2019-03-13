/**
 * Copyright 2016 ThoughtWorks, Inc.
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


import PipelineConfigs.DEFAULT_GROUP;
import com.thoughtworks.go.config.elastic.ElasticConfig;
import com.thoughtworks.go.config.elastic.ElasticProfile;
import com.thoughtworks.go.config.materials.MaterialConfigs;
import com.thoughtworks.go.domain.materials.MaterialConfig;
import com.thoughtworks.go.domain.packagerepository.PackageDefinitionMother;
import com.thoughtworks.go.domain.packagerepository.PackageRepositoryMother;
import com.thoughtworks.go.domain.scm.SCMMother;
import com.thoughtworks.go.helper.GoConfigMother;
import com.thoughtworks.go.helper.MaterialConfigsMother;
import junit.framework.TestCase;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class PipelineConfigSaveValidationContextTest {
    private PipelineConfig pipelineConfig;

    private PipelineConfigSaveValidationContext pipelineContext;

    @Test
    public void shouldCreatePipelineValidationContext() {
        Assert.assertThat(pipelineContext.getPipeline(), Matchers.is(pipelineConfig));
        Assert.assertThat(pipelineContext.getStage(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(pipelineContext.getJob(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldCreateStageValidationContextBasedOnParent() {
        StageConfig stageConfig = Mockito.mock(StageConfig.class);
        PipelineConfigSaveValidationContext stageContext = PipelineConfigSaveValidationContext.forChain(true, "group", pipelineConfig, stageConfig);
        Assert.assertThat(stageContext.getPipeline(), Matchers.is(pipelineConfig));
        Assert.assertThat(stageContext.getStage(), Matchers.is(stageConfig));
        Assert.assertThat(stageContext.getJob(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldCreateJobValidationContextBasedOnParent() {
        StageConfig stageConfig = Mockito.mock(StageConfig.class);
        JobConfig jobConfig = Mockito.mock(JobConfig.class);
        PipelineConfigSaveValidationContext jobContext = PipelineConfigSaveValidationContext.forChain(true, "group", pipelineConfig, stageConfig, jobConfig);
        Assert.assertThat(jobContext.getPipeline(), Matchers.is(pipelineConfig));
        Assert.assertThat(jobContext.getStage(), Matchers.is(stageConfig));
        Assert.assertThat(jobContext.getJob(), Matchers.is(jobConfig));
    }

    @Test
    public void shouldGetAllMaterialsByFingerPrint() throws Exception {
        CruiseConfig cruiseConfig = new GoConfigMother().cruiseConfigWithPipelineUsingTwoMaterials();
        MaterialConfig expectedMaterial = MaterialConfigsMother.multipleMaterialConfigs().get(1);
        PipelineConfigSaveValidationContext context = PipelineConfigSaveValidationContext.forChain(true, "group", cruiseConfig);
        MaterialConfigs allMaterialsByFingerPrint = context.getAllMaterialsByFingerPrint(expectedMaterial.getFingerprint());
        Assert.assertThat(allMaterialsByFingerPrint.size(), Matchers.is(1));
        Assert.assertThat(allMaterialsByFingerPrint.first(), Matchers.is(expectedMaterial));
    }

    @Test
    public void shouldReturnNullIfMatchingMaterialConfigIsNotFound() throws Exception {
        CruiseConfig cruiseConfig = new GoConfigMother().cruiseConfigWithPipelineUsingTwoMaterials();
        PipelineConfigSaveValidationContext context = PipelineConfigSaveValidationContext.forChain(true, "group", cruiseConfig);
        Assert.assertThat(context.getAllMaterialsByFingerPrint("does_not_exist"), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldGetDependencyMaterialsForPipelines() {
        BasicCruiseConfig cruiseConfig = GoConfigMother.configWithPipelines("p1", "p2", "p3");
        PipelineConfig p2 = cruiseConfig.getPipelineConfigByName(new CaseInsensitiveString("p2"));
        p2.addMaterialConfig(new com.thoughtworks.go.config.materials.dependency.DependencyMaterialConfig(new CaseInsensitiveString("p1"), new CaseInsensitiveString("stage")));
        PipelineConfig p3 = cruiseConfig.getPipelineConfigByName(new CaseInsensitiveString("p3"));
        p3.addMaterialConfig(new com.thoughtworks.go.config.materials.dependency.DependencyMaterialConfig(new CaseInsensitiveString("p2"), new CaseInsensitiveString("stage")));
        PipelineConfigSaveValidationContext context = PipelineConfigSaveValidationContext.forChain(true, "group", cruiseConfig);
        Assert.assertThat(context.getDependencyMaterialsFor(new CaseInsensitiveString("p1")).getDependencies().isEmpty(), Matchers.is(true));
        Assert.assertThat(context.getDependencyMaterialsFor(new CaseInsensitiveString("p2")).getDependencies(), Matchers.contains(new com.thoughtworks.go.util.Node.DependencyNode(new CaseInsensitiveString("p1"), new CaseInsensitiveString("stage"))));
        Assert.assertThat(context.getDependencyMaterialsFor(new CaseInsensitiveString("p3")).getDependencies(), Matchers.contains(new com.thoughtworks.go.util.Node.DependencyNode(new CaseInsensitiveString("p2"), new CaseInsensitiveString("stage"))));
        Assert.assertThat(context.getDependencyMaterialsFor(new CaseInsensitiveString("junk")).getDependencies().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldGetParentDisplayName() {
        Assert.assertThat(PipelineConfigSaveValidationContext.forChain(true, "group", new PipelineConfig()).getParentDisplayName(), Matchers.is("pipeline"));
        Assert.assertThat(PipelineConfigSaveValidationContext.forChain(true, "group", new PipelineConfig(), new StageConfig()).getParentDisplayName(), Matchers.is("stage"));
        Assert.assertThat(PipelineConfigSaveValidationContext.forChain(true, "group", new PipelineConfig(), new StageConfig(), new JobConfig()).getParentDisplayName(), Matchers.is("job"));
    }

    @Test
    public void shouldFindPipelineByName() {
        BasicCruiseConfig cruiseConfig = GoConfigMother.configWithPipelines("p1");
        PipelineConfigSaveValidationContext context = PipelineConfigSaveValidationContext.forChain(true, "group", cruiseConfig, new PipelineConfig(new CaseInsensitiveString("p2"), new MaterialConfigs()));
        Assert.assertThat(context.getPipelineConfigByName(new CaseInsensitiveString("p1")), Matchers.is(cruiseConfig.allPipelines().get(0)));
    }

    @Test
    public void shouldReturnNullWhenNoMatchingPipelineIsFound() throws Exception {
        BasicCruiseConfig cruiseConfig = GoConfigMother.configWithPipelines("p1");
        PipelineConfigSaveValidationContext context = PipelineConfigSaveValidationContext.forChain(true, "group", cruiseConfig, new PipelineConfig(new CaseInsensitiveString("p2"), new MaterialConfigs()));
        Assert.assertThat(context.getPipelineConfigByName(new CaseInsensitiveString("does_not_exist")), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldGetPipelineGroupForPipelineInContext() {
        String pipelineName = "p1";
        BasicCruiseConfig cruiseConfig = GoConfigMother.configWithPipelines(pipelineName);
        PipelineConfig p1 = cruiseConfig.pipelineConfigByName(new CaseInsensitiveString(pipelineName));
        PipelineConfigSaveValidationContext context = PipelineConfigSaveValidationContext.forChain(true, DEFAULT_GROUP, cruiseConfig, p1);
        Assert.assertThat(context.getPipelineGroup(), Matchers.is(cruiseConfig.findGroup(DEFAULT_GROUP)));
    }

    @Test
    public void shouldGetServerSecurityContext() {
        BasicCruiseConfig cruiseConfig = new BasicCruiseConfig();
        SecurityConfig securityConfig = new SecurityConfig();
        securityConfig.addRole(new RoleConfig(new CaseInsensitiveString("admin")));
        securityConfig.adminsConfig().add(new AdminUser(new CaseInsensitiveString("super-admin")));
        cruiseConfig.server().useSecurity(securityConfig);
        PipelineConfigSaveValidationContext context = PipelineConfigSaveValidationContext.forChain(true, "group", cruiseConfig);
        Assert.assertThat(context.getServerSecurityConfig(), Matchers.is(securityConfig));
    }

    @Test
    public void shouldReturnIfTheContextBelongsToPipeline() {
        ValidationContext context = PipelineConfigSaveValidationContext.forChain(true, "group", new PipelineConfig());
        Assert.assertThat(context.isWithinPipelines(), Matchers.is(true));
        Assert.assertThat(context.isWithinTemplates(), Matchers.is(false));
    }

    @Test
    public void shouldCheckForExistenceOfTemplate() {
        BasicCruiseConfig cruiseConfig = new BasicCruiseConfig();
        cruiseConfig.addTemplate(new PipelineTemplateConfig(new CaseInsensitiveString("t1")));
        PipelineConfigSaveValidationContext context = PipelineConfigSaveValidationContext.forChain(true, "group", cruiseConfig, new PipelineConfig());
        Assert.assertThat(context.doesTemplateExist(new CaseInsensitiveString("t1")), Matchers.is(true));
        Assert.assertThat(context.doesTemplateExist(new CaseInsensitiveString("t2")), Matchers.is(false));
    }

    @Test
    public void shouldCheckForExistenceOfSCM() throws Exception {
        BasicCruiseConfig cruiseConfig = new BasicCruiseConfig();
        cruiseConfig.setSCMs(new com.thoughtworks.go.domain.scm.SCMs(SCMMother.create("scm-id")));
        ValidationContext context = ConfigSaveValidationContext.forChain(cruiseConfig);
        MatcherAssert.assertThat(context.findScmById("scm-id").getId(), Matchers.is("scm-id"));
    }

    @Test
    public void shouldCheckForExistenceOfPackage() throws Exception {
        BasicCruiseConfig cruiseConfig = new BasicCruiseConfig();
        cruiseConfig.setPackageRepositories(new PackageRepositories(PackageRepositoryMother.create("repo-id")));
        cruiseConfig.getPackageRepositories().find("repo-id").setPackages(new Packages(PackageDefinitionMother.create("package-id")));
        ValidationContext context = ConfigSaveValidationContext.forChain(cruiseConfig);
        MatcherAssert.assertThat(context.findPackageById("package-id").getId(), Matchers.is("repo-id"));
    }

    @Test
    public void isValidProfileIdShouldBeValidInPresenceOfElasticProfile() {
        BasicCruiseConfig cruiseConfig = new BasicCruiseConfig();
        ElasticConfig elasticConfig = new ElasticConfig();
        elasticConfig.setProfiles(new com.thoughtworks.go.config.elastic.ElasticProfiles(new ElasticProfile("docker.unit-test", "docker")));
        cruiseConfig.setElasticConfig(elasticConfig);
        ValidationContext context = PipelineConfigSaveValidationContext.forChain(true, "group", cruiseConfig, new PipelineConfig());
        TestCase.assertTrue(context.isValidProfileId("docker.unit-test"));
    }

    @Test
    public void isValidProfileIdShouldBeInValidInAbsenceOfElasticProfileForTheGivenId() {
        BasicCruiseConfig cruiseConfig = new BasicCruiseConfig();
        ElasticConfig elasticConfig = new ElasticConfig();
        elasticConfig.setProfiles(new com.thoughtworks.go.config.elastic.ElasticProfiles(new ElasticProfile("docker.unit-test", "docker")));
        cruiseConfig.setElasticConfig(elasticConfig);
        ValidationContext context = PipelineConfigSaveValidationContext.forChain(true, "group", cruiseConfig, new PipelineConfig());
        Assert.assertFalse(context.isValidProfileId("invalid.profile-id"));
    }

    @Test
    public void isValidProfileIdShouldBeInValidInAbsenceOfElasticProfiles() {
        BasicCruiseConfig cruiseConfig = new BasicCruiseConfig();
        ValidationContext context = PipelineConfigSaveValidationContext.forChain(true, "group", cruiseConfig, new PipelineConfig());
        Assert.assertFalse(context.isValidProfileId("docker.unit-test"));
    }
}

