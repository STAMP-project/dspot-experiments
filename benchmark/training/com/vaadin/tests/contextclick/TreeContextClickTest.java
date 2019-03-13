package com.vaadin.tests.contextclick;


import com.vaadin.testbench.By;
import com.vaadin.v7.testbench.elements.TreeElement;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class TreeContextClickTest extends AbstractContextClickTest {
    @Test
    public void testContextClickOnItem() {
        openTestURL();
        addOrRemoveTypedListener();
        List<WebElement> nodes = $(TreeElement.class).first().findElements(By.className("v-tree-node"));
        contextClick(nodes.get(1));
        Assert.assertEquals("1. ContextClickEvent: Bar", getLogRow(0));
        contextClick(nodes.get(0));
        Assert.assertEquals("2. ContextClickEvent: Foo", getLogRow(0));
    }

    @Test
    public void testContextClickOnSubItem() {
        openTestURL();
        addOrRemoveTypedListener();
        List<WebElement> nodes = $(TreeElement.class).first().findElements(By.className("v-tree-node"));
        new org.openqa.selenium.interactions.Actions(getDriver()).moveToElement(nodes.get(1), getXOffset(nodes.get(1), 10), getYOffset(nodes.get(1), 10)).click().perform();
        nodes = $(TreeElement.class).first().findElements(By.className("v-tree-node"));
        contextClick(nodes.get(2));
        Assert.assertEquals("1. ContextClickEvent: Baz", getLogRow(0));
    }

    @Test
    public void testContextClickOnEmptyArea() {
        openTestURL();
        addOrRemoveTypedListener();
        contextClick($(TreeElement.class).first(), 20, 100);
        Assert.assertEquals("1. ContextClickEvent: null", getLogRow(0));
    }
}

