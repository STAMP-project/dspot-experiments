package sagan.blog.support;


import java.util.ArrayList;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ExtendedModelMap;
import sagan.blog.PostCategory;
import sagan.support.DateFactory;


public class BlogController_PublishedPostsTests {
    private static final int TEST_PAGE = 1;

    @Mock
    private BlogService blogService;

    private MockHttpServletRequest request = new MockHttpServletRequest();

    private BlogController controller;

    private DateFactory dateFactory = new DateFactory();

    private ExtendedModelMap model = new ExtendedModelMap();

    private List<PostView> posts = new ArrayList<>();

    private Page<PostView> page;

    private String viewName;

    @Test
    public void providesAllCategoriesInModel() {
        MatcherAssert.assertThat(model.get("categories"), is(PostCategory.values()));
    }

    @Test
    public void providesPaginationInfoInModel() {
        MatcherAssert.assertThat(model.get("paginationInfo"), notNullValue());
    }

    @Test
    public void viewNameIsIndex() {
        MatcherAssert.assertThat(viewName, is("blog/index"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void postsInModel() {
        MatcherAssert.assertThat(((List<PostView>) (model.get("posts"))).get(0).getTitle(), is("post title"));
    }
}

