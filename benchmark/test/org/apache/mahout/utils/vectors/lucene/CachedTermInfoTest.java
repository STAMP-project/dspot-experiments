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
package org.apache.mahout.utils.vectors.lucene;


import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.RAMDirectory;
import org.apache.mahout.common.MahoutTestCase;
import org.junit.Test;


public class CachedTermInfoTest extends MahoutTestCase {
    private RAMDirectory directory;

    private static final String[] DOCS = new String[]{ "a a b b c c", "a b a b a b a b", "a b a", "a", "b", "a", "a" };

    private static final String[] DOCS2 = new String[]{ "d d d d", "e e e e", "d e d e", "d", "e", "d", "e" };

    @Test
    public void test() throws Exception {
        IndexReader reader = DirectoryReader.open(directory);
        CachedTermInfo cti = new CachedTermInfo(reader, "content", 0, 100);
        assertEquals(3, cti.totalTerms("content"));
        assertNotNull(cti.getTermEntry("content", "a"));
        assertNull(cti.getTermEntry("content", "e"));
        // minDf
        cti = new CachedTermInfo(reader, "content", 3, 100);
        assertEquals(2, cti.totalTerms("content"));
        assertNotNull(cti.getTermEntry("content", "a"));
        assertNull(cti.getTermEntry("content", "c"));
        // maxDFPercent, a is in 6 of 7 docs: numDocs * maxDfPercent / 100 < 6 to exclude, 85% should suffice to exclude a
        cti = new CachedTermInfo(reader, "content", 0, 85);
        assertEquals(2, cti.totalTerms("content"));
        assertNotNull(cti.getTermEntry("content", "b"));
        assertNotNull(cti.getTermEntry("content", "c"));
        assertNull(cti.getTermEntry("content", "a"));
    }
}

