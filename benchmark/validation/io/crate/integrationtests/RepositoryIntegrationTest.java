/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.integrationtests;


import io.crate.action.sql.SQLActionException;
import java.io.File;
import java.util.HashMap;
import org.hamcrest.Matchers;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class RepositoryIntegrationTest extends SQLTransportIntegrationTest {
    @ClassRule
    public static TemporaryFolder TEMPORARY_FOLDER = new TemporaryFolder();

    @Test
    public void testDropExistingRepository() throws Exception {
        execute("CREATE REPOSITORY existing_repo TYPE \"fs\" with (location=?, compress=True)", new Object[]{ RepositoryIntegrationTest.TEMPORARY_FOLDER.newFolder().getAbsolutePath() });
        waitNoPendingTasksOnAll();
        execute("DROP REPOSITORY existing_repo");
        assertThat(response.rowCount(), Matchers.is(1L));
        waitNoPendingTasksOnAll();
        execute("select * from sys.repositories where name = ? ", new Object[]{ "existing_repo" });
        assertThat(response.rowCount(), Matchers.is(0L));
    }

    @Test
    public void testCreateRepository() throws Throwable {
        String repoLocation = RepositoryIntegrationTest.TEMPORARY_FOLDER.newFolder().getAbsolutePath();
        execute("CREATE REPOSITORY \"myRepo\" TYPE \"fs\" with (location=?, compress=True)", new Object[]{ repoLocation });
        waitNoPendingTasksOnAll();
        execute("select * from sys.repositories where name ='myRepo'");
        assertThat(response.rowCount(), Matchers.is(1L));
        assertThat(((String) (response.rows()[0][0])), Matchers.is("myRepo"));
        HashMap<String, String> settings = ((HashMap) (response.rows()[0][1]));
        assertThat(settings.get("compress"), Matchers.is("true"));
        assertThat(new File(settings.get("location")).getAbsolutePath(), Matchers.is(repoLocation));
        assertThat(((String) (response.rows()[0][2])), Matchers.is("fs"));
    }

    @Test
    public void testCreateExistingRepository() throws Throwable {
        String repoLocation = RepositoryIntegrationTest.TEMPORARY_FOLDER.newFolder().getAbsolutePath();
        execute("CREATE REPOSITORY \"myRepo\" TYPE \"fs\" with (location=?, compress=True)", new Object[]{ repoLocation });
        waitNoPendingTasksOnAll();
        expectedException.expect(SQLActionException.class);
        expectedException.expectMessage("Repository 'myRepo' already exists");
        execute("CREATE REPOSITORY \"myRepo\" TYPE \"fs\" with (location=?, compress=True)", new Object[]{ repoLocation });
    }
}

