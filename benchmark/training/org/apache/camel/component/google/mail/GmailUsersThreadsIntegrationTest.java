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


import com.google.api.services.gmail.model.ListThreadsResponse;
import com.google.api.services.gmail.model.Message;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.component.google.mail.internal.GmailUsersThreadsApiMethod;
import org.apache.camel.component.google.mail.internal.GoogleMailApiCollection;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test class for {@link com.google.api.services.gmail.Gmail$Users$Threads}
 * APIs.
 */
public class GmailUsersThreadsIntegrationTest extends AbstractGoogleMailTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(GmailUsersThreadsIntegrationTest.class);

    private static final String PATH_PREFIX = GoogleMailApiCollection.getCollection().getApiName(GmailUsersThreadsApiMethod.class).getName();

    @Test
    public void testList() throws Exception {
        Message m1 = createThreadedTestEmail(null);
        Message m2 = createThreadedTestEmail(m1.getThreadId());
        Map<String, Object> headers = new HashMap<>();
        headers.put("CamelGoogleMail.q", "subject:\"Hello from camel-google-mail\"");
        // using String message body for single parameter "userId"
        ListThreadsResponse result = requestBodyAndHeaders("direct://LIST", AbstractGoogleMailTestSupport.CURRENT_USERID, headers);
        assertNotNull("list result", result);
        assertTrue(((result.getThreads().size()) > 0));
        GmailUsersThreadsIntegrationTest.LOG.debug(("list: " + result));
        headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelGoogleMail.userId", AbstractGoogleMailTestSupport.CURRENT_USERID);
        // parameter type is String
        headers.put("CamelGoogleMail.id", m1.getThreadId());
        requestBodyAndHeaders("direct://DELETE", null, headers);
    }
}

