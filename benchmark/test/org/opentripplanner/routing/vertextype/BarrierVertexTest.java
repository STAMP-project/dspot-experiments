package org.opentripplanner.routing.vertextype;


import BarrierVertex.defaultBarrierPermissions;
import StreetTraversalPermission.NONE;
import StreetTraversalPermission.PEDESTRIAN;
import StreetTraversalPermission.PEDESTRIAN_AND_BICYCLE;
import TraverseMode.BICYCLE;
import TraverseMode.CAR;
import TraverseMode.WALK;
import org.junit.Assert;
import org.junit.Test;
import org.opentripplanner.graph_builder.module.osm.OSMFilter;
import org.opentripplanner.openstreetmap.model.OSMNode;
import org.opentripplanner.routing.core.TraverseModeSet;
import org.opentripplanner.routing.edgetype.StreetEdge;
import org.opentripplanner.routing.graph.Graph;


/**
 * Created by mabu on 17.8.2015.
 */
public class BarrierVertexTest {
    @Test
    public void testBarrierPermissions() throws Exception {
        OSMNode simpleBarier = new OSMNode();
        Assert.assertFalse(simpleBarier.isBollard());
        simpleBarier.addTag("barrier", "bollard");
        Assert.assertTrue(simpleBarier.isBollard());
        Graph graph = new Graph();
        String label = "simpleBarrier";
        BarrierVertex bv = new BarrierVertex(graph, label, simpleBarier.lon, simpleBarier.lat, 0);
        bv.setBarrierPermissions(OSMFilter.getPermissionsForEntity(simpleBarier, defaultBarrierPermissions));
        Assert.assertEquals(PEDESTRIAN_AND_BICYCLE, bv.getBarrierPermissions());
        simpleBarier.addTag("foot", "yes");
        bv.setBarrierPermissions(OSMFilter.getPermissionsForEntity(simpleBarier, defaultBarrierPermissions));
        Assert.assertEquals(PEDESTRIAN_AND_BICYCLE, bv.getBarrierPermissions());
        simpleBarier.addTag("bicycle", "yes");
        bv.setBarrierPermissions(OSMFilter.getPermissionsForEntity(simpleBarier, defaultBarrierPermissions));
        Assert.assertEquals(PEDESTRIAN_AND_BICYCLE, bv.getBarrierPermissions());
        simpleBarier.addTag("access", "no");
        bv.setBarrierPermissions(OSMFilter.getPermissionsForEntity(simpleBarier, defaultBarrierPermissions));
        Assert.assertEquals(PEDESTRIAN_AND_BICYCLE, bv.getBarrierPermissions());
        simpleBarier.addTag("motor_vehicle", "no");
        bv.setBarrierPermissions(OSMFilter.getPermissionsForEntity(simpleBarier, defaultBarrierPermissions));
        Assert.assertEquals(PEDESTRIAN_AND_BICYCLE, bv.getBarrierPermissions());
        simpleBarier.addTag("bicycle", "no");
        bv.setBarrierPermissions(OSMFilter.getPermissionsForEntity(simpleBarier, defaultBarrierPermissions));
        Assert.assertEquals(PEDESTRIAN, bv.getBarrierPermissions());
        OSMNode complexBarrier = new OSMNode();
        complexBarrier.addTag("barrier", "bollard");
        complexBarrier.addTag("access", "no");
        bv.setBarrierPermissions(OSMFilter.getPermissionsForEntity(complexBarrier, defaultBarrierPermissions));
        Assert.assertEquals(NONE, bv.getBarrierPermissions());
        OSMNode noBikeBollard = new OSMNode();
        noBikeBollard.addTag("barrier", "bollard");
        noBikeBollard.addTag("bicycle", "no");
        bv.setBarrierPermissions(OSMFilter.getPermissionsForEntity(noBikeBollard, defaultBarrierPermissions));
        Assert.assertEquals(PEDESTRIAN, bv.getBarrierPermissions());
    }

    @Test
    public void testStreetsWithBollard() {
        Graph _graph = new Graph();
        // default permissions are PEDESTRIAND and BICYCLE
        BarrierVertex bv = new BarrierVertex(_graph, "start_bollard", 2.0, 2.0, 0);
        StreetVertex endVertex = new IntersectionVertex(_graph, "end_vertex", 1.0, 2.0);
        StreetEdge bv_to_endVertex_forward = edge(bv, endVertex, 100, false);
        Assert.assertTrue(bv_to_endVertex_forward.canTraverse(new TraverseModeSet("CAR")));
        Assert.assertTrue(bv_to_endVertex_forward.canTraverse(new TraverseModeSet("BICYCLE")));
        Assert.assertTrue(bv_to_endVertex_forward.canTraverse(new TraverseModeSet("WALK")));
        Assert.assertFalse(bv_to_endVertex_forward.canTraverseIncludingBarrier(CAR));
        Assert.assertTrue(bv_to_endVertex_forward.canTraverseIncludingBarrier(BICYCLE));
        Assert.assertTrue(bv_to_endVertex_forward.canTraverseIncludingBarrier(WALK));
        StreetEdge endVertex_to_bv_backward = edge(endVertex, bv, 100, true);
        Assert.assertTrue(endVertex_to_bv_backward.canTraverse(new TraverseModeSet("CAR")));
        Assert.assertTrue(endVertex_to_bv_backward.canTraverse(new TraverseModeSet("BICYCLE")));
        Assert.assertTrue(endVertex_to_bv_backward.canTraverse(new TraverseModeSet("WALK")));
        Assert.assertFalse(endVertex_to_bv_backward.canTraverseIncludingBarrier(CAR));
        Assert.assertTrue(endVertex_to_bv_backward.canTraverseIncludingBarrier(BICYCLE));
        Assert.assertTrue(endVertex_to_bv_backward.canTraverseIncludingBarrier(WALK));
        StreetEdge bv_to_endVertex_backward = edge(bv, endVertex, 100, true);
        Assert.assertTrue(bv_to_endVertex_backward.canTraverse(new TraverseModeSet("CAR")));
        Assert.assertTrue(bv_to_endVertex_backward.canTraverse(new TraverseModeSet("BICYCLE")));
        Assert.assertTrue(bv_to_endVertex_backward.canTraverse(new TraverseModeSet("WALK")));
        Assert.assertFalse(bv_to_endVertex_backward.canTraverseIncludingBarrier(CAR));
        Assert.assertTrue(bv_to_endVertex_backward.canTraverseIncludingBarrier(BICYCLE));
        Assert.assertTrue(bv_to_endVertex_backward.canTraverseIncludingBarrier(WALK));
        StreetEdge endVertex_to_bv_forward = edge(endVertex, bv, 100, false);
        Assert.assertTrue(endVertex_to_bv_forward.canTraverse(new TraverseModeSet("CAR")));
        Assert.assertTrue(endVertex_to_bv_forward.canTraverse(new TraverseModeSet("BICYCLE")));
        Assert.assertTrue(endVertex_to_bv_forward.canTraverse(new TraverseModeSet("WALK")));
        Assert.assertFalse(endVertex_to_bv_forward.canTraverseIncludingBarrier(CAR));
        Assert.assertTrue(endVertex_to_bv_forward.canTraverseIncludingBarrier(BICYCLE));
        Assert.assertTrue(endVertex_to_bv_forward.canTraverseIncludingBarrier(WALK));
        // tests bollard which doesn't allow cycling
        BarrierVertex noBicycleBollard = new BarrierVertex(_graph, "no_bike_bollard", 1.5, 1, 0);
        noBicycleBollard.setBarrierPermissions(PEDESTRIAN);
        StreetEdge no_bike_to_endVertex = edge(noBicycleBollard, endVertex, 100, false);
        Assert.assertTrue(no_bike_to_endVertex.canTraverse(new TraverseModeSet("CAR")));
        Assert.assertTrue(no_bike_to_endVertex.canTraverse(new TraverseModeSet("BICYCLE")));
        Assert.assertTrue(no_bike_to_endVertex.canTraverse(new TraverseModeSet("WALK")));
        Assert.assertFalse(no_bike_to_endVertex.canTraverseIncludingBarrier(CAR));
        Assert.assertFalse(no_bike_to_endVertex.canTraverseIncludingBarrier(BICYCLE));
        Assert.assertTrue(no_bike_to_endVertex.canTraverseIncludingBarrier(WALK));
    }
}

