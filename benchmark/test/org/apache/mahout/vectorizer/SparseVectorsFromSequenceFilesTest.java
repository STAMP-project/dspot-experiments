/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.mahout.vectorizer;


import SequenceFile.Writer;
import ThreadLeakScope.Scope;
import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope;
import com.google.common.io.Closeables;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.mahout.common.MahoutTestCase;
import org.apache.mahout.common.iterator.sequencefile.PathFilters;
import org.apache.mahout.common.iterator.sequencefile.PathType;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;
import org.junit.Test;


@ThreadLeakScope(Scope.NONE)
public class SparseVectorsFromSequenceFilesTest extends MahoutTestCase {
    private static final int NUM_DOCS = 100;

    private Configuration conf;

    private Path inputPath;

    @Test
    public void testCreateTermFrequencyVectors() throws Exception {
        setupDocs();
        runTest(false, false, false, (-1), SparseVectorsFromSequenceFilesTest.NUM_DOCS);
    }

    @Test
    public void testCreateTermFrequencyVectorsNam() throws Exception {
        setupDocs();
        runTest(false, false, true, (-1), SparseVectorsFromSequenceFilesTest.NUM_DOCS);
    }

    @Test
    public void testCreateTermFrequencyVectorsSeq() throws Exception {
        setupDocs();
        runTest(false, true, false, (-1), SparseVectorsFromSequenceFilesTest.NUM_DOCS);
    }

    @Test
    public void testCreateTermFrequencyVectorsSeqNam() throws Exception {
        setupDocs();
        runTest(false, true, true, (-1), SparseVectorsFromSequenceFilesTest.NUM_DOCS);
    }

    @Test
    public void testPruning() throws Exception {
        conf = getConfiguration();
        inputPath = getTestTempFilePath("documents/docs.file");
        FileSystem fs = FileSystem.get(inputPath.toUri(), conf);
        SequenceFile.Writer writer = new SequenceFile.Writer(fs, conf, inputPath, Text.class, Text.class);
        String[] docs = new String[]{ "a b c", "a a a a a b", "a a a a a c" };
        try {
            for (int i = 0; i < (docs.length); i++) {
                writer.append(new Text(("Document::ID::" + i)), new Text(docs[i]));
            }
        } finally {
            Closeables.close(writer, false);
        }
        Path outPath = runTest(false, false, false, 2, docs.length);
        Path tfidfVectors = new Path(outPath, "tfidf-vectors");
        int count = 0;
        Vector[] res = new Vector[docs.length];
        for (VectorWritable value : new org.apache.mahout.common.iterator.sequencefile.SequenceFileDirValueIterable<VectorWritable>(tfidfVectors, PathType.LIST, PathFilters.partFilter(), null, true, conf)) {
            Vector v = value.get();
            System.out.println(v);
            assertEquals(2, v.size());
            res[count] = v;
            count++;
        }
        assertEquals(docs.length, count);
        // the first doc should have two values, the second and third should have 1, since the a gets removed
        assertEquals(2, res[0].getNumNondefaultElements());
        assertEquals(1, res[1].getNumNondefaultElements());
        assertEquals(1, res[2].getNumNondefaultElements());
    }

    @Test
    public void testPruningTF() throws Exception {
        conf = getConfiguration();
        FileSystem fs = FileSystem.get(conf);
        inputPath = getTestTempFilePath("documents/docs.file");
        SequenceFile.Writer writer = new SequenceFile.Writer(fs, conf, inputPath, Text.class, Text.class);
        String[] docs = new String[]{ "a b c", "a a a a a b", "a a a a a c" };
        try {
            for (int i = 0; i < (docs.length); i++) {
                writer.append(new Text(("Document::ID::" + i)), new Text(docs[i]));
            }
        } finally {
            Closeables.close(writer, false);
        }
        Path outPath = runTest(true, false, false, 2, docs.length);
        Path tfVectors = new Path(outPath, "tf-vectors");
        int count = 0;
        Vector[] res = new Vector[docs.length];
        for (VectorWritable value : new org.apache.mahout.common.iterator.sequencefile.SequenceFileDirValueIterable<VectorWritable>(tfVectors, PathType.LIST, PathFilters.partFilter(), null, true, conf)) {
            Vector v = value.get();
            System.out.println(v);
            assertEquals(2, v.size());
            res[count] = v;
            count++;
        }
        assertEquals(docs.length, count);
        // the first doc should have two values, the second and third should have 1, since the a gets removed
        assertEquals(2, res[0].getNumNondefaultElements());
        assertEquals(1, res[1].getNumNondefaultElements());
        assertEquals(1, res[2].getNumNondefaultElements());
    }
}

