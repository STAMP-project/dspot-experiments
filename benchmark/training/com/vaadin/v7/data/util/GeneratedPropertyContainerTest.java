package com.vaadin.v7.data.util;


import com.vaadin.v7.data.Container.Filter;
import com.vaadin.v7.data.Container.Indexed;
import com.vaadin.v7.data.Container.ItemSetChangeEvent;
import com.vaadin.v7.data.Container.ItemSetChangeListener;
import com.vaadin.v7.data.Container.PropertySetChangeEvent;
import com.vaadin.v7.data.Container.PropertySetChangeListener;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.sort.SortOrder;
import com.vaadin.v7.data.util.GeneratedPropertyContainer.GeneratedPropertyItem;
import com.vaadin.v7.data.util.filter.Compare;
import com.vaadin.v7.data.util.filter.UnsupportedFilterException;
import org.junit.Assert;
import org.junit.Test;


public class GeneratedPropertyContainerTest {
    GeneratedPropertyContainer container;

    Indexed wrappedContainer;

    private static double MILES_CONVERSION = 0.6214;

    private class GeneratedPropertyListener implements PropertySetChangeListener {
        private int callCount = 0;

        public int getCallCount() {
            return callCount;
        }

        @Override
        public void containerPropertySetChange(PropertySetChangeEvent event) {
            ++(callCount);
            Assert.assertEquals("Container for event was not GeneratedPropertyContainer", event.getContainer(), container);
        }
    }

    private class GeneratedItemSetListener implements ItemSetChangeListener {
        private int callCount = 0;

        public int getCallCount() {
            return callCount;
        }

        @Override
        public void containerItemSetChange(ItemSetChangeEvent event) {
            ++(callCount);
            Assert.assertEquals("Container for event was not GeneratedPropertyContainer", event.getContainer(), container);
        }
    }

    @Test
    public void testSimpleGeneratedProperty() {
        container.addGeneratedProperty("hello", new PropertyValueGenerator<String>() {
            @Override
            public String getValue(Item item, Object itemId, Object propertyId) {
                return "Hello World!";
            }

            @Override
            public Class<String> getType() {
                return String.class;
            }
        });
        Object itemId = container.addItem();
        Assert.assertEquals("Expected value not in item.", container.getItem(itemId).getItemProperty("hello").getValue(), "Hello World!");
    }

    @Test
    public void testSortableProperties() {
        container.addGeneratedProperty("baz", new PropertyValueGenerator<String>() {
            @Override
            public String getValue(Item item, Object itemId, Object propertyId) {
                return ((item.getItemProperty("foo").getValue()) + " ") + (item.getItemProperty("bar").getValue());
            }

            @Override
            public Class<String> getType() {
                return String.class;
            }

            @Override
            public SortOrder[] getSortProperties(SortOrder order) {
                SortOrder[] sortOrder = new SortOrder[1];
                sortOrder[0] = new SortOrder("bar", order.getDirection());
                return sortOrder;
            }
        });
        container.sort(new Object[]{ "baz" }, new boolean[]{ true });
        Assert.assertEquals("foo 0", container.getItem(container.getIdByIndex(0)).getItemProperty("baz").getValue());
        container.sort(new Object[]{ "baz" }, new boolean[]{ false });
        Assert.assertEquals("foo 10", container.getItem(container.getIdByIndex(0)).getItemProperty("baz").getValue());
    }

    @Test
    public void testOverrideSortableProperties() {
        Assert.assertTrue(container.getSortableContainerPropertyIds().contains("bar"));
        container.addGeneratedProperty("bar", new PropertyValueGenerator<String>() {
            @Override
            public String getValue(Item item, Object itemId, Object propertyId) {
                return ((item.getItemProperty("foo").getValue()) + " ") + (item.getItemProperty("bar").getValue());
            }

            @Override
            public Class<String> getType() {
                return String.class;
            }
        });
        Assert.assertFalse(container.getSortableContainerPropertyIds().contains("bar"));
    }

    @Test
    public void testFilterByMiles() {
        container.addGeneratedProperty("miles", new PropertyValueGenerator<Double>() {
            @Override
            public Double getValue(Item item, Object itemId, Object propertyId) {
                return ((Double) (item.getItemProperty("km").getValue())) * (GeneratedPropertyContainerTest.MILES_CONVERSION);
            }

            @Override
            public Class<Double> getType() {
                return Double.class;
            }

            @Override
            public Filter modifyFilter(Filter filter) throws UnsupportedFilterException {
                if (filter instanceof Compare.LessOrEqual) {
                    Double value = ((Double) (((Compare.LessOrEqual) (filter)).getValue()));
                    value = value / (GeneratedPropertyContainerTest.MILES_CONVERSION);
                    return new Compare.LessOrEqual("km", value);
                }
                return super.modifyFilter(filter);
            }
        });
        for (Object itemId : container.getItemIds()) {
            Item item = container.getItem(itemId);
            Double km = ((Double) (item.getItemProperty("km").getValue()));
            Double miles = ((Double) (item.getItemProperty("miles").getValue()));
            Assert.assertTrue(miles.equals((km * (GeneratedPropertyContainerTest.MILES_CONVERSION))));
        }
        Filter filter = new Compare.LessOrEqual("miles", GeneratedPropertyContainerTest.MILES_CONVERSION);
        container.addContainerFilter(filter);
        for (Object itemId : container.getItemIds()) {
            Item item = container.getItem(itemId);
            Assert.assertTrue("Item did not pass original filter.", filter.passesFilter(itemId, item));
        }
        Assert.assertTrue(container.getContainerFilters().contains(filter));
        container.removeContainerFilter(filter);
        Assert.assertFalse(container.getContainerFilters().contains(filter));
        boolean allPass = true;
        for (Object itemId : container.getItemIds()) {
            Item item = container.getItem(itemId);
            if (!(filter.passesFilter(itemId, item))) {
                allPass = false;
            }
        }
        if (allPass) {
            Assert.fail("Removing filter did not introduce any previous filtered items");
        }
    }

    @Test
    public void testPropertySetChangeNotifier() {
        GeneratedPropertyContainerTest.GeneratedPropertyListener listener = new GeneratedPropertyContainerTest.GeneratedPropertyListener();
        GeneratedPropertyContainerTest.GeneratedPropertyListener removedListener = new GeneratedPropertyContainerTest.GeneratedPropertyListener();
        container.addPropertySetChangeListener(listener);
        container.addPropertySetChangeListener(removedListener);
        container.addGeneratedProperty("foo", new PropertyValueGenerator<String>() {
            @Override
            public String getValue(Item item, Object itemId, Object propertyId) {
                return "";
            }

            @Override
            public Class<String> getType() {
                return String.class;
            }
        });
        // Adding property to wrapped container should cause an event
        wrappedContainer.addContainerProperty("baz", String.class, "");
        container.removePropertySetChangeListener(removedListener);
        container.removeGeneratedProperty("foo");
        Assert.assertEquals("Listener was not called correctly.", 3, listener.getCallCount());
        Assert.assertEquals("Removed listener was not called correctly.", 2, removedListener.getCallCount());
    }

    @Test
    public void testItemSetChangeNotifier() {
        GeneratedPropertyContainerTest.GeneratedItemSetListener listener = new GeneratedPropertyContainerTest.GeneratedItemSetListener();
        container.addItemSetChangeListener(listener);
        container.sort(new Object[]{ "foo" }, new boolean[]{ true });
        container.sort(new Object[]{ "foo" }, new boolean[]{ false });
        Assert.assertEquals("Listener was not called correctly.", 2, listener.getCallCount());
    }

    @Test
    public void testRemoveProperty() {
        container.removeContainerProperty("foo");
        Assert.assertFalse("Container contained removed property", container.getContainerPropertyIds().contains("foo"));
        Assert.assertTrue("Wrapped container did not contain removed property", wrappedContainer.getContainerPropertyIds().contains("foo"));
        Assert.assertFalse(container.getItem(container.firstItemId()).getItemPropertyIds().contains("foo"));
        container.addContainerProperty("foo", null, null);
        Assert.assertTrue("Container did not contain returned property", container.getContainerPropertyIds().contains("foo"));
    }

    @Test
    public void testGetWrappedItem() {
        Object itemId = wrappedContainer.getItemIds().iterator().next();
        Item wrappedItem = wrappedContainer.getItem(itemId);
        GeneratedPropertyItem generatedPropertyItem = ((GeneratedPropertyItem) (container.getItem(itemId)));
        Assert.assertEquals(wrappedItem, generatedPropertyItem.getWrappedItem());
    }
}

