package com.querydsl.sql.spatial;


import java.util.List;
import org.geolatte.geom.Geometry;
import org.junit.Assert;
import org.junit.Test;


public class PGgeometryConverterTest extends AbstractConverterTest {
    @Test
    public void roundTrip() {
        List<Geometry> geometries = getGeometries();
        for (Geometry geometry : geometries) {
            org.postgis.Geometry converted = PGgeometryConverter.convert(geometry);
            Geometry back = PGgeometryConverter.convert(converted);
            Assert.assertEquals(geometry, back);
        }
    }
}

