package com.vaadin.tests.components.upload;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class DisabledUploadButtonTest extends MultiBrowserTest {
    @Test
    public void buttonIsDisabled() {
        Assert.assertThat(getUploadButtonClass(), CoreMatchers.not(CoreMatchers.containsString("v-disabled")));
        clickButton("Set disabled");
        Assert.assertThat(getUploadButtonClass(), CoreMatchers.containsString("v-disabled"));
    }
}

