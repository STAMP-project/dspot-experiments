/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) {DATE}, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.jpa.test.criteria.selectcase;


import CriteriaBuilder.SimpleCase;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.dialect.DB2Dialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.TestForIssue;
import org.junit.Test;


@TestForIssue(jiraKey = "HHH-9731")
@SkipForDialect(value = DB2Dialect.class, comment = "We would need casts in the case clauses. See HHH-12822.")
public class SelectCaseTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void selectCaseWithValuesShouldWork() {
        EntityManager entityManager = getOrCreateEntityManager();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaBuilder.Case<SelectCaseTest.EnumValue> selectCase = cb.selectCase();
        Predicate somePredicate = cb.equal(cb.literal(1), 1);
        selectCase.when(somePredicate, SelectCaseTest.EnumValue.VALUE_1);
        selectCase.otherwise(SelectCaseTest.EnumValue.VALUE_2);
        CriteriaQuery<SelectCaseTest.Entity> query = cb.createQuery(SelectCaseTest.Entity.class);
        Root<SelectCaseTest.Entity> from = query.from(SelectCaseTest.Entity.class);
        query.select(from).where(cb.equal(from.get("value"), selectCase));
        entityManager.createQuery(query).getResultList();
    }

    @Test
    public void selectCaseWithCastedTypeValuesShouldWork() {
        EntityManager entityManager = getOrCreateEntityManager();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaBuilder.Case<String> selectCase = cb.selectCase();
        Predicate somePredicate = cb.equal(cb.literal(1), 1);
        selectCase.when(somePredicate, SelectCaseTest.EnumValue.VALUE_1.name());
        selectCase.otherwise(SelectCaseTest.EnumValue.VALUE_2.name());
        CriteriaQuery<SelectCaseTest.Entity> query = cb.createQuery(SelectCaseTest.Entity.class);
        Root<SelectCaseTest.Entity> from = query.from(SelectCaseTest.Entity.class);
        query.select(from).where(cb.equal(from.get("value").as(String.class), selectCase.as(String.class)));
        entityManager.createQuery(query).getResultList();
    }

    @Test
    public void simpleSelectCaseWithValuesShouldWork() {
        EntityManager entityManager = getOrCreateEntityManager();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        SimpleCase<Integer, SelectCaseTest.EnumValue> selectCase = cb.selectCase(cb.literal(1));
        selectCase.when(1, SelectCaseTest.EnumValue.VALUE_1);
        selectCase.otherwise(SelectCaseTest.EnumValue.VALUE_2);
        CriteriaQuery<SelectCaseTest.Entity> query = cb.createQuery(SelectCaseTest.Entity.class);
        Root<SelectCaseTest.Entity> from = query.from(SelectCaseTest.Entity.class);
        query.select(from).where(cb.equal(from.get("value"), selectCase));
        List<?> result = entityManager.createQuery(query).getResultList();
    }

    @Test
    public void simpleSelectCaseWithCastedTypeValuesShouldWork() {
        EntityManager entityManager = getOrCreateEntityManager();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        SimpleCase<Integer, String> selectCase = cb.selectCase(cb.literal(1));
        selectCase.when(1, SelectCaseTest.EnumValue.VALUE_1.name());
        selectCase.otherwise(SelectCaseTest.EnumValue.VALUE_2.name());
        CriteriaQuery<SelectCaseTest.Entity> query = cb.createQuery(SelectCaseTest.Entity.class);
        Root<SelectCaseTest.Entity> from = query.from(SelectCaseTest.Entity.class);
        query.select(from).where(cb.equal(from.get("value").as(String.class), selectCase.as(String.class)));
        entityManager.createQuery(query).getResultList();
    }

    @javax.persistence.Entity
    @Table(name = "entity")
    public static class Entity {
        @Id
        private Long id;

        @Enumerated(EnumType.STRING)
        private SelectCaseTest.EnumValue value;
    }

    public enum EnumValue {

        VALUE_1,
        VALUE_2;}
}

