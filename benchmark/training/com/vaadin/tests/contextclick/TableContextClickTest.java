package com.vaadin.tests.contextclick;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TableElement;
import org.junit.Assert;
import org.junit.Test;


public class TableContextClickTest extends TableContextClickTestBase {
    @Test
    public void testBodyContextClickWithTypedListener() {
        addOrRemoveTypedListener();
        assertTypedContextClickListener(1);
    }

    @Test
    public void testHeaderContextClickWithTypedListener() {
        addOrRemoveTypedListener();
        contextClick($(TableElement.class).first().getHeaderCell(0));
        Assert.assertEquals("1. ContextClickEvent value: address, propertyId: address, section: HEADER", getLogRow(0));
        contextClick($(TableElement.class).first().getHeaderCell(3));
        Assert.assertEquals("2. ContextClickEvent value: lastName, propertyId: lastName, section: HEADER", getLogRow(0));
    }

    @Test
    public void testFooterContextClickWithTypedListener() {
        addOrRemoveTypedListener();
        contextClick($(TableElement.class).first().getFooterCell(0));
        Assert.assertEquals("1. ContextClickEvent value: null, propertyId: address, section: FOOTER", getLogRow(0));
        contextClick($(TableElement.class).first().getFooterCell(3));
        Assert.assertEquals("2. ContextClickEvent value: null, propertyId: lastName, section: FOOTER", getLogRow(0));
    }

    @Test
    public void testContextClickInEmptyTable() {
        addOrRemoveTypedListener();
        $(ButtonElement.class).caption("Remove all content").first().click();
        contextClick($(TableElement.class).first(), 100, 100);
        Assert.assertEquals("1. ContextClickEvent value: , propertyId: null, section: BODY", getLogRow(0));
    }
}

