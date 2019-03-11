package com.querydsl.sql.spatial;


import java.io.IOException;
import org.geolatte.geom.Geometry;
import org.junit.Assert;
import org.junit.Test;


public class SQLServerGeometryWriterTest extends AbstractConverterTest {
    @Test
    public void roundTrip() throws IOException {
        for (Geometry geometry : getGeometries()) {
            byte[] bytes = new SQLServerGeometryWriter().write(geometry);
            Geometry geometry2 = new SQLServerGeometryReader().read(bytes);
            Assert.assertEquals(geometry, geometry2);
        }
    }
}

