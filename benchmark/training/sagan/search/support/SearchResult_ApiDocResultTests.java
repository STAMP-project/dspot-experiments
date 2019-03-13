package sagan.search.support;


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class SearchResult_ApiDocResultTests {
    @Test
    public void resultText_returnsSummary_whenNoHighlight() {
        SearchResult result = new SearchResult("id", "title", "subTitle", "summary", "path", "apiDoc", null, "original search term");
        MatcherAssert.assertThat(result.getDisplayText(), Matchers.equalTo("summary"));
    }

    @Test
    public void resultText_returnsHighlight_whenPresent() {
        SearchResult result = new SearchResult("id", "title", "subTitle", "summary", "path", "apiDoc", "highlight", "original search term");
        MatcherAssert.assertThat(result.getDisplayText(), Matchers.equalTo("highlight"));
    }

    @Test
    public void resultText_returnsSummary_whenTermMatchesTitle_ignoringHighlight() {
        SearchResult result = new SearchResult("id", "title", "subTitle", "summary", "path", "apiDoc", "highlight", "title");
        MatcherAssert.assertThat(result.getDisplayText(), Matchers.equalTo("summary"));
    }
}

