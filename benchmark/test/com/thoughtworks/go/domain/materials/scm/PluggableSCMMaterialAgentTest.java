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
package com.thoughtworks.go.domain.materials.scm;


import com.google.gson.Gson;
import com.thoughtworks.go.config.materials.PluggableSCMMaterial;
import com.thoughtworks.go.domain.MaterialRevision;
import com.thoughtworks.go.domain.config.Configuration;
import com.thoughtworks.go.domain.materials.Modification;
import com.thoughtworks.go.domain.scm.SCM;
import com.thoughtworks.go.domain.scm.SCMMother;
import com.thoughtworks.go.helper.MaterialsMother;
import com.thoughtworks.go.helper.ModificationsMother;
import com.thoughtworks.go.plugin.access.scm.SCMExtension;
import com.thoughtworks.go.plugin.access.scm.SCMPropertyConfiguration;
import com.thoughtworks.go.plugin.access.scm.revision.SCMRevision;
import com.thoughtworks.go.plugin.api.response.Result;
import com.thoughtworks.go.util.command.ConsoleOutputStreamConsumer;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class PluggableSCMMaterialAgentTest {
    @Mock
    private SCMExtension scmExtension;

    @Mock
    private ConsoleOutputStreamConsumer consumer;

    private ArgumentCaptor<SCMPropertyConfiguration> scmConfiguration;

    private ArgumentCaptor<SCMRevision> scmRevision;

    @Test
    public void shouldTalkToPluginCheckoutForPrepare() {
        PluggableSCMMaterial pluggableSCMMaterial = MaterialsMother.pluggableSCMMaterial();
        pluggableSCMMaterial.setFolder("destination-folder");
        Modification modification = ModificationsMother.oneModifiedFile("r1");
        Map<String, String> additionalData = new HashMap<>();
        additionalData.put("a1", "v1");
        additionalData.put("a2", "v2");
        modification.setAdditionalData(new Gson().toJson(additionalData));
        MaterialRevision revision = new MaterialRevision(pluggableSCMMaterial, modification);
        String pipelineFolder = new File(System.getProperty("java.io.tmpdir")).getAbsolutePath();
        String destinationFolder = new File(pipelineFolder, "destination-folder").getAbsolutePath();
        PluggableSCMMaterialAgent pluggableSCMMaterialAgent = new PluggableSCMMaterialAgent(scmExtension, revision, new File(pipelineFolder), consumer);
        Mockito.when(scmExtension.checkout(ArgumentMatchers.eq("pluginid"), scmConfiguration.capture(), ArgumentMatchers.eq(destinationFolder), scmRevision.capture())).thenReturn(new Result());
        pluggableSCMMaterialAgent.prepare();
        Mockito.verify(scmExtension).checkout(ArgumentMatchers.any(String.class), ArgumentMatchers.any(SCMPropertyConfiguration.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(SCMRevision.class));
        Assert.assertThat(scmConfiguration.getValue().size(), Matchers.is(2));
        Assert.assertThat(scmConfiguration.getValue().get("k1").getValue(), Matchers.is("v1"));
        Assert.assertThat(scmConfiguration.getValue().get("k2").getValue(), Matchers.is("v2"));
        Assert.assertThat(scmRevision.getValue().getRevision(), Matchers.is("r1"));
        Assert.assertThat(scmRevision.getValue().getTimestamp(), Matchers.is(modification.getModifiedTime()));
        Assert.assertThat(scmRevision.getValue().getData().size(), Matchers.is(2));
        Assert.assertThat(scmRevision.getValue().getDataFor("a1"), Matchers.is("v1"));
        Assert.assertThat(scmRevision.getValue().getDataFor("a2"), Matchers.is("v2"));
    }

    @Test
    public void shouldLogToStdOutWhenPluginSendsCheckoutResultWithSuccessMessages() {
        SCM scmConfig = SCMMother.create("scm-id", "scm-name", "pluginid", "version", new Configuration());
        PluggableSCMMaterial pluggableSCMMaterial = MaterialsMother.pluggableSCMMaterial();
        pluggableSCMMaterial.setFolder("destination-folder");
        pluggableSCMMaterial.setSCMConfig(scmConfig);
        Modification modification = ModificationsMother.oneModifiedFile("r1");
        Map<String, String> additionalData = new HashMap<>();
        additionalData.put("a1", "v1");
        additionalData.put("a2", "v2");
        modification.setAdditionalData(new Gson().toJson(additionalData));
        MaterialRevision revision = new MaterialRevision(pluggableSCMMaterial, modification);
        String pipelineFolder = new File(System.getProperty("java.io.tmpdir")).getAbsolutePath();
        String destinationFolder = new File(pipelineFolder, "destination-folder").getAbsolutePath();
        PluggableSCMMaterialAgent pluggableSCMMaterialAgent = new PluggableSCMMaterialAgent(scmExtension, revision, new File(pipelineFolder), consumer);
        Mockito.when(scmExtension.checkout(ArgumentMatchers.eq("pluginid"), ArgumentMatchers.any(), ArgumentMatchers.eq(destinationFolder), ArgumentMatchers.any())).thenReturn(new Result().withSuccessMessages("Material scm-name is updated."));
        pluggableSCMMaterialAgent.prepare();
        Mockito.verify(consumer, Mockito.times(1)).stdOutput("Material scm-name is updated.");
    }

    @Test(expected = RuntimeException.class)
    public void shouldLogToErrorOutputWhenPluginReturnResultWithCheckoutFailure() {
        SCM scmConfig = SCMMother.create("scm-id", "scm-name", "pluginid", "version", new Configuration());
        PluggableSCMMaterial pluggableSCMMaterial = MaterialsMother.pluggableSCMMaterial();
        pluggableSCMMaterial.setFolder("destination-folder");
        pluggableSCMMaterial.setSCMConfig(scmConfig);
        Modification modification = ModificationsMother.oneModifiedFile("r1");
        Map<String, String> additionalData = new HashMap<>();
        additionalData.put("a1", "v1");
        additionalData.put("a2", "v2");
        modification.setAdditionalData(new Gson().toJson(additionalData));
        MaterialRevision revision = new MaterialRevision(pluggableSCMMaterial, modification);
        String pipelineFolder = new File(System.getProperty("java.io.tmpdir")).getAbsolutePath();
        String destinationFolder = new File(pipelineFolder, "destination-folder").getAbsolutePath();
        PluggableSCMMaterialAgent pluggableSCMMaterialAgent = new PluggableSCMMaterialAgent(scmExtension, revision, new File(pipelineFolder), consumer);
        Mockito.when(scmExtension.checkout(ArgumentMatchers.eq("pluginid"), ArgumentMatchers.any(), ArgumentMatchers.eq(destinationFolder), ArgumentMatchers.any())).thenReturn(new Result().withErrorMessages("No such revision."));
        pluggableSCMMaterialAgent.prepare();
        Mockito.verify(consumer, Mockito.times(1)).errOutput("Material scm-name checkout failed: No such revision.");
    }

    @Test(expected = RuntimeException.class)
    public void shouldLogToErrorOutputWhenPluginSendsErrorResponse() {
        SCM scmConfig = SCMMother.create("scm-id", "scm-name", "pluginid", "version", new Configuration());
        PluggableSCMMaterial pluggableSCMMaterial = MaterialsMother.pluggableSCMMaterial();
        pluggableSCMMaterial.setFolder("destination-folder");
        pluggableSCMMaterial.setSCMConfig(scmConfig);
        Modification modification = ModificationsMother.oneModifiedFile("r1");
        Map<String, String> additionalData = new HashMap<>();
        additionalData.put("a1", "v1");
        additionalData.put("a2", "v2");
        modification.setAdditionalData(new Gson().toJson(additionalData));
        MaterialRevision revision = new MaterialRevision(pluggableSCMMaterial, modification);
        String pipelineFolder = new File(System.getProperty("java.io.tmpdir")).getAbsolutePath();
        String destinationFolder = new File(pipelineFolder, "destination-folder").getAbsolutePath();
        PluggableSCMMaterialAgent pluggableSCMMaterialAgent = new PluggableSCMMaterialAgent(scmExtension, revision, new File(pipelineFolder), consumer);
        Mockito.when(scmExtension.checkout(ArgumentMatchers.eq("pluginid"), ArgumentMatchers.any(), ArgumentMatchers.eq(destinationFolder), ArgumentMatchers.any())).thenThrow(new RuntimeException("some message from plugin"));
        pluggableSCMMaterialAgent.prepare();
        Mockito.verify(consumer, Mockito.times(1)).errOutput("Material scm-name checkout failed: some message from plugin");
    }
}

