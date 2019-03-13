package sagan.projects;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

import static ReleaseStatus.GENERAL_AVAILABILITY;


public class ProjectTest {
    Project project;

    ProjectRelease currentRelease = new ProjectRelease("1.8.7.RELEASE", GENERAL_AVAILABILITY, true, "ref-doc-url", "api-doc-url", "group-id", "artifact-id");

    ProjectRelease olderRelease = new ProjectRelease("2.0.0.RELEASE", GENERAL_AVAILABILITY, false, "ref-doc-url", "api-doc-url", "group-id", "artifact-id");

    @Test
    public void currentVersion() {
        Assert.assertThat(project.getMostCurrentRelease(), Is.is(Optional.of(currentRelease)));
    }

    @Test
    public void currentVersionNotAvailable() {
        List<ProjectRelease> releases = Arrays.asList();
        Project project = new Project("id", "my-special-project", "my-repo-url", "my-site-url", releases, "my-special-category");
        Optional<String> noVersion = Optional.empty();
        Assert.assertThat(project.getMostCurrentRelease(), Is.is(noVersion));
    }

    @Test
    public void nonMostCurrentVersions() {
        Assert.assertThat(project.getNonMostCurrentReleases(), Matchers.contains(olderRelease));
    }

    @Test
    public void isTopLevelProjectWhenItHasNoParentProject() {
        Project childProject = new Project("child-project", "", "", "", Collections.emptyList(), "");
        childProject.setParentProject(project);
        project.setChildProjectList(Arrays.asList(childProject));
        Assert.assertThat(project.isTopLevelProject(), Is.is(true));
        Assert.assertThat(childProject.isTopLevelProject(), Is.is(false));
    }

    @Test
    public void orderedProjectSamples() {
        ProjectSample first = new ProjectSample("First", 1);
        ProjectSample second = new ProjectSample("Second", 2);
        ProjectSample third = new ProjectSample("Third", 3);
        List<ProjectSample> samples = Arrays.asList(third, first, second);
        Project project = new Project("id", "my-special-project", "my-repo-url", "my-site-url", Collections.emptyList(), "my-special-category");
        project.setProjectSamples(samples);
        Assert.assertThat(project.getProjectSamples(), Matchers.contains(first, second, third));
    }
}

