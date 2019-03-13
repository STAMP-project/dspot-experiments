package com.vaadin.tests.components.grid.basics;


import com.vaadin.testbench.elements.GridElement;
import org.junit.Test;


public class GridEventListenersTest extends GridBasicsTest {
    @Test
    public void testItemClickListener() {
        selectMenuPath("Component", "State", "Item click listener");
        selectMenuPath("Component", "State", "Selection model", "none");
        checkItemClickOnRow(0);
        checkItemClickOnRow(2);
        GridElement grid = getGridElement();
        grid.getHeaderCell(0, 7);
        checkItemClickOnRow(0);
        checkItemClickOnRow(2);
    }
}

