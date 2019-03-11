/**
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, ???, yang-shangchuan@qq.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.apdplat.word.lucene;


import Field.Store;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apdplat.word.util.Utils;
import org.apdplat.word.util.WordConfTools;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author ???
 */
public class ChineseWordAnalyzerTest {
    @Test
    public void test1() {
        try {
            Analyzer analyzer = new ChineseWordAnalyzer();
            TokenStream tokenStream = analyzer.tokenStream("text", "????APDPlat????????????");
            List<String> words = new ArrayList<>();
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                CharTermAttribute charTermAttribute = tokenStream.getAttribute(CharTermAttribute.class);
                words.add(charTermAttribute.toString());
            } 
            tokenStream.close();
            String expResult = "[???, ?, apdplat, ???, ??, ??, ??, ?, ??]";
            if ("bigram".equals(WordConfTools.get("ngram", "bigram"))) {
                expResult = "[???, ?, apdplat, ??, ?, ??, ??, ??, ?, ??]";
            }
            Assert.assertEquals(expResult, words.toString());
        } catch (IOException e) {
            Assert.fail(("????" + (e.getMessage())));
        }
    }

    @Test
    public void test2() {
        try {
            Analyzer analyzer = new ChineseWordAnalyzer();
            TokenStream tokenStream = analyzer.tokenStream("text", "???????????");
            List<String> words = new ArrayList<>();
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                CharTermAttribute charTermAttribute = tokenStream.getAttribute(CharTermAttribute.class);
                words.add(charTermAttribute.toString());
            } 
            tokenStream.close();
            String expResult = "[??, ??, ?, ??, ?, ??, ?]";
            Assert.assertEquals(expResult, words.toString());
        } catch (IOException e) {
            Assert.fail(("????" + (e.getMessage())));
        }
    }

    @Test
    public void test3() {
        Analyzer analyzer = new ChineseWordAnalyzer();
        List<String> sentences = new ArrayList<>();
        sentences.add("????APDPlat????????????");
        sentences.add("???????");
        sentences.add("????????");
        sentences.add("???????????????");
        sentences.add("??????????????");
        sentences.add("???????????????????");
        sentences.add("???????");
        sentences.add("????????");
        sentences.add("?????????????");
        sentences.add("?????");
        sentences.add("?????????????");
        sentences.add("????????????");
        sentences.add("????????");
        sentences.add("????????,???????");
        sentences.add("???????");
        sentences.add("??????");
        sentences.add("????????");
        sentences.add("??????????");
        sentences.add("???????");
        sentences.add("??????");
        sentences.add("?????");
        sentences.add("?????????");
        sentences.add("???????");
        sentences.add("??????????");
        sentences.add("??????????");
        sentences.add("???????????");
        sentences.add("?????");
        sentences.add("???????");
        sentences.add("????????????");
        sentences.add("????????????");
        sentences.add("???????????");
        sentences.add("????????????");
        sentences.add("?????");
        sentences.add("??");
        sentences.add("???????????");
        sentences.add("???????????");
        sentences.add("????");
        sentences.add("??????");
        sentences.add("??????");
        sentences.add("??????");
        sentences.add("??????????????");
        sentences.add("word?????????????????????????ysc");
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setUseCompoundFile(false);
        File index = new File("target/indexes");
        Utils.deleteDir(index);
        try (Directory directory = new SimpleFSDirectory(index.toPath());IndexWriter indexWriter = new IndexWriter(directory, config)) {
            for (String sentence : sentences) {
                Document doc = new Document();
                Field field = new org.apache.lucene.document.TextField("text", sentence, Store.YES);
                doc.add(field);
                indexWriter.addDocument(doc);
            }
            indexWriter.commit();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(("????" + (e.getMessage())));
        }
        try (Directory directory = new SimpleFSDirectory(index.toPath());DirectoryReader directoryReader = DirectoryReader.open(directory)) {
            IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
            QueryParser queryParser = new QueryParser("text", analyzer);
            Query query = queryParser.parse("text:???");
            TopDocs docs = indexSearcher.search(query, Integer.MAX_VALUE);
            Assert.assertEquals(2, docs.totalHits);
            Assert.assertEquals("word?????????????????????????ysc", indexSearcher.doc(docs.scoreDocs[0].doc).get("text"));
            Assert.assertEquals("????APDPlat????????????", indexSearcher.doc(docs.scoreDocs[1].doc).get("text"));
            query = queryParser.parse("text:??");
            docs = indexSearcher.search(query, Integer.MAX_VALUE);
            Assert.assertEquals(1, docs.totalHits);
            Assert.assertEquals("???????", indexSearcher.doc(docs.scoreDocs[0].doc).get("text"));
        } catch (Exception e) {
            Assert.fail(("????" + (e.getMessage())));
        }
    }
}

