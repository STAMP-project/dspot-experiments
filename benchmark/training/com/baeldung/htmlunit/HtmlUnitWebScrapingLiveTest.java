package com.baeldung.htmlunit;


import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class HtmlUnitWebScrapingLiveTest {
    private WebClient webClient;

    @Test
    public void givenBaeldungArchive_whenRetrievingArticle_thenHasH1() throws Exception {
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
        final String url = "http://www.baeldung.com/full_archive";
        final HtmlPage page = webClient.getPage(url);
        final String xpath = "(//ul[@class='car-monthlisting']/li)[1]/a";
        final HtmlAnchor latestPostLink = ((HtmlAnchor) (page.getByXPath(xpath).get(0)));
        final HtmlPage postPage = latestPostLink.click();
        final List<Object> h1 = postPage.getByXPath("//h1");
        Assert.assertTrue(((h1.size()) > 0));
    }
}

