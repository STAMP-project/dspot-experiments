/**
 * The MIT License
 *
 * Copyright (c) 2015, CloudBees, Inc. and others
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.security;


import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.xml.XmlPage;
import hudson.model.User;
import hudson.security.pages.SignupPage;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import jenkins.security.ApiTokenProperty;
import jenkins.security.SecurityListener;
import jenkins.security.apitoken.ApiTokenTestHelper;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.JenkinsRule.WebClient;
import org.jvnet.hudson.test.TestExtension;
import org.jvnet.hudson.test.WithoutJenkins;
import org.mindrot.jbcrypt.BCrypt;


public class HudsonPrivateSecurityRealmTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    private HudsonPrivateSecurityRealmTest.SpySecurityListenerImpl spySecurityListener;

    @Test
    @WithoutJenkins
    public void hashCompatibility() {
        String old = HudsonPrivateSecurityRealm.CLASSIC.encodePassword("hello world", null);
        Assert.assertTrue(HudsonPrivateSecurityRealm.PASSWORD_ENCODER.isPasswordValid(old, "hello world", null));
        String secure = HudsonPrivateSecurityRealm.PASSWORD_ENCODER.encodePassword("hello world", null);
        Assert.assertTrue(HudsonPrivateSecurityRealm.PASSWORD_ENCODER.isPasswordValid(old, "hello world", null));
        Assert.assertFalse(secure.equals(old));
    }

    @Issue("SECURITY-243")
    @Test
    public void fullNameCollisionPassword() throws Exception {
        HudsonPrivateSecurityRealm securityRealm = new HudsonPrivateSecurityRealm(false, false, null);
        j.jenkins.setSecurityRealm(securityRealm);
        User u1 = securityRealm.createAccount("user1", "password1");
        u1.setFullName("User One");
        u1.save();
        User u2 = securityRealm.createAccount("user2", "password2");
        u2.setFullName("User Two");
        u2.save();
        WebClient wc1 = j.createWebClient();
        wc1.login("user1", "password1");
        WebClient wc2 = j.createWebClient();
        wc2.login("user2", "password2");
        // Check both users can use their token
        XmlPage w1 = ((XmlPage) (wc1.goTo("whoAmI/api/xml", "application/xml")));
        Assert.assertThat(w1, hasXPath("//name", Matchers.is("user1")));
        XmlPage w2 = ((XmlPage) (wc2.goTo("whoAmI/api/xml", "application/xml")));
        Assert.assertThat(w2, hasXPath("//name", Matchers.is("user2")));
        u1.setFullName("user2");
        u1.save();
        // check the tokens still work
        wc1 = j.createWebClient();
        wc1.login("user1", "password1");
        wc2 = j.createWebClient();
        // throws FailingHttpStatusCodeException on login failure
        wc2.login("user2", "password2");
        // belt and braces incase the failed login no longer throws exceptions.
        w1 = ((XmlPage) (wc1.goTo("whoAmI/api/xml", "application/xml")));
        Assert.assertThat(w1, hasXPath("//name", Matchers.is("user1")));
        w2 = ((XmlPage) (wc2.goTo("whoAmI/api/xml", "application/xml")));
        Assert.assertThat(w2, hasXPath("//name", Matchers.is("user2")));
    }

    @Issue("SECURITY-243")
    @Test
    public void fullNameCollisionToken() throws Exception {
        ApiTokenTestHelper.enableLegacyBehavior();
        HudsonPrivateSecurityRealm securityRealm = new HudsonPrivateSecurityRealm(false, false, null);
        j.jenkins.setSecurityRealm(securityRealm);
        User u1 = securityRealm.createAccount("user1", "password1");
        u1.setFullName("User One");
        u1.save();
        String u1Token = u1.getProperty(ApiTokenProperty.class).getApiToken();
        User u2 = securityRealm.createAccount("user2", "password2");
        u2.setFullName("User Two");
        u2.save();
        String u2Token = u2.getProperty(ApiTokenProperty.class).getApiToken();
        WebClient wc1 = j.createWebClient();
        wc1.addRequestHeader("Authorization", HudsonPrivateSecurityRealmTest.basicHeader("user1", u1Token));
        // wc1.setCredentialsProvider(new FixedCredentialsProvider("user1", u1Token));
        WebClient wc2 = j.createWebClient();
        wc2.addRequestHeader("Authorization", HudsonPrivateSecurityRealmTest.basicHeader("user2", u2Token));
        // wc2.setCredentialsProvider(new FixedCredentialsProvider("user2", u1Token));
        // Check both users can use their token
        XmlPage w1 = ((XmlPage) (wc1.goTo("whoAmI/api/xml", "application/xml")));
        Assert.assertThat(w1, hasXPath("//name", Matchers.is("user1")));
        XmlPage w2 = ((XmlPage) (wc2.goTo("whoAmI/api/xml", "application/xml")));
        Assert.assertThat(w2, hasXPath("//name", Matchers.is("user2")));
        u1.setFullName("user2");
        u1.save();
        // check the tokens still work
        w1 = ((XmlPage) (wc1.goTo("whoAmI/api/xml", "application/xml")));
        Assert.assertThat(w1, hasXPath("//name", Matchers.is("user1")));
        w2 = ((XmlPage) (wc2.goTo("whoAmI/api/xml", "application/xml")));
        Assert.assertThat(w2, hasXPath("//name", Matchers.is("user2")));
    }

    @Test
    public void signup() throws Exception {
        HudsonPrivateSecurityRealm securityRealm = new HudsonPrivateSecurityRealm(true, false, null);
        j.jenkins.setSecurityRealm(securityRealm);
        JenkinsRule.WebClient wc = j.createWebClient();
        SignupPage signup = new SignupPage(wc.goTo("signup"));
        signup.enterUsername("alice");
        signup.enterPassword("alice");
        signup.enterFullName("Alice User");
        signup.enterEmail("alice@nowhere.com");
        HtmlPage success = signup.submit(j);
        Assert.assertThat(success.getElementById("main-panel").getTextContent(), Matchers.containsString("Success"));
        Assert.assertThat(success.getAnchorByHref("/jenkins/user/alice").getTextContent(), Matchers.containsString("Alice User"));
        Assert.assertEquals("Alice User", securityRealm.getUser("alice").getDisplayName());
    }

    @Issue("SECURITY-166")
    @Test
    public void anonymousCantSignup() throws Exception {
        HudsonPrivateSecurityRealm securityRealm = new HudsonPrivateSecurityRealm(true, false, null);
        j.jenkins.setSecurityRealm(securityRealm);
        JenkinsRule.WebClient wc = j.createWebClient();
        SignupPage signup = new SignupPage(wc.goTo("signup"));
        signup.enterUsername("anonymous");
        signup.enterFullName("Bob");
        signup.enterPassword("nothing");
        signup.enterEmail("noone@nowhere.com");
        signup = new SignupPage(signup.submit(j));
        signup.assertErrorContains("prohibited as a username");
        Assert.assertNull(User.get("anonymous", false, Collections.emptyMap()));
    }

    @Issue("SECURITY-166")
    @Test
    public void systemCantSignup() throws Exception {
        HudsonPrivateSecurityRealm securityRealm = new HudsonPrivateSecurityRealm(true, false, null);
        j.jenkins.setSecurityRealm(securityRealm);
        JenkinsRule.WebClient wc = j.createWebClient();
        SignupPage signup = new SignupPage(wc.goTo("signup"));
        signup.enterUsername("system");
        signup.enterFullName("Bob");
        signup.enterPassword("nothing");
        signup.enterEmail("noone@nowhere.com");
        signup = new SignupPage(signup.submit(j));
        signup.assertErrorContains("prohibited as a username");
        Assert.assertNull(User.get("system", false, Collections.emptyMap()));
    }

    /**
     * We don't allow prohibited fullnames since this may encumber auditing.
     */
    @Issue("SECURITY-166")
    @Test
    public void fullNameOfUnknownCantSignup() throws Exception {
        HudsonPrivateSecurityRealm securityRealm = new HudsonPrivateSecurityRealm(true, false, null);
        j.jenkins.setSecurityRealm(securityRealm);
        JenkinsRule.WebClient wc = j.createWebClient();
        SignupPage signup = new SignupPage(wc.goTo("signup"));
        signup.enterUsername("unknown2");
        signup.enterPassword("unknown2");
        signup.enterFullName("unknown");
        signup.enterEmail("noone@nowhere.com");
        signup = new SignupPage(signup.submit(j));
        signup.assertErrorContains("prohibited as a full name");
        Assert.assertNull(User.get("unknown2", false, Collections.emptyMap()));
    }

    @Issue("JENKINS-48383")
    @Test
    public void selfRegistrationTriggerLoggedIn() throws Exception {
        HudsonPrivateSecurityRealm securityRealm = new HudsonPrivateSecurityRealm(true, false, null);
        j.jenkins.setSecurityRealm(securityRealm);
        j.jenkins.setCrumbIssuer(null);
        Assert.assertTrue(spySecurityListener.loggedInUsernames.isEmpty());
        createFirstAccount("admin");
        Assert.assertTrue(spySecurityListener.loggedInUsernames.get(0).equals("admin"));
        createAccountByAdmin("alice");
        // no new event in such case
        Assert.assertTrue(spySecurityListener.loggedInUsernames.isEmpty());
        selfRegistration("bob");
        Assert.assertTrue(spySecurityListener.loggedInUsernames.get(0).equals("bob"));
    }

    @Issue("JENKINS-55307")
    @Test
    public void selfRegistrationTriggerUserCreation() throws Exception {
        HudsonPrivateSecurityRealm securityRealm = new HudsonPrivateSecurityRealm(true, false, null);
        j.jenkins.setSecurityRealm(securityRealm);
        j.jenkins.setCrumbIssuer(null);
        spySecurityListener.createdUsers.clear();
        Assert.assertTrue(spySecurityListener.createdUsers.isEmpty());
        selfRegistration("bob");
        selfRegistration("charlie");
        Assert.assertTrue(spySecurityListener.createdUsers.get(0).equals("bob"));
        Assert.assertTrue(spySecurityListener.createdUsers.get(1).equals("charlie"));
    }

    @Issue("JENKINS-55307")
    @Test
    public void userCreationFromRealm() throws Exception {
        HudsonPrivateSecurityRealm securityRealm = new HudsonPrivateSecurityRealm(false, false, null);
        j.jenkins.setSecurityRealm(securityRealm);
        spySecurityListener.createdUsers.clear();
        Assert.assertTrue(spySecurityListener.createdUsers.isEmpty());
        User u1 = securityRealm.createAccount("alice", "alicePassword");
        u1.setFullName("Alice User");
        u1.save();
        User u2 = securityRealm.createAccount("debbie", "debbiePassword");
        u2.setFullName("Debbie User");
        u2.save();
        Assert.assertTrue(spySecurityListener.createdUsers.get(0).equals("alice"));
        Assert.assertTrue(spySecurityListener.createdUsers.get(1).equals("debbie"));
    }

    @Issue("JENKINS-55307")
    @Test
    public void userCreationWithHashedPasswords() throws Exception {
        HudsonPrivateSecurityRealm securityRealm = new HudsonPrivateSecurityRealm(false, false, null);
        j.jenkins.setSecurityRealm(securityRealm);
        spySecurityListener.createdUsers.clear();
        Assert.assertTrue(spySecurityListener.createdUsers.isEmpty());
        securityRealm.createAccountWithHashedPassword("charlie_hashed", ("#jbcrypt:" + (BCrypt.hashpw("charliePassword", BCrypt.gensalt()))));
        Assert.assertTrue(spySecurityListener.createdUsers.get(0).equals("charlie_hashed"));
    }

    @TestExtension
    public static class SpySecurityListenerImpl extends SecurityListener {
        private List<String> loggedInUsernames = new ArrayList<>();

        private List<String> createdUsers = new ArrayList<String>();

        @Override
        protected void loggedIn(@Nonnull
        String username) {
            loggedInUsernames.add(username);
        }

        @Override
        protected void userCreated(@Nonnull
        String username) {
            createdUsers.add(username);
        }
    }

    @Issue("SECURITY-786")
    @Test
    public void controlCharacterAreNoMoreValid() throws Exception {
        HudsonPrivateSecurityRealm securityRealm = new HudsonPrivateSecurityRealm(true, false, null);
        j.jenkins.setSecurityRealm(securityRealm);
        String password = "testPwd";
        String email = "test@test.com";
        int i = 0;
        // regular case = only accepting a-zA-Z0-9 + "-_"
        checkUserCanBeCreatedWith(securityRealm, ("test" + i), password, ("Test" + i), email);
        Assert.assertNotNull(User.getById(("test" + i), false));
        i++;
        checkUserCanBeCreatedWith(securityRealm, ("te-st_123" + i), password, ("Test" + i), email);
        Assert.assertNotNull(User.getById(("te-st_123" + i), false));
        i++;
        {
            // user id that contains invalid characters
            checkUserCannotBeCreatedWith(securityRealm, ("test " + i), password, ("Test" + i), email);
            i++;
            checkUserCannotBeCreatedWith(securityRealm, ("te@st" + i), password, ("Test" + i), email);
            i++;
            checkUserCannotBeCreatedWith(securityRealm, ("test.com" + i), password, ("Test" + i), email);
            i++;
            checkUserCannotBeCreatedWith(securityRealm, ("test,com" + i), password, ("Test" + i), email);
            i++;
            checkUserCannotBeCreatedWith(securityRealm, ("test,com" + i), password, ("Test" + i), email);
            i++;
            checkUserCannotBeCreatedWith(securityRealm, ("test?com" + i), password, ("Test" + i), email);
            i++;
            checkUserCannotBeCreatedWith(securityRealm, ("Starg?te" + i), password, ("Test" + i), email);
            i++;
            checkUserCannotBeCreatedWith(securityRealm, ("te\u0000st" + i), password, ("Test" + i), email);
            i++;
        }
    }

    @Issue("SECURITY-786")
    @Test
    public void controlCharacterAreNoMoreValid_CustomRegex() throws Exception {
        HudsonPrivateSecurityRealm securityRealm = new HudsonPrivateSecurityRealm(true, false, null);
        j.jenkins.setSecurityRealm(securityRealm);
        String currentRegex = "^[A-Z]+[0-9]*$";
        Field field = HudsonPrivateSecurityRealm.class.getDeclaredField("ID_REGEX");
        field.setAccessible(true);
        field.set(null, currentRegex);
        String password = "testPwd";
        String email = "test@test.com";
        int i = 0;
        // regular case = only accepting a-zA-Z0-9 + "-_"
        checkUserCanBeCreatedWith(securityRealm, ("TEST" + i), password, ("Test" + i), email);
        Assert.assertNotNull(User.getById(("TEST" + i), false));
        i++;
        checkUserCanBeCreatedWith(securityRealm, ("TEST123" + i), password, ("Test" + i), email);
        Assert.assertNotNull(User.getById(("TEST123" + i), false));
        i++;
        {
            // user id that do not follow custom regex
            checkUserCannotBeCreatedWith_custom(securityRealm, ("test " + i), password, ("Test" + i), email, currentRegex);
            i++;
            checkUserCannotBeCreatedWith_custom(securityRealm, ("@" + i), password, ("Test" + i), email, currentRegex);
            i++;
            checkUserCannotBeCreatedWith_custom(securityRealm, ("T2A" + i), password, ("Test" + i), email, currentRegex);
            i++;
        }
        {
            // we can even change regex on the fly
            currentRegex = "^[0-9]*$";
            field.set(null, currentRegex);
            checkUserCanBeCreatedWith(securityRealm, ("125213" + i), password, ("Test" + i), email);
            Assert.assertNotNull(User.getById(("125213" + i), false));
            i++;
            checkUserCannotBeCreatedWith_custom(securityRealm, ("TEST12" + i), password, ("Test" + i), email, currentRegex);
            i++;
        }
    }

    @Test
    public void createAccountSupportsHashedPasswords() throws Exception {
        HudsonPrivateSecurityRealm securityRealm = new HudsonPrivateSecurityRealm(false, false, null);
        j.jenkins.setSecurityRealm(securityRealm);
        securityRealm.createAccountWithHashedPassword("user_hashed", ("#jbcrypt:" + (BCrypt.hashpw("password", BCrypt.gensalt()))));
        WebClient wc = j.createWebClient();
        wc.login("user_hashed", "password");
        XmlPage w2 = ((XmlPage) (wc.goTo("whoAmI/api/xml", "application/xml")));
        Assert.assertThat(w2, hasXPath("//name", Matchers.is("user_hashed")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createAccountWithHashedPasswordRequiresPrefix() throws Exception {
        HudsonPrivateSecurityRealm securityRealm = new HudsonPrivateSecurityRealm(false, false, null);
        j.jenkins.setSecurityRealm(securityRealm);
        securityRealm.createAccountWithHashedPassword("user_hashed", BCrypt.hashpw("password", BCrypt.gensalt()));
    }

    @Test
    public void hashedPasswordTest() {
        Assert.assertTrue("password is hashed", HudsonPrivateSecurityRealm.PASSWORD_ENCODER.isPasswordHashed(("#jbcrypt:" + (BCrypt.hashpw("password", BCrypt.gensalt())))));
        Assert.assertFalse("password is not hashed", HudsonPrivateSecurityRealm.PASSWORD_ENCODER.isPasswordHashed("password"));
        Assert.assertFalse("only valid hashed passwords allowed", HudsonPrivateSecurityRealm.PASSWORD_ENCODER.isPasswordHashed("#jbcrypt:$2a$blah"));
        Assert.assertFalse("only valid hashed passwords allowed", HudsonPrivateSecurityRealm.PASSWORD_ENCODER.isPasswordHashed("#jbcrypt:password"));
        // real examples
        // password = a
        Assert.assertTrue(HudsonPrivateSecurityRealm.PASSWORD_ENCODER.isPasswordHashed("#jbcrypt:$2a$06$m0CrhHm10qJ3lXRY.5zDGO3rS2KdeeWLuGmsfGlMfOxih58VYVfxe"));
        // password = a
        Assert.assertTrue(HudsonPrivateSecurityRealm.PASSWORD_ENCODER.isPasswordHashed("#jbcrypt:$2a$12$8NJH3LsPrANStV6XtBakCez0cKHXVxmvxIlcz785vxAIZrihHZpeS"));
        // password = password
        Assert.assertFalse("too big number of iterations", HudsonPrivateSecurityRealm.PASSWORD_ENCODER.isPasswordHashed("#jbcrypt:$2a208$aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));
        // until https://github.com/jeremyh/jBCrypt/pull/16 is merged, the lib released and the dep updated, only the version 2a is supported
        Assert.assertFalse("unsupported version", HudsonPrivateSecurityRealm.PASSWORD_ENCODER.isPasswordHashed("#jbcrypt:$2x$08$Ro0CUfOqk6cXEKf3dyaM7OhSCvnwM9s4wIX9JeLapehKK5YdLxKcm"));
        Assert.assertFalse("unsupported version", HudsonPrivateSecurityRealm.PASSWORD_ENCODER.isPasswordHashed("#jbcrypt:$2y$06$m0CrhHm10qJ3lXRY.5zDGO3rS2KdeeWLuGmsfGlMfOxih58VYVfxe"));
        Assert.assertFalse("invalid version", HudsonPrivateSecurityRealm.PASSWORD_ENCODER.isPasswordHashed("#jbcrypt:$2t$10$aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));
        Assert.assertFalse("invalid version", HudsonPrivateSecurityRealm.PASSWORD_ENCODER.isPasswordHashed("#jbcrypt:$3t$10$aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));
    }

    @Test
    public void ensureHashingVersion_2a_isSupported() {
        Assert.assertTrue("version 2a is supported", BCrypt.checkpw("a", "$2a$06$m0CrhHm10qJ3lXRY.5zDGO3rS2KdeeWLuGmsfGlMfOxih58VYVfxe"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureHashingVersion_2x_isNotSupported() {
        BCrypt.checkpw("abc", "$2x$08$Ro0CUfOqk6cXEKf3dyaM7OhSCvnwM9s4wIX9JeLapehKK5YdLxKcm");
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureHashingVersion_2y_isNotSupported() {
        BCrypt.checkpw("a", "$2y$08$cfcvVd2aQ8CMvoMpP2EBfeodLEkkFJ9umNEfPD18.hUF62qqlC/V.");
    }
}

