/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.immutability;


import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.annotations.Immutable;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::entity-immutability-example[]
public class EntityImmutabilityTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        // tag::entity-immutability-persist-example[]
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.immutability.Event event = new org.hibernate.userguide.immutability.Event();
            event.setId(1L);
            event.setCreatedOn(new Date());
            event.setMessage("Hibernate User Guide rocks!");
            entityManager.persist(event);
        });
        // end::entity-immutability-persist-example[]
        // tag::entity-immutability-update-example[]
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.immutability.Event event = entityManager.find(.class, 1L);
            log.info("Change event message");
            event.setMessage("Hibernate User Guide");
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.immutability.Event event = entityManager.find(.class, 1L);
            assertEquals("Hibernate User Guide rocks!", event.getMessage());
        });
        // end::entity-immutability-update-example[]
    }

    // tag::entity-immutability-example[]
    // tag::entity-immutability-example[]
    @Entity(name = "Event")
    @Immutable
    public static class Event {
        @Id
        private Long id;

        private Date createdOn;

        private String message;

        // Getters and setters are omitted for brevity
        // end::entity-immutability-example[]
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Date getCreatedOn() {
            return createdOn;
        }

        public void setCreatedOn(Date createdOn) {
            this.createdOn = createdOn;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}

