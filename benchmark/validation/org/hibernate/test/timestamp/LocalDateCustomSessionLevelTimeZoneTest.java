/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.timestamp;


import AvailableSettings.URL;
import java.time.LocalDate;
import java.util.Map;
import java.util.TimeZone;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.jdbc.ConnectionProviderDelegate;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@RequiresDialect(MySQL5Dialect.class)
public class LocalDateCustomSessionLevelTimeZoneTest extends BaseNonConfigCoreFunctionalTestCase {
    private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("Europe/Berlin");

    private ConnectionProviderDelegate connectionProvider = new ConnectionProviderDelegate() {
        @Override
        public void configure(Map configurationValues) {
            String url = ((String) (configurationValues.get(URL)));
            if (!(url.contains("?"))) {
                url += "?";
            } else
                if (!(url.endsWith("&"))) {
                    url += "&";
                }

            url += "useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Europe/Berlin";
            configurationValues.put(URL, url);
            super.configure(configurationValues);
        }
    };

    @Test
    @TestForIssue(jiraKey = "HHH-11396")
    public void testTimeZone() {
        TimeZone old = TimeZone.getDefault();
        try {
            // The producer (MySQL) Berlin and returns 1980-01-01
            TimeZone jdbcTimeZone = TimeZone.getTimeZone("Europe/Berlin");
            TimeZone.setDefault(jdbcTimeZone);
            // hibernate.connection.url jdbc:mysql://localhost/hibernate_orm_test
            TransactionUtil.doInHibernateSessionBuilder(() -> sessionFactory().withOptions().jdbcTimeZone(TIME_ZONE), ( s) -> {
                org.hibernate.test.timestamp.Person person = new org.hibernate.test.timestamp.Person();
                person.id = 1L;
                s.persist(person);
            });
            TransactionUtil.doInHibernateSessionBuilder(() -> sessionFactory().withOptions().jdbcTimeZone(TIME_ZONE), ( s) -> {
                org.hibernate.test.timestamp.Person person = s.find(.class, 1L);
                assertEquals(LocalDate.of(2017, 3, 7), person.createdOn);
            });
        } finally {
            TimeZone.setDefault(old);
        }
    }

    @Entity(name = "Person")
    public static class Person {
        @Id
        private Long id;

        private LocalDate createdOn = LocalDate.of(2017, 3, 7);
    }
}

