package sagan.guides.support;


import java.util.Arrays;
import java.util.List;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.social.github.api.GitHubRepo;
import sagan.guides.GuideMetadata;
import sagan.projects.Project;
import sagan.projects.support.ProjectMetadataService;


@RunWith(MockitoJUnitRunner.class)
public class ProjectGuidesRepositoryTest {
    ProjectGuidesRepository subject;

    Project bananaProject = new Project("banana-project", "Banana Project", "my-banana-url", "my-banana-url", null, "my-banana-category");

    Project specialProject = new Project("special-project", "Special Project", "my-repo-url", "my-site-url", null, "my-special-category");

    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    private GuideOrganization guideOrganization;

    @Mock
    private ProjectMetadataService projectMetadataService;

    @Test
    public void findByProject() throws Exception {
        GitHubRepo awesomeGithubRepo = new GitHubRepo();
        awesomeGithubRepo.setDescription("My Special Project Guide :: Learn awesome stuff with this guide :: special-project");
        awesomeGithubRepo.setName("gs-special-guide");
        GitHubRepo bananaGithubRepo = new GitHubRepo();
        bananaGithubRepo.setDescription("Bananas Guide :: Everything you ever wondered about bananas :: banana-project");
        bananaGithubRepo.setName("gs-banana-guide");
        List<GitHubRepo> repos = Arrays.asList(awesomeGithubRepo, bananaGithubRepo);
        BDDMockito.given(guideOrganization.getName()).willReturn("spring-guides");
        BDDMockito.given(guideOrganization.findRepositoriesByPrefix("gs-")).willReturn(repos);
        BDDMockito.given(projectMetadataService.getProject("banana-project")).willReturn(bananaProject);
        BDDMockito.given(projectMetadataService.getProject("special-project")).willReturn(specialProject);
        List<GuideMetadata> guides = subject.findByProject(specialProject);
        Assert.assertThat(guides, hasSize(1));
        Assert.assertThat(guides.get(0).getRepoName(), Is.is("gs-special-guide"));
    }
}

