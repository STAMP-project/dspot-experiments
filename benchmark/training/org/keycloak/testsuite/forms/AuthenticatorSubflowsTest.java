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
package org.keycloak.testsuite.forms;


import Details.USERNAME;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AbstractTestRealmKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.pages.AppPage;
import org.keycloak.testsuite.pages.ErrorPage;
import org.keycloak.testsuite.pages.LoginPage;
import org.openqa.selenium.By;


/**
 *
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
// @Test
// public void testSubflow31() {
// // Fill foo=bar2. I am see the pushButton
// String loginFormUrl = oauth.getLoginFormUrl();
// loginFormUrl = loginFormUrl + "&foo=bar2";
// log.info("loginFormUrl: " + loginFormUrl);
// 
// //Thread.sleep(10000000);
// 
// driver.navigate().to(loginFormUrl);
// Assert.assertEquals("PushTheButton", driver.getTitle());
// 
// // Confirm push button. I am authenticated as john-doh@localhost
// driver.findElement(By.name("submit1")).click();
// 
// appPage.assertCurrent();
// 
// events.expectLogin().detail(Details.USERNAME, "john-doh@localhost").assertEvent();
// }
// 
// 
// @Test
// public void testSubflow32() {
// // Fill foo=bar3. I am login automatically as "keycloak-user@localhost"
// 
// 
// }
public class AuthenticatorSubflowsTest extends AbstractTestRealmKeycloakTest {
    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Page
    protected AppPage appPage;

    @Page
    protected LoginPage loginPage;

    @Page
    protected ErrorPage errorPage;

    // @Test
    // public void testSleep() throws Exception {
    // log.info("Start sleeping");
    // Thread.sleep(1000000);
    // }
    @Test
    public void testSubflow1() throws Exception {
        // Add foo=bar1 . I am redirected to subflow1 - username+password form
        String loginFormUrl = oauth.getLoginFormUrl();
        loginFormUrl = loginFormUrl + "&foo=bar1";
        log.info(("loginFormUrl: " + loginFormUrl));
        // Thread.sleep(10000000);
        driver.navigate().to(loginFormUrl);
        loginPage.assertCurrent();
        // Fill username+password. I am successfully authenticated
        oauth.fillLoginForm("test-user@localhost", "password");
        appPage.assertCurrent();
        events.expectLogin().detail(USERNAME, "test-user@localhost").assertEvent();
    }

    @Test
    public void testSubflow2() throws Exception {
        // Don't add 'foo' parameter. I am redirected to subflow2 - push the button
        String loginFormUrl = oauth.getLoginFormUrl();
        log.info(("loginFormUrl: " + loginFormUrl));
        // Thread.sleep(10000000);
        driver.navigate().to(loginFormUrl);
        Assert.assertEquals("PushTheButton", driver.getTitle());
        // Push the button. I am redirected to username+password form
        driver.findElement(By.name("submit1")).click();
        loginPage.assertCurrent();
        // Fill username+password. I am successfully authenticated
        oauth.fillLoginForm("test-user@localhost", "password");
        appPage.assertCurrent();
        events.expectLogin().detail(USERNAME, "test-user@localhost").assertEvent();
    }
}

