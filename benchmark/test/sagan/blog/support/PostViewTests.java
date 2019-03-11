package sagan.blog.support;


import java.text.ParseException;
import java.util.Locale;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import sagan.blog.Post;
import sagan.blog.PostBuilder;
import sagan.support.DateFactory;
import sagan.support.DateTestUtils;


public class PostViewTests {
    @Mock
    private DateFactory dateFactory;

    private Locale defaultLocale;

    private Post post;

    private PostView postView;

    @Test
    public void formattedPublishDateForUnscheduledDraft() {
        post = PostBuilder.post().draft().unscheduled().build();
        postView = PostView.of(post, dateFactory);
        assertThat(postView.getFormattedPublishDate(), Matchers.equalTo("Unscheduled"));
    }

    @Test
    public void formattedPublishDateForPublishedPosts() throws ParseException {
        post = PostBuilder.post().publishAt("2012-07-02 13:42").build();
        postView = PostView.of(post, dateFactory);
        assertThat(postView.getFormattedPublishDate(), Matchers.equalTo("July 02, 2012"));
    }

    @Test
    public void draftPath() throws ParseException {
        BDDMockito.given(dateFactory.now()).willReturn(DateTestUtils.getDate("2012-07-02 13:42"));
        post = PostBuilder.post().id(123L).title("My Post").draft().build();
        postView = PostView.of(post, dateFactory);
        assertThat(postView.getPath(), Matchers.equalTo("/admin/blog/123-my-post"));
    }

    @Test
    public void scheduledPost() throws ParseException {
        BDDMockito.given(dateFactory.now()).willReturn(DateTestUtils.getDate("2012-07-02 13:42"));
        post = PostBuilder.post().id(123L).title("My Post").publishAt("2012-07-05 13:42").build();
        postView = PostView.of(post, dateFactory);
        assertThat(postView.getPath(), Matchers.equalTo("/admin/blog/123-my-post"));
    }

    @Test
    public void publishedPost() throws ParseException {
        BDDMockito.given(dateFactory.now()).willReturn(DateTestUtils.getDate("2012-07-02 13:42"));
        post = PostBuilder.post().id(123L).title("My Post").publishAt("2012-07-01 13:42").build();
        postView = PostView.of(post, dateFactory);
        assertThat(postView.getPath(), Matchers.equalTo("/blog/2012/07/01/my-post"));
    }

    @Test
    public void knowsWhenSummaryAndContentDiffer() throws Exception {
        Post post = PostBuilder.post().renderedContent("A string").renderedSummary("A different string").build();
        postView = PostView.of(post, dateFactory);
        assertThat(postView.showReadMore(), CoreMatchers.is(true));
    }

    @Test
    public void knowsWhenSummaryAndContentAreEqual() throws Exception {
        String content = "Test content";
        Post post = PostBuilder.post().renderedContent(content).renderedSummary(content).build();
        postView = PostView.of(post, dateFactory);
        assertThat(postView.showReadMore(), CoreMatchers.is(false));
    }
}

