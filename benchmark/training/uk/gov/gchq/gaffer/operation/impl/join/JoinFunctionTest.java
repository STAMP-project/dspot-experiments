/**
 * Copyright 2018-2019 Crown Copyright
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
 */
package uk.gov.gchq.gaffer.operation.impl.join;


import MatchKey.LEFT;
import MatchKey.RIGHT;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.TestPropertyNames;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.comparison.ElementJoinComparator;
import uk.gov.gchq.gaffer.operation.impl.join.match.Match;
import uk.gov.gchq.koryphe.tuple.MapTuple;


public abstract class JoinFunctionTest {
    private List<Element> leftInput = Arrays.asList(getElement(1), getElement(2), getElement(3), getElement(3), getElement(4), getElement(10));

    private List<Element> rightInput = Arrays.asList(getElement(1), getElement(2), getElement(2), getElement(3), getElement(4), getElement(12));

    @Test
    public void shouldCorrectlyJoinTwoIterablesUsingLeftKey() {
        if (null == (getJoinFunction())) {
            throw new RuntimeException("No JoinFunction specified by the test.");
        }
        Iterable result = getJoinFunction().join(leftInput, rightInput, new JoinFunctionTest.ElementMatch(), LEFT, false);
        List<MapTuple> expected = getExpectedLeftKeyResults();
        Assert.assertEquals(expected.size(), ((List) (result)).size());
        assertTupleListsEquality(expected, ((List<MapTuple>) (result)));
    }

    @Test
    public void shouldCorrectlyJoinTwoIterablesUsingRightKey() {
        if (null == (getJoinFunction())) {
            throw new RuntimeException("No JoinFunction specified by the test.");
        }
        Iterable result = getJoinFunction().join(leftInput, rightInput, new JoinFunctionTest.ElementMatch(), RIGHT, false);
        List<MapTuple> expected = getExpectedRightKeyResults();
        Assert.assertEquals(expected.size(), ((List) (result)).size());
        assertTupleListsEquality(expected, ((List<MapTuple>) (result)));
    }

    @Test
    public void shouldCorrectlyJoinTwoIterablesUsingLeftKeyAndFlattenResults() {
        if (null == (getJoinFunction())) {
            throw new RuntimeException("No JoinFunction specified by the test.");
        }
        Iterable result = getJoinFunction().join(leftInput, rightInput, new JoinFunctionTest.ElementMatch(), LEFT, true);
        List<MapTuple> expected = getExpectedLeftKeyResultsFlattened();
        Assert.assertEquals(expected.size(), ((List) (result)).size());
        assertTupleListsEquality(expected, ((List<MapTuple>) (result)));
    }

    @Test
    public void shouldCorrectlyJoinTwoIterablesUsingRightKeyAndFlattenResults() {
        if (null == (getJoinFunction())) {
            throw new RuntimeException("No JoinFunction specified by the test.");
        }
        Iterable result = getJoinFunction().join(leftInput, rightInput, new JoinFunctionTest.ElementMatch(), RIGHT, true);
        List<MapTuple> expected = getExpectedRightKeyResultsFlattened();
        Assert.assertEquals(expected.size(), ((List) (result)).size());
        assertTupleListsEquality(expected, ((List<MapTuple>) (result)));
    }

    /**
     * private copy of the ElementMatch class using the count property to match by.
     */
    private class ElementMatch implements Match {
        private Iterable matchCandidates;

        @Override
        public void init(final Iterable matchCandidates) {
            this.matchCandidates = matchCandidates;
        }

        @Override
        public List matching(final Object testObject) {
            List matches = new ArrayList<>();
            ElementJoinComparator elementJoinComparator = new ElementJoinComparator(TestPropertyNames.COUNT);
            for (Object entry : matchCandidates) {
                if (elementJoinComparator.test(((Element) (entry)), ((Element) (testObject)))) {
                    matches.add(shallowClone());
                }
            }
            return matches;
        }
    }
}

