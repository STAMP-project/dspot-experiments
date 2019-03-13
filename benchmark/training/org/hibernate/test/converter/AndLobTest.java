/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.converter;


import java.sql.Types;
import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.Converter;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.hibernate.type.Type;
import org.hibernate.type.descriptor.converter.AttributeConverterTypeAdapter;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test mapping a model with an attribute combining {@code @Lob} with an AttributeConverter.
 * <p/>
 * Originally developed to diagnose HHH-9615
 *
 * @author Steve Ebersole
 */
public class AndLobTest extends BaseUnitTestCase {
    private StandardServiceRegistry ssr;

    @Test
    public void testMappingAttributeWithLobAndAttributeConverter() {
        final Metadata metadata = addAnnotatedClass(AndLobTest.EntityImpl.class).buildMetadata();
        final Type type = metadata.getEntityBinding(AndLobTest.EntityImpl.class.getName()).getProperty("status").getType();
        final AttributeConverterTypeAdapter concreteType = ExtraAssertions.assertTyping(AttributeConverterTypeAdapter.class, type);
        Assert.assertEquals(Types.BLOB, concreteType.getSqlTypeDescriptor().getSqlType());
    }

    @Converter
    public static class ConverterImpl implements AttributeConverter<String, Integer> {
        @Override
        public Integer convertToDatabaseColumn(String attribute) {
            return attribute.length();
        }

        @Override
        public String convertToEntityAttribute(Integer dbData) {
            return "";
        }
    }

    @Entity
    public static class EntityImpl {
        @Id
        private Integer id;

        @Lob
        @Convert(converter = AndLobTest.ConverterImpl.class)
        private String status;
    }
}

