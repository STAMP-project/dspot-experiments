package com.vaadin.v7.tests.components.tree;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class TreeItemDoubleClickTest extends MultiBrowserTest {
    @Test
    public void test() throws InterruptedException {
        openTestURL();
        String caption = "Tree Item 2";
        doubleClick(getTreeNodeByCaption(caption));
        assertLogText(("Double Click " + caption));
        changeImmediate();
        caption = "Tree Item 3";
        doubleClick(getTreeNodeByCaption(caption));
        assertLogText(("Double Click " + caption));
    }
}

