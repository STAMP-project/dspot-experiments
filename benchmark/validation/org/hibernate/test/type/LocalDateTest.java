/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.type;


import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hamcrest.core.Is;
import org.hibernate.Session;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-10371")
public class LocalDateTest extends BaseNonConfigCoreFunctionalTestCase {
    private static final LocalDate expectedLocalDate = LocalDate.of(1, 1, 1);

    @Test
    public void testLocalDate() {
        final Session s = openSession();
        try {
            final LocalDateTest.LocalDateEvent localDateEvent = s.get(LocalDateTest.LocalDateEvent.class, 1L);
            Assert.assertThat(localDateEvent.getStartDate(), Is.is(LocalDateTest.expectedLocalDate));
        } finally {
            s.close();
        }
    }

    @Entity(name = "LocalDateEvent")
    @Table(name = "LOCAL_DATE_EVENT")
    public static class LocalDateEvent {
        private Long id;

        private LocalDate startDate;

        public LocalDateEvent() {
        }

        public LocalDateEvent(Long id, LocalDate startDate) {
            this.id = id;
            this.startDate = startDate;
        }

        @Id
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        @Column(name = "START_DATE")
        public LocalDate getStartDate() {
            return startDate;
        }

        public void setStartDate(LocalDate startDate) {
            this.startDate = startDate;
        }
    }
}

