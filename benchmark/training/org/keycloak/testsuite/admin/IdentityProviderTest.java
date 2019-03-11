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
package org.keycloak.testsuite.admin;


import ComponentRepresentation.SECRET_VALUE;
import MediaType.APPLICATION_XML_TYPE;
import MediaType.TEXT_PLAIN_TYPE;
import OperationType.CREATE;
import OperationType.DELETE;
import OperationType.UPDATE;
import ResourceType.IDENTITY_PROVIDER;
import ResourceType.IDENTITY_PROVIDER_MAPPER;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.junit.Test;
import org.keycloak.admin.client.resource.IdentityProviderResource;
import org.keycloak.representations.idm.AdminEventRepresentation;
import org.keycloak.representations.idm.IdentityProviderMapperRepresentation;
import org.keycloak.representations.idm.IdentityProviderMapperTypeRepresentation;
import org.keycloak.representations.idm.IdentityProviderRepresentation;
import org.keycloak.saml.common.exceptions.ParsingException;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.Assert;
import org.keycloak.testsuite.util.AdminEventPaths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class IdentityProviderTest extends AbstractAdminTest {
    // Certificate imported from
    private static final String SIGNING_CERT_1 = "MIICmzCCAYMCBgFUYnC0OjANBgkqhkiG9w0BAQsFADARMQ8wDQY" + ((((((((("DVQQDDAZtYXN0ZXIwHhcNMTYwNDI5MTQzMjEzWhcNMjYwNDI5MTQzMzUzWjARMQ8wDQYDVQQDDAZtYXN0ZXI" + "wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCN25AW1poMEZRbuMAHG58AThZmCwMV6/Gcui4mjGa") + "cRFyudgqzLjQ2rxpoW41JAtLjbjeAhuWvirUcFVcOeS3gM/ZC27qCpYighAcylZz6MYocnEe1+e8rPPk4JlI") + "D6Wv62dgu+pL/vYsQpRhvD3Y2c/ytgr5D32xF+KnzDehUy5BSyzypvu12Wq9mS5vK5tzkN37EjkhpY2ZxaXP") + "ubjDIITCAL4Q8M/m5IlacBaUZbzI4AQrHnMP1O1IH2dHSWuMiBe+xSDTco72PmuYPJKTV4wQdeBUIkYbfLc4") + "RxVmXEvgkQgyW86EoMPxlWJpj7+mTIR+l+2thZPr/VgwTs82rAgMBAAEwDQYJKoZIhvcNAQELBQADggEBAA/") + "Ip/Hi8RoVu5ouaFFlc5whT7ltuK8slfLGW4tM4vJXhInYwsqIRQKBNDYW/64xle3eII4u1yAH1OYRRwEs7Em") + "1pr4QuFuTY1at+aE0sE46XDlyESI0txJjWxYoT133vM0We2pj1b2nxgU30rwjKA3whnKEfTEYT/n3JBSqNgg") + "y6l8ZGw/oPSgvPaR4+xeB1tfQFC4VrLoYKoqH6hAL530nKxL+qV8AIfL64NDEE8ankIAEDAAFe8x3CPUfXR/") + "p4KOANKkpz8ieQaHDb1eITkAwUwjESj6UF9D1aePlhWls/HX0gujFXtWfWfrJ8CU/ogwlH8y1jgRuLjFQYZk6llc=");

    private static final String SIGNING_CERT_2 = "MIIBnDCCAQUCBgFYKXKsPTANBgkqhkiG9w0BAQsFADAUMRIwEAY" + ((((("DVQQDDAlzYW1sLWRlbW8wHhcNMTYxMTAzMDkwNzEwWhcNMjYxMTAzMDkwODUwWjAUMRIwEAYDVQQDDAlzYW1" + "sLWRlbW8wgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBAKtWsK5O0CtuBpnMvWG+HTG0vmZzujQ2o9WdheQ") + "u+BzCILcGMsbDW0YQaglpcO5JpGWWhubnckGGPHfdQ2/7nP9QwbiTK0FbGF41UqcvoaCqU1psxoV88s8IXyQ") + "CAqeyLv00yj6foqdJjxh5SZ5z+na+M7Y2OxIBVxYRAxWEnfUvAgMBAAEwDQYJKoZIhvcNAQELBQADgYEAhet") + "vOU8TyqfZF5jpv0IcrviLl/DoFrbjByeHR+pu/vClcAOjL/u7oQELuuTfNsBI4tpexUj5G8q/YbEz0gk7idf") + "LXrAUVcsR73oTngrhRfwUSmPrjjK0kjcRb6HL9V/+wh3R/6mEd59U08ExT8N38rhmn0CI3ehMdebReprP7U8=");

    @Test
    public void testFindAll() {
        create(createRep("google", "google"));
        create(createRep("facebook", "facebook"));
        Assert.assertNames(realm.identityProviders().findAll(), "google", "facebook");
    }

    @Test
    public void testCreate() {
        IdentityProviderRepresentation newIdentityProvider = createRep("new-identity-provider", "oidc");
        newIdentityProvider.getConfig().put("clientId", "clientId");
        newIdentityProvider.getConfig().put("clientSecret", "some secret value");
        create(newIdentityProvider);
        IdentityProviderResource identityProviderResource = realm.identityProviders().get("new-identity-provider");
        org.junit.Assert.assertNotNull(identityProviderResource);
        IdentityProviderRepresentation representation = identityProviderResource.toRepresentation();
        org.junit.Assert.assertNotNull(representation);
        org.junit.Assert.assertNotNull(representation.getInternalId());
        org.junit.Assert.assertEquals("new-identity-provider", representation.getAlias());
        org.junit.Assert.assertEquals("oidc", representation.getProviderId());
        org.junit.Assert.assertEquals("clientId", representation.getConfig().get("clientId"));
        org.junit.Assert.assertEquals(SECRET_VALUE, representation.getConfig().get("clientSecret"));
        assertTrue(representation.isEnabled());
        assertFalse(representation.isStoreToken());
        assertFalse(representation.isTrustEmail());
        org.junit.Assert.assertEquals("some secret value", testingClient.testing("admin-client-test").getIdentityProviderConfig("new-identity-provider").get("clientSecret"));
        IdentityProviderRepresentation rep = realm.identityProviders().findAll().stream().filter(( i) -> i.getAlias().equals("new-identity-provider")).findFirst().get();
        org.junit.Assert.assertEquals(SECRET_VALUE, rep.getConfig().get("clientSecret"));
    }

    @Test
    public void testUpdate() {
        IdentityProviderRepresentation newIdentityProvider = createRep("update-identity-provider", "oidc");
        newIdentityProvider.getConfig().put("clientId", "clientId");
        newIdentityProvider.getConfig().put("clientSecret", "some secret value");
        create(newIdentityProvider);
        IdentityProviderResource identityProviderResource = realm.identityProviders().get("update-identity-provider");
        org.junit.Assert.assertNotNull(identityProviderResource);
        IdentityProviderRepresentation representation = identityProviderResource.toRepresentation();
        org.junit.Assert.assertNotNull(representation);
        org.junit.Assert.assertEquals("update-identity-provider", representation.getAlias());
        representation.setAlias("changed-alias");
        representation.setEnabled(false);
        representation.setStoreToken(true);
        representation.getConfig().put("clientId", "changedClientId");
        identityProviderResource.update(representation);
        AdminEventRepresentation event = assertAdminEvents.assertEvent(realmId, UPDATE, AdminEventPaths.identityProviderPath("update-identity-provider"), representation, IDENTITY_PROVIDER);
        assertFalse(event.getRepresentation().contains("some secret value"));
        assertTrue(event.getRepresentation().contains(SECRET_VALUE));
        identityProviderResource = realm.identityProviders().get(representation.getInternalId());
        org.junit.Assert.assertNotNull(identityProviderResource);
        representation = identityProviderResource.toRepresentation();
        assertFalse(representation.isEnabled());
        assertTrue(representation.isStoreToken());
        org.junit.Assert.assertEquals("changedClientId", representation.getConfig().get("clientId"));
        org.junit.Assert.assertEquals("some secret value", testingClient.testing("admin-client-test").getIdentityProviderConfig("changed-alias").get("clientSecret"));
    }

    @Test
    public void testRemove() {
        IdentityProviderRepresentation newIdentityProvider = createRep("remove-identity-provider", "saml");
        create(newIdentityProvider);
        IdentityProviderResource identityProviderResource = realm.identityProviders().get("remove-identity-provider");
        org.junit.Assert.assertNotNull(identityProviderResource);
        IdentityProviderRepresentation representation = identityProviderResource.toRepresentation();
        org.junit.Assert.assertNotNull(representation);
        identityProviderResource.remove();
        assertAdminEvents.assertEvent(realmId, DELETE, AdminEventPaths.identityProviderPath("remove-identity-provider"), IDENTITY_PROVIDER);
        try {
            realm.identityProviders().get("remove-identity-provider").toRepresentation();
            fail("Not expected to found");
        } catch (NotFoundException nfe) {
            // Expected
        }
    }

    @Test
    public void testMapperTypes() {
        IdentityProviderResource provider;
        Map<String, IdentityProviderMapperTypeRepresentation> mapperTypes;
        create(createRep("google", "google"));
        provider = realm.identityProviders().get("google");
        mapperTypes = provider.getMapperTypes();
        assertMapperTypes(mapperTypes, "google-user-attribute-mapper");
        create(createRep("facebook", "facebook"));
        provider = realm.identityProviders().get("facebook");
        mapperTypes = provider.getMapperTypes();
        assertMapperTypes(mapperTypes, "facebook-user-attribute-mapper");
        create(createRep("github", "github"));
        provider = realm.identityProviders().get("github");
        mapperTypes = provider.getMapperTypes();
        assertMapperTypes(mapperTypes, "github-user-attribute-mapper");
        create(createRep("twitter", "twitter"));
        provider = realm.identityProviders().get("twitter");
        mapperTypes = provider.getMapperTypes();
        assertMapperTypes(mapperTypes);
        create(createRep("linkedin", "linkedin"));
        provider = realm.identityProviders().get("linkedin");
        mapperTypes = provider.getMapperTypes();
        assertMapperTypes(mapperTypes, "linkedin-user-attribute-mapper");
        create(createRep("microsoft", "microsoft"));
        provider = realm.identityProviders().get("microsoft");
        mapperTypes = provider.getMapperTypes();
        assertMapperTypes(mapperTypes, "microsoft-user-attribute-mapper");
        create(createRep("stackoverflow", "stackoverflow"));
        provider = realm.identityProviders().get("stackoverflow");
        mapperTypes = provider.getMapperTypes();
        assertMapperTypes(mapperTypes, "stackoverflow-user-attribute-mapper");
        create(createRep("keycloak-oidc", "keycloak-oidc"));
        provider = realm.identityProviders().get("keycloak-oidc");
        mapperTypes = provider.getMapperTypes();
        assertMapperTypes(mapperTypes, "keycloak-oidc-role-to-role-idp-mapper", "oidc-user-attribute-idp-mapper", "oidc-role-idp-mapper", "oidc-username-idp-mapper");
        create(createRep("oidc", "oidc"));
        provider = realm.identityProviders().get("oidc");
        mapperTypes = provider.getMapperTypes();
        assertMapperTypes(mapperTypes, "oidc-user-attribute-idp-mapper", "oidc-role-idp-mapper", "oidc-username-idp-mapper");
        create(createRep("saml", "saml"));
        provider = realm.identityProviders().get("saml");
        mapperTypes = provider.getMapperTypes();
        assertMapperTypes(mapperTypes, "saml-user-attribute-idp-mapper", "saml-role-idp-mapper", "saml-username-idp-mapper");
    }

    @Test
    public void testNoExport() {
        create(createRep("keycloak-oidc", "keycloak-oidc"));
        Response response = realm.identityProviders().get("keycloak-oidc").export("json");
        assertEquals("status", 204, response.getStatus());
        String body = response.readEntity(String.class);
        assertNull("body", body);
        response.close();
    }

    @Test
    public void testSamlImportAndExport() throws IOException, URISyntaxException, ParsingException {
        // Use import-config to convert IDPSSODescriptor file into key value pairs
        // to use when creating a SAML Identity Provider
        MultipartFormDataOutput form = new MultipartFormDataOutput();
        form.addFormData("providerId", "saml", TEXT_PLAIN_TYPE);
        URL idpMeta = getClass().getClassLoader().getResource("admin-test/saml-idp-metadata.xml");
        byte[] content = Files.readAllBytes(Paths.get(idpMeta.toURI()));
        String body = new String(content, Charset.forName("utf-8"));
        form.addFormData("file", body, APPLICATION_XML_TYPE, "saml-idp-metadata.xml");
        Map<String, String> result = realm.identityProviders().importFrom(form);
        assertSamlImport(result, IdentityProviderTest.SIGNING_CERT_1);
        // Create new SAML identity provider using configuration retrieved from import-config
        create(createRep("saml", "saml", result));
        IdentityProviderResource provider = realm.identityProviders().get("saml");
        IdentityProviderRepresentation rep = provider.toRepresentation();
        assertCreatedSamlIdp(rep);
        // Now list the providers - we should see the one just created
        List<IdentityProviderRepresentation> providers = realm.identityProviders().findAll();
        assertNotNull("identityProviders not null", providers);
        assertEquals("identityProviders instance count", 1, providers.size());
        assertEqual(rep, providers.get(0));
        // Perform export, and make sure some of the values are like they're supposed to be
        Response response = realm.identityProviders().get("saml").export("xml");
        Assert.assertEquals(200, response.getStatus());
        body = response.readEntity(String.class);
        response.close();
        assertSamlExport(body);
    }

    @Test
    public void testSamlImportAndExportMultipleSigningKeys() throws IOException, URISyntaxException, ParsingException {
        // Use import-config to convert IDPSSODescriptor file into key value pairs
        // to use when creating a SAML Identity Provider
        MultipartFormDataOutput form = new MultipartFormDataOutput();
        form.addFormData("providerId", "saml", TEXT_PLAIN_TYPE);
        URL idpMeta = getClass().getClassLoader().getResource("admin-test/saml-idp-metadata-two-signing-certs.xml");
        byte[] content = Files.readAllBytes(Paths.get(idpMeta.toURI()));
        String body = new String(content, Charset.forName("utf-8"));
        form.addFormData("file", body, APPLICATION_XML_TYPE, "saml-idp-metadata-two-signing-certs");
        Map<String, String> result = realm.identityProviders().importFrom(form);
        assertSamlImport(result, (((IdentityProviderTest.SIGNING_CERT_1) + ",") + (IdentityProviderTest.SIGNING_CERT_2)));
        // Create new SAML identity provider using configuration retrieved from import-config
        create(createRep("saml", "saml", result));
        IdentityProviderResource provider = realm.identityProviders().get("saml");
        IdentityProviderRepresentation rep = provider.toRepresentation();
        assertCreatedSamlIdp(rep);
        // Now list the providers - we should see the one just created
        List<IdentityProviderRepresentation> providers = realm.identityProviders().findAll();
        assertNotNull("identityProviders not null", providers);
        assertEquals("identityProviders instance count", 1, providers.size());
        assertEqual(rep, providers.get(0));
        // Perform export, and make sure some of the values are like they're supposed to be
        Response response = realm.identityProviders().get("saml").export("xml");
        Assert.assertEquals(200, response.getStatus());
        body = response.readEntity(String.class);
        response.close();
        assertSamlExport(body);
    }

    @Test
    public void testMappers() {
        create(createRep("google", "google"));
        IdentityProviderResource provider = realm.identityProviders().get("google");
        IdentityProviderMapperRepresentation mapper = new IdentityProviderMapperRepresentation();
        mapper.setIdentityProviderAlias("google");
        mapper.setName("my_mapper");
        mapper.setIdentityProviderMapper("oidc-hardcoded-role-idp-mapper");
        Map<String, String> config = new HashMap<>();
        config.put("role", "offline_access");
        mapper.setConfig(config);
        // createRep and add mapper
        Response response = provider.addMapper(mapper);
        String id = ApiUtil.getCreatedId(response);
        Assert.assertNotNull(id);
        response.close();
        assertAdminEvents.assertEvent(realmId, CREATE, AdminEventPaths.identityProviderMapperPath("google", id), mapper, IDENTITY_PROVIDER_MAPPER);
        // list mappers
        List<IdentityProviderMapperRepresentation> mappers = provider.getMappers();
        assertEquals("mappers count", 1, mappers.size());
        assertEquals("newly created mapper id", id, mappers.get(0).getId());
        // get mapper
        mapper = provider.getMapperById(id);
        assertNotNull("mapperById not null", mapper);
        assertEquals("mapper id", id, mapper.getId());
        assertNotNull("mapper.config exists", mapper.getConfig());
        assertEquals("config retained", "offline_access", mapper.getConfig().get("role"));
        // add duplicate mapper
        Response error = provider.addMapper(mapper);
        assertEquals("mapper unique name", 400, error.getStatus());
        error.close();
        // update mapper
        mapper.getConfig().put("role", "master-realm.manage-realm");
        provider.update(id, mapper);
        assertAdminEvents.assertEvent(realmId, UPDATE, AdminEventPaths.identityProviderMapperPath("google", id), mapper, IDENTITY_PROVIDER_MAPPER);
        mapper = provider.getMapperById(id);
        assertNotNull("mapperById not null", mapper);
        assertEquals("config changed", "master-realm.manage-realm", mapper.getConfig().get("role"));
        // delete mapper
        provider.delete(id);
        assertAdminEvents.assertEvent(realmId, DELETE, AdminEventPaths.identityProviderMapperPath("google", id), IDENTITY_PROVIDER_MAPPER);
        try {
            provider.getMapperById(id);
            fail("Should fail with NotFoundException");
        } catch (NotFoundException e) {
            // Expected
        }
    }

    // KEYCLOAK-4962
    @Test
    public void testUpdateProtocolMappers() {
        create(createRep("google2", "google"));
        IdentityProviderResource provider = realm.identityProviders().get("google2");
        IdentityProviderMapperRepresentation mapper = new IdentityProviderMapperRepresentation();
        mapper.setIdentityProviderAlias("google2");
        mapper.setName("my_mapper");
        mapper.setIdentityProviderMapper("oidc-hardcoded-role-idp-mapper");
        Map<String, String> config = new HashMap<>();
        config.put("role", "");
        mapper.setConfig(config);
        Response response = provider.addMapper(mapper);
        String mapperId = ApiUtil.getCreatedId(response);
        List<IdentityProviderMapperRepresentation> mappers = provider.getMappers();
        org.junit.Assert.assertEquals(1, mappers.size());
        org.junit.Assert.assertEquals(0, mappers.get(0).getConfig().size());
        mapper = provider.getMapperById(mapperId);
        mapper.getConfig().put("role", "offline_access");
        provider.update(mapperId, mapper);
        mappers = provider.getMappers();
        org.junit.Assert.assertEquals(1, mappers.size());
        org.junit.Assert.assertEquals(1, mappers.get(0).getConfig().size());
        org.junit.Assert.assertEquals("offline_access", mappers.get(0).getConfig().get("role"));
    }

    // KEYCLOAK-7872
    @Test
    public void testDeleteProtocolMappersAfterDeleteIdentityProvider() {
        create(createRep("google3", "google"));
        IdentityProviderResource provider = realm.identityProviders().get("google3");
        IdentityProviderMapperRepresentation mapper = new IdentityProviderMapperRepresentation();
        mapper.setIdentityProviderAlias("google3");
        mapper.setName("my_mapper");
        mapper.setIdentityProviderMapper("oidc-hardcoded-role-idp-mapper");
        Map<String, String> config = new HashMap<>();
        config.put("role", "offline_access");
        mapper.setConfig(config);
        Response response = provider.addMapper(mapper);
        List<IdentityProviderMapperRepresentation> mappers = provider.getMappers();
        assertThat(mappers, Matchers.hasSize(1));
        assertAdminEvents.clear();
        provider.remove();
        assertAdminEvents.assertEvent(realmId, DELETE, AdminEventPaths.identityProviderPath("google3"), IDENTITY_PROVIDER);
        create(createRep("google3", "google"));
        IdentityProviderResource newProvider = realm.identityProviders().get("google3");
        assertThat(newProvider.getMappers(), Matchers.empty());
    }

    @Test
    public void testInstalledIdentityProviders() {
        Response response = realm.identityProviders().getIdentityProviders("oidc");
        assertEquals("Status", 200, response.getStatus());
        Map<String, String> body = response.readEntity(Map.class);
        assertProviderInfo(body, "oidc", "OpenID Connect v1.0");
        response = realm.identityProviders().getIdentityProviders("keycloak-oidc");
        assertEquals("Status", 200, response.getStatus());
        body = response.readEntity(Map.class);
        assertProviderInfo(body, "keycloak-oidc", "Keycloak OpenID Connect");
        response = realm.identityProviders().getIdentityProviders("saml");
        assertEquals("Status", 200, response.getStatus());
        body = response.readEntity(Map.class);
        assertProviderInfo(body, "saml", "SAML v2.0");
        response = realm.identityProviders().getIdentityProviders("google");
        assertEquals("Status", 200, response.getStatus());
        body = response.readEntity(Map.class);
        assertProviderInfo(body, "google", "Google");
        response = realm.identityProviders().getIdentityProviders("facebook");
        assertEquals("Status", 200, response.getStatus());
        body = response.readEntity(Map.class);
        assertProviderInfo(body, "facebook", "Facebook");
        response = realm.identityProviders().getIdentityProviders("github");
        assertEquals("Status", 200, response.getStatus());
        body = response.readEntity(Map.class);
        assertProviderInfo(body, "github", "GitHub");
        response = realm.identityProviders().getIdentityProviders("twitter");
        assertEquals("Status", 200, response.getStatus());
        body = response.readEntity(Map.class);
        assertProviderInfo(body, "twitter", "Twitter");
        response = realm.identityProviders().getIdentityProviders("linkedin");
        assertEquals("Status", 200, response.getStatus());
        body = response.readEntity(Map.class);
        assertProviderInfo(body, "linkedin", "LinkedIn");
        response = realm.identityProviders().getIdentityProviders("microsoft");
        assertEquals("Status", 200, response.getStatus());
        body = response.readEntity(Map.class);
        assertProviderInfo(body, "microsoft", "Microsoft");
        response = realm.identityProviders().getIdentityProviders("stackoverflow");
        assertEquals("Status", 200, response.getStatus());
        body = response.readEntity(Map.class);
        assertProviderInfo(body, "stackoverflow", "StackOverflow");
        response = realm.identityProviders().getIdentityProviders("nonexistent");
        assertEquals("Status", 400, response.getStatus());
    }
}

