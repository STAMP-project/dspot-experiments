package com.thinkaurelius.titan.hadoop;


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
import com.thinkaurelius.titan.core.EdgeLabel;
import com.thinkaurelius.titan.core.PropertyKey;
import com.thinkaurelius.titan.core.RelationType;
import com.thinkaurelius.titan.core.TitanVertex;
import com.thinkaurelius.titan.core.schema.RelationTypeIndex;
import com.thinkaurelius.titan.core.schema.TitanGraphIndex;
import com.thinkaurelius.titan.core.schema.TitanManagement;
import com.thinkaurelius.titan.diskstorage.BackendException;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.scan.ScanMetrics;
import com.thinkaurelius.titan.example.GraphOfTheGodsFactory;
import com.thinkaurelius.titan.graphdb.TitanGraphBaseTest;
import com.thinkaurelius.titan.graphdb.database.management.ManagementSystem;
import java.util.concurrent.ExecutionException;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractIndexManagementIT extends TitanGraphBaseTest {
    @Test
    public void testRemoveGraphIndex() throws BackendException, InterruptedException, ExecutionException {
        tx.commit();
        mgmt.commit();
        // Load the "Graph of the Gods" sample data
        GraphOfTheGodsFactory.loadWithoutMixedIndex(graph, true);
        // Disable the "name" composite index
        TitanManagement m = graph.openManagement();
        TitanGraphIndex nameIndex = m.getGraphIndex("name");
        m.updateIndex(nameIndex, DISABLE_INDEX);
        m.commit();
        graph.tx().commit();
        // Block until the SchemaStatus transitions to DISABLED
        Assert.assertTrue(ManagementSystem.awaitGraphIndexStatus(graph, "name").status(DISABLED).call().getSucceeded());
        // Remove index
        MapReduceIndexManagement mri = new MapReduceIndexManagement(graph);
        m = graph.openManagement();
        TitanGraphIndex index = m.getGraphIndex("name");
        ScanMetrics metrics = mri.updateIndex(index, REMOVE_INDEX).get();
        Assert.assertEquals(12, metrics.getCustom(DELETED_RECORDS_COUNT));
    }

    @Test
    public void testRemoveRelationIndex() throws BackendException, InterruptedException, ExecutionException {
        tx.commit();
        mgmt.commit();
        // Load the "Graph of the Gods" sample data
        GraphOfTheGodsFactory.loadWithoutMixedIndex(graph, true);
        // Disable the "battlesByTime" index
        TitanManagement m = graph.openManagement();
        RelationType battled = m.getRelationType("battled");
        RelationTypeIndex battlesByTime = m.getRelationIndex(battled, "battlesByTime");
        m.updateIndex(battlesByTime, DISABLE_INDEX);
        m.commit();
        graph.tx().commit();
        // Block until the SchemaStatus transitions to DISABLED
        Assert.assertTrue(ManagementSystem.awaitRelationIndexStatus(graph, "battlesByTime", "battled").status(DISABLED).call().getSucceeded());
        // Remove index
        MapReduceIndexManagement mri = new MapReduceIndexManagement(graph);
        m = graph.openManagement();
        battled = m.getRelationType("battled");
        battlesByTime = m.getRelationIndex(battled, "battlesByTime");
        ScanMetrics metrics = mri.updateIndex(battlesByTime, REMOVE_INDEX).get();
        Assert.assertEquals(6, metrics.getCustom(DELETED_RECORDS_COUNT));
    }

    @Test
    public void testRepairGraphIndex() throws BackendException, InterruptedException, ExecutionException {
        tx.commit();
        mgmt.commit();
        // Load the "Graph of the Gods" sample data (WITHOUT mixed index coverage)
        GraphOfTheGodsFactory.loadWithoutMixedIndex(graph, true);
        // Create and enable a graph index on age
        TitanManagement m = graph.openManagement();
        PropertyKey age = m.getPropertyKey("age");
        m.buildIndex("verticesByAge", Vertex.class).addKey(age).buildCompositeIndex();
        m.commit();
        graph.tx().commit();
        // Block until the SchemaStatus transitions to REGISTERED
        Assert.assertTrue(ManagementSystem.awaitGraphIndexStatus(graph, "verticesByAge").status(REGISTERED).call().getSucceeded());
        m = graph.openManagement();
        TitanGraphIndex index = m.getGraphIndex("verticesByAge");
        m.updateIndex(index, ENABLE_INDEX);
        m.commit();
        graph.tx().commit();
        // Block until the SchemaStatus transitions to ENABLED
        Assert.assertTrue(ManagementSystem.awaitGraphIndexStatus(graph, "verticesByAge").status(ENABLED).call().getSucceeded());
        // Run a query that hits the index but erroneously returns nothing because we haven't repaired yet
        Assert.assertFalse(graph.query().has("age", 10000).vertices().iterator().hasNext());
        // Repair
        MapReduceIndexManagement mri = new MapReduceIndexManagement(graph);
        m = graph.openManagement();
        index = m.getGraphIndex("verticesByAge");
        ScanMetrics metrics = mri.updateIndex(index, REINDEX).get();
        Assert.assertEquals(6, metrics.getCustom(ADDED_RECORDS_COUNT));
        // Test the index
        Iterable<TitanVertex> hits = graph.query().has("age", 4500).vertices();
        Assert.assertNotNull(hits);
        Assert.assertEquals(1, Iterables.size(hits));
        TitanVertex v = Iterables.getOnlyElement(hits);
        Assert.assertNotNull(v);
        Assert.assertEquals("neptune", v.value("name"));
    }

    @Test
    public void testRepairRelationIndex() throws BackendException, InterruptedException, ExecutionException {
        tx.commit();
        mgmt.commit();
        // Load the "Graph of the Gods" sample data (WITHOUT mixed index coverage)
        GraphOfTheGodsFactory.loadWithoutMixedIndex(graph, true);
        // Create and enable a relation index on lives edges by reason
        TitanManagement m = graph.openManagement();
        PropertyKey reason = m.getPropertyKey("reason");
        EdgeLabel lives = m.getEdgeLabel("lives");
        m.buildEdgeIndex(lives, "livesByReason", BOTH, decr, reason);
        m.commit();
        graph.tx().commit();
        // Block until the SchemaStatus transitions to REGISTERED
        Assert.assertTrue(ManagementSystem.awaitRelationIndexStatus(graph, "livesByReason", "lives").status(REGISTERED).call().getSucceeded());
        m = graph.openManagement();
        RelationTypeIndex index = m.getRelationIndex(m.getRelationType("lives"), "livesByReason");
        m.updateIndex(index, ENABLE_INDEX);
        m.commit();
        graph.tx().commit();
        // Block until the SchemaStatus transitions to ENABLED
        Assert.assertTrue(ManagementSystem.awaitRelationIndexStatus(graph, "livesByReason", "lives").status(ENABLED).call().getSucceeded());
        // Run a query that hits the index but erroneously returns nothing because we haven't repaired yet
        // assertFalse(graph.query().has("reason", "no fear of death").edges().iterator().hasNext());
        // Repair
        MapReduceIndexManagement mri = new MapReduceIndexManagement(graph);
        m = graph.openManagement();
        index = m.getRelationIndex(m.getRelationType("lives"), "livesByReason");
        ScanMetrics metrics = mri.updateIndex(index, REINDEX).get();
        Assert.assertEquals(8, metrics.getCustom(ADDED_RECORDS_COUNT));
    }
}

