package com.vaadin.v7.tests.server.component.grid;


import Container.Indexed;
import Grid.Column;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Grid;
import org.junit.Assert;
import org.junit.Test;


public class GridColumnAddingAndRemovingTest {
    Grid grid = new Grid();

    Indexed container;

    @Test
    public void testAddColumn() {
        grid.addColumn("foo");
        Property<?> property = container.getContainerProperty(container.firstItemId(), "foo");
        Assert.assertEquals(property.getType(), String.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testAddColumnTwice() {
        grid.addColumn("foo");
        grid.addColumn("foo");
    }

    @Test
    public void testAddRemoveAndAddAgainColumn() {
        grid.addColumn("foo");
        grid.removeColumn("foo");
        // Removing a column, doesn't remove the property
        Property<?> property = container.getContainerProperty(container.firstItemId(), "foo");
        Assert.assertEquals(property.getType(), String.class);
        grid.addColumn("foo");
    }

    @Test
    public void testAddNumberColumns() {
        grid.addColumn("bar", Integer.class);
        grid.addColumn("baz", Double.class);
        Property<?> property = container.getContainerProperty(container.firstItemId(), "bar");
        Assert.assertEquals(property.getType(), Integer.class);
        Assert.assertEquals(null, property.getValue());
        property = container.getContainerProperty(container.firstItemId(), "baz");
        Assert.assertEquals(property.getType(), Double.class);
        Assert.assertEquals(null, property.getValue());
    }

    @Test(expected = IllegalStateException.class)
    public void testAddDifferentTypeColumn() {
        grid.addColumn("foo");
        grid.removeColumn("foo");
        grid.addColumn("foo", Integer.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testAddColumnToNonDefaultContainer() {
        grid.setContainerDataSource(new IndexedContainer());
        grid.addColumn("foo");
    }

    @Test
    public void testAddColumnForExistingProperty() {
        grid.addColumn("bar");
        IndexedContainer container2 = new IndexedContainer();
        container2.addContainerProperty("foo", Integer.class, 0);
        container2.addContainerProperty("bar", String.class, "");
        grid.setContainerDataSource(container2);
        Assert.assertNull("Grid should not have a column for property foo", grid.getColumn("foo"));
        Assert.assertNotNull("Grid did should have a column for property bar", grid.getColumn("bar"));
        for (Grid.Column column : grid.getColumns()) {
            Assert.assertNotNull("Grid getColumns returned a null value", column);
        }
        grid.removeAllColumns();
        grid.addColumn("foo");
        Assert.assertNotNull("Grid should now have a column for property foo", grid.getColumn("foo"));
        Assert.assertNull("Grid should not have a column for property bar anymore", grid.getColumn("bar"));
    }

    @Test(expected = IllegalStateException.class)
    public void testAddIncompatibleColumnProperty() {
        grid.addColumn("bar");
        grid.removeAllColumns();
        grid.addColumn("bar", Integer.class);
    }

    @Test
    public void testAddBooleanColumnProperty() {
        grid.addColumn("foo", Boolean.class);
        Property<?> property = container.getContainerProperty(container.firstItemId(), "foo");
        Assert.assertEquals(property.getType(), Boolean.class);
        Assert.assertEquals(property.getValue(), null);
    }
}

