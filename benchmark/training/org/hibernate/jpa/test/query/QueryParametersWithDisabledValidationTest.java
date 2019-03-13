/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.query;


import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-11579")
public class QueryParametersWithDisabledValidationTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void setParameterWithWrongTypeShouldNotThrowIllegalArgumentException() {
        final EntityManager entityManager = entityManagerFactory().createEntityManager();
        try {
            entityManager.createQuery("select e from TestEntity e where e.id = :id").setParameter("id", 1);
        } finally {
            entityManager.close();
        }
    }

    @Test
    public void setParameterWithCorrectTypeShouldNotThrowIllegalArgumentException() {
        final EntityManager entityManager = entityManagerFactory().createEntityManager();
        try {
            entityManager.createQuery("select e from TestEntity e where e.id = :id").setParameter("id", 1L);
        } finally {
            entityManager.close();
        }
    }

    @Entity(name = "TestEntity")
    public class TestEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;
    }
}

