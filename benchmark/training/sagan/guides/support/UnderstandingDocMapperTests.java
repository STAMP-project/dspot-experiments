package sagan.guides.support;


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.Test;
import sagan.guides.UnderstandingDoc;
import sagan.search.types.GuideDoc;
import sagan.search.types.SearchEntry;
import sagan.support.Fixtures;


/**
 * Unit tests for {@link UnderstandingDocMapper}.
 */
public class UnderstandingDocMapperTests {
    private final UnderstandingDocMapper mapper = new UnderstandingDocMapper();

    private final UnderstandingDoc doc = new UnderstandingDoc("foo", "<h1>Understanding: foo</h1><p>content</p>");

    private final GuideDoc entry = mapper.map(doc);

    @Test
    public void titleIsSubject() throws Exception {
        MatcherAssert.assertThat(entry.getTitle(), Matchers.equalTo("foo"));
    }

    @Test
    public void rawContentDoesNotHaveAnyHtml() throws Exception {
        MatcherAssert.assertThat(entry.getRawContent(), Matchers.equalTo("Understanding: foo content"));
    }

    @Test
    public void facetTypeIsUnderstanding() throws Exception {
        MatcherAssert.assertThat(entry.getFacetPaths(), Matchers.containsInAnyOrder("Guides", "Guides/Understanding"));
    }

    @Test
    public void summaryIsFirst500Characters() throws Exception {
        String content = Fixtures.load("/fixtures/understanding/amqp/README.html");
        UnderstandingDoc guide = new UnderstandingDoc("foo", content);
        SearchEntry entry = mapper.map(guide);
        MatcherAssert.assertThat(entry.getSummary().length(), Matchers.equalTo(500));
    }

    @Test
    public void summaryIsWholeContentIfLessThan500Characters() throws Exception {
        MatcherAssert.assertThat(entry.getSummary(), Matchers.equalTo("Understanding: foo content"));
    }

    @Test
    public void hasPath() throws Exception {
        MatcherAssert.assertThat(entry.getPath(), Matchers.equalTo("understanding/foo"));
    }

    @Test
    public void mapsSubTitle() throws Exception {
        MatcherAssert.assertThat(entry.getSubTitle(), IsEqual.equalTo("Understanding Doc"));
    }
}

