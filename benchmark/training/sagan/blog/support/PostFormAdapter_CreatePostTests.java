package sagan.blog.support;


import java.util.Date;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import sagan.blog.Post;
import sagan.blog.PostCategory;
import sagan.support.DateFactory;
import sagan.support.DateTestUtils;
import sagan.team.support.TeamRepository;


@RunWith(MockitoJUnitRunner.class)
public class PostFormAdapter_CreatePostTests {
    public static final String RENDERED_HTML = "<p>Rendered HTML</p><p>from Markdown</p>";

    public static final String RENDERED_SUMMARY = "<p>Rendered HTML</p>";

    private static final String AUTHOR_USERNAME = "author";

    private static final String AUTHOR_NAME = "mr author";

    private Post post;

    private String title = "Title";

    private String content = "Rendered HTML\n\nfrom Markdown";

    private PostCategory category = PostCategory.ENGINEERING;

    private Date publishAt = DateTestUtils.getDate("2013-07-01 12:00");

    private boolean broadcast = true;

    private boolean draft = false;

    private Date now = DateTestUtils.getDate("2013-07-01 13:00");

    @Mock
    private DateFactory dateFactory;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private PostSummary postSummary;

    @Mock
    private PostContentRenderer renderer;

    @Rule
    public ExpectedException expected = ExpectedException.none();

    private PostForm postForm;

    private PostFormAdapter adapter;

    @Test
    public void postHasCorrectUserEnteredValues() {
        Assert.assertThat(post.getTitle(), equalTo(title));
        Assert.assertThat(post.getRawContent(), equalTo(content));
        Assert.assertThat(post.getCategory(), equalTo(category));
        Assert.assertThat(post.isBroadcast(), equalTo(broadcast));
        Assert.assertThat(post.isDraft(), equalTo(draft));
        Assert.assertThat(post.getPublishAt(), equalTo(publishAt));
    }

    @Test
    public void postHasAuthor() {
        Assert.assertThat(post.getAuthor().getName(), equalTo(PostFormAdapter_CreatePostTests.AUTHOR_NAME));
    }

    @Test
    public void postHasRenderedContent() {
        Assert.assertThat(post.getRenderedContent(), equalTo(PostFormAdapter_CreatePostTests.RENDERED_HTML));
    }

    @Test
    public void postHasRenderedSummary() {
        Assert.assertThat(post.getRenderedSummary(), equalTo(PostFormAdapter_CreatePostTests.RENDERED_SUMMARY));
    }

    @Test
    public void postHasPublicSlug() {
        Assert.assertThat(post.getPublicSlug(), equalTo("2013/07/01/title"));
    }

    @Test
    public void draftWithNullPublishDate() {
        postForm.setDraft(true);
        postForm.setPublishAt(null);
        post = adapter.createPostFromPostForm(postForm, PostFormAdapter_CreatePostTests.AUTHOR_USERNAME);
        Assert.assertThat(post.getPublishAt(), is(nullValue()));
    }

    @Test
    public void postWithNullPublishDateSetsPublishAtToNow() {
        postForm.setDraft(false);
        postForm.setPublishAt(null);
        post = adapter.createPostFromPostForm(postForm, PostFormAdapter_CreatePostTests.AUTHOR_USERNAME);
        Assert.assertThat(post.getPublishAt(), equalTo(now));
    }

    @Test
    public void postCreatedDateDefaultsToNow() throws Exception {
        Assert.assertThat(post.getCreatedAt(), is(now));
    }

    @Test
    public void postCreatedDateCanBeSetFromAPostForm() throws Exception {
        Date createdAt = DateTestUtils.getDate("2013-05-23 22:58");
        postForm.setCreatedAt(createdAt);
        Post post = adapter.createPostFromPostForm(postForm, PostFormAdapter_CreatePostTests.AUTHOR_USERNAME);
        Assert.assertThat(post.getCreatedAt(), is(createdAt));
    }
}

