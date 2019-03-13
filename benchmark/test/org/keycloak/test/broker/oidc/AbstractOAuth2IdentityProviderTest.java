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
package org.keycloak.test.broker.oidc;


import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.broker.oidc.AbstractOAuth2IdentityProvider;
import org.keycloak.broker.oidc.OAuth2IdentityProviderConfig;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.provider.IdentityBrokerException;
import org.keycloak.models.IdentityProviderModel;


/**
 * Unit test for {@link org.keycloak.broker.oidc.AbstractOAuth2IdentityProvider}
 *
 * @author Vlastimil Elias (velias at redhat dot com)
 */
public class AbstractOAuth2IdentityProviderTest {
    @Test
    public void constructor_defaultScopeHandling() {
        AbstractOAuth2IdentityProviderTest.TestProvider tested = getTested();
        // default scope is set from the provider if not provided in the configuration
        Assert.assertEquals(tested.getDefaultScopes(), getConfig().getDefaultScope());
        // default scope is preserved if provided in the configuration
        IdentityProviderModel model = new IdentityProviderModel();
        OAuth2IdentityProviderConfig config = new OAuth2IdentityProviderConfig(model);
        config.setDefaultScope("myscope");
        tested = new AbstractOAuth2IdentityProviderTest.TestProvider(config);
        Assert.assertEquals("myscope", getConfig().getDefaultScope());
    }

    @Test
    public void getJsonProperty_asJsonNode() throws IOException {
        AbstractOAuth2IdentityProviderTest.TestProvider tested = getTested();
        JsonNode jsonNode = asJsonNode("{\"nullone\":null, \"emptyone\":\"\", \"blankone\": \" \", \"withvalue\" : \"my value\", \"withbooleanvalue\" : true, \"withnumbervalue\" : 10}");
        Assert.assertNull(getJsonProperty(jsonNode, "nonexisting"));
        Assert.assertNull(getJsonProperty(jsonNode, "nullone"));
        Assert.assertNull(getJsonProperty(jsonNode, "emptyone"));
        Assert.assertEquals(" ", getJsonProperty(jsonNode, "blankone"));
        Assert.assertEquals("my value", getJsonProperty(jsonNode, "withvalue"));
        Assert.assertEquals("true", getJsonProperty(jsonNode, "withbooleanvalue"));
        Assert.assertEquals("10", getJsonProperty(jsonNode, "withnumbervalue"));
    }

    @Test(expected = IdentityBrokerException.class)
    public void getFederatedIdentity_responseUrlLine_tokenNotFound() {
        AbstractOAuth2IdentityProviderTest.TestProvider tested = getTested();
        getFederatedIdentity("cosi=sss");
    }

    @Test(expected = IdentityBrokerException.class)
    public void getFederatedIdentity_responseJSON_tokenNotFound() {
        AbstractOAuth2IdentityProviderTest.TestProvider tested = getTested();
        Map<String, String> notes = new HashMap<>();
        getFederatedIdentity("{\"cosi\":\"sss\"}");
    }

    @Test(expected = IdentityBrokerException.class)
    public void getFederatedIdentity_responseJSON_invalidFormat() {
        AbstractOAuth2IdentityProviderTest.TestProvider tested = getTested();
        Map<String, String> notes = new HashMap<>();
        getFederatedIdentity("{\"cosi\":\"sss\"");
    }

    @Test(expected = IdentityBrokerException.class)
    public void getFederatedIdentity_responseJSON_emptyTokenField() {
        AbstractOAuth2IdentityProviderTest.TestProvider tested = getTested();
        tested.getFederatedIdentity((("{\"" + (AbstractOAuth2IdentityProvider.OAUTH2_PARAMETER_ACCESS_TOKEN)) + "\" : \"\"}"));
    }

    @Test(expected = IdentityBrokerException.class)
    public void getFederatedIdentity_responseJSON_nullTokenField() {
        AbstractOAuth2IdentityProviderTest.TestProvider tested = getTested();
        tested.getFederatedIdentity((("{\"" + (AbstractOAuth2IdentityProvider.OAUTH2_PARAMETER_ACCESS_TOKEN)) + "\" : null}"));
    }

    @Test
    public void getFederatedIdentity_responseJSON() {
        AbstractOAuth2IdentityProviderTest.TestProvider tested = getTested();
        BrokeredIdentityContext fi = tested.getFederatedIdentity((("{\"" + (AbstractOAuth2IdentityProvider.OAUTH2_PARAMETER_ACCESS_TOKEN)) + "\" : \"458rt\"}"));
        Assert.assertNotNull(fi);
        Assert.assertEquals("458rt", fi.getId());
    }

    @Test
    public void getFederatedIdentity_responseUrlLine() {
        AbstractOAuth2IdentityProviderTest.TestProvider tested = getTested();
        BrokeredIdentityContext fi = tested.getFederatedIdentity((("cosi=sss&" + (AbstractOAuth2IdentityProvider.OAUTH2_PARAMETER_ACCESS_TOKEN)) + "=458rtf&kdesi=ss}"));
        Assert.assertNotNull(fi);
        Assert.assertEquals("458rtf", fi.getId());
    }

    private static class TestProvider extends AbstractOAuth2IdentityProvider<OAuth2IdentityProviderConfig> {
        public TestProvider(OAuth2IdentityProviderConfig config) {
            super(null, config);
        }

        @Override
        protected String getDefaultScopes() {
            return "default";
        }

        protected BrokeredIdentityContext doGetFederatedIdentity(String accessToken) {
            return new BrokeredIdentityContext(accessToken);
        }
    }
}

