/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.opensearch.rest;


import HttpStatus.CREATED;
import com.google.common.collect.Sets;
import com.jayway.jsonpath.DocumentContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.sf.json.JSONObject;
import org.geoserver.opensearch.rest.ProductsController.ProductPart;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Envelope;
import org.opengis.feature.simple.SimpleFeature;
import org.springframework.mock.web.MockHttpServletResponse;


public class ProductsControllerTest extends OSEORestTestSupport {
    public static final String PRODUCT_CREATE_UPDATE_ID = "S2A_OPER_MSI_L1C_TL_SGS__20180101T000000_A006640_T32TPP_N02.04";

    public static final String PRODUCT_ATM_CREATE_UPDATE_ID = "SAS1_20180101T000000.01";

    @Test
    public void testGetProductsForNonExistingCollection() throws Exception {
        MockHttpServletResponse response = getAsServletResponse("/rest/oseo/collections/fooBar/products");
        Assert.assertEquals(404, response.getStatus());
        Assert.assertThat(response.getContentAsString(), Matchers.containsString("fooBar"));
    }

    @Test
    public void testGetProducts() throws Exception {
        DocumentContext json = getAsJSONPath("/rest/oseo/collections/SENTINEL2/products", 200);
        Assert.assertEquals(19, json.read("$.products.*", List.class).size());
        // check the first (sorted alphabetically, it should be stable)
        Assert.assertEquals("S2A_OPER_MSI_L1C_TL_MTI__20170308T220244_A008933_T11SLT_N02.04", json.read("$.products[0].id"));
        Assert.assertEquals("http://localhost:8080/geoserver/rest/oseo/collections/SENTINEL2/products/S2A_OPER_MSI_L1C_TL_MTI__20170308T220244_A008933_T11SLT_N02.04", json.read("$.products[0].href"));
        Assert.assertEquals("http://localhost:8080/geoserver/oseo/search?uid=S2A_OPER_MSI_L1C_TL_MTI__20170308T220244_A008933_T11SLT_N02.04", json.read("$.products[0].rss"));
    }

    @Test
    public void testGetProductsPaging() throws Exception {
        DocumentContext json = getAsJSONPath("/rest/oseo/collections/SENTINEL2/products?offset=1&limit=1", 200);
        Assert.assertEquals(1, json.read("$.products.*", List.class).size());
        // check the first (sorted alphabetically, it should be stable)
        Assert.assertEquals("S2A_OPER_MSI_L1C_TL_SGS__20160117T141030_A002979_T32TPL_N02.01", json.read("$.products[0].id"));
        Assert.assertEquals("http://localhost:8080/geoserver/rest/oseo/collections/SENTINEL2/products/S2A_OPER_MSI_L1C_TL_SGS__20160117T141030_A002979_T32TPL_N02.01", json.read("$.products[0].href"));
        Assert.assertEquals("http://localhost:8080/geoserver/oseo/search?uid=S2A_OPER_MSI_L1C_TL_SGS__20160117T141030_A002979_T32TPL_N02.01", json.read("$.products[0].rss"));
    }

    @Test
    public void testGetProductsPagingValidation() throws Exception {
        MockHttpServletResponse response = getAsServletResponse("/rest/oseo/collections/SENTINEL2/products?offset=-1");
        Assert.assertEquals(400, response.getStatus());
        Assert.assertThat(response.getErrorMessage(), Matchers.containsString("offset"));
        response = getAsServletResponse("/rest/oseo/collections/SENTINEL2/products?limit=-1");
        Assert.assertEquals(400, response.getStatus());
        Assert.assertThat(response.getErrorMessage(), Matchers.containsString("limit"));
        response = getAsServletResponse("/rest/oseo/collections/SENTINEL2/products?limit=1000");
        Assert.assertEquals(400, response.getStatus());
        Assert.assertThat(response.getErrorMessage(), Matchers.containsString("limit"));
    }

    @Test
    public void testNonExistingProduct() throws Exception {
        MockHttpServletResponse response = getAsServletResponse("/rest/oseo/collections/SENTINEL2/products/foobar");
        Assert.assertEquals(404, response.getStatus());
        Assert.assertThat(response.getContentAsString(), Matchers.containsString("foobar"));
    }

    @Test
    public void testGetProduct() throws Exception {
        DocumentContext json = getAsJSONPath("/rest/oseo/collections/SENTINEL2/products/S2A_OPER_MSI_L1C_TL_SGS__20160117T141030_A002979_T32TPL_N02.01", 200);
        Assert.assertEquals("S2A_OPER_MSI_L1C_TL_SGS__20160117T141030_A002979_T32TPL_N02.01", json.read("$.id"));
        Assert.assertEquals("Feature", json.read("$.type"));
        Assert.assertEquals("S2A_OPER_MSI_L1C_TL_SGS__20160117T141030_A002979_T32TPL_N02.01", json.read("$.properties['eop:identifier']"));
        Assert.assertEquals("SENTINEL2", json.read("$.properties['eop:parentIdentifier']"));
        Assert.assertEquals("http://localhost:8080/geoserver/rest/oseo/collections/SENTINEL2/products/S2A_OPER_MSI_L1C_TL_SGS__20160117T141030_A002979_T32TPL_N02.01/ogcLinks", json.read("$.properties['ogcLinksHref']"));
        Assert.assertEquals("http://localhost:8080/geoserver/rest/oseo/collections/SENTINEL2/products/S2A_OPER_MSI_L1C_TL_SGS__20160117T141030_A002979_T32TPL_N02.01/metadata", json.read("$.properties['metadataHref']"));
        Assert.assertEquals("http://localhost:8080/geoserver/rest/oseo/collections/SENTINEL2/products/S2A_OPER_MSI_L1C_TL_SGS__20160117T141030_A002979_T32TPL_N02.01/description", json.read("$.properties['descriptionHref']"));
        Assert.assertEquals("http://localhost:8080/geoserver/rest/oseo/collections/SENTINEL2/products/S2A_OPER_MSI_L1C_TL_SGS__20160117T141030_A002979_T32TPL_N02.01/thumbnail", json.read("$.properties['thumbnailHref']"));
        Assert.assertEquals("http://localhost:8080/geoserver/rest/oseo/collections/SENTINEL2/products/S2A_OPER_MSI_L1C_TL_SGS__20160117T141030_A002979_T32TPL_N02.01/granules", json.read("$.properties['granulesHref']"));
    }

    @Test
    public void testGetAtmosphericProduct() throws Exception {
        DocumentContext json = getAsJSONPath("/rest/oseo/collections/SAS1/products/SAS1_20180226102021.01", 200);
        Assert.assertEquals("SAS1_20180226102021.01", json.read("$.id"));
        Assert.assertEquals("Feature", json.read("$.type"));
        Assert.assertEquals("SAS1_20180226102021.01", json.read("$.properties['eop:identifier']"));
        Assert.assertEquals("SAS1", json.read("$.properties['eop:parentIdentifier']"));
        Assert.assertEquals(jsonArray("O3", "O3", "CO2"), json.read("$.properties['atm:species']"));
        Assert.assertEquals(jsonArray(1000.0, 2000.0, 0.0), json.read("$.properties['atm:verticalRange']"));
    }

    @Test
    public void testCreateProduct() throws Exception {
        MockHttpServletResponse response = postAsServletResponse("rest/oseo/collections/SENTINEL2/products", getTestData("/product.json"), MediaType.APPLICATION_JSON_VALUE);
        Assert.assertEquals(201, response.getStatus());
        Assert.assertEquals(("http://localhost:8080/geoserver/rest/oseo/collections/SENTINEL2/products/" + (ProductsControllerTest.PRODUCT_CREATE_UPDATE_ID)), response.getHeader("location"));
        // check it's really there
        assertProduct("2018-01-01T00:00:00.000+0000", "2018-01-01T00:00:00.000+0000");
    }

    @Test
    public void testCreateAtmosphericProduct() throws Exception {
        MockHttpServletResponse response = postAsServletResponse("rest/oseo/collections/SAS1/products", getTestData("/product-atm.json"), MediaType.APPLICATION_JSON_VALUE);
        Assert.assertEquals(201, response.getStatus());
        Assert.assertEquals(("http://localhost:8080/geoserver/rest/oseo/collections/SAS1/products/" + (ProductsControllerTest.PRODUCT_ATM_CREATE_UPDATE_ID)), response.getHeader("location"));
        // check it's really there
        DocumentContext json = getAsJSONPath(("/rest/oseo/collections/SAS1/products/" + (ProductsControllerTest.PRODUCT_ATM_CREATE_UPDATE_ID)), 200);
        Assert.assertEquals(ProductsControllerTest.PRODUCT_ATM_CREATE_UPDATE_ID, json.read("$.id"));
        Assert.assertEquals("Feature", json.read("$.type"));
        Assert.assertEquals("SAS1", json.read("$.properties['eop:parentIdentifier']"));
        Assert.assertEquals("NOMINAL", json.read("$.properties['eop:acquisitionType']"));
        Assert.assertEquals(Integer.valueOf(65), json.read("$.properties['eop:orbitNumber']"));
        Assert.assertEquals("2018-01-01T00:00:00.000+0000", json.read("$.properties['timeStart']"));
        Assert.assertEquals("2018-01-01T00:00:00.000+0000", json.read("$.properties['timeEnd']"));
        Assert.assertEquals("EPSG:32632", json.read("$.properties['crs']"));
        Assert.assertEquals(jsonArray("O2", "O2", "NO3", "NO3"), json.read("$.properties['atm:species']"));
        Assert.assertEquals(jsonArray(250.0, 500.0, 250.0, 500.0), json.read("$.properties['atm:verticalRange']"));
        SimpleFeature sf = new FeatureJSON().readFeature(json.jsonString());
        ReferencedEnvelope bounds = ReferencedEnvelope.reference(sf.getBounds());
        Assert.assertTrue(new Envelope((-180), 180, (-90), 90).equals(bounds));
    }

    @Test
    public void testCreateProductInCustomCollection() throws Exception {
        MockHttpServletResponse response = postAsServletResponse("rest/oseo/collections/gsTestCollection/products", getTestData("/product-custom-class.json"), MediaType.APPLICATION_JSON_VALUE);
        Assert.assertEquals(201, response.getStatus());
        Assert.assertEquals("http://localhost:8080/geoserver/rest/oseo/collections/gsTestCollection/products/GS_TEST_PRODUCT.02", response.getHeader("location"));
        // check it's really there
        DocumentContext json = getAsJSONPath("rest/oseo/collections/gsTestCollection/products/GS_TEST_PRODUCT.02", 200);
        Assert.assertEquals("GS_TEST_PRODUCT.02", json.read("$.id"));
        Assert.assertEquals("Feature", json.read("$.type"));
        Assert.assertEquals("gsTestCollection", json.read("$.properties['eop:parentIdentifier']"));
        Assert.assertEquals("NOMINAL", json.read("$.properties['eop:acquisitionType']"));
        Assert.assertEquals(Integer.valueOf(65), json.read("$.properties['eop:orbitNumber']"));
        Assert.assertEquals("2018-01-01T00:00:00.000+0000", json.read("$.properties['timeStart']"));
        Assert.assertEquals("2018-01-01T00:00:00.000+0000", json.read("$.properties['timeEnd']"));
        Assert.assertEquals("123456", json.read("$.properties['gs:test']"));
        SimpleFeature sf = new FeatureJSON().readFeature(json.jsonString());
        ReferencedEnvelope bounds = ReferencedEnvelope.reference(sf.getBounds());
        Assert.assertTrue(new Envelope((-180), 180, (-90), 90).equals(bounds));
    }

    @Test
    public void testUpdateProduct() throws Exception {
        // create the product
        MockHttpServletResponse response = postAsServletResponse("rest/oseo/collections/SENTINEL2/products", getTestData("/product.json"), MediaType.APPLICATION_JSON_VALUE);
        Assert.assertEquals(201, response.getStatus());
        Assert.assertEquals(("http://localhost:8080/geoserver/rest/oseo/collections/SENTINEL2/products/" + (ProductsControllerTest.PRODUCT_CREATE_UPDATE_ID)), response.getHeader("location"));
        // grab the JSON to modify some bits
        JSONObject feature = ((JSONObject) (getAsJSON(("rest/oseo/collections/SENTINEL2/products/" + (ProductsControllerTest.PRODUCT_CREATE_UPDATE_ID)))));
        JSONObject properties = feature.getJSONObject("properties");
        properties.element("eop:orbitNumber", 66);
        properties.element("timeStart", "2017-01-01T00:00:00Z");
        // send it back
        response = putAsServletResponse(("rest/oseo/collections/SENTINEL2/products/" + (ProductsControllerTest.PRODUCT_CREATE_UPDATE_ID)), feature.toString(), "application/json");
        Assert.assertEquals(200, response.getStatus());
        // check the changes
        DocumentContext json = getAsJSONPath(("rest/oseo/collections/SENTINEL2/products/" + (ProductsControllerTest.PRODUCT_CREATE_UPDATE_ID)), 200);
        Assert.assertEquals(Integer.valueOf(66), json.read("$.properties['eop:orbitNumber']"));
        Assert.assertEquals("2017-01-01T00:00:00.000+0000", json.read("$.properties['timeStart']"));
    }

    @Test
    public void testUpdateAtmosphericProduct() throws Exception {
        // create the product
        MockHttpServletResponse response = postAsServletResponse("rest/oseo/collections/SAS1/products", getTestData("/product-atm.json"), MediaType.APPLICATION_JSON_VALUE);
        Assert.assertEquals(201, response.getStatus());
        Assert.assertEquals(("http://localhost:8080/geoserver/rest/oseo/collections/SAS1/products/" + (ProductsControllerTest.PRODUCT_ATM_CREATE_UPDATE_ID)), response.getHeader("location"));
        // grab the JSON to modify some bits
        JSONObject feature = ((JSONObject) (getAsJSON(("rest/oseo/collections/SAS1/products/" + (ProductsControllerTest.PRODUCT_ATM_CREATE_UPDATE_ID)))));
        JSONObject properties = feature.getJSONObject("properties");
        properties.element("atm:species", Arrays.asList("A", "B", "C", "D"));
        // send it back
        response = putAsServletResponse(("rest/oseo/collections/SAS1/products/" + (ProductsControllerTest.PRODUCT_ATM_CREATE_UPDATE_ID)), feature.toString(), "application/json");
        Assert.assertEquals(200, response.getStatus());
        // check the changes
        DocumentContext json = getAsJSONPath(("rest/oseo/collections/SAS1/products/" + (ProductsControllerTest.PRODUCT_ATM_CREATE_UPDATE_ID)), 200);
        Assert.assertEquals(jsonArray("A", "B", "C", "D"), json.read("$.properties['atm:species']"));
    }

    @Test
    public void testDeleteProduct() throws Exception {
        // create the product
        MockHttpServletResponse response = postAsServletResponse("rest/oseo/collections/SENTINEL2/products", getTestData("/product.json"), MediaType.APPLICATION_JSON_VALUE);
        Assert.assertEquals(201, response.getStatus());
        Assert.assertEquals(("http://localhost:8080/geoserver/rest/oseo/collections/SENTINEL2/products/" + (ProductsControllerTest.PRODUCT_CREATE_UPDATE_ID)), response.getHeader("location"));
        // it's there
        getAsJSONPath(("rest/oseo/collections/SENTINEL2/products/" + (ProductsControllerTest.PRODUCT_CREATE_UPDATE_ID)), 200);
        // and now kill the poor beast
        response = deleteAsServletResponse(("rest/oseo/collections/SENTINEL2/products/" + (ProductsControllerTest.PRODUCT_CREATE_UPDATE_ID)));
        Assert.assertEquals(200, response.getStatus());
        // no more there
        response = getAsServletResponse(("rest/oseo/collections/SENTINEL2/products/" + (ProductsControllerTest.PRODUCT_CREATE_UPDATE_ID)));
        Assert.assertEquals(404, response.getStatus());
    }

    @Test
    public void testGetProductLinks() throws Exception {
        DocumentContext json = getAsJSONPath("/rest/oseo/collections/SENTINEL2/products/S2A_OPER_MSI_L1C_TL_SGS__20160117T141030_A002979_T32TPL_N02.01/ogcLinks", 200);
        Assert.assertEquals("http://www.opengis.net/spec/owc/1.0/req/atom/wms", json.read("$.links[0].offering"));
        Assert.assertEquals("GET", json.read("$.links[0].method"));
        Assert.assertEquals("GetCapabilities", json.read("$.links[0].code"));
        Assert.assertEquals("application/xml", json.read("$.links[0].type"));
        Assert.assertEquals("${BASE_URL}/sentinel2/ows?service=wms&version=1.3.0&request=GetCapabilities", json.read("$.links[0].href"));
    }

    @Test
    public void testPutProductLinks() throws Exception {
        testCreateProduct();
        // create the links
        MockHttpServletResponse response = putAsServletResponse("rest/oseo/collections/SENTINEL2/products/S2A_OPER_MSI_L1C_TL_SGS__20180101T000000_A006640_T32TPP_N02.04/ogcLinks", getTestData("/product-links.json"), MediaType.APPLICATION_JSON_VALUE);
        Assert.assertEquals(200, response.getStatus());
        // check they are there
        assertProductLinks("application/xml");
    }

    @Test
    public void testDeleteProductLinks() throws Exception {
        testPutProductLinks();
        // delete the links
        MockHttpServletResponse response = deleteAsServletResponse("rest/oseo/collections/SENTINEL2/products/S2A_OPER_MSI_L1C_TL_SGS__20180101T000000_A006640_T32TPP_N02.04/ogcLinks");
        Assert.assertEquals(200, response.getStatus());
        // check they are gone
        response = getAsServletResponse("rest/oseo/collections/SENTINEL2/products/S2A_OPER_MSI_L1C_TL_SGS__20180101T000000_A006640_T32TPP_N02.04/ogcLinks");
        Assert.assertEquals(404, response.getStatus());
    }

    @Test
    public void testGetProductMetadata() throws Exception {
        MockHttpServletResponse response = getAsServletResponse("/rest/oseo/collections/SENTINEL2/products/S2A_OPER_MSI_L1C_TL_SGS__20160117T141030_A002979_T32TPL_N02.01/metadata");
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("text/xml", response.getContentType());
        Assert.assertThat(response.getContentAsString(), Matchers.both(Matchers.containsString("opt:EarthObservation")).and(Matchers.containsString("S2A_OPER_MSI_L1C_TL_SGS__20160117T141030_A002979_T32TPL_N02.01")));
    }

    @Test
    public void testPutProductMetadata() throws Exception {
        testCreateProduct();
        // create the metadata
        MockHttpServletResponse response = putAsServletResponse("rest/oseo/collections/SENTINEL2/products/S2A_OPER_MSI_L1C_TL_SGS__20180101T000000_A006640_T32TPP_N02.04/metadata", getTestData("/product-metadata.xml"), MediaType.TEXT_XML_VALUE);
        Assert.assertEquals(200, response.getStatus());
        // grab and check
        assertProductMetadata("<eop:orbitType>LEO</eop:orbitType>");
    }

    @Test
    public void testDeleteProductMetadata() throws Exception {
        // creates the product and adds the metadata
        testPutProductMetadata();
        // now remove
        MockHttpServletResponse response = deleteAsServletResponse("rest/oseo/collections/SENTINEL2/products/S2A_OPER_MSI_L1C_TL_SGS__20180101T000000_A006640_T32TPP_N02.04/metadata");
        Assert.assertEquals(200, response.getStatus());
        // check it's not there anymore
        response = getAsServletResponse("rest/oseo/collections/SENTINEL2/products/S2A_OPER_MSI_L1C_TL_SGS__20180101T000000_A006640_T32TPP_N02.04/metadata");
        Assert.assertEquals(404, response.getStatus());
    }

    @Test
    public void testGetProductDescription() throws Exception {
        MockHttpServletResponse response = getAsServletResponse("/rest/oseo/collections/SENTINEL2/products/S2A_OPER_MSI_L1C_TL_SGS__20160117T141030_A002979_T32TPL_N02.01/description");
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("text/html", response.getContentType());
        Assert.assertThat(response.getContentAsString(), Matchers.both(Matchers.containsString("<table>")).and(Matchers.containsString("2016-01-17T10:10:30.743Z / 2016-01-17T10:10:30.743Z")));
    }

    @Test
    public void testPutProductDescription() throws Exception {
        testCreateProduct();
        // create the description
        MockHttpServletResponse response = putAsServletResponse("rest/oseo/collections/SENTINEL2/products/S2A_OPER_MSI_L1C_TL_SGS__20180101T000000_A006640_T32TPP_N02.04/description", getTestData("/product-description.html"), MediaType.TEXT_HTML_VALUE);
        Assert.assertEquals(200, response.getStatus());
        // grab and check
        assertProductDescription("2016-09-29T10:20:22.026Z / 2016-09-29T10:23:44.107Z");
    }

    @Test
    public void testDeleteCollectionDescription() throws Exception {
        // creates the collection and adds the metadata
        testPutProductDescription();
        // now remove
        MockHttpServletResponse response = deleteAsServletResponse("rest/oseo/collections/SENTINEL2/products/S2A_OPER_MSI_L1C_TL_SGS__20180101T000000_A006640_T32TPP_N02.04/description");
        Assert.assertEquals(200, response.getStatus());
        // check it's not there anymore
        response = getAsServletResponse("rest/oseo/collections/SENTINEL2/products/S2A_OPER_MSI_L1C_TL_SGS__20180101T000000_A006640_T32TPP_N02.04/description");
        Assert.assertEquals(404, response.getStatus());
    }

    @Test
    public void testGetProductThumbnail() throws Exception {
        // just checking we get an image indeed from the only product that has a thumb
        getAsImage("/rest/oseo/collections/SENTINEL2/products/S2A_OPER_MSI_L1C_TL_SGS__20160117T141030_A002979_T33TWH_N02.01/thumbnail", "image/jpeg");
    }

    @Test
    public void testGetProductMissingThumbnail() throws Exception {
        // this one does not have a thumbnail
        MockHttpServletResponse response = getAsServletResponse("/rest/oseo/collections/SENTINEL2/products/S2A_OPER_MSI_L1C_TL_SGS__20160117T141030_A002979_T32TPL_N02.01/thumbnail");
        Assert.assertEquals(404, response.getStatus());
        Assert.assertThat(response.getContentAsString(), Matchers.containsString("S2A_OPER_MSI_L1C_TL_SGS__20160117T141030_A002979_T32TPL_N02.01"));
    }

    @Test
    public void testPutProductThumbnail() throws Exception {
        testCreateProduct();
        // create the image
        MockHttpServletResponse response = putAsServletResponse("rest/oseo/collections/SENTINEL2/products/S2A_OPER_MSI_L1C_TL_SGS__20180101T000000_A006640_T32TPP_N02.04/thumbnail", getTestData("/product-thumb.jpeg"), MediaType.IMAGE_JPEG_VALUE);
        Assert.assertEquals(200, response.getStatus());
        // grab and check
        assertProductThumbnail("./src/test/resources/product-thumb.jpeg");
    }

    @Test
    public void testDeleteProductThumbnail() throws Exception {
        testPutProductThumbnail();
        // now delete it
        MockHttpServletResponse response = deleteAsServletResponse("rest/oseo/collections/SENTINEL2/products/S2A_OPER_MSI_L1C_TL_SGS__20180101T000000_A006640_T32TPP_N02.04/thumbnail");
        Assert.assertEquals(200, response.getStatus());
        // no more there now
        response = getAsServletResponse("rest/oseo/collections/SENTINEL2/products/S2A_OPER_MSI_L1C_TL_SGS__20180101T000000_A006640_T32TPP_N02.04/thumbnail");
        Assert.assertEquals(404, response.getStatus());
    }

    @Test
    public void testGetProductGranules() throws Exception {
        DocumentContext json = getAsJSONPath("/rest/oseo/collections/SENTINEL2/products/S2A_OPER_MSI_L1C_TL_SGS__20160117T141030_A002979_T32TPL_N02.01/granules", 200);
        Assert.assertEquals("FeatureCollection", json.read("$.type"));
        Assert.assertEquals("Feature", json.read("$.features[0].type"));
        Assert.assertEquals("Polygon", json.read("$.features[0].geometry.type"));
        Assert.assertEquals("/efs/geoserver_data/coverages/sentinel/california/S2A_OPER_MSI_L1C_TL_SGS__20160117T141030_A002979_T32TPL_N02.01.tif", json.read("$.features[0].properties.location"));
    }

    @Test
    public void testPutProductGranules() throws Exception {
        testCreateProduct();
        // add the granules
        MockHttpServletResponse response = putAsServletResponse("/rest/oseo/collections/SENTINEL2/products/S2A_OPER_MSI_L1C_TL_SGS__20180101T000000_A006640_T32TPP_N02.04/granules", getTestData("/product-granules.json"), MediaType.APPLICATION_JSON_VALUE);
        Assert.assertEquals(200, response.getStatus());
        assertProductGranules("/efs/geoserver_data/coverages/sentinel/california/R1C1.tif");
    }

    @Test
    public void testPutProductGranulesWithBands() throws Exception {
        testCreateProduct();
        // add the granules
        MockHttpServletResponse response = putAsServletResponse("/rest/oseo/collections/SENTINEL2/products/S2A_OPER_MSI_L1C_TL_SGS__20180101T000000_A006640_T32TPP_N02.04/granules", getTestData("/product-granules-bands.json"), MediaType.APPLICATION_JSON_VALUE);
        Assert.assertEquals(200, response.getStatus());
        assertProductGranules("/efs/geoserver_data/coverages/sentinel/california/R1C1.tif");
        DocumentContext json = getAsJSONPath("/rest/oseo/collections/SENTINEL2/products/S2A_OPER_MSI_L1C_TL_SGS__20180101T000000_A006640_T32TPP_N02.04/granules", 200);
        Assert.assertEquals("B1", json.read("$.features[0].properties.band"));
        Assert.assertEquals("B2", json.read("$.features[1].properties.band"));
    }

    @Test
    public void testDeleteProductGranules() throws Exception {
        testPutProductDescription();
        // now delete it
        MockHttpServletResponse response = deleteAsServletResponse("rest/oseo/collections/SENTINEL2/products/S2A_OPER_MSI_L1C_TL_SGS__20180101T000000_A006640_T32TPP_N02.04/granules");
        Assert.assertEquals(200, response.getStatus());
        // no more there now
        DocumentContext json = getAsJSONPath("rest/oseo/collections/SENTINEL2/products/S2A_OPER_MSI_L1C_TL_SGS__20180101T000000_A006640_T32TPP_N02.04/granules", 200);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("FeatureCollection", json.read("$.type"));
        Assert.assertEquals(Integer.valueOf(0), json.read("$.features.length()"));
    }

    @Test
    public void testCreateProductAsZip() throws Exception {
        // build all possible combinations of elements in the zip and check they all work
        HashSet<ProductPart> allProducts = new HashSet(Arrays.asList(Product, Description, Metadata, Thumbnail, OwsLinks, Granules));
        Set<Set<ProductPart>> sets = Sets.powerSet(allProducts);
        for (Set<ProductPart> parts : sets) {
            if (parts.isEmpty()) {
                continue;
            }
            LOGGER.info(("Testing zip product creation with parts:" + parts));
            cleanupTestProduct();
            testCreateProductAsZip(parts);
        }
    }

    @Test
    public void testUpdateProductAsZipFromFullProduct() throws Exception {
        // prepare a full initial product
        Set<ProductPart> allProductParts = new java.util.LinkedHashSet(Arrays.asList(Product, Description, Metadata, Thumbnail, OwsLinks, Granules));
        cleanupTestProduct();
        Assert.assertEquals(CREATED.value(), createProductAsZip(allProductParts).getStatus());
        // update one items at a time
        for (ProductPart part : allProductParts) {
            testUpdateProductAsZip(Arrays.asList(part));
        }
    }

    @Test
    public void testUpdateAllProductPartsAsZipFromFullProduct() throws Exception {
        // prepare a full initial product
        Set<ProductPart> allProductParts = new java.util.LinkedHashSet(Arrays.asList(Product, Description, Metadata, Thumbnail, OwsLinks, Granules));
        cleanupTestProduct();
        Assert.assertEquals(CREATED.value(), createProductAsZip(allProductParts).getStatus());
        // update all in one shot
        testUpdateProductAsZip(allProductParts);
    }

    @Test
    public void testUpdateProductAsZipFromBasicProduct() throws Exception {
        // prepare a basic initial product
        Set<ProductPart> initialProduct = new HashSet(Arrays.asList(Product));
        cleanupTestProduct();
        Assert.assertEquals(CREATED.value(), createProductAsZip(initialProduct).getStatus());
        // update/add one item at a time
        Set<ProductPart> allProductParts = new java.util.LinkedHashSet(Arrays.asList(Product, Description, Metadata, Thumbnail, OwsLinks, Granules));
        for (ProductPart part : allProductParts) {
            testUpdateProductAsZip(Arrays.asList(part));
        }
    }
}

