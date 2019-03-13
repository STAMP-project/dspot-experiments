package com.vaadin.tests.components.table;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


/**
 * Tests that clicking on active fields doesn't change Table selection, nor does
 * dragging rows.
 *
 * @author Vaadin Ltd
 */
public class TableDropIndicatorValoTest extends MultiBrowserTest {
    @Test
    public void indicator() throws Exception {
        dragRowWithoutDropping(1);
        compareScreen("indicator");
    }
}

