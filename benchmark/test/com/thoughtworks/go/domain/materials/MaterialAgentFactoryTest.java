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
package com.thoughtworks.go.domain.materials;


import com.thoughtworks.go.config.materials.PackageMaterial;
import com.thoughtworks.go.config.materials.PluggableSCMMaterial;
import com.thoughtworks.go.config.materials.SubprocessExecutionContext;
import com.thoughtworks.go.config.materials.git.GitMaterial;
import com.thoughtworks.go.domain.MaterialRevision;
import com.thoughtworks.go.domain.materials.scm.PluggableSCMMaterialAgent;
import com.thoughtworks.go.plugin.access.packagematerial.PackageRepositoryExtension;
import com.thoughtworks.go.plugin.access.scm.SCMExtension;
import com.thoughtworks.go.remote.AgentIdentifier;
import com.thoughtworks.go.util.CachedDigestUtils;
import com.thoughtworks.go.util.ReflectionUtil;
import com.thoughtworks.go.util.command.DevNull;
import java.io.File;
import java.io.IOException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;


public class MaterialAgentFactoryTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Mock
    private PackageRepositoryExtension packageRepositoryExtension;

    @Mock
    private SCMExtension scmExtension;

    @Test
    public void shouldCreateMaterialAgent_withAgentsUuidAsSubprocessExecutionContextNamespace() throws IOException {
        String agentUuid = "uuid-01783738";
        File workingDirectory = temporaryFolder.newFolder();
        MaterialAgentFactory factory = new MaterialAgentFactory(new com.thoughtworks.go.util.command.ProcessOutputStreamConsumer(new DevNull(), new DevNull()), workingDirectory, new AgentIdentifier("host", "1.1.1.1", agentUuid), scmExtension);
        GitMaterial gitMaterial = new GitMaterial("http://foo", "master", "dest_folder");
        MaterialAgent agent = factory.createAgent(new MaterialRevision(gitMaterial));
        Assert.assertThat(agent, Matchers.is(Matchers.instanceOf(AbstractMaterialAgent.class)));
        SubprocessExecutionContext execCtx = ((SubprocessExecutionContext) (ReflectionUtil.getField(agent, "execCtx")));
        Assert.assertThat(execCtx.getProcessNamespace("fingerprint"), Matchers.is(CachedDigestUtils.sha256Hex(String.format("%s%s%s", "fingerprint", agentUuid, gitMaterial.workingdir(workingDirectory)))));
    }

    @Test
    public void shouldGetPackageMaterialAgent() {
        File workingDirectory = new File("/tmp/workingDirectory");
        MaterialRevision revision = new MaterialRevision(new PackageMaterial(), new Modifications());
        MaterialAgentFactory factory = new MaterialAgentFactory(null, workingDirectory, null, scmExtension);
        MaterialAgent agent = factory.createAgent(revision);
        Assert.assertThat(agent, Matchers.is(MaterialAgent.NO_OP));
    }

    @Test
    public void shouldGetPluggableSCMMaterialAgent() {
        File workingDirectory = new File("/tmp/workingDirectory");
        MaterialRevision revision = new MaterialRevision(new PluggableSCMMaterial(), new Modifications());
        MaterialAgentFactory factory = new MaterialAgentFactory(null, workingDirectory, null, scmExtension);
        MaterialAgent agent = factory.createAgent(revision);
        Assert.assertThat((agent instanceof PluggableSCMMaterialAgent), Matchers.is(true));
        Assert.assertThat(ReflectionUtil.getField(agent, "scmExtension"), Matchers.is(scmExtension));
        Assert.assertThat(ReflectionUtil.getField(agent, "revision"), Matchers.is(revision));
        Assert.assertThat(ReflectionUtil.getField(agent, "workingDirectory"), Matchers.is(workingDirectory));
    }
}

