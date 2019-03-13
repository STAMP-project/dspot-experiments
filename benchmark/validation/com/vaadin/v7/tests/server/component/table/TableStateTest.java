package com.vaadin.v7.tests.server.component.table;


import com.vaadin.v7.shared.ui.table.TableState;
import com.vaadin.v7.ui.Table;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for Table State.
 */
public class TableStateTest {
    @Test
    public void getState_tableHasCustomState() {
        TableStateTest.TestTable table = new TableStateTest.TestTable();
        TableState state = table.getState();
        Assert.assertEquals("Unexpected state class", TableState.class, state.getClass());
    }

    @Test
    public void getPrimaryStyleName_tableHasCustomPrimaryStyleName() {
        Table table = new Table();
        TableState state = new TableState();
        Assert.assertEquals("Unexpected primary style name", state.primaryStyleName, table.getPrimaryStyleName());
    }

    @Test
    public void tableStateHasCustomPrimaryStyleName() {
        TableState state = new TableState();
        Assert.assertEquals("Unexpected primary style name", "v-table", state.primaryStyleName);
    }

    private static class TestTable extends Table {
        @Override
        public TableState getState() {
            return super.getState();
        }
    }
}

