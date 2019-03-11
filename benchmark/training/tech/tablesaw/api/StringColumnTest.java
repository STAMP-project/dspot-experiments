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
package tech.tablesaw.api;


import ColumnType.STRING;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.tablesaw.TestDataUtil;
import tech.tablesaw.columns.strings.StringColumnFormatter;
import tech.tablesaw.columns.strings.StringPredicates;
import tech.tablesaw.selection.Selection;


public class StringColumnTest {
    private final StringColumn column = StringColumn.create("testing");

    /* TODO: fix
    @Test
    public void testSummarizeIf() {
    double result = column.summarizeIf(
    column.endsWith("3").or(column.endsWith("4")),
    count);
    assertEquals(2, result, 0.0);

    double result2 = column.summarizeIf(column.endsWith("3"), count);
    assertEquals(1, result2, 0.0);
    }
     */
    @Test
    public void testAppendObj2() {
        final StringColumn sc = StringColumn.create("sc", Arrays.asList("a", "b", "c", "a"));
        Assertions.assertArrayEquals(sc.asList().toArray(), sc.asObjectArray());
    }

    @Test
    public void testForNulls() {
        String[] array1 = new String[]{ "1", "2", "3", "4", null };
        Table table1 = Table.create("table1", StringColumn.create("id", array1));
        Assertions.assertEquals("", table1.stringColumn("id").get(4));
        String[] array2 = new String[]{ "1", "2", null, "", "5" };
        Table table2 = Table.create("table2", StringColumn.create("id", array2));
        Assertions.assertEquals("", table2.stringColumn("id").get(3));
    }

    @Test
    public void testAppendObj() {
        StringColumn column = StringColumn.create("testing");
        column.appendObj("Value 1");
        column.appendObj(null);
        column.appendObj("Value 2");
        Assertions.assertEquals(3, column.size());
    }

    @Test
    public void testConditionalSet() {
        column.set(column.isEqualTo("Value 4"), "no Value");
        Assertions.assertTrue(column.contains("no Value"));
        Assertions.assertFalse(column.contains("Value 4"));
    }

    @Test
    public void lag() {
        StringColumn c1 = column.lag(1);
        Table t = Table.create("Test");
        t.addColumns(column, c1);
        Assertions.assertEquals("", c1.get(0));
        Assertions.assertEquals("Value 1", c1.get(1));
        Assertions.assertEquals("Value 2", c1.get(2));
    }

    @Test
    public void lag2() {
        StringColumn c1 = column.lag((-1));
        Table t = Table.create("Test");
        t.addColumns(column, c1);
        Assertions.assertEquals("Value 2", c1.get(0));
        Assertions.assertEquals("Value 3", c1.get(1));
        Assertions.assertEquals("", c1.get(3));
    }

    @Test
    public void lead() {
        StringColumn c1 = column.lead(1);
        Table t = Table.create("Test");
        t.addColumns(column, c1);
        Assertions.assertEquals("Value 2", c1.get(0));
        Assertions.assertEquals("Value 3", c1.get(1));
        Assertions.assertEquals("", c1.get(3));
    }

    @Test
    public void testSelectWhere() {
        StringColumn result = column.where(column.equalsIgnoreCase("VALUE 1"));
        Assertions.assertEquals(1, result.size());
    }

    @Test
    public void testDefaultReturnValue() {
        Assertions.assertEquals((-1), column.firstIndexOf("test"));
    }

    @Test
    public void testType() {
        Assertions.assertEquals(STRING, column.type());
    }

    @Test
    public void testGetString() {
        Assertions.assertEquals("Value 2", column.getString(1));
    }

    @Test
    public void testSize() {
        Assertions.assertEquals(4, column.size());
    }

    @Test
    public void testGetDummies() {
        List<BooleanColumn> dummies = column.getDummies();
        Assertions.assertEquals(4, dummies.size());
    }

    @Test
    public void testToString() {
        Assertions.assertEquals("String column: testing", column.toString());
    }

    @Test
    public void testMax() {
        StringColumn stringColumn = StringColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());
        Assertions.assertEquals("Wyoming", stringColumn.top(5).get(0));
    }

    @Test
    public void testMin() {
        StringColumn stringColumn = StringColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());
        Assertions.assertEquals("Alabama", stringColumn.bottom(5).get(0));
    }

    @Test
    public void testStartsWith() {
        StringColumn stringColumn = StringColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());
        StringColumn selection = stringColumn.where(stringColumn.startsWith("A"));
        Assertions.assertEquals("Alabama", selection.get(0));
        Assertions.assertEquals("Alaska", selection.get(1));
        Assertions.assertEquals("Arizona", selection.get(2));
        Assertions.assertEquals("Arkansas", selection.get(3));
        selection = stringColumn.where(stringColumn.startsWith("T"));
        Assertions.assertEquals("Tennessee", selection.get(0));
        Assertions.assertEquals("Texas", selection.get(1));
    }

    @Test
    public void testFormattedPrinting() {
        StringColumn stringColumn = StringColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());
        Function<String, String> formatter = ( s) -> String.format("[[%s]]", s);
        stringColumn.setPrintFormatter(new StringColumnFormatter(formatter));
        Assertions.assertEquals("[[Alabama]]", stringColumn.getString(0));
    }

    @Test
    public void testSelectWithFilter() {
        StringColumn stringColumn = StringColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());
        StringColumn selection = stringColumn.where(stringColumn.startsWith("A").and(stringColumn.containsString("kan")));
        Assertions.assertEquals(1, selection.size());
        Assertions.assertEquals("Arkansas", selection.getString(0));
    }

    @Test
    public void testIsNotEqualTo() {
        StringColumn stringColumn = StringColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());
        Selection selection = stringColumn.isNotEqualTo("Alabama");
        StringColumn result = stringColumn.where(selection);
        Assertions.assertEquals(result.size(), ((stringColumn.size()) - 1));
        Assertions.assertFalse(result.contains("Alabama"));
        Assertions.assertEquals(stringColumn.size(), 51);
    }

    @Test
    public void testColumnEqualIgnoringCase() {
        StringColumn other = column.copy();
        other.set(1, "Some other thing");
        other.set(2, other.get(2).toUpperCase());
        Assertions.assertFalse(other.contains("Value 3"));
        Assertions.assertTrue(other.contains("Value 1"));
        Assertions.assertFalse(other.contains("Value 2"));
        Assertions.assertTrue(other.contains("Some other thing"));
        Assertions.assertTrue(other.contains("VALUE 3"));
        Assertions.assertTrue(other.contains("Value 4"));
        Assertions.assertEquals(4, other.size());
        StringColumn result = column.where(column.eval(StringPredicates.isEqualToIgnoringCase, other));
        Assertions.assertEquals(3, result.size());
    }

    @Test
    public void testIsEqualTo() {
        StringColumn stringColumn = StringColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());
        stringColumn.append("Alabama");// so we have two entries

        Selection selection = stringColumn.isEqualTo("Alabama");
        StringColumn result = stringColumn.where(selection);
        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains("Alabama"));
        Selection result2 = stringColumn.isEqualTo("Alabama");
        Assertions.assertEquals(2, result2.size());
        stringColumn = stringColumn.where(result2);
        Assertions.assertTrue(stringColumn.contains("Alabama"));
    }

    @Test
    public void testIsNotEqualTo2() {
        StringColumn stringColumn = StringColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());
        Selection selection2 = stringColumn.isNotEqualTo("Yugoslavia");
        Assertions.assertEquals(selection2.size(), 51);
        StringColumn result2 = stringColumn.where(selection2);
        Assertions.assertEquals(result2.size(), stringColumn.size());
    }

    @Test
    public void testIsIn() {
        StringColumn stringColumn = StringColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());
        StringColumn selection = stringColumn.where(stringColumn.isIn("Alabama", "Texas"));
        Assertions.assertEquals("Alabama", selection.get(0));
        Assertions.assertEquals("Texas", selection.get(1));
        Assertions.assertEquals(2, selection.size());
    }

    @Test
    public void testIsNotIn() {
        StringColumn stringColumn = StringColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());
        StringColumn selection = stringColumn.where(stringColumn.isNotIn("Alabama", "Texas"));
        Assertions.assertEquals("Alaska", selection.get(0));
        Assertions.assertEquals("Arizona", selection.get(1));
        Assertions.assertEquals("Arkansas", selection.get(2));
        Assertions.assertEquals(49, selection.size());
    }

    @Test
    public void testToList() {
        StringColumn stringColumn = StringColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());
        List<String> states = stringColumn.asList();
        Assertions.assertEquals(51, states.size());// includes Wash. DC

    }

    @Test
    public void testFormatting() {
        String[] names = new String[]{ "John White", "George Victor" };
        StringColumn nameColumn = StringColumn.create("names", names);
        StringColumn formatted = nameColumn.format("Name: %s");
        Assertions.assertEquals("Name: John White", formatted.get(0));
    }

    @Test
    public void testDistance() {
        String[] words = new String[]{ "canary", "banana", "island", "reggae" };
        String[] words2 = new String[]{ "cancel", "bananas", "islander", "calypso" };
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn word2Column = StringColumn.create("words2", words2);
        DoubleColumn distance = wordColumn.distance(word2Column);
        Assertions.assertEquals(distance.get(0), 3, 1.0E-4);
        Assertions.assertEquals(distance.get(3), 7, 1.0E-4);
    }

    @Test
    public void testCommonSuffix() {
        String[] words = new String[]{ "running", "icecube", "regular", "reggae" };
        String[] words2 = new String[]{ "rowing", "cube", "premium", "place" };
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn word2Column = StringColumn.create("words2", words2);
        StringColumn suffix = wordColumn.commonSuffix(word2Column);
        Assertions.assertEquals(suffix.get(0), "ing");
        Assertions.assertEquals(suffix.get(1), "cube");
        Assertions.assertEquals(suffix.get(3), "e");
    }

    @Test
    public void testCommonPrefix() {
        String[] words = new String[]{ "running", "icecube", "back" };
        String[] words2 = new String[]{ "rowing", "iceland", "backup" };
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn word2Column = StringColumn.create("words2", words2);
        StringColumn result = wordColumn.commonPrefix(word2Column);
        Assertions.assertEquals(result.get(0), "r");
        Assertions.assertEquals(result.get(1), "ice");
        Assertions.assertEquals(result.get(2), "back");
    }

    @Test
    public void testPadStart() {
        String[] words = new String[]{ "running", "icecube", "back" };
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn result = wordColumn.padStart(8, ' ');
        Assertions.assertEquals(result.get(0), " running");
        Assertions.assertEquals(result.get(1), " icecube");
        Assertions.assertEquals(result.get(2), "    back");
    }

    @Test
    public void testPadEnd() {
        String[] words = new String[]{ "running", "icecube", "back" };
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn result = wordColumn.padEnd(8, 'X');
        Assertions.assertEquals(result.get(0), "runningX");
        Assertions.assertEquals(result.get(1), "icecubeX");
        Assertions.assertEquals(result.get(2), "backXXXX");
    }

    @Test
    public void testSubstring() {
        String[] words = new String[]{ "running", "icecube", "back" };
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn result = wordColumn.substring(3);
        Assertions.assertEquals(result.get(0), "ning");
        Assertions.assertEquals(result.get(1), "cube");
        Assertions.assertEquals(result.get(2), "k");
    }

    @Test
    public void testSubstring2() {
        String[] words = new String[]{ "running", "icecube", "back" };
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn result = wordColumn.substring(1, 3);
        Assertions.assertEquals(result.get(0), "un");
        Assertions.assertEquals(result.get(1), "ce");
        Assertions.assertEquals(result.get(2), "ac");
    }

    @Test
    public void testReplaceFirst() {
        String[] words = new String[]{ "running", "run run run" };
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn result = wordColumn.replaceFirst("run", "walk");
        Assertions.assertEquals(result.get(0), "walkning");
        Assertions.assertEquals(result.get(1), "walk run run");
    }

    @Test
    public void testReplaceAll() {
        String[] words = new String[]{ "running", "run run run" };
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn result = wordColumn.replaceAll("run", "walk");
        Assertions.assertEquals(result.get(0), "walkning");
        Assertions.assertEquals(result.get(1), "walk walk walk");
    }

    @Test
    public void testReplaceAll2() {
        String[] words = new String[]{ "running", "run run run" };
        String[] regex = new String[]{ "n", "g" };
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn result = wordColumn.replaceAll(regex, "XX");
        Assertions.assertEquals(result.get(0), "ruXXXXiXXXX");
        Assertions.assertEquals(result.get(1), "ruXX ruXX ruXX");
    }

    @Test
    public void testJoin() {
        String[] words = new String[]{ "running", "run" };
        String[] words2 = new String[]{ "walking", "walk" };
        String[] words3 = new String[]{ "swimming", "swim" };
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn wordColumn2 = StringColumn.create("words2", words2);
        StringColumn wordColumn3 = StringColumn.create("words3", words3);
        StringColumn result = wordColumn.join("--", wordColumn2, wordColumn3);
        Assertions.assertEquals(result.get(0), "running--walking--swimming");
        Assertions.assertEquals(result.get(1), "run--walk--swim");
    }

    @Test
    public void testTrim() {
        String[] words = new String[]{ " running ", " run run run " };
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn result = wordColumn.trim();
        Assertions.assertEquals(result.get(0), "running");
        Assertions.assertEquals(result.get(1), "run run run");
    }

    @Test
    public void testUpperCase() {
        String[] words = new String[]{ "running", "run run run" };
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn result = wordColumn.upperCase();
        Assertions.assertEquals(result.get(0), "RUNNING");
        Assertions.assertEquals(result.get(1), "RUN RUN RUN");
    }

    @Test
    public void testLowerCase() {
        String[] words = new String[]{ "RUNNING", "RUN RUN RUN" };
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn result = wordColumn.lowerCase();
        Assertions.assertEquals(result.get(0), "running");
        Assertions.assertEquals(result.get(1), "run run run");
    }

    @Test
    public void testAbbreviate() {
        String[] words = new String[]{ "running", "Stop Breaking Down", "Backwards Writing" };
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn result = wordColumn.abbreviate(10);
        Assertions.assertEquals(result.get(0), "running");
        Assertions.assertEquals(result.get(1), "Stop Br...");
        Assertions.assertEquals(result.get(2), "Backwar...");
    }

    @Test
    public void tokenizeAndSort() {
        String[] words = new String[]{ "Stop Breaking Down", "Backwards Writing" };
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn result = wordColumn.tokenizeAndSort();
        Assertions.assertEquals(result.get(0), "Breaking Down Stop");
        Assertions.assertEquals(result.get(1), "Backwards Writing");
    }

    @Test
    public void tokenizeAndSort1() {
        String[] words = new String[]{ "Stop,Breaking,Down", "Writing Backwards" };
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn result = wordColumn.tokenizeAndSort(",");
        Assertions.assertEquals(result.get(0), "Breaking,Down,Stop");
        Assertions.assertEquals(result.get(1), "Writing Backwards");
    }

    @Test
    public void tokenizeAndRemoveDuplicates() {
        String[] words = new String[]{ "Stop Breaking Stop Down", "walk run run" };
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn result = wordColumn.tokenizeAndRemoveDuplicates(" ");
        Assertions.assertEquals("Stop Breaking Down", result.get(0));
        Assertions.assertEquals("walk run", result.get(1));
    }

    @Test
    public void chainMaps() {
        String[] words = new String[]{ "Stop Breaking Stop Down", "walk run run" };
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn result = wordColumn.tokenizeAndRemoveDuplicates(" ").tokenizeAndSort();
        Assertions.assertEquals("Breaking Down Stop", result.get(0));
        Assertions.assertEquals("run walk", result.get(1));
    }

    @Test
    public void chainMaps1() {
        String[] words = new String[]{ "foo", "bar" };
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn result = wordColumn.concatenate(" bam");
        Assertions.assertEquals("foo bam", result.get(0));
        Assertions.assertEquals("bar bam", result.get(1));
    }

    @Test
    public void asDoubleColumn() {
        String[] words = new String[]{ "foo", "bar", "larry", "foo", "lion", "ben", "tiger", "bar" };
        StringColumn wordColumn = StringColumn.create("words", words);
        DoubleColumn result = wordColumn.asDoubleColumn();
        Assertions.assertArrayEquals(new double[]{ 0.0, 1.0, 2.0, 0.0, 3.0, 4.0, 5.0, 1.0 }, result.asDoubleArray(), 1.0E-7);
    }

    @Test
    public void asDoubleArray() {
        String[] words = new String[]{ "foo", "bar", "larry", "foo", "lion", null, "ben", "tiger", "bar" };
        StringColumn wordColumn = StringColumn.create("words", words);
        double[] result = wordColumn.asDoubleArray();
        Assertions.assertArrayEquals(new double[]{ 0.0, 1.0, 2.0, 0.0, 3.0, 4.0, 5.0, 6.0, 1.0 }, result, 1.0E-7);
    }

    @Test
    public void getDouble() {
        String[] words = new String[]{ "foo", "bar", "larry", "foo", "lion", null, "ben", "tiger", "bar" };
        StringColumn wordColumn = StringColumn.create("words", words);
        double[] expected = new double[]{ 0.0, 1.0, 2.0, 0.0, 3.0, 4.0, 5.0, 6.0, 1.0 };
        double[] result = new double[words.length];
        for (int i = 0; i < (words.length); i++) {
            result[i] = wordColumn.getDouble(i);
        }
        Assertions.assertArrayEquals(expected, result, 1.0E-7);
    }
}

