package com.vaadin.tests.components.grid;


import SelectionMode.MULTI;
import SelectionMode.NONE;
import SelectionMode.SINGLE;
import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.GridMultiSelect;
import com.vaadin.ui.components.grid.GridSingleSelect;
import com.vaadin.ui.components.grid.MultiSelectionModel;
import com.vaadin.ui.components.grid.MultiSelectionModelImpl;
import com.vaadin.ui.components.grid.NoSelectionModel;
import com.vaadin.ui.components.grid.SingleSelectionModel;
import com.vaadin.ui.components.grid.SingleSelectionModelImpl;
import org.junit.Assert;
import org.junit.Test;


public class GridSelectionModeTest {
    private Grid<String> grid;

    @Test
    public void testSelectionModes() {
        Assert.assertEquals(SingleSelectionModelImpl.class, grid.getSelectionModel().getClass());
        Assert.assertEquals(MultiSelectionModelImpl.class, grid.setSelectionMode(MULTI).getClass());
        Assert.assertEquals(MultiSelectionModelImpl.class, grid.getSelectionModel().getClass());
        Assert.assertEquals(NoSelectionModel.class, grid.setSelectionMode(NONE).getClass());
        Assert.assertEquals(NoSelectionModel.class, grid.getSelectionModel().getClass());
        Assert.assertEquals(SingleSelectionModelImpl.class, grid.setSelectionMode(SINGLE).getClass());
        Assert.assertEquals(SingleSelectionModelImpl.class, grid.getSelectionModel().getClass());
    }

    @Test(expected = NullPointerException.class)
    public void testNullSelectionMode() {
        grid.setSelectionMode(null);
    }

    @Test
    public void testGridAsMultiSelectHasAllAPI() {
        assertAllAPIAvailable(GridMultiSelect.class, MultiSelectionModel.class, "asMultiSelect");
    }

    @Test
    public void testGridAsSingleSelectHasAllAPI() {
        assertAllAPIAvailable(GridSingleSelect.class, SingleSelectionModel.class, "asSingleSelect");
    }
}

