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


import com.thoughtworks.go.config.materials.Materials;
import com.thoughtworks.go.config.materials.git.GitMaterial;
import com.thoughtworks.go.config.materials.mercurial.HgMaterial;
import com.thoughtworks.go.config.materials.svn.SvnMaterial;
import com.thoughtworks.go.domain.MaterialRevisions;
import com.thoughtworks.go.domain.materials.git.GitTestRepo;
import com.thoughtworks.go.helper.HgTestRepo;
import com.thoughtworks.go.helper.SvnTestRepo;
import java.io.File;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class MixedMultipleMaterialsTest {
    private SvnTestRepo svnRepo;

    private HgTestRepo hgRepo;

    private GitTestRepo gitRepo;

    private File pipelineDir;

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldGetLatestModifications() throws Exception {
        HgMaterial hgMaterial = hgRepo.material();
        SvnMaterial svnMaterial = svnRepo.createMaterial("multiple-materials/trunk/part1", "part1");
        Materials materials = new Materials(hgMaterial, svnMaterial);
        MaterialRevisions revisions = materials.latestModification(pipelineDir, new TestSubprocessExecutionContext());
        Assert.assertThat(revisions.getMaterialRevision(0).numberOfModifications(), Matchers.is(1));
        Assert.assertThat(revisions.getMaterialRevision(0).getRevision(), Matchers.is(new Modifications(hgRepo.latestModifications()).latestRevision(hgMaterial)));
        Assert.assertThat(revisions.getMaterialRevision(1).numberOfModifications(), Matchers.is(1));
        Assert.assertThat(revisions.getMaterialRevision(1).getRevision(), Matchers.is(latestRevision(svnMaterial, pipelineDir, new TestSubprocessExecutionContext())));
        Assert.assertThat(revisions.toString(), revisions.totalNumberOfModifications(), Matchers.is(2));
    }

    @Test
    public void shouldGetLatestModificationswithThreeRepositories() throws Exception {
        HgMaterial hgMaterial = hgRepo.material();
        SvnMaterial svnMaterial = svnRepo.createMaterial("multiple-materials/trunk/part1", "part1");
        GitMaterial gitMaterial = gitRepo.createMaterial();
        Materials materials = new Materials(hgMaterial, svnMaterial, gitMaterial);
        MaterialRevisions revisions = materials.latestModification(pipelineDir, new TestSubprocessExecutionContext());
        Assert.assertThat(revisions.getMaterialRevision(0).numberOfModifications(), Matchers.is(1));
        Assert.assertThat(revisions.getMaterialRevision(0).getRevision(), Matchers.is(new Modifications(hgRepo.latestModifications()).latestRevision(hgMaterial)));
        Assert.assertThat(revisions.getMaterialRevision(1).numberOfModifications(), Matchers.is(1));
        Assert.assertThat(revisions.getMaterialRevision(1).getRevision(), Matchers.is(latestRevision(svnMaterial, pipelineDir, new TestSubprocessExecutionContext())));
        Assert.assertThat(revisions.getMaterialRevision(2).numberOfModifications(), Matchers.is(1));
        Assert.assertThat(revisions.getMaterialRevision(2).getRevision(), Matchers.is(new Modifications(gitRepo.latestModifications()).latestRevision(gitMaterial)));
        Assert.assertThat(revisions.toString(), revisions.totalNumberOfModifications(), Matchers.is(3));
    }
}

