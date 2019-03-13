package com.vaadin.tests.components.ui;


import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


/**
 * Test for testing if a component is missing from a widgetset.
 *
 * @author Vaadin Ltd
 */
public class ComponentMissingFromDefaultWidgetsetTest extends SingleBrowserTest {
    @Test
    public void testComponentInTestingWidgetset() {
        openTestURL();
        WebElement component = vaadinElementById("missing-component");
        Assert.assertTrue(component.getText().startsWith("Widgetset 'com.vaadin.DefaultWidgetSet' does not contain an implementation for com.vaadin.tests.widgetset.server.MissingFromDefaultWidgetsetComponent."));
    }
}

