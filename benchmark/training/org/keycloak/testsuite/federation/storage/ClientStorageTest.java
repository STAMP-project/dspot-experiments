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
package org.keycloak.testsuite.federation.storage;


import Details.CODE_ID;
import Details.CONSENT;
import Details.GRANT_TYPE;
import Details.REDIRECT_URI;
import Details.REFRESH_TOKEN_ID;
import Details.REFRESH_TOKEN_TYPE;
import Details.TOKEN_ID;
import Details.USERNAME;
import OAuth2Constants.OFFLINE_ACCESS;
import OAuth2Constants.PASSWORD;
import OAuthClient.AccessTokenResponse;
import TokenUtil.TOKEN_TYPE_OFFLINE;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.models.ClientModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.cache.infinispan.ClientAdapter;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.RefreshToken;
import org.keycloak.storage.client.ClientStorageProviderModel;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AbstractTestRealmKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.pages.AppPage;
import org.keycloak.testsuite.pages.ErrorPage;
import org.keycloak.testsuite.pages.LoginPage;
import org.keycloak.testsuite.util.OAuthClient;


/**
 * Test that clients can override auth flows
 *
 * @author <a href="mailto:bburke@redhat.com">Bill Burke</a>
 */
public class ClientStorageTest extends AbstractTestRealmKeycloakTest {
    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Page
    protected AppPage appPage;

    @Page
    protected LoginPage loginPage;

    @Page
    protected ErrorPage errorPage;

    protected String providerId;

    protected String userId;

    @Test
    public void testClientStats() throws Exception {
        testDirectGrant("hardcoded-client");
        testDirectGrant("hardcoded-client");
        testBrowser("test-app");
        offlineTokenDirectGrantFlowNoRefresh();
        List<Map<String, String>> list = adminClient.realm("test").getClientSessionStats();
        boolean hardTested = false;
        boolean testAppTested = false;
        for (Map<String, String> entry : list) {
            if (entry.get("clientId").equals("hardcoded-client")) {
                Assert.assertEquals("3", entry.get("active"));
                Assert.assertEquals("1", entry.get("offline"));
                hardTested = true;
            } else
                if (entry.get("clientId").equals("test-app")) {
                    Assert.assertEquals("1", entry.get("active"));
                    Assert.assertEquals("0", entry.get("offline"));
                    testAppTested = true;
                }

        }
        Assert.assertTrue((hardTested && testAppTested));
    }

    @Test
    public void testBrowser() throws Exception {
        String clientId = "hardcoded-client";
        testBrowser(clientId);
        // Thread.sleep(10000000);
    }

    @Test
    public void testGrantAccessTokenNoOverride() throws Exception {
        testDirectGrant("hardcoded-client");
    }

    @Test
    public void testDailyEviction() {
        testIsCached();
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            ClientStorageProviderModel model = realm.getClientStorageProviders().get(0);
            Calendar eviction = Calendar.getInstance();
            eviction.add(Calendar.HOUR, 1);
            model.setCachePolicy(CacheableStorageProviderModel.CachePolicy.EVICT_DAILY);
            model.setEvictionHour(eviction.get(Calendar.HOUR_OF_DAY));
            model.setEvictionMinute(eviction.get(Calendar.MINUTE));
            realm.updateComponent(model);
        });
        testIsCached();
        setTimeOffset(((2 * 60) * 60));// 2 hours in future

        testNotCached();
        testIsCached();
        setDefaultCachePolicy();
        testIsCached();
    }

    @Test
    public void testWeeklyEviction() {
        testIsCached();
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            ClientStorageProviderModel model = realm.getClientStorageProviders().get(0);
            Calendar eviction = Calendar.getInstance();
            eviction.add(Calendar.HOUR, (4 * 24));
            model.setCachePolicy(CacheableStorageProviderModel.CachePolicy.EVICT_WEEKLY);
            model.setEvictionDay(eviction.get(Calendar.DAY_OF_WEEK));
            model.setEvictionHour(eviction.get(Calendar.HOUR_OF_DAY));
            model.setEvictionMinute(eviction.get(Calendar.MINUTE));
            realm.updateComponent(model);
        });
        testIsCached();
        setTimeOffset((((2 * 24) * 60) * 60));// 2 days in future

        testIsCached();
        setTimeOffset((((5 * 24) * 60) * 60));// 5 days in future

        testNotCached();
        testIsCached();
        setDefaultCachePolicy();
        testIsCached();
    }

    @Test
    public void testMaxLifespan() {
        testIsCached();
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            ClientStorageProviderModel model = realm.getClientStorageProviders().get(0);
            model.setCachePolicy(CacheableStorageProviderModel.CachePolicy.MAX_LIFESPAN);
            model.setMaxLifespan((((1 * 60) * 60) * 1000));
            realm.updateComponent(model);
        });
        testIsCached();
        setTimeOffset((((1 / 2) * 60) * 60));// 1/2 hour in future

        testIsCached();
        setTimeOffset(((2 * 60) * 60));// 2 hours in future

        testNotCached();
        testIsCached();
        setDefaultCachePolicy();
        testIsCached();
    }

    @Test
    public void testIsCached() {
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            ClientModel hardcoded = realm.getClientByClientId("hardcoded-client");
            Assert.assertNotNull(hardcoded);
            Assert.assertTrue((hardcoded instanceof ClientAdapter));
        });
    }

    @Test
    public void testNoCache() {
        testIsCached();
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            ClientStorageProviderModel model = realm.getClientStorageProviders().get(0);
            model.setCachePolicy(CacheableStorageProviderModel.CachePolicy.NO_CACHE);
            realm.updateComponent(model);
        });
        testNotCached();
        // test twice because updating component should evict
        testNotCached();
        // set it back
        setDefaultCachePolicy();
        testIsCached();
    }

    @Test
    public void offlineTokenDirectGrantFlow() throws Exception {
        oauth.scope(OFFLINE_ACCESS);
        oauth.clientId("hardcoded-client");
        OAuthClient.AccessTokenResponse tokenResponse = oauth.doGrantAccessTokenRequest("password", "test-user@localhost", "password");
        Assert.assertNull(tokenResponse.getErrorDescription());
        AccessToken token = oauth.verifyToken(tokenResponse.getAccessToken());
        String offlineTokenString = tokenResponse.getRefreshToken();
        RefreshToken offlineToken = oauth.parseRefreshToken(offlineTokenString);
        events.expectLogin().client("hardcoded-client").user(userId).session(token.getSessionState()).detail(GRANT_TYPE, PASSWORD).detail(TOKEN_ID, token.getId()).detail(REFRESH_TOKEN_ID, offlineToken.getId()).detail(REFRESH_TOKEN_TYPE, TOKEN_TYPE_OFFLINE).detail(USERNAME, "test-user@localhost").removeDetail(CODE_ID).removeDetail(REDIRECT_URI).removeDetail(CONSENT).assertEvent();
        Assert.assertEquals(TOKEN_TYPE_OFFLINE, offlineToken.getType());
        Assert.assertEquals(0, offlineToken.getExpiration());
        testRefreshWithOfflineToken(token, offlineToken, offlineTokenString, token.getSessionState(), userId);
        // Assert same token can be refreshed again
        testRefreshWithOfflineToken(token, offlineToken, offlineTokenString, token.getSessionState(), userId);
    }
}

