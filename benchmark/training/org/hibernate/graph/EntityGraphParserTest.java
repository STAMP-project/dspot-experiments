/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.graph;


import java.util.List;
import java.util.Map;
import javax.persistence.AttributeNode;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.Subgraph;
import org.hibernate.graph.spi.AttributeNodeImplementor;
import org.hibernate.graph.spi.RootGraphImplementor;
import org.hibernate.graph.spi.SubGraphImplementor;
import org.junit.Assert;
import org.junit.Test;


/**
 * A unit test of {@link GraphParser}.
 *
 * @author asusnjar
 */
public class EntityGraphParserTest extends AbstractEntityGraphTest {
    @Test
    public void testNullParsing() {
        EntityGraph<GraphParsingTestEntity> graph = parseGraph(((String) (null)));
        Assert.assertNull(graph);
    }

    @Test
    public void testOneBasicAttributeParsing() {
        EntityGraph<GraphParsingTestEntity> graph = parseGraph("name");
        assertBasicAttributes(graph, "name");
    }

    @Test
    public void testTwoBasicAttributesParsing() {
        EntityGraph<GraphParsingTestEntity> graph = parseGraph("name, description");
        assertBasicAttributes(graph, "name", "description");
    }

    @Test
    public void testLinkParsing() {
        EntityGraph<GraphParsingTestEntity> graph = parseGraph("linkToOne(name, description)");
        Assert.assertNotNull(graph);
        List<AttributeNode<?>> attrs = graph.getAttributeNodes();
        Assert.assertNotNull(attrs);
        Assert.assertEquals(1, attrs.size());
        AttributeNode<?> node = attrs.get(0);
        Assert.assertNotNull(node);
        Assert.assertEquals("linkToOne", node.getAttributeName());
        assertNullOrEmpty(node.getKeySubgraphs());
        @SuppressWarnings("rawtypes")
        Map<Class, Subgraph> sub = node.getSubgraphs();
        assertBasicAttributes(sub.get(GraphParsingTestEntity.class), "name", "description");
    }

    @Test
    public void testMapKeyParsing() {
        EntityGraph<GraphParsingTestEntity> graph = parseGraph("map.key(name, description)");
        Assert.assertNotNull(graph);
        List<AttributeNode<?>> attrs = graph.getAttributeNodes();
        Assert.assertNotNull(attrs);
        Assert.assertEquals(1, attrs.size());
        AttributeNode<?> node = attrs.get(0);
        Assert.assertNotNull(node);
        Assert.assertEquals("map", node.getAttributeName());
        assertNullOrEmpty(node.getSubgraphs());
        @SuppressWarnings("rawtypes")
        Map<Class, Subgraph> sub = node.getKeySubgraphs();
        assertBasicAttributes(sub.get(GraphParsingTestEntity.class), "name", "description");
    }

    @Test
    public void testMapValueParsing() {
        EntityGraph<GraphParsingTestEntity> graph = parseGraph("map.value(name, description)");
        Assert.assertNotNull(graph);
        List<AttributeNode<?>> attrs = graph.getAttributeNodes();
        Assert.assertNotNull(attrs);
        Assert.assertEquals(1, attrs.size());
        AttributeNode<?> node = attrs.get(0);
        Assert.assertNotNull(node);
        Assert.assertEquals("map", node.getAttributeName());
        assertNullOrEmpty(node.getKeySubgraphs());
        @SuppressWarnings("rawtypes")
        Map<Class, Subgraph> sub = node.getSubgraphs();
        assertBasicAttributes(sub.get(GraphParsingTestEntity.class), "name", "description");
    }

    // @Ignore
    @Test
    public void testMixParsingWithMaps() {
        // Fails due to: https://hibernate.atlassian.net/browse/HHH-12696
        String g = " name , linkToOne ( description, map . key ( name ) , map . value ( description ) , name ) , description , map . key ( name , description ) , map . value ( description ) ";
        g = g.replace(" ", "       ");
        for (int i = 1; i <= 2; i++ , g = g.replace(" ", "")) {
            EntityGraph<GraphParsingTestEntity> graph = parseGraph(g);
            assertBasicAttributes(graph, "name", "description");
            AttributeNode<?> linkToOne = getAttributeNodeByName(graph, "linkToOne", true);
            assertNullOrEmpty(linkToOne.getKeySubgraphs());
            @SuppressWarnings("rawtypes")
            Map<Class, Subgraph> linkToOneSubgraphs = linkToOne.getSubgraphs();
            @SuppressWarnings("rawtypes")
            Subgraph linkToOneRoot = linkToOneSubgraphs.get(GraphParsingTestEntity.class);
            assertBasicAttributes(linkToOneRoot, "name", "description");
            AttributeNode<?> linkToOneMap = getAttributeNodeByName(linkToOneRoot, "map", true);
            @SuppressWarnings("rawtypes")
            Map<Class, Subgraph> linkToOneMapKeySubgraphs = linkToOneMap.getKeySubgraphs();
            @SuppressWarnings("rawtypes")
            Subgraph linkToOneMapKeyRoot = linkToOneMapKeySubgraphs.get(GraphParsingTestEntity.class);
            assertBasicAttributes(linkToOneMapKeyRoot, "name");
            @SuppressWarnings("rawtypes")
            Map<Class, Subgraph> linkToOneMapSubgraphs = linkToOneMap.getSubgraphs();
            @SuppressWarnings("rawtypes")
            Subgraph linkToOneMapRoot = linkToOneMapSubgraphs.get(GraphParsingTestEntity.class);
            assertBasicAttributes(linkToOneMapRoot, "description");
            AttributeNode<?> map = getAttributeNodeByName(graph, "map", true);
            @SuppressWarnings("rawtypes")
            Map<Class, Subgraph> mapKeySubgraphs = map.getKeySubgraphs();
            @SuppressWarnings("rawtypes")
            Subgraph mapKeyRoot = mapKeySubgraphs.get(GraphParsingTestEntity.class);
            assertBasicAttributes(mapKeyRoot, "name", "description");
            @SuppressWarnings("rawtypes")
            Map<Class, Subgraph> mapSubgraphs = map.getSubgraphs();
            @SuppressWarnings("rawtypes")
            Subgraph mapRoot = mapSubgraphs.get(GraphParsingTestEntity.class);
            assertBasicAttributes(mapRoot, "description");
        }
    }

    @Test
    public void testMixParsingWithSimplifiedMaps() {
        String g = " name , linkToOne ( description, map . key ( name )  , name ) , description , map . value ( description, name ) ";
        g = g.replace(" ", "       ");
        for (int i = 1; i <= 2; i++ , g = g.replace(" ", "")) {
            EntityGraph<GraphParsingTestEntity> graph = parseGraph(g);
            assertBasicAttributes(graph, "name", "description");
            AttributeNode<?> linkToOne = getAttributeNodeByName(graph, "linkToOne", true);
            assertNullOrEmpty(linkToOne.getKeySubgraphs());
            @SuppressWarnings("rawtypes")
            Map<Class, Subgraph> linkToOneSubgraphs = linkToOne.getSubgraphs();
            @SuppressWarnings("rawtypes")
            Subgraph linkToOneRoot = linkToOneSubgraphs.get(GraphParsingTestEntity.class);
            assertBasicAttributes(linkToOneRoot, "name", "description");
            AttributeNode<?> linkToOneMap = getAttributeNodeByName(linkToOneRoot, "map", true);
            @SuppressWarnings("rawtypes")
            Map<Class, Subgraph> linkToOneMapKeySubgraphs = linkToOneMap.getKeySubgraphs();
            @SuppressWarnings("rawtypes")
            Subgraph linkToOneMapKeyRoot = linkToOneMapKeySubgraphs.get(GraphParsingTestEntity.class);
            assertBasicAttributes(linkToOneMapKeyRoot, "name");
            AttributeNode<?> map = getAttributeNodeByName(graph, "map", true);
            @SuppressWarnings("rawtypes")
            Map<Class, Subgraph> mapSubgraphs = map.getSubgraphs();
            @SuppressWarnings("rawtypes")
            Subgraph mapRoot = mapSubgraphs.get(GraphParsingTestEntity.class);
            assertBasicAttributes(mapRoot, "description", "name");
        }
    }

    // @Ignore("Cannot run due to Hibernate bug: https://hibernate.atlassian.net/browse/HHH-10378")
    // TODO Re-enable when Hibernate bug HHH-10378 is fixed
    @Test
    public void testLinkSubtypeParsing() {
        // https://hibernate.atlassian.net/browse/HHH-10378
        // 
        // Specifically the isTreatable(...) method in org.hibernate.jpa.graph.internal.AttributeNodeImpl
        // 
        // It states:
        // 
        // Check to make sure that the java type of the given entity persister is treatable as the given type. In other
        // words, is the given type a subclass of the class represented by the persister.
        // 
        // But that does not clearly match the implementation (seems opposite):
        // 
        // return type.isAssignableFrom( entityPersister.getMappedClass() );
        RootGraphImplementor<GraphParsingTestEntity> graph = parseGraph("linkToOne(name, description), linkToOne(GraphParsingTestSubentity: sub)");
        Assert.assertNotNull(graph);
        List<AttributeNodeImplementor<?>> attrs = graph.getAttributeNodeImplementors();
        Assert.assertNotNull(attrs);
        Assert.assertEquals(1, attrs.size());
        AttributeNodeImplementor<?> linkToOneNode = attrs.get(0);
        Assert.assertNotNull(linkToOneNode);
        Assert.assertEquals("linkToOne", linkToOneNode.getAttributeName());
        assertNullOrEmpty(linkToOneNode.getKeySubgraphs());
        final SubGraphImplementor subGraph = linkToOneNode.getSubGraphMap().get(GraphParsingTestSubentity.class);
        Assert.assertNotNull(subGraph);
        assertBasicAttributes(subGraph, "sub");
    }

    @Test
    public void testHHH10378IsNotFixedYet() {
        EntityManager entityManager = getOrCreateEntityManager();
        RootGraphImplementor<GraphParsingTestEntity> graph = createEntityGraph(GraphParsingTestEntity.class);
        final SubGraphImplementor<GraphParsingTestSubentity> subGraph = graph.addSubGraph("linkToOne", GraphParsingTestSubentity.class);
        Assert.assertEquals(subGraph.getGraphedType().getJavaType(), GraphParsingTestSubentity.class);
        final AttributeNodeImplementor<Object> subTypeAttrNode = subGraph.addAttributeNode("sub");
        assert subTypeAttrNode != null;
    }

    // @Ignore("Cannot run due to Hibernate bug: https://hibernate.atlassian.net/browse/HHH-12696")
    @Test
    public void testHHH12696MapSubgraphsKeyFirst() {
        EntityManager entityManager = getOrCreateEntityManager();
        EntityGraph<GraphParsingTestEntity> graph = entityManager.createEntityGraph(GraphParsingTestEntity.class);
        final String mapAttributeName = "map";
        Subgraph<GraphParsingTestEntity> keySubgraph = graph.addKeySubgraph(mapAttributeName);
        Subgraph<GraphParsingTestEntity> valueSubgraph = graph.addSubgraph(mapAttributeName);
        checkMapKeyAndValueSubgraphs(graph, mapAttributeName, keySubgraph, valueSubgraph);
    }

    // @Ignore("Cannot run due to Hibernate bug: https://hibernate.atlassian.net/browse/HHH-12696")
    @Test
    public void testHHH12696MapSubgraphsValueFirst() {
        EntityManager entityManager = getOrCreateEntityManager();
        EntityGraph<GraphParsingTestEntity> graph = entityManager.createEntityGraph(GraphParsingTestEntity.class);
        final String mapAttributeName = "map";
        Subgraph<GraphParsingTestEntity> valueSubgraph = graph.addSubgraph(mapAttributeName);
        Subgraph<GraphParsingTestEntity> keySubgraph = graph.addKeySubgraph(mapAttributeName);
        checkMapKeyAndValueSubgraphs(graph, mapAttributeName, keySubgraph, valueSubgraph);
    }
}

