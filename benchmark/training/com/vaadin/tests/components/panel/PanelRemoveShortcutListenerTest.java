package com.vaadin.tests.components.panel;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.PanelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;


/**
 * Test for removing a shortcut listener from Panel.
 *
 * @author Vaadin Ltd
 */
public class PanelRemoveShortcutListenerTest extends MultiBrowserTest {
    private PanelElement panel;

    @Test
    public void testToggleWithShortcut() {
        Assert.assertThat(panel.findElement(By.className("v-panel-caption")).findElement(By.tagName("span")).getText(), Matchers.is("No shortcut effects (press 'A')"));
        attemptShortcut("A on");
        attemptShortcut("A off");
    }

    @Test
    public void testShortcutGetsRemoved() {
        attemptShortcut("A on");
        $(ButtonElement.class).first().click();
        waitForElementPresent(By.className("v-label"));
        attemptShortcut("A on");
        // add a bit more delay to make sure the caption doesn't change later
        sleep(2000);
        Assert.assertThat(panel.findElement(By.className("v-panel-caption")).findElement(By.tagName("span")).getText(), Matchers.is("A on"));
    }
}

