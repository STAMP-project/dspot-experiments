/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.data;


import MockData.BUILDINGS;
import MockData.CITE_PREFIX;
import java.util.List;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.web.FormTestPage;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class ConfirmRemovalPanelTest extends GeoServerWicketTestSupport {
    @Test
    public void testRemoveWorkspace() {
        setupPanel(getCatalog().getWorkspaceByName(CITE_PREFIX));
        // print(tester.getLastRenderedPage(), true, true);
        GeoServerWicketTestSupport.tester.assertRenderedPage(FormTestPage.class);
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
        GeoServerWicketTestSupport.tester.assertLabel("form:panel:removedObjects:storesRemoved:stores", "cite");
        GeoServerWicketTestSupport.tester.assertLabel("form:panel:removedObjects:stylesRemoved:styles", "lakes");
        String layers = GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("form:panel:removedObjects:layersRemoved:layers").getDefaultModelObjectAsString();
        String[] layerArray = layers.split(", ");
        DataStoreInfo citeStore = getCatalog().getStoreByName("cite", DataStoreInfo.class);
        List<FeatureTypeInfo> typeInfos = getCatalog().getResourcesByStore(citeStore, FeatureTypeInfo.class);
        Assert.assertEquals(typeInfos.size(), layerArray.length);
    }

    @Test
    public void testRemoveLayer() {
        setupPanel(getCatalog().getLayerByName(getLayerId(BUILDINGS)));
        // print(tester.getLastRenderedPage(), true, true);
        GeoServerWicketTestSupport.tester.assertRenderedPage(FormTestPage.class);
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
        // damn wicket tester, it does not have a assertHidden...
        Assert.assertFalse(GeoServerWicketTestSupport.tester.getLastRenderedPage().get("form:panel:removedObjects:storesRemoved").isVisible());
        Assert.assertFalse(GeoServerWicketTestSupport.tester.getLastRenderedPage().get("form:panel:modifiedObjects").isVisible());
    }

    @Test
    public void testRemoveStyle() {
        setupPanel(getCatalog().getStyleByName(BUILDINGS.getLocalPart()));
        // print(tester.getLastRenderedPage(), true, true);
        GeoServerWicketTestSupport.tester.assertRenderedPage(FormTestPage.class);
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
        // damn wicket tester, it does not have a assertHidden...
        Assert.assertFalse(GeoServerWicketTestSupport.tester.getLastRenderedPage().get("form:panel:removedObjects:storesRemoved").isVisible());
        GeoServerWicketTestSupport.tester.assertLabel("form:panel:modifiedObjects:layersModified:layers", "Buildings");
    }
}

