package io.fabric8.maven.docker.access;


import DockerConnectionDetector.ConnectionParameter;
import DockerConnectionDetector.DockerHostProvider;
import DockerConnectionDetector.EnvDockerHostProvider;
import DockerConnectionDetector.UnixSocketDockerHostProvider;
import DockerConnectionDetector.WindowsPipeDockerHostProvider;
import java.io.File;
import java.io.IOException;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Assert;
import org.junit.Test;


// any further testing requires a 'docker-machine' on the build host ...
public class DockerConnectionDetectorTest {
    DockerConnectionDetector detector = new DockerConnectionDetector(null);

    @Test
    public void testGetUrlFromHostConfig() throws IOException, MojoExecutionException {
        DockerConnectionDetector.ConnectionParameter param = detector.detectConnectionParameter("hostconfig", "certpath");
        Assert.assertEquals("hostconfig", param.getUrl());
        Assert.assertEquals("certpath", param.getCertPath());
    }

    @Test
    public void testGetUrlFromEnvironment() throws IOException, MojoExecutionException {
        String dockerHost = System.getenv("DOCKER_HOST");
        if (dockerHost != null) {
            Assert.assertEquals(dockerHost.replaceFirst("^tcp:/", ""), detector.detectConnectionParameter(null, null).getUrl().replaceFirst("^https?:/", ""));
        } else
            if (System.getProperty("os.name").equalsIgnoreCase("Windows 10")) {
                try {
                    Assert.assertEquals("npipe:////./pipe/docker_engine", detector.detectConnectionParameter(null, null).getUrl());
                } catch (IllegalArgumentException expectedIfNoUnixSocket) {
                    // expected if no unix socket
                }
            } else {
                try {
                    Assert.assertEquals("unix:///var/run/docker.sock", detector.detectConnectionParameter(null, null).getUrl());
                } catch (IllegalArgumentException expectedIfNoUnixSocket) {
                    // expected if no unix socket
                }
            }

    }

    @Test
    public void testOrderDefaultDockerHostProviders() {
        Class[] expectedProviders = new Class[]{ EnvDockerHostProvider.class, UnixSocketDockerHostProvider.class, WindowsPipeDockerHostProvider.class };
        int i = 0;
        for (DockerConnectionDetector.DockerHostProvider provider : detector.dockerHostProviders) {
            Assert.assertEquals(expectedProviders[(i++)], provider.getClass());
        }
    }

    @Test
    public void testGetCertPathFromEnvironment() throws IOException, MojoExecutionException {
        try {
            DockerConnectionDetector.ConnectionParameter param = detector.detectConnectionParameter(null, null);
            String certPath = System.getenv("DOCKER_CERT_PATH");
            if (certPath != null) {
                Assert.assertEquals(certPath, param.getCertPath());
            } else {
                String maybeUserDocker = param.getCertPath();
                if (maybeUserDocker != null) {
                    Assert.assertEquals(new File(System.getProperty("user.home"), ".docker").getAbsolutePath(), maybeUserDocker);
                }
            }
        } catch (IllegalArgumentException exp) {
            // Can happen if there is now docker connection configured in the environment
        }
    }
}

