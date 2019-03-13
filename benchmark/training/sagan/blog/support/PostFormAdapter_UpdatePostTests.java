package sagan.blog.support;


import java.util.Date;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import sagan.blog.Post;
import sagan.blog.PostBuilder;
import sagan.blog.PostCategory;
import sagan.support.DateFactory;
import sagan.support.DateTestUtils;
import sagan.team.support.TeamRepository;


@RunWith(MockitoJUnitRunner.class)
public class PostFormAdapter_UpdatePostTests {
    public static final String RENDERED_HTML = "<p>Rendered HTML</p><p>from Markdown</p>";

    public static final String SUMMARY = "<p>Rendered HTML</p>";

    private Post post;

    private String title = "Title";

    private String content = "Rendered HTML\n\nfrom Markdown";

    private PostCategory category = PostCategory.ENGINEERING;

    private boolean broadcast = true;

    private boolean draft = false;

    private Date publishAt = DateTestUtils.getDate("2013-07-01 12:00");

    private Date now = DateTestUtils.getDate("2013-07-01 13:00");

    private PostForm postForm;

    private String ORIGINAL_AUTHOR = "original author";

    @Mock
    private DateFactory dateFactory;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private PostContentRenderer renderer;

    @Mock
    private PostSummary postSummary;

    private PostFormAdapter postFormAdapter;

    @Test
    public void postRetainsOriginalAuthor() {
        MatcherAssert.assertThat(post.getAuthor().getName(), equalTo(ORIGINAL_AUTHOR));
    }

    @Test
    public void postHasRenderedContent() {
        MatcherAssert.assertThat(post.getRenderedContent(), equalTo(PostFormAdapter_UpdatePostTests.RENDERED_HTML));
    }

    @Test
    public void postHasRenderedSummary() {
        MatcherAssert.assertThat(post.getRenderedSummary(), equalTo(PostFormAdapter_UpdatePostTests.SUMMARY));
    }

    @Test
    public void draftWithNullPublishDate() {
        postForm.setDraft(true);
        postForm.setPublishAt(null);
        postFormAdapter.updatePostFromPostForm(post, postForm);
        MatcherAssert.assertThat(post.getPublishAt(), is(nullValue()));
    }

    @Test
    public void postWithNullPublishDateSetsPublishAtToNow() {
        postForm.setDraft(false);
        postForm.setPublishAt(null);
        postFormAdapter.updatePostFromPostForm(post, postForm);
        MatcherAssert.assertThat(post.getPublishAt(), equalTo(now));
    }

    @Test
    public void updatingABlogPost_doesNotChangeItsCreatedDateByDefault() throws Exception {
        Date originalDate = DateTestUtils.getDate("2009-11-20 07:00");
        Post post = PostBuilder.post().createdAt(originalDate).build();
        postFormAdapter.updatePostFromPostForm(post, postForm);
        MatcherAssert.assertThat(post.getCreatedAt(), is(originalDate));
    }

    @Test
    public void updatingABlogPost_usesTheCreatedDateFromThePostFormIfPresent() throws Exception {
        Date originalDate = DateTestUtils.getDate("2009-11-20 07:00");
        Post post = PostBuilder.post().createdAt(originalDate).build();
        Date newDate = DateTestUtils.getDate("2010-01-11 03:00");
        postForm.setCreatedAt(newDate);
        postFormAdapter.updatePostFromPostForm(post, postForm);
        MatcherAssert.assertThat(post.getCreatedAt(), is(newDate));
    }
}

