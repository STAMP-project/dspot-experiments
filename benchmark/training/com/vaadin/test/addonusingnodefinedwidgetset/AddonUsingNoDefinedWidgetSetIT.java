package com.vaadin.test.addonusingnodefinedwidgetset;


import com.vaadin.test.defaultwidgetset.AbstractWidgetSetIT;
import org.junit.Test;


public class AddonUsingNoDefinedWidgetSetIT extends AbstractWidgetSetIT {
    @Test
    public void appStartsUserCanInteract() {
        testAppStartsUserCanInteract("AppWidgetset");
        assertNoUnknownComponentShown();
    }
}

