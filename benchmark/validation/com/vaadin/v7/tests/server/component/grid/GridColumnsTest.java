package com.vaadin.v7.tests.server.component.grid;


import com.vaadin.server.KeyMapper;
import com.vaadin.shared.util.SharedUtil;
import com.vaadin.v7.shared.ui.grid.GridState;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.Column;
import com.vaadin.v7.ui.Grid.ColumnResizeEvent;
import com.vaadin.v7.ui.Grid.ColumnResizeListener;
import com.vaadin.v7.ui.TextField;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class GridColumnsTest {
    private Grid grid;

    private GridState state;

    private Method getStateMethod;

    private Field columnIdGeneratorField;

    private KeyMapper<Object> columnIdMapper;

    @Test
    public void testColumnGeneration() throws Exception {
        for (Object propertyId : grid.getContainerDataSource().getContainerPropertyIds()) {
            // All property ids should get a column
            Column column = grid.getColumn(propertyId);
            Assert.assertNotNull(column);
            // Humanized property id should be the column header by default
            Assert.assertEquals(SharedUtil.camelCaseToHumanFriendly(propertyId.toString()), grid.getDefaultHeaderRow().getCell(propertyId).getText());
        }
    }

    @Test
    public void testModifyingColumnProperties() throws Exception {
        // Modify first column
        Column column = grid.getColumn("column1");
        Assert.assertNotNull(column);
        column.setHeaderCaption("CustomHeader");
        Assert.assertEquals("CustomHeader", column.getHeaderCaption());
        Assert.assertEquals(column.getHeaderCaption(), grid.getDefaultHeaderRow().getCell("column1").getText());
        column.setWidth(100);
        Assert.assertEquals(100, column.getWidth(), 0.49);
        Assert.assertEquals(column.getWidth(), getColumnState("column1").width, 0.49);
        try {
            column.setWidth((-1));
            Assert.fail("Setting width to -1 should throw exception");
        } catch (IllegalArgumentException iae) {
            // expected
        }
        Assert.assertEquals(100, column.getWidth(), 0.49);
        Assert.assertEquals(100, getColumnState("column1").width, 0.49);
    }

    @Test
    public void testRemovingColumnByRemovingPropertyFromContainer() throws Exception {
        Column column = grid.getColumn("column1");
        Assert.assertNotNull(column);
        // Remove column
        grid.getContainerDataSource().removeContainerProperty("column1");
        try {
            column.setHeaderCaption("asd");
            Assert.fail("Succeeded in modifying a detached column");
        } catch (IllegalStateException ise) {
            // Detached state should throw exception
        }
        try {
            column.setWidth(123);
            Assert.fail("Succeeded in modifying a detached column");
        } catch (IllegalStateException ise) {
            // Detached state should throw exception
        }
        Assert.assertNull(grid.getColumn("column1"));
        Assert.assertNull(getColumnState("column1"));
    }

    @Test
    public void testAddingColumnByAddingPropertyToContainer() throws Exception {
        grid.getContainerDataSource().addContainerProperty("columnX", String.class, "");
        Column column = grid.getColumn("columnX");
        Assert.assertNotNull(column);
    }

    @Test
    public void testHeaderVisiblility() throws Exception {
        Assert.assertTrue(grid.isHeaderVisible());
        Assert.assertTrue(state.header.visible);
        grid.setHeaderVisible(false);
        Assert.assertFalse(grid.isHeaderVisible());
        Assert.assertFalse(state.header.visible);
        grid.setHeaderVisible(true);
        Assert.assertTrue(grid.isHeaderVisible());
        Assert.assertTrue(state.header.visible);
    }

    @Test
    public void testFooterVisibility() throws Exception {
        Assert.assertTrue(grid.isFooterVisible());
        Assert.assertTrue(state.footer.visible);
        grid.setFooterVisible(false);
        Assert.assertFalse(grid.isFooterVisible());
        Assert.assertFalse(state.footer.visible);
        grid.setFooterVisible(true);
        Assert.assertTrue(grid.isFooterVisible());
        Assert.assertTrue(state.footer.visible);
    }

    @Test
    public void testSetFrozenColumnCount() {
        Assert.assertEquals("Grid should not start with a frozen column", 0, grid.getFrozenColumnCount());
        grid.setFrozenColumnCount(2);
        Assert.assertEquals("Freezing two columns should freeze two columns", 2, grid.getFrozenColumnCount());
    }

    @Test
    public void testSetFrozenColumnCountThroughColumn() {
        Assert.assertEquals("Grid should not start with a frozen column", 0, grid.getFrozenColumnCount());
        grid.getColumns().get(2).setLastFrozenColumn();
        Assert.assertEquals("Setting the third column as last frozen should freeze three columns", 3, grid.getFrozenColumnCount());
    }

    @Test
    public void testFrozenColumnRemoveColumn() {
        Assert.assertEquals("Grid should not start with a frozen column", 0, grid.getFrozenColumnCount());
        int containerSize = grid.getContainerDataSource().getContainerPropertyIds().size();
        grid.setFrozenColumnCount(containerSize);
        Object propertyId = grid.getContainerDataSource().getContainerPropertyIds().iterator().next();
        grid.getContainerDataSource().removeContainerProperty(propertyId);
        Assert.assertEquals("Frozen column count should update when removing last row", (containerSize - 1), grid.getFrozenColumnCount());
    }

    @Test
    public void testReorderColumns() {
        Set<?> containerProperties = new LinkedHashSet<Object>(grid.getContainerDataSource().getContainerPropertyIds());
        Object[] properties = new Object[]{ "column3", "column2", "column6" };
        grid.setColumnOrder(properties);
        int i = 0;
        // Test sorted columns are first in order
        for (Object property : properties) {
            containerProperties.remove(property);
            Assert.assertEquals(columnIdMapper.key(property), state.columnOrder.get((i++)));
        }
        // Test remaining columns are in original order
        for (Object property : containerProperties) {
            Assert.assertEquals(columnIdMapper.key(property), state.columnOrder.get((i++)));
        }
        try {
            grid.setColumnOrder("foo", "bar", "baz");
            Assert.fail("Grid allowed sorting with non-existent properties");
        } catch (IllegalArgumentException e) {
            // All ok
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveColumnThatDoesNotExist() {
        grid.removeColumn("banana phone");
    }

    @Test(expected = IllegalStateException.class)
    public void testSetNonSortableColumnSortable() {
        Column noSortColumn = grid.getColumn("noSort");
        Assert.assertFalse("Object property column should not be sortable.", noSortColumn.isSortable());
        noSortColumn.setSortable(true);
    }

    @Test
    public void testColumnsEditableByDefault() {
        for (Column c : grid.getColumns()) {
            Assert.assertTrue((c + " should be editable"), c.isEditable());
        }
    }

    @Test
    public void testPropertyAndColumnEditorFieldsMatch() {
        Column column1 = grid.getColumn("column1");
        column1.setEditorField(new TextField());
        Assert.assertSame(column1.getEditorField(), grid.getColumn("column1").getEditorField());
        Column column2 = grid.getColumn("column2");
        column2.setEditorField(new TextField());
        Assert.assertSame(column2.getEditorField(), column2.getEditorField());
    }

    @Test
    public void testUneditableColumnHasNoField() {
        Column col = grid.getColumn("column1");
        col.setEditable(false);
        Assert.assertFalse("Column should be uneditable", col.isEditable());
        Assert.assertNull("Uneditable column should not be auto-assigned a Field", col.getEditorField());
    }

    @Test
    public void testAddAndRemoveSortableColumn() {
        boolean sortable = grid.getColumn("column1").isSortable();
        grid.removeColumn("column1");
        grid.addColumn("column1");
        Assert.assertEquals("Column sortability changed when re-adding", sortable, grid.getColumn("column1").isSortable());
    }

    @Test
    public void testSetColumns() {
        grid.setColumns("column7", "column0", "column9");
        Iterator<Column> it = grid.getColumns().iterator();
        Assert.assertEquals(it.next().getPropertyId(), "column7");
        Assert.assertEquals(it.next().getPropertyId(), "column0");
        Assert.assertEquals(it.next().getPropertyId(), "column9");
        Assert.assertFalse(it.hasNext());
    }

    @Test
    public void testAddingColumnsWithSetColumns() {
        Grid g = new Grid();
        g.setColumns("c1", "c2", "c3");
        Iterator<Column> it = g.getColumns().iterator();
        Assert.assertEquals(it.next().getPropertyId(), "c1");
        Assert.assertEquals(it.next().getPropertyId(), "c2");
        Assert.assertEquals(it.next().getPropertyId(), "c3");
        Assert.assertFalse(it.hasNext());
    }

    @Test(expected = IllegalStateException.class)
    public void testAddingColumnsWithSetColumnsNonDefaultContainer() {
        grid.setColumns("column1", "column2", "column50");
    }

    @Test
    public void testDefaultColumnHidingToggleCaption() {
        Column firstColumn = grid.getColumns().get(0);
        firstColumn.setHeaderCaption("headerCaption");
        Assert.assertEquals(null, firstColumn.getHidingToggleCaption());
    }

    @Test
    public void testOverriddenColumnHidingToggleCaption() {
        Column firstColumn = grid.getColumns().get(0);
        firstColumn.setHidingToggleCaption("hidingToggleCaption");
        firstColumn.setHeaderCaption("headerCaption");
        Assert.assertEquals("hidingToggleCaption", firstColumn.getHidingToggleCaption());
    }

    @Test
    public void testColumnSetWidthFiresResizeEvent() {
        final Column firstColumn = grid.getColumns().get(0);
        // prepare a listener mock that captures the argument
        ColumnResizeListener mock = EasyMock.createMock(ColumnResizeListener.class);
        Capture<ColumnResizeEvent> capturedEvent = new Capture<ColumnResizeEvent>();
        mock.columnResize(and(capture(capturedEvent), isA(ColumnResizeEvent.class)));
        EasyMock.expectLastCall().once();
        // Tell it to wait for the call
        EasyMock.replay(mock);
        // Cause a resize event
        grid.addColumnResizeListener(mock);
        firstColumn.setWidth(((firstColumn.getWidth()) + 10));
        // Verify the method was called
        EasyMock.verify(mock);
        // Asserts on the captured event
        ColumnResizeEvent event = capturedEvent.getValue();
        Assert.assertEquals("Event column was not first column.", firstColumn, event.getColumn());
        Assert.assertFalse("Event should not be userOriginated", event.isUserOriginated());
    }

    @Test
    public void textHeaderCaptionIsReturned() {
        Column firstColumn = grid.getColumns().get(0);
        firstColumn.setHeaderCaption("text");
        MatcherAssert.assertThat(firstColumn.getHeaderCaption(), Is.is("text"));
    }

    @Test
    public void defaultCaptionIsReturnedForHtml() {
        Column firstColumn = grid.getColumns().get(0);
        grid.getDefaultHeaderRow().getCell("column0").setHtml("<b>html</b>");
        MatcherAssert.assertThat(firstColumn.getHeaderCaption(), Is.is("Column0"));
    }

    @Test(expected = IllegalStateException.class)
    public void addColumnManyTimes() {
        grid.removeAllColumns();
        grid.addColumn("column0");
        grid.addColumn("column0");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setColumnDuplicates() {
        grid.removeAllColumns();
        grid.setColumns("column0", "column0");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setColumnOrderDuplicates() {
        grid.setColumnOrder("column0", "column0");
    }
}

