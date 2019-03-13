package com.vaadin.tests.themes.chameleon;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ChameleonNotificationTest extends MultiBrowserTest {
    @Test
    public void gradientPathIsCorrect() throws IOException {
        openTestURL();
        $(ButtonElement.class).first().click();
        NotificationElement notificationElement = $(NotificationElement.class).first();
        Assert.assertThat(notificationElement.getCssValue("background-image"), CoreMatchers.containsString("chameleon/img/grad"));
    }
}

