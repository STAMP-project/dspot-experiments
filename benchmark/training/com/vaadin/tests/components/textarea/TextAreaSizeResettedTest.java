package com.vaadin.tests.components.textarea;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TextAreaSizeResettedTest extends MultiBrowserTest {
    private final int OFFSET = 100;

    @Test
    public void textAreaIsNotResizedOnBlur() {
        resizeAndAssertTextAreaTo(TextAreaSizeResetted.TEXTAREAHEIGHT, OFFSET);
        getTextArea().sendKeys("foo");
        moveFocusOutsideTextArea();
        // We can't use a waitUntil to check the text area size here, because it
        // won't release the focus from
        // the text area, so we need to do use something else. This workaround
        // uses a label which is updated to indicate
        // polling, which should trigger a resize.
        waitUntilPollingOccurs();
        Assert.assertThat(getTextAreaHeight(), CoreMatchers.is(((TextAreaSizeResetted.TEXTAREAHEIGHT) + (OFFSET))));
        Assert.assertThat(getTextAreaWidth(), CoreMatchers.is(((TextAreaSizeResetted.TEXTAREAWIDTH) + (OFFSET))));
        waitUntilPollingOccurs();
    }

    @Test
    public void textAreaWidthIsPresevedOnHeightResize() {
        resizeAndAssertTextAreaTo(TextAreaSizeResetted.TEXTAREAHEIGHT, OFFSET);
        changeHeightTo((((TextAreaSizeResetted.TEXTAREAHEIGHT) + (OFFSET)) + (OFFSET)));
        Assert.assertThat(getTextAreaWidth(), CoreMatchers.is(((TextAreaSizeResetted.TEXTAREAWIDTH) + (OFFSET))));
        Assert.assertThat(getTextAreaHeight(), CoreMatchers.is((((TextAreaSizeResetted.TEXTAREAHEIGHT) + (OFFSET)) + (OFFSET))));
    }
}

