/**
 * *  Copyright 2016 OrientDB LTD (info(at)orientdb.com)
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *       http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *  *
 *  * For more information: http://www.orientdb.com
 */
package com.orientechnologies.orient.core.tx;


import Interpretation.Dictionary;
import Interpretation.NonUnique;
import Interpretation.Unique;
import com.orientechnologies.orient.core.tx.OTransactionIndexChangesPerKey.OTransactionIndexEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.Test;


/**
 *
 *
 * @author Sergey Sitnikov
 */
public class IndexChangesInterpretationTest {
    // @formatter:off
    private static final String[][] TEST_VECTORS = new String[][]{ // Following domain specific language is used:
    // 
    // 1. Input: 'pN' for put N into the key, 'rN' for remove N from the key, 'd' for the key deletion.
    // 
    // 2. Output: same as input, with addition of sets like '{N p1 p2}', there order of operation doesn't matter,
    // but N of them must match or all of them if N is not specified. Sets can't nest. Also, 'r' w/o number matches
    // any removal operation, either 'rN' or 'd'.
    // 
    // "Parsers" are very ad hoc, you have been warned. As always, the only problems with DSLs is that you have
    // to prove they are parsed correctly. I'm too lazy to write tests for the tests.
    // 
    // 1st column is changes sequence, 2nd is expected sequence for unique index, 3rd for dictionary, 4th for non-unique.
    new String[]{ "", "", "", "" }, new String[]{ "p1", "p1", "p1", "p1" }, new String[]{ "r1", "r", "r", "r1" }, new String[]{ "d", "d", "d", "d" }, new String[]{ "p1 p2", "{p1 p2}", "p2", "{p1 p2}" }, new String[]{ "p1 r1", "", "", "" }, new String[]{ "p1 r2", "r p1", "p1", "{p1 r2}" }, new String[]{ "r1 r2", "r", "r", "{r1 r2}" }, new String[]{ "r1 p1", "r p1", "p1", "p1" }, // in theory, maybe optimized to an empty sequence, but it's not safe, can't touch this
    new String[]{ "r2 p1", "r p1", "p1", "{p1 r2}" }, new String[]{ "r1 p1 p2", "r {p1 p2}", "p2", "{p1 p2}" }, new String[]{ "r1 p2 p1", "r {p1 p2}", "p1", "{p1 p2}" }, new String[]{ "p2 r1 p1", "r {p1 p2}", "p1", "{p1, p2}" }, new String[]{ "r1 r2 p2 p1", "r {p1 p2}", "p1", "{p1 p2}" }// actually, invalid input, but we must support things like that
    // actually, invalid input, but we must support things like that
    // actually, invalid input, but we must support things like that
    , new String[]{ "r1 r2 p1 p2", "r {p1 p2}", "p2", "{p1 p2}" }, new String[]{ "r1 p1 r2 p2", "r {p1 p2}", "p2", "{p1 p2}" }, new String[]{ "p1 p2 r2", "p1", "p1", "p1" }, new String[]{ "p1 p2 r1", "p2", "p2", "p2" }, new String[]{ "p1 r1 p2", "p2", "p2", "p2" }, new String[]{ "p1 p2 p3", "{2 p1 p2 p3}", "p3", "{p1 p2 p3}" }, new String[]{ "p1 p2 p3 p4", "{2 p1 p2 p3 p4}", "p4", "{p1 p2 p3 p4}" }, new String[]{ "p1 r1", "", "", "" }, new String[]{ "p1 p2 r1 r2", "", "", "" }, new String[]{ "p1 p2 r2 r1", "", "", "" }, new String[]{ "p1 r1 p2 r2", "", "", "" }, new String[]{ "p1 r1 p2 r2 r3", "r3", "r3", "r3" }, new String[]{ "p1 p1", "p1", "p1", "p1" }, new String[]{ "r1 r1", "r", "r", "r1" }, new String[]{ "p1 p1 p1", "p1", "p1", "p1" }, new String[]{ "r1 r1 r1", "r", "r", "r1" }, new String[]{ "p1 p1 p2", "{p1 p2}", "p2", "{p1 p2}" }, new String[]{ "p1 p1 p2 p1 p1", "{p1 p2}", "p1", "{p1 p2}" }, new String[]{ "r1 r1 r2 r1 r1", "r", "r", "{r1 r2}" }, new String[]{ "p1 d", "r", "r", "d" }, new String[]{ "d p1", "r p1", "p1", "d p1" }, new String[]{ "r1 d", "r", "r", "d" }, new String[]{ "d r1", "r", "r", "d" }, new String[]{ "d d", "r", "r", "d" }, new String[]{ "p1 d p1", "r p1", "p1", "d p1" }, new String[]{ "p2 d p1", "r p1", "p1", "d p1" }, new String[]{ "d p1 p2", "r {p1 p2}", "p2", "d {p1 p2}" }, new String[]{ "d p1 p2 r2", "r p1", "p1", "d p1" }, new String[]{ "r1 d r2", "r", "r", "d" }, new String[]{ "r1 d r2 d", "r", "r", "d" }, new String[]{ "d r1 r2", "r", "r", "d" }, new String[]{ "d d d", "r", "r", "d" }, new String[]{ "p1 p2 p3 p4 p5 d p1", "r p1", "p1", "d p1" }, new String[]{ "p1 p2 p3 p4 p5 d p1 p10", "r {p1 p10}", "p10", "d {p1 p10}" }, new String[]{ "p1 p2 p3 p4 p5 d p1 p10 d", "r", "r", "d" }, new String[]{ "r1 p1 p2 p3 r1 r3 d r10 p100 p200", "r {p100 p200}", "p200", "d {p100 p200}" }, new String[]{ "r1 p1 p2 p3 r1 r3 d r10 p100 p200 r100", "r p200", "p200", "d p200" }, new String[]{ "r1 p1 p2 p3 r1 r3 d r10 p100 p200 r100 d", "r", "r", "d" }, new String[]{ "r1 p1 p2 p3 r1 r3 d r10 p100 p200 r100 d r1 p1 p2 p3 r2 r100", "r {p1 p3}", "p3", "d {p1 p3}" }, new String[]{ "p1 p2 p3 r2 r3 p4", "{p1 p4}", "p4", "{p1 p4}" }, new String[]{ "p1 p2 p3 r2 p4 r3", "{p1 p4}", "p4", "{p1 p4}" }, new String[]{ "p1 p2 p3 p4 r2 r3", "{p1 p4}", "p4", "{p1 p4}" }, new String[]{ "p1 p2 p4 p3 r2 r3", "{p1 p4}", "p4", "{p1 p4}" }, new String[]{ "p1 p2 p4 p3 r3 r2", "{p1 p4}", "p4", "{p1 p4}" }, new String[]{ "p1 p4 p2 p3 r3 r2", "{p1 p4}", "p4", "{p1 p4}" }, new String[]{ "p4 p1 p2 p3 r2 r3", "{p1 p4}", "p1", "{p1 p4}" }, new String[]{ "p1 p2 p3 d p1 p2 p3", "r {2 p1 p2 p3}", "p3", "d {p1 p2 p3}" }, new String[]{ "p1 p2 p3 d p1 d p2 p3", "r {2 p2 p3}", "p3", "d {p2 p3}" }, new String[]{ "p1 p2 p3 d p1 p2 d p3", "r p3", "p3", "d p3" } };

    // @formatter:on
    private static final Pattern INPUT_GRAMMAR = Pattern.compile("\\s*([pr]\\d+|d)\\s*", Pattern.CASE_INSENSITIVE);

    private static final Pattern OUTPUT_GRAMMAR = Pattern.compile("\\s*([pr]\\d+|d|r|\\{.*\\})\\s*", Pattern.CASE_INSENSITIVE);

    private static final Pattern OUTPUT_ITEMS_GRAMMAR = Pattern.compile("\\s*([pr]\\d+|d|r)\\s*", Pattern.CASE_INSENSITIVE);

    @Test
    public void test() {
        final OTransactionIndexChangesPerKey changes = new OTransactionIndexChangesPerKey("key");
        final List<IndexChangesInterpretationTest.OutputCollection> expectedUnique = new ArrayList<IndexChangesInterpretationTest.OutputCollection>();
        final List<IndexChangesInterpretationTest.OutputCollection> expectedDictionary = new ArrayList<IndexChangesInterpretationTest.OutputCollection>();
        final List<IndexChangesInterpretationTest.OutputCollection> expectedNonUnique = new ArrayList<IndexChangesInterpretationTest.OutputCollection>();
        for (String[] vector : IndexChangesInterpretationTest.TEST_VECTORS) {
            parseInput(vector[0], changes.entries);
            parseOutput(vector[1], expectedUnique);
            parseOutput(vector[2], expectedDictionary);
            parseOutput(vector[3], expectedNonUnique);
            verify(expectedUnique, changes.interpret(Unique), "unique", changes.entries);
            verify(expectedDictionary, changes.interpret(Dictionary), "dictionary", changes.entries);
            verify(expectedNonUnique, changes.interpret(NonUnique), "non-unique", changes.entries);
        }
    }

    private interface OutputCollection extends Collection<OTransactionIndexEntry> {
        boolean matches(Iterator<OTransactionIndexEntry> actualIterator);
    }

    private static class OutputList extends ArrayList<OTransactionIndexEntry> implements IndexChangesInterpretationTest.OutputCollection {
        @Override
        public boolean matches(Iterator<OTransactionIndexEntry> actualIterator) {
            for (OTransactionIndexEntry expected : this) {
                if (!(actualIterator.hasNext()))
                    return false;

                final OTransactionIndexEntry actual = actualIterator.next();
                if (!(IndexChangesInterpretationTest.entryEquals(expected, actual)))
                    return false;

            }
            return true;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            for (OTransactionIndexEntry entry : this)
                builder.append(IndexChangesInterpretationTest.entryToString(entry)).append(' ');

            if ((builder.length()) > 0)
                builder.setLength(((builder.length()) - 1));

            return builder.toString();
        }
    }

    private static class OutputSet extends ArrayList<OTransactionIndexEntry> implements IndexChangesInterpretationTest.OutputCollection {
        private final int requiredMatches;

        public OutputSet() {
            this((-1));
        }

        public OutputSet(int requiredMatches) {
            this.requiredMatches = requiredMatches;
        }

        @Override
        public boolean matches(Iterator<OTransactionIndexEntry> actualIterator) {
            final int requiredMatches = ((this.requiredMatches) == (-1)) ? this.size() : this.requiredMatches;
            final ArrayList<OTransactionIndexEntry> unmatched = new ArrayList<OTransactionIndexEntry>(this);
            for (int i = 0; i < requiredMatches; ++i) {
                if (!(actualIterator.hasNext()))
                    return false;

                final OTransactionIndexEntry actual = actualIterator.next();
                final int expectedIndex = unmatched.indexOf(actual);
                if (expectedIndex == (-1))
                    return false;

                final OTransactionIndexEntry expected = unmatched.get(expectedIndex);
                if (!(IndexChangesInterpretationTest.entryEquals(expected, actual)))
                    return false;

                unmatched.remove(expectedIndex);
            }
            return true;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append('{');
            if ((requiredMatches) != (-1))
                builder.append(requiredMatches).append(' ');

            for (OTransactionIndexEntry entry : this)
                builder.append(IndexChangesInterpretationTest.entryToString(entry)).append(' ');

            if ((builder.length()) > 1)
                builder.setLength(((builder.length()) - 1));

            builder.append('}');
            return builder.toString();
        }
    }
}

