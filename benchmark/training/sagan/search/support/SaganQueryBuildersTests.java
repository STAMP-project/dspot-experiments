package sagan.search.support;


import Search.Builder;
import com.google.gson.Gson;
import io.searchbox.core.Search;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StreamUtils;


public class SaganQueryBuildersTests {
    @Test
    public void deleteUnsupportedProjectEntries() {
        String projectId = "spring-framework";
        List<String> supportedVersions = Arrays.asList("4.1.0.RELEASE", "4.0.0.RELEASE");
        String expected = "{" + ((((((((((((((("\"query\":{" + "\"filtered\":{") + "\"query\":{\"match_all\":{}},") + "\"filter\":{") + "\"and\":{\"filters\":[") + "{\"term\":{\"projectId\":\"spring-framework\"}},") + "{\"not\":{\"filter\":{") + "\"or\":{\"filters\":[") + "{\"term\":{\"version\":\"4.1.0.RELEASE\"}},") + "{\"term\":{\"version\":\"4.0.0.RELEASE\"}}") + "]}") + "}}}") + "]}") + "}") + "}") + "}}");
        FilteredQueryBuilder builder = SaganQueryBuilders.matchUnsupportedProjectEntries(projectId, supportedVersions);
        String result = SaganQueryBuilders.wrapQuery(builder.toString());
        MatcherAssert.assertThat(result.replaceAll("[\\s|\\r|\\n]", ""), Matchers.equalTo(expected));
    }

    @Test
    public void fullTextSearch() throws Exception {
        String query = "spring boot";
        List<String> filters = Arrays.asList("Projects/Api", "Projects/Reference", "Blog/Engineering", "Projects/Reactor Project/1.1.0.RELEASE");
        Search.Builder builder = SaganQueryBuilders.fullTextSearch(query, new PageRequest(0, 10), filters);
        String expected = StreamUtils.copyToString(new ClassPathResource("/fulltext-query.json", getClass()).getInputStream(), Charset.forName("UTF-8"));
        String actual = builder.build().getData(new Gson());
        MatcherAssert.assertThat(actual.replaceAll("[\\s|\\r|\\n]", ""), Matchers.equalTo(expected.replaceAll("[\\s|\\r|\\n]", "")));
    }
}

