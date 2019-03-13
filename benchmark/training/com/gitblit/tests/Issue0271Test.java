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


import AccessPermission.CREATE;
import AccessPermission.PUSH;
import AccessRestrictionType.VIEW;
import com.gitblit.ConfigUserService;
import com.gitblit.models.RepositoryModel;
import com.gitblit.models.UserModel;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;


/**
 * https://code.google.com/p/gitblit/issues/detail?id=271
 *
 * Reported Problem:
 * Inherited team permissions are incorrect.
 *
 * @see src/test/resources/issue0270.conf
 * @author James Moger
 */
public class Issue0271Test extends GitblitUnitTest {
    /**
     * Test the provided users.conf file for expected access permissions.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFile() throws Exception {
        File realmFile = new File("src/test/resources/issue0271.conf");
        ConfigUserService service = new ConfigUserService(realmFile);
        RepositoryModel test = repo("test.git", VIEW);
        RepositoryModel teama_test = repo("teama/test.git", VIEW);
        UserModel a = service.getUserModel("a");
        UserModel b = service.getUserModel("b");
        UserModel c = service.getUserModel("c");
        // assert V for test.git
        Assert.assertEquals(AccessPermission.VIEW, a.getRepositoryPermission(test).permission);
        Assert.assertEquals(AccessPermission.VIEW, b.getRepositoryPermission(test).permission);
        Assert.assertEquals(AccessPermission.VIEW, c.getRepositoryPermission(test).permission);
        // assert expected permissions for teama/test.git
        Assert.assertEquals(AccessPermission.VIEW, a.getRepositoryPermission(teama_test).permission);
        Assert.assertEquals(PUSH, b.getRepositoryPermission(teama_test).permission);
        Assert.assertEquals(CREATE, c.getRepositoryPermission(teama_test).permission);
    }
}

