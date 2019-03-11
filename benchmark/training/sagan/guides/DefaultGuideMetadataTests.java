package sagan.guides;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


/**
 * Unit tests for {@link DefaultGuideMetadata}.
 */
public class DefaultGuideMetadataTests {
    private DefaultGuideMetadata guide;

    @Test
    public void testGetRepoName() throws Exception {
        MatcherAssert.assertThat(guide.getRepoName(), CoreMatchers.is("gs-rest-service"));
    }

    @Test
    public void testGetZipUrl() throws Exception {
        MatcherAssert.assertThat(guide.getZipUrl(), CoreMatchers.is("https://github.com/my-org/gs-rest-service/archive/master.zip"));
    }

    @Test
    public void testGetGitRepoHttpsUrl() throws Exception {
        MatcherAssert.assertThat(guide.getGitRepoHttpsUrl(), CoreMatchers.is("https://github.com/my-org/gs-rest-service.git"));
    }

    @Test
    public void testGetGithubRepoHttpsUrl() throws Exception {
        MatcherAssert.assertThat(guide.getGithubHttpsUrl(), CoreMatchers.is("https://github.com/my-org/gs-rest-service"));
    }

    @Test
    public void testGetGitRepoSshUrl() throws Exception {
        MatcherAssert.assertThat(guide.getGitRepoSshUrl(), CoreMatchers.is("git@github.com:my-org/gs-rest-service.git"));
    }

    @Test
    public void testGetRepoSubversionUrl() throws Exception {
        MatcherAssert.assertThat(guide.getGitRepoSubversionUrl(), CoreMatchers.is("https://github.com/my-org/gs-rest-service"));
    }

    @Test
    public void testGetCiStatusImageUrl() throws Exception {
        MatcherAssert.assertThat(guide.getCiStatusImageUrl(), CoreMatchers.is("https://travis-ci.org/my-org/gs-rest-service.svg?branch=master"));
    }

    @Test
    public void testGetCiLatestUrl() throws Exception {
        MatcherAssert.assertThat(guide.getCiLatestUrl(), CoreMatchers.is("https://travis-ci.org/my-org/gs-rest-service"));
    }

    @Test
    public void testGetTitle() throws Exception {
        MatcherAssert.assertThat(guide.getTitle(), CoreMatchers.is("Title"));
    }

    @Test
    public void testGetSubtitle() throws Exception {
        MatcherAssert.assertThat(guide.getSubtitle(), CoreMatchers.is("Subtitle"));
    }

    @Test
    public void testGetProjects() throws Exception {
        MatcherAssert.assertThat(guide.getProjects(), CoreMatchers.hasItems("project-1", "project-2"));
    }

    @Test
    public void testGetEmptyProjectList() throws Exception {
        guide = new DefaultGuideMetadata("my-org", "rest-service", "gs-rest-service", "Title :: Subtitle");
        MatcherAssert.assertThat(guide.getProjects(), Matchers.empty());
    }
}

