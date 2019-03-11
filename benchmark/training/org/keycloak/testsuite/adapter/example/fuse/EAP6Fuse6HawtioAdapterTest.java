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
package org.keycloak.testsuite.adapter.example.fuse;


import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.adapter.AbstractExampleAdapterTest;
import org.keycloak.testsuite.adapter.page.HawtioPage;
import org.keycloak.testsuite.arquillian.annotation.AppServerContainer;
import org.keycloak.testsuite.arquillian.containers.ContainerConstants;
import org.keycloak.testsuite.arquillian.containers.SelfManagedAppContainerLifecycle;
import org.keycloak.testsuite.auth.page.login.OIDCLogin;
import org.keycloak.testsuite.util.DroneUtils;
import org.keycloak.testsuite.util.JavascriptBrowser;
import org.keycloak.testsuite.util.URLAssert;
import org.keycloak.testsuite.util.WaitUtils;
import org.openqa.selenium.WebDriver;


/**
 *
 *
 * @author mhajas
 */
@AppServerContainer(ContainerConstants.APP_SERVER_EAP6)
public class EAP6Fuse6HawtioAdapterTest extends AbstractExampleAdapterTest implements SelfManagedAppContainerLifecycle {
    @ArquillianResource
    private ContainerController controller;

    @Drone
    @JavascriptBrowser
    protected WebDriver jsDriver;

    @Page
    @JavascriptBrowser
    private HawtioPage hawtioPage;

    @Page
    @JavascriptBrowser
    private OIDCLogin testRealmLoginPageFuse;

    @Test
    public void hawtioLoginAndLogoutTest() {
        testRealmLoginPageFuse.setAuthRealm(DEMO);
        log.debug("Go to hawtioPage");
        hawtioPage.navigateTo();
        WaitUtils.waitForPageToLoad();
        log.debug("log in");
        testRealmLoginPageFuse.form().login("root", "password");
        WaitUtils.waitForPageToLoad();
        URLAssert.assertCurrentUrlStartsWith(((hawtioPage.toString()) + "/welcome"), DroneUtils.getCurrentDriver());
        hawtioPage.logout();
        WaitUtils.waitForPageToLoad();
        URLAssert.assertCurrentUrlStartsWith(testRealmLoginPageFuse);
        hawtioPage.navigateTo();
        WaitUtils.waitForPageToLoad();
        URLAssert.assertCurrentUrlStartsWith(testRealmLoginPageFuse);
    }
}

