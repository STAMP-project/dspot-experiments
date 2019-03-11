/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.type;


import ZonedDateTimeType.INSTANCE;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hamcrest.core.Is;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
public class ZonedDateTimeTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testZoneDateTimeWithHoursZoneOffset() {
        final ZonedDateTime expectedStartDate = ZonedDateTime.of(2015, 1, 1, 0, 0, 0, 0, ZoneOffset.ofHours(5));
        saveZoneDateTimeEventWithStartDate(expectedStartDate);
        checkSavedZonedDateTimeIsEqual(expectedStartDate);
        compareSavedZonedDateTimeWith(expectedStartDate);
    }

    @Test
    public void testZoneDateTimeWithUTCZoneOffset() {
        final ZonedDateTime expectedStartDate = ZonedDateTime.of(1, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        saveZoneDateTimeEventWithStartDate(expectedStartDate);
        checkSavedZonedDateTimeIsEqual(expectedStartDate);
        compareSavedZonedDateTimeWith(expectedStartDate);
    }

    @Test
    public void testRetrievingEntityByZoneDateTime() {
        final ZonedDateTime startDate = ZonedDateTime.of(1, 1, 1, 0, 0, 0, 0, ZoneOffset.ofHours(3));
        saveZoneDateTimeEventWithStartDate(startDate);
        final Session s = openSession();
        try {
            Query query = s.createQuery("from ZonedDateTimeEvent o where o.startDate = :date");
            query.setParameter("date", startDate, INSTANCE);
            List<ZonedDateTimeTest.ZonedDateTimeEvent> list = query.list();
            Assert.assertThat(list.size(), Is.is(1));
        } finally {
            s.close();
        }
    }

    @Entity(name = "ZonedDateTimeEvent")
    @Table(name = "ZONE_DATE_TIME_EVENT")
    public static class ZonedDateTimeEvent {
        @Id
        private Long id;

        @Column(name = "START_DATE")
        private ZonedDateTime startDate;
    }
}

