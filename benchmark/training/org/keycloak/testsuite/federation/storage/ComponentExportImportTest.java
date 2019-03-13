package org.keycloak.testsuite.federation.storage;


import UserMapStorageFactory.PROVIDER_ID;
import java.io.File;
import javax.ws.rs.NotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.exportimport.ExportImportConfig;
import org.keycloak.exportimport.ExportImportManager;
import org.keycloak.representations.idm.ComponentRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.testsuite.AbstractAuthTest;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.util.RealmBuilder;


/**
 *
 *
 * @author tkyjovsk
 */
public class ComponentExportImportTest extends AbstractAuthTest {
    private static final String REALM_NAME = "exported-component";

    private File exportFile;

    @Test
    public void testSingleFile() {
        ComponentExportImportTest.clearExportImportProperties(testingClient);
        RealmRepresentation realmRep = RealmBuilder.create().name(ComponentExportImportTest.REALM_NAME).build();
        adminClient.realms().create(realmRep);
        String realmId = testRealmResource().toRepresentation().getId();
        ComponentRepresentation parentComponent = new ComponentRepresentation();
        parentComponent.setParentId(realmId);
        parentComponent.setName("parent");
        parentComponent.setSubType("subtype");
        parentComponent.setProviderId(PROVIDER_ID);
        parentComponent.setProviderType(UserStorageProvider.class.getName());
        parentComponent.setConfig(new org.keycloak.common.util.MultivaluedHashMap());
        parentComponent.getConfig().putSingle("priority", Integer.toString(0));
        parentComponent.getConfig().putSingle("attr", "value");
        String parentComponentId = addComponent(parentComponent);
        ComponentRepresentation subcomponent = new ComponentRepresentation();
        subcomponent.setParentId(parentComponentId);
        subcomponent.setName("child");
        subcomponent.setSubType("subtype2");
        subcomponent.setProviderId(PROVIDER_ID);
        subcomponent.setProviderType(UserStorageProvider.class.getName());
        subcomponent.setConfig(new org.keycloak.common.util.MultivaluedHashMap());
        subcomponent.getConfig().putSingle("priority", Integer.toString(0));
        subcomponent.getConfig().putSingle("attr", "value2");
        String subcomponentId = addComponent(subcomponent);
        final String exportFilePath = exportFile.getAbsolutePath();
        // export
        testingClient.server().run(( session) -> {
            ExportImportConfig.setProvider(SingleFileExportProviderFactory.PROVIDER_ID);
            ExportImportConfig.setFile(exportFilePath);
            ExportImportConfig.setRealmName(REALM_NAME);
            ExportImportConfig.setAction(ExportImportConfig.ACTION_EXPORT);
            new ExportImportManager(session).runExport();
        });
        testRealmResource().remove();
        try {
            testRealmResource().toRepresentation();
            Assert.fail("Realm wasn't expected to be found");
        } catch (NotFoundException nfe) {
            // Expected
        }
        // import
        testingClient.server().run(( session) -> {
            Assert.assertNull(session.realms().getRealmByName(REALM_NAME));
            ExportImportConfig.setAction(ExportImportConfig.ACTION_IMPORT);
            new ExportImportManager(session).runImport();
        });
        // Assert realm was imported
        Assert.assertNotNull(testRealmResource().toRepresentation());
        try {
            parentComponent = testRealmResource().components().component(parentComponentId).toRepresentation();
            subcomponent = testRealmResource().components().component(subcomponentId).toRepresentation();
        } catch (NotFoundException nfe) {
            Assert.fail("Components not found after import.");
        }
        Assert.assertEquals(parentComponent.getParentId(), realmId);
        Assert.assertEquals(parentComponent.getName(), "parent");
        Assert.assertEquals(parentComponent.getSubType(), "subtype");
        Assert.assertEquals(parentComponent.getProviderId(), PROVIDER_ID);
        Assert.assertEquals(parentComponent.getProviderType(), UserStorageProvider.class.getName());
        Assert.assertEquals(parentComponent.getConfig().getFirst("attr"), "value");
        Assert.assertEquals(subcomponent.getParentId(), parentComponent.getId());
        Assert.assertEquals(subcomponent.getName(), "child");
        Assert.assertEquals(subcomponent.getSubType(), "subtype2");
        Assert.assertEquals(subcomponent.getProviderId(), PROVIDER_ID);
        Assert.assertEquals(subcomponent.getProviderType(), UserStorageProvider.class.getName());
        Assert.assertEquals(subcomponent.getConfig().getFirst("attr"), "value2");
    }
}

