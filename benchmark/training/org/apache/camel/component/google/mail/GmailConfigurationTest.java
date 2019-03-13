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


import org.apache.camel.component.google.mail.internal.GmailUsersMessagesApiMethod;
import org.apache.camel.component.google.mail.internal.GoogleMailApiCollection;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test class for {@link GoogleMailConfiguration}.
 */
public class GmailConfigurationTest extends AbstractGoogleMailTestSupport {
    // userid of the currently authenticated user
    public static final String CURRENT_USERID = "me";

    private static final Logger LOG = LoggerFactory.getLogger(GmailConfigurationTest.class);

    private static final String PATH_PREFIX = GoogleMailApiCollection.getCollection().getApiName(GmailUsersMessagesApiMethod.class).getName();

    private static final String TEST_URI = ("google-mail://" + (GmailConfigurationTest.PATH_PREFIX)) + "/send?clientId=a&clientSecret=b&applicationName=c&accessToken=d&refreshToken=e";

    @Test
    public void testConfiguration() throws Exception {
        GoogleMailEndpoint endpoint = getMandatoryEndpoint(GmailConfigurationTest.TEST_URI, GoogleMailEndpoint.class);
        GoogleMailConfiguration configuration = endpoint.getConfiguration();
        assertNotNull(configuration);
        assertEquals("a", configuration.getClientId());
        assertEquals("b", configuration.getClientSecret());
        assertEquals("c", configuration.getApplicationName());
        assertEquals("d", configuration.getAccessToken());
        assertEquals("e", configuration.getRefreshToken());
    }
}

