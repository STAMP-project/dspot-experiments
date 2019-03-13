package com.vaadin.tests.components;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


/**
 * Tests that a user is notified about a missing component from the widgetset
 */
public class UnknownComponentConnectorTest extends MultiBrowserTest {
    @Test
    public void testConnectorNotFoundInWidgetset() throws Exception {
        openTestURL();
        WebElement component = vaadinElementById("no-connector-component");
        Assert.assertTrue(component.getText().startsWith(("Widgetset 'com.vaadin.DefaultWidgetSet' does not contain an " + ("implementation for com.vaadin.tests.components.UnknownComponentConnector." + "ComponentWithoutConnector."))));
    }
}

