package com.vaadin.tests.components.grid;


import GridHeight.FULL;
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
    public void testGridHeightAndResizingFull() throws InterruptedException {
        assertNoErrors(testGridHeightAndResizing(FULL));
    }
}

