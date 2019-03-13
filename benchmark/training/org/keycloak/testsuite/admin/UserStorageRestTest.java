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


import AuthenticationExecutionModel.Requirement.ALTERNATIVE;
import AuthenticationExecutionModel.Requirement.DISABLED;
import AuthenticationExecutionModel.Requirement.REQUIRED;
import KerberosConstants.ALLOW_KERBEROS_AUTHENTICATION;
import LDAPConstants.BIND_CREDENTIAL;
import LDAPConstants.BIND_DN;
import LDAPConstants.CUSTOM_USER_SEARCH_FILTER;
import OperationType.UPDATE;
import ResourceType.AUTH_EXECUTION;
import java.util.List;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.representations.idm.AuthenticationExecutionInfoRepresentation;
import org.keycloak.representations.idm.ComponentRepresentation;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.testsuite.util.AdminEventPaths;


/**
 *
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
/* @Test
public void testProviderFactories() {
List<UserFederationProviderFactoryRepresentation> providerFactories = userFederation().getProviderFactories();
Assert.assertNames(providerFactories, "ldap", "kerberos", "dummy", "dummy-configurable");

// Builtin provider without properties
UserFederationProviderFactoryRepresentation ldapProvider = userFederation().getProviderFactory("ldap");
Assert.assertEquals(ldapProvider.getId(), "ldap");
Assert.assertEquals(0, ldapProvider.getOptions().size());

// Configurable through the "old-way" options
UserFederationProviderFactoryRepresentation dummyProvider = userFederation().getProviderFactory("dummy");
Assert.assertEquals(dummyProvider.getId(), "dummy");
Assert.assertNames(new LinkedList<>(dummyProvider.getOptions()), "important.config");

// Configurable through the "new-way" ConfiguredProvider
UserFederationProviderFactoryRepresentation dummyConfiguredProvider = userFederation().getProviderFactory("dummy-configurable");
Assert.assertEquals(dummyConfiguredProvider.getId(), "dummy-configurable");
Assert.assertTrue(dummyConfiguredProvider.getOptions() == null || dummyConfiguredProvider.getOptions().isEmpty());
Assert.assertEquals("Dummy User Federation Provider Help Text", dummyConfiguredProvider.getHelpText());
Assert.assertEquals(2, dummyConfiguredProvider.getProperties().size());
Assert.assertProviderConfigProperty(dummyConfiguredProvider.getProperties().get(0), "prop1", "Prop1", "prop1Default", "Prop1 HelpText", ProviderConfigProperty.STRING_TYPE);
Assert.assertProviderConfigProperty(dummyConfiguredProvider.getProperties().get(1), "prop2", "Prop2", "true", "Prop2 HelpText", ProviderConfigProperty.BOOLEAN_TYPE);

try {
userFederation().getProviderFactory("not-existent");
Assert.fail("Not expected to find not-existent provider");
} catch (NotFoundException nfe) {
// Expected
}
}

private UserFederationProvidersResource userFederation() {
return null;//realm.userFederation();
}


@Test
public void testCreateProvider() {
// create provider without configuration and displayName
UserFederationProviderRepresentation dummyRep1 = UserFederationProviderBuilder.create()
.providerName("dummy")
.displayName("")
.priority(2)
.fullSyncPeriod(1000)
.changedSyncPeriod(500)
.lastSync(123)
.build();

String id1 = createUserFederationProvider(dummyRep1);

// create provider with configuration and displayName
UserFederationProviderRepresentation dummyRep2 = UserFederationProviderBuilder.create()
.providerName("dummy")
.displayName("dn1")
.priority(1)
.configProperty("prop1", "prop1Val")
.configProperty("prop2", "true")
.build();
String id2 = createUserFederationProvider(dummyRep2);

// Assert provider instances available
assertFederationProvider(userFederation().get(id1).toBriefRepresentation(), id1, id1, "dummy", 2, 1000, 500, 123);
assertFederationProvider(userFederation().get(id2).toBriefRepresentation(), id2, "dn1", "dummy", 1, -1, -1, -1, "prop1", "prop1Val", "prop2", "true");

// Assert sorted
List<UserFederationProviderRepresentation> providerInstances = userFederation().getProviderInstances();
Assert.assertEquals(providerInstances.size(), 2);
assertFederationProvider(providerInstances.get(0), id2, "dn1", "dummy", 1, -1, -1, -1, "prop1", "prop1Val", "prop2", "true");
assertFederationProvider(providerInstances.get(1), id1, id1, "dummy", 2, 1000, 500, 123);

// Remove providers
removeUserFederationProvider(id1);
removeUserFederationProvider(id2);
}








@Test (expected = NotFoundException.class)
public void testLookupNotExistentProvider() {
userFederation().get("not-existent").toBriefRepresentation();
}


@Test
public void testSyncFederationProvider() {
// create provider
UserFederationProviderRepresentation dummyRep1 = UserFederationProviderBuilder.create()
.providerName("dummy")
.build();
String id1 = createUserFederationProvider(dummyRep1);


// Sync with unknown action shouldn't pass
try {
userFederation().get(id1).syncUsers("unknown");
Assert.fail("Not expected to sync with unknown action");
} catch (NotFoundException nfe) {
// Expected
}

// Assert sync didn't happen
Assert.assertEquals(-1, userFederation().get(id1).toBriefRepresentation().getLastSync());

// Sync and assert it happened
SynchronizationResultRepresentation syncResult = userFederation().get(id1).syncUsers("triggerFullSync");
Assert.assertEquals("0 imported users, 0 updated users", syncResult.getStatus());

Map<String, Object> eventRep = new HashMap<>();
eventRep.put("action", "triggerFullSync");
assertAdminEvents.assertEvent(realmId, OperationType.ACTION, AdminEventPaths.userFederationResourcePath(id1) + "/sync", eventRep, ResourceType.USER_FEDERATION_PROVIDER);

int fullSyncTime = userFederation().get(id1).toBriefRepresentation().getLastSync();
Assert.assertTrue(fullSyncTime > 0);

// Changed sync
setTimeOffset(50);
syncResult = userFederation().get(id1).syncUsers("triggerChangedUsersSync");

eventRep.put("action", "triggerChangedUsersSync");
assertAdminEvents.assertEvent(realmId, OperationType.ACTION, AdminEventPaths.userFederationResourcePath(id1) + "/sync", eventRep, ResourceType.USER_FEDERATION_PROVIDER);

Assert.assertEquals("0 imported users, 0 updated users", syncResult.getStatus());
int changedSyncTime = userFederation().get(id1).toBriefRepresentation().getLastSync();
Assert.assertTrue(fullSyncTime + 50 <= changedSyncTime);

// Cleanup
resetTimeOffset();
removeUserFederationProvider(id1);
}



private void assertFederationProvider(UserFederationProviderRepresentation rep, String id, String displayName, String providerName,
int priority, int fullSyncPeriod, int changeSyncPeriod, int lastSync,
String... config) {
Assert.assertEquals(id, rep.getId());
Assert.assertEquals(displayName, rep.getDisplayName());
Assert.assertEquals(providerName, rep.getProviderName());
Assert.assertEquals(priority, rep.getPriority());
Assert.assertEquals(fullSyncPeriod, rep.getFullSyncPeriod());
Assert.assertEquals(changeSyncPeriod, rep.getChangedSyncPeriod());
Assert.assertEquals(lastSync, rep.getLastSync());

Assert.assertMap(rep.getConfig(), config);
}
 */
public class UserStorageRestTest extends AbstractAdminTest {
    @Test
    public void testKerberosAuthenticatorEnabledAutomatically() {
        // Assert kerberos authenticator DISABLED
        AuthenticationExecutionInfoRepresentation kerberosExecution = findKerberosExecution();
        org.keycloak.testsuite.Assert.assertEquals(kerberosExecution.getRequirement(), DISABLED.toString());
        // create LDAP provider with kerberos
        ComponentRepresentation ldapRep = new ComponentRepresentation();
        ldapRep.setName("ldap2");
        ldapRep.setProviderId("ldap");
        ldapRep.setProviderType(UserStorageProvider.class.getName());
        ldapRep.setConfig(new org.keycloak.common.util.MultivaluedHashMap());
        ldapRep.getConfig().putSingle("priority", Integer.toString(2));
        ldapRep.getConfig().putSingle(ALLOW_KERBEROS_AUTHENTICATION, "true");
        String id = createComponent(ldapRep);
        // Assert kerberos authenticator ALTERNATIVE
        kerberosExecution = findKerberosExecution();
        org.keycloak.testsuite.Assert.assertEquals(kerberosExecution.getRequirement(), ALTERNATIVE.toString());
        // Switch kerberos authenticator to DISABLED
        kerberosExecution.setRequirement(DISABLED.toString());
        realm.flows().updateExecutions("browser", kerberosExecution);
        assertAdminEvents.assertEvent(realmId, UPDATE, AdminEventPaths.authUpdateExecutionPath("browser"), kerberosExecution, AUTH_EXECUTION);
        // update LDAP provider with kerberos
        ldapRep = realm.components().component(id).toRepresentation();
        realm.components().component(id).update(ldapRep);
        assertAdminEvents.clear();
        // Assert kerberos authenticator ALTERNATIVE
        kerberosExecution = findKerberosExecution();
        org.keycloak.testsuite.Assert.assertEquals(kerberosExecution.getRequirement(), ALTERNATIVE.toString());
        // Cleanup
        kerberosExecution.setRequirement(DISABLED.toString());
        realm.flows().updateExecutions("browser", kerberosExecution);
        assertAdminEvents.assertEvent(realmId, UPDATE, AdminEventPaths.authUpdateExecutionPath("browser"), kerberosExecution, AUTH_EXECUTION);
        removeComponent(id);
    }

    @Test
    public void testKerberosAuthenticatorChangedOnlyIfDisabled() {
        // Change kerberos to REQUIRED
        AuthenticationExecutionInfoRepresentation kerberosExecution = findKerberosExecution();
        kerberosExecution.setRequirement(REQUIRED.toString());
        realm.flows().updateExecutions("browser", kerberosExecution);
        assertAdminEvents.assertEvent(realmId, UPDATE, AdminEventPaths.authUpdateExecutionPath("browser"), kerberosExecution, AUTH_EXECUTION);
        // create LDAP provider with kerberos
        ComponentRepresentation ldapRep = new ComponentRepresentation();
        ldapRep.setName("ldap2");
        ldapRep.setProviderId("ldap");
        ldapRep.setProviderType(UserStorageProvider.class.getName());
        ldapRep.setConfig(new org.keycloak.common.util.MultivaluedHashMap());
        ldapRep.getConfig().putSingle("priority", Integer.toString(2));
        ldapRep.getConfig().putSingle(ALLOW_KERBEROS_AUTHENTICATION, "true");
        String id = createComponent(ldapRep);
        // Assert kerberos authenticator still REQUIRED
        kerberosExecution = findKerberosExecution();
        org.keycloak.testsuite.Assert.assertEquals(kerberosExecution.getRequirement(), REQUIRED.toString());
        // update LDAP provider with kerberos
        ldapRep = realm.components().component(id).toRepresentation();
        realm.components().component(id).update(ldapRep);
        assertAdminEvents.clear();
        // Assert kerberos authenticator still REQUIRED
        kerberosExecution = findKerberosExecution();
        org.keycloak.testsuite.Assert.assertEquals(kerberosExecution.getRequirement(), REQUIRED.toString());
        // Cleanup
        kerberosExecution.setRequirement(DISABLED.toString());
        realm.flows().updateExecutions("browser", kerberosExecution);
        assertAdminEvents.assertEvent(realmId, UPDATE, AdminEventPaths.authUpdateExecutionPath("browser"), kerberosExecution, AUTH_EXECUTION);
        removeComponent(id);
    }

    // KEYCLOAK-4438
    @Test
    public void testKerberosAuthenticatorDisabledWhenProviderRemoved() {
        // Assert kerberos authenticator DISABLED
        AuthenticationExecutionInfoRepresentation kerberosExecution = findKerberosExecution();
        org.keycloak.testsuite.Assert.assertEquals(kerberosExecution.getRequirement(), DISABLED.toString());
        // create LDAP provider with kerberos
        ComponentRepresentation ldapRep = new ComponentRepresentation();
        ldapRep.setName("ldap2");
        ldapRep.setProviderId("ldap");
        ldapRep.setProviderType(UserStorageProvider.class.getName());
        ldapRep.setConfig(new org.keycloak.common.util.MultivaluedHashMap());
        ldapRep.getConfig().putSingle("priority", Integer.toString(2));
        ldapRep.getConfig().putSingle(ALLOW_KERBEROS_AUTHENTICATION, "true");
        String id = createComponent(ldapRep);
        // Assert kerberos authenticator ALTERNATIVE
        kerberosExecution = findKerberosExecution();
        org.keycloak.testsuite.Assert.assertEquals(kerberosExecution.getRequirement(), ALTERNATIVE.toString());
        // Remove LDAP provider
        realm.components().component(id).remove();
        // Assert kerberos authenticator DISABLED
        kerberosExecution = findKerberosExecution();
        org.keycloak.testsuite.Assert.assertEquals(kerberosExecution.getRequirement(), DISABLED.toString());
        // Add kerberos provider
        ComponentRepresentation kerberosRep = new ComponentRepresentation();
        kerberosRep.setName("kerberos");
        kerberosRep.setProviderId("kerberos");
        kerberosRep.setProviderType(UserStorageProvider.class.getName());
        kerberosRep.setConfig(new org.keycloak.common.util.MultivaluedHashMap());
        kerberosRep.getConfig().putSingle("priority", Integer.toString(2));
        id = createComponent(kerberosRep);
        // Assert kerberos authenticator ALTERNATIVE
        kerberosExecution = findKerberosExecution();
        org.keycloak.testsuite.Assert.assertEquals(kerberosExecution.getRequirement(), ALTERNATIVE.toString());
        // Switch kerberos authenticator to REQUIRED
        kerberosExecution.setRequirement(REQUIRED.toString());
        realm.flows().updateExecutions("browser", kerberosExecution);
        // Remove Kerberos provider
        realm.components().component(id).remove();
        // Assert kerberos authenticator DISABLED
        kerberosExecution = findKerberosExecution();
        org.keycloak.testsuite.Assert.assertEquals(kerberosExecution.getRequirement(), DISABLED.toString());
    }

    @Test
    public void testValidateAndCreateLdapProvider() {
        // Invalid filter
        ComponentRepresentation ldapRep = new ComponentRepresentation();
        ldapRep.setName("ldap2");
        ldapRep.setProviderId("ldap");
        ldapRep.setProviderType(UserStorageProvider.class.getName());
        ldapRep.setConfig(new org.keycloak.common.util.MultivaluedHashMap());
        ldapRep.getConfig().putSingle("priority", Integer.toString(2));
        ldapRep.getConfig().putSingle(CUSTOM_USER_SEARCH_FILTER, "dc=something");
        Response resp = realm.components().add(ldapRep);
        org.keycloak.testsuite.Assert.assertEquals(400, resp.getStatus());
        resp.close();
        // Invalid filter
        ldapRep.getConfig().putSingle(CUSTOM_USER_SEARCH_FILTER, "(dc=something");
        resp = realm.components().add(ldapRep);
        org.keycloak.testsuite.Assert.assertEquals(400, resp.getStatus());
        resp.close();
        // Invalid filter
        ldapRep.getConfig().putSingle(CUSTOM_USER_SEARCH_FILTER, "dc=something)");
        resp = realm.components().add(ldapRep);
        org.keycloak.testsuite.Assert.assertEquals(400, resp.getStatus());
        resp.close();
        // Assert nothing created so far
        org.keycloak.testsuite.Assert.assertTrue(realm.components().query(realmId, UserStorageProvider.class.getName()).isEmpty());
        assertAdminEvents.assertEmpty();
        // Valid filter. Creation success
        ldapRep.getConfig().putSingle(CUSTOM_USER_SEARCH_FILTER, "(dc=something)");
        String id1 = createComponent(ldapRep);
        // Missing filter is ok too. Creation success
        ComponentRepresentation ldapRep2 = new ComponentRepresentation();
        ldapRep2.setName("ldap3");
        ldapRep2.setProviderId("ldap");
        ldapRep2.setProviderType(UserStorageProvider.class.getName());
        ldapRep2.setConfig(new org.keycloak.common.util.MultivaluedHashMap());
        ldapRep2.getConfig().putSingle("priority", Integer.toString(2));
        ldapRep2.getConfig().putSingle(BIND_DN, "cn=manager");
        ldapRep2.getConfig().putSingle(BIND_CREDENTIAL, "password");
        String id2 = createComponent(ldapRep2);
        // Assert both providers created
        List<ComponentRepresentation> providerInstances = realm.components().query(realmId, UserStorageProvider.class.getName());
        org.keycloak.testsuite.Assert.assertEquals(providerInstances.size(), 2);
        // Cleanup
        removeComponent(id1);
        removeComponent(id2);
    }

    @Test
    public void testUpdateProvider() {
        ComponentRepresentation ldapRep = new ComponentRepresentation();
        ldapRep.setName("ldap2");
        ldapRep.setProviderId("ldap");
        ldapRep.setProviderType(UserStorageProvider.class.getName());
        ldapRep.setConfig(new org.keycloak.common.util.MultivaluedHashMap());
        ldapRep.getConfig().putSingle("priority", Integer.toString(2));
        ldapRep.getConfig().putSingle(BIND_DN, "cn=manager");
        ldapRep.getConfig().putSingle(BIND_CREDENTIAL, "password");
        String id = createComponent(ldapRep);
        // Assert update with invalid filter should fail
        ldapRep = realm.components().component(id).toRepresentation();
        ldapRep.getConfig().putSingle(CUSTOM_USER_SEARCH_FILTER, "(dc=something2");
        ldapRep.getConfig().putSingle(BIND_DN, "cn=manager-updated");
        try {
            realm.components().component(id).update(ldapRep);
            org.keycloak.testsuite.Assert.fail("Not expected to successfull update");
        } catch (BadRequestException bre) {
            // Expected
        }
        // Assert nothing was updated
        assertFederationProvider(realm.components().component(id).toRepresentation(), id, "ldap2", "ldap", BIND_DN, "cn=manager", BIND_CREDENTIAL, "**********");
        // Change filter to be valid
        ldapRep.getConfig().putSingle(CUSTOM_USER_SEARCH_FILTER, "(dc=something2)");
        realm.components().component(id).update(ldapRep);
        assertAdminEvents.clear();
        // Assert updated successfully
        ldapRep = realm.components().component(id).toRepresentation();
        assertFederationProvider(ldapRep, id, "ldap2", "ldap", BIND_DN, "cn=manager-updated", BIND_CREDENTIAL, "**********", CUSTOM_USER_SEARCH_FILTER, "(dc=something2)");
        // Assert update displayName
        ldapRep.setName("ldap2");
        realm.components().component(id).update(ldapRep);
        assertFederationProvider(realm.components().component(id).toRepresentation(), id, "ldap2", "ldap", BIND_DN, "cn=manager-updated", BIND_CREDENTIAL, "**********", CUSTOM_USER_SEARCH_FILTER, "(dc=something2)");
        // Cleanup
        removeComponent(id);
    }
}

