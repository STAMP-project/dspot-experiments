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
package org.keycloak.testsuite.forms;


import Details.CONSENT;
import RequestType.AUTH_RESPONSE;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.models.OTPPolicy;
import org.keycloak.models.utils.HmacOTP;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AbstractTestRealmKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.pages.AppPage;
import org.keycloak.testsuite.pages.LoginPage;
import org.keycloak.testsuite.pages.LoginTotpPage;
import org.keycloak.testsuite.util.GreenMailRule;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 * @author Stan Silvert ssilvert@redhat.com (C) 2016 Red Hat Inc.
 */
public class LoginHotpTest extends AbstractTestRealmKeycloakTest {
    public static OTPPolicy policy;

    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Rule
    public GreenMailRule greenMail = new GreenMailRule();

    @Page
    protected AppPage appPage;

    @Page
    protected LoginPage loginPage;

    @Page
    protected LoginTotpPage loginTotpPage;

    private HmacOTP otp;// = new HmacOTP(policy.getDigits(), policy.getAlgorithm(), policy.getLookAheadWindow());


    private int lifespan;

    private static int counter = 0;

    @Test
    public void loginWithHotpFailure() throws Exception {
        loginPage.open();
        loginPage.login("test-user@localhost", "password");
        Assert.assertTrue(loginTotpPage.isCurrent());
        loginTotpPage.login("123456");
        loginTotpPage.assertCurrent();
        Assert.assertEquals("Invalid authenticator code.", loginPage.getError());
        // loginPage.assertCurrent();  // Invalid authenticator code.
        // Assert.assertEquals("Invalid username or password.", loginPage.getError());
        events.expectLogin().error("invalid_user_credentials").session(((String) (null))).removeDetail(CONSENT).assertEvent();
    }

    @Test
    public void loginWithMissingHotp() throws Exception {
        loginPage.open();
        loginPage.login("test-user@localhost", "password");
        Assert.assertTrue(loginTotpPage.isCurrent());
        loginTotpPage.login(null);
        loginTotpPage.assertCurrent();
        Assert.assertEquals("Invalid authenticator code.", loginPage.getError());
        // loginPage.assertCurrent();  // Invalid authenticator code.
        // Assert.assertEquals("Invalid username or password.", loginPage.getError());
        events.expectLogin().error("invalid_user_credentials").session(((String) (null))).removeDetail(CONSENT).assertEvent();
    }

    @Test
    public void loginWithHotpSuccess() throws Exception {
        loginPage.open();
        loginPage.login("test-user@localhost", "password");
        Assert.assertTrue(("expecting totpPage got: " + (driver.getCurrentUrl())), loginTotpPage.isCurrent());
        loginTotpPage.login(otp.generateHOTP("hotpSecret", ((LoginHotpTest.counter)++)));
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        events.expectLogin().assertEvent();
    }

    @Test
    public void loginWithHotpInvalidPassword() throws Exception {
        loginPage.open();
        loginPage.login("test-user@localhost", "invalid");
        Assert.assertTrue(loginPage.isCurrent());
        Assert.assertEquals("Invalid username or password.", loginPage.getError());
        events.expectLogin().error("invalid_user_credentials").session(((String) (null))).removeDetail(CONSENT).assertEvent();
    }
}

