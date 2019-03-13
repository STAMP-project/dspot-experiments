package com.orientechnologies.lucene.sandbox;


import Field.Store;
import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Test;


/**
 * Created by frank on 9/28/15.
 */
public class VertexIndexTest {
    @Test
    public void testSpacesInQuery() throws IOException, ParseException {
        IndexWriterConfig conf = new IndexWriterConfig(new StandardAnalyzer());
        final RAMDirectory directory = new RAMDirectory();
        final IndexWriter writer = new IndexWriter(directory, conf);
        Document doc = new Document();
        doc.add(new org.apache.lucene.document.TextField("name", "Max Water", Store.YES));
        writer.addDocument(doc);
        doc = new Document();
        doc.add(new org.apache.lucene.document.TextField("name", "Max Waterson", Store.YES));
        writer.addDocument(doc);
        doc = new Document();
        doc.add(new org.apache.lucene.document.TextField("name", "Cory Watney", Store.YES));
        writer.addDocument(doc);
        writer.commit();
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new StandardAnalyzer();
        QueryParser queryParser = new QueryParser("name", analyzer);
        final Query query = queryParser.parse("name:Max AND name:Wat*");
        final TopDocs topDocs = searcher.search(query, 10);
        assertThat(topDocs.totalHits).isEqualTo(2);
        for (int i = 0; i < (topDocs.totalHits); i++) {
            final Document found = searcher.doc(topDocs.scoreDocs[i].doc);
            assertThat(found.get("name")).startsWith("Max");
        }
        reader.close();
        writer.close();
    }
}

