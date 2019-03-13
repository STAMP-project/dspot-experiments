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


import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.component.google.mail.internal.GmailUsersMessagesApiMethod;
import org.apache.camel.component.google.mail.internal.GoogleMailApiCollection;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test class for {@link com.google.api.services.gmail.Gmail$Users$Messages}
 * APIs.
 */
public class GmailUsersMessagesIntegrationTest extends AbstractGoogleMailTestSupport {
    // userid of the currently authenticated user
    public static final String CURRENT_USERID = "me";

    private static final Logger LOG = LoggerFactory.getLogger(GmailUsersMessagesIntegrationTest.class);

    private static final String PATH_PREFIX = GoogleMailApiCollection.getCollection().getApiName(GmailUsersMessagesApiMethod.class).getName();

    @Test
    public void testMessages() throws Exception {
        // ==== Send test email ====
        Message testEmail = createTestEmail();
        Map<String, Object> headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelGoogleMail.userId", GmailUsersMessagesIntegrationTest.CURRENT_USERID);
        // parameter type is com.google.api.services.gmail.model.Message
        headers.put("CamelGoogleMail.content", testEmail);
        Message result = requestBodyAndHeaders("direct://SEND", null, headers);
        assertNotNull("send result", result);
        String testEmailId = result.getId();
        // ==== Search for message we just sent ====
        headers = new HashMap<>();
        headers.put("CamelGoogleMail.q", "subject:\"Hello from camel-google-mail\"");
        // using String message body for single parameter "userId"
        ListMessagesResponse listOfMessages = requestBody("direct://LIST", GmailUsersMessagesIntegrationTest.CURRENT_USERID);
        assertTrue(idInList(testEmailId, listOfMessages));
        // ===== trash it ====
        headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelGoogleMail.userId", GmailUsersMessagesIntegrationTest.CURRENT_USERID);
        // parameter type is String
        headers.put("CamelGoogleMail.id", testEmailId);
        requestBodyAndHeaders("direct://TRASH", null, headers);
        // ==== Search for message we just trashed ====
        headers = new HashMap<>();
        headers.put("CamelGoogleMail.q", "subject:\"Hello from camel-google-mail\"");
        // using String message body for single parameter "userId"
        listOfMessages = requestBody("direct://LIST", GmailUsersMessagesIntegrationTest.CURRENT_USERID);
        assertFalse(idInList(testEmailId, listOfMessages));
        // ===== untrash it ====
        headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelGoogleMail.userId", GmailUsersMessagesIntegrationTest.CURRENT_USERID);
        // parameter type is String
        headers.put("CamelGoogleMail.id", testEmailId);
        requestBodyAndHeaders("direct://UNTRASH", null, headers);
        // ==== Search for message we just trashed ====
        headers = new HashMap<>();
        headers.put("CamelGoogleMail.q", "subject:\"Hello from camel-google-mail\"");
        // using String message body for single parameter "userId"
        listOfMessages = requestBody("direct://LIST", GmailUsersMessagesIntegrationTest.CURRENT_USERID);
        assertTrue(idInList(testEmailId, listOfMessages));
        // ===== permanently delete it ====
        headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelGoogleMail.userId", GmailUsersMessagesIntegrationTest.CURRENT_USERID);
        // parameter type is String
        headers.put("CamelGoogleMail.id", testEmailId);
        requestBodyAndHeaders("direct://DELETE", null, headers);
        // ==== Search for message we just deleted ====
        headers = new HashMap<>();
        headers.put("CamelGoogleMail.q", "subject:\"Hello from camel-google-mail\"");
        // using String message body for single parameter "userId"
        listOfMessages = requestBody("direct://LIST", GmailUsersMessagesIntegrationTest.CURRENT_USERID);
        assertFalse(idInList(testEmailId, listOfMessages));
    }
}

