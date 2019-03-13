package com.vaadin.tests.push;


import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


@TestCategory("push")
public class PushFromInitTest extends MultiBrowserTest {
    @Test
    public void testPushFromInit() {
        openTestURL();
        waitUntil(( input) -> ("3. " + PushFromInit.LOG_AFTER_INIT).equals(getLogRow(0)));
    }
}

