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
package smile.nlp.pos;


import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import smile.validation.CrossValidation;


/**
 *
 *
 * @author Haifeng Li
 */
public class HMMPOSTaggerTest {
    List<String[]> sentences = new ArrayList<>();

    List<PennTreebankPOS[]> labels = new ArrayList<>();

    public HMMPOSTaggerTest() {
    }

    /**
     * Test of learn method, of class HMMPOSTagger.
     */
    @Test
    public void testWSJ() {
        System.out.println("WSJ");
        load("D:\\sourceforge\\corpora\\PennTreebank\\PennTreebank2\\TAGGED\\POS\\WSJ");
        String[][] x = sentences.toArray(new String[sentences.size()][]);
        PennTreebankPOS[][] y = labels.toArray(new PennTreebankPOS[labels.size()][]);
        int n = x.length;
        int k = 10;
        CrossValidation cv = new CrossValidation(n, k);
        int error = 0;
        int total = 0;
        for (int i = 0; i < k; i++) {
            String[][] trainx = Math.slice(x, cv.train[i]);
            PennTreebankPOS[][] trainy = Math.slice(y, cv.train[i]);
            String[][] testx = Math.slice(x, cv.test[i]);
            PennTreebankPOS[][] testy = Math.slice(y, cv.test[i]);
            HMMPOSTagger tagger = HMMPOSTagger.learn(trainx, trainy);
            for (int j = 0; j < (testx.length); j++) {
                PennTreebankPOS[] label = tagger.tag(testx[j]);
                total += label.length;
                for (int l = 0; l < (label.length); l++) {
                    if ((label[l]) != (testy[j][l])) {
                        error++;
                    }
                }
            }
        }
        System.out.format("Error rate = %.2f as %d of %d\n", ((100.0 * error) / total), error, total);
    }

    /**
     * Test of learn method, of class HMMPOSTagger.
     */
    @Test
    public void testBrown() {
        System.out.println("BROWN");
        load("D:\\sourceforge\\corpora\\PennTreebank\\PennTreebank2\\TAGGED\\POS\\BROWN");
        String[][] x = sentences.toArray(new String[sentences.size()][]);
        PennTreebankPOS[][] y = labels.toArray(new PennTreebankPOS[labels.size()][]);
        int n = x.length;
        int k = 10;
        CrossValidation cv = new CrossValidation(n, k);
        int error = 0;
        int total = 0;
        for (int i = 0; i < k; i++) {
            String[][] trainx = Math.slice(x, cv.train[i]);
            PennTreebankPOS[][] trainy = Math.slice(y, cv.train[i]);
            String[][] testx = Math.slice(x, cv.test[i]);
            PennTreebankPOS[][] testy = Math.slice(y, cv.test[i]);
            HMMPOSTagger tagger = HMMPOSTagger.learn(trainx, trainy);
            for (int j = 0; j < (testx.length); j++) {
                PennTreebankPOS[] label = tagger.tag(testx[j]);
                total += label.length;
                for (int l = 0; l < (label.length); l++) {
                    if ((label[l]) != (testy[j][l])) {
                        error++;
                    }
                }
            }
        }
        System.out.format("Error rate = %.2f as %d of %d\n", ((100.0 * error) / total), error, total);
    }
}

