package com.vaadin.tests.components.javascriptcomponent;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class DuplicateJavascriptDependenciesTest extends SingleBrowserTest {
    @Test
    public void duplicateJavascriptsDoNotCauseProblems() {
        openTestURL();
        $(ButtonElement.class).first().click();
        Assert.assertEquals("It works", $(LabelElement.class).id("result").getText());
    }
}

