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


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.tablesaw.columns.booleans.BooleanFormatter;


/**
 * Tests for BooleanColumn
 */
public class BooleanColumnTest {
    private static final String LINE_END = System.lineSeparator();

    private final BooleanColumn column = BooleanColumn.create("Test");

    @Test
    public void testAny() {
        Assertions.assertTrue(column.any());
    }

    @Test
    public void testAll() {
        Assertions.assertFalse(column.all());
        BooleanColumn filtered = column.where(column.isTrue());
        Assertions.assertTrue(filtered.all());
    }

    @Test
    public void inRange() {
        Assertions.assertFalse(column.all());
        BooleanColumn filtered = column.inRange(0, 2);
        Assertions.assertEquals(2, filtered.size());
    }

    @Test
    public void testNone() {
        Assertions.assertFalse(column.none());
        BooleanColumn filtered = column.where(column.isFalse());
        Assertions.assertTrue(filtered.none());
    }

    @Test
    public void testSet() {
        Assertions.assertFalse(column.none());
        column.set(column.isTrue(), false);
        Assertions.assertTrue(column.none());
    }

    @Test
    public void testGetDouble() {
        Assertions.assertEquals(1, column.getDouble(4), 0.0);
        Assertions.assertEquals(0, column.getDouble(0), 0.0);
    }

    @Test
    public void testAppendColumn() {
        BooleanColumn column1 = column.copy();
        column1.append(column);
        Assertions.assertEquals((2 * (column.size())), column1.size());
    }

    @Test
    public void testPrinting() {
        column.appendCell("");
        column.setPrintFormatter(new BooleanFormatter("Yes", "No", "IDK"));
        Assertions.assertEquals("No", column.getString(0));
        Assertions.assertEquals("Yes", column.getString(5));
        Assertions.assertEquals("IDK", column.getString(((column.size()) - 1)));
    }

    @Test
    public void testGetElements() {
        Assertions.assertEquals(7, column.size());
    }

    @Test
    public void testCounts() {
        Assertions.assertEquals(7, column.size());
        Assertions.assertEquals(7, ((column.countTrue()) + (column.countFalse())));
        Assertions.assertEquals(2, column.countTrue());
    }

    @Test
    public void testAddCell() {
        column.append(true);
        Assertions.assertEquals(8, column.size());
        // Add some other types and ensure that they're correctly truthy
        column.appendCell("true");
        Assertions.assertTrue(lastEntry());
        column.appendCell("false");
        Assertions.assertFalse(lastEntry());
        column.appendCell("TRUE");
        Assertions.assertTrue(lastEntry());
        column.appendCell("FALSE");
        Assertions.assertFalse(lastEntry());
        column.appendCell("T");
        Assertions.assertTrue(lastEntry());
        column.appendCell("F");
        Assertions.assertFalse(lastEntry());
        column.appendCell("Y");
        Assertions.assertTrue(lastEntry());
        column.appendCell("N");
        Assertions.assertFalse(lastEntry());
        column.appendCell("");
        Assertions.assertNull(column.get(((column.size()) - 1)));
    }

    @Test
    public void testGetType() {
        Assertions.assertEquals("Boolean".toUpperCase(), column.type().name());
    }

    @Test
    public void testToString() {
        Assertions.assertEquals(("Boolean column: " + (column.name())), column.toString());
    }

    @Test
    public void testPrint() {
        Assertions.assertEquals(((((((((((((((("Column: Test" + (BooleanColumnTest.LINE_END)) + "false") + (BooleanColumnTest.LINE_END)) + "false") + (BooleanColumnTest.LINE_END)) + "false") + (BooleanColumnTest.LINE_END)) + "false") + (BooleanColumnTest.LINE_END)) + "true") + (BooleanColumnTest.LINE_END)) + "true") + (BooleanColumnTest.LINE_END)) + "false") + (BooleanColumnTest.LINE_END)), column.print());
    }

    @Test
    public void testSummary() {
        Table summary = column.summary();
        Assertions.assertEquals(2, summary.columnCount());
        Assertions.assertEquals(2, summary.rowCount());
        Assertions.assertEquals("false", summary.getUnformatted(0, 0));
        Assertions.assertEquals("5.0", summary.getUnformatted(0, 1));
        Assertions.assertEquals("true", summary.getUnformatted(1, 0));
        Assertions.assertEquals("2.0", summary.getUnformatted(1, 1));
    }

    @Test
    public void testCountUnique() {
        int result = column.countUnique();
        Assertions.assertEquals(2, result);
    }

    @Test
    public void testToDoubleArray() {
        double[] result = column.asDoubleArray();
        Assertions.assertEquals(0.0, result[0], 0.01);
        Assertions.assertEquals(0.0, result[1], 0.01);
        Assertions.assertEquals(0.0, result[2], 0.01);
        Assertions.assertEquals(0.0, result[3], 0.01);
        Assertions.assertEquals(1.0, result[4], 0.01);
        Assertions.assertEquals(1.0, result[5], 0.01);
        Assertions.assertEquals(0.0, result[6], 0.01);
    }

    /**
     * Tests construction from a bitmap. The test uses the isFalse() method, which inverts the values in the column it's
     * invoked on, so the true false counts are the opposite of those in the original
     */
    @Test
    public void testBitmapConstructor() {
        BooleanColumn bc = BooleanColumn.create("Is false", column.isFalse(), column.size());
        Table summary = bc.summary();
        Assertions.assertEquals(2, summary.columnCount());
        Assertions.assertEquals(2, summary.rowCount());
        Assertions.assertEquals("false", summary.getUnformatted(0, 0));
        Assertions.assertEquals("2.0", summary.getUnformatted(0, 1));
        Assertions.assertEquals("true", summary.getUnformatted(1, 0));
        Assertions.assertEquals("5.0", summary.getUnformatted(1, 1));
    }

    @Test
    public void testSelectionMethods() {
        Assertions.assertEquals(5, column.isFalse().size());
        Assertions.assertEquals(2, column.isTrue().size());
        Assertions.assertEquals(7, column.isNotMissing().size());
        Assertions.assertEquals(0, column.isMissing().size());
    }
}

