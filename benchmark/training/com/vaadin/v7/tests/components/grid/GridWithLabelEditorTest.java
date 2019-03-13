package com.vaadin.v7.tests.components.grid;


import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class GridWithLabelEditorTest extends SingleBrowserTest {
    @Test
    public void testNoExceptionOnEdit() {
        setDebug(true);
        openTestURL();
        assertNoErrorNotifications();
        Assert.assertEquals("LabelEditor content not correct.", "FooFoo", $(GridElement.class).first().getEditor().getField(0).getText());
    }
}

