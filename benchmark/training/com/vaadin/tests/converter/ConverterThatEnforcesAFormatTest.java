package com.vaadin.tests.converter;


import Keys.ENTER;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class ConverterThatEnforcesAFormatTest extends MultiBrowserTest {
    private TextFieldElement field;

    @Test
    public void checkDefault() {
        waitUntilValueIs("50.000");
    }

    @Test
    public void checkRounding() {
        setValue("50.0202", ENTER);
        waitUntilValueIs("50.020");
    }

    @Test
    public void checkElaborating() {
        setValue("12");
        waitUntilValueIs("12.000");
    }

    @Test
    public void checkText() {
        setValue("abc", ENTER);
        waitUntilValueIs("abc");
        waitUntilHasCssClass("v-textfield-error");
    }
}

