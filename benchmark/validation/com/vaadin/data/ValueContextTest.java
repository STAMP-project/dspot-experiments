package com.vaadin.data;


import com.vaadin.ui.CheckBox;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import java.util.Locale;
import java.util.Objects;
import org.junit.Assert;
import org.junit.Test;


public class ValueContextTest extends UI {
    private static final Locale UI_LOCALE = Locale.GERMAN;

    private static final Locale COMPONENT_LOCALE = Locale.FRENCH;

    private TextField textField;

    @Test
    public void locale_from_component() {
        textField.setLocale(ValueContextTest.COMPONENT_LOCALE);
        ValueContext fromComponent = new ValueContext(textField);
        Locale locale = fromComponent.getLocale().orElse(null);
        Objects.requireNonNull(locale);
        Assert.assertEquals("Unexpected locale from component", ValueContextTest.COMPONENT_LOCALE, locale);
    }

    @Test
    public void locale_from_ui() {
        ValueContext fromComponent = new ValueContext(textField);
        Locale locale = fromComponent.getLocale().orElse(null);
        Objects.requireNonNull(locale);
        Assert.assertEquals("Unexpected locale from component", ValueContextTest.UI_LOCALE, locale);
    }

    @Test
    public void default_locale() {
        setLocale(null);
        ValueContext fromComponent = new ValueContext(textField);
        Locale locale = fromComponent.getLocale().orElse(null);
        Objects.requireNonNull(locale);
        Assert.assertEquals("Unexpected locale from component", Locale.getDefault(), locale);
    }

    @Test
    public void testHasValue1() {
        setLocale(null);
        ValueContext fromComponent = new ValueContext(textField);
        Assert.assertEquals(textField, fromComponent.getHasValue().get());
    }

    @Test
    public void testHasValue2() {
        setLocale(null);
        ValueContext fromComponent = new ValueContext(new CheckBox(), textField);
        Assert.assertEquals(textField, fromComponent.getHasValue().get());
    }

    @Test
    public void testHasValue3() {
        setLocale(null);
        ValueContext fromComponent = new ValueContext(new CheckBox(), textField, Locale.CANADA);
        Assert.assertEquals(textField, fromComponent.getHasValue().get());
        Assert.assertEquals(Locale.CANADA, fromComponent.getLocale().get());
    }
}

