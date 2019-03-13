/**
 * -
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2016 Spotify AB
 * --
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
 * -/-/-
 */
package com.spotify.docker.client;


import com.spotify.docker.client.DockerHost.SystemDelegate;
import java.net.URI;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;


public class DockerHostTest {
    private SystemDelegate systemDelegate;

    @Test
    public void testDefaultDockerEndpoint() throws Exception {
        Mockito.when(systemDelegate.getProperty("os.name")).thenReturn("linux", "mac", "other");
        DockerHost.setSystemDelegate(systemDelegate);
        MatcherAssert.assertThat(DockerHost.defaultDockerEndpoint(), Matchers.equalTo("unix:///var/run/docker.sock"));
        MatcherAssert.assertThat(DockerHost.defaultDockerEndpoint(), Matchers.equalTo("unix:///var/run/docker.sock"));
        MatcherAssert.assertThat(DockerHost.defaultDockerEndpoint(), Matchers.equalTo("localhost:2375"));
    }

    @Test
    public void testEndpointFromEnv() throws Exception {
        Mockito.when(systemDelegate.getenv("DOCKER_HOST")).thenReturn("foo", ((String) (null)));
        Mockito.when(systemDelegate.getProperty("os.name")).thenReturn("linux");
        DockerHost.setSystemDelegate(systemDelegate);
        MatcherAssert.assertThat(DockerHost.endpointFromEnv(), Matchers.equalTo("foo"));
        MatcherAssert.assertThat(DockerHost.endpointFromEnv(), Matchers.equalTo("unix:///var/run/docker.sock"));
    }

    @Test
    public void testDefaultUnixEndpoint() throws Exception {
        MatcherAssert.assertThat(DockerHost.defaultUnixEndpoint(), Matchers.equalTo("unix:///var/run/docker.sock"));
    }

    @Test
    public void testDefaultAddress() throws Exception {
        MatcherAssert.assertThat(DockerHost.defaultAddress(), Matchers.equalTo("localhost"));
    }

    @Test
    public void testDefaultPort() throws Exception {
        MatcherAssert.assertThat(DockerHost.defaultPort(), Matchers.equalTo(2375));
    }

    @Test
    public void testPortFromEnv() throws Exception {
        Mockito.when(systemDelegate.getenv("DOCKER_PORT")).thenReturn("1234", ((String) (null)));
        DockerHost.setSystemDelegate(systemDelegate);
        MatcherAssert.assertThat(DockerHost.portFromEnv(), Matchers.equalTo(1234));
        MatcherAssert.assertThat(DockerHost.portFromEnv(), Matchers.equalTo(2375));
    }

    @Test
    public void testDefaultCertPath() throws Exception {
        Mockito.when(systemDelegate.getProperty("user.home")).thenReturn("foobar");
        DockerHost.setSystemDelegate(systemDelegate);
        MatcherAssert.assertThat(DockerHost.defaultCertPath(), Matchers.equalTo("foobar/.docker"));
    }

    @Test
    public void testCertPathFromEnv() throws Exception {
        Mockito.when(systemDelegate.getenv("DOCKER_CERT_PATH")).thenReturn("foo", ((String) (null)));
        Mockito.when(systemDelegate.getProperty("user.home")).thenReturn("bar");
        DockerHost.setSystemDelegate(systemDelegate);
        MatcherAssert.assertThat(DockerHost.certPathFromEnv(), Matchers.equalTo("foo"));
        MatcherAssert.assertThat(DockerHost.certPathFromEnv(), Matchers.nullValue());
    }

    @Test
    public void testFromUnixSocket() throws Exception {
        final String unixSocket = "unix:///var/run/docker.sock";
        final String certPath = "/path/to/cert";
        final URI unixSocketUri = new URI(unixSocket);
        final DockerHost dockerHost = DockerHost.from(unixSocket, certPath);
        MatcherAssert.assertThat(dockerHost.host(), Matchers.equalTo(unixSocket));
        MatcherAssert.assertThat(dockerHost.uri(), Matchers.equalTo(unixSocketUri));
        MatcherAssert.assertThat(dockerHost.bindUri(), Matchers.equalTo(unixSocketUri));
        MatcherAssert.assertThat(dockerHost.port(), Matchers.equalTo(0));
        MatcherAssert.assertThat(dockerHost.address(), Matchers.equalTo("localhost"));
        MatcherAssert.assertThat(dockerHost.dockerCertPath(), Matchers.equalTo(certPath));
    }

    @Test
    public void testFromTcpSocketNoCert() throws Exception {
        final String tcpSocket = "tcp://127.0.0.1:2375";
        final DockerHost dockerHost = DockerHost.from(tcpSocket, null);
        MatcherAssert.assertThat(dockerHost.host(), Matchers.equalTo("127.0.0.1:2375"));
        MatcherAssert.assertThat(dockerHost.uri(), Matchers.equalTo(new URI("http://127.0.0.1:2375")));
        MatcherAssert.assertThat(dockerHost.bindUri(), Matchers.equalTo(new URI(tcpSocket)));
        MatcherAssert.assertThat(dockerHost.port(), Matchers.equalTo(2375));
        MatcherAssert.assertThat(dockerHost.address(), Matchers.equalTo("127.0.0.1"));
        MatcherAssert.assertThat(dockerHost.dockerCertPath(), Matchers.nullValue());
    }

    @Test
    public void testFromTcpSocketWithCert() throws Exception {
        final String tcpSocket = "tcp://127.0.0.1:2375";
        final String certPath = "/path/to/cert";
        final DockerHost dockerHost = DockerHost.from(tcpSocket, certPath);
        MatcherAssert.assertThat(dockerHost.host(), Matchers.equalTo("127.0.0.1:2375"));
        MatcherAssert.assertThat(dockerHost.uri(), Matchers.equalTo(new URI("https://127.0.0.1:2375")));
        MatcherAssert.assertThat(dockerHost.bindUri(), Matchers.equalTo(new URI(tcpSocket)));
        MatcherAssert.assertThat(dockerHost.port(), Matchers.equalTo(2375));
        MatcherAssert.assertThat(dockerHost.address(), Matchers.equalTo("127.0.0.1"));
        MatcherAssert.assertThat(dockerHost.dockerCertPath(), Matchers.equalTo(certPath));
    }

    @Test
    public void testFromEnv() throws Exception {
        Mockito.when(systemDelegate.getProperty("os.name")).thenReturn("linux");
        DockerHost.setSystemDelegate(systemDelegate);
        final String dockerHostEnvVar = DockerHost.defaultDockerEndpoint();
        final boolean isUnixSocket = dockerHostEnvVar.startsWith("unix://");
        final URI dockerHostUri = new URI(dockerHostEnvVar);
        final String dockerHostAndPort;
        final URI dockerHostHttpUri;
        final URI dockerTcpUri;
        final int dockerHostPort;
        final String dockerHostHost;
        if (isUnixSocket) {
            dockerHostAndPort = dockerHostEnvVar;
            dockerHostHttpUri = dockerHostUri;
            dockerTcpUri = dockerHostUri;
            dockerHostPort = 0;
            dockerHostHost = "localhost";
        } else {
            dockerHostAndPort = ((dockerHostUri.getHost()) + ":") + (dockerHostUri.getPort());
            dockerHostHttpUri = new URI(("http://" + dockerHostAndPort));
            dockerTcpUri = new URI(("tcp://" + dockerHostAndPort));
            dockerHostPort = dockerHostUri.getPort();
            dockerHostHost = dockerHostUri.getHost();
        }
        final DockerHost dockerHost = DockerHost.fromEnv();
        MatcherAssert.assertThat(dockerHost.host(), Matchers.equalTo(dockerHostAndPort));
        MatcherAssert.assertThat(dockerHost.uri(), Matchers.equalTo(dockerHostHttpUri));
        MatcherAssert.assertThat(dockerHost.bindUri(), Matchers.equalTo(dockerTcpUri));
        MatcherAssert.assertThat(dockerHost.port(), Matchers.equalTo(dockerHostPort));
        MatcherAssert.assertThat(dockerHost.address(), Matchers.equalTo(dockerHostHost));
        MatcherAssert.assertThat(dockerHost.dockerCertPath(), Matchers.nullValue());
    }
}

