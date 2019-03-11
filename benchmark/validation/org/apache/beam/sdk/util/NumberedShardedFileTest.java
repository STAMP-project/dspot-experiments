/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.util;


import NumberedShardedFile.BACK_OFF_FACTORY;
import StandardResolveOptions.RESOLVE_FILE;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import org.apache.beam.sdk.io.LocalResources;
import org.apache.beam.vendor.guava.v20_0.com.google.common.io.Files;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static NumberedShardedFile.MAX_READ_RETRIES;


/**
 * Tests for {@link NumberedShardedFile}.
 */
@RunWith(JUnit4.class)
public class NumberedShardedFileTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Sleeper fastClock = ( millis) -> {
        // No sleep.
    };

    private final BackOff backOff = BACK_OFF_FACTORY.backoff();

    private String filePattern;

    @Test
    public void testPreconditionFilePathIsNull() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(Matchers.containsString("Expected valid file path, but received"));
        new NumberedShardedFile(null);
    }

    @Test
    public void testPreconditionFilePathIsEmpty() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(Matchers.containsString("Expected valid file path, but received"));
        new NumberedShardedFile("");
    }

    @Test
    public void testReadMultipleShards() throws Exception {
        String contents1 = "To be or not to be, ";
        String contents2 = "it is not a question.";
        String contents3 = "should not be included";
        File tmpFile1 = tmpFolder.newFile("result-000-of-002");
        File tmpFile2 = tmpFolder.newFile("result-001-of-002");
        File tmpFile3 = tmpFolder.newFile("tmp");
        Files.write(contents1, tmpFile1, StandardCharsets.UTF_8);
        Files.write(contents2, tmpFile2, StandardCharsets.UTF_8);
        Files.write(contents3, tmpFile3, StandardCharsets.UTF_8);
        filePattern = LocalResources.fromFile(tmpFolder.getRoot(), true).resolve("result-*", RESOLVE_FILE).toString();
        NumberedShardedFile shardedFile = new NumberedShardedFile(filePattern);
        MatcherAssert.assertThat(shardedFile.readFilesWithRetries(), Matchers.containsInAnyOrder(contents1, contents2));
    }

    @Test
    public void testReadEmpty() throws Exception {
        File emptyFile = tmpFolder.newFile("result-000-of-001");
        Files.write("", emptyFile, StandardCharsets.UTF_8);
        NumberedShardedFile shardedFile = new NumberedShardedFile(filePattern);
        MatcherAssert.assertThat(shardedFile.readFilesWithRetries(), Matchers.empty());
    }

    @Test
    public void testReadCustomTemplate() throws Exception {
        String contents1 = "To be or not to be, ";
        String contents2 = "it is not a question.";
        // Customized template: resultSSS-totalNNN
        File tmpFile1 = tmpFolder.newFile("result0-total2");
        File tmpFile2 = tmpFolder.newFile("result1-total2");
        Files.write(contents1, tmpFile1, StandardCharsets.UTF_8);
        Files.write(contents2, tmpFile2, StandardCharsets.UTF_8);
        Pattern customizedTemplate = Pattern.compile("(?x) result (?<shardnum>\\d+) - total (?<numshards>\\d+)");
        NumberedShardedFile shardedFile = new NumberedShardedFile(filePattern, customizedTemplate);
        MatcherAssert.assertThat(shardedFile.readFilesWithRetries(), Matchers.containsInAnyOrder(contents1, contents2));
    }

    @Test
    public void testReadWithRetriesFailsWhenTemplateIncorrect() throws Exception {
        File tmpFile = tmpFolder.newFile();
        Files.write("Test for file checksum verifier.", tmpFile, StandardCharsets.UTF_8);
        NumberedShardedFile shardedFile = new NumberedShardedFile(filePattern, Pattern.compile("incorrect-template"));
        thrown.expect(IOException.class);
        thrown.expectMessage(Matchers.containsString(("Unable to read file(s) after retrying " + (MAX_READ_RETRIES))));
        shardedFile.readFilesWithRetries(fastClock, backOff);
    }

    @Test
    public void testReadWithRetriesFailsSinceFilesystemError() throws Exception {
        File tmpFile = tmpFolder.newFile();
        Files.write("Test for file checksum verifier.", tmpFile, StandardCharsets.UTF_8);
        NumberedShardedFile shardedFile = Mockito.spy(new NumberedShardedFile(filePattern));
        Mockito.doThrow(IOException.class).when(shardedFile).readLines(ArgumentMatchers.anyCollection());
        thrown.expect(IOException.class);
        thrown.expectMessage(Matchers.containsString(("Unable to read file(s) after retrying " + (MAX_READ_RETRIES))));
        shardedFile.readFilesWithRetries(fastClock, backOff);
    }

    @Test
    public void testReadWithRetriesFailsWhenOutputDirEmpty() throws Exception {
        NumberedShardedFile shardedFile = new NumberedShardedFile(filePattern);
        thrown.expect(IOException.class);
        thrown.expectMessage(Matchers.containsString(("Unable to read file(s) after retrying " + (MAX_READ_RETRIES))));
        shardedFile.readFilesWithRetries(fastClock, backOff);
    }

    @Test
    public void testReadWithRetriesFailsWhenRedundantFileLoaded() throws Exception {
        tmpFolder.newFile("result-000-of-001");
        tmpFolder.newFile("tmp-result-000-of-001");
        NumberedShardedFile shardedFile = new NumberedShardedFile(filePattern);
        thrown.expect(IOException.class);
        thrown.expectMessage(Matchers.containsString(("Unable to read file(s) after retrying " + (MAX_READ_RETRIES))));
        shardedFile.readFilesWithRetries(fastClock, backOff);
    }
}

