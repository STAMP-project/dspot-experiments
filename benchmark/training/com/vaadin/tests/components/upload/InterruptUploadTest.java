package com.vaadin.tests.components.upload;


import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;


public class InterruptUploadTest extends MultiBrowserTest {
    private static final String EXPECTED_COUNTER_TEXT = " (counting interrupted at ";

    @Test
    public void testInterruptUpload() throws Exception {
        openTestURL();
        File tempFile = createTempFile();
        scheduleUploadCancel();
        fillPathToUploadInput(tempFile.getPath());
        // Wait for 3 seconds until everything is done.
        Thread.sleep(3000);
        String actual = $(LabelElement.class).caption("Line breaks counted").first().getText();
        Assert.assertTrue((("Line break count note does not match expected (was: " + actual) + ")"), actual.contains(InterruptUploadTest.EXPECTED_COUNTER_TEXT));
        $(WindowElement.class).first().close();
        waitForElementNotPresent(By.className("v-window"));
        // Check if second upload happens
        tempFile = createTempFile();
        scheduleUploadCancel();
        fillPathToUploadInput(tempFile.getPath());
        actual = $(LabelElement.class).caption("Line breaks counted").first().getText();
        Assert.assertTrue((("Line break count note does not match expected (was: " + actual) + ")"), actual.contains(InterruptUploadTest.EXPECTED_COUNTER_TEXT));
    }
}

