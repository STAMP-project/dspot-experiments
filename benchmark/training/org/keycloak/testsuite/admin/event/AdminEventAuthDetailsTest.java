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
package org.keycloak.testsuite.admin.event;


import Constants.ADMIN_CLI_CLIENT_ID;
import org.junit.Assert;
import org.junit.ComparisonFailure;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.testsuite.AbstractAuthTest;
import org.keycloak.testsuite.util.AssertAdminEvents;


/**
 * Test authDetails in admin events
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class AdminEventAuthDetailsTest extends AbstractAuthTest {
    @Rule
    public AssertAdminEvents assertAdminEvents = new AssertAdminEvents(this);

    private String masterAdminCliUuid;

    private String masterAdminUserId;

    private String masterAdminUser2Id;

    private String realmUuid;

    private String client1Uuid;

    private String adminCliUuid;

    private String admin1Id;

    private String admin2Id;

    private String appUserId;

    @Test
    public void testAuth() {
        testClient(MASTER, ADMIN, ADMIN, ADMIN_CLI_CLIENT_ID, MASTER, masterAdminCliUuid, masterAdminUserId);
        testClient(MASTER, "admin2", "password", ADMIN_CLI_CLIENT_ID, MASTER, masterAdminCliUuid, masterAdminUser2Id);
        testClient("test", "admin1", "password", ADMIN_CLI_CLIENT_ID, realmUuid, adminCliUuid, admin1Id);
        testClient("test", "admin2", "password", ADMIN_CLI_CLIENT_ID, realmUuid, adminCliUuid, admin2Id);
        testClient("test", "admin1", "password", "client1", realmUuid, client1Uuid, admin1Id);
        testClient("test", "admin2", "password", "client1", realmUuid, client1Uuid, admin2Id);
        // Should fail due to different client UUID
        try {
            testClient("test", "admin1", "password", "client1", realmUuid, adminCliUuid, admin1Id);
            org.keycloak.testsuite.Assert.fail("Not expected to pass");
        } catch (ComparisonFailure expected) {
            // expected
        }
        // Should fail due to different user ID
        try {
            testClient("test", "admin1", "password", "client1", realmUuid, client1Uuid, admin2Id);
            org.keycloak.testsuite.Assert.fail("Not expected to pass");
        } catch (ComparisonFailure expected) {
            // expected
        }
    }
}

