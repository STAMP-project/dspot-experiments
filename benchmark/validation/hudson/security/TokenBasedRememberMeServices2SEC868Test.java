/**
 * The MIT License
 *
 * Copyright (c) 2018, CloudBees, Inc.
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


import JenkinsRule.WebClient;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.util.Cookie;
import hudson.model.User;
import java.util.concurrent.TimeUnit;
import jenkins.security.seed.UserSeedProperty;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.For;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;

import static TokenBasedRememberMeServices2.SKIP_TOO_FAR_EXPIRATION_DATE_CHECK;


@For(TokenBasedRememberMeServices2.class)
public class TokenBasedRememberMeServices2SEC868Test {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    @Issue("SECURITY-868")
    @For(UserSeedProperty.class)
    public void rememberMeToken_invalid_afterUserSeedReset() throws Exception {
        j.jenkins.setDisableRememberMe(false);
        HudsonPrivateSecurityRealm realm = new HudsonPrivateSecurityRealm(false, false, null);
        j.jenkins.setSecurityRealm(realm);
        String username = "alice";
        User alice = realm.createAccount(username, username);
        JenkinsRule.WebClient wc = j.createWebClient();
        wc.login(username, username, true);
        CookieManager cm = wc.getCookieManager();
        cm.removeCookie(cm.getCookie("JSESSIONID"));
        assertUserConnected(wc, username);
        alice.getProperty(UserSeedProperty.class).renewSeed();
        cm.removeCookie(cm.getCookie("JSESSIONID"));
        assertUserNotConnected(wc, username);
    }

    @Test
    @Issue("SECURITY-868")
    @For(UserSeedProperty.class)
    public void rememberMeToken_stillValid_afterUserSeedReset_ifUserSeedDisabled() throws Exception {
        boolean currentStatus = UserSeedProperty.DISABLE_USER_SEED;
        try {
            UserSeedProperty.DISABLE_USER_SEED = true;
            j.jenkins.setDisableRememberMe(false);
            HudsonPrivateSecurityRealm realm = new HudsonPrivateSecurityRealm(false, false, null);
            j.jenkins.setSecurityRealm(realm);
            String username = "alice";
            User alice = realm.createAccount(username, username);
            JenkinsRule.WebClient wc = j.createWebClient();
            wc.login(username, username, true);
            CookieManager cm = wc.getCookieManager();
            cm.removeCookie(cm.getCookie("JSESSIONID"));
            assertUserConnected(wc, username);
            alice.getProperty(UserSeedProperty.class).renewSeed();
            cm.removeCookie(cm.getCookie("JSESSIONID"));
            // as userSeed disabled, no care about the renew
            assertUserConnected(wc, username);
        } finally {
            UserSeedProperty.DISABLE_USER_SEED = currentStatus;
        }
    }

    @Test
    @Issue("SECURITY-868")
    public void rememberMeToken_shouldNotAccept_expirationDurationLargerThanConfigured() throws Exception {
        j.jenkins.setDisableRememberMe(false);
        HudsonPrivateSecurityRealm realm = new HudsonPrivateSecurityRealm(false, false, null);
        TokenBasedRememberMeServices2 tokenService = ((TokenBasedRememberMeServices2) (realm.getSecurityComponents().rememberMe));
        j.jenkins.setSecurityRealm(realm);
        String username = "alice";
        User alice = realm.createAccount(username, username);
        {
            // a malicious cookie with expiration too far in the future should not work
            JenkinsRule.WebClient wc = j.createWebClient();
            // by default we have 14 days of validity,
            // here we increase artificially the duration of validity, that could be used to have permanent access
            long oneDay = TimeUnit.DAYS.toMillis(1);
            Cookie cookie = createRememberMeCookie(tokenService, oneDay, alice);
            wc.getCookieManager().addCookie(cookie);
            // the application should not use the cookie to connect
            assertUserNotConnected(wc, username);
        }
        {
            // a hand crafted cookie with regular expiration duration works
            JenkinsRule.WebClient wc = j.createWebClient();
            // by default we have 14 days of validity,
            // here we reduce a bit the expiration date to simulate an "old" cookie (regular usage)
            long minusFiveMinutes = TimeUnit.MINUTES.toMillis((-5));
            Cookie cookie = createRememberMeCookie(tokenService, minusFiveMinutes, alice);
            wc.getCookieManager().addCookie(cookie);
            // if we reactivate the remember me feature, it's ok
            assertUserConnected(wc, username);
        }
    }

    @Test
    @Issue("SECURITY-868")
    public void rememberMeToken_skipExpirationCheck() throws Exception {
        boolean previousConfig = SKIP_TOO_FAR_EXPIRATION_DATE_CHECK;
        try {
            SKIP_TOO_FAR_EXPIRATION_DATE_CHECK = true;
            j.jenkins.setDisableRememberMe(false);
            HudsonPrivateSecurityRealm realm = new HudsonPrivateSecurityRealm(false, false, null);
            TokenBasedRememberMeServices2 tokenService = ((TokenBasedRememberMeServices2) (realm.getSecurityComponents().rememberMe));
            j.jenkins.setSecurityRealm(realm);
            String username = "alice";
            User alice = realm.createAccount(username, username);
            {
                // a malicious cookie with expiration too far in the future should not work
                JenkinsRule.WebClient wc = j.createWebClient();
                // by default we have 14 days of validity,
                // here we increase artificially the duration of validity, that could be used to have permanent access
                long oneDay = TimeUnit.DAYS.toMillis(1);
                Cookie cookie = createRememberMeCookie(tokenService, oneDay, alice);
                wc.getCookieManager().addCookie(cookie);
                // the application should not use the cookie to connect
                assertUserConnected(wc, username);
            }
            {
                // a hand crafted cookie with regular expiration duration works
                JenkinsRule.WebClient wc = j.createWebClient();
                // by default we have 14 days of validity,
                // here we reduce a bit the expiration date to simulate an "old" cookie (regular usage)
                long minusFiveMinutes = TimeUnit.MINUTES.toMillis((-5));
                Cookie cookie = createRememberMeCookie(tokenService, minusFiveMinutes, alice);
                wc.getCookieManager().addCookie(cookie);
                // if we reactivate the remember me feature, it's ok
                assertUserConnected(wc, username);
            }
        } finally {
            SKIP_TOO_FAR_EXPIRATION_DATE_CHECK = previousConfig;
        }
    }
}

