package com.vaadin.tests.components.treetable;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.TreeTableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


/**
 * Tests expanding TreeTable rows when page length is zero.
 *
 * @author Vaadin Ltd
 */
public class TreeTablePartialUpdatesPageLength0Test extends MultiBrowserTest {
    @Test
    public void testExpanding() throws IOException {
        openTestURL();
        TreeTableElement treeTable = $(TreeTableElement.class).first();
        List<WebElement> rows = treeTable.findElement(By.className("v-table-body")).findElements(By.tagName("tr"));
        Assert.assertEquals("unexpected row count", 4, rows.size());
        Assert.assertEquals("unexpected contents", "root1", treeTable.getCell(0, 0).getText());
        Assert.assertEquals("unexpected contents", "root2", treeTable.getCell(1, 0).getText());
        Assert.assertEquals("unexpected contents", "root3", treeTable.getCell(2, 0).getText());
        Assert.assertEquals("unexpected contents", "END", treeTable.getCell(3, 0).getText());
        // expand first row, should have 10 children
        treeTable.getCell(0, 0).findElement(By.className("v-treetable-treespacer")).click();
        treeTable = $(TreeTableElement.class).first();
        rows = treeTable.findElement(By.className("v-table-body")).findElements(By.tagName("tr"));
        Assert.assertEquals("unexpected row count", 14, rows.size());
        // expand root3, should have 200 children
        Assert.assertEquals("unexpected contents", "root3", treeTable.getCell(12, 0).getText());
        treeTable.getCell(12, 0).findElement(By.className("v-treetable-treespacer")).click();
        // expand root2, should have 200 children
        Assert.assertEquals("unexpected contents", "root2", treeTable.getCell(11, 0).getText());
        treeTable.getCell(11, 0).findElement(By.className("v-treetable-treespacer")).click();
        treeTable = $(TreeTableElement.class).first();
        rows = treeTable.findElement(By.className("v-table-body")).findElements(By.tagName("tr"));
        Assert.assertEquals("unexpected row count", 414, rows.size());
        // scroll all the way to the bottom
        WebElement ui = findElement(By.className("v-ui"));
        testBenchElement(ui).scroll(12500);
        compareScreen("bottom");
    }
}

