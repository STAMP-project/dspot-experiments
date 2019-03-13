/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates
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
package org.keycloak.testsuite.federation.ldap.noimport;


import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.federation.ldap.LDAPMultipleAttributesTest;
import org.keycloak.testsuite.federation.ldap.LDAPTestAsserts;
import org.keycloak.testsuite.federation.ldap.LDAPTestContext;


/**
 *
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LDAPMultipleAttributesNoImportTest extends LDAPMultipleAttributesTest {
    @Test
    public void testUserImport() {
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            session.userCache().clear();
            RealmModel appRealm = ctx.getRealm();
            // Test user NOT imported in local storage now. He is available just through "session.users()"
            UserModel user = session.users().getUserByUsername("jbrown", appRealm);
            Assert.assertNotNull(user);
            Assert.assertNull(session.userLocalStorage().getUserById(user.getId(), appRealm));
            LDAPTestAsserts.assertUserImported(session.users(), appRealm, "jbrown", "James", "Brown", "jbrown@keycloak.org", "88441");
        });
    }
}

