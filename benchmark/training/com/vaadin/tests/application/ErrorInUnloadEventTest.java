package com.vaadin.tests.application;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.PasswordFieldElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class ErrorInUnloadEventTest extends MultiBrowserTest {
    @Test
    public void testError() {
        openTestURL();
        TextFieldElement user = $(TextFieldElement.class).id("user");
        user.focus();
        user.sendKeys("a");
        PasswordFieldElement pass = $(PasswordFieldElement.class).id("pwd");
        pass.focus();
        pass.sendKeys("d");
        ButtonElement button = $(ButtonElement.class).id("loginButton");
        button.click();
        Assert.assertEquals("label is incorrect, error while loading page", "...Title...", $(LabelElement.class).first().getText());
        openTestURL();
        // no errors and page fully reloaded
        Assert.assertTrue($(LabelElement.class).all().isEmpty());
    }
}

