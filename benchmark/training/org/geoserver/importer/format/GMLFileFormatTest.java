/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.importer.format;


import java.io.File;
import java.util.Date;
import org.geotools.referencing.CRS;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeatureType;


public class GMLFileFormatTest {
    private GMLFileFormat gmlFileFormat;

    @Test
    public void testParsePoiGML2() throws Exception {
        File file = new File("./src/test/resources/org/geoserver/importer/test-data/gml/poi.gml2.gml");
        SimpleFeatureType schema = gmlFileFormat.getSchema(file);
        Assert.assertEquals(Point.class, schema.getGeometryDescriptor().getType().getBinding());
        Assert.assertEquals(CRS.decode("EPSG:4326", true), schema.getGeometryDescriptor().getType().getCoordinateReferenceSystem());
        Assert.assertEquals(String.class, schema.getDescriptor("NAME").getType().getBinding());
        Assert.assertEquals(Integer.class, schema.getDescriptor("intAttribute").getType().getBinding());
        Assert.assertEquals(Double.class, schema.getDescriptor("floatAttribute").getType().getBinding());
    }

    @Test
    public void testParsePoiGML3() throws Exception {
        File file = new File("./src/test/resources/org/geoserver/importer/test-data/gml/poi.gml3.gml");
        SimpleFeatureType schema = gmlFileFormat.getSchema(file);
        Assert.assertEquals(Point.class, schema.getGeometryDescriptor().getType().getBinding());
        Assert.assertEquals(CRS.decode("urn:x-ogc:def:crs:EPSG:4326", false), schema.getGeometryDescriptor().getType().getCoordinateReferenceSystem());
        Assert.assertEquals(String.class, schema.getDescriptor("NAME").getType().getBinding());
        Assert.assertEquals(Integer.class, schema.getDescriptor("intAttribute").getType().getBinding());
        Assert.assertEquals(Double.class, schema.getDescriptor("floatAttribute").getType().getBinding());
    }

    @Test
    public void testParseStreamsGML2() throws Exception {
        File file = new File("./src/test/resources/org/geoserver/importer/test-data/gml/streams.gml2.gml");
        SimpleFeatureType schema = gmlFileFormat.getSchema(file);
        Assert.assertEquals(MultiLineString.class, schema.getGeometryDescriptor().getType().getBinding());
        Assert.assertEquals(CRS.decode("EPSG:26713"), schema.getGeometryDescriptor().getType().getCoordinateReferenceSystem());
        Assert.assertEquals(String.class, schema.getDescriptor("cat").getType().getBinding());
        Assert.assertEquals(Date.class, schema.getDescriptor("acquired").getType().getBinding());
        Assert.assertEquals(Date.class, schema.getDescriptor("acquiredFull").getType().getBinding());
    }

    @Test
    public void testParseStreamsGML3() throws Exception {
        File file = new File("./src/test/resources/org/geoserver/importer/test-data/gml/streams.gml3.gml");
        SimpleFeatureType schema = gmlFileFormat.getSchema(file);
        Assert.assertEquals(MultiLineString.class, schema.getGeometryDescriptor().getType().getBinding());
        Assert.assertEquals(CRS.decode("urn:x-ogc:def:crs:EPSG:26713"), schema.getGeometryDescriptor().getType().getCoordinateReferenceSystem());
        Assert.assertEquals(Integer.class, schema.getDescriptor("cat").getType().getBinding());
    }
}

