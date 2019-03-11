/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.opensearch.rest;


import com.jayway.jsonpath.DocumentContext;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import org.geoserver.catalog.LayerInfo;
import org.geotools.image.test.ImageAssert;
import org.geotools.styling.ChannelSelection;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


public class CollectionLayerTest extends OSEORestTestSupport {
    private String resourceBase;

    @Test
    public void testCreateCollectionSimpleLayer() throws Exception {
        // setup the granules
        setupDefaultLayer("/test123-product-granules-rgb.json", "/test123-layer-simple.json", "gs", Boolean.FALSE);
        // check the configuration elements are there too
        LayerInfo layer = validateBasicLayerStructure("gs", "test123", new String[]{ "RED_BAND", "GREEN_BAND", "BLUE_BAND" });
        // ... its style is the default one
        Assert.assertThat(layer.getDefaultStyle().getName(), Matchers.equalTo("raster"));
        BufferedImage image = getAsImage("wms/reflect?layers=gs:test123&format=image/png&width=200", "image/png");
        File expected = new File("src/test/resources/test123-simple-rgb.png");
        ImageAssert.assertEquals(expected, image, 1000);
    }

    @Test
    public void testCreateCollectionSimpleLayerTestWorkspace() throws Exception {
        // setup the granules
        setupDefaultLayer("/test123-product-granules-rgb.json", "/test123-layer-simple-testws.json", "test", Boolean.FALSE);
        // check the configuration elements are there too
        LayerInfo layer = validateBasicLayerStructure("test", "test123", new String[]{ "RED_BAND", "GREEN_BAND", "BLUE_BAND" });
        // ... its style is the default one
        Assert.assertThat(layer.getDefaultStyle().getName(), Matchers.equalTo("raster"));
        BufferedImage image = getAsImage("wms/reflect?layers=test:test123&format=image/png&width=200", "image/png");
        File expected = new File("src/test/resources/test123-simple-rgb.png");
        ImageAssert.assertEquals(expected, image, 1000);
    }

    @Test
    public void testCreateCollectionSimpleLayerWithCustomStyle() throws Exception {
        // setup the granules
        setupDefaultLayer("/test123-product-granules-rgb.json", "/test123-layer-simple-graystyle.json", "gs", Boolean.FALSE);
        LayerInfo layer = validateBasicLayerStructure("gs", "test123", new String[]{ "RED_BAND", "GREEN_BAND", "BLUE_BAND" });
        // ... its style is a gray one based on the RED band
        Assert.assertThat(layer.getDefaultStyle().prefixedName(), Matchers.equalTo("gs:test123"));
        ChannelSelection cs = getChannelSelection(layer);
        Assert.assertNull(cs.getRGBChannels()[0]);
        Assert.assertNull(cs.getRGBChannels()[1]);
        Assert.assertNull(cs.getRGBChannels()[2]);
        Assert.assertEquals("1", cs.getGrayChannel().getChannelName().evaluate(null, String.class));
        BufferedImage image = getAsImage("wms/reflect?layers=gs:test123&format=image/png&width=200", "image/png");
        File expected = new File("src/test/resources/test123-simple-gray.png");
        ImageAssert.assertEquals(expected, image, 1000);
    }

    @Test
    public void testCreateCollectionMultiband() throws Exception {
        // setup the granules
        setupDefaultLayer("/test123-product-granules-multiband.json", "/test123-layer-multiband.json", "gs", Boolean.TRUE);
        // check the configuration elements are there too
        LayerInfo layer = validateBasicLayerStructure("gs", "test123", new String[]{ "B02", "B03", "B04", "B08" });
        // ... its style is a RGB one based on the B2, B3, B4
        Assert.assertThat(layer.getDefaultStyle().prefixedName(), Matchers.equalTo("gs:test123"));
        ChannelSelection cs = getChannelSelection(layer);
        Assert.assertEquals("4", cs.getRGBChannels()[0].getChannelName().evaluate(null, String.class));
        Assert.assertEquals("2", cs.getRGBChannels()[1].getChannelName().evaluate(null, String.class));
        Assert.assertEquals("1", cs.getRGBChannels()[2].getChannelName().evaluate(null, String.class));
        Assert.assertNull(cs.getGrayChannel());
        BufferedImage image = getAsImage("wms/reflect?layers=gs:test123&format=image/png&width=200", "image/png");
        File expected = new File("src/test/resources/test123-multiband.png");
        ImageAssert.assertEquals(expected, image, 1000);
    }

    /**
     * This test checks it's possible to change an existing configuration and stuff still works
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testModifyConfigurationSingleBand() throws Exception {
        // setup and check one collection
        testCreateCollectionSimpleLayer();
        // now go and setup another single collection
        testCreateCollectionMultiband();
        // switch to multiband
        testCreateCollectionMultiband();
        // and back to single
        testCreateCollectionSimpleLayer();
    }

    @Test
    public void testBandsFlagsAll() throws Exception {
        setupDefaultLayer("/test123-product-granules-bands-flags.json", "/test123-layer-bands-flags-all.json", "gs", Boolean.TRUE);
        // check the configuration elements are there too
        LayerInfo layer = validateBasicLayerStructure("gs", "test123", new String[]{ "VNIR_0", "VNIR_1", "VNIR_2", "QUALITY", "CLOUDSHADOW", "HAZE", "SNOW" });
        // ... its style has been generated
        Assert.assertThat(layer.getDefaultStyle().prefixedName(), Matchers.equalTo("gs:test123"));
        // ... and it uses all VNIR bands
        ChannelSelection cs = getChannelSelection(layer);
        Assert.assertEquals("1", cs.getRGBChannels()[0].getChannelName().evaluate(null, String.class));
        Assert.assertEquals("2", cs.getRGBChannels()[1].getChannelName().evaluate(null, String.class));
        Assert.assertEquals("3", cs.getRGBChannels()[2].getChannelName().evaluate(null, String.class));
        Assert.assertNull(cs.getGrayChannel());
        BufferedImage image = getAsImage("wms/reflect?layers=gs:test123&format=image/png&width=200", "image/png");
        File expected = new File("src/test/resources/test123-vnir.png");
        ImageAssert.assertEquals(expected, image, 1000);
    }

    @Test
    public void testBandsFlagsMix() throws Exception {
        setupDefaultLayer("/test123-product-granules-bands-flags.json", "/test123-layer-bands-flags-browseMix.json", "gs", Boolean.TRUE);
        // check the configuration elements are there too
        LayerInfo layer = validateBasicLayerStructure("gs", "test123", new String[]{ "VNIR_0", "VNIR_1", "VNIR_2", "QUALITY", "CLOUDSHADOW", "HAZE", "SNOW" });
        // ... its style has been generated
        Assert.assertThat(layer.getDefaultStyle().prefixedName(), Matchers.equalTo("gs:test123"));
        // ... and it uses all two vnir bands and a flag
        ChannelSelection cs = getChannelSelection(layer);
        Assert.assertEquals("1", cs.getRGBChannels()[0].getChannelName().evaluate(null, String.class));
        Assert.assertEquals("2", cs.getRGBChannels()[1].getChannelName().evaluate(null, String.class));
        Assert.assertEquals("7", cs.getRGBChannels()[2].getChannelName().evaluate(null, String.class));
        Assert.assertNull(cs.getGrayChannel());
        BufferedImage image = getAsImage("wms/reflect?layers=gs:test123&format=image/png&width=200", "image/png");
        File expected = new File("src/test/resources/test123-vnir-snow.png");
        ImageAssert.assertEquals(expected, image, 1000);
    }

    @Test
    public void testBandsFlagsGrayFlag() throws Exception {
        setupDefaultLayer("/test123-product-granules-bands-flags.json", "/test123-layer-bands-flags-grayFlag.json", "gs", Boolean.TRUE);
        // check the configuration elements are there too
        LayerInfo layer = validateBasicLayerStructure("gs", "test123", new String[]{ "VNIR_0", "VNIR_1", "VNIR_2", "QUALITY", "CLOUDSHADOW", "HAZE", "SNOW" });
        // ... its style has been generated
        Assert.assertThat(layer.getDefaultStyle().prefixedName(), Matchers.equalTo("gs:test123"));
        // ... and it uses only a gray band, the snow flag
        ChannelSelection cs = getChannelSelection(layer);
        Assert.assertNull(cs.getRGBChannels()[0]);
        Assert.assertNull(cs.getRGBChannels()[1]);
        Assert.assertNull(cs.getRGBChannels()[2]);
        Assert.assertEquals("7", cs.getGrayChannel().getChannelName().evaluate(null, String.class));
        // the image is almost black, but not fully
        BufferedImage image = getAsImage("wms/reflect?layers=gs:test123&format=image/png&width=200", "image/png");
        File expected = new File("src/test/resources/test123-gray-snow.png");
        ImageAssert.assertEquals(expected, image, 1000);
    }

    @Test
    public void testGetCollectionDefaultLayer() throws Exception {
        DocumentContext json = getAsJSONPath("/rest/oseo/collections/SENTINEL2/layer", 200);
        Assert.assertEquals("gs", json.read("$.workspace"));
        Assert.assertEquals("sentinel2", json.read("$.layer"));
        Assert.assertEquals(Integer.valueOf(12), json.read("$.bands.length()"));
        Assert.assertEquals(Boolean.TRUE, json.read("$.separateBands"));
        Assert.assertEquals("B01", json.read("$.bands[0]"));
        Assert.assertEquals(Integer.valueOf(3), json.read("$.browseBands.length()"));
        Assert.assertEquals("B04", json.read("$.browseBands[0]"));
        Assert.assertEquals(Boolean.TRUE, json.read("$.heterogeneousCRS"));
        Assert.assertEquals("EPSG:4326", json.read("$.mosaicCRS"));
    }

    @Test
    public void testGetCollectionLayers() throws Exception {
        DocumentContext json = getAsJSONPath("/rest/oseo/collections/LANDSAT8/layers", 200);
        Assert.assertEquals(Integer.valueOf(2), json.read("$.layers.length()"));
        Assert.assertEquals(Arrays.asList("http://localhost:8080/geoserver/rest/oseo/collections/LANDSAT8/layers/landsat8-SINGLE"), json.read("$.layers[?(@.name == 'landsat8-SINGLE')].href"));
        Assert.assertEquals(Arrays.asList("http://localhost:8080/geoserver/rest/oseo/collections/LANDSAT8/layers/landsat8-SEPARATE"), json.read("$.layers[?(@.name == 'landsat8-SEPARATE')].href"));
    }

    @Test
    public void testGetCollectionLayerByName() throws Exception {
        DocumentContext json = getAsJSONPath("/rest/oseo/collections/LANDSAT8/layers/landsat8-SINGLE", 200);
        Assert.assertEquals("gs", json.read("$.workspace"));
        Assert.assertEquals("landsat8-SINGLE", json.read("$.layer"));
        Assert.assertEquals(Boolean.FALSE, json.read("$.separateBands"));
        Assert.assertEquals(Boolean.TRUE, json.read("$.heterogeneousCRS"));
        Assert.assertEquals("EPSG:4326", json.read("$.mosaicCRS"));
    }

    @Test
    public void testDeleteCollectionLayer() throws Exception {
        // create something to delete
        setupDefaultLayer("/test123-product-granules-rgb.json", "/test123-layer-simple.json", "gs", Boolean.FALSE);
        // check the GeoServer layer is there
        Assert.assertNotNull(getCatalog().getLayerByName("test123"));
        // remove
        MockHttpServletResponse response = deleteAsServletResponse("rest/oseo/collections/TEST123/layer");
        Assert.assertEquals(200, response.getStatus());
        // no more there on REST API and on catalog
        response = getAsServletResponse("rest/oseo/collections/TEST123/layer");
        Assert.assertEquals(404, response.getStatus());
        Assert.assertNull(getCatalog().getLayerByName("test123"));
    }

    @Test
    public void testAddSecondLayerByPut() throws Exception {
        // add the first layer and granules
        setupDefaultLayer("/test123-product-granules-bands-flags.json", "/test123-layer-bands-flags-grayFlag.json", "gs", Boolean.TRUE);
        // confirm on layer on the list
        DocumentContext json = getAsJSONPath("/rest/oseo/collections/TEST123/layers", 200);
        Assert.assertEquals(Integer.valueOf(1), json.read("$.layers.length()"));
        // now add another layer
        MockHttpServletResponse response = putAsServletResponse("rest/oseo/collections/TEST123/layers/test123-secondary", getTestData("/test123-layer-bands-flags-browseMix-secondary.json"), MediaType.APPLICATION_JSON_VALUE);
        Assert.assertEquals(201, response.getStatus());
        // confirm there are two layers on list now
        json = getAsJSONPath("/rest/oseo/collections/TEST123/layers", 200);
        Assert.assertEquals(Integer.valueOf(2), json.read("$.layers.length()"));
        checkTest123SecondaryLayer();
        return;
    }

    @Test
    public void testAddSecondLayerByPost() throws Exception {
        // add the first layer and granules
        setupDefaultLayer("/test123-product-granules-bands-flags.json", "/test123-layer-bands-flags-grayFlag.json", "gs", Boolean.TRUE);
        // confirm on layer on the list
        DocumentContext json = getAsJSONPath("/rest/oseo/collections/TEST123/layers", 200);
        Assert.assertEquals(Integer.valueOf(1), json.read("$.layers.length()"));
        // now add another layer
        MockHttpServletResponse response = postAsServletResponse("rest/oseo/collections/TEST123/layers", getTestData("/test123-layer-bands-flags-browseMix-secondary.json"), MediaType.APPLICATION_JSON_VALUE);
        Assert.assertEquals(201, response.getStatus());
        // confirm there are two layers on list now
        json = getAsJSONPath("/rest/oseo/collections/TEST123/layers", 200);
        Assert.assertEquals(Integer.valueOf(2), json.read("$.layers.length()"));
        // check it has been created from REST
        checkTest123SecondaryLayer();
    }

    @Test
    public void testRemoveLayer() throws Exception {
        // add two layers
        testAddSecondLayerByPost();
        getAsJSONPath("/rest/oseo/collections/TEST123/layers", 200);
        // now go and remove the first, which was the default one
        MockHttpServletResponse response = deleteAsServletResponse("rest/oseo/collections/TEST123/layers/test123");
        Assert.assertEquals(200, response.getStatus());
        // check it got removed from list
        DocumentContext json = getAsJSONPath("/rest/oseo/collections/TEST123/layers", 200);
        Assert.assertEquals(Integer.valueOf(1), json.read("$.layers.length()"));
        Assert.assertEquals(Arrays.asList("http://localhost:8080/geoserver/rest/oseo/collections/TEST123/layers/test123-secondary"), json.read("$.layers[?(@.name == 'test123-secondary')].href"));
        // check it's a 404 on direct request
        response = getAsServletResponse("rest/oseo/collections/TEST123/layers/test123");
        Assert.assertEquals(404, response.getStatus());
        // check the other layer is now the default
        json = getAsJSONPath("rest/oseo/collections/TEST123/layer", 200);
        Assert.assertEquals("gs", json.read("$.workspace"));
        Assert.assertEquals("test123-secondary", json.read("$.layer"));
        Assert.assertEquals(Boolean.TRUE, json.read("$.separateBands"));
        Assert.assertEquals(Boolean.TRUE, json.read("$.heterogeneousCRS"));
        // the image is almost black, but not fully
        BufferedImage image = getAsImage("wms/reflect?layers=gs:test123-secondary&format=image/png&width=200", "image/png");
        File expected = new File("src/test/resources/test123-vnir-snow.png");
        ImageAssert.assertEquals(expected, image, 1000);
    }

    @Test
    public void testCreateTimeRangesSimpleLayer() throws Exception {
        // setup the granules
        setupDefaultLayer("/test123-product-granules-rgb.json", "/test123-layer-timerange.json", "gs", Boolean.FALSE);
        // check the configuration elements are there too
        LayerInfo layer = validateBasicLayerStructure("gs", "test123", new String[]{ "RED_BAND", "GREEN_BAND", "BLUE_BAND" });
        // ... its style is the default one
        Assert.assertThat(layer.getDefaultStyle().getName(), Matchers.equalTo("raster"));
        // get the capabilites and check the times are indeed ranges
        Document dom = getAsDOM("wms?service=WMS&version=1.3.0&request=GetCapabilities");
        // check dimension has been declared
        assertXpathEvaluatesTo("1", "count(//wms:Layer/wms:Dimension)", dom);
        assertXpathEvaluatesTo("time", "//wms:Layer/wms:Dimension/@name", dom);
        assertXpathEvaluatesTo("ISO8601", "//wms:Layer/wms:Dimension/@units", dom);
        // check we have the extent
        assertXpathEvaluatesTo("1", "count(//wms:Layer/wms:Dimension)", dom);
        assertXpathEvaluatesTo("time", "//wms:Layer/wms:Dimension/@name", dom);
        assertXpathEvaluatesTo("2018-01-01T02:00:00Z", "//wms:Layer/wms:Dimension/@default", dom);
        assertXpathEvaluatesTo("2018-01-01T00:00:00.000Z/2018-01-01T02:00:00.000Z/PT1S", "//wms:Layer/wms:Dimension", dom);
    }

    @Test
    public void testCreateTimeRangesMultiband() throws Exception {
        // setup the granules
        setupDefaultLayer("/test123-product-granules-multiband.json", "/test123-layer-multiband-timerange.json", "gs", Boolean.TRUE);
        // check the configuration elements are there too
        LayerInfo layer = validateBasicLayerStructure("gs", "test123", new String[]{ "B02", "B03", "B04", "B08" });
        // get the capabilites and check the times are indeed ranges
        Document dom = getAsDOM("wms?service=WMS&version=1.3.0&request=GetCapabilities");
        // print(dom);
        // check dimension has been declared
        assertXpathEvaluatesTo("1", "count(//wms:Layer/wms:Dimension)", dom);
        assertXpathEvaluatesTo("time", "//wms:Layer/wms:Dimension/@name", dom);
        assertXpathEvaluatesTo("ISO8601", "//wms:Layer/wms:Dimension/@units", dom);
        // check we have the extent
        assertXpathEvaluatesTo("1", "count(//wms:Layer/wms:Dimension)", dom);
        assertXpathEvaluatesTo("time", "//wms:Layer/wms:Dimension/@name", dom);
        assertXpathEvaluatesTo("2018-01-01T02:00:00Z", "//wms:Layer/wms:Dimension/@default", dom);
        assertXpathEvaluatesTo("2018-01-01T00:00:00.000Z/2018-01-01T02:00:00.000Z/PT1S", "//wms:Layer/wms:Dimension", dom);
    }
}

