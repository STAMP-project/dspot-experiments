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
package org.keycloak.testsuite.adapter.example.fuse;


import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import org.apache.sshd.client.SshClient;
import org.hamcrest.Matchers;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.adapter.AbstractExampleAdapterTest;
import org.keycloak.testsuite.adapter.page.Hawtio2Page;
import org.keycloak.testsuite.adapter.page.HawtioPage;
import org.keycloak.testsuite.arquillian.annotation.AppServerContainer;
import org.keycloak.testsuite.arquillian.containers.ContainerConstants;
import org.keycloak.testsuite.auth.page.login.OIDCLogin;
import org.keycloak.testsuite.util.ContainerAssume;
import org.keycloak.testsuite.util.DroneUtils;
import org.keycloak.testsuite.util.JavascriptBrowser;
import org.keycloak.testsuite.util.URLAssert;
import org.keycloak.testsuite.util.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


@AppServerContainer(ContainerConstants.APP_SERVER_FUSE63)
@AppServerContainer(ContainerConstants.APP_SERVER_FUSE7X)
public class FuseAdminAdapterTest extends AbstractExampleAdapterTest {
    @Drone
    @JavascriptBrowser
    protected WebDriver jsDriver;

    @Page
    @JavascriptBrowser
    private HawtioPage hawtioPage;

    @Page
    @JavascriptBrowser
    private Hawtio2Page hawtio2Page;

    @Page
    @JavascriptBrowser
    private OIDCLogin testRealmLoginPageFuse;

    private SshClient client;

    protected enum Result {

        OK,
        NOT_FOUND,
        NO_CREDENTIALS,
        NO_ROLES;}

    @Test
    public void hawtio1LoginTest() throws Exception {
        // Note that this does work only in Fuse 6 with Hawtio 1, Fuse 7 contains Hawtio 2
        ContainerAssume.assumeNotAppServerFuse7();
        hawtioPage.navigateTo();
        testRealmLoginPageFuse.form().login("user", "invalid-password");
        URLAssert.assertCurrentUrlDoesntStartWith(hawtioPage);
        testRealmLoginPageFuse.form().login("invalid-user", "password");
        URLAssert.assertCurrentUrlDoesntStartWith(hawtioPage);
        testRealmLoginPageFuse.form().login("root", "password");
        URLAssert.assertCurrentUrlStartsWith(((hawtioPage.toString()) + "/welcome"));
        hawtioPage.logout();
        URLAssert.assertCurrentUrlStartsWith(testRealmLoginPageFuse);
        hawtioPage.navigateTo();
        log.debug("logging in as mary");
        testRealmLoginPageFuse.form().login("mary", "password");
        log.debug("Previous WARN waitForPageToLoad time exceeded! is expected");
        Assert.assertThat(DroneUtils.getCurrentDriver().getPageSource(), Matchers.allOf(Matchers.containsString("Unauthorized User"), Matchers.not(Matchers.containsString("welcome"))));
    }

    @Test
    public void hawtio2LoginTest() throws Exception {
        // Note that this does work only in Fuse 7 with Hawtio 2, Fuse 6 contains Hawtio 1
        ContainerAssume.assumeNotAppServerFuse6();
        hawtio2Page.navigateTo();
        WaitUtils.waitForPageToLoad();
        testRealmLoginPageFuse.form().login("user", "invalid-password");
        URLAssert.assertCurrentUrlDoesntStartWith(hawtio2Page);
        testRealmLoginPageFuse.form().login("invalid-user", "password");
        URLAssert.assertCurrentUrlDoesntStartWith(hawtio2Page);
        testRealmLoginPageFuse.form().login("root", "password");
        URLAssert.assertCurrentUrlStartsWith(hawtio2Page.toString());
        WaitUtils.waitForPageToLoad();
        WaitUtils.waitUntilElement(By.xpath("//img[@alt='Red Hat Fuse Management Console']")).is().present();
        hawtio2Page.logout();
        WaitUtils.waitForPageToLoad();
        URLAssert.assertCurrentUrlStartsWith(testRealmLoginPageFuse);
        hawtio2Page.navigateTo();
        WaitUtils.waitForPageToLoad();
        log.debug("logging in as mary");
        testRealmLoginPageFuse.form().login("mary", "password");
        log.debug(("Current URL: " + (DroneUtils.getCurrentDriver().getCurrentUrl())));
        URLAssert.assertCurrentUrlStartsWith(hawtio2Page.toString());
        WaitUtils.waitForPageToLoad();
        WaitUtils.waitUntilElement(By.xpath("//img[@alt='Red Hat Fuse Management Console']")).is().present();
        Assert.assertThat(DroneUtils.getCurrentDriver().getPageSource(), Matchers.not(Matchers.containsString("Camel")));
    }

    @Test
    public void sshLoginTestFuse6() throws Exception {
        // Note that this does not work for Fuse 7 since the error codes have changed
        ContainerAssume.assumeNotAppServerFuse7();
        assertCommand("mary", "password", "shell:date", FuseAdminAdapterTest.Result.NO_CREDENTIALS);
        assertCommand("john", "password", "shell:info", FuseAdminAdapterTest.Result.NO_CREDENTIALS);
        assertCommand("john", "password", "shell:date", FuseAdminAdapterTest.Result.OK);
        assertCommand("root", "password", "shell:info", FuseAdminAdapterTest.Result.OK);
    }

    @Test
    public void sshLoginTestFuse7() throws Exception {
        // Note that this works for Fuse 7 and newer
        ContainerAssume.assumeNotAppServerFuse6();
        assertCommand("mary", "password", "shell:date", FuseAdminAdapterTest.Result.NOT_FOUND);
        assertCommand("john", "password", "shell:info", FuseAdminAdapterTest.Result.NOT_FOUND);
        assertCommand("john", "password", "shell:date", FuseAdminAdapterTest.Result.OK);
        assertRoles("root", "ssh", "jmxAdmin", "admin", "manager", "viewer", "Administrator", "Auditor", "Deployer", "Maintainer", "Operator", "SuperUser");
    }

    @Test
    public void jmxLoginTest() throws Exception {
        setJMXAuthentication("keycloak", "password");
        ObjectName mbean = new ObjectName("org.apache.karaf:type=config,name=root");
        // invalid credentials
        try {
            getJMXConnector("mary", "password1").getMBeanServerConnection();
            Assert.fail();
        } catch (SecurityException se) {
        }
        // no role
        MBeanServerConnection connection = getJMXConnector("mary", "password").getMBeanServerConnection();
        assertJmxInvoke(false, connection, mbean, "listProperties", new Object[]{ "" }, new String[]{ String.class.getName() });
        assertJmxInvoke(false, connection, mbean, "setProperty", new Object[]{ "", "x", "y" }, new String[]{ String.class.getName(), String.class.getName(), String.class.getName() });
        // read only role
        connection = getJMXConnector("john", "password").getMBeanServerConnection();
        assertJmxInvoke(true, connection, mbean, "listProperties", new Object[]{ "" }, new String[]{ String.class.getName() });
        assertJmxInvoke(false, connection, mbean, "setProperty", new Object[]{ "", "x", "y" }, new String[]{ String.class.getName(), String.class.getName(), String.class.getName() });
        // read write role
        connection = getJMXConnector("root", "password").getMBeanServerConnection();
        assertJmxInvoke(true, connection, mbean, "listProperties", new Object[]{ "" }, new String[]{ String.class.getName() });
        assertJmxInvoke(true, connection, mbean, "setProperty", new Object[]{ "", "x", "y" }, new String[]{ String.class.getName(), String.class.getName(), String.class.getName() });
        setJMXAuthentication("karaf", "admin");
    }
}

