package com.vaadin.tests.components.abstractfield;


import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;


public abstract class ConfigurableAbstractFieldTest extends SingleBrowserTest {
    private static final By REQUIRED_BY = By.className("v-required");

    private static final By ERROR_INDICATOR_BY = By.className("v-errorindicator");

    @Test
    public void requiredIndicator() {
        openTestURL();
        assertNoRequiredIndicator();
        selectMenuPath("Component", "State", "Required");
        assertRequiredIndicator();
        selectMenuPath("Component", "State", "Required");
        assertNoRequiredIndicator();
    }

    @Test
    public void errorIndicator() {
        openTestURL();
        assertNoErrorIndicator();
        selectMenuPath("Component", "State", "Error indicator");
        assertErrorIndicator();
        selectMenuPath("Component", "State", "Error indicator");
        assertNoErrorIndicator();
    }
}

