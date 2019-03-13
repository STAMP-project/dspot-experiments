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
package smile.nlp.collocation;


import java.io.BufferedReader;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import smile.data.parser.IOUtils;
import smile.nlp.SimpleCorpus;


/**
 *
 *
 * @author Haifeng Li
 */
public class BigramCollocationFinderTest {
    public BigramCollocationFinderTest() {
    }

    /**
     * Test of find method, of class BigramCollocationFinder.
     */
    @Test
    public void testFind_Corpus_int() {
        System.out.println("find");
        SimpleCorpus corpus = new SimpleCorpus();
        try (BufferedReader input = IOUtils.getTestDataReader("text/quote.tok.gt9.5000")) {
            String line = null;
            int id = 0;
            while ((line = input.readLine()) != null) {
                line = line.trim();
                if (!(line.isEmpty())) {
                    corpus.add(String.valueOf((id++)), null, line);
                }
            } 
        } catch (IOException e) {
            e.printStackTrace();
        }
        int k = 10;
        BigramCollocationFinder instance = new BigramCollocationFinder(5);
        BigramCollocation[] result = instance.find(corpus, k);
        for (int i = 0; i < (result.length); i++) {
            System.out.println(result[i]);
        }
    }

    /**
     * Test of find method, of class BigramCollocationFinder.
     */
    @Test
    public void testFind_Corpus_double() {
        System.out.println("find");
        SimpleCorpus corpus = new SimpleCorpus();
        try (BufferedReader input = IOUtils.getTestDataReader("text/quote.tok.gt9.5000")) {
            String line = null;
            int id = 0;
            while ((line = input.readLine()) != null) {
                line = line.trim();
                if (!(line.isEmpty())) {
                    corpus.add(String.valueOf(id), null, line);
                }
            } 
        } catch (IOException e) {
            e.printStackTrace();
        }
        double p = 1.0E-4;
        BigramCollocationFinder instance = new BigramCollocationFinder(5);
        BigramCollocation[] result = instance.find(corpus, p);
        Assert.assertEquals(52, result.length);
        for (int i = 0; i < (result.length); i++) {
            System.out.println(result[i]);
        }
    }
}

