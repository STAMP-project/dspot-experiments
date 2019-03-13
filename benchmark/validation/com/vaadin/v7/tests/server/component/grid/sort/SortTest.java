package com.vaadin.v7.tests.server.component.grid.sort;


import SortDirection.DESCENDING;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.v7.data.sort.Sort;
import com.vaadin.v7.data.sort.SortOrder;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.event.SortEvent;
import com.vaadin.v7.event.SortEvent.SortListener;
import com.vaadin.v7.ui.Grid;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class SortTest {
    class DummySortingIndexedContainer extends IndexedContainer {
        private Object[] expectedProperties;

        private boolean[] expectedAscending;

        private boolean sorted = true;

        @Override
        public void sort(Object[] propertyId, boolean[] ascending) {
            Assert.assertEquals("Different amount of expected and actual properties,", expectedProperties.length, propertyId.length);
            Assert.assertEquals("Different amount of expected and actual directions", expectedAscending.length, ascending.length);
            for (int i = 0; i < (propertyId.length); ++i) {
                Assert.assertEquals("Sorting properties differ", expectedProperties[i], propertyId[i]);
                Assert.assertEquals("Sorting directions differ", expectedAscending[i], ascending[i]);
            }
            sorted = true;
        }

        public void expectedSort(Object[] properties, SortDirection[] directions) {
            assert (directions.length) == (properties.length) : "Array dimensions differ";
            expectedProperties = properties;
            expectedAscending = new boolean[directions.length];
            for (int i = 0; i < (directions.length); ++i) {
                expectedAscending[i] = (directions[i]) == (SortDirection.ASCENDING);
            }
            sorted = false;
        }

        public boolean isSorted() {
            return sorted;
        }
    }

    class RegisteringSortChangeListener implements SortListener {
        private List<SortOrder> order;

        @Override
        public void sort(SortEvent event) {
            assert (order) == null : "The same listener was notified multipe times without checking";
            order = event.getSortOrder();
        }

        public void assertEventFired(SortOrder... expectedOrder) {
            Assert.assertEquals(Arrays.asList(expectedOrder), order);
            // Reset for nest test
            order = null;
        }
    }

    private SortTest.DummySortingIndexedContainer container;

    private SortTest.RegisteringSortChangeListener listener;

    private Grid grid;

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidSortDirection() {
        Sort.by("foo", null);
    }

    @Test(expected = IllegalStateException.class)
    public void testSortOneColumnMultipleTimes() {
        Sort.by("foo").then("bar").then("foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSortingByUnexistingProperty() {
        grid.sort("foobar");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSortingByUnsortableProperty() {
        addContainerProperty("foobar", Object.class, null);
        grid.sort("foobar");
    }

    @Test
    public void testGridDirectSortAscending() {
        container.expectedSort(new Object[]{ "foo" }, new SortDirection[]{ SortDirection.ASCENDING });
        grid.sort("foo");
        listener.assertEventFired(new SortOrder("foo", SortDirection.ASCENDING));
    }

    @Test
    public void testGridDirectSortDescending() {
        container.expectedSort(new Object[]{ "foo" }, new SortDirection[]{ SortDirection.DESCENDING });
        grid.sort("foo", DESCENDING);
        listener.assertEventFired(new SortOrder("foo", SortDirection.DESCENDING));
    }

    @Test
    public void testGridSortBy() {
        container.expectedSort(new Object[]{ "foo", "bar", "baz" }, new SortDirection[]{ SortDirection.ASCENDING, SortDirection.ASCENDING, SortDirection.DESCENDING });
        grid.sort(Sort.by("foo").then("bar").then("baz", DESCENDING));
        listener.assertEventFired(new SortOrder("foo", SortDirection.ASCENDING), new SortOrder("bar", SortDirection.ASCENDING), new SortOrder("baz", SortDirection.DESCENDING));
    }

    @Test
    public void testChangeContainerAfterSorting() {
        class Person {}
        container.expectedSort(new Object[]{ "foo", "bar", "baz" }, new SortDirection[]{ SortDirection.ASCENDING, SortDirection.ASCENDING, SortDirection.DESCENDING });
        grid.sort(Sort.by("foo").then("bar").then("baz", DESCENDING));
        listener.assertEventFired(new SortOrder("foo", SortDirection.ASCENDING), new SortOrder("bar", SortDirection.ASCENDING), new SortOrder("baz", SortDirection.DESCENDING));
        container = new SortTest.DummySortingIndexedContainer();
        addContainerProperty("foo", Person.class, null);
        container.addContainerProperty("baz", String.class, "");
        addContainerProperty("bar", Person.class, null);
        container.expectedSort(new Object[]{ "baz" }, new SortDirection[]{ SortDirection.DESCENDING });
        grid.setContainerDataSource(container);
        listener.assertEventFired(new SortOrder("baz", SortDirection.DESCENDING));
    }
}

