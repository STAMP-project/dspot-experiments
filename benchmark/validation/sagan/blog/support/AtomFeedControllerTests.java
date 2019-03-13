package sagan.blog.support;


import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ExtendedModelMap;
import sagan.blog.Post;
import sagan.blog.PostCategory;
import sagan.support.DateFactory;


/**
 * Unit tests for {@link AtomFeedController}.
 */
public class AtomFeedControllerTests {
    public static final PostCategory TEST_CATEGORY = PostCategory.ENGINEERING;

    @Mock
    private BlogService blogService;

    @Mock
    private SiteUrl siteUrl;

    @Mock
    private DateFactory dateFactory;

    private AtomFeedController controller;

    private ExtendedModelMap model = new ExtendedModelMap();

    private Page<Post> page;

    private List<Post> posts = new ArrayList<>();

    private HttpServletResponse response = new MockHttpServletResponse();

    @SuppressWarnings("unchecked")
    @Test
    public void postsInModelForAllPublishedPosts() {
        controller.listPublishedPosts(model, response);
        MatcherAssert.assertThat(((List<Post>) (model.get("posts"))), Matchers.is(posts));
    }

    @Test
    public void feedMetadataInModelForAllPublishedPosts() {
        controller.listPublishedPosts(model, response);
        MatcherAssert.assertThat(((String) (model.get("feed-title"))), Matchers.is("Spring"));
        MatcherAssert.assertThat(((String) (model.get("feed-path"))), Matchers.is("/blog.atom"));
        MatcherAssert.assertThat(((String) (model.get("blog-path"))), Matchers.is("/blog"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void postsInModelForPublishedCategoryPosts() {
        controller.listPublishedPostsForCategory(AtomFeedControllerTests.TEST_CATEGORY, model, response);
        MatcherAssert.assertThat(((List<Post>) (model.get("posts"))), Matchers.is(posts));
    }

    @Test
    public void feedMetadataInModelForCategoryPosts() {
        controller.listPublishedPostsForCategory(AtomFeedControllerTests.TEST_CATEGORY, model, response);
        MatcherAssert.assertThat(((String) (model.get("feed-title"))), Matchers.is(("Spring " + (AtomFeedControllerTests.TEST_CATEGORY.getDisplayName()))));
        MatcherAssert.assertThat(((String) (model.get("feed-path"))), Matchers.is((("/blog/category/" + (AtomFeedControllerTests.TEST_CATEGORY.getUrlSlug())) + ".atom")));
        MatcherAssert.assertThat(((String) (model.get("blog-path"))), Matchers.is(("/blog/category/" + (AtomFeedControllerTests.TEST_CATEGORY.getUrlSlug()))));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void postsInModelForPublishedBroadcastPosts() {
        controller.listPublishedBroadcastPosts(model, response);
        MatcherAssert.assertThat(((List<Post>) (model.get("posts"))), Matchers.is(posts));
    }

    @Test
    public void feedMetadataInModelForBroadcastPosts() {
        controller.listPublishedBroadcastPosts(model, response);
        MatcherAssert.assertThat(((String) (model.get("feed-title"))), Matchers.is("Spring Broadcasts"));
        MatcherAssert.assertThat(((String) (model.get("feed-path"))), Matchers.is("/blog/broadcasts.atom"));
        MatcherAssert.assertThat(((String) (model.get("blog-path"))), Matchers.is("/blog/broadcasts"));
    }
}

