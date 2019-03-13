package com.vaadin.tests.widgetset.server.gwtrpc;


import GwtRpc.BUTTON_ID;
import GwtRpcButtonConnector.SUCCESS_LABEL_ID;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;


/**
 * Test the GWT RPC with Vaadin DevMode. See #11709.
 *
 * @author Vaadin Ltd
 */
public class GwtRpcTest extends MultiBrowserTest {
    @Test
    public void testGwtRpc() {
        openTestURL();
        getDriver().findElement(By.id(BUTTON_ID)).click();
        By label = By.id(SUCCESS_LABEL_ID);
        waitForElementVisible(label);
        getDriver().findElement(label);
    }
}

