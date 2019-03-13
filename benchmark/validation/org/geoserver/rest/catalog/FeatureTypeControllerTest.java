/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.rest.catalog;


import java.util.List;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.geoserver.catalog.AttributeTypeInfo;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.rest.RestBaseController;
import org.geotools.data.DataAccess;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.MultiPolygon;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


public class FeatureTypeControllerTest extends CatalogRESTTestSupport {
    private static String BASEPATH = RestBaseController.ROOT_PATH;

    @Test
    public void testGetAllByWorkspace() throws Exception {
        Document dom = getAsDOM(((FeatureTypeControllerTest.BASEPATH) + "/workspaces/sf/featuretypes.xml"));
        Assert.assertEquals(CatalogRESTTestSupport.catalog.getFeatureTypesByNamespace(CatalogRESTTestSupport.catalog.getNamespaceByPrefix("sf")).size(), dom.getElementsByTagName("featureType").getLength());
    }

    @Test
    public void testGetAllByDataStore() throws Exception {
        // two stores to play with
        addPropertyDataStore(true);
        addGeomlessPropertyDataStore(true);
        Document dom = getAsDOM(((FeatureTypeControllerTest.BASEPATH) + "/workspaces/gs/datastores/pds/featuretypes.xml"));
        Assert.assertEquals(2, dom.getElementsByTagName("featureType").getLength());
        assertXpathEvaluatesTo("1", "count(//featureType/name[text()='pdsa'])", dom);
        assertXpathEvaluatesTo("1", "count(//featureType/name[text()='pdsb'])", dom);
    }

    @Test
    public void testGetAllAvailable() throws Exception {
        addPropertyDataStore(false);
        Document dom = getAsDOM(((FeatureTypeControllerTest.BASEPATH) + "/workspaces/gs/datastores/pds/featuretypes.xml?list=available"));
        assertXpathEvaluatesTo("1", "count(//featureTypeName[text()='pdsa'])", dom);
        assertXpathEvaluatesTo("1", "count(//featureTypeName[text()='pdsb'])", dom);
    }

    @Test
    public void testGetAllAvailableWithGeometryOnly() throws Exception {
        addGeomlessPropertyDataStore(false);
        Document dom = getAsDOM(((FeatureTypeControllerTest.BASEPATH) + "/workspaces/gs/datastores/ngpds/featuretypes.xml?list=available"));
        assertXpathEvaluatesTo("2", "count(//featureTypeName)", dom);
        dom = getAsDOM(((FeatureTypeControllerTest.BASEPATH) + "/workspaces/gs/datastores/ngpds/featuretypes.xml?list=available_with_geom"));
        assertXpathEvaluatesTo("0", "count(//featureTypeName)", dom);
    }

    /**
     * Test that a list of all feature types for a data source are returned when "list=all",
     * including both configured and unconfigured ones.
     */
    @Test
    public void testGetAllByDataStoreWithListAll() throws Exception {
        // Create a data store with only the first feature type configured.
        addPropertyDataStoreOnlyConfigureFirst();
        Document dom = getAsDOM(((FeatureTypeControllerTest.BASEPATH) + "/workspaces/gs/datastores/pds/featuretypes.xml?list=all"));
        Assert.assertEquals(2, dom.getElementsByTagName("featureTypeName").getLength());
        assertXpathEvaluatesTo("1", "count(//featureTypeName[text()='pdsa'])", dom);
        assertXpathEvaluatesTo("1", "count(//featureTypeName[text()='pdsb'])", dom);
    }

    @Test
    public void testPutAllUnauthorized() throws Exception {
        Assert.assertEquals(405, putAsServletResponse(((FeatureTypeControllerTest.BASEPATH) + "/workspaces/sf/datastores/sf/featuretypes")).getStatus());
    }

    @Test
    public void testDeleteAllUnauthorized() throws Exception {
        Assert.assertEquals(405, deleteAsServletResponse(((FeatureTypeControllerTest.BASEPATH) + "/workspaces/sf/datastores/sf/featuretypes")).getStatus());
    }

    @Test
    public void testPostToResource() throws Exception {
        addPropertyDataStore(true);
        String xml = "<featureType>" + ("<name>pdsa</name>" + "</featureType>");
        MockHttpServletResponse response = postAsServletResponse(((FeatureTypeControllerTest.BASEPATH) + "/workspaces/gs/datastores/pds/featuretypes/pdsa"), xml, "text/xml");
        Assert.assertEquals(405, response.getStatus());
    }

    @Test
    public void testGetAsXML() throws Exception {
        Document dom = getAsDOM(((FeatureTypeControllerTest.BASEPATH) + "/workspaces/sf/featuretypes/PrimitiveGeoFeature.xml"));
        Assert.assertEquals("featureType", dom.getDocumentElement().getNodeName());
        assertXpathEvaluatesTo("PrimitiveGeoFeature", "/featureType/name", dom);
        assertXpathEvaluatesTo("EPSG:4326", "/featureType/srs", dom);
        Assert.assertEquals(CRS.decode("EPSG:4326").toWKT(), CatalogRESTTestSupport.xp.evaluate("/featureType/nativeCRS", dom));
        FeatureTypeInfo ft = CatalogRESTTestSupport.catalog.getFeatureTypeByName("sf", "PrimitiveGeoFeature");
        /* ReferencedEnvelope re = ft.getNativeBoundingBox(); assertXpathEvaluatesTo( re.getMinX()+"" , "/featureType/nativeBoundingBox/minx", dom );
        assertXpathEvaluatesTo( re.getMaxX()+"" , "/featureType/nativeBoundingBox/maxx", dom ); assertXpathEvaluatesTo( re.getMinY()+"" ,
        "/featureType/nativeBoundingBox/miny", dom ); assertXpathEvaluatesTo( re.getMaxY()+"" , "/featureType/nativeBoundingBox/maxy", dom );
         */
        ReferencedEnvelope re = ft.getLatLonBoundingBox();
        assertXpathEvaluatesTo(((re.getMinX()) + ""), "/featureType/latLonBoundingBox/minx", dom);
        assertXpathEvaluatesTo(((re.getMaxX()) + ""), "/featureType/latLonBoundingBox/maxx", dom);
        assertXpathEvaluatesTo(((re.getMinY()) + ""), "/featureType/latLonBoundingBox/miny", dom);
        assertXpathEvaluatesTo(((re.getMaxY()) + ""), "/featureType/latLonBoundingBox/maxy", dom);
    }

    @Test
    public void testGetAsJSON() throws Exception {
        JSON json = getAsJSON(((FeatureTypeControllerTest.BASEPATH) + "/workspaces/sf/featuretypes/PrimitiveGeoFeature.json"));
        JSONObject featureType = getJSONObject("featureType");
        assertNotNull(featureType);
        Assert.assertEquals("PrimitiveGeoFeature", featureType.get("name"));
        Assert.assertEquals(CRS.decode("EPSG:4326").toWKT(), featureType.get("nativeCRS"));
        Assert.assertEquals("EPSG:4326", featureType.get("srs"));
    }

    @Test
    public void testGetAsHTML() throws Exception {
        Document dom = getAsDOM(((FeatureTypeControllerTest.BASEPATH) + "/workspaces/sf/datastores/sf/featuretypes/PrimitiveGeoFeature.html"));
    }

    @Test
    public void testGetAllAsHTML() throws Exception {
        addPropertyDataStore(true);
        String dom = getAsString(((FeatureTypeControllerTest.BASEPATH) + "/workspaces/gs/datastores/pds/featuretypes.xml"));
        System.out.println(dom);
    }

    @Test
    public void testGetWrongFeatureType() throws Exception {
        // Parameters for the request
        String ws = "sf";
        String ds = "sf";
        String ft = "PrimitiveGeoFeaturessss";
        // Request path
        String requestPath = (((((FeatureTypeControllerTest.BASEPATH) + "/workspaces/") + ws) + "/featuretypes/") + ft) + ".html";
        String requestPath2 = (((((((FeatureTypeControllerTest.BASEPATH) + "/workspaces/") + ws) + "/datastores/") + ds) + "/featuretypes/") + ft) + ".html";
        // Exception path
        String exception = (("No such feature type: " + ws) + ",") + ft;
        String exception2 = (((("No such feature type: " + ws) + ",") + ds) + ",") + ft;
        // CASE 1: No datastore set
        // First request should thrown an exception
        MockHttpServletResponse response = getAsServletResponse(requestPath);
        Assert.assertEquals(404, response.getStatus());
        FeatureTypeControllerTest.assertContains(response.getContentAsString(), exception);
        // Same request with ?quietOnNotFound should not throw an exception
        response = getAsServletResponse((requestPath + "?quietOnNotFound=true"));
        Assert.assertEquals(404, response.getStatus());
        // No exception thrown
        Assert.assertTrue(response.getContentAsString().isEmpty());
        // CASE 2: datastore set
        // First request should thrown an exception
        response = getAsServletResponse(requestPath2);
        Assert.assertEquals(404, response.getStatus());
        FeatureTypeControllerTest.assertContains(response.getContentAsString(), exception2);
        // Same request with ?quietOnNotFound should not throw an exception
        response = getAsServletResponse((requestPath2 + "?quietOnNotFound=true"));
        Assert.assertEquals(404, response.getStatus());
        // No exception thrown
        Assert.assertTrue(response.getContentAsString().isEmpty());
    }

    @Test
    public void testPut() throws Exception {
        String xml = "<featureType>" + ("<title>new title</title>" + "</featureType>");
        MockHttpServletResponse response = putAsServletResponse(((FeatureTypeControllerTest.BASEPATH) + "/workspaces/sf/datastores/sf/featuretypes/PrimitiveGeoFeature"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        Document dom = getAsDOM(((FeatureTypeControllerTest.BASEPATH) + "/workspaces/sf/datastores/sf/featuretypes/PrimitiveGeoFeature.xml"));
        assertXpathEvaluatesTo("new title", "/featureType/title", dom);
        FeatureTypeInfo ft = CatalogRESTTestSupport.catalog.getFeatureTypeByName("sf", "PrimitiveGeoFeature");
        Assert.assertEquals("new title", ft.getTitle());
    }

    @Test
    public void testPutWithoutStore() throws Exception {
        String xml = "<featureType>" + ("<title>new title</title>" + "</featureType>");
        MockHttpServletResponse response = putAsServletResponse(((FeatureTypeControllerTest.BASEPATH) + "/workspaces/sf/featuretypes/PrimitiveGeoFeature"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        Document dom = getAsDOM(((FeatureTypeControllerTest.BASEPATH) + "/workspaces/sf/featuretypes/PrimitiveGeoFeature.xml"));
        assertXpathEvaluatesTo("new title", "/featureType/title", dom);
        FeatureTypeInfo ft = CatalogRESTTestSupport.catalog.getFeatureTypeByName("sf", "PrimitiveGeoFeature");
        Assert.assertEquals("new title", ft.getTitle());
    }

    @Test
    public void testPutNonDestructive() throws Exception {
        FeatureTypeInfo ft = CatalogRESTTestSupport.catalog.getFeatureTypeByName("sf", "PrimitiveGeoFeature");
        Assert.assertTrue(ft.isEnabled());
        boolean isAdvertised = ft.isAdvertised();
        int maxFeatures = ft.getMaxFeatures();
        int numDecimals = ft.getNumDecimals();
        boolean isOverridingServiceSRS = ft.isOverridingServiceSRS();
        boolean getSkipNumberMatched = ft.getSkipNumberMatched();
        boolean isCircularArcPresent = ft.isCircularArcPresent();
        String xml = "<featureType>" + ("<title>new title</title>" + "</featureType>");
        MockHttpServletResponse response = putAsServletResponse(((FeatureTypeControllerTest.BASEPATH) + "/workspaces/sf/datastores/sf/featuretypes/PrimitiveGeoFeature"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        ft = CatalogRESTTestSupport.catalog.getFeatureTypeByName("sf", "PrimitiveGeoFeature");
        Assert.assertTrue(ft.isEnabled());
        Assert.assertEquals(isAdvertised, ft.isAdvertised());
        Assert.assertEquals(maxFeatures, ft.getMaxFeatures());
        Assert.assertEquals(numDecimals, ft.getNumDecimals());
        Assert.assertEquals(isOverridingServiceSRS, ft.isOverridingServiceSRS());
        Assert.assertEquals(getSkipNumberMatched, ft.getSkipNumberMatched());
        Assert.assertEquals(isCircularArcPresent, ft.isCircularArcPresent());
    }

    /**
     * Check feature type modification involving calculation of bounds.
     *
     * <p>Update: Ensure feature type modification does not reset ResourcePool DataStoreCache
     */
    @SuppressWarnings("rawtypes")
    @Test
    public void testPutWithCalculation() throws Exception {
        DataStoreInfo dataStoreInfo = getCatalog().getDataStoreByName("sf", "sf");
        String dataStoreId = dataStoreInfo.getId();
        DataAccess dataAccessBefore = dataStoreInfo.getDataStore(null);
        assertSame("ResourcePool DataStoreCache", dataAccessBefore, getCatalog().getResourcePool().getDataStoreCache().get(dataStoreId));
        String clearLatLonBoundingBox = "<featureType>" + (((((((("<nativeBoundingBox>" + "<minx>-180.0</minx>") + "<maxx>180.0</maxx>") + "<miny>-90.0</miny>") + "<maxy>90.0</maxy>") + "<crs>EPSG:4326</crs>") + "</nativeBoundingBox>") + "<latLonBoundingBox/>") + "</featureType>");
        String path = (FeatureTypeControllerTest.BASEPATH) + "/workspaces/sf/datastores/sf/featuretypes/PrimitiveGeoFeature";
        MockHttpServletResponse response = putAsServletResponse(path, clearLatLonBoundingBox, "text/xml");
        Assert.assertEquals(("Couldn\'t remove lat/lon bounding box:\n" + (response.getContentAsString())), 200, response.getStatus());
        Document dom = getAsDOM((path + ".xml"));
        assertXpathEvaluatesTo("0.0", "/featureType/latLonBoundingBox/minx", dom);
        // confirm ResourcePoool cache of DataStore is unchanged
        DataAccess dataAccessAfter = getCatalog().getDataStoreByName("sf", "sf").getDataStore(null);
        assertSame("ResourcePool DataStoreCache check 1", dataAccessBefore, dataAccessAfter);
        assertSame("ResourcePool DataStoreCache", dataAccessBefore, getCatalog().getResourcePool().getDataStoreCache().get(dataStoreId));
        String updateNativeBounds = "<featureType>" + (((((((("<srs>EPSG:3785</srs>" + "<nativeBoundingBox>") + "<minx>-20037508.34</minx>") + "<maxx>20037508.34</maxx>") + "<miny>-20037508.34</miny>") + "<maxy>20037508.34</maxy>") + "<crs>EPSG:3785</crs>") + "</nativeBoundingBox>") + "</featureType>");
        response = putAsServletResponse((path + ".xml"), updateNativeBounds, "text/xml");
        Assert.assertEquals(("Couldn\'t update native bounding box: \n" + (response.getContentAsString())), 200, response.getStatus());
        dom = getAsDOM((path + ".xml"));
        print(dom);
        assertXpathExists("/featureType/latLonBoundingBox/minx[text()!='0.0']", dom);
        dataAccessAfter = getCatalog().getDataStoreByName("sf", "sf").getDataStore(null);
        assertSame("ResourcePool DataStoreCache check 2", dataAccessBefore, dataAccessAfter);
        assertSame("ResourcePool DataStoreCache", dataAccessBefore, getCatalog().getResourcePool().getDataStoreCache().get(dataStoreId));
    }

    @Test
    public void testPutNonExistant() throws Exception {
        String xml = "<featureType>" + ("<title>new title</title>" + "</featureType>");
        MockHttpServletResponse response = putAsServletResponse(((FeatureTypeControllerTest.BASEPATH) + "/workspaces/sf/datastores/sf/featuretypes/NonExistant"), xml, "text/xml");
        Assert.assertEquals(404, response.getStatus());
    }

    @Test
    public void testDelete() throws Exception {
        FeatureTypeInfo featureType = CatalogRESTTestSupport.catalog.getFeatureTypeByName("sf", "PrimitiveGeoFeature");
        String featureTypeId = featureType.getId();
        String dataStoreId = featureType.getStore().getId();
        Name name = featureType.getFeatureType().getName();
        assertNotNull("PrmitiveGeoFeature available", featureType);
        for (LayerInfo l : CatalogRESTTestSupport.catalog.getLayers(featureType)) {
            CatalogRESTTestSupport.catalog.remove(l);
        }
        Assert.assertEquals(200, deleteAsServletResponse(((FeatureTypeControllerTest.BASEPATH) + "/workspaces/sf/datastores/sf/featuretypes/PrimitiveGeoFeature")).getStatus());
        Assert.assertNull(CatalogRESTTestSupport.catalog.getFeatureTypeByName("sf", "PrimitiveGeoFeature"));
        if (CatalogRESTTestSupport.catalog.getResourcePool().getFeatureTypeAttributeCache().containsKey(featureTypeId)) {
            List<AttributeTypeInfo> attributesList = CatalogRESTTestSupport.catalog.getResourcePool().getFeatureTypeAttributeCache().get(featureTypeId);
            Assert.assertNull("attributes cleared", attributesList);
        }
        if (CatalogRESTTestSupport.catalog.getResourcePool().getDataStoreCache().containsKey(dataStoreId)) {
            DataAccess dataStore = CatalogRESTTestSupport.catalog.getResourcePool().getDataStoreCache().get(dataStoreId);
            List<Name> names = dataStore.getNames();
            Assert.assertTrue(names.contains(name));
        }
    }

    @Test
    public void testDeleteWithoutStore() throws Exception {
        FeatureTypeInfo featureType = CatalogRESTTestSupport.catalog.getFeatureTypeByName("sf", "PrimitiveGeoFeature");
        String featureTypeId = featureType.getId();
        String dataStoreId = featureType.getStore().getId();
        Name name = featureType.getFeatureType().getName();
        assertNotNull("PrmitiveGeoFeature available", featureType);
        for (LayerInfo l : CatalogRESTTestSupport.catalog.getLayers(featureType)) {
            CatalogRESTTestSupport.catalog.remove(l);
        }
        Assert.assertEquals(200, deleteAsServletResponse(((FeatureTypeControllerTest.BASEPATH) + "/workspaces/sf/featuretypes/PrimitiveGeoFeature")).getStatus());
        Assert.assertNull(CatalogRESTTestSupport.catalog.getFeatureTypeByName("sf", "PrimitiveGeoFeature"));
        if (CatalogRESTTestSupport.catalog.getResourcePool().getFeatureTypeAttributeCache().containsKey(featureTypeId)) {
            List<AttributeTypeInfo> attributesList = CatalogRESTTestSupport.catalog.getResourcePool().getFeatureTypeAttributeCache().get(featureTypeId);
            Assert.assertNull("attributes cleared", attributesList);
        }
        if (CatalogRESTTestSupport.catalog.getResourcePool().getDataStoreCache().containsKey(dataStoreId)) {
            DataAccess dataStore = CatalogRESTTestSupport.catalog.getResourcePool().getDataStoreCache().get(dataStoreId);
            List<Name> names = dataStore.getNames();
            Assert.assertTrue(names.contains(name));
        }
    }

    @Test
    public void testDeleteNonExistant() throws Exception {
        Assert.assertEquals(404, deleteAsServletResponse(((FeatureTypeControllerTest.BASEPATH) + "/workspaces/sf/datastores/sf/featuretypes/NonExistant")).getStatus());
    }

    @Test
    public void testDeleteRecursive() throws Exception {
        assertNotNull(CatalogRESTTestSupport.catalog.getFeatureTypeByName("sf", "PrimitiveGeoFeature"));
        assertNotNull(CatalogRESTTestSupport.catalog.getLayerByName("sf:PrimitiveGeoFeature"));
        Assert.assertEquals(403, deleteAsServletResponse(((FeatureTypeControllerTest.BASEPATH) + "/workspaces/sf/datastores/sf/featuretypes/PrimitiveGeoFeature")).getStatus());
        Assert.assertEquals(200, deleteAsServletResponse(((FeatureTypeControllerTest.BASEPATH) + "/workspaces/sf/datastores/sf/featuretypes/PrimitiveGeoFeature?recurse=true")).getStatus());
        Assert.assertNull(CatalogRESTTestSupport.catalog.getFeatureTypeByName("sf", "PrimitiveGeoFeature"));
        Assert.assertNull(CatalogRESTTestSupport.catalog.getLayerByName("sf:PrimitiveGeoFeature"));
    }

    @Test
    public void testPostGeometrylessFeatureType() throws Exception {
        addGeomlessPropertyDataStore(false);
        String xml = "<featureType>" + ("<name>ngpdsa</name>" + "</featureType>");
        MockHttpServletResponse response = postAsServletResponse(((FeatureTypeControllerTest.BASEPATH) + "/workspaces/gs/datastores/ngpds/featuretypes"), xml, "text/xml");
        Assert.assertEquals(201, response.getStatus());
        assertNotNull(response.getHeader("Location"));
        Assert.assertTrue(response.getHeader("Location").endsWith("/workspaces/gs/datastores/ngpds/featuretypes/ngpdsa"));
    }

    @Test
    public void testDeleteWsNotSameAsStoreName() throws Exception {
        // create a feature type whose store name is not same as workspace name
        testPostGeometrylessFeatureType();
        Assert.assertEquals(200, deleteAsServletResponse(((FeatureTypeControllerTest.BASEPATH) + "/workspaces/gs/datastores/ngpds/featuretypes/ngpdsa?recurse=true")).getStatus());
        Assert.assertNull(CatalogRESTTestSupport.catalog.getFeatureTypeByName("gs", "ngpdsa"));
    }

    @Test
    public void testCreateFeatureType() throws Exception {
        String xml = "<featureType>\n" + (((((((((((((((((((((("  <name>states</name>\n" + "  <nativeName>states</nativeName>\n") + "  <namespace>\n") + "    <name>cite</name>\n") + "  </namespace>\n") + "  <title>USA Population</title>\n") + "  <srs>EPSG:4326</srs>\n") + "  <attributes>\n") + "    <attribute>\n") + "      <name>the_geom</name>\n") + "      <binding>org.locationtech.jts.geom.MultiPolygon</binding>\n") + "    </attribute>\n") + "    <attribute>\n") + "      <name>STATE_NAME</name>\n") + "      <binding>java.lang.String</binding>\n") + "      <length>25</length>\n") + "    </attribute>\n") + "    <attribute>\n") + "      <name>LAND_KM</name>\n") + "      <binding>java.lang.Double</binding>\n") + "    </attribute>\n") + "  </attributes>\n") + "</featureType>");
        MockHttpServletResponse response = postAsServletResponse(((FeatureTypeControllerTest.BASEPATH) + "/workspaces/cite/datastores/default/featuretypes"), xml, "text/xml");
        Assert.assertEquals(201, response.getStatus());
        assertNotNull(response.getHeader("Location"));
        Assert.assertTrue(response.getHeader("Location").endsWith("/workspaces/cite/datastores/default/featuretypes/states"));
        FeatureTypeInfo ft = CatalogRESTTestSupport.catalog.getFeatureTypeByName("cite", "states");
        assertNotNull(ft);
        FeatureType schema = ft.getFeatureType();
        Assert.assertEquals("states", schema.getName().getLocalPart());
        Assert.assertEquals(CatalogRESTTestSupport.catalog.getNamespaceByPrefix("cite").getURI(), schema.getName().getNamespaceURI());
        Assert.assertEquals(3, schema.getDescriptors().size());
        assertNotNull(schema.getDescriptor("the_geom"));
        Assert.assertEquals(MultiPolygon.class, schema.getDescriptor("the_geom").getType().getBinding());
        assertNotNull(schema.getDescriptor("LAND_KM"));
        Assert.assertEquals(Double.class, schema.getDescriptor("LAND_KM").getType().getBinding());
    }

    @Test
    public void testRoundTripFeatureTypeXML() throws Exception {
        // Fetch the feature directly from the catalog
        FeatureTypeInfo before = CatalogRESTTestSupport.catalog.getFeatureTypeByName("sf", "PrimitiveGeoFeature");
        // Do a round-trip GET and PUT of the resource
        String xml = getAsString(((FeatureTypeControllerTest.BASEPATH) + "/workspaces/sf/datastores/sf/featuretypes/PrimitiveGeoFeature.xml"));
        MockHttpServletResponse response = putAsServletResponse(((FeatureTypeControllerTest.BASEPATH) + "/workspaces/sf/datastores/sf/featuretypes/PrimitiveGeoFeature"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        // Fetch the feature from the catalog again, and ensure nothing changed.
        FeatureTypeInfo after = CatalogRESTTestSupport.catalog.getFeatureTypeByName("sf", "PrimitiveGeoFeature");
        Assert.assertEquals(before, after);
    }

    @Test
    public void testRoundTripFeatureTypJSONL() throws Exception {
        // Fetch the feature directly from the catalog
        FeatureTypeInfo before = CatalogRESTTestSupport.catalog.getFeatureTypeByName("sf", "PrimitiveGeoFeature");
        // Do a round-trip GET and PUT of the resource
        String json = getAsString(((FeatureTypeControllerTest.BASEPATH) + "/workspaces/sf/datastores/sf/featuretypes/PrimitiveGeoFeature.json"));
        System.out.println(json);
        MockHttpServletResponse response = putAsServletResponse(((FeatureTypeControllerTest.BASEPATH) + "/workspaces/sf/datastores/sf/featuretypes/PrimitiveGeoFeature"), json, "text/json");
        Assert.assertEquals(200, response.getStatus());
        // Fetch the feature from the catalog again, and ensure nothing changed.
        FeatureTypeInfo after = CatalogRESTTestSupport.catalog.getFeatureTypeByName("sf", "PrimitiveGeoFeature");
        Assert.assertEquals(before, after);
    }

    /**
     * Tests services disabled on layer-resource
     */
    @Test
    public void testEnabledServicesOnLayer() throws Exception {
        disableServicesOnBuildings();
        Document dom = getAsDOM(((RestBaseController.ROOT_PATH) + "/workspaces/cite/datastores/cite/featuretypes/Buildings.xml"), 200);
        assertXpathEvaluatesTo("true", "//serviceConfiguration", dom);
        assertXpathExists("//disabledServices/string[.='WFS']", dom);
        assertXpathExists("//disabledServices/string[.='CSW']", dom);
        enableServicesOnBuildings();
    }
}

