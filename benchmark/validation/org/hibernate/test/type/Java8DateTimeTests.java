/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.type;


import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.Session;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.type.SerializableType;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class Java8DateTimeTests extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void basicTests() {
        final PersistentClass entityBinding = metadata().getEntityBinding(Java8DateTimeTests.TheEntity.class.getName());
        final Iterator propertyBindingIterator = entityBinding.getPropertyClosureIterator();
        while (propertyBindingIterator.hasNext()) {
            final Property propertyBinding = ((Property) (propertyBindingIterator.next()));
            Assert.assertFalse(("Found property bound as Serializable : " + (propertyBinding.getName())), ((propertyBinding.getType()) instanceof SerializableType));
        } 
        Java8DateTimeTests.TheEntity theEntity = new Java8DateTimeTests.TheEntity(1);
        Session s = openSession();
        s.beginTransaction();
        s.save(theEntity);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        theEntity = ((Java8DateTimeTests.TheEntity) (s.get(Java8DateTimeTests.TheEntity.class, 1)));
        dump(entityBinding, theEntity);
        Assert.assertNotNull(theEntity);
        s.delete(theEntity);
        s.getTransaction().commit();
        s.close();
    }

    @Entity(name = "TheEntity")
    @Table(name = "the_entity")
    public static class TheEntity {
        @Id
        private Integer id;

        @Column(name = "local_date_time")
        private LocalDateTime localDateTime = LocalDateTime.now();

        @Column(name = "local_date")
        private LocalDate localDate = LocalDate.now();

        @Column(name = "local_time")
        private LocalTime localTime = LocalTime.now();

        @Column(name = "instant_value")
        private Instant instant = Instant.now();

        @Column(name = "zoned_date_time")
        private ZonedDateTime zonedDateTime = ZonedDateTime.now();

        @Column(name = "offset_date_time")
        private OffsetDateTime offsetDateTime = OffsetDateTime.now();

        @Column(name = "offset_time")
        private OffsetTime offsetTime = OffsetTime.now();

        @Column(name = "duration_value")
        private Duration duration = Duration.of(20, ChronoUnit.DAYS);

        public TheEntity() {
        }

        public TheEntity(Integer id) {
            this.id = id;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public LocalDateTime getLocalDateTime() {
            return localDateTime;
        }

        public void setLocalDateTime(LocalDateTime localDateTime) {
            this.localDateTime = localDateTime;
        }

        public LocalDate getLocalDate() {
            return localDate;
        }

        public void setLocalDate(LocalDate localDate) {
            this.localDate = localDate;
        }

        public LocalTime getLocalTime() {
            return localTime;
        }

        public void setLocalTime(LocalTime localTime) {
            this.localTime = localTime;
        }

        public Instant getInstant() {
            return instant;
        }

        public void setInstant(Instant instant) {
            this.instant = instant;
        }

        public ZonedDateTime getZonedDateTime() {
            return zonedDateTime;
        }

        public void setZonedDateTime(ZonedDateTime zonedDateTime) {
            this.zonedDateTime = zonedDateTime;
        }

        public OffsetDateTime getOffsetDateTime() {
            return offsetDateTime;
        }

        public void setOffsetDateTime(OffsetDateTime offsetDateTime) {
            this.offsetDateTime = offsetDateTime;
        }

        public OffsetTime getOffsetTime() {
            return offsetTime;
        }

        public void setOffsetTime(OffsetTime offsetTime) {
            this.offsetTime = offsetTime;
        }

        public Duration getDuration() {
            return duration;
        }

        public void setDuration(Duration duration) {
            this.duration = duration;
        }
    }
}

