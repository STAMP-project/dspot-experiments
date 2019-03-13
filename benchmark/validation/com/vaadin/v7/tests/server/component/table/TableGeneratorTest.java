package com.vaadin.v7.tests.server.component.table;


import com.vaadin.v7.ui.Table;
import junit.framework.Assert;
import org.junit.Test;


public class TableGeneratorTest {
    @Test
    public void testTableGenerator() {
        Table t = TableGeneratorTest.createTableWithDefaultContainer(1, 1);
        Assert.assertEquals(t.size(), 1);
        Assert.assertEquals(t.getContainerPropertyIds().size(), 1);
        t = TableGeneratorTest.createTableWithDefaultContainer(100, 50);
        Assert.assertEquals(t.size(), 50);
        Assert.assertEquals(t.getContainerPropertyIds().size(), 100);
    }
}

