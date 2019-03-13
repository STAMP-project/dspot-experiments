package com.baeldung.convListVal;


import java.net.URL;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;


@RunWith(Arquillian.class)
public class ConvListValLiveTest {
    @ArquillianResource
    private URL deploymentUrl;

    private static final String WEBAPP_SRC = "src/main/webapp";

    @Drone
    WebDriver browser;

    @ArquillianResource
    URL contextPath;

    @FindBy(id = "myForm:age")
    private WebElement ageInput;

    @FindBy(id = "myForm:average")
    private WebElement averageInput;

    @FindBy(id = "myForm:send")
    private WebElement sendButton;

    @Test
    @RunAsClient
    public void givenAge_whenAgeInvalid_thenErrorMessage() throws Exception {
        browser.get(((deploymentUrl.toExternalForm()) + "ConvListVal.jsf"));
        ageInput.sendKeys("stringage");
        guardHttp(sendButton).click();
        Assert.assertTrue("Show Age error message", ((browser.findElements(By.id("myForm:ageError")).size()) > 0));
    }

    @Test
    @RunAsClient
    public void givenAverage_whenAverageInvalid_thenErrorMessage() throws Exception {
        browser.get(((deploymentUrl.toExternalForm()) + "ConvListVal.jsf"));
        averageInput.sendKeys("stringaverage");
        guardHttp(sendButton).click();
        Assert.assertTrue("Show Average error message", ((browser.findElements(By.id("myForm:averageError")).size()) > 0));
    }

    @Test
    @RunAsClient
    public void givenDate_whenDateInvalid_thenErrorMessage() throws Exception {
        browser.get(((deploymentUrl.toExternalForm()) + "ConvListVal.jsf"));
        averageInput.sendKeys("123");
        guardHttp(sendButton).click();
        Assert.assertTrue("Show Date error message", ((browser.findElements(By.id("myForm:myDateError")).size()) > 0));
    }

    @Test
    @RunAsClient
    public void givenSurname_whenSurnameMinLenghtInvalid_thenErrorMessage() throws Exception {
        browser.get(((deploymentUrl.toExternalForm()) + "ConvListVal.jsf"));
        averageInput.sendKeys("aaa");
        guardHttp(sendButton).click();
        Assert.assertTrue("Show Surname error message", ((browser.findElements(By.id("myForm:surnameError")).size()) > 0));
    }

    @Test
    @RunAsClient
    public void givenSurname_whenSurnameMaxLenghtInvalid_thenErrorMessage() throws Exception {
        browser.get(((deploymentUrl.toExternalForm()) + "ConvListVal.jsf"));
        averageInput.sendKeys("aaaaabbbbbc");
        guardHttp(sendButton).click();
        Assert.assertTrue("Show Surname error message", ((browser.findElements(By.id("myForm:surnameError")).size()) > 0));
    }
}

