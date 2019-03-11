/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.converter;


import TemporalType.TIMESTAMP;
import java.io.Serializable;
import java.util.Date;
import java.util.stream.Stream;
import javax.persistence.AttributeConverter;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hamcrest.CoreMatchers;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-10959")
public class LongToDateConversionTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testSetParameter() throws Exception {
        try (Session session = openSession()) {
            final Query<LongToDateConversionTest.TestEntity> query = session.createQuery("SELECT e FROM TestEntity e WHERE e.date <= :ts", LongToDateConversionTest.TestEntity.class).setParameter("ts", new LongToDateConversionTest.DateAttribute(System.currentTimeMillis()), TIMESTAMP);
            final Stream<LongToDateConversionTest.TestEntity> stream = query.stream();
            Assert.assertThat(stream.count(), CoreMatchers.is(1L));
        }
    }

    @Entity(name = "TestEntity")
    @Table(name = "TEST_ENTITY")
    public static class TestEntity {
        @Id
        @GeneratedValue
        private long id;

        @Convert(converter = LongToDateConversionTest.DateAttributeConverter.class)
        @Column(name = "attribute_date")
        private LongToDateConversionTest.DateAttribute date;

        public LongToDateConversionTest.DateAttribute getDate() {
            return date;
        }

        public void setDate(LongToDateConversionTest.DateAttribute date) {
            this.date = date;
        }
    }

    public static class DateAttribute implements Serializable {
        private long field;

        public DateAttribute(long field) {
            this.field = field;
        }
    }

    public static class DateAttributeConverter implements AttributeConverter<LongToDateConversionTest.DateAttribute, Date> {
        @Override
        public Date convertToDatabaseColumn(LongToDateConversionTest.DateAttribute attribute) {
            if (attribute == null) {
                return null;
            }
            return new Date(attribute.field);
        }

        @Override
        public LongToDateConversionTest.DateAttribute convertToEntityAttribute(Date dbData) {
            if (dbData == null) {
                return null;
            }
            return new LongToDateConversionTest.DateAttribute(dbData.getTime());
        }
    }
}

