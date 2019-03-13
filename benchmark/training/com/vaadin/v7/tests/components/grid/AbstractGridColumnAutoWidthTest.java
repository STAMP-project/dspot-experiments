package com.vaadin.v7.tests.components.grid;


import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.AbstractTB3Test;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


@SuppressWarnings("boxing")
@TestCategory("grid")
public abstract class AbstractGridColumnAutoWidthTest extends MultiBrowserTest {
    public static final int TOTAL_MARGIN_PX = 21;

    @Test
    public void testNarrowHeaderWideBody() {
        WebElement[] col = getColumn(1);
        int headerWidth = col[0].getSize().getWidth();
        int bodyWidth = col[1].getSize().getWidth();
        int colWidth = (col[2].getSize().getWidth()) - (AbstractGridColumnAutoWidthTest.TOTAL_MARGIN_PX);
        AbstractTB3Test.assertLessThan("header should've been narrower than body", headerWidth, bodyWidth);
        Assert.assertEquals("column should've been roughly as wide as the body", bodyWidth, colWidth, 5);
    }

    @Test
    public void testWideHeaderNarrowBody() {
        WebElement[] col = getColumn(2);
        int headerWidth = col[0].getSize().getWidth();
        int bodyWidth = col[1].getSize().getWidth();
        int colWidth = (col[2].getSize().getWidth()) - (AbstractGridColumnAutoWidthTest.TOTAL_MARGIN_PX);
        AbstractTB3Test.assertGreater("header should've been wider than body", headerWidth, bodyWidth);
        Assert.assertEquals("column should've been roughly as wide as the header", headerWidth, colWidth, 5);
    }

    @Test
    public void testTooNarrowColumn() {
        if (BrowserUtil.isIE(getDesiredCapabilities())) {
            // IE can't deal with overflow nicely.
            return;
        }
        WebElement[] col = getColumn(3);
        int headerWidth = col[0].getSize().getWidth();
        int colWidth = (col[2].getSize().getWidth()) - (AbstractGridColumnAutoWidthTest.TOTAL_MARGIN_PX);
        AbstractTB3Test.assertLessThan("column should've been narrower than content", colWidth, headerWidth);
    }

    @Test
    public void testTooWideColumn() {
        WebElement[] col = getColumn(4);
        int headerWidth = col[0].getSize().getWidth();
        int colWidth = (col[2].getSize().getWidth()) - (AbstractGridColumnAutoWidthTest.TOTAL_MARGIN_PX);
        AbstractTB3Test.assertGreater("column should've been wider than content", colWidth, headerWidth);
    }

    @Test
    public void testColumnsRenderCorrectly() throws IOException {
        compareScreen("initialRender");
    }
}

