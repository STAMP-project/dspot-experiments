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


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.component.box.internal.BoxApiCollection;
import org.apache.camel.component.box.internal.BoxSearchManagerApiMethod;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test class for {@link BoxSearchManager}
 * APIs.
 */
public class BoxSearchManagerIntegrationTest extends AbstractBoxTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(BoxSearchManagerIntegrationTest.class);

    private static final String PATH_PREFIX = BoxApiCollection.getCollection().getApiName(BoxSearchManagerApiMethod.class).getName();

    private static final String CAMEL_TEST_FILE = "/CamelTestFile.txt";

    private static final String CAMEL_TEST_FILE_NAME = "CamelTestFile.txt";

    @Test
    public void testSearchFolder() throws Exception {
        final Map<String, Object> headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelBox.folderId", "0");
        // parameter type is String
        headers.put("CamelBox.query", BoxSearchManagerIntegrationTest.CAMEL_TEST_FILE_NAME);
        @SuppressWarnings("rawtypes")
        final Collection result = requestBodyAndHeaders("direct://SEARCHFOLDER", null, headers);
        assertNotNull("searchFolder result", result);
        assertEquals("searchFolder file found", 1, result.size());
        BoxSearchManagerIntegrationTest.LOG.debug(("searchFolder: " + result));
    }
}

