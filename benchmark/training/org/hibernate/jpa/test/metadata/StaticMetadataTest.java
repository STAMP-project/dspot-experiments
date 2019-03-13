/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.metadata;


import Address_.address1;
import Address_.address2;
import Address_.city;
import Animal_.id;
import Animal_.legNbr;
import Attribute.PersistentAttributeType.ELEMENT_COLLECTION;
import Attribute.PersistentAttributeType.EMBEDDED;
import Bindable.BindableType.PLURAL_ATTRIBUTE;
import Bindable.BindableType.SINGULAR_ATTRIBUTE;
import Cat_.nickname;
import FoodItem_.version;
import Fridge_.brand;
import Fridge_.temperature;
import Garden_.flowers;
import House_.address;
import House_.key;
import House_.rooms;
import House_.roomsByName;
import House_.roomsBySize;
import Person_.firstName;
import Person_.lastName;
import PluralAttribute.CollectionType.LIST;
import PluralAttribute.CollectionType.MAP;
import PluralAttribute.CollectionType.SET;
import Type.PersistenceType.BASIC;
import Type.PersistenceType.EMBEDDABLE;
import java.util.Set;
import javax.persistence.metamodel.EmbeddableType;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class StaticMetadataTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testInjections() throws Exception {
        // Address (embeddable)
        Assert.assertNotNull(address1);
        Assert.assertNotNull(address2);
        Assert.assertNotNull(city);
        final EmbeddableType<Address> addressType = ((EmbeddableType<Address>) (address.getType()));
        Assert.assertEquals(addressType.getDeclaredSingularAttribute("address1"), address1);
        Assert.assertEquals(addressType.getDeclaredSingularAttribute("address2"), address2);
        Assert.assertTrue(address1.isOptional());
        Assert.assertFalse(address2.isOptional());
        // Animal (mapped superclass)
        Assert.assertNotNull(id);
        Assert.assertTrue(id.isId());
        Assert.assertEquals(Long.class, id.getJavaType());
        Assert.assertNotNull(legNbr);
        // assertEquals( Integer.class, Animal_.legNbr.getJavaType() );
        Assert.assertEquals(int.class, legNbr.getJavaType());
        // Cat (hierarchy)
        Assert.assertNotNull(Cat_.id);
        Assert.assertNotNull(Cat_.id.isId());
        Assert.assertEquals(Animal.class, Cat_.id.getJavaMember().getDeclaringClass());
        Assert.assertNotNull(nickname);
        // FoodItem
        Assert.assertNotNull(version);
        Assert.assertTrue(version.isVersion());
        // Fridge
        Assert.assertNotNull(Fridge_.id);
        Assert.assertTrue(Fridge_.id.isId());
        Assert.assertEquals(Long.class, Fridge_.id.getJavaType());
        Assert.assertNotNull(temperature);
        Assert.assertEquals("temperature", temperature.getName());
        Assert.assertEquals(Fridge.class, temperature.getDeclaringType().getJavaType());
        // assertEquals( Integer.class, Fridge_.temperature.getJavaType() );
        // assertEquals( Integer.class, Fridge_.temperature.getBindableJavaType() );
        // assertEquals( Integer.class, Fridge_.temperature.getType().getJavaType() );
        Assert.assertEquals(int.class, temperature.getJavaType());
        Assert.assertEquals(int.class, temperature.getBindableJavaType());
        Assert.assertEquals(int.class, temperature.getType().getJavaType());
        Assert.assertEquals(SINGULAR_ATTRIBUTE, temperature.getBindableType());
        Assert.assertEquals(BASIC, temperature.getType().getPersistenceType());
        Assert.assertEquals(Attribute.PersistentAttributeType.BASIC, temperature.getPersistentAttributeType());
        Assert.assertFalse(temperature.isId());
        Assert.assertFalse(temperature.isOptional());
        Assert.assertFalse(temperature.isAssociation());
        Assert.assertFalse(temperature.isCollection());
        Assert.assertFalse(brand.isOptional());
        // House (embedded id)
        Assert.assertNotNull(key);
        Assert.assertTrue(key.isId());
        Assert.assertEquals(EMBEDDED, key.getPersistentAttributeType());
        Assert.assertNotNull(address);
        Assert.assertEquals(EMBEDDED, address.getPersistentAttributeType());
        Assert.assertFalse(address.isCollection());
        Assert.assertFalse(address.isAssociation());
        Assert.assertNotNull(rooms);
        Assert.assertFalse(rooms.isAssociation());
        Assert.assertTrue(rooms.isCollection());
        Assert.assertEquals(ELEMENT_COLLECTION, rooms.getPersistentAttributeType());
        Assert.assertEquals(Room.class, rooms.getBindableJavaType());
        Assert.assertEquals(PLURAL_ATTRIBUTE, rooms.getBindableType());
        Assert.assertEquals(Set.class, rooms.getJavaType());
        Assert.assertEquals(SET, rooms.getCollectionType());
        Assert.assertEquals(EMBEDDABLE, rooms.getElementType().getPersistenceType());
        Assert.assertNotNull(roomsByName);
        Assert.assertEquals(String.class, roomsByName.getKeyJavaType());
        Assert.assertEquals(BASIC, roomsByName.getKeyType().getPersistenceType());
        Assert.assertEquals(MAP, roomsByName.getCollectionType());
        Assert.assertNotNull(roomsBySize);
        Assert.assertEquals(EMBEDDABLE, roomsBySize.getElementType().getPersistenceType());
        Assert.assertEquals(LIST, roomsBySize.getCollectionType());
        // Person (mapped id)
        Assert.assertNotNull(firstName);
        Assert.assertNotNull(lastName);
        Assert.assertTrue(firstName.isId());
        Assert.assertTrue(lastName.isId());
        Assert.assertTrue(lastName.isId());
        // Garden List as bag
        Assert.assertNotNull(flowers);
    }
}

