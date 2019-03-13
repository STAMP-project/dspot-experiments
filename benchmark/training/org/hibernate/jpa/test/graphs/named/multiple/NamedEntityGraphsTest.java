/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.graphs.named.multiple;


import java.util.List;
import javax.persistence.AttributeNode;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import junit.framework.Assert;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


/**
 *
 *
 * @author Steve Ebersole
 */
public class NamedEntityGraphsTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testIt() {
        EntityGraph graph = getOrCreateEntityManager().getEntityGraph("abc");
        Assert.assertNotNull(graph);
        graph = getOrCreateEntityManager().getEntityGraph("xyz");
        Assert.assertNotNull(graph);
    }

    @Test
    public void testAttributeNodesAreAvailable() {
        EntityManager em = getOrCreateEntityManager();
        EntityGraph graph = em.getEntityGraph("name_salary_graph");
        Assert.assertNotNull(graph);
        List<AttributeNode<?>> list = graph.getAttributeNodes();
        Assert.assertNotNull(list);
        assertTrue(("expected list.size() is two but actual list size is " + (list.size())), (2 == (list.size())));
        AttributeNode attributeNode1 = list.get(0);
        AttributeNode attributeNode2 = list.get(1);
        Assert.assertNotNull(attributeNode1);
        Assert.assertNotNull(attributeNode2);
        assertTrue(("node1 attribute name is expected to be either 'name' or 'salary' but actually is " + (attributeNode1.getAttributeName())), (("name".equals(attributeNode1.getAttributeName())) || ("salary".equals(attributeNode1.getAttributeName()))));
        assertTrue(("node2 attribute name is expected to be either 'name' or 'salary' but actually is " + (attributeNode2.getAttributeName())), (("name".equals(attributeNode2.getAttributeName())) || ("salary".equals(attributeNode2.getAttributeName()))));
    }
}

