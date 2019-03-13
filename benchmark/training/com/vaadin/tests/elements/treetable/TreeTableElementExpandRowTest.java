package com.vaadin.tests.elements.treetable;


import com.vaadin.testbench.elements.TreeTableElement;
import com.vaadin.testbench.elements.TreeTableRowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class TreeTableElementExpandRowTest extends MultiBrowserTest {
    TreeTableElement tree;

    @Test
    public void testGetRow() {
        testRowByIndex(1, "testValue", "");
    }

    @Test
    public void testExpandRow0() {
        TreeTableRowElement row = tree.getRow(0);
        row.toggleExpanded();// expand row

        testRowByIndex(1, "item1_1", "Should expand row with index 0.");
        testRowByIndex(2, "item1_2", "Should expand row with index 0.");
    }

    @Test
    public void testCollapseRow0() {
        TreeTableRowElement row = tree.getRow(0);
        row.toggleExpanded();// expand row

        testRowByIndex(1, "item1_1", "Should expand row with index 0.");
        row = tree.getRow(0);
        row.toggleExpanded();// collapse row

        testRowByIndex(1, "testValue", "Should collapse row with index 0.");
    }
}

