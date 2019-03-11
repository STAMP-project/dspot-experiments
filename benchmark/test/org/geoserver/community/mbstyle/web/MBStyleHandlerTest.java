/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.community.mbstyle.web;


import MBStyleHandler.FORMAT;
import SLDHandler.VERSION_10;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class MBStyleHandlerTest extends GeoServerSystemTestSupport {
    @Test
    public void testParseThroughStyles() throws IOException {
        String mbstyle = "{\"layers\": [{\n" + ((((("    \"type\": \"line\",\n" + "    \"paint\": {\n") + "        \"line-color\": \"#0099ff\",\n") + "        \"line-width\": 10,\n") + "    }\n") + "}]}");
        StyledLayerDescriptor sld = Styles.handler(FORMAT).parse(mbstyle, null, null, null);
        Assert.assertNotNull(sld);
        LineSymbolizer ls = SLD.lineSymbolizer(Styles.style(sld));
        Assert.assertNotNull(ls);
    }

    @Test
    public void testRoundTripMBStyleGroup() throws IOException {
        Catalog catalog = getCatalog();
        LayerGroupInfo lg = catalog.getFactory().createLayerGroup();
        lg.setName("citeGroup");
        lg.getLayers().add(catalog.getLayerByName(getLayerId(MockData.LAKES)));
        lg.getLayers().add(catalog.getLayerByName(getLayerId(MockData.BASIC_POLYGONS)));
        lg.getLayers().add(catalog.getLayerByName(getLayerId(MockData.NAMED_PLACES)));
        lg.getStyles().add(null);
        lg.getStyles().add(null);
        lg.getStyles().add(null);
        catalog.add(lg);
        StyledLayerDescriptor sld = Styles.handler(FORMAT).parse(getClass().getResourceAsStream("citeGroup.json"), null, null, null);
        Assert.assertEquals(4, sld.getStyledLayers().length);
        StyleHandler sldHandler = Styles.handler(SLDHandler.FORMAT);
        File sldFile = Files.createTempFile("citeGroup", "sld").toFile();
        OutputStream fout = new FileOutputStream(sldFile);
        sldHandler.encode(sld, VERSION_10, true, fout);
        StyledLayerDescriptor sld2 = sldHandler.parse(new FileInputStream(sldFile), VERSION_10, null, null);
        Assert.assertEquals(4, sld2.getStyledLayers().length);
    }
}

