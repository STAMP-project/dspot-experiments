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
package org.apache.drill.test.rowSet.test;


import MinorType.BIGINT;
import MinorType.FLOAT8;
import MinorType.INT;
import MinorType.VARCHAR;
import java.util.Iterator;
import org.apache.drill.categories.RowSetTests;
import org.apache.drill.exec.record.MaterializedField;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.record.metadata.TupleMetadata;
import org.apache.drill.exec.vector.accessor.ArrayReader;
import org.apache.drill.exec.vector.accessor.ArrayWriter;
import org.apache.drill.exec.vector.accessor.ScalarReader;
import org.apache.drill.exec.vector.accessor.ScalarWriter;
import org.apache.drill.exec.vector.accessor.TupleReader;
import org.apache.drill.exec.vector.accessor.TupleWriter;
import org.apache.drill.exec.vector.complex.RepeatedMapVector;
import org.apache.drill.test.ClientFixture;
import org.apache.drill.test.ClusterFixture;
import org.apache.drill.test.SubOperatorTest;
import org.apache.drill.test.rowSet.RowSet;
import org.apache.drill.test.rowSet.RowSetBuilder;
import org.apache.drill.test.rowSet.RowSetComparison;
import org.apache.drill.test.rowSet.RowSetReader;
import org.apache.drill.test.rowSet.RowSetUtilities;
import org.apache.drill.test.rowSet.RowSetWriter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test map support in the column readers and writers.
 * <p>
 * The tests here are a simplified form of those in
 * TestResultSetLoaderMaps -- the RowSet mechanism requires a fixed
 * schema, which makes this mechanism far simpler.
 */
@Category(RowSetTests.class)
public class TestMapAccessors extends SubOperatorTest {
    @Test
    public void testBasics() {
        final TupleMetadata schema = new SchemaBuilder().add("a", INT).addMap("m").add("c", INT).add("d", VARCHAR).resumeSchema().add("e", VARCHAR).buildSchema();
        RowSetBuilder builder = SubOperatorTest.fixture.rowSetBuilder(schema);
        RowSetWriter rootWriter = builder.writer();
        // Verify structure and schema
        final TupleMetadata actualSchema = tupleSchema();
        Assert.assertEquals(3, actualSchema.size());
        Assert.assertTrue(actualSchema.metadata(1).isMap());
        Assert.assertEquals(2, actualSchema.metadata("m").mapSchema().size());
        Assert.assertEquals(2, actualSchema.column("m").getChildren().size());
        // Write a row the way that clients will do.
        final ScalarWriter aWriter = scalar("a");
        final TupleWriter mWriter = tuple("m");
        final ScalarWriter cWriter = mWriter.scalar("c");
        final ScalarWriter dWriter = mWriter.scalar("d");
        final ScalarWriter eWriter = scalar("e");
        aWriter.setInt(10);
        cWriter.setInt(110);
        dWriter.setString("fred");
        eWriter.setString("pebbles");
        rootWriter.save();
        // Write another using the test-time conveniences
        rootWriter.addRow(20, RowSetUtilities.mapValue(210, "barney"), "bam-bam");
        // Validate data. Do so using the readers to avoid verifying
        // using the very mechanisms we want to test.
        RowSet result = builder.build();
        RowSetReader rootReader = result.reader();
        final ScalarReader aReader = rootReader.scalar("a");
        final TupleReader mReader = rootReader.tuple("m");
        final ScalarReader cReader = mReader.scalar("c");
        final ScalarReader dReader = mReader.scalar("d");
        final ScalarReader eReader = rootReader.scalar("e");
        rootReader.next();
        Assert.assertEquals(10, aReader.getInt());
        Assert.assertEquals(110, cReader.getInt());
        Assert.assertEquals("fred", dReader.getString());
        Assert.assertEquals("pebbles", eReader.getString());
        rootReader.next();
        Assert.assertEquals(20, aReader.getInt());
        Assert.assertEquals(210, cReader.getInt());
        Assert.assertEquals("barney", dReader.getString());
        Assert.assertEquals("bam-bam", eReader.getString());
        // Verify using the convenience methods.
        final RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(schema).addRow(10, RowSetUtilities.mapValue(110, "fred"), "pebbles").addRow(20, RowSetUtilities.mapValue(210, "barney"), "bam-bam").build();
        new RowSetComparison(expected).verify(result);
        // Test that the row set rebuilds its internal structure from
        // a vector container.
        RowSet wrapped = SubOperatorTest.fixture.wrap(result.container());
        RowSetUtilities.verify(expected, wrapped);
    }

    /**
     * Create nested maps. Use required, variable-width columns since
     * those require the most processing and are most likely to
     * fail if anything is out of place.
     */
    @Test
    public void testNestedMapsRequired() {
        final TupleMetadata schema = new SchemaBuilder().add("a", INT).addMap("m1").add("b", VARCHAR).addMap("m2").add("c", VARCHAR).resumeMap().add("d", VARCHAR).resumeSchema().buildSchema();
        RowSetBuilder builder = SubOperatorTest.fixture.rowSetBuilder(schema);
        RowSetWriter rootWriter = builder.writer();
        rootWriter.addRow(10, RowSetUtilities.mapValue("b1", RowSetUtilities.mapValue("c1"), "d1"));
        rootWriter.addRow(20, RowSetUtilities.mapValue("b2", RowSetUtilities.mapValue("c2"), "d2"));
        // Validate directly
        RowSet result = builder.build();
        RowSetReader rootReader = result.reader();
        TupleReader m1Reader = rootReader.tuple("m1");
        TupleReader m2Reader = m1Reader.tuple("m2");
        rootReader.next();
        Assert.assertEquals(10, rootReader.scalar("a").getInt());
        Assert.assertEquals("b1", m1Reader.scalar("b").getString());
        Assert.assertEquals("c1", m2Reader.scalar("c").getString());
        Assert.assertEquals("d1", m1Reader.scalar("d").getString());
        rootReader.next();
        Assert.assertEquals(20, rootReader.scalar("a").getInt());
        Assert.assertEquals("b2", m1Reader.scalar("b").getString());
        Assert.assertEquals("c2", m2Reader.scalar("c").getString());
        Assert.assertEquals("d2", m1Reader.scalar("d").getString());
        // Validate with convenience methods
        RowSet expected = SubOperatorTest.fixture.rowSetBuilder(schema).addRow(10, RowSetUtilities.mapValue("b1", RowSetUtilities.mapValue("c1"), "d1")).addRow(20, RowSetUtilities.mapValue("b2", RowSetUtilities.mapValue("c2"), "d2")).build();
        new RowSetComparison(expected).verify(result);
        // Test that the row set rebuilds its internal structure from
        // a vector container.
        RowSet wrapped = SubOperatorTest.fixture.wrap(result.container());
        RowSetUtilities.verify(expected, wrapped);
    }

    @Test
    public void testBasicRepeatedMap() {
        TupleMetadata schema = new SchemaBuilder().add("a", INT).addMapArray("m").add("c", INT).add("d", VARCHAR).resumeSchema().buildSchema();
        RowSetBuilder builder = SubOperatorTest.fixture.rowSetBuilder(schema);
        RowSetWriter rootWriter = builder.writer();
        // Verify structure and schema
        TupleMetadata actualSchema = tupleSchema();
        Assert.assertEquals(2, actualSchema.size());
        Assert.assertTrue(actualSchema.metadata(1).isArray());
        Assert.assertTrue(actualSchema.metadata(1).isMap());
        Assert.assertEquals(2, actualSchema.metadata("m").mapSchema().size());
        Assert.assertEquals(2, actualSchema.column("m").getChildren().size());
        TupleWriter mapWriter = array("m").tuple();
        Assert.assertSame(actualSchema.metadata("m").mapSchema(), mapWriter.schema().mapSchema());
        Assert.assertSame(mapWriter.tupleSchema(), mapWriter.schema().mapSchema());
        Assert.assertSame(mapWriter.tupleSchema().metadata(0), mapWriter.scalar(0).schema());
        Assert.assertSame(mapWriter.tupleSchema().metadata(1), mapWriter.scalar(1).schema());
        // Write a couple of rows with arrays.
        rootWriter.addRow(10, RowSetUtilities.mapArray(RowSetUtilities.mapValue(110, "d1.1"), RowSetUtilities.mapValue(120, "d2.2"))).addRow(20, RowSetUtilities.mapArray()).addRow(30, RowSetUtilities.mapArray(RowSetUtilities.mapValue(310, "d3.1"), RowSetUtilities.mapValue(320, "d3.2"), RowSetUtilities.mapValue(330, "d3.3")));
        // Verify the first batch
        RowSet actual = builder.build();
        RepeatedMapVector mapVector = ((RepeatedMapVector) (actual.container().getValueVector(1).getValueVector()));
        MaterializedField mapField = mapVector.getField();
        Assert.assertEquals(2, mapField.getChildren().size());
        Iterator<MaterializedField> iter = mapField.getChildren().iterator();
        Assert.assertTrue(mapWriter.scalar(0).schema().schema().isEquivalent(iter.next()));
        Assert.assertTrue(mapWriter.scalar(1).schema().schema().isEquivalent(iter.next()));
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(schema).addRow(10, RowSetUtilities.mapArray(RowSetUtilities.mapValue(110, "d1.1"), RowSetUtilities.mapValue(120, "d2.2"))).addRow(20, RowSetUtilities.mapArray()).addRow(30, RowSetUtilities.mapArray(RowSetUtilities.mapValue(310, "d3.1"), RowSetUtilities.mapValue(320, "d3.2"), RowSetUtilities.mapValue(330, "d3.3"))).build();
        new RowSetComparison(expected).verify(actual);
        // Test that the row set rebuilds its internal structure from
        // a vector container.
        RowSet wrapped = SubOperatorTest.fixture.wrap(actual.container());
        RowSetUtilities.verify(expected, wrapped);
    }

    @Test
    public void testNestedArray() {
        TupleMetadata schema = new SchemaBuilder().add("a", INT).addMapArray("m").add("c", INT).addArray("d", VARCHAR).resumeSchema().buildSchema();
        RowSetBuilder builder = SubOperatorTest.fixture.rowSetBuilder(schema);
        RowSetWriter rootWriter = builder.writer();
        // Write a couple of rows with arrays within arrays.
        // (And, of course, the Varchar is actually an array of
        // bytes, so that's three array levels.)
        rootWriter.addRow(10, RowSetUtilities.mapArray(RowSetUtilities.mapValue(110, RowSetUtilities.strArray("d1.1.1", "d1.1.2")), RowSetUtilities.mapValue(120, RowSetUtilities.strArray("d1.2.1", "d1.2.2")))).addRow(20, RowSetUtilities.mapArray()).addRow(30, RowSetUtilities.mapArray(RowSetUtilities.mapValue(310, RowSetUtilities.strArray("d3.1.1", "d3.2.2")), RowSetUtilities.mapValue(320, RowSetUtilities.strArray()), RowSetUtilities.mapValue(330, RowSetUtilities.strArray("d3.3.1", "d1.2.2"))));
        // Verify the batch
        RowSet actual = builder.build();
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(schema).addRow(10, RowSetUtilities.mapArray(RowSetUtilities.mapValue(110, RowSetUtilities.strArray("d1.1.1", "d1.1.2")), RowSetUtilities.mapValue(120, RowSetUtilities.strArray("d1.2.1", "d1.2.2")))).addRow(20, RowSetUtilities.mapArray()).addRow(30, RowSetUtilities.mapArray(RowSetUtilities.mapValue(310, RowSetUtilities.strArray("d3.1.1", "d3.2.2")), RowSetUtilities.mapValue(320, RowSetUtilities.strArray()), RowSetUtilities.mapValue(330, RowSetUtilities.strArray("d3.3.1", "d1.2.2")))).build();
        new RowSetComparison(expected).verify(actual);
        // Test that the row set rebuilds its internal structure from
        // a vector container.
        RowSet wrapped = SubOperatorTest.fixture.wrap(actual.container());
        RowSetUtilities.verify(expected, wrapped);
    }

    /**
     * Test a doubly-nested array of maps.
     */
    @Test
    public void testDoubleNestedArray() {
        TupleMetadata schema = new SchemaBuilder().add("a", INT).addMapArray("m1").add("b", INT).addMapArray("m2").add("c", INT).addArray("d", VARCHAR).resumeMap().resumeSchema().buildSchema();
        RowSetBuilder builder = SubOperatorTest.fixture.rowSetBuilder(schema);
        RowSetWriter rootWriter = builder.writer();
        ScalarWriter aWriter = scalar("a");
        ArrayWriter a1Writer = rootWriter.array("m1");
        TupleWriter m1Writer = a1Writer.tuple();
        ScalarWriter bWriter = m1Writer.scalar("b");
        ArrayWriter a2Writer = m1Writer.array("m2");
        TupleWriter m2Writer = a2Writer.tuple();
        ScalarWriter cWriter = m2Writer.scalar("c");
        ScalarWriter dWriter = m2Writer.array("d").scalar();
        for (int i = 0; i < 5; i++) {
            aWriter.setInt(i);
            for (int j = 0; j < 4; j++) {
                int a1Key = (i + 10) + j;
                bWriter.setInt(a1Key);
                for (int k = 0; k < 3; k++) {
                    int a2Key = (a1Key * 10) + k;
                    cWriter.setInt(a2Key);
                    for (int l = 0; l < 2; l++) {
                        dWriter.setString(("d-" + ((a2Key * 10) + l)));
                    }
                    a2Writer.save();
                }
                a1Writer.save();
            }
            rootWriter.save();
        }
        RowSet results = builder.build();
        RowSetReader reader = results.reader();
        ScalarReader aReader = reader.scalar("a");
        ArrayReader a1Reader = reader.array("m1");
        TupleReader m1Reader = a1Reader.tuple();
        ScalarReader bReader = m1Reader.scalar("b");
        ArrayReader a2Reader = m1Reader.array("m2");
        TupleReader m2Reader = a2Reader.tuple();
        ScalarReader cReader = m2Reader.scalar("c");
        ArrayReader dArray = m2Reader.array("d");
        ScalarReader dReader = dArray.scalar();
        for (int i = 0; i < 5; i++) {
            Assert.assertTrue(reader.next());
            Assert.assertEquals(i, aReader.getInt());
            for (int j = 0; j < 4; j++) {
                Assert.assertTrue(a1Reader.next());
                int a1Key = (i + 10) + j;
                Assert.assertEquals(a1Key, bReader.getInt());
                for (int k = 0; k < 3; k++) {
                    Assert.assertTrue(a2Reader.next());
                    int a2Key = (a1Key * 10) + k;
                    Assert.assertEquals(a2Key, cReader.getInt());
                    for (int l = 0; l < 2; l++) {
                        Assert.assertTrue(dArray.next());
                        Assert.assertEquals(("d-" + ((a2Key * 10) + l)), dReader.getString());
                    }
                }
            }
        }
        results.clear();
    }

    /**
     * Test that the schema inference handles repeated map vectors.
     * <p>
     * It turns out that when some operators create a map array, it adds the
     * $offset$ vector to the list of children for the map's MaterializedField.
     * But, the RowSet utilities do not. This test verifies that both forms
     * work.
     */
    @Test
    public void testDrill6809() throws Exception {
        try (ClusterFixture cluster = ClusterFixture.standardCluster(SubOperatorTest.dirTestWatcher);ClientFixture client = cluster.clientFixture()) {
            // Input: {"a" : [ {"c": 1} ], "b" : 2.1}
            String sql = "select * from `cp`.`jsoninput/repeatedmap_sort_bug.json`";
            RowSet actual = client.queryBuilder().sql(sql).rowSet();
            TupleMetadata schema = new SchemaBuilder().addMapArray("a").addNullable("c", BIGINT).resumeSchema().addNullable("b", FLOAT8).buildSchema();
            RowSet expected = SubOperatorTest.fixture.rowSetBuilder(schema).addRow(RowSetUtilities.mapArray(RowSetUtilities.mapValue(1L)), 2.1).build();
            RowSetUtilities.verify(expected, actual);
        }
    }
}

