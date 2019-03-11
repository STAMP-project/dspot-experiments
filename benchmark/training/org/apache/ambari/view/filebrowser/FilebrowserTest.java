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
package org.apache.ambari.view.filebrowser;


import DownloadService.DownloadRequest;
import FileOperationService.MkdirRequest;
import java.util.Map;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.ambari.view.ViewContext;
import org.apache.ambari.view.ViewResourceHandler;
import org.apache.ambari.view.commons.hdfs.FileOperationService;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Test;


public class FilebrowserTest {
    private ViewResourceHandler handler;

    private ViewContext context;

    private HttpHeaders httpHeaders;

    private UriInfo uriInfo;

    private Map<String, String> properties;

    private FileBrowserService fileBrowserService;

    private MiniDFSCluster hdfsCluster;

    public static final String BASE_URI = "http://localhost:8084/myapp/";

    @Test
    public void testListDir() throws Exception {
        FileOperationService.MkdirRequest request = new FileOperationService.MkdirRequest();
        request.path = "/tmp1";
        fileBrowserService.fileOps().mkdir(request);
        Response response = fileBrowserService.fileOps().listdir("/", null);
        JSONObject responseObject = ((JSONObject) (response.getEntity()));
        JSONArray statuses = ((JSONArray) (responseObject.get("files")));
        System.out.println(response.getEntity());
        Assert.assertEquals(200, response.getStatus());
        Assert.assertTrue(((statuses.size()) > 0));
        System.out.println(statuses);
    }

    @Test
    public void testUploadFile() throws Exception {
        Response response = uploadFile("/tmp/", "testUpload", ".tmp", "Hello world");
        Assert.assertEquals(200, response.getStatus());
        Response listdir = fileBrowserService.fileOps().listdir("/tmp", null);
        JSONObject responseObject = ((JSONObject) (listdir.getEntity()));
        JSONArray statuses = ((JSONArray) (responseObject.get("files")));
        System.out.println(statuses.size());
        Response response2 = fileBrowserService.download().browse("/tmp/testUpload.tmp", false, false, httpHeaders, uriInfo);
        Assert.assertEquals(200, response2.getStatus());
    }

    @Test
    public void testStreamingGzip() throws Exception {
        String gzipDir = "/tmp/testGzip";
        createDirectoryWithFiles(gzipDir);
        DownloadService.DownloadRequest dr = new DownloadService.DownloadRequest();
        dr.entries = new String[]{ gzipDir };
        Response result = fileBrowserService.download().downloadGZip(dr);
    }

    @Test
    public void testStreamingDownloadGzipName() throws Exception {
        String gzipDir = "/tmp/testGzip1";
        createDirectoryWithFiles(gzipDir);
        // test download 1 folder
        validateDownloadZipName(new String[]{ gzipDir }, "testGzip1.zip");
        // test download 1 folder
        validateDownloadZipName(new String[]{ gzipDir + "/testGzip11.txt" }, "testGzip11.txt.zip");
        String gzipDir2 = "/tmp/testGzip2";
        createDirectoryWithFiles(gzipDir2);
        // test download 2 folders
        validateDownloadZipName(new String[]{ gzipDir, gzipDir2 }, "hdfs.zip");
        // test download 2 files of same folder
        validateDownloadZipName(new String[]{ gzipDir + "/testGzip11", gzipDir + "/testGzip12" }, "hdfs.zip");
        // test download 2 files of different folder -- although I think UI does not allow it
        validateDownloadZipName(new String[]{ gzipDir + "/testGzip11", gzipDir2 + "/testGzip21" }, "hdfs.zip");
    }

    @Test
    public void testUsername() throws Exception {
        Assert.assertEquals(System.getProperty("user.name"), fileBrowserService.upload().getDoAsUsername(context));
        properties.put("webhdfs.username", "test-user");
        Assert.assertEquals("test-user", fileBrowserService.upload().getDoAsUsername(context));
    }
}

