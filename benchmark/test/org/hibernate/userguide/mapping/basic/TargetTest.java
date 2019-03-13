/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.mapping.basic;


import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.annotations.Target;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::mapping-Target-example[]
public class TargetTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLifecycle() {
        // tag::mapping-Target-persist-example[]
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.mapping.basic.City cluj = new org.hibernate.userguide.mapping.basic.City();
            cluj.setName("Cluj");
            cluj.setCoordinates(new org.hibernate.userguide.mapping.basic.GPS(46.7712, 23.6236));
            entityManager.persist(cluj);
        });
        // end::mapping-Target-persist-example[]
        // tag::mapping-Target-fetching-example[]
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.mapping.basic.City cluj = entityManager.find(.class, 1L);
            assertEquals(46.7712, cluj.getCoordinates().x(), 1.0E-5);
            assertEquals(23.6236, cluj.getCoordinates().y(), 1.0E-5);
        });
        // end::mapping-Target-fetching-example[]
    }

    // tag::mapping-Target-example[]
    public interface Coordinates {
        double x();

        double y();
    }

    @Embeddable
    public static class GPS implements TargetTest.Coordinates {
        private double latitude;

        private double longitude;

        private GPS() {
        }

        public GPS(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public double x() {
            return latitude;
        }

        @Override
        public double y() {
            return longitude;
        }
    }

    // tag::mapping-Target-example[]
    @Entity(name = "City")
    public static class City {
        @Id
        @GeneratedValue
        private Long id;

        private String name;

        @Embedded
        @Target(TargetTest.GPS.class)
        private TargetTest.Coordinates coordinates;

        // Getters and setters omitted for brevity
        // end::mapping-Target-example[]
        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public TargetTest.Coordinates getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(TargetTest.Coordinates coordinates) {
            this.coordinates = coordinates;
        }
    }
}

