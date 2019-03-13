/**
 * Copyright 2011 gitblit.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gitblit.tests;


import AccessRestrictionType.CLONE;
import AccessRestrictionType.NONE;
import AccessRestrictionType.PUSH;
import AccessRestrictionType.VIEW;
import com.gitblit.Constants.AccessRestrictionType;
import com.gitblit.FileSettings;
import com.gitblit.models.RepositoryModel;
import com.gitblit.models.UserModel;
import com.gitblit.utils.FileUtils;
import java.io.File;
import java.util.List;
import org.eclipse.jgit.lib.Repository;
import org.junit.Assert;
import org.junit.Test;


public class GitBlitTest extends GitblitUnitTest {
    @Test
    public void testRepositoryModel() throws Exception {
        List<String> repositories = GitblitUnitTest.repositories().getRepositoryList();
        Assert.assertTrue("Repository list is empty!", ((repositories.size()) > 0));
        Assert.assertTrue("Missing Helloworld repository!", repositories.contains(GitBlitSuite.getHelloworldRepository().getDirectory().getName()));
        Repository r = GitBlitSuite.getHelloworldRepository();
        RepositoryModel model = GitblitUnitTest.repositories().getRepositoryModel(r.getDirectory().getName());
        Assert.assertTrue("Helloworld model is null!", (model != null));
        Assert.assertEquals(GitBlitSuite.getHelloworldRepository().getDirectory().getName(), model.name);
        Assert.assertTrue(((GitblitUnitTest.repositories().updateLastChangeFields(r, model)) > 22000L));
        r.close();
    }

    @Test
    public void testUserModel() throws Exception {
        List<String> users = GitblitUnitTest.users().getAllUsernames();
        Assert.assertTrue("No users found!", ((users.size()) > 0));
        Assert.assertTrue("Admin not found", users.contains("admin"));
        UserModel user = GitblitUnitTest.users().getUserModel("admin");
        Assert.assertEquals("admin", user.toString());
        Assert.assertTrue("Admin missing #admin role!", user.canAdmin);
        user.canAdmin = false;
        Assert.assertFalse("Admin should not have #admin!", user.canAdmin);
        String repository = GitBlitSuite.getHelloworldRepository().getDirectory().getName();
        RepositoryModel repositoryModel = GitblitUnitTest.repositories().getRepositoryModel(repository);
        repositoryModel.accessRestriction = AccessRestrictionType.VIEW;
        Assert.assertFalse("Admin can still access repository!", user.canView(repositoryModel));
        user.addRepositoryPermission(repository);
        Assert.assertTrue("Admin can't access repository!", user.canView(repositoryModel));
        Assert.assertEquals(GitblitUnitTest.repositories().getRepositoryModel(user, "pretend"), null);
        Assert.assertNotNull(GitblitUnitTest.repositories().getRepositoryModel(user, repository));
        Assert.assertTrue(((GitblitUnitTest.repositories().getRepositoryModels(user).size()) > 0));
    }

    @Test
    public void testUserModelVerification() throws Exception {
        UserModel user = new UserModel("james");
        user.displayName = "James Moger";
        Assert.assertFalse(user.is("James", null));
        Assert.assertFalse(user.is("James", ""));
        Assert.assertFalse(user.is("JaMeS", "anything"));
        Assert.assertFalse(user.is("james moger", null));
        Assert.assertFalse(user.is("james moger", ""));
        Assert.assertFalse(user.is("james moger", "anything"));
        Assert.assertFalse(user.is("joe", null));
        Assert.assertFalse(user.is("joe", ""));
        Assert.assertFalse(user.is("joe", "anything"));
        // specify email address which results in address verification
        user.emailAddress = "something";
        Assert.assertFalse(user.is("James", null));
        Assert.assertFalse(user.is("James", ""));
        Assert.assertFalse(user.is("JaMeS", "anything"));
        Assert.assertFalse(user.is("james moger", null));
        Assert.assertFalse(user.is("james moger", ""));
        Assert.assertFalse(user.is("james moger", "anything"));
        Assert.assertTrue(user.is("JaMeS", user.emailAddress));
        Assert.assertTrue(user.is("JaMeS mOgEr", user.emailAddress));
    }

    @Test
    public void testAccessRestrictionTypes() throws Exception {
        Assert.assertTrue(PUSH.exceeds(NONE));
        Assert.assertTrue(CLONE.exceeds(PUSH));
        Assert.assertTrue(VIEW.exceeds(CLONE));
        Assert.assertFalse(NONE.exceeds(PUSH));
        Assert.assertFalse(PUSH.exceeds(CLONE));
        Assert.assertFalse(CLONE.exceeds(VIEW));
        Assert.assertTrue(PUSH.atLeast(NONE));
        Assert.assertTrue(CLONE.atLeast(PUSH));
        Assert.assertTrue(VIEW.atLeast(CLONE));
        Assert.assertFalse(NONE.atLeast(PUSH));
        Assert.assertFalse(PUSH.atLeast(CLONE));
        Assert.assertFalse(CLONE.atLeast(VIEW));
        Assert.assertTrue(PUSH.toString().equals("PUSH"));
        Assert.assertTrue(CLONE.toString().equals("CLONE"));
        Assert.assertTrue(VIEW.toString().equals("VIEW"));
        Assert.assertEquals(NONE, AccessRestrictionType.fromName("none"));
        Assert.assertEquals(PUSH, AccessRestrictionType.fromName("push"));
        Assert.assertEquals(CLONE, AccessRestrictionType.fromName("clone"));
        Assert.assertEquals(VIEW, AccessRestrictionType.fromName("view"));
    }

    @Test
    public void testFileSettings() throws Exception {
        FileSettings settings = new FileSettings("src/main/distrib/data/gitblit.properties");
        Assert.assertEquals(true, settings.getBoolean("missing", true));
        Assert.assertEquals("default", settings.getString("missing", "default"));
        Assert.assertEquals(10, settings.getInteger("missing", 10));
        Assert.assertEquals(5, settings.getInteger("realm.realmFile", 5));
        Assert.assertTrue(settings.getBoolean("git.enableGitServlet", false));
        Assert.assertEquals("${baseFolder}/users.conf", settings.getString("realm.userService", null));
        Assert.assertEquals(5, settings.getInteger("realm.minPasswordLength", 0));
        List<String> mdExtensions = settings.getStrings("web.markdownExtensions");
        Assert.assertTrue(((mdExtensions.size()) > 0));
        Assert.assertTrue(mdExtensions.contains("md"));
        List<String> keys = settings.getAllKeys("server");
        Assert.assertTrue(((keys.size()) > 0));
        Assert.assertTrue(keys.contains("server.httpsPort"));
        Assert.assertTrue(((settings.getChar("web.forwardSlashCharacter", ' ')) == '/'));
    }

    @Test
    public void testGitblitSettings() throws Exception {
        // These are already tested by above test method.
        Assert.assertTrue(GitblitUnitTest.settings().getBoolean("missing", true));
        Assert.assertEquals("default", GitblitUnitTest.settings().getString("missing", "default"));
        Assert.assertEquals(10, GitblitUnitTest.settings().getInteger("missing", 10));
        Assert.assertEquals(5, GitblitUnitTest.settings().getInteger("realm.userService", 5));
        Assert.assertTrue(GitblitUnitTest.settings().getBoolean("git.enableGitServlet", false));
        File userDir = new File(System.getProperty("user.dir"));
        File userService = new File(GitblitUnitTest.settings().getString("realm.userService", null));
        Assert.assertEquals("src/test/config/test-users.conf", FileUtils.getRelativePath(userDir, userService));
        Assert.assertEquals(5, GitblitUnitTest.settings().getInteger("realm.minPasswordLength", 0));
        List<String> mdExtensions = GitblitUnitTest.settings().getStrings("web.markdownExtensions");
        Assert.assertTrue(((mdExtensions.size()) > 0));
        Assert.assertTrue(mdExtensions.contains("md"));
        List<String> keys = GitblitUnitTest.settings().getAllKeys("server");
        Assert.assertTrue(((keys.size()) > 0));
        Assert.assertTrue(keys.contains("server.httpsPort"));
        Assert.assertTrue(((GitblitUnitTest.settings().getChar("web.forwardSlashCharacter", ' ')) == '/'));
        Assert.assertFalse(GitblitUnitTest.runtime().isDebugMode());
    }

    @Test
    public void testAuthentication() throws Exception {
        Assert.assertTrue(((GitblitUnitTest.authentication().authenticate("admin", "admin".toCharArray(), null)) != null));
    }

    @Test
    public void testRepositories() throws Exception {
        Assert.assertTrue(((GitblitUnitTest.repositories().getRepository("missing")) == null));
        Assert.assertTrue(((GitblitUnitTest.repositories().getRepositoryModel("missing")) == null));
    }
}

