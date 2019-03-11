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
package org.apache.drill.exec.physical.impl.scan.project;


import MinorType.BIGINT;
import MinorType.FLOAT8;
import MinorType.INT;
import MinorType.NULL;
import MinorType.VARCHAR;
import ResolvedTableColumn.ID;
import java.util.List;
import org.apache.drill.categories.RowSetTests;
import org.apache.drill.exec.physical.impl.protocol.SchemaTracker;
import org.apache.drill.exec.physical.impl.scan.ScanTestUtils;
import org.apache.drill.exec.physical.impl.scan.file.FileMetadataManager;
import org.apache.drill.exec.physical.impl.scan.project.ResolvedTuple.ResolvedRow;
import org.apache.drill.exec.physical.impl.scan.project.SchemaSmoother.IncompatibleSchemaException;
import org.apache.drill.exec.physical.rowSet.ResultSetLoader;
import org.apache.drill.exec.physical.rowSet.impl.RowSetTestUtils;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.record.metadata.TupleMetadata;
import org.apache.drill.shaded.guava.com.google.common.collect.Lists;
import org.apache.drill.test.SubOperatorTest;
import org.apache.drill.test.rowSet.RowSet;
import org.apache.drill.test.rowSet.RowSetComparison;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Tests schema smoothing at the schema projection level.
 * This level handles reusing prior types when filling null
 * values. But, because no actual vectors are involved, it
 * does not handle the schema chosen for a table ahead of
 * time, only the schema as it is merged with prior schema to
 * detect missing columns.
 * <p>
 * Focuses on the <tt>SmoothingProjection</tt> class itself.
 * <p>
 * Note that, at present, schema smoothing does not work for entire
 * maps. That is, if file 1 has, say <tt>{a: {b: 10, c: "foo"}}</tt>
 * and file 2 has, say, <tt>{a: null}</tt>, then schema smoothing does
 * not currently know how to recreate the map. The same is true of
 * lists and unions. Handling such cases is complex and is probably
 * better handled via a system that allows the user to specify their
 * intent by providing a schema to apply to the two files.
 * <p>
 * Note that schema smoothing itself is an experimental work-around
 * to a fundamental limitation in Drill:
 * <ul>
 * <li>Drill cannot predict the future: each file (or batch)
 * may have a different schema.</li>
 * <li>Drill does not know about these differences until they
 * occur.</li>
 * <li>The scan operator is obligated to return the same schema
 * (and same vectors) from one file to the next, else a "hard"
 * schema change is sent down stream.</li>
 * </ul>
 *
 * The problem is actually intractable. The schema smoother handles the
 * cases that can be handled, such as required --> nullable, a column
 * disappearing, etc. This whole mechanism should be scrapped if/when
 * Drill can work with schemas. Or, keep this to handle, as best we can,
 * odd schemas, but insist on a schema to resolve issues that this
 * mechanism cannot handle (and that, indeed, no algorithm could handle
 * because such an algorithm would require time-travel: looking into
 * the future to know what data will be scanned.)
 */
// TODO: Test schema smoothing with repeated
// TODO: Test hard schema change
// TODO: Typed null column tests (resurrect)
// TODO: Test maps and arrays of maps
@Category(RowSetTests.class)
public class TestSchemaSmoothing extends SubOperatorTest {
    /**
     * Sanity test for the simple, discrete case. The purpose of
     * discrete is just to run the basic lifecycle in a way that
     * is compatible with the schema-persistence version.
     */
    @Test
    public void testDiscrete() {
        // Set up the file metadata manager
        Path filePathA = new Path("hdfs:///w/x/y/a.csv");
        Path filePathB = new Path("hdfs:///w/x/y/b.csv");
        FileMetadataManager metadataManager = new FileMetadataManager(SubOperatorTest.fixture.getOptionManager(), new Path("hdfs:///w"), Lists.newArrayList(filePathA, filePathB));
        // Set up the scan level projection
        ScanLevelProjection scanProj = new ScanLevelProjection(RowSetTestUtils.projectList(ScanTestUtils.FILE_NAME_COL, "a", "b"), ScanTestUtils.parsers(metadataManager.projectionParser()));
        {
            // Define a file a.csv
            metadataManager.startFile(filePathA);
            // Build the output schema from the (a, b) table schema
            TupleMetadata twoColSchema = new SchemaBuilder().add("a", INT).addNullable("b", VARCHAR, 10).buildSchema();
            NullColumnBuilder builder = new NullColumnBuilder(null, false);
            ResolvedRow rootTuple = new ResolvedRow(builder);
            new ExplicitSchemaProjection(scanProj, twoColSchema, rootTuple, ScanTestUtils.resolvers(metadataManager));
            // Verify the full output schema
            TupleMetadata expectedSchema = new SchemaBuilder().add("filename", VARCHAR).add("a", INT).addNullable("b", VARCHAR, 10).buildSchema();
            // Verify
            List<ResolvedColumn> columns = rootTuple.columns();
            Assert.assertEquals(3, columns.size());
            Assert.assertTrue(ScanTestUtils.schema(rootTuple).isEquivalent(expectedSchema));
            Assert.assertEquals(ScanTestUtils.FILE_NAME_COL, columns.get(0).name());
            Assert.assertEquals("a.csv", value());
            Assert.assertEquals(ID, columns.get(1).nodeType());
        }
        {
            // Define a file b.csv
            metadataManager.startFile(filePathB);
            // Build the output schema from the (a) table schema
            TupleMetadata oneColSchema = new SchemaBuilder().add("a", INT).buildSchema();
            NullColumnBuilder builder = new NullColumnBuilder(null, false);
            ResolvedRow rootTuple = new ResolvedRow(builder);
            new ExplicitSchemaProjection(scanProj, oneColSchema, rootTuple, ScanTestUtils.resolvers(metadataManager));
            // Verify the full output schema
            // Since this mode is "discrete", we don't remember the type
            // of the missing column. (Instead, it is filled in at the
            // vector level as part of vector persistence.) During projection, it is
            // marked with type NULL so that the null column builder will fill in
            // the proper type.
            TupleMetadata expectedSchema = new SchemaBuilder().add("filename", VARCHAR).add("a", INT).addNullable("b", NULL).buildSchema();
            // Verify
            List<ResolvedColumn> columns = rootTuple.columns();
            Assert.assertEquals(3, columns.size());
            Assert.assertTrue(ScanTestUtils.schema(rootTuple).isEquivalent(expectedSchema));
            Assert.assertEquals(ScanTestUtils.FILE_NAME_COL, columns.get(0).name());
            Assert.assertEquals("b.csv", value());
            Assert.assertEquals(ID, columns.get(1).nodeType());
            Assert.assertEquals(ResolvedNullColumn.ID, columns.get(2).nodeType());
        }
    }

    /**
     * Low-level test of the smoothing projection, including the exceptions
     * it throws when things are not going its way.
     */
    @Test
    public void testSmoothingProjection() {
        final ScanLevelProjection scanProj = new ScanLevelProjection(RowSetTestUtils.projectAll(), ScanTestUtils.parsers());
        // Table 1: (a: nullable bigint, b)
        final TupleMetadata schema1 = new SchemaBuilder().addNullable("a", BIGINT).addNullable("b", VARCHAR).add("c", FLOAT8).buildSchema();
        ResolvedRow priorSchema;
        {
            final NullColumnBuilder builder = new NullColumnBuilder(null, false);
            final ResolvedRow rootTuple = new ResolvedRow(builder);
            new WildcardSchemaProjection(scanProj, schema1, rootTuple, ScanTestUtils.resolvers());
            priorSchema = rootTuple;
        }
        // Table 2: (a: nullable bigint, c), column omitted, original schema preserved
        final TupleMetadata schema2 = new SchemaBuilder().addNullable("a", BIGINT).add("c", FLOAT8).buildSchema();
        try {
            final NullColumnBuilder builder = new NullColumnBuilder(null, false);
            final ResolvedRow rootTuple = new ResolvedRow(builder);
            new SmoothingProjection(scanProj, schema2, priorSchema, rootTuple, ScanTestUtils.resolvers());
            Assert.assertTrue(schema1.isEquivalent(ScanTestUtils.schema(rootTuple)));
            priorSchema = rootTuple;
        } catch (final IncompatibleSchemaException e) {
            Assert.fail();
        }
        // Table 3: (a, c, d), column added, must replan schema
        final TupleMetadata schema3 = new SchemaBuilder().addNullable("a", BIGINT).addNullable("b", VARCHAR).add("c", FLOAT8).add("d", INT).buildSchema();
        try {
            final NullColumnBuilder builder = new NullColumnBuilder(null, false);
            final ResolvedRow rootTuple = new ResolvedRow(builder);
            new SmoothingProjection(scanProj, schema3, priorSchema, rootTuple, ScanTestUtils.resolvers());
            Assert.fail();
        } catch (final IncompatibleSchemaException e) {
            // Expected
        }
        // Table 4: (a: double), change type must replan schema
        final TupleMetadata schema4 = new SchemaBuilder().addNullable("a", FLOAT8).addNullable("b", VARCHAR).add("c", FLOAT8).buildSchema();
        try {
            final NullColumnBuilder builder = new NullColumnBuilder(null, false);
            final ResolvedRow rootTuple = new ResolvedRow(builder);
            new SmoothingProjection(scanProj, schema4, priorSchema, rootTuple, ScanTestUtils.resolvers());
            Assert.fail();
        } catch (final IncompatibleSchemaException e) {
            // Expected
        }
        // Table 5: Drop a non-nullable column, must replan
        final TupleMetadata schema6 = new SchemaBuilder().addNullable("a", BIGINT).addNullable("b", VARCHAR).buildSchema();
        try {
            final NullColumnBuilder builder = new NullColumnBuilder(null, false);
            final ResolvedRow rootTuple = new ResolvedRow(builder);
            new SmoothingProjection(scanProj, schema6, priorSchema, rootTuple, ScanTestUtils.resolvers());
            Assert.fail();
        } catch (final IncompatibleSchemaException e) {
            // Expected
        }
    }

    /**
     * Case in which the table schema is a superset of the prior
     * schema. Discard prior schema. Turn off auto expansion of
     * metadata for a simpler test.
     */
    @Test
    public void testSmaller() {
        final ScanLevelProjection scanProj = new ScanLevelProjection(RowSetTestUtils.projectAll(), ScanTestUtils.parsers());
        final SchemaSmoother smoother = new SchemaSmoother(scanProj, ScanTestUtils.resolvers());
        final TupleMetadata priorSchema = new SchemaBuilder().add("a", INT).buildSchema();
        final TupleMetadata tableSchema = new SchemaBuilder().add("a", INT).add("b", VARCHAR).buildSchema();
        {
            final NullColumnBuilder builder = new NullColumnBuilder(null, false);
            final ResolvedRow rootTuple = new ResolvedRow(builder);
            smoother.resolve(priorSchema, rootTuple);
            Assert.assertEquals(1, smoother.schemaVersion());
            Assert.assertTrue(ScanTestUtils.schema(rootTuple).isEquivalent(priorSchema));
        }
        {
            final NullColumnBuilder builder = new NullColumnBuilder(null, false);
            final ResolvedRow rootTuple = new ResolvedRow(builder);
            smoother.resolve(tableSchema, rootTuple);
            Assert.assertEquals(2, smoother.schemaVersion());
            Assert.assertTrue(ScanTestUtils.schema(rootTuple).isEquivalent(tableSchema));
        }
    }

    /**
     * Case in which the table schema and prior are disjoint
     * sets. Discard the prior schema.
     */
    @Test
    public void testDisjoint() {
        final ScanLevelProjection scanProj = new ScanLevelProjection(RowSetTestUtils.projectAll(), ScanTestUtils.parsers());
        final SchemaSmoother smoother = new SchemaSmoother(scanProj, ScanTestUtils.resolvers());
        final TupleMetadata priorSchema = new SchemaBuilder().add("a", INT).buildSchema();
        final TupleMetadata tableSchema = new SchemaBuilder().add("b", VARCHAR).buildSchema();
        {
            doResolve(smoother, priorSchema);
        }
        {
            final ResolvedRow rootTuple = doResolve(smoother, tableSchema);
            Assert.assertEquals(2, smoother.schemaVersion());
            Assert.assertTrue(ScanTestUtils.schema(rootTuple).isEquivalent(tableSchema));
        }
    }

    /**
     * Column names match, but types differ. Discard the prior schema.
     */
    @Test
    public void testDifferentTypes() {
        final ScanLevelProjection scanProj = new ScanLevelProjection(RowSetTestUtils.projectAll(), ScanTestUtils.parsers());
        final SchemaSmoother smoother = new SchemaSmoother(scanProj, ScanTestUtils.resolvers());
        final TupleMetadata priorSchema = new SchemaBuilder().add("a", INT).add("b", VARCHAR).buildSchema();
        final TupleMetadata tableSchema = new SchemaBuilder().add("a", INT).addNullable("b", VARCHAR).buildSchema();
        {
            doResolve(smoother, priorSchema);
        }
        {
            final ResolvedRow rootTuple = doResolve(smoother, tableSchema);
            Assert.assertEquals(2, smoother.schemaVersion());
            Assert.assertTrue(ScanTestUtils.schema(rootTuple).isEquivalent(tableSchema));
        }
    }

    /**
     * The prior and table schemas are identical. Preserve the prior
     * schema (though, the output is no different than if we discarded
     * the prior schema...)
     */
    @Test
    public void testSameSchemas() {
        final ScanLevelProjection scanProj = new ScanLevelProjection(RowSetTestUtils.projectAll(), ScanTestUtils.parsers());
        final SchemaSmoother smoother = new SchemaSmoother(scanProj, ScanTestUtils.resolvers());
        final TupleMetadata priorSchema = new SchemaBuilder().add("a", INT).add("b", VARCHAR).buildSchema();
        final TupleMetadata tableSchema = new SchemaBuilder().add("a", INT).add("b", VARCHAR).buildSchema();
        {
            doResolve(smoother, priorSchema);
        }
        {
            final ResolvedRow rootTuple = doResolve(smoother, tableSchema);
            Assert.assertEquals(1, smoother.schemaVersion());
            final TupleMetadata actualSchema = ScanTestUtils.schema(rootTuple);
            Assert.assertTrue(actualSchema.isEquivalent(tableSchema));
            Assert.assertTrue(actualSchema.isEquivalent(priorSchema));
        }
    }

    /**
     * The prior and table schemas are identical, but the cases of names differ.
     * Preserve the case of the first schema.
     */
    @Test
    public void testDifferentCase() {
        final ScanLevelProjection scanProj = new ScanLevelProjection(RowSetTestUtils.projectAll(), ScanTestUtils.parsers());
        final SchemaSmoother smoother = new SchemaSmoother(scanProj, ScanTestUtils.resolvers());
        final TupleMetadata priorSchema = new SchemaBuilder().add("a", INT).add("b", VARCHAR).buildSchema();
        final TupleMetadata tableSchema = new SchemaBuilder().add("A", INT).add("B", VARCHAR).buildSchema();
        {
            doResolve(smoother, priorSchema);
        }
        {
            final ResolvedRow rootTuple = doResolve(smoother, tableSchema);
            Assert.assertEquals(1, smoother.schemaVersion());
            Assert.assertTrue(ScanTestUtils.schema(rootTuple).isEquivalent(priorSchema));
            final List<ResolvedColumn> columns = rootTuple.columns();
            Assert.assertEquals("a", columns.get(0).name());
        }
    }

    /**
     * Can't preserve the prior schema if it had required columns
     * where the new schema has no columns.
     */
    @Test
    public void testRequired() {
        final ScanLevelProjection scanProj = new ScanLevelProjection(RowSetTestUtils.projectAll(), ScanTestUtils.parsers());
        final SchemaSmoother smoother = new SchemaSmoother(scanProj, ScanTestUtils.resolvers());
        final TupleMetadata priorSchema = new SchemaBuilder().add("a", INT).addNullable("b", VARCHAR).buildSchema();
        final TupleMetadata tableSchema = new SchemaBuilder().addNullable("b", VARCHAR).buildSchema();
        {
            doResolve(smoother, priorSchema);
        }
        {
            final ResolvedRow rootTuple = doResolve(smoother, tableSchema);
            Assert.assertEquals(2, smoother.schemaVersion());
            Assert.assertTrue(ScanTestUtils.schema(rootTuple).isEquivalent(tableSchema));
        }
    }

    /**
     * Preserve the prior schema if table is a subset and missing columns
     * are nullable or repeated.
     */
    @Test
    public void testMissingNullableColumns() {
        final ScanLevelProjection scanProj = new ScanLevelProjection(RowSetTestUtils.projectAll(), ScanTestUtils.parsers());
        final SchemaSmoother smoother = new SchemaSmoother(scanProj, ScanTestUtils.resolvers());
        final TupleMetadata priorSchema = new SchemaBuilder().addNullable("a", INT).add("b", VARCHAR).addArray("c", BIGINT).buildSchema();
        final TupleMetadata tableSchema = new SchemaBuilder().add("b", VARCHAR).buildSchema();
        {
            doResolve(smoother, priorSchema);
        }
        {
            final ResolvedRow rootTuple = doResolve(smoother, tableSchema);
            Assert.assertEquals(1, smoother.schemaVersion());
            Assert.assertTrue(ScanTestUtils.schema(rootTuple).isEquivalent(priorSchema));
        }
    }

    /**
     * Preserve the prior schema if table is a subset. Map the table
     * columns to the output using the prior schema ordering.
     */
    @Test
    public void testReordering() {
        final ScanLevelProjection scanProj = new ScanLevelProjection(RowSetTestUtils.projectAll(), ScanTestUtils.parsers());
        final SchemaSmoother smoother = new SchemaSmoother(scanProj, ScanTestUtils.resolvers());
        final TupleMetadata priorSchema = new SchemaBuilder().addNullable("a", INT).add("b", VARCHAR).addArray("c", BIGINT).buildSchema();
        final TupleMetadata tableSchema = new SchemaBuilder().add("b", VARCHAR).addNullable("a", INT).buildSchema();
        {
            doResolve(smoother, priorSchema);
        }
        {
            final ResolvedRow rootTuple = doResolve(smoother, tableSchema);
            Assert.assertEquals(1, smoother.schemaVersion());
            Assert.assertTrue(ScanTestUtils.schema(rootTuple).isEquivalent(priorSchema));
        }
    }

    /**
     * If using the legacy wildcard expansion, reuse schema if partition paths
     * are the same length.
     */
    @Test
    public void testSamePartitionLength() {
        // Set up the file metadata manager
        Path filePathA = new Path("hdfs:///w/x/y/a.csv");
        Path filePathB = new Path("hdfs:///w/x/y/b.csv");
        FileMetadataManager metadataManager = new FileMetadataManager(SubOperatorTest.fixture.getOptionManager(), new Path("hdfs:///w"), Lists.newArrayList(filePathA, filePathB));
        // Set up the scan level projection
        ScanLevelProjection scanProj = new ScanLevelProjection(ScanTestUtils.projectAllWithMetadata(2), ScanTestUtils.parsers(metadataManager.projectionParser()));
        // Define the schema smoother
        SchemaSmoother smoother = new SchemaSmoother(scanProj, ScanTestUtils.resolvers(metadataManager));
        TupleMetadata tableSchema = new SchemaBuilder().add("a", INT).add("b", VARCHAR).buildSchema();
        TupleMetadata expectedSchema = ScanTestUtils.expandMetadata(tableSchema, metadataManager, 2);
        {
            metadataManager.startFile(filePathA);
            ResolvedRow rootTuple = doResolve(smoother, tableSchema);
            Assert.assertTrue(ScanTestUtils.schema(rootTuple).isEquivalent(expectedSchema));
        }
        {
            metadataManager.startFile(filePathB);
            ResolvedRow rootTuple = doResolve(smoother, tableSchema);
            Assert.assertEquals(1, smoother.schemaVersion());
            Assert.assertTrue(ScanTestUtils.schema(rootTuple).isEquivalent(expectedSchema));
        }
    }

    /**
     * If using the legacy wildcard expansion, reuse schema if the new partition path
     * is shorter than the previous. (Unneeded partitions will be set to null by the
     * scan projector.)
     */
    @Test
    public void testShorterPartitionLength() {
        // Set up the file metadata manager
        Path filePathA = new Path("hdfs:///w/x/y/a.csv");
        Path filePathB = new Path("hdfs:///w/x/b.csv");
        FileMetadataManager metadataManager = new FileMetadataManager(SubOperatorTest.fixture.getOptionManager(), new Path("hdfs:///w"), Lists.newArrayList(filePathA, filePathB));
        // Set up the scan level projection
        ScanLevelProjection scanProj = new ScanLevelProjection(ScanTestUtils.projectAllWithMetadata(2), ScanTestUtils.parsers(metadataManager.projectionParser()));
        // Define the schema smoother
        SchemaSmoother smoother = new SchemaSmoother(scanProj, ScanTestUtils.resolvers(metadataManager));
        TupleMetadata tableSchema = new SchemaBuilder().add("a", INT).add("b", VARCHAR).buildSchema();
        TupleMetadata expectedSchema = ScanTestUtils.expandMetadata(tableSchema, metadataManager, 2);
        {
            metadataManager.startFile(filePathA);
            ResolvedRow rootTuple = doResolve(smoother, tableSchema);
            Assert.assertTrue(ScanTestUtils.schema(rootTuple).isEquivalent(expectedSchema));
        }
        {
            metadataManager.startFile(filePathB);
            ResolvedRow rootTuple = doResolve(smoother, tableSchema);
            Assert.assertEquals(1, smoother.schemaVersion());
            Assert.assertTrue(ScanTestUtils.schema(rootTuple).isEquivalent(expectedSchema));
        }
    }

    /**
     * If using the legacy wildcard expansion, we are able to use the same
     * schema even if the new partition path is longer than the previous.
     * Because all file names are provided up front.
     */
    @Test
    public void testLongerPartitionLength() {
        // Set up the file metadata manager
        Path filePathA = new Path("hdfs:///w/x/a.csv");
        Path filePathB = new Path("hdfs:///w/x/y/b.csv");
        FileMetadataManager metadataManager = new FileMetadataManager(SubOperatorTest.fixture.getOptionManager(), new Path("hdfs:///w"), Lists.newArrayList(filePathA, filePathB));
        // Set up the scan level projection
        ScanLevelProjection scanProj = new ScanLevelProjection(ScanTestUtils.projectAllWithMetadata(2), ScanTestUtils.parsers(metadataManager.projectionParser()));
        // Define the schema smoother
        SchemaSmoother smoother = new SchemaSmoother(scanProj, ScanTestUtils.resolvers(metadataManager));
        TupleMetadata tableSchema = new SchemaBuilder().add("a", INT).add("b", VARCHAR).buildSchema();
        TupleMetadata expectedSchema = ScanTestUtils.expandMetadata(tableSchema, metadataManager, 2);
        {
            metadataManager.startFile(filePathA);
            ResolvedRow rootTuple = doResolve(smoother, tableSchema);
            Assert.assertTrue(ScanTestUtils.schema(rootTuple).isEquivalent(expectedSchema));
        }
        {
            metadataManager.startFile(filePathB);
            ResolvedRow rootTuple = doResolve(smoother, tableSchema);
            Assert.assertEquals(1, smoother.schemaVersion());
            Assert.assertTrue(ScanTestUtils.schema(rootTuple).isEquivalent(expectedSchema));
        }
    }

    /**
     * Integrated test across multiple schemas at the batch level.
     */
    @Test
    public void testSmoothableSchemaBatches() {
        final ScanLevelProjection scanProj = new ScanLevelProjection(RowSetTestUtils.projectAll(), ScanTestUtils.parsers());
        final SchemaSmoother smoother = new SchemaSmoother(scanProj, ScanTestUtils.resolvers());
        // Table 1: (a: bigint, b)
        final TupleMetadata schema1 = new SchemaBuilder().addNullable("a", BIGINT).addNullable("b", VARCHAR).add("c", FLOAT8).buildSchema();
        {
            final ResolvedRow rootTuple = doResolve(smoother, schema1);
            // Just use the original schema.
            Assert.assertTrue(schema1.isEquivalent(ScanTestUtils.schema(rootTuple)));
            Assert.assertEquals(1, smoother.schemaVersion());
        }
        // Table 2: (a: nullable bigint, c), column ommitted, original schema preserved
        final TupleMetadata schema2 = new SchemaBuilder().addNullable("a", BIGINT).add("c", FLOAT8).buildSchema();
        {
            final ResolvedRow rootTuple = doResolve(smoother, schema2);
            Assert.assertTrue(schema1.isEquivalent(ScanTestUtils.schema(rootTuple)));
            Assert.assertEquals(1, smoother.schemaVersion());
        }
        // Table 3: (a, c, d), column added, must replan schema
        final TupleMetadata schema3 = new SchemaBuilder().addNullable("a", BIGINT).addNullable("b", VARCHAR).add("c", FLOAT8).add("d", INT).buildSchema();
        {
            final ResolvedRow rootTuple = doResolve(smoother, schema3);
            Assert.assertTrue(schema3.isEquivalent(ScanTestUtils.schema(rootTuple)));
            Assert.assertEquals(2, smoother.schemaVersion());
        }
        // Table 4: Drop a non-nullable column, must replan
        final TupleMetadata schema4 = new SchemaBuilder().addNullable("a", BIGINT).addNullable("b", VARCHAR).buildSchema();
        {
            final ResolvedRow rootTuple = doResolve(smoother, schema4);
            Assert.assertTrue(schema4.isEquivalent(ScanTestUtils.schema(rootTuple)));
            Assert.assertEquals(3, smoother.schemaVersion());
        }
        // Table 5: (a: double), change type must replan schema
        final TupleMetadata schema5 = new SchemaBuilder().addNullable("a", FLOAT8).addNullable("b", VARCHAR).buildSchema();
        {
            final ResolvedRow rootTuple = doResolve(smoother, schema5);
            Assert.assertTrue(schema5.isEquivalent(ScanTestUtils.schema(rootTuple)));
            Assert.assertEquals(4, smoother.schemaVersion());
        }
    }

    /**
     * A SELECT * query uses the schema of the table as the output schema.
     * This is trivial when the scanner has one table. But, if two or more
     * tables occur, then things get interesting. The first table sets the
     * schema. The second table then has:
     * <ul>
     * <li>The same schema, trivial case.</li>
     * <li>A subset of the first table. The type of the "missing" column
     * from the first table is used for a null column in the second table.</li>
     * <li>A superset or disjoint set of the first schema. This triggers a hard schema
     * change.</li>
     * </ul>
     * <p>
     * It is an open question whether previous columns should be preserved on
     * a hard reset. For now, the code implements, and this test verifies, that a
     * hard reset clears the "memory" of prior schemas.
     */
    @Test
    public void testWildcardSmoothing() {
        final ScanSchemaOrchestrator projector = new ScanSchemaOrchestrator(SubOperatorTest.fixture.allocator());
        projector.enableSchemaSmoothing(true);
        projector.build(RowSetTestUtils.projectAll());
        final TupleMetadata firstSchema = new SchemaBuilder().add("a", INT).addNullable("b", VARCHAR, 10).addNullable("c", BIGINT).buildSchema();
        final TupleMetadata subsetSchema = new SchemaBuilder().addNullable("b", VARCHAR, 10).add("a", INT).buildSchema();
        final TupleMetadata disjointSchema = new SchemaBuilder().add("a", INT).addNullable("b", VARCHAR, 10).add("d", VARCHAR).buildSchema();
        final SchemaTracker tracker = new SchemaTracker();
        int schemaVersion;
        {
            // First table, establishes the baseline
            // ... FROM table 1
            final ReaderSchemaOrchestrator reader = projector.startReader();
            final ResultSetLoader loader = reader.makeTableLoader(firstSchema);
            reader.startBatch();
            loader.writer().addRow(10, "fred", 110L).addRow(20, "wilma", 110L);
            reader.endBatch();
            tracker.trackSchema(projector.output());
            schemaVersion = tracker.schemaVersion();
            final RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(firstSchema).addRow(10, "fred", 110L).addRow(20, "wilma", 110L).build();
            new RowSetComparison(expected).verifyAndClearAll(SubOperatorTest.fixture.wrap(projector.output()));
        }
        {
            // Second table, same schema, the trivial case
            // ... FROM table 2
            final ReaderSchemaOrchestrator reader = projector.startReader();
            final ResultSetLoader loader = reader.makeTableLoader(firstSchema);
            reader.startBatch();
            loader.writer().addRow(70, "pebbles", 770L).addRow(80, "hoppy", 880L);
            reader.endBatch();
            tracker.trackSchema(projector.output());
            Assert.assertEquals(schemaVersion, tracker.schemaVersion());
            final RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(firstSchema).addRow(70, "pebbles", 770L).addRow(80, "hoppy", 880L).build();
            new RowSetComparison(expected).verifyAndClearAll(SubOperatorTest.fixture.wrap(projector.output()));
        }
        {
            // Third table: subset schema of first two
            // ... FROM table 3
            final ReaderSchemaOrchestrator reader = projector.startReader();
            final ResultSetLoader loader = reader.makeTableLoader(subsetSchema);
            reader.startBatch();
            loader.writer().addRow("bambam", 30).addRow("betty", 40);
            reader.endBatch();
            tracker.trackSchema(projector.output());
            Assert.assertEquals(schemaVersion, tracker.schemaVersion());
            final RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(firstSchema).addRow(30, "bambam", null).addRow(40, "betty", null).build();
            new RowSetComparison(expected).verifyAndClearAll(SubOperatorTest.fixture.wrap(projector.output()));
        }
        {
            // Fourth table: disjoint schema, cases a schema reset
            // ... FROM table 4
            final ReaderSchemaOrchestrator reader = projector.startReader();
            final ResultSetLoader loader = reader.makeTableLoader(disjointSchema);
            reader.startBatch();
            loader.writer().addRow(50, "dino", "supporting").addRow(60, "barney", "main");
            reader.endBatch();
            tracker.trackSchema(projector.output());
            Assert.assertNotEquals(schemaVersion, tracker.schemaVersion());
            final RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(disjointSchema).addRow(50, "dino", "supporting").addRow(60, "barney", "main").build();
            new RowSetComparison(expected).verifyAndClearAll(SubOperatorTest.fixture.wrap(projector.output()));
        }
        projector.close();
    }
}

