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


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.mahout.common.MahoutTestCase;
import org.junit.Test;


public class EncodedVectorsFromSequenceFilesTest extends MahoutTestCase {
    private static final int NUM_DOCS = 100;

    private Configuration conf;

    private Path inputPath;

    @Test
    public void testCreate() throws Exception {
        runTest(false, false);
    }

    @Test
    public void testCreateNamed() throws Exception {
        runTest(false, true);
    }

    @Test
    public void testCreateSeq() throws Exception {
        runTest(true, false);
    }

    @Test
    public void testCreateSeqNamed() throws Exception {
        runTest(true, true);
    }
}

