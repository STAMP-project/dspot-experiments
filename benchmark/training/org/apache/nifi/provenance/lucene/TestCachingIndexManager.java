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
package org.apache.nifi.provenance.lucene;


import java.io.File;
import java.io.IOException;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.nifi.provenance.index.EventIndexSearcher;
import org.apache.nifi.provenance.index.EventIndexWriter;
import org.junit.Assert;
import org.junit.Test;


public class TestCachingIndexManager {
    private File indexDir;

    private CachingIndexManager manager;

    @Test
    public void test() throws IOException {
        // Create and IndexWriter and add a document to the index, then close the writer.
        // This gives us something that we can query.
        final EventIndexWriter writer = manager.borrowIndexWriter(indexDir);
        final Document doc = new Document();
        doc.add(new org.apache.lucene.document.StringField("unit test", "true", Store.YES));
        writer.index(doc, 1000);
        manager.returnIndexWriter(writer);
        // Get an Index Searcher that we can use to query the index.
        final EventIndexSearcher cachedSearcher = manager.borrowIndexSearcher(indexDir);
        // Ensure that we get the expected results.
        assertCount(cachedSearcher, 1);
        // While we already have an Index Searcher, get a writer for the same index.
        // This will cause the Index Searcher to be marked as poisoned.
        final EventIndexWriter writer2 = manager.borrowIndexWriter(indexDir);
        // Obtain a new Index Searcher with the writer open. This Index Searcher should *NOT*
        // be the same as the previous searcher because the new one will be a Near-Real-Time Index Searcher
        // while the other is not.
        final EventIndexSearcher nrtSearcher = manager.borrowIndexSearcher(indexDir);
        Assert.assertNotSame(cachedSearcher, nrtSearcher);
        // Ensure that we get the expected query results.
        assertCount(nrtSearcher, 1);
        // Return the writer, so that there is no longer an active writer for the index.
        manager.returnIndexWriter(writer2);
        // Ensure that we still get the same result.
        assertCount(cachedSearcher, 1);
        manager.returnIndexSearcher(cachedSearcher);
        // Ensure that our near-real-time index searcher still gets the same result.
        assertCount(nrtSearcher, 1);
        manager.returnIndexSearcher(nrtSearcher);
    }
}

