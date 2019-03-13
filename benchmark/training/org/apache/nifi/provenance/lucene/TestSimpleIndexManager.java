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
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.nifi.provenance.RepositoryConfiguration;
import org.apache.nifi.provenance.index.EventIndexSearcher;
import org.apache.nifi.provenance.index.EventIndexWriter;
import org.apache.nifi.util.file.FileUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestSimpleIndexManager {
    @Test
    public void testMultipleWritersSimultaneouslySameIndex() throws IOException {
        final SimpleIndexManager mgr = new SimpleIndexManager(new RepositoryConfiguration());
        final File dir = new File(("target/" + (UUID.randomUUID().toString())));
        try {
            final EventIndexWriter writer1 = mgr.borrowIndexWriter(dir);
            final EventIndexWriter writer2 = mgr.borrowIndexWriter(dir);
            final Document doc1 = new Document();
            doc1.add(new org.apache.lucene.document.StringField("id", "1", Store.YES));
            final Document doc2 = new Document();
            doc2.add(new org.apache.lucene.document.StringField("id", "2", Store.YES));
            writer1.index(doc1, 1000);
            writer2.index(doc2, 1000);
            mgr.returnIndexWriter(writer2);
            mgr.returnIndexWriter(writer1);
            final EventIndexSearcher searcher = mgr.borrowIndexSearcher(dir);
            final TopDocs topDocs = searcher.getIndexSearcher().search(new MatchAllDocsQuery(), 2);
            Assert.assertEquals(2, topDocs.totalHits);
            mgr.returnIndexSearcher(searcher);
        } finally {
            FileUtils.deleteFile(dir, true);
        }
    }

    @Test
    public void testWriterCloseIfPreviouslyMarkedCloseable() throws IOException {
        final AtomicInteger closeCount = new AtomicInteger(0);
        final SimpleIndexManager mgr = new SimpleIndexManager(new RepositoryConfiguration()) {
            @Override
            protected void close(IndexWriterCount count) throws IOException {
                closeCount.incrementAndGet();
            }
        };
        final File dir = new File(("target/" + (UUID.randomUUID().toString())));
        final EventIndexWriter writer1 = mgr.borrowIndexWriter(dir);
        final EventIndexWriter writer2 = mgr.borrowIndexWriter(dir);
        Assert.assertTrue((writer1 == writer2));
        mgr.returnIndexWriter(writer1, true, true);
        Assert.assertEquals(0, closeCount.get());
        final EventIndexWriter[] writers = new EventIndexWriter[10];
        for (int i = 0; i < (writers.length); i++) {
            writers[i] = mgr.borrowIndexWriter(dir);
            Assert.assertTrue(((writers[i]) == writer1));
        }
        for (int i = 0; i < (writers.length); i++) {
            mgr.returnIndexWriter(writers[i], true, false);
            Assert.assertEquals(0, closeCount.get());
            Assert.assertEquals(1, mgr.getWriterCount());
        }
        // this should close the index writer even though 'false' is passed in
        // because the previous call marked the writer as closeable and this is
        // the last reference to the writer.
        mgr.returnIndexWriter(writer2, false, false);
        Assert.assertEquals(1, closeCount.get());
        Assert.assertEquals(0, mgr.getWriterCount());
    }

    @Test
    public void testWriterCloseIfOnlyUser() throws IOException {
        final AtomicInteger closeCount = new AtomicInteger(0);
        final SimpleIndexManager mgr = new SimpleIndexManager(new RepositoryConfiguration()) {
            @Override
            protected void close(IndexWriterCount count) throws IOException {
                closeCount.incrementAndGet();
            }
        };
        final File dir = new File(("target/" + (UUID.randomUUID().toString())));
        final EventIndexWriter writer = mgr.borrowIndexWriter(dir);
        mgr.returnIndexWriter(writer, true, true);
        Assert.assertEquals(1, closeCount.get());
    }

    @Test
    public void testWriterLeftOpenIfNotCloseable() throws IOException {
        final AtomicInteger closeCount = new AtomicInteger(0);
        final SimpleIndexManager mgr = new SimpleIndexManager(new RepositoryConfiguration()) {
            @Override
            protected void close(IndexWriterCount count) throws IOException {
                closeCount.incrementAndGet();
            }
        };
        final File dir = new File(("target/" + (UUID.randomUUID().toString())));
        final EventIndexWriter writer = mgr.borrowIndexWriter(dir);
        mgr.returnIndexWriter(writer, true, false);
        Assert.assertEquals(0, closeCount.get());
    }
}

