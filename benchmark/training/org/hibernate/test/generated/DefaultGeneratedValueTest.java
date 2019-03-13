/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.generated;


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
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.Session;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GeneratorType;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.SybaseDialect;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.tuple.ValueGenerator;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for the generation of column values using different
 * {@link org.hibernate.tuple.ValueGeneration} implementations.
 *
 * @author Steve Ebersole
 * @author Gunnar Morling
 */
@SkipForDialect(value = SybaseDialect.class, comment = "CURRENT_TIMESTAMP not supported as default value in Sybase")
@SkipForDialect(value = MySQLDialect.class, comment = "See HHH-10196", strictMatching = false)
public class DefaultGeneratedValueTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-2907")
    public void testGeneration() {
        Session s = openSession();
        s.beginTransaction();
        DefaultGeneratedValueTest.TheEntity theEntity = new DefaultGeneratedValueTest.TheEntity(1);
        Assert.assertNull(theEntity.createdDate);
        Assert.assertNull(theEntity.alwaysDate);
        Assert.assertNull(theEntity.vmCreatedDate);
        Assert.assertNull(theEntity.vmCreatedSqlDate);
        Assert.assertNull(theEntity.vmCreatedSqlTime);
        Assert.assertNull(theEntity.vmCreatedSqlTimestamp);
        Assert.assertNull(theEntity.vmCreatedSqlLocalDate);
        Assert.assertNull(theEntity.vmCreatedSqlLocalTime);
        Assert.assertNull(theEntity.vmCreatedSqlLocalDateTime);
        Assert.assertNull(theEntity.vmCreatedSqlMonthDay);
        Assert.assertNull(theEntity.vmCreatedSqlOffsetDateTime);
        Assert.assertNull(theEntity.vmCreatedSqlOffsetTime);
        Assert.assertNull(theEntity.vmCreatedSqlYear);
        Assert.assertNull(theEntity.vmCreatedSqlYearMonth);
        Assert.assertNull(theEntity.vmCreatedSqlZonedDateTime);
        Assert.assertNull(theEntity.name);
        s.save(theEntity);
        // TODO: Actually the values should be non-null after save
        Assert.assertNull(theEntity.createdDate);
        Assert.assertNull(theEntity.alwaysDate);
        Assert.assertNull(theEntity.vmCreatedDate);
        Assert.assertNull(theEntity.vmCreatedSqlDate);
        Assert.assertNull(theEntity.vmCreatedSqlTime);
        Assert.assertNull(theEntity.vmCreatedSqlTimestamp);
        Assert.assertNull(theEntity.vmCreatedSqlLocalDate);
        Assert.assertNull(theEntity.vmCreatedSqlLocalTime);
        Assert.assertNull(theEntity.vmCreatedSqlLocalDateTime);
        Assert.assertNull(theEntity.vmCreatedSqlMonthDay);
        Assert.assertNull(theEntity.vmCreatedSqlOffsetDateTime);
        Assert.assertNull(theEntity.vmCreatedSqlOffsetTime);
        Assert.assertNull(theEntity.vmCreatedSqlYear);
        Assert.assertNull(theEntity.vmCreatedSqlYearMonth);
        Assert.assertNull(theEntity.vmCreatedSqlZonedDateTime);
        Assert.assertNull(theEntity.name);
        s.getTransaction().commit();
        s.close();
        Assert.assertNotNull(theEntity.createdDate);
        Assert.assertNotNull(theEntity.alwaysDate);
        Assert.assertEquals("Bob", theEntity.name);
        s = openSession();
        s.beginTransaction();
        theEntity = ((DefaultGeneratedValueTest.TheEntity) (s.get(DefaultGeneratedValueTest.TheEntity.class, 1)));
        Assert.assertNotNull(theEntity.createdDate);
        Assert.assertNotNull(theEntity.alwaysDate);
        Assert.assertNotNull(theEntity.vmCreatedDate);
        Assert.assertNotNull(theEntity.vmCreatedSqlDate);
        Assert.assertNotNull(theEntity.vmCreatedSqlTime);
        Assert.assertNotNull(theEntity.vmCreatedSqlTimestamp);
        Assert.assertNotNull(theEntity.vmCreatedSqlLocalDate);
        Assert.assertNotNull(theEntity.vmCreatedSqlLocalTime);
        Assert.assertNotNull(theEntity.vmCreatedSqlLocalDateTime);
        Assert.assertNotNull(theEntity.vmCreatedSqlMonthDay);
        Assert.assertNotNull(theEntity.vmCreatedSqlOffsetDateTime);
        Assert.assertNotNull(theEntity.vmCreatedSqlOffsetTime);
        Assert.assertNotNull(theEntity.vmCreatedSqlYear);
        Assert.assertNotNull(theEntity.vmCreatedSqlYearMonth);
        Assert.assertNotNull(theEntity.vmCreatedSqlZonedDateTime);
        Assert.assertEquals("Bob", theEntity.name);
        theEntity.lastName = "Smith";
        s.delete(theEntity);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-2907")
    public void testUpdateTimestampGeneration() {
        Session s = openSession();
        s.beginTransaction();
        DefaultGeneratedValueTest.TheEntity theEntity = new DefaultGeneratedValueTest.TheEntity(1);
        Assert.assertNull(theEntity.updated);
        s.save(theEntity);
        // TODO: Actually the value should be non-null after save
        Assert.assertNull(theEntity.updated);
        s.getTransaction().commit();
        s.close();
        Timestamp created = theEntity.vmCreatedSqlTimestamp;
        Timestamp updated = theEntity.updated;
        Assert.assertNotNull(updated);
        Assert.assertNotNull(created);
        s = openSession();
        s.beginTransaction();
        theEntity = ((DefaultGeneratedValueTest.TheEntity) (s.get(DefaultGeneratedValueTest.TheEntity.class, 1)));
        theEntity.lastName = "Smith";
        try {
            Thread.sleep(1);
        } catch (InterruptedException ignore) {
        }
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        theEntity = ((DefaultGeneratedValueTest.TheEntity) (s.get(DefaultGeneratedValueTest.TheEntity.class, 1)));
        Assert.assertEquals("Creation timestamp should not change on update", created, theEntity.vmCreatedSqlTimestamp);
        Assert.assertTrue("Update timestamp should have changed due to update", theEntity.updated.after(updated));
        s.delete(theEntity);
        s.getTransaction().commit();
        s.close();
    }

    @Entity(name = "TheEntity")
    @Table(name = "T_ENT_GEN_DEF")
    private static class TheEntity {
        @Id
        private Integer id;

        @Generated(GenerationTime.INSERT)
        @ColumnDefault("CURRENT_TIMESTAMP")
        @Column(nullable = false)
        private Date createdDate;

        @Generated(GenerationTime.ALWAYS)
        @ColumnDefault("CURRENT_TIMESTAMP")
        @Column(nullable = false)
        private Calendar alwaysDate;

        @CreationTimestamp
        private Date vmCreatedDate;

        @CreationTimestamp
        private Calendar vmCreatedCalendar;

        @CreationTimestamp
        private java.sql.Date vmCreatedSqlDate;

        @CreationTimestamp
        private Time vmCreatedSqlTime;

        @CreationTimestamp
        private Timestamp vmCreatedSqlTimestamp;

        @CreationTimestamp
        private Instant vmCreatedSqlInstant;

        @CreationTimestamp
        private LocalDate vmCreatedSqlLocalDate;

        @CreationTimestamp
        private LocalTime vmCreatedSqlLocalTime;

        @CreationTimestamp
        private LocalDateTime vmCreatedSqlLocalDateTime;

        @CreationTimestamp
        private MonthDay vmCreatedSqlMonthDay;

        @CreationTimestamp
        private OffsetDateTime vmCreatedSqlOffsetDateTime;

        @CreationTimestamp
        private OffsetTime vmCreatedSqlOffsetTime;

        @CreationTimestamp
        private Year vmCreatedSqlYear;

        @CreationTimestamp
        private YearMonth vmCreatedSqlYearMonth;

        @CreationTimestamp
        private ZonedDateTime vmCreatedSqlZonedDateTime;

        @UpdateTimestamp
        private Timestamp updated;

        @GeneratorType(type = DefaultGeneratedValueTest.MyVmValueGenerator.class, when = GenerationTime.INSERT)
        private String name;

        @SuppressWarnings("unused")
        private String lastName;

        private TheEntity() {
        }

        private TheEntity(Integer id) {
            this.id = id;
        }
    }

    public static class MyVmValueGenerator implements ValueGenerator<String> {
        @Override
        public String generateValue(Session session, Object owner) {
            return "Bob";
        }
    }
}

