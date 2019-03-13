/**
 * Copyright 2016 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.keycloak.testsuite.admin.event;


import EventType.LOGIN;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.EventRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.console.page.events.LoginEvents;


/**
 * Test getting and filtering login-related events.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2016 Red Hat Inc.
 */
/* Removed this test because it takes too long.  The default interval for
event cleanup is 15 minutes (900 seconds).  I don't have time to figure out
a way to set the cleanup thread to a lower interval for testing.
@Test
public void eventExpirationTest() {
configRep.setEventsExpiration(1L); //  second
saveConfig();
badLogin();
assertEquals(1, events().size());
pause(900); // pause 900 seconds
assertEquals(0, events().size());
}*
 */
public class LoginEventsTest extends AbstractEventTest {
    @Page
    private LoginEvents loginEventsPage;

    @Test
    public void eventAttributesTest() {
        badLogin();
        List<EventRepresentation> events = events();
        Assert.assertEquals(1, events.size());
        EventRepresentation event = events.get(0);
        Assert.assertTrue(((event.getTime()) > 0));
        Assert.assertNotNull(event.getIpAddress());
        Assert.assertEquals("LOGIN_ERROR", event.getType());
        Assert.assertEquals(realmName(), event.getRealmId());
        Assert.assertNull(event.getUserId());// no user for bad login

        Assert.assertNull(event.getSessionId());// no session for bad login

        Assert.assertEquals("user_not_found", event.getError());
        Map<String, String> details = event.getDetails();
        Assert.assertEquals("openid-connect", details.get("auth_method"));
        Assert.assertEquals("code", details.get("auth_type"));
        Assert.assertNotNull(details.get("redirect_uri"));
        Assert.assertNotNull(details.get("code_id"));
        Assert.assertEquals("bad", details.get("username"));
    }

    @Test
    public void clearEventsTest() {
        Assert.assertEquals(0, events().size());
        badLogin();
        badLogin();
        Assert.assertEquals(2, events().size());
        testRealmResource().clearEvents();
        Assert.assertEquals(0, events().size());
    }

    @Test
    public void loggingOfCertainTypeTest() {
        Assert.assertEquals(0, events().size());
        configRep.setEnabledEventTypes(Arrays.asList("REVOKE_GRANT"));
        saveConfig();
        badLogin();
        Assert.assertEquals(0, events().size());
        configRep.setEnabledEventTypes(Arrays.asList("LOGIN_ERROR"));
        saveConfig();
        badLogin();
        Assert.assertEquals(1, events().size());
    }

    @Test
    public void filterTest() {
        badLogin();
        badLogin();
        Assert.assertEquals(2, events().size());
        List<EventRepresentation> filteredEvents = testRealmResource().getEvents(Arrays.asList("REVOKE_GRANT"), null, null, null, null, null, null, null);
        Assert.assertEquals(0, filteredEvents.size());
        filteredEvents = testRealmResource().getEvents(Arrays.asList("LOGIN_ERROR"), null, null, null, null, null, null, null);
        Assert.assertEquals(2, filteredEvents.size());
    }

    @Test
    public void defaultMaxResults() {
        RealmResource realm = adminClient.realms().realm("test");
        EventRepresentation event = new EventRepresentation();
        event.setRealmId(realm.toRepresentation().getId());
        event.setType(LOGIN.toString());
        for (int i = 0; i < 110; i++) {
            testingClient.testing("test").onEvent(event);
        }
        Assert.assertEquals(100, realm.getEvents(null, null, null, null, null, null, null, null).size());
        Assert.assertEquals(105, realm.getEvents(null, null, null, null, null, null, 0, 105).size());
        Assert.assertTrue(((realm.getEvents(null, null, null, null, null, null, 0, 1000).size()) >= 110));
    }
}

