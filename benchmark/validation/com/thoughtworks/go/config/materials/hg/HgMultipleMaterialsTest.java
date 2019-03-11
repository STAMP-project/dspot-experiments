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
package com.thoughtworks.go.config.materials.hg;


import com.thoughtworks.go.config.MaterialRevisionsMatchers;
import com.thoughtworks.go.config.materials.Materials;
import com.thoughtworks.go.config.materials.mercurial.HgMaterial;
import com.thoughtworks.go.domain.MaterialRevision;
import com.thoughtworks.go.domain.MaterialRevisions;
import com.thoughtworks.go.domain.materials.TestSubprocessExecutionContext;
import com.thoughtworks.go.helper.HgTestRepo;
import com.thoughtworks.go.util.command.ProcessOutputStreamConsumer;
import java.io.File;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class HgMultipleMaterialsTest {
    private HgTestRepo repo;

    private File pipelineDir;

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldCloneMaterialToItsDestFolder() throws Exception {
        HgMaterial material1 = repo.createMaterial("dest1");
        MaterialRevision materialRevision = new MaterialRevision(material1, material1.latestModification(pipelineDir, new TestSubprocessExecutionContext()));
        materialRevision.updateTo(pipelineDir, ProcessOutputStreamConsumer.inMemoryConsumer(), new TestSubprocessExecutionContext());
        Assert.assertThat(new File(pipelineDir, "dest1").exists(), Matchers.is(true));
        Assert.assertThat(new File(pipelineDir, "dest1/.hg").exists(), Matchers.is(true));
    }

    @Test
    public void shouldIgnoreDestinationFolderWhenServerSide() throws Exception {
        HgMaterial material1 = repo.createMaterial("dest1");
        MaterialRevision materialRevision = new MaterialRevision(material1, material1.latestModification(pipelineDir, new TestSubprocessExecutionContext()));
        materialRevision.updateTo(pipelineDir, ProcessOutputStreamConsumer.inMemoryConsumer(), new TestSubprocessExecutionContext(true));
        Assert.assertThat(new File(pipelineDir, "dest1").exists(), Matchers.is(false));
        Assert.assertThat(new File(pipelineDir, ".hg").exists(), Matchers.is(true));
    }

    @Test
    public void shouldFindModificationsForBothMaterials() throws Exception {
        Materials materials = new Materials(repo.createMaterial("dest1"), repo.createMaterial("dest2"));
        repo.commitAndPushFile("SomeDocumentation.txt");
        MaterialRevisions materialRevisions = materials.latestModification(pipelineDir, new TestSubprocessExecutionContext());
        Assert.assertThat(materialRevisions.getRevisions().size(), Matchers.is(2));
        Assert.assertThat(materialRevisions, MaterialRevisionsMatchers.containsModifiedBy("SomeDocumentation.txt", "user"));
    }
}

