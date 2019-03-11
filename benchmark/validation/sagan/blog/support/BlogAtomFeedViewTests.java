package sagan.blog.support;


import DateFactory.DEFAULT_TIME_ZONE;
import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.atom.Link;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.ui.ExtendedModelMap;
import sagan.blog.Post;
import sagan.blog.PostBuilder;


public class BlogAtomFeedViewTests {
    private ExtendedModelMap model = new ExtendedModelMap();

    private SiteUrl siteUrl;

    private AtomFeedView atomFeedView;

    private Feed feed = new Feed();

    private Calendar calendar = Calendar.getInstance(DEFAULT_TIME_ZONE);

    private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    @Test
    public void hasFeedTitleFromModel() {
        model.addAttribute("feed-title", "Spring Engineering");
        atomFeedView.buildFeedMetadata(model, feed, Mockito.mock(HttpServletRequest.class));
        Assert.assertThat(feed.getTitle(), CoreMatchers.is("Spring Engineering"));
    }

    @Test
    public void hasLinkToAssociatedBlogList() {
        String expectedBlogPath = "/blog/category/engineering";
        String expectedBlogUrl = "http://localhost:8080/blog/category/engineering";
        BDDMockito.given(siteUrl.getAbsoluteUrl(ArgumentMatchers.eq(expectedBlogPath))).willReturn(expectedBlogUrl);
        model.addAttribute("blog-path", expectedBlogPath);
        atomFeedView.buildFeedMetadata(model, feed, Mockito.mock(HttpServletRequest.class));
        Link feedLink = ((Link) (feed.getAlternateLinks().get(0)));
        Assert.assertThat(feedLink.getHref(), CoreMatchers.is(expectedBlogUrl));
        Assert.assertThat(feedLink.getRel(), CoreMatchers.is("alternate"));
    }

    @Test
    public void hasLinkToSelf() {
        String expectedFeedPath = "/blog/category/engineering.atom";
        String expectedFeedUrl = "http://localhost:8080/blog/category/engineering.atom";
        BDDMockito.given(siteUrl.getAbsoluteUrl(ArgumentMatchers.eq(expectedFeedPath))).willReturn(expectedFeedUrl);
        model.addAttribute("feed-path", expectedFeedPath);
        atomFeedView.buildFeedMetadata(model, feed, Mockito.mock(HttpServletRequest.class));
        Link feedLink = ((Link) (feed.getOtherLinks().get(0)));
        Assert.assertThat(feedLink.getHref(), CoreMatchers.is(expectedFeedUrl));
        Assert.assertThat(feedLink.getRel(), CoreMatchers.is("self"));
    }

    @Test
    public void hasCorrectIdForFeed() throws Exception {
        model.addAttribute("feed-path", "/blog.atom");
        atomFeedView.buildFeedMetadata(model, feed, request);
        Assert.assertThat(feed.getId(), CoreMatchers.is("http://spring.io/blog.atom"));
    }

    @Test
    public void feedUpdatedDateIsMostRecentPublishedPostDate() throws Exception {
        List<Post> posts = new ArrayList<>();
        buildPostsWithDate(5, posts);
        model.addAttribute("posts", posts);
        atomFeedView.buildFeedMetadata(model, feed, request);
        Post latestPost = posts.get(0);
        Assert.assertThat(feed.getUpdated(), CoreMatchers.is(latestPost.getPublishAt()));
    }

    @Test
    public void feedUpdatedDateIsNotPresentWhenThereAreNoPosts() throws Exception {
        List<Post> noPosts = new ArrayList<>();
        model.addAttribute("posts", noPosts);
        atomFeedView.buildFeedMetadata(model, feed, request);
        Assert.assertThat(feed.getUpdated(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void hasCorrectIdForEntry() throws Exception {
        calendar.set(2013, 6, 1);
        Post post = Mockito.spy(PostBuilder.post().build());
        post.setCreatedAt(calendar.getTime());
        BDDMockito.given(post.getId()).willReturn(123L);
        model.addAttribute("posts", Arrays.asList(post));
        List<Entry> entries = atomFeedView.buildFeedEntries(model, request, Mockito.mock(HttpServletResponse.class));
        Entry entry = entries.get(0);
        Assert.assertThat(entry.getId(), CoreMatchers.is("tag:springsource.org,2013-07-01:123"));
    }
}

