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
package org.apache.mahout.math.neighborhood;


import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import java.util.List;
import org.apache.mahout.common.Pair;
import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.random.WeightedThing;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class SearchQualityTest {
    private static final int NUM_DATA_POINTS = 1 << 14;

    private static final int NUM_QUERIES = 1 << 10;

    private static final int NUM_DIMENSIONS = 40;

    private static final int NUM_RESULTS = 2;

    private final Searcher searcher;

    private final Matrix dataPoints;

    private final Matrix queries;

    private Pair<List<List<WeightedThing<Vector>>>, Long> reference;

    private Pair<List<WeightedThing<Vector>>, Long> referenceSearchFirst;

    public SearchQualityTest(Searcher searcher, Matrix dataPoints, Matrix queries, Pair<List<List<WeightedThing<Vector>>>, Long> reference, Pair<List<WeightedThing<Vector>>, Long> referenceSearchFirst) {
        this.searcher = searcher;
        this.dataPoints = dataPoints;
        this.queries = queries;
        this.reference = reference;
        this.referenceSearchFirst = referenceSearchFirst;
    }

    @Test
    public void testOverlapAndRuntimeSearchFirst() {
        searcher.clear();
        searcher.addAll(dataPoints);
        Pair<List<WeightedThing<Vector>>, Long> results = SearchQualityTest.getResultsAndRuntimeSearchFirst(searcher, queries);
        int numFirstMatches = 0;
        for (int i = 0; i < (queries.numRows()); ++i) {
            WeightedThing<Vector> referenceVector = referenceSearchFirst.getFirst().get(i);
            WeightedThing<Vector> resultVector = results.getFirst().get(i);
            if (referenceVector.getValue().equals(resultVector.getValue())) {
                ++numFirstMatches;
            }
        }
        double bruteSearchAvgTime = (reference.getSecond()) / ((queries.numRows()) * 1.0);
        double searcherAvgTime = (results.getSecond()) / ((queries.numRows()) * 1.0);
        System.out.printf("%s: first matches %d [%d]; avg_time(1 query) %f(s) [%f]\n", searcher.getClass().getName(), numFirstMatches, queries.numRows(), searcherAvgTime, bruteSearchAvgTime);
        Assert.assertEquals("Closest vector returned doesn't match", queries.numRows(), numFirstMatches);
        Assert.assertTrue((("Searcher " + (searcher.getClass().getName())) + " slower than brute"), (bruteSearchAvgTime > searcherAvgTime));
    }

    @Test
    public void testOverlapAndRuntime() {
        searcher.clear();
        searcher.addAll(dataPoints);
        Pair<List<List<WeightedThing<Vector>>>, Long> results = SearchQualityTest.getResultsAndRuntime(searcher, queries);
        int numFirstMatches = 0;
        int numMatches = 0;
        SearchQualityTest.StripWeight stripWeight = new SearchQualityTest.StripWeight();
        for (int i = 0; i < (queries.numRows()); ++i) {
            List<WeightedThing<Vector>> referenceVectors = reference.getFirst().get(i);
            List<WeightedThing<Vector>> resultVectors = results.getFirst().get(i);
            if (referenceVectors.get(0).getValue().equals(resultVectors.get(0).getValue())) {
                ++numFirstMatches;
            }
            for (Vector v : Iterables.transform(referenceVectors, stripWeight)) {
                for (Vector w : Iterables.transform(resultVectors, stripWeight)) {
                    if (v.equals(w)) {
                        ++numMatches;
                    }
                }
            }
        }
        double bruteSearchAvgTime = (reference.getSecond()) / ((queries.numRows()) * 1.0);
        double searcherAvgTime = (results.getSecond()) / ((queries.numRows()) * 1.0);
        System.out.printf("%s: first matches %d [%d]; total matches %d [%d]; avg_time(1 query) %f(s) [%f]\n", searcher.getClass().getName(), numFirstMatches, queries.numRows(), numMatches, ((queries.numRows()) * (SearchQualityTest.NUM_RESULTS)), searcherAvgTime, bruteSearchAvgTime);
        Assert.assertEquals("Closest vector returned doesn't match", queries.numRows(), numFirstMatches);
        Assert.assertTrue((("Searcher " + (searcher.getClass().getName())) + " slower than brute"), (bruteSearchAvgTime > searcherAvgTime));
    }

    static class StripWeight implements Function<WeightedThing<Vector>, Vector> {
        @Override
        public Vector apply(WeightedThing<Vector> input) {
            Preconditions.checkArgument((input != null), "input is null");
            // noinspection ConstantConditions
            return input.getValue();
        }
    }
}

