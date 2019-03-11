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
package org.keycloak.testsuite.oidc;


import javax.ws.rs.core.Response;
import org.junit.Test;
import org.keycloak.admin.client.resource.ClientScopeResource;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.representations.idm.EventRepresentation;
import org.keycloak.representations.idm.ProtocolMapperRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.util.ProtocolMapperUtil;


/**
 * Test for the 'aud' claim in tokens
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class AudienceTest extends AbstractOIDCScopeTest {
    private static String userId = KeycloakModelUtils.generateId();

    @Test
    public void testAudienceProtocolMapperWithClientAudience() throws Exception {
        // Add audience protocol mapper to the clientScope "audience-scope"
        ProtocolMapperRepresentation audienceMapper = ProtocolMapperUtil.createAudienceMapper("audience mapper", "service-client", null, true, false);
        ClientScopeResource clientScope = ApiUtil.findClientScopeByName(testRealm(), "audience-scope");
        Response resp = clientScope.getProtocolMappers().createMapper(audienceMapper);
        String mapperId = ApiUtil.getCreatedId(resp);
        resp.close();
        // Login and check audiences in the token (just accessToken contains it)
        oauth.scope("openid audience-scope");
        oauth.doLogin("john", "password");
        EventRepresentation loginEvent = events.expectLogin().user(AudienceTest.userId).assertEvent();
        AbstractOIDCScopeTest.Tokens tokens = sendTokenRequest(loginEvent, AudienceTest.userId, "openid profile email audience-scope", "test-app");
        assertAudiences(tokens.accessToken, "service-client");
        assertAudiences(tokens.idToken, "test-app");
        // Revert
        clientScope.getProtocolMappers().delete(mapperId);
    }

    @Test
    public void testAudienceProtocolMapperWithCustomAudience() throws Exception {
        // Add audience protocol mapper to the clientScope "audience-scope"
        ProtocolMapperRepresentation audienceMapper = ProtocolMapperUtil.createAudienceMapper("audience mapper 1", null, "http://host/service/ctx1", true, false);
        ClientScopeResource clientScope = ApiUtil.findClientScopeByName(testRealm(), "audience-scope");
        Response resp = clientScope.getProtocolMappers().createMapper(audienceMapper);
        String mapper1Id = ApiUtil.getCreatedId(resp);
        resp.close();
        audienceMapper = ProtocolMapperUtil.createAudienceMapper("audience mapper 2", null, "http://host/service/ctx2", true, true);
        resp = clientScope.getProtocolMappers().createMapper(audienceMapper);
        String mapper2Id = ApiUtil.getCreatedId(resp);
        resp.close();
        // Login and check audiences in the token
        oauth.scope("openid audience-scope");
        oauth.doLogin("john", "password");
        EventRepresentation loginEvent = events.expectLogin().user(AudienceTest.userId).assertEvent();
        AbstractOIDCScopeTest.Tokens tokens = sendTokenRequest(loginEvent, AudienceTest.userId, "openid profile email audience-scope", "test-app");
        assertAudiences(tokens.accessToken, "http://host/service/ctx1", "http://host/service/ctx2");
        assertAudiences(tokens.idToken, "test-app", "http://host/service/ctx2");
        // Revert
        clientScope.getProtocolMappers().delete(mapper1Id);
        clientScope.getProtocolMappers().delete(mapper2Id);
    }
}

