package sagan.blog.support;


import java.util.Locale;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.servlet.view.RedirectView;
import sagan.blog.PostMovedException;
import sagan.support.DateFactory;


public class BlogControllerTests {
    @Mock
    private BlogService blogService;

    private BlogController blogController;

    private DateFactory dateFactory = new DateFactory();

    private Locale defaultLocale;

    private ExtendedModelMap model = new ExtendedModelMap();

    @Test
    public void titleForBlogYearPage() throws Exception {
        blogController.listPublishedPostsForYear(2013, 1, model);
        String title = ((String) (model.get("title")));
        MatcherAssert.assertThat(title, Matchers.equalTo("Archive for 2013"));
    }

    @Test
    public void titleForBlogYearMonthPage() throws Exception {
        blogController.listPublishedPostsForYearAndMonth(2013, 1, 1, model);
        String title = ((String) (model.get("title")));
        MatcherAssert.assertThat(title, Matchers.equalTo("Archive for January 2013"));
    }

    @Test
    public void titleForBlogYearMonthDayPage() throws Exception {
        blogController.listPublishedPostsForDate(2011, 3, 23, 1, model);
        String title = ((String) (model.get("title")));
        MatcherAssert.assertThat(title, Matchers.equalTo("Archive for March 23, 2011"));
    }

    @Test
    public void handleBlogPostMovedExceptionRedirects() {
        String publicSlug = "slug";
        RedirectView result = blogController.handle(new PostMovedException(publicSlug));
        MatcherAssert.assertThat(result.getUrl(), Matchers.equalTo(("/blog/" + publicSlug)));
    }
}

