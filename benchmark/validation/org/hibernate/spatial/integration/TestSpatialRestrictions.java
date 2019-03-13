/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.spatial.integration;


import SpatialFunction.contains;
import SpatialFunction.crosses;
import SpatialFunction.disjoint;
import SpatialFunction.dwithin;
import SpatialFunction.equals;
import SpatialFunction.intersects;
import SpatialFunction.isempty;
import SpatialFunction.overlaps;
import SpatialFunction.srid;
import SpatialFunction.touches;
import SpatialFunction.within;
import java.sql.SQLException;
import java.util.Map;
import org.hibernate.criterion.Criterion;
import org.hibernate.spatial.HSMessageLogger;
import org.hibernate.spatial.criterion.SpatialRestrictions;
import org.hibernate.spatial.dialect.hana.HANASpatialDialect;
import org.hibernate.spatial.testing.SpatialDialectMatcher;
import org.hibernate.spatial.testing.SpatialFunctionalTestCase;
import org.hibernate.testing.Skip;
import org.hibernate.testing.SkipForDialect;
import org.jboss.logging.Logger;
import org.junit.Test;


@Skip(condition = SpatialDialectMatcher.class, message = "No Spatial Dialect")
@SkipForDialect(value = HANASpatialDialect.class, comment = "The HANA dialect is tested via org.hibernate.spatial.dialect.hana.TestHANASpatialFunctions", jiraKey = "HHH-12426")
public class TestSpatialRestrictions extends SpatialFunctionalTestCase {
    private static HSMessageLogger LOG = Logger.getMessageLogger(HSMessageLogger.class, TestSpatialRestrictions.class.getName());

    @Test
    public void within() throws SQLException {
        if (!(isSupportedByDialect(within))) {
            return;
        }
        Map<Integer, Boolean> dbexpected = expectationsFactory.getWithin(expectationsFactory.getTestPolygon());
        Criterion spatialCriterion = SpatialRestrictions.within("geom", expectationsFactory.getTestPolygon());
        retrieveAndCompare(dbexpected, spatialCriterion);
    }

    @Test
    public void filter() throws SQLException {
        if (!(dialectSupportsFiltering())) {
            return;
        }
        Map<Integer, Boolean> dbexpected = expectationsFactory.getFilter(expectationsFactory.getTestPolygon());
        Criterion spatialCriterion = SpatialRestrictions.filter("geom", expectationsFactory.getTestPolygon());
        retrieveAndCompare(dbexpected, spatialCriterion);
    }

    @Test
    public void contains() throws SQLException {
        if (!(isSupportedByDialect(contains))) {
            return;
        }
        Map<Integer, Boolean> dbexpected = expectationsFactory.getContains(expectationsFactory.getTestPolygon());
        Criterion spatialCriterion = SpatialRestrictions.contains("geom", expectationsFactory.getTestPolygon());
        retrieveAndCompare(dbexpected, spatialCriterion);
    }

    @Test
    public void crosses() throws SQLException {
        if (!(isSupportedByDialect(crosses))) {
            return;
        }
        Map<Integer, Boolean> dbexpected = expectationsFactory.getCrosses(expectationsFactory.getTestPolygon());
        Criterion spatialCriterion = SpatialRestrictions.crosses("geom", expectationsFactory.getTestPolygon());
        retrieveAndCompare(dbexpected, spatialCriterion);
    }

    @Test
    public void touches() throws SQLException {
        if (!(isSupportedByDialect(touches))) {
            return;
        }
        Map<Integer, Boolean> dbexpected = expectationsFactory.getTouches(expectationsFactory.getTestPolygon());
        Criterion spatialCriterion = SpatialRestrictions.touches("geom", expectationsFactory.getTestPolygon());
        retrieveAndCompare(dbexpected, spatialCriterion);
    }

    @Test
    public void disjoint() throws SQLException {
        if (!(isSupportedByDialect(disjoint))) {
            return;
        }
        Map<Integer, Boolean> dbexpected = expectationsFactory.getDisjoint(expectationsFactory.getTestPolygon());
        Criterion spatialCriterion = SpatialRestrictions.disjoint("geom", expectationsFactory.getTestPolygon());
        retrieveAndCompare(dbexpected, spatialCriterion);
    }

    @Test
    public void eq() throws SQLException {
        if (!(isSupportedByDialect(equals))) {
            return;
        }
        Map<Integer, Boolean> dbexpected = expectationsFactory.getEquals(expectationsFactory.getTestPolygon());
        Criterion spatialCriterion = SpatialRestrictions.eq("geom", expectationsFactory.getTestPolygon());
        retrieveAndCompare(dbexpected, spatialCriterion);
    }

    @Test
    public void intersects() throws SQLException {
        if (!(isSupportedByDialect(intersects))) {
            return;
        }
        Map<Integer, Boolean> dbexpected = expectationsFactory.getIntersects(expectationsFactory.getTestPolygon());
        Criterion spatialCriterion = SpatialRestrictions.intersects("geom", expectationsFactory.getTestPolygon());
        retrieveAndCompare(dbexpected, spatialCriterion);
    }

    @Test
    public void overlaps() throws SQLException {
        if (!(isSupportedByDialect(overlaps))) {
            return;
        }
        Map<Integer, Boolean> dbexpected = expectationsFactory.getOverlaps(expectationsFactory.getTestPolygon());
        Criterion spatialCriterion = SpatialRestrictions.overlaps("geom", expectationsFactory.getTestPolygon());
        retrieveAndCompare(dbexpected, spatialCriterion);
    }

    @Test
    public void dwithin() throws SQLException {
        if (!(isSupportedByDialect(dwithin))) {
            return;
        }
        Map<Integer, Boolean> dbexpected = expectationsFactory.getDwithin(expectationsFactory.getTestPoint(), 30.0);
        Criterion spatialCriterion = SpatialRestrictions.distanceWithin("geom", expectationsFactory.getTestPoint(), 30.0);
        retrieveAndCompare(dbexpected, spatialCriterion);
    }

    @Test
    public void isEmpty() throws SQLException {
        if (!(isSupportedByDialect(isempty))) {
            return;
        }
        Map<Integer, Boolean> dbexpected = expectationsFactory.getIsEmpty();
        Criterion spatialCriterion = SpatialRestrictions.isEmpty("geom");
        retrieveAndCompare(dbexpected, spatialCriterion);
    }

    @Test
    public void isNotEmpty() throws SQLException {
        if (!(isSupportedByDialect(isempty))) {
            return;
        }
        Map<Integer, Boolean> dbexpected = expectationsFactory.getIsNotEmpty();
        Criterion spatialCriterion = SpatialRestrictions.isNotEmpty("geom");
        retrieveAndCompare(dbexpected, spatialCriterion);
    }

    @Test
    public void havingSRID() throws SQLException {
        if (!(isSupportedByDialect(srid))) {
            return;
        }
        Map<Integer, Boolean> dbexpected = expectationsFactory.havingSRID(4326);
        Criterion spatialCriterion = SpatialRestrictions.havingSRID("geom", 4326);
        retrieveAndCompare(dbexpected, spatialCriterion);
        dbexpected = expectationsFactory.havingSRID(31370);
        spatialCriterion = SpatialRestrictions.havingSRID("geom", 31370);
        retrieveAndCompare(dbexpected, spatialCriterion);
    }
}

