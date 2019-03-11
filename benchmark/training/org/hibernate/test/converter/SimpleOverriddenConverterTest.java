/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.converter;


import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.junit.Test;


/**
 * Tests MappedSuperclass/Entity overriding of Convert definitions
 *
 * @author Steve Ebersole
 */
public class SimpleOverriddenConverterTest extends BaseNonConfigCoreFunctionalTestCase {
    /**
     * Test outcome of annotations exclusively.
     */
    @Test
    public void testSimpleConvertOverrides() {
        final EntityPersister ep = sessionFactory().getEntityPersister(SimpleOverriddenConverterTest.Sub.class.getName());
        Type type = ep.getPropertyType("it");
        ExtraAssertions.assertTyping(StringType.class, type);
    }

    @MappedSuperclass
    public static class Super {
        @Id
        public Integer id;

        @Convert(converter = SillyStringConverter.class)
        public String it;
    }

    @Entity(name = "Sub")
    @Convert(attributeName = "it", disableConversion = true)
    public static class Sub extends SimpleOverriddenConverterTest.Super {}
}

