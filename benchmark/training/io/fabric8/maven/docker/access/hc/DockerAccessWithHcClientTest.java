package io.fabric8.maven.docker.access.hc;


import ArchiveCompression.none;
import io.fabric8.maven.docker.access.AuthConfig;
import io.fabric8.maven.docker.config.ArchiveCompression;
import io.fabric8.maven.docker.util.Logger;
import java.io.IOException;
import mockit.Mocked;
import org.junit.Test;


public class DockerAccessWithHcClientTest {
    private AuthConfig authConfig;

    private DockerAccessWithHcClient client;

    private String imageName;

    @Mocked
    private ApacheHttpClientDelegate mockDelegate;

    @Mocked
    private Logger mockLogger;

    private int pushRetries;

    private String registry;

    private Exception thrownException;

    private String archiveFile;

    private String filename;

    private ArchiveCompression compression;

    @Test
    public void testPushFailes_noRetry() throws Exception {
        givenAnImageName("test");
        givenThePushWillFail(0);
        whenPushImage();
        thenImageWasNotPushed();
    }

    @Test
    public void testRetryPush() throws Exception {
        givenAnImageName("test");
        givenANumberOfRetries(1);
        givenThePushWillFailAndEventuallySucceed(1);
        whenPushImage();
        thenImageWasPushed();
    }

    @Test
    public void testRetriesExceeded() throws Exception {
        givenAnImageName("test");
        givenANumberOfRetries(1);
        givenThePushWillFail(1);
        whenPushImage();
        thenImageWasNotPushed();
    }

    @Test
    public void testLoadImage() {
        givenAnImageName("test");
        givenArchiveFile("test.tar");
        whenLoadImage();
        thenNoException();
    }

    @Test
    public void testLoadImageFail() throws IOException {
        givenAnImageName("test");
        givenArchiveFile("test.tar");
        givenThePostWillFail();
        whenLoadImage();
        thenImageWasNotLoaded();
    }

    @Test
    public void testSaveImage() throws IOException {
        givenAnImageName("test");
        givenFilename("test.tar");
        givenCompression(none);
        whenSaveImage();
        thenNoException();
    }

    @Test
    public void testSaveImageFail() throws IOException {
        givenAnImageName("test");
        givenFilename("test.tar");
        givenCompression(none);
        givenTheGetWillFail();
        whenSaveImage();
        thenImageWasNotSaved();
    }
}

