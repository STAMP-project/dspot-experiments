/**
 * Copyright 2018. Crown Copyright
 *
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
package uk.gov.gchq.gaffer.parquetstore.operation.handler.utilities;


import TestGroups.EDGE;
import TestGroups.EDGE_2;
import TestGroups.ENTITY;
import TestGroups.ENTITY_2;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.gov.gchq.gaffer.commonutil.CommonTestConstants;
import uk.gov.gchq.gaffer.commonutil.TestGroups;
import uk.gov.gchq.gaffer.parquetstore.partitioner.GraphPartitioner;
import uk.gov.gchq.gaffer.parquetstore.partitioner.GroupPartitioner;
import uk.gov.gchq.gaffer.parquetstore.partitioner.PartitionKey;
import uk.gov.gchq.gaffer.parquetstore.utils.SchemaUtils;
import uk.gov.gchq.gaffer.store.schema.Schema;


public class CalculatePartitionerTest {
    @Rule
    public final TemporaryFolder testFolder = new TemporaryFolder(CommonTestConstants.TMP_DIRECTORY);

    @Test
    public void calculatePartitionerTest() throws IOException {
        // Given
        final FileSystem fs = FileSystem.get(new Configuration());
        final Schema schema = getSchema();
        final SchemaUtils schemaUtils = new SchemaUtils(schema);
        final String topLevelFolder = testFolder.newFolder().toPath().toString();
        CalculatePartitionerTest.writeData(topLevelFolder, schemaUtils);
        // When
        // - Calculate partitioner from files
        final GraphPartitioner actual = call();
        // - Manually create the correct partitioner
        final GraphPartitioner expected = new GraphPartitioner();
        final List<PartitionKey> splitPointsEntity = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            splitPointsEntity.add(new PartitionKey(new Object[]{ 10L * i }));
        }
        final GroupPartitioner groupPartitionerEntity = new GroupPartitioner(TestGroups.ENTITY, splitPointsEntity);
        expected.addGroupPartitioner(ENTITY, groupPartitionerEntity);
        final GroupPartitioner groupPartitionerEntity2 = new GroupPartitioner(TestGroups.ENTITY_2, splitPointsEntity);
        expected.addGroupPartitioner(ENTITY_2, groupPartitionerEntity2);
        final List<PartitionKey> splitPointsEdge = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            splitPointsEdge.add(new PartitionKey(new Object[]{ 10L * i, (10L * i) + 1, true }));
        }
        final GroupPartitioner groupPartitionerEdge = new GroupPartitioner(TestGroups.EDGE, splitPointsEdge);
        expected.addGroupPartitioner(EDGE, groupPartitionerEdge);
        final GroupPartitioner groupPartitionerEdge2 = new GroupPartitioner(TestGroups.EDGE_2, splitPointsEdge);
        expected.addGroupPartitioner(EDGE_2, groupPartitionerEdge2);
        final List<PartitionKey> splitPointsReversedEdge = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            splitPointsReversedEdge.add(new PartitionKey(new Object[]{ (10L * i) + 1, 10L * i, true }));
        }
        final GroupPartitioner reversedGroupPartitionerEdge = new GroupPartitioner(TestGroups.EDGE, splitPointsReversedEdge);
        expected.addGroupPartitionerForReversedEdges(EDGE, reversedGroupPartitionerEdge);
        final GroupPartitioner reversedGroupPartitionerEdge2 = new GroupPartitioner(TestGroups.EDGE_2, splitPointsReversedEdge);
        expected.addGroupPartitionerForReversedEdges(EDGE_2, reversedGroupPartitionerEdge2);
        // Then
        Assert.assertEquals(expected, actual);
    }
}

