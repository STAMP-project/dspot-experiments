/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.data.store;


import java.util.Arrays;
import java.util.List;
import org.apache.wicket.Component;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.NamespaceInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geoserver.web.data.store.panel.FileParamPanel;
import org.geoserver.web.data.store.panel.WorkspacePanel;
import org.geotools.data.property.PropertyDataStoreFactory;
import org.geotools.geopkg.GeoPkgDataStoreFactory;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test suite for {@link DataAccessNewPage}
 *
 * @author Gabriel Roldan
 */
public class DataAccessNewPageTest extends GeoServerWicketTestSupport {
    /**
     * print page structure?
     */
    private static final boolean debugMode = false;

    @Test
    public void testInitCreateNewDataStoreInvalidDataStoreFactoryName() {
        final String dataStoreFactoryDisplayName = "_invalid_";
        try {
            login();
            new DataAccessNewPage(dataStoreFactoryDisplayName);
            Assert.fail("Expected IAE on invalid datastore factory name");
        } catch (IllegalArgumentException e) {
            // hum.. change the assertion if the text changes in GeoserverApplication.properties...
            // but I still want to assert the reason for failure is the expected one..
            Assert.assertTrue(e.getMessage().startsWith("Can't find the factory"));
        }
    }

    /**
     * A kind of smoke test that only asserts the page is rendered when first loaded
     */
    @Test
    public void testPageRendersOnLoad() {
        final PropertyDataStoreFactory dataStoreFactory = new PropertyDataStoreFactory();
        final String dataStoreFactoryDisplayName = dataStoreFactory.getDisplayName();
        startPage();
        GeoServerWicketTestSupport.tester.assertLabel("dataStoreForm:storeType", dataStoreFactoryDisplayName);
        GeoServerWicketTestSupport.tester.assertLabel("dataStoreForm:storeTypeDescription", dataStoreFactory.getDescription());
        GeoServerWicketTestSupport.tester.assertComponent("dataStoreForm:workspacePanel", WorkspacePanel.class);
    }

    @Test
    public void testDefaultWorkspace() {
        startPage();
        WorkspaceInfo defaultWs = getCatalog().getDefaultWorkspace();
        GeoServerWicketTestSupport.tester.assertModelValue("dataStoreForm:workspacePanel:border:border_body:paramValue", defaultWs);
        WorkspaceInfo anotherWs = getCatalog().getFactory().createWorkspace();
        anotherWs.setName("anotherWs");
        getCatalog().add(anotherWs);
        getCatalog().setDefaultWorkspace(anotherWs);
        anotherWs = getCatalog().getDefaultWorkspace();
        startPage();
        GeoServerWicketTestSupport.tester.assertModelValue("dataStoreForm:workspacePanel:border:border_body:paramValue", anotherWs);
    }

    @Test
    public void testDefaultNamespace() {
        // final String namespacePath =
        // "dataStoreForm:parameters:1:parameterPanel:border:border_body:paramValue";
        final String namespacePath = "dataStoreForm:parametersPanel:parameters:1:parameterPanel:paramValue";
        startPage();
        NamespaceInfo defaultNs = getCatalog().getDefaultNamespace();
        GeoServerWicketTestSupport.tester.assertModelValue(namespacePath, defaultNs.getURI());
    }

    @Test
    public void testDataStoreParametersAreCreated() {
        startPage();
        List parametersListViewValues = Arrays.asList(new Object[]{ "directory", "namespace" });
        GeoServerWicketTestSupport.tester.assertListView("dataStoreForm:parametersPanel:parameters", parametersListViewValues);
    }

    /**
     * Make sure in case the DataStore has a "namespace" parameter, its value is initialized to the
     * NameSpaceInfo one that matches the workspace
     */
    @Test
    public void testInitCreateNewDataStoreSetsNamespaceParam() {
        final Catalog catalog = getGeoServerApplication().getCatalog();
        final AbstractDataAccessPage page = startPage();
        page.get(null);
        // final NamespaceInfo assignedNamespace = (NamespaceInfo) page.parametersMap
        // .get(AbstractDataAccessPage.NAMESPACE_PROPERTY);
        // final NamespaceInfo expectedNamespace = catalog.getDefaultNamespace();
        // 
        // assertEquals(expectedNamespace, assignedNamespace);
    }

    @Test
    public void testGeoPackagePage() {
        final String displayName = new GeoPkgDataStoreFactory().getDisplayName();
        login();
        final AbstractDataAccessPage page = new DataAccessNewPage(displayName);
        GeoServerWicketTestSupport.tester.startPage(page);
        // tester.debugComponentTrees();
        // the "database" key is the second, should be a file panel
        Component component = GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("dataStoreForm:parametersPanel:parameters:1:parameterPanel");
        Assert.assertThat(component, CoreMatchers.instanceOf(FileParamPanel.class));
    }
}

