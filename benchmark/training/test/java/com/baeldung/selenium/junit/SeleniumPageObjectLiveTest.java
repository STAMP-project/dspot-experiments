package test.java.com.baeldung.selenium.junit;


import main.java.com.baeldung.selenium.config.SeleniumConfig;
import main.java.com.baeldung.selenium.models.BaeldungAbout;
import main.java.com.baeldung.selenium.pages.BaeldungHomePage;
import main.java.com.baeldung.selenium.pages.StartHerePage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class SeleniumPageObjectLiveTest {
    private SeleniumConfig config;

    private BaeldungHomePage homePage;

    private BaeldungAbout about;

    @Test
    public void givenHomePage_whenNavigate_thenTitleMatch() {
        homePage.navigate();
        MatcherAssert.assertThat(homePage.getPageTitle(), Matchers.is("Baeldung"));
    }

    @Test
    public void givenHomePage_whenNavigate_thenShouldBeInStartHere() {
        homePage.navigate();
        StartHerePage startHerePage = homePage.clickOnStartHere();
        MatcherAssert.assertThat(startHerePage.getPageTitle(), Matchers.is("Start Here"));
    }

    @Test
    public void givenAboutPage_whenNavigate_thenTitleMatch() {
        about.navigateTo();
        MatcherAssert.assertThat(about.getPageTitle(), Matchers.is("About Baeldung"));
    }
}

