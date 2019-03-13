package sagan.guides.support;


import GettingStartedGuides.REPO_PREFIX;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.social.github.api.GitHubRepo;
import sagan.support.Fixtures;
import sagan.support.github.GitHubClient;


@RunWith(MockitoJUnitRunner.class)
public class GuidesOrgTests {
    private static final String OWNER_TYPE = "orgs";

    private static final String OWNER_NAME = "my-org";

    @Mock
    private GitHubClient ghClient;

    private GuideOrganization service;

    @Test
    public void getRawFileAsHtml_fetchesRenderedHtmlFromGitHub() throws Exception {
        BDDMockito.given(ghClient.sendRequestForHtml("/path/to/html")).willReturn("<h1>Something</h1>");
        MatcherAssert.assertThat(service.getMarkdownFileAsHtml("/path/to/html"), equalTo("<h1>Something</h1>"));
    }

    @Test
    public void getRepoInfo_fetchesGitHubRepos() {
        BDDMockito.given(ghClient.sendRequestForJson(ArgumentMatchers.anyString(), ArgumentMatchers.anyVararg())).willReturn(Fixtures.githubRepoJson());
        GitHubRepo repoInfo = service.getRepoInfo("repo");
        MatcherAssert.assertThat(repoInfo.getName(), equalTo("spring-boot"));
    }

    @Test
    public void getGitHubRepos_fetchesGuideReposGitHub() {
        BDDMockito.given(ghClient.sendRequestForJson(ArgumentMatchers.anyString(), ArgumentMatchers.anyVararg())).willReturn(Fixtures.githubRepoListJson());
        GitHubRepo[] repos = service.findAllRepositories();
        MatcherAssert.assertThat(repos[0].getName(), equalTo("gs-rest-service"));
    }

    @Test
    public void shouldFindByPrefix() throws Exception {
        BDDMockito.given(ghClient.sendRequestForJson(ArgumentMatchers.anyString(), ArgumentMatchers.anyVararg())).willReturn(Fixtures.githubRepoListJson());
        List<GitHubRepo> matches = service.findRepositoriesByPrefix(REPO_PREFIX);
        MatcherAssert.assertThat(matches.size(), greaterThan(0));
        for (GitHubRepo match : matches) {
            MatcherAssert.assertThat(match.getName(), CoreMatchers.startsWith(REPO_PREFIX));
        }
    }
}

