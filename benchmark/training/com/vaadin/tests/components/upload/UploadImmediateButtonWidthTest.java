package com.vaadin.tests.components.upload;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public abstract class UploadImmediateButtonWidthTest extends MultiBrowserTest {
    @Test
    public void immediateButtonWithPixelWidth() {
        MatcherAssert.assertThat(getButtonWidth("upload1"), CoreMatchers.is(300.0));
    }

    @Test
    public void immediateButtonWithPercentageWidth() {
        MatcherAssert.assertThat(getButtonWidth("upload2"), CoreMatchers.is(250.0));
    }
}

