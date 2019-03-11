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
import org.hibernate.boot.Metadata;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.hibernate.type.descriptor.converter.AttributeConverterTypeAdapter;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test simple application of Convert annotation via XML.
 *
 * @author Steve Ebersole
 */
public class SimpleXmlOverriddenTest extends BaseUnitTestCase {
    private StandardServiceRegistry ssr;

    /**
     * A baseline test, with an explicit @Convert annotation that should be in effect
     */
    @Test
    public void baseline() {
        Metadata metadata = addAnnotatedClass(SimpleXmlOverriddenTest.TheEntity.class).buildMetadata();
        PersistentClass pc = metadata.getEntityBinding(SimpleXmlOverriddenTest.TheEntity.class.getName());
        Type type = pc.getProperty("it").getType();
        AttributeConverterTypeAdapter adapter = ExtraAssertions.assertTyping(AttributeConverterTypeAdapter.class, type);
        Assert.assertTrue(SillyStringConverter.class.isAssignableFrom(adapter.getAttributeConverter().getConverterJavaTypeDescriptor().getJavaType()));
    }

    /**
     * Test outcome of applying overrides via orm.xml, specifically at the attribute level
     */
    @Test
    public void testDefinitionAtAttributeLevel() {
        // NOTE : simple-override.xml applied disable-conversion="true" at the attribute-level
        Metadata metadata = addAnnotatedClass(SimpleXmlOverriddenTest.TheEntity.class).addResource("org/hibernate/test/converter/simple-override.xml").buildMetadata();
        PersistentClass pc = metadata.getEntityBinding(SimpleXmlOverriddenTest.TheEntity.class.getName());
        Type type = pc.getProperty("it").getType();
        ExtraAssertions.assertTyping(StringType.class, type);
    }

    /**
     * Test outcome of applying overrides via orm.xml, specifically at the entity level
     */
    @Test
    public void testDefinitionAtEntityLevel() {
        // NOTE : simple-override2.xml applied disable-conversion="true" at the entity-level
        Metadata metadata = addAnnotatedClass(SimpleXmlOverriddenTest.TheEntity2.class).addResource("org/hibernate/test/converter/simple-override2.xml").buildMetadata();
        PersistentClass pc = metadata.getEntityBinding(SimpleXmlOverriddenTest.TheEntity2.class.getName());
        Type type = pc.getProperty("it").getType();
        ExtraAssertions.assertTyping(StringType.class, type);
    }

    @Entity(name = "TheEntity")
    public static class TheEntity {
        @Id
        public Integer id;

        @Convert(converter = SillyStringConverter.class)
        public String it;
    }

    @Entity(name = "TheEntity2")
    @Convert(attributeName = "it", converter = SillyStringConverter.class)
    public static class TheEntity2 {
        @Id
        public Integer id;

        public String it;
    }
}

