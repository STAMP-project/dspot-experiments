/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.criteria.nulliteral;


import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hamcrest.CoreMatchers;
import org.hibernate.dialect.AbstractHANADialect;
import org.hibernate.dialect.DB2Dialect;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.dialect.SybaseDialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
public class CriteriaLiteralInSelectExpressionTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-10729")
    public void testBooleanLiteral() throws Exception {
        final EntityManager entityManager = getOrCreateEntityManager();
        try {
            entityManager.getTransaction().begin();
            final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            final CriteriaQuery<CriteriaLiteralInSelectExpressionTest.MyEntityDTO> query = criteriaBuilder.createQuery(CriteriaLiteralInSelectExpressionTest.MyEntityDTO.class);
            final Root<CriteriaLiteralInSelectExpressionTest.MyEntity> entity = query.from(CriteriaLiteralInSelectExpressionTest.MyEntity.class);
            query.multiselect(criteriaBuilder.literal(false), entity.get("name"));
            final List<CriteriaLiteralInSelectExpressionTest.MyEntityDTO> dtos = entityManager.createQuery(query).getResultList();
            Assert.assertThat(dtos.size(), CoreMatchers.is(1));
            Assert.assertThat(dtos.get(0).active, CoreMatchers.is(false));
            Assert.assertThat(dtos.get(0).name, CoreMatchers.is("Fab"));
            Assert.assertThat(dtos.get(0).surname, CoreMatchers.nullValue());
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        } finally {
            entityManager.close();
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10861")
    public void testNullLiteral() throws Exception {
        final EntityManager entityManager = getOrCreateEntityManager();
        try {
            entityManager.getTransaction().begin();
            final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            final CriteriaQuery<CriteriaLiteralInSelectExpressionTest.MyEntityDTO> query = criteriaBuilder.createQuery(CriteriaLiteralInSelectExpressionTest.MyEntityDTO.class);
            final Root<CriteriaLiteralInSelectExpressionTest.MyEntity> entity = query.from(CriteriaLiteralInSelectExpressionTest.MyEntity.class);
            query.multiselect(criteriaBuilder.literal(false), criteriaBuilder.nullLiteral(String.class));
            final List<CriteriaLiteralInSelectExpressionTest.MyEntityDTO> dtos = entityManager.createQuery(query).getResultList();
            Assert.assertThat(dtos.size(), CoreMatchers.is(1));
            Assert.assertThat(dtos.get(0).active, CoreMatchers.is(false));
            Assert.assertThat(dtos.get(0).name, CoreMatchers.nullValue());
            Assert.assertThat(dtos.get(0).surname, CoreMatchers.nullValue());
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        } finally {
            entityManager.close();
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10861")
    public void testNullLiteralFirst() throws Exception {
        final EntityManager entityManager = getOrCreateEntityManager();
        try {
            entityManager.getTransaction().begin();
            final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            final CriteriaQuery<CriteriaLiteralInSelectExpressionTest.MyEntityDTO> query = criteriaBuilder.createQuery(CriteriaLiteralInSelectExpressionTest.MyEntityDTO.class);
            final Root<CriteriaLiteralInSelectExpressionTest.MyEntity> entity = query.from(CriteriaLiteralInSelectExpressionTest.MyEntity.class);
            query.multiselect(criteriaBuilder.nullLiteral(String.class), entity.get("surname"));
            final List<CriteriaLiteralInSelectExpressionTest.MyEntityDTO> dtos = entityManager.createQuery(query).getResultList();
            Assert.assertThat(dtos.size(), CoreMatchers.is(1));
            Assert.assertThat(dtos.get(0).name, CoreMatchers.nullValue());
            Assert.assertThat(dtos.get(0).surname, CoreMatchers.is("A"));
            Assert.assertThat(dtos.get(0).active, CoreMatchers.is(false));
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        } finally {
            entityManager.close();
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10729")
    public void testStringLiteral() throws Exception {
        final EntityManager entityManager = getOrCreateEntityManager();
        try {
            entityManager.getTransaction().begin();
            final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            final CriteriaQuery<CriteriaLiteralInSelectExpressionTest.MyEntityDTO> query = criteriaBuilder.createQuery(CriteriaLiteralInSelectExpressionTest.MyEntityDTO.class);
            final Root<CriteriaLiteralInSelectExpressionTest.MyEntity> entity = query.from(CriteriaLiteralInSelectExpressionTest.MyEntity.class);
            query.multiselect(criteriaBuilder.literal("Leo"), entity.get("surname"));
            final List<CriteriaLiteralInSelectExpressionTest.MyEntityDTO> dtos = entityManager.createQuery(query).getResultList();
            Assert.assertThat(dtos.size(), CoreMatchers.is(1));
            Assert.assertThat(dtos.get(0).name, CoreMatchers.is("Leo"));
            Assert.assertThat(dtos.get(0).surname, CoreMatchers.is("A"));
            Assert.assertThat(dtos.get(0).active, CoreMatchers.is(false));
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        } finally {
            entityManager.close();
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9021")
    @SkipForDialect({ Oracle8iDialect.class, DB2Dialect.class, SQLServerDialect.class, SybaseDialect.class, AbstractHANADialect.class })
    public void testStringLiteral2() {
        final EntityManager entityManager = getOrCreateEntityManager();
        try {
            final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            final CriteriaQuery<Tuple> criteriaQuery = builder.createQuery(Tuple.class);
            criteriaQuery.from(CriteriaLiteralInSelectExpressionTest.MyEntity.class);
            criteriaQuery.multiselect(builder.equal(builder.literal(1), builder.literal(2)));
            final TypedQuery<Tuple> typedQuery = entityManager.createQuery(criteriaQuery);
            final List<Tuple> results = typedQuery.getResultList();
            Assert.assertThat(results.size(), CoreMatchers.is(1));
            Assert.assertThat(results.get(0).getElements().size(), CoreMatchers.is(1));
            Assert.assertThat(results.get(0).get(0), CoreMatchers.is(false));
        } finally {
            entityManager.close();
        }
    }

    @Entity(name = "MyEntity")
    @Table(name = "MY_ENTITY")
    public static class MyEntity implements Serializable {
        @Id
        @Column(name = "id")
        @GeneratedValue
        private Integer id;

        private String name;

        private String surname;

        public MyEntity() {
        }

        public MyEntity(String name, String surname) {
            this.name = name;
            this.surname = surname;
        }

        public MyEntity(String name, boolean surname) {
            this.name = name;
        }
    }

    public static class MyEntityDTO {
        private String name;

        private String surname;

        private boolean active;

        public MyEntityDTO() {
        }

        public MyEntityDTO(String name, String surname) {
            this.name = name;
            this.surname = surname;
        }

        public MyEntityDTO(boolean active, String name) {
            this.name = name;
            this.active = active;
        }
    }
}

