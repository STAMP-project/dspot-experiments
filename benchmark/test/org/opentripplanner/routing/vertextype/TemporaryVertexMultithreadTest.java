package org.opentripplanner.routing.vertextype;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import org.junit.Assert;
import org.junit.Test;
import org.opentripplanner.routing.core.RoutingRequest;
import org.opentripplanner.routing.core.State;
import org.opentripplanner.routing.graph.Edge;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.graph.Vertex;


public class TemporaryVertexMultithreadTest {
    private static final double ANY_LOC = 1;

    @Test
    public void testTemporaryVertexOnOtherThreadUnreachable() throws InterruptedException, ExecutionException {
        Graph graph = new Graph();
        Vertex a = new TemporaryVertexMultithreadTest.V(graph, "A");
        Vertex b = new TemporaryVertexMultithreadTest.V(graph, "B");
        Vertex c = new TemporaryVertexMultithreadTest.TempVertex(graph, "C");
        Vertex d = new TemporaryVertexMultithreadTest.V(graph, "D");
        Vertex e = new TemporaryVertexMultithreadTest.V(graph, "E");
        new org.opentripplanner.routing.edgetype.FreeEdge(a, b);
        Edge edgeToTemporaryVertex = new org.opentripplanner.routing.edgetype.FreeEdge(b, c);
        new org.opentripplanner.routing.edgetype.FreeEdge(c, d);
        new org.opentripplanner.routing.edgetype.FreeEdge(d, e);
        RoutingRequest options1 = new RoutingRequest();
        options1.setRoutingContext(graph, a, e);
        options1.rctx.temporaryVertices.add(c);
        State init1 = new State(b, options1);
        Assert.assertNotNull(edgeToTemporaryVertex.traverse(init1));
        FutureTask<State> otherThreadState = new FutureTask(() -> {
            RoutingRequest options2 = new RoutingRequest();
            options2.setRoutingContext(graph, a, e);
            State init2 = new State(b, options2);
            return edgeToTemporaryVertex.traverse(init2);
        });
        new Thread(otherThreadState).start();
        Assert.assertNull(otherThreadState.get());
    }

    private class V extends Vertex {
        private V(Graph graph, String label) {
            super(graph, label, TemporaryVertexMultithreadTest.ANY_LOC, TemporaryVertexMultithreadTest.ANY_LOC);
        }

        @Override
        public String toString() {
            return getLabel();
        }
    }

    private class TempVertex extends TemporaryVertexMultithreadTest.V implements TemporaryVertex {
        private TempVertex(Graph graph, String label) {
            super(graph, label);
        }

        @Override
        public boolean isEndVertex() {
            throw new IllegalStateException("The `isEndVertex` is not used by dispose logic.");
        }
    }
}

