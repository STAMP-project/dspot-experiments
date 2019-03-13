package com.vaadin.tests.components.table;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


/**
 * Scroll position should be restored when removing and re-adding all rows in
 * Table.
 *
 * @author Vaadin Ltd
 */
public class TableRepairsScrollPositionOnReAddingAllRowsTest extends MultiBrowserTest {
    private int rowLocation0;

    @Test
    public void testReAddAllViaAddAll() {
        int rowLocation = getCellY(70);
        // This button is for re-adding all rows (original itemIds) at once
        // (removeAll() + addAll())
        hitButton("buttonReAddAllViaAddAll");
        int newRowLocation = getCellY(70);
        assertCloseTo("Scroll position should be the same as before Re-Adding rows via addAll()", newRowLocation, rowLocation);
    }

    @Test
    public void testReplaceByAnotherCollectionViaAddAll() {
        int rowLocation = getCellY(70);
        // This button is for replacing all rows at once (removeAll() +
        // addAll())
        hitButton("buttonReplaceByAnotherCollectionViaAddAll");
        // new collection has one less element
        int newRowLocation = getCellY(69);
        assertCloseTo("Scroll position should be the same as before Replacing rows via addAll()", newRowLocation, rowLocation);
    }

    @Test
    public void testReplaceByAnotherCollectionViaAdd() {
        // This button is for replacing all rows one by one (removeAll() + add()
        // + add()..)
        hitButton("buttonReplaceByAnotherCollectionViaAdd");
        int newRowLocation = getCellY(0);
        assertCloseTo("Scroll position should be 0", newRowLocation, rowLocation0);
    }

    @Test
    public void testReplaceBySubsetOfSmallerSize() {
        // This button is for replacing all rows at once but the count of rows
        // is less then first index to scroll
        hitButton("buttonReplaceBySubsetOfSmallerSize");
        int newRowLocation = getCellY(5);
        assertCloseTo("Scroll position should be 0", newRowLocation, rowLocation0);
    }

    @Test
    public void testReplaceByWholeSubsetPlusOneNew() {
        int rowLocation = getCellY(70);
        // This button is for replacing by whole original sub-set of items plus
        // one new
        hitButton("buttonReplaceByWholeSubsetPlusOneNew");
        int newRowLocation = getCellY(70);
        assertCloseTo("Scroll position should be the same as before Replacing", newRowLocation, rowLocation);
    }

    @Test
    public void testRemoveAllAddOne() {
        // This button is for removing all and then adding only one new item
        hitButton("buttonRemoveAllAddOne");
        int newRowLocation = getCellY(0);
        assertCloseTo("Scroll position should be 0", newRowLocation, rowLocation0);
    }

    @Test
    public void testReplaceByNewDatasource() {
        // This button is for remove all items and add new datasource
        hitButton("buttonReplaceByNewDatasource");
        int newRowLocation = getCellY(0);
        assertCloseTo("Scroll position should be 0", newRowLocation, rowLocation0);
    }
}

