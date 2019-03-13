package com.vaadin.tests.components.datefield;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import java.util.regex.Pattern;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class PopupDateFieldStatesTest extends MultiBrowserTest {
    @Test
    public void readOnlyDateFieldPopupShouldNotOpen() throws IOException, InterruptedException {
        openTestURL();
        // wait until loading indicator becomes invisible
        WebElement loadingIndicator = findElement(By.className("v-loading-indicator"));
        Pattern pattern = Pattern.compile("display: *none;");
        waitUntil(( driver) -> pattern.matcher(loadingIndicator.getAttribute("style")).find());
        compareScreen("dateFieldStates");
    }
}

