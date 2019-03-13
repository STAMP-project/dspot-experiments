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


import com.google.common.collect.Maps;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.AclStatus;
import org.apache.hadoop.hdfs.web.WebHdfsFileSystem;
import org.apache.hadoop.net.NetUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Tests OfflineImageViewer if the input fsimage has HDFS ACLs
 */
public class TestOfflineImageViewerForAcl {
    private static final Logger LOG = LoggerFactory.getLogger(TestOfflineImageViewerForAcl.class);

    private static File originalFsimage = null;

    // ACLs as set to dfs, to be compared with viewer's output
    static final HashMap<String, AclStatus> writtenAcls = Maps.newHashMap();

    @Test
    public void testWebImageViewerForAcl() throws Exception {
        WebImageViewer viewer = new WebImageViewer(NetUtils.createSocketAddr("localhost:0"));
        try {
            viewer.initServer(TestOfflineImageViewerForAcl.originalFsimage.getAbsolutePath());
            int port = viewer.getPort();
            // create a WebHdfsFileSystem instance
            URI uri = new URI(("webhdfs://localhost:" + (String.valueOf(port))));
            Configuration conf = new Configuration();
            WebHdfsFileSystem webhdfs = ((WebHdfsFileSystem) (FileSystem.get(uri, conf)));
            // GETACLSTATUS operation to a directory without ACL
            AclStatus acl = webhdfs.getAclStatus(new Path("/dirWithNoAcl"));
            Assert.assertEquals(TestOfflineImageViewerForAcl.writtenAcls.get("/dirWithNoAcl"), acl);
            // GETACLSTATUS operation to a directory with a default ACL
            acl = webhdfs.getAclStatus(new Path("/dirWithDefaultAcl"));
            Assert.assertEquals(TestOfflineImageViewerForAcl.writtenAcls.get("/dirWithDefaultAcl"), acl);
            // GETACLSTATUS operation to a file without ACL
            acl = webhdfs.getAclStatus(new Path("/noAcl"));
            Assert.assertEquals(TestOfflineImageViewerForAcl.writtenAcls.get("/noAcl"), acl);
            // GETACLSTATUS operation to a file with a ACL
            acl = webhdfs.getAclStatus(new Path("/withAcl"));
            Assert.assertEquals(TestOfflineImageViewerForAcl.writtenAcls.get("/withAcl"), acl);
            // GETACLSTATUS operation to a file with several ACL entries
            acl = webhdfs.getAclStatus(new Path("/withSeveralAcls"));
            Assert.assertEquals(TestOfflineImageViewerForAcl.writtenAcls.get("/withSeveralAcls"), acl);
            // GETACLSTATUS operation to a invalid path
            URL url = new URL((("http://localhost:" + port) + "/webhdfs/v1/invalid/?op=GETACLSTATUS"));
            HttpURLConnection connection = ((HttpURLConnection) (url.openConnection()));
            connection.setRequestMethod("GET");
            connection.connect();
            Assert.assertEquals(HttpURLConnection.HTTP_NOT_FOUND, connection.getResponseCode());
        } finally {
            // shutdown the viewer
            viewer.close();
        }
    }

    @Test
    public void testPBImageXmlWriterForAcl() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream o = new PrintStream(output);
        PBImageXmlWriter v = new PBImageXmlWriter(new Configuration(), o);
        v.visit(new RandomAccessFile(TestOfflineImageViewerForAcl.originalFsimage, "r"));
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser parser = spf.newSAXParser();
        final String xml = output.toString();
        parser.parse(new InputSource(new StringReader(xml)), new DefaultHandler());
    }

    @Test
    public void testPBDelimitedWriterForAcl() throws Exception {
        final String DELIMITER = "\t";
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream o = new PrintStream(output)) {
            PBImageDelimitedTextWriter v = new PBImageDelimitedTextWriter(o, DELIMITER, "");// run in memory.

            v.visit(new RandomAccessFile(TestOfflineImageViewerForAcl.originalFsimage, "r"));
        }
        try (ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            String line;
            boolean header = true;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(DELIMITER);
                if (!header) {
                    String filePath = fields[0];
                    String permission = fields[9];
                    if (!(filePath.equals("/"))) {
                        boolean hasAcl = !(filePath.toLowerCase().contains("noacl"));
                        Assert.assertEquals(hasAcl, permission.endsWith("+"));
                    }
                }
                header = false;
            } 
        }
    }
}

