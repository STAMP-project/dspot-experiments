package com.vaadin.tests.server;


import com.vaadin.server.DownloadStream;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import java.net.URISyntaxException;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class StreamResourceTest {
    @Test
    public void testEqualsWithNullFields() {
        StreamResource resource1 = new StreamResource(null, null);
        StreamResource resource2 = new StreamResource(null, null);
        Assert.assertEquals(resource1, resource2);
    }

    @Test
    public void testNotEqualsWithNullFields() {
        StreamResource resource1 = new StreamResource(null, null);
        StreamResource resource2 = new StreamResource(EasyMock.createMock(StreamSource.class), "");
        Assert.assertNotEquals(resource1, resource2);
    }

    @Test
    public void testHashCodeForNullFields() {
        StreamResource resource = new StreamResource(null, null);
        // No NPE
        resource.hashCode();
    }

    @Test
    public void cacheTime() throws URISyntaxException {
        StreamResource resource = new StreamResource(EasyMock.createMock(StreamSource.class), "") {
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

