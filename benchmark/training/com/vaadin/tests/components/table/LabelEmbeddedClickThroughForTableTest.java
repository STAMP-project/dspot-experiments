package com.vaadin.tests.components.table;


import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


/**
 * Tests clicks on different types of Table contents.
 *
 * @author Vaadin Ltd
 */
public class LabelEmbeddedClickThroughForTableTest extends MultiBrowserTest {
    @Test
    public void testNotification() {
        openTestURL();
        TableElement table = $(TableElement.class).first();
        // click first cell of first row
        clickCell(table, 0, 0);
        // click first cell of second row
        clickCell(table, 1, 0);
        // click the ordinary label component on first row
        clickLabel(table, 0, 1);
        // click the ordinary label component on second row
        clickLabel(table, 1, 1);
        // click the html-content label component on first row
        clickBoldTag(table, 0, 2);
        // click the ordinary label component on second row (some browsers
        // navigate away from the page if you try to click the link in the
        // html-content label)
        clickLabel(table, 1, 1);
        // click the embedded image on first row
        clickImageTag(table, 0, 3);
        // click the embedded image on second row
        clickImageTag(table, 1, 3);
    }
}

