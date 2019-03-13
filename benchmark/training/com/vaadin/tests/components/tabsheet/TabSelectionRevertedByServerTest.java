package com.vaadin.tests.components.tabsheet;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Test;


/**
 * If user selected the last tab the test will change it back to the first one
 * from a server side selection listener. This test makes sure that actually
 * happen.
 *
 * @author Vaadin Ltd
 */
public class TabSelectionRevertedByServerTest extends MultiBrowserTest {
    @Test
    public void testFocus() throws IOException, InterruptedException {
        openTestURL();
        // Selects Tab 4 which should be selected.
        click(4);
        assertSelection(4, 1);
        // Select Tab 5 which should revert to Tab 1.
        click(5);
        assertSelection(1, 5);
        // Make sure after reverting the selection the tab selection still
        // works.
        click(3);
        assertSelection(3, 1);
    }

    /* Delay for PhantomJS. */
    private static final int DELAY = 10;
}

