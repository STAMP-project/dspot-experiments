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
package org.keycloak.testsuite.adduser;


import Constants.AUTHZ_UMA_AUTHORIZATION;
import Pbkdf2Sha256PasswordHashProviderFactory.ID;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.util.JsonSerialization;
import org.keycloak.wildfly.adduser.AddUser;


/**
 *
 *
 * @author <a href="mailto:mabartos@redhat.com">Martin Bartos</a>
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class AddUserTest extends AbstractKeycloakTest {
    @ArquillianResource
    private ContainerController controller;

    @Test
    public void addUserTest() throws Exception {
        final String username = "addusertest-admin";
        final String realmName = "master";
        final String configDir = System.getProperty("auth.server.config.dir");
        Assert.assertThat("AuthServer config directory is NULL !!", configDir, IsNull.notNullValue());
        String authServerQualifier = suiteContext.getAuthServerInfo().getQualifier();
        Assert.assertThat("Qualifier of AuthServer is empty or NULL !!", authServerQualifier, IsNot.not(isEmptyOrNullString()));
        Assert.assertThat("Controller isn't running.", controller.isStarted(authServerQualifier), Is.is(true));
        AddUser.main(new String[]{ "-u", username, "-p", "password", "--sc", configDir });
        // Read keycloak-add-user.json
        List<RealmRepresentation> realms = JsonSerialization.readValue(new FileInputStream(new File(configDir, "keycloak-add-user.json")), new TypeReference<List<RealmRepresentation>>() {});
        Assert.assertThat("File 'keycloak-add-user.json' is empty.", ((realms.size()) > 0), Is.is(true));
        // -----------------Get-Indexes-------------------//
        int realmIndex = getRealmIndex(realmName, realms);
        Assert.assertThat((("Realm " + realmName) + " not found."), realmIndex, Is.is(IsNot.not((-1))));
        int userIndex = getUserIndex(username, realms.get(realmIndex).getUsers());
        Assert.assertThat((("User " + username) + " not found"), userIndex, Is.is(IsNot.not((-1))));
        UserRepresentation user = realms.get(realmIndex).getUsers().get(userIndex);
        Assert.assertThat("Username from Json file is wrong.", user.getUsername(), Is.is(username));
        // ------------------Credentials-----------------------------//
        Assert.assertThat("User Credentials are NULL", user.getCredentials().get(0), IsNull.notNullValue());
        CredentialRepresentation credentials = user.getCredentials().get(0);
        Assert.assertThat("User Credentials have wrong Algorithm.", credentials.getAlgorithm(), Is.is(ID));
        Assert.assertThat("User Credentials have wrong Hash Iterations", credentials.getHashIterations(), Is.is(100000));
        // ------------------Restart--Container---------------------//
        controller.stop(authServerQualifier);
        controller.start(authServerQualifier);
        RealmResource realmResource = getAdminClient().realm(realmName);
        Assert.assertThat("Realm resource is NULL !!", realmResource, IsNull.notNullValue());
        user = realmResource.users().search(username).get(0);
        Assert.assertThat("Username is wrong.", user.getUsername(), Is.is(username));
        UserResource userResource = realmResource.users().get(user.getId());
        Assert.assertThat("User resource is NULL !!", userResource, IsNull.notNullValue());
        // --------------Roles-----------------------//
        try {
            List<RoleRepresentation> realmRoles = userResource.roles().realmLevel().listAll();
            assertRoles(realmRoles, "admin", "offline_access", AUTHZ_UMA_AUTHORIZATION);
            List<ClientRepresentation> clients = realmResource.clients().findAll();
            String accountId = null;
            for (ClientRepresentation c : clients) {
                if (c.getClientId().equals("account")) {
                    accountId = c.getId();
                }
            }
            List<RoleRepresentation> accountRoles = userResource.roles().clientLevel(accountId).listAll();
            assertRoles(accountRoles, "view-profile", "manage-account");
        } finally {
            userResource.remove();
        }
    }
}

