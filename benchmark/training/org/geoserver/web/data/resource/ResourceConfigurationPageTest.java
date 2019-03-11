/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.data.resource;


import ImageMosaicFormat.BANDS;
import MockData.BRIDGES;
import MockData.POLYGONS;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.xml.namespace.QName;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CatalogBuilder;
import org.geoserver.catalog.CatalogFactory;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.data.test.MockData;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geoserver.web.data.store.panel.CheckBoxParamPanel;
import org.geoserver.web.data.store.panel.ParamPanel;
import org.geoserver.web.util.MapModel;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ResourceConfigurationPageTest extends GeoServerWicketTestSupport {
    protected static QName TIMERANGES = new QName(MockData.SF_URI, "timeranges", MockData.SF_PREFIX);

    @Test
    public void testBasic() {
        LayerInfo layer = getGeoServerApplication().getCatalog().getLayerByName(getLayerId(MockData.BASIC_POLYGONS));
        login();
        GeoServerWicketTestSupport.tester.startPage(new ResourceConfigurationPage(layer, false));
        GeoServerWicketTestSupport.tester.assertLabel("publishedinfoname", layer.getResource().getPrefixedName());
        GeoServerWicketTestSupport.tester.assertComponent("publishedinfo:tabs:panel:theList:0:content", BasicResourceConfig.class);
    }

    @Test
    public void testUpdateResource() {
        LayerInfo layer = getGeoServerApplication().getCatalog().getLayerByName(getLayerId(MockData.GEOMETRYLESS));
        login();
        ResourceConfigurationPage page = new ResourceConfigurationPage(layer, false);
        GeoServerWicketTestSupport.tester.startPage(page);
        GeoServerWicketTestSupport.tester.assertContainsNot("the_geom");
        FeatureTypeInfo info = getCatalog().getResourceByName(BRIDGES.getLocalPart(), FeatureTypeInfo.class);
        // Apply the new feature to the page
        page.add(new AjaxEventBehavior("ondblclick") {
            public void onEvent(AjaxRequestTarget target) {
                page.updateResource(info, target);
            }
        });
        GeoServerWicketTestSupport.tester.executeAjaxEvent(page, "ondblclick");
        print(GeoServerWicketTestSupport.tester.getLastRenderedPage(), true, true);
        // verify contents were updated
        GeoServerWicketTestSupport.tester.assertContains("the_geom");
    }

    @Test
    public void testSerializedModel() throws Exception {
        CatalogFactory fac = getGeoServerApplication().getCatalog().getFactory();
        FeatureTypeInfo fti = fac.createFeatureType();
        fti.setName("mylayer");
        fti.setStore(getGeoServerApplication().getCatalog().getDataStoreByName(POLYGONS.getPrefix()));
        LayerInfo layer = fac.createLayer();
        layer.setResource(fti);
        login();
        ResourceConfigurationPage page = new ResourceConfigurationPage(layer, true);
        byte[] serialized;
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(os)) {
                oos.writeObject(page);
            }
            serialized = os.toByteArray();
        }
        ResourceConfigurationPage page2;
        try (ByteArrayInputStream is = new ByteArrayInputStream(serialized)) {
            try (ObjectInputStream ois = new ObjectInputStream(is)) {
                page2 = ((ResourceConfigurationPage) (ois.readObject()));
            }
        }
        Assert.assertTrue(((page2.getPublishedInfo()) instanceof LayerInfo));
        Assert.assertEquals(layer.prefixedName(), page2.getPublishedInfo().prefixedName());
        // the crucial test: the layer is attached to the catalog
        Assert.assertNotNull(getCatalog());
    }

    @Test
    public void testComputeLatLon() throws Exception {
        final Catalog catalog = getCatalog();
        final CatalogBuilder cb = new CatalogBuilder(catalog);
        cb.setStore(catalog.getStoreByName(POLYGONS.getPrefix(), DataStoreInfo.class));
        FeatureTypeInfo ft = cb.buildFeatureType(new org.geotools.feature.NameImpl(MockData.POLYGONS));
        LayerInfo layer = cb.buildLayer(ft);
        login();
        ResourceConfigurationPage page = new ResourceConfigurationPage(layer, true);
        GeoServerWicketTestSupport.tester.startPage(page);
        // print(tester.getLastRenderedPage(), true, true, true);
        GeoServerWicketTestSupport.tester.executeAjaxEvent("publishedinfo:tabs:panel:theList:0:content:referencingForm:computeLatLon", "onclick");
        // print(tester.getLastRenderedPage(), true, true, true);
        // we used to have error messages
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
        Component llbox = GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("publishedinfo:tabs:panel:theList:0:content:referencingForm:latLonBoundingBox");
        ReferencedEnvelope re = ((ReferencedEnvelope) (llbox.getDefaultModelObject()));
        Assert.assertEquals((-93), re.getMinX(), 0.1);
        Assert.assertEquals(4.5, re.getMinY(), 0.1);
        Assert.assertEquals((-93), re.getMaxX(), 0.1);
        Assert.assertEquals(4.5, re.getMaxY(), 0.1);
    }

    @Test
    public void testParametersUI() throws Exception {
        LayerInfo layer = getGeoServerApplication().getCatalog().getLayerByName(getLayerId(ResourceConfigurationPageTest.TIMERANGES));
        login();
        GeoServerWicketTestSupport.tester.startPage(new ResourceConfigurationPage(layer, false));
        // print(tester.getLastRenderedPage(), true, true);
        // get the list of parameters in the UI
        ListView parametersList = ((ListView) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("publishedinfo:tabs:panel:theList:1:content:parameters")));
        parametersList.visitChildren(ParamPanel.class, ( c, v) -> {
            MapModel mapModel = ((MapModel) (c.getDefaultModel()));
            String parameterKey = mapModel.getExpression();
            if (((USE_JAI_IMAGEREAD.getName().getCode().equals(parameterKey)) || (ACCURATE_RESOLUTION.getName().getCode().equals(parameterKey))) || (ALLOW_MULTITHREADING.getName().getCode().equals(parameterKey))) {
                assertThat(parameterKey, c, CoreMatchers.instanceOf(.class));
            } else
                if ((((EXCESS_GRANULE_REMOVAL.getName().getCode().equals(parameterKey)) || (FOOTPRINT_BEHAVIOR.getName().getCode().equals(parameterKey))) || (MERGE_BEHAVIOR.getName().getCode().equals(parameterKey))) || (OVERVIEW_POLICY.getName().getCode().equals(parameterKey))) {
                    assertThat(parameterKey, c, CoreMatchers.instanceOf(.class));
                } else
                    if (((BACKGROUND_COLOR.getName().getCode().equals(parameterKey)) || (OUTPUT_TRANSPARENT_COLOR.getName().getCode().equals(parameterKey))) || (INPUT_TRANSPARENT_COLOR.getName().getCode().equals(parameterKey))) {
                        assertThat(parameterKey, c, CoreMatchers.instanceOf(.class));
                    } else {
                        assertThat(parameterKey, c, CoreMatchers.instanceOf(.class));
                    }


        });
        GeoServerWicketTestSupport.tester.assertComponent("publishedinfo:tabs:panel:theList:1:content:parameters:0:parameterPanel", CheckBoxParamPanel.class);
    }

    @Test
    public void testMissingParameters() {
        // get mosaic, remove a parameter
        CoverageInfo coverage = getCatalog().getCoverageByName(getLayerId(ResourceConfigurationPageTest.TIMERANGES));
        String bandCode = BANDS.getName().getCode();
        coverage.getParameters().remove(bandCode);
        getCatalog().save(coverage);
        // start up the page
        LayerInfo layer = getCatalog().getLayerByName(getLayerId(ResourceConfigurationPageTest.TIMERANGES));
        login();
        GeoServerWicketTestSupport.tester.startPage(new ResourceConfigurationPage(layer, false));
        // print(tester.getLastRenderedPage(), true, true);
        // get the list of parameters in the UI
        ListView parametersList = ((ListView) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("publishedinfo:tabs:panel:theList:1:content:parameters")));
        AtomicBoolean editorFound = new AtomicBoolean(false);
        parametersList.visitChildren(ParamPanel.class, ( c, v) -> {
            MapModel mapModel = ((MapModel) (c.getDefaultModel()));
            String parameterKey = mapModel.getExpression();
            if (bandCode.equals(parameterKey)) {
                editorFound.set(true);
            }
        });
        Assert.assertTrue("Bands parameter not found", editorFound.get());
    }

    @Test
    public void testSaveEnumsAsString() {
        Catalog catalog = getGeoServerApplication().getCatalog();
        LayerInfo layer = catalog.getLayerByName(getLayerId(ResourceConfigurationPageTest.TIMERANGES));
        login();
        GeoServerWicketTestSupport.tester.startPage(new ResourceConfigurationPage(layer, false));
        // locate the overview parameter editor
        ListView parametersList = ((ListView) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("publishedinfo:tabs:panel:theList:1:content:parameters")));
        AtomicReference ref = new AtomicReference(null);
        parametersList.visitChildren(ParamPanel.class, ( c, v) -> {
            MapModel mapModel = ((MapModel) (c.getDefaultModel()));
            String parameterKey = mapModel.getExpression();
            if (OVERVIEW_POLICY.getName().getCode().equals(parameterKey)) {
                ref.set(c.getPageRelativePath().substring((("publishedInfo".length()) + 1)));
            }
        });
        FormTester ft = GeoServerWicketTestSupport.tester.newFormTester("publishedinfo");
        ft.select(((ref.get()) + ":border:border_body:paramValue"), 2);
        ft.submit("save");
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
        // check it was saved
        CoverageInfo ci = catalog.getResourceByName(ResourceConfigurationPageTest.TIMERANGES.getPrefix(), ResourceConfigurationPageTest.TIMERANGES.getLocalPart(), CoverageInfo.class);
        Map<String, Serializable> parameters = ci.getParameters();
        Assert.assertEquals("NEAREST", parameters.get(OVERVIEW_POLICY.getName().toString()));
    }
}

