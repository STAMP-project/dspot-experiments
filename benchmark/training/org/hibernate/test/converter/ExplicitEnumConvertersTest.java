/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.converter;


import java.net.MalformedURLException;
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
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
@TestForIssue(jiraKey = "HHH-8809")
public class ExplicitEnumConvertersTest extends BaseNonConfigCoreFunctionalTestCase {
    // NOTE : initially unable to reproduce the reported problem
    public static enum MediaType {

        MUSIC,
        VIDEO,
        PHOTO,
        MUSIC_STREAM,
        VIDEO_STREAM;}

    static boolean convertToDatabaseColumnCalled = false;

    static boolean convertToEntityAttributeCalled = false;

    public static class MediaTypeConverter implements AttributeConverter<ExplicitEnumConvertersTest.MediaType, String> {
        @Override
        public String convertToDatabaseColumn(ExplicitEnumConvertersTest.MediaType attribute) {
            ExplicitEnumConvertersTest.convertToDatabaseColumnCalled = true;
            return attribute.name();
        }

        @Override
        public ExplicitEnumConvertersTest.MediaType convertToEntityAttribute(String dbData) {
            ExplicitEnumConvertersTest.convertToEntityAttributeCalled = true;
            return ExplicitEnumConvertersTest.MediaType.valueOf(dbData);
        }
    }

    @Entity(name = "Entity1")
    public static class Entity1 {
        @Id
        private Integer id;

        private String name;

        @Convert(converter = ExplicitEnumConvertersTest.MediaTypeConverter.class)
        private ExplicitEnumConvertersTest.MediaType mediaType;

        public Entity1() {
        }

        public Entity1(Integer id, String name, ExplicitEnumConvertersTest.MediaType mediaType) {
            this.id = id;
            this.name = name;
            this.mediaType = mediaType;
        }
    }

    @Test
    public void testSimpleConvertUsage() throws MalformedURLException {
        final EntityPersister ep = sessionFactory().getEntityPersister(ExplicitEnumConvertersTest.Entity1.class.getName());
        final Type theDatePropertyType = ep.getPropertyType("mediaType");
        final AttributeConverterTypeAdapter type = ExtraAssertions.assertTyping(AttributeConverterTypeAdapter.class, theDatePropertyType);
        Assert.assertTrue(ExplicitEnumConvertersTest.MediaTypeConverter.class.isAssignableFrom(type.getAttributeConverter().getConverterJavaTypeDescriptor().getJavaType()));
        resetFlags();
        Session session = openSession();
        session.getTransaction().begin();
        session.persist(new ExplicitEnumConvertersTest.Entity1(1, "300", ExplicitEnumConvertersTest.MediaType.VIDEO));
        session.getTransaction().commit();
        session.close();
        Assert.assertTrue(ExplicitEnumConvertersTest.convertToDatabaseColumnCalled);
        resetFlags();
        session = openSession();
        session.getTransaction().begin();
        session.get(ExplicitEnumConvertersTest.Entity1.class, 1);
        session.getTransaction().commit();
        session.close();
        Assert.assertTrue(ExplicitEnumConvertersTest.convertToEntityAttributeCalled);
        session = openSession();
        session.getTransaction().begin();
        session.createQuery("delete Entity1").executeUpdate();
        session.getTransaction().commit();
        session.close();
    }
}

