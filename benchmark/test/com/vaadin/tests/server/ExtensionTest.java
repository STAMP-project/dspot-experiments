package com.vaadin.tests.server;


import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.AbstractExtension;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import org.junit.Test;


public class ExtensionTest {
    public static class DummyExtension extends AbstractExtension {
        public DummyExtension(AbstractClientConnector target) {
            super(target);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveExtensionFromWrongConnector() {
        Label l = new Label();
        TextField t = new TextField();
        t.removeExtension(new ExtensionTest.DummyExtension(l));
    }
}

