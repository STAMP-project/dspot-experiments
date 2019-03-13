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
package org.keycloak.testsuite.admin.client;


import OperationType.CREATE;
import OperationType.DELETE;
import OperationType.UPDATE;
import ResourceType.PROTOCOL_MAPPER;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.keycloak.admin.client.resource.ProtocolMappersResource;
import org.keycloak.representations.idm.ProtocolMapperRepresentation;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.util.AdminEventPaths;


/**
 *
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ClientScopeProtocolMapperTest extends AbstractProtocolMapperTest {
    private String oidcClientScopeId;

    private ProtocolMappersResource oidcMappersRsc;

    private String samlClientScopeId;

    private ProtocolMappersResource samlMappersRsc;

    @Test
    public void test01GetMappersList() {
        Assert.assertTrue(oidcMappersRsc.getMappers().isEmpty());
        Assert.assertTrue(samlMappersRsc.getMappers().isEmpty());
    }

    @Test
    public void test02CreateOidcMappersFromList() {
        testAddAllBuiltinMappers(oidcMappersRsc, "openid-connect", AdminEventPaths.clientScopeProtocolMappersPath(oidcClientScopeId));
    }

    @Test
    public void test03CreateSamlMappersFromList() {
        testAddAllBuiltinMappers(samlMappersRsc, "saml", AdminEventPaths.clientScopeProtocolMappersPath(samlClientScopeId));
    }

    @Test
    public void test04CreateSamlProtocolMapper() {
        // {"protocol":"saml",
        // "config":{"role":"account.view-profile","new.role.name":"new-role-name"},
        // "consentRequired":true,
        // "consentText":"My consent text",
        // "name":"saml-role-name-maper",
        // "protocolMapper":"saml-role-name-mapper"}
        ProtocolMapperRepresentation rep = makeSamlMapper("saml-role-name-mapper");
        int totalMappers = samlMappersRsc.getMappers().size();
        int totalSamlMappers = samlMappersRsc.getMappersPerProtocol("saml").size();
        Response resp = samlMappersRsc.createMapper(rep);
        resp.close();
        String createdId = ApiUtil.getCreatedId(resp);
        assertAdminEvents.assertEvent(getRealmId(), CREATE, AdminEventPaths.clientScopeProtocolMapperPath(samlClientScopeId, createdId), rep, PROTOCOL_MAPPER);
        Assert.assertEquals((totalMappers + 1), samlMappersRsc.getMappers().size());
        Assert.assertEquals((totalSamlMappers + 1), samlMappersRsc.getMappersPerProtocol("saml").size());
        ProtocolMapperRepresentation created = samlMappersRsc.getMapperById(createdId);
        assertEqualMappers(rep, created);
    }

    @Test
    public void test05CreateOidcProtocolMapper() {
        // {"protocol":"openid-connect",
        // "config":{"role":"myrole"},
        // "consentRequired":true,
        // "consentText":"My consent text",
        // "name":"oidc-hardcoded-role-mapper",
        // "protocolMapper":"oidc-hardcoded-role-mapper"}
        ProtocolMapperRepresentation rep = makeOidcMapper("oidc-hardcoded-role-mapper");
        int totalMappers = oidcMappersRsc.getMappers().size();
        int totalOidcMappers = oidcMappersRsc.getMappersPerProtocol("openid-connect").size();
        Response resp = oidcMappersRsc.createMapper(rep);
        resp.close();
        String createdId = ApiUtil.getCreatedId(resp);
        assertAdminEvents.assertEvent(getRealmId(), CREATE, AdminEventPaths.clientScopeProtocolMapperPath(oidcClientScopeId, createdId), rep, PROTOCOL_MAPPER);
        Assert.assertEquals((totalMappers + 1), oidcMappersRsc.getMappers().size());
        Assert.assertEquals((totalOidcMappers + 1), oidcMappersRsc.getMappersPerProtocol("openid-connect").size());
        ProtocolMapperRepresentation created = oidcMappersRsc.getMapperById(createdId);// findByName(samlMappersRsc, "saml-role-name-mapper");

        assertEqualMappers(rep, created);
    }

    @Test
    public void test06UpdateSamlMapper() {
        ProtocolMapperRepresentation rep = makeSamlMapper("saml-role-name-mapper2");
        Response resp = samlMappersRsc.createMapper(rep);
        resp.close();
        String createdId = ApiUtil.getCreatedId(resp);
        assertAdminEvents.assertEvent(getRealmId(), CREATE, AdminEventPaths.clientScopeProtocolMapperPath(samlClientScopeId, createdId), rep, PROTOCOL_MAPPER);
        rep.getConfig().put("role", "account.manage-account");
        rep.setId(createdId);
        samlMappersRsc.update(createdId, rep);
        assertAdminEvents.assertEvent(getRealmId(), UPDATE, AdminEventPaths.clientScopeProtocolMapperPath(samlClientScopeId, createdId), rep, PROTOCOL_MAPPER);
        ProtocolMapperRepresentation updated = samlMappersRsc.getMapperById(createdId);
        assertEqualMappers(rep, updated);
    }

    @Test
    public void test07UpdateOidcMapper() {
        ProtocolMapperRepresentation rep = makeOidcMapper("oidc-hardcoded-role-mapper2");
        Response resp = oidcMappersRsc.createMapper(rep);
        resp.close();
        String createdId = ApiUtil.getCreatedId(resp);
        assertAdminEvents.assertEvent(getRealmId(), CREATE, AdminEventPaths.clientScopeProtocolMapperPath(oidcClientScopeId, createdId), rep, PROTOCOL_MAPPER);
        rep.getConfig().put("role", "myotherrole");
        rep.setId(createdId);
        oidcMappersRsc.update(createdId, rep);
        assertAdminEvents.assertEvent(getRealmId(), UPDATE, AdminEventPaths.clientScopeProtocolMapperPath(oidcClientScopeId, createdId), rep, PROTOCOL_MAPPER);
        ProtocolMapperRepresentation updated = oidcMappersRsc.getMapperById(createdId);
        assertEqualMappers(rep, updated);
    }

    @Test
    public void testDeleteSamlMapper() {
        ProtocolMapperRepresentation rep = makeSamlMapper("saml-role-name-mapper3");
        Response resp = samlMappersRsc.createMapper(rep);
        resp.close();
        String createdId = ApiUtil.getCreatedId(resp);
        assertAdminEvents.assertEvent(getRealmId(), CREATE, AdminEventPaths.clientScopeProtocolMapperPath(samlClientScopeId, createdId), rep, PROTOCOL_MAPPER);
        samlMappersRsc.delete(createdId);
        assertAdminEvents.assertEvent(getRealmId(), DELETE, AdminEventPaths.clientScopeProtocolMapperPath(samlClientScopeId, createdId), PROTOCOL_MAPPER);
        try {
            samlMappersRsc.getMapperById(createdId);
            Assert.fail("Not expected to find mapper");
        } catch (NotFoundException nfe) {
            // Expected
        }
    }

    @Test
    public void testDeleteOidcMapper() {
        ProtocolMapperRepresentation rep = makeOidcMapper("oidc-hardcoded-role-mapper3");
        Response resp = oidcMappersRsc.createMapper(rep);
        resp.close();
        String createdId = ApiUtil.getCreatedId(resp);
        assertAdminEvents.assertEvent(getRealmId(), CREATE, AdminEventPaths.clientScopeProtocolMapperPath(oidcClientScopeId, createdId), rep, PROTOCOL_MAPPER);
        oidcMappersRsc.delete(createdId);
        assertAdminEvents.assertEvent(getRealmId(), DELETE, AdminEventPaths.clientScopeProtocolMapperPath(oidcClientScopeId, createdId), PROTOCOL_MAPPER);
        try {
            oidcMappersRsc.getMapperById(createdId);
            Assert.fail("Not expected to find mapper");
        } catch (NotFoundException nfe) {
            // Expected
        }
    }
}

