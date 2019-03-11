/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates
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
package org.keycloak.testsuite.adapter.undertow.servlet;


import OAuth2Constants.REDIRECT_URI;
import java.util.List;
import java.util.Map;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.keycloak.protocol.oidc.OIDCLoginProtocolService;
import org.keycloak.testsuite.AbstractAuthTest;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.adapter.AbstractServletsAdapterTest;
import org.keycloak.testsuite.adapter.page.CustomerPortal;
import org.keycloak.testsuite.adapter.page.ProductPortal;
import org.keycloak.testsuite.arquillian.annotation.AppServerContainer;
import org.keycloak.testsuite.util.URLAssert;


/**
 * Also tests relative URIs in the adapter and valid redirect uris.
 * Also tests adapters not configured with public key
 *
 * note: migrated from old testsuite
 *
 * @author <a href="mailto:bburke@redhat.com">Bill Burke</a>
 */
@AppServerContainer(AUTH_SERVER_CONTAINER_DEFAULT)
@Ignore("Need to resolve default relative scenario when running on non-undertow")
public class UndertowRelaviteUriAdapterTest extends AbstractServletsAdapterTest {
    @Page
    private CustomerPortal customerPortal;

    @Page
    private ProductPortal productPortal;

    @Test
    public void testLoginSSOAndLogout() {
        // test login to customer-portal which does a bearer request to customer-db
        customerPortal.navigateTo();
        URLAssert.assertCurrentUrlStartsWithLoginUrlOf(testRealmPage);
        testRealmLoginPage.form().login("bburke@redhat.com", "password");
        URLAssert.assertCurrentUrlEquals(customerPortal);
        String pageSource = driver.getPageSource();
        Assert.assertTrue(((pageSource.contains("Bill Burke")) && (pageSource.contains("Stian Thorgersen"))));
        // test SSO
        productPortal.navigateTo();
        URLAssert.assertCurrentUrlEquals(productPortal);
        pageSource = driver.getPageSource();
        Assert.assertTrue(((pageSource.contains("iPhone")) && (pageSource.contains("iPad"))));
        // View stats
        List<Map<String, String>> stats = adminClient.realm(DEMO).getClientSessionStats();
        Map<String, String> customerPortalStats = null;
        Map<String, String> productPortalStats = null;
        for (Map<String, String> s : stats) {
            switch (s.get("clientId")) {
                case "customer-portal" :
                    customerPortalStats = s;
                    break;
                case "product-portal" :
                    productPortalStats = s;
                    break;
            }
        }
        Assert.assertEquals(1, Integer.parseInt(customerPortalStats.get("active")));
        Assert.assertEquals(1, Integer.parseInt(productPortalStats.get("active")));
        // test logout
        String logoutUri = OIDCLoginProtocolService.logoutUrl(authServerPage.createUriBuilder()).queryParam(REDIRECT_URI, customerPortal.toString()).build("demo").toString();
        driver.navigate().to(logoutUri);
        URLAssert.assertCurrentUrlStartsWithLoginUrlOf(testRealmPage);
        productPortal.navigateTo();
        URLAssert.assertCurrentUrlStartsWithLoginUrlOf(testRealmPage);
        customerPortal.navigateTo();
        URLAssert.assertCurrentUrlStartsWithLoginUrlOf(testRealmPage);
    }

    @Test
    public void testServletRequestLogout() {
        customerPortal.navigateTo();
        URLAssert.assertCurrentUrlStartsWithLoginUrlOf(testRealmPage);
        testRealmLoginPage.form().login("bburke@redhat.com", "password");
        URLAssert.assertCurrentUrlEquals(customerPortal);
        Assert.assertTrue(driver.getPageSource().contains("Bill Burke"));
        productPortal.navigateTo();
        URLAssert.assertCurrentUrlEquals(productPortal);
        Assert.assertTrue(driver.getPageSource().contains("iPhone"));
        // test logout
        driver.navigate().to(customerPortal.logout().toASCIIString());
        Assert.assertTrue(driver.getPageSource().contains("servlet logout ok"));
        customerPortal.navigateTo();
        URLAssert.assertCurrentUrlStartsWithLoginUrlOf(testRealmPage);
        productPortal.navigateTo();
        URLAssert.assertCurrentUrlStartsWithLoginUrlOf(testRealmPage);
    }
}

