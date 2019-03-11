/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms;


import DimensionPresentation.LIST;
import ResourceInfo.TIME;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.wms.map.GIFMapResponse;
import org.geotools.data.FeatureSource;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.styling.Style;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


public class GetMapCallbackTest extends WMSDimensionsTestSupport {
    private GetMap getMap;

    @Test
    public void testStandardWorkflow() throws Exception {
        GetMapCallbackTest.TestCallback callback = new GetMapCallbackTest.TestCallback();
        getMap.setGetMapCallbacks(Arrays.asList(((GetMapCallback) (callback))));
        // request a layer group with two layers
        Document dom = getAsDOM("wms?request=reflect&layers=nature&format=rss");
        assertXpathExists("rss/channel/title[text() = 'cite:Lakes,cite:Forests']", dom);
        Assert.assertEquals(1, callback.requests.size());
        Assert.assertEquals(1, callback.mapContentsInited.size());
        Assert.assertEquals(2, callback.layers.size());
        Assert.assertEquals(1, callback.mapContents.size());
        Assert.assertEquals(1, callback.maps.size());
        Assert.assertEquals(0, callback.exceptions.size());
    }

    @Test
    public void testBreakRequest() throws Exception {
        final String message = "This layer is not allowed";
        GetMapCallbackTest.TestCallback callback = new GetMapCallbackTest.TestCallback() {
            @Override
            public Layer beforeLayer(WMSMapContent content, Layer layer) {
                throw new RuntimeException(message);
            }
        };
        getMap.setGetMapCallbacks(Arrays.asList(((GetMapCallback) (callback))));
        // request a layer group with two layers
        Document dom = getAsDOM("wms?request=reflect&layers=nature&format=rss&version=1.1.0");
        // print(dom);
        assertXpathExists("/ServiceExceptionReport", dom);
        Assert.assertEquals(1, callback.requests.size());
        Assert.assertEquals(1, callback.mapContentsInited.size());
        Assert.assertEquals(0, callback.layers.size());
        Assert.assertEquals(0, callback.mapContents.size());
        Assert.assertEquals(0, callback.maps.size());
        Assert.assertEquals(1, callback.exceptions.size());
        Assert.assertEquals(message, callback.exceptions.get(0).getMessage());
    }

    @Test
    public void testAddLayer() throws Exception {
        FeatureTypeInfo ft = getCatalog().getFeatureTypeByName(getLayerId(MockData.BRIDGES));
        FeatureSource<? extends FeatureType, ? extends Feature> fs = ft.getFeatureSource(null, null);
        Style style = getCatalog().getStyleByName("point").getStyle();
        final FeatureLayer layer = new FeatureLayer(fs, style);
        layer.setTitle("extra");
        GetMapCallbackTest.TestCallback callback = new GetMapCallbackTest.TestCallback() {
            @Override
            public WMSMapContent beforeRender(WMSMapContent mapContent) {
                mapContent.addLayer(layer);
                return super.beforeRender(mapContent);
            }
        };
        getMap.setGetMapCallbacks(Arrays.asList(((GetMapCallback) (callback))));
        // request a layer group with two layers
        Document dom = getAsDOM("wms?request=reflect&layers=nature&format=rss&version=1.1.0");
        // print(dom);
        assertXpathExists("rss/channel/title[text() = 'cite:Lakes,cite:Forests,extra']", dom);
        Assert.assertEquals(1, callback.requests.size());
        Assert.assertEquals(1, callback.mapContentsInited.size());
        Assert.assertEquals(3, callback.layers.size());
        Assert.assertEquals(1, callback.mapContents.size());
        Assert.assertEquals(1, callback.maps.size());
        Assert.assertEquals(0, callback.exceptions.size());
        Assert.assertEquals(layer, callback.layers.get(2));
    }

    @Test
    public void testRemoveLayer() throws Exception {
        GetMapCallbackTest.TestCallback callback = new GetMapCallbackTest.TestCallback() {
            @Override
            public Layer beforeLayer(WMSMapContent content, Layer layer) {
                if ("cite:Lakes".equals(layer.getTitle())) {
                    return null;
                } else {
                    return super.beforeLayer(content, layer);
                }
            }
        };
        getMap.setGetMapCallbacks(Arrays.asList(((GetMapCallback) (callback))));
        // request a layer group with two layers
        Document dom = getAsDOM("wms?request=reflect&layers=nature&format=rss&version=1.1.0");
        // print(dom);
        assertXpathExists("rss/channel/title[text() = 'cite:Forests']", dom);
        Assert.assertEquals(1, callback.requests.size());
        Assert.assertEquals(1, callback.mapContentsInited.size());
        Assert.assertEquals(1, callback.layers.size());
        Assert.assertEquals(1, callback.mapContents.size());
        Assert.assertEquals(1, callback.maps.size());
        Assert.assertEquals(0, callback.exceptions.size());
        Assert.assertEquals("cite:Forests", callback.layers.get(0).getTitle());
    }

    @Test
    public void testAnimator() throws Exception {
        GetMapCallbackTest.TestCallback callback = new GetMapCallbackTest.TestCallback();
        getMap.setGetMapCallbacks(Arrays.asList(((GetMapCallback) (callback))));
        String requestURL = ("wms/animate?layers=" + (getLayerId(MockData.BASIC_POLYGONS))) + "&aparam=fake_param&avalues=val0,val1,val2";
        MockHttpServletResponse resp = getAsServletResponse(requestURL);
        Assert.assertEquals("image/gif", resp.getContentType());
        // the three frames, plus the fake request the animator does to get the mime type and
        // map content for the output
        Assert.assertEquals(4, callback.requests.size());
        Assert.assertEquals(4, callback.mapContentsInited.size());
        Assert.assertEquals(4, callback.layers.size());
        Assert.assertEquals(4, callback.mapContents.size());
        Assert.assertEquals(4, callback.maps.size());
        Assert.assertEquals(0, callback.exceptions.size());
    }

    @Test
    public void testAnimatedGifDimensions() throws Exception {
        GetMapCallbackTest.TestCallback callback = new GetMapCallbackTest.TestCallback();
        getMap.setGetMapCallbacks(Arrays.asList(((GetMapCallback) (callback))));
        setupVectorDimension(TIME, "time", LIST, null, null, null);
        MockHttpServletResponse response = getAsServletResponse((((("wms?service=WMS&version=1.1.1&request=GetMap" + ("&bbox=-180,-90,180,90&styles=&Format=image/png&width=80&height=40&srs=EPSG:4326" + "&layers=")) + (getLayerId(V_TIME_ELEVATION))) + "&time=2011-05-02,2011-05-04,2011-05-10&format=") + (GIFMapResponse.IMAGE_GIF_SUBTYPE_ANIMATED)));
        Assert.assertEquals("image/gif", response.getContentType());
        // the three frames in a single request
        Assert.assertEquals(1, callback.requests.size());
        Assert.assertEquals(3, callback.mapContentsInited.size());
        Assert.assertEquals(3, callback.layers.size());
        Assert.assertEquals(3, callback.mapContents.size());
        Assert.assertEquals(1, callback.maps.size());
        Assert.assertEquals(0, callback.exceptions.size());
    }

    private class TestCallback implements GetMapCallback {
        private List<GetMapRequest> requests = new ArrayList<GetMapRequest>();

        private List<WMSMapContent> mapContentsInited = new ArrayList<WMSMapContent>();

        private List<Layer> layers = new ArrayList<Layer>();

        private List<WMSMapContent> mapContents = new ArrayList<WMSMapContent>();

        private List<WebMap> maps = new ArrayList<WebMap>();

        private List<Throwable> exceptions = new ArrayList<Throwable>();

        @Override
        public synchronized GetMapRequest initRequest(GetMapRequest request) {
            requests.add(request);
            return request;
        }

        @Override
        public synchronized void initMapContent(WMSMapContent mapContent) {
            mapContentsInited.add(mapContent);
        }

        @Override
        public synchronized Layer beforeLayer(WMSMapContent content, Layer layer) {
            layers.add(layer);
            return layer;
        }

        @Override
        public synchronized WMSMapContent beforeRender(WMSMapContent mapContent) {
            mapContents.add(mapContent);
            return mapContent;
        }

        @Override
        public synchronized WebMap finished(WebMap map) {
            maps.add(map);
            return map;
        }

        @Override
        public synchronized void failed(Throwable t) {
            exceptions.add(t);
        }
    }
}

