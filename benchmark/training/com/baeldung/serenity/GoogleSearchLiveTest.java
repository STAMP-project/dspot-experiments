package com.baeldung.serenity;


import Keys.ENTER;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Managed;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


@RunWith(SerenityRunner.class)
public class GoogleSearchLiveTest {
    @Managed(driver = "chrome")
    private WebDriver browser;

    @Test
    public void whenGoogleBaeldungThenShouldSeeEugen() {
        browser.get("https://www.google.com/ncr");
        browser.findElement(By.name("q")).sendKeys("baeldung", ENTER);
        new org.openqa.selenium.support.ui.WebDriverWait(browser, 5).until(visibilityOfElementLocated(By.cssSelector("._ksh")));
        Assert.assertThat(browser.findElement(By.cssSelector("._ksh")).getText(), CoreMatchers.containsString("Eugen (Baeldung)"));
    }
}

