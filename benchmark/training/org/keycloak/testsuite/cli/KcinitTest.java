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
package org.keycloak.testsuite.cli;


import Profile.Feature.TOKEN_EXCHANGE;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.internet.MimeMessage;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.common.Profile;
import org.keycloak.models.AuthenticationFlowModel;
import org.keycloak.models.ClientModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.TimeBasedOTP;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AbstractTestRealmKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.pages.LoginPage;
import org.keycloak.testsuite.util.GreenMailRule;
import org.keycloak.testsuite.util.MailUtils;
import org.keycloak.utils.TotpUtils;
import org.openqa.selenium.By;


/**
 * Test that clients can override auth flows
 *
 * @author <a href="mailto:bburke@redhat.com">Bill Burke</a>
 */
public class KcinitTest extends AbstractTestRealmKeycloakTest {
    public static final String KCINIT_CLIENT = "kcinit";

    public static final String APP = "app";

    public static final String UNAUTHORIZED_APP = "unauthorized_app";

    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Page
    protected LoginPage loginPage;

    @Test
    public void testBrowserContinueAuthenticator() throws Exception {
        // test that we can continue in the middle of a console login that doesn't support console display mode
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            ClientModel kcinit = realm.getClientByClientId(KCINIT_CLIENT);
            AuthenticationFlowModel flow = realm.getFlowByAlias("no-console-flow");
            kcinit.setAuthenticationFlowBindingOverride(AuthenticationFlowBindings.BROWSER_BINDING, flow.getId());
        });
        // Thread.sleep(100000000);
        try {
            testInstall();
            KcinitExec exe = // --fake-browser is a hidden command so that this test can execute
            KcinitExec.newBuilder().argsLine("login -f --fake-browser").executeAsync();
            exe.waitForStderr("Open browser and continue login? [y/n]");
            exe.sendLine("y");
            exe.waitForStdout("http");
            // the --fake-browser skips launching a browser and outputs url to stdout
            String redirect = exe.stdoutString().trim();
            // System.out.println("********************************");
            // System.out.println("Redirect: " + redirect);
            // redirect.replace("Browser required to complete login", "");
            driver.navigate().to(redirect.trim());
            Assert.assertEquals("PushTheButton", driver.getTitle());
            // Push the button. I am redirected to username+password form
            driver.findElement(By.name("submit1")).click();
            // System.out.println("-----");
            // System.out.println(driver.getPageSource());
            // System.out.println(driver.getTitle());
            loginPage.assertCurrent();
            // Fill username+password. I am successfully authenticated
            try {
                oauth.fillLoginForm("wburke", "password");
            } catch (Throwable e) {
                e.printStackTrace();
            }
            String current = driver.getCurrentUrl();
            exe.waitForStderr("Login successful");
            exe.waitCompletion();
            Assert.assertEquals(0, exe.exitCode());
            Assert.assertTrue(driver.getPageSource().contains("Login Successful"));
        } finally {
            testingClient.server().run(( session) -> {
                RealmModel realm = session.realms().getRealmByName("test");
                ClientModel kcinit = realm.getClientByClientId(KCINIT_CLIENT);
                kcinit.removeAuthenticationFlowBindingOverride(AuthenticationFlowBindings.BROWSER_BINDING);
            });
        }
    }

    @Test
    public void testBrowserContinueRequiredAction() throws Exception {
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            UserModel user = session.users().getUserByUsername("wburke", realm);
            user.addRequiredAction("dummy");
        });
        testInstall();
        // login
        // System.out.println("login....");
        KcinitExec exe = KcinitExec.newBuilder().argsLine("login -f --fake-browser").executeAsync();
        // System.out.println(exe.stderrString());
        exe.waitForStderr("Username:");
        exe.sendLine("wburke");
        // System.out.println(exe.stderrString());
        exe.waitForStderr("Password:");
        exe.sendLine("password");
        exe.waitForStderr("Open browser and continue login? [y/n]");
        exe.sendLine("y");
        exe.waitForStdout("http");
        // the --fake-browser skips launching a browser and outputs url to stdout
        String redirect = exe.stdoutString().trim();
        driver.navigate().to(redirect.trim());
        // System.out.println(exe.stderrString());
        exe.waitForStderr("Login successful");
        exe.waitCompletion();
        Assert.assertEquals(0, exe.exitCode());
        Assert.assertTrue(driver.getPageSource().contains("Login Successful"));
    }

    @Test
    public void testBadCommand() throws Exception {
        KcinitExec exe = KcinitExec.execute("covfefe");
        Assert.assertEquals(1, exe.exitCode());
        Assert.assertEquals("stderr first line", "Error: unknown command \"covfefe\" for \"kcinit\"", exe.stderrLines().get(0));
    }

    @Test
    public void testOffline() throws Exception {
        testInstall();
        // login
        // System.out.println("login....");
        KcinitExec exe = KcinitExec.newBuilder().argsLine("login --offline").executeAsync();
        // System.out.println(exe.stderrString());
        exe.waitForStderr("Username:");
        exe.sendLine("wburke");
        // System.out.println(exe.stderrString());
        exe.waitForStderr("Password:");
        exe.sendLine("password");
        // System.out.println(exe.stderrString());
        exe.waitForStderr("Offline tokens not allowed for the user or client");
        exe.waitCompletion();
        Assert.assertEquals(1, exe.exitCode());
    }

    @Test
    public void testBasic() throws Exception {
        testInstall();
        // login
        // System.out.println("login....");
        KcinitExec exe = KcinitExec.newBuilder().argsLine("login").executeAsync();
        // System.out.println(exe.stderrString());
        exe.waitForStderr("Username:");
        exe.sendLine("wburke");
        // System.out.println(exe.stderrString());
        exe.waitForStderr("Password:");
        exe.sendLine("password");
        // System.out.println(exe.stderrString());
        exe.waitForStderr("Login successful");
        exe.waitCompletion();
        Assert.assertEquals(0, exe.exitCode());
        Assert.assertEquals(0, exe.stdoutLines().size());
        if (Profile.isFeatureEnabled(TOKEN_EXCHANGE)) {
            exe = KcinitExec.execute("token");
            Assert.assertEquals(0, exe.exitCode());
            Assert.assertEquals(1, exe.stdoutLines().size());
            String token = exe.stdoutLines().get(0).trim();
            // System.out.println("token: " + token);
            exe = KcinitExec.execute("token app");
            Assert.assertEquals(0, exe.exitCode());
            Assert.assertEquals(1, exe.stdoutLines().size());
            String appToken = exe.stdoutLines().get(0).trim();
            Assert.assertFalse(appToken.equals(token));
            // System.out.println("token: " + token);
            exe = KcinitExec.execute("token badapp");
            Assert.assertEquals(1, exe.exitCode());
            Assert.assertEquals(0, exe.stdoutLines().size());
            Assert.assertEquals(1, exe.stderrLines().size());
            Assert.assertTrue(exe.stderrLines().get(0), exe.stderrLines().get(0).contains("failed to exchange token: invalid_client Audience not found"));
        }
        exe = KcinitExec.execute("logout");
        Assert.assertEquals(0, exe.exitCode());
    }

    @Test
    public void testTerms() throws Exception {
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            UserModel user = session.users().getUserByUsername("wburke", realm);
            user.addRequiredAction(TermsAndConditions.PROVIDER_ID);
        });
        testInstall();
        KcinitExec exe = KcinitExec.newBuilder().argsLine("login").executeAsync();
        exe.waitForStderr("Username:");
        exe.sendLine("wburke");
        exe.waitForStderr("Password:");
        exe.sendLine("password");
        exe.waitForStderr("Accept Terms? [y/n]:");
        exe.sendLine("y");
        exe.waitForStderr("Login successful");
        exe.waitCompletion();
        Assert.assertEquals(0, exe.exitCode());
        Assert.assertEquals(0, exe.stdoutLines().size());
    }

    @Test
    public void testUpdateProfile() throws Exception {
        // expects that updateProfile is a passthrough
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            UserModel user = session.users().getUserByUsername("wburke", realm);
            user.addRequiredAction(UserModel.RequiredAction.UPDATE_PROFILE);
        });
        try {
            testInstall();
            // Thread.sleep(100000000);
            KcinitExec exe = KcinitExec.newBuilder().argsLine("login").executeAsync();
            try {
                exe.waitForStderr("Username:");
                exe.sendLine("wburke");
                exe.waitForStderr("Password:");
                exe.sendLine("password");
                exe.waitForStderr("Login successful");
                exe.waitCompletion();
                Assert.assertEquals(0, exe.exitCode());
                Assert.assertEquals(0, exe.stdoutLines().size());
            } catch (Exception ex) {
                System.out.println(exe.stderrString());
                throw ex;
            }
        } finally {
            testingClient.server().run(( session) -> {
                RealmModel realm = session.realms().getRealmByName("test");
                UserModel user = session.users().getUserByUsername("wburke", realm);
                user.removeRequiredAction(UserModel.RequiredAction.UPDATE_PROFILE);
            });
        }
    }

    @Test
    public void testUpdatePassword() throws Exception {
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            UserModel user = session.users().getUserByUsername("wburke", realm);
            user.addRequiredAction(UserModel.RequiredAction.UPDATE_PASSWORD);
        });
        try {
            testInstall();
            KcinitExec exe = KcinitExec.newBuilder().argsLine("login").executeAsync();
            exe.waitForStderr("Username:");
            exe.sendLine("wburke");
            exe.waitForStderr("Password:");
            exe.sendLine("password");
            exe.waitForStderr("New Password:");
            exe.sendLine("pw");
            exe.waitForStderr("Confirm Password:");
            exe.sendLine("pw");
            exe.waitForStderr("Login successful");
            exe.waitCompletion();
            Assert.assertEquals(0, exe.exitCode());
            Assert.assertEquals(0, exe.stdoutLines().size());
            exe = KcinitExec.newBuilder().argsLine("login -f").executeAsync();
            exe.waitForStderr("Username:");
            exe.sendLine("wburke");
            exe.waitForStderr("Password:");
            exe.sendLine("pw");
            exe.waitForStderr("Login successful");
            exe.waitCompletion();
            Assert.assertEquals(0, exe.exitCode());
            Assert.assertEquals(0, exe.stdoutLines().size());
            exe = KcinitExec.execute("logout");
            Assert.assertEquals(0, exe.exitCode());
        } finally {
            testingClient.server().run(( session) -> {
                RealmModel realm = session.realms().getRealmByName("test");
                UserModel user = session.users().getUserByUsername("wburke", realm);
                session.userCredentialManager().updateCredential(realm, user, UserCredentialModel.password("password"));
            });
        }
    }

    protected TimeBasedOTP totp = new TimeBasedOTP();

    @Test
    public void testConfigureTOTP() throws Exception {
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            UserModel user = session.users().getUserByUsername("wburke", realm);
            user.addRequiredAction(UserModel.RequiredAction.CONFIGURE_TOTP);
        });
        try {
            testInstall();
            KcinitExec exe = KcinitExec.newBuilder().argsLine("login").executeAsync();
            exe.waitForStderr("Username:");
            exe.sendLine("wburke");
            exe.waitForStderr("Password:");
            exe.sendLine("password");
            exe.waitForStderr("One Time Password:");
            Pattern p = Pattern.compile("Open the application and enter the key\\s+(.+)\\s+Use the following configuration values");
            // Pattern p = Pattern.compile("Open the application and enter the key");
            String stderr = exe.stderrString();
            // System.out.println("***************");
            // System.out.println(stderr);
            // System.out.println("***************");
            Matcher m = p.matcher(stderr);
            Assert.assertTrue(m.find());
            String otpSecret = m.group(1).trim();
            // System.out.println("***************");
            // System.out.println(otpSecret);
            // System.out.println("***************");
            otpSecret = TotpUtils.decode(otpSecret);
            String code = totp.generateTOTP(otpSecret);
            // System.out.println("***************");
            // System.out.println("code: " + code);
            // System.out.println("***************");
            exe.sendLine(code);
            Thread.sleep(100);
            // stderr = exe.stderrString();
            // System.out.println("***************");
            // System.out.println(stderr);
            // System.out.println("***************");
            exe.waitForStderr("Login successful");
            exe.waitCompletion();
            Assert.assertEquals(0, exe.exitCode());
            Assert.assertEquals(0, exe.stdoutLines().size());
            exe = KcinitExec.execute("logout");
            Assert.assertEquals(0, exe.exitCode());
            exe = KcinitExec.newBuilder().argsLine("login").executeAsync();
            exe.waitForStderr("Username:");
            exe.sendLine("wburke");
            exe.waitForStderr("Password:");
            exe.sendLine("password");
            exe.waitForStderr("One Time Password:");
            exe.sendLine(totp.generateTOTP(otpSecret));
            exe.waitForStderr("Login successful");
            exe.waitCompletion();
            exe = KcinitExec.execute("logout");
            Assert.assertEquals(0, exe.exitCode());
        } finally {
            testingClient.server().run(( session) -> {
                RealmModel realm = session.realms().getRealmByName("test");
                UserModel user = session.users().getUserByUsername("wburke", realm);
                session.userCredentialManager().disableCredentialType(realm, user, CredentialModel.OTP);
            });
        }
    }

    @Rule
    public GreenMailRule greenMail = new GreenMailRule();

    @Test
    public void testVerifyEmail() throws Exception {
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            UserModel user = session.users().getUserByUsername("test-user@localhost", realm);
            user.addRequiredAction(UserModel.RequiredAction.VERIFY_EMAIL);
        });
        testInstall();
        KcinitExec exe = KcinitExec.newBuilder().argsLine("login").executeAsync();
        exe.waitForStderr("Username:");
        exe.sendLine("test-user@localhost");
        exe.waitForStderr("Password:");
        exe.sendLine("password");
        exe.waitForStderr("Email Code:");
        Assert.assertEquals(1, greenMail.getReceivedMessages().length);
        MimeMessage message = greenMail.getReceivedMessages()[0];
        String text = MailUtils.getBody(message).getText();
        Assert.assertTrue(text.contains("Please verify your email address by entering in the following code."));
        String code = text.substring("Please verify your email address by entering in the following code.".length()).trim();
        exe.sendLine(code);
        exe.waitForStderr("Login successful");
        exe.waitCompletion();
        Assert.assertEquals(0, exe.exitCode());
        Assert.assertEquals(0, exe.stdoutLines().size());
        exe = KcinitExec.execute("logout");
        Assert.assertEquals(0, exe.exitCode());
    }
}

