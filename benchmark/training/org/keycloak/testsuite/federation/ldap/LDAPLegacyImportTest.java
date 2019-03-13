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
package org.keycloak.testsuite.federation.ldap;


import AppPage.RequestType.AUTH_RESPONSE;
import OAuth2Constants.CODE;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.util.LDAPRule;
import org.keycloak.testsuite.util.LDAPTestConfiguration;


/**
 * Tests that legacy UserFederationProvider json export is converted to ComponentModel
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LDAPLegacyImportTest extends AbstractLDAPTest {
    @ClassRule
    public static LDAPRule ldapRule = new LDAPRule().assumeTrue((LDAPTestConfiguration ldapConfig) -> {
        return ldapConfig.isStartEmbeddedLdapServer();
    });

    @Test
    public void loginClassic() {
        loginPage.open();
        loginPage.login("marykeycloak", "password-app");
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        Assert.assertNotNull(oauth.getCurrentQuery().get(CODE));
    }

    @Test
    public void loginLdap() {
        loginPage.open();
        loginPage.login("johnkeycloak", "Password1");
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        Assert.assertNotNull(oauth.getCurrentQuery().get(CODE));
        profilePage.open();
        Assert.assertEquals("John", profilePage.getFirstName());
        Assert.assertEquals("Doe", profilePage.getLastName());
        Assert.assertEquals("john@email.org", profilePage.getEmail());
    }
}

