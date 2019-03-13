package com.vaadin.tests.components.richtextarea;


import Keys.ENTER;
import Keys.SHIFT;
import com.vaadin.testbench.elements.RichTextAreaElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;


public class RichTextAreaDelegateToShortcutHandlerTest extends MultiBrowserTest {
    @Test
    public void shouldDelegateToShortcutActionHandler() {
        openTestURL();
        WebElement textAreaEditor = $(RichTextAreaElement.class).first().getEditorIframe();
        textAreaEditor.sendKeys("Test");
        textAreaEditor.sendKeys(ENTER);
        Assert.assertThat("Shortcut handler has not been invoked", getLogRow(0), Matchers.containsString("ShortcutHandler invoked Test"));
        textAreaEditor.sendKeys(Keys.chord(SHIFT, ENTER));
        textAreaEditor.sendKeys("another row");
        textAreaEditor.sendKeys(ENTER);
        Assert.assertThat("Shortcut handler has not been invoked", getLogRow(0), Matchers.containsString("ShortcutHandler invoked Test\nanother row"));
    }
}

