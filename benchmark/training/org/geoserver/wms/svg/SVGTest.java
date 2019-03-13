/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.svg;


import MockData.BASIC_POLYGONS;
import WMS.SVG_BATIK;
import WMS.SVG_SIMPLE;
import org.geoserver.wms.WMSTestSupport;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.w3c.dom.Document;

import static SVG.MIME_TYPE;


public class SVGTest extends WMSTestSupport {
    @Test
    public void testBasicSvgGenerator() throws Exception {
        getWMS().setSvgRenderer(SVG_SIMPLE);
        Document doc = getAsDOM((((((((((("wms?request=getmap&service=wms&version=1.1.1" + "&format=") + (MIME_TYPE)) + "&layers=") + (BASIC_POLYGONS.getPrefix())) + ":") + (BASIC_POLYGONS.getLocalPart())) + "&styles=") + (BASIC_POLYGONS.getLocalPart())) + "&height=1024&width=1024&bbox=-180,-90,180,90&srs=EPSG:4326") + "&featureid=BasicPolygons.1107531493643"));
        Assert.assertEquals(1, doc.getElementsByTagName("svg").getLength());
        Assert.assertEquals(1, doc.getElementsByTagName("g").getLength());
    }

    @Test
    public void testBasicSvgGeneratorMultipleFts() throws Exception {
        getWMS().setSvgRenderer(SVG_SIMPLE);
        Document doc = getAsDOM(((((((("wms?request=getmap&service=wms&version=1.1.1" + "&format=") + (MIME_TYPE)) + "&layers=") + (getLayerId(BASIC_POLYGONS))) + "&styles=multifts") + "&height=1024&width=1024&bbox=-180,-90,180,90&srs=EPSG:4326") + "&featureid=BasicPolygons.1107531493643"));
        Assert.assertEquals(1, doc.getElementsByTagName("svg").getLength());
        Assert.assertEquals(1, doc.getElementsByTagName("g").getLength());
    }

    @Test
    public void testBatikSvgGenerator() throws Exception {
        Assume.assumeTrue(isw3OrgReachable());
        getWMS().setSvgRenderer(SVG_BATIK);
        Document doc = getAsDOM((((((((("wms?request=getmap&service=wms&version=1.1.1" + "&format=") + (MIME_TYPE)) + "&layers=") + (getLayerId(BASIC_POLYGONS))) + "&styles=") + (BASIC_POLYGONS.getLocalPart())) + "&height=1024&width=1024&bbox=-180,-90,180,90&srs=EPSG:4326") + "&featureid=BasicPolygons.1107531493643"));
        Assert.assertEquals(1, doc.getElementsByTagName("svg").getLength());
        Assert.assertTrue(((doc.getElementsByTagName("g").getLength()) > 1));
    }

    @Test
    public void testBatikMultipleFts() throws Exception {
        Assume.assumeTrue(isw3OrgReachable());
        getWMS().setSvgRenderer(SVG_BATIK);
        Document doc = getAsDOM(((((((("wms?request=getmap&service=wms&version=1.1.1" + "&format=") + (MIME_TYPE)) + "&layers=") + (getLayerId(BASIC_POLYGONS))) + "&styles=multifts") + "&height=1024&width=1024&bbox=-180,-90,180,90&srs=EPSG:4326") + "&featureid=BasicPolygons.1107531493643"));
        Assert.assertEquals(1, doc.getElementsByTagName("svg").getLength());
        Assert.assertTrue(((doc.getElementsByTagName("g").getLength()) > 1));
    }
}

