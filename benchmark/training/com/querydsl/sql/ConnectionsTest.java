package com.querydsl.sql;


import Wkt.Dialect.POSTGIS_EWKT_1;
import org.geolatte.geom.codec.Wkt;
import org.junit.Assert;
import org.junit.Test;


public class ConnectionsTest {
    @Test
    public void valid_wkt() {
        for (String wkt : Connections.getSpatialData().values()) {
            Assert.assertNotNull(Wkt.newDecoder(POSTGIS_EWKT_1).decode(wkt));
        }
    }
}

