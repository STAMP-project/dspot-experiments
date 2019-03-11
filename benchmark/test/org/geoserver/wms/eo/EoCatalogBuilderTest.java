/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.eo;


import EoLayerType.BAND_COVERAGE;
import EoLayerType.BITMASK;
import EoLayerType.BROWSE_IMAGE;
import EoLayerType.COVERAGE_OUTLINE;
import EoLayerType.GEOPHYSICAL_PARAMETER;
import EoLayerType.KEY;
import LayerGroupInfo.Mode.EO;
import java.net.URISyntaxException;
import java.util.List;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.CoverageStoreInfo;
import org.geoserver.catalog.DimensionInfo;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.ResourceInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geotools.coverage.grid.io.StructuredGridCoverage2DReader;
import org.junit.Assert;
import org.junit.Test;


/**
 * EoCatalogBuilder tests
 *
 * @author Davide Savazzi - geo-solutions.it
 */
public class EoCatalogBuilderTest extends GeoServerSystemTestSupport {
    private Catalog catalog;

    private WorkspaceInfo ws;

    private EoCatalogBuilder builder;

    private List<String> styles;

    @Test
    public void testEoMasksLayerCreation() {
        String groupName = "EO-Dataset";
        String maskName = "Masks";
        LayerInfo layer = builder.createEoMasksLayer(ws, groupName, maskName, getUrl("EO_Airmass"));
        Assert.assertNotNull(layer);
        Assert.assertEquals(((groupName + "_") + maskName), layer.getName());
        layer = catalog.getLayerByName(layer.getName());
        Assert.assertEquals(BITMASK.name(), layer.getMetadata().get(KEY));
        checkTimeDimension(layer);
        checkStyles(layer);
    }

    @Test
    public void testEoParametersLayerCreation() {
        String groupName = "EO-Dataset";
        String paramName = "Params";
        LayerInfo layer = builder.createEoParametersLayer(ws, groupName, paramName, getUrl("EO_Airmass"));
        Assert.assertNotNull(layer);
        Assert.assertEquals(((groupName + "_") + paramName), layer.getName());
        layer = catalog.getLayerByName(layer.getName());
        Assert.assertEquals(GEOPHYSICAL_PARAMETER.name(), layer.getMetadata().get(KEY));
        checkTimeDimension(layer);
    }

    @Test
    public void testEoOutlineLayerCreation() throws Exception {
        String groupName = "EO-Dataset";
        LayerInfo browseLayer = builder.createEoBrowseImageLayer(ws, groupName, getUrl("EO_Nat"));
        Assert.assertNotNull(browseLayer);
        Assert.assertEquals((groupName + "_BROWSE"), browseLayer.getName());
        Assert.assertEquals(BROWSE_IMAGE.name(), browseLayer.getMetadata().get(KEY));
        checkTimeDimension(browseLayer);
        CoverageInfo coverage = ((CoverageInfo) (browseLayer.getResource()));
        StructuredGridCoverage2DReader reader = ((StructuredGridCoverage2DReader) (coverage.getGridCoverageReader(null, null)));
        LayerInfo layer = builder.createEoOutlineLayer(getUrl("EO_Nat"), ws, groupName, coverage.getNativeCoverageName(), reader);
        Assert.assertNotNull(layer);
        Assert.assertEquals((groupName + "_outlines"), layer.getName());
        checkTimeDimension(layer);
        layer = catalog.getLayerByName(layer.getName());
        Assert.assertEquals(COVERAGE_OUTLINE.name(), layer.getMetadata().get(KEY));
        checkStyles(layer);
    }

    @Test
    public void testEoBandsLayerCreation() {
        try {
            builder.createEoMosaicLayer(ws, "EO-Band", BAND_COVERAGE, getUrl("EO_Airmass"), true);
            Assert.fail("The layer must not be created because it doesn't have custom dimensions");
        } catch (IllegalArgumentException e) {
        }
        LayerInfo layer = builder.createEoMosaicLayer(ws, "EO-Band", BAND_COVERAGE, getUrl("EO_Channels"), true);
        layer = catalog.getLayerByName("EO-Band");
        Assert.assertEquals(BAND_COVERAGE.name(), layer.getMetadata().get(KEY));
        // check dimensions
        checkTimeDimension(layer);
        DimensionInfo customDimension = ((DimensionInfo) (layer.getResource().getMetadata().get(((ResourceInfo.CUSTOM_DIMENSION_PREFIX) + "CHANNEL"))));
        Assert.assertNotNull(customDimension);
    }

    @Test
    public void testEoBandsLayerUsage() {
        try {
            builder.createEoMosaicLayer(ws, "EO-Band-2", BAND_COVERAGE, getUrl("EO_Airmass"), true);
            Assert.fail("The layer must not be created because it doesn't have custom dimensions");
        } catch (IllegalArgumentException e) {
        }
        LayerInfo layer = builder.createEoMosaicLayer(ws, "EO-Band-2", BAND_COVERAGE, getUrl("EO_Channels"), true);
        layer = catalog.getLayerByName("EO-Band-2");
        Assert.assertEquals(BAND_COVERAGE.name(), layer.getMetadata().get(KEY));
        // check dimensions
        checkTimeDimension(layer);
        DimensionInfo customDimension = ((DimensionInfo) (layer.getResource().getMetadata().get(((ResourceInfo.CUSTOM_DIMENSION_PREFIX) + "CHANNEL"))));
        Assert.assertNotNull(customDimension);
    }

    @Test
    public void testStoreCreation() throws URISyntaxException {
        CoverageStoreInfo store = builder.createEoMosaicStore(ws, "EO-store", getUrl("EO_Airmass"));
        try {
            Assert.assertNotNull(store);
            Assert.assertEquals(ws, store.getWorkspace());
            Assert.assertEquals("EO-store", store.getName());
            Assert.assertEquals("ImageMosaic", store.getType());
            store = catalog.getStoreByName("EO-store", CoverageStoreInfo.class);
            Assert.assertNotNull(store);
            Assert.assertEquals(ws, store.getWorkspace());
            Assert.assertEquals("EO-store", store.getName());
            Assert.assertEquals("ImageMosaic", store.getType());
        } finally {
            catalog.remove(store);
        }
    }

    @Test
    public void testEoLayerGroupCreation() {
        String groupName = "EO-Dataset2";
        String groupTitle = "title";
        LayerGroupInfo group = builder.createEoLayerGroup(ws, groupName, groupTitle, getUrl("EO_Nat"), getUrl("EO_Channels"), "airmass", getUrl("EO_Airmass"), null, null);
        Assert.assertNotNull(group);
        Assert.assertEquals(groupName, group.getName());
        Assert.assertEquals(groupTitle, group.getTitle());
        Assert.assertEquals(3, group.getLayers().size());
        Assert.assertEquals(EO, group.getMode());
        Assert.assertNotNull(group.getRootLayer());
        Assert.assertEquals((groupName + "_BROWSE"), group.getRootLayer().getName());
    }
}

