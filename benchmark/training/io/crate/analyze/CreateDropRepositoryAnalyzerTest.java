/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial agreement.
 */
package io.crate.analyze;


import io.crate.exceptions.RepositoryAlreadyExistsException;
import io.crate.exceptions.RepositoryUnknownException;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.SQLExecutor;
import io.crate.testing.SettingMatcher;
import org.hamcrest.Matchers;
import org.junit.Test;


public class CreateDropRepositoryAnalyzerTest extends CrateDummyClusterServiceUnitTest {
    private SQLExecutor e;

    @Test
    public void testCreateRepository() throws Exception {
        CreateRepositoryAnalyzedStatement statement = e.analyze("CREATE REPOSITORY \"new_repository\" TYPE \"fs\" with (location=\'/mount/backups/my_backup\', compress=True)");
        assertThat(statement.repositoryName(), Matchers.is("new_repository"));
        assertThat(statement.repositoryType(), Matchers.is("fs"));
        assertThat(statement.settings().get("compress"), Matchers.is("true"));
        assertThat(statement.settings().get("location"), Matchers.is("/mount/backups/my_backup"));
    }

    @Test
    public void testCreateExistingRepository() throws Exception {
        expectedException.expect(RepositoryAlreadyExistsException.class);
        expectedException.expectMessage("Repository 'my_repo' already exists");
        e.analyze("create repository my_repo TYPE fs");
    }

    @Test
    public void testDropUnknownRepository() throws Exception {
        expectedException.expect(RepositoryUnknownException.class);
        expectedException.expectMessage("Repository 'unknown_repo' unknown");
        e.analyze("DROP REPOSITORY \"unknown_repo\"");
    }

    @Test
    public void testDropExistingRepo() throws Exception {
        DropRepositoryAnalyzedStatement statement = e.analyze("DROP REPOSITORY my_repo");
        assertThat(statement.repositoryName(), Matchers.is("my_repo"));
    }

    @Test
    public void testCreateS3RepositoryWithAllSettings() throws Exception {
        CreateRepositoryAnalyzedStatement analysis = e.analyze(("CREATE REPOSITORY foo TYPE s3 WITH (" + (((((((((((("bucket='abc'," + "endpoint='www.example.com',") + "protocol='http',") + "base_path='/holz/',") + "access_key='0xAFFE',") + "secret_key='0xCAFEE',") + "chunk_size='12mb',") + "compress=true,") + "server_side_encryption=false,") + "buffer_size='5mb',") + "max_retries=2,") + "use_throttle_retries=false,") + "canned_acl=false)")));
        assertThat(analysis.repositoryType(), Matchers.is("s3"));
        assertThat(analysis.repositoryName(), Matchers.is("foo"));
        assertThat(analysis.settings(), Matchers.allOf(SettingMatcher.hasEntry("access_key", "0xAFFE"), SettingMatcher.hasEntry("base_path", "/holz/"), SettingMatcher.hasEntry("bucket", "abc"), SettingMatcher.hasEntry("buffer_size", "5mb"), SettingMatcher.hasEntry("canned_acl", "false"), SettingMatcher.hasEntry("chunk_size", "12mb"), SettingMatcher.hasEntry("compress", "true"), SettingMatcher.hasEntry("endpoint", "www.example.com"), SettingMatcher.hasEntry("max_retries", "2"), SettingMatcher.hasEntry("use_throttle_retries", "false"), SettingMatcher.hasEntry("protocol", "http"), SettingMatcher.hasEntry("secret_key", "0xCAFEE"), SettingMatcher.hasEntry("server_side_encryption", "false")));
    }

    @Test
    public void testCreateS3RepoWithWrongSettings() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("setting 'wrong' not supported");
        e.analyze("CREATE REPOSITORY foo TYPE s3 WITH (wrong=true)");
    }
}

