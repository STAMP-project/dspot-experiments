/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.mapping.basic;


import java.util.Calendar;
import java.util.GregorianCalendar;
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
public class CalendarWithTemporalTimestampTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLifecycle() {
        final Calendar calendar = new GregorianCalendar();
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            entityManager.persist(new org.hibernate.userguide.mapping.basic.DateEvent(calendar));
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.mapping.basic.DateEvent dateEvent = entityManager.createQuery("from DateEvent", .class).getSingleResult();
            // Assert.assertEquals( calendar, dateEvent.getTimestamp() );
        });
    }

    @Entity(name = "DateEvent")
    public static class DateEvent {
        @Id
        @GeneratedValue
        private Long id;

        @Temporal(TemporalType.TIMESTAMP)
        @Column(name = "`timestamp`")
        private Calendar timestamp;

        public DateEvent() {
        }

        public DateEvent(Calendar timestamp) {
            this.timestamp = timestamp;
        }

        public Long getId() {
            return id;
        }

        public Calendar getTimestamp() {
            return timestamp;
        }
    }
}

