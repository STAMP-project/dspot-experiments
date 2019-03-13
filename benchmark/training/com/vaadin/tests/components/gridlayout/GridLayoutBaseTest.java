package com.vaadin.tests.components.gridlayout;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public abstract class GridLayoutBaseTest extends MultiBrowserTest {
    @Test
    public void cellSizesAreCorrectlyCalculated() {
        openTestURL();
        hideMiddleRowAndColumn();
        final List<WebElement> slots4x4 = getSlots(1);
        waitUntilColumnAndRowAreHidden(slots4x4);
        final List<WebElement> slots5x5 = getSlots(0);
        for (int i = 0; i < (slots5x5.size()); i++) {
            WebElement compared = slots5x5.get(i);
            WebElement actual = slots4x4.get(i);
            Assert.assertEquals(("Different top coordinate for element " + i), compared.getCssValue("top"), actual.getCssValue("top"));
            Assert.assertEquals(("Different left coordinate for element " + i), compared.getCssValue("left"), actual.getCssValue("left"));
        }
    }
}

