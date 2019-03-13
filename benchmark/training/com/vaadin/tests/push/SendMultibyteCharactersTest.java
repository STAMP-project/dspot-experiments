package com.vaadin.tests.push;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


@TestCategory("push")
public abstract class SendMultibyteCharactersTest extends MultiBrowserTest {
    @Test
    public void transportSupportsMultibyteCharacters() {
        setDebug(true);
        openTestURL(("transport=" + (getTransport())));
        openDebugLogTab();
        TextAreaElement textArea = $(TextAreaElement.class).first();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            text.append("??????????????");
        }
        // timing matters for Firefox, this needs to be before sendKeys
        clearDebugMessages();
        textArea.sendKeys(text.toString());
        findElement(By.tagName("body")).click();
        waitForDebugMessage("RPC invocations to be sent to the server:", 5);
        waitForDebugMessage("Handling message from server", 10);
    }
}

