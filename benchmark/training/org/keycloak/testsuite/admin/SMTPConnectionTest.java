/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.testsuite.admin;


import java.util.Map;
import javax.ws.rs.core.Response;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.ComponentRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.util.GreenMailRule;


/**
 *
 *
 * @author <a href="mailto:bruno@abstractj.org">Bruno Oliveira</a>
 */
public class SMTPConnectionTest extends AbstractKeycloakTest {
    @Rule
    public GreenMailRule greenMailRule = new GreenMailRule();

    private RealmResource realm;

    @Test
    public void testWithNullSettings() throws Exception {
        Response response = realm.testSMTPConnection(settings(null, null, null, null, null, null, null, null));
        assertStatus(response, 500);
    }

    @Test
    public void testWithProperSettings() throws Exception {
        Response response = realm.testSMTPConnection(settings("127.0.0.1", "3025", "auto@keycloak.org", null, null, null, null, null));
        assertStatus(response, 204);
        assertMailReceived();
    }

    @Test
    public void testWithAuthEnabledCredentialsEmpty() throws Exception {
        Response response = realm.testSMTPConnection(settings("127.0.0.1", "3025", "auto@keycloak.org", "true", null, null, null, null));
        assertStatus(response, 500);
    }

    @Test
    public void testWithAuthEnabledValidCredentials() throws Exception {
        greenMailRule.credentials("admin@localhost", "admin");
        Response response = realm.testSMTPConnection(settings("127.0.0.1", "3025", "auto@keycloak.org", "true", null, null, "admin@localhost", "admin"));
        assertStatus(response, 204);
    }

    @Test
    public void testAuthEnabledAndSavedCredentials() throws Exception {
        RealmRepresentation realmRep = realm.toRepresentation();
        Map<String, String> oldSmtp = realmRep.getSmtpServer();
        try {
            realmRep.setSmtpServer(smtpMap("127.0.0.1", "3025", "auto@keycloak.org", "true", null, null, "admin@localhost", "admin", null, null));
            realm.update(realmRep);
            greenMailRule.credentials("admin@localhost", "admin");
            Response response = realm.testSMTPConnection(settings("127.0.0.1", "3025", "auto@keycloak.org", "true", null, null, "admin@localhost", ComponentRepresentation.SECRET_VALUE));
            assertStatus(response, 204);
        } finally {
            // Revert SMTP back
            realmRep.setSmtpServer(oldSmtp);
            realm.update(realmRep);
        }
    }
}

