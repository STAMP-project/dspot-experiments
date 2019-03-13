/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.spatial.dialect.postgis;


import ByteOrder.NDR;
import ByteOrder.XDR;
import CoordinateReferenceSystems.PROJECTED_2D_METER;
import java.sql.SQLException;
import org.geolatte.geom.C2D;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.builder.DSL;
import org.geolatte.geom.codec.Wkb;
import org.geolatte.geom.codec.Wkt;
import org.geolatte.geom.crs.CoordinateReferenceSystem;
import org.geolatte.geom.crs.CoordinateReferenceSystems;
import org.junit.Test;


/**
 * Tests the different ways Postgis seraialises Geometries
 * <p>
 * Created by Karel Maesen, Geovise BVBA on 29/10/16.
 */
public class PostgisUnmarshalTest {
    private CoordinateReferenceSystem<G2D> crs = CoordinateReferenceSystems.WGS84;

    private Geometry<G2D> geom = DSL.linestring(crs, DSL.g(6.123, 53.234), DSL.g(6.133, 53.244));

    private Geometry<C2D> geomNoSrid = DSL.linestring(PROJECTED_2D_METER, DSL.c(6.123, 53.234), DSL.c(6.133, 53.244));

    @Test
    public void testWktWithSrid() throws SQLException {
        String ewkt = Wkt.toWkt(geom);
        testCase(ewkt, geom);
    }

    @Test
    public void testWktWithoutSrid() throws SQLException {
        String wkt = Wkt.toWkt(geom).split(";")[1];
        testCase(wkt, geomNoSrid);
    }

    @Test
    public void testWkbXDR() throws SQLException {
        String wkb = Wkb.toWkb(geom, XDR).toString();
        testCase(wkb, geom);
    }

    @Test
    public void testWkbNDR() throws SQLException {
        String wkb = Wkb.toWkb(geom, NDR).toString();
        testCase(wkb, geom);
    }
}

