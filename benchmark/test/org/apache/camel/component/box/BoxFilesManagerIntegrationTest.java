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
package org.apache.camel.component.box;


import BoxItem.Info;
import BoxSharedLink.Access.DEFAULT;
import ThumbnailFileType.JPG;
import com.box.sdk.BoxAPIException;
import com.box.sdk.BoxFile;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxItem;
import com.box.sdk.BoxSharedLink;
import com.box.sdk.Metadata;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.component.box.internal.BoxApiCollection;
import org.apache.camel.component.box.internal.BoxFilesManagerApiMethod;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test class for {@link BoxFilesManager}
 * APIs.
 */
public class BoxFilesManagerIntegrationTest extends AbstractBoxTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(BoxFilesManagerIntegrationTest.class);

    private static final String PATH_PREFIX = BoxApiCollection.getCollection().getApiName(BoxFilesManagerApiMethod.class).getName();

    private static final String CAMEL_TEST_FILE = "/CamelTestFile.txt";

    private static final String CAMEL_TEST_FILE_NAME = "CamelTestFile.txt";

    private static final String CAMEL_TEST_FILE_DESCRIPTION = "CamelTestFile.txt description";

    private static final String CAMEL_TEST_COPY_FILE_NAME = "CamelTestFile_Copy.txt";

    private static final String CAMEL_TEST_MOVE_FILE_NAME = "CamelTestFile_Move.txt";

    private static final String CAMEL_TEST_RENAME_FILE_NAME = "CamelTestFile_Rename.txt";

    private static final String CAMEL_TEST_UPLOAD_FILE_NAME = "CamelTestFile_Upload.txt";

    @Test
    public void testCopyFile() throws Exception {
        BoxFile result = null;
        try {
            final Map<String, Object> headers = new HashMap<>();
            // parameter type is String
            headers.put("CamelBox.fileId", testFile.getID());
            // parameter type is String
            headers.put("CamelBox.destinationFolderId", "0");
            // parameter type is String
            headers.put("CamelBox.newName", BoxFilesManagerIntegrationTest.CAMEL_TEST_COPY_FILE_NAME);
            result = requestBodyAndHeaders("direct://COPYFILE", null, headers);
            assertNotNull("copyFile result", result);
            assertEquals("copyFile name", BoxFilesManagerIntegrationTest.CAMEL_TEST_COPY_FILE_NAME, result.getInfo().getName());
            BoxFilesManagerIntegrationTest.LOG.debug(("copyFile: " + result));
        } finally {
            if (result != null) {
                result.delete();
            }
        }
    }

    @Test
    public void testCreateFileMetadata() throws Exception {
        Metadata metadata = new Metadata();
        metadata.add("/foo", "bar");
        final Map<String, Object> headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelBox.fileId", testFile.getID());
        // parameter type is com.box.sdk.Metadata
        headers.put("CamelBox.metadata", metadata);
        // parameter type is String
        headers.put("CamelBox.typeName", null);
        final Metadata result = requestBodyAndHeaders("direct://CREATEFILEMETADATA", null, headers);
        assertNotNull("createFileMetadata result", result);
        assertEquals("createFileMetadata result", "bar", result.get("/foo"));
        BoxFilesManagerIntegrationTest.LOG.debug(("createFileMetadata: " + result));
    }

    @Test
    public void testCreateFileSharedLink() throws Exception {
        final Map<String, Object> headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelBox.fileId", testFile.getID());
        // parameter type is com.box.sdk.BoxSharedLink.Access
        headers.put("CamelBox.access", DEFAULT);
        // parameter type is java.util.Date
        headers.put("CamelBox.unshareDate", null);
        // parameter type is com.box.sdk.BoxSharedLink.Permissions
        headers.put("CamelBox.permissions", null);
        final BoxSharedLink result = requestBodyAndHeaders("direct://CREATEFILESHAREDLINK", null, headers);
        assertNotNull("createFileSharedLink result", result);
        BoxFilesManagerIntegrationTest.LOG.debug(("createFileSharedLink: " + result));
    }

    @Test
    public void testDeleteFile() throws Exception {
        // using String message body for single parameter "fileId"
        requestBody("direct://DELETEFILE", testFile.getID());
        BoxFolder rootFolder = BoxFolder.getRootFolder(getConnection());
        Iterable<BoxItem.Info> it = rootFolder.search((("^" + (BoxFilesManagerIntegrationTest.CAMEL_TEST_FILE)) + "$"));
        int searchResults = sizeOfIterable(it);
        boolean exists = (searchResults > 0) ? true : false;
        assertEquals("deleteFile exists", false, exists);
        BoxFilesManagerIntegrationTest.LOG.debug(("deleteFile: exists? " + exists));
    }

    @Test
    public void testDeleteFileMetadata() throws Exception {
        testFile.createMetadata(new Metadata());
        // using String message body for single parameter "fileId"
        requestBody("direct://DELETEFILEMETADATA", testFile.getID());
        try {
            testFile.getMetadata();
        } catch (BoxAPIException e) {
            if ((e.getResponseCode()) == 404) {
                // Box API should return a
                return;
            }
        }
        fail("deleteFileMetadata metadata");
    }

    @Test
    public void testDownloadFile() throws Exception {
        final Map<String, Object> headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelBox.fileId", testFile.getID());
        // parameter type is java.io.OutputStream
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        headers.put("CamelBox.output", output);
        // parameter type is Long
        headers.put("CamelBox.rangeStart", null);
        // parameter type is Long
        headers.put("CamelBox.rangeEnd", null);
        // parameter type is com.box.sdk.ProgressListener
        headers.put("CamelBox.listener", null);
        final OutputStream result = requestBodyAndHeaders("direct://DOWNLOADFILE", null, headers);
        assertNotNull("downloadFile result", result);
        BoxFilesManagerIntegrationTest.LOG.debug(("downloadFile: " + result));
    }

    @Test
    public void testGetDownloadURL() throws Exception {
        // using String message body for single parameter "fileId"
        final URL result = requestBody("direct://GETDOWNLOADURL", testFile.getID());
        assertNotNull("getDownloadURL result", result);
        BoxFilesManagerIntegrationTest.LOG.debug(("getDownloadURL: " + result));
    }

    @Test
    public void testGetFileInfo() throws Exception {
        final Map<String, Object> headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelBox.fileId", testFile.getID());
        // parameter type is String[]
        headers.put("CamelBox.fields", null);
        final BoxFile.Info result = requestBodyAndHeaders("direct://GETFILEINFO", null, headers);
        assertNotNull("getFileInfo result", result);
        BoxFilesManagerIntegrationTest.LOG.debug(("getFileInfo: " + result));
    }

    @Test
    public void testGetFileMetadata() throws Exception {
        testFile.createMetadata(new Metadata());
        final Map<String, Object> headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelBox.fileId", testFile.getID());
        // parameter type is String
        headers.put("CamelBox.typeName", null);
        final Metadata result = requestBodyAndHeaders("direct://GETFILEMETADATA", null, headers);
        assertNotNull("getFileMetadata result", result);
        BoxFilesManagerIntegrationTest.LOG.debug(("getFileMetadata: " + result));
    }

    @Test
    public void testGetFilePreviewLink() throws Exception {
        // using String message body for single parameter "fileId"
        final URL result = requestBody("direct://GETFILEPREVIEWLINK", testFile.getID());
        assertNotNull("getFilePreviewLink result", result);
        BoxFilesManagerIntegrationTest.LOG.debug(("getFilePreviewLink: " + result));
    }

    @Test
    public void testGetFileThumbnail() throws Exception {
        final Map<String, Object> headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelBox.fileId", testFile.getID());
        // parameter type is com.box.sdk.BoxFile.ThumbnailFileType
        headers.put("CamelBox.fileType", JPG);
        // parameter type is Integer
        headers.put("CamelBox.minWidth", 32);
        // parameter type is Integer
        headers.put("CamelBox.minHeight", 32);
        // parameter type is Integer
        headers.put("CamelBox.maxWidth", 32);
        // parameter type is Integer
        headers.put("CamelBox.maxHeight", 32);
        final byte[] result = requestBodyAndHeaders("direct://GETFILETHUMBNAIL", null, headers);
        assertNotNull("getFileThumbnail result", result);
        BoxFilesManagerIntegrationTest.LOG.debug(("getFileThumbnail: " + result));
    }

    @Test
    public void testGetFileVersions() throws Exception {
        // using String message body for single parameter "fileId"
        @SuppressWarnings("rawtypes")
        final Collection result = requestBody("direct://GETFILEVERSIONS", testFile.getID());
        assertNotNull("getFileVersions result", result);
        BoxFilesManagerIntegrationTest.LOG.debug(("getFileVersions: " + result));
    }

    @Test
    public void testMoveFile() throws Exception {
        BoxFile result = null;
        try {
            final Map<String, Object> headers = new HashMap<>();
            // parameter type is String
            headers.put("CamelBox.fileId", testFile.getID());
            // parameter type is String
            headers.put("CamelBox.destinationFolderId", "0");
            // parameter type is String
            headers.put("CamelBox.newName", BoxFilesManagerIntegrationTest.CAMEL_TEST_MOVE_FILE_NAME);
            result = requestBodyAndHeaders("direct://MOVEFILE", null, headers);
            assertNotNull("moveFile result", result);
            assertEquals("moveFile name", BoxFilesManagerIntegrationTest.CAMEL_TEST_MOVE_FILE_NAME, result.getInfo().getName());
            BoxFilesManagerIntegrationTest.LOG.debug(("moveFile: " + result));
        } finally {
            if (result != null) {
                result.delete();
            }
        }
    }

    @Test
    public void testRenameFile() throws Exception {
        BoxFile result = null;
        try {
            final Map<String, Object> headers = new HashMap<>();
            // parameter type is String
            headers.put("CamelBox.fileId", testFile.getID());
            // parameter type is String
            headers.put("CamelBox.newFileName", BoxFilesManagerIntegrationTest.CAMEL_TEST_RENAME_FILE_NAME);
            result = requestBodyAndHeaders("direct://RENAMEFILE", null, headers);
            assertNotNull("renameFile result", result);
            assertEquals("renameFile name", BoxFilesManagerIntegrationTest.CAMEL_TEST_RENAME_FILE_NAME, result.getInfo().getName());
            BoxFilesManagerIntegrationTest.LOG.debug(("renameFile: " + result));
        } finally {
            if (result != null) {
                result.delete();
            }
        }
    }

    @Test
    public void testUpdateFileInfo() throws Exception {
        BoxFile.Info info = testFile.getInfo();
        info.setDescription(BoxFilesManagerIntegrationTest.CAMEL_TEST_FILE_DESCRIPTION);
        final Map<String, Object> headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelBox.fileId", testFile.getID());
        // parameter type is com.box.sdk.BoxFile.Info
        headers.put("CamelBox.info", info);
        final BoxFile result = requestBodyAndHeaders("direct://UPDATEFILEINFO", null, headers);
        assertNotNull("updateFileInfo result", result);
        assertEquals("updateFileInfo info", BoxFilesManagerIntegrationTest.CAMEL_TEST_FILE_DESCRIPTION, result.getInfo().getDescription());
        BoxFilesManagerIntegrationTest.LOG.debug(("updateFileInfo: " + result));
    }

    @Test
    public void testUpdateFileMetadata() throws Exception {
        Metadata metadata = new Metadata();
        // metadata.add("/foo", "bar");
        metadata = testFile.createMetadata(metadata);
        final Map<String, Object> headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelBox.fileId", testFile.getID());
        // parameter type is com.box.sdk.Metadata
        headers.put("CamelBox.metadata", metadata);
        final Metadata result = requestBodyAndHeaders("direct://UPDATEFILEMETADATA", null, headers);
        assertNotNull("updateFileMetadata result", result);
        BoxFilesManagerIntegrationTest.LOG.debug(("updateFileMetadata: " + result));
    }

    @Test
    public void testUploadNewFileVersion() throws Exception {
        BoxFile result = null;
        try {
            final Map<String, Object> headers = new HashMap<>();
            // parameter type is String
            headers.put("CamelBox.fileId", testFile.getID());
            // parameter type is java.io.InputStream
            headers.put("CamelBox.fileContent", getClass().getResourceAsStream(BoxFilesManagerIntegrationTest.CAMEL_TEST_FILE));
            // parameter type is java.util.Date
            headers.put("CamelBox.modified", null);
            // parameter type is Long
            headers.put("CamelBox.fileSize", null);
            // parameter type is com.box.sdk.ProgressListener
            headers.put("CamelBox.listener", null);
            result = requestBodyAndHeaders("direct://UPLOADNEWFILEVERSION", null, headers);
            assertNotNull("uploadNewFileVersion result", result);
            BoxFilesManagerIntegrationTest.LOG.debug(("uploadNewFileVersion: " + result));
        } finally {
            if (result != null) {
                try {
                    result.delete();
                } catch (Throwable t) {
                }
            }
        }
    }
}

