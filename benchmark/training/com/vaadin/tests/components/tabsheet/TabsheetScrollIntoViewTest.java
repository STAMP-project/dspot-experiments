package com.vaadin.tests.components.tabsheet;


import TabsheetScrollIntoView.BTN_SELECT_LAST_TAB;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TabSheetElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class TabsheetScrollIntoViewTest extends MultiBrowserTest {
    @Test
    public void scrollIntoView() {
        openTestURL();
        $(ButtonElement.class).id(BTN_SELECT_LAST_TAB).click();
        TabSheetElement tabSheet = $(TabSheetElement.class).first();
        Assert.assertTrue("Select last should not hide other tabs", tabSheet.getTabCaptions().contains("Tab 98"));
        List<WebElement> scrollerPrev = tabSheet.findElements(By.className("v-tabsheet-scrollerPrev"));
        Assert.assertTrue("Select last should not disable tab scrolling", ((!(scrollerPrev.isEmpty())) && (scrollerPrev.get(0).isDisplayed())));
    }
}

