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
package com.thoughtworks.go.util;


import GoConstants.USE_COMPRESSED_JAVASCRIPT;
import Level.DEBUG;
import Level.ERROR;
import Level.INFO;
import Level.WARN;
import SystemEnvironment.ACTIVEMQ_USE_JMX;
import SystemEnvironment.AGENT_CONNECTION_TIMEOUT_IN_SECONDS;
import SystemEnvironment.APP_SERVER;
import SystemEnvironment.ARTIFACT_FULL_SIZE_LIMIT;
import SystemEnvironment.CONFIGURATION_NO;
import SystemEnvironment.CONFIGURATION_YES;
import SystemEnvironment.CONFIG_DIR_PROPERTY;
import SystemEnvironment.CONFIG_FILE_PROPERTY;
import SystemEnvironment.CRUISE_CONFIG_REPO_DIR;
import SystemEnvironment.CRUISE_DB_CACHE_SIZE;
import SystemEnvironment.CRUISE_EXPERIMENTAL_ENABLE_ALL;
import SystemEnvironment.CRUISE_SERVER_SSL_PORT;
import SystemEnvironment.DATABASE_FULL_SIZE_LIMIT;
import SystemEnvironment.ENABLE_CONFIG_MERGE_FEATURE;
import SystemEnvironment.ENABLE_CONFIG_MERGE_PROPERTY;
import SystemEnvironment.GO_CHECK_UPDATES;
import SystemEnvironment.GO_CONFIG_REPO_GC_AGGRESSIVE;
import SystemEnvironment.GO_CONFIG_REPO_GC_EXPIRE;
import SystemEnvironment.GO_CONFIG_REPO_PERIODIC_GC;
import SystemEnvironment.GO_ENCRYPTION_API_MAX_REQUESTS;
import SystemEnvironment.GO_FETCH_ARTIFACT_TEMPLATE_AUTO_SUGGEST;
import SystemEnvironment.GO_SSL_EXCLUDE_CIPHERS;
import SystemEnvironment.GO_SSL_EXCLUDE_PROTOCOLS;
import SystemEnvironment.GO_SSL_INCLUDE_CIPHERS;
import SystemEnvironment.GO_SSL_INCLUDE_PROTOCOLS;
import SystemEnvironment.GO_SSL_RENEGOTIATION_ALLOWED;
import SystemEnvironment.GO_SSL_TRANSPORT_PROTOCOL_TO_BE_USED_BY_AGENT;
import SystemEnvironment.GO_UPDATE_SERVER_PUBLIC_KEY_FILE_NAME;
import SystemEnvironment.GO_UPDATE_SERVER_URL;
import SystemEnvironment.GoStringArraySystemProperty;
import SystemEnvironment.JETTY9;
import SystemEnvironment.JETTY_XML_FILE_NAME;
import SystemEnvironment.MATERIAL_UPDATE_IDLE_INTERVAL_PROPERTY;
import SystemEnvironment.RESOLVE_FANIN_REVISIONS;
import SystemEnvironment.TFS_SOCKET_TIMEOUT_IN_MILLISECONDS;
import SystemEnvironment.TFS_SOCKET_TIMEOUT_PROPERTY;
import SystemEnvironment.UNRESPONSIVE_JOB_WARNING_THRESHOLD;
import com.googlecode.junit.ext.JunitExtRunner;
import com.googlecode.junit.ext.RunIf;
import com.rits.cloning.Cloner;
import com.thoughtworks.go.junitext.DatabaseChecker;
import java.io.File;
import java.util.Properties;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static SystemEnvironment.GO_SSL_EXCLUDE_CIPHERS;
import static SystemEnvironment.GO_SSL_EXCLUDE_PROTOCOLS;
import static SystemEnvironment.GO_SSL_INCLUDE_CIPHERS;
import static SystemEnvironment.GO_SSL_INCLUDE_PROTOCOLS;


@RunWith(JunitExtRunner.class)
public class SystemEnvironmentTest {
    static final Cloner CLONER = new Cloner();

    private Properties original;

    private SystemEnvironment systemEnvironment;

    @Test
    public void shouldDisableNewFeaturesByDefault() {
        Assert.assertThat(systemEnvironment.isFeatureEnabled("cruise.experimental.feature.some-feature"), Matchers.is(false));
    }

    @Test
    public void shouldBeAbletoEnableAllNewFeatures() {
        Properties properties = new Properties();
        properties.setProperty(CRUISE_EXPERIMENTAL_ENABLE_ALL, "true");
        SystemEnvironment systemEnvironment = new SystemEnvironment(properties);
        Assert.assertThat(systemEnvironment.isFeatureEnabled("cruise.experimental.feature.some-feature"), Matchers.is(true));
    }

    @Test
    public void shouldFindJettyConfigInTheConfigDir() {
        Assert.assertThat(systemEnvironment.getJettyConfigFile(), Matchers.is(new File(systemEnvironment.getConfigDir(), "jetty.xml")));
        systemEnvironment.set(JETTY_XML_FILE_NAME, "jetty-old.xml");
        Assert.assertThat(systemEnvironment.getJettyConfigFile(), Matchers.is(new File(systemEnvironment.getConfigDir(), "jetty-old.xml")));
    }

    @Test
    public void shouldUnderstandOperatingSystem() {
        Assert.assertThat(systemEnvironment.getOperatingSystemName(), Matchers.is(System.getProperty("os.name")));
    }

    @Test
    public void shouldUnderstandWetherToUseCompressedJs() throws Exception {
        Assert.assertThat(systemEnvironment.useCompressedJs(), Matchers.is(true));
        systemEnvironment.setProperty(USE_COMPRESSED_JAVASCRIPT, Boolean.FALSE.toString());
        Assert.assertThat(systemEnvironment.useCompressedJs(), Matchers.is(false));
        systemEnvironment.setProperty(USE_COMPRESSED_JAVASCRIPT, Boolean.TRUE.toString());
        Assert.assertThat(systemEnvironment.useCompressedJs(), Matchers.is(true));
    }

    @Test
    public void shouldSetTrueAsDefaultForUseIframeSandbox() {
        Assert.assertThat(systemEnvironment.useIframeSandbox(), Matchers.is(true));
    }

    @Test
    public void shouldCacheAgentConnectionSystemPropertyOnFirstAccess() {
        System.setProperty(AGENT_CONNECTION_TIMEOUT_IN_SECONDS, "1");
        Assert.assertThat(systemEnvironment.getAgentConnectionTimeout(), Matchers.is(1));
        System.setProperty(AGENT_CONNECTION_TIMEOUT_IN_SECONDS, "2");
        Assert.assertThat(systemEnvironment.getAgentConnectionTimeout(), Matchers.is(1));
    }

    @Test
    public void shouldCacheSslPortSystemPropertyOnFirstAccess() {
        System.setProperty(CRUISE_SERVER_SSL_PORT, "8154");
        Assert.assertThat(systemEnvironment.getSslServerPort(), Matchers.is(8154));
        System.setProperty(CRUISE_SERVER_SSL_PORT, "20000");
        Assert.assertThat(systemEnvironment.getSslServerPort(), Matchers.is(8154));
    }

    @Test
    public void shouldCacheConfigDirOnFirstAccess() {
        Assert.assertThat(systemEnvironment.getConfigDir(), Matchers.is("config"));
        System.setProperty(CONFIG_DIR_PROPERTY, "raghu");
        Assert.assertThat(systemEnvironment.getConfigDir(), Matchers.is("config"));
    }

    @Test
    public void shouldCacheConfigFilePathOnFirstAccess() {
        Assert.assertThat(systemEnvironment.configDir(), Matchers.is(new File("config")));
        System.setProperty(CONFIG_FILE_PROPERTY, "foo");
        Assert.assertThat(systemEnvironment.getConfigDir(), Matchers.is("config"));
    }

    @Test
    public void shouldCacheDatabaseDiskFullOnFirstAccess() {
        System.setProperty(DATABASE_FULL_SIZE_LIMIT, "100");
        Assert.assertThat(systemEnvironment.getDatabaseDiskSpaceFullLimit(), Matchers.is(100L));
        System.setProperty(DATABASE_FULL_SIZE_LIMIT, "50M");
        Assert.assertThat(systemEnvironment.getDatabaseDiskSpaceFullLimit(), Matchers.is(100L));
    }

    @Test
    public void shouldCacheArtifactDiskFullOnFirstAccess() {
        System.setProperty(ARTIFACT_FULL_SIZE_LIMIT, "100");
        Assert.assertThat(systemEnvironment.getArtifactReposiotryFullLimit(), Matchers.is(100L));
        System.setProperty(ARTIFACT_FULL_SIZE_LIMIT, "50M");
        Assert.assertThat(systemEnvironment.getArtifactReposiotryFullLimit(), Matchers.is(100L));
    }

    @Test
    public void shouldClearCachedValuesOnSettingNewProperty() {
        System.setProperty(ARTIFACT_FULL_SIZE_LIMIT, "100");
        Assert.assertThat(systemEnvironment.getArtifactReposiotryFullLimit(), Matchers.is(100L));
        systemEnvironment.setProperty(ARTIFACT_FULL_SIZE_LIMIT, "50");
        Assert.assertThat(systemEnvironment.getArtifactReposiotryFullLimit(), Matchers.is(50L));
    }

    @Test
    public void shouldPrefixApplicationPathWithContext() {
        Assert.assertThat(systemEnvironment.pathFor("foo/bar"), Matchers.is("/go/foo/bar"));
        Assert.assertThat(systemEnvironment.pathFor("/baz/quux"), Matchers.is("/go/baz/quux"));
    }

    @Test
    public void shouldUnderstandConfigRepoDir() {
        Properties properties = new Properties();
        SystemEnvironment systemEnvironment = new SystemEnvironment(properties);
        Assert.assertThat(systemEnvironment.getConfigRepoDir(), Matchers.is(new File("db/config.git")));
        properties.setProperty(CRUISE_CONFIG_REPO_DIR, "foo/bar.git");
        Assert.assertThat(systemEnvironment.getConfigRepoDir(), Matchers.is(new File("foo/bar.git")));
    }

    @Test
    public void shouldUnderstandMaterialUpdateInterval() {
        Assert.assertThat(systemEnvironment.getMaterialUpdateIdleInterval(), Matchers.is(60000L));
        systemEnvironment.setProperty(MATERIAL_UPDATE_IDLE_INTERVAL_PROPERTY, "20");
        Assert.assertThat(systemEnvironment.getMaterialUpdateIdleInterval(), Matchers.is(20L));
    }

    @Test
    public void shouldUnderstandH2CacheSize() {
        Assert.assertThat(systemEnvironment.getCruiseDbCacheSize(), Matchers.is(String.valueOf((128 * 1024))));
        System.setProperty(CRUISE_DB_CACHE_SIZE, String.valueOf((512 * 1024)));
        Assert.assertThat(systemEnvironment.getCruiseDbCacheSize(), Matchers.is(String.valueOf((512 * 1024))));
    }

    @Test
    public void shouldReturnTheJobWarningLimit() {
        Assert.assertThat(systemEnvironment.getUnresponsiveJobWarningThreshold(), Matchers.is(((5 * 60) * 1000L)));
        System.setProperty(UNRESPONSIVE_JOB_WARNING_THRESHOLD, "30");
        Assert.assertThat(systemEnvironment.getUnresponsiveJobWarningThreshold(), Matchers.is(((30 * 60) * 1000L)));
    }

    @Test
    public void shouldReturnTheDefaultValueForActiveMqUseJMX() {
        Assert.assertThat(systemEnvironment.getActivemqUseJmx(), Matchers.is(false));
        System.setProperty(ACTIVEMQ_USE_JMX, "true");
        Assert.assertThat(systemEnvironment.getActivemqUseJmx(), Matchers.is(true));
    }

    @Test
    public void shouldResolveRevisionsForDependencyGraph_byDefault() {
        Assert.assertThat(System.getProperty(RESOLVE_FANIN_REVISIONS), Matchers.nullValue());
        Assert.assertThat(new SystemEnvironment().enforceRevisionCompatibilityWithUpstream(), Matchers.is(true));
    }

    @Test
    public void should_NOT_resolveRevisionsForDependencyGraph_whenExplicitlyDisabled() {
        System.setProperty(RESOLVE_FANIN_REVISIONS, CONFIGURATION_NO);
        Assert.assertThat(new SystemEnvironment().enforceRevisionCompatibilityWithUpstream(), Matchers.is(false));
    }

    @Test
    public void shouldResolveRevisionsForDependencyGraph_whenEnabledExplicitly() {
        System.setProperty(RESOLVE_FANIN_REVISIONS, CONFIGURATION_YES);
        Assert.assertThat(new SystemEnvironment().enforceRevisionCompatibilityWithUpstream(), Matchers.is(true));
    }

    @Test
    public void should_cache_whetherToResolveRevisionsForDependencyGraph() {
        // because access to properties is synchronized
        Assert.assertThat(System.getProperty(RESOLVE_FANIN_REVISIONS), Matchers.nullValue());
        SystemEnvironment systemEnvironment = new SystemEnvironment();
        Assert.assertThat(systemEnvironment.enforceRevisionCompatibilityWithUpstream(), Matchers.is(true));
        System.setProperty(RESOLVE_FANIN_REVISIONS, CONFIGURATION_NO);
        Assert.assertThat(systemEnvironment.enforceRevisionCompatibilityWithUpstream(), Matchers.is(true));
    }

    @Test
    public void shouldTurnOnConfigMergeFeature_byDefault() {
        Assert.assertThat(System.getProperty(ENABLE_CONFIG_MERGE_PROPERTY), Matchers.nullValue());
        Assert.assertThat(new SystemEnvironment().get(ENABLE_CONFIG_MERGE_FEATURE), Matchers.is(true));
    }

    @Test
    public void should_NOT_TurnOnConfigMergeFeature_whenExplicitlyDisabled() {
        System.setProperty(ENABLE_CONFIG_MERGE_PROPERTY, CONFIGURATION_NO);
        Assert.assertThat(new SystemEnvironment().get(ENABLE_CONFIG_MERGE_FEATURE), Matchers.is(false));
    }

    @Test
    public void shouldTurnOnConfigMergeFeature_whenEnabledExplicitly() {
        System.setProperty(ENABLE_CONFIG_MERGE_PROPERTY, CONFIGURATION_YES);
        Assert.assertThat(new SystemEnvironment().get(ENABLE_CONFIG_MERGE_FEATURE), Matchers.is(true));
    }

    @Test
    public void should_cache_whetherToTurnOnConfigMergeFeature() {
        // because access to properties is synchronized
        Assert.assertThat(System.getProperty(ENABLE_CONFIG_MERGE_PROPERTY), Matchers.nullValue());
        Assert.assertThat(new SystemEnvironment().get(ENABLE_CONFIG_MERGE_FEATURE), Matchers.is(true));
        System.setProperty(ENABLE_CONFIG_MERGE_PROPERTY, CONFIGURATION_NO);
        Assert.assertThat(new SystemEnvironment().get(ENABLE_CONFIG_MERGE_FEATURE), Matchers.is(true));
    }

    @Test
    public void shouldGetTfsSocketTimeOut() {
        Assert.assertThat(systemEnvironment.getTfsSocketTimeout(), Matchers.is(TFS_SOCKET_TIMEOUT_IN_MILLISECONDS));
        System.setProperty(TFS_SOCKET_TIMEOUT_PROPERTY, "100000000");
        Assert.assertThat(systemEnvironment.getTfsSocketTimeout(), Matchers.is(100000000));
    }

    @Test
    public void shouldGiveINFOAsTheDefaultLevelOfAPluginWithoutALoggingLevelSet() throws Exception {
        Assert.assertThat(systemEnvironment.pluginLoggingLevel("some-plugin-1"), Matchers.is(INFO));
    }

    @Test
    public void shouldGiveINFOAsTheDefaultLevelOfAPluginWithAnInvalidLoggingLevelSet() throws Exception {
        System.setProperty("plugin.some-plugin-2.log.level", "SOME-INVALID-LOG-LEVEL");
        Assert.assertThat(systemEnvironment.pluginLoggingLevel("some-plugin-2"), Matchers.is(INFO));
    }

    @Test
    public void shouldGiveTheLevelOfAPluginWithALoggingLevelSet() throws Exception {
        System.setProperty("plugin.some-plugin-3.log.level", "DEBUG");
        System.setProperty("plugin.some-plugin-4.log.level", "INFO");
        System.setProperty("plugin.some-plugin-5.log.level", "WARN");
        System.setProperty("plugin.some-plugin-6.log.level", "ERROR");
        Assert.assertThat(systemEnvironment.pluginLoggingLevel("some-plugin-3"), Matchers.is(DEBUG));
        Assert.assertThat(systemEnvironment.pluginLoggingLevel("some-plugin-4"), Matchers.is(INFO));
        Assert.assertThat(systemEnvironment.pluginLoggingLevel("some-plugin-5"), Matchers.is(WARN));
        Assert.assertThat(systemEnvironment.pluginLoggingLevel("some-plugin-6"), Matchers.is(ERROR));
    }

    @Test
    @RunIf(value = DatabaseChecker.class, arguments = { DatabaseChecker.H2 })
    public void shouldGetGoDatabaseProvider() {
        Assert.assertThat("default provider should be h2db", systemEnvironment.getDatabaseProvider(), Matchers.is("com.thoughtworks.go.server.database.H2Database"));
        System.setProperty("go.database.provider", "foo");
        Assert.assertThat(systemEnvironment.getDatabaseProvider(), Matchers.is("foo"));
    }

    @Test
    public void shouldFindGoServerStatusToBeActiveByDefault() throws Exception {
        Assert.assertThat(systemEnvironment.isServerActive(), Matchers.is(true));
    }

    @Test
    public void shouldPutServerInActiveMode() throws Exception {
        String key = "go.server.state";
        try {
            System.setProperty(key, "passive");
            systemEnvironment.switchToActiveState();
            Assert.assertThat(systemEnvironment.isServerActive(), Matchers.is(true));
        } finally {
            System.clearProperty(key);
        }
    }

    @Test
    public void shouldPutServerInPassiveMode() throws Exception {
        String key = "go.server.state";
        try {
            System.setProperty(key, "active");
            systemEnvironment.switchToPassiveState();
            Assert.assertThat(systemEnvironment.isServerActive(), Matchers.is(false));
        } finally {
            System.clearProperty(key);
        }
    }

    @Test
    public void shouldFindGoServerStatusToBePassive() throws Exception {
        try {
            SystemEnvironment systemEnvironment = new SystemEnvironment();
            System.setProperty("go.server.state", "passive");
            Assert.assertThat(systemEnvironment.isServerActive(), Matchers.is(false));
        } finally {
            System.clearProperty("go.server.state");
        }
    }

    @Test
    public void shouldUseJetty9ByDefault() {
        Assert.assertThat(systemEnvironment.get(APP_SERVER), Matchers.is(JETTY9));
        Assert.assertThat(systemEnvironment.usingJetty9(), Matchers.is(true));
        systemEnvironment.set(APP_SERVER, "JETTY6");
        Assert.assertThat(systemEnvironment.usingJetty9(), Matchers.is(false));
    }

    @Test
    public void shouldGetDefaultLandingPageAsPipelines() throws Exception {
        String landingPage = systemEnvironment.landingPage();
        Assert.assertThat(landingPage, Matchers.is("/pipelines"));
    }

    @Test
    public void shouldAbleToOverrideDefaultLandingPageAsPipelines() throws Exception {
        try {
            System.setProperty("go.landing.page", "/admin/pipelines");
            String landingPage = systemEnvironment.landingPage();
            Assert.assertThat(landingPage, Matchers.is("/admin/pipelines"));
        } finally {
            System.clearProperty("go.landing.page");
        }
    }

    @Test
    public void shouldSetTLS1Dot2AsDefaultTransportProtocolForAgent() {
        Assert.assertThat(GO_SSL_TRANSPORT_PROTOCOL_TO_BE_USED_BY_AGENT.propertyName(), Matchers.is("go.ssl.agent.protocol"));
        Assert.assertThat(systemEnvironment.get(GO_SSL_TRANSPORT_PROTOCOL_TO_BE_USED_BY_AGENT), Matchers.is("TLSv1.2"));
        System.setProperty(GO_SSL_TRANSPORT_PROTOCOL_TO_BE_USED_BY_AGENT.propertyName(), "SSL");
        Assert.assertThat(systemEnvironment.get(GO_SSL_TRANSPORT_PROTOCOL_TO_BE_USED_BY_AGENT), Matchers.is("SSL"));
    }

    @Test
    public void shouldGetIncludedCiphersForSSLConfig() {
        Assert.assertThat(GO_SSL_INCLUDE_CIPHERS.propertyName(), Matchers.is("go.ssl.ciphers.include"));
        Assert.assertThat(((GO_SSL_INCLUDE_CIPHERS) instanceof SystemEnvironment.GoStringArraySystemProperty), Matchers.is(true));
        Assert.assertThat(systemEnvironment.get(GO_SSL_INCLUDE_CIPHERS), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldGetExcludedCiphersForSSLConfig() {
        Assert.assertThat(GO_SSL_EXCLUDE_CIPHERS.propertyName(), Matchers.is("go.ssl.ciphers.exclude"));
        Assert.assertThat(((GO_SSL_EXCLUDE_CIPHERS) instanceof SystemEnvironment.GoStringArraySystemProperty), Matchers.is(true));
        Assert.assertThat(systemEnvironment.get(GO_SSL_EXCLUDE_CIPHERS), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldGetExcludedProtocolsForSSLConfig() {
        Assert.assertThat(GO_SSL_EXCLUDE_PROTOCOLS.propertyName(), Matchers.is("go.ssl.protocols.exclude"));
        Assert.assertThat(((GO_SSL_EXCLUDE_PROTOCOLS) instanceof SystemEnvironment.GoStringArraySystemProperty), Matchers.is(true));
        Assert.assertThat(systemEnvironment.get(GO_SSL_EXCLUDE_PROTOCOLS), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldGetIncludedProtocolsForSSLConfig() {
        Assert.assertThat(GO_SSL_INCLUDE_PROTOCOLS.propertyName(), Matchers.is("go.ssl.protocols.include"));
        Assert.assertThat(((GO_SSL_INCLUDE_PROTOCOLS) instanceof SystemEnvironment.GoStringArraySystemProperty), Matchers.is(true));
        Assert.assertThat(systemEnvironment.get(GO_SSL_INCLUDE_PROTOCOLS), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldGetRenegotiationAllowedFlagForSSLConfig() {
        Assert.assertThat(GO_SSL_RENEGOTIATION_ALLOWED.propertyName(), Matchers.is("go.ssl.renegotiation.allowed"));
        boolean defaultValue = true;
        Assert.assertThat(systemEnvironment.get(GO_SSL_RENEGOTIATION_ALLOWED), Matchers.is(defaultValue));
        System.clearProperty("go.ssl.renegotiation.allowed");
        Assert.assertThat(systemEnvironment.get(GO_SSL_RENEGOTIATION_ALLOWED), Matchers.is(defaultValue));
        System.setProperty("go.ssl.renegotiation.allowed", "false");
        Assert.assertThat(systemEnvironment.get(GO_SSL_RENEGOTIATION_ALLOWED), Matchers.is(false));
    }

    @Test
    public void ShouldRemoveWhiteSpacesForStringArraySystemProperties() {
        String[] defaultValue = new String[]{ "junk", "funk" };
        String propertyName = "property.name";
        SystemEnvironment.GoStringArraySystemProperty property = new SystemEnvironment.GoStringArraySystemProperty(propertyName, defaultValue);
        System.setProperty(propertyName, " foo    ,  bar  ");
        Assert.assertThat(systemEnvironment.get(property).length, Matchers.is(2));
        Assert.assertThat(systemEnvironment.get(property)[0], Matchers.is("foo"));
        Assert.assertThat(systemEnvironment.get(property)[1], Matchers.is("bar"));
    }

    @Test
    public void ShouldUseDefaultValueForStringArraySystemPropertiesWhenTheValueIsSetToEmptyString() {
        String[] defaultValue = new String[]{ "junk", "funk" };
        String propertyName = "property.name";
        SystemEnvironment.GoStringArraySystemProperty property = new SystemEnvironment.GoStringArraySystemProperty(propertyName, defaultValue);
        System.clearProperty(propertyName);
        Assert.assertThat(systemEnvironment.get(property), Matchers.is(defaultValue));
        System.setProperty(propertyName, " ");
        Assert.assertThat(systemEnvironment.get(property), Matchers.is(defaultValue));
    }

    @Test
    public void shouldSetConfigRepoGCToBeAggressiveByDefault() {
        Assert.assertThat(new SystemEnvironment().get(GO_CONFIG_REPO_GC_AGGRESSIVE), Matchers.is(true));
    }

    @Test
    public void shouldTurnOffPeriodicGCByDefault() {
        Assert.assertThat(new SystemEnvironment().get(GO_CONFIG_REPO_PERIODIC_GC), Matchers.is(false));
    }

    @Test
    public void shouldGetUpdateServerPublicKeyFilePath() {
        Assert.assertThat(GO_UPDATE_SERVER_PUBLIC_KEY_FILE_NAME.propertyName(), Matchers.is("go.update.server.public.key.file.name"));
        System.setProperty("go.update.server.public.key.file.name", "public_key");
        Assert.assertThat(systemEnvironment.getUpdateServerPublicKeyPath(), Matchers.is(((systemEnvironment.getConfigDir()) + "/public_key")));
    }

    @Test
    public void shouldGetUpdateServerUrl() {
        Assert.assertThat(GO_UPDATE_SERVER_URL.propertyName(), Matchers.is("go.update.server.url"));
        System.setProperty("go.update.server.url", "http://update_server_url");
        Assert.assertThat(systemEnvironment.getUpdateServerUrl(), Matchers.is("http://update_server_url"));
    }

    @Test
    public void shouldGetMaxNumberOfRequestsForEncryptionApi() {
        Assert.assertThat(GO_ENCRYPTION_API_MAX_REQUESTS.propertyName(), Matchers.is("go.encryption.api.max.requests"));
        Assert.assertThat(systemEnvironment.getMaxEncryptionAPIRequestsPerMinute(), Matchers.is(30));
        System.setProperty("go.encryption.api.max.requests", "50");
        Assert.assertThat(systemEnvironment.getMaxEncryptionAPIRequestsPerMinute(), Matchers.is(50));
    }

    @Test
    public void shouldCheckIfGOUpdatesIsEnabled() {
        Assert.assertThat(GO_CHECK_UPDATES.propertyName(), Matchers.is("go.check.updates"));
        Assert.assertTrue(systemEnvironment.isGOUpdateCheckEnabled());
        System.setProperty("go.check.updates", "false");
        Assert.assertFalse(systemEnvironment.isGOUpdateCheckEnabled());
    }

    @Test
    public void shouldEnableTemplateAutoSuggestByDefault() {
        Assert.assertThat(GO_FETCH_ARTIFACT_TEMPLATE_AUTO_SUGGEST.propertyName(), Matchers.is("go.fetch-artifact.template.auto-suggest"));
        Assert.assertTrue(systemEnvironment.isFetchArtifactTemplateAutoSuggestEnabled());
    }

    @Test
    public void shouldDisableTemplateAutoSuggest() {
        System.setProperty("go.fetch-artifact.template.auto-suggest", "false");
        Assert.assertFalse(systemEnvironment.isFetchArtifactTemplateAutoSuggestEnabled());
    }

    @Test
    public void shouldReturnTheDefaultGCExpireTimeInMilliSeconds() {
        Assert.assertThat(GO_CONFIG_REPO_GC_EXPIRE.propertyName(), Matchers.is("go.config.repo.gc.expire"));
        Assert.assertThat(systemEnvironment.getConfigGitGCExpireTime(), Matchers.is((((24 * 60) * 60) * 1000L)));
    }

    @Test
    public void shouldReturnTHeGCExpireTimeInMilliSeconds() {
        Assert.assertThat(systemEnvironment.getConfigGitGCExpireTime(), Matchers.is((((24 * 60) * 60) * 1000L)));
        System.setProperty("go.config.repo.gc.expire", "1");
        Assert.assertThat(systemEnvironment.getConfigGitGCExpireTime(), Matchers.is(((60 * 60) * 1000L)));
    }

    @Test
    public void shouldReturnTrueIfBooleanSystemPropertyIsEnabledByY() {
        Assert.assertThat(new SystemEnvironment().get(GO_CONFIG_REPO_PERIODIC_GC), Matchers.is(false));
        System.setProperty("go.config.repo.gc.periodic", "Y");
        Assert.assertThat(new SystemEnvironment().get(GO_CONFIG_REPO_PERIODIC_GC), Matchers.is(true));
    }

    @Test
    public void shouldReturnTrueIfBooleanSystemPropertyIsEnabledByTrue() {
        Assert.assertThat(new SystemEnvironment().get(GO_CONFIG_REPO_PERIODIC_GC), Matchers.is(false));
        System.setProperty("go.config.repo.gc.periodic", "true");
        Assert.assertThat(new SystemEnvironment().get(GO_CONFIG_REPO_PERIODIC_GC), Matchers.is(true));
    }

    @Test
    public void shouldReturnFalseIfBooleanSystemPropertyIsAnythingButYOrTrue() {
        Assert.assertThat(new SystemEnvironment().get(GO_CONFIG_REPO_PERIODIC_GC), Matchers.is(false));
        System.setProperty("go.config.repo.gc.periodic", "some-value");
        Assert.assertThat(new SystemEnvironment().get(GO_CONFIG_REPO_PERIODIC_GC), Matchers.is(false));
    }

    @Test
    public void shouldBeInStandByModeIfGoServerModeIsSetToStandby() {
        Assert.assertFalse(new SystemEnvironment().isServerInStandbyMode());
        System.setProperty("go.server.mode", "StandBy");
        Assert.assertTrue(new SystemEnvironment().isServerInStandbyMode());
    }
}

