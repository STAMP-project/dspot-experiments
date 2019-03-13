/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.namingstrategy.components;


import ImplicitNamingStrategyJpaCompliantImpl.INSTANCE;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class ComponentNamingStrategyTest extends BaseUnitTestCase {
    @Test
    public void testDefaultNamingStrategy() {
        final StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().build();
        try {
            final MetadataSources ms = new MetadataSources(ssr);
            ms.addAnnotatedClass(Container.class).addAnnotatedClass(Item.class);
            final Metadata metadata = ms.getMetadataBuilder().applyImplicitNamingStrategy(INSTANCE).build();
            final PersistentClass pc = metadata.getEntityBinding(Container.class.getName());
            Property p = pc.getProperty("items");
            Bag value = ExtraAssertions.assertTyping(Bag.class, p.getValue());
            SimpleValue elementValue = ExtraAssertions.assertTyping(SimpleValue.class, value.getElement());
            Assert.assertEquals(1, elementValue.getColumnSpan());
            Column column = ExtraAssertions.assertTyping(Column.class, elementValue.getColumnIterator().next());
            Assert.assertEquals(column.getName(), "name");
        } finally {
            StandardServiceRegistryBuilder.destroy(ssr);
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-6005")
    public void testComponentSafeNamingStrategy() {
        final StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().build();
        try {
            final MetadataSources ms = new MetadataSources(ssr);
            ms.addAnnotatedClass(Container.class).addAnnotatedClass(Item.class);
            final Metadata metadata = ms.getMetadataBuilder().applyImplicitNamingStrategy(ImplicitNamingStrategyComponentPathImpl.INSTANCE).build();
            final PersistentClass pc = metadata.getEntityBinding(Container.class.getName());
            Property p = pc.getProperty("items");
            Bag value = ExtraAssertions.assertTyping(Bag.class, p.getValue());
            SimpleValue elementValue = ExtraAssertions.assertTyping(SimpleValue.class, value.getElement());
            Assert.assertEquals(1, elementValue.getColumnSpan());
            Column column = ExtraAssertions.assertTyping(Column.class, elementValue.getColumnIterator().next());
            Assert.assertEquals("items_name", column.getName());
        } finally {
            StandardServiceRegistryBuilder.destroy(ssr);
        }
    }
}

