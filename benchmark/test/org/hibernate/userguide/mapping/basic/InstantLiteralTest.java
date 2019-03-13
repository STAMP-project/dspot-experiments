/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.mapping.basic;


import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@RequiresDialect(H2Dialect.class)
public class InstantLiteralTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        final InstantLiteralTest.DateEvent _dateEvent = doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.mapping.basic.DateEvent dateEvent = new org.hibernate.userguide.mapping.basic.DateEvent();
            dateEvent.setCreatedOn(Instant.from(DateTimeFormatter.ISO_INSTANT.parse("2016-10-13T06:40:18.745Z")));
            entityManager.persist(dateEvent);
            return dateEvent;
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.mapping.basic.DateEvent dateEvent = entityManager.unwrap(.class).createQuery(("select de " + ("from DateEvent de " + "where de.createdOn = '2016-10-13T06:40:18.745Z' ")), .class).getSingleResult();
            assertNotNull(dateEvent);
            assertEquals(_dateEvent.getId(), dateEvent.getId());
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.mapping.basic.DateEvent dateEvent = entityManager.unwrap(.class).createQuery(("select de " + ("from DateEvent de " + "where de.createdOn = :createdOn ")), .class).setParameter("createdOn", Instant.from(DateTimeFormatter.ISO_INSTANT.parse("2016-10-13T06:40:18.745Z"))).getSingleResult();
            assertNotNull(dateEvent);
            assertEquals(_dateEvent.getId(), dateEvent.getId());
        });
    }

    @Entity(name = "DateEvent")
    public static class DateEvent {
        @Id
        @GeneratedValue
        private Long id;

        @Column
        private Instant createdOn;

        public Long getId() {
            return id;
        }

        public Instant getCreatedOn() {
            return createdOn;
        }

        public void setCreatedOn(Instant createdOn) {
            this.createdOn = createdOn;
        }
    }
}

