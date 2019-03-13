package com.vaadin.tests.components.grid;


import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.GridSelectionModel;
import org.junit.Test;


public class GridDelegatesToSelectionModelTest {
    private GridSelectionModel<String> selectionModelMock;

    private GridDelegatesToSelectionModelTest.CustomGrid grid;

    private class CustomGrid extends Grid<String> {
        CustomGrid() {
            super();
            setSelectionModel(selectionModelMock);
        }
    }

    @Test
    public void grid_getSelectedItems_delegated_to_SelectionModel() {
        getSelectedItems();
        getSelectedItems();
    }

    @Test
    public void grid_select_delegated_to_SelectionModel() {
        select("");
        select("");
    }

    @Test
    public void grid_deselect_delegated_to_SelectionModel() {
        deselect("");
        deselect("");
    }

    @Test
    public void grid_deselectAll_delegated_to_SelectionModel() {
        deselectAll();
        deselectAll();
    }
}

