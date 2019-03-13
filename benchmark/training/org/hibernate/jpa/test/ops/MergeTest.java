/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.ops;


import javax.persistence.EntityManager;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 * @author Hardy Ferentschik
 */
public class MergeTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testMergeTree() {
        clearCounts();
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Node root = new Node("root");
        Node child = new Node("child");
        root.addChild(child);
        em.persist(root);
        em.getTransaction().commit();
        em.close();
        assertInsertCount(2);
        clearCounts();
        root.setDescription("The root node");
        child.setDescription("The child node");
        Node secondChild = new Node("second child");
        root.addChild(secondChild);
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        em.merge(root);
        em.getTransaction().commit();
        em.close();
        assertInsertCount(1);
        assertUpdateCount(2);
    }
}

