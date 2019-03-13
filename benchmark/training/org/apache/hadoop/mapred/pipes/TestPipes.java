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
package org.apache.hadoop.mapred.pipes;


import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.mapred.MiniMRCluster;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Ignore
public class TestPipes {
    private static final Logger LOG = LoggerFactory.getLogger(TestPipes.class);

    private static Path cppExamples = new Path(System.getProperty("install.c++.examples"));

    private static Path wordCountSimple = new Path(TestPipes.cppExamples, "bin/wordcount-simple");

    private static Path wordCountPart = new Path(TestPipes.cppExamples, "bin/wordcount-part");

    private static Path wordCountNoPipes = new Path(TestPipes.cppExamples, "bin/wordcount-nopipe");

    static Path nonPipedOutDir;

    @Test
    public void testPipes() throws IOException {
        if ((System.getProperty("compile.c++")) == null) {
            TestPipes.LOG.info("compile.c++ is not defined, so skipping TestPipes");
            return;
        }
        MiniDFSCluster dfs = null;
        MiniMRCluster mr = null;
        Path inputPath = new Path("testing/in");
        Path outputPath = new Path("testing/out");
        try {
            final int numWorkers = 2;
            Configuration conf = new Configuration();
            dfs = numDataNodes(numWorkers).build();
            mr = new MiniMRCluster(numWorkers, dfs.getFileSystem().getUri().toString(), 1);
            TestPipes.writeInputFile(dfs.getFileSystem(), inputPath);
            TestPipes.runProgram(mr, dfs, TestPipes.wordCountSimple, inputPath, outputPath, 3, 2, TestPipes.twoSplitOutput, null);
            TestPipes.cleanup(dfs.getFileSystem(), outputPath);
            TestPipes.runProgram(mr, dfs, TestPipes.wordCountSimple, inputPath, outputPath, 3, 0, TestPipes.noSortOutput, null);
            TestPipes.cleanup(dfs.getFileSystem(), outputPath);
            TestPipes.runProgram(mr, dfs, TestPipes.wordCountPart, inputPath, outputPath, 3, 2, TestPipes.fixedPartitionOutput, null);
            TestPipes.runNonPipedProgram(mr, dfs, TestPipes.wordCountNoPipes, null);
            mr.waitUntilIdle();
        } finally {
            mr.shutdown();
            dfs.shutdown();
        }
    }

    static final String[] twoSplitOutput = new String[]{ "`and\t1\na\t1\nand\t1\nbeginning\t1\nbook\t1\nbut\t1\nby\t1\n" + (("conversation?\'\t1\ndo:\t1\nhad\t2\nhaving\t1\nher\t2\nin\t1\nit\t1\n" + "it,\t1\nno\t1\nnothing\t1\nof\t3\non\t1\nonce\t1\nor\t3\npeeped\t1\n") + "pictures\t2\nthe\t3\nthought\t1\nto\t2\nuse\t1\nwas\t2\n"), "Alice\t2\n`without\t1\nbank,\t1\nbook,\'\t1\nconversations\t1\nget\t1\n" + ("into\t1\nis\t1\nreading,\t1\nshe\t1\nsister\t2\nsitting\t1\ntired\t1\n" + "twice\t1\nvery\t1\nwhat\t1\n") };

    static final String[] noSortOutput = new String[]{ "it,\t1\n`and\t1\nwhat\t1\nis\t1\nthe\t1\nuse\t1\nof\t1\na\t1\n" + ("book,\'\t1\nthought\t1\nAlice\t1\n`without\t1\npictures\t1\nor\t1\n" + "conversation?\'\t1\n"), "Alice\t1\nwas\t1\nbeginning\t1\nto\t1\nget\t1\nvery\t1\ntired\t1\n" + ("of\t1\nsitting\t1\nby\t1\nher\t1\nsister\t1\non\t1\nthe\t1\nbank,\t1\n" + "and\t1\nof\t1\nhaving\t1\nnothing\t1\nto\t1\ndo:\t1\nonce\t1\n"), "or\t1\ntwice\t1\nshe\t1\nhad\t1\npeeped\t1\ninto\t1\nthe\t1\nbook\t1\n" + ("her\t1\nsister\t1\nwas\t1\nreading,\t1\nbut\t1\nit\t1\nhad\t1\nno\t1\n" + "pictures\t1\nor\t1\nconversations\t1\nin\t1\n") };

    static final String[] fixedPartitionOutput = new String[]{ "Alice\t2\n`and\t1\n`without\t1\na\t1\nand\t1\nbank,\t1\nbeginning\t1\n" + ((((("book\t1\nbook,\'\t1\nbut\t1\nby\t1\nconversation?\'\t1\nconversations\t1\n" + "do:\t1\nget\t1\nhad\t2\nhaving\t1\nher\t2\nin\t1\ninto\t1\nis\t1\n") + "it\t1\nit,\t1\nno\t1\nnothing\t1\nof\t3\non\t1\nonce\t1\nor\t3\n") + "peeped\t1\npictures\t2\nreading,\t1\nshe\t1\nsister\t2\nsitting\t1\n") + "the\t3\nthought\t1\ntired\t1\nto\t2\ntwice\t1\nuse\t1\n") + "very\t1\nwas\t2\nwhat\t1\n"), "" };
}

