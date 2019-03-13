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


import OperationType.CREATE;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.AdminEventRepresentation;
import org.keycloak.representations.idm.AuthDetailsRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;


/**
 * Test getting and filtering admin events.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2016 Red Hat Inc.
 */
public class AdminEventTest extends AbstractEventTest {
    @Test
    public void clearAdminEventsTest() {
        createUser("user0");
        Assert.assertThat(events().size(), Matchers.is(Matchers.equalTo(1)));
        testRealmResource().clearAdminEvents();
        Assert.assertThat(events(), Matchers.is(Matchers.empty()));
    }

    @Test
    public void adminEventAttributeTest() {
        createUser("user5");
        List<AdminEventRepresentation> events = events();
        Assert.assertThat(events().size(), Matchers.is(Matchers.equalTo(1)));
        AdminEventRepresentation event = events.get(0);
        Assert.assertThat(event.getTime(), Matchers.is(Matchers.greaterThan(0L)));
        Assert.assertThat(event.getRealmId(), Matchers.is(Matchers.equalTo(realmName())));
        Assert.assertThat(event.getOperationType(), Matchers.is(Matchers.equalTo("CREATE")));
        Assert.assertThat(event.getResourcePath(), Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(event.getError(), Matchers.is(Matchers.nullValue()));
        AuthDetailsRepresentation details = event.getAuthDetails();
        Assert.assertThat(details.getRealmId(), Matchers.is(Matchers.equalTo("master")));
        Assert.assertThat(details.getClientId(), Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(details.getUserId(), Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(details.getIpAddress(), Matchers.is(Matchers.notNullValue()));
    }

    @Test
    public void retrieveAdminEventTest() {
        createUser("user1");
        List<AdminEventRepresentation> events = events();
        Assert.assertThat(events.size(), Matchers.is(Matchers.equalTo(1)));
        AdminEventRepresentation event = events().get(0);
        Assert.assertThat(event.getOperationType(), Matchers.is(Matchers.equalTo("CREATE")));
        Assert.assertThat(event.getRealmId(), Matchers.is(Matchers.equalTo(realmName())));
        Assert.assertThat(event.getAuthDetails().getRealmId(), Matchers.is(Matchers.equalTo("master")));
        Assert.assertThat(event.getRepresentation(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testGetRepresentation() {
        configRep.setAdminEventsDetailsEnabled(Boolean.TRUE);
        saveConfig();
        createUser("user2");
        AdminEventRepresentation event = events().stream().filter(( adminEventRep) -> adminEventRep.getOperationType().equals("CREATE")).findFirst().orElseThrow(() -> new IllegalStateException("Wasn't able to obtain CREATE admin event."));
        Assert.assertThat(event.getRepresentation(), Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(event.getRepresentation(), Matchers.allOf(Matchers.containsString("foo"), Matchers.containsString("bar")));
    }

    @Test
    public void testFilterAdminEvents() {
        // two CREATE and one UPDATE
        createUser("user3");
        createUser("user4");
        updateRealm();
        Assert.assertThat(events().size(), Matchers.is(Matchers.equalTo(3)));
        List<AdminEventRepresentation> events = testRealmResource().getAdminEvents(Arrays.asList("CREATE"), null, null, null, null, null, null, null, null, null);
        Assert.assertThat(events.size(), Matchers.is(Matchers.equalTo(2)));
    }

    @Test
    public void defaultMaxResults() {
        RealmResource realm = adminClient.realms().realm("test");
        AdminEventRepresentation event = new AdminEventRepresentation();
        event.setOperationType(CREATE.toString());
        event.setAuthDetails(new AuthDetailsRepresentation());
        event.setRealmId(realm.toRepresentation().getId());
        for (int i = 0; i < 110; i++) {
            testingClient.testing("test").onAdminEvent(event, false);
        }
        Assert.assertThat(realm.getAdminEvents(null, null, null, null, null, null, null, null, null, null).size(), Matchers.is(Matchers.equalTo(100)));
        Assert.assertThat(realm.getAdminEvents(null, null, null, null, null, null, null, null, 0, 105).size(), Matchers.is(Matchers.equalTo(105)));
        Assert.assertThat(realm.getAdminEvents(null, null, null, null, null, null, null, null, 0, 1000).size(), Matchers.is(Matchers.greaterThanOrEqualTo(110)));
    }

    @Test
    public void updateRealmEventsConfig() {
        // change from OFF to ON should be stored
        configRep.setAdminEventsDetailsEnabled(Boolean.TRUE);
        configRep.setAdminEventsEnabled(Boolean.TRUE);
        saveConfig();
        checkUpdateRealmEventsConfigEvent(1);
        // any other change should be store too
        configRep.setEventsEnabled(Boolean.TRUE);
        saveConfig();
        checkUpdateRealmEventsConfigEvent(2);
        // change from ON to OFF should be stored too
        configRep.setAdminEventsEnabled(Boolean.FALSE);
        saveConfig();
        checkUpdateRealmEventsConfigEvent(3);
        // another change should not be stored cos it was OFF already
        configRep.setAdminEventsDetailsEnabled(Boolean.FALSE);
        saveConfig();
        Assert.assertThat(events().size(), Matchers.is(Matchers.equalTo(3)));
    }
}

