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
package smile.sequence;


import CRF.Trainer;
import org.junit.Assert;
import org.junit.Test;
import smile.data.Attribute;


/**
 *
 *
 * @author Haifeng Li
 */
@SuppressWarnings("unused")
public class CRFTest {
    class Dataset {
        Attribute[] attributes;

        double[][][] x;

        int[][] y;

        int p;

        int k;
    }

    class IntDataset {
        int[][][] x;

        int[][] y;

        int p;

        int k;
    }

    public CRFTest() {
    }

    /**
     * Test of learn method, of class CRF.
     */
    @Test
    public void testLearnProteinSparse() {
        System.out.println("learn protein sparse");
        CRFTest.IntDataset train = load("sequence/sparse.protein.11.train");
        CRFTest.IntDataset test = load("sequence/sparse.protein.11.test");
        CRF.Trainer trainer = new CRF.Trainer(train.p, train.k);
        trainer.setLearningRate(0.3);
        trainer.setMaxNodes(100);
        trainer.setNumTrees(100);
        CRF crf = trainer.train(train.x, train.y);
        int error = 0;
        int n = 0;
        for (int i = 0; i < (test.x.length); i++) {
            n += test.x[i].length;
            int[] label = crf.predict(test.x[i]);
            for (int j = 0; j < (test.x[i].length); j++) {
                if ((test.y[i][j]) != (label[j])) {
                    error++;
                }
            }
        }
        int viterbiError = 0;
        crf.setViterbi(true);
        for (int i = 0; i < (test.x.length); i++) {
            n += test.x[i].length;
            int[] label = crf.predict(test.x[i]);
            for (int j = 0; j < (test.x[i].length); j++) {
                if ((test.y[i][j]) != (label[j])) {
                    viterbiError++;
                }
            }
        }
        System.out.format("Protein error (forward-backward) is %d of %d%n", error, n);
        System.out.format("Protein error (forward-backward) rate = %.2f%%%n", ((100.0 * error) / n));
        System.out.format("Protein error (Viterbi) is %d of %d%n", viterbiError, n);
        System.out.format("Protein error (Viterbi) rate = %.2f%%%n", ((100.0 * viterbiError) / n));
        Assert.assertEquals(1234, error);
        Assert.assertEquals(1318, viterbiError);
    }

    /**
     * Test of learn method, of class CRF.
     */
    @Test
    public void testLearnHyphenSparse() {
        System.out.println("learn hyphen sparse");
        CRFTest.IntDataset train = load("sequence/sparse.hyphen.6.train");
        CRFTest.IntDataset test = load("sequence/sparse.hyphen.6.test");
        CRF.Trainer trainer = new CRF.Trainer(train.p, train.k);
        trainer.setLearningRate(1.0);
        trainer.setMaxNodes(100);
        trainer.setNumTrees(100);
        CRF crf = trainer.train(train.x, train.y);
        int error = 0;
        int n = 0;
        for (int i = 0; i < (test.x.length); i++) {
            n += test.x[i].length;
            int[] label = crf.predict(test.x[i]);
            for (int j = 0; j < (test.x[i].length); j++) {
                if ((test.y[i][j]) != (label[j])) {
                    error++;
                }
            }
        }
        int viterbiError = 0;
        crf.setViterbi(true);
        for (int i = 0; i < (test.x.length); i++) {
            n += test.x[i].length;
            int[] label = crf.predict(test.x[i]);
            for (int j = 0; j < (test.x[i].length); j++) {
                if ((test.y[i][j]) != (label[j])) {
                    viterbiError++;
                }
            }
        }
        System.out.format("Hypen error (forward-backward) is %d of %d%n", error, n);
        System.out.format("Hypen error (forward-backward) rate = %.2f%%%n", ((100.0 * error) / n));
        System.out.format("Hypen error (Viterbi) is %d of %d%n", viterbiError, n);
        System.out.format("Hypen error (Viterbi) rate = %.2f%%%n", ((100.0 * viterbiError) / n));
        Assert.assertEquals(470, error);
        Assert.assertEquals(478, viterbiError);
    }

    /**
     * Test of learn method, of class CRF.
     */
    @Test
    public void testLearnProtein() {
        System.out.println("learn protein");
        CRFTest.Dataset train = load("sequence/sparse.protein.11.train", null);
        CRFTest.Dataset test = load("sequence/sparse.protein.11.test", train.attributes);
        CRF.Trainer trainer = new CRF.Trainer(train.attributes, train.k);
        trainer.setLearningRate(0.3);
        trainer.setMaxNodes(100);
        trainer.setNumTrees(100);
        CRF crf = trainer.train(train.x, train.y);
        int error = 0;
        int n = 0;
        for (int i = 0; i < (test.x.length); i++) {
            n += test.x[i].length;
            int[] label = crf.predict(test.x[i]);
            for (int j = 0; j < (test.x[i].length); j++) {
                if ((test.y[i][j]) != (label[j])) {
                    error++;
                }
            }
        }
        int viterbiError = 0;
        crf.setViterbi(true);
        for (int i = 0; i < (test.x.length); i++) {
            n += test.x[i].length;
            int[] label = crf.predict(test.x[i]);
            for (int j = 0; j < (test.x[i].length); j++) {
                if ((test.y[i][j]) != (label[j])) {
                    viterbiError++;
                }
            }
        }
        System.out.format("Protein error (forward-backward) is %d of %d%n", error, n);
        System.out.format("Protein error (forward-backward) rate = %.2f%%%n", ((100.0 * error) / n));
        System.out.format("Protein error (Viterbi) is %d of %d%n", viterbiError, n);
        System.out.format("Protein error (Viterbi) rate = %.2f%%%n", ((100.0 * viterbiError) / n));
        Assert.assertEquals(1270, error);
        Assert.assertEquals(1420, viterbiError);
    }

    /**
     * Test of learn method, of class CRF.
     */
    @Test
    public void testLearnHyphen() {
        System.out.println("learn hyphen");
        CRFTest.Dataset train = load("sequence/sparse.hyphen.6.train", null);
        CRFTest.Dataset test = load("sequence/sparse.hyphen.6.test", train.attributes);
        CRF.Trainer trainer = new CRF.Trainer(train.attributes, train.k);
        trainer.setLearningRate(1.0);
        trainer.setMaxNodes(100);
        trainer.setNumTrees(100);
        CRF crf = trainer.train(train.x, train.y);
        int error = 0;
        int n = 0;
        for (int i = 0; i < (test.x.length); i++) {
            n += test.x[i].length;
            int[] label = crf.predict(test.x[i]);
            for (int j = 0; j < (test.x[i].length); j++) {
                if ((test.y[i][j]) != (label[j])) {
                    error++;
                }
            }
        }
        int viterbiError = 0;
        crf.setViterbi(true);
        for (int i = 0; i < (test.x.length); i++) {
            n += test.x[i].length;
            int[] label = crf.predict(test.x[i]);
            for (int j = 0; j < (test.x[i].length); j++) {
                if ((test.y[i][j]) != (label[j])) {
                    viterbiError++;
                }
            }
        }
        System.out.format("Hypen error (forward-backward) is %d of %d%n", error, n);
        System.out.format("Hypen error (forward-backward) rate = %.2f%%%n", ((100.0 * error) / n));
        System.out.format("Hypen error (Viterbi) is %d of %d%n", viterbiError, n);
        System.out.format("Hypen error (Viterbi) rate = %.2f%%%n", ((100.0 * viterbiError) / n));
        Assert.assertEquals(473, error);
        Assert.assertEquals(478, viterbiError);
    }
}

