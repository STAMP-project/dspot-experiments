package me.ccrama.redditslide.test;


import Type.ALBUM;
import Type.DEVIANTART;
import Type.EXTERNAL;
import Type.GIF;
import Type.IMAGE;
import Type.IMGUR;
import Type.LINK;
import Type.NONE;
import Type.REDDIT;
import Type.SPOILER;
import Type.STREAMABLE;
import Type.VIDEO;
import Type.VID_ME;
import me.ccrama.redditslide.ContentType;
import me.ccrama.redditslide.Reddit;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ContentTypeTest {
    @Test
    public void comparesHosts() {
        Assert.assertTrue(ContentType.hostContains("www.example.com", "example.com"));
        Assert.assertTrue(ContentType.hostContains("www.example.com", "www.example.com"));
        Assert.assertTrue(ContentType.hostContains("www.example.com", "no-match", "example.com"));
        Assert.assertTrue(ContentType.hostContains("www.example.com", "", null, "example.com"));
        Assert.assertTrue(ContentType.hostContains("www.example.com", "example.com", "no-match"));
        Assert.assertTrue(ContentType.hostContains("example.com.www.example.com", "example.com"));
        Assert.assertFalse(ContentType.hostContains("www.example.com", "www.example.com.au"));
        Assert.assertFalse(ContentType.hostContains("www.example.com", "www.example"));
        Assert.assertFalse(ContentType.hostContains("www.example.com", "notexample.com"));
        Assert.assertFalse(ContentType.hostContains("www.example.com", ""));
    }

    @Test
    public void detectsAlbum() {
        Assert.assertThat(ContentType.getContentType("http://www.imgur.com/a/duARTe"), CoreMatchers.is(ALBUM));
        Assert.assertThat(ContentType.getContentType("https://imgur.com/gallery/DmXJ4"), CoreMatchers.is(ALBUM));
        Assert.assertThat(ContentType.getContentType("http://imgur.com/82UIrJk,dIjBFjv"), CoreMatchers.is(ALBUM));
    }

    @Test
    public void detectsExternal() {
        Assert.assertThat(ContentType.getContentType("https://twitter.com/jaffathecake/status/718071903378735105?s=09"), CoreMatchers.is(EXTERNAL));
        Assert.assertThat(ContentType.getContentType("https://github.com/ccrama/Slide"), CoreMatchers.is(EXTERNAL));
        Assert.assertThat(ContentType.getContentType("http://example.com/path/that/matches"), CoreMatchers.is(EXTERNAL));
        Assert.assertThat(ContentType.getContentType("http://example.com/path"), CoreMatchers.is(EXTERNAL));
        Assert.assertThat(ContentType.getContentType("http://subdomain.example.com/path"), CoreMatchers.is(EXTERNAL));
        Assert.assertThat(ContentType.getContentType("http://subdomain.twitter.com"), CoreMatchers.is(EXTERNAL));
        // t.co NOT t.com
        Assert.assertThat(ContentType.getContentType("https://t.com"), CoreMatchers.is(CoreMatchers.not(EXTERNAL)));
        Assert.assertThat(ContentType.getContentType("example.com/differentpath"), CoreMatchers.is(CoreMatchers.not(EXTERNAL)));
        Assert.assertThat(ContentType.getContentType("https://example.com"), CoreMatchers.is(CoreMatchers.not(EXTERNAL)));
    }

    @Test
    public void detectsGif() {
        Assert.assertThat(ContentType.getContentType("https://i.imgur.com/33YIg0B.gifv"), CoreMatchers.is(GIF));
        Assert.assertThat(ContentType.getContentType("https://i.imgur.com/33YIg0B.gif"), CoreMatchers.is(GIF));
        Assert.assertThat(ContentType.getContentType("i.imgur.com/33YIg0B.gif?args=should&not=matter"), CoreMatchers.is(GIF));
        Assert.assertThat(ContentType.getContentType("https://i.imgur.com/33YIg0B.gifnot"), CoreMatchers.is(CoreMatchers.not(GIF)));
        Assert.assertThat(ContentType.getContentType("https://fat.gfycat.com/EcstaticLegitimateAnemone.webm"), CoreMatchers.is(GIF));
        Assert.assertThat(ContentType.getContentType("https://thumbs.gfycat.com/EcstaticLegitimateAnemone-mobile.mp4"), CoreMatchers.is(GIF));
        Assert.assertThat(ContentType.getContentType("https://gfycat.com/BogusAmpleArmednylonshrimp"), CoreMatchers.is(GIF));
    }

    @Test
    public void detectsImage() {
        Assert.assertThat(ContentType.getContentType("https://i.imgur.com/FGtUo6c.jpg"), CoreMatchers.is(IMAGE));
        Assert.assertThat(ContentType.getContentType("https://i.imgur.com/FGtUo6c.png"), CoreMatchers.is(IMAGE));
        Assert.assertThat(ContentType.getContentType("https://i.imgur.com/FGtUo6c.png?moo=1"), CoreMatchers.is(IMAGE));
        Assert.assertThat(ContentType.getContentType("https://i.reddituploads.com/289b451dc4bf4306878852f83b5cf6f9?fit=max&h=1536&w=1536&s=103e17990aa7084727ea43cda02c318b"), CoreMatchers.is(IMAGE));
    }

    @Test
    public void detectsImgur() {
        Assert.assertThat(ContentType.getContentType("https://i.imgur.com/33YIg0B"), CoreMatchers.is(IMGUR));
    }

    @Test
    public void detectsSpoiler() {
        Assert.assertThat(ContentType.getContentType("/s"), CoreMatchers.is(SPOILER));
        Assert.assertThat(ContentType.getContentType("/sp"), CoreMatchers.is(SPOILER));
        Assert.assertThat(ContentType.getContentType("/spoiler"), CoreMatchers.is(SPOILER));
        Assert.assertThat(ContentType.getContentType("#s"), CoreMatchers.is(SPOILER));
    }

    @Test
    public void detectsReddit() {
        Assert.assertThat(ContentType.getContentType("https://www.reddit.com/r/todayilearned/comments/42wgbg/til_the_tshirt_was_invented_in_1904_and_marketed/"), CoreMatchers.is(REDDIT));
        Assert.assertThat(ContentType.getContentType("https://www.reddit.com/42wgbg/"), CoreMatchers.is(REDDIT));
        Assert.assertThat(ContentType.getContentType("https://www.reddit.com/r/live/"), CoreMatchers.is(REDDIT));
        Assert.assertThat(ContentType.getContentType("https://www.reddit.com"), CoreMatchers.is(REDDIT));
        Assert.assertThat(ContentType.getContentType("redd.it/eorhm"), CoreMatchers.is(REDDIT));
        Assert.assertThat(ContentType.getContentType("/r/Android"), CoreMatchers.is(REDDIT));
        Assert.assertThat(ContentType.getContentType("https://www.reddit.com/r/Android/wiki/index"), CoreMatchers.is(REDDIT));
        Assert.assertThat(ContentType.getContentType("https://www.reddit.com/r/Android/help"), CoreMatchers.is(REDDIT));
        Assert.assertThat(ContentType.getContentType("https://www.reddit.com/live/wbjbjba8zrl6"), CoreMatchers.is(REDDIT));
    }

    @Test
    public void detectsWithoutScheme() {
        // Capitalised
        Assert.assertThat(ContentType.getContentType("Https://google.com"), CoreMatchers.is(CoreMatchers.not(NONE)));
        // Missing
        Assert.assertThat(ContentType.getContentType("google.com"), CoreMatchers.is(CoreMatchers.not(NONE)));
        // Protocol relative
        Assert.assertThat(ContentType.getContentType("//google.com"), CoreMatchers.is(CoreMatchers.not(NONE)));
    }

    @Test
    public void detectsVideo() {
        Reddit.videoPlugin = true;
        Assert.assertThat(ContentType.getContentType("https://www.youtube.com/watch?v=lX_pF03vCSU"), CoreMatchers.is(VIDEO));
        Assert.assertThat(ContentType.getContentType("https://youtu.be/lX_pF03vCSU"), CoreMatchers.is(VIDEO));
        Assert.assertThat(ContentType.getContentType("https://www.gifyoutube.com/"), CoreMatchers.is(CoreMatchers.not(VIDEO)));
        Reddit.videoPlugin = false;
        Assert.assertThat(ContentType.getContentType("https://www.youtube.com/watch?v=lX_pF03vCSU"), CoreMatchers.is(CoreMatchers.not(VIDEO)));
        Assert.assertThat(ContentType.getContentType("https://youtu.be/lX_pF03vCSU"), CoreMatchers.is(CoreMatchers.not(VIDEO)));
    }

    @Test
    public void detectsVidme() {
        Assert.assertThat(ContentType.getContentType("https://vid.me/6tPY"), CoreMatchers.is(VID_ME));
    }

    @Test
    public void detectsStreamable() {
        Assert.assertThat(ContentType.getContentType("https://streamable.com/l41f"), CoreMatchers.is(STREAMABLE));
    }

    @Test
    public void detectsDeviantart() {
        Assert.assertThat(ContentType.getContentType("http://manweri.deviantart.com/art/A-centaur-in-disguise-179507382"), CoreMatchers.is(DEVIANTART));
    }

    @Test
    public void detectsLink() {
        Assert.assertThat(ContentType.getContentType("https://stackoverflow.com/"), CoreMatchers.is(LINK));
    }

    @Test
    public void detectsNone() {
        Assert.assertThat(ContentType.getContentType(""), CoreMatchers.is(NONE));
    }
}

