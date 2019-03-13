package com.vaadin.tests.push;


import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


@TestCategory("push")
public abstract class BasicPushTest extends MultiBrowserTest {
    @Test
    public void testPush() throws InterruptedException {
        openTestURL();
        getIncrementButton().click();
        testBench().disableWaitForVaadin();
        waitUntilClientCounterChanges(1);
        getIncrementButton().click();
        getIncrementButton().click();
        getIncrementButton().click();
        waitUntilClientCounterChanges(4);
        // Test server initiated push
        getServerCounterStartButton().click();
        waitUntilServerCounterChanges();
    }
}

