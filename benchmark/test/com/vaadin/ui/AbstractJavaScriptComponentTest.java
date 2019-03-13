package com.vaadin.ui;


import com.vaadin.shared.ui.JavaScriptComponentState;
import com.vaadin.tests.util.MockUI;
import org.junit.Test;


public class AbstractJavaScriptComponentTest {
    public static class TestJsComponentState extends JavaScriptComponentState {
        public String ownField = "foo";
    }

    public static class TestJsComponent extends AbstractJavaScriptComponent {
        @Override
        protected AbstractJavaScriptComponentTest.TestJsComponentState getState() {
            return ((AbstractJavaScriptComponentTest.TestJsComponentState) (super.getState()));
        }
    }

    @Test
    public void testComponentStateEncoding() {
        MockUI ui = new MockUI();
        AbstractJavaScriptComponentTest.TestJsComponent component = new AbstractJavaScriptComponentTest.TestJsComponent();
        setContent(component);
        ComponentTest.assertEncodedStateProperties(component, "Only defaults not known by the client should be sent", "ownField");
        setCaption("My caption");
        ComponentTest.assertEncodedStateProperties(component, "Caption should be the only changed state property", "caption");
    }
}

