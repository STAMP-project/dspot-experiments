package com.vaadin.tests.push;


import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


@TestCategory("push")
public class PushLargeDataStreamingTest extends MultiBrowserTest {
    @Test
    public void testStreamingLargeData() throws InterruptedException {
        openTestURL();
        // Without this there is a large chance that we will wait for all pushes
        // to complete before moving on
        testBench(driver).disableWaitForVaadin();
        push();
        // Push complete. Browser will reconnect now as > 10MB has been sent
        // Push again to ensure push still works
        push();
    }
}

