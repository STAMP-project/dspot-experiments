package us.codecraft.webmagic.configurable;


import ExpressionType.XPath;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.MockGithubDownloader;


/**
 *
 *
 * @author code4crafter@gmail.com
 * @unknown 14-4-5
 */
public class ConfigurablePageProcessorTest {
    @Test
    public void test() throws Exception {
        List<ExtractRule> extractRules = new ArrayList<ExtractRule>();
        ExtractRule extractRule = new ExtractRule();
        extractRule.setExpressionType(XPath);
        extractRule.setExpressionValue("//title");
        extractRule.setFieldName("title");
        extractRules.add(extractRule);
        extractRule = new ExtractRule();
        extractRule.setExpressionType(XPath);
        extractRule.setExpressionValue("//ul[@class='pagehead-actions']/li[1]//a[@class='social-count js-social-count']/text()");
        extractRule.setFieldName("star");
        extractRules.add(extractRule);
        ResultItems resultItems = Spider.create(new ConfigurablePageProcessor(Site.me(), extractRules)).setDownloader(new MockGithubDownloader()).get("https://github.com/code4craft/webmagic");
        assertThat(resultItems.getAll()).containsEntry("title", "<title>code4craft/webmagic ? GitHub</title>");
        assertThat(resultItems.getAll()).containsEntry("star", " 86 ");
    }
}

