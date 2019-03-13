package sagan.staticpage.support;


import StaticPagePathFinder.PagePaths;
import java.io.IOException;
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import sagan.search.support.CrawlerService;
import sagan.search.support.SearchService;
import sagan.support.StaticPagePathFinder;


@RunWith(MockitoJUnitRunner.class)
public class StaticPageIndexerTests {
    @Mock
    private CrawlerService crawlerService;

    @Mock
    private SearchService searchService;

    @Mock
    private StaticPagePathFinder staticPagePathFinder;

    private StaticPageIndexer indexer;

    @Test
    public void itReturnsStaticPages() throws IOException {
        StaticPagePathFinder.PagePaths paths = new StaticPagePathFinder.PagePaths("/template/path", "/foo");
        BDDMockito.given(staticPagePathFinder.findPaths()).willReturn(Arrays.asList(paths));
        Iterable<String> pages = indexer.indexableItems();
        MatcherAssert.assertThat(pages, Matchers.containsInAnyOrder("http://www.example.com/foo"));
    }

    @Test
    public void itIgnoresErrorPages() throws IOException {
        StaticPagePathFinder.PagePaths paths = new StaticPagePathFinder.PagePaths("/template/path", "/foo");
        StaticPagePathFinder.PagePaths error1 = new StaticPagePathFinder.PagePaths("/template/path", "/error");
        StaticPagePathFinder.PagePaths error2 = new StaticPagePathFinder.PagePaths("/template/path", "/404");
        StaticPagePathFinder.PagePaths error3 = new StaticPagePathFinder.PagePaths("/template/path", "/500");
        BDDMockito.given(staticPagePathFinder.findPaths()).willReturn(Arrays.asList(paths, error1, error2, error3));
        Iterable<String> pages = indexer.indexableItems();
        MatcherAssert.assertThat(pages, Matchers.containsInAnyOrder("http://www.example.com/foo"));
    }
}

