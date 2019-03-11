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
package smile.neighbor;


import SNLSH.AbstractSentence;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.junit.Test;


/**
 * Test data set: http://research.microsoft.com/en-us/downloads/607d14d9-20cd-47e3-85bc-a2f65cd28042/
 *
 * @author Qiyang Zuo
 * @since 03/31/15
 */
public class SNLSHTest {
    private class Sentence extends SNLSH.AbstractSentence {
        public Sentence(String line) {
            this.line = line;
            this.tokens = tokenize(line);
        }

        @Override
        List<String> tokenize(String line) {
            return tokenize(line, " ");
        }

        private List<String> tokenize(String line, String regex) {
            List<String> tokens = new LinkedList<>();
            if ((line == null) || (line.isEmpty())) {
                throw new IllegalArgumentException("Line should not be blank!");
            }
            String[] ss = line.split(regex);
            for (String s : ss) {
                if ((s == null) || (s.isEmpty())) {
                    continue;
                }
                tokens.add(s);
            }
            return tokens;
        }
    }

    private String[] texts = new String[]{ "This is a test case", "This is another test case", "This is another test case too", "I want to be far from other cases" };

    private List<SNLSHTest.Sentence> testData;

    private List<SNLSHTest.Sentence> trainData;

    private List<SNLSHTest.Sentence> toyData;

    private Map<String, Long> signCache;// tokens<->sign


    @Test
    public void testKNN() {
        SNLSH<SNLSH.AbstractSentence> lsh = createLSH(toyData);
        SNLSH.AbstractSentence sentence = new SNLSHTest.Sentence(texts[0]);
        Neighbor<SNLSH.AbstractSentence, SNLSH.AbstractSentence>[] ns = lsh.knn(sentence, 10);
        System.out.println("-----test knn: ------");
        for (int i = 0; i < (ns.length); i++) {
            System.out.println(((((("neighbor" + i) + " : ") + (ns[i].key.line)) + ". distance: ") + (ns[i].distance)));
        }
        System.out.println("------test knn end------");
    }

    @Test
    public void testKNNRecall() {
        SNLSH<SNLSH.AbstractSentence> lsh = createLSH(trainData);
        double recall = 0.0;
        for (SNLSH.AbstractSentence q : testData) {
            int k = 3;
            Neighbor<SNLSH.AbstractSentence, SNLSH.AbstractSentence>[] n1 = lsh.knn(q, k);
            Neighbor<SNLSH.AbstractSentence, SNLSH.AbstractSentence>[] n2 = linearKNN(q, k);
            int hit = 0;
            for (int m = 0; (m < (n1.length)) && ((n1[m]) != null); m++) {
                for (int n = 0; (n < (n2.length)) && ((n2[n]) != null); n++) {
                    if (n1[m].value.equals(n2[n].value)) {
                        hit++;
                        break;
                    }
                }
            }
            recall += (1.0 * hit) / k;
        }
        recall /= testData.size();
        System.out.println(("SNLSH KNN recall is " + recall));
    }

    @Test
    public void testNearest() {
        SNLSH<SNLSH.AbstractSentence> lsh = createLSH(toyData);
        System.out.println("----------test nearest start:-------");
        Neighbor<SNLSH.AbstractSentence, SNLSH.AbstractSentence> n = lsh.nearest(((SNLSH.AbstractSentence) (new SNLSHTest.Sentence(texts[0]))));
        System.out.println((((("neighbor" + " : ") + (n.key.line)) + " distance: ") + (n.distance)));
        System.out.println("----------test nearest end-------");
    }

    @Test
    public void testNearestRecall() {
        SNLSH<SNLSH.AbstractSentence> lsh = createLSH(trainData);
        double recall = 0.0;
        for (SNLSH.AbstractSentence q : testData) {
            Neighbor<SNLSH.AbstractSentence, SNLSH.AbstractSentence> n1 = lsh.nearest(q);
            Neighbor<SNLSH.AbstractSentence, SNLSH.AbstractSentence> n2 = linearNearest(q);
            if (n1.value.equals(n2.value)) {
                recall++;
            }
        }
        recall /= testData.size();
        System.out.println(("SNLSH Nearest recall is " + recall));
    }

    @Test
    public void testRange() {
        SNLSH<SNLSH.AbstractSentence> lsh = createLSH(toyData);
        List<Neighbor<SNLSH.AbstractSentence, SNLSH.AbstractSentence>> ns = new ArrayList<>();
        lsh.range(new SNLSHTest.Sentence(texts[0]), 10, ns);
        System.out.println("-------test range begin-------");
        for (Neighbor<SNLSH.AbstractSentence, SNLSH.AbstractSentence> n : ns) {
            System.out.println((((n.key.line) + "  distance: ") + (n.distance)));
        }
        System.out.println("-----test range end ----------");
    }

    @Test
    public void testRangeRecall() {
        SNLSH<SNLSH.AbstractSentence> lsh = createLSH(trainData);
        double dist = 15.0;
        double recall = 0.0;
        for (SNLSHTest.Sentence q : testData) {
            List<Neighbor<SNLSH.AbstractSentence, SNLSH.AbstractSentence>> n1 = new ArrayList<>();
            lsh.range(q, dist, n1);
            List<Neighbor<SNLSH.AbstractSentence, SNLSH.AbstractSentence>> n2 = new ArrayList<>();
            linearRange(q, dist, n2);
            int hit = 0;
            for (int m = 0; m < (n1.size()); m++) {
                for (int n = 0; n < (n2.size()); n++) {
                    if (n1.get(m).value.equals(n2.get(n).value)) {
                        hit++;
                        break;
                    }
                }
            }
            if (!(n2.isEmpty())) {
                recall += (1.0 * hit) / (n2.size());
            }
        }
        recall /= testData.size();
        System.out.println(("SNLSH range recall is " + recall));
    }
}

