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
package smile.gap;


import BitString.Crossover;
import GeneticAlgorithm.Selection;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Haifeng Li
 */
public class GeneticAlgorithmTest {
    public GeneticAlgorithmTest() {
    }

    class Knapnack implements FitnessMeasure<BitString> {
        int limit = 9;// weight limit


        int[] weight = new int[]{ 2, 3, 4, 4, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };

        double[] reward = new double[]{ 6, 6, 6, 5, 1.3, 1.2, 1.1, 1.0, 1.1, 1.3, 1.0, 1.0, 0.9, 0.8, 0.6 };

        @Override
        public double fit(BitString chromosome) {
            double wsum = 0.0;
            double rew = 0.0;
            int[] bits = chromosome.bits();
            for (int i = 0; i < (weight.length); i++) {
                if ((bits[i]) == 1) {
                    wsum += weight[i];
                    rew += reward[i];
                }
            }
            // subtract penalty for exceeding weight
            if (wsum > (limit)) {
                rew -= 5 * (wsum - (limit));
            }
            return rew;
        }
    }

    /**
     * Test of evolve method, of class GeneticAlgorithm.
     */
    @Test
    public void testEvolve() {
        System.out.println("evolve");
        BitString[] seeds = new BitString[100];
        // The mutation parameters are set higher than usual to prevent premature convergence.
        for (int i = 0; i < (seeds.length); i++) {
            seeds[i] = new BitString(15, new GeneticAlgorithmTest.Knapnack(), Crossover.UNIFORM, 1.0, 0.2);
        }
        GeneticAlgorithm<BitString> instance = new GeneticAlgorithm(seeds, Selection.TOURNAMENT);
        instance.setElitism(2);
        instance.setTournament(3, 0.95);
        BitString result = instance.evolve(1000, 18);
        Assert.assertEquals(18, result.fitness(), 1.0E-7);
        int[] best = new int[]{ 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        for (int i = 0; i < (best.length); i++) {
            Assert.assertEquals(best[i], result.bits()[i]);
        }
    }
}

