/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
package org.hibernate.test.cdi.converters.legacy;


import java.util.List;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test AttributeConverter functioning in various Query scenarios.
 *
 * @author Etienne Miret
 */
public class QueryTest extends BaseEntityManagerFunctionalTestCase {
    public static final float SALARY = 267.89F;

    @Test
    @TestForIssue(jiraKey = "HHH-9356")
    public void testCriteriaBetween() {
        final EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<QueryTest.Employee> query = cb.createQuery(QueryTest.Employee.class);
        final Root<QueryTest.Employee> root = query.from(QueryTest.Employee.class);
        query.select(root);
        query.where(cb.between(root.<Float>get("salary"), new Float(300.0F), new Float(400.0F)));
        final List<QueryTest.Employee> result0 = em.createQuery(query).getResultList();
        Assert.assertEquals(0, result0.size());
        query.where(cb.between(root.<Float>get("salary"), new Float(100.0F), new Float(200.0F)));
        final List<QueryTest.Employee> result1 = em.createQuery(query).getResultList();
        Assert.assertEquals(0, result1.size());
        query.where(cb.between(root.<Float>get("salary"), new Float(200.0F), new Float(300.0F)));
        final List<QueryTest.Employee> result2 = em.createQuery(query).getResultList();
        Assert.assertEquals(1, result2.size());
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testJpqlLiteralBetween() {
        final EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        @SuppressWarnings("unchecked")
        final List<QueryTest.Employee> result0 = em.createQuery("from Employee where salary between 300.0F and 400.0F").getResultList();
        Assert.assertEquals(0, result0.size());
        @SuppressWarnings("unchecked")
        final List<QueryTest.Employee> result1 = em.createQuery("from Employee where salary between 100.0F and 200.0F").getResultList();
        Assert.assertEquals(0, result1.size());
        @SuppressWarnings("unchecked")
        final List<QueryTest.Employee> result2 = em.createQuery("from Employee where salary between 200.0F and 300.0F").getResultList();
        Assert.assertEquals(1, result2.size());
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testJpqlParametrizedBetween() {
        final EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        final Query query = em.createQuery("from Employee where salary between :low and :high");
        query.setParameter("low", new Float(300.0F));
        query.setParameter("high", new Float(400.0F));
        Assert.assertEquals(0, query.getResultList().size());
        query.setParameter("low", new Float(100.0F));
        query.setParameter("high", new Float(200.0F));
        Assert.assertEquals(0, query.getResultList().size());
        query.setParameter("low", new Float(200.0F));
        query.setParameter("high", new Float(300.0F));
        Assert.assertEquals(1, query.getResultList().size());
        em.getTransaction().commit();
        em.close();
    }

    @Entity(name = "Employee")
    @Table(name = "EMP")
    public static class Employee {
        @Id
        public Integer id;

        @Embedded
        public QueryTest.Name name;

        public Float salary;

        public Employee() {
        }

        public Employee(Integer id, QueryTest.Name name, Float salary) {
            this.id = id;
            this.name = name;
            this.salary = salary;
        }
    }

    @Embeddable
    public static class Name {
        public String first;

        public String middle;

        public String last;

        public Name() {
        }

        public Name(String first, String middle, String last) {
            this.first = first;
            this.middle = middle;
            this.last = last;
        }
    }

    @Converter(autoApply = true)
    public static class SalaryConverter implements AttributeConverter<Float, Long> {
        @Override
        @SuppressWarnings("UnnecessaryBoxing")
        public Long convertToDatabaseColumn(Float attribute) {
            if (attribute == null) {
                return null;
            }
            return new Long(((long) (attribute * 100)));
        }

        @Override
        @SuppressWarnings("UnnecessaryBoxing")
        public Float convertToEntityAttribute(Long dbData) {
            if (dbData == null) {
                return null;
            }
            return new Float(((dbData.floatValue()) / 100));
        }
    }
}

