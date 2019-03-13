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
package com.thoughtworks.go.config.materials.tfs;


import AbstractMaterial.SQL_CRITERIA_TYPE;
import com.thoughtworks.go.config.PasswordEncrypter;
import com.thoughtworks.go.config.materials.AbstractMaterial;
import com.thoughtworks.go.config.materials.PasswordAwareMaterial;
import com.thoughtworks.go.domain.materials.Modification;
import com.thoughtworks.go.domain.materials.TestSubprocessExecutionContext;
import com.thoughtworks.go.domain.materials.ValidationBean;
import com.thoughtworks.go.domain.materials.mercurial.StringRevision;
import com.thoughtworks.go.domain.materials.tfs.TfsCommand;
import com.thoughtworks.go.security.CryptoException;
import com.thoughtworks.go.security.GoCipher;
import com.thoughtworks.go.util.DataStructureUtils;
import com.thoughtworks.go.util.ReflectionUtil;
import com.thoughtworks.go.util.command.UrlArgument;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;


public class TfsMaterialTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private TfsMaterial tfsMaterialFirstCollectionFirstProject;

    private TfsMaterial tfsMaterialFirstCollectionSecondProject;

    private final String DOMAIN = "domain";

    private final String USERNAME = "username";

    private final String PASSWORD = "password";

    private final String TFS_FIRST_COLLECTION_URL = "http://some.tfs.repo.local";

    private final String TFS_FIRST_PROJECT = "$/first_project";

    private final String TFS_SECOND_PROJECT = "$/second_project";

    @Test
    public void shouldShowLatestModification() throws IOException {
        File dir = temporaryFolder.newFolder("tfs-dir");
        TestSubprocessExecutionContext execCtx = new TestSubprocessExecutionContext();
        TfsMaterial spy = Mockito.spy(tfsMaterialFirstCollectionSecondProject);
        TfsCommand tfsCommand = Mockito.mock(TfsCommand.class);
        Mockito.when(tfsCommand.latestModification(dir)).thenReturn(new ArrayList());
        Mockito.doReturn(tfsCommand).when(spy).tfs(execCtx);
        List<Modification> actual = spy.latestModification(dir, execCtx);
        Assert.assertThat(actual, Matchers.is(new ArrayList<Modification>()));
        Mockito.verify(tfsCommand).latestModification(dir);
    }

    @Test
    public void shouldLoadAllModificationsSinceAGivenRevision() throws IOException {
        File dir = temporaryFolder.newFolder("tfs-dir");
        TestSubprocessExecutionContext execCtx = new TestSubprocessExecutionContext();
        TfsMaterial spy = Mockito.spy(tfsMaterialFirstCollectionFirstProject);
        TfsCommand tfsCommand = Mockito.mock(TfsCommand.class);
        Mockito.when(tfsCommand.modificationsSince(dir, new StringRevision("5"))).thenReturn(new ArrayList());
        Mockito.doReturn(tfsCommand).when(spy).tfs(execCtx);
        List<Modification> actual = spy.modificationsSince(dir, new StringRevision("5"), execCtx);
        Assert.assertThat(actual, Matchers.is(new ArrayList<Modification>()));
        Mockito.verify(tfsCommand).modificationsSince(dir, new StringRevision("5"));
    }

    @Test
    public void shouldInjectAllRelevantAttributesInSqlCriteriaMap() {
        TfsMaterial tfsMaterial = new TfsMaterial(new GoCipher(), new UrlArgument("my-url"), "loser", DOMAIN, "foo_bar_baz", "/dev/null");
        Assert.assertThat(tfsMaterial.getSqlCriteria(), Matchers.is(DataStructureUtils.m(AbstractMaterial.SQL_CRITERIA_TYPE, ((Object) ("TfsMaterial")), "url", "my-url", "username", "loser", "projectPath", "/dev/null", "domain", DOMAIN)));
    }

    @Test
    public void shouldInjectAllRelevantAttributesInAttributeMap() {
        TfsMaterial tfsMaterial = new TfsMaterial(new GoCipher(), new UrlArgument("my-url"), "loser", DOMAIN, "foo_bar_baz", "/dev/null");
        Assert.assertThat(tfsMaterial.getAttributesForXml(), Matchers.is(DataStructureUtils.m(SQL_CRITERIA_TYPE, ((Object) ("TfsMaterial")), "url", "my-url", "username", "loser", "projectPath", "/dev/null", "domain", DOMAIN)));
    }

    @Test
    public void shouldReturnUrlForCommandLine_asUrl_IfSet() {
        TfsMaterial tfsMaterial = new TfsMaterial(new GoCipher(), new UrlArgument("http://foo:bar@my-url.com"), "loser", DOMAIN, "foo_bar_baz", "/dev/null");
        Assert.assertThat(tfsMaterial.getUrl(), Matchers.is("http://foo:bar@my-url.com"));
        tfsMaterial = new TfsMaterial(new GoCipher(), null, "loser", DOMAIN, "foo_bar_baz", "/dev/null");
        Assert.assertThat(tfsMaterial.getUrl(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldReturnUrlForCommandLine_asLocation_IfSet() {
        TfsMaterial tfsMaterial = new TfsMaterial(new GoCipher(), new UrlArgument("http://foo:bar@my-url.com"), "loser", DOMAIN, "foo_bar_baz", "/dev/null");
        Assert.assertThat(tfsMaterial.getLocation(), Matchers.is("http://foo:******@my-url.com"));
        tfsMaterial = new TfsMaterial(new GoCipher(), null, "loser", DOMAIN, "foo_bar_baz", "/dev/null");
        Assert.assertThat(tfsMaterial.getLocation(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldEncryptTfsPasswordAndMarkPasswordAsNull() throws Exception {
        GoCipher mockGoCipher = Mockito.mock(GoCipher.class);
        Mockito.when(mockGoCipher.encrypt("password")).thenReturn("encrypted");
        Mockito.when(mockGoCipher.maybeReEncryptForPostConstructWithoutExceptions("encrypted")).thenReturn("encrypted");
        TfsMaterial tfsMaterial = new TfsMaterial(mockGoCipher, new UrlArgument("/foo"), "username", DOMAIN, "password", "");
        tfsMaterial.ensureEncrypted();
        Assert.assertThat(tfsMaterial.getPassword(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(tfsMaterial.getEncryptedPassword(), Matchers.is("encrypted"));
    }

    @Test
    public void shouldDecryptTfsPassword() throws Exception {
        GoCipher mockGoCipher = Mockito.mock(GoCipher.class);
        Mockito.when(mockGoCipher.decrypt("encrypted")).thenReturn("password");
        Mockito.when(mockGoCipher.maybeReEncryptForPostConstructWithoutExceptions("encrypted")).thenReturn("encrypted");
        TfsMaterial tfsMaterial = new TfsMaterial(mockGoCipher, new UrlArgument("/foo"), "username", DOMAIN, null, "");
        ReflectionUtil.setField(tfsMaterial, "encryptedPassword", "encrypted");
        tfsMaterial.ensureEncrypted();
        Assert.assertThat(tfsMaterial.getPassword(), Matchers.is("password"));
    }

    @Test
    public void shouldNotDecryptPasswordIfPasswordIsNotNull() throws Exception {
        GoCipher mockGoCipher = Mockito.mock(GoCipher.class);
        Mockito.when(mockGoCipher.encrypt("password")).thenReturn("encrypted");
        Mockito.when(mockGoCipher.decrypt("encrypted")).thenReturn("password");
        TfsMaterial material = new TfsMaterial(mockGoCipher, new UrlArgument("/foo"), "username", DOMAIN, "password", "");
        material.ensureEncrypted();
        Mockito.when(mockGoCipher.encrypt("new_password")).thenReturn("new_encrypted");
        material.setPassword("new_password");
        Mockito.when(mockGoCipher.decrypt("new_encrypted")).thenReturn("new_password");
        Assert.assertThat(material.getPassword(), Matchers.is("new_password"));
    }

    @Test
    public void shouldErrorOutIfDecryptionFails() throws CryptoException {
        GoCipher mockGoCipher = Mockito.mock(GoCipher.class);
        String fakeCipherText = "fake cipher text";
        Mockito.when(mockGoCipher.decrypt(fakeCipherText)).thenThrow(new CryptoException("exception"));
        TfsMaterial material = new TfsMaterial(mockGoCipher, new UrlArgument("/foo"), "username", DOMAIN, "password", "");
        ReflectionUtil.setField(material, "encryptedPassword", fakeCipherText);
        try {
            material.getPassword();
            Assert.fail("Should have thrown up");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), Matchers.is("Could not decrypt the password to get the real password"));
        }
    }

    @Test
    public void shouldErrorOutIfEncryptionFails() throws Exception {
        GoCipher mockGoCipher = Mockito.mock(GoCipher.class);
        Mockito.when(mockGoCipher.encrypt("password")).thenThrow(new CryptoException("exception"));
        try {
            new TfsMaterial(mockGoCipher, new UrlArgument("/foo"), "username", DOMAIN, "password", "");
            Assert.fail("Should have thrown up");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), Matchers.is("Password encryption failed. Please verify your cipher key."));
        }
    }

    @Test
    public void shouldBePasswordAware() {
        Assert.assertThat(PasswordAwareMaterial.class.isAssignableFrom(TfsMaterial.class), Matchers.is(true));
    }

    @Test
    public void shouldBePasswordEncrypter() {
        Assert.assertThat(PasswordEncrypter.class.isAssignableFrom(TfsMaterial.class), Matchers.is(true));
    }

    @Test
    public void shouldKnowItsType() {
        Assert.assertThat(tfsMaterialFirstCollectionFirstProject.getTypeForDisplay(), Matchers.is("Tfs"));
    }

    @Test
    public void shouldCheckConnection() {
        TestSubprocessExecutionContext execCtx = new TestSubprocessExecutionContext();
        TfsCommand tfsCommand = Mockito.mock(TfsCommand.class);
        Mockito.doNothing().when(tfsCommand).checkConnection();
        TfsMaterial spy = Mockito.spy(tfsMaterialFirstCollectionFirstProject);
        Mockito.doReturn(tfsCommand).when(spy).tfs(execCtx);
        Assert.assertThat(spy.checkConnection(execCtx), Matchers.is(ValidationBean.valid()));
        Mockito.verify(tfsCommand, Mockito.times(1)).checkConnection();
    }

    @Test
    public void shouldGetLongDescriptionForMaterial() {
        TfsMaterial material = new TfsMaterial(new GoCipher(), new UrlArgument("http://url/"), "user", "domain", "password", "$project/path/");
        Assert.assertThat(material.getLongDescription(), Matchers.is("URL: http://url/, Username: user, Domain: domain, ProjectPath: $project/path/"));
    }

    @Test
    public void shouldCopyOverPasswordWhenConvertingToConfig() throws Exception {
        TfsMaterial material = new TfsMaterial(new GoCipher(), new UrlArgument("http://url/"), "user", "domain", "password", "$project/path/");
        TfsMaterialConfig config = ((TfsMaterialConfig) (material.config()));
        Assert.assertThat(config.getPassword(), Matchers.is("password"));
        Assert.assertThat(config.getEncryptedPassword(), Matchers.is(Matchers.not(Matchers.nullValue())));
    }

    @Test
    public void shouldGetAttributesWithSecureFields() {
        TfsMaterial material = new TfsMaterial(new GoCipher(), new UrlArgument("http://username:password@tfsrepo.com"), "username", "domain", "password", "$project/path/");
        Map<String, Object> attributes = material.getAttributes(true);
        Assert.assertThat(attributes.get("type"), Matchers.is("tfs"));
        Map<String, Object> configuration = ((Map<String, Object>) (attributes.get("tfs-configuration")));
        Assert.assertThat(configuration.get("url"), Matchers.is("http://username:password@tfsrepo.com"));
        Assert.assertThat(configuration.get("domain"), Matchers.is("domain"));
        Assert.assertThat(configuration.get("username"), Matchers.is("username"));
        Assert.assertThat(configuration.get("password"), Matchers.is("password"));
        Assert.assertThat(configuration.get("project-path"), Matchers.is("$project/path/"));
    }

    @Test
    public void shouldGetAttributesWithoutSecureFields() {
        TfsMaterial material = new TfsMaterial(new GoCipher(), new UrlArgument("http://username:password@tfsrepo.com"), "username", "domain", "password", "$project/path/");
        Map<String, Object> attributes = material.getAttributes(false);
        Assert.assertThat(attributes.get("type"), Matchers.is("tfs"));
        Map<String, Object> configuration = ((Map<String, Object>) (attributes.get("tfs-configuration")));
        Assert.assertThat(configuration.get("url"), Matchers.is("http://username:******@tfsrepo.com"));
        Assert.assertThat(configuration.get("domain"), Matchers.is("domain"));
        Assert.assertThat(configuration.get("username"), Matchers.is("username"));
        Assert.assertThat(configuration.get("password"), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(configuration.get("project-path"), Matchers.is("$project/path/"));
    }
}

