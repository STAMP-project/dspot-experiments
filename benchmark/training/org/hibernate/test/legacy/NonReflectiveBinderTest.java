/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.legacy;


import java.util.Iterator;
import java.util.Map;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.MetaAttribute;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


public class NonReflectiveBinderTest extends BaseUnitTestCase {
    private StandardServiceRegistry ssr;

    private Metadata metadata;

    @Test
    public void testMetaInheritance() {
        PersistentClass cm = metadata.getEntityBinding("org.hibernate.test.legacy.Wicked");
        Map m = cm.getMetaAttributes();
        Assert.assertNotNull(m);
        Assert.assertNotNull(cm.getMetaAttribute("global"));
        Assert.assertNull(cm.getMetaAttribute("globalnoinherit"));
        MetaAttribute metaAttribute = cm.getMetaAttribute("implements");
        Assert.assertNotNull(metaAttribute);
        Assert.assertEquals("implements", metaAttribute.getName());
        Assert.assertTrue(metaAttribute.isMultiValued());
        Assert.assertEquals(3, metaAttribute.getValues().size());
        Assert.assertEquals("java.lang.Observer", metaAttribute.getValues().get(0));
        Assert.assertEquals("java.lang.Observer", metaAttribute.getValues().get(1));
        Assert.assertEquals("org.foo.BogusVisitor", metaAttribute.getValues().get(2));
        /* Property property = cm.getIdentifierProperty();
        property.getMetaAttribute(null);
         */
        Iterator propertyIterator = cm.getPropertyIterator();
        while (propertyIterator.hasNext()) {
            Property element = ((Property) (propertyIterator.next()));
            System.out.println(element);
            Map ma = element.getMetaAttributes();
            Assert.assertNotNull(ma);
            Assert.assertNotNull(element.getMetaAttribute("global"));
            MetaAttribute metaAttribute2 = element.getMetaAttribute("implements");
            Assert.assertNotNull(metaAttribute2);
            Assert.assertNull(element.getMetaAttribute("globalnoinherit"));
        } 
        Property element = cm.getProperty("component");
        Map ma = element.getMetaAttributes();
        Assert.assertNotNull(ma);
        Assert.assertNotNull(element.getMetaAttribute("global"));
        Assert.assertNotNull(element.getMetaAttribute("componentonly"));
        Assert.assertNotNull(element.getMetaAttribute("allcomponent"));
        Assert.assertNull(element.getMetaAttribute("globalnoinherit"));
        MetaAttribute compimplements = element.getMetaAttribute("implements");
        Assert.assertNotNull(compimplements);
        Assert.assertEquals(compimplements.getValue(), "AnotherInterface");
        Property xp = getProperty("x");
        MetaAttribute propximplements = xp.getMetaAttribute("implements");
        Assert.assertNotNull(propximplements);
        Assert.assertEquals(propximplements.getValue(), "AnotherInterface");
    }

    @Test
    @TestForIssue(jiraKey = "HBX-718")
    public void testNonMutatedInheritance() {
        PersistentClass cm = metadata.getEntityBinding("org.hibernate.test.legacy.Wicked");
        MetaAttribute metaAttribute = cm.getMetaAttribute("globalmutated");
        Assert.assertNotNull(metaAttribute);
        /* assertEquals( metaAttribute.getValues().size(), 2 );		
        assertEquals( "top level", metaAttribute.getValues().get(0) );
         */
        Assert.assertEquals("wicked level", metaAttribute.getValue());
        Property property = cm.getProperty("component");
        MetaAttribute propertyAttribute = property.getMetaAttribute("globalmutated");
        Assert.assertNotNull(propertyAttribute);
        /* assertEquals( propertyAttribute.getValues().size(), 3 );
        assertEquals( "top level", propertyAttribute.getValues().get(0) );
        assertEquals( "wicked level", propertyAttribute.getValues().get(1) );
         */
        Assert.assertEquals("monetaryamount level", propertyAttribute.getValue());
        Component component = ((Component) (property.getValue()));
        property = component.getProperty("x");
        propertyAttribute = property.getMetaAttribute("globalmutated");
        Assert.assertNotNull(propertyAttribute);
        /* assertEquals( propertyAttribute.getValues().size(), 4 );
        assertEquals( "top level", propertyAttribute.getValues().get(0) );
        assertEquals( "wicked level", propertyAttribute.getValues().get(1) );
        assertEquals( "monetaryamount level", propertyAttribute.getValues().get(2) );
         */
        Assert.assertEquals("monetaryamount x level", propertyAttribute.getValue());
        property = cm.getProperty("sortedEmployee");
        propertyAttribute = property.getMetaAttribute("globalmutated");
        Assert.assertNotNull(propertyAttribute);
        /* assertEquals( propertyAttribute.getValues().size(), 3 );
        assertEquals( "top level", propertyAttribute.getValues().get(0) );
        assertEquals( "wicked level", propertyAttribute.getValues().get(1) );
         */
        Assert.assertEquals("sortedemployee level", propertyAttribute.getValue());
        property = cm.getProperty("anotherSet");
        propertyAttribute = property.getMetaAttribute("globalmutated");
        Assert.assertNotNull(propertyAttribute);
        /* assertEquals( propertyAttribute.getValues().size(), 2 );
        assertEquals( "top level", propertyAttribute.getValues().get(0) );
         */
        Assert.assertEquals("wicked level", propertyAttribute.getValue());
        Bag bag = ((Bag) (property.getValue()));
        component = ((Component) (bag.getElement()));
        Assert.assertEquals(4, component.getMetaAttributes().size());
        metaAttribute = component.getMetaAttribute("globalmutated");
        /* assertEquals( metaAttribute.getValues().size(), 3 );
        assertEquals( "top level", metaAttribute.getValues().get(0) );
        assertEquals( "wicked level", metaAttribute.getValues().get(1) );
         */
        Assert.assertEquals("monetaryamount anotherSet composite level", metaAttribute.getValue());
        property = component.getProperty("emp");
        propertyAttribute = property.getMetaAttribute("globalmutated");
        Assert.assertNotNull(propertyAttribute);
        /* assertEquals( propertyAttribute.getValues().size(), 4 );
        assertEquals( "top level", propertyAttribute.getValues().get(0) );
        assertEquals( "wicked level", propertyAttribute.getValues().get(1) );
        assertEquals( "monetaryamount anotherSet composite level", propertyAttribute.getValues().get(2) );
         */
        Assert.assertEquals("monetaryamount anotherSet composite property emp level", propertyAttribute.getValue());
        property = component.getProperty("empinone");
        propertyAttribute = property.getMetaAttribute("globalmutated");
        Assert.assertNotNull(propertyAttribute);
        /* assertEquals( propertyAttribute.getValues().size(), 4 );
        assertEquals( "top level", propertyAttribute.getValues().get(0) );
        assertEquals( "wicked level", propertyAttribute.getValues().get(1) );
        assertEquals( "monetaryamount anotherSet composite level", propertyAttribute.getValues().get(2) );
         */
        Assert.assertEquals("monetaryamount anotherSet composite property empinone level", propertyAttribute.getValue());
    }

    @Test
    public void testComparator() {
        PersistentClass cm = metadata.getEntityBinding("org.hibernate.test.legacy.Wicked");
        Property property = cm.getProperty("sortedEmployee");
        Collection col = ((Collection) (property.getValue()));
        Assert.assertEquals(col.getComparatorClassName(), "org.hibernate.test.legacy.NonExistingComparator");
    }
}

