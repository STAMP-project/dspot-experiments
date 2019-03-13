package com.vaadin.tests.server.componentcontainer;


import com.vaadin.shared.ui.orderedlayout.FormLayoutState;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import org.junit.Assert;
import org.junit.Test;


public class FormLayoutTest extends AbstractIndexedLayoutTestBase {
    Component[] children = new Component[]{ new Label("A"), new Label("B"), new Label("C"), new Label("D") };

    @Test
    public void testConstructorWithComponents() {
        FormLayout l = new FormLayout(children);
        assertOrder(l, new int[]{ 0, 1, 2, 3 });
    }

    @Test
    public void testAddComponents() {
        FormLayout l = new FormLayout();
        l.addComponents(children);
        assertOrder(l, new int[]{ 0, 1, 2, 3 });
    }

    @Test
    public void getState_formLayoutHasCustomState() {
        FormLayoutTest.TestFormLayout layout = new FormLayoutTest.TestFormLayout();
        FormLayoutState state = layout.getState();
        Assert.assertEquals("Unexpected state class", FormLayoutState.class, state.getClass());
    }

    @Test
    public void getPrimaryStyleName_formLayoutHasCustomPrimaryStyleName() {
        FormLayout layout = new FormLayout();
        FormLayoutState state = new FormLayoutState();
        Assert.assertEquals("Unexpected primary style name", state.primaryStyleName, layout.getPrimaryStyleName());
    }

    @Test
    public void formLayoutStateHasCustomPrimaryStyleName() {
        FormLayoutState state = new FormLayoutState();
        Assert.assertEquals("Unexpected primary style name", "v-formlayout", state.primaryStyleName);
    }

    private static class TestFormLayout extends FormLayout {
        @Override
        public FormLayoutState getState() {
            return super.getState();
        }
    }
}

