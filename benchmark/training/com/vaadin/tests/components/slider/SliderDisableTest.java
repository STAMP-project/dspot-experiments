package com.vaadin.tests.components.slider;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class SliderDisableTest extends MultiBrowserTest {
    @Test
    public void disableSlider() throws IOException {
        openTestURL();
        String originalPosition = getSliderHandlePosition();
        moveSlider(112);
        String expectedPosition = getSliderHandlePosition();
        MatcherAssert.assertThat(expectedPosition, CoreMatchers.is(CoreMatchers.not(originalPosition)));
        hitButton("disableButton");
        assertSliderIsDisabled();
        MatcherAssert.assertThat(getSliderHandlePosition(), CoreMatchers.is(expectedPosition));
    }
}

