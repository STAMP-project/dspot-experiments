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
package tech.tablesaw.io.json;


import ColumnType.LONG;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.Table;


public class JsonReaderTest {
    @Test
    public void arrayOfArraysWithHeader() throws IOException {
        String json = "[[\"Date\",\"Value\"],[1453438800000,-2.1448117025014],[1454043600000,-2.9763153817574],[1454648400000,-2.9545283436391]]";
        Table table = Table.read().json(new java.io.StringReader(json), "jsonTable");
        Assertions.assertEquals(2, table.columnCount());
        Assertions.assertEquals(3, table.rowCount());
        Assertions.assertEquals("Date", table.column(0).name());
        Assertions.assertEquals("Value", table.column(1).name());
        Assertions.assertEquals(LONG, table.columnTypes()[0]);
    }

    @Test
    public void arrayOfArraysNoHeader() throws IOException {
        String json = "[[1453438800000,-2.1448117025014],[1454043600000,-2.9763153817574],[1454648400000,-2.9545283436391]]";
        Table table = Table.read().json(new java.io.StringReader(json), "jsonTable");
        Assertions.assertEquals(2, table.columnCount());
        Assertions.assertEquals(3, table.rowCount());
        Assertions.assertEquals(LONG, table.columnTypes()[0]);
    }

    @Test
    public void arrayOfNestedObjects() throws IOException {
        String json = "[{\"a\":1453438800000,\"b\":{\"c\":-2.1448117025014}},{\"a\":1454043600000,\"b\":{\"c\":-2.9763153817574}},{\"a\":1454648400000,\"b\":{\"c\":-2.9545283436391}}]";
        Table table = Table.read().json(new java.io.StringReader(json), "jsonTable");
        Assertions.assertEquals(2, table.columnCount());
        Assertions.assertEquals(3, table.rowCount());
        Assertions.assertEquals("a", table.column(0).name());
        Assertions.assertEquals("b.c", table.column(1).name());
        Assertions.assertEquals(LONG, table.columnTypes()[0]);
    }
}

