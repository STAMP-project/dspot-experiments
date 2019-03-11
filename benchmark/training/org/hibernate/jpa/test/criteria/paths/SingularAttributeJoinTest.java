/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.criteria.paths;


import Attribute.PersistentAttributeType.MANY_TO_ONE;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.PathSource;
import org.hibernate.query.criteria.internal.path.SingularAttributeJoin;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Brad Koehn
 */
public class SingularAttributeJoinTest extends BaseEntityManagerFunctionalTestCase {
    /**
     * When building a join from a non-class based entity (EntityMode.MAP), make sure you get the Bindable from
     * the SingularAttribute as the join model. If you don't, you'll get the first non-classed based entity
     * you added to your configuration. Regression for HHH-9142.
     */
    @Test
    public void testEntityModeMapJoins() throws Exception {
        CriteriaBuilderImpl criteriaBuilder = Mockito.mock(CriteriaBuilderImpl.class);
        PathSource pathSource = Mockito.mock(PathSource.class);
        SingularAttribute joinAttribute = Mockito.mock(SingularAttribute.class);
        Mockito.when(joinAttribute.getPersistentAttributeType()).thenReturn(MANY_TO_ONE);
        Type joinType = Mockito.mock(Type.class, Mockito.withSettings().extraInterfaces(Bindable.class));
        Mockito.when(joinAttribute.getType()).thenReturn(joinType);
        SingularAttributeJoin join = new SingularAttributeJoin(criteriaBuilder, null, pathSource, joinAttribute, JoinType.LEFT);
        Assert.assertEquals(joinType, join.getModel());
    }

    @Test
    public void testEntityModeMapJoinCriteriaQuery() throws Exception {
        final EntityManager entityManager = entityManagerFactory().createEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        EntityType distributionEntity = getEntityType("Distribution");
        From distributionFrom = criteriaQuery.from(distributionEntity);
        From policyJoin = distributionFrom.join("policy");
        Path policyId = policyJoin.get("policyId");
        criteriaQuery.select(policyId);
        TypedQuery typedQuery = entityManager.createQuery(criteriaQuery);
        // typedQuery.getResultList();
    }
}

