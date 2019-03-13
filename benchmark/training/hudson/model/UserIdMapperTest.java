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


import IdStrategy.CASE_INSENSITIVE;
import java.io.File;
import java.io.IOException;
import jenkins.model.IdStrategy;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


public class UserIdMapperTest {
    @Rule
    public TestName name = new TestName();

    @Test
    public void testNonexistentFileLoads() throws IOException {
        UserIdMapper mapper = createUserIdMapper(CASE_INSENSITIVE);
    }

    @Test
    public void testEmptyGet() throws IOException {
        UserIdMapper mapper = createUserIdMapper(CASE_INSENSITIVE);
        MatcherAssert.assertThat(mapper.getDirectory("anything"), nullValue());
    }

    @Test
    public void testSimplePutGet() throws IOException {
        UserIdMapper mapper = createUserIdMapper(CASE_INSENSITIVE);
        String user1 = "user1";
        File directory = mapper.putIfAbsent(user1, true);
        MatcherAssert.assertThat(directory, is(mapper.getDirectory(user1)));
    }

    @Test
    public void testMultiple() throws IOException {
        UserIdMapper mapper = createUserIdMapper(CASE_INSENSITIVE);
        String user1 = "user1";
        File directory1 = mapper.putIfAbsent(user1, true);
        String user2 = "user2";
        File directory2 = mapper.putIfAbsent(user2, true);
        String user3 = "user3";
        File directory3 = mapper.putIfAbsent(user3, true);
        MatcherAssert.assertThat(directory1, is(mapper.getDirectory(user1)));
        MatcherAssert.assertThat(directory2, is(mapper.getDirectory(user2)));
        MatcherAssert.assertThat(directory3, is(mapper.getDirectory(user3)));
    }

    @Test
    public void testMultipleSaved() throws IOException {
        File usersDirectory = UserIdMigratorTest.createTestDirectory(getClass(), name);
        IdStrategy idStrategy = IdStrategy.CASE_INSENSITIVE;
        UserIdMapper mapper = new TestUserIdMapper(usersDirectory, idStrategy);
        mapper.init();
        String user1 = "user1";
        File directory1 = mapper.putIfAbsent(user1, true);
        String user2 = "user2";
        File directory2 = mapper.putIfAbsent(user2, true);
        String user3 = "user3";
        File directory3 = mapper.putIfAbsent(user3, true);
        mapper = new TestUserIdMapper(usersDirectory, idStrategy);
        mapper.init();
        MatcherAssert.assertThat(directory1, is(mapper.getDirectory(user1)));
        MatcherAssert.assertThat(directory2, is(mapper.getDirectory(user2)));
        MatcherAssert.assertThat(directory3, is(mapper.getDirectory(user3)));
    }

    @Test
    public void testRepeatPut() throws IOException {
        UserIdMapper mapper = createUserIdMapper(CASE_INSENSITIVE);
        String user1 = "user1";
        File directory1 = mapper.putIfAbsent(user1, true);
        File directory2 = mapper.putIfAbsent(user1, true);
        MatcherAssert.assertThat(directory1, is(directory2));
    }

    @Test
    public void testIsNotMapped() throws IOException {
        UserIdMapper mapper = createUserIdMapper(CASE_INSENSITIVE);
        MatcherAssert.assertThat(mapper.isMapped("anything"), is(false));
    }

    @Test
    public void testIsMapped() throws IOException {
        UserIdMapper mapper = createUserIdMapper(CASE_INSENSITIVE);
        String user1 = "user1";
        File directory = mapper.putIfAbsent(user1, true);
        MatcherAssert.assertThat(mapper.isMapped(user1), is(true));
    }

    @Test
    public void testInitialUserIds() throws IOException {
        UserIdMapper mapper = createUserIdMapper(CASE_INSENSITIVE);
        MatcherAssert.assertThat(mapper.getConvertedUserIds(), empty());
    }

    @Test
    public void testSingleUserIds() throws IOException {
        UserIdMapper mapper = createUserIdMapper(CASE_INSENSITIVE);
        String user1 = "user1";
        File directory = mapper.putIfAbsent(user1, true);
        MatcherAssert.assertThat(mapper.getConvertedUserIds(), hasSize(1));
        MatcherAssert.assertThat(mapper.getConvertedUserIds().iterator().next(), is(user1));
    }

    @Test
    public void testMultipleUserIds() throws IOException {
        UserIdMapper mapper = createUserIdMapper(CASE_INSENSITIVE);
        String user1 = "user1";
        File directory = mapper.putIfAbsent(user1, true);
        String user2 = "user2";
        File directory2 = mapper.putIfAbsent(user2, true);
        MatcherAssert.assertThat(mapper.getConvertedUserIds(), hasSize(2));
        MatcherAssert.assertThat(mapper.getConvertedUserIds(), hasItems(user1, user2));
    }

    @Test
    public void testRemove() throws IOException {
        UserIdMapper mapper = createUserIdMapper(CASE_INSENSITIVE);
        String user1 = "user1";
        File directory = mapper.putIfAbsent(user1, true);
        mapper.remove(user1);
        MatcherAssert.assertThat(mapper.isMapped(user1), is(false));
    }

    @Test
    public void testRemoveAfterSave() throws IOException {
        File usersDirectory = UserIdMigratorTest.createTestDirectory(getClass(), name);
        IdStrategy idStrategy = IdStrategy.CASE_INSENSITIVE;
        UserIdMapper mapper = new TestUserIdMapper(usersDirectory, idStrategy);
        mapper.init();
        String user1 = "user1";
        File directory = mapper.putIfAbsent(user1, true);
        mapper = new TestUserIdMapper(usersDirectory, idStrategy);
        mapper.init();
        mapper.remove(user1);
        mapper = new TestUserIdMapper(usersDirectory, idStrategy);
        MatcherAssert.assertThat(mapper.isMapped(user1), is(false));
    }

    @Test
    public void testPutGetCaseInsensitive() throws IOException {
        UserIdMapper mapper = createUserIdMapper(CASE_INSENSITIVE);
        String user1 = "user1";
        File directory = mapper.putIfAbsent(user1, true);
        MatcherAssert.assertThat(mapper.getDirectory(user1.toUpperCase()), notNullValue());
    }

    @Test
    public void testPutGetCaseSensitive() throws IOException {
        IdStrategy idStrategy = new IdStrategy.CaseSensitive();
        UserIdMapper mapper = createUserIdMapper(idStrategy);
        String user1 = "user1";
        File directory = mapper.putIfAbsent(user1, true);
        MatcherAssert.assertThat(mapper.getDirectory(user1.toUpperCase()), nullValue());
    }

    @Test
    public void testIsMappedCaseInsensitive() throws IOException {
        UserIdMapper mapper = createUserIdMapper(CASE_INSENSITIVE);
        String user1 = "user1";
        File directory = mapper.putIfAbsent(user1, true);
        MatcherAssert.assertThat(mapper.isMapped(user1.toUpperCase()), is(true));
    }

    @Test
    public void testIsMappedCaseSensitive() throws IOException {
        IdStrategy idStrategy = new IdStrategy.CaseSensitive();
        UserIdMapper mapper = createUserIdMapper(idStrategy);
        String user1 = "user1";
        File directory = mapper.putIfAbsent(user1, true);
        MatcherAssert.assertThat(mapper.isMapped(user1.toUpperCase()), is(false));
    }

    @Test
    public void testRemoveCaseInsensitive() throws IOException {
        UserIdMapper mapper = createUserIdMapper(CASE_INSENSITIVE);
        String user1 = "user1";
        File directory = mapper.putIfAbsent(user1, true);
        mapper.remove(user1.toUpperCase());
        MatcherAssert.assertThat(mapper.isMapped(user1), is(false));
    }

    @Test
    public void testRemoveCaseSensitive() throws IOException {
        IdStrategy idStrategy = new IdStrategy.CaseSensitive();
        UserIdMapper mapper = createUserIdMapper(idStrategy);
        String user1 = "user1";
        File directory = mapper.putIfAbsent(user1, true);
        mapper.remove(user1.toUpperCase());
        MatcherAssert.assertThat(mapper.isMapped(user1), is(true));
    }

    @Test
    public void testRepeatRemove() throws IOException {
        UserIdMapper mapper = createUserIdMapper(CASE_INSENSITIVE);
        String user1 = "user1";
        File directory1 = mapper.putIfAbsent(user1, true);
        mapper.remove(user1);
        mapper.remove(user1);
        MatcherAssert.assertThat(mapper.isMapped(user1), is(false));
    }

    @Test
    public void testClear() throws IOException {
        UserIdMapper mapper = createUserIdMapper(CASE_INSENSITIVE);
        String user1 = "user1";
        File directory1 = mapper.putIfAbsent(user1, true);
        mapper.clear();
        MatcherAssert.assertThat(mapper.isMapped(user1), is(false));
        MatcherAssert.assertThat(mapper.getConvertedUserIds(), empty());
    }

    @Test
    public void testReload() throws IOException {
        UserIdMapper mapper = createUserIdMapper(CASE_INSENSITIVE);
        String user1 = "user1";
        File directory1 = mapper.putIfAbsent(user1, true);
        mapper.reload();
        MatcherAssert.assertThat(mapper.isMapped(user1), is(true));
        MatcherAssert.assertThat(mapper.getConvertedUserIds(), hasSize(1));
    }

    @Test
    public void testDirectoryFormatBasic() throws IOException {
        UserIdMapper mapper = createUserIdMapper(CASE_INSENSITIVE);
        String user1 = "1user";
        File directory1 = mapper.putIfAbsent(user1, true);
        MatcherAssert.assertThat(directory1.getName(), startsWith((user1 + '_')));
    }

    @Test
    public void testDirectoryFormatLongerUserId() throws IOException {
        UserIdMapper mapper = createUserIdMapper(CASE_INSENSITIVE);
        String user1 = "muchlongeruserid";
        File directory1 = mapper.putIfAbsent(user1, true);
        MatcherAssert.assertThat(directory1.getName(), startsWith("muchlongeruser_"));
    }

    @Test
    public void testDirectoryFormatAllSuppressedCharacters() throws IOException {
        UserIdMapper mapper = createUserIdMapper(CASE_INSENSITIVE);
        String user1 = "!@#$%^";
        File directory1 = mapper.putIfAbsent(user1, true);
        MatcherAssert.assertThat(directory1.getName(), startsWith("_"));
    }

    @Test
    public void testDirectoryFormatSingleCharacter() throws IOException {
        UserIdMapper mapper = createUserIdMapper(CASE_INSENSITIVE);
        String user1 = ".";
        File directory1 = mapper.putIfAbsent(user1, true);
        MatcherAssert.assertThat(directory1.getName(), startsWith("_"));
    }

    @Test
    public void testDirectoryFormatMixed() throws IOException {
        UserIdMapper mapper = createUserIdMapper(CASE_INSENSITIVE);
        String user1 = "a$b!c^d~e@f";
        File directory1 = mapper.putIfAbsent(user1, true);
        MatcherAssert.assertThat(directory1.getName(), startsWith("abcdef_"));
    }

    @Test(expected = IOException.class)
    public void testXmlFileCorrupted() throws IOException {
        UserIdMapper mapper = createUserIdMapper(CASE_INSENSITIVE);
    }

    @Test
    public void testDuplicatedUserId() throws IOException {
        UserIdMapper mapper = createUserIdMapper(CASE_INSENSITIVE);
        MatcherAssert.assertThat(mapper.isMapped("user2"), is(true));
        MatcherAssert.assertThat(mapper.isMapped("user1"), is(true));
    }
}

