package com.baeldung.retrofit.basic;


import com.baeldung.retrofit.models.Contributor;
import com.baeldung.retrofit.models.Repository;
import java.io.IOException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class GitHubBasicApiLiveTest {
    GitHubBasicApi gitHub;

    @Test
    public void whenListRepos_thenExpectReposThatContainTutorials() {
        try {
            List<Repository> repos = gitHub.listRepos("eugenp").execute().body();
            assertThat(repos).isNotEmpty().extracting(Repository::getName).contains("tutorials");
        } catch (IOException e) {
            Assert.fail("Can not communicate with GitHub API");
        }
    }

    @Test
    public void whenListRepoContributers_thenExpectContributorsThatContainEugenp() {
        try {
            List<Contributor> contributors = gitHub.listRepoContributors("eugenp", "tutorials").execute().body();
            assertThat(contributors).isNotEmpty().extracting(Contributor::getName).contains("eugenp");
        } catch (IOException e) {
            Assert.fail("Can not communicate with GitHub API");
        }
    }
}

