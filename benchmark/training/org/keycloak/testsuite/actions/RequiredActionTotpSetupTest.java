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
package org.keycloak.testsuite.actions;


import Details.CODE_ID;
import Details.USERNAME;
import EventType.REMOVE_TOTP;
import EventType.UPDATE_TOTP;
import HmacOTP.HMAC_SHA1;
import RequestType.AUTH_RESPONSE;
import UserCredentialModel.HOTP;
import UserCredentialModel.TOTP;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.models.utils.HmacOTP;
import org.keycloak.models.utils.TimeBasedOTP;
import org.keycloak.representations.idm.EventRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AbstractTestRealmKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.pages.AccountTotpPage;
import org.keycloak.testsuite.pages.AppPage;
import org.keycloak.testsuite.pages.LoginConfigTotpPage;
import org.keycloak.testsuite.pages.LoginPage;
import org.keycloak.testsuite.pages.LoginTotpPage;
import org.keycloak.testsuite.pages.RegisterPage;
import org.keycloak.testsuite.util.RealmBuilder;
import org.openqa.selenium.By;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class RequiredActionTotpSetupTest extends AbstractTestRealmKeycloakTest {
    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Page
    protected AppPage appPage;

    @Page
    protected LoginPage loginPage;

    @Page
    protected LoginTotpPage loginTotpPage;

    @Page
    protected LoginConfigTotpPage totpPage;

    @Page
    protected AccountTotpPage accountTotpPage;

    @Page
    protected RegisterPage registerPage;

    protected TimeBasedOTP totp = new TimeBasedOTP();

    @Test
    public void setupTotpRegister() {
        loginPage.open();
        loginPage.clickRegister();
        registerPage.register("firstName", "lastName", "email@mail.com", "setupTotp", "password", "password");
        String userId = events.expectRegister("setupTotp", "email@mail.com").assertEvent().getUserId();
        Assert.assertTrue(totpPage.isCurrent());
        totpPage.configure(totp.generateTOTP(totpPage.getTotpSecret()));
        String authSessionId = events.expectRequiredAction(UPDATE_TOTP).user(userId).detail(USERNAME, "setuptotp").assertEvent().getDetails().get(CODE_ID);
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        events.expectLogin().user(userId).session(authSessionId).detail(USERNAME, "setuptotp").assertEvent();
    }

    @Test
    public void setupTotpRegisterManual() {
        loginPage.open();
        loginPage.clickRegister();
        registerPage.register("firstName", "lastName", "checkQrCode@mail.com", "checkQrCode", "password", "password");
        String pageSource = driver.getPageSource();
        Assert.assertTrue(pageSource.contains("Install one of the following applications on your mobile"));
        Assert.assertTrue(pageSource.contains("FreeOTP"));
        Assert.assertTrue(pageSource.contains("Google Authenticator"));
        Assert.assertTrue(pageSource.contains("Open the application and scan the barcode"));
        Assert.assertFalse(pageSource.contains("Open the application and enter the key"));
        Assert.assertTrue(pageSource.contains("Unable to scan?"));
        Assert.assertFalse(pageSource.contains("Scan barcode?"));
        totpPage.clickManual();
        pageSource = driver.getPageSource();
        Assert.assertTrue(pageSource.contains("Install one of the following applications on your mobile"));
        Assert.assertTrue(pageSource.contains("FreeOTP"));
        Assert.assertTrue(pageSource.contains("Google Authenticator"));
        Assert.assertFalse(pageSource.contains("Open the application and scan the barcode"));
        Assert.assertTrue(pageSource.contains("Open the application and enter the key"));
        Assert.assertFalse(pageSource.contains("Unable to scan?"));
        Assert.assertTrue(pageSource.contains("Scan barcode?"));
        Assert.assertTrue(driver.findElement(By.id("kc-totp-secret-key")).getText().matches("[\\w]{4}( [\\w]{4}){7}"));
        Assert.assertEquals("Type: Time-based", driver.findElement(By.id("kc-totp-type")).getText());
        Assert.assertEquals("Algorithm: SHA1", driver.findElement(By.id("kc-totp-algorithm")).getText());
        Assert.assertEquals("Digits: 6", driver.findElement(By.id("kc-totp-digits")).getText());
        Assert.assertEquals("Interval: 30", driver.findElement(By.id("kc-totp-period")).getText());
        totpPage.clickBarcode();
        pageSource = driver.getPageSource();
        Assert.assertTrue(pageSource.contains("Install one of the following applications on your mobile"));
        Assert.assertTrue(pageSource.contains("FreeOTP"));
        Assert.assertTrue(pageSource.contains("Google Authenticator"));
        Assert.assertTrue(pageSource.contains("Open the application and scan the barcode"));
        Assert.assertFalse(pageSource.contains("Open the application and enter the key"));
        Assert.assertTrue(pageSource.contains("Unable to scan?"));
        Assert.assertFalse(pageSource.contains("Scan barcode?"));
    }

    // KEYCLOAK-7081
    @Test
    public void setupTotpRegisterManualModeSwitchesOnBadSubmit() {
        loginPage.open();
        loginPage.clickRegister();
        registerPage.register("firstName", "lastName", "setupTotpRegisterManualModeSwitchesOnBadSubmit@mail.com", "setupTotpRegisterManualModeSwitchesOnBadSubmit", "password", "password");
        String pageSource = driver.getPageSource();
        Assert.assertTrue(pageSource.contains("Unable to scan?"));
        Assert.assertFalse(pageSource.contains("Scan barcode?"));
        totpPage.clickManual();
        pageSource = driver.getPageSource();
        Assert.assertFalse(pageSource.contains("Unable to scan?"));
        Assert.assertTrue(pageSource.contains("Scan barcode?"));
        totpPage.submit();
        pageSource = driver.getPageSource();
        Assert.assertFalse(pageSource.contains("Unable to scan?"));
        Assert.assertTrue(pageSource.contains("Scan barcode?"));
        Assert.assertEquals("Please specify authenticator code.", totpPage.getError());
    }

    // KEYCLOAK-7081
    @Test
    public void setupTotpRegisterBarcodeModeSwitchesOnBadSubmit() {
        loginPage.open();
        loginPage.clickRegister();
        registerPage.register("firstName", "lastName", "setupTotpRegisterBarcodeModeSwitchesOnBadSubmit@mail.com", "setupTotpRegisterBarcodeModeSwitchesOnBadSubmit", "password", "password");
        String pageSource = driver.getPageSource();
        Assert.assertTrue(pageSource.contains("Unable to scan?"));
        Assert.assertFalse(pageSource.contains("Scan barcode?"));
        totpPage.submit();
        pageSource = driver.getPageSource();
        Assert.assertTrue(pageSource.contains("Unable to scan?"));
        Assert.assertFalse(pageSource.contains("Scan barcode?"));
        Assert.assertEquals("Please specify authenticator code.", totpPage.getError());
        totpPage.clickManual();
        pageSource = driver.getPageSource();
        Assert.assertFalse(pageSource.contains("Unable to scan?"));
        Assert.assertTrue(pageSource.contains("Scan barcode?"));
    }

    @Test
    public void setupTotpModifiedPolicy() {
        RealmResource realm = testRealm();
        RealmRepresentation rep = realm.toRepresentation();
        rep.setOtpPolicyDigits(8);
        rep.setOtpPolicyType("hotp");
        rep.setOtpPolicyAlgorithm("HmacSHA256");
        realm.update(rep);
        try {
            loginPage.open();
            loginPage.clickRegister();
            registerPage.register("firstName", "lastName", "setupTotpModifiedPolicy@mail.com", "setupTotpModifiedPolicy", "password", "password");
            String pageSource = driver.getPageSource();
            Assert.assertTrue(pageSource.contains("FreeOTP"));
            Assert.assertFalse(pageSource.contains("Google Authenticator"));
            totpPage.clickManual();
            Assert.assertEquals("Type: Counter-based", driver.findElement(By.id("kc-totp-type")).getText());
            Assert.assertEquals("Algorithm: SHA256", driver.findElement(By.id("kc-totp-algorithm")).getText());
            Assert.assertEquals("Digits: 8", driver.findElement(By.id("kc-totp-digits")).getText());
            Assert.assertEquals("Counter: 0", driver.findElement(By.id("kc-totp-counter")).getText());
        } finally {
            rep.setOtpPolicyDigits(6);
            rep.setOtpPolicyType("totp");
            rep.setOtpPolicyAlgorithm("HmacSHA1");
            realm.update(rep);
        }
    }

    @Test
    public void setupTotpExisting() {
        loginPage.open();
        loginPage.login("test-user@localhost", "password");
        totpPage.assertCurrent();
        String totpSecret = totpPage.getTotpSecret();
        totpPage.configure(totp.generateTOTP(totpSecret));
        String authSessionId = events.expectRequiredAction(UPDATE_TOTP).assertEvent().getDetails().get(CODE_ID);
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        EventRepresentation loginEvent = events.expectLogin().session(authSessionId).assertEvent();
        oauth.openLogout();
        events.expectLogout(authSessionId).assertEvent();
        loginPage.open();
        loginPage.login("test-user@localhost", "password");
        String src = driver.getPageSource();
        loginTotpPage.login(totp.generateTOTP(totpSecret));
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        events.expectLogin().assertEvent();
    }

    @Test
    public void setupTotpRegisteredAfterTotpRemoval() {
        // Register new user
        loginPage.open();
        loginPage.clickRegister();
        registerPage.register("firstName2", "lastName2", "email2@mail.com", "setupTotp2", "password2", "password2");
        String userId = events.expectRegister("setupTotp2", "email2@mail.com").assertEvent().getUserId();
        // Configure totp
        totpPage.assertCurrent();
        String totpCode = totpPage.getTotpSecret();
        totpPage.configure(totp.generateTOTP(totpCode));
        // After totp config, user should be on the app page
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        events.expectRequiredAction(UPDATE_TOTP).user(userId).detail(USERNAME, "setuptotp2").assertEvent();
        EventRepresentation loginEvent = events.expectLogin().user(userId).detail(USERNAME, "setuptotp2").assertEvent();
        // Logout
        oauth.openLogout();
        events.expectLogout(loginEvent.getSessionId()).user(userId).assertEvent();
        // Try to login after logout
        loginPage.open();
        loginPage.login("setupTotp2", "password2");
        // Totp is already configured, thus one-time password is needed, login page should be loaded
        String uri = driver.getCurrentUrl();
        String src = driver.getPageSource();
        Assert.assertTrue(loginPage.isCurrent());
        Assert.assertFalse(totpPage.isCurrent());
        // Login with one-time password
        loginTotpPage.login(totp.generateTOTP(totpCode));
        loginEvent = events.expectLogin().user(userId).detail(USERNAME, "setupTotp2").assertEvent();
        // Open account page
        accountTotpPage.open();
        accountTotpPage.assertCurrent();
        // Remove google authentificator
        accountTotpPage.removeTotp();
        events.expectAccount(REMOVE_TOTP).user(userId).assertEvent();
        // Logout
        oauth.openLogout();
        events.expectLogout(loginEvent.getSessionId()).user(userId).assertEvent();
        // Try to login
        loginPage.open();
        loginPage.login("setupTotp2", "password2");
        // Since the authentificator was removed, it has to be set up again
        totpPage.assertCurrent();
        totpPage.configure(totp.generateTOTP(totpPage.getTotpSecret()));
        String sessionId = events.expectRequiredAction(UPDATE_TOTP).user(userId).detail(USERNAME, "setupTotp2").assertEvent().getDetails().get(CODE_ID);
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        events.expectLogin().user(userId).session(sessionId).detail(USERNAME, "setupTotp2").assertEvent();
    }

    @Test
    public void setupOtpPolicyChangedTotp8Digits() {
        // set policy to 8 digits
        RealmRepresentation realmRep = adminClient.realm("test").toRepresentation();
        RealmBuilder.edit(realmRep).otpLookAheadWindow(1).otpDigits(8).otpPeriod(30).otpType(TOTP).otpAlgorithm(HMAC_SHA1).otpInitialCounter(0);
        adminClient.realm("test").update(realmRep);
        loginPage.open();
        loginPage.login("test-user@localhost", "password");
        totpPage.assertCurrent();
        String totpSecret = totpPage.getTotpSecret();
        TimeBasedOTP timeBased = new TimeBasedOTP(HmacOTP.HMAC_SHA1, 8, 30, 1);
        totpPage.configure(timeBased.generateTOTP(totpSecret));
        String sessionId = events.expectRequiredAction(UPDATE_TOTP).assertEvent().getDetails().get(CODE_ID);
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        EventRepresentation loginEvent = events.expectLogin().session(sessionId).assertEvent();
        oauth.openLogout();
        events.expectLogout(loginEvent.getSessionId()).assertEvent();
        loginPage.open();
        loginPage.login("test-user@localhost", "password");
        String src = driver.getPageSource();
        String token = timeBased.generateTOTP(totpSecret);
        Assert.assertEquals(8, token.length());
        loginTotpPage.login(token);
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        events.expectLogin().assertEvent();
        // Revert
        realmRep = adminClient.realm("test").toRepresentation();
        RealmBuilder.edit(realmRep).otpDigits(6);
        adminClient.realm("test").update(realmRep);
    }

    @Test
    public void setupOtpPolicyChangedHotp() {
        RealmRepresentation realmRep = adminClient.realm("test").toRepresentation();
        RealmBuilder.edit(realmRep).otpLookAheadWindow(0).otpDigits(6).otpPeriod(30).otpType(HOTP).otpAlgorithm(HMAC_SHA1).otpInitialCounter(0);
        adminClient.realm("test").update(realmRep);
        loginPage.open();
        loginPage.login("test-user@localhost", "password");
        totpPage.assertCurrent();
        String totpSecret = totpPage.getTotpSecret();
        HmacOTP otpgen = new HmacOTP(6, HmacOTP.HMAC_SHA1, 1);
        totpPage.configure(otpgen.generateHOTP(totpSecret, 0));
        String uri = driver.getCurrentUrl();
        String sessionId = events.expectRequiredAction(UPDATE_TOTP).assertEvent().getDetails().get(CODE_ID);
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        EventRepresentation loginEvent = events.expectLogin().session(sessionId).assertEvent();
        oauth.openLogout();
        events.expectLogout(loginEvent.getSessionId()).assertEvent();
        loginPage.open();
        loginPage.login("test-user@localhost", "password");
        String token = otpgen.generateHOTP(totpSecret, 1);
        loginTotpPage.login(token);
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        events.expectLogin().assertEvent();
        oauth.openLogout();
        events.expectLogout(null).session(AssertEvents.isUUID()).assertEvent();
        // test lookAheadWindow
        realmRep = adminClient.realm("test").toRepresentation();
        RealmBuilder.edit(realmRep).otpLookAheadWindow(5).otpDigits(6).otpPeriod(30).otpType(HOTP).otpAlgorithm(HMAC_SHA1).otpInitialCounter(0);
        adminClient.realm("test").update(realmRep);
        loginPage.open();
        loginPage.login("test-user@localhost", "password");
        token = otpgen.generateHOTP(totpSecret, 4);
        loginTotpPage.assertCurrent();
        loginTotpPage.login(token);
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        events.expectLogin().assertEvent();
        // Revert
        realmRep = adminClient.realm("test").toRepresentation();
        RealmBuilder.edit(realmRep).otpLookAheadWindow(1).otpDigits(6).otpPeriod(30).otpType(TOTP).otpAlgorithm(HMAC_SHA1).otpInitialCounter(0);
        adminClient.realm("test").update(realmRep);
    }
}

