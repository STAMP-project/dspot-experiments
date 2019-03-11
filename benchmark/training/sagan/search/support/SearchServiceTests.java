package sagan.search.support;


import SearchType.BLOG_POST;
import com.google.gson.Gson;
import io.searchbox.action.Action;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Index;
import java.util.Collections;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import sagan.search.SearchException;
import sagan.search.types.SearchEntry;


public class SearchServiceTests {
    private JestClient jestClient = Mockito.mock(JestClient.class);

    private SearchService searchService = new SearchService(jestClient, Mockito.mock(SearchResultParser.class), new Gson());

    private SearchEntry entry;

    @Test(expected = SearchException.class)
    public void saveToIndexExceptionHandling() throws Exception {
        throwsException();
        searchService.saveToIndex(entry);
    }

    @Test(expected = SearchException.class)
    public void searchExceptionHandling() throws Exception {
        throwsException();
        searchService.search("foo", Mockito.mock(Pageable.class), Collections.<String>emptyList());
    }

    @Test(expected = SearchException.class)
    public void removeFromIndexExceptionHandling() throws Exception {
        throwsException();
        searchService.removeFromIndex(entry);
    }

    @Test
    public void usesTheSearchEntriesType() throws Exception {
        BDDMockito.given(jestClient.execute(ArgumentMatchers.any(SearchServiceTests.GenericAction.class))).willReturn(Mockito.mock(JestResult.class));
        searchService.saveToIndex(entry);
        Mockito.verify(jestClient).execute(ArgumentMatchers.argThat(new ArgumentMatcher<SearchServiceTests.GenericAction>() {
            @Override
            public boolean matches(Object item) {
                Index action = ((Index) (item));
                return action.getType().equals(BLOG_POST.toString());
            }
        }));
    }

    @Test
    public void handlesNullFilters() throws Exception {
        BDDMockito.given(jestClient.execute(ArgumentMatchers.any(SearchServiceTests.GenericAction.class))).willReturn(Mockito.mock(JestResult.class));
        searchService.search("foo", Mockito.mock(Pageable.class), null);
    }

    private interface GenericAction extends Action<JestResult> {}
}

