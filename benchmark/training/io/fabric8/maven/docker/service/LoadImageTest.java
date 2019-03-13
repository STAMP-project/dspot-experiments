package io.fabric8.maven.docker.service;


import io.fabric8.maven.docker.access.DockerAccess;
import io.fabric8.maven.docker.access.DockerAccessException;
import io.fabric8.maven.docker.config.ImageConfiguration;
import io.fabric8.maven.docker.util.Logger;
import io.fabric8.maven.docker.util.MojoParameters;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.junit.Test;


public class LoadImageTest {
    @Tested
    private BuildService buildService;

    @Injectable
    private DockerAccess docker;

    private ImageConfiguration imageConfig;

    @Injectable
    private Logger log;

    @Mocked
    private MavenProject project;

    @Mocked
    private MojoParameters params;

    @Injectable
    private QueryService queryService;

    @Injectable
    private ArchiveService archiveService;

    @Injectable
    private RegistryService registryService;

    private String dockerArchive;

    @Test
    public void testLoadImage() throws DockerAccessException, MojoExecutionException {
        givenMojoParameters();
        givenAnImageConfiguration();
        givenDockerArchive("test.tar");
        whenBuildImage();
        thenImageIsBuilt();
    }
}

