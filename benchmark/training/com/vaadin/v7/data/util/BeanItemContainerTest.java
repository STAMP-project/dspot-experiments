package com.vaadin.v7.data.util;


import com.vaadin.v7.data.Container.Indexed.ItemAddEvent;
import com.vaadin.v7.data.Container.Indexed.ItemRemoveEvent;
import com.vaadin.v7.data.Container.ItemSetChangeListener;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.filter.Compare;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test basic functionality of BeanItemContainer.
 *
 * Most sorting related tests are in {@link BeanItemContainerSortTest}.
 */
public class BeanItemContainerTest extends AbstractBeanContainerTestBase {
    // basics from the common container test
    private Map<String, AbstractBeanContainerTestBase.ClassName> nameToBean = new LinkedHashMap<String, AbstractBeanContainerTestBase.ClassName>();

    @Test
    public void testGetType_existingProperty_typeReturned() {
        BeanItemContainer<AbstractBeanContainerTestBase.ClassName> container = getContainer();
        Assert.assertEquals("Unexpected type is returned for property 'simpleName'", String.class, container.getType("simpleName"));
    }

    @Test
    public void testGetType_notExistingProperty_nullReturned() {
        BeanItemContainer<AbstractBeanContainerTestBase.ClassName> container = getContainer();
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
    // BeanItemContainer differs from other containers
    @Test
    public void testContainerOrdered() {
        BeanItemContainer<String> container = new BeanItemContainer<String>(String.class);
        String id = "test1";
        Item item = container.addBean(id);
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
        item = container.addItemAfter(null, newFirstId);
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
        // addItemAfter(Object)
        String newSecondItemId = "newSecond";
        item = container.addItemAfter(newFirstId, newSecondItemId);
        // order is now: newFirstId, newSecondItemId, id
        Assert.assertNotNull(item);
        Assert.assertNotNull(container.getItem(newSecondItemId));
        Assert.assertEquals(id, container.nextItemId(newSecondItemId));
        Assert.assertEquals(newFirstId, container.prevItemId(newSecondItemId));
        // addItemAfter(Object,Object)
        String fourthId = "id of the fourth item";
        Item fourth = container.addItemAfter(newFirstId, fourthId);
        // order is now: newFirstId, fourthId, newSecondItemId, id
        Assert.assertNotNull(fourth);
        Assert.assertEquals(fourth, container.getItem(fourthId));
        Assert.assertEquals(newSecondItemId, container.nextItemId(fourthId));
        Assert.assertEquals(newFirstId, container.prevItemId(fourthId));
        // addItemAfter(Object,Object)
        Object fifthId = "fifth";
        Item fifth = container.addItemAfter(null, fifthId);
        // order is now: fifthId, newFirstId, fourthId, newSecondItemId, id
        Assert.assertNotNull(fifth);
        Assert.assertEquals(fifth, container.getItem(fifthId));
        Assert.assertEquals(newFirstId, container.nextItemId(fifthId));
        Assert.assertNull(container.prevItemId(fifthId));
    }

    @Test
    public void testContainerIndexed() {
        testContainerIndexed(getContainer(), nameToBean.get(sampleData[2]), 2, false, new AbstractBeanContainerTestBase.ClassName("org.vaadin.test.Test", 8888), true);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testCollectionConstructors() {
        List<AbstractBeanContainerTestBase.ClassName> classNames = new ArrayList<AbstractBeanContainerTestBase.ClassName>();
        classNames.add(new AbstractBeanContainerTestBase.ClassName("a.b.c.Def", 1));
        classNames.add(new AbstractBeanContainerTestBase.ClassName("a.b.c.Fed", 2));
        classNames.add(new AbstractBeanContainerTestBase.ClassName("b.c.d.Def", 3));
        // note that this constructor is problematic, users should use the
        // version that
        // takes the bean class as a parameter
        BeanItemContainer<AbstractBeanContainerTestBase.ClassName> container = new BeanItemContainer<AbstractBeanContainerTestBase.ClassName>(classNames);
        Assert.assertEquals(3, container.size());
        Assert.assertEquals(classNames.get(0), container.firstItemId());
        Assert.assertEquals(classNames.get(1), container.getIdByIndex(1));
        Assert.assertEquals(classNames.get(2), container.lastItemId());
        BeanItemContainer<AbstractBeanContainerTestBase.ClassName> container2 = new BeanItemContainer<AbstractBeanContainerTestBase.ClassName>(AbstractBeanContainerTestBase.ClassName.class, classNames);
        Assert.assertEquals(3, container2.size());
        Assert.assertEquals(classNames.get(0), container2.firstItemId());
        Assert.assertEquals(classNames.get(1), container2.getIdByIndex(1));
        Assert.assertEquals(classNames.get(2), container2.lastItemId());
    }

    // this only applies to the collection constructor with no type parameter
    @SuppressWarnings("deprecation")
    @Test
    public void testEmptyCollectionConstructor() {
        try {
            new BeanItemContainer<AbstractBeanContainerTestBase.ClassName>(((Collection<AbstractBeanContainerTestBase.ClassName>) (null)));
            Assert.fail("Initializing BeanItemContainer from a null collection should not work!");
        } catch (IllegalArgumentException e) {
            // success
        }
        try {
            new BeanItemContainer<AbstractBeanContainerTestBase.ClassName>(new ArrayList<AbstractBeanContainerTestBase.ClassName>());
            Assert.fail("Initializing BeanItemContainer from an empty collection should not work!");
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    @Test
    public void testItemSetChangeListeners() {
        BeanItemContainer<AbstractBeanContainerTestBase.ClassName> container = getContainer();
        AbstractContainerTestBase.ItemSetChangeCounter counter = new AbstractContainerTestBase.ItemSetChangeCounter();
        container.addListener(counter);
        AbstractBeanContainerTestBase.ClassName cn1 = new AbstractBeanContainerTestBase.ClassName("com.example.Test", 1111);
        AbstractBeanContainerTestBase.ClassName cn2 = new AbstractBeanContainerTestBase.ClassName("com.example.Test2", 2222);
        initializeContainer(container);
        counter.reset();
        container.addBean(cn1);
        counter.assertOnce();
        initializeContainer(container);
        counter.reset();
        container.addItem(cn1);
        counter.assertOnce();
        // no notification if already in container
        container.addItem(cn1);
        counter.assertNone();
        container.addItem(cn2);
        counter.assertOnce();
        initializeContainer(container);
        counter.reset();
        container.addItemAfter(null, cn1);
        counter.assertOnce();
        Assert.assertEquals("com.example.Test", container.getContainerProperty(container.firstItemId(), AbstractContainerTestBase.FULLY_QUALIFIED_NAME).getValue());
        initializeContainer(container);
        counter.reset();
        container.addItemAfter(container.firstItemId(), cn1);
        counter.assertOnce();
        Assert.assertEquals("com.example.Test", container.getContainerProperty(container.getIdByIndex(1), AbstractContainerTestBase.FULLY_QUALIFIED_NAME).getValue());
        initializeContainer(container);
        counter.reset();
        container.addItemAfter(container.lastItemId(), cn1);
        counter.assertOnce();
        Assert.assertEquals("com.example.Test", container.getContainerProperty(container.lastItemId(), AbstractContainerTestBase.FULLY_QUALIFIED_NAME).getValue());
        initializeContainer(container);
        counter.reset();
        container.addItemAt(0, cn1);
        counter.assertOnce();
        Assert.assertEquals("com.example.Test", container.getContainerProperty(container.firstItemId(), AbstractContainerTestBase.FULLY_QUALIFIED_NAME).getValue());
        initializeContainer(container);
        counter.reset();
        container.addItemAt(1, cn1);
        counter.assertOnce();
        Assert.assertEquals("com.example.Test", container.getContainerProperty(container.getIdByIndex(1), AbstractContainerTestBase.FULLY_QUALIFIED_NAME).getValue());
        initializeContainer(container);
        counter.reset();
        container.addItemAt(container.size(), cn1);
        counter.assertOnce();
        Assert.assertEquals("com.example.Test", container.getContainerProperty(container.lastItemId(), AbstractContainerTestBase.FULLY_QUALIFIED_NAME).getValue());
        initializeContainer(container);
        counter.reset();
        container.removeItem(nameToBean.get(sampleData[0]));
        counter.assertOnce();
        initializeContainer(container);
        counter.reset();
        // no notification for removing a non-existing item
        container.removeItem(cn1);
        counter.assertNone();
        initializeContainer(container);
        counter.reset();
        container.removeAllItems();
        counter.assertOnce();
        // already empty
        container.removeAllItems();
        counter.assertNone();
    }

    @Test
    public void testItemSetChangeListenersFiltering() {
        BeanItemContainer<AbstractBeanContainerTestBase.ClassName> container = getContainer();
        AbstractContainerTestBase.ItemSetChangeCounter counter = new AbstractContainerTestBase.ItemSetChangeCounter();
        container.addListener(counter);
        AbstractBeanContainerTestBase.ClassName cn1 = new AbstractBeanContainerTestBase.ClassName("com.example.Test", 1111);
        AbstractBeanContainerTestBase.ClassName cn2 = new AbstractBeanContainerTestBase.ClassName("com.example.Test2", 2222);
        AbstractBeanContainerTestBase.ClassName other = new AbstractBeanContainerTestBase.ClassName("com.example.Other", 3333);
        // simply adding or removing container filters should cause event
        // (content changes)
        initializeContainer(container);
        counter.reset();
        container.addContainerFilter(AbstractContainerTestBase.SIMPLE_NAME, "a", true, false);
        counter.assertOnce();
        container.removeContainerFilters(AbstractContainerTestBase.SIMPLE_NAME);
        counter.assertOnce();
        initializeContainer(container);
        counter.reset();
        container.addContainerFilter(AbstractContainerTestBase.SIMPLE_NAME, "a", true, false);
        counter.assertOnce();
        container.removeAllContainerFilters();
        counter.assertOnce();
        // perform operations while filtering container
        initializeContainer(container);
        counter.reset();
        container.addContainerFilter(AbstractContainerTestBase.FULLY_QUALIFIED_NAME, "Test", true, false);
        counter.assertOnce();
        // passes filter
        container.addBean(cn1);
        counter.assertOnce();
        // passes filter but already in the container
        container.addBean(cn1);
        counter.assertNone();
        initializeContainer(container);
        counter.reset();
        // passes filter
        container.addItem(cn1);
        counter.assertOnce();
        // already in the container
        container.addItem(cn1);
        counter.assertNone();
        container.addItem(cn2);
        counter.assertOnce();
        // does not pass filter
        container.addItem(other);
        counter.assertNone();
        initializeContainer(container);
        counter.reset();
        container.addItemAfter(null, cn1);
        counter.assertOnce();
        Assert.assertEquals("com.example.Test", container.getContainerProperty(container.firstItemId(), AbstractContainerTestBase.FULLY_QUALIFIED_NAME).getValue());
        initializeContainer(container);
        counter.reset();
        container.addItemAfter(container.firstItemId(), cn1);
        counter.assertOnce();
        Assert.assertEquals("com.example.Test", container.getContainerProperty(container.getIdByIndex(1), AbstractContainerTestBase.FULLY_QUALIFIED_NAME).getValue());
        initializeContainer(container);
        counter.reset();
        container.addItemAfter(container.lastItemId(), cn1);
        counter.assertOnce();
        Assert.assertEquals("com.example.Test", container.getContainerProperty(container.lastItemId(), AbstractContainerTestBase.FULLY_QUALIFIED_NAME).getValue());
        initializeContainer(container);
        counter.reset();
        container.addItemAt(0, cn1);
        counter.assertOnce();
        Assert.assertEquals("com.example.Test", container.getContainerProperty(container.firstItemId(), AbstractContainerTestBase.FULLY_QUALIFIED_NAME).getValue());
        initializeContainer(container);
        counter.reset();
        container.addItemAt(1, cn1);
        counter.assertOnce();
        Assert.assertEquals("com.example.Test", container.getContainerProperty(container.getIdByIndex(1), AbstractContainerTestBase.FULLY_QUALIFIED_NAME).getValue());
        initializeContainer(container);
        counter.reset();
        container.addItemAt(container.size(), cn1);
        counter.assertOnce();
        Assert.assertEquals("com.example.Test", container.getContainerProperty(container.lastItemId(), AbstractContainerTestBase.FULLY_QUALIFIED_NAME).getValue());
        // does not pass filter
        // note: testAddRemoveWhileFiltering() checks position for these after
        // removing filter etc, here concentrating on listeners
        initializeContainer(container);
        counter.reset();
        container.addItemAfter(null, other);
        counter.assertNone();
        initializeContainer(container);
        counter.reset();
        container.addItemAfter(container.firstItemId(), other);
        counter.assertNone();
        initializeContainer(container);
        counter.reset();
        container.addItemAfter(container.lastItemId(), other);
        counter.assertNone();
        initializeContainer(container);
        counter.reset();
        container.addItemAt(0, other);
        counter.assertNone();
        initializeContainer(container);
        counter.reset();
        container.addItemAt(1, other);
        counter.assertNone();
        initializeContainer(container);
        counter.reset();
        container.addItemAt(container.size(), other);
        counter.assertNone();
        // passes filter
        initializeContainer(container);
        counter.reset();
        container.addItem(cn1);
        counter.assertOnce();
        container.removeItem(cn1);
        counter.assertOnce();
        // does not pass filter
        initializeContainer(container);
        counter.reset();
        // not visible
        container.removeItem(nameToBean.get(sampleData[0]));
        counter.assertNone();
        container.removeAllItems();
        counter.assertOnce();
        // no visible items
        container.removeAllItems();
        counter.assertNone();
    }

    @Test
    public void testAddRemoveWhileFiltering() {
        BeanItemContainer<AbstractBeanContainerTestBase.Person> container = new BeanItemContainer<AbstractBeanContainerTestBase.Person>(AbstractBeanContainerTestBase.Person.class);
        AbstractBeanContainerTestBase.Person john = new AbstractBeanContainerTestBase.Person("John");
        AbstractBeanContainerTestBase.Person jane = new AbstractBeanContainerTestBase.Person("Jane");
        AbstractBeanContainerTestBase.Person matthew = new AbstractBeanContainerTestBase.Person("Matthew");
        AbstractBeanContainerTestBase.Person jack = new AbstractBeanContainerTestBase.Person("Jack");
        AbstractBeanContainerTestBase.Person michael = new AbstractBeanContainerTestBase.Person("Michael");
        AbstractBeanContainerTestBase.Person william = new AbstractBeanContainerTestBase.Person("William");
        AbstractBeanContainerTestBase.Person julia = new AbstractBeanContainerTestBase.Person("Julia");
        AbstractBeanContainerTestBase.Person george = new AbstractBeanContainerTestBase.Person("George");
        AbstractBeanContainerTestBase.Person mark = new AbstractBeanContainerTestBase.Person("Mark");
        container.addBean(john);
        container.addBean(jane);
        container.addBean(matthew);
        Assert.assertEquals(3, container.size());
        // john, jane, matthew
        container.addContainerFilter("name", "j", true, true);
        Assert.assertEquals(2, container.size());
        // john, jane, (matthew)
        // add a bean that passes the filter
        container.addBean(jack);
        Assert.assertEquals(3, container.size());
        Assert.assertEquals(jack, container.lastItemId());
        // john, jane, (matthew), jack
        // add beans that do not pass the filter
        container.addBean(michael);
        // john, jane, (matthew), jack, (michael)
        container.addItemAfter(null, william);
        // (william), john, jane, (matthew), jack, (michael)
        // add after an item that is shown
        container.addItemAfter(john, george);
        // (william), john, (george), jane, (matthew), jack, (michael)
        Assert.assertEquals(3, container.size());
        Assert.assertEquals(john, container.firstItemId());
        // add after an item that is not shown does nothing
        container.addItemAfter(william, julia);
        // (william), john, (george), jane, (matthew), jack, (michael)
        Assert.assertEquals(3, container.size());
        Assert.assertEquals(john, container.firstItemId());
        container.addItemAt(1, julia);
        // (william), john, julia, (george), jane, (matthew), jack, (michael)
        container.addItemAt(2, mark);
        // (william), john, julia, (mark), (george), jane, (matthew), jack,
        // (michael)
        container.removeItem(matthew);
        // (william), john, julia, (mark), (george), jane, jack, (michael)
        Assert.assertEquals(4, container.size());
        Assert.assertEquals(jack, container.lastItemId());
        container.removeContainerFilters("name");
        Assert.assertEquals(8, container.size());
        Assert.assertEquals(william, container.firstItemId());
        Assert.assertEquals(john, container.nextItemId(william));
        Assert.assertEquals(julia, container.nextItemId(john));
        Assert.assertEquals(mark, container.nextItemId(julia));
        Assert.assertEquals(george, container.nextItemId(mark));
        Assert.assertEquals(jane, container.nextItemId(george));
        Assert.assertEquals(jack, container.nextItemId(jane));
        Assert.assertEquals(michael, container.lastItemId());
    }

    @Test
    public void testRefilterOnPropertyModification() {
        BeanItemContainer<AbstractBeanContainerTestBase.Person> container = new BeanItemContainer<AbstractBeanContainerTestBase.Person>(AbstractBeanContainerTestBase.Person.class);
        AbstractBeanContainerTestBase.Person john = new AbstractBeanContainerTestBase.Person("John");
        AbstractBeanContainerTestBase.Person jane = new AbstractBeanContainerTestBase.Person("Jane");
        AbstractBeanContainerTestBase.Person matthew = new AbstractBeanContainerTestBase.Person("Matthew");
        container.addBean(john);
        container.addBean(jane);
        container.addBean(matthew);
        Assert.assertEquals(3, container.size());
        // john, jane, matthew
        container.addContainerFilter("name", "j", true, true);
        Assert.assertEquals(2, container.size());
        // john, jane, (matthew)
        // #6053 currently, modification of an item that is not visible does not
        // trigger refiltering - should it?
        // matthew.setName("Julia");
        // assertEquals(3, container.size());
        // john, jane, julia
        john.setName("Mark");
        Assert.assertEquals(2, container.size());
        // (mark), jane, julia
        container.removeAllContainerFilters();
        Assert.assertEquals(3, container.size());
    }

    @Test
    public void testAddAll() {
        BeanItemContainer<AbstractBeanContainerTestBase.Person> container = new BeanItemContainer<AbstractBeanContainerTestBase.Person>(AbstractBeanContainerTestBase.Person.class);
        AbstractBeanContainerTestBase.Person john = new AbstractBeanContainerTestBase.Person("John");
        AbstractBeanContainerTestBase.Person jane = new AbstractBeanContainerTestBase.Person("Jane");
        AbstractBeanContainerTestBase.Person matthew = new AbstractBeanContainerTestBase.Person("Matthew");
        container.addBean(john);
        container.addBean(jane);
        container.addBean(matthew);
        Assert.assertEquals(3, container.size());
        // john, jane, matthew
        AbstractBeanContainerTestBase.Person jack = new AbstractBeanContainerTestBase.Person("Jack");
        AbstractBeanContainerTestBase.Person michael = new AbstractBeanContainerTestBase.Person("Michael");
        // addAll
        container.addAll(Arrays.asList(jack, michael));
        // john, jane, matthew, jack, michael
        Assert.assertEquals(5, container.size());
        Assert.assertEquals(jane, container.nextItemId(john));
        Assert.assertEquals(matthew, container.nextItemId(jane));
        Assert.assertEquals(jack, container.nextItemId(matthew));
        Assert.assertEquals(michael, container.nextItemId(jack));
    }

    @Test
    public void testUnsupportedMethods() {
        BeanItemContainer<AbstractBeanContainerTestBase.Person> container = new BeanItemContainer<AbstractBeanContainerTestBase.Person>(AbstractBeanContainerTestBase.Person.class);
        container.addBean(new AbstractBeanContainerTestBase.Person("John"));
        try {
            container.addItem();
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
            container.addContainerProperty("lastName", String.class, "");
            Assert.fail();
        } catch (UnsupportedOperationException e) {
            // should get exception
        }
        Assert.assertEquals(1, container.size());
    }

    @Test
    public void testRemoveContainerProperty() {
        BeanItemContainer<AbstractBeanContainerTestBase.Person> container = new BeanItemContainer<AbstractBeanContainerTestBase.Person>(AbstractBeanContainerTestBase.Person.class);
        AbstractBeanContainerTestBase.Person john = new AbstractBeanContainerTestBase.Person("John");
        container.addBean(john);
        Assert.assertEquals("John", container.getContainerProperty(john, "name").getValue());
        Assert.assertTrue(container.removeContainerProperty("name"));
        Assert.assertNull(container.getContainerProperty(john, "name"));
        Assert.assertNotNull(container.getItem(john));
        // property removed also from item
        Assert.assertNull(container.getItem(john).getItemProperty("name"));
    }

    @Test
    public void testAddNullBean() {
        BeanItemContainer<AbstractBeanContainerTestBase.Person> container = new BeanItemContainer<AbstractBeanContainerTestBase.Person>(AbstractBeanContainerTestBase.Person.class);
        AbstractBeanContainerTestBase.Person john = new AbstractBeanContainerTestBase.Person("John");
        container.addBean(john);
        Assert.assertNull(container.addItem(null));
        Assert.assertNull(container.addItemAfter(null, null));
        Assert.assertNull(container.addItemAfter(john, null));
        Assert.assertNull(container.addItemAt(0, null));
        Assert.assertEquals(1, container.size());
    }

    @Test
    public void testBeanIdResolver() {
        BeanItemContainer<AbstractBeanContainerTestBase.Person> container = new BeanItemContainer<AbstractBeanContainerTestBase.Person>(AbstractBeanContainerTestBase.Person.class);
        AbstractBeanContainerTestBase.Person john = new AbstractBeanContainerTestBase.Person("John");
        Assert.assertSame(john, container.getBeanIdResolver().getIdForBean(john));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullBeanClass() {
        new BeanItemContainer<Object>(((Class<Object>) (null)));
    }

    @Test
    public void testAddNestedContainerProperty() {
        BeanItemContainer<NestedMethodPropertyTest.Person> container = new BeanItemContainer<NestedMethodPropertyTest.Person>(NestedMethodPropertyTest.Person.class);
        NestedMethodPropertyTest.Person john = new NestedMethodPropertyTest.Person("John", new NestedMethodPropertyTest.Address("Ruukinkatu 2-4", 20540));
        container.addBean(john);
        Assert.assertTrue(container.addNestedContainerProperty("address.street"));
        Assert.assertEquals("Ruukinkatu 2-4", container.getContainerProperty(john, "address.street").getValue());
    }

    @Test
    public void testNestedContainerPropertyWithNullBean() {
        BeanItemContainer<NestedMethodPropertyTest.Person> container = new BeanItemContainer<NestedMethodPropertyTest.Person>(NestedMethodPropertyTest.Person.class);
        NestedMethodPropertyTest.Person john = new NestedMethodPropertyTest.Person("John", null);
        Assert.assertNotNull(container.addBean(john));
        Assert.assertTrue(container.addNestedContainerProperty("address.postalCodeObject"));
        Assert.assertTrue(container.addNestedContainerProperty("address.street"));
        // the nested properties should return null
        Assert.assertNull(container.getContainerProperty(john, "address.street").getValue());
    }

    @Test
    public void testItemAddedEvent() {
        BeanItemContainer<AbstractBeanContainerTestBase.Person> container = new BeanItemContainer<AbstractBeanContainerTestBase.Person>(AbstractBeanContainerTestBase.Person.class);
        AbstractBeanContainerTestBase.Person bean = new AbstractBeanContainerTestBase.Person("John");
        ItemSetChangeListener addListener = createListenerMockFor(container);
        addListener.containerItemSetChange(EasyMock.isA(ItemAddEvent.class));
        EasyMock.replay(addListener);
        container.addItem(bean);
        EasyMock.verify(addListener);
    }

    @Test
    public void testItemAddedEvent_AddedItem() {
        BeanItemContainer<AbstractBeanContainerTestBase.Person> container = new BeanItemContainer<AbstractBeanContainerTestBase.Person>(AbstractBeanContainerTestBase.Person.class);
        AbstractBeanContainerTestBase.Person bean = new AbstractBeanContainerTestBase.Person("John");
        ItemSetChangeListener addListener = createListenerMockFor(container);
        Capture<ItemAddEvent> capturedEvent = captureAddEvent(addListener);
        EasyMock.replay(addListener);
        container.addItem(bean);
        Assert.assertEquals(bean, capturedEvent.getValue().getFirstItemId());
    }

    @Test
    public void testItemAddedEvent_addItemAt_IndexOfAddedItem() {
        BeanItemContainer<AbstractBeanContainerTestBase.Person> container = new BeanItemContainer<AbstractBeanContainerTestBase.Person>(AbstractBeanContainerTestBase.Person.class);
        AbstractBeanContainerTestBase.Person bean = new AbstractBeanContainerTestBase.Person("John");
        container.addItem(bean);
        ItemSetChangeListener addListener = createListenerMockFor(container);
        Capture<ItemAddEvent> capturedEvent = captureAddEvent(addListener);
        EasyMock.replay(addListener);
        container.addItemAt(1, new AbstractBeanContainerTestBase.Person(""));
        Assert.assertEquals(1, capturedEvent.getValue().getFirstIndex());
    }

    @Test
    public void testItemAddedEvent_addItemAfter_IndexOfAddedItem() {
        BeanItemContainer<AbstractBeanContainerTestBase.Person> container = new BeanItemContainer<AbstractBeanContainerTestBase.Person>(AbstractBeanContainerTestBase.Person.class);
        AbstractBeanContainerTestBase.Person bean = new AbstractBeanContainerTestBase.Person("John");
        container.addItem(bean);
        ItemSetChangeListener addListener = createListenerMockFor(container);
        Capture<ItemAddEvent> capturedEvent = captureAddEvent(addListener);
        EasyMock.replay(addListener);
        container.addItemAfter(bean, new AbstractBeanContainerTestBase.Person(""));
        Assert.assertEquals(1, capturedEvent.getValue().getFirstIndex());
    }

    @Test
    public void testItemAddedEvent_amountOfAddedItems() {
        BeanItemContainer<AbstractBeanContainerTestBase.Person> container = new BeanItemContainer<AbstractBeanContainerTestBase.Person>(AbstractBeanContainerTestBase.Person.class);
        ItemSetChangeListener addListener = createListenerMockFor(container);
        Capture<ItemAddEvent> capturedEvent = captureAddEvent(addListener);
        EasyMock.replay(addListener);
        List<AbstractBeanContainerTestBase.Person> beans = Arrays.asList(new AbstractBeanContainerTestBase.Person("Jack"), new AbstractBeanContainerTestBase.Person("John"));
        container.addAll(beans);
        Assert.assertEquals(2, capturedEvent.getValue().getAddedItemsCount());
    }

    @Test
    public void testItemAddedEvent_someItemsAreFiltered_amountOfAddedItemsIsReducedByAmountOfFilteredItems() {
        BeanItemContainer<AbstractBeanContainerTestBase.Person> container = new BeanItemContainer<AbstractBeanContainerTestBase.Person>(AbstractBeanContainerTestBase.Person.class);
        ItemSetChangeListener addListener = createListenerMockFor(container);
        Capture<ItemAddEvent> capturedEvent = captureAddEvent(addListener);
        EasyMock.replay(addListener);
        List<AbstractBeanContainerTestBase.Person> beans = Arrays.asList(new AbstractBeanContainerTestBase.Person("Jack"), new AbstractBeanContainerTestBase.Person("John"));
        container.addFilter(new Compare.Equal("name", "John"));
        container.addAll(beans);
        Assert.assertEquals(1, capturedEvent.getValue().getAddedItemsCount());
    }

    @Test
    public void testItemAddedEvent_someItemsAreFiltered_addedItemIsTheFirstVisibleItem() {
        BeanItemContainer<AbstractBeanContainerTestBase.Person> container = new BeanItemContainer<AbstractBeanContainerTestBase.Person>(AbstractBeanContainerTestBase.Person.class);
        AbstractBeanContainerTestBase.Person bean = new AbstractBeanContainerTestBase.Person("John");
        ItemSetChangeListener addListener = createListenerMockFor(container);
        Capture<ItemAddEvent> capturedEvent = captureAddEvent(addListener);
        EasyMock.replay(addListener);
        List<AbstractBeanContainerTestBase.Person> beans = Arrays.asList(new AbstractBeanContainerTestBase.Person("Jack"), bean);
        container.addFilter(new Compare.Equal("name", "John"));
        container.addAll(beans);
        Assert.assertEquals(bean, capturedEvent.getValue().getFirstItemId());
    }

    @Test
    public void testItemRemovedEvent() {
        BeanItemContainer<AbstractBeanContainerTestBase.Person> container = new BeanItemContainer<AbstractBeanContainerTestBase.Person>(AbstractBeanContainerTestBase.Person.class);
        AbstractBeanContainerTestBase.Person bean = new AbstractBeanContainerTestBase.Person("John");
        container.addItem(bean);
        ItemSetChangeListener removeListener = createListenerMockFor(container);
        removeListener.containerItemSetChange(EasyMock.isA(ItemRemoveEvent.class));
        EasyMock.replay(removeListener);
        container.removeItem(bean);
        EasyMock.verify(removeListener);
    }

    @Test
    public void testItemRemovedEvent_RemovedItem() {
        BeanItemContainer<AbstractBeanContainerTestBase.Person> container = new BeanItemContainer<AbstractBeanContainerTestBase.Person>(AbstractBeanContainerTestBase.Person.class);
        AbstractBeanContainerTestBase.Person bean = new AbstractBeanContainerTestBase.Person("John");
        container.addItem(bean);
        ItemSetChangeListener removeListener = createListenerMockFor(container);
        Capture<ItemRemoveEvent> capturedEvent = captureRemoveEvent(removeListener);
        EasyMock.replay(removeListener);
        container.removeItem(bean);
        Assert.assertEquals(bean, capturedEvent.getValue().getFirstItemId());
    }

    @Test
    public void testItemRemovedEvent_indexOfRemovedItem() {
        BeanItemContainer<AbstractBeanContainerTestBase.Person> container = new BeanItemContainer<AbstractBeanContainerTestBase.Person>(AbstractBeanContainerTestBase.Person.class);
        container.addItem(new AbstractBeanContainerTestBase.Person("Jack"));
        AbstractBeanContainerTestBase.Person secondBean = new AbstractBeanContainerTestBase.Person("John");
        container.addItem(secondBean);
        ItemSetChangeListener removeListener = createListenerMockFor(container);
        Capture<ItemRemoveEvent> capturedEvent = captureRemoveEvent(removeListener);
        EasyMock.replay(removeListener);
        container.removeItem(secondBean);
        Assert.assertEquals(1, capturedEvent.getValue().getFirstIndex());
    }

    @Test
    public void testItemRemovedEvent_amountOfRemovedItems() {
        BeanItemContainer<AbstractBeanContainerTestBase.Person> container = new BeanItemContainer<AbstractBeanContainerTestBase.Person>(AbstractBeanContainerTestBase.Person.class);
        container.addItem(new AbstractBeanContainerTestBase.Person("Jack"));
        container.addItem(new AbstractBeanContainerTestBase.Person("John"));
        ItemSetChangeListener removeListener = createListenerMockFor(container);
        Capture<ItemRemoveEvent> capturedEvent = captureRemoveEvent(removeListener);
        EasyMock.replay(removeListener);
        container.removeAllItems();
        Assert.assertEquals(2, capturedEvent.getValue().getRemovedItemsCount());
    }

    @Test
    public void testAddNestedContainerBeanBeforeData() {
        BeanItemContainer<NestedMethodPropertyTest.Person> container = new BeanItemContainer<NestedMethodPropertyTest.Person>(NestedMethodPropertyTest.Person.class);
        container.addNestedContainerBean("address");
        Assert.assertTrue(container.getContainerPropertyIds().contains("address.street"));
        NestedMethodPropertyTest.Person john = new NestedMethodPropertyTest.Person("John", new NestedMethodPropertyTest.Address("streetname", 12345));
        container.addBean(john);
        Assert.assertTrue(container.getItem(john).getItemPropertyIds().contains("address.street"));
        Assert.assertEquals("streetname", container.getItem(john).getItemProperty("address.street").getValue());
    }

    @Test
    public void testAddNestedContainerBeanAfterData() {
        BeanItemContainer<NestedMethodPropertyTest.Person> container = new BeanItemContainer<NestedMethodPropertyTest.Person>(NestedMethodPropertyTest.Person.class);
        NestedMethodPropertyTest.Person john = new NestedMethodPropertyTest.Person("John", new NestedMethodPropertyTest.Address("streetname", 12345));
        container.addBean(john);
        container.addNestedContainerBean("address");
        Assert.assertTrue(container.getContainerPropertyIds().contains("address.street"));
        Assert.assertTrue(container.getItem(john).getItemPropertyIds().contains("address.street"));
        Assert.assertEquals("streetname", container.getItem(john).getItemProperty("address.street").getValue());
    }
}

