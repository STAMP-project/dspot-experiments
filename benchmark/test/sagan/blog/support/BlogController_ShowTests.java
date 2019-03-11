package sagan.blog.support;


import javax.servlet.http.HttpServletRequest;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.ui.ExtendedModelMap;
import sagan.blog.Post;
import sagan.blog.PostCategory;
import sagan.support.DateFactory;


public class BlogController_ShowTests {
    @Mock
    private BlogService blogService;

    @Mock
    private HttpServletRequest request;

    private DateFactory dateFactory;

    private BlogController controller;

    private ExtendedModelMap model = new ExtendedModelMap();

    private String viewName;

    private Post post;

    @Test
    public void providesActiveCategoryInModel() {
        MatcherAssert.assertThat(((String) (model.get("activeCategory"))), equalTo(post.getCategory().getDisplayName()));
    }

    @Test
    public void providesAllCategoriesInModel() {
        MatcherAssert.assertThat(((PostCategory[]) (model.get("categories"))), is(PostCategory.values()));
    }

    @Test
    public void providesDisqusShortnameInModel() {
        MatcherAssert.assertThat(((String) (model.get("disqusShortname"))), equalTo("spring-io-test"));
    }

    @Test
    public void viewNameIsShow() {
        MatcherAssert.assertThat(viewName, is("blog/show"));
    }

    @Test
    public void singlePostInModelForOnePost() {
        MatcherAssert.assertThat(getId(), is(post.getId()));
    }
}

