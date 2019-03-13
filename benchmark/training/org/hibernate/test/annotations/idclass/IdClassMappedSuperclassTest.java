/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.idclass;


import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.MappedSuperclass;
import org.hibernate.Session;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-9114")
public class IdClassMappedSuperclassTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testIdClassWithMappedSuperclassAndId() throws Exception {
        Session session = openSession();
        try {
            // Persist the entity
            IdClassMappedSuperclassTest.Simple simple = new IdClassMappedSuperclassTest.Simple();
            simple.setSimpleId("1");
            simple.setCategoryId("2");
            session.getTransaction().begin();
            session.save(simple);
            session.getTransaction().commit();
            session.clear();
            // Query the entity.
            session.getTransaction().begin();
            simple = session.createQuery("FROM Simple", IdClassMappedSuperclassTest.Simple.class).getSingleResult();
            session.getTransaction().commit();
            // tests.
            Assert.assertNotNull(simple);
            Assert.assertEquals("1", simple.getSimpleId());
            Assert.assertEquals("2", simple.getCategoryId());
        } catch (Throwable t) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            throw t;
        } finally {
            session.close();
        }
    }

    @MappedSuperclass
    public abstract static class AbstractMappedSuperclass {
        @Id
        private String categoryId;

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }
    }

    @Entity(name = "Simple")
    @IdClass(IdClassMappedSuperclassTest.SimpleId.class)
    public static class Simple extends IdClassMappedSuperclassTest.AbstractMappedSuperclass {
        @Id
        private String simpleId;

        private String data;

        public String getSimpleId() {
            return simpleId;
        }

        public void setSimpleId(String simpleId) {
            this.simpleId = simpleId;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }

    public static class SimpleId implements Serializable {
        private String simpleId;

        private String categoryId;

        public String getSimpleId() {
            return simpleId;
        }

        public void setSimpleId(String simpleId) {
            this.simpleId = simpleId;
        }

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            IdClassMappedSuperclassTest.SimpleId simpleId1 = ((IdClassMappedSuperclassTest.SimpleId) (o));
            if ((getSimpleId()) != null ? !(getSimpleId().equals(simpleId1.getSimpleId())) : (simpleId1.getSimpleId()) != null) {
                return false;
            }
            return (getCategoryId()) != null ? getCategoryId().equals(simpleId1.getCategoryId()) : (simpleId1.getCategoryId()) == null;
        }

        @Override
        public int hashCode() {
            int result = ((getSimpleId()) != null) ? getSimpleId().hashCode() : 0;
            result = (31 * result) + ((getCategoryId()) != null ? getCategoryId().hashCode() : 0);
            return result;
        }
    }
}

