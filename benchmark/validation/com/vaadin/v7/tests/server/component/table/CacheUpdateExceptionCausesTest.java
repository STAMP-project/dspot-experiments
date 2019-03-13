package com.vaadin.v7.tests.server.component.table;


import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.CacheUpdateException;
import org.junit.Assert;
import org.junit.Test;


public class CacheUpdateExceptionCausesTest {
    @Test
    public void testSingleCauseException() {
        Table table = new Table();
        Throwable[] causes = new Throwable[]{ new RuntimeException("Broken in one way.") };
        CacheUpdateException exception = new CacheUpdateException(table, "Error during Table cache update.", causes);
        Assert.assertSame(causes[0], exception.getCause());
        Assert.assertEquals("Error during Table cache update.", exception.getMessage());
    }

    @Test
    public void testMultipleCauseException() {
        Table table = new Table();
        Throwable[] causes = new Throwable[]{ new RuntimeException("Broken in the first way."), new RuntimeException("Broken in the second way.") };
        CacheUpdateException exception = new CacheUpdateException(table, "Error during Table cache update.", causes);
        Assert.assertSame(causes[0], exception.getCause());
        Assert.assertEquals("Error during Table cache update. Additional causes not shown.", exception.getMessage());
    }
}

