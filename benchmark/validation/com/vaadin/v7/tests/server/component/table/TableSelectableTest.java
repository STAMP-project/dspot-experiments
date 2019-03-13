package com.vaadin.v7.tests.server.component.table;


import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.ui.Table;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for 'selectable' property of {@link Table} class.
 *
 * @author Vaadin Ltd
 */
public class TableSelectableTest {
    @Test
    public void setSelectable_explicitSelectable_tableIsSelectable() {
        Table table = new Table();
        table.setSelectable(true);
        Assert.assertTrue(table.isSelectable());
    }

    @Test
    public void addValueChangeListener_explicitSelectable_tableIsSelectable() {
        TableSelectableTest.TestTable table = new TableSelectableTest.TestTable();
        table.addValueChangeListener(EasyMock.createMock(ValueChangeListener.class));
        Assert.assertTrue(isSelectable());
        Assert.assertTrue(table.markAsDirtyCalled);
    }

    @Test
    public void tableIsNotSelectableByDefult() {
        Table table = new Table();
        Assert.assertFalse(table.isSelectable());
    }

    @Test
    public void setSelectable_explicitNotSelectable_tableIsNotSelectable() {
        Table table = new Table();
        table.setSelectable(false);
        table.addValueChangeListener(EasyMock.createMock(ValueChangeListener.class));
        Assert.assertFalse(table.isSelectable());
    }

    private static final class TestTable extends Table {
        @Override
        public void markAsDirty() {
            markAsDirtyCalled = true;
        }

        private boolean markAsDirtyCalled;
    }
}

