package sagan.search.support;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.ui.ExtendedModelMap;


public class SearchControllerTests {
    @Mock
    private SearchService searchService;

    private SearchController controller;

    private ExtendedModelMap model = new ExtendedModelMap();

    private Page<SearchResult> resultsPage;

    private List<SearchResult> entries = new ArrayList<>();

    private SearchForm searchForm = new SearchForm();

    @Test
    public void search_providesQueryInModel() {
        searchForm.setQ("searchTerm");
        controller.search(searchForm, 1, model);
        Assert.assertThat(((SearchForm) (model.get("searchForm"))), equalTo(searchForm));
    }

    @Test
    public void search_providesPaginationInfoInModel() {
        searchForm.setQ("searchTerm");
        controller.search(searchForm, 1, model);
        Assert.assertThat(model.get("paginationInfo"), is(notNullValue()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void search_providesResultsInModel() {
        searchForm.setQ("searchTerm");
        controller.search(searchForm, 1, model);
        Assert.assertThat(((List<SearchResult>) (model.get("results"))), equalTo(entries));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void search_providesAllResultsForBlankQuery() {
        searchForm.setQ("");
        controller.search(searchForm, 1, model);
        Assert.assertThat(((List<SearchResult>) (model.get("results"))), equalTo(entries));
    }
}

