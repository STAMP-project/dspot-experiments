package io.fabric8.maven.docker.assembly;


import io.fabric8.maven.docker.config.AssemblyConfiguration;
import io.fabric8.maven.docker.config.BuildImageConfiguration;
import mockit.Injectable;
import mockit.Tested;
import org.apache.maven.plugins.assembly.archive.AssemblyArchiver;
import org.apache.maven.plugins.assembly.io.AssemblyReader;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.junit.Assert;
import org.junit.Test;


public class DockerAssemblyManagerTest {
    @Tested
    private DockerAssemblyManager assemblyManager;

    @Injectable
    private AssemblyArchiver assemblyArchiver;

    @Injectable
    private AssemblyReader assemblyReader;

    @Injectable
    private ArchiverManager archiverManager;

    @Injectable
    private MappingTrackArchiver trackArchiver;

    @Test
    public void testNoAssembly() {
        BuildImageConfiguration buildConfig = new BuildImageConfiguration();
        AssemblyConfiguration assemblyConfig = buildConfig.getAssemblyConfiguration();
        DockerFileBuilder builder = assemblyManager.createDockerFileBuilder(buildConfig, assemblyConfig);
        String content = builder.content();
        Assert.assertFalse(content.contains("COPY"));
        Assert.assertFalse(content.contains("VOLUME"));
    }
}

