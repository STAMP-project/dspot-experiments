package io.fabric8.maven.docker.service;


import io.fabric8.maven.docker.access.DockerAccess;
import io.fabric8.maven.docker.assembly.DockerAssemblyManager;
import io.fabric8.maven.docker.config.ImageConfiguration;
import io.fabric8.maven.docker.util.Logger;
import io.fabric8.maven.docker.util.MojoParameters;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import org.junit.Test;


public class BuildServiceTest {
    private static final String NEW_IMAGE_ID = "efg789efg789";

    private static final String OLD_IMAGE_ID = "abc123abc123";

    @Tested
    private BuildService buildService;

    @Injectable
    private DockerAccess docker;

    @Mocked
    private DockerAssemblyManager dockerAssemblyManager;

    private ImageConfiguration imageConfig;

    @Injectable
    private Logger log;

    private String oldImageId;

    @Mocked
    private MojoParameters params;

    @Injectable
    private QueryService queryService;

    @Injectable
    private ArchiveService archiveService;

    @Injectable
    private RegistryService registryService;

    @Test
    public void testBuildImageWithCleanup() throws Exception {
        givenAnImageConfiguration(true);
        givenImageIds(BuildServiceTest.OLD_IMAGE_ID, BuildServiceTest.NEW_IMAGE_ID);
        whenBuildImage(true, false);
        thenImageIsBuilt();
        thenOldImageIsRemoved();
    }

    @Test
    public void testBuildImageWithNoCleanup() throws Exception {
        givenAnImageConfiguration(false);
        givenImageIds(BuildServiceTest.OLD_IMAGE_ID, BuildServiceTest.NEW_IMAGE_ID);
        whenBuildImage(false, false);
        thenImageIsBuilt();
        thenOldImageIsNotRemoved();
    }

    @Test
    public void testCleanupCachedImage() throws Exception {
        givenAnImageConfiguration(true);
        givenImageIds(BuildServiceTest.OLD_IMAGE_ID, BuildServiceTest.OLD_IMAGE_ID);
        whenBuildImage(false, false);
        thenImageIsBuilt();
        thenOldImageIsNotRemoved();
    }

    @Test
    public void testCleanupNoExistingImage() throws Exception {
        givenAnImageConfiguration(true);
        givenImageIds(null, BuildServiceTest.NEW_IMAGE_ID);
        whenBuildImage(false, false);
        thenImageIsBuilt();
        thenOldImageIsNotRemoved();
    }
}

