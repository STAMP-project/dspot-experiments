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
package com.thoughtworks.go.config.materials.mercurial;


import com.googlecode.junit.ext.JunitExtRunner;
import com.googlecode.junit.ext.RunIf;
import com.googlecode.junit.ext.checkers.OSChecker;
import com.thoughtworks.go.domain.materials.mercurial.HgCommand;
import com.thoughtworks.go.domain.materials.mercurial.StringRevision;
import com.thoughtworks.go.helper.HgTestRepo;
import com.thoughtworks.go.helper.MaterialsMother;
import com.thoughtworks.go.junitext.EnhancedOSChecker;
import com.thoughtworks.go.util.JsonUtils;
import com.thoughtworks.go.util.JsonValue;
import com.thoughtworks.go.util.ReflectionUtil;
import com.thoughtworks.go.util.command.ConsoleResult;
import com.thoughtworks.go.util.command.InMemoryStreamConsumer;
import com.thoughtworks.go.util.command.ProcessOutputStreamConsumer;
import com.thoughtworks.go.util.command.UrlArgument;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(JunitExtRunner.class)
public class HgMaterialTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private HgMaterial hgMaterial;

    private HgTestRepo hgTestRepo;

    private File workingFolder;

    private InMemoryStreamConsumer outputStreamConsumer;

    private static final String LINUX_HG_094 = "Mercurial Distributed SCM (version 0.9.4)\n" + ((("\n" + "Copyright (C) 2005-2007 Matt Mackall <mpm@selenic.com> and others\n") + "This is free software; see the source for copying conditions. There is NO\n") + "warranty; not even for MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.");

    private static final String LINUX_HG_101 = "Mercurial Distributed SCM (version 1.0.1)\n" + ((("\n" + "Copyright (C) 2005-2007 Matt Mackall <mpm@selenic.com> and others\n") + "This is free software; see the source for copying conditions. There is NO\n") + "warranty; not even for MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.");

    private static final String LINUX_HG_10 = "Mercurial Distributed SCM (version 1.0)\n" + ((("\n" + "Copyright (C) 2005-2007 Matt Mackall <mpm@selenic.com> and others\n") + "This is free software; see the source for copying conditions. There is NO\n") + "warranty; not even for MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.");

    private static final String WINDOWS_HG_OFFICAL_102 = "Mercurial Distributed SCM (version 1.0.2+20080813)\n" + ((("\n" + "Copyright (C) 2005-2008 Matt Mackall <mpm@selenic.com>; and others\n") + "This is free software; see the source for copying conditions. There is NO\n") + "warranty; not even for MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.");

    private static final String WINDOWS_HG_TORTOISE = "Mercurial Distributed SCM (version 626cb86a6523+tortoisehg)";

    private static final String REVISION_0 = "b61d12de515d82d3a377ae3aae6e8abe516a2651";

    private static final String REVISION_1 = "35ff2159f303ecf986b3650fc4299a6ffe5a14e1";

    private static final String REVISION_2 = "ca3ebb67f527c0ad7ed26b789056823d8b9af23f";

    @Test
    public void shouldRefreshWorkingFolderWhenRepositoryChanged() throws Exception {
        new HgCommand(null, workingFolder, "default", hgTestRepo.url().forCommandline(), null).clone(ProcessOutputStreamConsumer.inMemoryConsumer(), hgTestRepo.url());
        File testFile = createNewFileInWorkingFolder();
        HgTestRepo hgTestRepo2 = new HgTestRepo("hgTestRepo2", temporaryFolder);
        hgMaterial = MaterialsMother.hgMaterial(hgTestRepo2.projectRepositoryUrl());
        hgMaterial.latestModification(workingFolder, new TestSubprocessExecutionContext());
        String workingUrl = workingRepositoryUrl().outputAsString();
        Assert.assertThat(workingUrl, is(hgTestRepo2.projectRepositoryUrl()));
        Assert.assertThat(testFile.exists(), is(false));
    }

    @Test
    @RunIf(value = EnhancedOSChecker.class, arguments = { DO_NOT_RUN_ON, OSChecker.WINDOWS })
    public void shouldNotRefreshWorkingFolderWhenFileProtocolIsUsedOnLinux() throws Exception {
        final UrlArgument repoUrl = hgTestRepo.url();
        new HgCommand(null, workingFolder, "default", repoUrl.forCommandline(), null).clone(ProcessOutputStreamConsumer.inMemoryConsumer(), repoUrl);
        File testFile = createNewFileInWorkingFolder();
        hgMaterial = MaterialsMother.hgMaterial(("file://" + (hgTestRepo.projectRepositoryUrl())));
        updateMaterial(hgMaterial, new StringRevision("0"));
        String workingUrl = workingRepositoryUrl().outputAsString();
        Assert.assertThat(workingUrl, is(hgTestRepo.projectRepositoryUrl()));
        Assert.assertThat(testFile.exists(), is(true));
    }

    @Test
    public void shouldNotRefreshWorkingDirectoryIfDefaultRemoteUrlDoesNotContainPasswordButMaterialUrlDoes() throws Exception {
        final HgMaterial material = new HgMaterial("http://user:pwd@domain:9999/path", null);
        final HgCommand hgCommand = Mockito.mock(HgCommand.class);
        final ConsoleResult consoleResult = Mockito.mock(ConsoleResult.class);
        Mockito.when(consoleResult.outputAsString()).thenReturn("http://user@domain:9999/path");
        Mockito.when(hgCommand.workingRepositoryUrl()).thenReturn(consoleResult);
        Assert.assertFalse(((Boolean) (ReflectionUtil.invoke(material, "isRepositoryChanged", hgCommand))));
    }

    @Test
    public void shouldRefreshWorkingDirectoryIfUsernameInDefaultRemoteUrlIsDifferentFromOneInMaterialUrl() throws Exception {
        final HgMaterial material = new HgMaterial("http://some_new_user:pwd@domain:9999/path", null);
        final HgCommand hgCommand = Mockito.mock(HgCommand.class);
        final ConsoleResult consoleResult = Mockito.mock(ConsoleResult.class);
        Mockito.when(consoleResult.outputAsString()).thenReturn("http://user:pwd@domain:9999/path");
        Mockito.when(hgCommand.workingRepositoryUrl()).thenReturn(consoleResult);
        Assert.assertTrue(((Boolean) (ReflectionUtil.invoke(material, "isRepositoryChanged", hgCommand))));
    }

    @Test
    public void shouldGetModifications() {
        List<Modification> mods = hgMaterial.modificationsSince(workingFolder, new StringRevision(HgMaterialTest.REVISION_0), new TestSubprocessExecutionContext());
        Assert.assertThat(mods.size(), is(2));
        Modification modification = mods.get(0);
        Assert.assertThat(modification.getRevision(), is(HgMaterialTest.REVISION_2));
        Assert.assertThat(modification.getModifiedFiles().size(), is(1));
    }

    @Test
    public void shouldNotAppendDestinationDirectoryWhileFetchingModifications() {
        hgMaterial.setFolder("dest");
        hgMaterial.modificationsSince(workingFolder, new StringRevision(HgMaterialTest.REVISION_0), new TestSubprocessExecutionContext());
        Assert.assertThat(new File(workingFolder, "dest").exists(), is(false));
    }

    @Test
    public void shouldGetModificationsBasedOnRevision() {
        List<Modification> modificationsSince = hgMaterial.modificationsSince(workingFolder, new StringRevision(HgMaterialTest.REVISION_0), new TestSubprocessExecutionContext());
        Assert.assertThat(modificationsSince.get(0).getRevision(), is(HgMaterialTest.REVISION_2));
        Assert.assertThat(modificationsSince.get(1).getRevision(), is(HgMaterialTest.REVISION_1));
        Assert.assertThat(modificationsSince.size(), is(2));
    }

    @Test
    public void shouldReturnLatestRevisionIfNoModificationsDetected() {
        List<Modification> modification = hgMaterial.modificationsSince(workingFolder, new StringRevision(HgMaterialTest.REVISION_2), new TestSubprocessExecutionContext());
        Assert.assertThat(modification.isEmpty(), is(true));
    }

    @Test
    public void shouldUpdateToSpecificRevision() {
        updateMaterial(hgMaterial, new StringRevision("0"));
        File end2endFolder = new File(workingFolder, "end2end");
        Assert.assertThat(end2endFolder.listFiles().length, is(3));
        Assert.assertThat(outputStreamConsumer.getStdOut(), is(not("")));
        updateMaterial(hgMaterial, new StringRevision("1"));
        Assert.assertThat(end2endFolder.listFiles().length, is(4));
    }

    @Test
    public void shouldUpdateToDestinationFolder() {
        hgMaterial.setFolder("dest");
        updateMaterial(hgMaterial, new StringRevision("0"));
        File end2endFolder = new File(workingFolder, "dest/end2end");
        Assert.assertThat(end2endFolder.exists(), is(true));
    }

    @Test
    public void shouldLogRepoInfoToConsoleOutWithoutFolder() {
        updateMaterial(hgMaterial, new StringRevision("0"));
        Assert.assertThat(outputStreamConsumer.getStdOut(), containsString(String.format("Start updating %s at revision %s from %s", "files", "0", hgMaterial.getUrl())));
    }

    @Test
    public void shouldDeleteWorkingFolderWhenItIsNotAnHgRepository() throws Exception {
        File testFile = createNewFileInWorkingFolder();
        hgMaterial.latestModification(workingFolder, new TestSubprocessExecutionContext());
        Assert.assertThat(testFile.exists(), is(false));
    }

    @Test
    public void shouldThrowExceptionWithUsefulInfoIfFailedToFindModifications() {
        final String url = "/tmp/notExistDir";
        hgMaterial = MaterialsMother.hgMaterial(url);
        try {
            hgMaterial.latestModification(workingFolder, new TestSubprocessExecutionContext());
            Assert.fail("Should have thrown an exception when failed to clone from an invalid url");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), containsString((("abort: repository " + url) + " not found!")));
        }
    }

    @Test
    public void shouldBeEqualWhenUrlSameForHgMaterial() {
        final Material material = MaterialsMother.hgMaterials("url1", "hgdir").get(0);
        final Material anotherMaterial = MaterialsMother.hgMaterials("url1", "hgdir").get(0);
        Assert.assertThat(material.equals(anotherMaterial), is(true));
        Assert.assertThat(anotherMaterial.equals(material), is(true));
    }

    @Test
    public void shouldNotBeEqualWhenUrlDifferent() {
        final Material material1 = MaterialsMother.hgMaterials("url1", "hgdir").get(0);
        final Material material2 = MaterialsMother.hgMaterials("url2", "hgdir").get(0);
        Assert.assertThat(material1.equals(material2), is(false));
        Assert.assertThat(material2.equals(material1), is(false));
    }

    @Test
    public void shouldNotBeEqualWhenTypeDifferent() {
        final Material material = MaterialsMother.hgMaterials("url1", "hgdir").get(0);
        final Material svnMaterial = MaterialsMother.defaultSvnMaterialsWithUrl("url1").get(0);
        Assert.assertThat(material.equals(svnMaterial), is(false));
        Assert.assertThat(svnMaterial.equals(material), is(false));
    }

    @Test
    public void shouldBeEqual() {
        final Material hgMaterial1 = MaterialsMother.hgMaterials("url1", "hgdir").get(0);
        final Material hgMaterial2 = MaterialsMother.hgMaterials("url1", "hgdir").get(0);
        Assert.assertThat(hgMaterial1.equals(hgMaterial2), is(true));
        Assert.assertThat(hgMaterial1.hashCode(), is(hgMaterial2.hashCode()));
    }

    @Test
    public void shouldReturnTrueForLinuxDistributeLowerThanOneZero() {
        Assert.assertThat(hgMaterial.isVersionOnedotZeorOrHigher(HgMaterialTest.LINUX_HG_094), is(false));
    }

    @Test
    public void shouldReturnTrueForLinuxDistributeHigerThanOneZero() {
        Assert.assertThat(hgMaterial.isVersionOnedotZeorOrHigher(HgMaterialTest.LINUX_HG_101), is(true));
    }

    @Test
    public void shouldReturnTrueForLinuxDistributeEqualsOneZero() {
        Assert.assertThat(hgMaterial.isVersionOnedotZeorOrHigher(HgMaterialTest.LINUX_HG_10), is(true));
    }

    @Test
    public void shouldReturnTrueForWindowsDistributionHigerThanOneZero() {
        Assert.assertThat(hgMaterial.isVersionOnedotZeorOrHigher(HgMaterialTest.WINDOWS_HG_OFFICAL_102), is(true));
    }

    @Test(expected = Exception.class)
    public void shouldReturnFalseWhenVersionIsNotRecgonized() {
        hgMaterial.isVersionOnedotZeorOrHigher(HgMaterialTest.WINDOWS_HG_TORTOISE);
    }

    @Test
    public void shouldCheckConnection() {
        ValidationBean validation = hgMaterial.checkConnection(new TestSubprocessExecutionContext());
        Assert.assertThat(validation.isValid(), is(true));
        String notExistUrl = "http://notExisthost/hg";
        hgMaterial = MaterialsMother.hgMaterial(notExistUrl);
        validation = hgMaterial.checkConnection(new TestSubprocessExecutionContext());
        Assert.assertThat(validation.isValid(), is(false));
    }

    @Test
    public void shouldReturnInvalidBeanWithRootCauseAsLowerVersionInstalled() {
        ValidationBean validationBean = hgMaterial.handleException(new Exception(), HgMaterialTest.LINUX_HG_094);
        Assert.assertThat(validationBean.isValid(), is(false));
        Assert.assertThat(validationBean.getError(), containsString("Please install Mercurial Version 1.0 or above"));
    }

    @Test
    public void shouldReturnInvalidBeanWithRootCauseAsRepositoryURLIsNotFound() {
        ValidationBean validationBean = hgMaterial.handleException(new Exception(), HgMaterialTest.WINDOWS_HG_OFFICAL_102);
        Assert.assertThat(validationBean.isValid(), is(false));
        Assert.assertThat(validationBean.getError(), containsString("not found!"));
    }

    @Test
    public void shouldReturnInvalidBeanWithRootCauseAsRepositoryURLIsNotFoundIfVersionIsNotKnown() {
        ValidationBean validationBean = hgMaterial.handleException(new Exception(), HgMaterialTest.WINDOWS_HG_TORTOISE);
        Assert.assertThat(validationBean.isValid(), is(false));
        Assert.assertThat(validationBean.getError(), containsString("not found!"));
    }

    @Test
    public void shouldBeAbleToConvertToJson() {
        Map<String, Object> json = new LinkedHashMap<>();
        hgMaterial.toJson(json, new StringRevision("123"));
        JsonValue jsonValue = JsonUtils.from(json);
        Assert.assertThat(jsonValue.getString("scmType"), is("Mercurial"));
        Assert.assertThat(new File(jsonValue.getString("location")), is(new File(hgTestRepo.projectRepositoryUrl())));
        Assert.assertThat(jsonValue.getString("action"), is("Modified"));
    }

    @Test
    public void shouldRemoveTheForwardSlashAndApplyThePattern() {
        Material material = MaterialsMother.hgMaterial();
        Assert.assertThat(material.matches("a.doc", "/a.doc"), is(true));
        Assert.assertThat(material.matches("/a.doc", "a.doc"), is(false));
    }

    @Test
    public void shouldApplyThePatternDirectly() {
        Material material = MaterialsMother.hgMaterial();
        Assert.assertThat(material.matches("a.doc", "a.doc"), is(true));
    }

    // #3103
    @Test
    public void shouldParseComplexCommitMessage() throws Exception {
        String comment = "changeset:   8139:b1a0b0bbb4d1\n" + ((("branch:      trunk\n" + "user:        QYD\n") + "date:        Tue Jun 30 14:56:37 2009 +0800\n") + "summary:     add story #3001 - 'Pipelines should use the latest version of ...");
        hgTestRepo.commitAndPushFile("SomeDocumentation.txt", comment);
        List<Modification> modification = hgMaterial.latestModification(workingFolder, new TestSubprocessExecutionContext());
        Assert.assertThat(modification.size(), Matchers.is(1));
        Assert.assertThat(modification.get(0).getComment(), is(comment));
    }

    @Test
    public void shouldGenerateSqlCriteriaMapInSpecificOrder() {
        Map<String, Object> map = hgMaterial.getSqlCriteria();
        Assert.assertThat(map.size(), is(2));
        Iterator<Map.Entry<String, Object>> iter = map.entrySet().iterator();
        Assert.assertThat(iter.next().getKey(), is("type"));
        Assert.assertThat(iter.next().getKey(), is("url"));
    }

    /**
     * An hg abbreviated hash is 12 chars. See the hg documentation.
     * %h:	short-form changeset hash (12 bytes of hexadecimal)
     */
    @Test
    public void shouldtruncateHashTo12charsforAShortRevision() {
        Material hgMaterial = new HgMaterial("file:///foo", null);
        Assert.assertThat(hgMaterial.getShortRevision("dc3d7e656831d1b203d8b7a63c4de82e26604e52"), is("dc3d7e656831"));
        Assert.assertThat(hgMaterial.getShortRevision("dc3d7e65683"), is("dc3d7e65683"));
        Assert.assertThat(hgMaterial.getShortRevision("dc3d7e6568312"), is("dc3d7e656831"));
        Assert.assertThat(hgMaterial.getShortRevision("24"), is("24"));
        Assert.assertThat(hgMaterial.getShortRevision(null), is(nullValue()));
    }

    @Test
    public void shouldNotDisplayPasswordInToString() {
        HgMaterial hgMaterial = new HgMaterial("https://user:loser@foo.bar/baz?quux=bang", null);
        Assert.assertThat(hgMaterial.toString(), not(containsString("loser")));
    }

    @Test
    public void shouldGetLongDescriptionForMaterial() {
        HgMaterial material = new HgMaterial("http://url/", "folder");
        Assert.assertThat(material.getLongDescription(), is("URL: http://url/"));
    }

    @Test
    public void shouldFindTheBranchName() {
        HgMaterial material = new HgMaterial("http://url/##foo", "folder");
        Assert.assertThat(material.getBranch(), is("foo"));
    }

    @Test
    public void shouldSetDefaultAsBranchNameIfBranchNameIsNotSpecifiedInUrl() {
        HgMaterial material = new HgMaterial("http://url/", "folder");
        Assert.assertThat(material.getBranch(), is("default"));
    }

    @Test
    public void shouldMaskThePasswordInDisplayName() {
        HgMaterial material = new HgMaterial("http://user:pwd@url##branch", "folder");
        Assert.assertThat(material.getDisplayName(), is("http://user:******@url##branch"));
    }

    @Test
    public void shouldGetAttributesWithSecureFields() {
        HgMaterial material = new HgMaterial("http://username:password@hgrepo.com", null);
        Map<String, Object> attributes = material.getAttributes(true);
        Assert.assertThat(attributes.get("type"), is("mercurial"));
        Map<String, Object> configuration = ((Map<String, Object>) (attributes.get("mercurial-configuration")));
        Assert.assertThat(configuration.get("url"), is("http://username:password@hgrepo.com"));
    }

    @Test
    public void shouldGetAttributesWithoutSecureFields() {
        HgMaterial material = new HgMaterial("http://username:password@hgrepo.com", null);
        Map<String, Object> attributes = material.getAttributes(false);
        Assert.assertThat(attributes.get("type"), is("mercurial"));
        Map<String, Object> configuration = ((Map<String, Object>) (attributes.get("mercurial-configuration")));
        Assert.assertThat(configuration.get("url"), is("http://username:******@hgrepo.com"));
    }
}

