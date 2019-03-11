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
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.ContentSummary;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.web.WebHdfsFileSystem;
import org.apache.hadoop.net.NetUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests GETCONTENTSUMMARY operation for WebImageViewer
 */
public class TestOfflineImageViewerForContentSummary {
    private static final Logger LOG = LoggerFactory.getLogger(TestOfflineImageViewerForContentSummary.class);

    private static File originalFsimage = null;

    private static ContentSummary summaryFromDFS = null;

    private static ContentSummary emptyDirSummaryFromDFS = null;

    private static ContentSummary fileSummaryFromDFS = null;

    private static ContentSummary symLinkSummaryFromDFS = null;

    private static ContentSummary symLinkSummaryForDirContainsFromDFS = null;

    @Test
    public void testGetContentSummaryForEmptyDirectory() throws Exception {
        try (WebImageViewer viewer = new WebImageViewer(NetUtils.createSocketAddr("localhost:0"))) {
            viewer.initServer(TestOfflineImageViewerForContentSummary.originalFsimage.getAbsolutePath());
            int port = viewer.getPort();
            URL url = new URL((("http://localhost:" + port) + "/webhdfs/v1/parentDir/childDir2?op=GETCONTENTSUMMARY"));
            HttpURLConnection connection = ((HttpURLConnection) (url.openConnection()));
            connection.setRequestMethod("GET");
            connection.connect();
            Assert.assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode());
            // create a WebHdfsFileSystem instance
            URI uri = new URI(("webhdfs://localhost:" + (String.valueOf(port))));
            Configuration conf = new Configuration();
            WebHdfsFileSystem webfs = ((WebHdfsFileSystem) (FileSystem.get(uri, conf)));
            ContentSummary summary = webfs.getContentSummary(new Path("/parentDir/childDir2"));
            verifyContentSummary(TestOfflineImageViewerForContentSummary.emptyDirSummaryFromDFS, summary);
        }
    }

    @Test
    public void testGetContentSummaryForDirectory() throws Exception {
        try (WebImageViewer viewer = new WebImageViewer(NetUtils.createSocketAddr("localhost:0"))) {
            viewer.initServer(TestOfflineImageViewerForContentSummary.originalFsimage.getAbsolutePath());
            int port = viewer.getPort();
            URL url = new URL((("http://localhost:" + port) + "/webhdfs/v1/parentDir/?op=GETCONTENTSUMMARY"));
            HttpURLConnection connection = ((HttpURLConnection) (url.openConnection()));
            connection.setRequestMethod("GET");
            connection.connect();
            Assert.assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode());
            // create a WebHdfsFileSystem instance
            URI uri = new URI(("webhdfs://localhost:" + (String.valueOf(port))));
            Configuration conf = new Configuration();
            WebHdfsFileSystem webfs = ((WebHdfsFileSystem) (FileSystem.get(uri, conf)));
            ContentSummary summary = webfs.getContentSummary(new Path("/parentDir/"));
            verifyContentSummary(TestOfflineImageViewerForContentSummary.summaryFromDFS, summary);
        }
    }

    @Test
    public void testGetContentSummaryForFile() throws Exception {
        try (WebImageViewer viewer = new WebImageViewer(NetUtils.createSocketAddr("localhost:0"))) {
            viewer.initServer(TestOfflineImageViewerForContentSummary.originalFsimage.getAbsolutePath());
            int port = viewer.getPort();
            URL url = new URL((("http://localhost:" + port) + "/webhdfs/v1/parentDir/file1?op=GETCONTENTSUMMARY"));
            HttpURLConnection connection = ((HttpURLConnection) (url.openConnection()));
            connection.setRequestMethod("GET");
            connection.connect();
            Assert.assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode());
            // create a WebHdfsFileSystem instance
            URI uri = new URI(("webhdfs://localhost:" + (String.valueOf(port))));
            Configuration conf = new Configuration();
            WebHdfsFileSystem webfs = ((WebHdfsFileSystem) (FileSystem.get(uri, conf)));
            ContentSummary summary = webfs.getContentSummary(new Path("/parentDir/file1"));
            verifyContentSummary(TestOfflineImageViewerForContentSummary.fileSummaryFromDFS, summary);
        }
    }

    @Test
    public void testGetContentSummaryForSymlink() throws Exception {
        try (WebImageViewer viewer = new WebImageViewer(NetUtils.createSocketAddr("localhost:0"))) {
            viewer.initServer(TestOfflineImageViewerForContentSummary.originalFsimage.getAbsolutePath());
            int port = viewer.getPort();
            // create a WebHdfsFileSystem instance
            URI uri = new URI(("webhdfs://localhost:" + (String.valueOf(port))));
            Configuration conf = new Configuration();
            WebHdfsFileSystem webfs = ((WebHdfsFileSystem) (FileSystem.get(uri, conf)));
            ContentSummary summary = webfs.getContentSummary(new Path("/link1"));
            verifyContentSummary(TestOfflineImageViewerForContentSummary.symLinkSummaryFromDFS, summary);
        }
    }

    @Test
    public void testGetContentSummaryForDirContainsSymlink() throws Exception {
        try (WebImageViewer viewer = new WebImageViewer(NetUtils.createSocketAddr("localhost:0"))) {
            viewer.initServer(TestOfflineImageViewerForContentSummary.originalFsimage.getAbsolutePath());
            int port = viewer.getPort();
            // create a WebHdfsFileSystem instance
            URI uri = new URI(("webhdfs://localhost:" + (String.valueOf(port))));
            Configuration conf = new Configuration();
            WebHdfsFileSystem webfs = ((WebHdfsFileSystem) (FileSystem.get(uri, conf)));
            ContentSummary summary = webfs.getContentSummary(new Path("/dirForLinks/"));
            verifyContentSummary(TestOfflineImageViewerForContentSummary.symLinkSummaryForDirContainsFromDFS, summary);
        }
    }

    @Test
    public void testGetContentSummaryResponseCode() throws Exception {
        try (WebImageViewer viewer = new WebImageViewer(NetUtils.createSocketAddr("localhost:0"))) {
            viewer.initServer(TestOfflineImageViewerForContentSummary.originalFsimage.getAbsolutePath());
            int port = viewer.getPort();
            URL url = new URL((("http://localhost:" + port) + "/webhdfs/v1/dir123/?op=GETCONTENTSUMMARY"));
            HttpURLConnection connection = ((HttpURLConnection) (url.openConnection()));
            connection.setRequestMethod("GET");
            connection.connect();
            Assert.assertEquals(HttpURLConnection.HTTP_NOT_FOUND, connection.getResponseCode());
        }
    }
}

