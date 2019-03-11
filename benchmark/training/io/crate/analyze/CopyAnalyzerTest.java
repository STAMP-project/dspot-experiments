/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial agreement.
 */
package io.crate.analyze;


import FileUriCollectPhase.InputFormat.CSV;
import FileUriCollectPhase.InputFormat.JSON;
import WriterProjection.CompressionType.GZIP;
import WriterProjection.OutputFormat.JSON_OBJECT;
import com.carrotsearch.randomizedtesting.RandomizedTest;
import io.crate.exceptions.OperationOnInaccessibleRelationException;
import io.crate.exceptions.PartitionUnknownException;
import io.crate.exceptions.RelationUnknown;
import io.crate.exceptions.SchemaUnknownException;
import io.crate.exceptions.UnsupportedFeatureException;
import io.crate.metadata.table.TableInfo;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.SQLExecutor;
import io.crate.testing.SymbolMatchers;
import io.crate.types.DataTypes;
import org.elasticsearch.common.lucene.BytesRefs;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.Test;


public class CopyAnalyzerTest extends CrateDummyClusterServiceUnitTest {
    private SQLExecutor e;

    @Test
    public void testCopyFromExistingTable() throws Exception {
        CopyFromAnalyzedStatement analysis = e.analyze("copy users from '/some/distant/file.ext'");
        assertThat(analysis.table().ident(), Matchers.is(TableDefinitions.USER_TABLE_IDENT));
        assertThat(analysis.uri(), SymbolMatchers.isLiteral("/some/distant/file.ext"));
    }

    @Test
    public void testCopyFromExistingPartitionedTable() {
        CopyFromAnalyzedStatement analysis = e.analyze("copy parted from '/some/distant/file.ext'");
        assertThat(analysis.table().ident(), Matchers.is(TableDefinitions.TEST_PARTITIONED_TABLE_IDENT));
        assertThat(analysis.uri(), SymbolMatchers.isLiteral("/some/distant/file.ext"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCopyFromPartitionedTablePARTITIONKeywordTooManyArgs() throws Exception {
        e.analyze("copy parted partition (a=1, b=2, c=3) from '/some/distant/file.ext'");
    }

    @Test
    public void testCopyFromPartitionedTablePARTITIONKeywordValidArgs() throws Exception {
        CopyFromAnalyzedStatement analysis = e.analyze("copy parted partition (date=1395874800000) from '/some/distant/file.ext'");
        String parted = ident();
        assertThat(analysis.partitionIdent(), IsEqual.equalTo(parted));
    }

    @Test(expected = RelationUnknown.class)
    public void testCopyFromNonExistingTable() throws Exception {
        e.analyze("copy unknown from '/some/distant/file.ext'");
    }

    @Test
    public void testCopyFromSystemTable() throws Exception {
        expectedException.expect(OperationOnInaccessibleRelationException.class);
        expectedException.expectMessage(("The relation \"sys.shards\" doesn\'t support or allow INSERT " + "operations, as it is read-only."));
        e.analyze("copy sys.shards from '/nope/nope/still.nope'");
    }

    @Test(expected = SchemaUnknownException.class)
    public void testCopyFromUnknownSchema() throws Exception {
        e.analyze("copy suess.shards from '/nope/nope/still.nope'");
    }

    @Test
    public void testCopyFromParameter() throws Exception {
        String path = "/some/distant/file.ext";
        CopyFromAnalyzedStatement analysis = e.analyze("copy users from ?", new Object[]{ path });
        assertThat(analysis.table().ident(), Matchers.is(TableDefinitions.USER_TABLE_IDENT));
        Object value = value();
        assertThat(BytesRefs.toString(value), Matchers.is(path));
    }

    @Test
    public void convertCopyFrom_givenFormatIsSetToJsonInStatement_thenInputFormatIsSetToJson() {
        CopyFromAnalyzedStatement analysis = e.analyze("copy users from '/some/distant/file.ext' with (format='json')");
        assertThat(analysis.inputFormat(), Matchers.is(JSON));
    }

    @Test
    public void convertCopyFrom_givenFormatIsSetToCsvInStatement_thenInputFormatIsSetToCsv() {
        CopyFromAnalyzedStatement analysis = e.analyze("copy users from '/some/distant/file.ext' with (format='csv')");
        assertThat(analysis.inputFormat(), Matchers.is(CSV));
    }

    @Test
    public void convertCopyFrom_givenFormatIsNotSetInStatement_thenInputFormatDefaultsToJson() {
        CopyFromAnalyzedStatement analysis = e.analyze("copy users from '/some/distant/file.ext'");
        assertThat(analysis.inputFormat(), Matchers.is(JSON));
    }

    @Test
    public void testCopyToFile() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("Using COPY TO without specifying a DIRECTORY is not supported");
        e.analyze("copy users to '/blah.txt'");
    }

    @Test
    public void testCopyToDirectory() throws Exception {
        CopyToAnalyzedStatement analysis = e.analyze("copy users to directory '/foo'");
        TableInfo tableInfo = analysis.subQueryRelation().tableRelation().tableInfo();
        assertThat(tableInfo.ident(), Matchers.is(TableDefinitions.USER_TABLE_IDENT));
        assertThat(analysis.uri(), SymbolMatchers.isLiteral("/foo"));
    }

    @Test
    public void testCopySysTableTo() throws Exception {
        expectedException.expect(OperationOnInaccessibleRelationException.class);
        expectedException.expectMessage(("The relation \"sys.nodes\" doesn\'t support or allow COPY TO " + "operations, as it is read-only."));
        e.analyze("copy sys.nodes to directory '/foo'");
    }

    @Test
    public void testCopyToWithColumnList() throws Exception {
        CopyToAnalyzedStatement analysis = e.analyze("copy users (id, name) to DIRECTORY '/tmp'");
        assertThat(analysis.subQueryRelation(), Matchers.instanceOf(QueriedTable.class));
        QuerySpec querySpec = analysis.subQueryRelation().querySpec();
        assertThat(querySpec.outputs().size(), Matchers.is(2));
        assertThat(querySpec.outputs().get(0), SymbolMatchers.isReference("_doc['id']"));
        assertThat(querySpec.outputs().get(1), SymbolMatchers.isReference("_doc['name']"));
    }

    @Test
    public void testCopyToFileWithCompressionParams() throws Exception {
        CopyToAnalyzedStatement analysis = e.analyze("copy users to directory '/blah' with (compression='gzip')");
        TableInfo tableInfo = analysis.subQueryRelation().tableRelation().tableInfo();
        assertThat(tableInfo.ident(), Matchers.is(TableDefinitions.USER_TABLE_IDENT));
        assertThat(analysis.uri(), SymbolMatchers.isLiteral("/blah"));
        assertThat(analysis.compressionType(), Matchers.is(GZIP));
    }

    @Test
    public void testCopyToFileWithUnknownParams() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("setting 'foo' not supported");
        e.analyze("copy users to directory '/blah' with (foo='gzip')");
    }

    @Test
    public void testCopyToFileWithPartitionedTable() throws Exception {
        CopyToAnalyzedStatement analysis = e.analyze("copy parted to directory '/blah'");
        TableInfo tableInfo = analysis.subQueryRelation().tableRelation().tableInfo();
        assertThat(tableInfo.ident(), Matchers.is(TableDefinitions.TEST_PARTITIONED_TABLE_IDENT));
        assertThat(analysis.overwrites().size(), Matchers.is(1));
    }

    @Test
    public void testCopyToFileWithPartitionClause() throws Exception {
        CopyToAnalyzedStatement analysis = e.analyze("copy parted partition (date=1395874800000) to directory '/blah'");
        String parted = asIndexName();
        QuerySpec querySpec = analysis.subQueryRelation().querySpec();
        assertThat(querySpec.where().partitions(), Matchers.contains(parted));
    }

    @Test
    public void testCopyToDirectoryWithPartitionClause() throws Exception {
        CopyToAnalyzedStatement analysis = e.analyze("copy parted partition (date=1395874800000) to directory '/tmp'");
        String parted = asIndexName();
        QuerySpec querySpec = analysis.subQueryRelation().querySpec();
        assertThat(querySpec.where().partitions(), Matchers.contains(parted));
        assertThat(analysis.overwrites().size(), Matchers.is(0));
    }

    @Test
    public void testCopyToDirectoryWithNotExistingPartitionClause() throws Exception {
        expectedException.expect(PartitionUnknownException.class);
        expectedException.expectMessage("No partition for table 'doc.parted' with ident '04130' exists");
        e.analyze("copy parted partition (date=0) to directory '/tmp/'");
    }

    @Test
    public void testCopyToWithWhereClause() throws Exception {
        CopyToAnalyzedStatement analysis = e.analyze("copy parted where id = 1 to directory '/tmp/foo'");
        QuerySpec querySpec = analysis.subQueryRelation().querySpec();
        assertThat(querySpec.where().query(), SymbolMatchers.isFunction("op_="));
    }

    @Test
    public void testCopyToWithPartitionIdentAndPartitionInWhereClause() throws Exception {
        CopyToAnalyzedStatement analysis = e.analyze("copy parted partition (date=1395874800000) where date = 1395874800000 to directory '/tmp/foo'");
        String parted = asIndexName();
        QuerySpec querySpec = analysis.subQueryRelation().querySpec();
        assertThat(querySpec.where().partitions(), Matchers.contains(parted));
    }

    @Test
    public void testCopyToWithPartitionIdentAndWhereClause() throws Exception {
        CopyToAnalyzedStatement analysis = e.analyze("copy parted partition (date=1395874800000) where id = 1 to directory '/tmp/foo'");
        String parted = asIndexName();
        QuerySpec querySpec = analysis.subQueryRelation().querySpec();
        assertThat(querySpec.where().partitions(), Matchers.contains(parted));
        assertThat(querySpec.where().query(), SymbolMatchers.isFunction("op_="));
    }

    @Test
    public void testCopyToWithNotExistingPartitionClause() throws Exception {
        expectedException.expect(PartitionUnknownException.class);
        expectedException.expectMessage("No partition for table 'doc.parted' with ident '04130' exists");
        e.analyze("copy parted partition (date=0) to directory '/tmp/blah'");
    }

    @Test
    public void testCopyToFileWithSelectedColumnsAndOutputFormatParam() throws Exception {
        CopyToAnalyzedStatement analysis = e.analyze("copy users (id, name) to directory '/blah' with (format='json_object')");
        TableInfo tableInfo = analysis.subQueryRelation().tableRelation().tableInfo();
        assertThat(tableInfo.ident(), Matchers.is(TableDefinitions.USER_TABLE_IDENT));
        assertThat(analysis.uri(), SymbolMatchers.isLiteral("/blah"));
        assertThat(analysis.outputFormat(), Matchers.is(JSON_OBJECT));
        assertThat(analysis.outputNames(), Matchers.contains("id", "name"));
    }

    @Test
    public void testCopyToFileWithUnsupportedOutputFormatParam() throws Exception {
        expectedException.expect(UnsupportedFeatureException.class);
        expectedException.expectMessage("Output format not supported without specifying columns.");
        e.analyze("copy users to directory '/blah' with (format='json_array')");
    }

    @Test
    public void testCopyFromWithReferenceAssignedToProperty() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Can\'t use column reference in property assignment \"compression = gzip\". Use literals instead.");
        e.analyze("copy users from '/blah.txt' with (compression = gzip)");
    }

    @Test
    public void testCopyFromFileUriArray() throws Exception {
        Object[] files = RandomizedTest.$("/f1.json", "/f2.json");
        CopyFromAnalyzedStatement copyFrom = e.analyze("copy users from ?", new Object[]{ files });
        assertThat(copyFrom.uri(), SymbolMatchers.isLiteral(RandomizedTest.$("/f1.json", "/f2.json"), new io.crate.types.ArrayType(DataTypes.STRING)));
    }

    @Test
    public void testCopyFromInvalidTypedExpression() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("fileUri must be of type STRING or STRING ARRAY. Got integer_array");
        Object[] files = RandomizedTest.$(1, 2, 3);
        e.analyze("copy users from ?", new Object[]{ files });
    }

    @Test
    public void testStringAsNodeFiltersArgument() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(("Invalid parameter passed to node_filters. " + "Expected an object with name or id keys and string values. Got 'invalid'"));
        e.analyze("copy users from '/' with (node_filters='invalid')");
    }

    @Test
    public void testObjectWithWrongKeyAsNodeFiltersArgument() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Invalid node_filters arguments: [dummy]");
        e.analyze("copy users from '/' with (node_filters={dummy='invalid'})");
    }

    @Test
    public void testObjectWithInvalidValueTypeAsNodeFiltersArgument() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("node_filters argument 'name' must be a String, not 20 (Long)");
        e.analyze("copy users from '/' with (node_filters={name=20})");
    }
}

