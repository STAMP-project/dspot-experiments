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
package tech.tablesaw;


import org.junit.jupiter.api.Test;
import tech.tablesaw.api.Table;


/**
 * Verify sorting functions
 */
public class SortTest {
    private static final int IQ_INDEX = 1;

    private static final int DOB_INDEX = 3;

    // Name,IQ,City,DOB
    private static final String[] columnNames = TestData.SIMPLE_UNSORTED_DATA.getColumnNames();

    private Table unsortedTable;

    @Test
    public void sortAscending() {
        // sort ascending by date and then an integer
        Table sortedTable = unsortedTable.sortAscendingOn("IQ", "DOB");
        Table expectedResults = TestData.SIMPLE_SORTED_DATA_BY_DOUBLE_AND_DATE_ASCENDING.getTable();
        assertTablesEquals(expectedResults, sortedTable);
    }

    /**
     * Same as sortAscending but descending
     */
    @Test
    public void sortDescending() {
        unsortedTable = TestData.SIMPLE_UNSORTED_DATA.getTable();
        Table sortedTable = unsortedTable.sortDescendingOn("IQ", "DOB");
        Table expectedResults = TestData.SIMPLE_SORTED_DATA_BY_DOUBLE_AND_DATE_DESCENDING.getTable();
        assertTablesEquals(expectedResults, sortedTable);
    }

    @Test
    public void testMultipleSortOrdersVerifyMinus() {
        Table sortedTable = unsortedTable.sortOn(("-" + (SortTest.columnNames[SortTest.IQ_INDEX])), ("-" + (SortTest.columnNames[SortTest.DOB_INDEX])));
        Table expectedResults = TestData.SIMPLE_SORTED_DATA_BY_DOUBLE_AND_DATE_DESCENDING.getTable();
        assertTablesEquals(expectedResults, sortedTable);
    }

    @Test
    public void testAscendingAndDescending() {
        Table sortedTable = unsortedTable.sortOn(("+" + (SortTest.columnNames[SortTest.IQ_INDEX])), ("-" + (SortTest.columnNames[SortTest.DOB_INDEX])));
        Table expectedResults = TestData.SIMPLE_SORTED_DATA_BY_DOUBLE_ASCENDING_AND_THEN_DATE_DESCENDING.getTable();
        assertTablesEquals(expectedResults, sortedTable);
    }

    @Test
    public void testMultipleSortOrdersVerifyPlus() {
        Table sortedTable = unsortedTable.sortOn(("+" + (SortTest.columnNames[SortTest.IQ_INDEX])), ("+" + (SortTest.columnNames[SortTest.DOB_INDEX])));
        Table expectedResults = TestData.SIMPLE_SORTED_DATA_BY_DOUBLE_AND_DATE_ASCENDING.getTable();
        assertTablesEquals(expectedResults, sortedTable);
        sortedTable = unsortedTable.sortOn(SortTest.columnNames[SortTest.IQ_INDEX], SortTest.columnNames[SortTest.DOB_INDEX]);
        expectedResults = TestData.SIMPLE_SORTED_DATA_BY_DOUBLE_AND_DATE_ASCENDING.getTable();
        assertTablesEquals(expectedResults, sortedTable);
    }

    @Test
    public void testAscendingWithPlusSign() {
        Table sortedTable = unsortedTable.sortOn(("+" + (SortTest.columnNames[SortTest.IQ_INDEX])));
        Table expectedResults = TestData.SIMPLE_SORTED_DATA_BY_DOUBLE_AND_DATE_ASCENDING.getTable();
        assertTablesEquals(expectedResults, sortedTable);
    }

    @Test
    public void testSortOnIndices() {
        Table sortedTable = unsortedTable.sortOn(SortTest.IQ_INDEX, SortTest.DOB_INDEX);
        Table expectedResults = TestData.SIMPLE_SORTED_DATA_BY_DOUBLE_AND_DATE_ASCENDING.getTable();
        assertTablesEquals(expectedResults, sortedTable);
    }

    @Test
    public void testSortOnIndicesAscendingAndDescending() {
        Table sortedTable = unsortedTable.sortOn(SortTest.IQ_INDEX, (-(SortTest.DOB_INDEX)));
        Table expectedResults = TestData.SIMPLE_SORTED_DATA_BY_DOUBLE_ASCENDING_AND_THEN_DATE_DESCENDING.getTable();
        assertTablesEquals(expectedResults, sortedTable);
    }
}

