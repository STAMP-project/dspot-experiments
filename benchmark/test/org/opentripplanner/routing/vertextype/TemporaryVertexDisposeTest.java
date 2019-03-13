package org.opentripplanner.routing.vertextype;


import org.junit.Assert;
import org.junit.Test;
import org.opentripplanner.routing.edgetype.FreeEdge;
import org.opentripplanner.routing.graph.Vertex;


public class TemporaryVertexDisposeTest {
    private static final double ANY_LOC = 1;

    // Given a very simple graph: A -> B
    private final Vertex a = new TemporaryVertexDisposeTest.V("A");

    private final Vertex b = new TemporaryVertexDisposeTest.V("B");

    {
        TemporaryVertexDisposeTest.edge(a, b);
        assertOriginalGraphIsIntact();
    }

    @Test
    public void disposeNormalCase() {
        // Given a temporary vertex 'origin' and 'destination' connected to graph
        Vertex origin = new TemporaryVertexDisposeTest.TempVertex("origin");
        Vertex destination = new TemporaryVertexDisposeTest.TempVertex("dest");
        TemporaryVertexDisposeTest.edge(origin, a);
        TemporaryVertexDisposeTest.edge(b, destination);
        // Then before we dispose temporary vertexes
        Assert.assertEquals("[origin->A]", a.getIncoming().toString());
        Assert.assertEquals("[B->dest]", b.getOutgoing().toString());
        // When
        TemporaryVertex.dispose(origin);
        TemporaryVertex.dispose(destination);
        // Then
        assertOriginalGraphIsIntact();
    }

    @Test
    public void disposeShouldNotDeleteOtherIncomingEdges() {
        // Given a temporary vertex 'origin' connected to B - has other incoming edges
        Vertex origin = new TemporaryVertexDisposeTest.TempVertex("origin");
        Vertex otherTemp = new TemporaryVertexDisposeTest.TempVertex("OT");
        TemporaryVertexDisposeTest.edge(origin, b);
        TemporaryVertexDisposeTest.edge(otherTemp, b);
        // Then before we dispose temporary vertexes
        Assert.assertEquals("[A->B, origin->B, OT->B]", b.getIncoming().toString());
        // When
        TemporaryVertex.dispose(origin);
        // Then B is back to normal
        Assert.assertEquals("[A->B, OT->B]", b.getIncoming().toString());
    }

    @Test
    public void disposeShouldNotDeleteOtherOutgoingEdges() {
        // Given a temporary vertex 'destination' connected from A - with one other outgoing edge
        Vertex destination = new TemporaryVertexDisposeTest.TempVertex("destination");
        Vertex otherTemp = new TemporaryVertexDisposeTest.TempVertex("OT");
        TemporaryVertexDisposeTest.edge(a, destination);
        TemporaryVertexDisposeTest.edge(a, otherTemp);
        // Then before we dispose temporary vertexes
        Assert.assertEquals("[A->B, A->destination, A->OT]", a.getOutgoing().toString());
        // When
        TemporaryVertex.dispose(destination);
        // A is back to normal
        Assert.assertEquals("[A->B, A->OT]", a.getOutgoing().toString());
    }

    @Test
    public void disposeShouldHandleLoopsInTemporaryPath() {
        Vertex x = new TemporaryVertexDisposeTest.TempVertex("x");
        Vertex y = new TemporaryVertexDisposeTest.TempVertex("y");
        Vertex z = new TemporaryVertexDisposeTest.TempVertex("z");
        TemporaryVertexDisposeTest.edge(x, y);
        TemporaryVertexDisposeTest.edge(y, z);
        TemporaryVertexDisposeTest.edge(z, x);
        // Add some random links the the main graph
        TemporaryVertexDisposeTest.edge(x, a);
        TemporaryVertexDisposeTest.edge(b, y);
        TemporaryVertexDisposeTest.edge(z, a);
        // When
        TemporaryVertex.dispose(x);
        // Then do return without stack overflow and:
        assertOriginalGraphIsIntact();
    }

    /**
     * Verify a complex temporary path is disposed. The temporary graph is connected to the
     * main graph in both directions (in/out) from many places (temp. vertexes).
     * <p/>
     * The temporary part of the graph do NOT contain any loops.
     */
    @Test
    public void disposeTemporaryVertexesWithComplexPaths() {
        Vertex x = new TemporaryVertexDisposeTest.TempVertex("x");
        Vertex y = new TemporaryVertexDisposeTest.TempVertex("y");
        Vertex z = new TemporaryVertexDisposeTest.TempVertex("z");
        Vertex q = new TemporaryVertexDisposeTest.TempVertex("q");
        // A and B are connected both ways A->B and B->A (A->B is done in the class init)
        TemporaryVertexDisposeTest.edge(b, a);
        // All temporary vertexes are connected in a chain
        TemporaryVertexDisposeTest.edge(x, y);
        TemporaryVertexDisposeTest.edge(y, z);
        TemporaryVertexDisposeTest.edge(z, q);
        // And they are connected to the graph in many ways - no loops
        TemporaryVertexDisposeTest.edge(x, a);
        TemporaryVertexDisposeTest.edge(y, b);
        TemporaryVertexDisposeTest.edge(z, a);
        TemporaryVertexDisposeTest.edge(q, a);
        TemporaryVertexDisposeTest.edge(q, b);
        TemporaryVertexDisposeTest.edge(a, x);
        TemporaryVertexDisposeTest.edge(b, y);
        TemporaryVertexDisposeTest.edge(a, z);
        // Then before we dispose temporary vertexes
        Assert.assertEquals("[B->A, x->A, z->A, q->A]", a.getIncoming().toString());
        Assert.assertEquals("[A->B, A->x, A->z]", a.getOutgoing().toString());
        Assert.assertEquals("[A->B, y->B, q->B]", b.getIncoming().toString());
        Assert.assertEquals("[B->A, B->y]", b.getOutgoing().toString());
        // When
        TemporaryVertex.dispose(x);
        // Then
        Assert.assertEquals("[B->A]", a.getIncoming().toString());
        Assert.assertEquals("[A->B]", a.getOutgoing().toString());
        Assert.assertEquals("[A->B]", b.getIncoming().toString());
        Assert.assertEquals("[B->A]", b.getOutgoing().toString());
    }

    /**
     * We should be able to delete an alternative path/loop created in the graph like:
     * <p/>
     * A -> x -> y -> B
     * <p/>
     * Where 'x' and 'y' are temporary vertexes
     */
    @Test
    public void disposeTemporaryAlternativePath() {
        Vertex x = new TemporaryVertexDisposeTest.TempVertex("x");
        Vertex y = new TemporaryVertexDisposeTest.TempVertex("y");
        // Make a new loop from 'A' via 'x' and 'y' to 'B'
        TemporaryVertexDisposeTest.edge(a, x);
        TemporaryVertexDisposeTest.edge(x, y);
        TemporaryVertexDisposeTest.edge(y, b);
        // Then before we dispose temporary vertexes
        Assert.assertEquals("[A->B, A->x]", a.getOutgoing().toString());
        Assert.assertEquals("[A->B, y->B]", b.getIncoming().toString());
        // When
        TemporaryVertex.dispose(x);
        // Then
        assertOriginalGraphIsIntact();
    }

    /**
     * We should be able to delete a very deep path without getting a stack overflow error.
     */
    @Test
    public void disposeVeryDeepTemporaryPath() {
        // Create access and egress legs with 1000 vertexes
        Vertex origin = new TemporaryVertexDisposeTest.TempVertex("origin");
        Vertex o1 = origin;
        Vertex o2 = null;
        // Number of temporary vertexes in path
        int i = 1024;
        while (i > 0) {
            o2 = new TemporaryVertexDisposeTest.TempVertex(("T" + (--i)));
            TemporaryVertexDisposeTest.edge(o1, o2);
            o1 = o2;
        } 
        TemporaryVertexDisposeTest.edge(o2, a);
        // Verify A is connected to the chain of temporary vertexes.
        Assert.assertEquals("[T0->A]", a.getIncoming().toString());
        // When
        TemporaryVertex.dispose(origin);
        // Then
        assertOriginalGraphIsIntact();
    }

    /* private test helper classes */
    private static class V extends Vertex {
        private V(String label) {
            super(null, label, TemporaryVertexDisposeTest.ANY_LOC, TemporaryVertexDisposeTest.ANY_LOC);
        }

        @Override
        public String toString() {
            return getLabel();
        }
    }

    private static class TempVertex extends TemporaryVertexDisposeTest.V implements TemporaryVertex {
        private TempVertex(String label) {
            super(label);
        }

        @Override
        public boolean isEndVertex() {
            throw new IllegalStateException("The `isEndVertex` is not used by dispose logic.");
        }
    }

    private static class E extends FreeEdge {
        private E(Vertex from, Vertex to) {
            super(from, to);
        }

        @Override
        public String toString() {
            return ((getFromVertex().getLabel()) + "->") + (getToVertex().getLabel());
        }
    }
}

