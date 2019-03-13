package com.vaadin.tests.components.grid;


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
public class GridManyColumnsV7Test extends MultiBrowserTest {
    @Test
    public void testGridPerformance() throws InterruptedException {
        long renderingTime = testBench().totalTimeSpentRendering();
        long requestTime = testBench().totalTimeSpentServicingRequests();
        System.out.println((((((("Grid V7 with many columns spent " + renderingTime) + "ms rendering and ") + requestTime) + "ms servicing requests (") + (getDesiredCapabilities().getBrowserName())) + ")"));
    }
}

