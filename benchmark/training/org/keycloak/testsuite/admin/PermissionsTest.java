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


import AdminRoles.CREATE_CLIENT;
import AdminRoles.QUERY_CLIENTS;
import AdminRoles.QUERY_REALMS;
import AdminRoles.QUERY_USERS;
import Constants.REALM_MANAGEMENT_CLIENT_ID;
import Resource.CLIENT;
import Resource.EVENTS;
import Resource.IDENTITY_PROVIDER;
import Resource.REALM;
import Resource.USER;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import javax.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.AuthorizationResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.models.AdminRoles;
import org.keycloak.representations.KeyStoreConfig;
import org.keycloak.representations.idm.AuthenticationExecutionInfoRepresentation;
import org.keycloak.representations.idm.AuthenticationExecutionRepresentation;
import org.keycloak.representations.idm.AuthenticationFlowRepresentation;
import org.keycloak.representations.idm.AuthenticatorConfigRepresentation;
import org.keycloak.representations.idm.ClientInitialAccessCreatePresentation;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.ClientScopeRepresentation;
import org.keycloak.representations.idm.ComponentRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.IdentityProviderMapperRepresentation;
import org.keycloak.representations.idm.IdentityProviderRepresentation;
import org.keycloak.representations.idm.PartialImportRepresentation;
import org.keycloak.representations.idm.ProtocolMapperRepresentation;
import org.keycloak.representations.idm.RealmEventsConfigRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RequiredActionProviderRepresentation;
import org.keycloak.representations.idm.RequiredActionProviderSimpleRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.authorization.PolicyRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ResourceServerRepresentation;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.Assert;
import org.keycloak.testsuite.util.ClientBuilder;
import org.keycloak.testsuite.util.CredentialBuilder;
import org.keycloak.testsuite.util.FederatedIdentityBuilder;
import org.keycloak.testsuite.util.GreenMailRule;
import org.keycloak.testsuite.util.IdentityProviderBuilder;
import org.keycloak.testsuite.util.RealmBuilder;
import org.keycloak.testsuite.util.UserBuilder;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.keycloak.models.AdminRoles.MANAGE_USERS;
import static org.keycloak.models.AdminRoles.VIEW_REALM;
import static org.keycloak.models.AdminRoles.VIEW_USERS;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class PermissionsTest extends AbstractKeycloakTest {
    private static final String REALM_NAME = "permissions-test";

    private Map<String, Keycloak> clients = new HashMap<>();

    @Rule
    public GreenMailRule greenMailRule = new GreenMailRule();

    @Test
    public void realms() throws Exception {
        // Check returned realms
        invoke((RealmResource realm) -> {
            clients.get("master-none").realms().findAll();
        }, clients.get("none"), false);
        invoke((RealmResource realm) -> {
            clients.get("none").realms().findAll();
        }, clients.get("none"), false);
        Assert.assertNames(clients.get("master-admin").realms().findAll(), "master", PermissionsTest.REALM_NAME, "realm2");
        Assert.assertNames(clients.get(AdminRoles.REALM_ADMIN).realms().findAll(), PermissionsTest.REALM_NAME);
        Assert.assertNames(clients.get("REALM2").realms().findAll(), "realm2");
        // Check realm only contains name if missing view realm permission
        List<RealmRepresentation> realms = clients.get(AdminRoles.VIEW_USERS).realms().findAll();
        Assert.assertNames(realms, PermissionsTest.REALM_NAME);
        assertGettersEmpty(realms.get(0));
        realms = clients.get(AdminRoles.VIEW_REALM).realms().findAll();
        Assert.assertNames(realms, PermissionsTest.REALM_NAME);
        assertNotNull(realms.get(0).getAccessTokenLifespan());
        // Check the same when access with users from 'master' realm
        realms = clients.get(("master-" + (VIEW_USERS))).realms().findAll();
        Assert.assertNames(realms, PermissionsTest.REALM_NAME);
        assertGettersEmpty(realms.get(0));
        realms = clients.get(("master-" + (VIEW_REALM))).realms().findAll();
        Assert.assertNames(realms, PermissionsTest.REALM_NAME);
        assertNotNull(realms.get(0).getAccessTokenLifespan());
        // Create realm
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                clients.get("master-admin").realms().create(RealmBuilder.create().name("master").build());
            }
        }, adminClient, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                clients.get(("master-" + (MANAGE_USERS))).realms().create(RealmBuilder.create().name("master").build());
            }
        }, adminClient, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                clients.get(AdminRoles.REALM_ADMIN).realms().create(RealmBuilder.create().name("master").build());
            }
        }, adminClient, false);
        // Get realm
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.toRepresentation();
            }
        }, REALM, false, true);
        assertGettersEmpty(clients.get(QUERY_REALMS).realm(PermissionsTest.REALM_NAME).toRepresentation());
        // this should pass given that users granted with "query" roles are allowed to access the realm with limited access
        for (String role : AdminRoles.ALL_QUERY_ROLES) {
            invoke(( realm) -> clients.get(role).realms().realm(REALM_NAME).toRepresentation(), clients.get(role), true);
        }
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.update(new RealmRepresentation());
            }
        }, REALM, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.pushRevocation();
            }
        }, REALM, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.deleteSession("nosuch");
            }
        }, USER, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.getClientSessionStats();
            }
        }, REALM, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.getDefaultGroups();
            }
        }, REALM, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.addDefaultGroup("nosuch");
            }
        }, REALM, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.removeDefaultGroup("nosuch");
            }
        }, REALM, true);
        GroupRepresentation newGroup = new GroupRepresentation();
        newGroup.setName("sample");
        adminClient.realm(PermissionsTest.REALM_NAME).groups().add(newGroup);
        GroupRepresentation group = adminClient.realms().realm(PermissionsTest.REALM_NAME).getGroupByPath("sample");
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.getGroupByPath("sample");
            }
        }, USER, false);
        adminClient.realms().realm(PermissionsTest.REALM_NAME).groups().group(group.getId()).remove();
        invoke(new PermissionsTest.InvocationWithResponse() {
            public void invoke(RealmResource realm, AtomicReference<Response> response) {
                response.set(realm.testLDAPConnection("nosuch", "nosuch", "nosuch", "nosuch", "nosuch", "nosuch"));
            }
        }, REALM, true);
        invoke(new PermissionsTest.InvocationWithResponse() {
            public void invoke(RealmResource realm, AtomicReference<Response> response) {
                response.set(realm.partialImport(new PartialImportRepresentation()));
            }
        }, REALM, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clearRealmCache();
            }
        }, REALM, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clearUserCache();
            }
        }, REALM, true);
        // Delete realm
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                clients.get("master-admin").realms().realm("nosuch").remove();
            }
        }, adminClient, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                clients.get("REALM2").realms().realm(PermissionsTest.REALM_NAME).remove();
            }
        }, adminClient, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                clients.get(AdminRoles.MANAGE_USERS).realms().realm(PermissionsTest.REALM_NAME).remove();
            }
        }, adminClient, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                clients.get(AdminRoles.REALM_ADMIN).realms().realm(PermissionsTest.REALM_NAME).remove();
            }
        }, adminClient, true);
        // Revert realm removal
        recreatePermissionRealm();
    }

    @Test
    public void realmLogoutAll() {
        PermissionsTest.Invocation invocation = new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.logoutAll();
            }
        };
        invoke(invocation, clients.get("master-none"), false);
        invoke(invocation, clients.get("master-view-realm"), false);
        invoke(invocation, clients.get("REALM2"), false);
        invoke(invocation, clients.get("none"), false);
        invoke(invocation, clients.get("view-users"), false);
        invoke(invocation, clients.get("manage-realm"), false);
        invoke(invocation, clients.get("master-manage-realm"), false);
        invoke(invocation, clients.get("manage-users"), true);
        invoke(invocation, clients.get("master-manage-users"), true);
    }

    @Test
    public void events() {
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.getRealmEventsConfig();
            }
        }, EVENTS, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.updateRealmEventsConfig(new RealmEventsConfigRepresentation());
            }
        }, EVENTS, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.getEvents();
            }
        }, EVENTS, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.getAdminEvents();
            }
        }, EVENTS, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clearEvents();
            }
        }, EVENTS, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clearAdminEvents();
            }
        }, EVENTS, true);
    }

    @Test
    public void attackDetection() {
        UserRepresentation newUser = new UserRepresentation();
        newUser.setUsername("attacked");
        newUser.setEnabled(true);
        adminClient.realms().realm(PermissionsTest.REALM_NAME).users().create(newUser);
        UserRepresentation user = adminClient.realms().realm(PermissionsTest.REALM_NAME).users().search("attacked").get(0);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.attackDetection().bruteForceUserStatus(user.getId());
            }
        }, USER, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.attackDetection().clearBruteForceForUser(user.getId());
            }
        }, USER, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.attackDetection().clearAllBruteForce();
            }
        }, USER, true);
        adminClient.realms().realm(PermissionsTest.REALM_NAME).users().get(user.getId()).remove();
    }

    @Test
    public void clients() {
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().findAll();
            }
        }, CLIENT, false, true);
        List<ClientRepresentation> l = clients.get(QUERY_CLIENTS).realm(PermissionsTest.REALM_NAME).clients().findAll();
        assertThat(l, Matchers.empty());
        l = clients.get(AdminRoles.VIEW_CLIENTS).realm(PermissionsTest.REALM_NAME).clients().findAll();
        assertThat(l, Matchers.not(Matchers.empty()));
        ClientRepresentation client = l.get(0);
        invoke(new PermissionsTest.InvocationWithResponse() {
            @Override
            public void invoke(RealmResource realm, AtomicReference<Response> response) {
                response.set(clients.get(QUERY_USERS).realm(PermissionsTest.REALM_NAME).clients().create(client));
            }
        }, clients.get(QUERY_USERS), false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                clients.get(QUERY_USERS).realm(PermissionsTest.REALM_NAME).clients().get(client.getId()).toRepresentation();
            }
        }, clients.get(QUERY_USERS), false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                clients.get(QUERY_USERS).realm(PermissionsTest.REALM_NAME).clients().get(client.getId()).update(client);
            }
        }, clients.get(QUERY_USERS), false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                clients.get(QUERY_USERS).realm(PermissionsTest.REALM_NAME).clients().get(client.getId()).remove();
            }
        }, clients.get(QUERY_USERS), false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.convertClientDescription("blahblah");
            }
        }, CLIENT, true);
        invoke(new PermissionsTest.InvocationWithResponse() {
            public void invoke(RealmResource realm, AtomicReference<Response> response) {
                response.set(realm.clients().create(ClientBuilder.create().clientId("foo").build()));
            }
        }, CLIENT, true);
        ClientRepresentation foo = adminClient.realms().realm(PermissionsTest.REALM_NAME).clients().findByClientId("foo").get(0);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).toRepresentation();
            }
        }, CLIENT, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).getInstallationProvider("nosuch");
            }
        }, CLIENT, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).update(foo);
            }
        }, CLIENT, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).remove();
                realm.clients().create(foo);
                ClientRepresentation temp = realm.clients().findByClientId("foo").get(0);
                foo.setId(temp.getId());
            }
        }, CLIENT, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).generateNewSecret();
            }
        }, CLIENT, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).regenerateRegistrationAccessToken();
            }
        }, CLIENT, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).getSecret();
            }
        }, CLIENT, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).getServiceAccountUser();
            }
        }, CLIENT, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).pushRevocation();
            }
        }, CLIENT, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).getApplicationSessionCount();
            }
        }, CLIENT, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).getUserSessions(0, 100);
            }
        }, CLIENT, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).getOfflineSessionCount();
            }
        }, CLIENT, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).getOfflineUserSessions(0, 100);
            }
        }, CLIENT, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).registerNode(Collections.<String, String>emptyMap());
            }
        }, CLIENT, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).unregisterNode("nosuch");
            }
        }, CLIENT, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).testNodesAvailable();
            }
        }, CLIENT, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).getCertficateResource("nosuch").generate();
            }
        }, CLIENT, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).getCertficateResource("nosuch").generateAndGetKeystore(new KeyStoreConfig());
            }
        }, CLIENT, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).getCertficateResource("nosuch").getKeyInfo();
            }
        }, CLIENT, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).getCertficateResource("nosuch").getKeystore(new KeyStoreConfig());
            }
        }, CLIENT, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).getCertficateResource("nosuch").uploadJks(new MultipartFormDataOutput());
            }
        }, CLIENT, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).getCertficateResource("nosuch").uploadJksCertificate(new MultipartFormDataOutput());
            }
        }, CLIENT, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).getProtocolMappers().createMapper(Collections.EMPTY_LIST);
            }
        }, CLIENT, true);
        invoke(new PermissionsTest.InvocationWithResponse() {
            public void invoke(RealmResource realm, AtomicReference<Response> response) {
                response.set(realm.clients().get(foo.getId()).getProtocolMappers().createMapper(new ProtocolMapperRepresentation()));
            }
        }, CLIENT, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).getProtocolMappers().getMapperById("nosuch");
            }
        }, CLIENT, false, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).getProtocolMappers().getMappers();
            }
        }, CLIENT, false, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).getProtocolMappers().getMappersPerProtocol("nosuch");
            }
        }, CLIENT, false, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).getProtocolMappers().update("nosuch", new ProtocolMapperRepresentation());
            }
        }, CLIENT, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).getProtocolMappers().delete("nosuch");
            }
        }, CLIENT, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).getScopeMappings().getAll();
            }
        }, CLIENT, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).getScopeMappings().realmLevel().listAll();
            }
        }, CLIENT, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).getScopeMappings().realmLevel().listEffective();
            }
        }, CLIENT, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).getScopeMappings().realmLevel().listAvailable();
            }
        }, CLIENT, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).getScopeMappings().realmLevel().add(Collections.<RoleRepresentation>emptyList());
            }
        }, CLIENT, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).getScopeMappings().realmLevel().remove(Collections.<RoleRepresentation>emptyList());
            }
        }, CLIENT, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get("nosuch").roles().list();
            }
        }, CLIENT, false, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).roles().create(new RoleRepresentation());
            }
        }, CLIENT, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).roles().get("nosuch").toRepresentation();
            }
        }, CLIENT, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).roles().deleteRole("nosuch");
            }
        }, CLIENT, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).roles().get("nosuch").update(new RoleRepresentation());
            }
        }, CLIENT, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).roles().get("nosuch").addComposites(Collections.<RoleRepresentation>emptyList());
            }
        }, CLIENT, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).roles().get("nosuch").deleteComposites(Collections.<RoleRepresentation>emptyList());
            }
        }, CLIENT, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).roles().get("nosuch").getRoleComposites();
            }
        }, CLIENT, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).roles().get("nosuch").getRealmRoleComposites();
            }
        }, CLIENT, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).roles().get("nosuch").getClientRoleComposites("nosuch");
            }
        }, CLIENT, false);
        // users with query-client role should be able to query flows so the client detail page can be rendered successfully when fine-grained permissions are enabled.
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.flows().getFlows();
            }
        }, clients.get(QUERY_CLIENTS), true);
    }

    @Test
    public void clientScopes() {
        invoke((RealmResource realm) -> {
            realm.clientScopes().findAll();
        }, CLIENT, false, true);
        invoke((RealmResource realm,AtomicReference<Response> response) -> {
            ClientScopeRepresentation scope = new ClientScopeRepresentation();
            scope.setName("scope");
            response.set(realm.clientScopes().create(scope));
        }, CLIENT, true);
        ClientScopeRepresentation scope = adminClient.realms().realm(PermissionsTest.REALM_NAME).clientScopes().findAll().get(0);
        invoke((RealmResource realm) -> {
            realm.clientScopes().get(scope.getId()).toRepresentation();
        }, CLIENT, false);
        invoke((RealmResource realm) -> {
            realm.clientScopes().get(scope.getId()).update(scope);
        }, CLIENT, true);
        invoke((RealmResource realm) -> {
            realm.clientScopes().get(scope.getId()).remove();
            realm.clientScopes().create(scope);
        }, CLIENT, true);
        invoke((RealmResource realm) -> {
            realm.clientScopes().get(scope.getId()).getProtocolMappers().getMappers();
        }, CLIENT, false, true);
        invoke((RealmResource realm) -> {
            realm.clientScopes().get(scope.getId()).getProtocolMappers().getMappersPerProtocol("nosuch");
        }, CLIENT, false, true);
        invoke((RealmResource realm) -> {
            realm.clientScopes().get(scope.getId()).getProtocolMappers().getMapperById("nosuch");
        }, CLIENT, false, true);
        invoke((RealmResource realm) -> {
            realm.clientScopes().get(scope.getId()).getProtocolMappers().update("nosuch", new ProtocolMapperRepresentation());
        }, CLIENT, true);
        invoke((RealmResource realm,AtomicReference<Response> response) -> {
            response.set(realm.clientScopes().get(scope.getId()).getProtocolMappers().createMapper(new ProtocolMapperRepresentation()));
        }, CLIENT, true);
        invoke((RealmResource realm) -> {
            realm.clientScopes().get(scope.getId()).getProtocolMappers().createMapper(Collections.<ProtocolMapperRepresentation>emptyList());
        }, CLIENT, true);
        invoke((RealmResource realm) -> {
            realm.clientScopes().get(scope.getId()).getProtocolMappers().delete("nosuch");
        }, CLIENT, true);
        invoke((RealmResource realm) -> {
            realm.clientScopes().get(scope.getId()).getScopeMappings().getAll();
        }, CLIENT, false);
        invoke((RealmResource realm) -> {
            realm.clientScopes().get(scope.getId()).getScopeMappings().realmLevel().listAll();
        }, CLIENT, false);
        invoke((RealmResource realm) -> {
            realm.clientScopes().get(scope.getId()).getScopeMappings().realmLevel().listAvailable();
        }, CLIENT, false);
        invoke((RealmResource realm) -> {
            realm.clientScopes().get(scope.getId()).getScopeMappings().realmLevel().listEffective();
        }, CLIENT, false);
        invoke((RealmResource realm) -> {
            realm.clientScopes().get(scope.getId()).getScopeMappings().realmLevel().add(Collections.<RoleRepresentation>emptyList());
        }, CLIENT, true);
        invoke((RealmResource realm) -> {
            realm.clientScopes().get(scope.getId()).getScopeMappings().realmLevel().remove(Collections.<RoleRepresentation>emptyList());
        }, CLIENT, true);
        ClientRepresentation realmAccessClient = adminClient.realms().realm(PermissionsTest.REALM_NAME).clients().findByClientId(REALM_MANAGEMENT_CLIENT_ID).get(0);
        invoke((RealmResource realm) -> {
            realm.clientScopes().get(scope.getId()).getScopeMappings().clientLevel(realmAccessClient.getId()).listAll();
        }, CLIENT, false);
        invoke((RealmResource realm) -> {
            realm.clientScopes().get(scope.getId()).getScopeMappings().clientLevel(realmAccessClient.getId()).listAvailable();
        }, CLIENT, false);
        invoke((RealmResource realm) -> {
            realm.clientScopes().get(scope.getId()).getScopeMappings().clientLevel(realmAccessClient.getId()).listEffective();
        }, CLIENT, false);
        invoke((RealmResource realm) -> {
            realm.clientScopes().get(scope.getId()).getScopeMappings().clientLevel(realmAccessClient.getId()).add(Collections.<RoleRepresentation>emptyList());
        }, CLIENT, true);
        invoke((RealmResource realm) -> {
            realm.clientScopes().get(scope.getId()).getScopeMappings().clientLevel(realmAccessClient.getId()).remove(Collections.<RoleRepresentation>emptyList());
        }, CLIENT, true);
        // this should throw forbidden as "query-users" role isn't enough
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                clients.get(QUERY_USERS).realm(PermissionsTest.REALM_NAME).clientScopes().findAll();
            }
        }, clients.get(QUERY_USERS), false);
    }

    @Test
    public void clientInitialAccess() {
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clientInitialAccess().list();
            }
        }, CLIENT, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clientInitialAccess().create(new ClientInitialAccessCreatePresentation());
            }
        }, CLIENT, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clientInitialAccess().delete("nosuch");
            }
        }, CLIENT, true);
    }

    @Test
    public void clientAuthorization() {
        ClientRepresentation newClient = new ClientRepresentation();
        newClient.setClientId("foo-authz");
        adminClient.realms().realm(PermissionsTest.REALM_NAME).clients().create(newClient);
        ClientRepresentation foo = adminClient.realms().realm(PermissionsTest.REALM_NAME).clients().findByClientId("foo-authz").get(0);
        invoke(new PermissionsTest.InvocationWithResponse() {
            public void invoke(RealmResource realm, AtomicReference<Response> response) {
                foo.setServiceAccountsEnabled(true);
                foo.setAuthorizationServicesEnabled(true);
                realm.clients().get(foo.getId()).update(foo);
            }
        }, CLIENT, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.clients().get(foo.getId()).authorization().getSettings();
            }
        }, AUTHORIZATION, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                AuthorizationResource authorization = realm.clients().get(foo.getId()).authorization();
                ResourceServerRepresentation settings = authorization.getSettings();
                authorization.update(settings);
            }
        }, AUTHORIZATION, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                AuthorizationResource authorization = realm.clients().get(foo.getId()).authorization();
                authorization.resources().resources();
            }
        }, AUTHORIZATION, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                AuthorizationResource authorization = realm.clients().get(foo.getId()).authorization();
                authorization.scopes().scopes();
            }
        }, AUTHORIZATION, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                AuthorizationResource authorization = realm.clients().get(foo.getId()).authorization();
                authorization.policies().policies();
            }
        }, AUTHORIZATION, false);
        invoke(new PermissionsTest.InvocationWithResponse() {
            public void invoke(RealmResource realm, AtomicReference<Response> response) {
                AuthorizationResource authorization = realm.clients().get(foo.getId()).authorization();
                response.set(authorization.resources().create(new ResourceRepresentation("Test", Collections.emptySet())));
            }
        }, AUTHORIZATION, true);
        invoke(new PermissionsTest.InvocationWithResponse() {
            public void invoke(RealmResource realm, AtomicReference<Response> response) {
                AuthorizationResource authorization = realm.clients().get(foo.getId()).authorization();
                response.set(authorization.scopes().create(new ScopeRepresentation("Test")));
            }
        }, AUTHORIZATION, true);
        invoke(new PermissionsTest.InvocationWithResponse() {
            public void invoke(RealmResource realm, AtomicReference<Response> response) {
                AuthorizationResource authorization = realm.clients().get(foo.getId()).authorization();
                PolicyRepresentation representation = new PolicyRepresentation();
                representation.setName("Test PermissionsTest");
                representation.setType("js");
                HashMap<String, String> config = new HashMap<>();
                config.put("code", "");
                representation.setConfig(config);
                response.set(authorization.policies().create(representation));
            }
        }, AUTHORIZATION, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                AuthorizationResource authorization = realm.clients().get(foo.getId()).authorization();
                authorization.resources().resource("nosuch").update(new ResourceRepresentation());
            }
        }, AUTHORIZATION, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                AuthorizationResource authorization = realm.clients().get(foo.getId()).authorization();
                authorization.scopes().scope("nosuch").update(new ScopeRepresentation());
            }
        }, AUTHORIZATION, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                AuthorizationResource authorization = realm.clients().get(foo.getId()).authorization();
                authorization.policies().policy("nosuch").update(new PolicyRepresentation());
            }
        }, AUTHORIZATION, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                AuthorizationResource authorization = realm.clients().get(foo.getId()).authorization();
                authorization.resources().resource("nosuch").remove();
            }
        }, AUTHORIZATION, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                AuthorizationResource authorization = realm.clients().get(foo.getId()).authorization();
                authorization.scopes().scope("nosuch").remove();
            }
        }, AUTHORIZATION, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                AuthorizationResource authorization = realm.clients().get(foo.getId()).authorization();
                authorization.policies().policy("nosuch").remove();
            }
        }, AUTHORIZATION, true);
    }

    @Test
    public void roles() {
        RoleRepresentation newRole = new RoleRepresentation();
        newRole.setName("sample-role");
        adminClient.realm(PermissionsTest.REALM_NAME).roles().create(newRole);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.roles().list();
            }
        }, REALM, false, true);
        // this should throw forbidden as "create-client" role isn't enough
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                clients.get(CREATE_CLIENT).realm(PermissionsTest.REALM_NAME).roles().list();
            }
        }, clients.get(CREATE_CLIENT), false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.roles().get("sample-role").toRepresentation();
            }
        }, REALM, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.roles().get("sample-role").update(newRole);
            }
        }, REALM, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.roles().create(new RoleRepresentation());
            }
        }, REALM, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.roles().deleteRole("sample-role");
                // need to recreate for other tests
                realm.roles().create(newRole);
            }
        }, REALM, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.roles().get("sample-role").getRoleComposites();
            }
        }, REALM, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.roles().get("sample-role").addComposites(Collections.<RoleRepresentation>emptyList());
            }
        }, REALM, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.roles().get("sample-role").deleteComposites(Collections.<RoleRepresentation>emptyList());
            }
        }, REALM, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.roles().get("sample-role").getRoleComposites();
            }
        }, REALM, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.roles().get("sample-role").getRealmRoleComposites();
            }
        }, REALM, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.roles().get("sample-role").getClientRoleComposites("nosuch");
            }
        }, REALM, false);
        adminClient.realms().realm(PermissionsTest.REALM_NAME).roles().deleteRole("sample-role");
    }

    @Test
    public void flows() throws Exception {
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.flows().getFormProviders();
            }
        }, REALM, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.flows().getAuthenticatorProviders();
            }
        }, REALM, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.flows().getClientAuthenticatorProviders();
            }
        }, REALM, false, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.flows().getFormActionProviders();
            }
        }, REALM, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.flows().getFlows();
            }
        }, REALM, false, true);
        invoke(new PermissionsTest.InvocationWithResponse() {
            public void invoke(RealmResource realm, AtomicReference<Response> response) {
                response.set(realm.flows().createFlow(new AuthenticationFlowRepresentation()));
            }
        }, REALM, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.flows().getFlow("nosuch");
            }
        }, REALM, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.flows().deleteFlow("nosuch");
            }
        }, REALM, true);
        invoke(new PermissionsTest.InvocationWithResponse() {
            public void invoke(RealmResource realm, AtomicReference<Response> response) {
                response.set(realm.flows().copy("nosuch", Collections.<String, String>emptyMap()));
            }
        }, REALM, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.flows().addExecutionFlow("nosuch", Collections.<String, String>emptyMap());
            }
        }, REALM, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.flows().addExecution("nosuch", Collections.<String, String>emptyMap());
            }
        }, REALM, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.flows().getExecutions("nosuch");
            }
        }, REALM, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.flows().updateExecutions("nosuch", new AuthenticationExecutionInfoRepresentation());
            }
        }, REALM, true);
        invoke(new PermissionsTest.InvocationWithResponse() {
            public void invoke(RealmResource realm, AtomicReference<Response> response) {
                AuthenticationExecutionRepresentation rep = new AuthenticationExecutionRepresentation();
                rep.setAuthenticator("auth-cookie");
                rep.setRequirement("OPTIONAL");
                response.set(realm.flows().addExecution(rep));
            }
        }, REALM, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.flows().raisePriority("nosuch");
            }
        }, REALM, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.flows().lowerPriority("nosuch");
            }
        }, REALM, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.flows().removeExecution("nosuch");
            }
        }, REALM, true);
        invoke(new PermissionsTest.InvocationWithResponse() {
            public void invoke(RealmResource realm, AtomicReference<Response> response) {
                response.set(realm.flows().newExecutionConfig("nosuch", new AuthenticatorConfigRepresentation()));
            }
        }, REALM, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.flows().getAuthenticatorConfig("nosuch");
            }
        }, REALM, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.flows().getUnregisteredRequiredActions();
            }
        }, REALM, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.flows().registerRequiredAction(new RequiredActionProviderSimpleRepresentation());
            }
        }, REALM, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.flows().getRequiredActions();
            }
        }, REALM, false, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.flows().getRequiredAction("nosuch");
            }
        }, REALM, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.flows().removeRequiredAction("nosuch");
            }
        }, REALM, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.flows().updateRequiredAction("nosuch", new RequiredActionProviderRepresentation());
            }
        }, REALM, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.flows().getAuthenticatorConfigDescription("nosuch");
            }
        }, REALM, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.flows().getPerClientConfigDescription();
            }
        }, REALM, false, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.flows().getAuthenticatorConfig("nosuch");
            }
        }, REALM, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.flows().removeAuthenticatorConfig("nosuch");
            }
        }, REALM, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.flows().updateAuthenticatorConfig("nosuch", new AuthenticatorConfigRepresentation());
            }
        }, REALM, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                clients.get(AdminRoles.VIEW_REALM).realm(PermissionsTest.REALM_NAME).flows().getPerClientConfigDescription();
                clients.get(AdminRoles.VIEW_REALM).realm(PermissionsTest.REALM_NAME).flows().getClientAuthenticatorProviders();
                clients.get(AdminRoles.VIEW_REALM).realm(PermissionsTest.REALM_NAME).flows().getRequiredActions();
            }
        }, adminClient, true);
        // Re-create realm
        adminClient.realm(PermissionsTest.REALM_NAME).remove();
        recreatePermissionRealm();
    }

    @Test
    public void rolesById() {
        RoleRepresentation newRole = new RoleRepresentation();
        newRole.setName("role-by-id");
        adminClient.realm(PermissionsTest.REALM_NAME).roles().create(newRole);
        RoleRepresentation role = adminClient.realm(PermissionsTest.REALM_NAME).roles().get("role-by-id").toRepresentation();
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.rolesById().getRole(role.getId());
            }
        }, REALM, false, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.rolesById().updateRole(role.getId(), role);
            }
        }, REALM, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.rolesById().deleteRole(role.getId());
                // need to recreate for other tests
                realm.roles().create(newRole);
                RoleRepresentation temp = realm.roles().get("role-by-id").toRepresentation();
                role.setId(temp.getId());
            }
        }, REALM, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.rolesById().getRoleComposites(role.getId());
            }
        }, REALM, false, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.rolesById().addComposites(role.getId(), Collections.<RoleRepresentation>emptyList());
            }
        }, REALM, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.rolesById().deleteComposites(role.getId(), Collections.<RoleRepresentation>emptyList());
            }
        }, REALM, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.rolesById().getRoleComposites(role.getId());
            }
        }, REALM, false, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.rolesById().getRealmRoleComposites(role.getId());
            }
        }, REALM, false, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.rolesById().getClientRoleComposites(role.getId(), "nosuch");
            }
        }, REALM, false, true);
        adminClient.realm(PermissionsTest.REALM_NAME).roles().deleteRole("role-by-id");
    }

    @Test
    public void groups() {
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.groups().groups();
            }
        }, USER, false);
        invoke(new PermissionsTest.InvocationWithResponse() {
            public void invoke(RealmResource realm, AtomicReference<Response> response) {
                GroupRepresentation group = new GroupRepresentation();
                group.setName("mygroup");
                response.set(realm.groups().add(group));
            }
        }, USER, true);
        GroupRepresentation group = adminClient.realms().realm(PermissionsTest.REALM_NAME).getGroupByPath("mygroup");
        ClientRepresentation realmAccessClient = adminClient.realms().realm(PermissionsTest.REALM_NAME).clients().findByClientId(REALM_MANAGEMENT_CLIENT_ID).get(0);
        // this should throw forbidden as "create-client" role isn't enough
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                clients.get(CREATE_CLIENT).realm(PermissionsTest.REALM_NAME).groups().groups();
            }
        }, clients.get(CREATE_CLIENT), false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.groups().group(group.getId()).toRepresentation();
            }
        }, USER, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.groups().group(group.getId()).update(group);
            }
        }, USER, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.groups().group(group.getId()).members(0, 100);
            }
        }, USER, false);
        invoke(new PermissionsTest.InvocationWithResponse() {
            public void invoke(RealmResource realm, AtomicReference<Response> response) {
                GroupRepresentation subgroup = new GroupRepresentation();
                subgroup.setName("sub");
                response.set(realm.groups().group(group.getId()).subGroup(subgroup));
            }
        }, USER, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.groups().group(group.getId()).roles().getAll();
            }
        }, USER, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.groups().group(group.getId()).roles().realmLevel().listAll();
            }
        }, USER, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.groups().group(group.getId()).roles().realmLevel().listEffective();
            }
        }, USER, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.groups().group(group.getId()).roles().realmLevel().listAvailable();
            }
        }, USER, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.groups().group(group.getId()).roles().realmLevel().add(Collections.<RoleRepresentation>emptyList());
            }
        }, USER, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.groups().group(group.getId()).roles().realmLevel().remove(Collections.<RoleRepresentation>emptyList());
            }
        }, USER, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.groups().group(group.getId()).roles().clientLevel(realmAccessClient.getId()).listAll();
            }
        }, USER, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.groups().group(group.getId()).roles().clientLevel(realmAccessClient.getId()).listEffective();
            }
        }, USER, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.groups().group(group.getId()).roles().clientLevel(realmAccessClient.getId()).listAvailable();
            }
        }, USER, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.groups().group(group.getId()).roles().clientLevel(realmAccessClient.getId()).add(Collections.<RoleRepresentation>emptyList());
            }
        }, USER, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.groups().group(group.getId()).roles().clientLevel(realmAccessClient.getId()).remove(Collections.<RoleRepresentation>emptyList());
            }
        }, USER, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.groups().group(group.getId()).remove();
                group.setId(null);
                realm.groups().add(group);
                GroupRepresentation temp = realm.getGroupByPath("mygroup");
                group.setId(temp.getId());
            }
        }, USER, true);
    }

    // Permissions for impersonation tested in ImpersonationTest
    @Test
    public void users() {
        invoke(new PermissionsTest.InvocationWithResponse() {
            public void invoke(RealmResource realm, AtomicReference<Response> response) {
                response.set(realm.users().create(UserBuilder.create().username("testuser").build()));
            }
        }, USER, true);
        UserRepresentation user = adminClient.realms().realm(PermissionsTest.REALM_NAME).users().search("testuser").get(0);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.users().get(user.getId()).remove();
                realm.users().create(user);
                UserRepresentation temp = realm.users().search("testuser").get(0);
                user.setId(temp.getId());
            }
        }, USER, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.users().get(user.getId()).toRepresentation();
            }
        }, USER, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.users().get(user.getId()).update(user);
            }
        }, USER, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.users().count();
            }
        }, USER, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.users().get(user.getId()).getUserSessions();
            }
        }, USER, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.users().get(user.getId()).getOfflineSessions("nosuch");
            }
        }, USER, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.users().get(user.getId()).getFederatedIdentity();
            }
        }, USER, false);
        invoke(new PermissionsTest.InvocationWithResponse() {
            public void invoke(RealmResource realm, AtomicReference<Response> response) {
                response.set(realm.users().get(user.getId()).addFederatedIdentity("nosuch", FederatedIdentityBuilder.create().identityProvider("nosuch").userId("nosuch").userName("nosuch").build()));
            }
        }, USER, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.users().get(user.getId()).removeFederatedIdentity("nosuch");
            }
        }, USER, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.users().get(user.getId()).getConsents();
            }
        }, USER, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.users().get(user.getId()).revokeConsent("testclient");
            }
        }, USER, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.users().get(user.getId()).logout();
            }
        }, USER, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.users().get(user.getId()).resetPassword(CredentialBuilder.create().password("password").build());
            }
        }, USER, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.users().get(user.getId()).removeTotp();
            }
        }, USER, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.users().get(user.getId()).resetPasswordEmail();
            }
        }, USER, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.users().get(user.getId()).executeActionsEmail(Collections.<String>emptyList());
            }
        }, USER, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.users().get(user.getId()).sendVerifyEmail();
            }
        }, USER, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.users().get(user.getId()).groups();
            }
        }, USER, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.users().get(user.getId()).leaveGroup("nosuch");
            }
        }, USER, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.users().get(user.getId()).joinGroup("nosuch");
            }
        }, USER, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.users().get(user.getId()).roles().getAll();
            }
        }, USER, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.users().get(user.getId()).roles().realmLevel().listAll();
            }
        }, USER, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.users().get(user.getId()).roles().realmLevel().listAvailable();
            }
        }, USER, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.users().get(user.getId()).roles().realmLevel().listEffective();
            }
        }, USER, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.users().get(user.getId()).roles().realmLevel().add(Collections.<RoleRepresentation>emptyList());
            }
        }, USER, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.users().get(user.getId()).roles().realmLevel().remove(Collections.<RoleRepresentation>emptyList());
            }
        }, USER, true);
        ClientRepresentation realmAccessClient = adminClient.realms().realm(PermissionsTest.REALM_NAME).clients().findByClientId(REALM_MANAGEMENT_CLIENT_ID).get(0);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.users().get(user.getId()).roles().clientLevel(realmAccessClient.getId()).listAll();
            }
        }, USER, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.users().get(user.getId()).roles().clientLevel(realmAccessClient.getId()).listAvailable();
            }
        }, USER, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.users().get(user.getId()).roles().clientLevel(realmAccessClient.getId()).listEffective();
            }
        }, USER, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.users().get(user.getId()).roles().clientLevel(realmAccessClient.getId()).add(Collections.<RoleRepresentation>emptyList());
            }
        }, USER, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.users().get(user.getId()).roles().clientLevel(realmAccessClient.getId()).remove(Collections.<RoleRepresentation>emptyList());
            }
        }, USER, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.users().search("foo", 0, 1);
            }
        }, USER, false);
        // this should throw forbidden as "query-client" role isn't enough
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                clients.get(QUERY_CLIENTS).realm(PermissionsTest.REALM_NAME).users().list();
            }
        }, clients.get(QUERY_CLIENTS), false);
        invoke(new PermissionsTest.InvocationWithResponse() {
            @Override
            public void invoke(RealmResource realm, AtomicReference<Response> response) {
                response.set(clients.get(QUERY_CLIENTS).realm(PermissionsTest.REALM_NAME).users().create(user));
            }
        }, clients.get(QUERY_CLIENTS), false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                clients.get(QUERY_CLIENTS).realm(PermissionsTest.REALM_NAME).users().search("test");
            }
        }, clients.get(QUERY_CLIENTS), false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.users().get(user.getId()).toRepresentation();
            }
        }, clients.get(QUERY_CLIENTS), false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.users().get(user.getId()).remove();
            }
        }, clients.get(QUERY_CLIENTS), false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.users().get(user.getId()).update(user);
            }
        }, clients.get(QUERY_CLIENTS), false);
        // users with query-user role should be able to query required actions so the user detail page can be rendered successfully when fine-grained permissions are enabled.
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.flows().getRequiredActions();
            }
        }, clients.get(QUERY_USERS), true);
        // users with query-user role should be able to query clients so the user detail page can be rendered successfully when fine-grained permissions are enabled.
        // if the admin wants to restrict the clients that an user can see he can define permissions for these clients
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                clients.get(QUERY_USERS).realm(PermissionsTest.REALM_NAME).clients().findAll();
            }
        }, clients.get(QUERY_USERS), true);
    }

    @Test
    public void identityProviders() {
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.identityProviders().findAll();
            }
        }, IDENTITY_PROVIDER, false);
        invoke(new PermissionsTest.InvocationWithResponse() {
            public void invoke(RealmResource realm, AtomicReference<Response> response) {
                response.set(realm.identityProviders().create(IdentityProviderBuilder.create().providerId("nosuch").displayName("nosuch-foo").alias("foo").build()));
            }
        }, IDENTITY_PROVIDER, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.identityProviders().get("nosuch").toRepresentation();
            }
        }, IDENTITY_PROVIDER, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.identityProviders().get("nosuch").update(new IdentityProviderRepresentation());
            }
        }, IDENTITY_PROVIDER, true);
        invoke(new PermissionsTest.InvocationWithResponse() {
            public void invoke(RealmResource realm, AtomicReference<Response> response) {
                response.set(realm.identityProviders().get("nosuch").export("saml"));
            }
        }, IDENTITY_PROVIDER, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.identityProviders().get("nosuch").remove();
            }
        }, IDENTITY_PROVIDER, true);
        invoke(new PermissionsTest.InvocationWithResponse() {
            public void invoke(RealmResource realm, AtomicReference<Response> response) {
                response.set(realm.identityProviders().get("nosuch").addMapper(new IdentityProviderMapperRepresentation()));
            }
        }, IDENTITY_PROVIDER, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.identityProviders().get("nosuch").delete("nosuch");
            }
        }, IDENTITY_PROVIDER, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.identityProviders().get("nosuch").getMappers();
            }
        }, IDENTITY_PROVIDER, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.identityProviders().get("nosuch").getMapperById("nosuch");
            }
        }, IDENTITY_PROVIDER, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.identityProviders().get("nosuch").getMapperTypes();
            }
        }, IDENTITY_PROVIDER, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.identityProviders().importFrom(Collections.<String, Object>emptyMap());
            }
        }, IDENTITY_PROVIDER, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.identityProviders().importFrom(new MultipartFormDataOutput());
            }
        }, IDENTITY_PROVIDER, true);
    }

    @Test
    public void components() {
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.components().query();
            }
        }, REALM, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.components().query("nosuch");
            }
        }, REALM, false);
        invoke(new PermissionsTest.InvocationWithResponse() {
            public void invoke(RealmResource realm, AtomicReference<Response> response) {
                response.set(realm.components().add(new ComponentRepresentation()));
            }
        }, REALM, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.components().component("nosuch").toRepresentation();
            }
        }, REALM, false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.components().component("nosuch").update(new ComponentRepresentation());
            }
        }, REALM, true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.components().component("nosuch").remove();
            }
        }, REALM, true);
    }

    @Test
    public void partialExport() {
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.partialExport(false, false);
            }
        }, clients.get("view-realm"), true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.partialExport(true, true);
            }
        }, clients.get("multi"), true);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.partialExport(true, false);
            }
        }, clients.get("view-realm"), false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.partialExport(false, true);
            }
        }, clients.get("view-realm"), false);
        invoke(new PermissionsTest.Invocation() {
            public void invoke(RealmResource realm) {
                realm.partialExport(false, false);
            }
        }, clients.get("none"), false);
    }

    public interface Invocation {
        void invoke(RealmResource realm);
    }

    public interface InvocationWithResponse {
        void invoke(RealmResource realm, AtomicReference<Response> response);
    }
}

