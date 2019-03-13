package com.vaadin.v7.tests.server.component.abstractselect;


import com.vaadin.v7.shared.ui.select.AbstractSelectState;
import com.vaadin.v7.ui.AbstractSelect;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for AbstractSelect state
 */
public class AbstractSelectStateTest {
    @Test
    public void getState_selectHasCustomState() {
        AbstractSelectStateTest.TestSelect select = new AbstractSelectStateTest.TestSelect();
        AbstractSelectState state = select.getState();
        Assert.assertEquals("Unexpected state class", AbstractSelectState.class, state.getClass());
    }

    @Test
    public void getPrimaryStyleName_selectHasCustomPrimaryStyleName() {
        AbstractSelectStateTest.TestSelect combobox = new AbstractSelectStateTest.TestSelect();
        AbstractSelectState state = new AbstractSelectState();
        Assert.assertEquals("Unexpected primary style name", state.primaryStyleName, getPrimaryStyleName());
    }

    @Test
    public void selectStateHasCustomPrimaryStyleName() {
        AbstractSelectState state = new AbstractSelectState();
        Assert.assertEquals("Unexpected primary style name", "v-select", state.primaryStyleName);
    }

    private static class TestSelect extends AbstractSelect {
        @Override
        public AbstractSelectState getState() {
            return super.getState();
        }
    }
}

