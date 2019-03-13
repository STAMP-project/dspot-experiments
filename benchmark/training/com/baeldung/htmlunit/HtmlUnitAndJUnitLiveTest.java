package com.baeldung.htmlunit;


import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.Assert;
import org.junit.Test;


public class HtmlUnitAndJUnitLiveTest {
    private WebClient webClient;

    @Test
    public void givenAClient_whenEnteringBaeldung_thenPageTitleIsOk() throws Exception {
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        HtmlPage page = webClient.getPage("http://www.baeldung.com/");
        Assert.assertEquals("Baeldung | Java, Spring and Web Development tutorials", page.getTitleText());
    }
}

