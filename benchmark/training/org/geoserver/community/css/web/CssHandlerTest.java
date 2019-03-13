/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.community.css.web;


import CssHandler.FORMAT;
import java.io.IOException;
import org.geoserver.catalog.Styles;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class CssHandlerTest extends GeoServerSystemTestSupport {
    @Test
    public void testParseThroughStyles() throws IOException {
        String css = "* { fill: lightgrey; }";
        StyledLayerDescriptor sld = Styles.handler(FORMAT).parse(css, null, null, null);
        Assert.assertNotNull(sld);
        PolygonSymbolizer ps = SLD.polySymbolizer(Styles.style(sld));
        Assert.assertNotNull(ps);
    }
}

