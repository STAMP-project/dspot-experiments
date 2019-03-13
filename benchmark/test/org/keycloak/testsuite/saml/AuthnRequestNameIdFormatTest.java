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
package org.keycloak.testsuite.saml;


import Binding.POST;
import Binding.REDIRECT;
import JBossSAMLURIConstants.NAMEID_FORMAT_EMAIL;
import JBossSAMLURIConstants.NAMEID_FORMAT_PERSISTENT;
import JBossSAMLURIConstants.NAMEID_FORMAT_UNSPECIFIED;
import SamlConfigAttributes.SAML_FORCE_POST_BINDING;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.dom.saml.v2.protocol.NameIDPolicyType;
import org.keycloak.representations.idm.ClientRepresentation;


/**
 *
 *
 * @author hmlnarik
 */
public class AuthnRequestNameIdFormatTest extends AbstractSamlTest {
    @Test
    public void testPostLoginNameIdPolicyUnspecified() throws Exception {
        NameIDPolicyType nameIdPolicy = new NameIDPolicyType();
        nameIdPolicy.setFormat(NAMEID_FORMAT_UNSPECIFIED.getUri());
        testLoginWithNameIdPolicy(POST, POST, nameIdPolicy, is("bburke"));
    }

    @Test
    public void testPostLoginNameIdPolicyEmail() throws Exception {
        NameIDPolicyType nameIdPolicy = new NameIDPolicyType();
        nameIdPolicy.setFormat(NAMEID_FORMAT_EMAIL.getUri());
        testLoginWithNameIdPolicy(POST, POST, nameIdPolicy, is("bburke@redhat.com"));
    }

    @Test
    public void testPostLoginNameIdPolicyPersistent() throws Exception {
        NameIDPolicyType nameIdPolicy = new NameIDPolicyType();
        nameIdPolicy.setFormat(NAMEID_FORMAT_PERSISTENT.getUri());
        testLoginWithNameIdPolicy(POST, POST, nameIdPolicy, startsWith("G-"));
    }

    @Test
    public void testPostLoginNoNameIdPolicyUnset() throws Exception {
        testLoginWithNameIdPolicy(POST, POST, null, is("bburke"));
    }

    @Test
    public void testRedirectLoginNameIdPolicyUnspecified() throws Exception {
        NameIDPolicyType nameIdPolicy = new NameIDPolicyType();
        nameIdPolicy.setFormat(NAMEID_FORMAT_UNSPECIFIED.getUri());
        testLoginWithNameIdPolicy(REDIRECT, REDIRECT, nameIdPolicy, is("bburke"));
    }

    @Test
    public void testRedirectLoginNameIdPolicyEmail() throws Exception {
        NameIDPolicyType nameIdPolicy = new NameIDPolicyType();
        nameIdPolicy.setFormat(NAMEID_FORMAT_EMAIL.getUri());
        testLoginWithNameIdPolicy(REDIRECT, REDIRECT, nameIdPolicy, is("bburke@redhat.com"));
    }

    @Test
    public void testRedirectLoginNameIdPolicyPersistent() throws Exception {
        NameIDPolicyType nameIdPolicy = new NameIDPolicyType();
        nameIdPolicy.setFormat(NAMEID_FORMAT_PERSISTENT.getUri());
        testLoginWithNameIdPolicy(REDIRECT, REDIRECT, nameIdPolicy, startsWith("G-"));
    }

    @Test
    public void testRedirectLoginNoNameIdPolicyUnset() throws Exception {
        testLoginWithNameIdPolicy(REDIRECT, REDIRECT, null, is("bburke"));
    }

    @Test
    public void testRedirectLoginNoNameIdPolicyForcePostBinding() throws Exception {
        ClientsResource clients = adminClient.realm(AbstractSamlTest.REALM_NAME).clients();
        List<ClientRepresentation> foundClients = clients.findByClientId(AbstractSamlTest.SAML_CLIENT_ID_SALES_POST);
        Assert.assertThat(foundClients, hasSize(1));
        ClientResource clientRes = clients.get(foundClients.get(0).getId());
        ClientRepresentation client = clientRes.toRepresentation();
        client.getAttributes().put(SAML_FORCE_POST_BINDING, "true");
        clientRes.update(client);
        testLoginWithNameIdPolicy(REDIRECT, POST, null, is("bburke"));
        // Revert
        client = clientRes.toRepresentation();
        client.getAttributes().put(SAML_FORCE_POST_BINDING, "false");
        clientRes.update(client);
    }
}

