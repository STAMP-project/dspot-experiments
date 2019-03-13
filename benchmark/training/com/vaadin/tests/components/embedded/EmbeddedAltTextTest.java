package com.vaadin.tests.components.embedded;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.EmbeddedElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class EmbeddedAltTextTest extends MultiBrowserTest {
    @Test
    public void testEmbeddedAltText() {
        EmbeddedElement embedded = $(EmbeddedElement.class).first();
        Assert.assertEquals("Alt text of the image", getAltText(embedded));
        assertHtmlSource("Alt text of the object");
        $(ButtonElement.class).first().click();
        Assert.assertEquals("New alt text of the image!", getAltText(embedded));
        assertHtmlSource("New alt text of the object!");
    }
}

