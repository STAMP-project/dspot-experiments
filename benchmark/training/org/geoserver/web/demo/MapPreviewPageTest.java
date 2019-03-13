/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.demo;


import java.util.Iterator;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.util.tester.TagTester;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.config.GeoServer;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geoserver.web.wicket.GeoServerTablePanel;
import org.geoserver.wfs.WFSInfo;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MapPreviewPageTest extends GeoServerWicketTestSupport {
    @Test
    public void testValues() throws Exception {
        tester.startPage(MapPreviewPage.class);
        tester.assertRenderedPage(MapPreviewPage.class);
    }

    @Test
    public void testLayerGroupNamesPrefixed() throws Exception {
        Catalog cat = getCatalog();
        LayerGroupInfo lg = cat.getFactory().createLayerGroup();
        lg.setName("foo");
        lg.setWorkspace(cat.getWorkspaceByName("sf"));
        lg.getLayers().add(cat.getLayerByName(getLayerId(MockData.PRIMITIVEGEOFEATURE)));
        new org.geoserver.catalog.CatalogBuilder(cat).calculateLayerGroupBounds(lg);
        cat.add(lg);
        tester.startPage(MapPreviewPage.class);
        tester.assertRenderedPage(MapPreviewPage.class);
        // move to next page
        GeoServerTablePanel table = ((GeoServerTablePanel) (tester.getComponentFromLastRenderedPage("table")));
        System.out.println(table.getDataProvider().size());
        tester.clickLink("table:navigatorBottom:navigator:next", true);
        DataView data = ((DataView) (tester.getComponentFromLastRenderedPage("table:listContainer:items")));
        boolean exists = false;
        for (Iterator it = data.iterator(); it.hasNext();) {
            MarkupContainer c = ((MarkupContainer) (it.next()));
            Label l = ((Label) (c.get("itemProperties:2:component")));
            String model = l.getDefaultModelObjectAsString();
            if ("sf:foo".equals(model)) {
                exists = true;
            }
        }
        Assert.assertTrue(exists);
    }

    @Test
    public void testMaxNumberOfFeaturesForPreview() throws Exception {
        GeoServer geoserver = getGeoServer();
        WFSInfo wfsInfo = geoserver.getService(WFSInfo.class);
        int maxFeatures = 100;
        wfsInfo.setMaxNumberOfFeaturesForPreview(maxFeatures);
        geoserver.save(wfsInfo);
        tester.startPage(MapPreviewPage.class);
        tester.assertRenderedPage(MapPreviewPage.class);
        assertMaxFeaturesInData(((DataView) (tester.getComponentFromLastRenderedPage("table:listContainer:items"))), maxFeatures);
        maxFeatures = 0;
        wfsInfo.setMaxNumberOfFeaturesForPreview(maxFeatures);
        geoserver.save(wfsInfo);
        tester.startPage(MapPreviewPage.class);
        tester.assertRenderedPage(MapPreviewPage.class);
        assertMaxFeaturesInData(((DataView) (tester.getComponentFromLastRenderedPage("table:listContainer:items"))), maxFeatures);
    }

    @Test
    public void testWfsOutputFormatValueUrlEncoding() {
        tester.startPage(MapPreviewPage.class);
        tester.assertRenderedPage(MapPreviewPage.class);
        Label optionLabel = ((Label) (tester.getComponentFromLastRenderedPage("table:listContainer:items:4:itemProperties:4:component:menu:wfs:wfsFormats:3")));
        Assert.assertTrue(optionLabel.getDefaultModelObjectAsString().equals("GML3.2"));
        for (Iterator<? extends Behavior> itBehaviors = optionLabel.getBehaviors().iterator(); itBehaviors.hasNext();) {
            Behavior b = itBehaviors.next();
            if (b instanceof AttributeModifier) {
                AttributeModifier am = ((AttributeModifier) (b));
                String url = am.toString();
                Assert.assertTrue((!(url.contains("gml+xml"))));
                Assert.assertTrue(url.contains("gml%2Bxml"));
                break;
            }
        }
    }

    @Test
    public void testNameURLEncoding() {
        Catalog catalog = getCatalog();
        FeatureTypeInfo ft = catalog.getFeatureTypeByName("cite:Lakes");
        ft.setName("Lakes + a plus");
        catalog.save(ft);
        try {
            tester.startPage(MapPreviewPage.class);
            tester.assertRenderedPage(MapPreviewPage.class);
            // print(tester.getLastRenderedPage(), true, true, true);
            DataView data = ((DataView) (tester.getComponentFromLastRenderedPage("table:listContainer:items")));
            boolean exists = false;
            String path = null;
            for (Iterator it = data.iterator(); it.hasNext();) {
                MarkupContainer c = ((MarkupContainer) (it.next()));
                Label l = ((Label) (c.get("itemProperties:2:component")));
                String model = l.getDefaultModelObjectAsString();
                if ("cite:Lakes + a plus".equals(model)) {
                    exists = true;
                    path = c.getPageRelativePath();
                    // check visible links
                    ExternalLink olLink = ((ExternalLink) (c.get("itemProperties:3:component:ol")));
                    ExternalLink gmlLink = ((ExternalLink) (c.get("itemProperties:3:component:gml")));
                    Assert.assertEquals("http://localhost:80/context/cite/wms?service=WMS&amp;version=1.1.0&amp;request=GetMap&amp;layers=cite%3ALakes%20%2B%20a%20plus&amp;bbox=-180.0%2C-90.0%2C180.0%2C90.0&amp;width=768&amp;height=384&amp;srs=EPSG%3A4326&amp;format=application/openlayers", olLink.getDefaultModelObjectAsString());
                    Assert.assertThat(gmlLink.getDefaultModelObjectAsString(), CoreMatchers.containsString("http://localhost:80/context/cite/ows?service=WFS&amp;version=1.0.0&amp;request=GetFeature&amp;typeName=cite%3ALakes%20%2B%20a%20plus"));
                }
            }
            Assert.assertTrue("Could not find layer with expected name", exists);
            String html = tester.getLastResponseAsString();
            TagTester menuTester = TagTester.createTagByAttribute(html, "wicketPath", ((path.replace(":", "_")) + "_itemProperties_4_component_menu"));
            String onchange = menuTester.getAttribute("onchange");
            Assert.assertThat(onchange, CoreMatchers.containsString("http://localhost:80/context/cite/wms?service=WMS&version=1.1.0&request=GetMap&layers=cite%3ALakes%20%2B%20a%20plus&bbox=-180.0%2C-90.0%2C180.0%2C90.0&width=768&height=384&srs=EPSG%3A4326&format="));
            Assert.assertThat(onchange, CoreMatchers.containsString("http://localhost:80/context/cite/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=cite%3ALakes%20%2B%20a%20plus"));
        } finally {
            ft.setName("Lines");
            catalog.save(ft);
        }
    }

    /**
     * Test for layer group service support check
     */
    @Test
    public void testHasServiceSupport() throws Exception {
        Catalog cat = getCatalog();
        LayerGroupInfo lg = cat.getFactory().createLayerGroup();
        lg.setName("linkgroup");
        lg.setWorkspace(cat.getWorkspaceByName("sf"));
        lg.getLayers().add(cat.getLayerByName(getLayerId(MockData.PRIMITIVEGEOFEATURE)));
        new org.geoserver.catalog.CatalogBuilder(cat).calculateLayerGroupBounds(lg);
        cat.add(lg);
        tester.startPage(MapPreviewPage.class);
        tester.assertRenderedPage(MapPreviewPage.class);
        MapPreviewPage page = ((MapPreviewPage) (tester.getLastRenderedPage()));
        Assert.assertTrue(page.hasServiceSupport("sf:linkgroup", "WMS"));
    }
}

