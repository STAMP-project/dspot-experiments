package com.vaadin.tests.components.ui;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


/**
 * Tests if a component is included in a custom widgetset
 * (com.vaadin.tests.widgetset.TestingWidgetSet)
 *
 * @author Vaadin Ltd
 */
public class ComponentIncludedInCustomWidgetsetTest extends MultiBrowserTest {
    @Test
    public void testComponentInTestingWidgetsetNotInDefaultWidgetset() {
        openTestURL();
        WebElement component = vaadinElementById("missing-component");
        Assert.assertEquals("This component is available in TestingWidgetset, but not in DefaultWidgetset", component.getText());
    }
}

