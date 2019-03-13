package com.vaadin.tests.components.datefield;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Test;


public class PopupDateTimeFieldStatesTest extends MultiBrowserTest {
    @Test
    public void readOnlyDateFieldPopupShouldNotOpen() throws IOException, InterruptedException {
        openTestURL();
        waitUntilLoadingIndicatorNotVisible();
        compareScreen("dateFieldStates");
    }
}

