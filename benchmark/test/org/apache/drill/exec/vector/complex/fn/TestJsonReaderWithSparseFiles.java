/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.vector.complex.fn;


import org.apache.drill.exec.record.RecordBatchLoader;
import org.apache.drill.exec.util.JsonStringArrayList;
import org.apache.drill.exec.util.JsonStringHashMap;
import org.apache.drill.exec.vector.ValueVector;
import org.apache.drill.test.BaseTestQuery;
import org.junit.Assert;
import org.junit.Test;


public class TestJsonReaderWithSparseFiles extends BaseTestQuery {
    private interface Function<T> {
        void apply(T param);
    }

    private static class TypeConverter {
        public Object convert(Object obj) {
            if ((obj instanceof JsonStringArrayList) || (obj instanceof JsonStringHashMap)) {
                return obj.toString();
            }
            return obj;
        }
    }

    private static class Verifier implements TestJsonReaderWithSparseFiles.Function<RecordBatchLoader> {
        private final int count;

        private final Object[][] values;

        private final TestJsonReaderWithSparseFiles.TypeConverter converter = new TestJsonReaderWithSparseFiles.TypeConverter();

        protected Verifier(int count, Object[][] values) {
            this.count = count;
            this.values = values;
        }

        @Override
        public void apply(RecordBatchLoader loader) {
            Assert.assertEquals("invalid record count returned", count, loader.getRecordCount());
            for (int r = 0; r < (values.length); r++) {
                final Object[] row = values[r];
                for (int c = 0; c < (values[r].length); c++) {
                    final Object expected = row[c];
                    final Object unconverted = loader.getValueAccessorById(ValueVector.class, c).getValueVector().getAccessor().getObject(r);
                    final Object actual = converter.convert(unconverted);
                    Assert.assertEquals(String.format("row:%d - col:%d - expected:%s[%s] - actual:%s[%s]", r, c, expected, (expected == null ? "null" : expected.getClass().getSimpleName()), actual, (actual == null ? "null" : actual.getClass().getSimpleName())), actual, expected);
                }
            }
        }
    }

    @Test
    public void testIfDrillCanReadSparseRecords() throws Exception {
        final String sql = "select * from cp.`vector/complex/fn/sparse.json`";
        // XXX: make sure value order matches vector order
        final Object[][] values = new Object[][]{ new Object[]{ null, null }, new Object[]{ 1L, null }, new Object[]{ null, 2L }, new Object[]{ 3L, 3L } };
        query(sql, new TestJsonReaderWithSparseFiles.Verifier(4, values));
    }

    @Test
    public void testIfDrillCanReadSparseNestedRecordsWithoutRaisingException() throws Exception {
        final String sql = "select * from cp.`vector/complex/fn/nested-with-nulls.json`";
        // XXX: make sure value order matches vector order
        final Object[][] values = new Object[][]{ new Object[]{ "[{},{},{},{\"name\":\"doe\"},{}]" }, new Object[]{ "[]" }, new Object[]{ "[{\"name\":\"john\",\"id\":10}]" }, new Object[]{ "[{},{}]" } };
        query(sql, new TestJsonReaderWithSparseFiles.Verifier(4, values));
    }

    @Test
    public void testIfDrillCanQuerySingleRecordWithEmpties() throws Exception {
        final String sql = "select * from cp.`vector/complex/fn/single-record-with-empties.json`";
        // XXX: make sure value order matches vector order
        final Object[][] values = new Object[][]{ new Object[]{ "[{},{}]" } };
        query(sql, new TestJsonReaderWithSparseFiles.Verifier(1, values));
    }
}

