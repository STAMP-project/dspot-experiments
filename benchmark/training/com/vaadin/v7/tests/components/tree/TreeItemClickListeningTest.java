package com.vaadin.v7.tests.components.tree;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class TreeItemClickListeningTest extends MultiBrowserTest {
    @Test
    public void test() throws InterruptedException {
        openTestURL();
        performLeftClick();
        assertEventFired("1. Left Click");
        performRightClick();
        assertEventFired("2. Right Click");
    }
}

