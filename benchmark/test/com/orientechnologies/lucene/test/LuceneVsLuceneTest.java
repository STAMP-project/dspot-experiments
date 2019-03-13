/**
 * * Copyright 2014 Orient Technologies.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 */
package com.orientechnologies.lucene.test;


import Field.Store;
import com.orientechnologies.lucene.analyzer.OLucenePerFieldAnalyzerWrapper;
import com.orientechnologies.orient.core.record.impl.ODocument;
import java.io.IOException;
import java.util.List;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Created by enricorisa on 08/10/14.
 */
@RunWith(JUnit4.class)
public class LuceneVsLuceneTest extends BaseLuceneTest {
    private IndexWriter indexWriter;

    private OLucenePerFieldAnalyzerWrapper analyzer;

    @Test
    public void testLuceneVsLucene() throws IOException, ParseException {
        for (ODocument oDocument : db.browseClass("Song")) {
            String title = oDocument.field("title");
            if (title != null) {
                Document d = new Document();
                d.add(new org.apache.lucene.document.TextField("title", title, Store.YES));
                d.add(new org.apache.lucene.document.TextField("Song.title", title, Store.YES));
                indexWriter.addDocument(d);
            }
        }
        indexWriter.commit();
        indexWriter.close();
        IndexReader reader = DirectoryReader.open(getDirectory());
        assertThat(reader.numDocs()).isEqualTo(Long.valueOf(db.countClass("Song")).intValue());
        IndexSearcher searcher = new IndexSearcher(reader);
        Query query = parse("down the");
        final TopDocs docs = searcher.search(query, Integer.MAX_VALUE);
        ScoreDoc[] hits = docs.scoreDocs;
        List<ODocument> oDocs = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select *,$score from Song where title LUCENE \"down the\""));
        Assert.assertEquals(oDocs.size(), hits.length);
        int i = 0;
        for (ScoreDoc hit : hits) {
            // Assert.assertEquals(oDocs.get(i).field("$score"), hit.score);
            assertThat(oDocs.get(i).<Float>field("$score")).isEqualTo(hit.score);
            i++;
        }
        reader.close();
    }
}

