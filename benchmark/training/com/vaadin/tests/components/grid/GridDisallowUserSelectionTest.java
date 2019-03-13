package com.vaadin.tests.components.grid;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


/**
 *
 *
 * @author Vaadin Ltd
 */
public class GridDisallowUserSelectionTest extends MultiBrowserTest {
    @Test
    public void checkSelection() {
        openTestURL();
        assertNoSelection(0);
        assertNoSelection(1);
        // change model from single select to mutli
        $(ButtonElement.class).first().click();
        assertNoSelection(0);
        assertNoSelection(1);
    }
}

