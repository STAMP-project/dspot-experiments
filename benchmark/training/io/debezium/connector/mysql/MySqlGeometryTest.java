/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.mysql;


import io.debezium.data.geometry.Point;
import javax.xml.bind.DatatypeConverter;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Omar Al-Safi
 */
public class MySqlGeometryTest {
    @Test
    public void shouldConvertMySqlBytesToPoint() throws Exception {
        byte[] mysqlBytes = DatatypeConverter.parseHexBinary("000000000101000000e3a59bc420f01b4015a143a69d383240");
        // This represents 'POINT(6.9845 18.22115554)'
        MySqlGeometry geom = MySqlGeometry.fromBytes(mysqlBytes);
        Assert.assertTrue(geom.isPoint());
        Assert.assertEquals(geom.getSrid(), null);
        double[] coords = Point.parseWKBPoint(geom.getWkb());
        Assert.assertEquals(coords[0], 6.9845, 1.0E-4);
        Assert.assertEquals(coords[1], 18.22115554, 1.0E-4);
    }

    @Test
    public void shouldConvertMySqlBytesToLine() throws Exception {
        byte[] mysqlBytes = DatatypeConverter.parseHexBinary("E6100000010200000002000000E3A59BC420F01B4015A143A69D38324000000000000000000000000000000000");
        // This represents 'SRID=4326;LINESTRING(6.9845 18.22115554, 0 0)'
        MySqlGeometry geom = MySqlGeometry.fromBytes(mysqlBytes);
        Assert.assertFalse(geom.isPoint());
        Assert.assertEquals(Integer.valueOf(4326), geom.getSrid());
        Assert.assertEquals("010200000002000000E3A59BC420F01B4015A143A69D38324000000000000000000000000000000000", DatatypeConverter.printHexBinary(geom.getWkb()));
    }

    @Test
    public void shouldConvertMySqlBytesToPolygon() throws Exception {
        byte[] mysqlBytes = DatatypeConverter.parseHexBinary("E61000000103000000010000000400000000000000000000000000000000000000000000000000F03F000000000000F03F000000000000F03F000000000000000000000000000000000000000000000000");
        // This represents 'SRID=4326;POLYGON((0 0, 1 1, 1 0, 0 0))'
        MySqlGeometry geom = MySqlGeometry.fromBytes(mysqlBytes);
        Assert.assertFalse(geom.isPoint());
        Assert.assertEquals(geom.getSrid(), Integer.valueOf(4326));
        Assert.assertEquals("0103000000010000000400000000000000000000000000000000000000000000000000F03F000000000000F03F000000000000F03F000000000000000000000000000000000000000000000000", DatatypeConverter.printHexBinary(geom.getWkb()));
    }

    @Test
    public void shouldConvertMySqlBytesToGeomCollection() throws Exception {
        byte[] mysqlBytes = DatatypeConverter.parseHexBinary("730C00000107000000020000000101000000000000000000F03F000000000000F03F01020000000200000000000000000000000000000000000000000000000000F03F000000000000F03F");
        // This represents 'SRID=3187;GEOMETRYCOLLECTION(POINT(1 1), LINESTRING(0 0, 1 1))'
        MySqlGeometry geom = MySqlGeometry.fromBytes(mysqlBytes);
        Assert.assertFalse(geom.isPoint());
        Assert.assertEquals(geom.getSrid(), Integer.valueOf(3187));
        Assert.assertEquals("0107000000020000000101000000000000000000F03F000000000000F03F01020000000200000000000000000000000000000000000000000000000000F03F000000000000F03F", DatatypeConverter.printHexBinary(geom.getWkb()));
    }

    @Test
    public void shouldConvertMySqlBytesToMultiGeometry() throws Exception {
        byte[] mysqlBytes = DatatypeConverter.parseHexBinary("000000000104000000020000000101000000000000000000F03F000000000000F03F010100000000000000000000400000000000000040");
        // This represents 'MULTIPOINT(1 1, 2 2)''
        MySqlGeometry geom = MySqlGeometry.fromBytes(mysqlBytes);
        Assert.assertFalse(geom.isPoint());
        Assert.assertEquals(geom.getSrid(), null);
        Assert.assertEquals("0104000000020000000101000000000000000000F03F000000000000F03F010100000000000000000000400000000000000040", DatatypeConverter.printHexBinary(geom.getWkb()));
    }
}

