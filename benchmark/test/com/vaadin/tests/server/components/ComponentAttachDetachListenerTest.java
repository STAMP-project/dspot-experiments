package com.vaadin.tests.server.components;


import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbsoluteLayout.ComponentPosition;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.GridLayout.Area;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.HasComponents.ComponentAttachEvent;
import com.vaadin.ui.HasComponents.ComponentAttachListener;
import com.vaadin.ui.HasComponents.ComponentDetachEvent;
import com.vaadin.ui.HasComponents.ComponentDetachListener;
import com.vaadin.ui.Label;
import org.junit.Assert;
import org.junit.Test;


public class ComponentAttachDetachListenerTest {
    private AbstractOrderedLayout olayout;

    private GridLayout gridlayout;

    private AbsoluteLayout absolutelayout;

    private CssLayout csslayout;

    private CustomLayout customlayout;

    // General variables
    private int attachCounter = 0;

    private Component attachedComponent = null;

    private HasComponents attachTarget = null;

    private boolean foundInContainer = false;

    private int detachCounter = 0;

    private Component detachedComponent = null;

    private HasComponents detachedTarget = null;

    // Ordered layout specific variables
    private int indexOfComponent = -1;

    // Grid layout specific variables
    private Area componentArea = null;

    // Absolute layout specific variables
    private ComponentPosition componentPosition = null;

    private class MyAttachListener implements ComponentAttachListener {
        @Override
        public void componentAttachedToContainer(ComponentAttachEvent event) {
            (attachCounter)++;
            attachedComponent = event.getAttachedComponent();
            attachTarget = event.getContainer();
            // Search for component in container (should be found)
            for (Component c : attachTarget) {
                if (c == (attachedComponent)) {
                    foundInContainer = true;
                    break;
                }
            }
            // Get layout specific variables
            if ((attachTarget) instanceof AbstractOrderedLayout) {
                indexOfComponent = ((AbstractOrderedLayout) (attachTarget)).getComponentIndex(attachedComponent);
            } else
                if ((attachTarget) instanceof GridLayout) {
                    componentArea = ((GridLayout) (attachTarget)).getComponentArea(attachedComponent);
                } else
                    if ((attachTarget) instanceof AbsoluteLayout) {
                        componentPosition = ((AbsoluteLayout) (attachTarget)).getPosition(attachedComponent);
                    }


        }
    }

    private class MyDetachListener implements ComponentDetachListener {
        @Override
        public void componentDetachedFromContainer(ComponentDetachEvent event) {
            (detachCounter)++;
            detachedComponent = event.getDetachedComponent();
            detachedTarget = event.getContainer();
            // Search for component in container (should NOT be found)
            for (Component c : detachedTarget) {
                if (c == (detachedComponent)) {
                    foundInContainer = true;
                    break;
                }
            }
            // Get layout specific variables
            if ((detachedTarget) instanceof AbstractOrderedLayout) {
                indexOfComponent = ((AbstractOrderedLayout) (detachedTarget)).getComponentIndex(detachedComponent);
            } else
                if ((detachedTarget) instanceof GridLayout) {
                    componentArea = ((GridLayout) (detachedTarget)).getComponentArea(detachedComponent);
                } else
                    if ((detachedTarget) instanceof AbsoluteLayout) {
                        componentPosition = ((AbsoluteLayout) (detachedTarget)).getPosition(detachedComponent);
                    }


        }
    }

    @Test
    public void testOrderedLayoutAttachListener() {
        // Reset state variables
        resetVariables();
        // Add component -> Should trigger attach listener
        Component comp = new Label();
        olayout.addComponent(comp);
        // Attach counter should get incremented
        Assert.assertEquals(1, attachCounter);
        // The attached component should be the label
        Assert.assertSame(comp, attachedComponent);
        // The attached target should be the layout
        Assert.assertSame(olayout, attachTarget);
        // The attached component should be found in the container
        Assert.assertTrue(foundInContainer);
        // The index of the component should not be -1
        Assert.assertFalse(((indexOfComponent) == (-1)));
    }

    @Test
    public void testOrderedLayoutDetachListener() {
        // Add a component to detach
        Component comp = new Label();
        olayout.addComponent(comp);
        // Reset state variables (since they are set by the attach listener)
        resetVariables();
        // Detach the component -> triggers the detach listener
        olayout.removeComponent(comp);
        // Detach counter should get incremented
        Assert.assertEquals(1, detachCounter);
        // The detached component should be the label
        Assert.assertSame(comp, detachedComponent);
        // The detached target should be the layout
        Assert.assertSame(olayout, detachedTarget);
        // The detached component should not be found in the container
        Assert.assertFalse(foundInContainer);
        // The index of the component should be -1
        Assert.assertEquals((-1), indexOfComponent);
    }

    @Test
    public void testGridLayoutAttachListener() {
        // Reset state variables
        resetVariables();
        // Add component -> Should trigger attach listener
        Component comp = new Label();
        gridlayout.addComponent(comp);
        // Attach counter should get incremented
        Assert.assertEquals(1, attachCounter);
        // The attached component should be the label
        Assert.assertSame(comp, attachedComponent);
        // The attached target should be the layout
        Assert.assertSame(gridlayout, attachTarget);
        // The attached component should be found in the container
        Assert.assertTrue(foundInContainer);
        // The grid area should not be null
        Assert.assertNotNull(componentArea);
    }

    @Test
    public void testGridLayoutDetachListener() {
        // Add a component to detach
        Component comp = new Label();
        gridlayout.addComponent(comp);
        // Reset state variables (since they are set by the attach listener)
        resetVariables();
        // Detach the component -> triggers the detach listener
        gridlayout.removeComponent(comp);
        // Detach counter should get incremented
        Assert.assertEquals(1, detachCounter);
        // The detached component should be the label
        Assert.assertSame(comp, detachedComponent);
        // The detached target should be the layout
        Assert.assertSame(gridlayout, detachedTarget);
        // The detached component should not be found in the container
        Assert.assertFalse(foundInContainer);
        // The grid area should be null
        Assert.assertNull(componentArea);
    }

    @Test
    public void testAbsoluteLayoutAttachListener() {
        // Reset state variables
        resetVariables();
        // Add component -> Should trigger attach listener
        Component comp = new Label();
        absolutelayout.addComponent(comp);
        // Attach counter should get incremented
        Assert.assertEquals(1, attachCounter);
        // The attached component should be the label
        Assert.assertSame(comp, attachedComponent);
        // The attached target should be the layout
        Assert.assertSame(absolutelayout, attachTarget);
        // The attached component should be found in the container
        Assert.assertTrue(foundInContainer);
        // The component position should not be null
        Assert.assertNotNull(componentPosition);
    }

    @Test
    public void testAbsoluteLayoutDetachListener() {
        // Add a component to detach
        Component comp = new Label();
        absolutelayout.addComponent(comp);
        // Reset state variables (since they are set by the attach listener)
        resetVariables();
        // Detach the component -> triggers the detach listener
        absolutelayout.removeComponent(comp);
        // Detach counter should get incremented
        Assert.assertEquals(1, detachCounter);
        // The detached component should be the label
        Assert.assertSame(comp, detachedComponent);
        // The detached target should be the layout
        Assert.assertSame(absolutelayout, detachedTarget);
        // The detached component should not be found in the container
        Assert.assertFalse(foundInContainer);
        // The component position should be null
        Assert.assertNull(componentPosition);
    }

    @Test
    public void testCSSLayoutAttachListener() {
        // Reset state variables
        resetVariables();
        // Add component -> Should trigger attach listener
        Component comp = new Label();
        csslayout.addComponent(comp);
        // Attach counter should get incremented
        Assert.assertEquals(1, attachCounter);
        // The attached component should be the label
        Assert.assertSame(comp, attachedComponent);
        // The attached target should be the layout
        Assert.assertSame(csslayout, attachTarget);
        // The attached component should be found in the container
        Assert.assertTrue(foundInContainer);
    }

    @Test
    public void testCSSLayoutDetachListener() {
        // Add a component to detach
        Component comp = new Label();
        csslayout.addComponent(comp);
        // Reset state variables (since they are set by the attach listener)
        resetVariables();
        // Detach the component -> triggers the detach listener
        csslayout.removeComponent(comp);
        // Detach counter should get incremented
        Assert.assertEquals(1, detachCounter);
        // The detached component should be the label
        Assert.assertSame(comp, detachedComponent);
        // The detached target should be the layout
        Assert.assertSame(csslayout, detachedTarget);
        // The detached component should not be found in the container
        Assert.assertFalse(foundInContainer);
    }

    @Test
    public void testCustomLayoutAttachListener() {
        // Reset state variables
        resetVariables();
        // Add component -> Should trigger attach listener
        Component comp = new Label();
        customlayout.addComponent(comp, "loc");
        Assert.assertEquals("Attach counter should get incremented", 1, attachCounter);
        Assert.assertSame("The attached component should be the label", comp, attachedComponent);
        Assert.assertSame("The attached target should be the layout", customlayout, attachTarget);
        Assert.assertTrue("The attached component should be found in the container", foundInContainer);
    }

    @Test
    public void testCustomLayoutDetachListener() {
        // Add a component to detach
        Component comp = new Label();
        customlayout.addComponent(comp);
        // Reset state variables (since they are set by the attach listener)
        resetVariables();
        // Detach the component -> triggers the detach listener
        customlayout.removeComponent(comp);
        Assert.assertEquals("Detach counter should get incremented", 1, detachCounter);
        Assert.assertSame("The detached component should be the label", comp, detachedComponent);
        Assert.assertSame("The detached target should be the layout", customlayout, detachedTarget);
        Assert.assertFalse("The detached component should not be found in the container", foundInContainer);
    }
}

