/**
 * Copyright 2017-2018. Crown Copyright
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
package uk.gov.gchq.gaffer.parquetstore.utils;


import TestGroups.EDGE;
import TestGroups.EDGE_2;
import TestGroups.ENTITY;
import TestGroups.ENTITY_2;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import org.apache.hadoop.fs.FileSystem;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.gov.gchq.gaffer.commonutil.CommonTestConstants;
import uk.gov.gchq.gaffer.commonutil.TestGroups;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.parquetstore.operation.handler.utilities.WriteUnsortedData;
import uk.gov.gchq.gaffer.parquetstore.partitioner.GraphPartitioner;
import uk.gov.gchq.gaffer.parquetstore.partitioner.PartitionKey;
import uk.gov.gchq.gaffer.parquetstore.testutils.TestUtils;


public class WriteUnsortedDataTest {
    private static FileSystem fs;

    private static Date DATE0;

    private static Date DATE1;

    private static Date DATE2;

    private static Date DATE3;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static {
        WriteUnsortedDataTest.DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            WriteUnsortedDataTest.DATE0 = WriteUnsortedDataTest.DATE_FORMAT.parse("1970-01-01 00:00:00");
            WriteUnsortedDataTest.DATE1 = WriteUnsortedDataTest.DATE_FORMAT.parse("1971-01-01 00:00:00");
            WriteUnsortedDataTest.DATE2 = WriteUnsortedDataTest.DATE_FORMAT.parse("1972-01-01 00:00:00");
            WriteUnsortedDataTest.DATE3 = WriteUnsortedDataTest.DATE_FORMAT.parse("1973-01-01 00:00:00");
        } catch (final ParseException e1) {
            // Won't happen
        }
    }

    @Rule
    public final TemporaryFolder testFolder = new TemporaryFolder(CommonTestConstants.TMP_DIRECTORY);

    @Test
    public void testNoSplitPointsCase() throws IOException, OperationException {
        // Given
        final String tempFilesDir = testFolder.newFolder().getAbsolutePath();
        final SchemaUtils schemaUtils = new SchemaUtils(TestUtils.gafferSchema("schemaUsingLongVertexType"));
        final GraphPartitioner graphPartitioner = new GraphPartitioner();
        graphPartitioner.addGroupPartitioner(ENTITY, new uk.gov.gchq.gaffer.parquetstore.partitioner.GroupPartitioner(TestGroups.ENTITY, new ArrayList()));
        graphPartitioner.addGroupPartitioner(ENTITY_2, new uk.gov.gchq.gaffer.parquetstore.partitioner.GroupPartitioner(TestGroups.ENTITY_2, new ArrayList()));
        graphPartitioner.addGroupPartitioner(EDGE, new uk.gov.gchq.gaffer.parquetstore.partitioner.GroupPartitioner(TestGroups.EDGE, new ArrayList()));
        graphPartitioner.addGroupPartitioner(EDGE_2, new uk.gov.gchq.gaffer.parquetstore.partitioner.GroupPartitioner(TestGroups.EDGE_2, new ArrayList()));
        graphPartitioner.addGroupPartitionerForReversedEdges(EDGE, new uk.gov.gchq.gaffer.parquetstore.partitioner.GroupPartitioner(TestGroups.EDGE, new ArrayList()));
        graphPartitioner.addGroupPartitionerForReversedEdges(EDGE_2, new uk.gov.gchq.gaffer.parquetstore.partitioner.GroupPartitioner(TestGroups.EDGE_2, new ArrayList()));
        final List<Element> elements = getData(3L);
        final BiFunction<String, Integer, String> fileNameForGroupAndPartitionId = ( group, partitionId) -> (((tempFilesDir + "/GROUP=") + group) + "/split-") + partitionId;
        final BiFunction<String, Integer, String> fileNameForGroupAndPartitionIdForReversedEdge = ( group, partitionId) -> (((tempFilesDir + "/REVERSED-GROUP=") + group) + "/split-") + partitionId;
        final WriteUnsortedData writeUnsortedData = new WriteUnsortedData(tempFilesDir, CompressionCodecName.GZIP, schemaUtils, graphPartitioner, fileNameForGroupAndPartitionId, fileNameForGroupAndPartitionIdForReversedEdge);
        // When
        writeUnsortedData.writeElements(elements);
        // Then
        // - Each directory should exist and contain one file
        WriteUnsortedDataTest.testExistsAndContainsNFiles((((tempFilesDir + "/GROUP=") + (TestGroups.ENTITY)) + "/split-0"), 1);
        WriteUnsortedDataTest.testExistsAndContainsNFiles((((tempFilesDir + "/GROUP=") + (TestGroups.ENTITY_2)) + "/split-0"), 1);
        WriteUnsortedDataTest.testExistsAndContainsNFiles((((tempFilesDir + "/GROUP=") + (TestGroups.EDGE)) + "/split-0"), 1);
        WriteUnsortedDataTest.testExistsAndContainsNFiles((((tempFilesDir + "/GROUP=") + (TestGroups.EDGE_2)) + "/split-0"), 1);
        WriteUnsortedDataTest.testExistsAndContainsNFiles((((tempFilesDir + "/REVERSED-GROUP=") + (TestGroups.EDGE)) + "/split-0"), 1);
        WriteUnsortedDataTest.testExistsAndContainsNFiles((((tempFilesDir + "/REVERSED-GROUP=") + (TestGroups.EDGE_2)) + "/split-0"), 1);
        // - Each file should contain the data that was written to it, in the order it was in the iterable
        WriteUnsortedDataTest.testContainsCorrectDataNoSplitPoints(ENTITY, (((tempFilesDir + "/GROUP=") + (TestGroups.ENTITY)) + "/split-0"), elements, schemaUtils);
        WriteUnsortedDataTest.testContainsCorrectDataNoSplitPoints(ENTITY_2, (((tempFilesDir + "/GROUP=") + (TestGroups.ENTITY_2)) + "/split-0"), elements, schemaUtils);
        WriteUnsortedDataTest.testContainsCorrectDataNoSplitPoints(EDGE, (((tempFilesDir + "/GROUP=") + (TestGroups.EDGE)) + "/split-0"), elements, schemaUtils);
        WriteUnsortedDataTest.testContainsCorrectDataNoSplitPoints(EDGE_2, (((tempFilesDir + "/GROUP=") + (TestGroups.EDGE_2)) + "/split-0"), elements, schemaUtils);
        WriteUnsortedDataTest.testContainsCorrectDataNoSplitPoints(EDGE, (((tempFilesDir + "/REVERSED-GROUP=") + (TestGroups.EDGE)) + "/split-0"), elements, schemaUtils);
        final List<Element> elementsWithSameSrcDstRemoved = elements.stream().filter(( e) -> e.getGroup().equals(TestGroups.EDGE_2)).map(( e) -> ((Edge) (e))).filter(( e) -> !(e.getSource().equals(e.getDestination()))).collect(Collectors.toList());
        WriteUnsortedDataTest.testContainsCorrectDataNoSplitPoints(EDGE_2, (((tempFilesDir + "/REVERSED-GROUP=") + (TestGroups.EDGE_2)) + "/split-0"), elementsWithSameSrcDstRemoved, schemaUtils);
    }

    @Test
    public void testOneSplitPointCase() throws IOException, OperationException {
        // Given
        final String tempFilesDir = testFolder.newFolder().getAbsolutePath();
        final SchemaUtils schemaUtils = new SchemaUtils(TestUtils.gafferSchema("schemaUsingLongVertexType"));
        final GraphPartitioner graphPartitioner = new GraphPartitioner();
        final List<Element> elements = new ArrayList<>();
        // TestGroups.ENTITY, split point is 10L. Create data with
        // VERTEX
        // 5L
        // 10L
        // 10L
        // 10L
        // 20L
        final List<PartitionKey> splitPointsEntity = new ArrayList<>();
        splitPointsEntity.add(new PartitionKey(new Object[]{ 10L }));
        graphPartitioner.addGroupPartitioner(ENTITY, new uk.gov.gchq.gaffer.parquetstore.partitioner.GroupPartitioner(TestGroups.ENTITY, splitPointsEntity));
        elements.add(createEntityForEntityGroup(5L));
        elements.add(createEntityForEntityGroup(10L));
        elements.add(createEntityForEntityGroup(10L));
        elements.add(createEntityForEntityGroup(10L));
        elements.add(createEntityForEntityGroup(20L));
        // TestGroups.ENTITY_2, split point is 100L. Create data with
        // VERTEX
        // 5L
        // 100L
        // 1000L
        final List<PartitionKey> splitPointsEntity_2 = new ArrayList<>();
        splitPointsEntity_2.add(new PartitionKey(new Object[]{ 100L }));
        graphPartitioner.addGroupPartitioner(ENTITY_2, new uk.gov.gchq.gaffer.parquetstore.partitioner.GroupPartitioner(TestGroups.ENTITY_2, splitPointsEntity_2));
        elements.add(WriteUnsortedDataTest.createEntityForEntityGroup_2(5L));
        elements.add(WriteUnsortedDataTest.createEntityForEntityGroup_2(100L));
        elements.add(WriteUnsortedDataTest.createEntityForEntityGroup_2(1000L));
        // TestGroups.EDGE, split point is [1000L, 200L, true]. Create data with
        // SOURCE   DESTINATION    DIRECTED
        // 5L         5000L        true
        // 5L         200L         false
        // 1000L         100L         true
        // 1000L         200L         false
        // 1000L         200L         true
        // 1000L         300L         true
        // 10000L         400L         false
        // 10000L         400L         true
        final List<PartitionKey> splitPointsEdge = new ArrayList<>();
        splitPointsEdge.add(new PartitionKey(new Object[]{ 1000L, 200L, true }));
        graphPartitioner.addGroupPartitioner(EDGE, new uk.gov.gchq.gaffer.parquetstore.partitioner.GroupPartitioner(TestGroups.EDGE, splitPointsEdge));
        final List<PartitionKey> splitPointsReversedEdge = new ArrayList<>();
        splitPointsReversedEdge.add(new PartitionKey(new Object[]{ 1000L, 300L, true }));
        graphPartitioner.addGroupPartitionerForReversedEdges(EDGE, new uk.gov.gchq.gaffer.parquetstore.partitioner.GroupPartitioner(TestGroups.EDGE, splitPointsReversedEdge));
        elements.add(WriteUnsortedDataTest.createEdgeForEdgeGroup(5L, 5000L, true));
        elements.add(WriteUnsortedDataTest.createEdgeForEdgeGroup(5L, 200L, false));
        elements.add(WriteUnsortedDataTest.createEdgeForEdgeGroup(1000L, 100L, true));
        elements.add(WriteUnsortedDataTest.createEdgeForEdgeGroup(1000L, 200L, false));
        elements.add(WriteUnsortedDataTest.createEdgeForEdgeGroup(1000L, 200L, true));
        elements.add(WriteUnsortedDataTest.createEdgeForEdgeGroup(1000L, 300L, true));
        elements.add(WriteUnsortedDataTest.createEdgeForEdgeGroup(10000L, 400L, false));
        elements.add(WriteUnsortedDataTest.createEdgeForEdgeGroup(10000L, 400L, true));
        // TestGroups.EDGE_2, split point is [10L, 2000L, true]. Create data with
        // SOURCE   DESTINATION    DIRECTED
        // 5L         5000L        true
        // 10L         2000L        false
        // 10L         2000L        true
        // 10L         3000L        false
        // 100L         1000L        true
        // 100L         3000L        false
        // 100L         3000L        true
        final List<PartitionKey> splitPointsEdge_2 = new ArrayList<>();
        splitPointsEdge_2.add(new PartitionKey(new Object[]{ 10L, 2000L, true }));
        graphPartitioner.addGroupPartitioner(EDGE_2, new uk.gov.gchq.gaffer.parquetstore.partitioner.GroupPartitioner(TestGroups.EDGE_2, splitPointsEdge_2));
        final List<PartitionKey> splitPointsReversedEdge_2 = new ArrayList<>();
        splitPointsReversedEdge_2.add(new PartitionKey(new Object[]{ 3000L, 20L, true }));
        graphPartitioner.addGroupPartitionerForReversedEdges(EDGE_2, new uk.gov.gchq.gaffer.parquetstore.partitioner.GroupPartitioner(TestGroups.EDGE_2, splitPointsReversedEdge_2));
        elements.add(WriteUnsortedDataTest.createEdgeForEdgeGroup_2(5L, 5000L, true));
        elements.add(WriteUnsortedDataTest.createEdgeForEdgeGroup_2(5L, 200L, false));
        elements.add(WriteUnsortedDataTest.createEdgeForEdgeGroup_2(1000L, 100L, true));
        elements.add(WriteUnsortedDataTest.createEdgeForEdgeGroup_2(1000L, 200L, false));
        elements.add(WriteUnsortedDataTest.createEdgeForEdgeGroup_2(1000L, 200L, true));
        elements.add(WriteUnsortedDataTest.createEdgeForEdgeGroup_2(1000L, 300L, true));
        elements.add(WriteUnsortedDataTest.createEdgeForEdgeGroup_2(10000L, 400L, false));
        elements.add(WriteUnsortedDataTest.createEdgeForEdgeGroup_2(10000L, 400L, true));
        final BiFunction<String, Integer, String> fileNameForGroupAndPartitionId = ( group, partitionId) -> (((tempFilesDir + "/GROUP=") + group) + "/split-") + partitionId;
        final BiFunction<String, Integer, String> fileNameForGroupAndPartitionIdForReversedEdge = ( group, partitionId) -> (((tempFilesDir + "/REVERSED-GROUP=") + group) + "/split-") + partitionId;
        final WriteUnsortedData writeUnsortedData = new WriteUnsortedData(tempFilesDir, CompressionCodecName.GZIP, schemaUtils, graphPartitioner, fileNameForGroupAndPartitionId, fileNameForGroupAndPartitionIdForReversedEdge);
        // When
        writeUnsortedData.writeElements(elements);
        // Then
        // - For each group, directories split0 and split1 should exist and each contain one file
        WriteUnsortedDataTest.testExistsAndContainsNFiles((((tempFilesDir + "/GROUP=") + (TestGroups.ENTITY)) + "/split-0"), 1);
        WriteUnsortedDataTest.testExistsAndContainsNFiles((((tempFilesDir + "/GROUP=") + (TestGroups.ENTITY)) + "/split-1"), 1);
        WriteUnsortedDataTest.testExistsAndContainsNFiles((((tempFilesDir + "/GROUP=") + (TestGroups.ENTITY_2)) + "/split-0"), 1);
        WriteUnsortedDataTest.testExistsAndContainsNFiles((((tempFilesDir + "/GROUP=") + (TestGroups.ENTITY_2)) + "/split-1"), 1);
        WriteUnsortedDataTest.testExistsAndContainsNFiles((((tempFilesDir + "/GROUP=") + (TestGroups.EDGE)) + "/split-0"), 1);
        WriteUnsortedDataTest.testExistsAndContainsNFiles((((tempFilesDir + "/GROUP=") + (TestGroups.EDGE)) + "/split-1"), 1);
        WriteUnsortedDataTest.testExistsAndContainsNFiles((((tempFilesDir + "/GROUP=") + (TestGroups.EDGE_2)) + "/split-0"), 1);
        WriteUnsortedDataTest.testExistsAndContainsNFiles((((tempFilesDir + "/GROUP=") + (TestGroups.EDGE_2)) + "/split-1"), 1);
        WriteUnsortedDataTest.testExistsAndContainsNFiles((((tempFilesDir + "/REVERSED-GROUP=") + (TestGroups.EDGE)) + "/split-0"), 1);
        WriteUnsortedDataTest.testExistsAndContainsNFiles((((tempFilesDir + "/REVERSED-GROUP=") + (TestGroups.EDGE)) + "/split-1"), 1);
        WriteUnsortedDataTest.testExistsAndContainsNFiles((((tempFilesDir + "/REVERSED-GROUP=") + (TestGroups.EDGE_2)) + "/split-0"), 1);
        WriteUnsortedDataTest.testExistsAndContainsNFiles((((tempFilesDir + "/REVERSED-GROUP=") + (TestGroups.EDGE_2)) + "/split-1"), 1);
        // - Each split file should contain the data for that split in the order it was written
        for (final String group : new java.util.HashSet(Arrays.asList(ENTITY, ENTITY_2))) {
            WriteUnsortedDataTest.testSplitFileContainsCorrectData((((tempFilesDir + "/GROUP=") + group) + "/split-0"), group, true, false, null, graphPartitioner.getGroupPartitioner(group).getIthPartitionKey(0), elements, schemaUtils);
            WriteUnsortedDataTest.testSplitFileContainsCorrectData((((tempFilesDir + "/GROUP=") + group) + "/split-1"), group, true, false, graphPartitioner.getGroupPartitioner(group).getIthPartitionKey(0), null, elements, schemaUtils);
        }
        for (final String group : new java.util.HashSet(Arrays.asList(EDGE, EDGE_2))) {
            WriteUnsortedDataTest.testSplitFileContainsCorrectData((((tempFilesDir + "/GROUP=") + group) + "/split-0"), group, false, false, null, graphPartitioner.getGroupPartitioner(group).getIthPartitionKey(0), elements, schemaUtils);
            WriteUnsortedDataTest.testSplitFileContainsCorrectData((((tempFilesDir + "/REVERSED-GROUP=") + group) + "/split-0"), group, false, true, null, graphPartitioner.getGroupPartitionerForReversedEdges(group).getIthPartitionKey(0), elements, schemaUtils);
            WriteUnsortedDataTest.testSplitFileContainsCorrectData((((tempFilesDir + "/GROUP=") + group) + "/split-1"), group, false, false, graphPartitioner.getGroupPartitioner(group).getIthPartitionKey(0), null, elements, schemaUtils);
            WriteUnsortedDataTest.testSplitFileContainsCorrectData((((tempFilesDir + "/REVERSED-GROUP=") + group) + "/split-1"), group, false, true, graphPartitioner.getGroupPartitionerForReversedEdges(group).getIthPartitionKey(0), null, elements, schemaUtils);
        }
    }

    @Test
    public void testMultipleSplitPointsCase() throws IOException, OperationException {
        // Given
        final String tempFilesDir = testFolder.newFolder().getAbsolutePath();
        final SchemaUtils schemaUtils = new SchemaUtils(TestUtils.gafferSchema("schemaUsingLongVertexType"));
        final GraphPartitioner graphPartitioner = new GraphPartitioner();
        final List<Element> elements = new ArrayList<>();
        // TestGroups.ENTITY, split points are 10L and 100L. Create data with
        // VERTEX
        // 5L
        // 10L
        // 10L
        // 11L
        // 12L
        // 100L
        // 100L
        // 200L
        final List<PartitionKey> splitPointsEntity = new ArrayList<>();
        splitPointsEntity.add(new PartitionKey(new Object[]{ 10L }));
        splitPointsEntity.add(new PartitionKey(new Object[]{ 100L }));
        graphPartitioner.addGroupPartitioner(ENTITY, new uk.gov.gchq.gaffer.parquetstore.partitioner.GroupPartitioner(TestGroups.ENTITY, splitPointsEntity));
        elements.add(createEntityForEntityGroup(5L));
        elements.add(createEntityForEntityGroup(10L));
        elements.add(createEntityForEntityGroup(10L));
        elements.add(createEntityForEntityGroup(11L));
        elements.add(createEntityForEntityGroup(12L));
        elements.add(createEntityForEntityGroup(100L));
        elements.add(createEntityForEntityGroup(100L));
        elements.add(createEntityForEntityGroup(200L));
        // TestGroups.ENTITY_2, split points are 100L and 1000L. Create data with
        // VERTEX
        // 5L
        // 100L
        // 200L
        // 1000L
        // 5000L
        final List<PartitionKey> splitPointsEntity_2 = new ArrayList<>();
        splitPointsEntity_2.add(new PartitionKey(new Object[]{ 100L }));
        splitPointsEntity_2.add(new PartitionKey(new Object[]{ 1000L }));
        graphPartitioner.addGroupPartitioner(ENTITY_2, new uk.gov.gchq.gaffer.parquetstore.partitioner.GroupPartitioner(TestGroups.ENTITY_2, splitPointsEntity_2));
        elements.add(WriteUnsortedDataTest.createEntityForEntityGroup_2(5L));
        elements.add(WriteUnsortedDataTest.createEntityForEntityGroup_2(100L));
        elements.add(WriteUnsortedDataTest.createEntityForEntityGroup_2(200L));
        elements.add(WriteUnsortedDataTest.createEntityForEntityGroup_2(1000L));
        elements.add(WriteUnsortedDataTest.createEntityForEntityGroup_2(5000L));
        // TestGroups.EDGE, split points are [1000L, 200L, true] and [1000L, 30000L, false]. Create data with
        // SOURCE   DESTINATION    DIRECTED
        // 5L        5000L         true
        // 5L         200L         false
        // 1000L         100L         true
        // 1000L       10000L         false
        // 1000L       30000L         false
        // 1000L      300000L         true
        // 10000L         400L         false
        final List<PartitionKey> splitPointsEdge = new ArrayList<>();
        splitPointsEdge.add(new PartitionKey(new Object[]{ 1000L, 200L, true }));
        splitPointsEdge.add(new PartitionKey(new Object[]{ 1000L, 30000L, false }));
        graphPartitioner.addGroupPartitioner(EDGE, new uk.gov.gchq.gaffer.parquetstore.partitioner.GroupPartitioner(TestGroups.EDGE, splitPointsEdge));
        final List<PartitionKey> splitPointsReversedEdge = new ArrayList<>();
        splitPointsReversedEdge.add(new PartitionKey(new Object[]{ 100L, 1000L, true }));
        splitPointsReversedEdge.add(new PartitionKey(new Object[]{ 300L, 2000L, false }));
        graphPartitioner.addGroupPartitionerForReversedEdges(EDGE, new uk.gov.gchq.gaffer.parquetstore.partitioner.GroupPartitioner(TestGroups.EDGE, splitPointsReversedEdge));
        elements.add(WriteUnsortedDataTest.createEdgeForEdgeGroup(5L, 5000L, true));
        elements.add(WriteUnsortedDataTest.createEdgeForEdgeGroup(5L, 200L, false));
        elements.add(WriteUnsortedDataTest.createEdgeForEdgeGroup(1000L, 90L, true));
        elements.add(WriteUnsortedDataTest.createEdgeForEdgeGroup(1000L, 10000L, false));
        elements.add(WriteUnsortedDataTest.createEdgeForEdgeGroup(1000L, 30000L, false));
        elements.add(WriteUnsortedDataTest.createEdgeForEdgeGroup(1000L, 300000L, true));
        elements.add(WriteUnsortedDataTest.createEdgeForEdgeGroup(10000L, 400L, false));
        // TestGroups.EDGE_2, split points are [10L, 2000L, true] and [100L, 1000L, false]. Create data with
        // SOURCE   DESTINATION    DIRECTED
        // 5L         5000L        true
        // 10L         2000L        false
        // 10L         2000L        true
        // 10L         3000L        false
        // 100L         1000L        false
        // 100L         3000L        false
        // 100L         3000L        true
        final List<PartitionKey> splitPointsEdge_2 = new ArrayList<>();
        splitPointsEdge_2.add(new PartitionKey(new Object[]{ 10L, 2000L, true }));
        splitPointsEdge_2.add(new PartitionKey(new Object[]{ 100L, 1000L, false }));
        graphPartitioner.addGroupPartitioner(EDGE_2, new uk.gov.gchq.gaffer.parquetstore.partitioner.GroupPartitioner(TestGroups.EDGE_2, splitPointsEdge_2));
        final List<PartitionKey> splitPointsReversedEdge_2 = new ArrayList<>();
        splitPointsReversedEdge_2.add(new PartitionKey(new Object[]{ 1000L, 1500L, true }));
        splitPointsReversedEdge_2.add(new PartitionKey(new Object[]{ 2000L, 2500L, false }));
        graphPartitioner.addGroupPartitionerForReversedEdges(EDGE_2, new uk.gov.gchq.gaffer.parquetstore.partitioner.GroupPartitioner(TestGroups.EDGE_2, splitPointsReversedEdge_2));
        elements.add(WriteUnsortedDataTest.createEdgeForEdgeGroup_2(5L, 5000L, true));
        elements.add(WriteUnsortedDataTest.createEdgeForEdgeGroup_2(10L, 2000L, false));
        elements.add(WriteUnsortedDataTest.createEdgeForEdgeGroup_2(10L, 2000L, true));
        elements.add(WriteUnsortedDataTest.createEdgeForEdgeGroup_2(10L, 3000L, false));
        elements.add(WriteUnsortedDataTest.createEdgeForEdgeGroup_2(100L, 1000L, false));
        elements.add(WriteUnsortedDataTest.createEdgeForEdgeGroup_2(100L, 3000L, false));
        elements.add(WriteUnsortedDataTest.createEdgeForEdgeGroup_2(100L, 3000L, true));
        final BiFunction<String, Integer, String> fileNameForGroupAndPartitionId = ( group, partitionId) -> (((tempFilesDir + "/GROUP=") + group) + "/split-") + partitionId;
        final BiFunction<String, Integer, String> fileNameForGroupAndPartitionIdForReversedEdge = ( group, partitionId) -> (((tempFilesDir + "/REVERSED-GROUP=") + group) + "/split-") + partitionId;
        final WriteUnsortedData writeUnsortedData = new WriteUnsortedData(tempFilesDir, CompressionCodecName.GZIP, schemaUtils, graphPartitioner, fileNameForGroupAndPartitionId, fileNameForGroupAndPartitionIdForReversedEdge);
        // When
        writeUnsortedData.writeElements(elements);
        // Then
        // - For each group, directories split0, split1 and split2 should exist and each contain one file
        WriteUnsortedDataTest.testExistsAndContainsNFiles((((tempFilesDir + "/GROUP=") + (TestGroups.ENTITY)) + "/split-0"), 1);
        WriteUnsortedDataTest.testExistsAndContainsNFiles((((tempFilesDir + "/GROUP=") + (TestGroups.ENTITY)) + "/split-1"), 1);
        WriteUnsortedDataTest.testExistsAndContainsNFiles((((tempFilesDir + "/GROUP=") + (TestGroups.ENTITY)) + "/split-2"), 1);
        WriteUnsortedDataTest.testExistsAndContainsNFiles((((tempFilesDir + "/GROUP=") + (TestGroups.ENTITY_2)) + "/split-0"), 1);
        WriteUnsortedDataTest.testExistsAndContainsNFiles((((tempFilesDir + "/GROUP=") + (TestGroups.ENTITY_2)) + "/split-1"), 1);
        WriteUnsortedDataTest.testExistsAndContainsNFiles((((tempFilesDir + "/GROUP=") + (TestGroups.ENTITY_2)) + "/split-2"), 1);
        WriteUnsortedDataTest.testExistsAndContainsNFiles((((tempFilesDir + "/GROUP=") + (TestGroups.EDGE)) + "/split-0"), 1);
        WriteUnsortedDataTest.testExistsAndContainsNFiles((((tempFilesDir + "/GROUP=") + (TestGroups.EDGE)) + "/split-1"), 1);
        WriteUnsortedDataTest.testExistsAndContainsNFiles((((tempFilesDir + "/GROUP=") + (TestGroups.EDGE)) + "/split-2"), 1);
        WriteUnsortedDataTest.testExistsAndContainsNFiles((((tempFilesDir + "/GROUP=") + (TestGroups.EDGE_2)) + "/split-0"), 1);
        WriteUnsortedDataTest.testExistsAndContainsNFiles((((tempFilesDir + "/GROUP=") + (TestGroups.EDGE_2)) + "/split-1"), 1);
        WriteUnsortedDataTest.testExistsAndContainsNFiles((((tempFilesDir + "/GROUP=") + (TestGroups.EDGE_2)) + "/split-2"), 1);
        // - Each split file should contain the data for that split in the order it was written
        for (final String group : new java.util.HashSet(Arrays.asList(ENTITY, ENTITY_2))) {
            WriteUnsortedDataTest.testSplitFileContainsCorrectData((((tempFilesDir + "/GROUP=") + group) + "/split-0"), group, true, false, null, graphPartitioner.getGroupPartitioner(group).getIthPartitionKey(0), elements, schemaUtils);
            WriteUnsortedDataTest.testSplitFileContainsCorrectData((((tempFilesDir + "/GROUP=") + group) + "/split-1"), group, true, false, graphPartitioner.getGroupPartitioner(group).getIthPartitionKey(0), graphPartitioner.getGroupPartitioner(group).getIthPartitionKey(1), elements, schemaUtils);
            WriteUnsortedDataTest.testSplitFileContainsCorrectData((((tempFilesDir + "/GROUP=") + group) + "/split-2"), group, true, false, graphPartitioner.getGroupPartitioner(group).getIthPartitionKey(1), null, elements, schemaUtils);
        }
        for (final String group : new java.util.HashSet(Arrays.asList(EDGE, EDGE_2))) {
            WriteUnsortedDataTest.testSplitFileContainsCorrectData((((tempFilesDir + "/GROUP=") + group) + "/split-0"), group, false, false, null, graphPartitioner.getGroupPartitioner(group).getIthPartitionKey(0), elements, schemaUtils);
            WriteUnsortedDataTest.testSplitFileContainsCorrectData((((tempFilesDir + "/REVERSED-GROUP=") + group) + "/split-0"), group, false, true, null, graphPartitioner.getGroupPartitionerForReversedEdges(group).getIthPartitionKey(0), elements, schemaUtils);
            WriteUnsortedDataTest.testSplitFileContainsCorrectData((((tempFilesDir + "/GROUP=") + group) + "/split-1"), group, false, false, graphPartitioner.getGroupPartitioner(group).getIthPartitionKey(0), graphPartitioner.getGroupPartitioner(group).getIthPartitionKey(1), elements, schemaUtils);
            WriteUnsortedDataTest.testSplitFileContainsCorrectData((((tempFilesDir + "/REVERSED-GROUP=") + group) + "/split-1"), group, false, true, graphPartitioner.getGroupPartitionerForReversedEdges(group).getIthPartitionKey(0), graphPartitioner.getGroupPartitionerForReversedEdges(group).getIthPartitionKey(1), elements, schemaUtils);
            WriteUnsortedDataTest.testSplitFileContainsCorrectData((((tempFilesDir + "/GROUP=") + group) + "/split-2"), group, false, false, graphPartitioner.getGroupPartitioner(group).getIthPartitionKey(1), null, elements, schemaUtils);
            WriteUnsortedDataTest.testSplitFileContainsCorrectData((((tempFilesDir + "/REVERSED-GROUP=") + group) + "/split-2"), group, false, true, graphPartitioner.getGroupPartitionerForReversedEdges(group).getIthPartitionKey(1), null, elements, schemaUtils);
        }
    }
}

