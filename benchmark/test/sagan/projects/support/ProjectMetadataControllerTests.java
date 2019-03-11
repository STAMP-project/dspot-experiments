package sagan.projects.support;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import sagan.projects.Project;
import sagan.projects.ProjectRelease;
import sagan.projects.ProjectRelease.ReleaseStatus;


@RunWith(MockitoJUnitRunner.class)
public class ProjectMetadataControllerTests {
    /**
     *
     */
    private static final String PROJECT_ID = "spring-framework";

    @Mock
    private ProjectMetadataService projectMetadataService;

    private List<ProjectRelease> releases = new ArrayList<>();

    Project project = new Project(ProjectMetadataControllerTests.PROJECT_ID, "spring", "http://example.com", "http://examples.com", releases, "project");

    private ProjectMetadataController controller;

    @Test
    public void getProject_doesNotContainVersionPlaceholders() throws Exception {
        ProjectRelease release = new ProjectRelease("1.2.3", ReleaseStatus.GENERAL_AVAILABILITY, false, "http://example.com/1.2.3", "http://example.com/1.2.3", "org.springframework", "spring-core");
        releases.add(release);
        Mockito.when(projectMetadataService.getProject(ProjectMetadataControllerTests.PROJECT_ID)).thenReturn(project);
        Project result = controller.projectMetadata(ProjectMetadataControllerTests.PROJECT_ID);
        MatcherAssert.assertThat(result.getProjectRelease("1.2.3"), Matchers.equalTo(release));
        MatcherAssert.assertThat(project.getProjectReleases().iterator().next().getApiDocUrl(), Matchers.equalTo("http://example.com/1.2.3"));
    }

    @Test
    public void editProjectReleases_replacesVersionPatterns() throws Exception {
        ProjectRelease release = new ProjectRelease("1.2.3", ReleaseStatus.GENERAL_AVAILABILITY, false, "http://example.com/1.2.3", "http://example.com/1.2.3", "org.springframework", "spring-core");
        ProjectRelease update = new ProjectRelease("1.2.4", ReleaseStatus.GENERAL_AVAILABILITY, false, "http://example.com/{version}", "http://example.com/{version}", "org.springframework", "spring-core");
        releases.add(release);
        Mockito.when(projectMetadataService.getProject(ProjectMetadataControllerTests.PROJECT_ID)).thenReturn(project);
        controller.updateProjectMetadata(ProjectMetadataControllerTests.PROJECT_ID, Arrays.asList(update));
        MatcherAssert.assertThat(project.getProjectReleases().iterator().next().getApiDocUrl(), Matchers.equalTo("http://example.com/1.2.4"));
    }

    @Test
    public void addProjectRelease_replacesVersionPatterns() throws Exception {
        ProjectRelease update = new ProjectRelease("1.2.4", ReleaseStatus.GENERAL_AVAILABILITY, false, "http://example.com/{version}", "http://example.com/{version}", "org.springframework", "spring-core");
        Mockito.when(projectMetadataService.getProject(ProjectMetadataControllerTests.PROJECT_ID)).thenReturn(project);
        controller.updateReleaseMetadata(ProjectMetadataControllerTests.PROJECT_ID, update);
        MatcherAssert.assertThat(project.getProjectReleases().iterator().next().getApiDocUrl(), Matchers.equalTo("http://example.com/1.2.4"));
    }

    @Test
    public void updateProject_patchesTheProject() throws Exception {
        List<ProjectRelease> newReleases = new ArrayList<>();
        newReleases.add(new ProjectRelease("1.0.0.RELEASE", ReleaseStatus.GENERAL_AVAILABILITY, true, "foo", "bar", "com.example", "artifact"));
        Project newProject = new Project(ProjectMetadataControllerTests.PROJECT_ID, null, "http://example.com", "newSite", newReleases, "newProject");
        newProject.setRawBootConfig("newRawBootConfig");
        newProject.setRawOverview("newRawOverview");
        Mockito.when(projectMetadataService.getProject(ProjectMetadataControllerTests.PROJECT_ID)).thenReturn(project);
        Mockito.when(projectMetadataService.save(Mockito.anyObject())).thenAnswer(( invocation) -> invocation.getArguments()[0]);
        Project updatedProject = controller.updateProject(ProjectMetadataControllerTests.PROJECT_ID, newProject);
        // for now we only patch the raw stuff
        MatcherAssert.assertThat(updatedProject.getName(), Matchers.equalTo(project.getName()));
        MatcherAssert.assertThat(updatedProject.getRepoUrl(), Matchers.equalTo(project.getRepoUrl()));
        MatcherAssert.assertThat(updatedProject.getRawOverview(), Matchers.equalTo("newRawOverview"));
        MatcherAssert.assertThat(updatedProject.getRawBootConfig(), Matchers.equalTo("newRawBootConfig"));
    }
}

