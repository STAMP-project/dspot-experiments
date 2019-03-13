package org.testcontainers.dockerclient;


import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import org.junit.Assert;
import org.junit.Test;


public class DockerClientConfigUtilsTest {
    @Test
    public void getDockerHostIpAddressShouldReturnLocalhostWhenUnixSocket() {
        DockerClientConfig configuration = // TODO - check wrt. https://github.com/docker-java/docker-java/issues/588
        DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerHost("unix:///var/run/docker.sock").withDockerTlsVerify(false).build();
        String actual = DockerClientConfigUtils.getDockerHostIpAddress(configuration);
        Assert.assertEquals("localhost", actual);
    }

    @Test
    public void getDockerHostIpAddressShouldReturnDockerHostIpWhenTcpUri() {
        DockerClientConfig configuration = // TODO - check wrt. https://github.com/docker-java/docker-java/issues/588
        DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerHost("tcp://12.23.34.45").withDockerTlsVerify(false).build();
        String actual = DockerClientConfigUtils.getDockerHostIpAddress(configuration);
        Assert.assertEquals("12.23.34.45", actual);
    }
}

