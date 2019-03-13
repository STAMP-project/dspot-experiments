/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.metadata;


import Attribute.PersistentAttributeType.ELEMENT_COLLECTION;
import Attribute.PersistentAttributeType.EMBEDDED;
import Attribute.PersistentAttributeType.MANY_TO_ONE;
import Bindable.BindableType.ENTITY_TYPE;
import Bindable.BindableType.PLURAL_ATTRIBUTE;
import Bindable.BindableType.SINGULAR_ATTRIBUTE;
import JpaMetaModelPopulationSetting.IGNORE_UNSUPPORTED;
import PluralAttribute.CollectionType.LIST;
import PluralAttribute.CollectionType.MAP;
import PluralAttribute.CollectionType.SET;
import Type.PersistenceType.BASIC;
import Type.PersistenceType.EMBEDDABLE;
import Type.PersistenceType.ENTITY;
import Type.PersistenceType.MAPPED_SUPERCLASS;
import java.util.Date;
import java.util.Set;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.MappedSuperclassType;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.metamodel.internal.MetamodelImpl;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class MetadataTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testBaseOfService() throws Exception {
        EntityManagerFactory emf = entityManagerFactory();
        Assert.assertNotNull(emf.getMetamodel());
        final EntityType<Fridge> entityType = emf.getMetamodel().entity(Fridge.class);
        Assert.assertNotNull(entityType);
    }

    @Test
    public void testInvalidAttributeCausesIllegalArgumentException() {
        // should not matter the exact subclass of ManagedType since this is implemented on the base class but
        // check each anyway..
        // entity
        checkNonExistentAttributeAccess(entityManagerFactory().getMetamodel().entity(Fridge.class));
        // embeddable
        checkNonExistentAttributeAccess(entityManagerFactory().getMetamodel().embeddable(Address.class));
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void testBuildingMetamodelWithParameterizedCollection() {
        Metadata metadata = new MetadataSources().addAnnotatedClass(WithGenericCollection.class).buildMetadata();
        SessionFactoryImplementor sfi = ((SessionFactoryImplementor) (metadata.buildSessionFactory()));
        MetamodelImpl metamodel = new MetamodelImpl(sfi, getTypeConfiguration());
        metamodel.initialize(((MetadataImplementor) (metadata)), IGNORE_UNSUPPORTED);
        sfi.close();
    }

    @Test
    public void testLogicalManyToOne() throws Exception {
        final EntityType<JoinedManyToOneOwner> entityType = entityManagerFactory().getMetamodel().entity(JoinedManyToOneOwner.class);
        final SingularAttribute attr = entityType.getDeclaredSingularAttribute("house");
        Assert.assertEquals(MANY_TO_ONE, attr.getPersistentAttributeType());
        Assert.assertEquals(House.class, attr.getBindableJavaType());
        final EntityType<House> houseType = entityManagerFactory().getMetamodel().entity(House.class);
        Assert.assertEquals(houseType.getBindableJavaType(), attr.getBindableJavaType());
    }

    @Test
    public void testEntity() throws Exception {
        final EntityType<Fridge> fridgeType = entityManagerFactory().getMetamodel().entity(Fridge.class);
        Assert.assertEquals(Fridge.class, fridgeType.getBindableJavaType());
        Assert.assertEquals(ENTITY_TYPE, fridgeType.getBindableType());
        SingularAttribute<Fridge, Integer> wrapped = fridgeType.getDeclaredSingularAttribute("temperature", Integer.class);
        Assert.assertNotNull(wrapped);
        SingularAttribute<Fridge, Integer> primitive = fridgeType.getDeclaredSingularAttribute("temperature", int.class);
        Assert.assertNotNull(primitive);
        Assert.assertNotNull(fridgeType.getDeclaredSingularAttribute("temperature"));
        Assert.assertNotNull(fridgeType.getDeclaredAttribute("temperature"));
        final SingularAttribute<Fridge, Long> id = fridgeType.getDeclaredId(Long.class);
        Assert.assertNotNull(id);
        Assert.assertTrue(id.isId());
        try {
            fridgeType.getDeclaredId(Date.class);
            Assert.fail("expecting failure");
        } catch (IllegalArgumentException ignore) {
            // expected result
        }
        final SingularAttribute<? super Fridge, Long> id2 = fridgeType.getId(Long.class);
        Assert.assertNotNull(id2);
        Assert.assertEquals("Fridge", fridgeType.getName());
        Assert.assertEquals(Long.class, fridgeType.getIdType().getJavaType());
        Assert.assertTrue(fridgeType.hasSingleIdAttribute());
        Assert.assertFalse(fridgeType.hasVersionAttribute());
        Assert.assertEquals(ENTITY, fridgeType.getPersistenceType());
        Assert.assertEquals(3, fridgeType.getDeclaredAttributes().size());
        final EntityType<House> houseType = entityManagerFactory().getMetamodel().entity(House.class);
        Assert.assertEquals("House", houseType.getName());
        Assert.assertTrue(houseType.hasSingleIdAttribute());
        final SingularAttribute<House, House.Key> houseId = houseType.getDeclaredId(House.Key.class);
        Assert.assertNotNull(houseId);
        Assert.assertTrue(houseId.isId());
        Assert.assertEquals(EMBEDDED, houseId.getPersistentAttributeType());
        final EntityType<Person> personType = entityManagerFactory().getMetamodel().entity(Person.class);
        Assert.assertEquals("Homo", personType.getName());
        Assert.assertFalse(personType.hasSingleIdAttribute());
        final Set<SingularAttribute<? super Person, ?>> ids = personType.getIdClassAttributes();
        Assert.assertNotNull(ids);
        Assert.assertEquals(2, ids.size());
        for (SingularAttribute<? super Person, ?> localId : ids) {
            Assert.assertTrue(localId.isId());
            Assert.assertSame(personType, localId.getDeclaringType());
            Assert.assertSame(localId, personType.getDeclaredAttribute(localId.getName()));
            Assert.assertSame(localId, personType.getDeclaredSingularAttribute(localId.getName()));
            Assert.assertSame(localId, personType.getAttribute(localId.getName()));
            Assert.assertSame(localId, personType.getSingularAttribute(localId.getName()));
            Assert.assertTrue(personType.getAttributes().contains(localId));
        }
        final EntityType<Giant> giantType = entityManagerFactory().getMetamodel().entity(Giant.class);
        Assert.assertEquals("HomoGigantus", giantType.getName());
        Assert.assertFalse(giantType.hasSingleIdAttribute());
        final Set<SingularAttribute<? super Giant, ?>> giantIds = giantType.getIdClassAttributes();
        Assert.assertNotNull(giantIds);
        Assert.assertEquals(2, giantIds.size());
        Assert.assertEquals(personType.getIdClassAttributes(), giantIds);
        for (SingularAttribute<? super Giant, ?> localGiantId : giantIds) {
            Assert.assertTrue(localGiantId.isId());
            try {
                giantType.getDeclaredAttribute(localGiantId.getName());
                Assert.fail(((localGiantId.getName()) + " is a declared attribute, but shouldn't be"));
            } catch (IllegalArgumentException ex) {
                // expected
            }
            try {
                giantType.getDeclaredSingularAttribute(localGiantId.getName());
                Assert.fail(((localGiantId.getName()) + " is a declared singular attribute, but shouldn't be"));
            } catch (IllegalArgumentException ex) {
                // expected
            }
            Assert.assertSame(localGiantId, giantType.getAttribute(localGiantId.getName()));
            Assert.assertTrue(giantType.getAttributes().contains(localGiantId));
        }
        final EntityType<FoodItem> foodType = entityManagerFactory().getMetamodel().entity(FoodItem.class);
        Assert.assertTrue(foodType.hasVersionAttribute());
        final SingularAttribute<? super FoodItem, Long> version = foodType.getVersion(Long.class);
        Assert.assertNotNull(version);
        Assert.assertTrue(version.isVersion());
        Assert.assertEquals(3, foodType.getDeclaredAttributes().size());
    }

    @Test
    public void testBasic() throws Exception {
        final EntityType<Fridge> entityType = entityManagerFactory().getMetamodel().entity(Fridge.class);
        final SingularAttribute<? super Fridge, Integer> singularAttribute = entityType.getDeclaredSingularAttribute("temperature", Integer.class);
        // assertEquals( Integer.class, singularAttribute.getBindableJavaType() );
        // assertEquals( Integer.class, singularAttribute.getType().getJavaType() );
        Assert.assertEquals(int.class, singularAttribute.getBindableJavaType());
        Assert.assertEquals(int.class, singularAttribute.getType().getJavaType());
        Assert.assertEquals(SINGULAR_ATTRIBUTE, singularAttribute.getBindableType());
        Assert.assertFalse(singularAttribute.isId());
        Assert.assertFalse(singularAttribute.isOptional());
        Assert.assertFalse(entityType.getDeclaredSingularAttribute("brand", String.class).isOptional());
        Assert.assertEquals(BASIC, singularAttribute.getType().getPersistenceType());
        final Attribute<? super Fridge, ?> attribute = entityType.getDeclaredAttribute("temperature");
        Assert.assertNotNull(attribute);
        Assert.assertEquals("temperature", attribute.getName());
        Assert.assertEquals(Fridge.class, attribute.getDeclaringType().getJavaType());
        Assert.assertEquals(Attribute.PersistentAttributeType.BASIC, attribute.getPersistentAttributeType());
        // assertEquals( Integer.class, attribute.getJavaType() );
        Assert.assertEquals(int.class, attribute.getJavaType());
        Assert.assertFalse(attribute.isAssociation());
        Assert.assertFalse(attribute.isCollection());
        boolean found = false;
        for (Attribute<Fridge, ?> attr : entityType.getDeclaredAttributes()) {
            if ("temperature".equals(attr.getName())) {
                found = true;
                break;
            }
        }
        Assert.assertTrue(found);
    }

    @Test
    public void testEmbeddable() throws Exception {
        final EntityType<House> entityType = entityManagerFactory().getMetamodel().entity(House.class);
        final SingularAttribute<? super House, Address> address = entityType.getDeclaredSingularAttribute("address", Address.class);
        Assert.assertNotNull(address);
        Assert.assertEquals(EMBEDDED, address.getPersistentAttributeType());
        Assert.assertFalse(address.isCollection());
        Assert.assertFalse(address.isAssociation());
        final EmbeddableType<Address> addressType = ((EmbeddableType<Address>) (address.getType()));
        Assert.assertEquals(EMBEDDABLE, addressType.getPersistenceType());
        Assert.assertEquals(3, addressType.getDeclaredAttributes().size());
        Assert.assertTrue(addressType.getDeclaredSingularAttribute("address1").isOptional());
        Assert.assertFalse(addressType.getDeclaredSingularAttribute("address2").isOptional());
        final EmbeddableType<Address> directType = entityManagerFactory().getMetamodel().embeddable(Address.class);
        Assert.assertNotNull(directType);
        Assert.assertEquals(EMBEDDABLE, directType.getPersistenceType());
    }

    @Test
    public void testCollection() throws Exception {
        final EntityType<Garden> entiytype = entityManagerFactory().getMetamodel().entity(Garden.class);
        final Set<PluralAttribute<? super Garden, ?, ?>> attributes = entiytype.getPluralAttributes();
        Assert.assertEquals(1, attributes.size());
        PluralAttribute<? super Garden, ?, ?> flowers = attributes.iterator().next();
        Assert.assertTrue((flowers instanceof ListAttribute));
    }

    @Test
    public void testElementCollection() throws Exception {
        final EntityType<House> entityType = entityManagerFactory().getMetamodel().entity(House.class);
        final SetAttribute<House, Room> rooms = entityType.getDeclaredSet("rooms", Room.class);
        Assert.assertNotNull(rooms);
        Assert.assertFalse(rooms.isAssociation());
        Assert.assertTrue(rooms.isCollection());
        Assert.assertEquals(ELEMENT_COLLECTION, rooms.getPersistentAttributeType());
        Assert.assertEquals(Room.class, rooms.getBindableJavaType());
        Assert.assertEquals(PLURAL_ATTRIBUTE, rooms.getBindableType());
        Assert.assertEquals(Set.class, rooms.getJavaType());
        Assert.assertEquals(SET, rooms.getCollectionType());
        Assert.assertEquals(3, entityType.getDeclaredPluralAttributes().size());
        Assert.assertEquals(EMBEDDABLE, rooms.getElementType().getPersistenceType());
        final MapAttribute<House, String, Room> roomsByName = entityType.getDeclaredMap("roomsByName", String.class, Room.class);
        Assert.assertNotNull(roomsByName);
        Assert.assertEquals(String.class, roomsByName.getKeyJavaType());
        Assert.assertEquals(BASIC, roomsByName.getKeyType().getPersistenceType());
        Assert.assertEquals(MAP, roomsByName.getCollectionType());
        final ListAttribute<House, Room> roomsBySize = entityType.getDeclaredList("roomsBySize", Room.class);
        Assert.assertNotNull(roomsBySize);
        Assert.assertEquals(EMBEDDABLE, roomsBySize.getElementType().getPersistenceType());
        Assert.assertEquals(LIST, roomsBySize.getCollectionType());
    }

    @Test
    public void testHierarchy() {
        final EntityType<Cat> cat = entityManagerFactory().getMetamodel().entity(Cat.class);
        Assert.assertNotNull(cat);
        Assert.assertEquals(7, cat.getAttributes().size());
        Assert.assertEquals(1, cat.getDeclaredAttributes().size());
        ensureProperMember(cat.getDeclaredAttributes());
        Assert.assertTrue(cat.hasVersionAttribute());
        Assert.assertEquals("version", cat.getVersion(Long.class).getName());
        verifyDeclaredVersionNotPresent(cat);
        verifyDeclaredIdNotPresentAndIdPresent(cat);
        Assert.assertEquals(MAPPED_SUPERCLASS, cat.getSupertype().getPersistenceType());
        MappedSuperclassType<Cattish> cattish = ((MappedSuperclassType<Cattish>) (cat.getSupertype()));
        Assert.assertEquals(6, cattish.getAttributes().size());
        Assert.assertEquals(1, cattish.getDeclaredAttributes().size());
        ensureProperMember(cattish.getDeclaredAttributes());
        Assert.assertTrue(cattish.hasVersionAttribute());
        Assert.assertEquals("version", cattish.getVersion(Long.class).getName());
        verifyDeclaredVersionNotPresent(cattish);
        verifyDeclaredIdNotPresentAndIdPresent(cattish);
        Assert.assertEquals(ENTITY, cattish.getSupertype().getPersistenceType());
        EntityType<Feline> feline = ((EntityType<Feline>) (cattish.getSupertype()));
        Assert.assertEquals(5, feline.getAttributes().size());
        Assert.assertEquals(1, feline.getDeclaredAttributes().size());
        ensureProperMember(feline.getDeclaredAttributes());
        Assert.assertTrue(feline.hasVersionAttribute());
        Assert.assertEquals("version", feline.getVersion(Long.class).getName());
        verifyDeclaredVersionNotPresent(feline);
        verifyDeclaredIdNotPresentAndIdPresent(feline);
        Assert.assertEquals(MAPPED_SUPERCLASS, feline.getSupertype().getPersistenceType());
        MappedSuperclassType<Animal> animal = ((MappedSuperclassType<Animal>) (feline.getSupertype()));
        Assert.assertEquals(4, animal.getAttributes().size());
        Assert.assertEquals(2, animal.getDeclaredAttributes().size());
        ensureProperMember(animal.getDeclaredAttributes());
        Assert.assertTrue(animal.hasVersionAttribute());
        Assert.assertEquals("version", animal.getVersion(Long.class).getName());
        verifyDeclaredVersionNotPresent(animal);
        Assert.assertEquals("id", animal.getId(Long.class).getName());
        final SingularAttribute<Animal, Long> id = animal.getDeclaredId(Long.class);
        Assert.assertEquals("id", id.getName());
        Assert.assertNotNull(id.getJavaMember());
        Assert.assertEquals(MAPPED_SUPERCLASS, animal.getSupertype().getPersistenceType());
        MappedSuperclassType<Thing> thing = ((MappedSuperclassType<Thing>) (animal.getSupertype()));
        Assert.assertEquals(2, thing.getAttributes().size());
        Assert.assertEquals(2, thing.getDeclaredAttributes().size());
        ensureProperMember(thing.getDeclaredAttributes());
        final SingularAttribute<Thing, Double> weight = thing.getDeclaredSingularAttribute("weight", Double.class);
        Assert.assertEquals(Double.class, weight.getJavaType());
        Assert.assertEquals("version", thing.getVersion(Long.class).getName());
        final SingularAttribute<Thing, Long> version = thing.getDeclaredVersion(Long.class);
        Assert.assertEquals("version", version.getName());
        Assert.assertNotNull(version.getJavaMember());
        Assert.assertNull(thing.getId(Long.class));
        Assert.assertNull(thing.getSupertype());
    }

    @Test
    public void testBackrefAndGenerics() throws Exception {
        final EntityType<Parent> parent = entityManagerFactory().getMetamodel().entity(Parent.class);
        Assert.assertNotNull(parent);
        final SetAttribute<? super Parent, ?> children = parent.getSet("children");
        Assert.assertNotNull(children);
        Assert.assertEquals(1, parent.getPluralAttributes().size());
        Assert.assertEquals(4, parent.getAttributes().size());
        final EntityType<Child> child = entityManagerFactory().getMetamodel().entity(Child.class);
        Assert.assertNotNull(child);
        Assert.assertEquals(2, child.getAttributes().size());
        final SingularAttribute<? super Parent, Parent.Relatives> attribute = parent.getSingularAttribute("siblings", Parent.Relatives.class);
        final EmbeddableType<Parent.Relatives> siblings = ((EmbeddableType<Parent.Relatives>) (attribute.getType()));
        Assert.assertNotNull(siblings);
        final SetAttribute<? super Parent.Relatives, ?> siblingsCollection = siblings.getSet("siblings");
        Assert.assertNotNull(siblingsCollection);
        final Type<?> collectionElement = siblingsCollection.getElementType();
        Assert.assertNotNull(collectionElement);
        Assert.assertEquals(collectionElement, child);
    }
}

