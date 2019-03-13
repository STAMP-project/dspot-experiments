package sagan.search.support;


import com.google.gson.Gson;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class SearchResultParser_FacetsTests {
    private static String RESULT_STRING = "{\n" + ((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("  \"took\": 185,\n" + "  \"timed_out\": false,\n") + "  \"_shards\": {\n") + "    \"total\": 1,\n") + "    \"successful\": 1,\n") + "    \"failed\": 0\n") + "  },\n") + "  \"hits\": {\n") + "    \"total\": 0,\n") + "    \"max_score\": 0.5340888,\n") + "    \"hits\": []\n") + "  },\n") + "  \"facets\": {\n") + "    \"facet_paths_result\": {\n") + "      \"_type\": \"terms\",\n") + "      \"missing\": 0,\n") + "      \"total\": 15,\n") + "      \"other\": 0,\n") + "      \"terms\": [{\n") + "        \"term\": \"Blog\",\n") + "        \"count\": 2\n") + "      }, {\n") + "        \"term\": \"Guides\",\n") + "        \"count\": 3\n") + "      }, {\n") + "        \"term\": \"Guides/GettingStarted\",\n") + "        \"count\": 2\n") + "      }, {\n") + "        \"term\": \"Guides/Tutorials\",\n") + "        \"count\": 1\n") + "      }, {\n") + "        \"term\": \"Guides/Reference Apps\",\n") + "        \"count\": 1\n") + "      }, {\n") + "        \"term\": \"Projects/Spring Framework\",\n") + "        \"count\": 1\n") + "      }, {\n") + "        \"term\": \"Projects/Spring Framework/3.1.3.RELEASE\",\n") + "        \"count\": 1\n") + "      }, {\n") + "        \"term\": \"Projects/Spring Framework/3.2.3.RELEASE\",\n") + "        \"count\": 1\n") + "      }, {\n") + "        \"term\": \"Projects/Spring Framework/4.0.0.M1\",\n") + "        \"count\": 1\n") + "      },\n") + "{\"term\":\"Projects/Spring Security\",\"count\":797},\n") + "{\"term\":\"Projects/Spring Security/3.1.4.RELEASE\",\"count\":157},\n") + "{\"term\":\"Projects/Spring Security/3.2.0.M2\",\"count\":640},\n") + "{\"term\":\"Projects/Spring Security Kerberos\",\"count\":12},\n") + "{\"term\":\"Projects/Spring Security Kerberos/1.0.0.CI-SNAPSHOT\",\"count\":12},\n") + "{\"term\":\"Projects/Spring Security SAML\",\"count\":105},\n") + "{\"term\":\"Projects/Spring Security SAML/1.0.0.RC2\",\"count\":105},") + "      {\n") + "        \"term\": \"Projects\",\n") + "        \"count\": 1\n") + "      }]\n") + "    }\n") + "  }\n") + "}\n");

    private Gson gson = new Gson();

    private SearchResultParser searchResultParser;

    private SearchResults searchResults;

    @Test
    public void returnTopLevelFacets() {
        List<SearchFacet> facets = searchResults.getFacets();
        MatcherAssert.assertThat(facets.size(), Matchers.equalTo(3));
        MatcherAssert.assertThat(facets.get(0).getName(), Matchers.equalTo("Blog"));
        MatcherAssert.assertThat(facets.get(1).getName(), Matchers.equalTo("Guides"));
        MatcherAssert.assertThat(facets.get(2).getName(), Matchers.equalTo("Projects"));
    }

    @Test
    public void returnNestedFacets() {
        SearchFacet guidesFacet = searchResults.getFacets().get(1);
        MatcherAssert.assertThat(guidesFacet.getFacets().size(), Matchers.equalTo(3));
        SearchFacet projectsFacet = searchResults.getFacets().get(2);
        List<SearchFacet> projectsFacetFacets = projectsFacet.getFacets();
        MatcherAssert.assertThat(projectsFacetFacets.size(), Matchers.equalTo(4));
        MatcherAssert.assertThat(projectsFacetFacets.get(0).getName(), Matchers.equalTo("Spring Framework"));
        MatcherAssert.assertThat(projectsFacetFacets.get(1).getName(), Matchers.equalTo("Spring Security"));
        MatcherAssert.assertThat(projectsFacetFacets.get(2).getName(), Matchers.equalTo("Spring Security Kerberos"));
        MatcherAssert.assertThat(projectsFacetFacets.get(3).getName(), Matchers.equalTo("Spring Security SAML"));
    }
}

