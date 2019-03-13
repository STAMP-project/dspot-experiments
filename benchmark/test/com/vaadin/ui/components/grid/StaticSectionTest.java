package com.vaadin.ui.components.grid;


import com.vaadin.data.ValueProvider;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import java.util.Arrays;
import org.junit.Test;


public class StaticSectionTest {
    private final Grid<String> grid = new Grid();

    private final Column<String, String> col1 = grid.addColumn(ValueProvider.identity()).setId("col1");

    private final Column<String, String> col2 = grid.addColumn(ValueProvider.identity()).setId("col2");

    private final Column<String, String> col3 = grid.addColumn(ValueProvider.identity()).setId("col3");

    private HeaderRow headerRow;

    private FooterRow footerRow;

    @Test
    public void joinFootersBySet() {
        footerRow.join(new java.util.HashSet(Arrays.asList(footerRow.getCell(col1), footerRow.getCell(col2))));
        assertFootersJoined();
    }

    @Test
    public void joinFootersByCells() {
        footerRow.join(footerRow.getCell(col1), footerRow.getCell(col2));
        assertFootersJoined();
    }

    @Test
    public void joinFootersByColumns() {
        footerRow.join(col1, col2);
        assertFootersJoined();
    }

    @Test
    public void joinFootersByIds() {
        footerRow.join("col1", "col2");
        assertFootersJoined();
    }

    @Test
    public void joinHeadersBySet() {
        headerRow.join(new java.util.HashSet(Arrays.asList(headerRow.getCell(col1), headerRow.getCell(col2))));
        assertHeadersJoined();
    }

    @Test
    public void joinHeadersByCells() {
        headerRow.join(headerRow.getCell(col1), headerRow.getCell(col2));
        assertHeadersJoined();
    }

    @Test
    public void joinHeadersByColumns() {
        headerRow.join(col1, col2);
        assertHeadersJoined();
    }

    @Test
    public void joinHeadersByIds() {
        headerRow.join("col1", "col2");
        assertHeadersJoined();
    }

    @Test(expected = IllegalStateException.class)
    public void joinHeadersByMissingIds() {
        headerRow.join("col1", "col4");
    }

    @Test(expected = IllegalStateException.class)
    public void joinFootersByMissingIds() {
        headerRow.join("col1", "col4");
    }
}

