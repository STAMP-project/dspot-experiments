/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.spatial.integration;


import java.sql.SQLException;
import org.hibernate.spatial.HSMessageLogger;
import org.hibernate.spatial.dialect.h2geodb.GeoDBDialect;
import org.hibernate.spatial.dialect.hana.HANASpatialDialect;
import org.hibernate.spatial.dialect.oracle.OracleSpatial10gDialect;
import org.hibernate.spatial.testing.SpatialDialectMatcher;
import org.hibernate.spatial.testing.SpatialFunctionalTestCase;
import org.hibernate.testing.Skip;
import org.hibernate.testing.SkipForDialect;
import org.jboss.logging.Logger;
import org.junit.Test;


/**
 *
 *
 * @author Karel Maesen, Geovise BVBA
 */
@Skip(condition = SpatialDialectMatcher.class, message = "No Spatial Dialect")
public class TestSpatialFunctions extends SpatialFunctionalTestCase {
    private static final HSMessageLogger LOG = Logger.getMessageLogger(HSMessageLogger.class, TestSpatialFunctions.class.getName());

    @Test
    public void test_dimension_on_jts() throws SQLException {
        dimension(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_dimension_on_geolatte() throws SQLException {
        dimension(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_astext_on_jts() throws SQLException {
        astext(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_astext_on_geolatte() throws SQLException {
        astext(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_asbinary_on_jts() throws SQLException {
        asbinary(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_asbinary_on_geolatte() throws SQLException {
        asbinary(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_geometrytype_on_jts() throws SQLException {
        geometrytype(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_geometrytype_on_geolatte() throws SQLException {
        geometrytype(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_srid_on_jts() throws SQLException {
        srid(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_srid_on_geolatte() throws SQLException {
        srid(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_issimple_on_jts() throws SQLException {
        issimple(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_issimple_on_geolatte() throws SQLException {
        issimple(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_isempty_on_jts() throws SQLException {
        isempty(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_isempty_on_geolatte() throws SQLException {
        isempty(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_boundary_on_jts() throws SQLException {
        boundary(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_boundary_on_geolatte() throws SQLException {
        boundary(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_envelope_on_jts() throws SQLException {
        envelope(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_envelope_on_geolatte() throws SQLException {
        envelope(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_within_on_jts() throws SQLException {
        within(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_within_on_geolatte() throws SQLException {
        within(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_equals_on_jts() throws SQLException {
        equals(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_equals_on_geolatte() throws SQLException {
        equals(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_crosses_on_jts() throws SQLException {
        crosses(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_crosses_on_geolatte() throws SQLException {
        crosses(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_contains_on_jts() throws SQLException {
        contains(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_contains_on_geolatte() throws SQLException {
        contains(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_disjoint_on_jts() throws SQLException {
        disjoint(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_disjoint_on_geolatte() throws SQLException {
        disjoint(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_intersects_on_jts() throws SQLException {
        intersects(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_intersects_on_geolatte() throws SQLException {
        intersects(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_overlaps_on_jts() throws SQLException {
        overlaps(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_overlaps_on_geolatte() throws SQLException {
        overlaps(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_touches_on_jts() throws SQLException {
        touches(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_touches_on_geolatte() throws SQLException {
        touches(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_relate_on_jts() throws SQLException {
        relate(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_relate_on_geolatte() throws SQLException {
        relate(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_distance_on_jts() throws SQLException {
        distance(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_distance_on_geolatte() throws SQLException {
        distance(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_buffer_on_jts() throws SQLException {
        buffer(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_buffer_on_geolatte() throws SQLException {
        buffer(SpatialFunctionalTestCase.GEOLATTE);
    }

    // The convexhull tests are skipped for Oracle because of error:
    // ORA-13276: internal error [Geodetic Geometry too big for Local Transform] in coordinate transformation
    @Test
    @SkipForDialect(OracleSpatial10gDialect.class)
    public void test_convexhull_on_jts() throws SQLException {
        convexhull(SpatialFunctionalTestCase.JTS);
    }

    @Test
    @SkipForDialect(OracleSpatial10gDialect.class)
    public void test_convexhull_on_geolatte() throws SQLException {
        convexhull(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    @SkipForDialect(GeoDBDialect.class)
    public void test_intersection_on_jts() throws SQLException {
        intersection(SpatialFunctionalTestCase.JTS);
    }

    @Test
    @SkipForDialect(GeoDBDialect.class)
    public void test_intersection_on_geolatte() throws SQLException {
        intersection(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_difference_on_jts() throws SQLException {
        difference(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_difference_on_geolatte() throws SQLException {
        difference(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_symdifference_on_jts() throws SQLException {
        symdifference(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_symdifference_on_geolatte() throws SQLException {
        symdifference(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_geomunion_on_jts() throws SQLException {
        geomunion(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_geomunion_on_geolatte() throws SQLException {
        geomunion(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_dwithin_on_jts() throws SQLException {
        dwithin(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_dwithin_on_geolatte() throws SQLException {
        dwithin(SpatialFunctionalTestCase.GEOLATTE);
    }

    // The transform tests are skipped for HANA because there is no transform definition for SRID 0
    @Test
    @SkipForDialect(HANASpatialDialect.class)
    public void test_transform_on_jts() throws SQLException {
        transform(SpatialFunctionalTestCase.JTS);
    }

    @Test
    @SkipForDialect(HANASpatialDialect.class)
    public void test_transform_on_geolatte() throws SQLException {
        transform(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_extent_on_jts() throws SQLException {
        extent(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_extent_on_geolatte() throws SQLException {
        extent(SpatialFunctionalTestCase.GEOLATTE);
    }
}

