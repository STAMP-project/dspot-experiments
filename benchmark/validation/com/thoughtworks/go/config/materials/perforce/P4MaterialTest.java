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


import com.thoughtworks.go.domain.materials.Modification;
import com.thoughtworks.go.domain.materials.perforce.P4Client;
import com.thoughtworks.go.domain.materials.perforce.PerforceFixture;
import com.thoughtworks.go.helper.MaterialsMother;
import com.thoughtworks.go.security.GoCipher;
import com.thoughtworks.go.util.ReflectionUtil;
import com.thoughtworks.go.util.command.EnvironmentVariableContext;
import com.thoughtworks.go.util.command.InMemoryStreamConsumer;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class P4MaterialTest extends P4MaterialTestBase {
    @Test
    public void shouldAddServerSideEnvironmentVariablesClientNameEnvironmentVariable() throws IOException {
        File p4_working_dir = temporaryFolder.newFolder();
        P4Material p4 = new P4Material("host:10", "beautiful", "user");
        p4.setPassword("loser");
        EnvironmentVariableContext envVarCtx;
        envVarCtx = new EnvironmentVariableContext();
        p4.populateEnvironmentContext(envVarCtx, new com.thoughtworks.go.domain.MaterialRevision(p4, new Modification("loser", "loserish commit", "loser@boozer.com", new Date(), "123")), p4_working_dir);
        Assert.assertThat(envVarCtx.getProperty("GO_REVISION"), Matchers.is("123"));
        Assert.assertThat(envVarCtx.getProperty("GO_TO_REVISION"), Matchers.is("123"));
        Assert.assertThat(envVarCtx.getProperty("GO_FROM_REVISION"), Matchers.is("123"));
    }

    @Test
    public void shouldAddClientNameEnvironmentVariable() throws IOException {
        File p4_working_dir = temporaryFolder.newFolder();
        P4Material p4 = new P4Material("host:10", "beautiful", "user");
        p4.setPassword("loser");
        EnvironmentVariableContext envVarCtx;
        envVarCtx = new EnvironmentVariableContext();
        p4.populateAgentSideEnvironmentContext(envVarCtx, p4_working_dir);
        Assert.assertThat(envVarCtx.getProperty("GO_P4_CLIENT"), Matchers.is(p4.clientName(p4_working_dir)));
    }

    @Test
    public void shouldGenerateTheSameP4ClientValueForCommandAndEnvironment() throws Exception {
        P4Material p4Material = new P4Material("server:10", "out-of-the-window");
        ReflectionUtil.setField(p4Material, "folder", "crapy_dir");
        P4Client p4Client = p4Material._p4(tempDir, new InMemoryStreamConsumer(), false);
        Assert.assertThat(p4Client, Matchers.is(Matchers.not(Matchers.nullValue())));
        String client = ((String) (ReflectionUtil.getField(p4Client, "p4ClientName")));
        Assert.assertThat(client, Matchers.is(p4Material.clientName(tempDir)));
    }

    @Test
    public void shouldNotDisplayPasswordInStringRepresentation() {
        P4Material p4 = new P4Material("host:10", "beautiful");
        p4.setUsername("user");
        p4.setPassword("loser");
        Assert.assertThat(p4.toString(), Matchers.not(Matchers.containsString("loser")));
    }

    @Test
    public void shouldEncryptP4Password() throws Exception {
        GoCipher mockGoCipher = Mockito.mock(GoCipher.class);
        Mockito.when(mockGoCipher.encrypt("password")).thenReturn("encrypted");
        Mockito.when(mockGoCipher.maybeReEncryptForPostConstructWithoutExceptions("encrypted")).thenReturn("encrypted");
        P4Material p4Material = new P4Material("example.com:1818", "view", mockGoCipher);
        p4Material.setPassword("password");
        p4Material.ensureEncrypted();
        Assert.assertThat(p4Material.getEncryptedPassword(), Matchers.is("encrypted"));
        Assert.assertThat(p4Material.getPassword(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldDecryptP4Password() throws Exception {
        GoCipher mockGoCipher = Mockito.mock(GoCipher.class);
        Mockito.when(mockGoCipher.decrypt("encrypted")).thenReturn("password");
        P4Material p4Material = new P4Material("example.com:1818", "view", mockGoCipher);
        ReflectionUtil.setField(p4Material, "encryptedPassword", "encrypted");
        p4Material.getPassword();
        Assert.assertThat(p4Material.getPassword(), Matchers.is("password"));
    }

    @Test
    public void shouldReturnEqualsEvenIfPasswordsAreDifferent() throws Exception {
        P4Material material = MaterialsMother.p4Material();
        material.setPassword("password");
        P4Material other = MaterialsMother.p4Material();
        other.setPassword("password1");
        Assert.assertThat(material, Matchers.is(other));
    }

    @Test
    public void shouldNotConsiderPasswordForEqualityCheck() {
        P4Material one = new P4Material("host:123", "through_window");
        one.setPassword("password");
        P4Material two = new P4Material("host:123", "through_window");
        two.setPassword("wordpass");
        Assert.assertThat(one, Matchers.is(two));
        Assert.assertThat(one.hashCode(), Matchers.is(two.hashCode()));
    }

    @Test
    public void shouldGetLongDescriptionForMaterial() {
        P4Material material = new P4Material("host:123", "through_window", "user", "folder");
        Assert.assertThat(material.getLongDescription(), Matchers.is("URL: host:123, View: through_window, Username: user"));
    }

    @Test
    public void shouldCopyOverPasswordWhenConvertingToConfig() throws Exception {
        P4Material material = new P4Material("blah.com", "view");
        material.setPassword("password");
        P4MaterialConfig config = ((P4MaterialConfig) (material.config()));
        Assert.assertThat(config.getPassword(), Matchers.is("password"));
        Assert.assertThat(config.getEncryptedPassword(), Matchers.is(Matchers.not(Matchers.nullValue())));
    }

    @Test
    public void shouldGetAttributesWithSecureFields() {
        P4Material material = new P4Material("host:1234", "view", "username");
        material.setPassword("password");
        material.setUseTickets(true);
        Map<String, Object> attributes = material.getAttributes(true);
        Assert.assertThat(attributes.get("type"), Matchers.is("perforce"));
        Map<String, Object> configuration = ((Map<String, Object>) (attributes.get("perforce-configuration")));
        Assert.assertThat(configuration.get("url"), Matchers.is("host:1234"));
        Assert.assertThat(configuration.get("username"), Matchers.is("username"));
        Assert.assertThat(configuration.get("password"), Matchers.is("password"));
        Assert.assertThat(configuration.get("view"), Matchers.is("view"));
        Assert.assertThat(configuration.get("use-tickets"), Matchers.is(true));
    }

    @Test
    public void shouldGetAttributesWithoutSecureFields() {
        P4Material material = new P4Material("host:1234", "view", "username");
        material.setPassword("password");
        material.setUseTickets(true);
        Map<String, Object> attributes = material.getAttributes(false);
        Assert.assertThat(attributes.get("type"), Matchers.is("perforce"));
        Map<String, Object> configuration = ((Map<String, Object>) (attributes.get("perforce-configuration")));
        Assert.assertThat(configuration.get("url"), Matchers.is("host:1234"));
        Assert.assertThat(configuration.get("username"), Matchers.is("username"));
        Assert.assertThat(configuration.get("password"), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(configuration.get("view"), Matchers.is("view"));
        Assert.assertThat(configuration.get("use-tickets"), Matchers.is(true));
    }

    @Test
    public void shouldSetGO_P4_CLIENT_toTheClientName() {
        P4Material material = new P4Material("host:1234", "view", "username", "destination");
        EnvironmentVariableContext environmentVariableContext = new EnvironmentVariableContext();
        File agentWorkingDirectory = new File("pipelines/pipeline-name");
        material.populateAgentSideEnvironmentContext(environmentVariableContext, agentWorkingDirectory);
        Assert.assertThat(environmentVariableContext.getProperty("GO_P4_CLIENT_DESTINATION"), Matchers.is(material.clientName(material.workingdir(agentWorkingDirectory))));
    }
}

