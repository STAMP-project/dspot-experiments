package com.vaadin.v7.tests.components.tree;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class SelectItemAfterRemoveTest extends MultiBrowserTest {
    @Test
    public void selectedItemIsSelected() {
        openTestURL();
        getSecondSpan().click();
        MatcherAssert.assertThat(getNodes().size(), Matchers.is(2));
        MatcherAssert.assertThat(getFirstNode().getAttribute("class"), Matchers.containsString("v-tree-node-selected"));
    }
}

