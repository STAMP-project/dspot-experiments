package com.vaadin.tests.components.table;


import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;


/**
 * Tests Table Footer
 *
 * @author Vaadin Ltd
 */
public class FooterTest extends MultiBrowserTest {
    @Test
    public void testFooter() throws IOException {
        openTestURL();
        waitForElementPresent(By.className("v-table"));
        compareScreen("initial");
        TableElement table = $(TableElement.class).first();
        TestBenchElement footer1 = table.getFooterCell(0);
        TestBenchElement footer2 = table.getFooterCell(1);
        TestBenchElement footer3 = table.getFooterCell(2);
        Assert.assertEquals("Footer1", footer1.getText());
        Assert.assertEquals("Footer2", footer2.getText());
        Assert.assertEquals("Footer3", footer3.getText());
        CheckBoxElement checkBox = $(CheckBoxElement.class).first();
        checkBox.click();
        compareScreen("no-footer");
        checkBox.click();
        compareScreen("footer-col1-col2-col3-a");
        table = $(TableElement.class).first();
        footer1 = table.getFooterCell(0);
        footer2 = table.getFooterCell(1);
        footer3 = table.getFooterCell(2);
        Assert.assertEquals("Footer1", footer1.getText());
        Assert.assertEquals("Footer2", footer2.getText());
        Assert.assertEquals("Footer3", footer3.getText());
        // open table column selector menu
        table.findElement(By.className("v-table-column-selector")).click();
        // hide col2
        findElements(By.className("gwt-MenuItem")).get(1).click();
        compareScreen("footer-col1-col3");
        // open table column selector menu
        table.findElement(By.className("v-table-column-selector")).click();
        // show col2
        findElements(By.className("gwt-MenuItem")).get(1).click();
        compareScreen("footer-col1-col2-col3-b");
        TextFieldElement tf = $(TextFieldElement.class).first();
        tf.clear();
        waitUntiltextFieldIsChangedTo(tf, "");
        tf.sendKeys("fuu");
        waitUntiltextFieldIsChangedTo(tf, "fuu");
        ButtonElement button = $(ButtonElement.class).first();
        button.click();
        table = $(TableElement.class).first();
        footer1 = table.getFooterCell(0);
        Assert.assertEquals("fuu", footer1.getText());
        tf = $(TextFieldElement.class).get(1);
        tf.clear();
        waitUntiltextFieldIsChangedTo(tf, "");
        tf.sendKeys("bar");
        waitUntiltextFieldIsChangedTo(tf, "bar");
        button = $(ButtonElement.class).get(1);
        button.click();
        table = $(TableElement.class).first();
        footer2 = table.getFooterCell(1);
        Assert.assertEquals("bar", footer2.getText());
        tf = $(TextFieldElement.class).get(2);
        tf.clear();
        waitUntiltextFieldIsChangedTo(tf, "");
        button = $(ButtonElement.class).get(2);
        button.click();
        table = $(TableElement.class).first();
        footer3 = table.getFooterCell(2);
        Assert.assertEquals("", footer3.getText().trim());
        TextFieldElement tf1 = $(TextFieldElement.class).first();
        tf1.clear();
        waitUntiltextFieldIsChangedTo(tf1, "");
        tf1.sendKeys("Footer1");
        waitUntiltextFieldIsChangedTo(tf1, "Footer1");
        TextFieldElement tf2 = $(TextFieldElement.class).get(1);
        tf2.clear();
        waitUntiltextFieldIsChangedTo(tf2, "");
        tf2.sendKeys("Footer2");
        waitUntiltextFieldIsChangedTo(tf2, "Footer2");
        TextFieldElement tf3 = $(TextFieldElement.class).get(2);
        tf3.clear();
        waitUntiltextFieldIsChangedTo(tf3, "");
        tf3.sendKeys("Footer3");
        waitUntiltextFieldIsChangedTo(tf3, "Footer3");
        button = $(ButtonElement.class).first();
        button.click();
        button = $(ButtonElement.class).get(1);
        button.click();
        button = $(ButtonElement.class).get(2);
        button.click();
        waitUntilFooterCellIsChangedTo(0, "Footer1");
        waitUntilFooterCellIsChangedTo(1, "Footer2");
        waitUntilFooterCellIsChangedTo(2, "Footer3");
        table = $(TableElement.class).first();
        footer1 = table.getFooterCell(0);
        Assert.assertEquals("Footer1", footer1.getText());
        compareScreen("footer-col1-col2-col3-c");
    }
}

