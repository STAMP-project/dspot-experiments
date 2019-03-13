package com.vaadin.v7.ui;


import com.vaadin.v7.data.util.BeanItemContainerGenerator;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;


public class TableTest {
    Table table;

    @Test
    public void initiallyEmpty() {
        Assert.assertTrue(table.isEmpty());
    }

    @Test
    public void emptyAfterClearSingleSelect() {
        table.setContainerDataSource(BeanItemContainerGenerator.createContainer(100));
        Assert.assertTrue(table.isEmpty());
        Object first = table.getContainerDataSource().getItemIds().iterator().next();
        table.setValue(first);
        Assert.assertEquals(first, table.getValue());
        Assert.assertFalse(table.isEmpty());
        table.clear();
        Assert.assertEquals(null, table.getValue());
        Assert.assertTrue(table.isEmpty());
    }

    @Test
    public void emptyAfterClearMultiSelect() {
        table.setMultiSelect(true);
        table.setContainerDataSource(BeanItemContainerGenerator.createContainer(100));
        Assert.assertTrue(table.isEmpty());
        Assert.assertArrayEquals(new Object[]{  }, ((Collection) (table.getValue())).toArray());
        Object first = table.getContainerDataSource().getItemIds().iterator().next();
        table.select(first);
        Assert.assertArrayEquals(new Object[]{ first }, ((Collection) (table.getValue())).toArray());
        Assert.assertFalse(table.isEmpty());
        table.clear();
        Assert.assertArrayEquals(new Object[]{  }, ((Collection) (table.getValue())).toArray());
        Assert.assertTrue(table.isEmpty());
    }
}

