package com.vaadin.v7.tests.components.grid;


import GridHeight.FULL;
import GridHeight.ROW3;
import GridHeight.UNDEFINED;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


/**
 * Tests that Grid gets correct height based on height mode, and resizes
 * properly with details row if height is undefined.
 *
 * @author Vaadin Ltd
 */
@TestCategory("grid")
public class GridHeightTest extends MultiBrowserTest {
    @Test
    public void testGridHeightAndResizingUndefined() throws InterruptedException {
        assertNoErrors(testGridHeightAndResizing(UNDEFINED));
    }

    @Test
    public void testGridHeightAndResizingRow() throws InterruptedException {
        assertNoErrors(testGridHeightAndResizing(ROW3));
    }

    @Test
    public void testGridHeightAndResizingFull() throws InterruptedException {
        assertNoErrors(testGridHeightAndResizing(FULL));
    }
}

