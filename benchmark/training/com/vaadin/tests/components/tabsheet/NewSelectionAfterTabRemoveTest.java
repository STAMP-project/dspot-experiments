package com.vaadin.tests.components.tabsheet;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Automatic test of the default TabSheet selection algorithm when removing a
 * selected tab.
 *
 * @author Vaadin Ltd
 */
public class NewSelectionAfterTabRemoveTest extends MultiBrowserTest {
    @Test
    public void testSelection() throws IOException, InterruptedException {
        openTestURL();
        while (scrollRight()) {
        } 
        selectAndClose(tab(19));
        Assert.assertTrue("Tab 18 selected", isTabSelected(tab(18)));
        selectAndClose(tab(16));
        Assert.assertTrue("Tab 17 selected", isTabSelected(tab(17)));
    }
}

