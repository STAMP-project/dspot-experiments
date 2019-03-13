package com.vaadin.test.addonusinginitparamwidgetset;


import com.vaadin.test.defaultwidgetset.AbstractWidgetSetIT;
import org.junit.Test;


public class AddonUsingInitParamWidgetSetIT extends AbstractWidgetSetIT {
    @Test
    public void appStartsUserCanInteract() {
        testAppStartsUserCanInteract("com.vaadin.DefaultWidgetSet", true);
        assertHasDebugMessage("does not contain an implementation for com.vaadin.contextmenu.ContextMenu");
    }
}

