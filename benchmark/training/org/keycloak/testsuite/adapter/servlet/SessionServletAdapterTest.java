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
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.protocol.oidc.OIDCLoginProtocolService;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testsuite.AbstractAuthTest;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.adapter.AbstractServletsAdapterTest;
import org.keycloak.testsuite.adapter.page.SessionPortal;
import org.keycloak.testsuite.arquillian.annotation.AppServerContainer;
import org.keycloak.testsuite.arquillian.containers.ContainerConstants;
import org.keycloak.testsuite.auth.page.account.Sessions;
import org.keycloak.testsuite.util.SecondBrowser;
import org.keycloak.testsuite.util.URLAssert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


/**
 *
 *
 * @author tkyjovsk
 */
@AppServerContainer(ContainerConstants.APP_SERVER_UNDERTOW)
@AppServerContainer(ContainerConstants.APP_SERVER_WILDFLY)
@AppServerContainer(ContainerConstants.APP_SERVER_WILDFLY_DEPRECATED)
@AppServerContainer(ContainerConstants.APP_SERVER_EAP)
@AppServerContainer(ContainerConstants.APP_SERVER_EAP6)
@AppServerContainer(ContainerConstants.APP_SERVER_EAP71)
@AppServerContainer(ContainerConstants.APP_SERVER_TOMCAT7)
@AppServerContainer(ContainerConstants.APP_SERVER_TOMCAT8)
@AppServerContainer(ContainerConstants.APP_SERVER_TOMCAT9)
public class SessionServletAdapterTest extends AbstractServletsAdapterTest {
    @Page
    private SessionPortal sessionPortalPage;

    @Page
    private Sessions testRealmSessions;

    @Drone
    @SecondBrowser
    protected WebDriver driver2;

    // KEYCLOAK-732
    @Test
    public void testSingleSessionInvalidated() {
        loginAndCheckSession(testRealmLoginPage);
        // cannot pass to loginAndCheckSession because loginPage is not working together with driver2, therefore copypasta
        driver2.navigate().to(sessionPortalPage.toString());
        URLAssert.assertCurrentUrlStartsWithLoginUrlOf(testRealmPage, driver2);
        driver2.findElement(By.id("username")).sendKeys("bburke@redhat.com");
        driver2.findElement(By.id("password")).sendKeys("password");
        driver2.findElement(By.id("password")).submit();
        URLAssert.assertCurrentUrlEquals(sessionPortalPage, driver2);
        String pageSource = driver2.getPageSource();
        Assert.assertThat(pageSource, CoreMatchers.containsString("Counter=1"));
        // Counter increased now
        driver2.navigate().to(sessionPortalPage.toString());
        pageSource = driver2.getPageSource();
        Assert.assertTrue(pageSource.contains("Counter=2"));
        // Logout in browser1
        String logoutUri = OIDCLoginProtocolService.logoutUrl(authServerPage.createUriBuilder()).queryParam(REDIRECT_URI, sessionPortalPage.toString()).build("demo").toString();
        driver.navigate().to(logoutUri);
        URLAssert.assertCurrentUrlStartsWithLoginUrlOf(testRealmPage);
        // Assert that I am logged out in browser1
        sessionPortalPage.navigateTo();
        URLAssert.assertCurrentUrlStartsWithLoginUrlOf(testRealmPage);
        // Assert that I am still logged in browser2 and same session is still preserved
        driver2.navigate().to(sessionPortalPage.toString());
        URLAssert.assertCurrentUrlEquals(sessionPortalPage, driver2);
        pageSource = driver2.getPageSource();
        Assert.assertThat(pageSource, CoreMatchers.containsString("Counter=3"));
        driver2.navigate().to(logoutUri);
        URLAssert.assertCurrentUrlStartsWithLoginUrlOf(testRealmPage, driver2);
    }

    // KEYCLOAK-741
    @Test
    public void testSessionInvalidatedAfterFailedRefresh() {
        RealmRepresentation testRealmRep = testRealmResource().toRepresentation();
        ClientResource sessionPortalRes = null;
        for (ClientRepresentation clientRep : testRealmResource().clients().findAll()) {
            if ("session-portal".equals(clientRep.getClientId())) {
                sessionPortalRes = testRealmResource().clients().get(clientRep.getId());
            }
        }
        Assert.assertNotNull(sessionPortalRes);
        sessionPortalRes.toRepresentation().setAdminUrl("");
        int origTokenLifespan = testRealmRep.getAccessCodeLifespan();
        testRealmRep.setAccessCodeLifespan(1);
        testRealmResource().update(testRealmRep);
        // Login
        loginAndCheckSession(testRealmLoginPage);
        // Logout
        String logoutUri = OIDCLoginProtocolService.logoutUrl(authServerPage.createUriBuilder()).queryParam(REDIRECT_URI, sessionPortalPage.toString()).build("demo").toString();
        driver.navigate().to(logoutUri);
        // Assert that http session was invalidated
        sessionPortalPage.navigateTo();
        URLAssert.assertCurrentUrlStartsWithLoginUrlOf(testRealmPage);
        testRealmLoginPage.form().login("bburke@redhat.com", "password");
        URLAssert.assertCurrentUrlEquals(sessionPortalPage);
        String pageSource = driver.getPageSource();
        Assert.assertTrue(pageSource.contains("Counter=1"));
        sessionPortalRes.toRepresentation().setAdminUrl(sessionPortalPage.toString());
        testRealmRep.setAccessCodeLifespan(origTokenLifespan);
        testRealmResource().update(testRealmRep);
    }

    // KEYCLOAK-942
    @Test
    public void testAdminApplicationLogout() {
        // login as bburke
        loginAndCheckSession(testRealmLoginPage);
        // logout mposolda with admin client
        UserRepresentation mposolda = testRealmResource().users().search("mposolda", null, null, null, null, null).get(0);
        testRealmResource().users().get(mposolda.getId()).logout();
        // bburke should be still logged with original httpSession in our browser window
        sessionPortalPage.navigateTo();
        URLAssert.assertCurrentUrlEquals(sessionPortalPage);
        String pageSource = driver.getPageSource();
        Assert.assertTrue(pageSource.contains("Counter=3"));
        String logoutUri = OIDCLoginProtocolService.logoutUrl(authServerPage.createUriBuilder()).queryParam(REDIRECT_URI, sessionPortalPage.toString()).build("demo").toString();
        driver.navigate().to(logoutUri);
    }

    // KEYCLOAK-1216
    @Test
    public void testAccountManagementSessionsLogout() {
        // login as bburke
        loginAndCheckSession(testRealmLoginPage);
        testRealmSessions.navigateTo();
        testRealmSessions.logoutAll();
        // Assert I need to login again (logout was propagated to the app)
        loginAndCheckSession(testRealmLoginPage);
    }
}

