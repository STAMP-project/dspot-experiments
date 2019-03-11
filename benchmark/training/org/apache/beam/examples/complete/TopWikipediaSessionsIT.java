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
package org.apache.beam.examples.complete;


import StandardResolveOptions.RESOLVE_DIRECTORY;
import StandardResolveOptions.RESOLVE_FILE;
import java.util.Date;
import org.apache.beam.sdk.io.FileSystems;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.testing.TestPipelineOptions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * End-to-end tests of TopWikipediaSessions.
 */
@RunWith(JUnit4.class)
public class TopWikipediaSessionsIT {
    private static final String DEFAULT_INPUT_10_FILES = "gs://apache-beam-samples/wikipedia_edits/wiki_data-000000000000.json";

    private static final String DEFAULT_OUTPUT_CHECKSUM = "13347784d49129bade295c371e30f9eb5325318a";

    /**
     * PipelineOptions for the TopWikipediaSessions integration test.
     */
    public interface TopWikipediaSessionsITOptions extends TopWikipediaSessions.Options , TestPipelineOptions {}

    @Test
    public void testE2ETopWikiPages() throws Exception {
        TopWikipediaSessionsIT.TopWikipediaSessionsITOptions options = TestPipeline.testingPipelineOptions().as(TopWikipediaSessionsIT.TopWikipediaSessionsITOptions.class);
        setWikiInput(TopWikipediaSessionsIT.DEFAULT_INPUT_10_FILES);
        options.setOutput(FileSystems.matchNewResource(getTempRoot(), true).resolve(String.format("topwikisessions-it-%tF-%<tH-%<tM-%<tS-%<tL", new Date()), RESOLVE_DIRECTORY).resolve("output", RESOLVE_DIRECTORY).resolve("results", RESOLVE_FILE).toString());
        setOnSuccessMatcher(new org.apache.beam.sdk.testing.FileChecksumMatcher(TopWikipediaSessionsIT.DEFAULT_OUTPUT_CHECKSUM, ((getOutput()) + "*-of-*")));
        TopWikipediaSessions.run(options);
    }
}

