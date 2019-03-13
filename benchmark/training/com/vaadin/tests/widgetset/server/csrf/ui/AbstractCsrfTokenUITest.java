package com.vaadin.tests.widgetset.server.csrf.ui;


import CsrfButtonConnector.ID;
import CsrfTokenDisabled.PRESS_ID;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.logging.Logger;
import org.junit.Test;
import org.openqa.selenium.By;


public abstract class AbstractCsrfTokenUITest extends MultiBrowserTest {
    static final Logger LOGGER = Logger.getLogger(AbstractCsrfTokenUITest.class.getName());

    @Test
    public void testTokens() {
        openTestURL();
        final By debugButton = By.id(ID);
        final String debugMessage1 = getDriver().findElement(debugButton).getText();
        getDriver().findElement(By.id(PRESS_ID)).click();
        waitUntil(( input) -> {
            getDriver().findElement(debugButton).click();
            String debugMessage2 = input.findElement(debugButton).getText();
            LOGGER.log(Level.INFO, ("1: " + debugMessage1));
            LOGGER.log(Level.INFO, ("2: " + debugMessage2));
            if (!(debugMessage1.equals(debugMessage2))) {
                compareMessage(split(debugMessage1), split(debugMessage2));
                LOGGER.log(Level.INFO, "DONE");
                return true;
            } else {
                return false;
            }
        });
    }

    /* Wrapps all tokens from the client app. */
    static class TokenGroup {
        public final String clientToken;

        public final String tokenReceivedFromServer;

        public final String tokenSentToServer;

        public TokenGroup(String clientToken, String tokenReceivedFromServer, String tokenSentToServer) {
            this.clientToken = clientToken;
            this.tokenReceivedFromServer = tokenReceivedFromServer;
            this.tokenSentToServer = tokenSentToServer;
        }
    }
}

