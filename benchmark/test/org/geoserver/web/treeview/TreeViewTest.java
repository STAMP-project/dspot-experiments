/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.treeview;


import TreeView.SELECTED_BEHAVIOR;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.model.IModel;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Niels Charlier
 */
public class TreeViewTest extends GeoServerWicketTestSupport {
    private class MockNode implements TreeNode<Integer> {
        private static final long serialVersionUID = 1012858609071186910L;

        protected int data;

        protected TreeViewTest.MockNode parent;

        protected List<TreeViewTest.MockNode> children = new ArrayList<TreeViewTest.MockNode>();

        protected IModel<Boolean> expanded = new org.apache.wicket.model.Model<Boolean>(false);

        public MockNode(int data, TreeViewTest.MockNode parent) {
            this.data = data;
            this.parent = parent;
            if (parent != null) {
                parent.children.add(this);
            }
        }

        @Override
        public Collection<? extends TreeNode<Integer>> getChildren() {
            return children;
        }

        @Override
        public TreeNode<Integer> getParent() {
            return parent;
        }

        @Override
        public Integer getObject() {
            return data;
        }

        @Override
        public IModel<Boolean> getExpanded() {
            return expanded;
        }

        @Override
        public String getUniqueId() {
            return "" + (data);
        }
    }

    protected final TreeViewTest.MockNode one = new TreeViewTest.MockNode(1, null);

    protected final TreeViewTest.MockNode two = new TreeViewTest.MockNode(2, one);

    protected final TreeViewTest.MockNode three = new TreeViewTest.MockNode(3, one);

    protected final TreeViewTest.MockNode four = new TreeViewTest.MockNode(4, two);

    protected final TreeViewTest.MockNode five = new TreeViewTest.MockNode(5, one);

    protected TreeView<Integer> treeView;

    @Test
    public void testSelection() {
        // initially nothing selected
        Assert.assertTrue(treeView.getSelectedNodes().isEmpty());
        Assert.assertEquals(0, treeView.getSelectedViews().length);
        // select programmatically, without ajax
        treeView.setSelectedNode(four);
        Assert.assertArrayEquals(new Object[]{ four }, treeView.getSelectedNodes().toArray());
        // automatic expand
        Assert.assertEquals(true, two.getExpanded().getObject());
        Assert.assertEquals(true, one.getExpanded().getObject());
        // view
        tester.startComponentInPage(treeView);
        Assert.assertEquals("4", treeView.getSelectedViews()[0].getId());
        Assert.assertTrue(tester.getComponentFromLastRenderedPage("treeView:rootView:1:children:2:children:4:selectableLabel").getBehaviors().contains(SELECTED_BEHAVIOR));
        // to test selection listening:
        final AtomicBoolean fired = new AtomicBoolean();
        treeView.addSelectionListener(( target) -> {
            fired.set(true);
        });
        // select programmatically, with ajax
        treeView.add(new AjaxEventBehavior("testSelectWithAjax") {
            private static final long serialVersionUID = 4422989219680841271L;

            @Override
            protected void onEvent(AjaxRequestTarget target) {
                treeView.setSelectedNode(three, target);
            }
        });
        fired.set(false);
        tester.executeAjaxEvent(treeView, "testSelectWithAjax");
        Assert.assertTrue(fired.get());
        Assert.assertArrayEquals(new Object[]{ three }, treeView.getSelectedNodes().toArray());
        Assert.assertEquals("3", treeView.getSelectedViews()[0].getId());
        Assert.assertFalse(tester.getComponentFromLastRenderedPage("treeView:rootView:1:children:2:children:4:selectableLabel").getBehaviors().contains(SELECTED_BEHAVIOR));
        Assert.assertTrue(tester.getComponentFromLastRenderedPage("treeView:rootView:1:children:3:selectableLabel").getBehaviors().contains(SELECTED_BEHAVIOR));
        // select with gui
        fired.set(false);
        tester.executeAjaxEvent("treeView:rootView:1:children:2:label:selectableLabel", "click");
        Assert.assertTrue(fired.get());
        Assert.assertArrayEquals(new Object[]{ two }, treeView.getSelectedNodes().toArray());
        Assert.assertEquals("2", treeView.getSelectedViews()[0].getId());
        Assert.assertFalse(tester.getComponentFromLastRenderedPage("treeView:rootView:1:children:3:selectableLabel").getBehaviors().contains(SELECTED_BEHAVIOR));
        Assert.assertTrue(tester.getComponentFromLastRenderedPage("treeView:rootView:1:children:2:label:selectableLabel").getBehaviors().contains(SELECTED_BEHAVIOR));
        // automatic unselect when unexpanding
        tester.executeAjaxEvent("treeView:rootView:1:cbExpand", "click");
        Assert.assertEquals(false, one.getExpanded().getObject());
        Assert.assertTrue(treeView.getSelectedNodes().isEmpty());
        Assert.assertEquals(0, treeView.getSelectedViews().length);
        // multi-select toggle with ctrl
        tester.executeAjaxEvent("treeView:rootView:1:children:2:label:selectableLabel", "click");
        fired.set(false);
        tester.getRequest().addParameter("ctrl", "true");
        tester.executeAjaxEvent("treeView:rootView:1:children:3:selectableLabel", "click");
        Assert.assertTrue(fired.get());
        Assert.assertEquals(2, treeView.getSelectedNodes().size());
        Assert.assertTrue(treeView.getSelectedNodes().contains(two));
        Assert.assertTrue(treeView.getSelectedNodes().contains(three));
        Assert.assertEquals(2, treeView.getSelectedViews().length);
        Assert.assertTrue(tester.getComponentFromLastRenderedPage("treeView:rootView:1:children:3:selectableLabel").getBehaviors().contains(SELECTED_BEHAVIOR));
        Assert.assertTrue(tester.getComponentFromLastRenderedPage("treeView:rootView:1:children:2:label:selectableLabel").getBehaviors().contains(SELECTED_BEHAVIOR));
        fired.set(false);
        tester.getRequest().addParameter("ctrl", "true");
        tester.executeAjaxEvent("treeView:rootView:1:children:2:label:selectableLabel", "click");
        Assert.assertTrue(fired.get());
        Assert.assertArrayEquals(new Object[]{ three }, treeView.getSelectedNodes().toArray());
        Assert.assertEquals("3", treeView.getSelectedViews()[0].getId());
        Assert.assertFalse(tester.getComponentFromLastRenderedPage("treeView:rootView:1:children:2:children:4:selectableLabel").getBehaviors().contains(SELECTED_BEHAVIOR));
        Assert.assertTrue(tester.getComponentFromLastRenderedPage("treeView:rootView:1:children:3:selectableLabel").getBehaviors().contains(SELECTED_BEHAVIOR));
        // multi-select with shift
        tester.executeAjaxEvent("treeView:rootView:1:children:2:label:selectableLabel", "click");
        fired.set(false);
        tester.getRequest().addParameter("shift", "true");
        tester.executeAjaxEvent("treeView:rootView:1:children:5:selectableLabel", "click");
        Assert.assertTrue(fired.get());
        Assert.assertEquals(3, treeView.getSelectedNodes().size());
        Assert.assertTrue(treeView.getSelectedNodes().contains(two));
        Assert.assertTrue(treeView.getSelectedNodes().contains(three));
        Assert.assertTrue(treeView.getSelectedNodes().contains(five));
        Assert.assertEquals(3, treeView.getSelectedViews().length);
        Assert.assertTrue(tester.getComponentFromLastRenderedPage("treeView:rootView:1:children:3:selectableLabel").getBehaviors().contains(SELECTED_BEHAVIOR));
        Assert.assertTrue(tester.getComponentFromLastRenderedPage("treeView:rootView:1:children:5:selectableLabel").getBehaviors().contains(SELECTED_BEHAVIOR));
        Assert.assertTrue(tester.getComponentFromLastRenderedPage("treeView:rootView:1:children:2:label:selectableLabel").getBehaviors().contains(SELECTED_BEHAVIOR));
        // same but upside down
        tester.executeAjaxEvent("treeView:rootView:1:children:5:selectableLabel", "click");
        fired.set(false);
        tester.getRequest().addParameter("shift", "true");
        tester.executeAjaxEvent("treeView:rootView:1:children:2:label:selectableLabel", "click");
        Assert.assertTrue(fired.get());
        Assert.assertEquals(3, treeView.getSelectedNodes().size());
        Assert.assertTrue(treeView.getSelectedNodes().contains(two));
        Assert.assertTrue(treeView.getSelectedNodes().contains(three));
        Assert.assertTrue(treeView.getSelectedNodes().contains(five));
        Assert.assertEquals(3, treeView.getSelectedViews().length);
        Assert.assertTrue(tester.getComponentFromLastRenderedPage("treeView:rootView:1:children:3:selectableLabel").getBehaviors().contains(SELECTED_BEHAVIOR));
        Assert.assertTrue(tester.getComponentFromLastRenderedPage("treeView:rootView:1:children:5:selectableLabel").getBehaviors().contains(SELECTED_BEHAVIOR));
        Assert.assertTrue(tester.getComponentFromLastRenderedPage("treeView:rootView:1:children:2:label:selectableLabel").getBehaviors().contains(SELECTED_BEHAVIOR));
    }

    @Test
    public void testNearestView() {
        tester.startComponentInPage(treeView);
        TreeViewTest.MockNode five = new TreeViewTest.MockNode(5, four);
        Assert.assertEquals("4", treeView.getNearestView(five).getId());
        tester.startComponentInPage(treeView);
        Assert.assertEquals("5", treeView.getNearestView(five).getId());
    }

    @Test
    public void testMarks() {
        final String TESTMARK = "testMark";
        treeView.setSelectedNodes(Collections.emptySet());
        treeView.registerMark("testMark");
        Assert.assertNotNull(treeView.marks.get(TESTMARK));
        treeView.addMarked("testMark", two);
        treeView.addMarked("testMark", three);
        Assert.assertFalse(treeView.hasMark(TESTMARK, one));
        Assert.assertTrue(treeView.hasMark(TESTMARK, two));
        Assert.assertTrue(treeView.hasMark(TESTMARK, three));
        Assert.assertFalse(treeView.hasMark(TESTMARK, four));
        final AttributeAppender app = treeView.marks.get(TESTMARK).getBehaviour();
        tester.startComponentInPage(treeView);
        Assert.assertTrue(tester.getComponentFromLastRenderedPage("treeView:rootView:1:children:2:label:selectableLabel").getBehaviors().contains(app));
        Assert.assertTrue(tester.getComponentFromLastRenderedPage("treeView:rootView:1:children:3:selectableLabel").getBehaviors().contains(app));
        treeView.clearMarked("testMark");
        Assert.assertFalse(treeView.hasMark(TESTMARK, two));
        Assert.assertFalse(treeView.hasMark(TESTMARK, three));
        tester.startComponentInPage(treeView);
        Assert.assertFalse(tester.getComponentFromLastRenderedPage("treeView:rootView:1:children:2:label:selectableLabel").getBehaviors().contains(app));
        Assert.assertFalse(tester.getComponentFromLastRenderedPage("treeView:rootView:1:children:3:selectableLabel").getBehaviors().contains(app));
    }
}

