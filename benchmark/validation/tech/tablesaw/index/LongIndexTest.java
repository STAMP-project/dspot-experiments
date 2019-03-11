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
package tech.tablesaw.index;


import DateTimePredicates.isEqualTo;
import DateTimePredicates.isGreaterThan;
import DateTimePredicates.isGreaterThanOrEqualTo;
import DateTimePredicates.isLessThan;
import DateTimePredicates.isLessThanOrEqualTo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;


/**
 *
 */
public class LongIndexTest {
    private LongIndex index;

    private Table table;

    @Test
    public void testGet() {
        Selection fromCol = table.dateTimeColumn("Midnights").eval(isEqualTo, 71);
        Selection fromIdx = index.get(71);
        Assertions.assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testGTE() {
        Selection fromCol = table.dateTimeColumn("Midnights").eval(isGreaterThanOrEqualTo, 71);
        Selection fromIdx = index.atLeast(71);
        Assertions.assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testLTE() {
        Selection fromCol = table.dateTimeColumn("Midnights").eval(isLessThanOrEqualTo, 71);
        Selection fromIdx = index.atMost(71);
        Assertions.assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testLT() {
        Selection fromCol = table.dateTimeColumn("Midnights").eval(isLessThan, 71);
        Selection fromIdx = index.lessThan(71);
        Assertions.assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testGT() {
        Selection fromCol = table.dateTimeColumn("Midnights").eval(isGreaterThan, 71);
        Selection fromIdx = index.greaterThan(71);
        Assertions.assertEquals(fromCol, fromIdx);
    }
}

