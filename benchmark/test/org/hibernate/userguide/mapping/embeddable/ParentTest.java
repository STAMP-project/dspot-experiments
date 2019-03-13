/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.mapping.embeddable;


import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.annotations.Parent;
import org.hibernate.annotations.Target;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::embeddable-Parent-example[]
public class ParentTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLifecycle() {
        // tag::embeddable-Parent-persist-example[]
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.mapping.embeddable.City cluj = new org.hibernate.userguide.mapping.embeddable.City();
            cluj.setName("Cluj");
            cluj.setCoordinates(new org.hibernate.userguide.mapping.embeddable.GPS(46.7712, 23.6236));
            entityManager.persist(cluj);
        });
        // end::embeddable-Parent-persist-example[]
        // tag::embeddable-Parent-fetching-example[]
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.mapping.embeddable.City cluj = entityManager.find(.class, 1L);
            assertSame(cluj, cluj.getCoordinates().getCity());
        });
        // end::embeddable-Parent-fetching-example[]
    }

    // tag::embeddable-Parent-example[]
    // tag::embeddable-Parent-example[]
    @Embeddable
    public static class GPS {
        private double latitude;

        private double longitude;

        @Parent
        private ParentTest.City city;

        // Getters and setters omitted for brevity
        // end::embeddable-Parent-example[]
        private GPS() {
        }

        public GPS(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public ParentTest.City getCity() {
            return city;
        }

        public void setCity(ParentTest.City city) {
            this.city = city;
        }
    }

    // end::embeddable-Parent-example[]
    // tag::embeddable-Parent-example[]
    // tag::embeddable-Parent-example[]
    @Entity(name = "City")
    public static class City {
        @Id
        @GeneratedValue
        private Long id;

        private String name;

        @Embedded
        @Target(ParentTest.GPS.class)
        private ParentTest.GPS coordinates;

        // Getters and setters omitted for brevity
        // end::embeddable-Parent-example[]
        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ParentTest.GPS getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(ParentTest.GPS coordinates) {
            this.coordinates = coordinates;
        }
    }
}

