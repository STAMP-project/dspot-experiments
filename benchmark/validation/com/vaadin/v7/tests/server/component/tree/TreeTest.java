package com.vaadin.v7.tests.server.component.tree;


import Container.Hierarchical;
import com.vaadin.v7.data.util.HierarchicalContainer;
import com.vaadin.v7.shared.ui.tree.TreeState;
import com.vaadin.v7.ui.Tree;
import java.lang.reflect.Field;
import java.util.HashSet;
import org.junit.Assert;
import org.junit.Test;


public class TreeTest {
    private Tree tree;

    private Tree tree2;

    private Tree tree3;

    private Tree tree4;

    @Test
    public void testRemoveChildren() {
        Assert.assertTrue(tree.hasChildren("parent"));
        tree.removeItem("child");
        Assert.assertFalse(tree.hasChildren("parent"));
        Assert.assertTrue(tree2.hasChildren("parent"));
        tree2.removeItem("child");
        Assert.assertFalse(tree2.hasChildren("parent"));
        Assert.assertTrue(tree3.hasChildren("parent"));
        tree3.removeItem("child");
        Assert.assertFalse(tree3.hasChildren("parent"));
        Assert.assertTrue(tree4.hasChildren("parent"));
        tree4.removeItem("child");
        Assert.assertFalse(tree4.hasChildren("parent"));
    }

    @Test
    public void testContainerTypeIsHierarchical() {
        Assert.assertTrue(HierarchicalContainer.class.isAssignableFrom(tree.getContainerDataSource().getClass()));
        Assert.assertTrue(HierarchicalContainer.class.isAssignableFrom(tree2.getContainerDataSource().getClass()));
        Assert.assertTrue(HierarchicalContainer.class.isAssignableFrom(tree3.getContainerDataSource().getClass()));
        Assert.assertFalse(HierarchicalContainer.class.isAssignableFrom(tree4.getContainerDataSource().getClass()));
        Assert.assertTrue(Hierarchical.class.isAssignableFrom(tree4.getContainerDataSource().getClass()));
    }

    @Test
    public void testRemoveExpandedItems() throws Exception {
        tree.expandItem("parent");
        tree.expandItem("child");
        Field expandedField = tree.getClass().getDeclaredField("expanded");
        Field expandedItemIdField = tree.getClass().getDeclaredField("expandedItemId");
        expandedField.setAccessible(true);
        expandedItemIdField.setAccessible(true);
        HashSet<Object> expanded = ((HashSet<Object>) (expandedField.get(tree)));
        Object expandedItemId = expandedItemIdField.get(tree);
        Assert.assertEquals(2, expanded.size());
        Assert.assertTrue("Contains parent", expanded.contains("parent"));
        Assert.assertTrue("Contains child", expanded.contains("child"));
        Assert.assertEquals("child", expandedItemId);
        tree.removeItem("parent");
        expanded = ((HashSet<Object>) (expandedField.get(tree)));
        expandedItemId = expandedItemIdField.get(tree);
        Assert.assertEquals(1, expanded.size());
        Assert.assertTrue("Contains child", expanded.contains("child"));
        Assert.assertEquals("child", expandedItemId);
        tree.removeItem("child");
        expanded = ((HashSet<Object>) (expandedField.get(tree)));
        expandedItemId = expandedItemIdField.get(tree);
        Assert.assertEquals(0, expanded.size());
        Assert.assertNull(expandedItemId);
    }

    @Test
    public void testRemoveExpandedItemsOnContainerChange() throws Exception {
        tree.expandItem("parent");
        tree.expandItem("child");
        tree.setContainerDataSource(new HierarchicalContainer());
        Field expandedField = tree.getClass().getDeclaredField("expanded");
        Field expandedItemIdField = tree.getClass().getDeclaredField("expandedItemId");
        expandedField.setAccessible(true);
        expandedItemIdField.setAccessible(true);
        HashSet<Object> expanded = ((HashSet<Object>) (expandedField.get(tree)));
        Assert.assertEquals(0, expanded.size());
        Object expandedItemId = expandedItemIdField.get(tree);
        Assert.assertNull(expandedItemId);
    }

    @Test
    public void getState_treeHasCustomState() {
        TreeTest.TestTree table = new TreeTest.TestTree();
        TreeState state = table.getState();
        Assert.assertEquals("Unexpected state class", TreeState.class, state.getClass());
    }

    @Test
    public void getPrimaryStyleName_treeHasCustomPrimaryStyleName() {
        Tree table = new Tree();
        TreeState state = new TreeState();
        Assert.assertEquals("Unexpected primary style name", state.primaryStyleName, table.getPrimaryStyleName());
    }

    @Test
    public void treeStateHasCustomPrimaryStyleName() {
        TreeState state = new TreeState();
        Assert.assertEquals("Unexpected primary style name", "v-tree", state.primaryStyleName);
    }

    private static class TestTree extends Tree {
        @Override
        public TreeState getState() {
            return super.getState();
        }
    }
}

