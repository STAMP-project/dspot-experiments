package com.vaadin.tests.components.textfield;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class TextFieldsValueChangeModeTest extends MultiBrowserTest {
    @Test
    public void textFieldEager() {
        testEager("textfield-eager");
    }

    @Test
    public void textAreaEager() {
        testEager("textarea-eager");
    }

    @Test
    public void textFieldDefault() {
        testDefault("textfield-default");
    }

    @Test
    public void textAreaDefault() {
        testDefault("textarea-default");
    }

    @Test
    public void textFieldTimeout() {
        testTimeout("textfield-timeout");
    }

    @Test
    public void textAreaTimeout() {
        testTimeout("textarea-timeout");
    }
}

