/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geoserver.ows;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 *
 *
 * @author Ian Schneider <ischneider@boundlessgeo.com>
 */
public class FilePublisherTest {
    static FilePublisher publisher;

    static List<String[]> paths = new ArrayList<String[]>();

    @Test
    public void testEncoding() throws Exception {
        for (String[] path : FilePublisherTest.paths) {
            MockHttpServletResponse response = request(path, null);
            Assert.assertEquals(Arrays.toString(path), 200, response.getStatus());
            Assert.assertEquals(path[((path.length) - 1)], response.getContentAsString());
        }
    }

    @Test
    public void testLastModified() throws Exception {
        for (String[] path : FilePublisherTest.paths) {
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
            Assert.assertEquals(path[((path.length) - 1)], response.getContentAsString());
        }
    }
}

