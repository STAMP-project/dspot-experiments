/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.envers;


import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.Audited;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::envers-audited-mapping-example[]
public class DefaultAuditTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::envers-audited-insert-example[]
            org.hibernate.userguide.envers.Customer customer = new org.hibernate.userguide.envers.Customer();
            customer.setId(1L);
            customer.setFirstName("John");
            customer.setLastName("Doe");
            entityManager.persist(customer);
            // end::envers-audited-insert-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::envers-audited-update-example[]
            org.hibernate.userguide.envers.Customer customer = entityManager.find(.class, 1L);
            customer.setLastName("Doe Jr.");
            // end::envers-audited-update-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::envers-audited-delete-example[]
            org.hibernate.userguide.envers.Customer customer = entityManager.getReference(.class, 1L);
            entityManager.remove(customer);
            // end::envers-audited-delete-example[]
        });
        // tag::envers-audited-revisions-example[]
        List<Number> revisions = doInJPA(this::entityManagerFactory, ( entityManager) -> {
            return AuditReaderFactory.get(entityManager).getRevisions(.class, 1L);
        });
        // end::envers-audited-revisions-example[]
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::envers-audited-rev1-example[]
            org.hibernate.userguide.envers.Customer customer = ((org.hibernate.userguide.envers.Customer) (AuditReaderFactory.get(entityManager).createQuery().forEntitiesAtRevision(.class, revisions.get(0)).getSingleResult()));
            assertEquals("Doe", customer.getLastName());
            // end::envers-audited-rev1-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::envers-audited-rev2-example[]
            org.hibernate.userguide.envers.Customer customer = ((org.hibernate.userguide.envers.Customer) (AuditReaderFactory.get(entityManager).createQuery().forEntitiesAtRevision(.class, revisions.get(1)).getSingleResult()));
            assertEquals("Doe Jr.", customer.getLastName());
            // end::envers-audited-rev2-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::envers-audited-rev3-example[]
            try {
                org.hibernate.userguide.envers.Customer customer = ((org.hibernate.userguide.envers.Customer) (AuditReaderFactory.get(entityManager).createQuery().forEntitiesAtRevision(.class, revisions.get(2)).getSingleResult()));
                fail(("The Customer was deleted at this revision: " + (revisions.get(2))));
            } catch ( expected) {
            }
            // end::envers-audited-rev3-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::envers-audited-rev4-example[]
            org.hibernate.userguide.envers.Customer customer = ((org.hibernate.userguide.envers.Customer) (AuditReaderFactory.get(entityManager).createQuery().forEntitiesAtRevision(.class, .class.getName(), revisions.get(2), true).getSingleResult()));
            assertEquals(Long.valueOf(1L), customer.getId());
            assertNull(customer.getFirstName());
            assertNull(customer.getLastName());
            assertNull(customer.getCreatedOn());
            // end::envers-audited-rev4-example[]
        });
    }

    // tag::envers-audited-mapping-example[]
    // tag::envers-audited-mapping-example[]
    @Audited
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

        // Getters and setters are omitted for brevity
        // end::envers-audited-mapping-example[]
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
    }
}

