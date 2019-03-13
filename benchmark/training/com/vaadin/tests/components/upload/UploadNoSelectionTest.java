package com.vaadin.tests.components.upload;


import UploadNoSelection.FILE_NAME_PREFIX;
import UploadNoSelection.RECEIVING_UPLOAD;
import UploadNoSelection.UPLOAD_FINISHED;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;

import static UploadNoSelection.FILE_LENGTH_PREFIX;


public class UploadNoSelectionTest extends MultiBrowserTest {
    @Test
    public void testUploadNoSelection() throws Exception {
        openTestURL();
        // empty content is populated by com.vaadin.tests.util.Log
        Assert.assertEquals(" ", getLogRow(0));
        getSubmitButton().click();
        // expecting empty file name
        assertLogRow(0, 4, FILE_NAME_PREFIX);
        // expecting 0-length file
        assertLogRow(1, 3, (((FILE_LENGTH_PREFIX) + " ") + 0));
        assertLogRow(2, 2, UPLOAD_FINISHED);
        assertLogRow(3, 1, RECEIVING_UPLOAD);
    }
}

