/**
 * Copyright 2017 JanusGraph Authors
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package org.janusgraph.hadoop;


import Direction.BOTH;
import IndexRemoveJob.DELETED_RECORDS_COUNT;
import IndexRepairJob.ADDED_RECORDS_COUNT;
import Order.decr;
import SchemaAction.DISABLE_INDEX;
import SchemaAction.ENABLE_INDEX;
import SchemaAction.REINDEX;
import SchemaAction.REMOVE_INDEX;
import SchemaStatus.DISABLED;
import SchemaStatus.ENABLED;
import SchemaStatus.REGISTERED;
import com.google.common.collect.Iterables;
import java.util.concurrent.ExecutionException;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.EdgeLabel;
import org.janusgraph.core.JanusGraphVertex;
import org.janusgraph.core.PropertyKey;
import org.janusgraph.core.RelationType;
import org.janusgraph.core.schema.JanusGraphIndex;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.janusgraph.core.schema.RelationTypeIndex;
import org.janusgraph.diskstorage.BackendException;
import org.janusgraph.diskstorage.keycolumnvalue.scan.ScanMetrics;
import org.janusgraph.example.GraphOfTheGodsFactory;
import org.janusgraph.graphdb.JanusGraphBaseTest;
import org.janusgraph.graphdb.database.management.ManagementSystem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public abstract class AbstractIndexManagementIT extends JanusGraphBaseTest {
    @Test
    public void testRemoveGraphIndex() throws InterruptedException, ExecutionException, BackendException {
        tx.commit();
        mgmt.commit();
        // Load the "Graph of the Gods" sample data
        GraphOfTheGodsFactory.loadWithoutMixedIndex(graph, true);
        // Disable the "name" composite index
        JanusGraphManagement m = graph.openManagement();
        JanusGraphIndex nameIndex = m.getGraphIndex("name");
        m.updateIndex(nameIndex, DISABLE_INDEX);
        m.commit();
        graph.tx().commit();
        // Block until the SchemaStatus transitions to DISABLED
        Assertions.assertTrue(ManagementSystem.awaitGraphIndexStatus(graph, "name").status(DISABLED).call().getSucceeded());
        // Remove index
        MapReduceIndexManagement mri = new MapReduceIndexManagement(graph);
        m = graph.openManagement();
        JanusGraphIndex index = m.getGraphIndex("name");
        ScanMetrics metrics = mri.updateIndex(index, REMOVE_INDEX).get();
        Assertions.assertEquals(12, metrics.getCustom(DELETED_RECORDS_COUNT));
    }

    @Test
    public void testRemoveRelationIndex() throws InterruptedException, ExecutionException, BackendException {
        tx.commit();
        mgmt.commit();
        // Load the "Graph of the Gods" sample data
        GraphOfTheGodsFactory.loadWithoutMixedIndex(graph, true);
        // Disable the "battlesByTime" index
        JanusGraphManagement m = graph.openManagement();
        RelationType battled = m.getRelationType("battled");
        RelationTypeIndex battlesByTime = m.getRelationIndex(battled, "battlesByTime");
        m.updateIndex(battlesByTime, DISABLE_INDEX);
        m.commit();
        graph.tx().commit();
        // Block until the SchemaStatus transitions to DISABLED
        Assertions.assertTrue(ManagementSystem.awaitRelationIndexStatus(graph, "battlesByTime", "battled").status(DISABLED).call().getSucceeded());
        // Remove index
        MapReduceIndexManagement mri = new MapReduceIndexManagement(graph);
        m = graph.openManagement();
        battled = m.getRelationType("battled");
        battlesByTime = m.getRelationIndex(battled, "battlesByTime");
        ScanMetrics metrics = mri.updateIndex(battlesByTime, REMOVE_INDEX).get();
        Assertions.assertEquals(6, metrics.getCustom(DELETED_RECORDS_COUNT));
    }

    @Test
    public void testRepairGraphIndex() throws InterruptedException, ExecutionException, BackendException {
        tx.commit();
        mgmt.commit();
        // Load the "Graph of the Gods" sample data (WITHOUT mixed index coverage)
        GraphOfTheGodsFactory.loadWithoutMixedIndex(graph, true);
        // Create and enable a graph index on age
        JanusGraphManagement m = graph.openManagement();
        PropertyKey age = m.getPropertyKey("age");
        m.buildIndex("verticesByAge", Vertex.class).addKey(age).buildCompositeIndex();
        m.commit();
        graph.tx().commit();
        // Block until the SchemaStatus transitions to REGISTERED
        Assertions.assertTrue(ManagementSystem.awaitGraphIndexStatus(graph, "verticesByAge").status(REGISTERED).call().getSucceeded());
        m = graph.openManagement();
        JanusGraphIndex index = m.getGraphIndex("verticesByAge");
        m.updateIndex(index, ENABLE_INDEX);
        m.commit();
        graph.tx().commit();
        // Block until the SchemaStatus transitions to ENABLED
        Assertions.assertTrue(ManagementSystem.awaitGraphIndexStatus(graph, "verticesByAge").status(ENABLED).call().getSucceeded());
        // Run a query that hits the index but erroneously returns nothing because we haven't repaired yet
        Assertions.assertFalse(graph.query().has("age", 10000).vertices().iterator().hasNext());
        // Repair
        MapReduceIndexManagement mri = new MapReduceIndexManagement(graph);
        m = graph.openManagement();
        index = m.getGraphIndex("verticesByAge");
        ScanMetrics metrics = mri.updateIndex(index, REINDEX).get();
        Assertions.assertEquals(6, metrics.getCustom(ADDED_RECORDS_COUNT));
        // Test the index
        Iterable<JanusGraphVertex> hits = graph.query().has("age", 4500).vertices();
        Assertions.assertNotNull(hits);
        Assertions.assertEquals(1, Iterables.size(hits));
        JanusGraphVertex v = Iterables.getOnlyElement(hits);
        Assertions.assertNotNull(v);
        Assertions.assertEquals("neptune", v.value("name"));
    }

    @Test
    public void testRepairRelationIndex() throws InterruptedException, ExecutionException, BackendException {
        tx.commit();
        mgmt.commit();
        // Load the "Graph of the Gods" sample data (WITHOUT mixed index coverage)
        GraphOfTheGodsFactory.loadWithoutMixedIndex(graph, true);
        // Create and enable a relation index on lives edges by reason
        JanusGraphManagement m = graph.openManagement();
        PropertyKey reason = m.getPropertyKey("reason");
        EdgeLabel lives = m.getEdgeLabel("lives");
        m.buildEdgeIndex(lives, "livesByReason", BOTH, decr, reason);
        m.commit();
        graph.tx().commit();
        // Block until the SchemaStatus transitions to REGISTERED
        Assertions.assertTrue(ManagementSystem.awaitRelationIndexStatus(graph, "livesByReason", "lives").status(REGISTERED).call().getSucceeded());
        m = graph.openManagement();
        RelationTypeIndex index = m.getRelationIndex(m.getRelationType("lives"), "livesByReason");
        m.updateIndex(index, ENABLE_INDEX);
        m.commit();
        graph.tx().commit();
        // Block until the SchemaStatus transitions to ENABLED
        Assertions.assertTrue(ManagementSystem.awaitRelationIndexStatus(graph, "livesByReason", "lives").status(ENABLED).call().getSucceeded());
        // Run a query that hits the index but erroneously returns nothing because we haven't repaired yet
        // assertFalse(graph.query().has("reason", "no fear of death").edges().iterator().hasNext());
        // Repair
        MapReduceIndexManagement mri = new MapReduceIndexManagement(graph);
        m = graph.openManagement();
        index = m.getRelationIndex(m.getRelationType("lives"), "livesByReason");
        ScanMetrics metrics = mri.updateIndex(index, REINDEX).get();
        Assertions.assertEquals(8, metrics.getCustom(ADDED_RECORDS_COUNT));
    }
}

