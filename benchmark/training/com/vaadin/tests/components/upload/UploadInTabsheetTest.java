package com.vaadin.tests.components.upload;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.File;
import org.junit.Test;


/**
 * Verifies that there's no client side errors when changing a tab containing
 * Upload right after uploading is succeeded (#8728)
 */
public class UploadInTabsheetTest extends MultiBrowserTest {
    @Test
    public void testThatChangingTabAfterUploadDoesntCauseErrors() throws Exception {
        setDebug(true);
        openTestURL();
        File tempFile = createTempFile();
        fillPathToUploadInput(tempFile.getPath());
        getSubmitButton().click();
        assertNoErrorNotifications();
    }
}

