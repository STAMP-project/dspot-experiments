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
package com.thoughtworks.go.config;


import ServerConfig.JOB_TIMEOUT;
import ServerConfig.PURGE_START;
import com.thoughtworks.go.domain.ServerSiteUrlConfig;
import com.thoughtworks.go.security.GoCipher;
import com.thoughtworks.go.util.SystemEnvironment;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.annotation.PostConstruct;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;


public class ServerConfigTest {
    private ServerConfig defaultServerConfig;

    private ServerConfig another;

    @Test
    public void shouldReturnSiteUrlAsSecurePreferedSiteUrlIfSecureSiteUrlIsNotDefined() {
        defaultServerConfig.setSiteUrl("http://example.com");
        defaultServerConfig.setSecureSiteUrl(null);
        Assert.assertThat(defaultServerConfig.getSiteUrlPreferablySecured().getUrl(), is("http://example.com"));
    }

    @Test
    public void shouldReturnSecureSiteUrlAsSecurePreferedSiteUrlIfBothSiteUrlAndSecureSiteUrlIsDefined() {
        defaultServerConfig.setSiteUrl("http://example.com");
        defaultServerConfig.setSecureSiteUrl("https://example.com");
        Assert.assertThat(defaultServerConfig.getSiteUrlPreferablySecured().getUrl(), is("https://example.com"));
    }

    @Test
    public void shouldReturnBlankUrlBothSiteUrlAndSecureSiteUrlIsNotDefined() {
        defaultServerConfig.setSiteUrl(null);
        defaultServerConfig.setSecureSiteUrl(null);
        Assert.assertThat(defaultServerConfig.getSiteUrlPreferablySecured().hasNonNullUrl(), is(false));
    }

    @Test
    public void shouldReturnAnEmptyForSecureSiteUrlIfOnlySiteUrlIsConfigured() throws Exception {
        ServerConfig serverConfig = new ServerConfig(null, null, new ServerSiteUrlConfig("http://foo.bar:813"), new ServerSiteUrlConfig());
        Assert.assertThat(serverConfig.getHttpsUrl(), is(new ServerSiteUrlConfig()));
    }

    @Test
    public void shouldReturnDefaultTaskRepositoryLocation() {
        ServerConfig serverConfig = new ServerConfig(null, null, new ServerSiteUrlConfig("http://foo.bar:813"), new ServerSiteUrlConfig());
        Assert.assertThat(serverConfig.getCommandRepositoryLocation(), is("default"));
    }

    @Test
    public void shouldReturnTaskRepositoryLocation() {
        ServerConfig serverConfig = new ServerConfig(null, null, new ServerSiteUrlConfig("http://foo.bar:813"), new ServerSiteUrlConfig());
        serverConfig.setCommandRepositoryLocation("foo");
        Assert.assertThat(serverConfig.getCommandRepositoryLocation(), is("foo"));
    }

    @Test
    public void shouldIgnoreErrorsFieldOnEquals() throws Exception {
        ServerConfig one = new ServerConfig(new SecurityConfig(), new MailHost(new GoCipher()), new ServerSiteUrlConfig("siteURL"), new ServerSiteUrlConfig("secureURL"));
        one.addError("siteUrl", "I dont like this url");
        Assert.assertThat(one, is(new ServerConfig(new SecurityConfig(), new MailHost(new GoCipher()), new ServerSiteUrlConfig("siteURL"), new ServerSiteUrlConfig("secureURL"))));
    }

    @Test
    public void shouldNotUpdatePasswordForMailHostIfNotChangedOrNull() throws IOException {
        File cipherFile = new SystemEnvironment().getDESCipherFile();
        FileUtils.deleteQuietly(cipherFile);
        FileUtils.writeStringToFile(cipherFile, "269298bc31c44620", StandardCharsets.UTF_8);
        GoCipher goCipher = new GoCipher();
        MailHost mailHost = new MailHost("abc", 12, "admin", "p", null, true, true, "anc@mail.com", "anc@mail.com", goCipher);
        ServerConfig serverConfig = new ServerConfig(null, mailHost, null, null);
        Assert.assertThat(serverConfig.mailHost().getPassword(), is("p"));
        String encryptedPassword = serverConfig.mailHost().getEncryptedPassword();
        serverConfig.updateMailHost(/* Password Not Changed */
        new MailHost("abc", 12, "admin", "p", encryptedPassword, false, true, "anc@mail.com", "anc@mail.com", goCipher));
        Assert.assertThat(serverConfig.mailHost().getPassword(), is("p"));
        Assert.assertThat(serverConfig.mailHost().getEncryptedPassword(), is(encryptedPassword));
        serverConfig.updateMailHost(new MailHost("abc", 12, "admin", null, "", true, true, "anc@mail.com", "anc@mail.com"));
        Assert.assertThat(serverConfig.mailHost().getPassword(), is(nullValue()));
        Assert.assertThat(serverConfig.mailHost().getEncryptedPassword(), is(nullValue()));
    }

    @Test
    public void shouldAllowArtifactPurgingIfPurgeParametersAreDefined() {
        another = new ServerConfig("artifacts", new SecurityConfig(), 10.0, 20.0);
        Assert.assertThat(another.isArtifactPurgingAllowed(), is(true));
        another = new ServerConfig("artifacts", new SecurityConfig(), null, 20.0);
        Assert.assertThat(another.isArtifactPurgingAllowed(), is(false));
        another = new ServerConfig("artifacts", new SecurityConfig(), 10.0, null);
        Assert.assertThat(another.isArtifactPurgingAllowed(), is(false));
        another = new ServerConfig("artifacts", new SecurityConfig(), null, null);
        Assert.assertThat(another.isArtifactPurgingAllowed(), is(false));
    }

    @Test
    public void shouldGetTheDefaultJobTimeoutValue() {
        Assert.assertThat(getJobTimeout(), is("0"));
        Assert.assertThat(getJobTimeout(), is("30"));
    }

    @Test
    public void shouldValidateThatTimeoutIsValidIfItsANumber() {
        ServerConfig serverConfig = new ServerConfig("artifacts", new SecurityConfig(), 10, 20, "30");
        serverConfig.validate(null);
        Assert.assertThat(serverConfig.errors().isEmpty(), is(true));
    }

    @Test
    public void shouldValidateThatTimeoutIsInvalidIfItsNotAValidNumber() {
        ServerConfig serverConfig = new ServerConfig("artifacts", new SecurityConfig(), 10, 20, "30M");
        serverConfig.validate(null);
        Assert.assertThat(serverConfig.errors().isEmpty(), is(false));
        Assert.assertThat(serverConfig.errors().on(JOB_TIMEOUT), is("Timeout should be a valid number as it represents number of minutes"));
    }

    @Test
    public void validate_shouldFailIfThePurgeStartIsBiggerThanPurgeUpto() {
        ServerConfig serverConfig = new ServerConfig("artifacts", new SecurityConfig(), 20.1, 20.05, "30");
        serverConfig.validate(null);
        Assert.assertThat(serverConfig.errors().isEmpty(), is(false));
        Assert.assertThat(serverConfig.errors().on(PURGE_START), is("Error in artifact cleanup values. The trigger value (20.1GB) should be less than the goal (20.05GB)"));
    }

    @Test
    public void validate_shouldFailIfThePurgeStartIsNotSpecifiedButPurgeUptoIs() {
        ServerConfig serverConfig = new ServerConfig("artifacts", new SecurityConfig(), null, 20.05);
        serverConfig.validate(null);
        Assert.assertThat(serverConfig.errors().isEmpty(), is(false));
        Assert.assertThat(serverConfig.errors().on(PURGE_START), is("Error in artifact cleanup values. The trigger value is has to be specified when a goal is set"));
    }

    @Test
    public void validate_shouldFailIfThePurgeStartIs0SpecifiedButPurgeUptoIs() {
        ServerConfig serverConfig = new ServerConfig("artifacts", new SecurityConfig(), 0, 20.05, "30");
        serverConfig.validate(null);
        Assert.assertThat(serverConfig.errors().isEmpty(), is(false));
        Assert.assertThat(serverConfig.errors().on(PURGE_START), is("Error in artifact cleanup values. The trigger value is has to be specified when a goal is set"));
    }

    @Test
    public void validate_shouldPassIfThePurgeStartIsSmallerThanPurgeUpto() {
        ServerConfig serverConfig = new ServerConfig("artifacts", new SecurityConfig(), 20.0, 20.05, "30");
        serverConfig.validate(null);
        Assert.assertThat(serverConfig.errors().isEmpty(), is(true));
    }

    @Test
    public void validate_shouldPassIfThePurgeStartAndPurgeUptoAreBothNotSet() {
        ServerConfig serverConfig = new ServerConfig("artifacts", new SecurityConfig());
        serverConfig.validate(null);
        Assert.assertThat(serverConfig.errors().isEmpty(), is(true));
    }

    @Test
    public void should_useServerId_forEqualityCheck() {
        ServerConfig configWithoutServerId = new ServerConfig();
        ServerConfig configWithServerId = new ServerConfig();
        configWithServerId.ensureServerIdExists();
        Assert.assertThat(configWithoutServerId, not(configWithServerId));
    }

    @Test
    public void shouldEnsureAgentAutoregisterKeyExists() throws Exception {
        ServerConfig serverConfig = new ServerConfig();
        Assert.assertNull(serverConfig.getAgentAutoRegisterKey());
        Assert.assertNotNull(serverConfig.getClass().getMethod("ensureAgentAutoregisterKeyExists").getAnnotation(PostConstruct.class));
        serverConfig.ensureAgentAutoregisterKeyExists();
        Assert.assertTrue(StringUtils.isNotBlank(serverConfig.getAgentAutoRegisterKey()));
    }

    @Test
    public void shouldEnsureWebhookSecretExists() throws Exception {
        ServerConfig serverConfig = new ServerConfig();
        Assert.assertNull(serverConfig.getWebhookSecret());
        Assert.assertNotNull(serverConfig.getClass().getMethod("ensureWebhookSecretExists").getAnnotation(PostConstruct.class));
        serverConfig.ensureWebhookSecretExists();
        Assert.assertTrue(StringUtils.isNotBlank(serverConfig.getWebhookSecret()));
    }

    @Test
    public void shouldEnsureTokenGenerationKeyExists() throws Exception {
        ServerConfig serverConfig = new ServerConfig();
        Assert.assertNull(serverConfig.getTokenGenerationKey());
        Assert.assertNotNull(serverConfig.getClass().getMethod("ensureTokenGenerationKeyExists").getAnnotation(PostConstruct.class));
        serverConfig.ensureTokenGenerationKeyExists();
        Assert.assertTrue(StringUtils.isNotBlank(serverConfig.getTokenGenerationKey()));
    }
}

