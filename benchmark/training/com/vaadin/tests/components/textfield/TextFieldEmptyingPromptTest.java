package com.vaadin.tests.components.textfield;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class TextFieldEmptyingPromptTest extends MultiBrowserTest {
    private String RANDOM_INPUT = "Some input here";

    private TextFieldElement textfield;

    private LabelElement label;

    private ButtonElement button;

    @Test
    public void testInputPrompt() throws InterruptedException {
        openTestURL();
        textfield = $(TextFieldElement.class).first();
        label = $(LabelElement.class).get(1);
        button = $(ButtonElement.class).first();
        // Write on the TextField
        writeOnTextField();
        // Make sure a complete server communication cycle happened
        waitServerUpdate(("Textfield value: " + (RANDOM_INPUT)));
        // Empty the TextField
        emptyTextField();
        // Click attempts to remove the prompt
        button.click();
        // Assert Prompt text disappeared
        waitServerUpdateText("");
    }
}

