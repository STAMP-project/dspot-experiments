package com.vaadin.test.addonusingownwidgetset;


import com.vaadin.test.defaultwidgetset.AbstractWidgetSetIT;
import org.junit.Test;


public class AddonUsingOwnWidgetSetIT extends AbstractWidgetSetIT {
    @Test
    public void appStartsUserCanInteract() {
        testAppStartsUserCanInteract("com.vaadin.test.addonusingownwidgetset.AddonUsingOwnWidgetSet");
        assertNoUnknownComponentShown();
    }
}

