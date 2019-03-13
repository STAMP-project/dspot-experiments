/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.timestamp;


import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.dialect.PostgreSQL82Dialect;
import org.hibernate.test.util.jdbc.TimeZoneConnectionProvider;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@RequiresDialect(PostgreSQL82Dialect.class)
public class JdbcTimestampWithoutUTCTimeZoneTest extends BaseNonConfigCoreFunctionalTestCase {
    private TimeZoneConnectionProvider connectionProvider = new TimeZoneConnectionProvider("America/Los_Angeles");

    @Test
    public void testTimeZone() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.timestamp.Person person = new org.hibernate.test.timestamp.Person();
            person.id = 1L;
            long y2kMillis = LocalDateTime.of(2000, 1, 1, 0, 0, 0).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
            assertEquals(946684800000L, y2kMillis);
            person.createdOn = new Timestamp(y2kMillis);
            session.persist(person);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            s.doWork(( connection) -> {
                try (Statement st = connection.createStatement()) {
                    try (ResultSet rs = st.executeQuery(("SELECT to_char(createdon, 'YYYY-MM-DD HH24:MI:SS.US') " + "FROM person"))) {
                        while (rs.next()) {
                            String timestamp = rs.getString(1);
                            assertEquals(expectedTimestampValue(), timestamp);
                        } 
                    }
                }
            });
        });
    }

    @Entity(name = "Person")
    public static class Person {
        @Id
        private Long id;

        private Timestamp createdOn;
    }
}

