package com.vaadin.v7.tests.server.component.treetable;


import com.vaadin.v7.ui.TreeTable;
import org.junit.Test;


public class TreeTableSetContainerNullTest {
    @Test
    public void testNullContainer() {
        TreeTable treeTable = new TreeTable();
        // should not cause an exception
        treeTable.setContainerDataSource(null);
    }
}

