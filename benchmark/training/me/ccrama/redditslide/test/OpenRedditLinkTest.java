package me.ccrama.redditslide.test;


import RedditLinkType.COMMENT_PERMALINK;
import RedditLinkType.HOME;
import RedditLinkType.LIVE;
import RedditLinkType.OTHER;
import RedditLinkType.SEARCH;
import RedditLinkType.SHORTENED;
import RedditLinkType.SUBMISSION;
import RedditLinkType.SUBMISSION_WITHOUT_SUB;
import RedditLinkType.SUBREDDIT;
import RedditLinkType.USER;
import RedditLinkType.WIKI;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class OpenRedditLinkTest {
    @Test
    public void detectsShortened() {
        Assert.assertThat(getType(formatURL("https://redd.it/eorhm/")), CoreMatchers.is(SHORTENED));
    }

    @Test
    public void detectsWiki() {
        Assert.assertThat(getType(formatURL("https://www.reddit.com/r/Android/wiki/index")), CoreMatchers.is(WIKI));
        Assert.assertThat(getType(formatURL("https://www.reddit.com/r/Android/help")), CoreMatchers.is(WIKI));
        Assert.assertThat(getType(formatURL("https://reddit.com/help")), CoreMatchers.is(WIKI));
    }

    @Test
    public void detectsComment() {
        Assert.assertThat(getType(formatURL("https://www.reddit.com/r/announcements/comments/eorhm/reddit_30_less_typing/c19qk6j")), CoreMatchers.is(COMMENT_PERMALINK));
        Assert.assertThat(getType(formatURL("https://www.reddit.com/r/announcements/comments/eorhm//c19qk6j")), CoreMatchers.is(COMMENT_PERMALINK));
    }

    @Test
    public void detectsSubmission() {
        Assert.assertThat(getType(formatURL("https://www.reddit.com/r/announcements/comments/eorhm/reddit_30_less_typing/")), CoreMatchers.is(SUBMISSION));
    }

    @Test
    public void detectsSubmissionWithoutSub() {
        Assert.assertThat(getType(formatURL("https://www.reddit.com/comments/eorhm/reddit_30_less_typing/")), CoreMatchers.is(SUBMISSION_WITHOUT_SUB));
    }

    @Test
    public void detectsSubreddit() {
        Assert.assertThat(getType(formatURL("https://www.reddit.com/r/android")), CoreMatchers.is(SUBREDDIT));
    }

    @Test
    public void detectsSearch() {
        // assertThat(getType(formatURL("https://www.reddit.com/search?q=test")),
        // is(RedditLinkType.SEARCH));
        Assert.assertThat(getType(formatURL("https://www.reddit.com/r/Android/search?q=test&restrict_sr=on&sort=relevance&t=all")), CoreMatchers.is(SEARCH));
    }

    @Test
    public void detectsUser() {
        Assert.assertThat(getType(formatURL("https://www.reddit.com/u/l3d00m")), CoreMatchers.is(USER));
    }

    @Test
    public void detectsHome() {
        Assert.assertThat(getType(formatURL("https://www.reddit.com/")), CoreMatchers.is(HOME));
    }

    @Test
    public void detectsOther() {
        Assert.assertThat(getType(formatURL("https://www.reddit.com/r/pics/about/moderators")), CoreMatchers.is(OTHER));
        Assert.assertThat(getType(formatURL("https://www.reddit.com/live/x9gf3donjlkq/discussions")), CoreMatchers.is(OTHER));
        Assert.assertThat(getType(formatURL("https://www.reddit.com/live/x9gf3donjlkq/contributors")), CoreMatchers.is(OTHER));
    }

    @Test
    public void detectsLive() {
        Assert.assertThat(getType(formatURL("https://www.reddit.com/live/x9gf3donjlkq")), CoreMatchers.is(LIVE));
    }

    @Test
    public void formatsBasic() {
        Assert.assertThat(formatURL("https://www.reddit.com/live/wbjbjba8zrl6"), CoreMatchers.is("reddit.com/live/wbjbjba8zrl6"));
    }

    @Test
    public void formatsNp() {
        Assert.assertThat(formatURL("https://np.reddit.com/live/wbjbjba8zrl6"), CoreMatchers.is("npreddit.com/live/wbjbjba8zrl6"));
    }

    @Test
    public void formatsSubdomains() {
        Assert.assertThat(formatURL("https://beta.reddit.com/"), CoreMatchers.is(""));
        Assert.assertThat(formatURL("https://blog.reddit.com/"), CoreMatchers.is(""));
        Assert.assertThat(formatURL("https://code.reddit.com/"), CoreMatchers.is(""));
        // https://www.reddit.com/r/modnews/comments/4z2nic/upcoming_change_updates_to_modredditcom/
        Assert.assertThat(formatURL("https://mod.reddit.com/"), CoreMatchers.is(""));
        // https://www.reddit.com/r/changelog/comments/49jjb7/reddit_change_click_events_on_outbound_links/
        Assert.assertThat(formatURL("https://out.reddit.com/"), CoreMatchers.is(""));
        Assert.assertThat(formatURL("https://store.reddit.com/"), CoreMatchers.is(""));
        Assert.assertThat(formatURL("https://pay.reddit.com/"), CoreMatchers.is("reddit.com"));
        Assert.assertThat(formatURL("https://ssl.reddit.com/"), CoreMatchers.is("reddit.com"));
        Assert.assertThat(formatURL("https://en-gb.reddit.com/"), CoreMatchers.is("reddit.com"));
        Assert.assertThat(formatURL("https://us.reddit.com/"), CoreMatchers.is("reddit.com"));
    }

    @Test
    public void formatsSubreddit() {
        Assert.assertThat(formatURL("/r/android"), CoreMatchers.is("reddit.com/r/android"));
        Assert.assertThat(formatURL("https://android.reddit.com"), CoreMatchers.is("reddit.com/r/android"));
    }

    @Test
    public void formatsWiki() {
        Assert.assertThat(formatURL("https://reddit.com/help"), CoreMatchers.is("reddit.com/r/reddit.com/wiki"));
        Assert.assertThat(formatURL("https://reddit.com/help/registration"), CoreMatchers.is("reddit.com/r/reddit.com/wiki/registration"));
        Assert.assertThat(formatURL("https://www.reddit.com/r/android/wiki/index"), CoreMatchers.is("reddit.com/r/android/wiki/index"));
    }

    @Test
    public void formatsProtocol() {
        Assert.assertThat(formatURL("http://reddit.com"), CoreMatchers.is("reddit.com"));
        Assert.assertThat(formatURL("https://reddit.com"), CoreMatchers.is("reddit.com"));
    }
}

