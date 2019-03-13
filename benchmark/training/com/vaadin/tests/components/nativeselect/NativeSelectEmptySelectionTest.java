package com.vaadin.tests.components.nativeselect;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


/**
 *
 *
 * @author Vaadin Ltd
 */
public class NativeSelectEmptySelectionTest extends MultiBrowserTest {
    @Test
    public void checkEmptySelection() {
        openTestURL();
        checkOptions("empty");
        // change the caption
        $(ButtonElement.class).first().click();
        checkOptions("updated");
        // disable empty caption
        $(ButtonElement.class).get(1).click();
        checkOptions(null);
        // enable back
        $(ButtonElement.class).get(2).click();
        checkOptions("updated");
    }
}

