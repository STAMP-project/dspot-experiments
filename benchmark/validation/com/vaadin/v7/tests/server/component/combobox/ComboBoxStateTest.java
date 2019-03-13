package com.vaadin.v7.tests.server.component.combobox;


import com.vaadin.v7.shared.ui.combobox.ComboBoxState;
import com.vaadin.v7.ui.ComboBox;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for ComboBox state.
 */
public class ComboBoxStateTest {
    @Test
    public void getState_comboboxHasCustomState() {
        ComboBoxStateTest.TestComboBox combobox = new ComboBoxStateTest.TestComboBox();
        ComboBoxState state = combobox.getState();
        Assert.assertEquals("Unexpected state class", ComboBoxState.class, state.getClass());
    }

    @Test
    public void getPrimaryStyleName_comboboxHasCustomPrimaryStyleName() {
        ComboBox combobox = new ComboBox();
        ComboBoxState state = new ComboBoxState();
        Assert.assertEquals("Unexpected primary style name", state.primaryStyleName, combobox.getPrimaryStyleName());
    }

    @Test
    public void comboboxStateHasCustomPrimaryStyleName() {
        ComboBoxState state = new ComboBoxState();
        Assert.assertEquals("Unexpected primary style name", "v-filterselect", state.primaryStyleName);
    }

    private static class TestComboBox extends ComboBox {
        @Override
        public ComboBoxState getState() {
            return super.getState();
        }
    }
}

