package org.keycloak.testsuite.admin.partialexport;


import org.junit.Assert;
import org.junit.Test;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.admin.AbstractAdminTest;


/**
 *
 *
 * @author <a href="mailto:mstrukel@redhat.com">Marko Strukelj</a>
 */
public class PartialExportTest extends AbstractAdminTest {
    private static final String EXPORT_TEST_REALM = "partial-export-test";

    @Test
    public void testExport() {
        // exportGroupsAndRoles == false, exportClients == false
        RealmRepresentation rep = adminClient.realm(PartialExportTest.EXPORT_TEST_REALM).partialExport(false, false);
        org.keycloak.testsuite.Assert.assertNull("Default groups are empty", rep.getDefaultGroups());
        org.keycloak.testsuite.Assert.assertNull("Groups are empty", rep.getGroups());
        org.keycloak.testsuite.Assert.assertNotNull("Default roles not empty", rep.getDefaultRoles());
        checkDefaultRoles(rep.getDefaultRoles());
        org.keycloak.testsuite.Assert.assertNull("Realm and client roles are empty", rep.getRoles());
        org.keycloak.testsuite.Assert.assertNull("Clients are empty", rep.getClients());
        checkScopeMappings(rep.getScopeMappings(), true);
        org.keycloak.testsuite.Assert.assertNull("Client scope mappings empty", rep.getClientScopeMappings());
        // exportGroupsAndRoles == true, exportClients == false
        rep = adminClient.realm(PartialExportTest.EXPORT_TEST_REALM).partialExport(true, false);
        org.keycloak.testsuite.Assert.assertNull("Default groups are empty", rep.getDefaultGroups());
        org.keycloak.testsuite.Assert.assertNotNull("Groups not empty", rep.getGroups());
        checkGroups(rep.getGroups());
        org.keycloak.testsuite.Assert.assertNotNull("Default roles not empty", rep.getDefaultRoles());
        checkDefaultRoles(rep.getDefaultRoles());
        org.keycloak.testsuite.Assert.assertNotNull("Realm and client roles not empty", rep.getRoles());
        org.keycloak.testsuite.Assert.assertNotNull("Realm roles not empty", rep.getRoles().getRealm());
        checkRealmRoles(rep.getRoles().getRealm());
        org.keycloak.testsuite.Assert.assertNull("Client roles are empty", rep.getRoles().getClient());
        org.keycloak.testsuite.Assert.assertNull("Clients are empty", rep.getClients());
        checkScopeMappings(rep.getScopeMappings(), true);
        org.keycloak.testsuite.Assert.assertNull("Client scope mappings empty", rep.getClientScopeMappings());
        // exportGroupsAndRoles == false, exportClients == true
        rep = adminClient.realm(PartialExportTest.EXPORT_TEST_REALM).partialExport(false, true);
        org.keycloak.testsuite.Assert.assertNull("Default groups are empty", rep.getDefaultGroups());
        org.keycloak.testsuite.Assert.assertNull("Groups are empty", rep.getGroups());
        org.keycloak.testsuite.Assert.assertNotNull("Default roles not empty", rep.getDefaultRoles());
        checkDefaultRoles(rep.getDefaultRoles());
        org.keycloak.testsuite.Assert.assertNull("Realm and client roles are empty", rep.getRoles());
        org.keycloak.testsuite.Assert.assertNotNull("Clients not empty", rep.getClients());
        checkClients(rep.getClients());
        checkScopeMappings(rep.getScopeMappings(), false);
        checkClientScopeMappings(rep.getClientScopeMappings());
        // exportGroupsAndRoles == true, exportClients == true
        rep = adminClient.realm(PartialExportTest.EXPORT_TEST_REALM).partialExport(true, true);
        org.keycloak.testsuite.Assert.assertNull("Default groups are empty", rep.getDefaultGroups());
        org.keycloak.testsuite.Assert.assertNotNull("Groups not empty", rep.getGroups());
        checkGroups(rep.getGroups());
        org.keycloak.testsuite.Assert.assertNotNull("Default roles not empty", rep.getDefaultRoles());
        checkDefaultRoles(rep.getDefaultRoles());
        org.keycloak.testsuite.Assert.assertNotNull("Realm and client roles not empty", rep.getRoles());
        org.keycloak.testsuite.Assert.assertNotNull("Realm roles not empty", rep.getRoles().getRealm());
        checkRealmRoles(rep.getRoles().getRealm());
        org.keycloak.testsuite.Assert.assertNotNull("Client roles not empty", rep.getRoles().getClient());
        checkClientRoles(rep.getRoles().getClient());
        org.keycloak.testsuite.Assert.assertNotNull("Clients not empty", rep.getClients());
        checkClients(rep.getClients());
        checkScopeMappings(rep.getScopeMappings(), false);
        checkClientScopeMappings(rep.getClientScopeMappings());
        // check that secrets are masked
        checkSecretsAreMasked(rep);
    }
}

