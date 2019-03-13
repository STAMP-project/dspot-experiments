package com.vaadin.tests.components.table;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;

import static TableRemovedQuicklySendsInvalidRpcCalls.BUTTON_ID;
import static TableRemovedQuicklySendsInvalidRpcCalls.FAILURE_CAPTION;
import static TableRemovedQuicklySendsInvalidRpcCalls.SUCCESS_CAPTION;


public class TableRemovedQuicklySendsInvalidRpcCallsTest extends MultiBrowserTest {
    private static final String BUTTON_ID = BUTTON_ID;

    private static final String FAILURE_CAPTION = FAILURE_CAPTION;

    private static final String SUCCESS_CAPTION = SUCCESS_CAPTION;

    @Test
    public void test() throws Exception {
        setDebug(true);
        openTestURL();
        Assert.assertFalse("Test started with the error present.", button().getText().equals(TableRemovedQuicklySendsInvalidRpcCallsTest.FAILURE_CAPTION));
        Assert.assertFalse("Test jumped the gun.", button().getText().equals(TableRemovedQuicklySendsInvalidRpcCallsTest.SUCCESS_CAPTION));
        button().click();
        Thread.sleep(5000);
        Assert.assertFalse("Test failed after trying to trigger the error.", button().getText().equals(TableRemovedQuicklySendsInvalidRpcCallsTest.FAILURE_CAPTION));
        Assert.assertTrue("Test didn't end up in correct success state.", button().getText().equals(TableRemovedQuicklySendsInvalidRpcCallsTest.SUCCESS_CAPTION));
    }
}

