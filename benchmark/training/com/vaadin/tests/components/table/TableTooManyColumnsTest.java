package com.vaadin.tests.components.table;


import com.vaadin.testbench.By;
import com.vaadin.testbench.commands.TestBenchElementCommands;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class TableTooManyColumnsTest extends MultiBrowserTest {
    @Test
    public void testDropdownTable() throws IOException {
        openTestURL();
        WebElement element = findElement(By.className("v-table-column-selector"));
        element.click();
        WebElement menu = findElement(By.className("gwt-MenuBar-vertical"));
        TestBenchElementCommands scrollable = testBenchElement(menu);
        scrollable.scroll(3000);
        compareScreen("init");
    }
}

