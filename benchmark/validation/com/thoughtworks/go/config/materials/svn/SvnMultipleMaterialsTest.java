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
package com.thoughtworks.go.config.materials.svn;


import com.thoughtworks.go.config.MaterialRevisionsMatchers;
import com.thoughtworks.go.config.materials.Materials;
import com.thoughtworks.go.domain.MaterialRevision;
import com.thoughtworks.go.domain.MaterialRevisions;
import com.thoughtworks.go.helper.SvnTestRepo;
import com.thoughtworks.go.util.ArtifactLogUtil;
import com.thoughtworks.go.util.command.ProcessOutputStreamConsumer;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class SvnMultipleMaterialsTest {
    private SvnTestRepo repo;

    private File pipelineDir;

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldNotThrowNPEIfTheWorkingDirectoryIsEmpty() throws Exception {
        SvnMaterial svnMaterial1 = repo.createMaterial("multiple-materials/trunk/part1", "part1");
        SvnMaterial svnMaterial2 = repo.createMaterial("multiple-materials/trunk/part2", "part2");
        Materials materials = new Materials(svnMaterial1, svnMaterial2);
        Revision revision = latestRevision(svnMaterial1, pipelineDir, new TestSubprocessExecutionContext());
        updateMaterials(materials, revision);
        FileUtils.deleteQuietly(pipelineDir);
        updateMaterials(materials, revision);
    }

    @Test
    public void shouldClearOutAllDirectoriesIfMaterialsChange() throws Exception {
        SvnMaterial part1 = repo.createMaterial("multiple-materials/trunk/part1", "part1");
        SvnMaterial part2 = repo.createMaterial("multiple-materials/trunk/part2", "part2");
        Materials materials = new Materials(part1, part2);
        Revision revision = latestRevision(part1, pipelineDir, new TestSubprocessExecutionContext());
        updateMaterials(materials, revision);
        Assert.assertThat(new File(pipelineDir, "part1").exists(), Matchers.is(true));
        Assert.assertThat(new File(pipelineDir, "part2").exists(), Matchers.is(true));
        SvnMaterial newFolder = repo.createMaterial("multiple-materials/trunk/part2", "newFolder");
        Materials changedMaterials = new Materials(part1, newFolder);
        updateMaterials(changedMaterials, revision);
        Assert.assertThat(new File(pipelineDir, "part1").exists(), Matchers.is(true));
        Assert.assertThat(new File(pipelineDir, "newFolder").exists(), Matchers.is(true));
    }

    @Test
    public void shouldClearOutAllDirectoriesIfMaterialIsDeleted() throws Exception {
        SvnMaterial part1 = repo.createMaterial("multiple-materials/trunk/part1", "part1");
        SvnMaterial part2 = repo.createMaterial("multiple-materials/trunk/part2", "part2");
        Materials materials = new Materials(part1, part2);
        Revision revision = latestRevision(part1, pipelineDir, new TestSubprocessExecutionContext());
        updateMaterials(materials, revision);
        Assert.assertThat(new File(pipelineDir, "part1").exists(), Matchers.is(true));
        Assert.assertThat(new File(pipelineDir, "part2").exists(), Matchers.is(true));
        Materials changedMaterials = new Materials(part1);
        updateMaterials(changedMaterials, revision);
        Assert.assertThat(new File(pipelineDir, "part1").exists(), Matchers.is(true));
        Assert.assertThat(new File(pipelineDir, "part2").exists(), Matchers.is(false));
    }

    @Test
    public void shouldNotDeleteDirectoriesWhenThereIsACruiseOutputDirectory() throws Exception {
        SvnMaterial svnMaterial1 = repo.createMaterial("multiple-materials/trunk/part1", "part1");
        SvnMaterial svnMaterial2 = repo.createMaterial("multiple-materials/trunk/part2", "part2");
        Materials materials = new Materials(svnMaterial1, svnMaterial2);
        Revision revision = latestRevision(svnMaterial1, pipelineDir, new TestSubprocessExecutionContext());
        updateMaterials(materials, revision);
        Assert.assertThat(new File(pipelineDir, "part1").exists(), Matchers.is(true));
        Assert.assertThat(new File(pipelineDir, "part2").exists(), Matchers.is(true));
        File testFile = new File(pipelineDir, "part1/test-file");
        testFile.createNewFile();
        Assert.assertThat(testFile.exists(), Matchers.is(true));
        // simulates what a build will do
        new File(pipelineDir, ArtifactLogUtil.CRUISE_OUTPUT_FOLDER).mkdir();
        Assert.assertThat(pipelineDir.listFiles().length, Matchers.is(3));
        updateMaterials(materials, revision);
        Assert.assertThat(new File(pipelineDir, "part1").exists(), Matchers.is(true));
        Assert.assertThat(new File(pipelineDir, "part2").exists(), Matchers.is(true));
        Assert.assertThat("Should not delete the part1 directory", testFile.exists(), Matchers.is(true));
    }

    @Test
    public void shouldNotDeleteWorkingDirIfThereAreNoRequiredFolders() throws Exception {
        SvnMaterial part = repo.createMaterial("multiple-materials/trunk/part1", null);
        Materials materials = new Materials(part);
        Revision revision = latestRevision(part, pipelineDir, new TestSubprocessExecutionContext());
        updateMaterials(materials, revision);
        File shouldNotCleanUp = new File(pipelineDir, "shouldNotDelete");
        shouldNotCleanUp.createNewFile();
        Assert.assertThat(shouldNotCleanUp.exists(), Matchers.is(true));
        updateMaterials(materials, revision);
        Assert.assertThat("should not clean up working dir for this pipeline if none of the materials specified a sub folder", shouldNotCleanUp.exists(), Matchers.is(true));
    }

    // This is bug #2320 - Cruise doing full checkouts most times
    @Test
    public void shouldNotDeleteWorkingDirIfMaterialsAreCheckedOutToSubfoldersWithTheSameRootBug2320() throws Exception {
        SvnMaterial svnMaterial1 = repo.createMaterial("multiple-materials/trunk/part1", "root/part1");
        SvnMaterial svnMaterial2 = repo.createMaterial("multiple-materials/trunk/part2", "root/part2");
        Materials materials = new Materials(svnMaterial1, svnMaterial2);
        Revision revision = latestRevision(svnMaterial1, pipelineDir, new TestSubprocessExecutionContext());
        updateMaterials(materials, revision);
        Assert.assertThat(new File(pipelineDir, "root/part1").exists(), Matchers.is(true));
        Assert.assertThat(new File(pipelineDir, "root/part2").exists(), Matchers.is(true));
        File testFile = new File(pipelineDir, "root/part1/test-file");
        testFile.createNewFile();
        Assert.assertThat(testFile.exists(), Matchers.is(true));
        // simulates what a build will do
        new File(pipelineDir, ArtifactLogUtil.CRUISE_OUTPUT_FOLDER).mkdir();
        Assert.assertThat(pipelineDir.listFiles().length, Matchers.is(2));
        updateMaterials(materials, revision);
        Assert.assertThat(new File(pipelineDir, "root/part1").exists(), Matchers.is(true));
        Assert.assertThat(new File(pipelineDir, "root/part2").exists(), Matchers.is(true));
        Assert.assertThat("Should not delete the part1 directory", testFile.exists(), Matchers.is(true));
    }

    @Test
    public void shouldDetectLatestModifications() throws Exception {
        SvnMaterial svnMaterial1 = repo.createMaterial("multiple-materials/trunk/part1", "part1");
        SvnMaterial svnMaterial2 = repo.createMaterial("multiple-materials/trunk/part2", "part2");
        Materials materials = new Materials(svnMaterial1, svnMaterial2);
        MaterialRevisions materialRevisions = materials.latestModification(pipelineDir, new TestSubprocessExecutionContext());
        MaterialRevision revision1 = materialRevisions.getMaterialRevision(0);
        Assert.assertThat(revision1.getRevision(), Matchers.is(latestRevision(svnMaterial1, pipelineDir, new TestSubprocessExecutionContext())));
        MaterialRevision revision2 = materialRevisions.getMaterialRevision(1);
        Assert.assertThat(revision2.getRevision(), Matchers.is(latestRevision(svnMaterial2, pipelineDir, new TestSubprocessExecutionContext())));
    }

    @Test
    public void shouldUpdateMaterialToItsDestFolder() throws Exception {
        SvnMaterial material1 = repo.createMaterial("multiple-materials/trunk/part1", "part1");
        MaterialRevision materialRevision = new MaterialRevision(material1, material1.latestModification(pipelineDir, new TestSubprocessExecutionContext()));
        materialRevision.updateTo(pipelineDir, ProcessOutputStreamConsumer.inMemoryConsumer(), new TestSubprocessExecutionContext());
        Assert.assertThat(new File(pipelineDir, "part1").exists(), Matchers.is(true));
    }

    @Test
    public void shouldIgnoreDestinationFolderWhenServerSide() throws Exception {
        SvnMaterial material1 = repo.createMaterial("multiple-materials/trunk/part1", "part1");
        MaterialRevision materialRevision = new MaterialRevision(material1, material1.latestModification(pipelineDir, new TestSubprocessExecutionContext()));
        materialRevision.updateTo(pipelineDir, ProcessOutputStreamConsumer.inMemoryConsumer(), new TestSubprocessExecutionContext(true));
        Assert.assertThat(new File(pipelineDir, "part1").exists(), Matchers.is(false));
    }

    @Test
    public void shouldFindModificationForEachMaterial() throws Exception {
        SvnMaterial material1 = repo.createMaterial("multiple-materials/trunk/part1", "part1");
        SvnMaterial material2 = repo.createMaterial("multiple-materials/trunk/part2", "part2");
        Materials materials = new Materials(material1, material2);
        repo.checkInOneFile("filename.txt", material1);
        repo.checkInOneFile("filename2.txt", material2);
        MaterialRevisions materialRevisions = materials.latestModification(pipelineDir, new TestSubprocessExecutionContext());
        Assert.assertThat(materialRevisions.getRevisions().size(), Matchers.is(2));
        Assert.assertThat(materialRevisions, MaterialRevisionsMatchers.containsModifiedFile("/trunk/part1/filename.txt"));
        Assert.assertThat(materialRevisions, MaterialRevisionsMatchers.containsModifiedFile("/trunk/part2/filename2.txt"));
    }
}

