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
package com.thoughtworks.go.remote.work;


import com.thoughtworks.go.config.CaseInsensitiveString;
import com.thoughtworks.go.config.materials.svn.SvnMaterial;
import com.thoughtworks.go.domain.MaterialRevision;
import com.thoughtworks.go.domain.MaterialRevisions;
import com.thoughtworks.go.helper.MaterialsMother;
import com.thoughtworks.go.helper.ModificationsMother;
import com.thoughtworks.go.util.SystemEnvironment;
import com.thoughtworks.go.util.command.EnvironmentVariableContext;
import java.io.IOException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.rules.TemporaryFolder;


public class EnvironmentVariableContextTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public final RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties();

    private String pipelineName = "pipeline-name";

    private String pipelineLabel = "pipeline-label";

    private String stageName = "stage-name";

    private String stageCounter = "stage-counter";

    private String jobName = "build-name";

    @Test
    public void shouldPopulateEnvironmentForServerUrl() {
        new SystemEnvironment().setProperty("serviceUrl", "some_random_place");
        EnvironmentVariableContext context = new EnvironmentVariableContext();
        context.setProperty("GO_SERVER_URL", SystemEnvironment.getProperty("serviceUrl"), false);
        jobIdentifier().populateEnvironmentVariables(context);
        Assert.assertThat(context.getProperty("GO_SERVER_URL"), Matchers.is("some_random_place"));
    }

    @Test
    public void shouldPopulateEnvironmentForJobIdentifier() {
        EnvironmentVariableContext context = new EnvironmentVariableContext();
        context.setProperty("GO_SERVER_URL", SystemEnvironment.getProperty("serviceUrl"), false);
        jobIdentifier().populateEnvironmentVariables(context);
        Assert.assertThat(context.getProperty("GO_PIPELINE_NAME"), Matchers.is(pipelineName));
        Assert.assertThat(context.getProperty("GO_PIPELINE_LABEL"), Matchers.is(pipelineLabel));
        Assert.assertThat(context.getProperty("GO_STAGE_NAME"), Matchers.is(stageName));
        Assert.assertThat(context.getProperty("GO_STAGE_COUNTER"), Matchers.is(stageCounter));
        Assert.assertThat(context.getProperty("GO_JOB_NAME"), Matchers.is(jobName));
    }

    @Test
    public void shouldPopulateEnvironmentForMaterialUsingMaterialName() throws IOException {
        SvnMaterial svn = MaterialsMother.svnMaterial();
        svn.setName(new CaseInsensitiveString("svn"));
        svn.setFolder("svn-dir");
        MaterialRevision revision = new MaterialRevision(svn, ModificationsMother.oneModifiedFile("revision1"));
        MaterialRevisions materialRevisions = new MaterialRevisions(revision);
        EnvironmentVariableContext context = new EnvironmentVariableContext();
        context.setProperty("GO_SERVER_URL", SystemEnvironment.getProperty("serviceUrl"), false);
        jobIdentifier().populateEnvironmentVariables(context);
        materialRevisions.populateEnvironmentVariables(context, temporaryFolder.newFolder());
        Assert.assertThat(context.getProperty("GO_REVISION_SVN"), Matchers.is("revision1"));
        Assert.assertThat(context.getProperty("GO_MATERIAL_SVN_HAS_CHANGED"), Matchers.is("false"));
    }

    @Test
    public void shouldPopulateEnvironmentForMaterialUsingDest() throws IOException {
        SvnMaterial svn = MaterialsMother.svnMaterial();
        svn.setFolder("svn-dir");
        MaterialRevision revision = new MaterialRevision(svn, ModificationsMother.oneModifiedFile("revision1"));
        MaterialRevisions materialRevisions = new MaterialRevisions(revision);
        EnvironmentVariableContext context = new EnvironmentVariableContext();
        context.setProperty("GO_SERVER_URL", SystemEnvironment.getProperty("serviceUrl"), false);
        jobIdentifier().populateEnvironmentVariables(context);
        materialRevisions.populateEnvironmentVariables(context, temporaryFolder.newFolder());
        Assert.assertThat(context.getProperty("GO_REVISION_SVN_DIR"), Matchers.is("revision1"));
    }

    @Test
    public void shouldPopulateEnvironmentForDependencyMaterialUsingMaterialName() throws IOException {
        String materialName = "upstreamPipeline";
        MaterialRevision revision = materialRevision(materialName, "pipeline-name", 1, "pipeline-label", "stage-name", 1);
        MaterialRevisions materialRevisions = new MaterialRevisions(revision);
        EnvironmentVariableContext context = new EnvironmentVariableContext();
        context.setProperty("GO_SERVER_URL", SystemEnvironment.getProperty("serviceUrl"), false);
        jobIdentifier().populateEnvironmentVariables(context);
        materialRevisions.populateEnvironmentVariables(context, temporaryFolder.newFolder());
        Assert.assertThat(context.getProperty("GO_DEPENDENCY_LABEL_UPSTREAMPIPELINE"), Matchers.is("pipeline-label"));
        Assert.assertThat(context.getProperty("GO_DEPENDENCY_LOCATOR_UPSTREAMPIPELINE"), Matchers.is("pipeline-name/1/stage-name/1"));
    }

    @Test
    public void shouldPopulateEnvironmentForDependencyMaterialUsingPipelineNameStageName() throws IOException {
        String EMPTY_NAME = "";
        MaterialRevision revision = materialRevision(EMPTY_NAME, "pipeline-name", 1, "pipeline-label", "stage-name", 1);
        MaterialRevisions materialRevisions = new MaterialRevisions(revision);
        EnvironmentVariableContext context = new EnvironmentVariableContext();
        context.setProperty("GO_SERVER_URL", SystemEnvironment.getProperty("serviceUrl"), false);
        jobIdentifier().populateEnvironmentVariables(context);
        materialRevisions.populateEnvironmentVariables(context, temporaryFolder.newFolder());
        Assert.assertThat(context.getProperty("GO_DEPENDENCY_LABEL_PIPELINE_NAME"), Matchers.is("pipeline-label"));
        Assert.assertThat(context.getProperty("GO_DEPENDENCY_LOCATOR_PIPELINE_NAME"), Matchers.is("pipeline-name/1/stage-name/1"));
    }
}

