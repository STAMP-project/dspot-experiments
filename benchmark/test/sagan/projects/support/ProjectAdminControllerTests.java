package sagan.projects.support;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.ExtendedModelMap;
import sagan.blog.support.PostContentRenderer;
import sagan.projects.Project;
import sagan.projects.ProjectRelease;
import sagan.projects.ProjectRelease.ReleaseStatus;
import sagan.projects.ProjectSample;


@RunWith(MockitoJUnitRunner.class)
public class ProjectAdminControllerTests {
    @Mock
    private ProjectMetadataService projectMetadataService;

    @Mock
    private PostContentRenderer renderer;

    private List<ProjectRelease> releases = new ArrayList<>();

    Project project = new Project("spring-framework", "spring", "http://example.com", "http://examples.com", releases, "project");

    private ExtendedModelMap model = new ExtendedModelMap();

    private ProjectAdminController controller;

    @Test
    public void listProjects_providesProjectMetadataServiceInModel() {
        releases.add(new ProjectRelease("1.2.3", ReleaseStatus.GENERAL_AVAILABILITY, false, "http://example.com/1.2.3", "http://example.com/1.2.3", "org.springframework", "spring-core"));
        Mockito.when(projectMetadataService.getProject("spring-framework")).thenReturn(project);
        List<Project> list = Arrays.asList(project);
        Mockito.when(projectMetadataService.getProjects()).thenReturn(list);
        controller.list(model);
        MatcherAssert.assertThat(model.get("projects"), Matchers.equalTo(list));
        MatcherAssert.assertThat(project.getProjectReleases().iterator().next().getApiDocUrl(), Matchers.equalTo("http://example.com/1.2.3"));
    }

    @Test
    public void editProject_presentsVersionPatternsInUris() {
        releases.add(new ProjectRelease("1.2.3", ReleaseStatus.GENERAL_AVAILABILITY, false, "http://example.com/1.2.3", "http://example.com/1.2.3", "org.springframework", "spring-core"));
        Mockito.when(projectMetadataService.getProject("spring-framework")).thenReturn(project);
        controller.edit("spring-framework", model);
        MatcherAssert.assertThat(model.get("project"), Matchers.equalTo(project));
        MatcherAssert.assertThat(getProjectReleases().iterator().next().getApiDocUrl(), Matchers.equalTo("http://example.com/{version}"));
    }

    @Test
    public void saveProject_rendersAsciidocContent() {
        Mockito.when(renderer.render(ArgumentMatchers.contains("boot-config"), ArgumentMatchers.any())).thenReturn("rendered-boot-config");
        Mockito.when(renderer.render(ArgumentMatchers.contains("overview"), ArgumentMatchers.any())).thenReturn("rendered-overview");
        project.setRawBootConfig("boot-config");
        project.setRawOverview("overview");
        controller.save(project, null, null, "", null);
        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        Mockito.verify(projectMetadataService, Mockito.times(1)).save(captor.capture());
        Project projectCaptured = captor.getValue();
        MatcherAssert.assertThat(projectCaptured.getRenderedBootConfig(), Matchers.equalTo("rendered-boot-config"));
        MatcherAssert.assertThat(projectCaptured.getRenderedOverview(), Matchers.equalTo("rendered-overview"));
    }

    @Test
    public void editProject_newProjectSampleDisplayOrder() {
        ProjectSample second = new ProjectSample("Second", 42);
        List<ProjectSample> samples = Arrays.asList(second);
        project.setProjectSamples(samples);
        Mockito.when(projectMetadataService.getProject("spring-framework")).thenReturn(project);
        controller.edit("spring-framework", model);
        MatcherAssert.assertThat(model.get("projectSampleDisplayOrder"), Matchers.equalTo(43));
    }
}

