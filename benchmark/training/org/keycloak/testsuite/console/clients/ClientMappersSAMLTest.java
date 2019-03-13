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


import java.util.Map;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.representations.idm.ProtocolMapperRepresentation;
import org.keycloak.testsuite.console.page.clients.mappers.ClientMapper;
import org.keycloak.testsuite.console.page.clients.mappers.ClientMappers;
import org.keycloak.testsuite.console.page.clients.mappers.CreateClientMappers;


/**
 *
 *
 * @author <a href="mailto:vramik@redhat.com">Vlastislav Ramik</a>
 */
public class ClientMappersSAMLTest extends AbstractClientTest {
    private String id;

    @Page
    private ClientMappers clientMappersPage;

    @Page
    private ClientMapper clientMapperPage;

    @Page
    private CreateClientMappers createClientMappersPage;

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
        Assert.assertEquals("saml-role-name-mapper", found.getProtocolMapper());
        Map<String, String> config = found.getConfig();
        Assert.assertEquals("offline_access", config.get("role"));
        Assert.assertEquals("new role", config.get("new.role.name"));
    }

    @Test
    public void testRoleList() {
        // create
        clientMappersPage.mapperTable().createMapper();
        setInitialValues("new role list");
        createClientMappersPage.form().setMapperType(ROLE_LIST);
        createClientMappersPage.form().setRoleAttributeName("role attribute name");
        createClientMappersPage.form().setFriendlyName("friendly name");
        createClientMappersPage.form().setSamlAttributeNameFormat("URI Reference");
        createClientMappersPage.form().setSingleRoleAttribute(true);
        createClientMappersPage.form().save();
        assertAlertSuccess();
        // check
        ProtocolMapperRepresentation found = findClientMapperByName(id, "new role list");
        Assert.assertNotNull(found);
        Assert.assertEquals("saml-role-list-mapper", found.getProtocolMapper());
        Map<String, String> config = found.getConfig();
        Assert.assertEquals("role attribute name", config.get("attribute.name"));
        Assert.assertEquals("URI Reference", config.get("attribute.nameformat"));
        Assert.assertEquals("friendly name", config.get("friendly.name"));
        Assert.assertEquals("true", config.get("single"));
    }

    @Test
    public void testUserProperty() {
        // create
        clientMappersPage.mapperTable().createMapper();
        setInitialValues("user property");
        createClientMappersPage.form().setMapperType(USER_PROPERTY);
        createClientMappersPage.form().save();
        assertAlertSuccess();
        // check
        ProtocolMapperRepresentation found = findClientMapperByName(id, "user property");
        Assert.assertEquals("saml-user-property-mapper", found.getProtocolMapper());
    }

    @Test
    public void testUserSessionNote() {
        // create
        clientMappersPage.mapperTable().createMapper();
        setInitialValues("user session note");
        createClientMappersPage.form().setMapperType(USER_SESSION_NOTE);
        createClientMappersPage.form().save();
        assertAlertSuccess();
        // check
        ProtocolMapperRepresentation found = findClientMapperByName(id, "user session note");
        Assert.assertNotNull(found);
        Assert.assertEquals("saml-user-session-note-mapper", found.getProtocolMapper());
    }

    @Test
    public void testHardcodedAttribute() {
        // create
        clientMappersPage.mapperTable().createMapper();
        setInitialValues("hardcoded attribute");
        createClientMappersPage.form().setMapperType(HARDCODED_ATTRIBUTE);
        createClientMappersPage.form().setAttributeValue("attribute value");
        createClientMappersPage.form().save();
        assertAlertSuccess();
        // check
        ProtocolMapperRepresentation found = findClientMapperByName(id, "hardcoded attribute");
        Assert.assertNotNull(found);
        Assert.assertEquals("saml-hardcode-attribute-mapper", found.getProtocolMapper());
        Map<String, String> config = found.getConfig();
        Assert.assertEquals("attribute value", config.get("attribute.value"));
    }

    @Test
    public void testGroupList() {
        // create
        clientMappersPage.mapperTable().createMapper();
        setInitialValues("group list");
        createClientMappersPage.form().setMapperType(GROUP_LIST);
        createClientMappersPage.form().setGroupAttributeName("group attribute name");
        createClientMappersPage.form().setSingleGroupAttribute(true);
        createClientMappersPage.form().setFullGroupPath(true);
        createClientMappersPage.form().save();
        assertAlertSuccess();
        // check
        ProtocolMapperRepresentation found = findClientMapperByName(id, "group list");
        Assert.assertEquals("saml-group-membership-mapper", found.getProtocolMapper());
        Map<String, String> config = found.getConfig();
        Assert.assertEquals("true", config.get("full.path"));
        Assert.assertEquals("true", config.get("single"));
        Assert.assertEquals("group attribute name", config.get("attribute.name"));
    }

    @Test
    public void testHardcodedRole() {
        // create
        clientMappersPage.mapperTable().createMapper();
        setInitialValues("hardcoded role");
        createClientMappersPage.form().setMapperType(HARDCODED_ROLE_SAML);
        createClientMappersPage.form().selectRole(REALM_ROLE, "offline_access", null);
        createClientMappersPage.form().save();
        assertAlertSuccess();
        // check
        ProtocolMapperRepresentation found = findClientMapperByName(id, "hardcoded role");
        Assert.assertNotNull(found);
        Assert.assertEquals("saml-hardcode-role-mapper", found.getProtocolMapper());
        Map<String, String> config = found.getConfig();
        Assert.assertEquals(1, config.size());
        Assert.assertEquals("offline_access", config.get("role"));
    }
}

