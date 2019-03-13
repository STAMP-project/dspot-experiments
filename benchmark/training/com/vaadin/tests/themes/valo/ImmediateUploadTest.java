package com.vaadin.tests.themes.valo;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


/**
 * Test to see if upload immediate mode hides the native file input.
 *
 * @author Vaadin Ltd
 */
public class ImmediateUploadTest extends MultiBrowserTest {
    @Test
    public void normalUploadButtonIsVisible() {
        WebElement button = getUploadButton("upload");
        Assert.assertThat(button.getCssValue("display"), CoreMatchers.is("block"));
    }

    @Test
    public void fileInputIsVisibleForNormalUpload() {
        WebElement input = getUploadFileInput("upload");
        Assert.assertThat(input.getCssValue("position"), CoreMatchers.is("static"));
    }

    @Test
    public void immediateUploadButtonIsVisible() {
        WebElement button = getUploadButton("immediateupload");
        Assert.assertThat(button.getCssValue("display"), CoreMatchers.is("block"));
    }

    @Test
    public void fileInputIsNotVisibleForImmediateUpload() {
        WebElement input = getUploadFileInput("immediateupload");
        Assert.assertThat(input.getCssValue("position"), CoreMatchers.is("absolute"));
    }

    @Test
    public void fileInputIsNotClickableForImmediateUpload() throws IOException {
        WebElement input = getUploadFileInput("immediateupload");
        // input.click() and then verifying if the upload window is opened
        // would be better but couldn't figure a way to do that. screenshots
        // don't show the upload window, not at least in firefox.
        Assert.assertThat(input.getCssValue("z-index"), CoreMatchers.is("-1"));
    }

    @Test
    public void testAcceptAttribute() {
        WebElement input = getUploadFileInput("immediateupload");
        Assert.assertThat(input.getAttribute("accept"), CoreMatchers.is(ImmediateUpload.TEST_MIME_TYPE));
    }
}

