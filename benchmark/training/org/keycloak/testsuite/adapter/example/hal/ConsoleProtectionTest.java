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
package org.keycloak.testsuite.adapter.example.hal;


import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.testsuite.adapter.AbstractAdapterTest;
import org.keycloak.testsuite.arquillian.annotation.AppServerContainer;
import org.keycloak.testsuite.arquillian.containers.ContainerConstants;
import org.keycloak.testsuite.pages.AccountUpdateProfilePage;
import org.keycloak.testsuite.pages.AppServerWelcomePage;
import org.keycloak.testsuite.util.JavascriptBrowser;
import org.openqa.selenium.WebDriver;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
@AppServerContainer(ContainerConstants.APP_SERVER_EAP71)
public class ConsoleProtectionTest extends AbstractAdapterTest {
    // Javascript browser needed KEYCLOAK-4703
    @Drone
    @JavascriptBrowser
    protected WebDriver jsDriver;

    @Page
    @JavascriptBrowser
    protected AppServerWelcomePage appServerWelcomePage;

    @Page
    @JavascriptBrowser
    protected AccountUpdateProfilePage accountUpdateProfilePage;

    @Test
    public void testUserCanAccessAccountService() throws InterruptedException {
        testLogin();
        appServerWelcomePage.navigateToAccessControl();
        appServerWelcomePage.navigateManageProfile();
        Assert.assertTrue(accountUpdateProfilePage.isCurrent());
    }
}

