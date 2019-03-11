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


import ProxyHostProperties.ProxyForScheme;
import ProxyHostProperties.ProxyForScheme.HTTP;
import com.jcraft.jsch.HostKey;
import com.jcraft.jsch.HostKeyRepository;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.ProxyHTTP;
import com.jcraft.jsch.Session;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.cloud.config.server.environment.JGitEnvironmentProperties;
import org.springframework.cloud.config.server.proxy.ProxyHostProperties;


/**
 * Unit tests for property based SSH config processor.
 *
 * @author William Tran
 * @author Ollie Hughes
 */
@RunWith(MockitoJUnitRunner.class)
public class PropertyBasedSshSessionFactoryTest {
    private static final String HOST_KEY = "AAAAE2VjZHNhLXNoYTItbmlzdHAyNTYAAAAIbmlzdHAyNTYAAAB" + "BBMzCa0AcNbahUFjFYJHIilhJOhKFHuDOOuY+/HqV9kALftitwNYo6dQ+tC9IK5JVZCZfqKfDWVMxspcPDf9eMoE=";

    private static final String HOST_KEY_ALGORITHM = "ecdsa-sha2-nistp256";

    private static final String PRIVATE_KEY = PropertyBasedSshSessionFactoryTest.getResourceAsString("/ssh/key");

    private PropertyBasedSshSessionFactory factory;

    @Mock
    private OpenSshConfig.Host hc;

    @Mock
    private Session session;

    @Mock
    private JSch jSch;

    @Mock
    private HostKeyRepository hostKeyRepository;

    @Mock
    private ProxyHTTP proxyMock;

    @Test
    public void strictHostKeyCheckingIsOptional() {
        JGitEnvironmentProperties sshKey = new JGitEnvironmentProperties();
        sshKey.setUri("ssh://gitlab.example.local:3322/somerepo.git");
        sshKey.setPrivateKey(PropertyBasedSshSessionFactoryTest.PRIVATE_KEY);
        setupSessionFactory(sshKey);
        this.factory.configure(this.hc, this.session);
        Mockito.verify(this.session).setConfig("StrictHostKeyChecking", "no");
        Mockito.verifyNoMoreInteractions(this.session);
    }

    @Test
    public void strictHostKeyCheckingIsUsed() {
        JGitEnvironmentProperties sshKey = new JGitEnvironmentProperties();
        sshKey.setUri("ssh://gitlab.example.local:3322/somerepo.git");
        sshKey.setHostKey(PropertyBasedSshSessionFactoryTest.HOST_KEY);
        sshKey.setPrivateKey(PropertyBasedSshSessionFactoryTest.PRIVATE_KEY);
        setupSessionFactory(sshKey);
        this.factory.configure(this.hc, this.session);
        Mockito.verify(this.session).setConfig("StrictHostKeyChecking", "yes");
        Mockito.verifyNoMoreInteractions(this.session);
    }

    @Test
    public void hostKeyAlgorithmIsSpecified() {
        JGitEnvironmentProperties sshKey = new JGitEnvironmentProperties();
        sshKey.setUri("ssh://gitlab.example.local:3322/somerepo.git");
        sshKey.setHostKeyAlgorithm(PropertyBasedSshSessionFactoryTest.HOST_KEY_ALGORITHM);
        sshKey.setHostKey(PropertyBasedSshSessionFactoryTest.HOST_KEY);
        sshKey.setPrivateKey(PropertyBasedSshSessionFactoryTest.PRIVATE_KEY);
        setupSessionFactory(sshKey);
        this.factory.configure(this.hc, this.session);
        Mockito.verify(this.session).setConfig("server_host_key", PropertyBasedSshSessionFactoryTest.HOST_KEY_ALGORITHM);
        Mockito.verify(this.session).setConfig("StrictHostKeyChecking", "yes");
        Mockito.verifyNoMoreInteractions(this.session);
    }

    @Test
    public void privateKeyIsUsed() throws Exception {
        JGitEnvironmentProperties sshKey = new JGitEnvironmentProperties();
        sshKey.setUri("git@gitlab.example.local:someorg/somerepo.git");
        sshKey.setPrivateKey(PropertyBasedSshSessionFactoryTest.PRIVATE_KEY);
        setupSessionFactory(sshKey);
        this.factory.createSession(this.hc, null, SshUriPropertyProcessor.getHostname(sshKey.getUri()), 22, null);
        Mockito.verify(this.jSch).addIdentity("gitlab.example.local", PropertyBasedSshSessionFactoryTest.PRIVATE_KEY.getBytes(), null, null);
    }

    @Test
    public void hostKeyIsUsed() throws Exception {
        JGitEnvironmentProperties sshKey = new JGitEnvironmentProperties();
        sshKey.setUri("git@gitlab.example.local:someorg/somerepo.git");
        sshKey.setHostKey(PropertyBasedSshSessionFactoryTest.HOST_KEY);
        sshKey.setPrivateKey(PropertyBasedSshSessionFactoryTest.PRIVATE_KEY);
        setupSessionFactory(sshKey);
        this.factory.createSession(this.hc, null, SshUriPropertyProcessor.getHostname(sshKey.getUri()), 22, null);
        ArgumentCaptor<HostKey> captor = ArgumentCaptor.forClass(HostKey.class);
        Mockito.verify(this.hostKeyRepository).add(captor.capture(), ArgumentMatchers.isNull());
        HostKey hostKey = captor.getValue();
        assertThat(hostKey.getHost()).isEqualTo("gitlab.example.local");
        assertThat(hostKey.getKey()).isEqualTo(PropertyBasedSshSessionFactoryTest.HOST_KEY);
    }

    @Test
    public void preferredAuthenticationsIsSpecified() {
        JGitEnvironmentProperties sshKey = new JGitEnvironmentProperties();
        sshKey.setUri("ssh://gitlab.example.local:3322/somerepo.git");
        sshKey.setPrivateKey(PropertyBasedSshSessionFactoryTest.PRIVATE_KEY);
        sshKey.setPreferredAuthentications("password,keyboard-interactive");
        setupSessionFactory(sshKey);
        this.factory.configure(this.hc, this.session);
        Mockito.verify(this.session).setConfig("PreferredAuthentications", "password,keyboard-interactive");
        Mockito.verify(this.session).setConfig("StrictHostKeyChecking", "no");
        Mockito.verifyNoMoreInteractions(this.session);
    }

    @Test
    public void customKnownHostsFileIsUsed() throws Exception {
        JGitEnvironmentProperties sshKey = new JGitEnvironmentProperties();
        sshKey.setUri("git@gitlab.example.local:someorg/somerepo.git");
        sshKey.setPrivateKey(PropertyBasedSshSessionFactoryTest.PRIVATE_KEY);
        sshKey.setKnownHostsFile("/ssh/known_hosts");
        setupSessionFactory(sshKey);
        this.factory.createSession(this.hc, null, SshUriPropertyProcessor.getHostname(sshKey.getUri()), 22, null);
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(this.jSch).setKnownHosts(captor.capture());
        assertThat(captor.getValue()).isEqualTo("/ssh/known_hosts");
    }

    @Test
    public void proxySettingsIsUsed() {
        JGitEnvironmentProperties sshProperties = new JGitEnvironmentProperties();
        sshProperties.setPrivateKey(PropertyBasedSshSessionFactoryTest.PRIVATE_KEY);
        Map<ProxyHostProperties.ProxyForScheme, ProxyHostProperties> map = new HashMap<>();
        ProxyHostProperties proxyHostProperties = new ProxyHostProperties();
        proxyHostProperties.setUsername("user");
        proxyHostProperties.setPassword("password");
        map.put(HTTP, proxyHostProperties);
        sshProperties.setProxy(map);
        setupSessionFactory(sshProperties);
        this.factory.configure(this.hc, this.session);
        ArgumentCaptor<ProxyHTTP> captor = ArgumentCaptor.forClass(ProxyHTTP.class);
        Mockito.verify(this.session).setProxy(captor.capture());
        assertThat(captor.getValue()).isNotNull();
        Mockito.verify(this.proxyMock).setUserPasswd("user", "password");
    }
}

