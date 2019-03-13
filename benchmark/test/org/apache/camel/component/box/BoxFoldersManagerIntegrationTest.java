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
import BoxSharedLink.Access.COLLABORATORS;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxItem;
import com.box.sdk.BoxSharedLink;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.component.box.internal.BoxApiCollection;
import org.apache.camel.component.box.internal.BoxFoldersManagerApiMethod;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test class for {@link BoxFoldersManager}
 * APIs.
 */
public class BoxFoldersManagerIntegrationTest extends AbstractBoxTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(BoxFoldersManagerIntegrationTest.class);

    private static final String PATH_PREFIX = BoxApiCollection.getCollection().getApiName(BoxFoldersManagerApiMethod.class).getName();

    private static final String CAMEL_TEST_FOLDER = "CamelTestFolder";

    private static final String CAMEL_TEST_FOLDER_DESCRIPTION = "This is a description of CamelTestFolder";

    private static final String CAMEL_TEST_COPY_FOLDER = (BoxFoldersManagerIntegrationTest.CAMEL_TEST_FOLDER) + "_Copy";

    private static final String CAMEL_TEST_MOVE_FOLDER = (BoxFoldersManagerIntegrationTest.CAMEL_TEST_FOLDER) + "_Move";

    private static final String CAMEL_TEST_RENAME_FOLDER = (BoxFoldersManagerIntegrationTest.CAMEL_TEST_FOLDER) + "_Rename";

    private static final String CAMEL_TEST_ROOT_FOLDER_ID = "0";

    private static final String CAMEL_TEST_DESTINATION_FOLDER_ID = "0";

    @Test
    public void testCreateFolder() throws Exception {
        // delete folder created in test setup.
        deleteTestFolder();
        final Map<String, Object> headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelBox.parentFolderId", "0");
        // parameter type is String
        headers.put("CamelBox.folderName", BoxFoldersManagerIntegrationTest.CAMEL_TEST_FOLDER);
        testFolder = requestBodyAndHeaders("direct://CREATEFOLDER", null, headers);
        assertNotNull("createFolder result", testFolder);
        assertEquals("createFolder folder name", BoxFoldersManagerIntegrationTest.CAMEL_TEST_FOLDER, testFolder.getInfo().getName());
        BoxFoldersManagerIntegrationTest.LOG.debug(("createFolder: " + (testFolder)));
    }

    @Test
    public void testCreateFolderByPath() throws Exception {
        // delete folder created in test setup.
        deleteTestFolder();
        final Map<String, Object> headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelBox.parentFolderId", "0");
        // parameter type is String[]
        headers.put("CamelBox.path", new String[]{ BoxFoldersManagerIntegrationTest.CAMEL_TEST_FOLDER });
        testFolder = requestBodyAndHeaders("direct://CREATEFOLDER", null, headers);
        assertNotNull("createFolder result", testFolder);
        assertEquals("createFolder folder name", BoxFoldersManagerIntegrationTest.CAMEL_TEST_FOLDER, testFolder.getInfo().getName());
        BoxFoldersManagerIntegrationTest.LOG.debug(("createFolder: " + (testFolder)));
    }

    @Test
    public void testDeleteFolder() throws Exception {
        // using String message body for single parameter "folderId"
        requestBody("direct://DELETEFOLDER", testFolder.getID());
        BoxFolder rootFolder = BoxFolder.getRootFolder(getConnection());
        Iterable<BoxItem.Info> it = rootFolder.search((("^" + (BoxFoldersManagerIntegrationTest.CAMEL_TEST_FOLDER)) + "$"));
        int searchResults = sizeOfIterable(it);
        boolean exists = (searchResults > 0) ? true : false;
        assertEquals("deleteFolder exists", false, exists);
        BoxFoldersManagerIntegrationTest.LOG.debug(("deleteFolder: exists? " + exists));
    }

    @Test
    public void testCopyFolder() throws Exception {
        BoxFolder result = null;
        try {
            final Map<String, Object> headers = new HashMap<>();
            // parameter type is String
            headers.put("CamelBox.folderId", testFolder.getID());
            // parameter type is String
            headers.put("CamelBox.destinationFolderId", BoxFoldersManagerIntegrationTest.CAMEL_TEST_DESTINATION_FOLDER_ID);
            // parameter type is String
            headers.put("CamelBox.newName", BoxFoldersManagerIntegrationTest.CAMEL_TEST_COPY_FOLDER);
            result = requestBodyAndHeaders("direct://COPYFOLDER", null, headers);
            assertNotNull("copyFolder result", result);
            assertEquals("copyFolder folder name", BoxFoldersManagerIntegrationTest.CAMEL_TEST_COPY_FOLDER, result.getInfo().getName());
            BoxFoldersManagerIntegrationTest.LOG.debug(("copyFolder: " + result));
        } finally {
            if (result != null) {
                try {
                    result.delete(true);
                } catch (Throwable t) {
                }
            }
        }
    }

    @Test
    public void testCreateSharedLink() throws Exception {
        final Map<String, Object> headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelBox.folderId", testFolder.getID());
        // parameter type is com.box.sdk.BoxSharedLink.Access
        headers.put("CamelBox.access", COLLABORATORS);
        // parameter type is java.util.Date
        headers.put("CamelBox.unshareDate", null);
        // parameter type is com.box.sdk.BoxSharedLink.Permissions
        headers.put("CamelBox.permissions", new BoxSharedLink.Permissions());
        final BoxSharedLink result = requestBodyAndHeaders("direct://CREATEFOLDERSHAREDLINK", null, headers);
        assertNotNull("createFolderSharedLink result", result);
        BoxFoldersManagerIntegrationTest.LOG.debug(("createFolderSharedLink: " + result));
    }

    @Test
    public void testGetFolder() throws Exception {
        // using String[] message body for single parameter "path"
        final BoxFolder result = requestBody("direct://GETFOLDER", new String[]{ BoxFoldersManagerIntegrationTest.CAMEL_TEST_FOLDER });
        assertNotNull("getFolder result", result);
        assertEquals("getFolder folder id", testFolder.getID(), result.getID());
        BoxFoldersManagerIntegrationTest.LOG.debug(("getFolder: " + result));
    }

    @Test
    public void testGetFolderInfo() throws Exception {
        final Map<String, Object> headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelBox.folderId", testFolder.getID());
        // parameter type is String[]
        headers.put("CamelBox.fields", new String[]{ "name" });
        final BoxFolder.Info result = requestBodyAndHeaders("direct://GETFOLDERINFO", null, headers);
        assertNotNull("getFolderInfo result", result);
        assertNotNull("getFolderInfo result.getName()", result.getName());
        assertEquals("getFolderInfo info name", BoxFoldersManagerIntegrationTest.CAMEL_TEST_FOLDER, result.getName());
        BoxFoldersManagerIntegrationTest.LOG.debug(("getFolderInfo: " + result));
    }

    @Test
    public void testGetFolderItems() throws Exception {
        final Map<String, Object> headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelBox.folderId", BoxFoldersManagerIntegrationTest.CAMEL_TEST_ROOT_FOLDER_ID);
        // parameter type is Long
        headers.put("CamelBox.offset", null);
        // parameter type is Long
        headers.put("CamelBox.limit", null);
        // parameter type is String[]
        headers.put("CamelBox.fields", null);
        @SuppressWarnings("rawtypes")
        final Collection result = requestBodyAndHeaders("direct://GETFOLDERITEMS", null, headers);
        assertNotNull("getFolderItems result", result);
        BoxFoldersManagerIntegrationTest.LOG.debug(("getFolderItems: " + result));
    }

    @Test
    public void testGetRootFolder() throws Exception {
        final BoxFolder result = requestBody("direct://GETROOTFOLDER", null);
        assertNotNull("getRootFolder result", result);
        BoxFoldersManagerIntegrationTest.LOG.debug(("getRootFolder: " + result));
    }

    @Test
    public void testMoveFolder() throws Exception {
        final Map<String, Object> headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelBox.folderId", testFolder.getID());
        // parameter type is String
        headers.put("CamelBox.destinationFolderId", BoxFoldersManagerIntegrationTest.CAMEL_TEST_DESTINATION_FOLDER_ID);
        // parameter type is String
        headers.put("CamelBox.newName", BoxFoldersManagerIntegrationTest.CAMEL_TEST_MOVE_FOLDER);
        final BoxFolder result = requestBodyAndHeaders("direct://MOVEFOLDER", null, headers);
        assertNotNull("moveFolder result", result);
        assertEquals("moveFolder folder name", BoxFoldersManagerIntegrationTest.CAMEL_TEST_MOVE_FOLDER, result.getInfo().getName());
        BoxFoldersManagerIntegrationTest.LOG.debug(("moveFolder: " + result));
    }

    @Test
    public void testRenameFolder() throws Exception {
        final Map<String, Object> headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelBox.folderId", testFolder.getID());
        // parameter type is String
        headers.put("CamelBox.newFolderName", BoxFoldersManagerIntegrationTest.CAMEL_TEST_RENAME_FOLDER);
        final BoxFolder result = requestBodyAndHeaders("direct://RENAMEFOLDER", null, headers);
        assertNotNull("renameFolder result", result);
        assertEquals("moveFolder folder name", BoxFoldersManagerIntegrationTest.CAMEL_TEST_RENAME_FOLDER, result.getInfo().getName());
        BoxFoldersManagerIntegrationTest.LOG.debug(("renameFolder: " + result));
    }

    @Test
    public void testUpdateInfo() throws Exception {
        final BoxFolder.Info testFolderInfo = testFolder.getInfo();
        final Map<String, Object> headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelBox.folderId", testFolder.getID());
        // parameter type is com.box.sdk.BoxFolder.Info
        testFolderInfo.setDescription(BoxFoldersManagerIntegrationTest.CAMEL_TEST_FOLDER_DESCRIPTION);
        headers.put("CamelBox.info", testFolderInfo);
        final BoxFolder result = requestBodyAndHeaders("direct://UPDATEFOLDERINFO", null, headers);
        assertNotNull("updateInfo result", result);
        assertEquals("update folder info description", BoxFoldersManagerIntegrationTest.CAMEL_TEST_FOLDER_DESCRIPTION, result.getInfo().getDescription());
        BoxFoldersManagerIntegrationTest.LOG.debug(("updateInfo: " + result));
    }
}

