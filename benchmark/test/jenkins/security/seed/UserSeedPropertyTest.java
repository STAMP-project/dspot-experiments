/**
 * The MIT License
 *
 * Copyright (c) 2018 CloudBees, Inc.
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
package jenkins.security.seed;


import JenkinsRule.WebClient;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import hudson.model.User;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import test.security.realm.InMemorySecurityRealm;

import static UserSeedProperty.DISABLE_USER_SEED;
import static UserSeedProperty.HIDE_USER_SEED_SECTION;


public class UserSeedPropertyTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    @Issue("SECURITY-901")
    public void userCreation_implies_userSeedCreation() throws Exception {
        User alice = User.getById("alice", true);
        Assert.assertNotNull(alice);
        UserSeedProperty userSeed = alice.getProperty(UserSeedProperty.class);
        Assert.assertNotNull(userSeed);
        Assert.assertNotNull(userSeed.getSeed());
    }

    @Test
    @Issue("SECURITY-901")
    public void userSeedRenewal_changeTheSeed() throws Exception {
        j.jenkins.setCrumbIssuer(null);
        Set<String> seeds = new HashSet<>();
        User alice = User.getById("alice", true);
        UserSeedProperty userSeed = alice.getProperty(UserSeedProperty.class);
        seeds.add(userSeed.getSeed());
        int times = 10;
        for (int i = 1; i < times; i++) {
            requestRenewSeedForUser(alice);
            userSeed = alice.getProperty(UserSeedProperty.class);
            seeds.add(userSeed.getSeed());
        }
        Assert.assertThat(seeds.size(), Matchers.equalTo(times));
        Assert.assertFalse(seeds.contains(""));
        Assert.assertFalse(seeds.contains(null));
    }

    @Test
    @Issue("SECURITY-901")
    public void initialUserSeedIsAlwaysDifferent() throws Exception {
        Set<String> seeds = new HashSet<>();
        int times = 10;
        for (int i = 0; i < times; i++) {
            User alice = User.getById("alice", true);
            UserSeedProperty userSeed = alice.getProperty(UserSeedProperty.class);
            seeds.add(userSeed.getSeed());
            alice.delete();
        }
        Assert.assertThat(seeds.size(), Matchers.equalTo(times));
        Assert.assertFalse(seeds.contains(""));
        Assert.assertFalse(seeds.contains(null));
    }

    @Test
    @Issue("SECURITY-901")
    public void differentUserHaveDifferentInitialSeeds() throws Exception {
        Set<String> seeds = new HashSet<>();
        List<String> userIds = Arrays.asList("Alice", "Bob", "Charles", "Derek", "Edward");
        userIds.forEach(( userId) -> {
            User user = User.getById(userId, true);
            UserSeedProperty userSeed = user.getProperty(UserSeedProperty.class);
            seeds.add(userSeed.getSeed());
        });
        Assert.assertThat(seeds.size(), Matchers.equalTo(userIds.size()));
        Assert.assertFalse(seeds.contains(""));
        Assert.assertFalse(seeds.contains(null));
    }

    @Test
    @Issue("SECURITY-901")
    public void userCreatedInThirdPartyRealm_cannotReconnect_afterSessionInvalidation_andRealmDeletion() throws Exception {
        InMemorySecurityRealm realm = new InMemorySecurityRealm();
        j.jenkins.setSecurityRealm(realm);
        j.jenkins.setCrumbIssuer(null);
        String ALICE = "alice";
        realm.createAccount(ALICE);
        JenkinsRule.WebClient wc = j.createWebClient();
        wc.login(ALICE);
        User alice = User.getById(ALICE, false);
        Assert.assertNotNull(alice);
        UserSeedProperty userSeed = alice.getProperty(UserSeedProperty.class);
        Assert.assertNotNull(userSeed);
        assertUserConnected(wc, ALICE);
        realm.deleteAccount(ALICE);
        // even after the security realm deleted the user, they can still connect, until session invalidation
        assertUserConnected(wc, ALICE);
        requestRenewSeedForUser(alice);
        assertUserNotConnected(wc, ALICE);
        assertUserConnected(wc, "anonymous");
        try {
            wc.login(ALICE);
            Assert.fail("Alice does not exist any longer and so should not be able to login");
        } catch (FailingHttpStatusCodeException e) {
            Assert.assertEquals(401, e.getStatusCode());
        }
    }

    @Test
    @Issue("SECURITY-901")
    public void userAfterBeingDeletedInThirdPartyRealm_canStillUseTheirSession_withDisabledSeed() throws Exception {
        boolean currentStatus = DISABLE_USER_SEED;
        try {
            DISABLE_USER_SEED = true;
            InMemorySecurityRealm realm = new InMemorySecurityRealm();
            j.jenkins.setSecurityRealm(realm);
            j.jenkins.setCrumbIssuer(null);
            String ALICE = "alice";
            realm.createAccount(ALICE);
            JenkinsRule.WebClient wc = j.createWebClient();
            wc.login(ALICE);
            User alice = User.getById(ALICE, false);
            Assert.assertNotNull(alice);
            UserSeedProperty userSeed = alice.getProperty(UserSeedProperty.class);
            Assert.assertNotNull(userSeed);
            assertUserConnected(wc, ALICE);
            realm.deleteAccount(ALICE);
            // even after the security realm deleted the user, they can still connect, until session invalidation
            assertUserConnected(wc, ALICE);
            try {
                requestRenewSeedForUser(alice);
                Assert.fail("The feature should be disabled");
            } catch (FailingHttpStatusCodeException e) {
                // as the feature is disabled, we cannot renew the seed
            }
            // failed attempt to renew the seed does not have any effect
            assertUserConnected(wc, ALICE);
            UserSeedProperty userSeedProperty = alice.getProperty(UserSeedProperty.class);
            userSeedProperty.renewSeed();
            // failed attempt to renew the seed does not have any effect
            assertUserConnected(wc, ALICE);
            JenkinsRule.WebClient wc2 = j.createWebClient();
            try {
                wc2.login(ALICE);
                Assert.fail("Alice is not longer backed by security realm");
            } catch (FailingHttpStatusCodeException e) {
                Assert.assertEquals(401, e.getStatusCode());
            }
        } finally {
            DISABLE_USER_SEED = currentStatus;
        }
    }

    @Test
    @Issue("SECURITY-901")
    public void userCreatedInThirdPartyRealm_canReconnect_afterSessionInvalidation() throws Exception {
        InMemorySecurityRealm realm = new InMemorySecurityRealm();
        j.jenkins.setSecurityRealm(realm);
        j.jenkins.setCrumbIssuer(null);
        String ALICE = "alice";
        realm.createAccount(ALICE);
        JenkinsRule.WebClient wc = j.createWebClient();
        wc.login(ALICE);
        User alice = User.getById(ALICE, false);
        Assert.assertNotNull(alice);
        UserSeedProperty userSeed = alice.getProperty(UserSeedProperty.class);
        Assert.assertNotNull(userSeed);
        assertUserConnected(wc, ALICE);
        requestRenewSeedForUser(alice);
        assertUserNotConnected(wc, ALICE);
        assertUserConnected(wc, "anonymous");
        wc.login(ALICE);
        assertUserConnected(wc, ALICE);
    }

    @Test
    public void userSeedSection_isCorrectlyDisplayed() throws Exception {
        InMemorySecurityRealm realm = new InMemorySecurityRealm();
        j.jenkins.setSecurityRealm(realm);
        j.jenkins.setCrumbIssuer(null);
        String ALICE = "alice";
        realm.createAccount(ALICE);
        JenkinsRule.WebClient wc = j.createWebClient();
        wc.login(ALICE);
        User alice = User.getById(ALICE, false);
        Assert.assertNotNull(alice);
        HtmlPage htmlPage = wc.goTo(((alice.getUrl()) + "/configure"));
        htmlPage.getDocumentElement().getOneHtmlElementByAttribute("div", "class", "user-seed-panel");
    }

    @Test
    public void userSeedSection_isCorrectlyHidden_withSpecificSetting() throws Exception {
        boolean currentStatus = HIDE_USER_SEED_SECTION;
        try {
            HIDE_USER_SEED_SECTION = true;
            InMemorySecurityRealm realm = new InMemorySecurityRealm();
            j.jenkins.setSecurityRealm(realm);
            j.jenkins.setCrumbIssuer(null);
            String ALICE = "alice";
            realm.createAccount(ALICE);
            JenkinsRule.WebClient wc = j.createWebClient();
            wc.login(ALICE);
            User alice = User.getById(ALICE, false);
            Assert.assertNotNull(alice);
            HtmlPage htmlPage = wc.goTo(((alice.getUrl()) + "/configure"));
            try {
                htmlPage.getDocumentElement().getOneHtmlElementByAttribute("div", "class", "user-seed-panel");
                Assert.fail("Seed section should not be displayed");
            } catch (ElementNotFoundException e) {
            }
        } finally {
            HIDE_USER_SEED_SECTION = currentStatus;
        }
    }
}

