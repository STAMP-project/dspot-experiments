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
package tech.tablesaw.columns.dates;


import java.time.LocalTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.TimeColumn;


public class DateMapFunctionsTest {
    private DateColumn column1;

    @Test
    public void testAtTimeColumn() {
        column1.appendCell("2013-10-23");
        column1.appendCell("12/24/1924");
        column1.appendCell("12-May-2015");
        column1.appendCell("14-Jan-2015");
        TimeColumn timeColumn = TimeColumn.create("times");
        timeColumn.append(LocalTime.NOON);
        timeColumn.append(LocalTime.NOON);
        timeColumn.append(LocalTime.NOON);
        timeColumn.append(LocalTime.NOON);
        DateTimeColumn dateTimes = column1.atTime(timeColumn);
        Assertions.assertNotNull(dateTimes);
        Assertions.assertTrue(dateTimes.get(0).toLocalTime().equals(LocalTime.NOON));
    }

    @Test
    public void testAtTime() {
        column1.appendCell("2013-10-23");
        column1.appendCell("12/24/1924");
        column1.appendCell("12-May-2015");
        column1.appendCell("14-Jan-2015");
        DateTimeColumn dateTimes = column1.atTime(LocalTime.NOON);
        Assertions.assertNotNull(dateTimes);
        Assertions.assertTrue(dateTimes.get(0).toLocalTime().equals(LocalTime.NOON));
    }
}

