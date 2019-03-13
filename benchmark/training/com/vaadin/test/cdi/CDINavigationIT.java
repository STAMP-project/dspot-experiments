package com.vaadin.test.cdi;


import com.vaadin.testbench.annotations.RunLocally;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.ParallelRunner;
import com.vaadin.testbench.parallel.ParallelTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;


@RunWith(ParallelRunner.class)
@RunLocally(Browser.PHANTOMJS)
public class CDINavigationIT extends ParallelTest {
    protected static final String BASE_URL = "http://localhost:8080";

    @Test
    public void testNavigation() {
        navigateTo("new");
        navigateTo("persisting");
        navigateTo("param/foo");
    }

    @Test
    public void testReloadPage() {
        navigateTo("name/foo");
        navigateTo("name/bar");
        List<String> content = getLogContent();
        getDriver().navigate().refresh();
        Assert.assertTrue(isElementPresent(By.id("name")));
        Assert.assertEquals("Content was lost when reloading", content, getLogContent());
    }
}

