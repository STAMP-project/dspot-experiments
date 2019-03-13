package com.tinkerpop.blueprints.oupls.sail;


import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import org.junit.Test;


/**
 *
 *
 * @author Joshua Shinavier (http://fortytwo.net)
 */
public class TinkerGraphSailTest extends GraphSailTest {
    @Test
    public void testTmp() throws Exception {
        KeyIndexableGraph g = new TinkerGraph();
        GraphSail sail = new GraphSail(g, "sp,p,c,pc");
    }
}

