/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.mapping.generated;


import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::mapping-generated-CreationTimestamp-example[]
public class CreationTimestampTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::mapping-generated-CreationTimestamp-persist-example[]
            org.hibernate.userguide.mapping.generated.Event dateEvent = new org.hibernate.userguide.mapping.generated.Event();
            entityManager.persist(dateEvent);
            // end::mapping-generated-CreationTimestamp-persist-example[]
        });
    }

    // tag::mapping-generated-CreationTimestamp-example[]
    // tag::mapping-generated-CreationTimestamp-example[]
    @Entity(name = "Event")
    public static class Event {
        @Id
        @GeneratedValue
        private Long id;

        @Column(name = "`timestamp`")
        @CreationTimestamp
        private Date timestamp;

        // Constructors, getters, and setters are omitted for brevity
        // end::mapping-generated-CreationTimestamp-example[]
        public Event() {
        }

        public Long getId() {
            return id;
        }

        public Date getTimestamp() {
            return timestamp;
        }
    }
}

