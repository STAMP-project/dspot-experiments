package com.gitblit.tests;


import Constants.DEFAULT_USER_REPOSITORY_PREFIX;
import com.gitblit.Constants;
import com.gitblit.utils.ModelUtils;
import org.junit.Assert;
import org.junit.Test;


public class ModelUtilsTest extends GitblitUnitTest {
    @Test
    public void testGetUserRepoPrefix() {
        Assert.assertEquals(DEFAULT_USER_REPOSITORY_PREFIX, ModelUtils.getUserRepoPrefix());
    }

    @Test
    public void testSetUserRepoPrefix() {
        Assert.assertEquals(DEFAULT_USER_REPOSITORY_PREFIX, ModelUtils.getUserRepoPrefix());
        ModelUtils.setUserRepoPrefix("@");
        Assert.assertEquals("@", ModelUtils.getUserRepoPrefix());
        ModelUtils.setUserRepoPrefix("");
        Assert.assertEquals(DEFAULT_USER_REPOSITORY_PREFIX, ModelUtils.getUserRepoPrefix());
        ModelUtils.setUserRepoPrefix("user/");
        Assert.assertEquals("user/", ModelUtils.getUserRepoPrefix());
        ModelUtils.setUserRepoPrefix("u_");
        Assert.assertEquals("u_", ModelUtils.getUserRepoPrefix());
        ModelUtils.setUserRepoPrefix(null);
        Assert.assertEquals(DEFAULT_USER_REPOSITORY_PREFIX, ModelUtils.getUserRepoPrefix());
        ModelUtils.setUserRepoPrefix("/somedir/otherdir/");
        Assert.assertEquals("somedir/otherdir/", ModelUtils.getUserRepoPrefix());
    }

    @Test
    public void testGetPersonalPath() {
        String username = "rob";
        Assert.assertEquals(((Constants.DEFAULT_USER_REPOSITORY_PREFIX) + (username.toLowerCase())), ModelUtils.getPersonalPath(username));
        username = "James";
        Assert.assertEquals(((Constants.DEFAULT_USER_REPOSITORY_PREFIX) + (username.toLowerCase())), ModelUtils.getPersonalPath(username));
        ModelUtils.setUserRepoPrefix("usr/");
        username = "noMan";
        Assert.assertEquals(("usr/" + (username.toLowerCase())), ModelUtils.getPersonalPath(username));
    }

    @Test
    public void testIsPersonalRepository() {
        String reponame = (Constants.DEFAULT_USER_REPOSITORY_PREFIX) + "one";
        Assert.assertTrue(ModelUtils.isPersonalRepository(reponame));
        reponame = "none";
        Assert.assertFalse(ModelUtils.isPersonalRepository(reponame));
        ModelUtils.setUserRepoPrefix("@@");
        reponame = "@@two";
        Assert.assertTrue(ModelUtils.isPersonalRepository(reponame));
        ModelUtils.setUserRepoPrefix("users/");
        reponame = "users/three";
        Assert.assertTrue(ModelUtils.isPersonalRepository(reponame));
        reponame = "project/four";
        Assert.assertFalse(ModelUtils.isPersonalRepository(reponame));
    }

    @Test
    public void testIsUsersPersonalRepository() {
        String reponame = (Constants.DEFAULT_USER_REPOSITORY_PREFIX) + "lynn";
        Assert.assertTrue(ModelUtils.isUsersPersonalRepository("lynn", reponame));
        reponame = "prjB";
        Assert.assertFalse(ModelUtils.isUsersPersonalRepository("lynn", reponame));
        ModelUtils.setUserRepoPrefix("@@");
        reponame = "@@newton";
        Assert.assertTrue(ModelUtils.isUsersPersonalRepository("newton", reponame));
        Assert.assertFalse(ModelUtils.isUsersPersonalRepository("hertz", reponame));
        ModelUtils.setUserRepoPrefix("users/");
        reponame = "users/fee";
        Assert.assertTrue(ModelUtils.isUsersPersonalRepository("fee", reponame));
        Assert.assertFalse(ModelUtils.isUsersPersonalRepository("gnome", reponame));
        reponame = "project/nsbl";
        Assert.assertFalse(ModelUtils.isUsersPersonalRepository("fee", reponame));
    }

    @Test
    public void testGetUserNameFromRepoPath() {
        String reponame = (Constants.DEFAULT_USER_REPOSITORY_PREFIX) + "lynn";
        Assert.assertEquals("lynn", ModelUtils.getUserNameFromRepoPath(reponame));
        ModelUtils.setUserRepoPrefix("@@");
        reponame = "@@newton";
        Assert.assertEquals("newton", ModelUtils.getUserNameFromRepoPath(reponame));
        ModelUtils.setUserRepoPrefix("users/");
        reponame = "users/fee";
        Assert.assertEquals("fee", ModelUtils.getUserNameFromRepoPath(reponame));
    }
}

