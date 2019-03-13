/**
 * (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.ows;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 *
 *
 * @author Ian Schneider <ischneider@boundlessgeo.com>
 */
public class StylePublisherTest extends GeoServerSystemTestSupport {
    static StylePublisher publisher;

    static List<String[]> paths = new ArrayList<String[]>();

    @Test
    public void testEncoding() throws Exception {
        for (String[] path : StylePublisherTest.paths) {
            MockHttpServletResponse response = request(path, null);
            Assert.assertEquals(Arrays.toString(path), 200, response.getStatus());
            Assert.assertArrayEquals(IOUtils.toByteArray(this.getClass().getClassLoader().getResourceAsStream(("org/geoserver/ows/" + (path[((path.length) - 1)])))), response.getContentAsByteArray());
        }
    }

    @Test
    public void testOverride() throws Exception {
        String[] path = new String[]{ "styles", "override.png" };
        MockHttpServletResponse response = request(path, null);
        Assert.assertEquals(Arrays.toString(path), 200, response.getStatus());
        Assert.assertArrayEquals(IOUtils.toByteArray(this.getClass().getClassLoader().getResourceAsStream("org/geoserver/ows/smileyface.png")), response.getContentAsByteArray());
        path = new String[]{ "styles", "cite", "override.png" };
        response = request(path, null);
        Assert.assertEquals(Arrays.toString(path), 200, response.getStatus());
        Assert.assertArrayEquals(IOUtils.toByteArray(this.getClass().getClassLoader().getResourceAsStream("org/geoserver/ows/grass_fill.png")), response.getContentAsByteArray());
        path = new String[]{ "styles", "icons", "override.png" };
        response = request(path, null);
        Assert.assertEquals(Arrays.toString(path), 200, response.getStatus());
        Assert.assertArrayEquals(IOUtils.toByteArray(this.getClass().getClassLoader().getResourceAsStream("org/geoserver/ows/smileyface.png")), response.getContentAsByteArray());
        path = new String[]{ "styles", "cite", "icons", "override.png" };
        response = request(path, null);
        Assert.assertEquals(Arrays.toString(path), 200, response.getStatus());
        Assert.assertArrayEquals(IOUtils.toByteArray(this.getClass().getClassLoader().getResourceAsStream("org/geoserver/ows/grass_fill.png")), response.getContentAsByteArray());
    }

    @Test
    public void testLastModified() throws Exception {
        for (String[] path : StylePublisherTest.paths) {
            MockHttpServletResponse response = request(path, null);
            String lastModified = response.getHeader("Last-Modified");
            Assert.assertNotNull(lastModified);
            response = request(path, lastModified);
            Assert.assertEquals(304, response.getStatus());
            long timeStamp = (AbstractURLPublisher.lastModified(lastModified)) + 10000;
            response = request(path, AbstractURLPublisher.lastModified(timeStamp));
            Assert.assertEquals(304, response.getStatus());
            timeStamp -= 20000;
            response = request(path, AbstractURLPublisher.lastModified(timeStamp));
            Assert.assertEquals(200, response.getStatus());
            Assert.assertArrayEquals(IOUtils.toByteArray(this.getClass().getClassLoader().getResourceAsStream(("org/geoserver/ows/" + (path[((path.length) - 1)])))), response.getContentAsByteArray());
        }
    }
}

