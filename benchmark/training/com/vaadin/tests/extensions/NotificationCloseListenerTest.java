package com.vaadin.tests.extensions;


import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class NotificationCloseListenerTest extends MultiBrowserTest {
    @Test
    public void closeListenerCalled() throws Exception {
        openTestURL();
        $(NotificationElement.class).first().close();
        waitUntil(( input) -> {
            try {
                return $(.class).first().isChecked();
            } catch ( e) {
                e.printStackTrace();
                return false;
            }
        });
    }
}

