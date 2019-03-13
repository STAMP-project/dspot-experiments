/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.query;


import java.util.Arrays;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.envers.Audited;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-8058")
public abstract class AbstractEntityWithChangesQueryTest extends BaseEnversJPAFunctionalTestCase {
    private Integer simpleId;

    @Test
    @Priority(10)
    public void initData() {
        // Revision 1
        simpleId = doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.query.Simple simple = new org.hibernate.envers.test.integration.query.Simple();
            simple.setName("Name");
            simple.setValue(25);
            entityManager.persist(simple);
            return simple.getId();
        });
        // Revision 2
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.query.Simple simple = entityManager.find(.class, simpleId);
            simple.setName("Name-Modified2");
            entityManager.merge(simple);
        });
        // Revision 3
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.query.Simple simple = entityManager.find(.class, simpleId);
            simple.setName("Name-Modified3");
            simple.setValue(100);
            entityManager.merge(simple);
        });
        // Revision 4
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.query.Simple simple = entityManager.find(.class, simpleId);
            entityManager.remove(simple);
        });
    }

    @Test
    public void testRevisionCount() {
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4), getAuditReader().getRevisions(AbstractEntityWithChangesQueryTest.Simple.class, simpleId));
    }

    @Test
    public void testEntityRevisionsWithChangesQueryNoDeletions() {
        List results = getAuditReader().createQuery().forRevisionsOfEntityWithChanges(AbstractEntityWithChangesQueryTest.Simple.class, false).add(AuditEntity.id().eq(simpleId)).getResultList();
        compareResults(getExpectedResults(false), results);
    }

    @Test
    public void testEntityRevisionsWithChangesQuery() {
        List results = getAuditReader().createQuery().forRevisionsOfEntityWithChanges(AbstractEntityWithChangesQueryTest.Simple.class, true).add(AuditEntity.id().eq(simpleId)).getResultList();
        compareResults(getExpectedResults(true), results);
    }

    @Audited
    @Entity(name = "Simple")
    public static class Simple {
        @Id
        @GeneratedValue
        private Integer id;

        private String name;

        private Integer value;

        Simple() {
        }

        Simple(Integer id, String name, Integer value) {
            this.id = id;
            this.name = name;
            this.value = value;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            AbstractEntityWithChangesQueryTest.Simple simple = ((AbstractEntityWithChangesQueryTest.Simple) (o));
            if ((getId()) != null ? !(getId().equals(simple.getId())) : (simple.getId()) != null) {
                return false;
            }
            if ((getName()) != null ? !(getName().equals(simple.getName())) : (simple.getName()) != null) {
                return false;
            }
            return (getValue()) != null ? getValue().equals(simple.getValue()) : (simple.getValue()) == null;
        }

        @Override
        public int hashCode() {
            int result = ((getId()) != null) ? getId().hashCode() : 0;
            result = (31 * result) + ((getName()) != null ? getName().hashCode() : 0);
            result = (31 * result) + ((getValue()) != null ? getValue().hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return ((((((("Simple{" + "id=") + (id)) + ", name='") + (name)) + '\'') + ", value=") + (value)) + '}';
        }
    }
}

