/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.spatial.integration.geolatte;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.geolatte.geom.C2D;
import org.geolatte.geom.Point;
import org.geolatte.geom.Polygon;
import org.geolatte.geom.builder.DSL;
import org.geolatte.geom.crs.CoordinateReferenceSystem;
import org.geolatte.geom.crs.CoordinateReferenceSystems;
import org.hibernate.spatial.dialect.postgis.PostgisPG95Dialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


/**
 * Integration tests for Postgis
 *
 * @author Vlad Mihalcea, Karel Maesen
 */
@RequiresDialect(PostgisPG95Dialect.class)
public class PostgisTest extends BaseCoreFunctionalTestCase {
    public static CoordinateReferenceSystem<C2D> crs = CoordinateReferenceSystems.PROJECTED_2D_METER;

    private final Polygon<C2D> window = DSL.polygon(PostgisTest.crs, DSL.ring(DSL.c(1, 1), DSL.c(1, 20), DSL.c(20, 20), DSL.c(20, 1), DSL.c(1, 1)));

    @Test
    public void testBuffer() {
        Long addressId = insertEvent(DSL.c(10, 5));
        doInHibernate(this::sessionFactory, ( session) -> {
            List<org.hibernate.spatial.integration.geolatte.Event> events = session.createQuery(("select e " + ("from Event e " + "where within( e.location, buffer(:window, 100)) = true")), .class).setParameter("window", window).getResultList();
            assertEquals(1, events.size());
        });
    }

    @Test
    public void testMakeEnvelope() {
        Long addressId = insertEvent(DSL.c(10, 5));
        doInHibernate(this::sessionFactory, ( session) -> {
            List<org.hibernate.spatial.integration.geolatte.Event> events = session.createQuery(("select e " + ("from Event e " + "where within(e.location, makeenvelope(0, 0, 11, 11, -1 )) = true")), .class).getResultList();
            assertEquals(1, events.size());
        });
    }

    @Entity(name = "Event")
    public static class Event {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;

        private Point<C2D> location;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Point getLocation() {
            return location;
        }

        public void setLocation(Point location) {
            this.location = location;
        }
    }
}

