/**
 * Copyright 2015-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.config.server.ssh;


import MultipleJGitEnvironmentProperties.PatternMatchingJGitEnvironmentProperties;
import java.util.Map;
import org.junit.Test;
import org.springframework.cloud.config.server.environment.JGitEnvironmentProperties;
import org.springframework.cloud.config.server.environment.MultipleJGitEnvironmentProperties;


/**
 * Unit tests for property based SSH config processor.
 *
 * @author William Tran
 * @author Ollie Hughes
 */
public class SshUriPropertyProcessorTest {
    private static final String PRIVATE_KEY1 = "privateKey";

    private static final String HOST_KEY1 = "hostKey";

    private static final String ALGO1 = "ssh-rsa";

    private static final String URI1 = "ollie@gitlab1.test.local:project/my-repo";

    private static final String HOST1 = "gitlab1.test.local";

    private static final String PRIVATE_KEY2 = "privateKey2";

    private static final String URI2 = "ssh://git@gitlab2.test.local/wtran/my-repo";

    private static final String HOST2 = "gitlab2.test.local";

    private static final String PRIVATE_KEY3 = "privateKey3";

    private static final String URI3 = "git+ssh://git@gitlab3.test.local/wtran/my-repo";

    private static final String HOST3 = "gitlab3.test.local";

    @Test
    public void testSingleSshUriProperties() {
        SshUriPropertyProcessor sshUriPropertyProcessor = new SshUriPropertyProcessor(mainRepoPropertiesFixture());
        Map<String, JGitEnvironmentProperties> sshKeysByHostname = sshUriPropertyProcessor.getSshKeysByHostname();
        assertThat(sshKeysByHostname.values()).hasSize(1);
        JGitEnvironmentProperties sshKey = sshKeysByHostname.get(SshUriPropertyProcessorTest.HOST1);
        assertMainRepo(sshKey);
    }

    @Test
    public void testMultipleSshUriPropertiess() {
        MultipleJGitEnvironmentProperties sshUriProperties = mainRepoPropertiesFixture();
        MultipleJGitEnvironmentProperties.PatternMatchingJGitEnvironmentProperties nestedSshUriProperties1;
        nestedSshUriProperties1 = new MultipleJGitEnvironmentProperties.PatternMatchingJGitEnvironmentProperties();
        nestedSshUriProperties1.setUri(SshUriPropertyProcessorTest.URI2);
        nestedSshUriProperties1.setPrivateKey(SshUriPropertyProcessorTest.PRIVATE_KEY2);
        MultipleJGitEnvironmentProperties.PatternMatchingJGitEnvironmentProperties nestedSshUriProperties2;
        nestedSshUriProperties2 = new MultipleJGitEnvironmentProperties.PatternMatchingJGitEnvironmentProperties();
        nestedSshUriProperties2.setUri(SshUriPropertyProcessorTest.URI3);
        nestedSshUriProperties2.setPrivateKey(SshUriPropertyProcessorTest.PRIVATE_KEY3);
        addRepoProperties(sshUriProperties, nestedSshUriProperties1, "repo2");
        addRepoProperties(sshUriProperties, nestedSshUriProperties2, "repo3");
        SshUriPropertyProcessor sshUriPropertyProcessor = new SshUriPropertyProcessor(sshUriProperties);
        Map<String, JGitEnvironmentProperties> sshKeysByHostname = sshUriPropertyProcessor.getSshKeysByHostname();
        assertThat(sshKeysByHostname.values()).hasSize(3);
        JGitEnvironmentProperties sshKey1 = sshKeysByHostname.get(SshUriPropertyProcessorTest.HOST1);
        assertMainRepo(sshKey1);
        JGitEnvironmentProperties sshKey2 = sshKeysByHostname.get(SshUriPropertyProcessorTest.HOST2);
        assertThat(SshUriPropertyProcessor.getHostname(sshKey2.getUri())).isEqualTo(SshUriPropertyProcessorTest.HOST2);
        assertThat(sshKey2.getHostKeyAlgorithm()).isNull();
        assertThat(sshKey2.getHostKey()).isNull();
        assertThat(sshKey2.getPrivateKey()).isEqualTo(SshUriPropertyProcessorTest.PRIVATE_KEY2);
        JGitEnvironmentProperties sshKey3 = sshKeysByHostname.get(SshUriPropertyProcessorTest.HOST3);
        assertThat(SshUriPropertyProcessor.getHostname(sshKey3.getUri())).isEqualTo(SshUriPropertyProcessorTest.HOST3);
        assertThat(sshKey3.getHostKeyAlgorithm()).isNull();
        assertThat(sshKey3.getHostKey()).isNull();
        assertThat(sshKey3.getPrivateKey()).isEqualTo(SshUriPropertyProcessorTest.PRIVATE_KEY3);
    }

    @Test
    public void testSameHostnameDifferentKeysFirstOneWins() {
        MultipleJGitEnvironmentProperties sshUriProperties = mainRepoPropertiesFixture();
        MultipleJGitEnvironmentProperties.PatternMatchingJGitEnvironmentProperties nestedSshUriProperties;
        nestedSshUriProperties = new MultipleJGitEnvironmentProperties.PatternMatchingJGitEnvironmentProperties();
        nestedSshUriProperties.setUri(SshUriPropertyProcessorTest.URI1);
        nestedSshUriProperties.setPrivateKey(SshUriPropertyProcessorTest.PRIVATE_KEY1);
        nestedSshUriProperties.setHostKey(SshUriPropertyProcessorTest.HOST_KEY1);
        nestedSshUriProperties.setHostKeyAlgorithm(SshUriPropertyProcessorTest.ALGO1);
        addRepoProperties(sshUriProperties, nestedSshUriProperties, "repo2");
        SshUriPropertyProcessor sshUriPropertyProcessor = new SshUriPropertyProcessor(sshUriProperties);
        Map<String, JGitEnvironmentProperties> sshKeysByHostname = sshUriPropertyProcessor.getSshKeysByHostname();
        assertThat(sshKeysByHostname.values()).hasSize(1);
        JGitEnvironmentProperties sshKey = sshKeysByHostname.get(SshUriPropertyProcessorTest.HOST1);
        assertMainRepo(sshKey);
    }

    @Test
    public void testNoSshUriProperties() {
        SshUriPropertyProcessor sshUriPropertyProcessor = new SshUriPropertyProcessor(new MultipleJGitEnvironmentProperties());
        Map<String, JGitEnvironmentProperties> sshKeysByHostname = sshUriPropertyProcessor.getSshKeysByHostname();
        assertThat(sshKeysByHostname.values()).hasSize(0);
    }

    @Test
    public void testInvalidUriDoesNotAddEntry() {
        MultipleJGitEnvironmentProperties sshUriProperties = new MultipleJGitEnvironmentProperties();
        sshUriProperties.setUri("invalid_uri");
        SshUriPropertyProcessor sshUriPropertyProcessor = new SshUriPropertyProcessor(sshUriProperties);
        Map<String, JGitEnvironmentProperties> sshKeysByHostname = sshUriPropertyProcessor.getSshKeysByHostname();
        assertThat(sshKeysByHostname.values()).hasSize(0);
    }

    @Test
    public void testHttpsUriDoesNotAddEntry() {
        MultipleJGitEnvironmentProperties sshUriProperties = new MultipleJGitEnvironmentProperties();
        sshUriProperties.setUri("https://user@github.com/proj/repo.git");
        SshUriPropertyProcessor sshUriPropertyProcessor = new SshUriPropertyProcessor(sshUriProperties);
        Map<String, JGitEnvironmentProperties> sshKeysByHostname = sshUriPropertyProcessor.getSshKeysByHostname();
        assertThat(sshKeysByHostname.values()).hasSize(0);
    }
}

