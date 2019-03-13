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
package com.thoughtworks.go.config.preprocessor;


import ClassAttributeCache.FieldCache;
import FetchTask.SRC;
import JobConfig.RESOURCES;
import P4MaterialConfig.VIEW;
import PipelineConfig.LOCK_BEHAVIOR;
import ScmMaterialConfig.FOLDER;
import com.thoughtworks.go.config.Approval;
import com.thoughtworks.go.config.ArtifactPropertyConfig;
import com.thoughtworks.go.config.JobConfig;
import com.thoughtworks.go.config.materials.AbstractMaterialConfig;
import com.thoughtworks.go.config.materials.mercurial.HgMaterialConfig;
import com.thoughtworks.go.config.materials.perforce.P4MaterialConfig;
import com.thoughtworks.go.config.materials.perforce.P4MaterialViewConfig;
import com.thoughtworks.go.config.materials.svn.SvnMaterialConfig;
import com.thoughtworks.go.config.merge.MergePipelineConfigs;
import com.thoughtworks.go.helper.MaterialConfigsMother;
import com.thoughtworks.go.helper.PipelineConfigMother;
import com.thoughtworks.go.util.ReflectionUtil;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;


public class ParamResolverTest {
    private FieldCache fieldCache;

    @Test
    public void shouldResolve_ConfigValue_MappedAsObject() {
        SecurityConfig securityConfig = new SecurityConfig();
        securityConfig.adminsConfig().add(new AdminUser(new CaseInsensitiveString("lo#{foo}")));
        securityConfig.addRole(new RoleConfig(new CaseInsensitiveString("boo#{bar}"), new RoleUser(new CaseInsensitiveString("choo#{foo}"))));
        new ParamResolver(new ParamSubstitutionHandlerFactory(params(param("foo", "ser"), param("bar", "zer"))), fieldCache).resolve(securityConfig);
        Assert.assertThat(CaseInsensitiveString.str(securityConfig.adminsConfig().get(0).getName()), is("loser"));
        Assert.assertThat(CaseInsensitiveString.str(securityConfig.getRoles().get(0).getName()), is("boozer"));
        Assert.assertThat(CaseInsensitiveString.str(securityConfig.getRoles().get(0).getUsers().get(0).getName()), is("chooser"));
    }

    @Test
    public void shouldResolveTopLevelAttribute() {
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("cruise", "dev", "ant");
        pipelineConfig.setLabelTemplate("2.1-${COUNT}");
        ReflectionUtil.setField(pipelineConfig, LOCK_BEHAVIOR, "#{partial}Finished");
        new ParamResolver(new ParamSubstitutionHandlerFactory(params(param("foo", "pavan"), param("partial", "unlockWhen"), param("COUNT", "quux"))), fieldCache).resolve(pipelineConfig);
        Assert.assertThat(pipelineConfig.getLabelTemplate(), is("2.1-${COUNT}"));
        Assert.assertThat(pipelineConfig.explicitLock(), is(true));
    }

    @Test
    public void shouldNotTryToResolveNonStringAttributes() {
        // this tests replacement doesn't fail when non-string config-attributes are present, and non opt-out annotated
        MailHost mailHost = new MailHost("host", 25, "loser", "passwd", true, false, "boozer@loser.com", "root@loser.com");
        new ParamResolver(new ParamSubstitutionHandlerFactory(params(param("foo", "pavan"), param("bool", "tr"))), fieldCache).resolve(mailHost);
    }

    @Test
    public void shouldNotResolveOptedOutConfigAttributes() throws NoSuchFieldException {
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("cruise-#{foo}-#{bar}", "dev", "ant");
        SvnMaterialConfig svn = ((SvnMaterialConfig) (pipelineConfig.materialConfigs().get(0)));
        svn.setPassword("#quux-#{foo}-#{bar}");
        pipelineConfig.setLabelTemplate("2.1-${COUNT}-#{foo}-bar-#{bar}");
        new ParamResolver(new ParamSubstitutionHandlerFactory(params(param("foo", "pavan"), param("bar", "jj"))), fieldCache).resolve(pipelineConfig);
        Assert.assertThat(pipelineConfig.getLabelTemplate(), is("2.1-${COUNT}-pavan-bar-jj"));
        Assert.assertThat(pipelineConfig.name(), is(new CaseInsensitiveString("cruise-#{foo}-#{bar}")));
        Assert.assertThat(getPassword(), is("#quux-#{foo}-#{bar}"));
        Assert.assertThat(pipelineConfig.getClass().getDeclaredField("name").getAnnotation(SkipParameterResolution.class), isA(SkipParameterResolution.class));
    }

    @Test
    public void shouldNotResolveOptedOutConfigSubtags() throws NoSuchFieldException {
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("cruise", "dev", "ant");
        pipelineConfig.setLabelTemplate("2.1-${COUNT}-#{foo}-bar-#{bar}");
        pipelineConfig.addParam(param("#{foo}-name", "#{foo}-#{bar}-baz"));
        new ParamResolver(new ParamSubstitutionHandlerFactory(params(param("foo", "pavan"), param("bar", "jj"))), fieldCache).resolve(pipelineConfig);
        Assert.assertThat(pipelineConfig.getLabelTemplate(), is("2.1-${COUNT}-pavan-bar-jj"));
        Assert.assertThat(pipelineConfig.getParams().get(0), is(param("#{foo}-name", "#{foo}-#{bar}-baz")));
        Assert.assertThat(pipelineConfig.getClass().getDeclaredField("params").getAnnotation(SkipParameterResolution.class), isA(SkipParameterResolution.class));
    }

    @Test
    public void shouldNotInterpolateEscapedSequences() {
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("cruise", "dev", "ant");
        pipelineConfig.setLabelTemplate("2.1-${COUNT}-##{foo}-bar-#{bar}");
        new ParamResolver(new ParamSubstitutionHandlerFactory(params(param("foo", "pavan"), param("bar", "jj"))), fieldCache).resolve(pipelineConfig);
        Assert.assertThat(pipelineConfig.getLabelTemplate(), is("2.1-${COUNT}-#{foo}-bar-jj"));
    }

    @Test
    public void shouldInterpolateLiteralEscapedSequences() {
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("cruise", "dev", "ant");
        pipelineConfig.setLabelTemplate("2.1-${COUNT}-###{foo}-bar-#{bar}");
        new ParamResolver(new ParamSubstitutionHandlerFactory(params(param("foo", "pavan"), param("bar", "jj"))), fieldCache).resolve(pipelineConfig);
        Assert.assertThat(pipelineConfig.getLabelTemplate(), is("2.1-${COUNT}-#pavan-bar-jj"));
    }

    @Test
    public void shouldEscapeEscapedPatternStartSequences() {
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("cruise", "dev", "ant");
        pipelineConfig.setLabelTemplate("2.1-${COUNT}-#######{foo}-bar-####{bar}");
        new ParamResolver(new ParamSubstitutionHandlerFactory(params(param("foo", "pavan"), param("bar", "jj"))), fieldCache).resolve(pipelineConfig);
        Assert.assertThat(pipelineConfig.getLabelTemplate(), is("2.1-${COUNT}-###pavan-bar-##{bar}"));
    }

    @Test
    public void shouldNotRecursivelySubstituteParams() {
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("cruise", "dev", "ant");
        pipelineConfig.setLabelTemplate("#{foo}");
        new ParamResolver(new ParamSubstitutionHandlerFactory(params(param("foo", "#{bar}"), param("bar", "baz"))), fieldCache).resolve(pipelineConfig);
        Assert.assertThat(pipelineConfig.getLabelTemplate(), is("#{bar}"));
        pipelineConfig.setLabelTemplate("#{foo}");
        new ParamResolver(new ParamSubstitutionHandlerFactory(params(param("foo", "###"))), fieldCache).resolve(pipelineConfig);
        Assert.assertThat(pipelineConfig.getLabelTemplate(), is("###"));
    }

    @Test
    public void shouldResolveConfigValue() throws NoSuchFieldException {
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("cruise", "dev", "ant");
        pipelineConfig.setLabelTemplate("2.1-${COUNT}-#{foo}-bar-#{bar}");
        StageConfig stageConfig = pipelineConfig.get(0);
        stageConfig.updateApproval(new Approval(new AuthConfig(new AdminUser(new CaseInsensitiveString("#{foo}")), new AdminUser(new CaseInsensitiveString("#{bar}")))));
        new ParamResolver(new ParamSubstitutionHandlerFactory(params(param("foo", "pavan"), param("bar", "jj"))), fieldCache).resolve(pipelineConfig);
        Assert.assertThat(pipelineConfig.getLabelTemplate(), is("2.1-${COUNT}-pavan-bar-jj"));
        Assert.assertThat(stageConfig.getApproval().getAuthConfig(), is(new AuthConfig(new AdminUser(new CaseInsensitiveString("pavan")), new AdminUser(new CaseInsensitiveString("jj")))));
    }

    @Test
    public void shouldResolveSubTags() throws NoSuchFieldException {
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("cruise", "dev", "ant");
        pipelineConfig.setLabelTemplate("2.1-${COUNT}-#{foo}-bar-#{bar}");
        TrackingTool trackingTool = new TrackingTool("http://#{foo}.com/#{bar}", "\\w+#{bar}");
        pipelineConfig.setTrackingTool(trackingTool);
        new ParamResolver(new ParamSubstitutionHandlerFactory(params(param("foo", "pavan"), param("bar", "jj"))), fieldCache).resolve(pipelineConfig);
        Assert.assertThat(pipelineConfig.getLabelTemplate(), is("2.1-${COUNT}-pavan-bar-jj"));
        Assert.assertThat(trackingTool.getLink(), is("http://pavan.com/jj"));
        Assert.assertThat(trackingTool.getRegex(), is("\\w+jj"));
    }

    @Test
    public void shouldResolveCollections() throws NoSuchFieldException {
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("cruise", "dev", "ant");
        pipelineConfig.setLabelTemplate("2.1-${COUNT}-#{foo}-bar-#{bar}");
        HgMaterialConfig materialConfig = MaterialConfigsMother.hgMaterialConfig("http://#{foo}.com/#{bar}");
        pipelineConfig.addMaterialConfig(materialConfig);
        new ParamResolver(new ParamSubstitutionHandlerFactory(params(param("foo", "pavan"), param("bar", "jj"))), fieldCache).resolve(pipelineConfig);
        Assert.assertThat(pipelineConfig.getLabelTemplate(), is("2.1-${COUNT}-pavan-bar-jj"));
        Assert.assertThat(pipelineConfig.materialConfigs().get(1).getUriForDisplay(), is("http://pavan.com/jj"));
    }

    @Test
    public void shouldResolveInBasicPipelineConfigs() throws NoSuchFieldException {
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("cruise", "dev", "ant");
        pipelineConfig.setLabelTemplate("2.1-${COUNT}-#{foo}-bar-#{bar}");
        HgMaterialConfig materialConfig = MaterialConfigsMother.hgMaterialConfig("http://#{foo}.com/#{bar}");
        pipelineConfig.addMaterialConfig(materialConfig);
        BasicPipelineConfigs pipelines = new BasicPipelineConfigs(pipelineConfig);
        new ParamResolver(new ParamSubstitutionHandlerFactory(params(param("foo", "pavan"), param("bar", "jj"))), fieldCache).resolve(pipelines);
        Assert.assertThat(pipelineConfig.getLabelTemplate(), is("2.1-${COUNT}-pavan-bar-jj"));
        Assert.assertThat(pipelineConfig.materialConfigs().get(1).getUriForDisplay(), is("http://pavan.com/jj"));
    }

    @Test
    public void shouldResolveInMergePipelineConfigs() throws NoSuchFieldException {
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("cruise", "dev", "ant");
        pipelineConfig.setLabelTemplate("2.1-${COUNT}-#{foo}-bar-#{bar}");
        HgMaterialConfig materialConfig = MaterialConfigsMother.hgMaterialConfig("http://#{foo}.com/#{bar}");
        pipelineConfig.addMaterialConfig(materialConfig);
        MergePipelineConfigs merge = new MergePipelineConfigs(new BasicPipelineConfigs(), new BasicPipelineConfigs(pipelineConfig));
        new ParamResolver(new ParamSubstitutionHandlerFactory(params(param("foo", "pavan"), param("bar", "jj"))), fieldCache).resolve(merge);
        Assert.assertThat(pipelineConfig.getLabelTemplate(), is("2.1-${COUNT}-pavan-bar-jj"));
        Assert.assertThat(pipelineConfig.materialConfigs().get(1).getUriForDisplay(), is("http://pavan.com/jj"));
    }

    @Test
    public void shouldProvideContextWhenAnExceptionOccurs() throws NoSuchFieldException {
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("cruise", "dev", "ant");
        pipelineConfig.setLabelTemplate("#a");
        new ParamResolver(new ParamSubstitutionHandlerFactory(params(param("foo", "pavan"), param("bar", "jj"))), fieldCache).resolve(pipelineConfig);
        Assert.assertThat(pipelineConfig.errors().on("labelTemplate"), is("Error when processing params for '#a' used in field 'labelTemplate', # must be followed by a parameter pattern or escaped by another #"));
    }

    @Test
    public void shouldUseValidationErrorKeyAnnotationForFieldNameInCaseOfException() throws NoSuchFieldException {
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("cruise", "dev", "ant", "nant");
        FetchTask task = new FetchTask(new CaseInsensitiveString("cruise"), new CaseInsensitiveString("dev"), new CaseInsensitiveString("ant"), "#a", "dest");
        pipelineConfig.get(0).getJobs().getJob(new CaseInsensitiveString("nant")).addTask(task);
        new ParamResolver(new ParamSubstitutionHandlerFactory(params(param("foo", "pavan"), param("bar", "jj"))), fieldCache).resolve(pipelineConfig);
        Assert.assertThat(task.errors().isEmpty(), is(false));
        Assert.assertThat(task.errors().on(SRC), is("Error when processing params for '#a' used in field 'src', # must be followed by a parameter pattern or escaped by another #"));
    }

    @Test
    public void shouldAddErrorTheMessageOnTheRightFieldOfTheRightElement() throws NoSuchFieldException {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.setName("#{not-found}");
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("cruise", "dev", "ant");
        pipelineConfig.setLabelTemplate("#a");
        pipelineConfig.get(0).getJobs().addJobWithoutValidityAssertion(new JobConfig(new CaseInsensitiveString("another"), new ResourceConfigs(resourceConfig), new ArtifactConfigs()));
        new ParamResolver(new ParamSubstitutionHandlerFactory(params(param("foo", "pavan"), param("bar", "jj"))), fieldCache).resolve(pipelineConfig);
        Assert.assertThat(pipelineConfig.errors().on("labelTemplate"), is("Error when processing params for '#a' used in field 'labelTemplate', # must be followed by a parameter pattern or escaped by another #"));
        Assert.assertThat(resourceConfig.errors().on(RESOURCES), is("Parameter 'not-found' is not defined. All pipelines using this parameter directly or via a template must define it."));
    }

    @Test
    public void shouldProvideContextWhenAnExceptionOccursBecauseOfHashAtEnd() throws NoSuchFieldException {
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("cruise", "dev", "ant");
        pipelineConfig.setLabelTemplate("abc#");
        new ParamResolver(new ParamSubstitutionHandlerFactory(params(param("foo", "pavan"), param("bar", "jj"))), fieldCache).resolve(pipelineConfig);
        Assert.assertThat(pipelineConfig.errors().on("labelTemplate"), is("Error when processing params for 'abc#' used in field 'labelTemplate', # must be followed by a parameter pattern or escaped by another #"));
    }

    @Test
    public void shouldProvideContextWhenAnExceptionOccursBecauseOfIncompleteParamAtEnd() throws NoSuchFieldException {
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("cruise", "dev", "ant");
        pipelineConfig.setLabelTemplate("abc#{");
        new ParamResolver(new ParamSubstitutionHandlerFactory(params(param("foo", "pavan"), param("bar", "jj"))), fieldCache).resolve(pipelineConfig);
        Assert.assertThat(pipelineConfig.errors().on("labelTemplate"), is("Incomplete param usage in 'abc#{'"));
    }

    @Test
    public void shouldResolveInheritedAttributes() throws NoSuchFieldException {
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("cruise", "dev", "ant");
        HgMaterialConfig materialConfig = MaterialConfigsMother.hgMaterialConfig();
        materialConfig.setConfigAttributes(Collections.singletonMap(FOLDER, "work/#{foo}/#{bar}/baz"));
        pipelineConfig.addMaterialConfig(materialConfig);
        new ParamResolver(new ParamSubstitutionHandlerFactory(params(param("foo", "pavan"), param("bar", "jj"))), fieldCache).resolve(pipelineConfig);
        Assert.assertThat(pipelineConfig.materialConfigs().get(1).getFolder(), is("work/pavan/jj/baz"));
    }

    @Test
    public void shouldAddResolutionErrorOnViewIfP4MaterialViewHasAnError() throws NoSuchFieldException {
        P4MaterialViewConfig p4MaterialViewConfig = new P4MaterialViewConfig("#");
        new ParamResolver(new ParamSubstitutionHandlerFactory(params(param("foo", "pavan"), param("bar", "jj"))), fieldCache).resolve(p4MaterialViewConfig);
        Assert.assertThat(p4MaterialViewConfig.errors().on(VIEW), is("Error when processing params for '#' used in field 'view', # must be followed by a parameter pattern or escaped by another #"));
    }

    @Test
    public void shouldErrorOutIfCannotResolveParamForP4View() {
        P4MaterialConfig p4MaterialConfig = new P4MaterialConfig("server:port", "#");
        new ParamResolver(new ParamSubstitutionHandlerFactory(params(param("foo", "pavan"), param("bar", "jj"))), fieldCache).resolve(p4MaterialConfig);
        Assert.assertThat(p4MaterialConfig.getP4MaterialView().errors().on(VIEW), is("Error when processing params for '#' used in field 'view', # must be followed by a parameter pattern or escaped by another #"));
    }

    @Test
    public void shouldLexicallyScopeTheParameters() throws NoSuchFieldException {
        PipelineConfig withParams = PipelineConfigMother.createPipelineConfig("cruise", "dev", "ant");
        withParams.addParam(param("foo", "pipeline"));
        PipelineConfig withoutParams = PipelineConfigMother.createPipelineConfig("mingle", "dev", "ant");
        CruiseConfig cruiseConfig = new BasicCruiseConfig();
        cruiseConfig.addPipeline("group", withParams);
        cruiseConfig.addPipeline("group", withoutParams);
        cruiseConfig.server().setArtifactsDir("/#{foo}/#{bar}");
        HgMaterialConfig materialConfig = MaterialConfigsMother.hgMaterialConfig();
        materialConfig.setConfigAttributes(Collections.singletonMap(FOLDER, "work/#{foo}/#{bar}/baz"));
        withParams.addMaterialConfig(materialConfig);
        withParams.setLabelTemplate("2.0.#{foo}-#{bar}");
        withoutParams.setLabelTemplate("2.0.#{foo}-#{bar}");
        new ParamResolver(new ParamSubstitutionHandlerFactory(params(param("foo", "global"), param("bar", "global-only"))), fieldCache).resolve(cruiseConfig);
        Assert.assertThat(withParams.materialConfigs().get(1).getFolder(), is("work/pipeline/global-only/baz"));
        Assert.assertThat(withParams.getLabelTemplate(), is("2.0.pipeline-global-only"));
        Assert.assertThat(withoutParams.getLabelTemplate(), is("2.0.global-global-only"));
    }

    @Test
    public void shouldSkipResolution() throws NoSuchFieldException {
        Object[] specs = new Object[]{ com.thoughtworks.go.config.BasicCruiseConfig.class, "serverConfig", com.thoughtworks.go.config.BasicCruiseConfig.class, "templatesConfig", com.thoughtworks.go.config.BasicCruiseConfig.class, "environments", com.thoughtworks.go.config.BasicCruiseConfig.class, "agents", com.thoughtworks.go.config.BasicPipelineConfigs.class, "authorization", com.thoughtworks.go.config.PipelineConfig.class, "name", com.thoughtworks.go.config.PipelineConfig.class, "params", com.thoughtworks.go.config.PipelineConfig.class, "templateName", com.thoughtworks.go.config.StageConfig.class, "name", AbstractMaterialConfig.class, "name", ArtifactPropertyConfig.class, "name", Approval.class, "type", JobConfig.class, "jobName", RunIfConfig.class, "status" };
        for (int i = 0; i < (specs.length); i += 2) {
            Class clz = ((Class) (specs[i]));
            String field = ((String) (specs[(i + 1)]));
            assertSkipsResolution(clz, field);
        }
    }
}

