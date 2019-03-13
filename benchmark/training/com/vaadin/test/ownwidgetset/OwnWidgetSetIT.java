package com.vaadin.test.ownwidgetset;


import com.vaadin.test.defaultwidgetset.AbstractWidgetSetIT;
import org.junit.Test;


public class OwnWidgetSetIT extends AbstractWidgetSetIT {
    @Test
    public void appStartsUserCanInteract() {
        testAppStartsUserCanInteract("com.vaadin.test.ownwidgetset.OwnWidgetSet");
    }
}

