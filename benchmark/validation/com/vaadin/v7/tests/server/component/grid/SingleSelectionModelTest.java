package com.vaadin.v7.tests.server.component.grid;


import Container.Indexed;
import com.vaadin.ui.ComponentTest;
import com.vaadin.v7.shared.ui.grid.selection.SingleSelectionModelServerRpc;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.SingleSelectionModel;
import org.junit.Assert;
import org.junit.Test;


public class SingleSelectionModelTest {
    private Object itemId1Present = "itemId1Present";

    private Object itemId2Present = "itemId2Present";

    private Object itemIdNotPresent = "itemIdNotPresent";

    private Indexed dataSource;

    private SingleSelectionModel model;

    private Grid grid;

    private boolean expectingEvent = false;

    @Test
    public void testSelectAndDeselctRow() throws Throwable {
        try {
            expectEvent(itemId1Present, null);
            model.select(itemId1Present);
            expectEvent(null, itemId1Present);
            model.select(null);
        } catch (Exception e) {
            throw e.getCause();
        }
    }

    @Test
    public void testSelectAndChangeSelectedRow() throws Throwable {
        try {
            expectEvent(itemId1Present, null);
            model.select(itemId1Present);
            expectEvent(itemId2Present, itemId1Present);
            model.select(itemId2Present);
        } catch (Exception e) {
            throw e.getCause();
        }
    }

    @Test
    public void testRemovingSelectedRowAndThenDeselecting() throws Throwable {
        try {
            expectEvent(itemId2Present, null);
            model.select(itemId2Present);
            dataSource.removeItem(itemId2Present);
            expectEvent(null, itemId2Present);
            model.select(null);
        } catch (Exception e) {
            throw e.getCause();
        }
    }

    @Test
    public void testSelectAndReSelectRow() throws Throwable {
        try {
            expectEvent(itemId1Present, null);
            model.select(itemId1Present);
            expectEvent(null, null);
            // This is no-op. Nothing should happen.
            model.select(itemId1Present);
        } catch (Exception e) {
            throw e.getCause();
        }
        Assert.assertTrue("Should still wait for event", expectingEvent);
        expectingEvent = false;
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSelectNonExistentRow() {
        model.select(itemIdNotPresent);
    }

    @Test(expected = IllegalStateException.class)
    public void refuseSelectionWhenUserSelectionDisallowed() {
        setUserSelectionAllowed(false);
        SingleSelectionModelServerRpc serverRpc = ComponentTest.getRpcProxy(grid.getSelectionModel(), SingleSelectionModelServerRpc.class);
        serverRpc.select("a");
    }
}

