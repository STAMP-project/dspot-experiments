/**
 * Copyright 2013 gitblit.com.
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


import AccessPermission.CLONE;
import AccessPermission.DELETE;
import AccessPermission.REWIND;
import AccessRestrictionType.VIEW;
import com.gitblit.ConfigUserService;
import com.gitblit.models.RepositoryModel;
import com.gitblit.models.UserModel;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;


/**
 * https://code.google.com/p/gitblit/issues/detail?id=259
 *
 * Reported Problem:
 * We have an user with RWD access rights, but he can?t push.
 *
 * @see src/test/resources/issue0259.conf

At the next day he try again and he can push to the project.
 * @author James Moger
 */
public class Issue0259Test extends GitblitUnitTest {
    /**
     * Test the provided users.conf file for expected access permissions.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFile() throws Exception {
        File realmFile = new File("src/test/resources/issue0259.conf");
        ConfigUserService service = new ConfigUserService(realmFile);
        RepositoryModel test = repo("test.git", VIEW);
        RepositoryModel projects_test = repo("projects/test.git", VIEW);
        UserModel a = service.getUserModel("a");
        UserModel b = service.getUserModel("b");
        UserModel c = service.getUserModel("c");
        // assert RWD or RW+ for projects/test.git
        Assert.assertEquals(DELETE, a.getRepositoryPermission(projects_test).permission);
        Assert.assertEquals(DELETE, b.getRepositoryPermission(projects_test).permission);
        Assert.assertEquals(REWIND, c.getRepositoryPermission(projects_test).permission);
        Assert.assertTrue(a.canPush(projects_test));
        Assert.assertTrue(b.canPush(projects_test));
        Assert.assertTrue(c.canPush(projects_test));
        Assert.assertTrue(a.canDeleteRef(projects_test));
        Assert.assertTrue(b.canDeleteRef(projects_test));
        Assert.assertTrue(c.canDeleteRef(projects_test));
        Assert.assertFalse(a.canRewindRef(projects_test));
        Assert.assertFalse(b.canRewindRef(projects_test));
        Assert.assertTrue(c.canRewindRef(projects_test));
        // assert R for test.git
        Assert.assertEquals(CLONE, a.getRepositoryPermission(test).permission);
        Assert.assertEquals(CLONE, b.getRepositoryPermission(test).permission);
        Assert.assertEquals(REWIND, c.getRepositoryPermission(test).permission);
        Assert.assertTrue(a.canClone(test));
        Assert.assertTrue(b.canClone(test));
        Assert.assertFalse(a.canPush(test));
        Assert.assertFalse(b.canPush(test));
        Assert.assertTrue(c.canPush(test));
    }

    @Test
    public void testTeamsOrder() throws Exception {
        testTeams(false);
    }

    @Test
    public void testTeamsReverseOrder() throws Exception {
        testTeams(true);
    }

    @Test
    public void testTeam() throws Exception {
        testTeam(false);
    }

    @Test
    public void testTeamReverseOrder() throws Exception {
        testTeam(true);
    }
}

