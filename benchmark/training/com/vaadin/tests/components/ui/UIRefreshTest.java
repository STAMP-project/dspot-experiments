package com.vaadin.tests.components.ui;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class UIRefreshTest extends MultiBrowserTest {
    @Test
    public void testUIRefresh() {
        openTestURL();
        Assert.assertFalse(reinitLabelExists());
        // Reload the page; UI.refresh should be invoked
        openTestURL();
        Assert.assertTrue(reinitLabelExists());
    }
}

