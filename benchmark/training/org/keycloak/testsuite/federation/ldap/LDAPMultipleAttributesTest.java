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


import OAuthClient.AccessTokenResponse;
import java.util.Collection;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.representations.IDToken;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.util.LDAPRule;
import org.keycloak.testsuite.util.LDAPTestConfiguration;
import org.keycloak.testsuite.util.OAuthClient;


/**
 *
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LDAPMultipleAttributesTest extends AbstractLDAPTest {
    // Skip this test on MSAD due to lack of supported user multivalued attributes
    @ClassRule
    public static LDAPRule ldapRule = new LDAPRule().assumeTrue((LDAPTestConfiguration ldapConfig) -> {
        String vendor = ldapConfig.getLDAPConfig().get(LDAPConstants.VENDOR);
        return !(LDAPConstants.VENDOR_ACTIVE_DIRECTORY.equals(vendor));
    });

    @Test
    public void testUserImport() {
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            session.userCache().clear();
            RealmModel appRealm = ctx.getRealm();
            // Test user imported in local storage now
            UserModel user = session.users().getUserByUsername("jbrown", appRealm);
            Assert.assertNotNull(session.userLocalStorage().getUserById(user.getId(), appRealm));
            LDAPTestAsserts.assertUserImported(session.userLocalStorage(), appRealm, "jbrown", "James", "Brown", "jbrown@keycloak.org", "88441");
        });
    }

    @Test
    public void testModel() {
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            session.userCache().clear();
            RealmModel appRealm = ctx.getRealm();
            UserModel user = session.users().getUserByUsername("bwilson", appRealm);
            Assert.assertEquals("bwilson@keycloak.org", user.getEmail());
            Assert.assertEquals("Bruce", user.getFirstName());
            // There are 2 lastnames in ldif
            Assert.assertTrue((("Wilson".equals(user.getLastName())) || ("Schneider".equals(user.getLastName()))));
            // Actually there are 2 postalCodes
            List<String> postalCodes = user.getAttribute("postal_code");
            assertPostalCodes(postalCodes, "88441", "77332");
            List<String> tmp = new LinkedList<>();
            tmp.addAll(postalCodes);
            postalCodes = tmp;
            postalCodes.remove("77332");
            user.setAttribute("postal_code", postalCodes);
        });
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            RealmModel appRealm = ctx.getRealm();
            UserModel user = session.users().getUserByUsername("bwilson", appRealm);
            List<String> postalCodes = user.getAttribute("postal_code");
            assertPostalCodes(postalCodes, "88441");
            List<String> tmp = new LinkedList<>();
            tmp.addAll(postalCodes);
            postalCodes = tmp;
            postalCodes.add("77332");
            user.setAttribute("postal_code", postalCodes);
        });
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            RealmModel appRealm = ctx.getRealm();
            UserModel user = session.users().getUserByUsername("bwilson", appRealm);
            assertPostalCodes(user.getAttribute("postal_code"), "88441", "77332");
        });
    }

    @Test
    public void ldapPortalEndToEndTest() {
        // Login as bwilson
        oauth.clientId("ldap-portal");
        oauth.redirectUri("/ldap-portal");
        loginPage.open();
        loginPage.login("bwilson", "Password1");
        String code = getCode();
        OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, "password");
        Assert.assertEquals(200, response.getStatusCode());
        IDToken idToken = oauth.verifyIDToken(response.getIdToken());
        Assert.assertEquals("Bruce Wilson", idToken.getName());
        Assert.assertEquals("Elm 5", idToken.getOtherClaims().get("street"));
        Collection postalCodes = ((Collection) (idToken.getOtherClaims().get("postal_code")));
        Assert.assertEquals(2, postalCodes.size());
        Assert.assertTrue(postalCodes.contains("88441"));
        Assert.assertTrue(postalCodes.contains("77332"));
        oauth.doLogout(response.getRefreshToken(), "password");
        // Login as jbrown
        loginPage.open();
        loginPage.login("jbrown", "Password1");
        code = getCode();
        response = oauth.doAccessTokenRequest(code, "password");
        org.keycloak.testsuite.Assert.assertEquals(200, response.getStatusCode());
        idToken = oauth.verifyIDToken(response.getIdToken());
        Assert.assertEquals("James Brown", idToken.getName());
        Assert.assertNull(idToken.getOtherClaims().get("street"));
        postalCodes = ((Collection) (idToken.getOtherClaims().get("postal_code")));
        Assert.assertEquals(1, postalCodes.size());
        Assert.assertTrue(postalCodes.contains("88441"));
        Assert.assertFalse(postalCodes.contains("77332"));
        oauth.doLogout(response.getRefreshToken(), "password");
    }
}

