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
package com.thoughtworks.go.domain.materials.svn;


import SvnMaterialConfig.CHECK_EXTERNALS;
import SvnMaterialConfig.URL;
import SvnMaterialConfig.USERNAME;
import com.thoughtworks.go.config.materials.svn.SvnMaterial;
import com.thoughtworks.go.config.materials.svn.SvnMaterialConfig;
import com.thoughtworks.go.domain.materials.Material;
import com.thoughtworks.go.helper.MaterialConfigsMother;
import com.thoughtworks.go.helper.MaterialsMother;
import com.thoughtworks.go.security.CryptoException;
import com.thoughtworks.go.security.GoCipher;
import com.thoughtworks.go.util.JsonUtils;
import com.thoughtworks.go.util.JsonValue;
import com.thoughtworks.go.util.ReflectionUtil;
import com.thoughtworks.go.util.command.InMemoryStreamConsumer;
import com.thoughtworks.go.util.command.ProcessOutputStreamConsumer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.codec.digest.DigestUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;


public class SvnMaterialTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Subversion subversion;

    private SvnMaterial svnMaterial;

    private static final String URL = "svn://something";

    SubversionRevision revision = new SubversionRevision("1");

    private InMemoryStreamConsumer outputStreamConsumer = ProcessOutputStreamConsumer.inMemoryConsumer();

    @Test
    public void shouldNotDisplayPasswordInStringRepresentation() {
        SvnMaterial svn = new SvnMaterial("my-url", "user", "loser", false);
        Assert.assertThat(svn.toString(), Matchers.not(Matchers.containsString("loser")));
        svn = new SvnMaterial("https://user:loser@foo.bar/baz?quux=bang", "user", "loser", false);
        Assert.assertThat(svn.toString(), Matchers.not(Matchers.containsString("loser")));
    }

    @Test
    public void shouldCheckoutWhenFolderDoesNotExist() {
        final File workingCopy = new File("xyz");
        updateMaterial(svnMaterial, revision, workingCopy);
        Mockito.verify(subversion).checkoutTo(outputStreamConsumer, workingCopy, revision);
    }

    @Test
    public void shouldLogRepoInfoToConsoleOutWithOutFolder() throws Exception {
        final File workingCopy = new File("xyz");
        updateMaterial(svnMaterial, revision, workingCopy);
        String stdout = outputStreamConsumer.getStdOut();
        Assert.assertThat(stdout, Matchers.containsString(String.format("Start updating %s at revision %s from %s", "files", revision.getRevision(), svnMaterial.getUrl())));
        Mockito.verify(subversion).checkoutTo(outputStreamConsumer, workingCopy, revision);
    }

    @Test
    public void shouldCheckoutForInvalidSvnWorkingCopy() throws IOException {
        final File workingCopy = createSvnWorkingCopy(false);
        updateMaterial(svnMaterial, revision, workingCopy);
        Assert.assertThat(workingCopy.exists(), Matchers.is(false));
        Mockito.verify(subversion).checkoutTo(outputStreamConsumer, workingCopy, revision);
    }

    @Test
    public void shouldCheckoutIfSvnRepositoryChanged() throws IOException {
        final File workingCopy = createSvnWorkingCopy(true);
        Mockito.when(subversion.workingRepositoryUrl(workingCopy)).thenReturn("new url");
        updateMaterial(svnMaterial, revision, workingCopy);
        Assert.assertThat(workingCopy.exists(), Matchers.is(false));
        Mockito.verify(subversion).checkoutTo(outputStreamConsumer, workingCopy, revision);
    }

    @Test
    public void shouldUpdateForValidSvnWorkingCopy() throws IOException {
        final File workingCopy = createSvnWorkingCopy(true);
        Mockito.when(subversion.workingRepositoryUrl(workingCopy)).thenReturn(SvnMaterialTest.URL);
        updateMaterial(svnMaterial, revision, workingCopy);
        Mockito.verify(subversion).cleanupAndRevert(outputStreamConsumer, workingCopy);
        Mockito.verify(subversion).updateTo(outputStreamConsumer, workingCopy, revision);
    }

    @Test
    public void shouldBeEqualWhenUrlSameForSvnMaterial() throws Exception {
        final Material material1 = MaterialsMother.defaultSvnMaterialsWithUrl("url1").get(0);
        final Material material = MaterialsMother.defaultSvnMaterialsWithUrl("url1").get(0);
        assertComplementaryEquals(material1, material, true);
    }

    @Test
    public void shouldNotBeEqualWhenUrlDifferent() throws Exception {
        final Material material1 = MaterialsMother.defaultSvnMaterialsWithUrl("url1").get(0);
        final Material material2 = MaterialsMother.defaultSvnMaterialsWithUrl("url2").get(0);
        assertComplementaryEquals(material1, material2, false);
    }

    @Test
    public void shouldNotBeEqualWhenTypeDifferent() throws Exception {
        final Material hgMaterial = MaterialsMother.hgMaterials("url1", "hgdir").get(0);
        final Material nonHgMaterial = MaterialsMother.defaultSvnMaterialsWithUrl("url1").get(0);
        assertComplementaryEquals(hgMaterial, nonHgMaterial, false);
    }

    @Test
    public void shouldNotBeEqualWhenAlternateFolderDifferent() throws Exception {
        final SvnMaterial material1 = MaterialsMother.svnMaterial("url1");
        final SvnMaterial material2 = MaterialsMother.svnMaterial("url1");
        assertComplementaryEquals(material1, material2, true);
        material1.setFolder("foo");
        material2.setFolder(null);
        assertComplementaryEquals(material1, material2, false);
        material1.setFolder("foo");
        material2.setFolder("bar");
        assertComplementaryEquals(material1, material2, false);
    }

    @Test
    public void shouldSerializeAndDeserializeCorrectly() throws Exception {
        final SvnMaterial material1 = MaterialsMother.svnMaterial("url1", "foo");
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        ObjectOutputStream serialized = new ObjectOutputStream(buf);
        serialized.writeObject(material1);
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buf.toByteArray()));
        Assert.assertThat(in.readObject(), Matchers.is(material1));
    }

    @Test
    public void shouldReturnNotEqualsWhenUrlIsChanged() throws Exception {
        SvnMaterial material = MaterialsMother.svnMaterial("A");
        SvnMaterial other = MaterialsMother.svnMaterial("B");
        Assert.assertThat(material, Matchers.is(Matchers.not(other)));
    }

    @Test
    public void shouldReturnNotEqualsWhenUserNameIsChanged() throws Exception {
        SvnMaterial material = MaterialsMother.svnMaterial("url", "svnDir", "userName", null, false, "*.txt");
        SvnMaterial other = MaterialsMother.svnMaterial("url", "svnDir", "userName1", null, false, "*.txt");
        Assert.assertThat(material, Matchers.is(Matchers.not(other)));
    }

    @Test
    public void shouldReturnEqualsEvenIfPasswordsAreDifferent() throws Exception {
        SvnMaterial material = MaterialsMother.svnMaterial();
        material.setPassword("password");
        SvnMaterial other = MaterialsMother.svnMaterial();
        other.setPassword("password1");
        Assert.assertThat(material, Matchers.is(other));
    }

    @Test
    public void shouldReturnNotEqualsWhenCheckExternalsIsChanged() throws Exception {
        SvnMaterial material = MaterialsMother.svnMaterial("url", "svnDir", null, null, true, "*.txt");
        SvnMaterial other = MaterialsMother.svnMaterial("url", "svnDir", null, null, false, "*.txt");
        Assert.assertThat(material, Matchers.is(Matchers.not(other)));
    }

    @Test
    public void shouldReturnEqualsWhenEverythingIsSame() throws Exception {
        SvnMaterial material = MaterialsMother.svnMaterial("URL", "dummy-folder", "userName", "password", true, "*.doc");
        SvnMaterial other = MaterialsMother.svnMaterial("URL", "dummy-folder", "userName", "password", true, "*.doc");
        Assert.assertThat(other, Matchers.is(material));
    }

    /* TODO: *SBD* Move this test into SvnMaterialConfig test after mothers are moved. */
    @Test
    public void shouldReturnEqualsWhenEverythingIsSameForSvnMaterialConfigs() throws Exception {
        SvnMaterialConfig svnMaterialConfig = MaterialConfigsMother.svnMaterialConfig();
        svnMaterialConfig.setConfigAttributes(Collections.singletonMap(CHECK_EXTERNALS, String.valueOf(true)));
        svnMaterialConfig.setConfigAttributes(Collections.singletonMap(USERNAME, "userName"));
        svnMaterialConfig.setPassword("password");
        svnMaterialConfig.setConfigAttributes(Collections.singletonMap(SvnMaterialConfig.URL, "URL"));
        SvnMaterialConfig other = MaterialConfigsMother.svnMaterialConfig();
        other.setConfigAttributes(Collections.singletonMap(CHECK_EXTERNALS, String.valueOf(true)));
        other.setConfigAttributes(Collections.singletonMap(USERNAME, "userName"));
        other.setPassword("password");
        other.setConfigAttributes(Collections.singletonMap(SvnMaterialConfig.URL, "URL"));
        Assert.assertThat(other, Matchers.is(svnMaterialConfig));
    }

    @Test
    public void shouldBeAbleToConvertToJson() {
        SvnMaterial material = MaterialsMother.svnMaterial("url");
        Map<String, Object> json = new LinkedHashMap<>();
        material.toJson(json, revision);
        JsonValue jsonValue = JsonUtils.from(json);
        Assert.assertThat(jsonValue.getString("scmType"), Matchers.is("Subversion"));
        Assert.assertThat(new File(jsonValue.getString("location")), Matchers.is(new File(material.getUrl())));
        Assert.assertThat(jsonValue.getString("action"), Matchers.is("Modified"));
    }

    @Test
    public void shouldAddTheForwardSlashAndApplyThePattern() throws Exception {
        SvnMaterial material = MaterialsMother.svnMaterial();
        Assert.assertThat(material.matches("/a.doc", "a.doc"), Matchers.is(true));
        Assert.assertThat(material.matches("a.doc", "a.doc"), Matchers.is(false));
    }

    @Test
    public void shouldApplyThePatternDirectly() throws Exception {
        SvnMaterial material = MaterialsMother.svnMaterial();
        Assert.assertThat(material.matches("/a.doc", "/a.doc"), Matchers.is(true));
    }

    @Test
    public void shouldGenerateSqlCriteriaMapInSpecificOrder() throws Exception {
        SvnMaterial material = new SvnMaterial("url", "username", "password", true);
        Map<String, Object> map = material.getSqlCriteria();
        Assert.assertThat(map.size(), Matchers.is(4));
        Iterator<Map.Entry<String, Object>> iter = map.entrySet().iterator();
        Assert.assertThat(iter.next().getKey(), Matchers.is("type"));
        Assert.assertThat(iter.next().getKey(), Matchers.is("url"));
        Assert.assertThat(iter.next().getKey(), Matchers.is("username"));
        Assert.assertThat(iter.next().getKey(), Matchers.is("checkExternals"));
    }

    @Test
    public void shouldGenerateFingerprintBasedOnSqlCriteria() throws Exception {
        SvnMaterial one = new SvnMaterial("url", "username", "password", true);
        SvnMaterial two = new SvnMaterial("url", "username", "password", false);
        Assert.assertThat(one.getFingerprint(), Matchers.is(Matchers.not(two.getFingerprint())));
        Assert.assertThat(one.getFingerprint(), Matchers.is(DigestUtils.sha256Hex("type=SvnMaterial<|>url=url<|>username=username<|>checkExternals=true")));
    }

    @Test
    public void shouldGeneratePipelineUniqueFingerprintBasedOnFingerprintAndDest() throws Exception {
        SvnMaterial one = new SvnMaterial("url", "username", "password", true, "folder1");
        SvnMaterial two = new SvnMaterial("url", "username", "password", true, "folder2");
        Assert.assertThat(one.getPipelineUniqueFingerprint(), Matchers.is(Matchers.not(two.getFingerprint())));
        Assert.assertThat(one.getPipelineUniqueFingerprint(), Matchers.is(DigestUtils.sha256Hex("type=SvnMaterial<|>url=url<|>username=username<|>checkExternals=true<|>dest=folder1")));
    }

    @Test
    public void shouldNotUsePasswordForEquality() {
        SvnMaterial svnBoozer = new SvnMaterial("foo.com", "loser", "boozer", true);
        SvnMaterial svnZooser = new SvnMaterial("foo.com", "loser", "zooser", true);
        Assert.assertThat(svnBoozer.hashCode(), Matchers.is(svnZooser.hashCode()));
        Assert.assertThat(svnBoozer, Matchers.is(svnZooser));
    }

    @Test
    public void shouldEncryptSvnPasswordAndMarkPasswordAsNull() throws Exception {
        GoCipher mockGoCipher = Mockito.mock(GoCipher.class);
        Mockito.when(mockGoCipher.encrypt("password")).thenReturn("encrypted");
        Mockito.when(mockGoCipher.maybeReEncryptForPostConstructWithoutExceptions("encrypted")).thenReturn("encrypted");
        SvnMaterial material = new SvnMaterial("/foo", "username", "password", false, mockGoCipher);
        material.ensureEncrypted();
        Assert.assertThat(material.getPassword(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(material.getEncryptedPassword(), Matchers.is("encrypted"));
    }

    @Test
    public void shouldDecryptSvnPassword() throws Exception {
        GoCipher mockGoCipher = Mockito.mock(GoCipher.class);
        Mockito.when(mockGoCipher.decrypt("encrypted")).thenReturn("password");
        Mockito.when(mockGoCipher.maybeReEncryptForPostConstructWithoutExceptions("encrypted")).thenReturn("encrypted");
        SvnMaterial material = new SvnMaterial("/foo", "username", null, false, mockGoCipher);
        ReflectionUtil.setField(material, "encryptedPassword", "encrypted");
        material.ensureEncrypted();
        Assert.assertThat(material.getPassword(), Matchers.is("password"));
    }

    @Test
    public void shouldNotDecryptSvnPasswordIfPasswordIsNotNull() throws Exception {
        GoCipher mockGoCipher = Mockito.mock(GoCipher.class);
        Mockito.when(mockGoCipher.encrypt("password")).thenReturn("encrypted");
        Mockito.when(mockGoCipher.decrypt("encrypted")).thenReturn("password");
        SvnMaterial material = new SvnMaterial("/foo", "username", "password", false, mockGoCipher);
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
        SvnMaterial material = new SvnMaterial("/foo", "username", null, false, mockGoCipher);
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
            new SvnMaterial("/foo", "username", "password", false, mockGoCipher);
            Assert.fail("Should have thrown up");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), Matchers.is("Password encryption failed. Please verify your cipher key."));
        }
    }

    @Test
    public void shouldGetLongDescriptionForMaterial() {
        SvnMaterial material = new SvnMaterial("http://url/", "user", "password", true, "folder");
        Assert.assertThat(material.getLongDescription(), Matchers.is("URL: http://url/, Username: user, CheckExternals: true"));
    }

    @Test
    public void shouldCopyOverPasswordWhenConvertingToConfig() throws Exception {
        SvnMaterial material = new SvnMaterial("abc", "def", "ghi", false);
        SvnMaterialConfig config = ((SvnMaterialConfig) (material.config()));
        Assert.assertThat(config.getEncryptedPassword(), Matchers.is(Matchers.not(Matchers.nullValue())));
        Assert.assertThat(config.getPassword(), Matchers.is("ghi"));
    }

    @Test
    public void shouldGetAttributesWithSecureFields() {
        SvnMaterial material = new SvnMaterial("http://username:password@svnrepo.com", "user", "password", true);
        Map<String, Object> attributes = material.getAttributes(true);
        Assert.assertThat(attributes.get("type"), Matchers.is("svn"));
        Map<String, Object> configuration = ((Map<String, Object>) (attributes.get("svn-configuration")));
        Assert.assertThat(configuration.get("url"), Matchers.is("http://username:password@svnrepo.com"));
        Assert.assertThat(configuration.get("username"), Matchers.is("user"));
        Assert.assertThat(configuration.get("password"), Matchers.is("password"));
        Assert.assertThat(configuration.get("check-externals"), Matchers.is(true));
    }

    @Test
    public void shouldGetAttributesWithoutSecureFields() {
        SvnMaterial material = new SvnMaterial("http://username:password@svnrepo.com", "user", "password", true);
        Map<String, Object> attributes = material.getAttributes(false);
        Assert.assertThat(attributes.get("type"), Matchers.is("svn"));
        Map<String, Object> configuration = ((Map<String, Object>) (attributes.get("svn-configuration")));
        Assert.assertThat(configuration.get("url"), Matchers.is("http://username:******@svnrepo.com"));
        Assert.assertThat(configuration.get("username"), Matchers.is("user"));
        Assert.assertThat(configuration.get("password"), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(configuration.get("check-externals"), Matchers.is(true));
    }
}

