/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.jpa.test.criteria.paths;


import org.hibernate.jpa.test.metamodel.AbstractMetamodelSpecificTest;
import org.hibernate.jpa.test.metamodel.Address;
import org.hibernate.jpa.test.metamodel.Article;
import org.hibernate.jpa.test.metamodel.MapEntity;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class PluralAttributeExpressionsTest extends AbstractMetamodelSpecificTest {
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // IS [NOT] EMPTY
    @Test
    public void testCollectionIsEmptyHql() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            entityManager.createQuery("select a from Address a where a.phones is empty").getResultList();
        });
    }

    @Test
    public void testCollectionIsEmptyCriteria() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final HibernateCriteriaBuilder cb = ((HibernateCriteriaBuilder) (entityManager.getCriteriaBuilder()));
            final CriteriaQuery<Address> criteria = cb.createQuery(.class);
            final Root<Address> root = criteria.from(.class);
            criteria.select(root).where(cb.isEmpty(root.get(Address_.phones)));
            entityManager.createQuery(criteria).getResultList();
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11225")
    @FailureExpected(jiraKey = "HHH-6686")
    public void testElementMapIsEmptyHql() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            entityManager.createQuery("select m from MapEntity m where m.localized is empty").getResultList();
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11225")
    @FailureExpected(jiraKey = "HHH-6686")
    public void testElementMapIsEmptyCriteria() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final HibernateCriteriaBuilder cb = ((HibernateCriteriaBuilder) (entityManager.getCriteriaBuilder()));
            final CriteriaQuery<MapEntity> criteria = cb.createQuery(.class);
            final Root<MapEntity> root = criteria.from(.class);
            criteria.select(root).where(cb.isMapEmpty(root.get(MapEntity_.localized)));
            entityManager.createQuery(criteria).getResultList();
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11225")
    public void testEntityMapIsEmptyHql() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            entityManager.createQuery("select a from Article a where a.translations is empty").getResultList();
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11225")
    public void testEntityMapIsEmptyCriteria() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final HibernateCriteriaBuilder cb = ((HibernateCriteriaBuilder) (entityManager.getCriteriaBuilder()));
            final CriteriaQuery<Article> criteria = cb.createQuery(.class);
            final Root<Article> root = criteria.from(.class);
            criteria.select(root).where(cb.isEmpty(root.get(Article_.translations)));
            entityManager.createQuery(criteria).getResultList();
        });
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // SIZE
    @Test
    public void testCollectionSizeHql() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            entityManager.createQuery("select a from Address a where size(a.phones) > 1").getResultList();
        });
    }

    @Test
    public void testCollectionSizeCriteria() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final HibernateCriteriaBuilder cb = ((HibernateCriteriaBuilder) (entityManager.getCriteriaBuilder()));
            final CriteriaQuery<Address> criteria = cb.createQuery(.class);
            final Root<Address> root = criteria.from(.class);
            criteria.select(root).where(cb.gt(cb.size(root.get(Address_.phones)), 1));
            entityManager.createQuery(criteria).getResultList();
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11225")
    public void testElementMapSizeHql() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            entityManager.createQuery("select m from MapEntity m where size( m.localized ) > 1").getResultList();
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11225")
    public void testElementMapSizeCriteria() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final HibernateCriteriaBuilder cb = ((HibernateCriteriaBuilder) (entityManager.getCriteriaBuilder()));
            final CriteriaQuery<MapEntity> criteria = cb.createQuery(.class);
            final Root<MapEntity> root = criteria.from(.class);
            criteria.select(root).where(cb.gt(cb.mapSize(root.get(MapEntity_.localized)), 1));
            entityManager.createQuery(criteria).getResultList();
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11225")
    public void testEntityMapSizeHql() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            entityManager.createQuery("select a from Article a where size(a.translations) > 1").getResultList();
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11225")
    public void testEntityMapSizeCriteria() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final HibernateCriteriaBuilder cb = ((HibernateCriteriaBuilder) (entityManager.getCriteriaBuilder()));
            final CriteriaQuery<Article> criteria = cb.createQuery(.class);
            final Root<Article> root = criteria.from(.class);
            criteria.select(root).where(cb.gt(cb.mapSize(root.get(Article_.translations)), 1));
            entityManager.createQuery(criteria).getResultList();
        });
    }
}

