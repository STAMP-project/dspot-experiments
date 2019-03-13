/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.data.store;


import org.apache.wicket.MarkupContainer;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geoserver.web.data.store.panel.WorkspacePanel;
import org.geotools.data.postgis.PostgisNGDataStoreFactory;
import org.geotools.jdbc.JDBCDataStoreFactory;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test suite for {@link DataAccessNewPage}, using {@link PostgisNGDataStoreFactory}
 */
public class DataAccessNewPageDatabaseTest extends GeoServerWicketTestSupport {
    final JDBCDataStoreFactory dataStoreFactory = new PostgisNGDataStoreFactory();

    @Test
    public void testPageRendersOnLoad() {
        startPage();
        GeoServerWicketTestSupport.tester.assertLabel("dataStoreForm:storeType", dataStoreFactory.getDisplayName());
        GeoServerWicketTestSupport.tester.assertLabel("dataStoreForm:storeTypeDescription", dataStoreFactory.getDescription());
        GeoServerWicketTestSupport.tester.assertComponent("dataStoreForm:workspacePanel", WorkspacePanel.class);
    }

    @Test
    public void testDbtypeParameterHidden() {
        startPage();
        // check the dbtype field is not visible
        MarkupContainer container = ((MarkupContainer) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("dataStoreForm:parametersPanel:parameters:0")));
        Assert.assertEquals("dbtype", container.getDefaultModelObject());
        Assert.assertFalse(container.get("parameterPanel").isVisible());
    }
}

