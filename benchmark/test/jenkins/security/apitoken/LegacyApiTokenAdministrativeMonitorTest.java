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
package jenkins.security.apitoken;


import ApiTokenStore.TokenUuidAndPlainValue;
import JenkinsRule.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlElementUtil;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import hudson.model.AdministrativeMonitor;
import hudson.model.User;
import jenkins.security.ApiTokenProperty;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;


public class LegacyApiTokenAdministrativeMonitorTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    private enum SelectFilter {

        ALL(0),
        ONLY_FRESH(1),
        ONLY_RECENT(2);
        int index;

        SelectFilter(int index) {
            this.index = index;
        }
    }

    @Test
    public void isActive() throws Exception {
        ApiTokenPropertyConfiguration config = ApiTokenPropertyConfiguration.get();
        config.setCreationOfLegacyTokenEnabled(true);
        config.setTokenGenerationOnCreationEnabled(false);
        // user created without legacy token
        User user = User.getById("user", true);
        ApiTokenProperty apiTokenProperty = user.getProperty(ApiTokenProperty.class);
        Assert.assertFalse(apiTokenProperty.hasLegacyToken());
        LegacyApiTokenAdministrativeMonitor monitor = j.jenkins.getExtensionList(AdministrativeMonitor.class).get(LegacyApiTokenAdministrativeMonitor.class);
        Assert.assertFalse(monitor.isActivated());
        ApiTokenStore.TokenUuidAndPlainValue tokenInfo = apiTokenProperty.getTokenStore().generateNewToken("Not Legacy");
        // "new" token does not trigger the monitor
        Assert.assertFalse(monitor.isActivated());
        apiTokenProperty.getTokenStore().revokeToken(tokenInfo.tokenUuid);
        Assert.assertFalse(monitor.isActivated());
        apiTokenProperty.changeApiToken();
        Assert.assertTrue(monitor.isActivated());
    }

    @Test
    @Issue("JENKINS-52441")
    public void takeCareOfUserWithIdNull() throws Exception {
        ApiTokenPropertyConfiguration config = ApiTokenPropertyConfiguration.get();
        config.setCreationOfLegacyTokenEnabled(true);
        config.setTokenGenerationOnCreationEnabled(false);
        // user created without legacy token
        User user = User.getById("null", true);
        ApiTokenProperty apiTokenProperty = user.getProperty(ApiTokenProperty.class);
        Assert.assertFalse(apiTokenProperty.hasLegacyToken());
        LegacyApiTokenAdministrativeMonitor monitor = j.jenkins.getExtensionList(AdministrativeMonitor.class).get(LegacyApiTokenAdministrativeMonitor.class);
        Assert.assertFalse(monitor.isActivated());
        apiTokenProperty.changeApiToken();
        Assert.assertTrue(monitor.isActivated());
        {
            // revoke the legacy token
            JenkinsRule.WebClient wc = j.createWebClient();
            HtmlPage page = wc.goTo(((monitor.getUrl()) + "/manage"));
            {
                // select all (only one user normally)
                HtmlAnchor filterAll = getFilterByIndex(page, LegacyApiTokenAdministrativeMonitorTest.SelectFilter.ALL);
                HtmlElementUtil.click(filterAll);
            }
            // revoke them
            HtmlButton revokeSelected = getRevokeSelected(page);
            HtmlElementUtil.click(revokeSelected);
        }
        Assert.assertFalse(monitor.isActivated());
    }

    @Test
    public void listOfUserWithLegacyTokenIsCorrect() throws Exception {
        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
        ApiTokenPropertyConfiguration config = ApiTokenPropertyConfiguration.get();
        config.setCreationOfLegacyTokenEnabled(true);
        config.setTokenGenerationOnCreationEnabled(false);
        LegacyApiTokenAdministrativeMonitor monitor = j.jenkins.getExtensionList(AdministrativeMonitor.class).get(LegacyApiTokenAdministrativeMonitor.class);
        JenkinsRule.WebClient wc = j.createWebClient();
        int numToken = 0;
        int numFreshToken = 0;
        int numRecentToken = 0;
        {
            // no user
            checkUserWithLegacyTokenListIsEmpty(wc, monitor);
        }
        {
            // with user without any token
            User user = User.getById("user", true);
            ApiTokenProperty apiTokenProperty = user.getProperty(ApiTokenProperty.class);
            Assert.assertFalse(apiTokenProperty.hasLegacyToken());
            checkUserWithLegacyTokenListIsEmpty(wc, monitor);
        }
        {
            // with user with token but without legacy token
            User user = User.getById("user", true);
            ApiTokenProperty apiTokenProperty = user.getProperty(ApiTokenProperty.class);
            Assert.assertFalse(apiTokenProperty.hasLegacyToken());
            apiTokenProperty.getTokenStore().generateNewToken("Not legacy");
            checkUserWithLegacyTokenListIsEmpty(wc, monitor);
            checkUserWithLegacyTokenListHasSizeOf(wc, monitor, numToken, numFreshToken, numRecentToken);
        }
        {
            // one user with just legacy token
            createUserWithToken(true, false, false);
            numToken++;
            checkUserWithLegacyTokenListHasSizeOf(wc, monitor, numToken, numFreshToken, numRecentToken);
        }
        {
            // one user with a fresh token
            // fresh = created after the last use of the legacy token (or its creation)
            createUserWithToken(true, true, false);
            numToken++;
            numFreshToken++;
            checkUserWithLegacyTokenListHasSizeOf(wc, monitor, numToken, numFreshToken, numRecentToken);
        }
        {
            // one user with a recent token (that is not fresh)
            // recent = last use after the last use of the legacy token (or its creation)
            createUserWithToken(true, false, true);
            numToken++;
            numRecentToken++;
            checkUserWithLegacyTokenListHasSizeOf(wc, monitor, numToken, numFreshToken, numRecentToken);
        }
        {
            // one user with a fresh + recent token
            createUserWithToken(true, true, true);
            numToken++;
            numFreshToken++;
            numRecentToken++;
            checkUserWithLegacyTokenListHasSizeOf(wc, monitor, numToken, numFreshToken, numRecentToken);
        }
    }

    @Test
    public void monitorManagePageFilterAreWorking() throws Exception {
        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
        ApiTokenPropertyConfiguration config = ApiTokenPropertyConfiguration.get();
        config.setCreationOfLegacyTokenEnabled(true);
        config.setTokenGenerationOnCreationEnabled(false);
        // create 1 user with legacy, 2 with fresh, 3 with recent and 4 with fresh+recent
        prepareUsersForFilters();
        LegacyApiTokenAdministrativeMonitor monitor = j.jenkins.getExtensionList(AdministrativeMonitor.class).get(LegacyApiTokenAdministrativeMonitor.class);
        JenkinsRule.WebClient wc = j.createWebClient();
        HtmlPage page = wc.goTo(((monitor.getUrl()) + "/manage"));
        checkUserWithLegacyTokenListHasSizeOf(page, (((1 + 2) + 3) + 4), (2 + 4), (3 + 4));
        HtmlElement document = page.getDocumentElement();
        HtmlElement filterDiv = document.getOneHtmlElementByAttribute("div", "class", "selection-panel");
        DomNodeList<HtmlElement> filters = filterDiv.getElementsByTagName("a");
        Assert.assertEquals(3, filters.size());
        HtmlAnchor filterAll = ((HtmlAnchor) (filters.get(0)));
        HtmlAnchor filterOnlyFresh = ((HtmlAnchor) (filters.get(1)));
        HtmlAnchor filterOnlyRecent = ((HtmlAnchor) (filters.get(2)));
        {
            // test just the filterAll
            checkNumberOfSelectedTr(document, 0);
            HtmlElementUtil.click(filterAll);
            checkNumberOfSelectedTr(document, (((1 + 2) + 3) + 4));
            HtmlElementUtil.click(filterAll);
            checkNumberOfSelectedTr(document, 0);
        }
        {
            // test just the filterOnlyFresh
            HtmlElementUtil.click(filterOnlyFresh);
            checkNumberOfSelectedTr(document, (2 + 4));
            HtmlElementUtil.click(filterOnlyFresh);
            checkNumberOfSelectedTr(document, 0);
        }
        {
            // test just the filterOnlyRecent
            HtmlElementUtil.click(filterOnlyRecent);
            checkNumberOfSelectedTr(document, (3 + 4));
            HtmlElementUtil.click(filterOnlyRecent);
            checkNumberOfSelectedTr(document, 0);
        }
        {
            // test interaction
            HtmlElementUtil.click(filterOnlyFresh);
            checkNumberOfSelectedTr(document, (2 + 4));
            // the 4 (recent+fresh) are still selected
            HtmlElementUtil.click(filterOnlyRecent);
            checkNumberOfSelectedTr(document, (3 + 4));
            HtmlElementUtil.click(filterAll);
            checkNumberOfSelectedTr(document, (((1 + 2) + 3) + 4));
        }
    }

    @Test
    public void monitorManagePageCanRevokeToken() throws Exception {
        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
        ApiTokenPropertyConfiguration config = ApiTokenPropertyConfiguration.get();
        config.setCreationOfLegacyTokenEnabled(true);
        config.setTokenGenerationOnCreationEnabled(false);
        // create 1 user with legacy, 2 with fresh, 3 with recent and 4 with fresh+recent
        prepareUsersForFilters();
        LegacyApiTokenAdministrativeMonitor monitor = j.jenkins.getExtensionList(AdministrativeMonitor.class).get(LegacyApiTokenAdministrativeMonitor.class);
        Assert.assertTrue(monitor.isActivated());
        JenkinsRule.WebClient wc = j.createWebClient();
        HtmlPage page = wc.goTo(((monitor.getUrl()) + "/manage"));
        checkUserWithLegacyTokenListHasSizeOf(page, (((1 + 2) + 3) + 4), (2 + 4), (3 + 4));
        {
            // select 2
            HtmlAnchor filterOnlyFresh = getFilterByIndex(page, LegacyApiTokenAdministrativeMonitorTest.SelectFilter.ONLY_FRESH);
            HtmlElementUtil.click(filterOnlyFresh);
        }
        // revoke them
        HtmlButton revokeSelected = getRevokeSelected(page);
        HtmlElementUtil.click(revokeSelected);
        HtmlPage newPage = checkUserWithLegacyTokenListHasSizeOf(wc, monitor, (1 + 3), 0, 3);
        Assert.assertTrue(monitor.isActivated());
        {
            // select 1 + 3
            HtmlAnchor filterAll = getFilterByIndex(newPage, LegacyApiTokenAdministrativeMonitorTest.SelectFilter.ALL);
            HtmlElementUtil.click(filterAll);
        }
        // revoke them
        revokeSelected = getRevokeSelected(newPage);
        HtmlElementUtil.click(revokeSelected);
        checkUserWithLegacyTokenListHasSizeOf(wc, monitor, 0, 0, 0);
        Assert.assertFalse(monitor.isActivated());
    }

    private int nextId = 0;
}

