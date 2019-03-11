package hudson.util;


import hudson.util.CyclicGraphDetector.CycleDetectedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
public class CyclicGraphDetectorTest {
    private class Edge {
        String src;

        String dst;

        private Edge(String src, String dst) {
            this.src = src;
            this.dst = dst;
        }
    }

    private class Graph extends ArrayList<CyclicGraphDetectorTest.Edge> {
        CyclicGraphDetectorTest.Graph e(String src, String dst) {
            add(new CyclicGraphDetectorTest.Edge(src, dst));
            return this;
        }

        Set<String> nodes() {
            Set<String> nodes = new LinkedHashSet<String>();
            for (CyclicGraphDetectorTest.Edge e : this) {
                nodes.add(e.src);
                nodes.add(e.dst);
            }
            return nodes;
        }

        Set<String> edges(String from) {
            Set<String> edges = new LinkedHashSet<String>();
            for (CyclicGraphDetectorTest.Edge e : this) {
                if (e.src.equals(from))
                    edges.add(e.dst);

            }
            return edges;
        }

        /**
         * Performs a cycle check.
         */
        void check() throws Exception {
            run(nodes());
        }

        void mustContainCycle(String... members) throws Exception {
            try {
                check();
                Assert.fail("Cycle expected");
            } catch (CycleDetectedException e) {
                String msg = (("Expected cycle of " + (Arrays.asList(members))) + " but found ") + (e.cycle);
                for (String s : members)
                    Assert.assertTrue(msg, e.cycle.contains(s));

            }
        }
    }

    @Test
    public void cycle1() throws Exception {
        new CyclicGraphDetectorTest.Graph().e("A", "B").e("B", "C").e("C", "A").mustContainCycle("A", "B", "C");
    }

    @Test
    public void cycle2() throws Exception {
        new CyclicGraphDetectorTest.Graph().e("A", "B").e("B", "C").e("C", "C").mustContainCycle("C");
    }

    @Test
    public void cycle3() throws Exception {
        new CyclicGraphDetectorTest.Graph().e("A", "B").e("B", "C").e("C", "D").e("B", "E").e("E", "D").e("E", "A").mustContainCycle("A", "B", "E");
    }
}

