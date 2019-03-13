package com.vaadin.tests.components.ui;


import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.net.URI;
import org.junit.Test;


public class PushStateAndReplaceStateTest extends MultiBrowserTest {
    @Test
    public void testUriFragment() throws Exception {
        driver.get(getTestUrl());
        assertUri(getTestUrl());
        hitButton("test");
        assertUri(((getTestUrl()) + "/test"));
        driver.navigate().back();
        driver.findElement(By.className("v-Notification")).getText().contains("Popstate event");
        assertUri(getTestUrl());
        hitButton("test");
        URI base = new URI(((getTestUrl()) + "/test"));
        hitButton("X");
        URI current = base.resolve("X");
        driver.findElement(By.xpath("//*[@id = 'replace']/input")).click();
        hitButton("root_X");
        current = current.resolve("/X");
        assertUri(current.toString());
        // Now that last change was with replace state, two back calls should go
        // to initial
        driver.navigate().back();
        driver.navigate().back();
        assertUri(getTestUrl());
    }
}

