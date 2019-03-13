package com.vaadin.v7.tests.server.component.abstractselect;


import com.vaadin.v7.ui.OptionGroup;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;


public class OptionGroupTest {
    private OptionGroup optionGroup;

    @Test
    public void itemsAreAdded() {
        optionGroup.addItems("foo", "bar");
        Collection<?> itemIds = optionGroup.getItemIds();
        Assert.assertEquals(2, itemIds.size());
        Assert.assertTrue(itemIds.contains("foo"));
        Assert.assertTrue(itemIds.contains("bar"));
    }
}

