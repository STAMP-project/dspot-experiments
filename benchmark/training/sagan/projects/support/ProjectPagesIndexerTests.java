package sagan.projects.support;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import sagan.projects.Project;
import sagan.search.support.CrawlerService;
import sagan.search.support.SearchService;


@RunWith(MockitoJUnitRunner.class)
public class ProjectPagesIndexerTests {
    private final Project springProject = new Project("spring", "Spring Project", "http://www.example.com/repo/spring-project", "http://www.example.com/spring-project", Collections.emptyList(), "release");

    private final Project springFramework = new Project("spring-framework", "Spring Framework", "http://www.example.com/repo/spring-framework", "https://projects.spring.io/spring-framework", Collections.emptyList(), "release");

    private final Project ioPlatform = new Project("spring-platform", "Spring IO Platform", "http://www.example.com/repo/spring-io-platform", "http://platform.spring.io/platform/", Collections.emptyList(), "release");

    @Mock
    private CrawlerService crawlerService;

    @Mock
    private ProjectMetadataService projectMetadataService;

    @Mock
    private SearchService searchService;

    private ProjectPagesIndexer indexer;

    private List<Project> projects = Arrays.asList(springProject, springFramework, ioPlatform);

    @Test
    public void itReturnsProjects() {
        BDDMockito.given(projectMetadataService.getProjectsWithReleases()).willReturn(this.projects);
        Iterable<Project> projects = indexer.indexableItems();
        MatcherAssert.assertThat(projects, Matchers.iterableWithSize(3));
    }

    @Test
    public void itOnlyCrawlsMatchingDomains() {
        indexer.indexItem(springProject);
        Mockito.verify(crawlerService, Mockito.never()).crawl(ArgumentMatchers.eq(springProject.getSiteUrl()), ArgumentMatchers.eq(0), ArgumentMatchers.any());
        indexer.indexItem(springFramework);
        Mockito.verify(crawlerService, Mockito.times(1)).crawl(ArgumentMatchers.eq(springFramework.getSiteUrl()), ArgumentMatchers.eq(0), ArgumentMatchers.any());
        indexer.indexItem(ioPlatform);
        Mockito.verify(crawlerService, Mockito.times(1)).crawl(ArgumentMatchers.eq(ioPlatform.getSiteUrl()), ArgumentMatchers.eq(0), ArgumentMatchers.any());
    }
}

