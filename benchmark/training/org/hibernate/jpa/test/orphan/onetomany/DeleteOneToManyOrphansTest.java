/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.orphan.onetomany;


import java.util.List;
import javax.persistence.EntityManager;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
public class DeleteOneToManyOrphansTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-9568")
    @FailureExpected(jiraKey = "HHH-9568")
    public void testOrphanedWhileManaged() {
        createData();
        EntityManager entityManager = getOrCreateEntityManager();
        entityManager.getTransaction().begin();
        List results = entityManager.createQuery("from Feature").getResultList();
        Assert.assertEquals(1, results.size());
        results = entityManager.createQuery("from Product").getResultList();
        Assert.assertEquals(1, results.size());
        Product product = ((Product) (results.get(0)));
        Assert.assertEquals(1, product.getFeatures().size());
        product.getFeatures().clear();
        entityManager.getTransaction().commit();
        entityManager.close();
        entityManager = getOrCreateEntityManager();
        entityManager.getTransaction().begin();
        product = entityManager.find(Product.class, product.getId());
        Assert.assertEquals(0, product.getFeatures().size());
        results = entityManager.createQuery("from Feature").getResultList();
        Assert.assertEquals(0, results.size());
        results = entityManager.createQuery("from Product").getResultList();
        Assert.assertEquals(1, results.size());
        entityManager.getTransaction().commit();
        entityManager.close();
        cleanupData();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9568")
    @FailureExpected(jiraKey = "HHH-9568")
    public void testOrphanedWhileManagedMergeOwner() {
        createData();
        EntityManager entityManager = getOrCreateEntityManager();
        entityManager.getTransaction().begin();
        List results = entityManager.createQuery("from Feature").getResultList();
        Assert.assertEquals(1, results.size());
        results = entityManager.createQuery("from Product").getResultList();
        Assert.assertEquals(1, results.size());
        Product product = ((Product) (results.get(0)));
        Assert.assertEquals(1, product.getFeatures().size());
        product.getFeatures().clear();
        entityManager.merge(product);
        entityManager.getTransaction().commit();
        entityManager.close();
        entityManager = getOrCreateEntityManager();
        entityManager.getTransaction().begin();
        product = entityManager.find(Product.class, product.getId());
        Assert.assertEquals(0, product.getFeatures().size());
        results = entityManager.createQuery("from Feature").getResultList();
        Assert.assertEquals(0, results.size());
        results = entityManager.createQuery("from Product").getResultList();
        Assert.assertEquals(1, results.size());
        entityManager.getTransaction().commit();
        entityManager.close();
        cleanupData();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9568")
    @FailureExpected(jiraKey = "HHH-9568")
    public void testReplacedWhileManaged() {
        createData();
        EntityManager entityManager = getOrCreateEntityManager();
        entityManager.getTransaction().begin();
        List results = entityManager.createQuery("from Feature").getResultList();
        Assert.assertEquals(1, results.size());
        results = entityManager.createQuery("from Product").getResultList();
        Assert.assertEquals(1, results.size());
        Product product = ((Product) (results.get(0)));
        Assert.assertEquals(1, product.getFeatures().size());
        // Replace with a new Feature instance
        product.getFeatures().remove(0);
        Feature featureNew = new Feature();
        featureNew.setName("Feature 2");
        featureNew.setProduct(product);
        product.getFeatures().add(featureNew);
        entityManager.persist(featureNew);
        entityManager.getTransaction().commit();
        entityManager.close();
        entityManager = getOrCreateEntityManager();
        entityManager.getTransaction().begin();
        results = entityManager.createQuery("from Feature").getResultList();
        Assert.assertEquals(1, results.size());
        Feature featureQueried = ((Feature) (results.get(0)));
        Assert.assertEquals(featureNew.getId(), featureQueried.getId());
        results = entityManager.createQuery("from Product").getResultList();
        Assert.assertEquals(1, results.size());
        Product productQueried = ((Product) (results.get(0)));
        Assert.assertEquals(1, productQueried.getFeatures().size());
        Assert.assertEquals(featureQueried, productQueried.getFeatures().get(0));
        entityManager.getTransaction().commit();
        entityManager.close();
        cleanupData();
    }
}

