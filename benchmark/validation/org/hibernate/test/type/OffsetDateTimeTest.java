/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.type;


import OffsetDateTimeType.INSTANCE;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hamcrest.core.Is;
import org.hibernate.Query;
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
@TestForIssue(jiraKey = "HHH-10372")
public class OffsetDateTimeTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testOffsetDateTimeWithHoursZoneOffset() {
        final OffsetDateTime expectedStartDate = OffsetDateTime.of(2015, 1, 1, 0, 0, 0, 0, ZoneOffset.ofHours(5));
        saveOffsetDateTimeEventWithStartDate(expectedStartDate);
        checkSavedOffsetDateTimeIsEqual(expectedStartDate);
        compareSavedOffsetDateTimeWith(expectedStartDate);
    }

    @Test
    public void testOffsetDateTimeWithUTCZoneOffset() {
        final OffsetDateTime expectedStartDate = OffsetDateTime.of(1, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        saveOffsetDateTimeEventWithStartDate(expectedStartDate);
        checkSavedOffsetDateTimeIsEqual(expectedStartDate);
        compareSavedOffsetDateTimeWith(expectedStartDate);
    }

    @Test
    public void testRetrievingEntityByOffsetDateTime() {
        final OffsetDateTime startDate = OffsetDateTime.of(1, 1, 1, 0, 0, 0, 0, ZoneOffset.ofHours(3));
        saveOffsetDateTimeEventWithStartDate(startDate);
        final Session s = openSession();
        try {
            Query query = s.createQuery("from OffsetDateTimeEvent o where o.startDate = :date");
            query.setParameter("date", startDate, INSTANCE);
            List<OffsetDateTimeTest.OffsetDateTimeEvent> list = query.list();
            Assert.assertThat(list.size(), Is.is(1));
        } finally {
            s.close();
        }
    }

    @Entity(name = "OffsetDateTimeEvent")
    @Table(name = "OFFSET_DATE_TIME_EVENT")
    public static class OffsetDateTimeEvent {
        @Id
        private Long id;

        @Column(name = "START_DATE")
        private OffsetDateTime startDate;
    }
}

