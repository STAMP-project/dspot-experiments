package com.vaadin.v7.tests.server.component.optiongroup;


import com.vaadin.v7.shared.ui.optiongroup.OptionGroupState;
import com.vaadin.v7.ui.OptionGroup;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for OptionGroup state.
 */
public class OptionGroupStateTest {
    @Test
    public void getState_optionGroupHasCustomState() {
        OptionGroupStateTest.TestOptionGroup group = new OptionGroupStateTest.TestOptionGroup();
        OptionGroupState state = group.getState();
        Assert.assertEquals("Unexpected state class", OptionGroupState.class, state.getClass());
    }

    @Test
    public void getPrimaryStyleName_optionGroupHasCustomPrimaryStyleName() {
        OptionGroup layout = new OptionGroup();
        OptionGroupState state = new OptionGroupState();
        Assert.assertEquals("Unexpected primary style name", state.primaryStyleName, layout.getPrimaryStyleName());
    }

    @Test
    public void optionGroupStateHasCustomPrimaryStyleName() {
        OptionGroupState state = new OptionGroupState();
        Assert.assertEquals("Unexpected primary style name", "v-select-optiongroup", state.primaryStyleName);
    }

    private static class TestOptionGroup extends OptionGroup {
        @Override
        public OptionGroupState getState() {
            return super.getState();
        }
    }
}

