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
package com.thoughtworks.go.config.materials.perforce;


import com.thoughtworks.go.domain.materials.TestSubprocessExecutionContext;
import com.thoughtworks.go.domain.materials.ValidationBean;
import com.thoughtworks.go.domain.materials.mercurial.StringRevision;
import com.thoughtworks.go.domain.materials.perforce.PerforceFixture;
import com.thoughtworks.go.helper.P4TestRepo;
import com.thoughtworks.go.util.JsonUtils;
import com.thoughtworks.go.util.JsonValue;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public abstract class P4MaterialTestBase extends PerforceFixture {
    protected static final String VIEW = "//depot/... //something/...";

    @Test
    public void shouldValidateCorrectConnection() throws Exception {
        P4Material p4Material = p4Fixture.material(P4MaterialTestBase.VIEW);
        p4Material.setPassword("secret");
        p4Material.setUseTickets(false);
        ValidationBean validation = p4Material.checkConnection(new TestSubprocessExecutionContext());
        Assert.assertThat(validation.isValid(), Matchers.is(true));
        Assert.assertThat(StringUtils.isBlank(validation.getError()), Matchers.is(true));
    }

    @Test
    public void shouldBeAbleToGetUrlArgument() throws Exception {
        P4Material p4Material = new P4Material("localhost:9876", "p4view");
        Assert.assertThat(p4Material.getUrlArgument().forDisplay(), Matchers.is("localhost:9876"));
    }

    @Test
    public void shouldCheckConnection() throws Exception {
        P4Material p4Material = new P4Material("localhost:9876", "p4view");
        p4Material.setPassword("secret");
        p4Material.setUseTickets(false);
        ValidationBean validation = p4Material.checkConnection(new TestSubprocessExecutionContext());
        Assert.assertThat(validation.isValid(), Matchers.is(false));
        Assert.assertThat(validation.getError(), Matchers.containsString("Unable to connect to server localhost:9876"));
        Assert.assertThat(validation.getError(), Matchers.not(Matchers.containsString("secret")));
    }

    @Test
    public void shouldReplacePasswordUsingStar() throws Exception {
        P4Material p4Material = new P4Material("localhost:9876", "p4view");
        p4Material.setPassword("secret");
        p4Material.setUseTickets(true);
        try {
            p4Material.latestModification(clientFolder, new TestSubprocessExecutionContext());
            Assert.fail("should throw exception because p4 server not exists.");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), Matchers.not(Matchers.containsString("secret")));
        }
    }

    @Test
    public void shouldUpdateToSpecificRevision() throws Exception {
        P4Material p4Material = p4Fixture.material(P4MaterialTestBase.VIEW);
        updateMaterial(p4Material, new StringRevision("2"));
        Assert.assertThat(clientFolder.listFiles().length, Matchers.is(8));
        updateMaterial(p4Material, new StringRevision("3"));
        Assert.assertThat(clientFolder.listFiles().length, Matchers.is(7));
    }

    @Test
    public void shouldCleanOutRepoWhenMaterialChanges() throws Exception {
        P4TestRepo secondTestRepo = P4TestRepo.createP4TestRepo(temporaryFolder, clientFolder);
        try {
            secondTestRepo.onSetup();
            P4Material p4Material = p4Fixture.material(P4MaterialTestBase.VIEW);
            updateMaterial(p4Material, new StringRevision("2"));
            File.createTempFile("temp", "txt", clientFolder);
            Assert.assertThat(clientFolder.listFiles().length, Matchers.is(9));
            P4Material otherMaterial = secondTestRepo.material("//depot/lib/... //something/...");
            otherMaterial.setUsername("cceuser1");
            updateMaterial(otherMaterial, new StringRevision("2"));
            File.createTempFile("temp", "txt", clientFolder);
            Assert.assertThat("Should clean and re-checkout after p4repo changed", clientFolder.listFiles().length, Matchers.is(3));
            otherMaterial.setUsername("cceuser");
            otherMaterial.setPassword("password");
            updateMaterial(otherMaterial, new StringRevision("2"));
            Assert.assertThat("Should clean and re-checkout after user changed", clientFolder.listFiles().length, Matchers.is(2));
            Assert.assertThat(outputconsumer.getStdOut(), Matchers.containsString("Working directory has changed. Deleting and re-creating it."));
        } finally {
            secondTestRepo.stop();
            secondTestRepo.onTearDown();
        }
    }

    @Test
    public void shouldMapDirectoryInRepoToClientRoot() throws Exception {
        P4Material p4Material = p4Fixture.material("//depot/lib/... //cws/...");
        updateMaterial(p4Material, new StringRevision("2"));
        Assert.assertThat(getContainedFileNames(clientFolder), Matchers.hasItem("junit.jar"));
    }

    @Test
    public void shouldMapDirectoryInRepoToDirectoryUnderClientRoot() throws Exception {
        P4Material p4Material = p4Fixture.material("//depot/lib/... //cws/release1/...");
        updateMaterial(p4Material, new StringRevision("2"));
        Assert.assertThat(getContainedFileNames(new File(clientFolder, "release1")), Matchers.hasItem("junit.jar"));
    }

    @Test
    public void shouldExcludeSpecifiedDirectory() throws Exception {
        P4Material p4Material = p4Fixture.material("//depot/... //cws/...   \n  -//depot/lib/... //cws/release1/...");
        updateMaterial(p4Material, new StringRevision("2"));
        Assert.assertThat(new File(clientFolder, "release1").exists(), Matchers.is(false));
        Assert.assertThat(new File(clientFolder, "release1/junit.jar").exists(), Matchers.is(false));
    }

    @Test
    public void shouldSupportAsterisk() throws Exception {
        P4Material p4Material = p4Fixture.material("//depot/lib/*.jar //cws/*.war");
        updateMaterial(p4Material, new StringRevision("2"));
        File file = new File(clientFolder, "junit.war");
        Assert.assertThat(file.exists(), Matchers.is(true));
    }

    @Test
    public void shouldSupportPercetage() throws Exception {
        P4Material p4Material = p4Fixture.material("//depot/lib/%%1.%%2 //cws/%%2.%%1");
        updateMaterial(p4Material, new StringRevision("2"));
        File file = new File(clientFolder, "jar.junit");
        Assert.assertThat(file.exists(), Matchers.is(true));
    }

    @Test
    public void laterDefinitionShouldOveridePreviousOne() throws Exception {
        P4Material p4Material = p4Fixture.material("//depot/src/... //cws/build/... \n //depot/lib/... //cws/build/...");
        File file = new File(clientFolder, "build/junit.jar");
        File folderNet = new File(clientFolder, "build/net");
        updateMaterial(p4Material, new StringRevision("2"));
        Assert.assertThat(folderNet.exists(), Matchers.is(false));
        Assert.assertThat(file.exists(), Matchers.is(true));
    }

    @Test
    public void laterDefinitionShouldMergeWithPreviousOneWhenPlusPresent() throws Exception {
        P4Material p4Material = p4Fixture.material("//depot/src/... //cws/build/... \n +//depot/lib/... //cws/build/...");
        File file = new File(clientFolder, "build/junit.jar");
        File folderNet = new File(clientFolder, "build/net");
        updateMaterial(p4Material, new StringRevision("2"));
        Assert.assertThat(folderNet.exists(), Matchers.is(true));
        Assert.assertThat(file.exists(), Matchers.is(true));
    }

    @Test
    public void shouldCleanOutRepoWhenViewChanges() throws Exception {
        P4Material p4Material = p4Fixture.material(P4MaterialTestBase.VIEW);
        updateMaterial(p4Material, new StringRevision("2"));
        Assert.assertThat(clientFolder.listFiles().length, Matchers.is(8));
        P4Material otherMaterial = p4Fixture.material("//depot/lib/... //something/...");
        updateMaterial(otherMaterial, new StringRevision("2"));
        Assert.assertThat(clientFolder.listFiles().length, Matchers.is(2));
    }

    @Test
    public void shouldCreateUniqueClientForDifferentFoldersOnTheSameMachine() {
        P4Material p4Material = p4Fixture.material(P4MaterialTestBase.VIEW);
        String name1 = p4Material.clientName(new File("/one-directory/foo/with_underlines"));
        String name2 = p4Material.clientName(new File("/another-directory/foo/with_underlines"));
        Assert.assertThat(name1, Matchers.is(Matchers.not(name2)));
        Assert.assertThat(("Client name should be a legal filename: " + name1), name1.matches("[0-9a-zA-Z_\\.\\-]*"), Matchers.is(true));
        Assert.assertThat(("Client name should be a legal filename: " + name2), name2.matches("[0-9a-zA-Z_\\.\\-]*"), Matchers.is(true));
    }

    @Test
    public void hashCodeShouldBeSameWhenP4MaterialEquals() throws Exception {
        P4Material p4 = p4Fixture.material("material1");
        P4Material anotherP4 = p4Fixture.material("material1");
        Assert.assertThat(p4, Matchers.is(anotherP4));
        Assert.assertThat(p4.hashCode(), Matchers.is(anotherP4.hashCode()));
    }

    @Test
    public void shouldBeAbleToConvertToJson() {
        P4Material p4Material = p4Fixture.material(P4MaterialTestBase.VIEW);
        Map<String, Object> json = new LinkedHashMap<>();
        p4Material.toJson(json, new StringRevision("123"));
        JsonValue jsonValue = JsonUtils.from(json);
        Assert.assertThat(jsonValue.getString("scmType"), Matchers.is("Perforce"));
        Assert.assertThat(jsonValue.getString("location"), Matchers.is(p4Material.getServerAndPort()));
        Assert.assertThat(jsonValue.getString("action"), Matchers.is("Modified"));
    }

    @Test
    public void shouldLogRepoInfoToConsoleOutWithoutFolder() throws Exception {
        P4Material p4Material = p4Fixture.material(P4MaterialTestBase.VIEW);
        updateMaterial(p4Material, new StringRevision("2"));
        String message = String.format("Start updating %s at revision %s from %s", "files", "2", p4Material.getUrl());
        Assert.assertThat(outputconsumer.getStdOut(), Matchers.containsString(message));
    }

    @Test
    public void shouldGenerateSqlCriteriaMapInSpecificOrder() throws Exception {
        Map<String, Object> map = p4Fixture.material("view").getSqlCriteria();
        Assert.assertThat(map.size(), Matchers.is(4));
        Iterator<Map.Entry<String, Object>> iter = map.entrySet().iterator();
        Assert.assertThat(iter.next().getKey(), Matchers.is("type"));
        Assert.assertThat(iter.next().getKey(), Matchers.is("url"));
        Assert.assertThat(iter.next().getKey(), Matchers.is("username"));
        Assert.assertThat(iter.next().getKey(), Matchers.is("view"));
    }
}

