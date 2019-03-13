package com.vaadin.v7.data.util;


import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.AbstractBeanContainer.BeanIdResolver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class BeanContainerTest extends AbstractBeanContainerTestBase {
    protected static class PersonNameResolver implements BeanIdResolver<String, AbstractBeanContainerTestBase.Person> {
        @Override
        public String getIdForBean(AbstractBeanContainerTestBase.Person bean) {
            return bean != null ? bean.getName() : null;
        }
    }

    protected static class NullResolver implements BeanIdResolver<String, AbstractBeanContainerTestBase.Person> {
        @Override
        public String getIdForBean(AbstractBeanContainerTestBase.Person bean) {
            return null;
        }
    }

    private Map<String, AbstractBeanContainerTestBase.ClassName> nameToBean = new LinkedHashMap<String, AbstractBeanContainerTestBase.ClassName>();

    @Test
    public void testGetType_existingProperty_typeReturned() {
        BeanContainer<String, AbstractBeanContainerTestBase.ClassName> container = getContainer();
        Assert.assertEquals("Unexpected type is returned for property 'simpleName'", String.class, container.getType("simpleName"));
    }

    @Test
    public void testGetType_notExistingProperty_nullReturned() {
        BeanContainer<String, AbstractBeanContainerTestBase.ClassName> container = getContainer();
        Assert.assertNull("Not null type is returned for property ''", container.getType(""));
    }

    @Test
    public void testBasicOperations() {
        testBasicContainerOperations(getContainer());
    }

    @Test
    public void testFiltering() {
        testContainerFiltering(getContainer());
    }

    @Test
    public void testSorting() {
        testContainerSorting(getContainer());
    }

    @Test
    public void testSortingAndFiltering() {
        testContainerSortingAndFiltering(getContainer());
    }

    // duplicated from parent class and modified - adding items to
    // BeanContainer differs from other containers
    @Test
    public void testContainerOrdered() {
        BeanContainer<String, String> container = new BeanContainer<String, String>(String.class);
        String id = "test1";
        Item item = container.addItem(id, "value");
        Assert.assertNotNull(item);
        Assert.assertEquals(id, container.firstItemId());
        Assert.assertEquals(id, container.lastItemId());
        // isFirstId
        Assert.assertTrue(container.isFirstId(id));
        Assert.assertTrue(container.isFirstId(container.firstItemId()));
        // isLastId
        Assert.assertTrue(container.isLastId(id));
        Assert.assertTrue(container.isLastId(container.lastItemId()));
        // Add a new item before the first
        // addItemAfter
        String newFirstId = "newFirst";
        item = container.addItemAfter(null, newFirstId, "newFirstValue");
        Assert.assertNotNull(item);
        Assert.assertNotNull(container.getItem(newFirstId));
        // isFirstId
        Assert.assertTrue(container.isFirstId(newFirstId));
        Assert.assertTrue(container.isFirstId(container.firstItemId()));
        // isLastId
        Assert.assertTrue(container.isLastId(id));
        Assert.assertTrue(container.isLastId(container.lastItemId()));
        // nextItemId
        Assert.assertEquals(id, container.nextItemId(newFirstId));
        Assert.assertNull(container.nextItemId(id));
        Assert.assertNull(container.nextItemId("not-in-container"));
        // prevItemId
        Assert.assertEquals(newFirstId, container.prevItemId(id));
        Assert.assertNull(container.prevItemId(newFirstId));
        Assert.assertNull(container.prevItemId("not-in-container"));
        // addItemAfter(IDTYPE, IDTYPE, BT)
        String newSecondItemId = "newSecond";
        item = container.addItemAfter(newFirstId, newSecondItemId, "newSecondValue");
        // order is now: newFirstId, newSecondItemId, id
        Assert.assertNotNull(item);
        Assert.assertNotNull(container.getItem(newSecondItemId));
        Assert.assertEquals(id, container.nextItemId(newSecondItemId));
        Assert.assertEquals(newFirstId, container.prevItemId(newSecondItemId));
        // addItemAfter(IDTYPE, IDTYPE, BT)
        String fourthId = "id of the fourth item";
        Item fourth = container.addItemAfter(newFirstId, fourthId, "fourthValue");
        // order is now: newFirstId, fourthId, newSecondItemId, id
        Assert.assertNotNull(fourth);
        Assert.assertEquals(fourth, container.getItem(fourthId));
        Assert.assertEquals(newSecondItemId, container.nextItemId(fourthId));
        Assert.assertEquals(newFirstId, container.prevItemId(fourthId));
        // addItemAfter(IDTYPE, IDTYPE, BT)
        String fifthId = "fifth";
        Item fifth = container.addItemAfter(null, fifthId, "fifthValue");
        // order is now: fifthId, newFirstId, fourthId, newSecondItemId, id
        Assert.assertNotNull(fifth);
        Assert.assertEquals(fifth, container.getItem(fifthId));
        Assert.assertEquals(newFirstId, container.nextItemId(fifthId));
        Assert.assertNull(container.prevItemId(fifthId));
    }

    // TODO test Container.Indexed interface operation - testContainerIndexed()?
    @Test
    public void testAddItemAt() {
        BeanContainer<String, String> container = new BeanContainer<String, String>(String.class);
        container.addItem("id1", "value1");
        // id1
        container.addItemAt(0, "id2", "value2");
        // id2, id1
        container.addItemAt(1, "id3", "value3");
        // id2, id3, id1
        container.addItemAt(container.size(), "id4", "value4");
        // id2, id3, id1, id4
        Assert.assertNull(container.addItemAt((-1), "id5", "value5"));
        Assert.assertNull(container.addItemAt(((container.size()) + 1), "id6", "value6"));
        Assert.assertEquals(4, container.size());
        Assert.assertEquals("id2", container.getIdByIndex(0));
        Assert.assertEquals("id3", container.getIdByIndex(1));
        Assert.assertEquals("id1", container.getIdByIndex(2));
        Assert.assertEquals("id4", container.getIdByIndex(3));
    }

    @Test
    public void testUnsupportedMethods() {
        BeanContainer<String, AbstractBeanContainerTestBase.Person> container = new BeanContainer<String, AbstractBeanContainerTestBase.Person>(AbstractBeanContainerTestBase.Person.class);
        container.addItem("John", new AbstractBeanContainerTestBase.Person("John"));
        try {
            container.addItem();
            Assert.fail();
        } catch (UnsupportedOperationException e) {
            // should get exception
        }
        try {
            container.addItem(null);
            Assert.fail();
        } catch (UnsupportedOperationException e) {
            // should get exception
        }
        try {
            container.addItemAfter(null, null);
            Assert.fail();
        } catch (UnsupportedOperationException e) {
            // should get exception
        }
        try {
            container.addItemAfter(new AbstractBeanContainerTestBase.Person("Jane"));
            Assert.fail();
        } catch (UnsupportedOperationException e) {
            // should get exception
        }
        try {
            container.addItemAt(0);
            Assert.fail();
        } catch (UnsupportedOperationException e) {
            // should get exception
        }
        try {
            container.addItemAt(0, new AbstractBeanContainerTestBase.Person("Jane"));
            Assert.fail();
        } catch (UnsupportedOperationException e) {
            // should get exception
        }
        try {
            container.addContainerProperty("lastName", String.class, "");
            Assert.fail();
        } catch (UnsupportedOperationException e) {
            // should get exception
        }
        Assert.assertEquals(1, container.size());
    }

    @Test
    public void testRemoveContainerProperty() {
        BeanContainer<String, AbstractBeanContainerTestBase.Person> container = new BeanContainer<String, AbstractBeanContainerTestBase.Person>(AbstractBeanContainerTestBase.Person.class);
        container.setBeanIdResolver(new BeanContainerTest.PersonNameResolver());
        container.addBean(new AbstractBeanContainerTestBase.Person("John"));
        Assert.assertEquals("John", container.getContainerProperty("John", "name").getValue());
        Assert.assertTrue(container.removeContainerProperty("name"));
        Assert.assertNull(container.getContainerProperty("John", "name"));
        Assert.assertNotNull(container.getItem("John"));
        // property removed also from item
        Assert.assertNull(container.getItem("John").getItemProperty("name"));
    }

    @Test
    public void testAddNullBeans() {
        BeanContainer<String, AbstractBeanContainerTestBase.Person> container = new BeanContainer<String, AbstractBeanContainerTestBase.Person>(AbstractBeanContainerTestBase.Person.class);
        Assert.assertNull(container.addItem("id1", null));
        Assert.assertNull(container.addItemAfter(null, "id2", null));
        Assert.assertNull(container.addItemAt(0, "id3", null));
        Assert.assertEquals(0, container.size());
    }

    @Test
    public void testAddNullId() {
        BeanContainer<String, AbstractBeanContainerTestBase.Person> container = new BeanContainer<String, AbstractBeanContainerTestBase.Person>(AbstractBeanContainerTestBase.Person.class);
        AbstractBeanContainerTestBase.Person john = new AbstractBeanContainerTestBase.Person("John");
        Assert.assertNull(container.addItem(null, john));
        Assert.assertNull(container.addItemAfter(null, null, john));
        Assert.assertNull(container.addItemAt(0, null, john));
        Assert.assertEquals(0, container.size());
    }

    @Test
    public void testEmptyContainer() {
        BeanContainer<String, AbstractBeanContainerTestBase.Person> container = new BeanContainer<String, AbstractBeanContainerTestBase.Person>(AbstractBeanContainerTestBase.Person.class);
        Assert.assertNull(container.firstItemId());
        Assert.assertNull(container.lastItemId());
        Assert.assertEquals(0, container.size());
        // could test more about empty container
    }

    @Test
    public void testAddBeanWithoutResolver() {
        BeanContainer<String, AbstractBeanContainerTestBase.Person> container = new BeanContainer<String, AbstractBeanContainerTestBase.Person>(AbstractBeanContainerTestBase.Person.class);
        try {
            container.addBean(new AbstractBeanContainerTestBase.Person("John"));
            Assert.fail();
        } catch (IllegalStateException e) {
            // should get exception
        }
        try {
            container.addBeanAfter(null, new AbstractBeanContainerTestBase.Person("Jane"));
            Assert.fail();
        } catch (IllegalStateException e) {
            // should get exception
        }
        try {
            container.addBeanAt(0, new AbstractBeanContainerTestBase.Person("Jack"));
            Assert.fail();
        } catch (IllegalStateException e) {
            // should get exception
        }
        try {
            container.addAll(Arrays.asList(new AbstractBeanContainerTestBase.Person[]{ new AbstractBeanContainerTestBase.Person("Jack") }));
            Assert.fail();
        } catch (IllegalStateException e) {
            // should get exception
        }
        Assert.assertEquals(0, container.size());
    }

    @Test
    public void testAddAllWithNullItemId() {
        BeanContainer<String, AbstractBeanContainerTestBase.Person> container = new BeanContainer<String, AbstractBeanContainerTestBase.Person>(AbstractBeanContainerTestBase.Person.class);
        // resolver that returns null as item id
        container.setBeanIdResolver(new BeanIdResolver<String, AbstractBeanContainerTestBase.Person>() {
            @Override
            public String getIdForBean(AbstractBeanContainerTestBase.Person bean) {
                return bean.getName();
            }
        });
        List<AbstractBeanContainerTestBase.Person> persons = new ArrayList<AbstractBeanContainerTestBase.Person>();
        persons.add(new AbstractBeanContainerTestBase.Person("John"));
        persons.add(new AbstractBeanContainerTestBase.Person("Marc"));
        persons.add(new AbstractBeanContainerTestBase.Person(null));
        persons.add(new AbstractBeanContainerTestBase.Person("foo"));
        try {
            container.addAll(persons);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // should get exception
        }
        container.removeAllItems();
        persons.remove(2);
        container.addAll(persons);
        Assert.assertEquals(3, container.size());
    }

    @Test
    public void testAddBeanWithNullResolver() {
        BeanContainer<String, AbstractBeanContainerTestBase.Person> container = new BeanContainer<String, AbstractBeanContainerTestBase.Person>(AbstractBeanContainerTestBase.Person.class);
        // resolver that returns null as item id
        container.setBeanIdResolver(new BeanContainerTest.NullResolver());
        try {
            container.addBean(new AbstractBeanContainerTestBase.Person("John"));
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // should get exception
        }
        try {
            container.addBeanAfter(null, new AbstractBeanContainerTestBase.Person("Jane"));
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // should get exception
        }
        try {
            container.addBeanAt(0, new AbstractBeanContainerTestBase.Person("Jack"));
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // should get exception
        }
        Assert.assertEquals(0, container.size());
    }

    @Test
    public void testAddBeanWithResolver() {
        BeanContainer<String, AbstractBeanContainerTestBase.Person> container = new BeanContainer<String, AbstractBeanContainerTestBase.Person>(AbstractBeanContainerTestBase.Person.class);
        container.setBeanIdResolver(new BeanContainerTest.PersonNameResolver());
        Assert.assertNotNull(container.addBean(new AbstractBeanContainerTestBase.Person("John")));
        Assert.assertNotNull(container.addBeanAfter(null, new AbstractBeanContainerTestBase.Person("Jane")));
        Assert.assertNotNull(container.addBeanAt(0, new AbstractBeanContainerTestBase.Person("Jack")));
        container.addAll(Arrays.asList(new AbstractBeanContainerTestBase.Person[]{ new AbstractBeanContainerTestBase.Person("Jill"), new AbstractBeanContainerTestBase.Person("Joe") }));
        Assert.assertTrue(container.containsId("John"));
        Assert.assertTrue(container.containsId("Jane"));
        Assert.assertTrue(container.containsId("Jack"));
        Assert.assertTrue(container.containsId("Jill"));
        Assert.assertTrue(container.containsId("Joe"));
        Assert.assertEquals(3, container.indexOfId("Jill"));
        Assert.assertEquals(4, container.indexOfId("Joe"));
        Assert.assertEquals(5, container.size());
    }

    @Test
    public void testAddNullBeansWithResolver() {
        BeanContainer<String, AbstractBeanContainerTestBase.Person> container = new BeanContainer<String, AbstractBeanContainerTestBase.Person>(AbstractBeanContainerTestBase.Person.class);
        container.setBeanIdResolver(new BeanContainerTest.PersonNameResolver());
        Assert.assertNull(container.addBean(null));
        Assert.assertNull(container.addBeanAfter(null, null));
        Assert.assertNull(container.addBeanAt(0, null));
        Assert.assertEquals(0, container.size());
    }

    @Test
    public void testAddBeanWithPropertyResolver() {
        BeanContainer<String, AbstractBeanContainerTestBase.Person> container = new BeanContainer<String, AbstractBeanContainerTestBase.Person>(AbstractBeanContainerTestBase.Person.class);
        container.setBeanIdProperty("name");
        Assert.assertNotNull(container.addBean(new AbstractBeanContainerTestBase.Person("John")));
        Assert.assertNotNull(container.addBeanAfter(null, new AbstractBeanContainerTestBase.Person("Jane")));
        Assert.assertNotNull(container.addBeanAt(0, new AbstractBeanContainerTestBase.Person("Jack")));
        container.addAll(Arrays.asList(new AbstractBeanContainerTestBase.Person[]{ new AbstractBeanContainerTestBase.Person("Jill"), new AbstractBeanContainerTestBase.Person("Joe") }));
        Assert.assertTrue(container.containsId("John"));
        Assert.assertTrue(container.containsId("Jane"));
        Assert.assertTrue(container.containsId("Jack"));
        Assert.assertTrue(container.containsId("Jill"));
        Assert.assertTrue(container.containsId("Joe"));
        Assert.assertEquals(3, container.indexOfId("Jill"));
        Assert.assertEquals(4, container.indexOfId("Joe"));
        Assert.assertEquals(5, container.size());
    }

    @Test
    public void testAddNestedContainerProperty() {
        BeanContainer<String, NestedMethodPropertyTest.Person> container = new BeanContainer<String, NestedMethodPropertyTest.Person>(NestedMethodPropertyTest.Person.class);
        container.setBeanIdProperty("name");
        container.addBean(new NestedMethodPropertyTest.Person("John", new NestedMethodPropertyTest.Address("Ruukinkatu 2-4", 20540)));
        Assert.assertTrue(container.addNestedContainerProperty("address.street"));
        Assert.assertEquals("Ruukinkatu 2-4", container.getContainerProperty("John", "address.street").getValue());
    }

    @Test
    public void testNestedContainerPropertyWithNullBean() {
        BeanContainer<String, NestedMethodPropertyTest.Person> container = new BeanContainer<String, NestedMethodPropertyTest.Person>(NestedMethodPropertyTest.Person.class);
        container.setBeanIdProperty("name");
        container.addBean(new NestedMethodPropertyTest.Person("John", null));
        Assert.assertTrue(container.addNestedContainerProperty("address.postalCodeObject"));
        Assert.assertTrue(container.addNestedContainerProperty("address.street"));
        // the nested properties added with allowNullBean setting should return
        // null
        Assert.assertNull(container.getContainerProperty("John", "address.street").getValue());
    }
}

