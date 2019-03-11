package test.java.com.baeldung.selenium.junit;


import main.java.com.baeldung.selenium.SeleniumExample;
import org.junit.Test;


public class SeleniumWithJUnitLiveTest {
    private static SeleniumExample seleniumExample;

    private String expectedTitle = "Baeldung | Java, Spring and Web Development tutorials";

    @Test
    public void whenAboutBaeldungIsLoaded_thenAboutEugenIsMentionedOnPage() {
        SeleniumWithJUnitLiveTest.seleniumExample.getAboutBaeldungPage();
        String actualTitle = SeleniumWithJUnitLiveTest.seleniumExample.getTitle();
        assertNotNull(actualTitle);
        assertEquals(expectedTitle, actualTitle);
        assertTrue(SeleniumWithJUnitLiveTest.seleniumExample.isAuthorInformationAvailable());
    }
}

