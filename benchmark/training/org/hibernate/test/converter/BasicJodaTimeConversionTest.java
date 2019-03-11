/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.converter;


import java.net.MalformedURLException;
import java.util.Date;
import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.Session;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.hibernate.type.Type;
import org.hibernate.type.descriptor.converter.AttributeConverterTypeAdapter;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
@TestForIssue(jiraKey = "HHH-8842")
public class BasicJodaTimeConversionTest extends BaseNonConfigCoreFunctionalTestCase {
    static boolean convertToDatabaseColumnCalled = false;

    static boolean convertToEntityAttributeCalled = false;

    public static class JodaLocalDateConverter implements AttributeConverter<LocalDate, Date> {
        public Date convertToDatabaseColumn(LocalDate localDate) {
            BasicJodaTimeConversionTest.convertToDatabaseColumnCalled = true;
            return localDate.toDate();
        }

        public LocalDate convertToEntityAttribute(Date date) {
            BasicJodaTimeConversionTest.convertToEntityAttributeCalled = true;
            return LocalDate.fromDateFields(date);
        }
    }

    @Entity(name = "TheEntity")
    public static class TheEntity {
        @Id
        public Integer id;

        @Convert(converter = BasicJodaTimeConversionTest.JodaLocalDateConverter.class)
        public LocalDate theDate;

        public TheEntity() {
        }

        public TheEntity(Integer id, LocalDate theDate) {
            this.id = id;
            this.theDate = theDate;
        }
    }

    @Test
    public void testSimpleConvertUsage() throws MalformedURLException {
        final EntityPersister ep = sessionFactory().getEntityPersister(BasicJodaTimeConversionTest.TheEntity.class.getName());
        final Type theDatePropertyType = ep.getPropertyType("theDate");
        final AttributeConverterTypeAdapter type = ExtraAssertions.assertTyping(AttributeConverterTypeAdapter.class, theDatePropertyType);
        Assert.assertTrue(BasicJodaTimeConversionTest.JodaLocalDateConverter.class.isAssignableFrom(type.getAttributeConverter().getConverterJavaTypeDescriptor().getJavaType()));
        resetFlags();
        Session session = openSession();
        session.getTransaction().begin();
        session.persist(new BasicJodaTimeConversionTest.TheEntity(1, new LocalDate()));
        session.getTransaction().commit();
        session.close();
        Assert.assertTrue(BasicJodaTimeConversionTest.convertToDatabaseColumnCalled);
        resetFlags();
        session = openSession();
        session.getTransaction().begin();
        session.get(BasicJodaTimeConversionTest.TheEntity.class, 1);
        session.getTransaction().commit();
        session.close();
        Assert.assertTrue(BasicJodaTimeConversionTest.convertToEntityAttributeCalled);
        resetFlags();
        session = openSession();
        session.getTransaction().begin();
        session.createQuery("delete TheEntity").executeUpdate();
        session.getTransaction().commit();
        session.close();
    }
}

