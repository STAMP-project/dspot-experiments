package com.vaadin.tests.server.component.upload;


import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.Upload;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the declarative support for implementations of {@link Upload}.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class UploadDeclarativeTest extends DeclarativeTestBase<Upload> {
    @Test
    public void testReadBasic() {
        testRead(getBasicDesign(), getBasicExpected());
    }

    @Test
    public void testWriteBasic() {
        testWrite(getBasicDesign(), getBasicExpected());
    }

    @Test
    public void testReadEmpty() {
        testRead("<vaadin-upload />", new Upload());
    }

    @Test
    public void testWriteEmpty() {
        testWrite("<vaadin-upload />", new Upload());
    }

    @Test
    public void testImmediateModeDefault() {
        Assert.assertTrue(testRead("<v-upload />", new Upload()).isImmediateMode());
        Upload upload = new Upload();
        upload.setImmediateMode(false);
        Assert.assertFalse(testRead("<v-upload immediate-mode=false />", upload).isImmediateMode());
    }
}

