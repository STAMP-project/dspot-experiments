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
import com.thoughtworks.go.config.materials.Materials;
import com.thoughtworks.go.config.materials.svn.SvnMaterial;
import com.thoughtworks.go.domain.materials.Modification;
import com.thoughtworks.go.helper.MaterialsMother;
import com.thoughtworks.go.helper.ModificationsMother;
import com.thoughtworks.go.util.command.EnvironmentVariableContext;
import java.util.Date;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class MaterialRevisionsTest {
    private Modification yesterdayMod;

    private Modification oneHourAgoMod;

    private Modification nowMod;

    private SvnMaterial material;

    private static final String MOD_USER_COMMITTER = "committer";

    private static final Filter FILTER_DOC_PDF = Filter.create("*.doc", "*.pdf");

    @Test
    public void shouldReturnModificationsForAGivenMaterial() {
        MaterialRevisions oneMaterialRevision = new MaterialRevisions(new MaterialRevision(material, yesterdayMod));
        Assert.assertThat(oneMaterialRevision.getModifications(material), Matchers.is(new com.thoughtworks.go.domain.materials.Modifications(yesterdayMod)));
        MaterialRevisions emptyMaterialRevision = new MaterialRevisions();
        Assert.assertThat(emptyMaterialRevision.getModifications(material).isEmpty(), Matchers.is(true));
        MaterialRevisions differentMaterialRevision = new MaterialRevisions(new MaterialRevision(MaterialsMother.hgMaterial(), yesterdayMod));
        Assert.assertThat(differentMaterialRevision.getModifications(material).isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldKnowDateOfLatestModification() {
        // modifications are ordered, the first modification is the latest one
        MaterialRevisions materialRevisions = new MaterialRevisions(svnMaterialRevision(yesterdayMod, oneHourAgoMod));
        Assert.assertThat(materialRevisions.getDateOfLatestModification(), Matchers.is(yesterdayMod.getModifiedTime()));
    }

    @Test
    public void shouldReturnMostRecentModifiedTimeIfExist() {
        MaterialRevisions materialRevisions = multipleModifications();
        Assert.assertThat(materialRevisions.getDateOfLatestModification(), Matchers.is(TODAY_CHECKIN));
    }

    @Test
    public void shouldReturnNullIfMostRecentModifiedTimeNotExist() {
        MaterialRevisions materialRevisions = empty();
        Assert.assertThat(materialRevisions.getDateOfLatestModification(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldReturnOrginalChangeSet() {
        MaterialRevisions first = new MaterialRevisions(svnMaterialRevision("folder1", MaterialRevisionsTest.FILTER_DOC_PDF, aCheckIn("99", "/a.java")), svnMaterialRevision("folder2", MaterialRevisionsTest.FILTER_DOC_PDF, aCheckIn("99", "/b.java")));
        MaterialRevisions second = new MaterialRevisions(svnMaterialRevision("folder1", MaterialRevisionsTest.FILTER_DOC_PDF, aCheckIn("100", "/a.doc"), aCheckIn("100", "/b.doc")), svnMaterialRevision("folder2", MaterialRevisionsTest.FILTER_DOC_PDF, aCheckIn("100", "/a.pdf")));
        Assert.assertThat(second.hasChangedSince(first), Matchers.is(false));
    }

    @Test
    public void shouldReturnLatestChangeSetForDependencyMaterial() {
        Materials materials = new Materials(MaterialsMother.dependencyMaterial());
        MaterialRevisions original = modifyOneFile(materials, "1");
        MaterialRevisions newRevisions = modifyOneFile(materials, "2");
        Assert.assertThat(newRevisions.hasChangedSince(original), Matchers.is(true));
    }

    // #3122
    @Test
    public void shouldNotIgnoreChangesInUpstreamPipeline() {
        MaterialRevisions original = new MaterialRevisions(dependencyMaterialRevision("cruise", 365, "1.3-365", "dist-zip", 1, new Date()));
        MaterialRevisions current = new MaterialRevisions(dependencyMaterialRevision("cruise", 370, "1.3-370", "dist-zip", 1, new Date()));
        Assert.assertThat(current.hasChangedSince(original), Matchers.is(true));
    }

    @Test
    public void shouldReturnLatestChangeSetIfAnyChangeSetShouldNotIgnore() {
        MaterialRevisions first = new MaterialRevisions(svnMaterialRevision("folder1", MaterialRevisionsTest.FILTER_DOC_PDF, aCheckIn("99", "/b.java")), svnMaterialRevision("folder2", MaterialRevisionsTest.FILTER_DOC_PDF, aCheckIn("5", "/a.html")));
        MaterialRevisions second = new MaterialRevisions(svnMaterialRevision("folder1", MaterialRevisionsTest.FILTER_DOC_PDF, aCheckIn("100", "/a.doc"), aCheckIn("100", "/b.doc")), svnMaterialRevision("folder2", MaterialRevisionsTest.FILTER_DOC_PDF, aCheckIn("5", "/a.html")));
        Assert.assertThat(second.hasChangedSince(first), Matchers.is(false));
    }

    @Test
    public void shouldBeAbleToDetectWhenMaterialRevisionsHaveChanged() {
        MaterialRevisions first = new MaterialRevisions(svnMaterialRevision(oneHourAgoMod, yesterdayMod));
        MaterialRevisions second = new MaterialRevisions(svnMaterialRevision(nowMod));
        Assert.assertThat(second.hasChangedSince(first), Matchers.is(true));
    }

    @Test
    public void shouldBeAbleToDetectWhenMaterialsDontMatch() {
        MaterialRevisions first = new MaterialRevisions(svnMaterialRevision("1", MaterialRevisionsTest.FILTER_DOC_PDF, oneHourAgoMod, yesterdayMod), svnMaterialRevision("2", MaterialRevisionsTest.FILTER_DOC_PDF, nowMod));
        MaterialRevisions second = new MaterialRevisions(svnMaterialRevision("1", MaterialRevisionsTest.FILTER_DOC_PDF, oneHourAgoMod, yesterdayMod), svnMaterialRevision("3", MaterialRevisionsTest.FILTER_DOC_PDF, nowMod));
        Assert.assertThat(second.hasChangedSince(first), Matchers.is(true));
    }

    @Test
    public void shouldBeAbleToDetectWhenThereAreMultipleMaterials() {
        MaterialRevisions first = new MaterialRevisions(svnMaterialRevision(oneHourAgoMod, yesterdayMod));
        MaterialRevisions second = new MaterialRevisions(svnMaterialRevision(oneHourAgoMod, yesterdayMod), svnMaterialRevision(nowMod));
        Assert.assertThat(second.hasChangedSince(first), Matchers.is(true));
    }

    @Test
    public void shouldBeAbleToDetectWhenThereAreMultipleMaterialsInADifferentOrder() {
        MaterialRevisions first = new MaterialRevisions(svnMaterialRevision("1", MaterialRevisionsTest.FILTER_DOC_PDF, oneHourAgoMod, yesterdayMod), svnMaterialRevision("2", MaterialRevisionsTest.FILTER_DOC_PDF, nowMod));
        MaterialRevisions second = new MaterialRevisions(svnMaterialRevision("2", MaterialRevisionsTest.FILTER_DOC_PDF, nowMod), svnMaterialRevision("1", MaterialRevisionsTest.FILTER_DOC_PDF, oneHourAgoMod, yesterdayMod));
        Assert.assertThat(second.hasChangedSince(first), Matchers.is(false));
    }

    @Test
    public void shouldNotBeAbleToAddANullModification() {
        MaterialRevisions materialRevisions = empty();
        try {
            materialRevisions.addRevision(MaterialsMother.svnMaterial(), ((Modification) (null)));
            Assert.fail("Should not be able to add a null modification");
        } catch (Exception ignored) {
        }
    }

    @Test
    public void shouldMatchByComment() {
        MaterialRevisions materialRevisions = new MaterialRevisions(svnMaterialRevision(aCheckIn("100", "README")));
        Assert.assertThat(materialRevisions.containsMyCheckin(new Matcher("readme")), Matchers.is(false));
    }

    @Test
    public void shouldMatchCommentWhenCaseAreSame() {
        MaterialRevisions materialRevisions = new MaterialRevisions(svnMaterialRevision(aCheckIn("100", "README")));
        Assert.assertThat(materialRevisions.containsMyCheckin(new Matcher("README")), Matchers.is(true));
    }

    @Test
    public void shouldMatchMultiLineComment() {
        MaterialRevisions materialRevisions = new MaterialRevisions(svnMaterialRevision(checkinWithComment("100", "Line1\nLine2\nLine3", "Committer1", EMAIL_ADDRESS, TODAY_CHECKIN, "README")));
        Assert.assertThat(materialRevisions.containsMyCheckin(new Matcher("Committer1")), Matchers.is(true));
        Assert.assertThat(materialRevisions.containsMyCheckin(new Matcher("Line1")), Matchers.is(true));
    }

    @Test
    public void shouldMatchByUserName() {
        MaterialRevisions materialRevisions = new MaterialRevisions(svnMaterialRevision(aCheckIn("100", "README")));
        Assert.assertThat(materialRevisions.containsMyCheckin(new Matcher(MaterialRevisionsTest.MOD_USER_COMMITTER)), Matchers.is(true));
    }

    @Test
    public void shouldNotMatchWhenUserNameDoesNotMatch() {
        MaterialRevisions materialRevisions = new MaterialRevisions(svnMaterialRevision(aCheckIn("100", "README")));
        Assert.assertThat(materialRevisions.containsMyCheckin(new Matcher("someone")), Matchers.is(false));
    }

    @Test
    public void shouldNotMatchWhenMatcherIsNull() throws Exception {
        MaterialRevisions materialRevisions = new MaterialRevisions(svnMaterialRevision(aCheckIn("100", "README")));
        Assert.assertThat(materialRevisions.containsMyCheckin(new Matcher(((String) (null)))), Matchers.is(false));
    }

    @Test
    public void shouldReturnTrueIfOneUsernameMatchesInMutipleModifications() throws Exception {
        MaterialRevisions materialRevisions = multipleModifications();
        Assert.assertThat(materialRevisions.containsMyCheckin(new Matcher(MaterialRevisionsTest.MOD_USER_COMMITTER)), Matchers.is(true));
    }

    @Test
    public void shouldReturnTrueIfUsernameMatchesInOneOfMaterialRevisions() throws Exception {
        MaterialRevisions materialRevisions = new MaterialRevisions(svnMaterialRevision(aCheckIn("100", "test.txt")), svnMaterialRevision(oneModifiedFile("revision")));
        Assert.assertThat(materialRevisions.containsMyCheckin(new Matcher("Fixing")), Matchers.is(true));
    }

    @Test
    public void shouldReturnFalseWhenMatcherIsNotAlphaNumberic() throws Exception {
        MaterialRevisions materialRevisions = multipleModifications();
        Assert.assertThat(materialRevisions.containsMyCheckin(new Matcher("committer.*")), Matchers.is(false));
    }

    @Test
    public void shouldNotMatchMaterialRevisionsWhenEmailMeIsDisabled() throws Exception {
        MaterialRevisions materialRevisions = multipleModifications();
        Assert.assertThat(materialRevisions.containsMyCheckin(new Matcher(MaterialRevisionsTest.MOD_USER_COMMITTER)), Matchers.is(true));
    }

    @Test
    public void shouldBeSameIfHasSameHeads() {
        MaterialRevisions first = new MaterialRevisions(svnMaterialRevision(oneModifiedFile("revision2"), oneModifiedFile("revision1")));
        MaterialRevisions second = new MaterialRevisions(svnMaterialRevision(oneModifiedFile("revision2")));
        Assert.assertThat(first.isSameAs(second), Matchers.is(true));
    }

    @Test
    public void shouldBeTheSameIfCurrentHasNoChanges() {
        MaterialRevisions first = new MaterialRevisions(svnMaterialRevision(oneModifiedFile("svn revision 2")), hgMaterialRevision(oneModifiedFile("hg revision 1")));
        MaterialRevisions second = new MaterialRevisions(svnMaterialRevision(false, oneModifiedFile("svn revision 2")), hgMaterialRevision(false, oneModifiedFile("hg revision 1")));
        Assert.assertThat(first.isSameAs(second), Matchers.is(true));
    }

    @Test
    public void shouldNotBeSameIfOneMaterialRevisionIsNew() throws Exception {
        MaterialRevisions first = new MaterialRevisions(svnMaterialRevision(oneModifiedFile("svn revision 2"), oneModifiedFile("svn revision 1")), hgMaterialRevision(oneModifiedFile("hg revision 1")));
        MaterialRevisions second = new MaterialRevisions(svnMaterialRevision(oneModifiedFile("svn revision 2")), hgMaterialRevision(oneModifiedFile("hg revision 2")));
        Assert.assertThat(first.isSameAs(second), Matchers.is(false));
    }

    @Test
    public void shouldNotBeSameIfOneMaterialRevisionIsNewAndOldOneHadOnlyOneRevision() throws Exception {
        MaterialRevisions first = new MaterialRevisions(svnMaterialRevision(oneModifiedFile("svn revision 2")), hgMaterialRevision(oneModifiedFile("hg revision 1")));
        MaterialRevisions second = new MaterialRevisions(svnMaterialRevision(oneModifiedFile("svn revision 2")), hgMaterialRevision(oneModifiedFile("hg revision 2")));
        Assert.assertThat(first.isSameAs(second), Matchers.is(false));
    }

    @Test
    public void shouldNotBeSameOnNewModification() {
        MaterialRevisions first = new MaterialRevisions(svnMaterialRevision(oneModifiedFile("revision1")));
        MaterialRevisions second = new MaterialRevisions(svnMaterialRevision(oneModifiedFile("revision2")));
        Assert.assertThat(first.isSameAs(second), Matchers.is(false));
    }

    @Test
    public void shouldNotBeSameIfMaterialsAreDifferent() {
        MaterialRevisions first = new MaterialRevisions(svnMaterialRevision("folder1", MaterialRevisionsTest.FILTER_DOC_PDF, oneModifiedFile("revision1")));
        MaterialRevisions second = new MaterialRevisions(svnMaterialRevision("folder2", MaterialRevisionsTest.FILTER_DOC_PDF, oneModifiedFile("revision1")));
        Assert.assertThat(first.isSameAs(second), Matchers.is(false));
    }

    @Test
    public void shouldUseFirstMaterialAsBuildCauseMessage() throws Exception {
        MaterialRevisions materialRevisions = madeChanges(new boolean[]{ true, true }, oneModifiedFile("user1", "svnRev", TWO_DAYS_AGO_CHECKIN), oneModifiedFile("user2", "hgRev", new Date()));
        Assert.assertThat(materialRevisions.buildCauseMessage(), Matchers.is("modified by user1"));
    }

    @Test
    public void shouldUseFirstChangedMaterialAsBuildCauseMessage() throws Exception {
        MaterialRevisions materialRevisions = madeChanges(new boolean[]{ false, true }, oneModifiedFile("user1", "svnRev", TWO_DAYS_AGO_CHECKIN), oneModifiedFile("user2", "hgRev", new Date()));
        Assert.assertThat(materialRevisions.buildCauseMessage(), Matchers.is("modified by user2"));
    }

    @Test
    public void shouldUseFirstMaterialAsbuildCausedBy() throws Exception {
        MaterialRevisions materialRevisions = madeChanges(new boolean[]{ true, true }, oneModifiedFile("user1", "svnRev", TWO_DAYS_AGO_CHECKIN), oneModifiedFile("user2", "hgRev", new Date()));
        Assert.assertThat(materialRevisions.buildCausedBy(), Matchers.is("user1"));
    }

    @Test
    public void shouldUseFirstChangedMaterialAsbuildCausedBy() throws Exception {
        MaterialRevisions materialRevisions = madeChanges(new boolean[]{ false, true }, oneModifiedFile("user1", "svnRev", TWO_DAYS_AGO_CHECKIN), oneModifiedFile("user2", "hgRev", new Date()));
        Assert.assertThat(materialRevisions.buildCausedBy(), Matchers.is("user2"));
    }

    @Test
    public void shouldUseFirstMaterialAsbuildDate() throws Exception {
        MaterialRevisions materialRevisions = madeChanges(new boolean[]{ true, true }, oneModifiedFile("user1", "svnRev", TWO_DAYS_AGO_CHECKIN), oneModifiedFile("user2", "hgRev", new Date()));
        Assert.assertThat(materialRevisions.getDateOfLatestModification(), Matchers.is(TWO_DAYS_AGO_CHECKIN));
    }

    @Test
    public void shouldUseFirstChangedMaterialAsDate() throws Exception {
        Date now = new Date();
        MaterialRevisions materialRevisions = madeChanges(new boolean[]{ false, true }, oneModifiedFile("user1", "svnRev", TWO_DAYS_AGO_CHECKIN), oneModifiedFile("user2", "hgRev", now));
        Assert.assertThat(materialRevisions.getDateOfLatestModification(), Matchers.is(now));
    }

    @Test
    public void shouldUseFirstMaterialAsLatestRevision() throws Exception {
        MaterialRevisions materialRevisions = madeChanges(new boolean[]{ true, true }, oneModifiedFile("user1", "Rev.1", TWO_DAYS_AGO_CHECKIN), oneModifiedFile("user2", "Rev.2", new Date()));
        Assert.assertThat(materialRevisions.latestRevision(), Matchers.is("Rev.1"));
    }

    @Test
    public void shouldUseFirstChangedMaterialAsLatestRevision() throws Exception {
        Date now = new Date();
        MaterialRevisions materialRevisions = madeChanges(new boolean[]{ false, true }, oneModifiedFile("user1", "Rev.1", TWO_DAYS_AGO_CHECKIN), oneModifiedFile("user2", "Rev.2", now));
        Assert.assertThat(materialRevisions.latestRevision(), Matchers.is("Rev.2"));
    }

    @Test
    public void shouldReturnFirstLatestRevisionIfNoChanged() throws Exception {
        MaterialRevisions materialRevisions = new MaterialRevisions(svnMaterialRevision(yesterdayMod, oneHourAgoMod), svnMaterialRevision(nowMod));
        Assert.assertThat(materialRevisions.latestRevision(), Matchers.is(yesterdayMod.getRevision()));
    }

    @Test
    public void shouldReturnMapKeyedByGivenMaterialName() {
        MaterialRevisions materialRevisions = new MaterialRevisions(svnMaterialRevision(yesterdayMod, oneHourAgoMod));
        Assert.assertThat(materialRevisions.getNamedRevisions().size(), Matchers.is(1));
        Assert.assertThat(materialRevisions.getNamedRevisions().get(new CaseInsensitiveString("Foo")), Matchers.is("9"));
    }

    @Test
    public void shouldNotAddMaterialWithEmptyNameIntoNamedRevisions() {
        MaterialRevisions materialRevisions = new MaterialRevisions(hgMaterialRevision(oneModifiedFile("hg revision 1")));
        Assert.assertThat(materialRevisions.getNamedRevisions().size(), Matchers.is(0));
    }

    @Test
    public void shouldFindDependencyMaterialRevisionByPipelineName() {
        MaterialRevision revision1 = dependencyMaterialRevision("cruise", 365, "1.3-365", "dist-zip", 1, new Date());
        MaterialRevision revision2 = dependencyMaterialRevision("mingle", 370, "2.3-370", "dist-zip", 1, new Date());
        MaterialRevisions materialRevisions = new MaterialRevisions(revision1, revision2);
        Assert.assertThat(materialRevisions.findDependencyMaterialRevision("cruise"), Matchers.is(revision1.getRevision()));
    }

    @Test
    public void shouldFindDependencyMaterialRevisionByPipelineNameWhenCaseDoNotMatch() {
        MaterialRevision revision1 = dependencyMaterialRevision("cruise", 365, "1.3-365", "dist-zip", 1, new Date());
        MaterialRevision revision2 = dependencyMaterialRevision("mingle", 370, "2.3-370", "dist-zip", 1, new Date());
        MaterialRevisions materialRevisions = new MaterialRevisions(revision1, revision2);
        Assert.assertThat(materialRevisions.findDependencyMaterialRevision("cruise"), Matchers.is(revision1.getRevision()));
        Assert.assertThat(materialRevisions.findDependencyMaterialRevision("Cruise"), Matchers.is(revision1.getRevision()));
        Assert.assertThat(materialRevisions.findDependencyMaterialRevision("CRUISE"), Matchers.is(revision1.getRevision()));
    }

    @Test
    public void shouldReturnTrueForMissingModificationsForEmptyList() {
        Assert.assertThat(new MaterialRevisions().isMissingModifications(), Matchers.is(true));
    }

    @Test
    public void shouldUseUpstreamPipelineLabelForDependencyMaterial() {
        CaseInsensitiveString pipelineName = new CaseInsensitiveString("upstream");
        String pipelineLabel = "1.3.0-1234";
        MaterialRevision materialRevision = ModificationsMother.dependencyMaterialRevision(pipelineName.toString(), 2, pipelineLabel, "dev", 1, new Date());
        MaterialRevisions materialRevisions = new MaterialRevisions(materialRevision);
        Map<CaseInsensitiveString, String> namedRevisions = materialRevisions.getNamedRevisions();
        Assert.assertThat(namedRevisions.get(pipelineName), Matchers.is(pipelineLabel));
    }

    @Test
    public void shouldPopulateEnvironmentVariablesForEachOfTheMaterialsWithTheValueForChangedFlag() {
        EnvironmentVariableContext context = new EnvironmentVariableContext();
        MaterialRevisions revisions = new MaterialRevisions();
        revisions.addRevision(new MaterialRevision(MaterialsMother.hgMaterial("empty-dest-and-no-name", null), true, ModificationsMother.multipleModificationList()));
        revisions.addRevision(new MaterialRevision(MaterialsMother.hgMaterial("url", "hg-folder1"), true, ModificationsMother.multipleModificationList()));
        revisions.addRevision(new MaterialRevision(MaterialsMother.hgMaterial("url", "hg-folder2"), false, ModificationsMother.multipleModificationList()));
        revisions.addRevision(new MaterialRevision(MaterialsMother.dependencyMaterial("p1", "s1"), true, ModificationsMother.changedDependencyMaterialRevision("p3", 1, "1", "s", 1, new Date()).getModifications()));
        revisions.addRevision(new MaterialRevision(MaterialsMother.dependencyMaterial("p2", "s2"), false, ModificationsMother.changedDependencyMaterialRevision("p3", 1, "1", "s", 1, new Date()).getModifications()));
        revisions.addRevision(new MaterialRevision(MaterialsMother.pluggableSCMMaterial("scm1", "scm1name"), true, ModificationsMother.multipleModificationList()));
        revisions.addRevision(new MaterialRevision(MaterialsMother.pluggableSCMMaterial("scm2", "scm2name"), false, ModificationsMother.multipleModificationList()));
        revisions.addRevision(new MaterialRevision(MaterialsMother.packageMaterial("repo1", "repo1name", "pkg1", "pkg1name"), true, ModificationsMother.multipleModificationList()));
        revisions.addRevision(new MaterialRevision(MaterialsMother.packageMaterial("repo2", "repo2name", "pkg2", "pkg2name"), false, ModificationsMother.multipleModificationList()));
        revisions.populateEnvironmentVariables(context, null);
        Assert.assertThat(context.getProperty("GO_MATERIAL_HAS_CHANGED"), Matchers.is("true"));
        Assert.assertThat(context.getProperty("GO_MATERIAL_HG_FOLDER1_HAS_CHANGED"), Matchers.is("true"));
        Assert.assertThat(context.getProperty("GO_MATERIAL_HG_FOLDER2_HAS_CHANGED"), Matchers.is("false"));
        Assert.assertThat(context.getProperty("GO_MATERIAL_P1_HAS_CHANGED"), Matchers.is("true"));
        Assert.assertThat(context.getProperty("GO_MATERIAL_P2_HAS_CHANGED"), Matchers.is("false"));
        Assert.assertThat(context.getProperty("GO_MATERIAL_SCM1NAME_HAS_CHANGED"), Matchers.is("true"));
        Assert.assertThat(context.getProperty("GO_MATERIAL_SCM2NAME_HAS_CHANGED"), Matchers.is("false"));
        Assert.assertThat(context.getProperty("GO_MATERIAL_REPO1NAME_PKG1NAME_HAS_CHANGED"), Matchers.is("true"));
        Assert.assertThat(context.getProperty("GO_MATERIAL_REPO2NAME_PKG2NAME_HAS_CHANGED"), Matchers.is("false"));
    }
}

