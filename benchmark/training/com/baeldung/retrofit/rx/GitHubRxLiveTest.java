package com.baeldung.retrofit.rx;


import com.baeldung.retrofit.models.Contributor;
import com.baeldung.retrofit.models.Repository;
import org.junit.Test;


public class GitHubRxLiveTest {
    GitHubRxApi gitHub;

    @Test
    public void whenListRepos_thenExpectReposThatContainTutorials() {
        gitHub.listRepos("eugenp").subscribe(( repos) -> {
            assertThat(repos).isNotEmpty().extracting(Repository::getName).contains("tutorials");
        });
    }

    @Test
    public void whenListRepoContributers_thenExpectContributorsThatContainEugenp() {
        gitHub.listRepoContributors("eugenp", "tutorials").subscribe(( contributors) -> {
            assertThat(contributors).isNotEmpty().extracting(Contributor::getName).contains("eugenp");
        });
    }
}

