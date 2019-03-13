package com.vaadin.v7.data.util;


import com.vaadin.v7.ui.Table;
import org.junit.Test;


public class ContainerSizeAssertTest {
    @Test(expected = AssertionError.class)
    public void testNegativeSizeAssert() {
        Table table = createAttachedTable();
        table.setContainerDataSource(createNegativeSizeContainer());
    }

    @Test
    public void testZeroSizeNoAssert() {
        Table table = createAttachedTable();
        table.setContainerDataSource(new IndexedContainer());
    }
}

