package com.vaadin.tests.applicationcontext;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class RpcForClosedUITest extends MultiBrowserTest {
    @Test
    public void testRpcForUIClosedInBackground() throws Exception {
        openTestURL();
        /* Close the UI in a background thread */
        clickButton("Close UI (background)");
        /* Try to log 'hello' */
        clickButton("Log 'hello'");
        /* Ensure 'hello' was not logged */
        checkLogMatches("2. Current WrappedSession id: .*");
        Assert.assertFalse("Page contains word 'Hello'", driver.getPageSource().contains("Hello"));
    }
}

