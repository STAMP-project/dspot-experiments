/**
 * (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps.ogr;


import OgrConfiguration.DEFAULT;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.geoserver.ogr.core.Format;
import org.geoserver.util.XmlTestUtil;
import org.geoserver.wfs.response.Ogr2OgrConfigurator;
import org.geoserver.wfs.response.Ogr2OgrTestUtil;
import org.geoserver.wps.WPSTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;

import static junit.framework.Assert.assertEquals;


public class WPSOgrTest extends WPSTestSupport {
    private XmlTestUtil xml;

    @Test
    public void testConfigurationLoad() throws Exception {
        File configuration = null;
        try {
            configuration = loadConfiguration();
            Ogr2OgrConfigurator configurator = applicationContext.getBean(Ogr2OgrConfigurator.class);
            configurator.loadConfiguration();
            List<String> formatNames = new ArrayList<>();
            for (Format f : configurator.of.getFormats()) {
                formatNames.add(f.getGeoserverFormat());
            }
            Assert.assertTrue(formatNames.contains("OGR-TAB"));
            Assert.assertTrue(formatNames.contains("OGR-MIF"));
            Assert.assertTrue(formatNames.contains("OGR-CSV"));
            Assert.assertTrue(formatNames.contains("OGR-KML"));
        } finally {
            if (configuration != null) {
                configuration.delete();
            }
        }
    }

    @Test
    public void testDescribeProcess() throws Exception {
        DEFAULT.ogr2ogrLocation = Ogr2OgrTestUtil.getOgr2Ogr();
        DEFAULT.gdalData = Ogr2OgrTestUtil.getGdalData();
        Ogr2OgrConfigurator configurator = applicationContext.getBean(Ogr2OgrConfigurator.class);
        configurator.loadConfiguration();
        Document d = getAsDOM(((root()) + "service=wps&request=describeprocess&identifier=gs:BufferFeatureCollection"));
        String base = "/wps:ProcessDescriptions/ProcessDescription/ProcessOutputs";
        for (Format f : DEFAULT.getFormats()) {
            if ((f.getMimeType()) != null) {
                assertXpathExists((((((base + "/Output[1]/ComplexOutput/Supported/Format[MimeType='") + (f.getMimeType())) + "; subtype=") + (f.getGeoserverFormat())) + "']"), d);
            }
        }
    }

    @Test
    public void testOGRKMLOutputExecuteRaw() throws Exception {
        File configuration = null;
        try {
            configuration = loadConfiguration();
            Ogr2OgrConfigurator configurator = applicationContext.getBean(Ogr2OgrConfigurator.class);
            configurator.loadConfiguration();
            MockHttpServletResponse r = postAsServletResponse("wps", getWpsRawXML("application/vnd.google-earth.kml; subtype=OGR-KML"));
            assertEquals("application/vnd.google-earth.kml; subtype=OGR-KML", r.getContentType());
            Assert.assertTrue(((r.getContentAsString().length()) > 0));
        } finally {
            if (configuration != null) {
                configuration.delete();
            }
        }
    }

    @Test
    public void testOGRKMLOutputExecuteDocument() throws Exception {
        File configuration = null;
        try {
            configuration = loadConfiguration();
            Ogr2OgrConfigurator configurator = applicationContext.getBean(Ogr2OgrConfigurator.class);
            configurator.loadConfiguration();
            Document d = postAsDOM("wps", getWpsDocumentXML("application/vnd.google-earth.kml; subtype=OGR-KML"));
            Assert.assertThat(d, xml.hasOneNode("//kml:kml/kml:Document/kml:Schema | //kml:kml/kml:Document/kml:Folder/kml:Schema"));
        } finally {
            if (configuration != null) {
                configuration.delete();
            }
        }
    }

    @Test
    public void testOGRCSVOutputExecuteDocument() throws Exception {
        File configuration = null;
        try {
            configuration = loadConfiguration();
            Ogr2OgrConfigurator configurator = applicationContext.getBean(Ogr2OgrConfigurator.class);
            configurator.loadConfiguration();
            MockHttpServletResponse r = postAsServletResponse("wps", getWpsRawXML("text/csv; subtype=OGR-CSV"));
            assertEquals("text/csv; subtype=OGR-CSV", r.getContentType());
            Assert.assertTrue(((r.getContentAsString().length()) > 0));
            Assert.assertTrue(((r.getContentAsString().contains("WKT,gml_id,STATE_NAME")) || (r.getContentAsString().contains("geometry,gml_id,STATE_NAME"))));
        } finally {
            if (configuration != null) {
                configuration.delete();
            }
        }
    }

    @Test
    public void testOGRBinaryOutputExecuteDocument() throws Exception {
        File configuration = null;
        try {
            configuration = loadConfiguration();
            Ogr2OgrConfigurator configurator = applicationContext.getBean(Ogr2OgrConfigurator.class);
            configurator.loadConfiguration();
            MockHttpServletResponse r = postAsServletResponse("wps", getWpsRawXML("application/zip; subtype=OGR-TAB"));
            assertEquals("application/zip; subtype=OGR-TAB", r.getContentType());
            ByteArrayInputStream bis = getBinaryInputStream(r);
            ZipInputStream zis = new ZipInputStream(bis);
            ZipEntry entry = null;
            boolean found = false;
            while ((entry = zis.getNextEntry()) != null) {
                final String name = entry.getName();
                zis.closeEntry();
                if (name.equals("feature.tab")) {
                    found = true;
                    break;
                }
            } 
            zis.close();
            Assert.assertTrue(found);
        } finally {
            if (configuration != null) {
                configuration.delete();
            }
        }
    }
}

