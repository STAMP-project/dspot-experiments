/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.tools.offlineImageViewer;


import java.io.File;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.web.WebHdfsFileSystem;
import org.apache.hadoop.net.NetUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests OfflineImageViewer if the input fsimage has XAttributes
 */
public class TestOfflineImageViewerForXAttr {
    private static final Logger LOG = LoggerFactory.getLogger(TestOfflineImageViewerForXAttr.class);

    private static File originalFsimage = null;

    static String attr1JSon;

    @Test
    public void testWebImageViewerForListXAttrs() throws Exception {
        try (WebImageViewer viewer = new WebImageViewer(NetUtils.createSocketAddr("localhost:0"))) {
            viewer.initServer(TestOfflineImageViewerForXAttr.originalFsimage.getAbsolutePath());
            int port = viewer.getPort();
            URL url = new URL((("http://localhost:" + port) + "/webhdfs/v1/dir1/?op=LISTXATTRS"));
            HttpURLConnection connection = ((HttpURLConnection) (url.openConnection()));
            connection.setRequestMethod("GET");
            connection.connect();
            Assert.assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode());
            String content = IOUtils.toString(connection.getInputStream());
            Assert.assertTrue("Missing user.attr1 in response ", content.contains("user.attr1"));
            Assert.assertTrue("Missing user.attr2 in response ", content.contains("user.attr2"));
        }
    }

    @Test
    public void testWebImageViewerForGetXAttrsWithOutParameters() throws Exception {
        try (WebImageViewer viewer = new WebImageViewer(NetUtils.createSocketAddr("localhost:0"))) {
            viewer.initServer(TestOfflineImageViewerForXAttr.originalFsimage.getAbsolutePath());
            int port = viewer.getPort();
            URL url = new URL((("http://localhost:" + port) + "/webhdfs/v1/dir1/?op=GETXATTRS"));
            HttpURLConnection connection = ((HttpURLConnection) (url.openConnection()));
            connection.setRequestMethod("GET");
            connection.connect();
            Assert.assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode());
            String content = IOUtils.toString(connection.getInputStream());
            Assert.assertTrue("Missing user.attr1 in response ", content.contains("user.attr1"));
            Assert.assertTrue("Missing user.attr2 in response ", content.contains("user.attr2"));
        }
    }

    @Test
    public void testWebImageViewerForGetXAttrsWithParameters() throws Exception {
        try (WebImageViewer viewer = new WebImageViewer(NetUtils.createSocketAddr("localhost:0"))) {
            viewer.initServer(TestOfflineImageViewerForXAttr.originalFsimage.getAbsolutePath());
            int port = viewer.getPort();
            URL url = new URL((("http://localhost:" + port) + "/webhdfs/v1/dir1/?op=GETXATTRS&xattr.name=attr8"));
            HttpURLConnection connection = ((HttpURLConnection) (url.openConnection()));
            connection.setRequestMethod("GET");
            connection.connect();
            Assert.assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, connection.getResponseCode());
            url = new URL((("http://localhost:" + port) + "/webhdfs/v1/dir1/?op=GETXATTRS&xattr.name=user.attr1"));
            connection = ((HttpURLConnection) (url.openConnection()));
            connection.setRequestMethod("GET");
            connection.connect();
            Assert.assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode());
            String content = IOUtils.toString(connection.getInputStream());
            Assert.assertEquals(TestOfflineImageViewerForXAttr.attr1JSon, content);
        }
    }

    @Test
    public void testWebImageViewerForGetXAttrsWithCodecParameters() throws Exception {
        try (WebImageViewer viewer = new WebImageViewer(NetUtils.createSocketAddr("localhost:0"))) {
            viewer.initServer(TestOfflineImageViewerForXAttr.originalFsimage.getAbsolutePath());
            int port = viewer.getPort();
            URL url = new URL((("http://localhost:" + port) + "/webhdfs/v1/dir1/?op=GETXATTRS&xattr.name=USER.attr1&encoding=TEXT"));
            HttpURLConnection connection = ((HttpURLConnection) (url.openConnection()));
            connection.setRequestMethod("GET");
            connection.connect();
            Assert.assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode());
            String content = IOUtils.toString(connection.getInputStream());
            Assert.assertEquals(TestOfflineImageViewerForXAttr.attr1JSon, content);
        }
    }

    @Test
    public void testWithWebHdfsFileSystem() throws Exception {
        try (WebImageViewer viewer = new WebImageViewer(NetUtils.createSocketAddr("localhost:0"))) {
            viewer.initServer(TestOfflineImageViewerForXAttr.originalFsimage.getAbsolutePath());
            int port = viewer.getPort();
            // create a WebHdfsFileSystem instance
            URI uri = new URI(("webhdfs://localhost:" + (String.valueOf(port))));
            Configuration conf = new Configuration();
            WebHdfsFileSystem webhdfs = ((WebHdfsFileSystem) (FileSystem.get(uri, conf)));
            List<String> names = webhdfs.listXAttrs(new Path("/dir1"));
            Assert.assertTrue(names.contains("user.attr1"));
            Assert.assertTrue(names.contains("user.attr2"));
            String value = new String(webhdfs.getXAttr(new Path("/dir1"), "user.attr1"));
            Assert.assertEquals("value1", value);
            value = new String(webhdfs.getXAttr(new Path("/dir1"), "USER.attr1"));
            Assert.assertEquals("value1", value);
            Map<String, byte[]> contentMap = webhdfs.getXAttrs(new Path("/dir1"), names);
            Assert.assertEquals("value1", new String(contentMap.get("user.attr1")));
            Assert.assertEquals("value2", new String(contentMap.get("user.attr2")));
        }
    }

    @Test
    public void testResponseCode() throws Exception {
        try (WebImageViewer viewer = new WebImageViewer(NetUtils.createSocketAddr("localhost:0"))) {
            viewer.initServer(TestOfflineImageViewerForXAttr.originalFsimage.getAbsolutePath());
            int port = viewer.getPort();
            URL url = new URL((("http://localhost:" + port) + "/webhdfs/v1/dir1/?op=GETXATTRS&xattr.name=user.notpresent&encoding=TEXT"));
            HttpURLConnection connection = ((HttpURLConnection) (url.openConnection()));
            connection.setRequestMethod("GET");
            connection.connect();
            Assert.assertEquals(HttpURLConnection.HTTP_FORBIDDEN, connection.getResponseCode());
        }
    }
}

