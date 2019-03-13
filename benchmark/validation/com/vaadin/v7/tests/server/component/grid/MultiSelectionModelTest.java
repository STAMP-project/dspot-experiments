package com.vaadin.v7.tests.server.component.grid;


import Container.Indexed;
import com.vaadin.ui.ComponentTest;
import com.vaadin.v7.shared.ui.grid.selection.MultiSelectionModelServerRpc;
import com.vaadin.v7.shared.ui.grid.selection.MultiSelectionModelState;
import com.vaadin.v7.ui.Grid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import static com.vaadin.v7.ui.Grid.MultiSelectionModel.<init>;


public class MultiSelectionModelTest {
    private static class MultiSelectionModel extends Grid.MultiSelectionModel {
        @Override
        protected MultiSelectionModelState getState() {
            // Overridden to be accessible from test
            return super.getState();
        }
    }

    private Object itemId1Present = "itemId1Present";

    private Object itemId2Present = "itemId2Present";

    private Object itemId3Present = "itemId3Present";

    private Object itemIdNotPresent = "itemIdNotPresent";

    private Indexed dataSource;

    private MultiSelectionModelTest.MultiSelectionModel model;

    private Grid grid;

    private boolean expectingEvent = false;

    private boolean expectingDeselectEvent;

    private List<Object> select = new ArrayList<Object>();

    private List<Object> deselect = new ArrayList<Object>();

    @Test
    public void testSelectAndDeselectRow() throws Throwable {
        try {
            expectSelectEvent(itemId1Present);
            model.select(itemId1Present);
            expectDeselectEvent(itemId1Present);
            model.deselect(itemId1Present);
        } catch (Exception e) {
            throw e.getCause();
        }
        verifyCurrentSelection();
    }

    @Test
    public void testAddSelection() throws Throwable {
        try {
            expectSelectEvent(itemId1Present);
            model.select(itemId1Present);
            expectSelectEvent(itemId2Present);
            model.select(itemId2Present);
        } catch (Exception e) {
            throw e.getCause();
        }
        verifyCurrentSelection(itemId1Present, itemId2Present);
    }

    @Test
    public void testSelectAllWithoutItems() throws Throwable {
        Assert.assertFalse(model.getState().allSelected);
        dataSource.removeAllItems();
        Assert.assertFalse(model.getState().allSelected);
        select();
        Assert.assertFalse(model.getState().allSelected);
        deselect();
        Assert.assertFalse(model.getState().allSelected);
    }

    @Test
    public void testSettingSelection() throws Throwable {
        try {
            expectSelectEvent(itemId2Present, itemId1Present);
            setSelected(Arrays.asList(new Object[]{ itemId1Present, itemId2Present }));
            verifyCurrentSelection(itemId1Present, itemId2Present);
            expectDeselectEvent(itemId1Present);
            expectSelectEvent(itemId3Present);
            setSelected(Arrays.asList(new Object[]{ itemId3Present, itemId2Present }));
            verifyCurrentSelection(itemId3Present, itemId2Present);
        } catch (Exception e) {
            throw e.getCause();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void refuseSelectWhenUserSelectionDisallowed() {
        setUserSelectionAllowed(false);
        MultiSelectionModelServerRpc serverRpc = ComponentTest.getRpcProxy(grid.getSelectionModel(), MultiSelectionModelServerRpc.class);
        serverRpc.select(Collections.singletonList("a"));
    }

    @Test(expected = IllegalStateException.class)
    public void refuseDeselectWhenUserSelectionDisallowed() {
        setUserSelectionAllowed(false);
        MultiSelectionModelServerRpc serverRpc = ComponentTest.getRpcProxy(grid.getSelectionModel(), MultiSelectionModelServerRpc.class);
        serverRpc.deselect(Collections.singletonList("a"));
    }

    @Test(expected = IllegalStateException.class)
    public void refuseSelectAllWhenUserSelectionDisallowed() {
        setUserSelectionAllowed(false);
        MultiSelectionModelServerRpc serverRpc = ComponentTest.getRpcProxy(grid.getSelectionModel(), MultiSelectionModelServerRpc.class);
        serverRpc.selectAll();
    }

    @Test(expected = IllegalStateException.class)
    public void refuseDeselectAllWhenUserSelectionDisallowed() {
        setUserSelectionAllowed(false);
        MultiSelectionModelServerRpc serverRpc = ComponentTest.getRpcProxy(grid.getSelectionModel(), MultiSelectionModelServerRpc.class);
        serverRpc.deselectAll();
    }
}

