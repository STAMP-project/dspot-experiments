package io.reark.rxgithubapp.shared.pojo;


import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class GitHubRepositorySearchTest {
    private static final List<Integer> ITEMS = Arrays.asList(10, 20, 30);

    @Test
    public void testSameIdPojoEquals() {
        GitHubRepositorySearch search1 = new GitHubRepositorySearch("search", GitHubRepositorySearchTest.ITEMS);
        GitHubRepositorySearch search2 = new GitHubRepositorySearch("search", GitHubRepositorySearchTest.ITEMS);
        Assert.assertEquals(search1, search2);
    }

    @Test
    public void testDifferentIdPojoDoesNotEqual() {
        GitHubRepositorySearch search1 = new GitHubRepositorySearch("search", GitHubRepositorySearchTest.ITEMS);
        GitHubRepositorySearch search2 = new GitHubRepositorySearch("other", GitHubRepositorySearchTest.ITEMS);
        Assert.assertFalse(search1.equals(search2));
    }
}

