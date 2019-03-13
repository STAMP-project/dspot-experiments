package sagan.projects.support;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.ExtendedModelMap;
import sagan.guides.AbstractGuide;
import sagan.guides.GettingStartedGuide;
import sagan.guides.Topical;
import sagan.guides.Tutorial;
import sagan.guides.support.GettingStartedGuides;
import sagan.guides.support.Topicals;
import sagan.guides.support.Tutorials;
import sagan.projects.Project;
import sagan.projects.ProjectRelease;


@RunWith(MockitoJUnitRunner.class)
public class ProjectsControllerTest {
    @Mock
    private ProjectMetadataService projectMetadataService;

    @Mock
    private GettingStartedGuides projectGuidesRepo;

    @Mock
    private Tutorials projectTutorialRepo;

    @Mock
    private Topicals projectTopicalRepo;

    private ProjectRelease currentRelease = new ProjectRelease("1.5.7.RELEASE", GENERAL_AVAILABILITY, true, "", "", "", "");

    private ProjectRelease anotherCurrentRelease = new ProjectRelease("1.4.7.RELEASE", GENERAL_AVAILABILITY, true, "", "", "", "");

    private ProjectRelease snapshotRelease = new ProjectRelease("1.7.7.SNAPSHOT", SNAPSHOT, false, "", "", "", "");

    private List<ProjectRelease> releases = Arrays.asList(currentRelease, anotherCurrentRelease, snapshotRelease);

    Project project = new Project("spring-framework", "spring", "http://example.com", "/project/spring-framework", 0, releases, "project", "spring-cool,spring-awesome", "");

    Project projectUmbrella = new Project("spring-parapluie", "Spring Parapluie", "http://example.com", "/project/spring-parapluie", 1, releases, "project", "spring-cool,spring-awesome", "");

    Project projectUmbrellaChild = new Project("spring-parapluie-child", "Spring Parapluie Child", "http://example.com", "/project/spring-parapluie-child", 1, releases, "project", "spring-cool,spring-awesome", "");

    private ExtendedModelMap model = new ExtendedModelMap();

    private ProjectsController controller;

    private String viewName;

    Topical topical = new Topical();

    Tutorial tutorial = new Tutorial();

    GettingStartedGuide guide = new GettingStartedGuide();

    @Test
    public void showProjectModelHasProjectData() {
        MatcherAssert.assertThat(model.get("selectedProject"), equalTo(project));
    }

    @Test
    public void showProjectModelHasProjectsListForSidebar() {
        List<Project> modelProjectList = ((List<Project>) (model.get("projects")));
        MatcherAssert.assertThat(modelProjectList, hasSize(2));
        MatcherAssert.assertThat(modelProjectList, is(Arrays.asList(project, projectUmbrella)));
        Project actualUmbrellaProject = modelProjectList.get(1);
        MatcherAssert.assertThat(actualUmbrellaProject.getChildProjectList(), equalTo(Arrays.asList(projectUmbrellaChild)));
    }

    @Test
    public void showProjectViewNameIsShow() {
        MatcherAssert.assertThat(viewName, is("projects/show"));
    }

    @Test
    public void showProjectHasStackOverflowLink() {
        MatcherAssert.assertThat(model.get("projectStackOverflow"), is("https://stackoverflow.com/questions/tagged/spring-cool+or+spring-awesome"));
    }

    @Test
    public void showProjectHasGuidesTutorialsTopicals() {
        List<AbstractGuide> guides = ((List<AbstractGuide>) (model.get("guides")));
        MatcherAssert.assertThat(guides, hasItems(guide));
        guides = ((List<AbstractGuide>) (model.get("topicals")));
        MatcherAssert.assertThat(guides, hasItems(topical));
        guides = ((List<AbstractGuide>) (model.get("tutorials")));
        MatcherAssert.assertThat(guides, hasItems(tutorial));
    }

    @Test
    public void showProjectHasReleases() {
        Optional<ProjectRelease> currentRelease = ((Optional<ProjectRelease>) (model.get("currentRelease")));
        List<ProjectRelease> otherReleases = ((List<ProjectRelease>) (model.get("otherReleases")));
        MatcherAssert.assertThat(currentRelease, is(Optional.of(this.currentRelease)));
        MatcherAssert.assertThat(otherReleases, hasItems(anotherCurrentRelease, snapshotRelease));
    }

    @Test
    public void showProjectDoesNotExplodeWhenThereAreNoReleases() {
        Project projectWithoutReleases = new Project("spring-spline-reticulator", "spring-spline-reticulator", "http://example.com", "/project/spring-spline-reticulator", 0, Arrays.asList(), "project", "spring-cool,spring-awesome", "");
        Mockito.when(projectMetadataService.getProject("spring-spline-reticulator")).thenReturn(projectWithoutReleases);
        model = new ExtendedModelMap();
        controller.showProject(model, "spring-spline-reticulator");
        Optional<ProjectRelease> currentRelease = ((Optional<ProjectRelease>) (model.get("currentRelease")));
        List<ProjectRelease> otherReleases = ((List<ProjectRelease>) (model.get("otherReleases")));
        MatcherAssert.assertThat(currentRelease, is(Optional.empty()));
        MatcherAssert.assertThat(otherReleases, hasSize(0));
    }

    @Test
    public void listProjects_providesProjectMetadataServiceInModel() {
        controller.listProjects(model);
        MatcherAssert.assertThat(((ProjectMetadataService) (model.get("projectMetadata"))), equalTo(projectMetadataService));
    }

    @Test
    public void listProjectReleases_providesReleaseMetadataInJsonPCallback() {
        controller.listProjects(model);
        MatcherAssert.assertThat(((ProjectMetadataService) (model.get("projectMetadata"))), equalTo(projectMetadataService));
    }
}

