/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.google.drive;


import com.google.api.client.http.FileContent;
import com.google.api.client.util.DateTime;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.camel.component.google.drive.internal.DriveFilesApiMethod;
import org.apache.camel.component.google.drive.internal.GoogleDriveApiCollection;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test class for com.google.api.services.drive.Drive$Files APIs.
 */
public class DriveFilesIntegrationTest extends AbstractGoogleDriveTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(DriveFilesIntegrationTest.class);

    private static final String PATH_PREFIX = GoogleDriveApiCollection.getCollection().getApiName(DriveFilesApiMethod.class).getName();

    @Test
    public void testCopy() throws Exception {
        File testFile = uploadTestFile();
        String fromFileId = testFile.getId();
        File toFile = new File();
        toFile.setTitle(((AbstractGoogleDriveTestSupport.UPLOAD_FILE.getName()) + "_copy"));
        final Map<String, Object> headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelGoogleDrive.fileId", fromFileId);
        // parameter type is com.google.api.services.drive.model.File
        headers.put("CamelGoogleDrive.content", toFile);
        final File result = requestBodyAndHeaders("direct://COPY", null, headers);
        assertNotNull("copy result", result);
        assertEquals(toFile.getTitle(), result.getTitle());
        DriveFilesIntegrationTest.LOG.debug(("copy: " + result));
    }

    @Test
    public void testDelete() throws Exception {
        File testFile = uploadTestFile();
        String fileId = testFile.getId();
        // using String message body for single parameter "fileId"
        sendBody("direct://DELETE", fileId);
        try {
            // the file should be gone now
            final File result = requestBody("direct://GET", fileId);
            assertTrue("Should have not found deleted file.", false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGet() throws Exception {
        File testFile = uploadTestFile();
        String fileId = testFile.getId();
        // using String message body for single parameter "fileId"
        final File result = requestBody("direct://GET", fileId);
        assertNotNull("get result", result);
        DriveFilesIntegrationTest.LOG.debug(("get: " + result));
    }

    @Test
    public void testInsert() throws Exception {
        File file = new File();
        file.setTitle(AbstractGoogleDriveTestSupport.UPLOAD_FILE.getName());
        // using com.google.api.services.drive.model.File message body for single parameter "content"
        File result = requestBody("direct://INSERT", file);
        assertNotNull("insert result", result);
        DriveFilesIntegrationTest.LOG.debug(("insert: " + result));
    }

    @Test
    public void testInsert1() throws Exception {
        File result = uploadTestFile();
        assertNotNull("insert result", result);
        DriveFilesIntegrationTest.LOG.debug(("insert: " + result));
    }

    @Test
    public void testList() throws Exception {
        // upload a test file
        File testFile = uploadTestFile();
        FileList result = requestBody("direct://LIST", null);
        assertNotNull("list result", result);
        assertTrue(((result.getItems().size()) >= 1));
        File testFile2 = uploadTestFile();
        Map<String, Object> headers = new HashMap<>();
        headers.put("CamelGoogleDrive.maxResults", 1);
        result = requestBodyAndHeaders("direct://LIST", null, headers);
        assertNotNull("list result", result);
        assertTrue(((result.getItems().size()) == 1));
        // test paging the list
        List<File> resultList = new ArrayList<>();
        String pageToken;
        int i = 0;
        do {
            result = requestBodyAndHeaders("direct://LIST", null, headers);
            resultList.addAll(result.getItems());
            pageToken = result.getNextPageToken();
            headers.put("CamelGoogleDrive.pageToken", pageToken);
            i++;
        } while (((pageToken != null) && ((pageToken.length()) > 0)) && (i < 2) );
        // we should have 2 files in result (one file for each of the 2 pages)
        assertTrue(((resultList.size()) == 2));
        // they should be different files
        assertFalse(resultList.get(0).getId().equals(resultList.get(1)));
    }

    @Test
    public void testPatch() throws Exception {
        File file = uploadTestFile();
        // lets update the filename
        file.setTitle(((AbstractGoogleDriveTestSupport.UPLOAD_FILE.getName()) + "PATCHED"));
        final Map<String, Object> headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelGoogleDrive.fileId", file.getId());
        // parameter type is String
        headers.put("CamelGoogleDrive.fields", "title");
        // parameter type is com.google.api.services.drive.model.File
        headers.put("CamelGoogleDrive.content", file);
        File result = requestBodyAndHeaders("direct://PATCH", null, headers);
        assertNotNull("patch result", result);
        assertEquals(((AbstractGoogleDriveTestSupport.UPLOAD_FILE.getName()) + "PATCHED"), result.getTitle());
        DriveFilesIntegrationTest.LOG.debug(("patch: " + result));
    }

    @Test
    public void testTouch() throws Exception {
        File theTestFile = uploadTestFile();
        DateTime createdDate = theTestFile.getModifiedDate();
        // using String message body for single parameter "fileId"
        File result = requestBody("direct://TOUCH", theTestFile.getId());
        assertNotNull("touch result", result);
        assertTrue(((result.getModifiedDate().getValue()) > (createdDate.getValue())));
    }

    @Test
    public void testTrash() throws Exception {
        File testFile = uploadTestFile();
        String fileId = testFile.getId();
        assertNotNull("trash result", requestBody("direct://TRASH", fileId));
        assertNotNull("untrash result", requestBody("direct://UNTRASH", fileId));
    }

    @Test
    public void testUpdate() throws Exception {
        File theTestFile = uploadTestFile();
        final Map<String, Object> headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelGoogleDrive.fileId", theTestFile.getId());
        // parameter type is com.google.api.services.drive.model.File
        headers.put("CamelGoogleDrive.content", theTestFile);
        File result = requestBodyAndHeaders("direct://UPDATE", null, headers);
        assertNotNull("update result", result);
        DriveFilesIntegrationTest.LOG.debug(("update: " + result));
    }

    @Test
    public void testUpdate1() throws Exception {
        // First retrieve the file from the API.
        File testFile = uploadTestFile();
        String fileId = testFile.getId();
        // using String message body for single parameter "fileId"
        final File file = requestBody("direct://GET", fileId);
        // File's new metadata.
        file.setTitle("camel.png");
        // File's new content.
        java.io.File fileContent = new java.io.File(AbstractGoogleDriveTestSupport.TEST_UPLOAD_IMG);
        FileContent mediaContent = new FileContent(null, fileContent);
        // Send the request to the API.
        final Map<String, Object> headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelGoogleDrive.fileId", fileId);
        // parameter type is com.google.api.services.drive.model.File
        headers.put("CamelGoogleDrive.content", file);
        // parameter type is com.google.api.client.http.AbstractInputStreamContent
        headers.put("CamelGoogleDrive.mediaContent", mediaContent);
        File result = requestBodyAndHeaders("direct://UPDATE_1", null, headers);
        assertNotNull("update result", result);
        DriveFilesIntegrationTest.LOG.debug(("update: " + result));
    }
}

