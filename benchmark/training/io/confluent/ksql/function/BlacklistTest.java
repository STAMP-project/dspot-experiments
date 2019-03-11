/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.function;


import com.google.common.collect.ImmutableList;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.IOException;
import junit.framework.TestCase;
import org.junit.Test;


public class BlacklistTest {
    private File blacklistFile;

    @Test
    public void shouldBlackListAllInPackage() throws IOException {
        writeBlacklist(ImmutableList.of("java.lang"));
        final Blacklist blacklist = new Blacklist(this.blacklistFile);
        TestCase.assertTrue(blacklist.test("java.lang.Class"));
        TestCase.assertFalse(blacklist.test("java.util.List"));
    }

    @Test
    public void shouldBlackListClassesMatching() throws IOException {
        writeBlacklist(ImmutableList.of("java.lang.Process"));
        final Blacklist blacklist = new Blacklist(this.blacklistFile);
        TestCase.assertTrue(blacklist.test("java.lang.Process"));
        TestCase.assertTrue(blacklist.test("java.lang.ProcessBuilder"));
        TestCase.assertTrue(blacklist.test("java.lang.ProcessEnvironment"));
        TestCase.assertFalse(blacklist.test("java.lang.Class"));
    }

    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void shouldNotBlacklistAnythingIfFailsToLoadFile() {
        blacklistFile.delete();
        final Blacklist blacklist = new Blacklist(this.blacklistFile);
        TestCase.assertFalse(blacklist.test("java.lang.Process"));
        TestCase.assertFalse(blacklist.test("java.util.List"));
        TestCase.assertFalse(blacklist.test("java.lang.ProcessEnvironment"));
        TestCase.assertFalse(blacklist.test("java.lang.Class"));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void shouldNotBlacklistAnythingIfBlacklistFileIsEmpty() {
        final Blacklist blacklist = new Blacklist(this.blacklistFile);
        TestCase.assertFalse(blacklist.test("java.lang.Process"));
        TestCase.assertFalse(blacklist.test("java.util.List"));
        TestCase.assertFalse(blacklist.test("java.lang.ProcessEnvironment"));
        TestCase.assertFalse(blacklist.test("java.lang.Class"));
    }

    @Test
    public void shouldIgnoreBlankLines() throws IOException {
        writeBlacklist(ImmutableList.<String>builder().add("", "java.util", "").build());
        final Blacklist blacklist = new Blacklist(this.blacklistFile);
        TestCase.assertFalse(blacklist.test("java.lang.Process"));
        TestCase.assertTrue(blacklist.test("java.util.List"));
    }

    @Test
    public void shouldIgnoreLinesStartingWithHash() throws IOException {
        writeBlacklist(ImmutableList.<String>builder().add("#", "java.util", "#").build());
        final Blacklist blacklist = new Blacklist(this.blacklistFile);
        TestCase.assertFalse(blacklist.test("java.lang.String"));
        TestCase.assertTrue(blacklist.test("java.util.Map"));
    }

    @Test
    public void shouldNotBlackListAllClassesIfItemEndsWith$() throws IOException {
        writeBlacklist(ImmutableList.<String>builder().add("java.lang.Runtime$").build());
        final Blacklist blacklist = new Blacklist(this.blacklistFile);
        TestCase.assertTrue(blacklist.test("java.lang.Runtime"));
        TestCase.assertFalse(blacklist.test("java.lang.RuntimeException"));
    }
}

