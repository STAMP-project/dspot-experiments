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


import Binding.POST;
import SamlConfigAttributes.SAML_CLIENT_SIGNATURE_ATTRIBUTE;
import SamlConfigAttributes.SAML_SIGNING_CERTIFICATE_ATTRIBUTE;
import SamlProtocol.ATTRIBUTE_TRUE_VALUE;
import Status.BAD_REQUEST;
import Status.OK;
import org.junit.Test;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.updaters.ClientAttributeUpdater;
import org.keycloak.testsuite.util.Matchers;
import org.keycloak.testsuite.util.SamlClientBuilder;


/**
 *
 *
 * @author hmlnarik
 */
public class SamlClientCertificateExpirationTest extends AbstractSamlTest {
    @Test
    public void testExpiredCertificate() throws Exception {
        try (AutoCloseable cl = ClientAttributeUpdater.forClient(adminClient, AbstractSamlTest.REALM_NAME, AbstractSamlTest.SAML_CLIENT_ID_SALES_POST_SIG).setAttribute(SAML_SIGNING_CERTIFICATE_ATTRIBUTE, AbstractSamlTest.SAML_CLIENT_SALES_POST_SIG_EXPIRED_CERTIFICATE).setAttribute(SAML_CLIENT_SIGNATURE_ATTRIBUTE, ATTRIBUTE_TRUE_VALUE).update()) {
            new SamlClientBuilder().authnRequest(getAuthServerSamlEndpoint(AbstractSamlTest.REALM_NAME), AbstractSamlTest.SAML_CLIENT_ID_SALES_POST_SIG, AbstractSamlTest.SAML_ASSERTION_CONSUMER_URL_SALES_POST_SIG, POST).signWith(AbstractSamlTest.SAML_CLIENT_SALES_POST_SIG_EXPIRED_PRIVATE_KEY, AbstractSamlTest.SAML_CLIENT_SALES_POST_SIG_EXPIRED_PUBLIC_KEY).build().assertResponse(Matchers.statusCodeIsHC(BAD_REQUEST));
        }
    }

    @Test
    public void testValidCertificate() throws Exception {
        // Unsigned request should fail
        new SamlClientBuilder().authnRequest(getAuthServerSamlEndpoint(AbstractSamlTest.REALM_NAME), AbstractSamlTest.SAML_CLIENT_ID_SALES_POST_SIG, AbstractSamlTest.SAML_ASSERTION_CONSUMER_URL_SALES_POST_SIG, POST).build().assertResponse(Matchers.statusCodeIsHC(BAD_REQUEST));
        // Signed request should succeed
        new SamlClientBuilder().authnRequest(getAuthServerSamlEndpoint(AbstractSamlTest.REALM_NAME), AbstractSamlTest.SAML_CLIENT_ID_SALES_POST_SIG, AbstractSamlTest.SAML_ASSERTION_CONSUMER_URL_SALES_POST_SIG, POST).signWith(AbstractSamlTest.SAML_CLIENT_SALES_POST_SIG_PRIVATE_KEY, AbstractSamlTest.SAML_CLIENT_SALES_POST_SIG_PUBLIC_KEY).build().assertResponse(Matchers.statusCodeIsHC(OK));
    }
}

