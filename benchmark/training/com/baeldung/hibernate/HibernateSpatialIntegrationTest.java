package com.baeldung.hibernate;


import com.baeldung.hibernate.pojo.PointEntity;
import com.baeldung.hibernate.pojo.PolygonEntity;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import javax.persistence.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Test;


public class HibernateSpatialIntegrationTest {
    private Session session;

    private Transaction transaction;

    @Test
    public void shouldConvertWktToGeometry() throws ParseException {
        Geometry geometry = wktToGeometry("POINT (2 5)");
        Assert.assertEquals("Point", geometry.getGeometryType());
        Assert.assertTrue((geometry instanceof Point));
    }

    @Test
    public void shouldInsertAndSelectPoints() throws ParseException {
        PointEntity entity = new PointEntity();
        entity.setPoint(((Point) (wktToGeometry("POINT (1 1)"))));
        session.persist(entity);
        PointEntity fromDb = session.find(PointEntity.class, entity.getId());
        Assert.assertEquals("POINT (1 1)", fromDb.getPoint().toString());
    }

    @Test
    public void shouldSelectDisjointPoints() throws ParseException {
        insertPoint("POINT (1 2)");
        insertPoint("POINT (3 4)");
        insertPoint("POINT (5 6)");
        Point point = ((Point) (wktToGeometry("POINT (3 4)")));
        Query query = session.createQuery(("select p from PointEntity p " + "where disjoint(p.point, :point) = true"), PointEntity.class);
        query.setParameter("point", point);
        Assert.assertEquals("POINT (1 2)", getPoint().toString());
        Assert.assertEquals("POINT (5 6)", getPoint().toString());
    }

    @Test
    public void shouldSelectAllPointsWithinPolygon() throws ParseException {
        insertPoint("POINT (1 1)");
        insertPoint("POINT (1 2)");
        insertPoint("POINT (3 4)");
        insertPoint("POINT (5 6)");
        Query query = session.createQuery("select p from PointEntity p where within(p.point, :area) = true", PointEntity.class);
        query.setParameter("area", wktToGeometry("POLYGON((0 0, 0 5, 5 5, 5 0, 0 0))"));
        assertThat(query.getResultList().stream().map(( p) -> getPoint().toString())).containsOnly("POINT (1 1)", "POINT (1 2)", "POINT (3 4)");
    }

    @Test
    public void shouldSelectAllPointsWithinRadius() throws ParseException {
        insertPoint("POINT (1 1)");
        insertPoint("POINT (1 2)");
        insertPoint("POINT (3 4)");
        insertPoint("POINT (5 6)");
        Query query = session.createQuery("select p from PointEntity p where within(p.point, :circle) = true", PointEntity.class);
        query.setParameter("circle", HibernateSpatialIntegrationTest.createCircle(0.0, 0.0, 5));
        assertThat(query.getResultList().stream().map(( p) -> getPoint().toString())).containsOnly("POINT (1 1)", "POINT (1 2)");
    }

    @Test
    public void shouldSelectAdjacentPolygons() throws ParseException {
        insertPolygon("POLYGON ((0 0, 0 5, 5 5, 5 0, 0 0))");
        insertPolygon("POLYGON ((3 0, 3 5, 8 5, 8 0, 3 0))");
        insertPolygon("POLYGON ((2 2, 3 1, 2 5, 4 3, 3 3, 2 2))");
        Query query = session.createQuery("select p from PolygonEntity p where touches(p.polygon, :polygon) = true", PolygonEntity.class);
        query.setParameter("polygon", wktToGeometry("POLYGON ((5 5, 5 10, 10 10, 10 5, 5 5))"));
        assertThat(query.getResultList().stream().map(( p) -> ((PolygonEntity) (p)).getPolygon().toString())).containsOnly("POLYGON ((0 0, 0 5, 5 5, 5 0, 0 0))", "POLYGON ((3 0, 3 5, 8 5, 8 0, 3 0))");
    }
}

