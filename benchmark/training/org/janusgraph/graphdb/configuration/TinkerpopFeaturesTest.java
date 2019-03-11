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
package org.janusgraph.graphdb.configuration;


import Graph.Features.EdgeFeatures;
import Graph.Features.VertexFeatures;
import junit.framework.TestCase;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.janusgraph.core.JanusGraph;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class TinkerpopFeaturesTest {
    private JanusGraph graph;

    @Test
    public void testVertexFeatures() {
        Graph.Features.VertexFeatures vf = graph.features().vertex();
        Assertions.assertFalse(vf.supportsCustomIds());
        Assertions.assertFalse(vf.supportsStringIds());
        Assertions.assertFalse(vf.supportsUuidIds());
        Assertions.assertFalse(vf.supportsAnyIds());
        Assertions.assertFalse(vf.supportsCustomIds());
        TestCase.assertTrue(vf.supportsNumericIds());
        Assertions.assertFalse(vf.supportsUserSuppliedIds());
        graph.close();
        graph = open(true);
        vf = graph.features().vertex();
        Assertions.assertFalse(vf.supportsCustomIds());
        Assertions.assertFalse(vf.supportsStringIds());
        Assertions.assertFalse(vf.supportsUuidIds());
        Assertions.assertFalse(vf.supportsAnyIds());
        Assertions.assertFalse(vf.supportsCustomIds());
        TestCase.assertTrue(vf.supportsNumericIds());
        TestCase.assertTrue(vf.supportsUserSuppliedIds());
    }

    @Test
    public void testEdgeFeatures() {
        Graph.Features.EdgeFeatures ef = graph.features().edge();
        Assertions.assertFalse(ef.supportsStringIds());
        Assertions.assertFalse(ef.supportsUuidIds());
        Assertions.assertFalse(ef.supportsAnyIds());
        Assertions.assertFalse(ef.supportsNumericIds());
        TestCase.assertTrue(ef.supportsCustomIds());
        Assertions.assertFalse(ef.supportsUserSuppliedIds());
    }
}

