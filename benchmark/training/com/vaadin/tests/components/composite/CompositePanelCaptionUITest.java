package com.vaadin.tests.components.composite;


import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;


public class CompositePanelCaptionUITest extends SingleBrowserTest {
    @Test
    public void compositeDoesNotDuplicateCaption() {
        openTestURL();
        assertElementNotPresent(By.className("v-caption"));
    }
}

