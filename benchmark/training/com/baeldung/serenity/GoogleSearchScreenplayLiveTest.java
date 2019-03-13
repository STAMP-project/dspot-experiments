package com.baeldung.serenity;


import com.baeldung.serenity.screenplay.GoogleSearchResults;
import com.baeldung.serenity.screenplay.SearchForKeyword;
import com.baeldung.serenity.screenplay.StartWith;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.thucydides.core.annotations.Managed;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;


@RunWith(SerenityRunner.class)
public class GoogleSearchScreenplayLiveTest {
    @Managed(driver = "chrome")
    private WebDriver browser;

    private Actor kitty = Actor.named("kitty");

    @Test
    public void whenGoogleBaeldungThenShouldSeeEugen() {
        givenThat(kitty).wasAbleTo(StartWith.googleSearchPage());
        when(kitty).attemptsTo(SearchForKeyword.of("baeldung"));
        then(kitty).should(seeThat(GoogleSearchResults.displayed(), CoreMatchers.hasItem(CoreMatchers.containsString("Eugen (Baeldung)"))));
    }
}

