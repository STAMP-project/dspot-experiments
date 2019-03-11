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


import NumberPredicates.isEqualTo;
import NumberPredicates.isGreaterThan;
import NumberPredicates.isGreaterThanOrEqualTo;
import NumberPredicates.isLessThan;
import NumberPredicates.isLessThanOrEqualTo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;


/**
 *
 */
public class DoubleIndexTest {
    private DoubleIndex index;

    private Table table;

    @Test
    public void testGet() {
        Selection fromCol = table.numberColumn("stop_lat").eval(isEqualTo, 30.330425);
        Selection fromIdx = index.get(30.330425);
        Assertions.assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testGTE() {
        Selection fromCol = table.numberColumn("stop_lat").eval(isGreaterThanOrEqualTo, 30.330425);
        Selection fromIdx = index.atLeast(30.330425);
        Assertions.assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testLTE() {
        Selection fromCol = table.numberColumn("stop_lat").eval(isLessThanOrEqualTo, 30.330425);
        Selection fromIdx = index.atMost(30.330425);
        Assertions.assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testLT() {
        Selection fromCol = table.numberColumn("stop_lat").eval(isLessThan, 30.330425);
        Selection fromIdx = index.lessThan(30.330425);
        Assertions.assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testGT() {
        Selection fromCol = table.numberColumn("stop_lat").eval(isGreaterThan, 30.330425);
        Selection fromIdx = index.greaterThan(30.330425);
        Assertions.assertEquals(fromCol, fromIdx);
    }
}

