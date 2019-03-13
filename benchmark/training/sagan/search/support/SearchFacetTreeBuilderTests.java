package sagan.search.support;


import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class SearchFacetTreeBuilderTests {
    private SearchFacetTreeBuilder builder;

    private List<SearchFacet> facets;

    @Test
    public void hasTopLevelFacets() {
        MatcherAssert.assertThat(facets.size(), Matchers.equalTo(2));
    }

    @Test
    public void hasTopLevelName() {
        MatcherAssert.assertThat(facets.get(0).getName(), Matchers.equalTo("Guides"));
    }

    @Test
    public void hasTopLevelCount() {
        MatcherAssert.assertThat(facets.get(0).getCount(), Matchers.equalTo(2));
    }

    @Test
    public void hasTwoNestedFacets() {
        MatcherAssert.assertThat(facets.get(0).getFacets().size(), Matchers.equalTo(2));
    }

    @Test
    public void hasNestedFacetName() {
        MatcherAssert.assertThat(facets.get(0).getFacets().get(0).getName(), Matchers.equalTo("GettingStarted"));
    }

    @Test
    public void hasNestedFacetCount() {
        MatcherAssert.assertThat(facets.get(0).getFacets().get(0).getCount(), Matchers.equalTo(1));
    }

    @Test
    public void hasDoubleNestedFacets() {
        MatcherAssert.assertThat(facets.get(0).getFacets().get(0).getFacets().size(), Matchers.equalTo(2));
    }

    @Test
    public void hasDoubleNestedFacetName() {
        MatcherAssert.assertThat(facets.get(0).getFacets().get(0).getFacets().get(0).getName(), Matchers.equalTo("SomethingElse"));
    }

    @Test
    public void hasDoubleNestedFacetCount() {
        MatcherAssert.assertThat(facets.get(0).getFacets().get(0).getFacets().get(0).getCount(), Matchers.equalTo(1));
    }
}

