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


import LDAPConnectionTestManager.TEST_AUTHENTICATION;
import LDAPConnectionTestManager.TEST_CONNECTION;
import javax.ws.rs.core.Response;
import org.junit.ClassRule;
import org.junit.Test;
import org.keycloak.testsuite.util.LDAPRule;


/**
 *
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class UserFederationLdapConnectionTest extends AbstractAdminTest {
    @ClassRule
    public static LDAPRule ldapRule = new LDAPRule();

    @Test
    public void testLdapConnections1() {
        // Unknown action
        Response response = realm.testLDAPConnection("unknown", "ldap://localhost:10389", "foo", "bar", "false", null);
        assertStatus(response, 400);
        // Bad host
        response = realm.testLDAPConnection(TEST_CONNECTION, "ldap://localhostt:10389", "foo", "bar", "false", null);
        assertStatus(response, 400);
        // Connection success
        response = realm.testLDAPConnection(TEST_CONNECTION, "ldap://localhost:10389", "foo", "bar", "false", null);
        assertStatus(response, 204);
        // Bad authentication
        response = realm.testLDAPConnection(TEST_AUTHENTICATION, "ldap://localhost:10389", "foo", "bar", "false", "10000");
        assertStatus(response, 400);
        // Authentication success
        response = realm.testLDAPConnection(TEST_AUTHENTICATION, "ldap://localhost:10389", "uid=admin,ou=system", "secret", "false", null);
        assertStatus(response, 204);
    }

    @Test
    public void testLdapConnectionsSsl() {
        Response response = realm.testLDAPConnection(TEST_CONNECTION, "ldaps://localhost:10636", "foo", "bar", "false", null);
        assertStatus(response, 204);
        response = realm.testLDAPConnection(TEST_CONNECTION, "ldaps://localhostt:10636", "foo", "bar", "false", null);
        assertStatus(response, 400);
        response = realm.testLDAPConnection(TEST_AUTHENTICATION, "ldaps://localhost:10636", "foo", "bar", "false", null);
        assertStatus(response, 400);
        response = realm.testLDAPConnection(TEST_AUTHENTICATION, "ldaps://localhost:10636", "uid=admin,ou=system", "secret", "true", null);
        assertStatus(response, 204);
        response = realm.testLDAPConnection(TEST_AUTHENTICATION, "ldaps://localhost:10636", "uid=admin,ou=system", "secret", "true", "10000");
        assertStatus(response, 204);
    }
}

