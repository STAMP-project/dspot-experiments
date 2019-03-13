package com.vaadin.tests.components.datefield;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vaadin Ltd
 */
public class DateTimeFieldTestTest extends MultiBrowserTest {
    @Test
    public void testMakingRequired() throws InterruptedException {
        setDebug(true);
        openTestURL();
        Thread.sleep(1000);
        menu("Component");
        menuSub("State");
        menu("Required");
        assertRequiredIndicatorVisible();
        assertNoErrorNotification();
    }

    @Test
    public void testValueAfterOpeningPopupInRequiredField() throws InterruptedException {
        setDebug(true);
        openTestURL();
        Thread.sleep(1000);
        menu("Component");
        menuSub("State");
        menu("Required");
        assertRequiredIndicatorVisible();
        menu("Component");
        menuSub("Features");
        menuSub("Resolution");
        menu("Month");
        menu("Component");
        menuSub("Listeners");
        menu("Value change listener");
        String inputtedValue = "2/12";
        getInput().sendKeys(inputtedValue);
        openPopup();
        closePopup();
        String actual = getInput().getAttribute("value");
        Assert.assertEquals(inputtedValue, actual);
        assertNoErrorNotification();
    }
}

