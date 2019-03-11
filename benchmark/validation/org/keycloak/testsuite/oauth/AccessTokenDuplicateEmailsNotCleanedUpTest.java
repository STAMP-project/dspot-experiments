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
package org.keycloak.testsuite.oauth;


import OAuth2Constants.CODE;
import OAuthClient.AccessTokenResponse;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.representations.AccessToken;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.util.OAuthClient;
import org.openqa.selenium.By;


/**
 *
 *
 * @author <a href="mailto:slawomir@dabek.name">Slawomir Dabek</a>
 */
public class AccessTokenDuplicateEmailsNotCleanedUpTest extends AbstractKeycloakTest {
    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Test
    public void loginWithNonDuplicateEmail() throws Exception {
        oauth.doLogin("non-duplicate-email-user@localhost", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, "password");
        Assert.assertEquals(200, response.getStatusCode());
        AccessToken token = oauth.verifyToken(response.getAccessToken());
        Assert.assertEquals(ApiUtil.findUserByUsername(adminClient.realm("test-duplicate-emails"), "non-duplicate-email-user").getId(), token.getSubject());
    }

    @Test
    public void loginWithDuplicateEmail() throws Exception {
        oauth.doLogin("duplicate-email-user@localhost", "password");
        Assert.assertEquals("Username already exists.", driver.findElement(By.xpath("//span[@class='kc-feedback-text']")).getText());
    }

    @Test
    public void loginWithUserHavingDuplicateEmailByUsername() throws Exception {
        oauth.doLogin("duplicate-email-user1", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, "password");
        Assert.assertEquals(200, response.getStatusCode());
        AccessToken token = oauth.verifyToken(response.getAccessToken());
        Assert.assertEquals(ApiUtil.findUserByUsername(adminClient.realm("test-duplicate-emails"), "duplicate-email-user1").getId(), token.getSubject());
        Assert.assertEquals("duplicate-email-user@localhost", token.getEmail());
    }
}

