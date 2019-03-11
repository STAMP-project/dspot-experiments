/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates
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
package org.keycloak.testsuite.saml;


import SamlClient.Binding.REDIRECT;
import org.apache.http.client.methods.HttpUriRequest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.dom.saml.v2.protocol.AuthnRequestType;
import org.keycloak.saml.processing.api.saml.v2.request.SAML2Request;
import org.keycloak.testsuite.util.SamlClient;


/**
 *
 *
 * @author hmlnarik
 */
public class SamlRedirectBindingTest extends AbstractSamlTest {
    @Test
    public void testNoWhitespaceInLoginRequest() throws Exception {
        AuthnRequestType authnRequest = SamlClient.createLoginRequestDocument(AbstractSamlTest.SAML_CLIENT_ID_SALES_POST, AbstractSamlTest.SAML_ASSERTION_CONSUMER_URL_SALES_POST, getAuthServerSamlEndpoint(AbstractSamlTest.REALM_NAME));
        HttpUriRequest req = REDIRECT.createSamlUnsignedRequest(getAuthServerSamlEndpoint(AbstractSamlTest.REALM_NAME), null, SAML2Request.convert(authnRequest));
        String url = req.getURI().getQuery();
        Assert.assertThat(url, Matchers.not(Matchers.containsString(" ")));
        Assert.assertThat(url, Matchers.not(Matchers.containsString("\n")));
        Assert.assertThat(url, Matchers.not(Matchers.containsString("\r")));
        Assert.assertThat(url, Matchers.not(Matchers.containsString("\t")));
    }
}

