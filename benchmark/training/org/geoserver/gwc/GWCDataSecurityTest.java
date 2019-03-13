/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc;


import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Logger;
import org.geoserver.security.CatalogMode;
import org.geoserver.security.TestResourceAccessManager;
import org.geoserver.wms.WMSTestSupport;
import org.geotools.util.logging.Logging;
import org.geowebcache.service.ve.VEConverter;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.filter.Filter;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * Performs integration tests using a mock {@link ResourceAccessManager}
 *
 * @author Niels Charlier, Scitus Development
 */
public class GWCDataSecurityTest extends WMSTestSupport {
    static final Logger LOGGER = Logging.getLogger(GWCDataSecurityTest.class);

    private static final String SECURITY_ERROR_TYPE = "text/plain";

    private static final String NOT_FOUND_ERROR_TYPE = "text/html";

    @Test
    public void testNoMosaic() throws Exception {
        GWC.get().getConfig().setSecurityEnabled(true);
        // first to cache
        setRequestAuth("cite", "cite");
        String path = "gwc/service/wms?bgcolor=0x000000&LAYERS=sf:mosaic&STYLES=&FORMAT=image/png&SERVICE=WMS&VERSION=1.1.1" + "&REQUEST=GetMap&SRS=EPSG:4326&BBOX=0,-90,180,90&WIDTH=256&HEIGHT=256&transparent=false";
        MockHttpServletResponse response = getAsServletResponse(path);
        Assert.assertEquals("image/png", response.getContentType());
        // try again, now should be cached
        response = getAsServletResponse(path);
        Assert.assertEquals("image/png", response.getContentType());
        // try now as different user
        setRequestAuth("cite_nomosaic", "cite");
        response = getAsServletResponse(path);
        Assert.assertEquals(GWCDataSecurityTest.NOT_FOUND_ERROR_TYPE, response.getContentType());
        String str = string(getBinaryInputStream(response));
        Assert.assertTrue(str.contains("org.geotools.ows.ServiceException: Could not find layer sf:mosaic"));
    }

    @Test
    public void testPermissionMosaicTileWmts() throws Exception {
        doPermissionMosaicTileTest(( layer) -> String.format(("gwc/service/wmts?LAYER=%s&FORMAT=image/png&SERVICE=WMTS&VERSION=1.0.0" + "&REQUEST=GetTile&TILEMATRIXSET=EPSG:900913&TILEMATRIX=EPSG:900913:0&TILECOL=0&TILEROW=0"), layer), GWCDataSecurityTest.SECURITY_ERROR_TYPE, GWCDataSecurityTest.NOT_FOUND_ERROR_TYPE);
    }

    enum TestGridset {

        GlobalCRS84Geometric("EPSG:4326", new long[]{ 7489, 1245, 12 }, new long[]{ 4096, 2048, 12 }),
        GoogleMapsCompatible("EPSG:900913", new long[]{ 7489, 3237, 13 }, new long[]{ 4096, 4096, 13 });
        public final String name;

        public final long[] tileInBounds;

        public final long[] tileOutOfBounds;

        private TestGridset(String name, long[] tileInBounds, long[] tileOutOfBounds) {
            this.name = name;
            this.tileInBounds = tileInBounds;
            this.tileOutOfBounds = tileOutOfBounds;
        }
    }

    @Test
    public void testPermissionMosaicTileGmaps() throws Exception {
        doPermissionMosaicTileTest(( layer) -> String.format("gwc/service/gmaps?LAYERS=%s&FORMAT=image/png&ZOOM=0&X=0&Y=0", layer), GWCDataSecurityTest.SECURITY_ERROR_TYPE, GWCDataSecurityTest.NOT_FOUND_ERROR_TYPE);
    }

    @Test
    public void testPermissionMosaicTileMGmaps() throws Exception {
        doPermissionMosaicTileTest(( layer) -> String.format("gwc/service/mgmaps?LAYERS=%s&FORMAT=image/png&ZOOM=17&X=0&Y=0", layer), GWCDataSecurityTest.SECURITY_ERROR_TYPE, GWCDataSecurityTest.NOT_FOUND_ERROR_TYPE);
    }

    @Test
    public void testPermissionMosaicTileTms() throws Exception {
        doPermissionMosaicTileTest(( layer) -> String.format("gwc/service/tms/1.0.0/%s@EPSG:900913@png/0/0/0.png", layer), GWCDataSecurityTest.SECURITY_ERROR_TYPE, GWCDataSecurityTest.NOT_FOUND_ERROR_TYPE);
    }

    @Test
    public void testPermissionMosaicTileKml() throws Exception {
        doPermissionMosaicTileTest(( layer) -> String.format("gwc/service/kml/%s/x0y0z0.png", layer), GWCDataSecurityTest.SECURITY_ERROR_TYPE, GWCDataSecurityTest.NOT_FOUND_ERROR_TYPE);
    }

    @Test
    public void testPermissionCropTileTms() throws Exception {
        doPermissionCropTileTest(( layer, index) -> String.format("gwc/service/tms/1.0.0/%s@EPSG:900913@png/%d/%d/%d.png", layer, index[2], index[0], index[1]), GWCDataSecurityTest.SECURITY_ERROR_TYPE, GWCDataSecurityTest.TestGridset.GoogleMapsCompatible);
    }

    @Test
    public void testPermissionCropTileWmts() throws Exception {
        System.out.println(Arrays.toString(GWC.get().getTileLayerByName("sf:mosaic").getGridSubset("EPSG:900913").getCoverage(13)));
        System.out.println(GWC.get().getTileLayerByName("sf:mosaic").getGridSubset("EPSG:900913").getOriginalExtent());
        System.out.println(getResource().getLatLonBoundingBox());
        System.out.println(getResource().getLatLonBoundingBox());
        doPermissionCropTileTest(( layer, index) -> String.format(("gwc/service/wmts?LAYER=%s&FORMAT=image/png&SERVICE=WMTS&VERSION=1.0.0" + "&REQUEST=GetTile&TILEMATRIXSET=EPSG:900913&TILEMATRIX=EPSG:900913:%d&TILECOL=%d&TILEROW=%d"), layer, index[2], index[0], (((1 << (index[2])) - (index[1])) - 1)), GWCDataSecurityTest.SECURITY_ERROR_TYPE, GWCDataSecurityTest.TestGridset.GoogleMapsCompatible);
    }

    @Test
    public void testPermissionCropTileGmaps() throws Exception {
        doPermissionCropTileTest(( layer, index) -> String.format("gwc/service/gmaps?LAYERS=%s&FORMAT=image/png&ZOOM=%d&X=%d&Y=%d", layer, index[2], index[0], (((1 << (index[2])) - (index[1])) - 1)), GWCDataSecurityTest.SECURITY_ERROR_TYPE, GWCDataSecurityTest.TestGridset.GoogleMapsCompatible);
    }

    @Test
    public void testPermissionCropTileMGmaps() throws Exception {
        doPermissionCropTileTest(( layer, index) -> String.format("gwc/service/mgmaps?LAYERS=%s&FORMAT=image/png&ZOOM=%d&X=%d&Y=%d", layer, (17 - (index[2])), index[0], (((1 << (index[2])) - (index[1])) - 1)), GWCDataSecurityTest.SECURITY_ERROR_TYPE, GWCDataSecurityTest.TestGridset.GoogleMapsCompatible);
    }

    @Test
    public void testPermissionCropTileKml() throws Exception {
        doPermissionCropTileTest(( layer, index) -> String.format("gwc/service/kml/%s/x%dy%dz%d.png", layer, index[0], index[1], index[2]), GWCDataSecurityTest.SECURITY_ERROR_TYPE, GWCDataSecurityTest.TestGridset.GlobalCRS84Geometric);
    }

    @Test
    public void testPermissionCropTileBing() throws Exception {
        doPermissionCropTileTest(( layer, index) -> {
            long col = index[0];
            long row = ((1 << (index[2])) - (index[1])) - 1;
            long zoom = index[2];
            long key = 0;
            for (int i = 0; i < zoom; i++) {
                key |= ((col & (1 << i)) != 0) ? 1 << (i * 2) : 0;
                key |= ((row & (1 << i)) != 0) ? 1 << ((i * 2) + 1) : 0;
            }
            String quadKey = Long.toString(key, 4);
            Assert.assertThat(VEConverter.convert(quadKey), Matchers.equalTo(index));// Check that the test key is correct. Failure means

            // the test is broken
            return String.format("gwc/service/ve?layers=%s&format=image/png&quadKey=%s", layer, quadKey);
        }, GWCDataSecurityTest.SECURITY_ERROR_TYPE, GWCDataSecurityTest.TestGridset.GoogleMapsCompatible);
    }

    @Test
    public void testPermissionMosaicKmlRasterSuperOverlay() throws Exception {
        doPermissionKmlOverlay(( layer) -> String.format("gwc/service/kml/%s.png.kmz", layer), "application/vnd.google-earth.kmz");
    }

    @Test
    public void testPermissionMosaicKmlVectorSuperOverlay() throws Exception {
        doPermissionKmlOverlay(( layer) -> String.format("gwc/service/kml/%s.kml.kmz", layer), "application/vnd.google-earth.kmz");
    }

    @Test
    public void testCroppedMosaic() throws Exception {
        // first to cache
        setRequestAuth("cite", "cite");
        String path = "gwc/service/wms?bgcolor=0x000000&LAYERS=sf:mosaic&STYLES=&FORMAT=image/png&SERVICE=WMS&VERSION=1.1.1" + "&REQUEST=GetMap&SRS=EPSG:4326&BBOX=0,-90,180,90&WIDTH=256&HEIGHT=256&transparent=false";
        MockHttpServletResponse response = getAsServletResponse(path);
        Assert.assertEquals("image/png", response.getContentType());
        // this should fail
        setRequestAuth("cite_cropmosaic", "cite");
        path = "gwc/service/wms?bgcolor=0x000000&LAYERS=sf:mosaic&STYLES=&FORMAT=image/png&SERVICE=WMS&VERSION=1.1.1" + "&REQUEST=GetMap&SRS=EPSG:4326&BBOX=0,-90,180,90&WIDTH=256&HEIGHT=256&transparent=false";
        response = getAsServletResponse(path);
        Assert.assertEquals(GWCDataSecurityTest.SECURITY_ERROR_TYPE, response.getContentType());
        String str = string(getBinaryInputStream(response));
        Assert.assertTrue(str.contains("Not Authorized"));
        // but this should be fine
        path = "gwc/service/wms?bgcolor=0x000000&LAYERS=sf:mosaic&STYLES=&FORMAT=image/png&SERVICE=WMS&VERSION=1.1.1" + "&REQUEST=GetMap&SRS=EPSG:4326&BBOX=143.4375,-42.1875,146.25,-39.375&WIDTH=256&HEIGHT=256&transparent=false";
        response = getAsServletResponse(path);
        Assert.assertEquals("image/png", response.getContentType());
    }

    @Test
    public void testFilterMosaic() throws Exception {
        // first to cache
        setRequestAuth("cite", "cite");
        String path = "gwc/service/wms?bgcolor=0x000000&LAYERS=sf:mosaic&STYLES=&FORMAT=image/png&SERVICE=WMS&VERSION=1.1.1" + "&REQUEST=GetMap&SRS=EPSG:4326&BBOX=0,-90,180,90&WIDTH=256&HEIGHT=256&transparent=false";
        MockHttpServletResponse response = getAsServletResponse(path);
        Assert.assertEquals("image/png", response.getContentType());
        // this should fail
        setRequestAuth("cite_filtermosaic", "cite");
        path = "gwc/service/wms?bgcolor=0x000000&LAYERS=sf:mosaic&STYLES=&FORMAT=image/png&SERVICE=WMS&VERSION=1.1.1" + "&REQUEST=GetMap&SRS=EPSG:4326&BBOX=0,-90,180,90&WIDTH=256&HEIGHT=256&transparent=false";
        response = getAsServletResponse(path);
        Assert.assertEquals(GWCDataSecurityTest.SECURITY_ERROR_TYPE, response.getContentType());
        String str = string(getBinaryInputStream(response));
        Assert.assertThat(str, Matchers.containsString("Not Authorized"));
        // but this should be fine
        path = "gwc/service/wms?bgcolor=0x000000&LAYERS=sf:mosaic&STYLES=&FORMAT=image/png&SERVICE=WMS&VERSION=1.1.1" + "&REQUEST=GetMap&SRS=EPSG:4326&BBOX=143.4375,-42.1875,146.25,-39.375&WIDTH=256&HEIGHT=256&transparent=false";
        response = getAsServletResponse(path);
        Assert.assertEquals("image/png", response.getContentType());
    }

    @Test
    public void testLayerGroup() throws Exception {
        // no auth, it should work
        setRequestAuth(null, null);
        String path = (("gwc/service/wms?bgcolor=0x000000&LAYERS=" + (NATURE_GROUP)) + "&STYLES=&FORMAT=image/png&SERVICE=WMS&VERSION=1.1.1") + "&REQUEST=GetMap&SRS=EPSG:4326&BBOX=0,-90,180,90&WIDTH=256&HEIGHT=256&transparent=false";
        MockHttpServletResponse response = getAsServletResponse(path);
        Assert.assertEquals("image/png", response.getContentType());
        // now setup auth for the group
        TestResourceAccessManager tam = ((TestResourceAccessManager) (applicationContext.getBean("testResourceAccessManager")));
        LayerInfo lakes = getCatalog().getLayerByName(getLayerId(MockData.LAKES));
        // LayerInfo forests = getCatalog().getLayerByName(getLayerId(MockData.FORESTS));
        tam.putLimits("cite_nogroup", lakes, new org.geoserver.security.DataAccessLimits(CatalogMode.HIDE, Filter.EXCLUDE));
        tam.putLimits("cite", lakes, new org.geoserver.security.DataAccessLimits(CatalogMode.HIDE, Filter.INCLUDE));
        // tam.putLimits("cite_nogroup", forests, new DataAccessLimits(CatalogMode.HIDE,
        // Filter.EXCLUDE));
        // tam.putLimits("cite", forests, new DataAccessLimits(CatalogMode.HIDE,
        // Filter.INCLUDE));
        // this one cannot get the image, one layer in the group is not accessible
        setRequestAuth("cite_nogroup", "cite");
        response = getAsServletResponse(path);
        Assert.assertEquals(GWCDataSecurityTest.NOT_FOUND_ERROR_TYPE, response.getContentType());
        String str = string(getBinaryInputStream(response));
        Assert.assertTrue(str.contains(("org.geotools.ows.ServiceException: Could not find layer " + (NATURE_GROUP))));
        // but this can access it all
        setRequestAuth("cite", "cite");
        response = getAsServletResponse(path);
        Assert.assertEquals("image/png", response.getContentType());
    }

    @Test
    public void testWorkspacedLayerGroup() throws Exception {
        Catalog catalog = getCatalog();
        LayerInfo lakes = catalog.getLayerByName(getLayerId(MockData.LAKES));
        WorkspaceInfo ws = lakes.getResource().getStore().getWorkspace();
        LayerGroupInfo workspacedLayerGroup = getCatalog().getFactory().createLayerGroup();
        workspacedLayerGroup.setWorkspace(ws);
        workspacedLayerGroup.setName("citeGroup");
        workspacedLayerGroup.getLayers().add(lakes);
        workspacedLayerGroup.getStyles().add(null);
        catalog.add(workspacedLayerGroup);
        // enable direct WMS integration
        GWC.get().getConfig().setDirectWMSIntegrationEnabled(true);
        // no auth, it should work
        setRequestAuth(null, null);
        String path = ((((ws.getName()) + "/wms?bgcolor=0x000000&LAYERS=") + (workspacedLayerGroup.prefixedName())) + "&STYLES=&FORMAT=image/png&SERVICE=WMS&VERSION=1.1.1") + "&REQUEST=GetMap&SRS=EPSG:4326&BBOX=0,-90,180,90&WIDTH=256&HEIGHT=256&transparent=false&tiled=true";
        MockHttpServletResponse response = getAsServletResponse(path);
        Assert.assertEquals("image/png", response.getContentType());
    }
}

