/**
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.oauth2.config.xml;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


/**
 * gh-808
 *
 * @author Joe Grandja
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "authorization-server-client-credentials-password-valid.xml")
@WebAppConfiguration
public class AuthorizationServerClientCredentialsPasswordValidXmlTests {
    private static final String CLIENT_ID = "acme";

    private static final String CLIENT_SECRET = "secret";

    private static final String USER_ID = "acme";

    private static final String USER_SECRET = "password";

    @Autowired
    WebApplicationContext context;

    @Autowired
    FilterChainProxy springSecurityFilterChain;

    MockMvc mockMvc;

    @Test
    public void clientAuthenticationFailsUsingUserCredentialsOnClientCredentialsGrantFlow() throws Exception {
        mockMvc.perform(post("/oauth/token").param("grant_type", "client_credentials").header("Authorization", AuthorizationServerClientCredentialsPasswordValidXmlTests.httpBasicCredentials(AuthorizationServerClientCredentialsPasswordValidXmlTests.USER_ID, AuthorizationServerClientCredentialsPasswordValidXmlTests.USER_SECRET))).andExpect(status().isUnauthorized());
    }

    @Test
    public void clientAuthenticationFailsUsingUserCredentialsOnResourceOwnerPasswordGrantFlow() throws Exception {
        mockMvc.perform(post("/oauth/token").param("grant_type", "password").param("client_id", AuthorizationServerClientCredentialsPasswordValidXmlTests.CLIENT_ID).param("username", AuthorizationServerClientCredentialsPasswordValidXmlTests.USER_ID).param("password", AuthorizationServerClientCredentialsPasswordValidXmlTests.USER_SECRET).header("Authorization", AuthorizationServerClientCredentialsPasswordValidXmlTests.httpBasicCredentials(AuthorizationServerClientCredentialsPasswordValidXmlTests.USER_ID, AuthorizationServerClientCredentialsPasswordValidXmlTests.USER_SECRET))).andExpect(status().isUnauthorized());
    }
}

