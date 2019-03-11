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
package org.keycloak.testsuite.admin.client;


import java.util.List;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.UserSessionRepresentation;
import org.keycloak.testsuite.AbstractAuthTest;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.auth.page.account.AccountManagement;


/**
 *
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2016 Red Hat Inc.
 */
public class SessionTest extends AbstractClientTest {
    @Page
    protected AccountManagement testRealmAccountManagementPage;

    @Test
    public void testGetAppSessionCount() {
        ClientResource accountClient = findClientResourceById("account");
        int sessionCount = accountClient.getApplicationSessionCount().get("count");
        Assert.assertEquals(0, sessionCount);
        testRealmAccountManagementPage.navigateTo();
        loginPage.form().login(testUser);
        sessionCount = accountClient.getApplicationSessionCount().get("count");
        Assert.assertEquals(1, sessionCount);
        testRealmAccountManagementPage.signOut();
        sessionCount = accountClient.getApplicationSessionCount().get("count");
        Assert.assertEquals(0, sessionCount);
    }

    @Test
    public void testGetUserSessions() {
        // List<java.util.Map<String, String>> stats = this.testRealmResource().getClientSessionStats();
        ClientResource account = findClientResourceById("account");
        testRealmAccountManagementPage.navigateTo();
        loginPage.form().login(testUser);
        List<UserSessionRepresentation> sessions = account.getUserSessions(0, 5);
        Assert.assertEquals(1, sessions.size());
        UserSessionRepresentation rep = sessions.get(0);
        UserRepresentation testUserRep = getFullUserRep(testUser.getUsername());
        Assert.assertEquals(testUserRep.getId(), rep.getUserId());
        Assert.assertEquals(testUserRep.getUsername(), rep.getUsername());
        String clientId = account.toRepresentation().getId();
        Assert.assertEquals("account", rep.getClients().get(clientId));
        Assert.assertNotNull(rep.getIpAddress());
        Assert.assertNotNull(rep.getLastAccess());
        Assert.assertNotNull(rep.getStart());
        testRealmAccountManagementPage.signOut();
    }
}

