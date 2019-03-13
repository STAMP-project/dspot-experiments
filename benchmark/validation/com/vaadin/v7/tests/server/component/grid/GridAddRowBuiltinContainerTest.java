package com.vaadin.v7.tests.server.component.grid;


import Container.Indexed;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.data.util.MethodProperty.MethodException;
import com.vaadin.v7.ui.Grid;
import org.junit.Assert;
import org.junit.Test;


public class GridAddRowBuiltinContainerTest {
    Grid grid = new Grid();

    Indexed container;

    @Test
    public void testSimpleCase() {
        Object itemId = grid.addRow("Hello");
        Assert.assertEquals(Integer.valueOf(1), itemId);
        Assert.assertEquals("There should be one item in the container", 1, container.size());
        Assert.assertEquals("Hello", container.getItem(itemId).getItemProperty("myColumn").getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullParameter() {
        // cast to Object[] to distinguish from one null varargs value
        grid.addRow(((Object[]) (null)));
    }

    @Test
    public void testNullValue() {
        // cast to Object to distinguish from a null varargs array
        Object itemId = grid.addRow(((Object) (null)));
        Assert.assertEquals(null, container.getItem(itemId).getItemProperty("myColumn").getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddInvalidType() {
        grid.addRow(Integer.valueOf(5));
    }

    @Test
    public void testMultipleProperties() {
        grid.addColumn("myOther", Integer.class);
        Object itemId = grid.addRow("Hello", Integer.valueOf(3));
        Item item = container.getItem(itemId);
        Assert.assertEquals("Hello", item.getItemProperty("myColumn").getValue());
        Assert.assertEquals(Integer.valueOf(3), item.getItemProperty("myOther").getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPropertyAmount() {
        grid.addRow("Hello", Integer.valueOf(3));
    }

    @Test
    public void testRemovedColumn() {
        grid.addColumn("myOther", Integer.class);
        grid.removeColumn("myColumn");
        grid.addRow(Integer.valueOf(3));
        Item item = container.getItem(Integer.valueOf(1));
        Assert.assertEquals("Default value should be used for removed column", "", item.getItemProperty("myColumn").getValue());
        Assert.assertEquals(Integer.valueOf(3), item.getItemProperty("myOther").getValue());
    }

    @Test
    public void testMultiplePropertiesAfterReorder() {
        grid.addColumn("myOther", Integer.class);
        grid.setColumnOrder("myOther", "myColumn");
        grid.addRow(Integer.valueOf(3), "Hello");
        Item item = container.getItem(Integer.valueOf(1));
        Assert.assertEquals("Hello", item.getItemProperty("myColumn").getValue());
        Assert.assertEquals(Integer.valueOf(3), item.getItemProperty("myOther").getValue());
    }

    @Test
    public void testInvalidType_NothingAdded() {
        try {
            grid.addRow(Integer.valueOf(5));
            // Can't use @Test(expect = Foo.class) since we also want to verify
            // state after exception was thrown
            Assert.fail("Adding wrong type should throw ClassCastException");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("No row should have been added", 0, container.size());
        }
    }

    @Test
    public void testUnsupportingContainer() {
        setContainerRemoveColumns(new com.vaadin.v7.data.util.BeanItemContainer<Person>(Person.class));
        try {
            grid.addRow("name");
            // Can't use @Test(expect = Foo.class) since we also want to verify
            // state after exception was thrown
            Assert.fail("Adding to BeanItemContainer container should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            Assert.assertEquals("No row should have been added", 0, container.size());
        }
    }

    @Test
    public void testCustomContainer() {
        com.vaadin.v7.data.util.BeanItemContainer<Person> container = new com.vaadin.v7.data.util.BeanItemContainer<Person>(Person.class) {
            @Override
            public Object addItem() {
                BeanItem<Person> item = addBean(new Person());
                return getBeanIdResolver().getIdForBean(item.getBean());
            }
        };
        setContainerRemoveColumns(container);
        grid.addRow("name");
        Assert.assertEquals(1, container.size());
        Assert.assertEquals("name", container.getIdByIndex(0).getFirstName());
    }

    @Test
    public void testSetterThrowing() {
        com.vaadin.v7.data.util.BeanItemContainer<Person> container = new com.vaadin.v7.data.util.BeanItemContainer<Person>(Person.class) {
            @Override
            public Object addItem() {
                BeanItem<Person> item = addBean(new Person() {
                    @Override
                    public void setFirstName(String firstName) {
                        if ("name".equals(firstName)) {
                            throw new RuntimeException(firstName);
                        } else {
                            super.setFirstName(firstName);
                        }
                    }
                });
                return getBeanIdResolver().getIdForBean(item.getBean());
            }
        };
        setContainerRemoveColumns(container);
        try {
            grid.addRow("name");
            // Can't use @Test(expect = Foo.class) since we also want to verify
            // state after exception was thrown
            Assert.fail("Adding row should throw MethodException");
        } catch (MethodException e) {
            Assert.assertEquals("Got the wrong exception", "name", e.getCause().getMessage());
            Assert.assertEquals("There should be no rows in the container", 0, container.size());
        }
    }
}

