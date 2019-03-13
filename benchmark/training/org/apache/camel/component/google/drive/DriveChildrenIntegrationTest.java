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


import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.drive.model.ChildList;
import com.google.api.services.drive.model.ChildReference;
import com.google.api.services.drive.model.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.component.google.drive.internal.DriveChildrenApiMethod;
import org.apache.camel.component.google.drive.internal.GoogleDriveApiCollection;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test class for {@link com.google.api.services.drive.Drive$Children} APIs.
 */
public class DriveChildrenIntegrationTest extends AbstractGoogleDriveTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(DriveChildrenIntegrationTest.class);

    private static final String PATH_PREFIX = GoogleDriveApiCollection.getCollection().getApiName(DriveChildrenApiMethod.class).getName();

    private static final String ROOT_FOLDER = "root";

    @Test
    public void testUploadFileToFolder() throws Exception {
        File folder = uploadTestFolder();
        File file = uploadTestFile();
        final Map<String, Object> headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelGoogleDrive.folderId", folder.getId());
        ChildReference child = new ChildReference();
        child.setId(file.getId());
        // parameter type is com.google.api.services.drive.model.ChildReference
        headers.put("CamelGoogleDrive.content", child);
        requestBodyAndHeaders("direct://INSERT", null, headers);
        final ChildList result = requestBody("direct://LIST", folder.getId());
        assertNotNull("insert result", result);
        DriveChildrenIntegrationTest.LOG.debug(("insert: " + result));
        headers.put("CamelGoogleDrive.childId", child.getId());
        ChildReference childReference = requestBodyAndHeaders("direct://GET", null, headers);
        assertNotNull("inserted child", childReference);
        requestBodyAndHeaders("direct://DELETE", null, headers);
        try {
            childReference = requestBodyAndHeaders("direct://GET", null, headers);
            fail("can't fetch a child that already get deleted");
        } catch (Exception ex) {
            assertTrue(((ex.getCause().getCause()) instanceof GoogleJsonResponseException));
        }
    }

    @Test
    public void testUploadFileToRootFolder() throws Exception {
        File file = uploadTestFile();
        final Map<String, Object> headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelGoogleDrive.folderId", DriveChildrenIntegrationTest.ROOT_FOLDER);
        ChildReference child = new ChildReference();
        child.setId(file.getId());
        // parameter type is com.google.api.services.drive.model.ChildReference
        headers.put("CamelGoogleDrive.content", child);
        requestBodyAndHeaders("direct://INSERT", null, headers);
        headers.put("CamelGoogleDrive.childId", child.getId());
        ChildReference childReference = requestBodyAndHeaders("direct://GET-BODY", child.getId(), headers);
        assertNotNull("inserted child", childReference);
        requestBodyAndHeaders("direct://DELETE", null, headers);
        try {
            childReference = requestBodyAndHeaders("direct://GET-BODY", child.getId(), headers, ChildReference.class);
            fail("can't fetch a child that already get deleted");
        } catch (Exception ex) {
            assertTrue(((ex.getCause().getCause()) instanceof GoogleJsonResponseException));
        }
    }
}

