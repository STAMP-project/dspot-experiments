package com.vaadin.tests.server.component.reachtextarea;


import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.tests.server.component.abstractfield.AbstractFieldDeclarativeTest;
import com.vaadin.ui.RichTextArea;
import java.util.Locale;
import org.junit.Test;


public class RichTextAreaDeclarativeTest extends AbstractFieldDeclarativeTest<RichTextArea, String> {
    @Test
    public void remainingAttributeDeserialization() {
        ValueChangeMode mode = ValueChangeMode.TIMEOUT;
        int timeout = 67;
        String design = String.format("<%s value-change-mode='%s' value-change-timeout='%d'/>", getComponentTag(), mode.name().toLowerCase(Locale.ROOT), timeout);
        RichTextArea component = new RichTextArea();
        component.setValueChangeMode(mode);
        component.setValueChangeTimeout(timeout);
        testRead(design, component);
        testWrite(design, component);
    }
}

