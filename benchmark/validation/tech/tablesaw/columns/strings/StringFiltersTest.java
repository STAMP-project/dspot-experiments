/**
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
package tech.tablesaw.columns.strings;


import com.google.common.collect.Lists;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;


public class StringFiltersTest {
    private StringColumn sc1 = StringColumn.create("sc1");

    private StringColumn sc2 = StringColumn.create("sc2");

    private Table table = Table.create("T");

    @Test
    public void testLength() {
        Assertions.assertEquals(5, sc1.length().get(0), 1.0E-6);
    }

    @Test
    public void testUniqueTokens() {
        String[] values = new String[]{ "a", "a b", "c d 3", "b 4" };
        StringColumn column1 = StringColumn.create("1", values);
        StringColumn tokens = column1.tokens(" ");
        Assertions.assertEquals(8, tokens.size(), 1.0E-4);
        StringColumn uniqueTokens = column1.uniqueTokens(" ");
        Assertions.assertEquals(6, uniqueTokens.size(), 1.0E-6);
    }

    @Test
    public void testCountOccurrences() {
        String[] values = new String[]{ "a", "a b", "c d 3", "b 4", "a" };
        StringColumn column1 = StringColumn.create("1", values);
        Assertions.assertEquals(0, column1.countOccurrences("v"), 1.0E-6);
        Assertions.assertEquals(1, column1.countOccurrences("b 4"), 1.0E-6);
        Assertions.assertEquals(2, column1.countOccurrences("a"), 1.0E-6);
    }

    @Test
    public void testEqualsIgnoreCase() {
        Assertions.assertTrue(sc1.equalsIgnoreCase("APPLE").contains(0));
        Assertions.assertTrue(sc1.equalsIgnoreCase(sc2).contains(0));
    }

    @Test
    public void testStartsWith() {
        // test column filtering
        Assertions.assertTrue(sc1.startsWith("dog").contains(9));
        Assertions.assertTrue(sc1.startsWith("dog").contains(10));
    }

    @Test
    public void testEndsWith() {
        Assertions.assertTrue(sc1.endsWith("dog").contains(9));
        Assertions.assertFalse(sc1.endsWith("dog").contains(10));
    }

    @Test
    public void testContainsString() {
        Assertions.assertTrue(sc1.containsString("eph").contains(4));
        Assertions.assertFalse(sc1.containsString("eph").contains(10));
    }

    @Test
    public void testMatchesRegex() {
        Assertions.assertTrue(sc1.matchesRegex("^apple").contains(0));
        Assertions.assertFalse(sc1.matchesRegex("^apple").contains(7));
        Assertions.assertFalse(sc1.matchesRegex("^apple").contains(10));
        Assertions.assertFalse(sc1.matchesRegex("^apple").contains(14));
    }

    @Test
    public void testIsAlpha() {
        Assertions.assertTrue(sc1.isAlpha().contains(4));
        Assertions.assertFalse(sc1.isAlpha().contains(11));
        Assertions.assertFalse(sc1.isAlpha().contains(13));
    }

    @Test
    public void testIsNumeric() {
        Assertions.assertFalse(sc1.isNumeric().contains(4));
        Assertions.assertTrue(sc1.isNumeric().contains(11));
        Assertions.assertFalse(sc1.isNumeric().contains(13));
    }

    @Test
    public void testIsAlphaNumeric() {
        Assertions.assertTrue(sc1.isAlphaNumeric().contains(4));
        Assertions.assertTrue(sc1.isAlphaNumeric().contains(11));
        Assertions.assertFalse(sc1.isAlphaNumeric().contains(13));
        Assertions.assertFalse(sc1.isAlphaNumeric().contains(15));
        Assertions.assertTrue(sc1.isAlphaNumeric().contains(16));
    }

    @Test
    public void testIsUpperCase() {
        Assertions.assertFalse(sc1.isUpperCase().contains(4));
        Assertions.assertFalse(sc1.isUpperCase().contains(13));
        Assertions.assertTrue(sc1.isUpperCase().contains(17));
    }

    @Test
    public void testIsLowerCase() {
        Assertions.assertTrue(sc1.isLowerCase().contains(4));
        Assertions.assertFalse(sc1.isLowerCase().contains(17));
    }

    @Test
    public void testLengthEquals() {
        Assertions.assertTrue(sc1.lengthEquals(5).contains(0));
        Assertions.assertFalse(sc1.lengthEquals(5).contains(8));
    }

    @Test
    public void testIsShorterThan() {
        Assertions.assertTrue(sc1.isShorterThan(5).contains(6));
        Assertions.assertFalse(sc1.isShorterThan(5).contains(0));
    }

    @Test
    public void testIsLongerThan() {
        Assertions.assertTrue(sc1.isLongerThan(5).contains(1));
        Assertions.assertFalse(sc1.isLongerThan(5).contains(0));
    }

    @Test
    public void testIsIn() {
        List<String> candidates = Lists.newArrayList("diamond", "dog", "canary");
        Assertions.assertTrue(sc1.isIn("diamond", "dog", "canary").contains(3));
        Assertions.assertFalse(sc1.isIn("diamond", "dog", "canary").contains(8));
        Assertions.assertTrue(sc1.isIn("diamond", "dog", "canary").contains(9));
        Assertions.assertTrue(sc1.isIn(candidates).contains(3));
        Assertions.assertFalse(sc1.isIn(candidates).contains(8));
        Assertions.assertTrue(sc1.isIn(candidates).contains(9));
    }

    @Test
    public void testIsNotIn() {
        List<String> candidates = Lists.newArrayList("diamond", "dog", "canary");
        Assertions.assertFalse(sc1.isNotIn("diamond", "dog", "canary").contains(3));
        Assertions.assertTrue(sc1.isNotIn("diamond", "dog", "canary").contains(8));
        Assertions.assertFalse(sc1.isNotIn("diamond", "dog", "canary").contains(9));
        Assertions.assertFalse(sc1.isNotIn(candidates).contains(3));
        Assertions.assertTrue(sc1.isNotIn(candidates).contains(8));
        Assertions.assertFalse(sc1.isNotIn(candidates).contains(9));
    }

    @Test
    public void testIsMissing() {
        Assertions.assertFalse(sc1.isMissing().contains(3));
        Assertions.assertTrue(sc1.isMissing().contains(18));
    }

    @Test
    public void testIsEmptyString() {
        Assertions.assertFalse(sc1.isEmptyString().contains(3));
        Assertions.assertTrue(sc1.isEmptyString().contains(18));
    }

    @Test
    public void testIsNotMissing() {
        Assertions.assertTrue(sc1.isNotMissing().contains(3));
        Assertions.assertFalse(sc1.isNotMissing().contains(18));
    }

    @Test
    public void testIsEqualTo() {
        Assertions.assertTrue(sc1.isEqualTo("10").contains(12));
        Assertions.assertFalse(sc1.isEqualTo("10").contains(13));
        Assertions.assertTrue(sc1.isEqualTo(sc2).contains(9));
        Assertions.assertFalse(sc1.isEqualTo(sc2).contains(0));
    }

    @Test
    public void testIsNotEqualTo() {
        Assertions.assertFalse(sc1.isNotEqualTo("10").contains(12));
        Assertions.assertTrue(sc1.isNotEqualTo("10").contains(13));
        Assertions.assertFalse(sc1.isNotEqualTo(sc2).contains(9));
        Assertions.assertTrue(sc1.isNotEqualTo(sc2).contains(0));
    }

    @Test
    public void testCountWords() {
        final String[] words1 = new String[]{ "one", "two words" };
        final StringColumn stringColumn1 = StringColumn.create("words", words1);
        DoubleColumn nc = stringColumn1.countTokens(" ");
        Assertions.assertEquals(3, nc.sum(), 1.0E-5);
    }
}

