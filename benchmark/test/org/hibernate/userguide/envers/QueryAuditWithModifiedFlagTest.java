/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.envers;


import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.Audited;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class QueryAuditWithModifiedFlagTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.envers.Address address = new org.hibernate.userguide.envers.Address();
            address.setId(1L);
            address.setCountry("Rom?nia");
            address.setCity("Cluj-Napoca");
            address.setStreet("Bulevardul Eroilor");
            address.setStreetNumber("1 A");
            entityManager.persist(address);
            org.hibernate.userguide.envers.Customer customer = new org.hibernate.userguide.envers.Customer();
            customer.setId(1L);
            customer.setFirstName("John");
            customer.setLastName("Doe");
            customer.setAddress(address);
            entityManager.persist(customer);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.envers.Customer customer = entityManager.find(.class, 1L);
            customer.setLastName("Doe Jr.");
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.envers.Customer customer = entityManager.getReference(.class, 1L);
            entityManager.remove(customer);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::envers-tracking-properties-changes-queries-hasChanged-example[]
            List<org.hibernate.userguide.envers.Customer> customers = AuditReaderFactory.get(entityManager).createQuery().forRevisionsOfEntity(.class, false, true).add(AuditEntity.id().eq(1L)).add(AuditEntity.property("lastName").hasChanged()).getResultList();
            // end::envers-tracking-properties-changes-queries-hasChanged-example[]
            assertEquals(3, customers.size());
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::envers-tracking-properties-changes-queries-hasChanged-and-hasNotChanged-example[]
            List<org.hibernate.userguide.envers.Customer> customers = AuditReaderFactory.get(entityManager).createQuery().forRevisionsOfEntity(.class, false, true).add(AuditEntity.id().eq(1L)).add(AuditEntity.property("lastName").hasChanged()).add(AuditEntity.property("firstName").hasNotChanged()).getResultList();
            // end::envers-tracking-properties-changes-queries-hasChanged-and-hasNotChanged-example[]
            assertEquals(1, customers.size());
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::envers-tracking-properties-changes-queries-at-revision-example[]
            org.hibernate.userguide.envers.Customer customer = ((org.hibernate.userguide.envers.Customer) (AuditReaderFactory.get(entityManager).createQuery().forEntitiesModifiedAtRevision(.class, 2).add(AuditEntity.id().eq(1L)).add(AuditEntity.property("lastName").hasChanged()).add(AuditEntity.property("firstName").hasNotChanged()).getSingleResult()));
            // end::envers-tracking-properties-changes-queries-at-revision-example[]
            assertNotNull(customer);
        });
    }

    // tag::envers-tracking-properties-changes-queries-entity-example[]
    // end::envers-tracking-properties-changes-queries-entity-example[]
    @Audited(withModifiedFlag = true)
    @Entity(name = "Customer")
    public static class Customer {
        @Id
        private Long id;

        private String firstName;

        private String lastName;

        @Temporal(TemporalType.TIMESTAMP)
        @Column(name = "created_on")
        @CreationTimestamp
        private Date createdOn;

        @ManyToOne(fetch = FetchType.LAZY)
        private QueryAuditWithModifiedFlagTest.Address address;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public Date getCreatedOn() {
            return createdOn;
        }

        public void setCreatedOn(Date createdOn) {
            this.createdOn = createdOn;
        }

        public QueryAuditWithModifiedFlagTest.Address getAddress() {
            return address;
        }

        public void setAddress(QueryAuditWithModifiedFlagTest.Address address) {
            this.address = address;
        }
    }

    @Audited
    @Entity(name = "Address")
    public static class Address {
        @Id
        private Long id;

        private String country;

        private String city;

        private String street;

        private String streetNumber;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getStreetNumber() {
            return streetNumber;
        }

        public void setStreetNumber(String streetNumber) {
            this.streetNumber = streetNumber;
        }
    }
}

