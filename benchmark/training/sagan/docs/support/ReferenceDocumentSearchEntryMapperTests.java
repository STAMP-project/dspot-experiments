package sagan.docs.support;


import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import sagan.projects.Project;
import sagan.projects.ProjectRelease;
import sagan.projects.ProjectReleaseBuilder;
import sagan.search.types.ReferenceDoc;


public class ReferenceDocumentSearchEntryMapperTests {
    private Project project = new Project("spring", "Spring Project", "http://www.example.com/repo/spring-framework", "http://www.example.com/spring-framework", Collections.<ProjectRelease>emptyList(), "release");

    private ProjectRelease version = new ProjectReleaseBuilder().versionName("3.2.1.RELEASE").releaseStatus(GENERAL_AVAILABILITY).current(true).build();

    private ReferenceDocumentSearchEntryMapper mapper = new ReferenceDocumentSearchEntryMapper(project, version);

    private ReferenceDoc entry;

    @Test
    public void facetPaths() {
        MatcherAssert.assertThat(entry.getFacetPaths(), contains("Projects", "Projects/Reference", "Projects/Spring Project", "Projects/Spring Project/3.2.1.RELEASE"));
    }

    @Test
    public void projectId() {
        MatcherAssert.assertThat(entry.getProjectId(), Matchers.equalTo("spring"));
    }

    @Test
    public void version() {
        MatcherAssert.assertThat(entry.getVersion(), Matchers.equalTo("3.2.1.RELEASE"));
    }

    @Test
    public void subTitle() {
        MatcherAssert.assertThat(entry.getSubTitle(), Matchers.equalTo("Spring Project (3.2.1.RELEASE Reference)"));
    }
}

