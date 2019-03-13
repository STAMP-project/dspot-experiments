/**
 * (c) 2014-2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geopkg.wps.gs;


import java.io.File;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.custommonkey.xmlunit.XMLUnit;
import org.geoserver.wps.WPSTestSupport;
import org.geotools.data.simple.SimpleFeatureReader;
import org.geotools.geopkg.FeatureEntry;
import org.geotools.geopkg.GeoPackage;
import org.geotools.geopkg.TileEntry;
import org.geotools.geopkg.TileMatrix;
import org.geotools.geopkg.TileReader;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


public class GeoPackageProcessTest extends WPSTestSupport {
    @Test
    public void testGeoPackageProcess() throws Exception {
        String urlPath = string(post("wps", getXml())).trim();
        String resourceUrl = urlPath.substring("http://localhost:8080/geoserver/".length());
        MockHttpServletResponse response = getAsServletResponse(resourceUrl);
        File file = new File(getDataDirectory().findOrCreateDir("tmp"), "test.gpkg");
        FileUtils.writeByteArrayToFile(file, getBinary(response));
        Assert.assertNotNull(file);
        Assert.assertEquals("test.gpkg", file.getName());
        Assert.assertTrue(file.exists());
        GeoPackage gpkg = new GeoPackage(file);
        List<FeatureEntry> features = gpkg.features();
        Assert.assertEquals(2, features.size());
        FeatureEntry fe = features.get(0);
        Assert.assertEquals("Fifteen", fe.getTableName());
        Assert.assertEquals("fifteen description", fe.getDescription());
        Assert.assertEquals("f15", fe.getIdentifier());
        Assert.assertEquals(32615, fe.getSrid().intValue());
        Assert.assertEquals(500000, fe.getBounds().getMinX(), 1.0E-4);
        Assert.assertEquals(500000, fe.getBounds().getMinY(), 1.0E-4);
        Assert.assertEquals(500100, fe.getBounds().getMaxX(), 1.0E-4);
        Assert.assertEquals(500100, fe.getBounds().getMaxY(), 1.0E-4);
        Assert.assertFalse(gpkg.hasSpatialIndex(fe));
        Assert.assertTrue(gpkg.hasSpatialIndex(features.get(1)));
        SimpleFeatureReader fr = gpkg.reader(fe, null, null);
        Assert.assertEquals(1, fr.getFeatureType().getAttributeCount());
        Assert.assertEquals("pointProperty", fr.getFeatureType().getAttributeDescriptors().get(0).getLocalName());
        Assert.assertTrue(fr.hasNext());
        fr.next();
        fr.close();
        fe = features.get(1);
        Assert.assertEquals("Lakes", fe.getTableName());
        Assert.assertEquals("lakes description", fe.getDescription());
        Assert.assertEquals("lakes1", fe.getIdentifier());
        fr = gpkg.reader(fe, null, null);
        Assert.assertTrue(fr.hasNext());
        fr.next();
        fr.close();
        List<TileEntry> tiles = gpkg.tiles();
        Assert.assertEquals(2, tiles.size());
        TileEntry te = tiles.get(0);
        Assert.assertEquals("world_lakes", te.getTableName());
        Assert.assertEquals("world and lakes overlay", te.getDescription());
        Assert.assertEquals("wl1", te.getIdentifier());
        Assert.assertEquals(4326, te.getSrid().intValue());
        Assert.assertEquals((-0.17578125), te.getBounds().getMinX(), 1.0E-4);
        Assert.assertEquals((-0.087890625), te.getBounds().getMinY(), 1.0E-4);
        Assert.assertEquals(0.17578125, te.getBounds().getMaxX(), 1.0E-4);
        Assert.assertEquals(0.087890625, te.getBounds().getMaxY(), 1.0E-4);
        List<TileMatrix> matrices = te.getTileMatricies();
        Assert.assertEquals(1, matrices.size());
        TileMatrix matrix = matrices.get(0);
        Assert.assertEquals(10, matrix.getZoomLevel().intValue());
        Assert.assertEquals(256, matrix.getTileWidth().intValue());
        Assert.assertEquals(256, matrix.getTileHeight().intValue());
        Assert.assertEquals(2048, matrix.getMatrixWidth().intValue());
        Assert.assertEquals(1024, matrix.getMatrixHeight().intValue());
        TileReader tr = gpkg.reader(te, null, null, null, null, null, null);
        Assert.assertTrue(tr.hasNext());
        Assert.assertEquals(10, tr.next().getZoom().intValue());
        tr.close();
        te = tiles.get(1);
        Assert.assertEquals("world_lakes2", te.getTableName());
        Assert.assertEquals("world and lakes overlay 2", te.getDescription());
        Assert.assertEquals("wl2", te.getIdentifier());
        Assert.assertEquals(4326, te.getSrid().intValue());
        Assert.assertEquals((-0.17578125), te.getBounds().getMinX(), 1.0E-4);
        Assert.assertEquals((-0.087890625), te.getBounds().getMinY(), 1.0E-4);
        Assert.assertEquals(0.17578125, te.getBounds().getMaxX(), 1.0E-4);
        Assert.assertEquals(0.087890625, te.getBounds().getMaxY(), 1.0E-4);
        gpkg.close();
    }

    @Test
    public void testGeoPackageProcessWithRemove() throws Exception {
        File path = getDataDirectory().findOrCreateDataRoot();
        String urlPath = string(post("wps", getXml2(path, true))).trim();
        String resourceUrl = urlPath.substring("http://localhost:8080/geoserver/".length());
        MockHttpServletResponse response = getAsServletResponse(resourceUrl);
        File file = new File(getDataDirectory().findOrCreateDir("tmp"), "test.gpkg");
        FileUtils.writeByteArrayToFile(file, getBinary(response));
        Assert.assertNotNull(file);
        Assert.assertEquals("test.gpkg", file.getName());
        Assert.assertTrue(file.exists());
        GeoPackage gpkg = new GeoPackage(file);
        List<TileEntry> tiles = gpkg.tiles();
        Assert.assertEquals(1, tiles.size());
        TileEntry te = tiles.get(0);
        Assert.assertEquals("world_lakes", te.getTableName());
        Assert.assertEquals("world and lakes overlay", te.getDescription());
        Assert.assertEquals("wl1", te.getIdentifier());
        Assert.assertEquals(4326, te.getSrid().intValue());
        Assert.assertEquals((-0.17578125), te.getBounds().getMinX(), 1.0E-4);
        Assert.assertEquals((-0.087890625), te.getBounds().getMinY(), 1.0E-4);
        Assert.assertEquals(0.17578125, te.getBounds().getMaxX(), 1.0E-4);
        Assert.assertEquals(0.087890625, te.getBounds().getMaxY(), 1.0E-4);
        List<TileMatrix> matrices = te.getTileMatricies();
        Assert.assertEquals(1, matrices.size());
        TileMatrix matrix = matrices.get(0);
        Assert.assertEquals(10, matrix.getZoomLevel().intValue());
        Assert.assertEquals(256, matrix.getTileWidth().intValue());
        Assert.assertEquals(256, matrix.getTileHeight().intValue());
        Assert.assertEquals(2048, matrix.getMatrixWidth().intValue());
        Assert.assertEquals(1024, matrix.getMatrixHeight().intValue());
        TileReader tr = gpkg.reader(te, null, null, null, null, null, null);
        Assert.assertTrue(tr.hasNext());
        Assert.assertEquals(10, tr.next().getZoom().intValue());
        tr.close();
        gpkg.close();
    }

    @Test
    public void testGeoPackageProcessWithPath() throws Exception {
        File path = getDataDirectory().findOrCreateDataRoot();
        String urlPath = string(post("wps", getXml2(path, false))).trim();
        File file = new File(path, "test.gpkg");
        Assert.assertNotNull(file);
        Assert.assertTrue(file.exists());
        GeoPackage gpkg = new GeoPackage(file);
        List<TileEntry> tiles = gpkg.tiles();
        Assert.assertEquals(1, tiles.size());
        TileEntry te = tiles.get(0);
        Assert.assertEquals("world_lakes", te.getTableName());
        Assert.assertEquals("world and lakes overlay", te.getDescription());
        Assert.assertEquals("wl1", te.getIdentifier());
        Assert.assertEquals(4326, te.getSrid().intValue());
        Assert.assertEquals((-0.17578125), te.getBounds().getMinX(), 1.0E-4);
        Assert.assertEquals((-0.087890625), te.getBounds().getMinY(), 1.0E-4);
        Assert.assertEquals(0.17578125, te.getBounds().getMaxX(), 1.0E-4);
        Assert.assertEquals(0.087890625, te.getBounds().getMaxY(), 1.0E-4);
        List<TileMatrix> matrices = te.getTileMatricies();
        Assert.assertEquals(1, matrices.size());
        TileMatrix matrix = matrices.get(0);
        Assert.assertEquals(10, matrix.getZoomLevel().intValue());
        Assert.assertEquals(256, matrix.getTileWidth().intValue());
        Assert.assertEquals(256, matrix.getTileHeight().intValue());
        Assert.assertEquals(2048, matrix.getMatrixWidth().intValue());
        Assert.assertEquals(1024, matrix.getMatrixHeight().intValue());
        TileReader tr = gpkg.reader(te, null, null, null, null, null, null);
        Assert.assertTrue(tr.hasNext());
        Assert.assertEquals(10, tr.next().getZoom().intValue());
        tr.close();
        gpkg.close();
    }

    @Test
    public void testGeoPackageProcessValidationError() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + ((((((((((((((((((((((("<wps:Execute version=\"1.0.0\" service=\"WPS\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.opengis.net/wps/1.0.0\" xmlns:wfs=\"http://www.opengis.net/wfs\" xmlns:wps=\"http://www.opengis.net/wps/1.0.0\" xmlns:ows=\"http://www.opengis.net/ows/1.1\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:wcs=\"http://www.opengis.net/wcs/1.1.1\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xsi:schemaLocation=\"http://www.opengis.net/wps/1.0.0 http://schemas.opengis.net/wps/1.0.0/wpsAll.xsd\">" + "  <ows:Identifier>gs:GeoPackage</ows:Identifier>") + "  <wps:DataInputs>") + "    <wps:Input>") + "      <ows:Identifier>contents</ows:Identifier>") + "      <wps:Data>") + "        <wps:ComplexData mimeType=\"text/xml; subtype=geoserver/geopackage\"><![CDATA[") + "<geopackage name=\"test\" xmlns=\"http://www.opengis.net/gpkg\">") + "  <features name=\"lakes\" identifier=\"lakes1\">") + "    <description>lakes description</description>") + "    <featuretype>cite:Lakes</featuretype>") + "    <indexed>HELLO WORLD</indexed>") + "   </features>") + "</geopackage>") + "]]></wps:ComplexData>") + "      </wps:Data>") + "    </wps:Input>") + "  </wps:DataInputs>") + "  <wps:ResponseForm>") + "    <wps:RawDataOutput>") + "      <ows:Identifier>geopackage</ows:Identifier>") + "    </wps:RawDataOutput>") + "  </wps:ResponseForm>") + "</wps:Execute>");
        Document d = postAsDOM("wps", xml);
        Assert.assertEquals("wps:ExecuteResponse", d.getDocumentElement().getNodeName());
        assertXpathExists("/wps:ExecuteResponse/wps:Status/wps:ProcessFailed", d);
        String message = XMLUnit.newXpathEngine().evaluate(("//wps:ExecuteResponse/wps:Status/wps:ProcessFailed" + "/ows:ExceptionReport/ows:Exception/ows:ExceptionText/text()"), d);
        Assert.assertThat(message, CoreMatchers.containsString("org.xml.sax.SAXParseException"));
        Assert.assertThat(message, CoreMatchers.containsString("HELLO WORLD"));
    }

    @Test
    public void testGeoPackageProcessValidationXXE() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + (((((((((((((((((((((((((("<wps:Execute version=\"1.0.0\" service=\"WPS\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.opengis.net/wps/1.0.0\" xmlns:wfs=\"http://www.opengis.net/wfs\" xmlns:wps=\"http://www.opengis.net/wps/1.0.0\" xmlns:ows=\"http://www.opengis.net/ows/1.1\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:wcs=\"http://www.opengis.net/wcs/1.1.1\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xsi:schemaLocation=\"http://www.opengis.net/wps/1.0.0 http://schemas.opengis.net/wps/1.0.0/wpsAll.xsd\">" + "  <ows:Identifier>gs:GeoPackage</ows:Identifier>") + "  <wps:DataInputs>") + "    <wps:Input>") + "      <ows:Identifier>contents</ows:Identifier>") + "      <wps:Data>") + "        <wps:ComplexData mimeType=\"text/xml; subtype=geoserver/geopackage\"><![CDATA[") + "<!DOCTYPE indexed [") + "<!ELEMENT indexed ANY >") + "<!ENTITY xxe SYSTEM \"file:///this/file/does/not/exist\" >]>") + "<geopackage name=\"test\" xmlns=\"http://www.opengis.net/gpkg\">") + "  <features name=\"lakes\" identifier=\"lakes1\">") + "    <description>lakes description</description>") + "    <featuretype>cite:Lakes</featuretype>") + "    <indexed>&xxe;</indexed>") + "   </features>") + "</geopackage>") + "]]></wps:ComplexData>") + "      </wps:Data>") + "    </wps:Input>") + "  </wps:DataInputs>") + "  <wps:ResponseForm>") + "    <wps:RawDataOutput>") + "      <ows:Identifier>geopackage</ows:Identifier>") + "    </wps:RawDataOutput>") + "  </wps:ResponseForm>") + "</wps:Execute>");
        Document d = postAsDOM("wps", xml);
        Assert.assertEquals("wps:ExecuteResponse", d.getDocumentElement().getNodeName());
        assertXpathExists("/wps:ExecuteResponse/wps:Status/wps:ProcessFailed", d);
        String message = XMLUnit.newXpathEngine().evaluate(("//wps:ExecuteResponse/wps:Status/wps:ProcessFailed" + "/ows:ExceptionReport/ows:Exception/ows:ExceptionText/text()"), d);
        Assert.assertThat(message, CoreMatchers.containsString("Entity resolution disallowed"));
    }

    @Test
    public void testGeoPackageProcessTilesNoFormat() throws Exception {
        String urlPath = string(post("wps", getXmlTilesNoFormat())).trim();
        String resourceUrl = urlPath.substring("http://localhost:8080/geoserver/".length());
        MockHttpServletResponse response = getAsServletResponse(resourceUrl);
        File file = new File(getDataDirectory().findOrCreateDir("tmp"), "test.gpkg");
        FileUtils.writeByteArrayToFile(file, getBinary(response));
        Assert.assertNotNull(file);
        Assert.assertEquals("test.gpkg", file.getName());
        Assert.assertTrue(file.exists());
        GeoPackage gpkg = new GeoPackage(file);
        List<TileEntry> tiles = gpkg.tiles();
        Assert.assertEquals(1, tiles.size());
        TileEntry te = tiles.get(0);
        Assert.assertEquals("world_lakes", te.getTableName());
        Assert.assertEquals("world and lakes overlay", te.getDescription());
        Assert.assertEquals("wl1", te.getIdentifier());
        Assert.assertEquals(4326, te.getSrid().intValue());
        Assert.assertEquals((-0.17578125), te.getBounds().getMinX(), 1.0E-4);
        Assert.assertEquals((-0.087890625), te.getBounds().getMinY(), 1.0E-4);
        Assert.assertEquals(0.17578125, te.getBounds().getMaxX(), 1.0E-4);
        Assert.assertEquals(0.087890625, te.getBounds().getMaxY(), 1.0E-4);
        TileReader tr = gpkg.reader(te, null, null, null, null, null, null);
        Assert.assertTrue(tr.hasNext());
        Assert.assertEquals(10, tr.next().getZoom().intValue());
        tr.close();
        te = tiles.get(0);
        Assert.assertEquals("world_lakes", te.getTableName());
        Assert.assertEquals("world and lakes overlay", te.getDescription());
        Assert.assertEquals("wl1", te.getIdentifier());
        Assert.assertEquals(4326, te.getSrid().intValue());
        Assert.assertEquals((-0.17578125), te.getBounds().getMinX(), 1.0E-4);
        Assert.assertEquals((-0.087890625), te.getBounds().getMinY(), 1.0E-4);
        Assert.assertEquals(0.17578125, te.getBounds().getMaxX(), 1.0E-4);
        Assert.assertEquals(0.087890625, te.getBounds().getMaxY(), 1.0E-4);
        gpkg.close();
    }
}

