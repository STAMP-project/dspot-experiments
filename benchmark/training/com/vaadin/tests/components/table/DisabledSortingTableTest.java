package com.vaadin.tests.components.table;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class DisabledSortingTableTest extends MultiBrowserTest {
    Class<?> uiClass;

    @Test
    public void sortingByEmptyArrayShouldClearSortingIndicator() {
        uiClass = DisabledSortingTable.class;
        openTestURL();
        assertThatFirstCellHasText("0");
        sortFirstColumnAscending();
        assertThatFirstCellHasText("4");
        disableSorting();
        sortByEmptyArray();
        assertThatFirstCellHasText("4");
    }
}

