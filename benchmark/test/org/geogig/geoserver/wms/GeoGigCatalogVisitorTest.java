/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geogig.geoserver.wms;


import java.util.List;
import java.util.Set;
import org.geogig.geoserver.GeoGigTestData;
import org.geoserver.catalog.DimensionInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.MetadataMap;
import org.geoserver.catalog.impl.DimensionInfoImpl;
import org.geoserver.rest.catalog.CatalogRESTTestSupport;
import org.geoserver.test.TestSetup;
import org.geoserver.test.TestSetupFrequency;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.locationtech.geogig.repository.IndexInfo;
import org.locationtech.geogig.repository.impl.GeoGIG;
import org.locationtech.geogig.storage.IndexDatabase;


/**
 *
 */
@TestSetup(run = TestSetupFrequency.REPEAT)
public class GeoGigCatalogVisitorTest extends CatalogRESTTestSupport {
    @Rule
    public GeoGigTestData geogigData = new GeoGigTestData();

    @Test
    public void testGetAttribute_SapitalIndexOnly() throws Exception {
        addAvailableGeogigLayers();
        GeoGigTestData.CatalogBuilder catalogBuilder = geogigData.newCatalogBuilder(catalog);
        String layerName = (catalogBuilder.workspaceName()) + ":lines";
        LayerInfo lineLayerInfo = catalog.getLayerByName(layerName);
        GeoGigCatalogVisitor visitor = new GeoGigCatalogVisitor();
        visitor.visit(lineLayerInfo);
        GeoGIG geoGig = geogigData.getGeogig();
        IndexDatabase indexDatabase = geoGig.getRepository().indexDatabase();
        List<IndexInfo> indexInfos = waitForIndexes(indexDatabase, "lines", 1);
        IndexInfo indexInfo = indexInfos.get(0);
        Set<String> materializedAttributeNames = IndexInfo.getMaterializedAttributeNames(indexInfo);
        Assert.assertTrue("Expected empty extra Attributes set", materializedAttributeNames.isEmpty());
    }

    @Test
    public void testGetAttribute_SpatialIndexWithExtraAttributes() throws Exception {
        addAvailableGeogigLayers();
        GeoGigTestData.CatalogBuilder catalogBuilder = geogigData.newCatalogBuilder(catalog);
        String layerName = (catalogBuilder.workspaceName()) + ":lines";
        LayerInfo lineLayerInfo = catalog.getLayerByName(layerName);
        // set the layer up with some time/elevation metadata
        MetadataMap metadata = lineLayerInfo.getResource().getMetadata();
        DimensionInfo timeInfo = new DimensionInfoImpl();
        timeInfo.setAttribute("S_TIME");
        timeInfo.setEndAttribute("E_TIME");
        DimensionInfo elevationInfo = new DimensionInfoImpl();
        elevationInfo.setAttribute("S_ELEV");
        elevationInfo.setEndAttribute("E_ELEV");
        metadata.put("time", timeInfo);
        metadata.put("elevation", elevationInfo);
        GeoGigCatalogVisitor visitor = new GeoGigCatalogVisitor();
        visitor.visit(lineLayerInfo);
        GeoGIG geoGig = geogigData.getGeogig();
        IndexDatabase indexDatabase = geoGig.getRepository().indexDatabase();
        List<IndexInfo> indexInfos = waitForIndexes(indexDatabase, "lines", 1);
        IndexInfo indexInfo = indexInfos.get(0);
        Set<String> materializedAttributeNames = IndexInfo.getMaterializedAttributeNames(indexInfo);
        Assert.assertFalse("Expected non-empty extra Attributes set", materializedAttributeNames.isEmpty());
        Assert.assertEquals("Expected 4 extra attributes", 4, materializedAttributeNames.size());
    }

    @Test
    public void testGetAttribute_SapitalIndexOnlyUsingRest() throws Exception {
        addAvailableGeoGigLayersWithDataStoreAddedViaRest();
        GeoGigTestData.CatalogBuilder catalogBuilder = geogigData.newCatalogBuilder(catalog);
        String layerName = (catalogBuilder.workspaceName()) + ":lines";
        LayerInfo lineLayerInfo = catalog.getLayerByName(layerName);
        GeoGigCatalogVisitor visitor = new GeoGigCatalogVisitor();
        visitor.visit(lineLayerInfo);
        GeoGIG geoGig = geogigData.getGeogig();
        IndexDatabase indexDatabase = geoGig.getRepository().indexDatabase();
        List<IndexInfo> indexInfos = waitForIndexes(indexDatabase, "lines", 1);
        IndexInfo indexInfo = indexInfos.get(0);
        Set<String> materializedAttributeNames = IndexInfo.getMaterializedAttributeNames(indexInfo);
        Assert.assertTrue("Expected empty extra Attributes set", materializedAttributeNames.isEmpty());
    }
}

