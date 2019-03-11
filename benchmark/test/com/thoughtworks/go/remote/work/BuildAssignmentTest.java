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
package com.thoughtworks.go.remote.work;


import com.google.gson.Gson;
import com.thoughtworks.go.config.ArtifactStores;
import com.thoughtworks.go.config.materials.dependency.DependencyMaterial;
import com.thoughtworks.go.config.materials.mercurial.HgMaterial;
import com.thoughtworks.go.config.materials.svn.SvnMaterial;
import com.thoughtworks.go.domain.buildcause.BuildCause;
import com.thoughtworks.go.domain.materials.Modification;
import com.thoughtworks.go.domain.materials.svn.SvnCommand;
import com.thoughtworks.go.helper.HgTestRepo;
import com.thoughtworks.go.helper.MaterialsMother;
import com.thoughtworks.go.helper.ModificationsMother;
import com.thoughtworks.go.util.SystemEnvironment;
import com.thoughtworks.go.util.command.EnvironmentVariableContext;
import com.thoughtworks.go.utils.SvnRepoFixture;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class BuildAssignmentTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static final String JOB_NAME = "one";

    private static final String STAGE_NAME = "first";

    private static final String PIPELINE_NAME = "cruise";

    private static final String TRIGGERED_BY_USER = "approver";

    private File dir;

    private SvnCommand command;

    private HgTestRepo hgTestRepo;

    private HgMaterial hgMaterial;

    private SvnMaterial svnMaterial;

    private DependencyMaterial dependencyMaterial;

    private DependencyMaterial dependencyMaterialWithName;

    private SvnRepoFixture svnRepoFixture;

    @Test
    public void shouldInitializeEnvironmentContextFromJobPlan() throws Exception {
        DefaultJobPlan defaultJobPlan = jobForPipeline("foo");
        EnvironmentVariables variables = new EnvironmentVariables();
        variables.add("key1", "value1");
        variables.add("key2", "value2");
        defaultJobPlan.setVariables(variables);
        BuildAssignment buildAssignment = BuildAssignment.create(defaultJobPlan, BuildCause.createManualForced(), new ArrayList(), null, null, new ArtifactStores());
        EnvironmentVariableContext context = buildAssignment.initialEnvironmentVariableContext();
        Assert.assertThat(context.getProperties().size(), Matchers.is(9));
        Assert.assertThat(context.getProperty("key1"), Matchers.is("value1"));
        Assert.assertThat(context.getProperty("key2"), Matchers.is("value2"));
    }

    @Test
    public void shouldInitializeEnvironmentContextFromJobPlanWithTriggerVariablesOverridingEnvVariablesFromJob() throws Exception {
        DefaultJobPlan defaultJobPlan = jobForPipeline("foo");
        EnvironmentVariables triggerVariables = new EnvironmentVariables();
        triggerVariables.add("key1", "override");
        triggerVariables.add("key3", "value3");
        EnvironmentVariables variables = new EnvironmentVariables();
        variables.add("key1", "value1");
        variables.add("key2", "value2");
        defaultJobPlan.setTriggerVariables(triggerVariables);
        defaultJobPlan.setVariables(variables);
        BuildAssignment buildAssignment = BuildAssignment.create(defaultJobPlan, BuildCause.createManualForced(), new ArrayList(), null, null, new ArtifactStores());
        EnvironmentVariableContext context = buildAssignment.initialEnvironmentVariableContext();
        Assert.assertThat(context.getProperties().size(), Matchers.is(9));
        Assert.assertThat(context.getProperty("key1"), Matchers.is("override"));
        Assert.assertThat(context.getProperty("key2"), Matchers.is("value2"));
    }

    @Test
    public void shouldIntializeEnvironmentContextWithJobPlanEnvironmentVariablesOveridingEnvVariablesFromTheEnvironment() throws Exception {
        DefaultJobPlan defaultJobPlan = jobForPipeline("foo");
        EnvironmentVariables variables = new EnvironmentVariables();
        variables.add("key1", "value_from_job_plan");
        variables.add("key2", "value2");
        defaultJobPlan.setVariables(variables);
        EnvironmentVariableContext contextFromEnvironment = new EnvironmentVariableContext("key1", "value_from_environment");
        contextFromEnvironment.setProperty("key3", "value3", false);
        BuildAssignment buildAssignment = BuildAssignment.create(defaultJobPlan, BuildCause.createManualForced(), new ArrayList(), null, contextFromEnvironment, new ArtifactStores());
        EnvironmentVariableContext context = buildAssignment.initialEnvironmentVariableContext();
        Assert.assertThat(context.getProperties().size(), Matchers.is(10));
        Assert.assertThat(context.getProperty("key1"), Matchers.is("value_from_job_plan"));
        Assert.assertThat(context.getProperty("key2"), Matchers.is("value2"));
        Assert.assertThat(context.getProperty("key3"), Matchers.is("value3"));
    }

    @Test
    public void shouldNotHaveReferenceToModifiedFilesSinceLargeCommitsCouldCauseBothServerAndAgentsToRunOutOfMemory_MoreoverThisInformationIsNotRequiredOnAgentSide() {
        List<Modification> modificationsForSvn = ModificationsMother.multipleModificationList();
        List<Modification> modificationsForHg = ModificationsMother.multipleModificationList();
        MaterialRevision svn = new MaterialRevision(MaterialsMother.svnMaterial(), modificationsForSvn);
        MaterialRevision hg = new MaterialRevision(MaterialsMother.hgMaterial(), modificationsForHg);
        MaterialRevisions materialRevisions = new MaterialRevisions(svn, hg);
        BuildCause buildCause = BuildCause.createWithModifications(materialRevisions, "user1");
        BuildAssignment buildAssignment = BuildAssignment.create(jobForPipeline("foo"), buildCause, new ArrayList(), null, null, new ArtifactStores());
        Assert.assertThat(buildAssignment.getBuildApprover(), Matchers.is("user1"));
        Assert.assertThat(buildAssignment.materialRevisions().getRevisions().size(), Matchers.is(materialRevisions.getRevisions().size()));
        assertRevisions(buildAssignment, svn);
        assertRevisions(buildAssignment, hg);
    }

    @Test
    public void shouldCopyAdditionalDataToBuildAssignment() {
        MaterialRevision packageMaterialRevision = ModificationsMother.createPackageMaterialRevision("revision");
        Map<String, String> additionalData = new HashMap<>();
        additionalData.put("a1", "v1");
        additionalData.put("a2", "v2");
        String additionalDataAsString = new Gson().toJson(additionalData);
        packageMaterialRevision.getModifications().first().setAdditionalData(additionalDataAsString);
        MaterialRevisions materialRevisions = new MaterialRevisions(packageMaterialRevision);
        BuildCause buildCause = BuildCause.createWithModifications(materialRevisions, "user1");
        BuildAssignment buildAssignment = BuildAssignment.create(jobForPipeline("foo"), buildCause, new ArrayList(), null, null, new ArtifactStores());
        Assert.assertThat(buildAssignment.getBuildApprover(), Matchers.is("user1"));
        Assert.assertThat(buildAssignment.materialRevisions().getRevisions().size(), Matchers.is(materialRevisions.getRevisions().size()));
        assertRevisions(buildAssignment, packageMaterialRevision);
        Modification actualModification = buildAssignment.materialRevisions().getRevisions().get(0).getModification(0);
        Assert.assertThat(actualModification.getAdditionalData(), Matchers.is(additionalDataAsString));
        Assert.assertThat(actualModification.getAdditionalDataMap(), Matchers.is(additionalData));
    }

    @Test
    public void shouldSetUpGoGeneratedEnvironmentContextCorrectly() throws Exception {
        new SystemEnvironment().setProperty("serviceUrl", "some_random_place");
        BuildAssignment buildAssigment = createAssignment(null);
        EnvironmentVariableContext environmentVariableContext = buildAssigment.initialEnvironmentVariableContext();
        Assert.assertThat(environmentVariableContext.getProperty("GO_REVISION"), Matchers.is("3"));
        Assert.assertThat(environmentVariableContext.getProperty("GO_PIPELINE_NAME"), Matchers.is(BuildAssignmentTest.PIPELINE_NAME));
        Assert.assertThat(environmentVariableContext.getProperty("GO_PIPELINE_LABEL"), Matchers.is("1"));
        Assert.assertThat(environmentVariableContext.getProperty("GO_STAGE_NAME"), Matchers.is(BuildAssignmentTest.STAGE_NAME));
        Assert.assertThat(environmentVariableContext.getProperty("GO_STAGE_COUNTER"), Matchers.is("1"));
        Assert.assertThat(environmentVariableContext.getProperty("GO_JOB_NAME"), Matchers.is(BuildAssignmentTest.JOB_NAME));
        Assert.assertThat(environmentVariableContext.getProperty("GO_TRIGGER_USER"), Matchers.is(BuildAssignmentTest.TRIGGERED_BY_USER));
    }
}

