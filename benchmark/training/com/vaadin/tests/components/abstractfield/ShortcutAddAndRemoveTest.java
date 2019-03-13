package com.vaadin.tests.components.abstractfield;


import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.elements.VerticalLayoutElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class ShortcutAddAndRemoveTest extends MultiBrowserTest {
    @Test
    public void addAndRemoveShortcut() {
        openTestURL();
        VerticalLayoutElement mainLayout = $(VerticalLayoutElement.class).first();
        TextFieldElement textField = $(TextFieldElement.class).first();
        // Enter in mainlayout -> should trigger shortcut
        sendEnter(mainLayout);
        assertLastLogRowIs("1. Log button was clicked");
        // Enter in textfield -> should trigger shortcut
        sendEnter(textField);
        assertLastLogRowIs("2. Log button was clicked");
        // Remove enter shortcut
        removeEnterShortcut();
        // Enter in field - should not trigger any shortcut anymore
        sendEnter(textField);
        assertLastLogRowIs("2. Log button was clicked");
        // Add shortcut again
        addEnterShortcut();
        sendEnter(textField);
        assertLastLogRowIs("3. Log button was clicked");
        sendEnter(mainLayout);
        assertLastLogRowIs("4. Log button was clicked");
        removeEnterShortcut();
        sendEnter(mainLayout);
        assertLastLogRowIs("4. Log button was clicked");
    }
}

