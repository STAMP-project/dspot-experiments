/**
 * The MIT License
 *
 * Copyright (c) 2018 CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.model;


import java.io.File;
import java.io.IOException;
import java.util.Map;
import jenkins.model.IdStrategy;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


public class UserIdMigratorTest {
    private static final String BASE_RESOURCE_PATH = "src/test/resources/hudson/model/";

    @Rule
    public TestName name = new TestName();

    @Test
    public void needsMigrationBasic() throws IOException {
        UserIdMigrator migrator = createUserIdMigrator();
        MatcherAssert.assertThat(migrator.needsMigration(), is(true));
    }

    @Test
    public void needsMigrationFalse() throws IOException {
        UserIdMigrator migrator = createUserIdMigrator();
        MatcherAssert.assertThat(migrator.needsMigration(), is(false));
    }

    @Test
    public void needsMigrationNoneExisting() throws IOException {
        UserIdMigrator migrator = createUserIdMigrator();
        MatcherAssert.assertThat(migrator.needsMigration(), is(false));
    }

    @Test
    public void needsMigrationNoUserConfigFiles() throws IOException {
        UserIdMigrator migrator = createUserIdMigrator();
        MatcherAssert.assertThat(migrator.needsMigration(), is(false));
    }

    @Test
    public void scanExistingUsersNone() throws IOException {
        File usersDirectory = UserIdMigratorTest.createTestDirectory(getClass(), name);
        UserIdMigrator migrator = new UserIdMigrator(usersDirectory, IdStrategy.CASE_INSENSITIVE);
        Map<String, File> userMappings = migrator.scanExistingUsers();
        MatcherAssert.assertThat(userMappings.keySet(), empty());
    }

    @Test
    public void scanExistingUsersNoUsersDirectory() throws IOException {
        UserIdMigrator migrator = createUserIdMigrator();
        Map<String, File> userMappings = migrator.scanExistingUsers();
        MatcherAssert.assertThat(userMappings.keySet(), empty());
    }

    @Test
    public void scanExistingUsersBasic() throws IOException {
        UserIdMigrator migrator = createUserIdMigrator();
        Map<String, File> userMappings = migrator.scanExistingUsers();
        MatcherAssert.assertThat(userMappings.keySet(), hasSize(2));
        MatcherAssert.assertThat(userMappings.keySet(), hasItems("admin", "jane"));
    }

    @Test
    public void scanExistingUsersLegacy() throws IOException {
        UserIdMigrator migrator = createUserIdMigrator();
        Map<String, File> userMappings = migrator.scanExistingUsers();
        MatcherAssert.assertThat(userMappings.keySet(), hasSize(8));
        MatcherAssert.assertThat(userMappings.keySet(), hasItems("foo/bar", "foo/bar/baz", "/", "..", "bla$phem.us", "make$1000000", "big$money", "~com1"));
    }

    @Test
    public void scanExistingUsersOldLegacy() throws IOException {
        UserIdMigrator migrator = createUserIdMigrator();
        Map<String, File> userMappings = migrator.scanExistingUsers();
        MatcherAssert.assertThat(userMappings.keySet(), hasSize(4));
        MatcherAssert.assertThat(userMappings.keySet(), hasItems("make\u1000000", "\u306f\u56fd\u5185\u3067\u6700\u5927", "\u1000yyy", "zzz\u1000"));
    }

    @Test
    public void emptyUsernameConfigScanned() throws IOException {
        UserIdMigrator migrator = createUserIdMigrator();
        Map<String, File> userMappings = migrator.scanExistingUsers();
        MatcherAssert.assertThat(userMappings.keySet(), hasSize(2));
        MatcherAssert.assertThat(userMappings.keySet(), hasItems("admin", ""));
    }

    @Test
    public void scanExistingUsersCaseSensitive() throws IOException {
        File usersDirectory = UserIdMigratorTest.createTestDirectory(getClass(), name);
        UserIdMigrator migrator = new UserIdMigrator(usersDirectory, new IdStrategy.CaseSensitive());
        Map<String, File> userMappings = migrator.scanExistingUsers();
        MatcherAssert.assertThat(userMappings.keySet(), hasSize(3));
        MatcherAssert.assertThat(userMappings.keySet(), hasItems("admin", "Fred", "Jane"));
    }

    @Test
    public void migrateSimpleUser() throws IOException {
        File usersDirectory = UserIdMigratorTest.createTestDirectory(getClass(), name);
        IdStrategy idStrategy = IdStrategy.CASE_INSENSITIVE;
        UserIdMigrator migrator = new UserIdMigrator(usersDirectory, idStrategy);
        TestUserIdMapper mapper = new TestUserIdMapper(usersDirectory, idStrategy);
        init();
        MatcherAssert.assertThat(migrator.needsMigration(), is(false));
        mapper = new TestUserIdMapper(usersDirectory, idStrategy);
        init();
        MatcherAssert.assertThat(getConvertedUserIds().size(), is(1));
        MatcherAssert.assertThat(isMapped("fred"), is(true));
    }

    @Test
    public void migrateMultipleUsers() throws IOException {
        File usersDirectory = UserIdMigratorTest.createTestDirectory(getClass(), name);
        IdStrategy idStrategy = IdStrategy.CASE_INSENSITIVE;
        UserIdMigrator migrator = new UserIdMigrator(usersDirectory, idStrategy);
        TestUserIdMapper mapper = new TestUserIdMapper(usersDirectory, idStrategy);
        init();
        MatcherAssert.assertThat(migrator.needsMigration(), is(false));
        mapper = new TestUserIdMapper(usersDirectory, idStrategy);
        init();
        MatcherAssert.assertThat(getConvertedUserIds().size(), is(3));
        MatcherAssert.assertThat(isMapped("fred"), is(true));
        MatcherAssert.assertThat(isMapped("foo/bar"), is(true));
        MatcherAssert.assertThat(isMapped("zzz\u1000"), is(true));
    }

    @Test
    public void migrateUsersXml() throws IOException {
        File usersDirectory = UserIdMigratorTest.createTestDirectory(getClass(), name);
        IdStrategy idStrategy = IdStrategy.CASE_INSENSITIVE;
        UserIdMigrator migrator = new UserIdMigrator(usersDirectory, idStrategy);
        TestUserIdMapper mapper = new TestUserIdMapper(usersDirectory, idStrategy);
        init();
        MatcherAssert.assertThat(migrator.needsMigration(), is(false));
        mapper = new TestUserIdMapper(usersDirectory, idStrategy);
        init();
        MatcherAssert.assertThat(getConvertedUserIds().size(), is(1));
        MatcherAssert.assertThat(isMapped("users.xml"), is(true));
    }

    @Test
    public void migrateEntireDirectory() throws IOException {
        File usersDirectory = UserIdMigratorTest.createTestDirectory(getClass(), name);
        IdStrategy idStrategy = IdStrategy.CASE_INSENSITIVE;
        UserIdMigrator migrator = new UserIdMigrator(usersDirectory, idStrategy);
        TestUserIdMapper mapper = new TestUserIdMapper(usersDirectory, idStrategy);
        init();
        File fredDirectory = getDirectory("fred");
        File otherFile = new File(fredDirectory, "otherfile.txt");
        MatcherAssert.assertThat(otherFile.exists(), is(true));
        File originalFredDirectory = new File(usersDirectory, "fred");
        MatcherAssert.assertThat(originalFredDirectory.exists(), is(false));
    }
}

