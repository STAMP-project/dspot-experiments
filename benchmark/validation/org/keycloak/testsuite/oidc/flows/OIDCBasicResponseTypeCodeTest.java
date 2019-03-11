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
package org.keycloak.testsuite.oidc.flows;


import OAuthClient.AuthorizationEndpointResponse;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.representations.IDToken;
import org.keycloak.representations.idm.EventRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.util.OAuthClient;


/**
 * Test for response_type=code
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class OIDCBasicResponseTypeCodeTest extends AbstractOIDCResponseTypeTest {
    @Test
    public void nonceNotUsed() {
        EventRepresentation loginEvent = loginUser(null);
        OAuthClient.AuthorizationEndpointResponse authzResponse = new OAuthClient.AuthorizationEndpointResponse(oauth, false);
        List<IDToken> idTokens = testAuthzResponseAndRetrieveIDTokens(authzResponse, loginEvent);
        for (IDToken idToken : idTokens) {
            org.keycloak.testsuite.Assert.assertNull(idToken.getNonce());
        }
    }

    @Test
    public void errorStandardFlowNotAllowed() throws Exception {
        super.validateErrorStandardFlowNotAllowed();
    }
}

