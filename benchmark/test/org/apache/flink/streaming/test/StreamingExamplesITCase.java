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
package org.apache.flink.streaming.test;


import FileSystem.WriteMode.OVERWRITE;
import TimeCharacteristic.IngestionTime;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.examples.iteration.IterateExample;
import org.apache.flink.streaming.examples.join.WindowJoin;
import org.apache.flink.streaming.examples.ml.IncrementalLearningSkeleton;
import org.apache.flink.streaming.examples.twitter.TwitterExample;
import org.apache.flink.streaming.examples.windowing.SessionWindowing;
import org.apache.flink.streaming.examples.windowing.WindowWordCount;
import org.apache.flink.streaming.examples.wordcount.WordCount;
import org.apache.flink.test.util.AbstractTestBase;
import org.junit.Test;

import static org.apache.flink.streaming.test.examples.join.WindowJoinData.GRADES_INPUT;
import static org.apache.flink.streaming.test.examples.join.WindowJoinData.SALARIES_INPUT;


/**
 * Integration test for streaming programs in Java examples.
 */
public class StreamingExamplesITCase extends AbstractTestBase {
    @Test
    public void testIterateExample() throws Exception {
        final String inputPath = createTempFile("fibonacciInput.txt", IterateExampleData.INPUT_PAIRS);
        final String resultPath = getTempDirPath("result");
        // the example is inherently non-deterministic. The iteration timeout of 5000 ms
        // is frequently not enough to make the test run stable on CI infrastructure
        // with very small containers, so we cannot do a validation here
        IterateExample.main(new String[]{ "--input", inputPath, "--output", resultPath });
    }

    @Test
    public void testWindowJoin() throws Exception {
        final String resultPath = File.createTempFile("result-path", "dir").toURI().toString();
        final class Parser implements MapFunction<String, Tuple2<String, Integer>> {
            @Override
            public Tuple2<String, Integer> map(String value) throws Exception {
                String[] fields = value.split(",");
                return new Tuple2(fields[1], Integer.parseInt(fields[2]));
            }
        }
        try {
            final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            env.setStreamTimeCharacteristic(IngestionTime);
            DataStream<Tuple2<String, Integer>> grades = env.fromElements(GRADES_INPUT.split("\n")).map(new Parser());
            DataStream<Tuple2<String, Integer>> salaries = env.fromElements(SALARIES_INPUT.split("\n")).map(new Parser());
            WindowJoin.runWindowJoin(grades, salaries, 100).writeAsText(resultPath, OVERWRITE);
            env.execute();
            // since the two sides of the join might have different speed
            // the exact output can not be checked just whether it is well-formed
            // checks that the result lines look like e.g. (bob, 2, 2015)
            checkLinesAgainstRegexp(resultPath, "^\\([a-z]+,(\\d),(\\d)+\\)");
        } finally {
            try {
                FileUtils.deleteDirectory(new File(resultPath));
            } catch (Throwable ignored) {
            }
        }
    }

    @Test
    public void testIncrementalLearningSkeleton() throws Exception {
        final String resultPath = getTempDirPath("result");
        IncrementalLearningSkeleton.main(new String[]{ "--output", resultPath });
        compareResultsByLinesInMemory(IncrementalLearningSkeletonData.RESULTS, resultPath);
    }

    @Test
    public void testTwitterStream() throws Exception {
        final String resultPath = getTempDirPath("result");
        TwitterExample.main(new String[]{ "--output", resultPath });
        compareResultsByLinesInMemory(TwitterExampleData.STREAMING_COUNTS_AS_TUPLES, resultPath);
    }

    @Test
    public void testSessionWindowing() throws Exception {
        final String resultPath = getTempDirPath("result");
        SessionWindowing.main(new String[]{ "--output", resultPath });
        compareResultsByLinesInMemory(SessionWindowingData.EXPECTED, resultPath);
    }

    @Test
    public void testWindowWordCount() throws Exception {
        final String windowSize = "250";
        final String slideSize = "150";
        final String textPath = createTempFile("text.txt", WordCountData.TEXT);
        final String resultPath = getTempDirPath("result");
        WindowWordCount.main(new String[]{ "--input", textPath, "--output", resultPath, "--window", windowSize, "--slide", slideSize });
        // since the parallel tokenizers might have different speed
        // the exact output can not be checked just whether it is well-formed
        // checks that the result lines look like e.g. (faust, 2)
        checkLinesAgainstRegexp(resultPath, "^\\([a-z]+,(\\d)+\\)");
    }

    @Test
    public void testWordCount() throws Exception {
        final String textPath = createTempFile("text.txt", WordCountData.TEXT);
        final String resultPath = getTempDirPath("result");
        WordCount.main(new String[]{ "--input", textPath, "--output", resultPath });
        compareResultsByLinesInMemory(WordCountData.STREAMING_COUNTS_AS_TUPLES, resultPath);
    }
}

