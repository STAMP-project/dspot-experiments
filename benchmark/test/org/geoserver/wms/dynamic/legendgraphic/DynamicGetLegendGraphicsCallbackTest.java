/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.dynamic.legendgraphic;


import Dispatcher.REQUEST;
import java.util.Map;
import org.geoserver.ows.Request;
import org.geoserver.ows.util.KvpMap;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.platform.Operation;
import org.geoserver.platform.Service;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geoserver.wms.GetLegendGraphicRequest;
import org.geoserver.wms.legendgraphic.GetLegendGraphicKvpReader;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.Style;
import org.junit.Assert;
import org.junit.Test;


public class DynamicGetLegendGraphicsCallbackTest extends GeoServerSystemTestSupport {
    @Test
    public void testLegendExpasion() throws Exception {
        // manually parse a request
        GetLegendGraphicKvpReader requestReader = GeoServerExtensions.bean(GetLegendGraphicKvpReader.class);
        Map params = new KvpMap();
        params.put("VERSION", "1.0.0");
        params.put("REQUEST", "GetLegendGraphic");
        params.put("LAYER", "watertemp_dynamic");
        params.put("STYLE", "style_rgb");
        params.put("FORMAT", "image/png");
        GetLegendGraphicRequest getLegendGraphics = requestReader.read(new GetLegendGraphicRequest(), params, params);
        // setup to call the callback
        Service wmsService = ((Service) (GeoServerExtensions.bean("wms-1_1_1-ServiceDescriptor")));
        Operation op = new Operation("getLegendGraphic", wmsService, null, new Object[]{ getLegendGraphics });
        Request request = new Request();
        request.setKvp(params);
        request.setRawKvp(params);
        REQUEST.set(request);
        DynamicGetLegendGraphicDispatcherCallback callback = GeoServerExtensions.bean(DynamicGetLegendGraphicDispatcherCallback.class);
        callback.operationDispatched(null, op);
        // get the style and check it has been transformed (we started with one having a
        // transformation, now
        // we have a static colormap)
        Style style = getLegendGraphics.getLegends().get(0).getStyle();
        FeatureTypeStyle fts = style.featureTypeStyles().get(0);
        Assert.assertNull(fts.getTransformation());
        RasterSymbolizer rs = ((RasterSymbolizer) (fts.rules().get(0).symbolizers().get(0)));
        Assert.assertNotNull(rs.getColorMap());
    }
}

