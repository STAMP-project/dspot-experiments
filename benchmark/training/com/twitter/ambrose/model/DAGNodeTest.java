package com.twitter.ambrose.model;


import com.google.common.collect.ImmutableList;
import java.io.IOException;
import org.junit.Test;


/**
 * Unit tests for {@link DAGNodeTest}.
 */
public class DAGNodeTest {
    @Test
    public void testRoundTrip() throws IOException {
        DAGNode<Job> node = new DAGNode<Job>("scope-1", new Job("job-1", null, null));
        DAGNode<Job> child = new DAGNode<Job>("scope-2", new Job("job-2", null, null));
        node.setSuccessors(ImmutableList.<DAGNode<? extends Job>>of(child));
        testRoundTrip(node);
    }
}

