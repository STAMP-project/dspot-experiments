package com.vaadin.tests.components.upload;


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class UploadImmediateButtonWidthChameleonTest extends UploadImmediateButtonWidthTest {
    @Test
    public void immediateButtonWithUndefinedWidth() {
        MatcherAssert.assertThat(getButtonWidth("upload3"), Matchers.closeTo(69, 5));
    }
}

