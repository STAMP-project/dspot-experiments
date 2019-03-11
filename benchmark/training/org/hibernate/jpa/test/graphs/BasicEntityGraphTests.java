/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.graphs;


import java.util.Set;
import javax.persistence.AttributeNode;
import javax.persistence.Entity;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Subgraph;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class BasicEntityGraphTests extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testBasicGraphBuilding() {
        EntityManager em = getOrCreateEntityManager();
        EntityGraph<BasicEntityGraphTests.Entity1> graphRoot = em.createEntityGraph(BasicEntityGraphTests.Entity1.class);
        Assert.assertNull(graphRoot.getName());
        Assert.assertEquals(0, graphRoot.getAttributeNodes().size());
    }

    @Test
    public void testBasicSubgraphBuilding() {
        EntityManager em = getOrCreateEntityManager();
        EntityGraph<BasicEntityGraphTests.Entity1> graphRoot = em.createEntityGraph(BasicEntityGraphTests.Entity1.class);
        Subgraph<BasicEntityGraphTests.Entity1> parentGraph = graphRoot.addSubgraph("parent");
        Subgraph<BasicEntityGraphTests.Entity1> childGraph = graphRoot.addSubgraph("children");
        Assert.assertNull(graphRoot.getName());
        Assert.assertEquals(2, graphRoot.getAttributeNodes().size());
        Assert.assertTrue(((graphRoot.getAttributeNodes().get(0).getSubgraphs().containsValue(parentGraph)) || (graphRoot.getAttributeNodes().get(0).getSubgraphs().containsValue(childGraph))));
        Assert.assertTrue(((graphRoot.getAttributeNodes().get(1).getSubgraphs().containsValue(parentGraph)) || (graphRoot.getAttributeNodes().get(1).getSubgraphs().containsValue(childGraph))));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBasicGraphImmutability() {
        EntityManager em = getOrCreateEntityManager();
        EntityGraph<BasicEntityGraphTests.Entity1> graphRoot = em.createEntityGraph(BasicEntityGraphTests.Entity1.class);
        graphRoot.addSubgraph("parent");
        graphRoot.addSubgraph("children");
        em.getEntityManagerFactory().addNamedEntityGraph("immutable", graphRoot);
        graphRoot = ((EntityGraph<BasicEntityGraphTests.Entity1>) (em.getEntityGraph("immutable")));
        Assert.assertEquals("immutable", graphRoot.getName());
        Assert.assertEquals(2, graphRoot.getAttributeNodes().size());
        try {
            graphRoot.addAttributeNodes("parent");
            Assert.fail("Should have failed");
        } catch (IllegalStateException ignore) {
            // expected outcome
        }
        for (AttributeNode attrNode : graphRoot.getAttributeNodes()) {
            Assert.assertEquals(1, attrNode.getSubgraphs().size());
            Subgraph subgraph = ((Subgraph) (attrNode.getSubgraphs().values().iterator().next()));
            try {
                graphRoot.addAttributeNodes("parent");
                Assert.fail("Should have failed");
            } catch (IllegalStateException ignore) {
                // expected outcome
            }
        }
    }

    @Entity(name = "Entity1")
    public static class Entity1 {
        @Id
        public Integer id;

        public String name;

        @ManyToOne
        public BasicEntityGraphTests.Entity1 parent;

        @OneToMany(mappedBy = "parent")
        public Set<BasicEntityGraphTests.Entity1> children;
    }
}

