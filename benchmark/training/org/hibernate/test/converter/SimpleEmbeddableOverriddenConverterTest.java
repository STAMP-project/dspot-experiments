/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.converter;


import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.hibernate.type.CompositeType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.junit.Test;


/**
 * Tests MappedSuperclass/Entity overriding of Convert definitions
 *
 * @author Steve Ebersole
 */
public class SimpleEmbeddableOverriddenConverterTest extends BaseNonConfigCoreFunctionalTestCase {
    /**
     * Test outcome of annotations exclusively.
     */
    @Test
    public void testSimpleConvertOverrides() {
        final EntityPersister ep = sessionFactory().getEntityPersister(SimpleEmbeddableOverriddenConverterTest.Person.class.getName());
        CompositeType homeAddressType = ExtraAssertions.assertTyping(CompositeType.class, ep.getPropertyType("homeAddress"));
        Type homeAddressCityType = findCompositeAttributeType(homeAddressType, "city");
        ExtraAssertions.assertTyping(StringType.class, homeAddressCityType);
    }

    @Embeddable
    public static class Address {
        public String street;

        @Convert(converter = SillyStringConverter.class)
        public String city;
    }

    @Entity(name = "Person")
    public static class Person {
        @Id
        public Integer id;

        @Embedded
        @Convert(attributeName = "city", disableConversion = true)
        public SimpleEmbeddableOverriddenConverterTest.Address homeAddress;
    }
}

