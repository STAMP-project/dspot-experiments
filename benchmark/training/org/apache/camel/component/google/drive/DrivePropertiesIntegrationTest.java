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


import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.PropertyList;
import org.apache.camel.component.google.drive.internal.DrivePropertiesApiMethod;
import org.apache.camel.component.google.drive.internal.GoogleDriveApiCollection;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test class for com.google.api.services.drive.Drive$Properties APIs.
 */
public class DrivePropertiesIntegrationTest extends AbstractGoogleDriveTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(DrivePropertiesIntegrationTest.class);

    private static final String PATH_PREFIX = GoogleDriveApiCollection.getCollection().getApiName(DrivePropertiesApiMethod.class).getName();

    @Test
    public void testList() throws Exception {
        File testFile = uploadTestFile();
        String fileId = testFile.getId();
        // using String message body for single parameter "fileId"
        final PropertyList result = requestBody("direct://LIST", fileId);
        assertNotNull("list result", result);
        DrivePropertiesIntegrationTest.LOG.debug(("list: " + result));
    }
}

