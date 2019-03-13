/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.mapping.basic;


import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class DateWithTemporalTimeTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLifecycle() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.mapping.basic.DateEvent dateEvent = new org.hibernate.userguide.mapping.basic.DateEvent(new Date());
            entityManager.persist(dateEvent);
        });
    }

    @Entity(name = "DateEvent")
    public static class DateEvent {
        @Id
        @GeneratedValue
        private Long id;

        // tag::basic-datetime-temporal-time-example[]
        @Column(name = "`timestamp`")
        @Temporal(TemporalType.TIME)
        private Date timestamp;

        // end::basic-datetime-temporal-time-example[]
        public DateEvent() {
        }

        public DateEvent(Date timestamp) {
            this.timestamp = timestamp;
        }

        public Long getId() {
            return id;
        }

        public Date getTimestamp() {
            return timestamp;
        }
    }
}

