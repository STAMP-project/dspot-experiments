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
package org.keycloak.testsuite.adapter.servlet;


import OAuth2Constants.REDIRECT_URI;
import java.util.ArrayList;
import java.util.List;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.protocol.oidc.OIDCLoginProtocolService;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testsuite.AbstractAuthTest;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.adapter.AbstractServletsAdapterTest;
import org.keycloak.testsuite.adapter.page.ProductPortal;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.arquillian.annotation.AppServerContainer;
import org.keycloak.testsuite.arquillian.containers.ContainerConstants;
import org.keycloak.testsuite.pages.ConsentPage;
import org.keycloak.testsuite.util.URLAssert;


/**
 *
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@AppServerContainer(ContainerConstants.APP_SERVER_UNDERTOW)
@AppServerContainer(ContainerConstants.APP_SERVER_WILDFLY)
@AppServerContainer(ContainerConstants.APP_SERVER_WILDFLY_DEPRECATED)
@AppServerContainer(ContainerConstants.APP_SERVER_EAP)
@AppServerContainer(ContainerConstants.APP_SERVER_EAP6)
@AppServerContainer(ContainerConstants.APP_SERVER_EAP71)
public class UserStorageConsentTest extends AbstractServletsAdapterTest {
    @Page
    private ProductPortal productPortal;

    @Page
    protected ConsentPage consentPage;

    /**
     * KEYCLOAK-5273
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testLogin() throws Exception {
        testingClient.server().run(UserStorageConsentTest::setupConsent);
        UserRepresentation memuser = new UserRepresentation();
        memuser.setUsername("memuser");
        String uid = ApiUtil.createUserAndResetPasswordWithAdminClient(testRealmResource(), memuser, "password");
        System.out.println(("uid: " + uid));
        Assert.assertTrue(uid.startsWith("f:"));// make sure its federated

        RoleRepresentation roleRep = adminClient.realm("demo").roles().get("user").toRepresentation();
        List<RoleRepresentation> roleList = new ArrayList<>();
        roleList.add(roleRep);
        adminClient.realm("demo").users().get(uid).roles().realmLevel().add(roleList);
        productPortal.navigateTo();
        URLAssert.assertCurrentUrlStartsWithLoginUrlOf(testRealmPage);
        testRealmLoginPage.form().login("memuser", "password");
        Assert.assertTrue(consentPage.isCurrent());
        consentPage.confirm();
        URLAssert.assertCurrentUrlEquals(productPortal.toString());
        Assert.assertTrue(driver.getPageSource().contains("iPhone"));
        String logoutUri = OIDCLoginProtocolService.logoutUrl(authServerPage.createUriBuilder()).queryParam(REDIRECT_URI, productPortal.toString()).build("demo").toString();
        driver.navigate().to(logoutUri);
        URLAssert.assertCurrentUrlStartsWithLoginUrlOf(testRealmPage);
        productPortal.navigateTo();
        URLAssert.assertCurrentUrlStartsWithLoginUrlOf(testRealmPage);
        testRealmLoginPage.form().login("memuser", "password");
        URLAssert.assertCurrentUrlEquals(productPortal.toString());
        Assert.assertTrue(driver.getPageSource().contains("iPhone"));
        adminClient.realm("demo").users().delete(uid).close();
    }
}

