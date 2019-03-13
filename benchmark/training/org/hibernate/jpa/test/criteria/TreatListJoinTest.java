/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.criteria;


import JoinType.LEFT;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import org.hamcrest.core.Is;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
public class TreatListJoinTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testTreatJoin() {
        EntityManager em = createEntityManager();
        try {
            final CriteriaBuilder cb = em.getCriteriaBuilder();
            final CriteriaQuery<Tuple> query = cb.createTupleQuery();
            final Root<TreatListJoinTest.TestEntity> testEntity = query.from(TreatListJoinTest.TestEntity.class);
            final List<Selection<?>> selections = new LinkedList();
            selections.add(testEntity.get("id"));
            final ListJoin<TreatListJoinTest.TestEntity, TreatListJoinTest.AbstractEntity> entities = testEntity.joinList("entities", LEFT);
            entities.on(cb.equal(entities.get("entityType"), TreatListJoinTest.EntityA.class.getName()));
            final ListJoin<TreatListJoinTest.TestEntity, TreatListJoinTest.EntityA> joinEntityA = cb.treat(entities, TreatListJoinTest.EntityA.class);
            selections.add(joinEntityA.get("id"));
            selections.add(joinEntityA.get("valueA"));
            final ListJoin<TreatListJoinTest.TestEntity, TreatListJoinTest.AbstractEntity> entitiesB = testEntity.joinList("entities", LEFT);
            entitiesB.on(cb.equal(entitiesB.get("entityType"), TreatListJoinTest.EntityB.class.getName()));
            final ListJoin<TreatListJoinTest.TestEntity, TreatListJoinTest.EntityB> joinEntityB = cb.treat(entitiesB, TreatListJoinTest.EntityB.class);
            selections.add(joinEntityB.get("id"));
            selections.add(joinEntityB.get("valueB"));
            query.multiselect(selections);
            final List<Tuple> resultList = em.createQuery(query).getResultList();
            Assert.assertThat(resultList.size(), Is.is(10));
        } finally {
            em.close();
        }
    }

    @MappedSuperclass
    public abstract static class MyEntity {
        @Id
        @GeneratedValue
        private long id;
    }

    @Entity(name = "TestEntity")
    public static class TestEntity extends TreatListJoinTest.MyEntity {
        @OneToMany(mappedBy = "parent", cascade = { CascadeType.ALL })
        private List<TreatListJoinTest.AbstractEntity> entities = new ArrayList<>();
    }

    @Entity(name = "AbstractEntity")
    public abstract static class AbstractEntity extends TreatListJoinTest.MyEntity {
        String entityType = getClass().getName();

        @ManyToOne
        private TreatListJoinTest.TestEntity parent;

        public TreatListJoinTest.TestEntity getParent() {
            return parent;
        }

        public void setParent(TreatListJoinTest.TestEntity parent) {
            this.parent = parent;
            parent.entities.add(this);
        }
    }

    @Entity(name = "EntityA")
    public static class EntityA extends TreatListJoinTest.AbstractEntity {
        public String valueA;

        public EntityA() {
            super.entityType = getClass().getName();
        }
    }

    @Entity(name = "EntityB")
    public static class EntityB extends TreatListJoinTest.AbstractEntity {
        public String valueB;

        public EntityB() {
            super.entityType = getClass().getName();
        }
    }
}

