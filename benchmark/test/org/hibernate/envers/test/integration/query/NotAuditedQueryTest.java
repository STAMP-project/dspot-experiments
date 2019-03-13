/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.query;


import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.envers.exception.NotAuditedException;
import org.hibernate.envers.test.BaseEnversFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
public class NotAuditedQueryTest extends BaseEnversFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-11558")
    public void testRevisionsOfEntityNotAuditedMultipleResults() {
        try {
            getAuditReader().createQuery().forRevisionsOfEntity(NotAuditedQueryTest.NonAuditedEntity.class, false, false).getResultList();
            Assert.fail("Expected a NotAuditedException");
        } catch (Exception e) {
            assertTyping(NotAuditedException.class, e);
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11558")
    public void testRevisionsOfEntityNotAuditedSingleResult() {
        try {
            getAuditReader().createQuery().forRevisionsOfEntity(NotAuditedQueryTest.NonAuditedEntity.class, false, false).setMaxResults(1).getSingleResult();
            Assert.fail("Expected a NotAuditedException");
        } catch (Exception e) {
            assertTyping(NotAuditedException.class, e);
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11558")
    public void testForEntitiesAtRevisionNotAuditedMultipleResults() {
        try {
            getAuditReader().createQuery().forEntitiesAtRevision(NotAuditedQueryTest.NonAuditedEntity.class, 1).getResultList();
            Assert.fail("Expected a NotAuditedException");
        } catch (Exception e) {
            assertTyping(NotAuditedException.class, e);
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11558")
    public void testForEntitiesAtRevisionNotAuditedSingleResult() {
        try {
            getAuditReader().createQuery().forEntitiesAtRevision(NotAuditedQueryTest.NonAuditedEntity.class, 1).setMaxResults(1).getSingleResult();
            Assert.fail("Expected a NotAuditedException");
        } catch (Exception e) {
            assertTyping(NotAuditedException.class, e);
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11558")
    public void testForEntitiesModifiedAtRevisionNotAuditedMultipleResults() {
        try {
            getAuditReader().createQuery().forEntitiesModifiedAtRevision(NotAuditedQueryTest.NonAuditedEntity.class, 1).getResultList();
            Assert.fail("Expected a NotAuditedException");
        } catch (Exception e) {
            assertTyping(NotAuditedException.class, e);
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11558")
    public void testForEntitiesModifiedAtRevisionNotAuditedSingleResult() {
        try {
            getAuditReader().createQuery().forEntitiesModifiedAtRevision(NotAuditedQueryTest.NonAuditedEntity.class, 1).setMaxResults(1).getSingleResult();
            Assert.fail("Expected a NotAuditedException");
        } catch (Exception e) {
            assertTyping(NotAuditedException.class, e);
        }
    }

    @Entity(name = "NonAuditedEntity")
    public static class NonAuditedEntity {
        @Id
        private Integer id;

        private String data;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}

