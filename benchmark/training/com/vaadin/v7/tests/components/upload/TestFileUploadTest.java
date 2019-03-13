package com.vaadin.v7.tests.components.upload;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;


public class TestFileUploadTest extends MultiBrowserTest {
    @Test
    public void testUploadAnyFile() throws Exception {
        openTestURL();
        File tempFile = createTempFile();
        fillPathToUploadInput(tempFile.getPath());
        getSubmitButton().click();
        String expected = String.format("1. Upload finished. Name: %s, Size: %s, md5: %s", tempFile.getName(), getTempFileContents().length(), md5(getTempFileContents()));
        String actual = getLogRow(0);
        Assert.assertEquals("Upload log row does not match expected", expected, actual);
    }
}

