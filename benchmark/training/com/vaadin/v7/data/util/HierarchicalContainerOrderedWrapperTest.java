package com.vaadin.v7.data.util;


import org.junit.Test;


public class HierarchicalContainerOrderedWrapperTest extends AbstractHierarchicalContainerTestBase {
    @Test
    public void testBasicOperations() {
        testBasicContainerOperations(createContainer());
    }

    @Test
    public void testHierarchicalContainer() {
        testHierarchicalContainer(createContainer());
    }

    @Test
    public void testContainerOrdered() {
        testContainerOrdered(createContainer());
    }

    @Test
    public void testRemoveSubtree() {
        testRemoveHierarchicalWrapperSubtree(createContainer());
    }
}

