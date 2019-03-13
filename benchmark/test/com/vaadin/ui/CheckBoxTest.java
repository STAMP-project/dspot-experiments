package com.vaadin.ui;


import com.vaadin.server.ServerRpcManager;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.checkbox.CheckBoxServerRpc;
import com.vaadin.tests.util.MockUI;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;


public class CheckBoxTest {
    @Test
    public void initiallyFalse() {
        CheckBox cb = new CheckBox();
        Assert.assertFalse(cb.getValue());
    }

    @Test
    public void testSetValue() {
        CheckBox cb = new CheckBox();
        cb.setValue(true);
        Assert.assertTrue(cb.getValue());
        cb.setValue(false);
        Assert.assertFalse(cb.getValue());
    }

    @Test
    public void setValueChangeFromClientIsUserOriginated() {
        UI ui = new MockUI();
        CheckBox cb = new CheckBox();
        ui.setContent(cb);
        AtomicBoolean userOriginated = new AtomicBoolean(false);
        cb.addValueChangeListener(( event) -> userOriginated.set(event.isUserOriginated()));
        ComponentTest.syncToClient(cb);
        ServerRpcManager.getRpcProxy(cb, CheckBoxServerRpc.class).setChecked(true, new MouseEventDetails());
        Assert.assertTrue(userOriginated.get());
        userOriginated.set(false);
        ComponentTest.syncToClient(cb);
        ServerRpcManager.getRpcProxy(cb, CheckBoxServerRpc.class).setChecked(false, new MouseEventDetails());
        Assert.assertTrue(userOriginated.get());
    }

    @Test
    public void setValueChangeFromServerIsNotUserOriginated() {
        UI ui = new MockUI();
        CheckBox cb = new CheckBox();
        ui.setContent(cb);
        AtomicBoolean userOriginated = new AtomicBoolean(true);
        cb.addValueChangeListener(( event) -> userOriginated.set(event.isUserOriginated()));
        cb.setValue(true);
        Assert.assertFalse(userOriginated.get());
        userOriginated.set(true);
        cb.setValue(false);
        Assert.assertFalse(userOriginated.get());
    }

    @Test(expected = NullPointerException.class)
    public void setValue_nullValue_throwsNPE() {
        CheckBox cb = new CheckBox();
        cb.setValue(null);
    }

    @Test
    public void getComboBoxInput() {
        CheckBox cb = new CheckBox();
        Assert.assertNotNull("getInputElement should always return a element", cb.getInputElement());
        assertHasStyleNames(cb.getInputElement());
    }

    @Test
    public void getCheckBoxLabel() {
        CheckBox cb = new CheckBox();
        Assert.assertNotNull("getLabelElement should always return a element", cb.getLabelElement());
        assertHasStyleNames(cb.getLabelElement());
    }
}

