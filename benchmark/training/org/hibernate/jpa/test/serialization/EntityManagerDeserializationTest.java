/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.serialization;


import java.io.IOException;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PersistenceException;
import javax.persistence.Table;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
public class EntityManagerDeserializationTest extends BaseEntityManagerFunctionalTestCase {
    @Test(expected = PersistenceException.class)
    @TestForIssue(jiraKey = "HHH-11555")
    public void deserializedEntityManagerPersistenceExceptionManagementTest() throws IOException {
        EntityManager entityManager = getOrCreateEntityManager();
        final EntityManager deserializedSession = spoofSerialization(entityManager);
        try {
            deserializedSession.getTransaction().begin();
            EntityManagerDeserializationTest.TestEntity entity = new EntityManagerDeserializationTest.TestEntity();
            entity.setName("Andrea");
            deserializedSession.persist(entity);
            entity.setName(null);
            deserializedSession.flush();
        } finally {
            deserializedSession.getTransaction().rollback();
            deserializedSession.close();
            entityManager.close();
        }
    }

    @Entity
    @Table(name = "TEST_ENTITY")
    public static class TestEntity {
        @Id
        @GeneratedValue
        private Long id;

        @Column(nullable = false)
        String name;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

