package com.vaadin.tests.components.grid;


import SortDirection.ASCENDING;
import SortDirection.DESCENDING;
import com.vaadin.server.SerializableComparator;
import com.vaadin.ui.Grid;
import java.util.Arrays;
import org.junit.Test;


public class GridNullValueSort {
    private static class TestGrid extends Grid<Integer> {
        @Override
        public SerializableComparator<Integer> createSortingComparator() {
            return super.createSortingComparator();
        }
    }

    private GridNullValueSort.TestGrid grid;

    @Test
    public void testNumbersNotNulls() {
        grid.sort(getColumn("int"), ASCENDING);
        performSort(Arrays.asList(2, 1, 3), Arrays.asList(1, 2, 3));
    }

    @Test
    public void testSortByColumnId() {
        sort("int");
        performSort(Arrays.asList(2, 1, 3), Arrays.asList(1, 2, 3));
    }

    @Test
    public void testSortByColumnIdAndDirection() {
        grid.sort("int", DESCENDING);
        performSort(Arrays.asList(2, 1, 3), Arrays.asList(3, 2, 1));
    }

    @Test(expected = IllegalStateException.class)
    public void testSortByMissingColumnId() {
        sort("notHere");
    }

    @Test(expected = IllegalStateException.class)
    public void testSortByMissingColumnIdAndDirection() {
        grid.sort("notHere", DESCENDING);
    }

    @Test
    public void testNumbers() {
        grid.sort(getColumn("int"), ASCENDING);
        performSort(Arrays.asList(1, 2, null, 3, null, null), Arrays.asList(1, 2, 3, null, null, null));
    }

    @Test
    public void testNumbersNotNullsDescending() {
        grid.sort(getColumn("int"), DESCENDING);
        performSort(Arrays.asList(1, 2, 3), Arrays.asList(3, 2, 1));
    }

    @Test
    public void testNumbersDescending() {
        grid.sort(getColumn("int"), DESCENDING);
        performSort(Arrays.asList(1, 3, null, null, null, 2), Arrays.asList(null, null, null, 3, 2, 1));
    }

    @Test
    public void testStringsNotNulls() {
        grid.sort(getColumn("String"), ASCENDING);
        performSort(Arrays.asList(2, 1, 3), Arrays.asList(1, 2, 3));
    }

    @Test
    public void testStrings() {
        grid.sort(getColumn("String"), ASCENDING);
        performSort(Arrays.asList(1, 2, null, 3, null, null), Arrays.asList(1, 2, 3, null, null, null));
    }

    @Test
    public void testStringsNotNullsDescending() {
        grid.sort(getColumn("String"), DESCENDING);
        performSort(Arrays.asList(1, 2, 3), Arrays.asList(3, 2, 1));
    }

    @Test
    public void testStringsDescending() {
        grid.sort(getColumn("String"), DESCENDING);
        performSort(Arrays.asList(1, 3, null, null, null, 2), Arrays.asList(null, null, null, 3, 2, 1));
    }

    @Test
    public void testBooleansNotNulls() {
        grid.sort(getColumn("Boolean"), ASCENDING);
        performSort(Arrays.asList(2, 1), Arrays.asList(1, 2));
    }

    @Test
    public void testBooleans() {
        grid.sort(getColumn("Boolean"), ASCENDING);
        performSort(Arrays.asList(1, null, 2, null, null), Arrays.asList(1, 2, null, null, null));
    }

    @Test
    public void testBooleansNotNullsDescending() {
        grid.sort(getColumn("Boolean"), DESCENDING);
        performSort(Arrays.asList(1, 2), Arrays.asList(2, 1));
    }

    @Test
    public void testBooleansDescending() {
        grid.sort(getColumn("Boolean"), DESCENDING);
        performSort(Arrays.asList(1, null, null, null, 2), Arrays.asList(null, null, null, 2, 1));
    }
}

