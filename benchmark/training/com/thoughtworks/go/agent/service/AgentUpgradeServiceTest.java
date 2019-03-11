/**
 * Copyright 2019 ThoughtWorks, Inc.
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
package com.thoughtworks.go.agent.service;


import AgentUpgradeService.JvmExitter;
import SystemEnvironment.AGENT_CONTENT_MD5_HEADER;
import SystemEnvironment.AGENT_EXTRA_PROPERTIES_HEADER;
import SystemEnvironment.AGENT_LAUNCHER_CONTENT_MD5_HEADER;
import SystemEnvironment.AGENT_PLUGINS_ZIP_MD5_HEADER;
import SystemEnvironment.AGENT_TFS_SDK_MD5_HEADER;
import com.thoughtworks.go.util.SystemEnvironment;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.rules.TestRule;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class AgentUpgradeServiceTest {
    @Rule
    public final TestRule restoreSystemProperties = new RestoreSystemProperties();

    private SystemEnvironment systemEnvironment;

    private AgentUpgradeService agentUpgradeService;

    private CloseableHttpResponse closeableHttpResponse;

    private JvmExitter jvmExitter;

    @Test
    public void checkForUpgradeShouldNotKillAgentIfAllDownloadsAreCompatible() throws Exception {
        setupForNoChangesToMD5();
        agentUpgradeService.checkForUpgradeAndExtraProperties();
        Mockito.verify(jvmExitter, Mockito.never()).jvmExit(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test
    public void checkForUpgradeShouldKillAgentIfAgentMD5doesNotMatch() {
        Mockito.when(systemEnvironment.getAgentMd5()).thenReturn("old-agent-md5");
        expectHeaderValue(AGENT_CONTENT_MD5_HEADER, "new-agent-md5");
        RuntimeException toBeThrown = new RuntimeException("Boo!");
        Mockito.doThrow(toBeThrown).when(jvmExitter).jvmExit(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        try {
            agentUpgradeService.checkForUpgradeAndExtraProperties();
            Assert.fail("should have done jvm exit");
        } catch (Exception e) {
            Assert.assertSame(e, toBeThrown);
        }
        Mockito.verify(jvmExitter).jvmExit("itself", "old-agent-md5", "new-agent-md5");
    }

    @Test
    public void checkForUpgradeShouldKillAgentIfLauncherMD5doesNotMatch() {
        Mockito.when(systemEnvironment.getAgentMd5()).thenReturn("not-changing");
        expectHeaderValue(AGENT_CONTENT_MD5_HEADER, "not-changing");
        Mockito.when(systemEnvironment.getGivenAgentLauncherMd5()).thenReturn("old-launcher-md5");
        expectHeaderValue(AGENT_LAUNCHER_CONTENT_MD5_HEADER, "new-launcher-md5");
        RuntimeException toBeThrown = new RuntimeException("Boo!");
        Mockito.doThrow(toBeThrown).when(jvmExitter).jvmExit(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        try {
            agentUpgradeService.checkForUpgradeAndExtraProperties();
            Assert.fail("should have done jvm exit");
        } catch (Exception e) {
            Assert.assertSame(e, toBeThrown);
        }
        Mockito.verify(jvmExitter).jvmExit("launcher", "old-launcher-md5", "new-launcher-md5");
    }

    @Test
    public void checkForUpgradeShouldKillAgentIfPluginZipMd5doesNotMatch() {
        Mockito.when(systemEnvironment.getAgentMd5()).thenReturn("not-changing");
        expectHeaderValue(AGENT_CONTENT_MD5_HEADER, "not-changing");
        Mockito.when(systemEnvironment.getGivenAgentLauncherMd5()).thenReturn("not-changing");
        expectHeaderValue(AGENT_LAUNCHER_CONTENT_MD5_HEADER, "not-changing");
        Mockito.when(systemEnvironment.getAgentPluginsMd5()).thenReturn("old-plugins-md5");
        expectHeaderValue(AGENT_PLUGINS_ZIP_MD5_HEADER, "new-plugins-md5");
        RuntimeException toBeThrown = new RuntimeException("Boo!");
        Mockito.doThrow(toBeThrown).when(jvmExitter).jvmExit(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        try {
            agentUpgradeService.checkForUpgradeAndExtraProperties();
            Assert.fail("should have done jvm exit");
        } catch (Exception e) {
            Assert.assertSame(e, toBeThrown);
        }
        Mockito.verify(jvmExitter).jvmExit("plugins", "old-plugins-md5", "new-plugins-md5");
    }

    @Test
    public void checkForUpgradeShouldKillAgentIfTfsMd5doesNotMatch() {
        Mockito.when(systemEnvironment.getAgentMd5()).thenReturn("not-changing");
        expectHeaderValue(AGENT_CONTENT_MD5_HEADER, "not-changing");
        Mockito.when(systemEnvironment.getGivenAgentLauncherMd5()).thenReturn("not-changing");
        expectHeaderValue(AGENT_LAUNCHER_CONTENT_MD5_HEADER, "not-changing");
        Mockito.when(systemEnvironment.getAgentPluginsMd5()).thenReturn("not-changing");
        expectHeaderValue(AGENT_PLUGINS_ZIP_MD5_HEADER, "not-changing");
        Mockito.when(systemEnvironment.getTfsImplMd5()).thenReturn("old-tfs-md5");
        expectHeaderValue(AGENT_TFS_SDK_MD5_HEADER, "new-tfs-md5");
        RuntimeException toBeThrown = new RuntimeException("Boo!");
        Mockito.doThrow(toBeThrown).when(jvmExitter).jvmExit(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        try {
            agentUpgradeService.checkForUpgradeAndExtraProperties();
            Assert.fail("should have done jvm exit");
        } catch (Exception e) {
            Assert.assertSame(e, toBeThrown);
        }
        Mockito.verify(jvmExitter).jvmExit("tfs-impl jar", "old-tfs-md5", "new-tfs-md5");
    }

    @Test
    public void shouldSetAnyExtraPropertiesSentByTheServer() throws Exception {
        setupForNoChangesToMD5();
        expectHeaderValue(AGENT_EXTRA_PROPERTIES_HEADER, Base64.encodeBase64String("abc=def%20ghi  jkl%20mno=pqr%20stu".getBytes(StandardCharsets.UTF_8)));
        agentUpgradeService.checkForUpgradeAndExtraProperties();
        Assert.assertThat(System.getProperty("abc"), Matchers.is("def ghi"));
        Assert.assertThat(System.getProperty("jkl mno"), Matchers.is("pqr stu"));
    }

    @Test
    public void shouldFailQuietlyWhenExtraPropertiesHeaderValueIsInvalid() throws Exception {
        setupForNoChangesToMD5();
        final Map<Object, Object> before = System.getProperties().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        expectHeaderValue(AGENT_EXTRA_PROPERTIES_HEADER, Base64.encodeBase64String("this_is_invalid".getBytes(StandardCharsets.UTF_8)));
        agentUpgradeService.checkForUpgradeAndExtraProperties();
        final Map<Object, Object> after = System.getProperties().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Assert.assertThat(after, Matchers.is(before));
    }
}

