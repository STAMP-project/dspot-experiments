/**
 * *****************************************************************************
 * Copyright (c) 2010 Haifeng Li
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */
package smile.nlp;


import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;
import smile.data.parser.IOUtils;
import smile.nlp.relevance.BM25;
import smile.nlp.relevance.Relevance;


/**
 *
 *
 * @author Haifeng Li
 */
public class SimpleCorpusTest {
    SimpleCorpus corpus = new SimpleCorpus();

    public SimpleCorpusTest() {
        try (BufferedReader input = IOUtils.getTestDataReader("text/quote.tok.gt9.5000")) {
            String line = null;
            int id = 0;
            while ((line = input.readLine()) != null) {
                line = line.trim();
                if (!(line.isEmpty())) {
                    corpus.add(String.valueOf((id++)), null, line);
                }
            } 
        } catch (IOException ex) {
        }
    }

    /**
     * Test of size method, of class SimpleCorpus.
     */
    @Test
    public void testSize() {
        System.out.println("size");
        Assert.assertEquals(51797, corpus.size());
    }

    /**
     * Test of getNumDocuments method, of class SimpleCorpus.
     */
    @Test
    public void testGetNumDocuments() {
        System.out.println("getNumDocuments");
        Assert.assertEquals(5000, corpus.getNumDocuments());
    }

    /**
     * Test of getNumTerms method, of class SimpleCorpus.
     */
    @Test
    public void testGetNumTerms() {
        System.out.println("getNumTerms");
        Assert.assertEquals(14335, corpus.getNumTerms());
    }

    /**
     * Test of getNumBigrams method, of class SimpleCorpus.
     */
    @Test
    public void testGetNumBigrams() {
        System.out.println("getNumBigrams");
        Assert.assertEquals(17121, corpus.getNumBigrams());
    }

    /**
     * Test of getAverageDocumentSize method, of class SimpleCorpus.
     */
    @Test
    public void testGetAverageDocumentSize() {
        System.out.println("getAverageDocumentSize");
        Assert.assertEquals(10, corpus.getAverageDocumentSize());
    }

    /**
     * Test of getTermFrequency method, of class SimpleCorpus.
     */
    @Test
    public void testGetTermFrequency() {
        System.out.println("getTermFrequency");
        Assert.assertEquals(50, corpus.getTermFrequency("romantic"));
    }

    /**
     * Test of getBigramFrequency method, of class SimpleCorpus.
     */
    @Test
    public void testGetBigramFrequency() {
        System.out.println("getBigramFrequency");
        Bigram bigram = new Bigram("romantic", "comedy");
        Assert.assertEquals(29, corpus.getBigramFrequency(bigram));
    }

    /**
     * Test of search method, of class SimpleCorpus.
     */
    @Test
    public void testSearch() {
        System.out.println("search 'romantic'");
        Iterator<Relevance> hits = corpus.search(new BM25(), "romantic");
        while (hits.hasNext()) {
            Relevance hit = hits.next();
            System.out.println((((hit.doc()) + "\t") + (hit.score())));
        } 
    }

    /**
     * Test of search method, of class SimpleCorpus, without hits.
     */
    @Test
    public void testSearchNoResult() {
        System.out.println("search 'romantic'");
        Iterator<Relevance> hits = corpus.search(new BM25(), "find");
        Assert.assertEquals(Collections.emptyIterator(), hits);
    }

    /**
     * Test of search method, of class SimpleCorpus.
     */
    @Test
    public void testSearch2() {
        System.out.println("search 'romantic comedy'");
        String[] terms = new String[]{ "romantic", "comedy" };
        Iterator<Relevance> hits = corpus.search(new BM25(), terms);
        while (hits.hasNext()) {
            Relevance hit = hits.next();
            System.out.println((((hit.doc()) + "\t") + (hit.score())));
        } 
    }

    /**
     * Test of search method, of class SimpleCorpus.
     */
    @Test
    public void testSearch2WithNoHits() {
        System.out.println("search 'no hits'");
        String[] terms = new String[]{ "thisisnotaword" };
        Iterator<Relevance> hits = corpus.search(new BM25(), terms);
        Assert.assertEquals(false, hits.hasNext());
    }
}

