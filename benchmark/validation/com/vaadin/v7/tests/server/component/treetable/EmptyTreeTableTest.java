package com.vaadin.v7.tests.server.component.treetable;


import com.vaadin.v7.ui.TreeTable;
import org.junit.Assert;
import org.junit.Test;


public class EmptyTreeTableTest {
    @Test
    public void testLastId() {
        TreeTable treeTable = new TreeTable();
        Assert.assertFalse(treeTable.isLastId(treeTable.getValue()));
    }
}

