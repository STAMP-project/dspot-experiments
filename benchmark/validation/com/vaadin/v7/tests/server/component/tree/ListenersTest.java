package com.vaadin.v7.tests.server.component.tree;


import com.vaadin.v7.ui.Tree;
import com.vaadin.v7.ui.Tree.CollapseListener;
import com.vaadin.v7.ui.Tree.ExpandListener;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ListenersTest implements CollapseListener , ExpandListener {
    private int expandCalled;

    private int collapseCalled;

    private Object lastExpanded;

    private Object lastCollapsed;

    @Test
    public void testExpandListener() {
        Tree tree = createTree(10, 20, false);
        tree.addListener(((ExpandListener) (this)));
        List<Object> rootIds = new ArrayList<Object>(tree.rootItemIds());
        Assert.assertEquals(10, rootIds.size());
        Assert.assertEquals(((10 + (10 * 20)) + 10), tree.size());
        // Expanding should send one expand event for the root item id
        tree.expandItem(rootIds.get(0));
        Assert.assertEquals(1, expandCalled);
        Assert.assertEquals(rootIds.get(0), lastExpanded);
        // Expand should send one event for each expanded item id.
        // In this case root + child 4
        expandCalled = 0;
        tree.expandItemsRecursively(rootIds.get(1));
        Assert.assertEquals(2, expandCalled);
        List<Object> c = new ArrayList<Object>(tree.getChildren(rootIds.get(1)));
        Assert.assertEquals(c.get(4), lastExpanded);
        // Expanding an already expanded item should send no expand event
        expandCalled = 0;
        tree.expandItem(rootIds.get(0));
        Assert.assertEquals(0, expandCalled);
    }

    @Test
    public void testCollapseListener() {
        Tree tree = createTree(7, 15, true);
        tree.addListener(((CollapseListener) (this)));
        List<Object> rootIds = new ArrayList<Object>(tree.rootItemIds());
        Assert.assertEquals(7, rootIds.size());
        Assert.assertEquals(((7 + (7 * 15)) + 7), tree.size());
        // Expanding should send one expand event for the root item id
        tree.collapseItem(rootIds.get(0));
        Assert.assertEquals(1, collapseCalled);
        Assert.assertEquals(rootIds.get(0), lastCollapsed);
        // Collapse sends one event for each collapsed node.
        // In this case root + child 4
        collapseCalled = 0;
        tree.collapseItemsRecursively(rootIds.get(1));
        Assert.assertEquals(2, collapseCalled);
        List<Object> c = new ArrayList<Object>(tree.getChildren(rootIds.get(1)));
        Assert.assertEquals(c.get(4), lastCollapsed);
        // Collapsing an already expanded item should send no expand event
        collapseCalled = 0;
        tree.collapseItem(rootIds.get(0));
        Assert.assertEquals(0, collapseCalled);
    }
}

