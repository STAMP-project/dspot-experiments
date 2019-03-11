/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.keycloak.testsuite.console.clients;


import OIDCAttributeMapperHelper.INCLUDE_IN_ACCESS_TOKEN;
import OIDCAttributeMapperHelper.INCLUDE_IN_ID_TOKEN;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsEqual;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.representations.idm.ProtocolMapperRepresentation;
import org.keycloak.testsuite.console.AbstractConsoleTest;
import org.keycloak.testsuite.console.page.clients.mappers.ClientMapper;
import org.keycloak.testsuite.console.page.clients.mappers.ClientMappers;
import org.keycloak.testsuite.console.page.clients.mappers.CreateClientMappers;


/**
 *
 *
 * @author <a href="mailto:vramik@redhat.com">Vlastislav Ramik</a>
 */
public class ClientMappersOIDCTest extends AbstractClientTest {
    private String id;

    @Page
    private ClientMappers clientMappersPage;

    @Page
    private ClientMapper clientMapperPage;

    @Page
    private CreateClientMappers createClientMappersPage;

    @Test
    public void testHardcodedRole() {
        // create
        clientMappersPage.mapperTable().createMapper();
        setInitialValues("hardcoded role");
        createClientMappersPage.form().setMapperType(HARDCODED_ROLE);
        createClientMappersPage.form().selectRole(REALM_ROLE, "offline_access", null);
        createClientMappersPage.form().save();
        assertAlertSuccess();
        // check
        ProtocolMapperRepresentation found = findClientMapperByName(id, "hardcoded role");
        Assert.assertNotNull(found);
        Assert.assertEquals("oidc-hardcoded-role-mapper", found.getProtocolMapper());
        Map<String, String> config = found.getConfig();
        Assert.assertEquals("offline_access", config.get("role"));
        // edit
        createClientMappersPage.form().selectRole(CLIENT_ROLE, "view-profile", "account");
        createClientMappersPage.form().save();
        assertAlertSuccess();
        // check
        config = findClientMapperByName(id, "hardcoded role").getConfig();
        Assert.assertEquals("account.view-profile", config.get("role"));
        // delete
        clientMapperPage.setMapperId(found.getId());
        clientMapperPage.delete();
        assertAlertSuccess();
        // check
        Assert.assertNull(findClientMapperByName(id, "hardcoded role"));
    }

    @Test
    public void testHardcodedClaim() {
        // create
        clientMappersPage.mapperTable().createMapper();
        setInitialValues("hardcoded claim");
        createClientMappersPage.form().setMapperType(HARDCODED_CLAIM);
        createClientMappersPage.form().setTokenClaimName("claim name");
        createClientMappersPage.form().setTokenClaimValue("claim value");
        createClientMappersPage.form().setClaimJSONType("long");
        createClientMappersPage.form().setAddToIDToken(true);
        createClientMappersPage.form().setAddToAccessToken(true);
        createClientMappersPage.form().save();
        assertAlertSuccess();
        // check
        ProtocolMapperRepresentation found = findClientMapperByName(id, "hardcoded claim");
        Assert.assertNotNull(found);
        Assert.assertEquals("oidc-hardcoded-claim-mapper", found.getProtocolMapper());
        Map<String, String> config = found.getConfig();
        Assert.assertEquals("true", config.get("id.token.claim"));
        Assert.assertEquals("true", config.get("access.token.claim"));
        Assert.assertEquals("claim name", config.get("claim.name"));
        Assert.assertEquals("claim value", config.get("claim.value"));
        Assert.assertEquals("long", config.get("jsonType.label"));
    }

    @Test
    public void testUserSessionNote() {
        // create
        clientMappersPage.mapperTable().createMapper();
        setInitialValues("user session note");
        createClientMappersPage.form().setMapperType(USER_SESSION_NOTE);
        createClientMappersPage.form().setUserSessionNote("session note");
        createClientMappersPage.form().setTokenClaimName("claim name");
        createClientMappersPage.form().setClaimJSONType("int");
        createClientMappersPage.form().setAddToIDToken(false);
        createClientMappersPage.form().setAddToAccessToken(false);
        createClientMappersPage.form().save();
        assertAlertSuccess();
        // check
        ProtocolMapperRepresentation found = findClientMapperByName(id, "user session note");
        Assert.assertNotNull(found);
        Assert.assertEquals("oidc-usersessionmodel-note-mapper", found.getProtocolMapper());
        Map<String, String> config = found.getConfig();
        Assert.assertEquals("claim name", config.get("claim.name"));
        Assert.assertEquals("session note", config.get("user.session.note"));
        Assert.assertEquals("int", config.get("jsonType.label"));
    }

    @Test
    public void testRoleName() {
        // create
        clientMappersPage.mapperTable().createMapper();
        setInitialValues("role name");
        createClientMappersPage.form().setMapperType(ROLE_NAME_MAPPER);
        createClientMappersPage.form().setRole("offline_access");
        createClientMappersPage.form().setNewRole("new role");
        createClientMappersPage.form().save();
        assertAlertSuccess();
        // check
        ProtocolMapperRepresentation found = findClientMapperByName(id, "role name");
        Assert.assertEquals("oidc-role-name-mapper", found.getProtocolMapper());
        Map<String, String> config = found.getConfig();
        Assert.assertEquals("offline_access", config.get("role"));
        Assert.assertEquals("new role", config.get("new.role.name"));
    }

    @Test
    public void testUserAddress() {
        // create
        clientMappersPage.mapperTable().createMapper();
        setInitialValues("user address");
        createClientMappersPage.form().setMapperType(USERS_FULL_NAME);
        createClientMappersPage.form().save();
        assertAlertSuccess();
        // check
        ProtocolMapperRepresentation found = findClientMapperByName(id, "user address");
        Assert.assertEquals("oidc-full-name-mapper", found.getProtocolMapper());
    }

    @Test
    public void testUserFullName() {
        // create
        clientMappersPage.mapperTable().createMapper();
        setInitialValues("user full name");
        createClientMappersPage.form().setMapperType(USERS_FULL_NAME);
        createClientMappersPage.form().save();
        assertAlertSuccess();
        // check
        ProtocolMapperRepresentation found = findClientMapperByName(id, "user full name");
        Assert.assertEquals("oidc-full-name-mapper", found.getProtocolMapper());
    }

    @Test
    public void testUserAttribute() {
        // create
        clientMappersPage.mapperTable().createMapper();
        setInitialValues("user attribute");
        createClientMappersPage.form().setMapperType(USER_ATTRIBUTE);
        createClientMappersPage.form().setUserAttribute("user attribute");
        createClientMappersPage.form().setMultivalued(true);
        createClientMappersPage.form().save();
        assertAlertSuccess();
        // check
        ProtocolMapperRepresentation found = findClientMapperByName(id, "user attribute");
        Assert.assertEquals("oidc-usermodel-attribute-mapper", found.getProtocolMapper());
        Map<String, String> config = found.getConfig();
        Assert.assertEquals("true", config.get("multivalued"));
        Assert.assertEquals("user attribute", config.get("user.attribute"));
    }

    @Test
    public void testUserProperty() {
        // create
        clientMappersPage.mapperTable().createMapper();
        setInitialValues("user property");
        createClientMappersPage.form().setMapperType(USER_PROPERTY);
        createClientMappersPage.form().setProperty("property");
        createClientMappersPage.form().save();
        assertAlertSuccess();
        // check
        ProtocolMapperRepresentation found = findClientMapperByName(id, "user property");
        Assert.assertEquals("oidc-usermodel-property-mapper", found.getProtocolMapper());
        Map<String, String> config = found.getConfig();
        Assert.assertEquals("property", config.get("user.attribute"));
    }

    @Test
    public void testGroupMembership() {
        // create
        clientMappersPage.mapperTable().createMapper();
        setInitialValues("group membership");
        createClientMappersPage.form().setMapperType(GROUP_MEMBERSHIP);
        createClientMappersPage.form().setFullGroupPath(true);
        createClientMappersPage.form().save();
        assertAlertSuccess();
        // check
        ProtocolMapperRepresentation found = findClientMapperByName(id, "group membership");
        Assert.assertEquals("oidc-group-membership-mapper", found.getProtocolMapper());
        Map<String, String> config = found.getConfig();
        Assert.assertEquals("true", config.get("full.path"));
    }

    @Test
    public void testEditMapper() {
        // prepare data
        ProtocolMapperRepresentation mapper = new ProtocolMapperRepresentation();
        mapper.setName("mapper name");
        // mapper.setConsentRequired(true);
        // mapper.setConsentText("consent text");
        mapper.setProtocol("openid-connect");
        mapper.setProtocolMapper("oidc-usersessionmodel-note-mapper");
        Map<String, String> config = new HashMap<>();
        config.put("access.token.claim", "true");
        config.put("id.token.claim", "true");
        config.put("claim.name", "claim name");
        config.put("jsonType.label", "String");
        config.put("user.session.note", "session note");
        mapper.setConfig(config);
        // insert data
        testRealmResource().clients().get(id).getProtocolMappers().createMapper(mapper).close();
        // check form
        clientMapperPage.setId(id);
        String mapperId = findClientMapperByName(id, "mapper name").getId();
        clientMapperPage.setMapperId(mapperId);
        clientMapperPage.navigateTo();
        Assert.assertEquals("openid-connect", clientMapperPage.form().getProtocol());
        Assert.assertEquals(mapperId, clientMapperPage.form().getMapperId());
        Assert.assertEquals("mapper name", clientMapperPage.form().getName());
        Assert.assertEquals("User Session Note", clientMapperPage.form().getMapperType());
        Assert.assertEquals("session note", clientMapperPage.form().getUserSessionNote());
        Assert.assertEquals("claim name", clientMapperPage.form().getTokenClaimName());
        Assert.assertEquals("String", clientMapperPage.form().getClaimJSONType());
        Assert.assertTrue(clientMapperPage.form().isAddToIDToken());
        Assert.assertTrue(clientMapperPage.form().isAddToAccessToken());
        // edit
        clientMapperPage.form().setAddToAccessToken(false);
        clientMapperPage.form().save();
        assertAlertSuccess();
        // check
        Assert.assertTrue(clientMapperPage.form().isAddToIDToken());
        Assert.assertFalse(clientMapperPage.form().isAddToAccessToken());
        ProtocolMapperRepresentation rep = findClientMapperByName(id, "mapper name");
        Assert.assertEquals("false", rep.getConfig().get(INCLUDE_IN_ACCESS_TOKEN));
        Assert.assertEquals("true", rep.getConfig().get(INCLUDE_IN_ID_TOKEN));
    }

    @Test
    public void testAddBuiltin() {
        clientMappersPage.mapperTable().addBuiltin();
        clientMappersPage.mapperTable().checkBuiltinMapper("locale");
        clientMappersPage.mapperTable().clickAddSelectedBuiltinMapper();
        assertAlertSuccess();
        Assert.assertTrue("Builtin mapper \"locale\" should be present.", isMapperPresent("locale"));
        clientMappersPage.mapperTable().deleteMapper("locale");
        modalDialog.confirmDeletion();
        assertAlertSuccess();
        Assert.assertFalse("Builtin mapper \"locale\" should not be present.", isMapperPresent("locale"));
    }

    @Test
    public void testCreateMapperInvalidValues() {
        // create some mapper, so we have some existing
        clientMappersPage.mapperTable().createMapper();
        setInitialValues("hardcoded role - existing");
        createClientMappersPage.form().setMapperType(HARDCODED_ROLE);
        createClientMappersPage.form().selectRole(REALM_ROLE, "offline_access", null);
        createClientMappersPage.form().save();
        assertAlertSuccess();
        // empty mapper type
        clientMappersPage.navigateTo();
        clientMappersPage.mapperTable().createMapper();
        createClientMappersPage.form().save();
        assertAlertDanger();
        // empty name
        createClientMappersPage.form().setMapperType(HARDCODED_ROLE);
        createClientMappersPage.form().save();
        assertAlertDanger();
        createClientMappersPage.form().setName("");
        createClientMappersPage.form().save();
        assertAlertDanger();
        createClientMappersPage.form().setName("name");
        createClientMappersPage.form().setName("");
        createClientMappersPage.form().save();
        assertAlertDanger();
        // existing name
        createClientMappersPage.form().setName("hardcoded role - existing");
        createClientMappersPage.form().save();
        assertAlertDanger();
    }

    @Test
    public void testUpdateTokenClaimName() {
        clientMappersPage.mapperTable().createMapper();
        createClientMappersPage.form().setName("test");
        createClientMappersPage.form().setTokenClaimName("test");
        createClientMappersPage.form().save();
        assertAlertSuccess();
        createClientMappersPage.form().setTokenClaimName("test2");
        createClientMappersPage.form().save();
        assertAlertSuccess();
        ProtocolMapperRepresentation mapper = testRealmResource().clients().get(id).getProtocolMappers().getMappers().stream().filter(( m) -> m.getName().equals("test")).findFirst().get();
        Assert.assertThat(mapper.getConfig().get("claim.name"), CoreMatchers.is(IsEqual.equalTo("test2")));
    }
}

