package com.vaadin.v7.tests.server.component.grid;


import SelectionMode.MULTI;
import SelectionMode.NONE;
import SelectionMode.SINGLE;
import SelectionModel.Multi;
import SelectionModel.Single;
import com.vaadin.v7.event.SelectionEvent;
import com.vaadin.v7.event.SelectionEvent.SelectionListener;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.SelectionModel;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;


public class GridSelectionTest {
    private static class MockSelectionChangeListener implements SelectionListener {
        private SelectionEvent event;

        @Override
        public void select(final SelectionEvent event) {
            this.event = event;
        }

        public Collection<?> getAdded() {
            return event.getAdded();
        }

        public Collection<?> getRemoved() {
            return event.getRemoved();
        }

        public void clearEvent() {
            /* This method is not strictly needed as the event will simply be
            overridden, but it's good practice, and makes the code more
            obvious.
             */
            event = null;
        }

        public boolean eventHasHappened() {
            return (event) != null;
        }
    }

    private Grid grid;

    private GridSelectionTest.MockSelectionChangeListener mockListener;

    private final Object itemId1Present = "itemId1Present";

    private final Object itemId2Present = "itemId2Present";

    private final Object itemId1NotPresent = "itemId1NotPresent";

    private final Object itemId2NotPresent = "itemId2NotPresent";

    @Test
    public void defaultSelectionModeIsSingle() {
        Assert.assertTrue(((grid.getSelectionModel()) instanceof SelectionModel.Single));
    }

    @Test(expected = IllegalStateException.class)
    public void getSelectedRowThrowsExceptionMulti() {
        grid.setSelectionMode(MULTI);
        grid.getSelectedRow();
    }

    @Test(expected = IllegalStateException.class)
    public void getSelectedRowThrowsExceptionNone() {
        grid.setSelectionMode(NONE);
        grid.getSelectedRow();
    }

    @Test(expected = IllegalStateException.class)
    public void selectThrowsExceptionNone() {
        grid.setSelectionMode(NONE);
        grid.select(itemId1Present);
    }

    @Test(expected = IllegalStateException.class)
    public void deselectRowThrowsExceptionNone() {
        grid.setSelectionMode(NONE);
        grid.deselect(itemId1Present);
    }

    @Test
    public void selectionModeMapsToMulti() {
        Assert.assertTrue(((grid.setSelectionMode(MULTI)) instanceof SelectionModel.Multi));
    }

    @Test
    public void selectionModeMapsToSingle() {
        Assert.assertTrue(((grid.setSelectionMode(SINGLE)) instanceof SelectionModel.Single));
    }

    @Test
    public void selectionModeMapsToNone() {
        Assert.assertTrue(((grid.setSelectionMode(NONE)) instanceof SelectionModel.None));
    }

    @Test(expected = IllegalArgumentException.class)
    public void selectionModeNullThrowsException() {
        grid.setSelectionMode(null);
    }

    @Test
    public void noSelectModel_isSelected() {
        grid.setSelectionMode(NONE);
        Assert.assertFalse("itemId1Present", grid.isSelected(itemId1Present));
        Assert.assertFalse("itemId1NotPresent", grid.isSelected(itemId1NotPresent));
    }

    @Test(expected = IllegalStateException.class)
    public void noSelectModel_getSelectedRow() {
        grid.setSelectionMode(NONE);
        grid.getSelectedRow();
    }

    @Test
    public void noSelectModel_getSelectedRows() {
        grid.setSelectionMode(NONE);
        Assert.assertTrue(grid.getSelectedRows().isEmpty());
    }

    @Test
    public void selectionCallsListenerMulti() {
        grid.setSelectionMode(MULTI);
        selectionCallsListener();
    }

    @Test
    public void selectionCallsListenerSingle() {
        grid.setSelectionMode(SINGLE);
        selectionCallsListener();
    }

    @Test
    public void deselectionCallsListenerMulti() {
        grid.setSelectionMode(MULTI);
        deselectionCallsListener();
    }

    @Test
    public void deselectionCallsListenerSingle() {
        grid.setSelectionMode(SINGLE);
        deselectionCallsListener();
    }

    @Test
    public void deselectPresentButNotSelectedItemIdShouldntFireListenerMulti() {
        grid.setSelectionMode(MULTI);
        deselectPresentButNotSelectedItemIdShouldntFireListener();
    }

    @Test
    public void deselectPresentButNotSelectedItemIdShouldntFireListenerSingle() {
        grid.setSelectionMode(SINGLE);
        deselectPresentButNotSelectedItemIdShouldntFireListener();
    }

    @Test
    public void deselectNotPresentItemIdShouldNotThrowExceptionMulti() {
        grid.setSelectionMode(MULTI);
        grid.deselect(itemId1NotPresent);
    }

    @Test
    public void deselectNotPresentItemIdShouldNotThrowExceptionSingle() {
        grid.setSelectionMode(SINGLE);
        grid.deselect(itemId1NotPresent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void selectNotPresentItemIdShouldThrowExceptionMulti() {
        grid.setSelectionMode(MULTI);
        grid.select(itemId1NotPresent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void selectNotPresentItemIdShouldThrowExceptionSingle() {
        grid.setSelectionMode(SINGLE);
        grid.select(itemId1NotPresent);
    }

    @Test
    public void selectAllMulti() {
        grid.setSelectionMode(MULTI);
        final SelectionModel.Multi select = ((SelectionModel.Multi) (grid.getSelectionModel()));
        select.selectAll();
        Assert.assertEquals("added size", 10, mockListener.getAdded().size());
        Assert.assertEquals("removed size", 0, mockListener.getRemoved().size());
        Assert.assertTrue("itemId1Present", mockListener.getAdded().contains(itemId1Present));
        Assert.assertTrue("itemId2Present", mockListener.getAdded().contains(itemId2Present));
    }

    @Test
    public void deselectAllMulti() {
        grid.setSelectionMode(MULTI);
        final SelectionModel.Multi select = ((SelectionModel.Multi) (grid.getSelectionModel()));
        select.selectAll();
        mockListener.clearEvent();
        select.deselectAll();
        Assert.assertEquals("removed size", 10, mockListener.getRemoved().size());
        Assert.assertEquals("added size", 0, mockListener.getAdded().size());
        Assert.assertTrue("itemId1Present", mockListener.getRemoved().contains(itemId1Present));
        Assert.assertTrue("itemId2Present", mockListener.getRemoved().contains(itemId2Present));
        Assert.assertTrue("selectedRows is empty", grid.getSelectedRows().isEmpty());
    }

    @Test
    public void gridDeselectAllMultiAllSelected() {
        grid.setSelectionMode(MULTI);
        final SelectionModel.Multi select = ((SelectionModel.Multi) (grid.getSelectionModel()));
        select.selectAll();
        mockListener.clearEvent();
        Assert.assertTrue(grid.deselectAll());
        Assert.assertEquals("removed size", 10, mockListener.getRemoved().size());
        Assert.assertEquals("added size", 0, mockListener.getAdded().size());
        Assert.assertTrue("itemId1Present", mockListener.getRemoved().contains(itemId1Present));
        Assert.assertTrue("itemId2Present", mockListener.getRemoved().contains(itemId2Present));
        Assert.assertTrue("selectedRows is empty", grid.getSelectedRows().isEmpty());
    }

    @Test
    public void gridDeselectAllMultiOneSelected() {
        grid.setSelectionMode(MULTI);
        final SelectionModel.Multi select = ((SelectionModel.Multi) (grid.getSelectionModel()));
        select.select(itemId2Present);
        mockListener.clearEvent();
        Assert.assertTrue(grid.deselectAll());
        Assert.assertEquals("removed size", 1, mockListener.getRemoved().size());
        Assert.assertEquals("added size", 0, mockListener.getAdded().size());
        Assert.assertFalse("itemId1Present", mockListener.getRemoved().contains(itemId1Present));
        Assert.assertTrue("itemId2Present", mockListener.getRemoved().contains(itemId2Present));
        Assert.assertTrue("selectedRows is empty", grid.getSelectedRows().isEmpty());
    }

    @Test
    public void gridDeselectAllSingleNoneSelected() {
        grid.setSelectionMode(SINGLE);
        Assert.assertFalse(grid.deselectAll());
        Assert.assertTrue("selectedRows is empty", grid.getSelectedRows().isEmpty());
    }

    @Test
    public void gridDeselectAllSingleOneSelected() {
        grid.setSelectionMode(SINGLE);
        final SelectionModel.Single select = ((SelectionModel.Single) (grid.getSelectionModel()));
        select.select(itemId2Present);
        mockListener.clearEvent();
        Assert.assertTrue(grid.deselectAll());
        Assert.assertEquals("removed size", 1, mockListener.getRemoved().size());
        Assert.assertEquals("added size", 0, mockListener.getAdded().size());
        Assert.assertFalse("itemId1Present", mockListener.getRemoved().contains(itemId1Present));
        Assert.assertTrue("itemId2Present", mockListener.getRemoved().contains(itemId2Present));
        Assert.assertTrue("selectedRows is empty", grid.getSelectedRows().isEmpty());
    }

    @Test
    public void gridDeselectAllMultiNoneSelected() {
        grid.setSelectionMode(MULTI);
        Assert.assertFalse(grid.deselectAll());
        Assert.assertTrue("selectedRows is empty", grid.getSelectedRows().isEmpty());
    }

    @Test
    public void reselectionDeselectsPreviousSingle() {
        grid.setSelectionMode(SINGLE);
        grid.select(itemId1Present);
        mockListener.clearEvent();
        grid.select(itemId2Present);
        Assert.assertEquals("added size", 1, mockListener.getAdded().size());
        Assert.assertEquals("removed size", 1, mockListener.getRemoved().size());
        Assert.assertEquals("added item", itemId2Present, mockListener.getAdded().iterator().next());
        Assert.assertEquals("removed item", itemId1Present, mockListener.getRemoved().iterator().next());
        Assert.assertEquals("selectedRows is correct", itemId2Present, grid.getSelectedRow());
    }

    @Test
    public void selectionChangeEventWhenChangingSelectionModeSingleToNone() {
        grid.select(itemId1Present);
        Assert.assertEquals(itemId1Present, grid.getSelectedRow());
        mockListener.clearEvent();
        grid.setSelectionMode(NONE);
        Assert.assertTrue(mockListener.eventHasHappened());
        Assert.assertTrue(mockListener.getRemoved().contains(itemId1Present));
    }

    @Test
    public void selectionChangeEventWhenChangingSelectionModeMultiToNone() {
        grid.setSelectionMode(MULTI);
        grid.select(itemId1Present);
        grid.select(itemId2Present);
        mockListener.clearEvent();
        grid.setSelectionMode(NONE);
        Assert.assertTrue(mockListener.eventHasHappened());
        Assert.assertTrue(mockListener.getRemoved().contains(itemId1Present));
        Assert.assertTrue(mockListener.getRemoved().contains(itemId2Present));
    }

    @Test
    public void noSelectionChangeEventWhenChanginModeWithNoneSelected() {
        mockListener.clearEvent();
        grid.setSelectionMode(SINGLE);
        Assert.assertFalse(mockListener.eventHasHappened());
        grid.setSelectionMode(NONE);
        Assert.assertFalse(mockListener.eventHasHappened());
        grid.setSelectionMode(MULTI);
        Assert.assertFalse(mockListener.eventHasHappened());
    }
}

