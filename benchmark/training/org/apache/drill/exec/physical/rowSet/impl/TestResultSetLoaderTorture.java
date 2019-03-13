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
package org.apache.drill.exec.physical.rowSet.impl;


import java.util.Arrays;
import org.apache.drill.categories.RowSetTests;
import org.apache.drill.exec.physical.rowSet.RowSetLoader;
import org.apache.drill.exec.vector.accessor.ArrayReader;
import org.apache.drill.exec.vector.accessor.ArrayWriter;
import org.apache.drill.exec.vector.accessor.ScalarReader;
import org.apache.drill.exec.vector.accessor.ScalarWriter;
import org.apache.drill.exec.vector.accessor.TupleReader;
import org.apache.drill.exec.vector.accessor.TupleWriter;
import org.apache.drill.shaded.guava.com.google.common.base.Charsets;
import org.apache.drill.test.LogFixture;
import org.apache.drill.test.SubOperatorTest;
import org.apache.drill.test.rowSet.RowSetReader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Runs a worst-case scenario test that combines aspects of all
 * previous tests. Run this test only <i>after</i> all other tests
 * pass. Combined conditions tested:
 * <ul>
 * <li>Nested maps and map arrays.</li>
 * <li>Nullable VarChar (which has an offset vector and null-bit vector
 * be kept in sync.)
 * <li>Repeated Varchar (which requires to offset vectors be kept in
 * sync.)</li>
 * <li>Null values.</li>
 * <li>Omitted values.</li>
 * <li>Skipped rows.</li>
 * <li>Vector overflow deep in the structure.</li>
 * <li>Multiple batches.</li>
 * </ul>
 * The proposition that this test asserts is that if this test passes,
 * then most clients will also work as they generally do not do all these
 * things in a single query.
 */
@Category(RowSetTests.class)
public class TestResultSetLoaderTorture extends SubOperatorTest {
    private static final Logger logger = LoggerFactory.getLogger(TestResultSetLoaderTorture.class);

    private static class TestSetup {
        int n1Cycle = 5;

        int n2Cycle = 7;

        int s2Cycle = 11;

        int m2Cycle = 13;

        int n3Cycle = 17;

        int s3Cycle = 19;

        int skipCycle = 23;

        int nullCycle = 3;

        int m2Count = 9;

        int s3Count = 29;

        String s3Value;

        public TestSetup() {
            byte[] s3Bytes = new byte[512];
            Arrays.fill(s3Bytes, ((byte) ('X')));
            s3Value = new String(s3Bytes, Charsets.UTF_8);
        }
    }

    // Write rows, skipping every 10th.
    // n0 is the row id, so appears in every row.
    // For n1, n2 and n3 and s2, omit selected values and makes others null.
    // For s3, write values large enough to cause overflow; but skip some
    // values and write 0 values for others.
    private static class BatchWriter {
        TestResultSetLoaderTorture.TestSetup setup;

        RowSetLoader rootWriter;

        ScalarWriter n1Writer;

        ArrayWriter a2Writer;

        ScalarWriter n2Writer;

        ScalarWriter s2Writer;

        ScalarWriter n3Writer;

        ScalarWriter s3Writer;

        int rowId = 0;

        int innerCount = 0;

        int writeRowCount = 0;

        int startPrint = -1;

        int endPrint = -1;

        boolean lastRowDiscarded;

        public BatchWriter(TestResultSetLoaderTorture.TestSetup setup, RowSetLoader rootWriter) {
            this.setup = setup;
            this.rootWriter = rootWriter;
            TupleWriter m1Writer = rootWriter.tuple("m1");
            n1Writer = m1Writer.scalar("n1");
            a2Writer = m1Writer.array("m2");
            TupleWriter m2Writer = a2Writer.tuple();
            n2Writer = m2Writer.scalar("n2");
            s2Writer = m2Writer.scalar("s2");
            TupleWriter m3Writer = m2Writer.tuple("m3");
            n3Writer = m3Writer.scalar("n3");
            s3Writer = m3Writer.array("s3").scalar();
        }

        public void writeBatch() {
            // Write until overflow
            writeRowCount = rootWriter.rowCount();
            while (!(rootWriter.isFull())) {
                lastRowDiscarded = false;
                writeRow();
                (rowId)++;
            } 
        }

        private void writeRow() {
            rootWriter.start();
            // Outer column
            rootWriter.scalar("n0").setInt(rowId);
            print("n0", rowId);
            // Map 1: non-array
            setInt("n1", n1Writer, rowId, setup.n1Cycle);
            // Map2: an array.
            if (((rowId) % (setup.m2Cycle)) != 0) {
                writeM2Array();
            }
            // Skip some rows
            if (((rowId) % (setup.skipCycle)) != 0) {
                rootWriter.save();
                (writeRowCount)++;
            } else {
                lastRowDiscarded = true;
            }
        }

        private void writeM2Array() {
            for (int i = 0; i < (setup.m2Count); i++) {
                // n2: usual int
                setInt(("n2." + i), n2Writer, innerCount, setup.n2Cycle);
                // S2: a nullable Varchar
                if (((innerCount) % (setup.s2Cycle)) == 0) {
                    // Skip
                } else
                    if ((((innerCount) % (setup.s2Cycle)) % (setup.nullCycle)) == 0) {
                        s2Writer.setNull();
                        print(("s2." + i), null);
                    } else {
                        s2Writer.setString(("s2-" + (innerCount)));
                        print(("s2." + i), ("s2-" + (innerCount)));
                    }

                // Map3: a non-repeated map
                // n2: usual int
                setInt(("n3." + i), n3Writer, innerCount, setup.n3Cycle);
                // s3: a repeated VarChar
                if (((innerCount) % (setup.s3Cycle)) != 0) {
                    for (int j = 0; j < (setup.s3Count); j++) {
                        s3Writer.setString(((setup.s3Value) + (((innerCount) * (setup.s3Count)) + j)));
                    }
                    print(("s3." + i), ((setup.s3Count) + "x"));
                }
                (innerCount)++;
                a2Writer.save();
            }
        }

        public void setInt(String label, ScalarWriter writer, int id, int cycle) {
            int cycleIndex = id % cycle;
            if (cycleIndex == 0) {
                // Skip
            } else
                if ((cycleIndex % (setup.nullCycle)) == 0) {
                    writer.setNull();
                    print(label, null);
                } else {
                    writer.setInt((id * cycle));
                    print(label, (id * cycle));
                }

        }

        public void print(String label, Object value) {
            if (((rowId) >= (startPrint)) && ((rowId) <= (endPrint))) {
                TestResultSetLoaderTorture.logger.info("{} = {}", label, value);
            }
        }

        public int rowCount() {
            return (writeRowCount) - (lastRowDiscarded ? 0 : 1);
        }
    }

    public static class ReadState {
        int rowId = 0;

        int innerCount = 0;
    }

    private static class BatchReader {
        private TestResultSetLoaderTorture.TestSetup setup;

        private RowSetReader rootReader;

        ScalarReader n1Reader;

        ArrayReader a2Reader;

        ScalarReader n2Reader;

        ScalarReader s2Reader;

        ScalarReader n3Reader;

        ArrayReader s3Array;

        ScalarReader s3Reader;

        TestResultSetLoaderTorture.ReadState readState;

        public BatchReader(TestResultSetLoaderTorture.TestSetup setup, RowSetReader reader, TestResultSetLoaderTorture.ReadState readState) {
            this.setup = setup;
            this.rootReader = reader;
            this.readState = readState;
            TupleReader m1Reader = tuple("m1");
            n1Reader = m1Reader.scalar("n1");
            a2Reader = m1Reader.array("m2");
            TupleReader m2Reader = a2Reader.tuple();
            n2Reader = m2Reader.scalar("n2");
            s2Reader = m2Reader.scalar("s2");
            TupleReader m3Reader = m2Reader.tuple("m3");
            n3Reader = m3Reader.scalar("n3");
            s3Array = m3Reader.array("s3");
            s3Reader = s3Array.scalar();
        }

        public void verify() {
            while (rootReader.next()) {
                verifyRow();
                (readState.rowId)++;
            } 
        }

        private void verifyRow() {
            // Skipped original row? Bump the row id.
            if (((readState.rowId) % (setup.skipCycle)) == 0) {
                if (((readState.rowId) % (setup.m2Cycle)) != 0) {
                    readState.innerCount += setup.m2Count;
                }
                (readState.rowId)++;
            }
            // Outer column
            Assert.assertEquals(readState.rowId, scalar("n0").getInt());
            // Map 1: non-array
            checkInt(n1Reader, readState.rowId, setup.n1Cycle);
            // Map2: an array.
            if (((readState.rowId) % (setup.m2Cycle)) == 0) {
                Assert.assertEquals(0, a2Reader.size());
            } else {
                verifyM2Array();
            }
        }

        private void verifyM2Array() {
            for (int i = 0; i < (setup.m2Count); i++) {
                assert a2Reader.next();
                // n2: usual int
                checkInt(n2Reader, readState.innerCount, setup.n2Cycle);
                if (((readState.innerCount) % (setup.s2Cycle)) == 0) {
                    // Skipped values should be null
                    Assert.assertTrue(String.format("Row %d, entry %d", rootReader.offset(), i), s2Reader.isNull());
                } else
                    if ((((readState.innerCount) % (setup.s2Cycle)) % (setup.nullCycle)) == 0) {
                        Assert.assertTrue(s2Reader.isNull());
                    } else {
                        Assert.assertEquals(("s2-" + (readState.innerCount)), s2Reader.getString());
                    }

                // Map3: a non-repeated map
                // n2: usual int
                checkInt(n3Reader, readState.innerCount, setup.n3Cycle);
                // s3: a repeated VarChar
                if (((readState.innerCount) % (setup.s3Cycle)) == 0) {
                    Assert.assertEquals(0, s3Array.size());
                } else {
                    for (int j = 0; j < (setup.s3Count); j++) {
                        Assert.assertTrue(s3Array.next());
                        Assert.assertEquals(((setup.s3Value) + (((readState.innerCount) * (setup.s3Count)) + j)), s3Reader.getString());
                    }
                }
                (readState.innerCount)++;
            }
        }

        public void checkInt(ScalarReader reader, int id, int cycle) {
            if ((id % cycle) == 0) {
                // Skipped values should be null
                Assert.assertTrue((("id = " + id) + " expected null for skipped"), reader.isNull());
            } else
                if (((id % cycle) % (setup.nullCycle)) == 0) {
                    Assert.assertTrue(reader.isNull());
                } else {
                    Assert.assertEquals((id * cycle), reader.getInt());
                }

        }
    }

    @Test
    public void tortureTest() {
        LogFixture.LogFixtureBuilder logBuilder = new LogFixture.LogFixtureBuilder();
        // Enable to get detailed tracing when things go wrong.
        // .logger("org.apache.drill.exec.physical.rowSet", Level.TRACE)
        try (LogFixture logFixture = logBuilder.build()) {
            doTortureTest();
        }
    }
}

