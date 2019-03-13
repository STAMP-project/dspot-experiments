package com.vaadin.tests.components.gridlayout;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class GridLayoutRequiredIndicatorLocationTest extends MultiBrowserTest {
    private WebElement gridLayoutSlot;

    @Test
    public void testRequiredIndicatorLocationLeftFixedField() {
        assertIndicatorPosition(getSlot(1));
    }

    @Test
    public void testRequiredIndicatorLocationLeftRelativeField() {
        assertIndicatorPosition(getSlot(3));
    }

    @Test
    public void testRequiredIndicatorLocationLeft100PercentField() {
        assertIndicatorPosition(getSlot(5));
    }

    @Test
    public void testRequiredIndicatorLocationCenterFixedField() {
        assertIndicatorPosition(getSlot(7));
    }

    @Test
    public void testRequiredIndicatorLocationCenterRelativeField() {
        assertIndicatorPosition(getSlot(9));
    }

    @Test
    public void testRequiredIndicatorLocationCenter100PercentField() {
        assertIndicatorPosition(getSlot(11));
    }

    @Test
    public void testRequiredIndicatorLocationRightFixedField() {
        assertIndicatorPosition(getSlot(13));
    }

    @Test
    public void testRequiredIndicatorLocationRightRelativeField() {
        assertIndicatorPosition(getSlot(15));
    }

    @Test
    public void testRequiredIndicatorLocationRight100PercentField() {
        assertIndicatorPosition(getSlot(17));
    }

    @Test
    public void testScreenshotMatches() throws IOException {
        compareScreen("indicators");
    }
}

