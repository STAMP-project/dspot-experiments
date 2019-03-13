package sagan.blog.support;


import PostCategory.ENGINEERING;
import java.text.ParseException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class PostCategoryFormatterTests {
    private PostCategoryFormatter formatter = new PostCategoryFormatter();

    @Test
    public void itConvertsUrlSlugStringsToPostCategories() throws ParseException {
        MatcherAssert.assertThat(formatter.parse(ENGINEERING.getUrlSlug(), null), Matchers.equalTo(ENGINEERING));
    }

    @Test
    public void itConvertsEnumNameStringsToPostCategories() throws ParseException {
        MatcherAssert.assertThat(formatter.parse(ENGINEERING.name(), null), Matchers.equalTo(ENGINEERING));
    }

    @Test
    public void itPrintsAStringThatCanBeParsed() throws ParseException {
        MatcherAssert.assertThat(formatter.parse(formatter.print(ENGINEERING, null), null), Matchers.equalTo(ENGINEERING));
    }
}

