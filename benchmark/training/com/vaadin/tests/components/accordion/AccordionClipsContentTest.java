package com.vaadin.tests.components.accordion;


import com.vaadin.testbench.elements.NativeButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class AccordionClipsContentTest extends MultiBrowserTest {
    @Test
    public void testAccordionClipsContent() throws Exception {
        openTestURL();
        selectMenuPath("Component", "Component container features", "Add component", "NativeButton", "auto x auto");
        $(NativeButtonElement.class).first().click();
        compareScreen("button-clicked");
    }
}

