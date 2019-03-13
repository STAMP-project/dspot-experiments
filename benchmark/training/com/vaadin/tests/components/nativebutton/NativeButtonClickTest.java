package com.vaadin.tests.components.nativebutton;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test to see if coordinates returned by click event on NativeButtons look
 * good. (see #14022)
 *
 * @author Vaadin Ltd
 */
public class NativeButtonClickTest extends MultiBrowserTest {
    /* (non-Javadoc)

    @see com.vaadin.tests.tb3.MultiBrowserTest#getBrowsersToTest()
     */
    @Test
    public void testClickCoordinates() {
        openTestURL();
        clickFirstButton();
        String eventCoordinates = getFirstLabelValue();
        Assert.assertNotEquals("0,0", eventCoordinates);
        clickSecondButton();
        eventCoordinates = getSecondLabelValue();
        Assert.assertNotEquals("0,0", eventCoordinates);
    }
}

