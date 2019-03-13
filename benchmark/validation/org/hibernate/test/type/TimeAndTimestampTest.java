/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.type;


import java.sql.Time;
import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.dialect.AbstractHANADialect;
import org.hibernate.dialect.MariaDBDialect;
import org.hibernate.dialect.MySQL8Dialect;
import org.hibernate.dialect.Oracle9iDialect;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-10465")
@SkipForDialect(MariaDBDialect.class)
@SkipForDialect(MySQL8Dialect.class)
@SkipForDialect(value = Oracle9iDialect.class, comment = "Oracle date does not support milliseconds  ")
@SkipForDialect(value = AbstractHANADialect.class, comment = "HANA date does not support milliseconds  ")
public class TimeAndTimestampTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void test() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.type.Event event = new org.hibernate.test.type.Event();
            event.id = 1L;
            event.timeValue = new Time(1000);
            event.timestampValue = new Timestamp(45678);
            session.persist(event);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.type.Event event = session.find(.class, 1L);
            assertEquals(1000, ((event.timeValue.getTime()) % (TimeUnit.DAYS.toMillis(1))));
            assertEquals(45678, ((event.timestampValue.getTime()) % (TimeUnit.DAYS.toMillis(1))));
        });
    }

    @Entity(name = "Event")
    public static class Event {
        @Id
        private Long id;

        private Time timeValue;

        private Timestamp timestampValue;
    }
}

