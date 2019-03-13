package com.vaadin.tests.components.tabsheet;


import com.vaadin.testbench.elements.AccordionElement;
import com.vaadin.testbench.elements.TabSheetElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class HtmlInTabCaptionTest extends SingleBrowserTest {
    static final String PLAIN_TEXT_RED = "<font color='red'>red</font>";

    static final String HTML_TEXT_RED = "red";

    static final String PLAIN_TEXT_BLUE = "<font color='blue'>blue</font>";

    static final String HTML_TEXT_BLUE = "blue";

    @Test
    public void tabsheetWithoutHtmlCaptions() {
        openTestURL();
        TabSheetElement ts = $(TabSheetElement.class).get(0);
        Assert.assertEquals(HtmlInTabCaptionTest.PLAIN_TEXT_RED, getTab(ts, 0).getText());
        Assert.assertEquals(HtmlInTabCaptionTest.PLAIN_TEXT_BLUE, getTab(ts, 1).getText());
    }

    @Test
    public void tabsheetWithHtmlCaptions() {
        openTestURL();
        TabSheetElement ts = $(TabSheetElement.class).get(1);
        Assert.assertEquals(HtmlInTabCaptionTest.HTML_TEXT_RED, getTab(ts, 0).getText());
        Assert.assertEquals(HtmlInTabCaptionTest.HTML_TEXT_BLUE, getTab(ts, 1).getText());
    }

    @Test
    public void accordionWithoutHtmlCaptions() {
        openTestURL();
        AccordionElement acc = $(AccordionElement.class).get(0);
        Assert.assertEquals(HtmlInTabCaptionTest.PLAIN_TEXT_RED, getTab(acc, 0).getText());
        Assert.assertEquals(HtmlInTabCaptionTest.PLAIN_TEXT_BLUE, getTab(acc, 1).getText());
    }

    @Test
    public void accordionWithHtmlCaptions() {
        openTestURL();
        AccordionElement acc = $(AccordionElement.class).get(1);
        Assert.assertEquals(HtmlInTabCaptionTest.HTML_TEXT_RED, getTab(acc, 0).getText());
        Assert.assertEquals(HtmlInTabCaptionTest.HTML_TEXT_BLUE, getTab(acc, 1).getText());
    }
}

