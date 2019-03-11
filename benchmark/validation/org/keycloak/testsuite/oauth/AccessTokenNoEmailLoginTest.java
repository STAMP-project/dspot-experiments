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
import org.junit.Test;
import org.keycloak.representations.AccessToken;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.util.OAuthClient;
import org.openqa.selenium.By;


/**
 *
 *
 * @author <a href="mailto:slawomir@dabek.name">Slawomir Dabek</a>
 */
public class AccessTokenNoEmailLoginTest extends AbstractKeycloakTest {
    @Test
    public void loginFormUsernameLabel() throws Exception {
        oauth.openLoginForm();
        Assert.assertEquals("Username", driver.findElement(By.xpath("//label[@for='username']")).getText());
    }

    @Test
    public void loginWithUsername() throws Exception {
        oauth.doLogin("non-duplicate-email-user", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, "password");
        Assert.assertEquals(200, response.getStatusCode());
        AccessToken token = oauth.verifyToken(response.getAccessToken());
        Assert.assertEquals(ApiUtil.findUserByUsername(adminClient.realm("test"), "non-duplicate-email-user").getId(), token.getSubject());
        Assert.assertEquals("non-duplicate-email-user@localhost", token.getEmail());
    }

    @Test
    public void loginWithEmail() throws Exception {
        oauth.doLoginGrant("non-duplicate-email-user@localhost", "password");
        Assert.assertEquals("Invalid username or password.", driver.findElement(By.xpath("//span[@class='kc-feedback-text']")).getText());
    }
}

