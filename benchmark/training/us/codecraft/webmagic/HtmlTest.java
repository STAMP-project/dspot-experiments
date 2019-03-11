package us.codecraft.webmagic;


import org.junit.Test;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;


/**
 *
 *
 * @author code4crafter@gmail.com <br>
Date: 13-4-21
Time: ??8:42
 */
public class HtmlTest {
    @Test
    public void testRegexSelector() {
        Html selectable = new Html("aaaaaaab");
        assertThat(selectable.regex("(a+b)").replace("aa(a)", "$1bb").toString()).isEqualTo("abbabbab");
    }

    @Test
    public void testEnableJsoupHtmlEntityEscape() throws Exception {
        Html html = new Html("aaaaaaa&b");
        assertThat(html.regex("(aaaaaaa&amp;b)").toString()).isEqualTo("aaaaaaa&amp;b");
    }

    @Test
    public void testAHrefExtract() {
        Html html = new Html("<a data-tip=\"p$t$xxx\" href=\"/xx/xx\">xx</a>");
        assertThat(html.links().all()).contains("/xx/xx");
    }

    @Test
    public void testNthNodesGet() {
        Html html = new Html("<a data-tip=\"p$t$xxx\" href=\"/xx/xx\">xx</a>");
        assertThat(html.xpath("//a[1]/@href").get()).isEqualTo("/xx/xx");
        Selectable selectable = html.xpath("//a[1]").nodes().get(0);
        assertThat(selectable.xpath("/a/@href").get()).isEqualTo("/xx/xx");
    }

    @Test
    public void testGetHrefsByJsoup() {
        Html html = new Html("<html><a href='issues'>issues</a><img src='webmagic.jpg'/></html>", "https://github.com/code4craft/webmagic/");
        assertThat(html.xpath("//a[1]/@abs:href").get()).isEqualTo("https://github.com/code4craft/webmagic/issues");
        assertThat(html.xpath("//img/@abs:src").get()).isEqualTo("https://github.com/code4craft/webmagic/webmagic.jpg");
        html = new Html("<html><base href='https://github.com/code4craft/webmagic/'><a href='issues'>issues</a><img src='webmagic.jpg'/></base></html>");
        assertThat(html.xpath("//a[1]/@abs:href").get()).isEqualTo("https://github.com/code4craft/webmagic/issues");
        assertThat(html.xpath("//img/@abs:src").get()).isEqualTo("https://github.com/code4craft/webmagic/webmagic.jpg");
    }
}

