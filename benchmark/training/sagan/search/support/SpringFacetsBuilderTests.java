package sagan.search.support;


import java.util.ArrayList;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.Test;


public class SpringFacetsBuilderTests {
    private List<SearchFacet> facets = new ArrayList<>();

    private SearchFacet guides = new SearchFacet("Guides", "Guides", 1);

    private SearchFacet blog = new SearchFacet("Blog", "Blog", 1);

    private SearchFacet projects = new SearchFacet("Projects", "Projects", 1);

    private SearchFacet api = new SearchFacet("Projects/Api", "Api", 1);

    private SearchFacet reference = new SearchFacet("Projects/Reference", "Reference", 1);

    private SearchFacet projectPage = new SearchFacet("Projects/Homepage", "Homepage", 1);

    private SearchFacet springFramework = new SearchFacet("Projects/SpringFramework", "Spring Framework", 1);

    @Test
    public void createsRootFacetWithProvidedFacets() throws Exception {
        SearchFacet root = build();
        MatcherAssert.assertThat(root.getFacets(), IsEqual.equalTo(facets));
        MatcherAssert.assertThat(root.getName(), IsEqual.equalTo(""));
        MatcherAssert.assertThat(root.getCount(), IsEqual.equalTo(0));
    }

    @Test
    public void addsAnEmptyBlogFacetIfNotInTheOriginalFacets() throws Exception {
        facets.remove(blog);
        SearchFacet rootFacet = build();
        SearchFacet emptyBlog = new SearchFacet("Blog", "Blog", 0);
        MatcherAssert.assertThat(rootFacet.getFacets(), contains(emptyBlog, guides, projects));
    }

    @Test
    public void addsAnEmptyGuidesFacetIfNotInTheOriginalFacets() throws Exception {
        facets.remove(guides);
        SearchFacet rootFacet = build();
        SearchFacet emptyGuides = new SearchFacet("Guides", "Guides", 0);
        MatcherAssert.assertThat(rootFacet.getFacets(), contains(blog, emptyGuides, projects));
    }

    @Test
    public void addsAnEmptyProjectsFacetIfNotInTheOriginalFacets() throws Exception {
        facets.remove(projects);
        SearchFacet rootFacet = build();
        SearchFacet emptyProjects = new SearchFacet("Projects", "Projects", 0);
        MatcherAssert.assertThat(rootFacet.getFacets(), contains(blog, guides, emptyProjects));
    }

    @Test
    public void movesProjectPageToBeAHeaderFacetUnderProjects() throws Exception {
        projects.getFacets().add(projectPage);
        projects.getFacets().add(springFramework);
        SearchFacet rootFacet = build();
        SearchFacet projectFacet = rootFacet.getFacets().get(2);
        MatcherAssert.assertThat(projectFacet.getHeaderFacets(), contains(projectPage));
        MatcherAssert.assertThat(projectFacet.getFacets(), contains(springFramework));
    }

    @Test
    public void movesApiToBeAHeaderFacetUnderProjects() throws Exception {
        projects.getFacets().add(api);
        projects.getFacets().add(springFramework);
        SearchFacet rootFacet = build();
        SearchFacet projectFacet = rootFacet.getFacets().get(2);
        MatcherAssert.assertThat(projectFacet.getHeaderFacets(), contains(api));
        MatcherAssert.assertThat(projectFacet.getFacets(), contains(springFramework));
    }

    @Test
    public void movesReferenceToBeAHeaderFacetUnderProjects() throws Exception {
        facets.add(projects);
        projects.getFacets().add(reference);
        projects.getFacets().add(springFramework);
        SearchFacet rootFacet = build();
        SearchFacet projectFacet = rootFacet.getFacets().get(2);
        MatcherAssert.assertThat(projectFacet.getHeaderFacets(), contains(reference));
        MatcherAssert.assertThat(projectFacet.getFacets(), contains(springFramework));
    }
}

