/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations;


import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Borys Piela
 */
public class UpdateTimestampTest extends BaseEntityManagerFunctionalTestCase {
    @Entity(name = "Event")
    private static class Event {
        @Id
        @GeneratedValue
        private Long id;

        @Column(name = "`date`")
        @UpdateTimestamp
        private Date date;

        @Column(name = "`calendar`")
        @UpdateTimestamp
        private Calendar calendar;

        @Column(name = "`sqlDate`")
        @UpdateTimestamp
        private java.sql.Date sqlDate;

        @Column(name = "`time`")
        @UpdateTimestamp
        private Time time;

        @Column(name = "`timestamp`")
        @UpdateTimestamp
        private Timestamp timestamp;

        @Column(name = "`instant`")
        @UpdateTimestamp
        private Instant instant;

        @Column(name = "`localDate`")
        @UpdateTimestamp
        private LocalDate localDate;

        @Column(name = "`localDateTime`")
        @UpdateTimestamp
        private LocalDateTime localDateTime;

        @Column(name = "`localTime`")
        @UpdateTimestamp
        private LocalTime localTime;

        @Column(name = "`monthDay`")
        @UpdateTimestamp
        private MonthDay monthDay;

        @Column(name = "`offsetDateTime`")
        @UpdateTimestamp
        private OffsetDateTime offsetDateTime;

        @Column(name = "`offsetTime`")
        @UpdateTimestamp
        private OffsetTime offsetTime;

        @Column(name = "`year`")
        @UpdateTimestamp
        private Year year;

        @Column(name = "`yearMonth`")
        @UpdateTimestamp
        private YearMonth yearMonth;

        @Column(name = "`zonedDateTime`")
        @UpdateTimestamp
        private ZonedDateTime zonedDateTime;

        public Event() {
        }

        public Long getId() {
            return id;
        }

        public Date getDate() {
            return date;
        }

        public Calendar getCalendar() {
            return calendar;
        }

        public java.sql.Date getSqlDate() {
            return sqlDate;
        }

        public Time getTime() {
            return time;
        }

        public Timestamp getTimestamp() {
            return timestamp;
        }

        public Instant getInstant() {
            return instant;
        }

        public LocalDate getLocalDate() {
            return localDate;
        }

        public LocalDateTime getLocalDateTime() {
            return localDateTime;
        }

        public LocalTime getLocalTime() {
            return localTime;
        }

        public MonthDay getMonthDay() {
            return monthDay;
        }

        public OffsetDateTime getOffsetDateTime() {
            return offsetDateTime;
        }

        public OffsetTime getOffsetTime() {
            return offsetTime;
        }

        public Year getYear() {
            return year;
        }

        public YearMonth getYearMonth() {
            return yearMonth;
        }

        public ZonedDateTime getZonedDateTime() {
            return zonedDateTime;
        }
    }

    @Test
    public void generatesCurrentTimestamp() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.annotations.Event event = new org.hibernate.test.annotations.Event();
            entityManager.persist(event);
            entityManager.flush();
            Assert.assertNotNull(event.getDate());
            Assert.assertNotNull(event.getCalendar());
            Assert.assertNotNull(event.getSqlDate());
            Assert.assertNotNull(event.getTime());
            Assert.assertNotNull(event.getTimestamp());
            Assert.assertNotNull(event.getInstant());
            Assert.assertNotNull(event.getLocalDate());
            Assert.assertNotNull(event.getLocalDateTime());
            Assert.assertNotNull(event.getLocalTime());
            Assert.assertNotNull(event.getMonthDay());
            Assert.assertNotNull(event.getOffsetDateTime());
            Assert.assertNotNull(event.getOffsetTime());
            Assert.assertNotNull(event.getYear());
            Assert.assertNotNull(event.getYearMonth());
            Assert.assertNotNull(event.getZonedDateTime());
        });
    }
}

