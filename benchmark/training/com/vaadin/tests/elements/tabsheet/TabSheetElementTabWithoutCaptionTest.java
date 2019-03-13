package com.vaadin.tests.elements.tabsheet;


import com.vaadin.testbench.elements.TabSheetElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;


public class TabSheetElementTabWithoutCaptionTest extends MultiBrowserTest {
    @Test
    public void openTabByCaption() {
        for (int i = 1; i <= 5; i++) {
            String caption = (i != 3) ? "Tab " + i : null;
            $(TabSheetElement.class).first().openTab(caption);
            checkSelectedTab(caption);
        }
    }

    @Test
    public void getCaptions() {
        List<String> expectedCaptions = Arrays.asList(new String[]{ "Tab 1", "Tab 2", null, "Tab 4", "Tab 5" });
        List<String> actualCaptions = $(TabSheetElement.class).first().getTabCaptions();
        Assert.assertEquals("Unexpected tab captions", expectedCaptions, actualCaptions);
    }

    @Test
    public void closeTabByCaption() {
        for (int i = 1; i <= 5; i++) {
            String caption = (i != 3) ? "Tab " + i : null;
            $(TabSheetElement.class).first().closeTab(caption);
            checkTabClosed(caption);
        }
    }

    @Test
    public void openTabByIndex() {
        int maxIndex = $(TabSheetElement.class).get(1).getTabCount();
        for (int i = 0; i < maxIndex; i++) {
            $(TabSheetElement.class).all().get(1).openTab(i);
            checkIconTabOpen(i);
        }
    }

    @Test
    public void closeTabByIndex() {
        $(TabSheetElement.class).get(1).closeTab(0);
        int numTabs = $(TabSheetElement.class).get(1).findElements(By.className("v-tabsheet-tabitemcell")).size();
        Assert.assertEquals("The number of open tabs is incorrect", 4, numTabs);
        $(TabSheetElement.class).get(1).closeTab(3);
        numTabs = $(TabSheetElement.class).get(1).findElements(By.className("v-tabsheet-tabitemcell")).size();
        Assert.assertEquals("The number of open tabs is incorrect", 3, numTabs);
        $(TabSheetElement.class).get(1).closeTab(2);
        numTabs = $(TabSheetElement.class).get(1).findElements(By.className("v-tabsheet-tabitemcell")).size();
        Assert.assertEquals("The number of open tabs is incorrect", 2, numTabs);
    }
}

