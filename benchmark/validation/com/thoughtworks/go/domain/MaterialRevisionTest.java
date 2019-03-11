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
package com.thoughtworks.go.domain;


import ModificationsMother.TODAY_CHECKIN;
import com.thoughtworks.go.config.CaseInsensitiveString;
import com.thoughtworks.go.config.materials.Filter;
import com.thoughtworks.go.config.materials.IgnoredFiles;
import com.thoughtworks.go.config.materials.ScmMaterialConfig;
import com.thoughtworks.go.config.materials.dependency.DependencyMaterial;
import com.thoughtworks.go.config.materials.mercurial.HgMaterial;
import com.thoughtworks.go.config.materials.svn.SvnMaterial;
import com.thoughtworks.go.domain.materials.dependency.DependencyMaterialRevision;
import com.thoughtworks.go.domain.materials.mercurial.StringRevision;
import java.io.File;
import java.util.Date;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class MaterialRevisionTest {
    private static final StringRevision REVISION_0 = new StringRevision("b61d12de515d82d3a377ae3aae6e8abe516a2651");

    private static final StringRevision REVISION_2 = new StringRevision("ca3ebb67f527c0ad7ed26b789056823d8b9af23f");

    private HgMaterial hgMaterial;

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File workingFolder;

    @Test
    public void shouldGetModifiedTimeFromTheLatestModification() throws Exception {
        final MaterialRevision materialRevision = new MaterialRevision(MaterialsMother.hgMaterial(), multipleModificationsInHg());
        Assert.assertThat(materialRevision.getDateOfLatestModification(), Matchers.is(TODAY_CHECKIN));
    }

    @Test
    public void shouldDetectChangesAfterACheckin() throws Exception {
        MaterialRevision original = new MaterialRevision(hgMaterial, hgMaterial.modificationsSince(workingFolder, MaterialRevisionTest.REVISION_0, new TestSubprocessExecutionContext()));
        checkInOneFile(hgMaterial);
        checkInOneFile(hgMaterial);
        checkInOneFile(hgMaterial);
        final MaterialRevision after = findNewRevision(original, hgMaterial, workingFolder, new TestSubprocessExecutionContext());
        Assert.assertThat(after, Matchers.not(original));
        Assert.assertThat(after.numberOfModifications(), Matchers.is(3));
        Assert.assertThat(after.getRevision(), Matchers.is(Matchers.not(original.getRevision())));
        Assert.assertThat(after.hasChangedSince(original), Matchers.is(true));
    }

    @Test
    public void shouldMarkRevisionAsChanged() throws Exception {
        MaterialRevision original = new MaterialRevision(hgMaterial, hgMaterial.modificationsSince(workingFolder, MaterialRevisionTest.REVISION_0, new TestSubprocessExecutionContext()));
        checkInFiles(hgMaterial, "user.doc");
        MaterialRevision newRev = findNewRevision(original, hgMaterial, workingFolder, new TestSubprocessExecutionContext());
        Assert.assertThat(newRev.isChanged(), Matchers.is(true));
    }

    @Test
    public void shouldMarkRevisionAsNotChanged() throws Exception {
        List<Modification> modifications = hgMaterial.latestModification(workingFolder, new TestSubprocessExecutionContext());
        MaterialRevision original = new MaterialRevision(hgMaterial, modifications);
        checkInFiles(hgMaterial, "user.doc");
        original = findNewRevision(original, hgMaterial, workingFolder, new TestSubprocessExecutionContext());
        MaterialRevision newRev = findNewRevision(original, hgMaterial, workingFolder, new TestSubprocessExecutionContext());
        Assert.assertThat(newRev.isChanged(), Matchers.is(false));
    }

    @Test
    public void shouldIgnoreDocumentCheckin() throws Exception {
        MaterialRevision previousRevision = new MaterialRevision(hgMaterial, hgMaterial.modificationsSince(workingFolder, MaterialRevisionTest.REVISION_0, new TestSubprocessExecutionContext()));
        Filter filter = new Filter(new IgnoredFiles("**/*.doc"));
        hgMaterial.setFilter(filter);
        checkInFiles(hgMaterial, "user.doc");
        MaterialRevision newRevision = findNewRevision(previousRevision, hgMaterial, workingFolder, new TestSubprocessExecutionContext());
        Assert.assertThat(newRevision.filter(previousRevision), Matchers.is(previousRevision));
    }

    @Test
    public void shouldIgnoreDocumentWhenCheckin() throws Exception {
        MaterialRevision original = new MaterialRevision(hgMaterial, hgMaterial.modificationsSince(workingFolder, MaterialRevisionTest.REVISION_0, new TestSubprocessExecutionContext()));
        Filter filter = new Filter(new IgnoredFiles("helper/**/*.*"));
        hgMaterial.setFilter(filter);
        checkInFiles(hgMaterial, "helper/topics/installing_go_agent.xml", "helper/topics/installing_go_server.xml");
        MaterialRevision newRev = findNewRevision(original, hgMaterial, workingFolder, new TestSubprocessExecutionContext());
        Assert.assertThat(newRev.filter(original), Matchers.is(original));
    }

    @Test
    public void shouldIgnoreDocumentsWithSemanticallyEqualsIgnoreFilter() throws Exception {
        MaterialRevision original = new MaterialRevision(hgMaterial, hgMaterial.modificationsSince(workingFolder, MaterialRevisionTest.REVISION_0, new TestSubprocessExecutionContext()));
        Filter filter = new Filter(new IgnoredFiles("**/*.doc"), new IgnoredFiles("*.doc"));
        hgMaterial.setFilter(filter);
        checkInFiles(hgMaterial, "user.doc");
        MaterialRevision newRev = findNewRevision(original, hgMaterial, workingFolder, new TestSubprocessExecutionContext());
        Assert.assertThat(newRev.filter(original), Matchers.is(original));
    }

    @Test
    public void shouldIncludeJavaFileWithSemanticallyEqualsIgnoreFilter() throws Exception {
        MaterialRevision original = new MaterialRevision(hgMaterial, hgMaterial.modificationsSince(workingFolder, MaterialRevisionTest.REVISION_0, new TestSubprocessExecutionContext()));
        Filter filter = new Filter(new IgnoredFiles("**/*.doc"), new IgnoredFiles("*.doc"));
        GoConfigMother.createPipelineConfig(filter, ((ScmMaterialConfig) (hgMaterial.config())));
        checkInFiles(hgMaterial, "A.java");
        checkInFiles(hgMaterial, "B.doc");
        checkInFiles(hgMaterial, "C.pdf");
        MaterialRevision newRev = findNewRevision(original, hgMaterial, workingFolder, new TestSubprocessExecutionContext());
        Assert.assertThat(newRev.filter(original), Matchers.is(newRev));
    }

    @Test
    public void shouldNotIgnoreJavaFile() throws Exception {
        MaterialRevision original = new MaterialRevision(hgMaterial, hgMaterial.modificationsSince(workingFolder, MaterialRevisionTest.REVISION_0, new TestSubprocessExecutionContext()));
        Filter filter = new Filter(new IgnoredFiles("**/*.doc"));
        GoConfigMother.createPipelineConfig(filter, ((ScmMaterialConfig) (hgMaterial.config())));
        checkInFiles(hgMaterial, "A.java");
        MaterialRevision newRev = findNewRevision(original, hgMaterial, workingFolder, new TestSubprocessExecutionContext());
        Assert.assertThat(newRev.filter(original), Matchers.is(newRev));
    }

    @Test
    public void shouldNotIgnoreAnyFileIfFilterIsNotDefinedForTheGivenMaterial() throws Exception {
        MaterialRevision original = new MaterialRevision(hgMaterial, hgMaterial.modificationsSince(workingFolder, MaterialRevisionTest.REVISION_0, new TestSubprocessExecutionContext()));
        Filter filter = new Filter();
        GoConfigMother.createPipelineConfig(filter, ((ScmMaterialConfig) (hgMaterial.config())));
        checkInFiles(hgMaterial, "A.java");
        MaterialRevision newRev = findNewRevision(original, hgMaterial, workingFolder, new TestSubprocessExecutionContext());
        Assert.assertThat(newRev.filter(original), Matchers.is(newRev));
    }

    @Test
    public void shouldMarkRevisionChangeFalseIfNoNewChangesAvailable() throws Exception {
        Modification modificationForRevisionTip = new Modification(new Date(), MaterialRevisionTest.REVISION_2.getRevision(), "MOCK_LABEL-12", null);
        MaterialRevision revision = new MaterialRevision(hgMaterial, modificationForRevisionTip);
        MaterialRevision unchangedRevision = findNewRevision(revision, hgMaterial, workingFolder, new TestSubprocessExecutionContext());
        Assert.assertThat(unchangedRevision.isChanged(), Matchers.is(false));
    }

    @Test
    public void shouldReturnOnlyLatestModificationIfNoNewChangesAvailable() throws Exception {
        Modification modificationForRevisionTip = new Modification("Unknown", "Unknown", null, new Date(), MaterialRevisionTest.REVISION_2.getRevision());
        Modification olderModification = new Modification("Unknown", "Unknown", null, new Date(), MaterialRevisionTest.REVISION_0.getRevision());
        MaterialRevision revision = new MaterialRevision(hgMaterial, modificationForRevisionTip, olderModification);
        MaterialRevision unchangedRevision = findNewRevision(revision, hgMaterial, workingFolder, new TestSubprocessExecutionContext());
        Assert.assertThat(unchangedRevision.getModifications().size(), Matchers.is(1));
        Assert.assertThat(unchangedRevision.getModifications().get(0), Matchers.is(modificationForRevisionTip));
    }

    @Test
    public void shouldNotConsiderChangedFlagAsPartOfEqualityAndHashCodeCheck() {
        Modification modification = oneModifiedFile("revision1");
        SvnMaterial material = MaterialsMother.svnMaterial();
        MaterialRevision notChanged = new MaterialRevision(material, false, modification);
        MaterialRevision changed = new MaterialRevision(material, true, modification);
        changed.markAsChanged();
        Assert.assertThat(changed, Matchers.is(notChanged));
        Assert.assertThat(changed.hashCode(), Matchers.is(notChanged.hashCode()));
    }

    @Test
    public void shouldDetectChangedRevision() {
        Modification modification1 = oneModifiedFile("revision1");
        Modification modification2 = oneModifiedFile("revision2");
        SvnMaterial material = MaterialsMother.svnMaterial();
        MaterialRevision materialRevision1 = new MaterialRevision(material, modification1);
        MaterialRevision materialRevision2 = new MaterialRevision(material, modification2);
        Assert.assertThat(materialRevision1.hasChangedSince(materialRevision2), Matchers.is(true));
    }

    @Test
    public void shouldDisplayRevisionAsBuildCausedByForDependencyMaterial() {
        DependencyMaterial dependencyMaterial = new DependencyMaterial(new CaseInsensitiveString("upstream"), new CaseInsensitiveString("stage"));
        MaterialRevision materialRevision = new MaterialRevision(dependencyMaterial, new Modification(new Date(), "upstream/2/stage/1", "1.3-2", null));
        Assert.assertThat(materialRevision.buildCausedBy(), Matchers.is("upstream/2/stage/1"));
    }

    @Test
    public void shouldUseLatestMaterial() throws Exception {
        MaterialRevision original = new MaterialRevision(hgMaterial, hgMaterial.modificationsSince(workingFolder, MaterialRevisionTest.REVISION_0, new TestSubprocessExecutionContext()));
        HgMaterial newMaterial = MaterialsMother.hgMaterial(hgMaterial.getUrl());
        newMaterial.setFilter(new Filter(new IgnoredFiles("**/*.txt")));
        final MaterialRevision after = findNewRevision(original, newMaterial, workingFolder, new TestSubprocessExecutionContext());
        Assert.assertThat(after.getMaterial(), Matchers.is(newMaterial));
    }

    @Test
    public void shouldDetectLatestAndOldestModification() throws Exception {
        MaterialRevision materialRevision = new MaterialRevision(hgMaterial, modification("3"), modification("2"), modification("1"));
        Assert.assertThat(materialRevision.getLatestModification(), Matchers.is(modification("3")));
        Assert.assertThat(materialRevision.getOldestModification(), Matchers.is(modification("1")));
    }

    @Test
    public void shouldDetectLatestRevision() throws Exception {
        MaterialRevision materialRevision = new MaterialRevision(hgMaterial, modification("3"), modification("2"), modification("1"));
        Assert.assertThat(materialRevision.getRevision(), Matchers.is(new StringRevision("3")));
    }

    @Test
    public void shouldDetectOldestScmRevision() throws Exception {
        MaterialRevision materialRevision = new MaterialRevision(hgMaterial, modification("3"), modification("2"), modification("1"));
        Assert.assertThat(materialRevision.getOldestRevision(), Matchers.is(new StringRevision("1")));
    }

    @Test
    public void shouldDetectOldestAndLatestDependencyRevision() throws Exception {
        DependencyMaterial dependencyMaterial = new DependencyMaterial(new CaseInsensitiveString("upstream"), new CaseInsensitiveString("stage"));
        MaterialRevision materialRevision = new MaterialRevision(dependencyMaterial, new Modification(new Date(), "upstream/3/stage/1", "1.3-3", null), new Modification(new Date(), "upstream/2/stage/1", "1.3-2", null));
        Assert.assertThat(materialRevision.getOldestRevision(), Matchers.is(DependencyMaterialRevision.create("upstream/2/stage/1", "1.3-2")));
        Assert.assertThat(materialRevision.getRevision(), Matchers.is(DependencyMaterialRevision.create("upstream/3/stage/1", "1.3-3")));
    }

    @Test
    public void shouldReturnNullRevisionWhenThereIsNoMaterial() throws Exception {
        Revision revision = new MaterialRevision(null).getRevision();
        Assert.assertThat(revision, Matchers.is(Matchers.not(Matchers.nullValue())));
        Assert.assertThat(revision.getRevision(), Matchers.is(""));
    }

    @Test
    public void shouldReturnFullRevisionForTheLatestModification() throws Exception {
        Assert.assertThat(hgRevision().getLatestRevisionString(), Matchers.is("012345678901234567890123456789"));
    }

    @Test
    public void shouldReturnShortRevisionForTheLatestModification() throws Exception {
        Assert.assertThat(hgRevision().getLatestShortRevision(), Matchers.is("012345678901"));
    }

    @Test
    public void shouldReturnMaterialName() throws Exception {
        Assert.assertThat(hgRevision().getMaterialName(), Matchers.is(hgMaterial.getDisplayName()));
    }

    @Test
    public void shouldReturnTruncatedMaterialName() throws Exception {
        Assert.assertThat(hgRevision().getTruncatedMaterialName(), Matchers.is(hgMaterial.getTruncatedDisplayName()));
    }

    @Test
    public void shouldReturnMaterialType() throws Exception {
        Assert.assertThat(hgRevision().getMaterialType(), Matchers.is("Mercurial"));
    }

    @Test
    public void shouldReturnLatestComments() throws Exception {
        Assert.assertThat(hgRevision().getLatestComment(), Matchers.is("Checkin 012345678901234567890123456789"));
    }

    @Test
    public void shouldReturnLatestUser() throws Exception {
        Assert.assertThat(hgRevision().getLatestUser(), Matchers.is("user"));
    }

    @Test
    public void shouldRecomputeIsChanged() throws Exception {
        MaterialRevision original = createHgMaterialWithMultipleRevisions(1, oneModifiedFile("rev2"), oneModifiedFile("rev1")).getMaterialRevision(0);
        MaterialRevision recomputed = createHgMaterialWithMultipleRevisions(1, oneModifiedFile("rev1")).getMaterialRevision(0);
        MaterialRevision recomputedAnother = createHgMaterialWithMultipleRevisions(1, oneModifiedFile("rev0")).getMaterialRevision(0);
        recomputed.updateRevisionChangedStatus(original);
        recomputedAnother.updateRevisionChangedStatus(original);
        Assert.assertThat(recomputed.isChanged(), Matchers.is(false));
        Assert.assertThat(recomputedAnother.isChanged(), Matchers.is(false));
        original.markAsChanged();
        recomputed.updateRevisionChangedStatus(original);
        recomputedAnother.updateRevisionChangedStatus(original);
        Assert.assertThat(recomputed.isChanged(), Matchers.is(true));
        Assert.assertThat(recomputedAnother.isChanged(), Matchers.is(false));
    }

    @Test
    public void shouldRemoveFromThisWhateverModificationIsPresentInThePassedInRevision() throws Exception {
        MaterialRevision revision = createHgMaterialWithMultipleRevisions(1, oneModifiedFile("rev2"), oneModifiedFile("rev1")).getMaterialRevision(0);
        MaterialRevision passedIn = createHgMaterialWithMultipleRevisions(1, oneModifiedFile("rev1")).getMaterialRevision(0);
        MaterialRevision expected = createHgMaterialWithMultipleRevisions(1, oneModifiedFile("rev2")).getMaterialRevision(0);
        Assert.assertThat(revision.subtract(passedIn), Matchers.is(expected));
    }

    @Test
    public void shouldReturnCurrentIfThePassedInDoesNotHaveAnythingThatCurrentHas() throws Exception {
        MaterialRevision revision = createHgMaterialWithMultipleRevisions(1, oneModifiedFile("rev2")).getMaterialRevision(0);
        MaterialRevision passedIn = createHgMaterialWithMultipleRevisions(1, oneModifiedFile("rev1")).getMaterialRevision(0);
        MaterialRevision expected = createHgMaterialWithMultipleRevisions(1, oneModifiedFile("rev2")).getMaterialRevision(0);
        Assert.assertThat(revision.subtract(passedIn), Matchers.is(expected));
    }
}

