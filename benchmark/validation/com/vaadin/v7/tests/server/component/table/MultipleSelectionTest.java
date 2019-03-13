package com.vaadin.v7.tests.server.component.table;


import MultiSelectMode.DEFAULT;
import MultiSelectMode.SIMPLE;
import com.vaadin.v7.ui.Table;
import java.util.Arrays;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class MultipleSelectionTest {
    /**
     * Tests weather the multiple select mode is set when using Table.set
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testSetMultipleItems() {
        Table table = new Table("", createTestContainer());
        // Tests if multiple selection is set
        table.setMultiSelect(true);
        Assert.assertTrue(table.isMultiSelect());
        // Test multiselect by setting several items at once
        table.setValue(Arrays.asList("1", new String[]{ "3" }));
        Assert.assertEquals(2, ((Set<String>) (table.getValue())).size());
    }

    /**
     * Tests setting the multiselect mode of the Table. The multiselect mode
     * affects how mouse selection is made in the table by the user.
     */
    @Test
    public void testSetMultiSelectMode() {
        Table table = new Table("", createTestContainer());
        // Default multiselect mode should be MultiSelectMode.DEFAULT
        Assert.assertEquals(DEFAULT, table.getMultiSelectMode());
        // Tests if multiselectmode is set
        table.setMultiSelectMode(SIMPLE);
        Assert.assertEquals(SIMPLE, table.getMultiSelectMode());
    }
}

