package com.vaadin.v7.tests.components.tree;


import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;


public class TreeHtmlContentAllowedTest extends SingleBrowserTest {
    @Test
    public void testTreeHtmlContentAllowed() {
        openTestURL();
        CheckBoxElement toggle = $(CheckBoxElement.class).first();
        Assert.assertEquals("HTML content should be disabled by default", "unchecked", toggle.getValue());
        // Markup is seen as plain text
        assertTreeCaptionTexts("Just text", "Some <b>html</b>", "Child <span id='my-html-element'>element html</span>");
        toggle.click();
        assertTreeCaptionTexts("Just text", "Some html", "Child element html");
        // Expand the HTML parent
        findElements(By.className("v-tree-node")).get(1).click();
        assertTreeCaptionTexts("Just text", "Some html", "Child html", "Child element html");
        toggle.click();
        assertTreeCaptionTexts("Just text", "Some <b>html</b>", "Child <i>html</i>", "Child <span id='my-html-element'>element html</span>");
        toggle.click();
        findElements(By.id("my-html-element")).get(0).click();
        assertHtmlElementSelected();
    }
}

