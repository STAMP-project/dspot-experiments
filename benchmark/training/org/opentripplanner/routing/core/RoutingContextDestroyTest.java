package org.opentripplanner.routing.core;


import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.locationtech.jts.geom.GeometryFactory;
import org.opentripplanner.common.geometry.GeometryUtils;
import org.opentripplanner.common.model.GenericLocation;
import org.opentripplanner.routing.graph.Edge;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.graph.Vertex;
import org.opentripplanner.routing.vertextype.StreetVertex;


public class RoutingContextDestroyTest {
    private final GeometryFactory gf = GeometryUtils.getGeometryFactory();

    private RoutingContext subject;

    // Given:
    // - a graph with 3 intersections/vertexes
    private final Graph g = new Graph();

    private final StreetVertex a = new org.opentripplanner.routing.vertextype.IntersectionVertex(g, "A", 1.0, 1.0);

    private final StreetVertex b = new org.opentripplanner.routing.vertextype.IntersectionVertex(g, "B", 0.0, 1.0);

    private final StreetVertex c = new org.opentripplanner.routing.vertextype.IntersectionVertex(g, "C", 1.0, 0.0);

    private final List<Vertex> permanentVertexes = Arrays.asList(a, b, c);

    // - And travel *origin* is 0,4 degrees on the road from B to A
    private final GenericLocation from = new GenericLocation(1.0, 0.4);

    // - and *destination* is slightly off 0.7 degrees on road from C to A
    private final GenericLocation to = new GenericLocation(0.701, 1.001);

    @Test
    public void temporaryChangesRemovedOnContextDestroy() {
        // Given - A request
        RoutingRequest request = new RoutingRequest();
        request.from = from;
        request.to = to;
        // When - the context is created
        subject = new RoutingContext(request, g);
        // Then:
        originAndDestinationInsertedCorrect();
        // And When:
        subject.destroy();
        // Then - permanent vertexes
        for (Vertex v : permanentVertexes) {
            // - does not reference the any temporary nodes any more
            for (Edge e : v.getIncoming()) {
                assertVertexEdgeIsNotReferencingTemporaryElements(v, e, e.getFromVertex());
            }
            for (Edge e : v.getOutgoing()) {
                assertVertexEdgeIsNotReferencingTemporaryElements(v, e, e.getToVertex());
            }
        }
    }
}

