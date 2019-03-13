package com.vaadin.v7.tests.server.component.listselect;


import com.vaadin.v7.shared.ui.select.AbstractSelectState;
import com.vaadin.v7.ui.ListSelect;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for ListSelect State.
 */
public class ListSelectStateTest {
    @Test
    public void getState_listSelectHasCustomState() {
        ListSelectStateTest.TestListSelect select = new ListSelectStateTest.TestListSelect();
        AbstractSelectState state = select.getState();
        Assert.assertEquals("Unexpected state class", AbstractSelectState.class, state.getClass());
    }

    private static class TestListSelect extends ListSelect {
        @Override
        public AbstractSelectState getState() {
            return super.getState();
        }
    }
}

