package com.vaadin.tests.components.grid;


import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import org.junit.Test;


public class GridNullValueSortTest {
    private Grid<GridNullValueSortTest.TestClass> grid;

    private Column<GridNullValueSortTest.TestClass, String> stringColumn;

    private Column<GridNullValueSortTest.TestClass, Object> nonComparableColumn;

    @Test
    public void sortWithNullValues() {
        this.grid.sort(this.stringColumn);
        this.grid.sort(this.nonComparableColumn);
        this.grid.getDataCommunicator().beforeClientResponse(true);
    }

    private static class TestClass {
        private final String stringField;

        private final Object nonComparableField;

        TestClass(final String stringField, final Object nonComparableField) {
            this.stringField = stringField;
            this.nonComparableField = nonComparableField;
        }
    }
}

