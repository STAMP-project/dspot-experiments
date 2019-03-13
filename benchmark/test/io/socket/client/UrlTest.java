package io.socket.client;


import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class UrlTest {
    @Test
    public void parse() throws URISyntaxException {
        Assert.assertThat(Url.parse("http://username:password@host:8080/directory/file?query#ref").toString(), CoreMatchers.is("http://username:password@host:8080/directory/file?query#ref"));
    }

    @Test
    public void parseRelativePath() throws URISyntaxException {
        URL url = Url.parse("https://woot.com/test");
        Assert.assertThat(url.getProtocol(), CoreMatchers.is("https"));
        Assert.assertThat(url.getHost(), CoreMatchers.is("woot.com"));
        Assert.assertThat(url.getPath(), CoreMatchers.is("/test"));
    }

    @Test
    public void parseNoProtocol() throws URISyntaxException {
        URL url = Url.parse("//localhost:3000");
        Assert.assertThat(url.getProtocol(), CoreMatchers.is("https"));
        Assert.assertThat(url.getHost(), CoreMatchers.is("localhost"));
        Assert.assertThat(url.getPort(), CoreMatchers.is(3000));
    }

    @Test
    public void parseNamespace() throws URISyntaxException {
        Assert.assertThat(Url.parse("http://woot.com/woot").getPath(), CoreMatchers.is("/woot"));
        Assert.assertThat(Url.parse("http://google.com").getPath(), CoreMatchers.is("/"));
        Assert.assertThat(Url.parse("http://google.com/").getPath(), CoreMatchers.is("/"));
    }

    @Test
    public void parseDefaultPort() throws URISyntaxException {
        Assert.assertThat(Url.parse("http://google.com/").toString(), CoreMatchers.is("http://google.com:80/"));
        Assert.assertThat(Url.parse("https://google.com/").toString(), CoreMatchers.is("https://google.com:443/"));
    }

    @Test
    public void extractId() throws MalformedURLException {
        String id1 = Url.extractId("http://google.com:80/");
        String id2 = Url.extractId("http://google.com/");
        String id3 = Url.extractId("https://google.com/");
        Assert.assertThat(id1, CoreMatchers.is(id2));
        Assert.assertThat(id1, CoreMatchers.is(CoreMatchers.not(id3)));
        Assert.assertThat(id2, CoreMatchers.is(CoreMatchers.not(id3)));
    }

    @Test
    public void ipv6() throws MalformedURLException, URISyntaxException {
        String url = "http://[::1]";
        URL parsed = Url.parse(url);
        Assert.assertThat(parsed.getProtocol(), CoreMatchers.is("http"));
        Assert.assertThat(parsed.getHost(), CoreMatchers.is("[::1]"));
        Assert.assertThat(parsed.getPort(), CoreMatchers.is(80));
        Assert.assertThat(Url.extractId(url), CoreMatchers.is("http://[::1]:80"));
    }
}

