package com.vaadin.tests.contextclick;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import org.junit.Assert;
import org.junit.Test;


public class GridContextClickTest extends AbstractContextClickTest {
    @Test
    public void testBodyContextClickWithTypedListener() {
        addOrRemoveTypedListener();
        contextClick($(GridElement.class).first().getCell(0, 0));
        Assert.assertEquals("1. ContextClickEvent value: Lisa Schneider, column: Address, section: BODY", getLogRow(0));
        contextClick($(GridElement.class).first().getCell(0, 3));
        Assert.assertEquals("2. ContextClickEvent value: Lisa Schneider, column: Last Name, section: BODY", getLogRow(0));
    }

    @Test
    public void testHeaderContextClickWithTypedListener() {
        addOrRemoveTypedListener();
        contextClick($(GridElement.class).first().getHeaderCell(0, 0));
        Assert.assertEquals("1. ContextClickEvent value: Address, column: Address, section: HEADER", getLogRow(0));
        contextClick($(GridElement.class).first().getHeaderCell(0, 3));
        Assert.assertEquals("2. ContextClickEvent value: Last Name, column: Last Name, section: HEADER", getLogRow(0));
    }

    @Test
    public void testFooterContextClickWithTypedListener() {
        addOrRemoveTypedListener();
        contextClick($(GridElement.class).first().getFooterCell(0, 0));
        Assert.assertEquals("1. ContextClickEvent value: Address, column: Address, section: FOOTER", getLogRow(0));
        contextClick($(GridElement.class).first().getFooterCell(0, 3));
        Assert.assertEquals("2. ContextClickEvent value: Last Name, column: Last Name, section: FOOTER", getLogRow(0));
    }

    @Test
    public void testContextClickInEmptyGrid() {
        addOrRemoveTypedListener();
        $(ButtonElement.class).caption("Remove all content").first().click();
        contextClick($(GridElement.class).first(), 100, 100);
        Assert.assertEquals("1. ContextClickEvent value: , section: BODY", getLogRow(0));
    }
}

