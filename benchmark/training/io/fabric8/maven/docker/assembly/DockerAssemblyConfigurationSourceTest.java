package io.fabric8.maven.docker.assembly;


import io.fabric8.maven.docker.config.AssemblyConfiguration;
import io.fabric8.maven.docker.util.MojoParameters;
import java.io.File;
import java.util.Arrays;
import org.apache.maven.project.MavenProject;
import org.junit.Assert;
import org.junit.Test;


public class DockerAssemblyConfigurationSourceTest {
    private AssemblyConfiguration assemblyConfig;

    @SuppressWarnings("deprecation")
    @Test
    public void permissionMode() {
        try {
            new AssemblyConfiguration.Builder().permissions("blub").build();
        } catch (IllegalArgumentException exp) {
            Assert.assertTrue(exp.getMessage().contains("blub"));
        }
        AssemblyConfiguration config = new AssemblyConfiguration.Builder().ignorePermissions(false).permissions("ignore").build();
        Assert.assertTrue(config.isIgnorePermissions());
    }

    @Test
    public void testCreateSourceAbsolute() {
        testCreateSource(buildParameters(".", "/src/docker".replace("/", File.separator), "/output/docker".replace("/", File.separator)));
    }

    @Test
    public void testCreateSourceRelative() {
        testCreateSource(buildParameters(".", "src/docker".replace("/", File.separator), "output/docker".replace("/", File.separator)));
    }

    @Test
    public void testOutputDirHasImage() {
        String image = "image";
        MojoParameters params = buildParameters(".", "src/docker", "output/docker");
        DockerAssemblyConfigurationSource source = new DockerAssemblyConfigurationSource(params, new BuildDirs(image, params), assemblyConfig);
        Assert.assertTrue(containsDir(image, source.getOutputDirectory()));
        Assert.assertTrue(containsDir(image, source.getWorkingDirectory()));
        Assert.assertTrue(containsDir(image, source.getTemporaryRootDirectory()));
    }

    @Test
    public void testEmptyAssemblyConfig() {
        DockerAssemblyConfigurationSource source = new DockerAssemblyConfigurationSource(new MojoParameters(null, null, null, null, null, null, "/src/docker", "/output/docker", null), null, null);
        Assert.assertEquals(0, source.getDescriptors().length);
    }

    @Test
    public void testReactorProjects() {
        MavenProject reactorProject1 = new MavenProject();
        reactorProject1.setFile(new File("../reactor-1"));
        MavenProject reactorProject2 = new MavenProject();
        reactorProject2.setFile(new File("../reactor-2"));
        DockerAssemblyConfigurationSource source = new DockerAssemblyConfigurationSource(new MojoParameters(null, null, null, null, null, null, "/src/docker", "/output/docker", Arrays.asList(new MavenProject[]{ reactorProject1, reactorProject2 })), null, null);
        Assert.assertEquals(2, source.getReactorProjects().size());
    }
}

