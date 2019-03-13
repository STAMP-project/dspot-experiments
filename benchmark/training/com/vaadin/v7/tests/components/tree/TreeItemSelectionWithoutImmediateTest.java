package com.vaadin.v7.tests.components.tree;


import TreeItemSelectionWithoutImmediate.MENU_ITEM_TEMPLATE;
import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class TreeItemSelectionWithoutImmediateTest extends MultiBrowserTest {
    private static final long serialVersionUID = 1L;

    @Test
    public void testSelectTreeWithItemClickListenerNotImmediate() throws InterruptedException {
        openTestURL();
        // click on item i (in circle we select next item and check if it is
        // selected in tree)
        for (int i = 1; i <= 4; i++) {
            WebElement treeItem = getTreeNode(String.format(MENU_ITEM_TEMPLATE, i));
            new org.openqa.selenium.interactions.Actions(getDriver()).moveToElement(treeItem).click().perform();
            Thread.sleep(100);
            WebElement selectedElement = driver.findElement(By.className("v-tree-node-selected"));
            treeItem = getTreeNode(String.format(MENU_ITEM_TEMPLATE, i));
            MatcherAssert.assertThat("Clicked element should be selected", selectedElement.getText().equals(treeItem.getText()), Matchers.is(true));
        }
    }
}

