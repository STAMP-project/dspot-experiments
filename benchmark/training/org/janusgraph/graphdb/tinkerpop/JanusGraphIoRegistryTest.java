/**
 * Copyright 2019 JanusGraph Authors
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
package org.janusgraph.graphdb.tinkerpop;


import Geoshape.GeoShapeGryoSerializer;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.apache.tinkerpop.gremlin.driver.ser.SerializationException;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph;
import org.apache.tinkerpop.shaded.kryo.Kryo;
import org.apache.tinkerpop.shaded.kryo.io.Input;
import org.apache.tinkerpop.shaded.kryo.io.Output;
import org.janusgraph.core.attribute.Geo;
import org.janusgraph.core.attribute.Geoshape;
import org.janusgraph.core.attribute.Text;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JanusGraphIoRegistryTest {
    private final Logger log = LoggerFactory.getLogger(JanusGraphIoRegistryTest.class);

    private static final ByteBufAllocator allocator = UnpooledByteBufAllocator.DEFAULT;

    /**
     * This is necessary since we replace the default TinkerPop PSerializer
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testTinkerPopPredicatesAsGryo() throws SerializationException {
        // Don't change this trivially. At the time of this writing (TinkerPop
        // 3.2.3), this is how many P predicate methods were defined. If this
        // fails, then JanusGraphPSerializer needs to be updated to add/remove
        // any TinkerPop predicates!
        Assertions.assertEquals(15, Stream.of(P.class.getDeclaredMethods()).filter(( m) -> Modifier.isStatic(m.getModifiers())).filter(( p) -> {
            log.debug("Predicate: {}", p);
            return !(p.isSynthetic());
        }).count());
        Graph graph = EmptyGraph.instance();
        GraphTraversalSource g = graph.traversal();
        // TinkerPop Predicates
        GraphTraversal[] traversals = new GraphTraversal[]{ g.V().has("age", within(5000)), g.V().has("age", without(5000)), g.V().has("age", within(5000, 45)), g.V().has("age", inside(45, 5000)), g.V().and(has("age", between(45, 5000)), has("name", within("pluto"))), g.V().or(has("age", between(45, 5000)), has("name", within("pluto", "neptune"))) };
        serializationTest(traversals);
    }

    @Test
    public void testJanusGraphPredicatesAsGryo() throws SerializationException {
        Graph graph = EmptyGraph.instance();
        GraphTraversalSource g = graph.traversal();
        // Janus Graph Geo, Text Predicates
        GraphTraversal[] traversals = new GraphTraversal[]{ g.E().has("place", Geo.geoIntersect(Geoshape.circle(37.97, 23.72, 50))), g.E().has("place", Geo.geoWithin(Geoshape.circle(37.97, 23.72, 50))), g.E().has("place", Geo.geoDisjoint(Geoshape.circle(37.97, 23.72, 50))), g.V().has("place", Geo.geoContains(Geoshape.point(37.97, 23.72))), g.V().has("name", Text.textContains("neptune")), g.V().has("name", Text.textContainsPrefix("nep")), g.V().has("name", Text.textContainsRegex("nep.*")), g.V().has("name", Text.textPrefix("n")), g.V().has("name", Text.textRegex(".*n.*")), g.V().has("name", Text.textContainsFuzzy("neptun")), g.V().has("name", Text.textFuzzy("nepitne")) };
        serializationTest(traversals);
    }

    @Test
    public void testGeoshapAsGryo() throws SerializationException {
        Graph graph = EmptyGraph.instance();
        GraphTraversalSource g = graph.traversal();
        GraphTraversal[] traversals = new GraphTraversal[]{ g.addV().property("loc", Geoshape.box(0.1, 0.2, 0.3, 0.4)), g.addV().property("loc", Geoshape.box(0.1F, 0.3F, 0.5F, 0.6F)), g.addV().property("loc", Geoshape.circle(0.1, 0.3, 0.3)), g.addV().property("loc", Geoshape.circle(0.2F, 0.4F, 0.5F)), g.addV().property("loc", Geoshape.point(1.0, 4.0)), g.addV().property("loc", Geoshape.point(1.0F, 1.0F)) };
        serializationTest(traversals);
    }

    @Test
    public void testLegacyGeoshapAsGryo() {
        final Geoshape point = Geoshape.point(1.0, 4.0);
        Kryo kryo = new Kryo();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        Output output = new Output(outStream, 4096);
        output.writeLong(1);
        output.writeFloat(((float) (point.getPoint().getLatitude())));
        output.writeFloat(((float) (point.getPoint().getLongitude())));
        output.flush();
        Geoshape.GeoShapeGryoSerializer serializer = new Geoshape.GeoShapeGryoSerializer();
        Input input = new Input(new ByteArrayInputStream(outStream.toByteArray()), 4096);
        Assertions.assertEquals(point, serializer.read(kryo, input, Geoshape.class));
    }
}

