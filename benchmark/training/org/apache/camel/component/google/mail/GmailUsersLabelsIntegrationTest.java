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
package org.apache.camel.component.google.mail;


import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.component.google.mail.internal.GmailUsersLabelsApiMethod;
import org.apache.camel.component.google.mail.internal.GoogleMailApiCollection;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test class for {@link com.google.api.services.gmail.Gmail$Users$Labels} APIs.
 */
public class GmailUsersLabelsIntegrationTest extends AbstractGoogleMailTestSupport {
    private static final String CAMEL_TEST_LABEL = "CamelTestLabel";

    private static final Logger LOG = LoggerFactory.getLogger(GmailUsersLabelsIntegrationTest.class);

    private static final String PATH_PREFIX = GoogleMailApiCollection.getCollection().getApiName(GmailUsersLabelsApiMethod.class).getName();

    @Test
    public void testLabels() throws Exception {
        // using String message body for single parameter "userId"
        ListLabelsResponse labels = requestBody("direct://LIST", AbstractGoogleMailTestSupport.CURRENT_USERID);
        String labelId = null;
        if ((getTestLabel(labels)) == null) {
            Map<String, Object> headers = new HashMap<>();
            // parameter type is String
            headers.put("CamelGoogleMail.userId", AbstractGoogleMailTestSupport.CURRENT_USERID);
            Label label = new Label().setName(GmailUsersLabelsIntegrationTest.CAMEL_TEST_LABEL).setMessageListVisibility("show").setLabelListVisibility("labelShow");
            // parameter type is com.google.api.services.gmail.model.Label
            headers.put("CamelGoogleMail.content", label);
            Label result = requestBodyAndHeaders("direct://CREATE", null, headers);
            assertNotNull("create result", result);
            labelId = result.getId();
        } else {
            labelId = getTestLabel(labels).getId();
        }
        // using String message body for single parameter "userId"
        labels = requestBody("direct://LIST", AbstractGoogleMailTestSupport.CURRENT_USERID);
        assertTrue(((getTestLabel(labels)) != null));
        Map<String, Object> headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelGoogleMail.userId", AbstractGoogleMailTestSupport.CURRENT_USERID);
        // parameter type is String
        headers.put("CamelGoogleMail.id", labelId);
        requestBodyAndHeaders("direct://DELETE", null, headers);
        // using String message body for single parameter "userId"
        labels = requestBody("direct://LIST", AbstractGoogleMailTestSupport.CURRENT_USERID);
        assertTrue(((getTestLabel(labels)) == null));
    }
}

