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
package org.apache.ambari.view.pig.test;


import FileService.FileResourceRequest;
import java.io.IOException;
import java.util.UUID;
import javax.ws.rs.core.Response;
import org.apache.ambari.view.pig.HDFSTest;
import org.apache.ambari.view.pig.resources.files.FileResource;
import org.apache.ambari.view.pig.resources.files.FileService;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class FileTest extends HDFSTest {
    private static final int PAGINATOR_PAGE_SIZE = 4;

    private FileService fileService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testCreateFile() throws IOException, InterruptedException {
        String name = UUID.randomUUID().toString().replaceAll("-", "");
        Response response = doCreateFile(name, "12323");
        Assert.assertEquals(204, response.getStatus());
        String name2 = UUID.randomUUID().toString().replaceAll("-", "");
        Response response2 = doCreateFile(name2, "12323");
        Assert.assertEquals(204, response2.getStatus());
    }

    @Test
    public void testCreateFilePathNotExists() throws IOException, InterruptedException {
        Response response = doCreateFile("Luke", null, "/non/existent/path/");
        Assert.assertEquals(204, response.getStatus());// path created automatically

        Response response2 = doCreateFile("Leia", null, "/tmp/");
        Assert.assertEquals(204, response2.getStatus());
        thrown.expect(ServiceFormattedException.class);
        Response response3 = doCreateFile("Leia", null, "/tmp/");// file already exists

        Assert.assertEquals(400, response3.getStatus());
    }

    @Test
    public void testUpdateFileContent() throws Exception {
        String name = UUID.randomUUID().toString().replaceAll("-", "");
        String filePath = "/tmp/" + name;
        Response createdFile = doCreateFile(name, "some content");
        FileService.FileResourceRequest request = new FileService.FileResourceRequest();
        request.file = new FileResource();
        request.file.setFilePath(filePath);
        request.file.setFileContent("1234567890");// 10 bytes, 3*(4b page)

        Response response = fileService.updateFile(request, filePath);
        Assert.assertEquals(204, response.getStatus());
        Response response2 = fileService.getFile(filePath, 0L, null);
        Assert.assertEquals(200, response2.getStatus());
        JSONObject obj = ((JSONObject) (response2.getEntity()));
        Assert.assertTrue(obj.containsKey("file"));
        Assert.assertEquals("1234", getFileContent());
    }

    @Test
    public void testPagination() throws Exception {
        String name = UUID.randomUUID().toString().replaceAll("-", "");
        String filePath = "/tmp/" + name;
        doCreateFile(name, "1234567890");
        Response response = fileService.getFile(filePath, 0L, null);
        Assert.assertEquals(200, response.getStatus());
        JSONObject obj = ((JSONObject) (response.getEntity()));
        Assert.assertTrue(obj.containsKey("file"));
        Assert.assertEquals("1234", getFileContent());
        Assert.assertEquals(3, getPageCount());
        Assert.assertEquals(0, getPage());
        Assert.assertTrue(isHasNext());
        Assert.assertEquals(filePath, getFilePath());
        response = fileService.getFile(filePath, 1L, null);
        Assert.assertEquals(200, response.getStatus());
        obj = ((JSONObject) (response.getEntity()));
        Assert.assertEquals("5678", getFileContent());
        Assert.assertEquals(1, getPage());
        Assert.assertTrue(isHasNext());
        response = fileService.getFile(filePath, 2L, null);
        Assert.assertEquals(200, response.getStatus());
        obj = ((JSONObject) (response.getEntity()));
        Assert.assertEquals("90", getFileContent());
        Assert.assertEquals(2, getPage());
        Assert.assertFalse(isHasNext());
        thrown.expect(BadRequestFormattedException.class);
        fileService.getFile(filePath, 3L, null);
    }

    @Test
    public void testZeroLengthFile() throws Exception {
        String name = UUID.randomUUID().toString().replaceAll("-", "");
        String filePath = "/tmp/" + name;
        doCreateFile(name, "");
        Response response = fileService.getFile(filePath, 0L, null);
        Assert.assertEquals(200, response.getStatus());
        JSONObject obj = ((JSONObject) (response.getEntity()));
        Assert.assertEquals("", getFileContent());
        Assert.assertEquals(0, getPage());
        Assert.assertFalse(isHasNext());
    }

    @Test
    public void testFileNotFound() throws IOException, InterruptedException {
        thrown.expect(NotFoundFormattedException.class);
        fileService.getFile("/tmp/notExistentFile", 2L, null);
    }

    @Test
    public void testDeleteFile() throws IOException, InterruptedException {
        String name = UUID.randomUUID().toString().replaceAll("-", "");
        String filePath = "/tmp/" + name;
        Response createdFile = doCreateFile(name, "some content");
        Response response = fileService.deleteFile(filePath);
        Assert.assertEquals(204, response.getStatus());
        thrown.expect(NotFoundFormattedException.class);
        fileService.getFile(filePath, 0L, null);
    }
}

