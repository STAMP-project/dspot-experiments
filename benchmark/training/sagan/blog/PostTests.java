package sagan.blog;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import sagan.support.DateTestUtils;


public class PostTests {
    PostBuilder builder = PostBuilder.post().id(1L);

    @Test
    public void slugReplacesSpacesWithDashes() {
        Assert.assertEquals("1-this-is-a-title", builder.title("This is a title").build().getAdminSlug());
    }

    @Test
    public void slugReplacesMultipleSpacesWithASingleDash() {
        Assert.assertEquals("1-this-is-a-title", builder.title("This    is a title").build().getAdminSlug());
    }

    @Test
    public void slugStripsNonAlphanumericCharacters() {
        Assert.assertEquals("1-title-1-with-characters", builder.title("Title 1, with characters\';:\\|").build().getAdminSlug());
    }

    @Test
    public void slugStripsNonAlphanumericCharactersUsedAsDividersWithSpaces() {
        Assert.assertEquals("1-title-1-something", builder.title("Title__--1/@something").build().getAdminSlug());
    }

    @Test
    public void slugStripsNewLineCharacters() {
        Assert.assertEquals("1-title-1-on-multiple-lines", builder.title("Title 1\n on multiple\nlines").build().getAdminSlug());
    }

    @Test
    public void isNotLiveIfDraft() throws ParseException {
        Post post = PostBuilder.post().draft().build();
        assertThat(post.isLiveOn(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2014-06-28 00:00")), CoreMatchers.is(false));
    }

    @Test
    public void isNotLiveIfScheduledInTheFuture() throws ParseException {
        Post post = PostBuilder.post().publishAt("2013-15-12 00:00").build();
        assertThat(post.isLiveOn(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2013-06-28 00:00")), CoreMatchers.is(false));
    }

    @Test
    public void isLiveIfPublishedInThePast() throws ParseException {
        Post post = PostBuilder.post().publishAt("2013-01-01 00:00").build();
        assertThat(post.isLiveOn(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2013-06-28 00:00")), CoreMatchers.is(true));
    }

    @Test
    public void isLiveIfPublishedNow() throws ParseException {
        Post post = PostBuilder.post().publishAt("2013-01-01 00:00").build();
        assertThat(post.isLiveOn(DateTestUtils.getDate("2013-01-01 00:00")), CoreMatchers.is(true));
    }
}

