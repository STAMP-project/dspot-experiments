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
package org.apache.beam.examples;


import FluentBackoff.DEFAULT;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BiFunction;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.options.StreamingOptions;
import org.apache.beam.sdk.testing.SerializableMatcher;
import org.apache.beam.sdk.testing.StreamingIT;
import org.apache.beam.sdk.testing.TestPipelineOptions;
import org.apache.beam.sdk.util.FluentBackoff;
import org.apache.beam.sdk.util.ShardedFile;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.joda.time.Duration;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.Matchers.equalTo;


/**
 * End-to-end integration test of {@link WindowedWordCount}.
 */
@RunWith(JUnit4.class)
public class WindowedWordCountIT {
    @Rule
    public TestName testName = new TestName();

    private static final String DEFAULT_INPUT = "gs://apache-beam-samples/shakespeare/sonnets.txt";

    static final int MAX_READ_RETRIES = 4;

    static final Duration DEFAULT_SLEEP_DURATION = Duration.standardSeconds(10L);

    static final FluentBackoff BACK_OFF_FACTORY = DEFAULT.withInitialBackoff(WindowedWordCountIT.DEFAULT_SLEEP_DURATION).withMaxRetries(WindowedWordCountIT.MAX_READ_RETRIES);

    /**
     * Options for the {@link WindowedWordCount} Integration Test.
     */
    public interface WindowedWordCountITOptions extends WindowedWordCount.Options , StreamingOptions , TestPipelineOptions {}

    @Test
    public void testWindowedWordCountInBatchDynamicSharding() throws Exception {
        WindowedWordCountIT.WindowedWordCountITOptions options = batchOptions();
        // This is the default value, but make it explicit.
        options.setNumShards(null);
        testWindowedWordCountPipeline(options);
    }

    @Test
    public void testWindowedWordCountInBatchStaticSharding() throws Exception {
        WindowedWordCountIT.WindowedWordCountITOptions options = batchOptions();
        setNumShards(3);
        testWindowedWordCountPipeline(options);
    }

    // TODO: add a test with streaming and dynamic sharding after resolving
    // https://issues.apache.org/jira/browse/BEAM-1438
    @Test
    @Category(StreamingIT.class)
    public void testWindowedWordCountInStreamingStaticSharding() throws Exception {
        WindowedWordCountIT.WindowedWordCountITOptions options = streamingOptions();
        setNumShards(3);
        testWindowedWordCountPipeline(options);
    }

    /**
     * A matcher that bakes in expected word counts, so they can be read directly via some other
     * mechanism, and compares a sharded output file with the result.
     */
    private static class WordCountsMatcher extends TypeSafeMatcher<PipelineResult> implements SerializableMatcher<PipelineResult> {
        private final SortedMap<String, Long> expectedWordCounts;

        private final List<ShardedFile> outputFiles;

        private SortedMap<String, Long> actualCounts;

        public WordCountsMatcher(SortedMap<String, Long> expectedWordCounts, List<ShardedFile> outputFiles) {
            this.expectedWordCounts = expectedWordCounts;
            this.outputFiles = outputFiles;
        }

        @Override
        public boolean matchesSafely(PipelineResult pipelineResult) {
            try {
                // Load output data
                List<String> outputLines = new ArrayList<>();
                for (ShardedFile outputFile : outputFiles) {
                    outputLines.addAll(outputFile.readFilesWithRetries(Sleeper.DEFAULT, WindowedWordCountIT.BACK_OFF_FACTORY.backoff()));
                }
                // Since the windowing is nondeterministic we only check the sums
                actualCounts = new TreeMap<>();
                for (String line : outputLines) {
                    String[] splits = line.split(": ", (-1));
                    String word = splits[0];
                    long count = Long.parseLong(splits[1]);
                    actualCounts.merge(word, count, ( a, b) -> a + b);
                }
                return actualCounts.equals(expectedWordCounts);
            } catch (Exception e) {
                throw new RuntimeException(String.format("Failed to read from sharded output: %s due to exception", outputFiles), e);
            }
        }

        @Override
        public void describeTo(Description description) {
            equalTo(expectedWordCounts).describeTo(description);
        }

        @Override
        public void describeMismatchSafely(PipelineResult pResult, Description description) {
            equalTo(expectedWordCounts).describeMismatch(actualCounts, description);
        }
    }
}

