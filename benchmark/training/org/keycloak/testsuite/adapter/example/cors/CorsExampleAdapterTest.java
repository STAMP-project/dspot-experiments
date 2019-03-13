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
package org.keycloak.testsuite.adapter.example.cors;


import junit.framework.TestCase;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.keycloak.testsuite.adapter.AbstractExampleAdapterTest;
import org.keycloak.testsuite.adapter.page.AngularCorsProductTestApp;
import org.keycloak.testsuite.arquillian.annotation.AppServerContainer;
import org.keycloak.testsuite.arquillian.containers.ContainerConstants;
import org.keycloak.testsuite.auth.page.account.Account;
import org.keycloak.testsuite.auth.page.login.OIDCLogin;
import org.keycloak.testsuite.util.JavascriptBrowser;
import org.keycloak.testsuite.util.URLAssert;
import org.keycloak.testsuite.util.WaitUtils;
import org.openqa.selenium.WebDriver;


/**
 * Tests CORS functionality in adapters.
 *
 * <p>
 *    Note, for SSL this test disables TLS certificate verification. Since CORS uses different hostnames
 *    (localhost-auth for example), the Subject Name won't match.
 * </p>
 *
 * @author fkiss
 */
@AppServerContainer(ContainerConstants.APP_SERVER_WILDFLY)
@AppServerContainer(ContainerConstants.APP_SERVER_WILDFLY_DEPRECATED)
@AppServerContainer(ContainerConstants.APP_SERVER_EAP)
@AppServerContainer(ContainerConstants.APP_SERVER_EAP6)
@AppServerContainer(ContainerConstants.APP_SERVER_EAP71)
public class CorsExampleAdapterTest extends AbstractExampleAdapterTest {
    public static final String CORS = "cors";

    public static final String AUTH_SERVER_HOST = "localhost-auth-127.0.0.1.nip.io";

    private static final String hostBackup;

    @ArquillianResource
    private Deployer deployer;

    // Javascript browser needed KEYCLOAK-4703
    @Drone
    @JavascriptBrowser
    protected WebDriver jsDriver;

    @Page
    @JavascriptBrowser
    protected OIDCLogin jsDriverTestRealmLoginPage;

    @Page
    @JavascriptBrowser
    private AngularCorsProductTestApp jsDriverAngularCorsProductPage;

    @Page
    @JavascriptBrowser
    private Account jsDriverTestRealmAccount;

    static {
        hostBackup = System.getProperty("auth.server.host", "localhost");
        System.setProperty("auth.server.host", CorsExampleAdapterTest.AUTH_SERVER_HOST);
    }

    @Test
    public void angularCorsProductTest() {
        jsDriverAngularCorsProductPage.navigateTo();
        jsDriverTestRealmLoginPage.form().login("bburke@redhat.com", "password");
        URLAssert.assertCurrentUrlStartsWith(jsDriverAngularCorsProductPage);
        jsDriverAngularCorsProductPage.reloadData();
        WaitUtils.waitUntilElement(jsDriverAngularCorsProductPage.getOutput()).text().contains("iphone");
        WaitUtils.waitUntilElement(jsDriverAngularCorsProductPage.getOutput()).text().contains("ipad");
        WaitUtils.waitUntilElement(jsDriverAngularCorsProductPage.getOutput()).text().contains("ipod");
        WaitUtils.waitUntilElement(jsDriverAngularCorsProductPage.getHeaders()).text().contains("\"x-custom1\":\"some-value\"");
        WaitUtils.waitUntilElement(jsDriverAngularCorsProductPage.getHeaders()).text().contains("\"www-authenticate\":\"some-value\"");
        jsDriverAngularCorsProductPage.loadRoles();
        WaitUtils.waitUntilElement(jsDriverAngularCorsProductPage.getOutput()).text().contains("user");
        jsDriverAngularCorsProductPage.addRole();
        WaitUtils.waitUntilElement(jsDriverAngularCorsProductPage.getOutput()).text().contains("stuff");
        jsDriverAngularCorsProductPage.deleteRole();
        WaitUtils.waitUntilElement(jsDriverAngularCorsProductPage.getOutput()).text().not().contains("stuff");
        jsDriverAngularCorsProductPage.loadAvailableSocialProviders();
        WaitUtils.waitUntilElement(jsDriverAngularCorsProductPage.getOutput()).text().contains("twitter");
        WaitUtils.waitUntilElement(jsDriverAngularCorsProductPage.getOutput()).text().contains("google");
        WaitUtils.waitUntilElement(jsDriverAngularCorsProductPage.getOutput()).text().contains("linkedin");
        WaitUtils.waitUntilElement(jsDriverAngularCorsProductPage.getOutput()).text().contains("facebook");
        WaitUtils.waitUntilElement(jsDriverAngularCorsProductPage.getOutput()).text().contains("stackoverflow");
        WaitUtils.waitUntilElement(jsDriverAngularCorsProductPage.getOutput()).text().contains("github");
        WaitUtils.waitUntilElement(jsDriverAngularCorsProductPage.getOutput()).text().contains("microsoft");
        jsDriverAngularCorsProductPage.loadPublicRealmInfo();
        WaitUtils.waitUntilElement(jsDriverAngularCorsProductPage.getOutput()).text().contains("Realm name: cors");
        String serverVersion = getAuthServerVersion();
        TestCase.assertNotNull(serverVersion);
        jsDriverAngularCorsProductPage.navigateTo();
        WaitUtils.waitForPageToLoad();
        jsDriverAngularCorsProductPage.loadVersion();
        WaitUtils.waitUntilElement(jsDriverAngularCorsProductPage.getOutput()).text().contains(("Keycloak version: " + serverVersion));
    }
}

