package io.reark.rxgithubapp.shared.pojo;


import org.junit.Assert;
import org.junit.Test;


public class GitHubRepositoryTest {
    private static final GitHubOwner OWNER = new GitHubOwner("owner");

    @Test
    public void testSameIdPojoEquals() {
        GitHubRepository repo1 = new GitHubRepository(100, "", 10, 10, GitHubRepositoryTest.OWNER);
        GitHubRepository repo2 = new GitHubRepository(100, "", 10, 10, GitHubRepositoryTest.OWNER);
        Assert.assertEquals(repo1, repo2);
    }

    @Test
    public void testDifferentIdPojoDoesNotEqual() {
        GitHubRepository repo1 = new GitHubRepository(100, "", 10, 10, GitHubRepositoryTest.OWNER);
        GitHubRepository repo2 = new GitHubRepository(200, "", 10, 10, GitHubRepositoryTest.OWNER);
        Assert.assertFalse(repo1.equals(repo2));
    }

    @Test
    public void testOverwrite_WithItself() {
        GitHubRepository repo = new GitHubRepository(100, "foo", 10, 10, GitHubRepositoryTest.OWNER);
        repo.overwrite(repo);
        Assert.assertEquals(100, repo.getId());
        Assert.assertEquals("foo", repo.getName());
    }

    @Test
    public void testOverwrite_WithSameId_WithDifferentName() {
        GitHubRepository repo1 = new GitHubRepository(100, "foo", 10, 10, GitHubRepositoryTest.OWNER);
        GitHubRepository repo2 = new GitHubRepository(100, "bar", 10, 10, GitHubRepositoryTest.OWNER);
        repo1.overwrite(repo2);
        Assert.assertEquals(repo1, repo2);
    }

    @Test
    public void testOverwrite_WithSameId_WithDifferentCount() {
        GitHubRepository repo1 = new GitHubRepository(100, "foo", 10, 10, GitHubRepositoryTest.OWNER);
        GitHubRepository repo2 = new GitHubRepository(100, "foo", 20, 20, GitHubRepositoryTest.OWNER);
        repo1.overwrite(repo2);
        Assert.assertEquals(repo1, repo2);
    }

    @Test
    public void testOverwrite_WithSameId_WithDifferentOwner() {
        GitHubRepository repo1 = new GitHubRepository(100, "foo", 10, 10, GitHubRepositoryTest.OWNER);
        GitHubRepository repo2 = new GitHubRepository(100, "foo", 10, 10, new GitHubOwner("thief"));
        repo1.overwrite(repo2);
        Assert.assertEquals(repo1, repo2);
    }

    @Test
    public void testOverwrite_WithAnother() {
        GitHubRepository repo1 = new GitHubRepository(100, "foo", 10, 10, GitHubRepositoryTest.OWNER);
        GitHubRepository repo2 = new GitHubRepository(200, "bar", 10, 10, GitHubRepositoryTest.OWNER);
        repo1.overwrite(repo2);
        Assert.assertEquals(repo1, repo2);
        Assert.assertEquals(200, repo1.getId());
        Assert.assertEquals("bar", repo1.getName());
    }
}

