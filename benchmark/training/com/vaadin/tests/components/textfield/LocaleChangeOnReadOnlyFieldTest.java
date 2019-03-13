package com.vaadin.tests.components.textfield;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;


public class LocaleChangeOnReadOnlyFieldTest extends MultiBrowserTest {
    @Test
    public void localeIsChangedOnReadOnlyField() {
        openTestURL();
        TextFieldElement textField = $(TextFieldElement.class).first();
        MatcherAssert.assertThat(textField.getValue(), Is.is("1,024,000"));
        $(ButtonElement.class).caption("Change Locale").first().click();
        MatcherAssert.assertThat(textField.getValue(), Is.is("1.024.000"));
    }
}

