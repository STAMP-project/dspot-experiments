package com.vaadin.tests.components.window;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.logging.LogEntry;


public class ClosingWindowWithBrowserFrameShouldntGenerate404Test extends MultiBrowserTest {
    @Test
    public void openWindowWithFrame_closeWindow_no404() {
        openTestURL();
        $(ButtonElement.class).first().click();
        $(WindowElement.class).first().close();
        $(LabelElement.class).exists();
        List<LogEntry> logs = getDriver().manage().logs().get("browser").getAll();
        Assert.assertTrue(theresNoLogWith404In(logs));
    }
}

