package com.vaadin.tests.server;


import com.vaadin.server.DownloadStream;
import com.vaadin.server.FileResource;
import java.io.File;
import java.net.URISyntaxException;
import org.junit.Assert;
import org.junit.Test;


public class FileResourceTest {
    @Test(expected = IllegalArgumentException.class)
    public void nullFile() {
        new FileResource(null);
    }

    @Test(expected = RuntimeException.class)
    public void nonExistingFile() {
        new FileResource(new File("nonexisting")).getStream();
    }

    @Test
    public void bufferSize() throws URISyntaxException {
        File file = new File(getClass().getResource("../styles.scss").toURI());
        FileResource resource = new FileResource(file) {
            @Override
            public long getCacheTime() {
                return 5;
            }
        };
        resource.setBufferSize(100);
        resource.setCacheTime(200);
        DownloadStream downloadStream = resource.getStream();
        Assert.assertEquals("DownloadStream buffer size must be same as resource buffer size", resource.getBufferSize(), downloadStream.getBufferSize());
        Assert.assertEquals("DownloadStream cache time must be same as resource cache time", resource.getCacheTime(), downloadStream.getCacheTime());
    }
}

