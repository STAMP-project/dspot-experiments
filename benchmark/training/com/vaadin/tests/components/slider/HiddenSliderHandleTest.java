package com.vaadin.tests.components.slider;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class HiddenSliderHandleTest extends MultiBrowserTest {
    @Test
    public void handleIsAccessible() throws IOException {
        openTestURL();
        MatcherAssert.assertThat(getSliderHandle().isDisplayed(), CoreMatchers.is(true));
    }
}

