package org.hibernate.userguide.associations;


import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author F?bio Ueno
 */
// end::associations-not-found-domain-model-example[]
public class NotFoundTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::associations-not-found-persist-example[]
            org.hibernate.userguide.associations.City _NewYork = new org.hibernate.userguide.associations.City();
            _NewYork.setName("New York");
            entityManager.persist(_NewYork);
            org.hibernate.userguide.associations.Person person = new org.hibernate.userguide.associations.Person();
            person.setId(1L);
            person.setName("John Doe");
            person.setCityName("New York");
            entityManager.persist(person);
            // end::associations-not-found-persist-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::associations-not-found-find-example[]
            org.hibernate.userguide.associations.Person person = entityManager.find(.class, 1L);
            assertEquals("New York", person.getCity().getName());
            // end::associations-not-found-find-example[]
            // tag::associations-not-found-non-existing-persist-example[]
            person.setCityName("Atlantis");
            // end::associations-not-found-non-existing-persist-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::associations-not-found-non-existing-find-example[]
            org.hibernate.userguide.associations.Person person = entityManager.find(.class, 1L);
            assertEquals("Atlantis", person.getCityName());
            assertNull(null, person.getCity());
            // end::associations-not-found-non-existing-find-example[]
        });
    }

    // tag::associations-not-found-domain-model-example[]
    // tag::associations-not-found-domain-model-example[]
    @Entity
    @Table(name = "Person")
    public static class Person {
        @Id
        private Long id;

        private String name;

        private String cityName;

        @ManyToOne
        @NotFound(action = NotFoundAction.IGNORE)
        @JoinColumn(name = "cityName", referencedColumnName = "name", insertable = false, updatable = false)
        private NotFoundTest.City city;

        // Getters and setters are omitted for brevity
        // end::associations-not-found-domain-model-example[]
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

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
            this.city = null;
        }

        public NotFoundTest.City getCity() {
            return city;
        }
    }

    // tag::associations-not-found-domain-model-example[]
    @Entity
    @Table(name = "City")
    public static class City implements Serializable {
        @Id
        @GeneratedValue
        private Long id;

        private String name;

        // Getters and setters are omitted for brevity
        // end::associations-not-found-domain-model-example[]
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
    }
}

