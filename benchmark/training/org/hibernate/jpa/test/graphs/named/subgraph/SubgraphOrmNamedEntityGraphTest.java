package org.hibernate.jpa.test.graphs.named.subgraph;


import java.util.List;
import javax.persistence.AttributeNode;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


public class SubgraphOrmNamedEntityGraphTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-10633")
    public void testSubgraphsAreLoadededFromOrmXml() throws Exception {
        EntityManager entityManager = getOrCreateEntityManager();
        List<EntityGraph<? super Book>> lneg = entityManager.getEntityGraphs(Book.class);
        Assert.assertNotNull(lneg);
        Assert.assertEquals(2, lneg.size());
        for (EntityGraph<? super Book> neg : lneg) {
            if (neg.getName().equalsIgnoreCase("full")) {
                Assert.assertNotNull(neg.getAttributeNodes());
                for (AttributeNode<?> n : neg.getAttributeNodes()) {
                    if (n.getAttributeName().equalsIgnoreCase("authors")) {
                        Assert.assertEquals(1, n.getSubgraphs().size());
                        List<AttributeNode<?>> attributeNodes = n.getSubgraphs().get(Author.class).getAttributeNodes();
                        Assert.assertNotNull("Subgraph attributes missing", attributeNodes);
                        Assert.assertEquals("Subgraph wrong number of attributes ", 3, attributeNodes.size());
                    }
                }
            }
        }
        entityManager.close();
    }
}

